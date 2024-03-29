/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2.brick.gameserver.datatables;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import l2.brick.Config;
import l2.brick.gameserver.model.L2NpcWalkerNode;
import l2.brick.gameserver.network.NpcStringId;

/**
 * Main Table to Load Npc Walkers Routes and Chat.<br>
 * @author Rayan, JIV
 */
public class NpcWalkerRoutesTable
{
	private final static Logger _log = Logger.getLogger(NpcWalkerRoutesTable.class.getName());
	
	private final TIntObjectHashMap<List<L2NpcWalkerNode>> _routes = new TIntObjectHashMap<List<L2NpcWalkerNode>>();
	
	public static NpcWalkerRoutesTable getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private NpcWalkerRoutesTable()
	{
		if (Config.ALLOW_NPC_WALKERS)
		{
			_log.info("Initializing Walkers Routes Table.");
			load();
		}
	}
	
	public void load()
	{
		_routes.clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		File file = new File(Config.DATAPACK_ROOT, "data/WalkerRoutes.xml");
		Document doc = null;
		if (file.exists())
		{
			try
			{
				doc = factory.newDocumentBuilder().parse(file);
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Could not parse WalkerRoutes.xml file: " + e.getMessage(), e);
			}
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equals("walker"))
				{
					List<L2NpcWalkerNode> list = new ArrayList<L2NpcWalkerNode>();
					int npcId = Integer.parseInt(d.getAttributes().getNamedItem("npcId").getNodeValue());
					for (Node r = d.getFirstChild(); r != null; r = r.getNextSibling())
					{
						if (r.getNodeName().equals("route"))
						{
							NamedNodeMap attrs = r.getAttributes();
							int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
							int x = Integer.parseInt(attrs.getNamedItem("X").getNodeValue());
							int y = Integer.parseInt(attrs.getNamedItem("Y").getNodeValue());
							int z = Integer.parseInt(attrs.getNamedItem("Z").getNodeValue());
							int delay = Integer.parseInt(attrs.getNamedItem("delay").getNodeValue());
							String chatString = null;
							NpcStringId npcString = null;
							Node node = attrs.getNamedItem("string");
							if (node != null)
							{
								chatString = node.getNodeValue();
							}
							else
							{
								node = attrs.getNamedItem("npcString");
								if (node != null)
								{
									npcString = NpcStringId.getNpcStringId(node.getNodeValue());
									if (npcString == null)
									{
										_log.log(Level.WARNING, "NpcWalkerRoutersTable: Unknown npcstring '" + node.getNodeValue() + ".");
										continue;
									}
								}
								else
								{
									node = attrs.getNamedItem("npcStringId");
									if (node != null)
									{
										npcString = NpcStringId.getNpcStringId(Integer.parseInt(node.getNodeValue()));
										if (npcString == null)
										{
											_log.log(Level.WARNING, "NpcWalkerRoutersTable: Unknown npcstring '" + node.getNodeValue() + ".");
											continue;
										}
									}
								}
							}
							
							boolean running = Boolean.parseBoolean(attrs.getNamedItem("run").getNodeValue());
							list.add(new L2NpcWalkerNode(id, npcString, chatString, x, y, z, delay, running));
						}
					}
					
					// ArrayList has initial capacity of 10, let's trim them to size before putting it into the map.
					((ArrayList<L2NpcWalkerNode>) list).trimToSize();
					_routes.put(npcId, list);
				}
			}
		}
		_log.info("WalkerRoutesTable: Loaded " + _routes.size() + " Npc Walker Routes.");
	}
	
	public List<L2NpcWalkerNode> getRouteForNpc(int id)
	{
		return _routes.get(id);
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final NpcWalkerRoutesTable _instance = new NpcWalkerRoutesTable();
	}
}
