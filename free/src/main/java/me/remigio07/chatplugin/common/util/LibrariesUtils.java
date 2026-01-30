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

package me.remigio07.chatplugin.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.Library.Relocation;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.VersionChange;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.bootstrap.IsolatedClassLoader;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;

public class LibrariesUtils {
	
	private static final char[] HEX_CODES = "0123456789ABCDEF".toCharArray();
	private static final Set<Library> ISOLATED_LIBRARIES = EnumSet.of(Library.ASM, Library.ASM_COMMONS, Library.JAR_RELOCATOR, Library.H2_DATABASE_ENGINE, Library.SQLITE_JDBC); // what about MySQL?
	private static IsolatedClassLoader isolatedClassLoader = IsolatedClassLoader.getInstance();
	
	public static boolean isLoaded(Library library) {
		try {
			boolean relocation = library.getRelocation() != null;
			Class.forName((relocation ? Relocation.PREFIX : "") + library.getClazz(), false, relocation ? JARLibraryLoader.getInstance() : isolatedClassLoader);
			return true;
		} catch (ClassNotFoundException cnfe) {
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void loadYAMLForFabric() throws ChatPluginManagerException {
		Library snakeyaml = Library.SNAKEYAML;
		
		if (!isLoaded(snakeyaml)) {
			try {
				Path path = getPath(snakeyaml);
				
				if (!Files.exists(path))
					download(snakeyaml);
				JARLibraryLoader.getInstance().load(path);
			} catch (Throwable t) {
				throw new ChatPluginManagerException("libraries utils", new LibraryException(t, snakeyaml));
			}
		}
	}
	
	public static void load(Library library) throws LibraryException {
		if (library == Library.SNAKEYAML)
			throw new UnsupportedOperationException("Use loadYAMLForFabric() instead");
		if (!isLoaded(library)) {
			try {
				Path path = getPath(library);
				
				if (Files.exists(path)) {
					if (ConfigurationManager.getInstance().getLastVersionChange() != VersionChange.NULL)
						downloadFreshCopy("Updating {0} library (new plugin version detected)...", 0, library);
				} else download(library);
				
				if (ISOLATED_LIBRARIES.contains(library))
					isolatedClassLoader.load(path);
				else JARLibraryLoader.getInstance().load(path);
			} catch (Throwable t) {
				throw new LibraryException(t, library);
			}
		}
	}
	
	public static void downloadFreshCopy(String message, int logLevel, Library library) throws Throwable {
		LogManager.log(message, logLevel, library.getName());
		delete(library);
		download(library);
	}
	
	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		
		for (byte b : bytes) {
			sb.append(HEX_CODES[(b >> 4) & 0xF]);
			sb.append(HEX_CODES[(b & 0xF)]);
		} return sb.toString();
	}
	
	public static void delete(Library library) throws IOException {
		Path path = getPath(library);
		
		Files.deleteIfExists(path);
		
		if (library.getRelocation() != null)
			Files.deleteIfExists(path.getParent().resolve(path.getFileName().toString() + ".tmp"));
	}
	
	public static void download(Library library) throws Throwable {
		long ms = System.currentTimeMillis();
		Path path = getPath(library);
		
		if (!Files.exists(path.getParent()))
			Files.createDirectories(path.getParent());
		Files.copy(Utils.download(library.getURL()), path);
		
		if (!bytesToHexString(MessageDigest.getInstance("MD5").digest(Files.readAllBytes(path))).equals(library.getMD5Hash())) {
			Files.delete(path);
			throw new IllegalArgumentException("The downloaded file was corrupted and has been deleted");
		} long size = Files.size(path);
		boolean megabyte = size > Math.pow(1024, 2);
		
		LogManager.log("{0} library ({1}) downloaded successfully in {2} ms.", 0, library.getName(), MemoryUtils.formatMemory(size, megabyte ? MemoryUtils.MEGABYTE : MemoryUtils.KILOBYTE) + (megabyte ? " MB" : " KB"), System.currentTimeMillis() - ms);
		relocate(library);
	}
	
	public static void relocate(Library library) throws Throwable {
		if (library.getRelocation() == null)
			return;
		long ms = System.currentTimeMillis();
		Path jar = getPath(library);
		Path tmp = jar.getParent().resolve(jar.getFileName().toString() + ".tmp");
		List<Object> rules = new ArrayList<>();
		Class<?> clazz = isolatedClassLoader.loadClass("me.remigio07.jarrelocator.JarRelocator");
		
		Files.deleteIfExists(tmp);
		
		for (String oldPackage : library.getRelocation().getOldPackages())
			rules.add(isolatedClassLoader.loadClass("me.remigio07.jarrelocator.Relocation").getConstructor(String.class, String.class).newInstance(oldPackage, Relocation.PREFIX + oldPackage));
		Files.move(jar, tmp, StandardCopyOption.REPLACE_EXISTING);
		clazz.getMethod("run").invoke(clazz.getConstructors()[0].newInstance(tmp.toFile(), jar.toFile(), rules));
		Files.delete(tmp);
		LogManager.log("{0} relocated successfully in {1} ms.", 0, library.getName(), System.currentTimeMillis() - ms);
	}
	
	public static Path getPath(Library library) {
		return ChatPlugin.getInstance().getDataFolder().resolve("libraries" + File.separator + library.getFileName());
	}
	
}
