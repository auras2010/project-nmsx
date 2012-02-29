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
package l2.universe.gameserver.cache;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javolution.util.FastMap;

import l2.universe.Config;
import l2.universe.gameserver.util.Util;

/**
 * @author Layane
 *
 */
public class HtmCache
{
	private static Logger _log = Logger.getLogger(HtmCache.class.getName());

	private FastMap<Integer, String> _cache;
	private FastMap<Integer, String> _files;
	private int _loadedFiles;
	private long _bytesBuffLen;
	
	private static boolean showFiles = true;

	private static final String[] VALID_TAGS = 
	{ 
		"column", "unknown", "ul", "u", "tt", "tr", "title", "textcode", "textarea", 
		"td", "table", "sup", "sub", "strike", "spin", "select", "right", "pre", "p", 
		"option", "ol", "multiedit", "li", "left", "input", "img", "i", "html", "h7", 
		"h6", "h5", "h4", "h3", "h2", "h1", "font", "extend", "edit", "comment", 
		"combobox", "center", "button", "br", "br1","body", "bar", "address", "a", "sel", "list", 
		"var", "fore", "readonl", "rows", "valign", "fixwidth", "bordercolorli", "bordercolorda", 
		"bordercolor", "border", "bgcolor", "background", "align", "valu", "readonly", "multiple", 
		"selected", "typ", "type", "maxlength", "checked", "src", "y", "x", "querydelay", 
		"noscrollbar", "imgsrc", "b", "fg", "size", "face", "color", "deffon",
		"deffixedfont", "width", "value", "tooltip", "name", "min", "max", "height", 
		"disabled", "align", "msg", "link", "href", "action", "head", "tbody", 
		"!--", "--","'","`"
	};
			
	private final static HashSet<String> VALID_TAGS_SET = new HashSet<String>(301);
	static
	{
		for (String tag : VALID_TAGS)
			VALID_TAGS_SET.add(tag);
	}
	
	public static HtmCache getInstance()
	{
		return SingletonHolder._instance;
	}

	private HtmCache()
	{
		_cache = new FastMap<Integer, String>(16000);
		if (showFiles)
			_files = new FastMap<Integer, String>();
		
		reload();
	}
	
	public void setShowFiles(boolean sets)
	{
		showFiles = sets;
	}

	public void reload()
	{
		reload(Config.DATAPACK_ROOT);
	}

	public void reload(File f)
	{
		if (!Config.LAZY_CACHE)
		{
			_log.info("Html cache start...");
			parseDir(f);
			_log.info("Validation starts...");
			validate();
			_log.info("Cache[HTML]: " + String.format("%.3f", getMemoryUsage()) + " megabytes on " + getLoadedFiles() + " files loaded");
		}
		else
		{
			_cache.clear();
			_files.clear();
			_loadedFiles = 0;
			_bytesBuffLen = 0;
			_log.info("Cache[HTML]: Running lazy cache");
		}
	}

	public void reloadPath(File f)
	{
		parseDir(f);
		_log.info("Cache[HTML]: Reloaded specified path.");
	}

	public double getMemoryUsage()
	{
		return ((float) _bytesBuffLen / 1048576);
	}

	public int getLoadedFiles()
	{
		return _loadedFiles;
	}

	private static class HtmFilter implements FileFilter
	{
		public boolean accept(File file)
		{
			if (!file.isDirectory())
			{
				return (file.getName().endsWith(".htm") || file.getName().endsWith(".html"));
			}
			return true;
		}
	}

	private void parseDir(File dir)
	{
		FileFilter filter = new HtmFilter();
		File[] files = dir.listFiles(filter);
		
		for (File file : files)
		{
			if (!file.isDirectory())
				loadFile(file);
			else
				parseDir(file);
		}
	}

	public String loadFile(File file)
	{
		final String relpath = Util.getRelativePath(Config.DATAPACK_ROOT, file);
		final int hashcode = relpath.hashCode();

		final HtmFilter filter = new HtmFilter();

		if (file.exists() && filter.accept(file) && !file.isDirectory())
		{
			String content;
			FileInputStream fis = null;

			try
			{
				fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				int bytes = bis.available();
				byte[] raw = new byte[bytes];
				
				bis.read(raw);
				content = new String(raw, "UTF-8");
				content = content.replaceAll("\r\n", "\n");

				String oldContent = _cache.get(hashcode);

				if (oldContent == null)
				{
					_bytesBuffLen += bytes;
					_loadedFiles++;
				}
				else
				{
					_bytesBuffLen = _bytesBuffLen - oldContent.length() + bytes;
				}

				_cache.put(hashcode, content);
				if (showFiles)
					_files.put(hashcode, file.getAbsolutePath());

				return content;
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "Problem with htm file " + e.getMessage(), e);
			}
			finally
			{
				try
				{
					fis.close();
				}
				catch (Exception e1){}
			}
		}

		return null;
	}

	public String getHtmForce(String prefix, String path)
	{
		String content = getHtm(prefix, path);

		if (content == null)
		{
			content = "<html><body>My text is missing:<br>" + path + "</body></html>";
			_log.warning("Cache[HTML]: Missing HTML page: " + path);
		}

		return content;
	}

	public String getHtm(String prefix, String path)
	{
		String newPath = null;
		String content;
		if (prefix != null && !prefix.isEmpty())
		{
			newPath = prefix + path;
			content = getHtm(newPath);
			if (content != null)
				return content;
		}

		content = getHtm(path);
		if (content != null && newPath != null)
		{
			Integer key = newPath.hashCode();
			_cache.put(key, content);
			if (showFiles)
				_files.put(key, newPath);
		}

		return content;
	}

	private String getHtm(String path)
	{
		if (path == null || path.isEmpty())
			return ""; // avoid possible NPE

		final int hashCode = path.hashCode();
		String content = _cache.get(hashCode);

		if (Config.LAZY_CACHE && content == null)
			content = loadFile(new File(Config.DATAPACK_ROOT, path));

		return content;
	}

	public boolean contains(String path)
	{
		return _cache.containsKey(path.hashCode());
	}

	/**
	 * Check if an HTM exists and can be loaded
	 * @param
	 * path The path to the HTM
	 * */
	public boolean isLoadable(String path)
	{
		File file = new File(path);
		HtmFilter filter = new HtmFilter();

		if (file.exists() && filter.accept(file) && !file.isDirectory())
			return true;
		
		return false;
	}

		
	private void validate() 
	{
		for (Entry<Integer, String> entry : _cache.entrySet()) 
		{
			final String html = entry.getValue();
			outer: for (int begin = 0; (begin = html.indexOf("<", begin)) != -1; begin++) 
			{
				int end;
				for (end = begin; end < html.length(); end++) 
				{
					if (html.charAt(end) == '>' || html.charAt(end) == ' ')
						break;
					
					// Some special quest-replaced tag
					if (end == begin + 1 && html.charAt(end) == '?')
						continue outer;
				}
				
				end++;
				final String tag = html.substring(begin + 1, end - 1).toLowerCase().replaceAll("/", "");
				if (!VALID_TAGS_SET.contains(tag)) 
					_log.info("Invalid tag used: '" + tag + "' at pos "	+ (begin + 1) + " " + (showFiles ? _files.get(entry.getKey()) : entry.getKey()));
			}
		}
	}
	
	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder
	{
		protected static final HtmCache _instance = new HtmCache();
	}
}
