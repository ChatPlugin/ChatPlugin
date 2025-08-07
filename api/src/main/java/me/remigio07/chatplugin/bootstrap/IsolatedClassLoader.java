/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2025  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://remigio07.me/chatplugin>
 */

package me.remigio07.chatplugin.bootstrap;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * Class used to load ChatPlugin's libraries that
 * require to be isolated from the server's classes.
 */
public class IsolatedClassLoader extends URLClassLoader {
	
	private static IsolatedClassLoader instance = new IsolatedClassLoader();
	
	static {
		registerAsParallelCapable();
	}
	
	private IsolatedClassLoader() {
		super(new URL[0], ClassLoader.getSystemClassLoader().getParent());
	}
	
	/**
	 * Loads a JAR library.
	 * 
	 * @param library Library to load
	 * @throws IOException If <code>library</code> does not
	 * represent a valid JAR file or an I/O error occurs
	 * @throws SecurityException If the {@link SecurityManager}
	 * denies access to <code>library</code>
	 */
	public void load(Path library) throws IOException {
		addURL(library.toUri().toURL());
	}
	
	/**
	 * Gets the loader's current instance.
	 * 
	 * @return Loader's instance
	 */
	public static IsolatedClassLoader getInstance() {
		return instance;
	}
	
}
