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
package l2.brick.util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Specialized {@link FileFilter} class.<br>
 * Accepts files ending with ".xml" only.
 * 
 * @author mrTJO
 */
public class XMLFilter implements FileFilter
{

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File pathname)
	{
		return pathname.getName().endsWith(".xml");
	}
	
}
