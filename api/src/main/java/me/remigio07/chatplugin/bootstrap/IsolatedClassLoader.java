/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.bootstrap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Class used to load ChatPlugin's libraries that
 * require to be isolated from the server's code.
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
	 * Loads a library contained in the specified JAR file.
	 * 
	 * @param target Library to load
	 * @throws IOException If <code>target</code> is not a valid JAR file or an I/O error occurrs
	 * @throws SecurityException If the {@link SecurityManager} denies the access to <code>target</code>
	 */
	public void load(File target) throws IOException {
		addURL(target.toURI().toURL());
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
