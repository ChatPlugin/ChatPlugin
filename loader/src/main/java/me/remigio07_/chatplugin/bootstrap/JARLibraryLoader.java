/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.bootstrap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class used to load ChatPlugin's libraries from JAR files.
 */
public class JARLibraryLoader extends URLClassLoader {
	
	private static JARLibraryLoader instance = new JARLibraryLoader();
	
	private JARLibraryLoader() {
		super(new URL[0], JARLibraryLoader.class.getClassLoader());
	}
	
	/**
	 * Initializes ChatPlugin.
	 * Uses {@link Environment#getCurrent()}'s {@link Environment#getEnableMethodArgsTypes()}.
	 * 
	 * @param args Enable method's args
	 */
	public void initialize(Object... args) {
		try {
			Environment environment = Environment.getCurrent();
			List<URL> jars = Stream.of(extract(false), extract(true)).filter(Objects::nonNull).collect(Collectors.toList());
			
			if (instance.getURLs().length == 0) {
				for (URL jar : jars) {
					instance.addURL(jar);
					
					for (String clazz : getClasses(new JarFile(new File(jar.toURI())), environment.getExcludedClasspaths())) {
						try {
							loadClass(clazz);
						} catch (NoClassDefFoundError e) {
							// optional dependencies
						}
					}
				}
			} Class<?> mainClass = Class.forName("me.remigio07_.chatplugin.ChatPlugin" + (jars.size() == 2 ? "Premium" : "") + "Impl", true, instance);
			
			if (environment == Environment.VELOCITY)
				mainClass.getMethod("load", Object.class, Object.class, Object.class).invoke(null, args);
			else mainClass.getMethod("load", Object.class, Object.class).invoke(null, args);
		} catch (IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calls <code>ChatPlugin#unload()</code>.
	 */
	public void disable() {
		try {
			Class<?> clazz = Class.forName("me.remigio07_.chatplugin.api.ChatPlugin");
			
			clazz.getMethod("unload").invoke(clazz.getMethod("getInstance").invoke(null));
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
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
		
		try {
			for (String clazz : getClasses(new JarFile(target), null))
				loadClass(clazz);
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			// optional dependencies
		}
	}
	
	private URL extract(boolean premium) throws IOException {
		URL url = JARLibraryLoader.class.getClassLoader().getResource("ChatPlugin" + (premium ? "-PREMIUM" : "") + ".jar");
		
		if (url == null)
			return null;
		try (InputStream input = url.openStream()) {
			Path path = Files.createTempFile("ChatPlugin" + (premium ? "-PREMIUM" : ""), ".jar.tmp");
			
			path.toFile().deleteOnExit();
			Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING);
			return path.toUri().toURL();
		}
	}
	
	private static List<String> getClasses(JarFile jar, List<String> excludedClasspaths) {
		List<String> classes = new ArrayList<>();
		Enumeration<JarEntry> entries = jar.entries();
		String entry;
		
		while (entries.hasMoreElements() && (entry = entries.nextElement().getName()) != null)
			if (entry.endsWith(".class") && !entry.startsWith("META-INF") && !entry.equals("module-info.class") && (excludedClasspaths == null || isIncluded(entry, excludedClasspaths)))
				classes.add(entry.substring(0, entry.lastIndexOf('.')).replace('/', '.'));
		return classes;
	}
	
	private static boolean isIncluded(String entryName, List<String> excludedClasspaths) {
		for (String excludedClasspath : excludedClasspaths)
			if (entryName.replace('/', '.').startsWith(excludedClasspath))
				return false;
		return true;
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
