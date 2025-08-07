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

package me.remigio07.chatplugin.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.Library.Relocation;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.VersionChange;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.bootstrap.IsolatedClassLoader;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;

public class LibrariesUtils {
	
	private static IsolatedClassLoader isolatedClassLoader = IsolatedClassLoader.getInstance();
	private static final char[] HEX_CODES = "0123456789ABCDEF".toCharArray();
	
	public static boolean isLoaded(Library library) {
		try {
			Class.forName(library.getClazz(), false, library.getRelocation() == null ? isolatedClassLoader : JARLibraryLoader.getInstance());
			return true;
		} catch (ClassNotFoundException cnfe) {
			return false;
		}
	}
	
	public static void load(Library library) throws LibraryException {
		if (!isLoaded(library)) {
			try {
				Path path = getPath(library);
				
				if (Files.exists(path)) {
					if (ConfigurationManager.getInstance().getLastVersionChange() != VersionChange.NULL)
						downloadFreshCopy("Updating {0} library (new plugin version detected)...", 0, library);
				} else download(library);
				
				if (library.getRelocation() == null) // only relocation libraries and database engines
					isolatedClassLoader.load(path);
				else JARLibraryLoader.getInstance().load(path);
			} catch (Throwable t) {
				throw new LibraryException(t, library);
			}
		}
	}
	
	private static void downloadFreshCopy(String message, int logLevel, Library library) throws Throwable {
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
