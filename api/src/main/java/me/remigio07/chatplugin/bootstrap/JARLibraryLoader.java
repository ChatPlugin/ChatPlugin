/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class used to load ChatPlugin's libraries from JAR files.
 */
public class JARLibraryLoader extends URLClassLoader {
	
	private static JARLibraryLoader instance = new JARLibraryLoader();
	private static final List<String> EDITIONS = Arrays.asList("Free", "Premium", "Private");
	
	static {
		registerAsParallelCapable();
	}
	
	private JARLibraryLoader() {
		super(new URL[0], JARLibraryLoader.class.getClassLoader());
	}
	
	void open(Object... args) {
		try {
			Path dataFolder = (Path) args[args.length - 1];
			Path files = dataFolder.resolve("files");
			List<URL> jars = getJARs();
			
			if (Files.exists(files)) {
				try (DirectoryStream<Path> stream = Files.newDirectoryStream(files)) {
					for (Path file : stream)
						if (file.getFileName().toString().endsWith(".jar.tmp"))
							Files.delete(file);
				}
			} else if (!Files.exists(dataFolder)) {
				Object logger = args[args.length - 2];
				
				logger.getClass().getMethod(logger instanceof Logger ? "warning" : "warn", String.class).invoke(
						logger,
						"Fresh installation? Welcome! Generating default files for you. Follow this guide for proper installation: https://remigio07.me/chatplugin/wiki/getting-started/Installation"
						);
				Files.createDirectories(files);
			} else Files.createDirectory(files);
			
			for (int i = 0; i < jars.size(); i++) {
				URL jar = jars.get(i);
				
				try (InputStream input = jar.openStream()) {
					Path path = Files.createTempFile(files, EDITIONS.get(i).toLowerCase() + "-", ".jar.tmp");
					
					Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING);
					path.toFile().deleteOnExit();
					addURL(path.toUri().toURL());
				}
			} (Environment.isVelocity()
					? Class.forName("me.remigio07.chatplugin.ChatPlugin" + EDITIONS.get(jars.size() - 1), true, this).getMethod("load", Object.class, Object.class, Path.class)
					: Class.forName("me.remigio07.chatplugin.ChatPlugin" + EDITIONS.get(jars.size() - 1), true, this).getMethod("load", Object.class, Path.class)
					).invoke(null, args);
		} catch (IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	private List<URL> getJARs() {
		return EDITIONS.stream().map(edition -> getClass().getClassLoader().getResource("ChatPlugin-" + edition.toUpperCase() + ".jar")).filter(Objects::nonNull).collect(Collectors.toList());
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
	public static JARLibraryLoader getInstance() {
		return instance;
	}
	
}
