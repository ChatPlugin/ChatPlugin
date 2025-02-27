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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to load ChatPlugin's libraries from JAR files.
 */
public class JARLibraryLoader extends URLClassLoader {
	
	private static JARLibraryLoader instance = new JARLibraryLoader();
	private static final String[] EDITIONS = { "", "Premium", "Private"};
	
	static {
		registerAsParallelCapable();
	}
	
	private JARLibraryLoader() {
		super(new URL[0], JARLibraryLoader.class.getClassLoader());
	}
	
	/**
	 * Initializes ChatPlugin.
	 * 
	 * <p>Uses {@link Environment#getCurrent()}'s
	 * {@link Environment#getEnableMethodArgsTypes()}.</p>
	 * 
	 * @param args Enable method's args
	 */
	public void initialize(Object... args) {
		try {
			Environment environment = Environment.getCurrent();
			List<URL> jars = extractJARs();
			
			if (instance.getURLs().length == 0)
				for (URL jar : jars)
					instance.addURL(jar);
			Class<?> mainClass = Class.forName("me.remigio07.chatplugin.ChatPlugin" + EDITIONS[jars.size() - 1] + "Impl", true, instance);
			
			if (environment == Environment.VELOCITY)
				mainClass.getMethod("load", Object.class, Object.class, Object.class).invoke(null, args);
			else mainClass.getMethod("load", Object.class, Object.class).invoke(null, args);
		} catch (IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  e) {
			e.printStackTrace();
		}
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
	
	private List<URL> extractJARs() throws IOException {
		List<URL> urls = new ArrayList<>();
		
		for (String edition : EDITIONS) {
			String fileName = "ChatPlugin" + (edition.isEmpty() ? "" : '-' + edition.toUpperCase());
			URL url = JARLibraryLoader.class.getClassLoader().getResource(fileName + ".jar");
			
			if (url != null)
				try (InputStream input = url.openStream()) {
					Path path = Files.createTempFile(fileName, ".jar.tmp");
					
					path.toFile().deleteOnExit();
					Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING);
					urls.add(path.toUri().toURL());
				}
		} return urls;
	}
	
	
	/**
	 * Gets the loader's current instance.
	 * 
	 * @return Loader's instance
	 */
	public static JARLibraryLoader getInstance() {
		return instance;
	}
	
}
