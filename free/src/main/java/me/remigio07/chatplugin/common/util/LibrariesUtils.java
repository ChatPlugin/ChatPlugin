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

package me.remigio07.chatplugin.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.Library.Relocation;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.bootstrap.IsolatedClassLoader;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;

public class LibrariesUtils {
	
	private static IsolatedClassLoader isolatedClassLoader = IsolatedClassLoader.getInstance();
	
	public static boolean isLoaded(Library library) {
		try {
			Class.forName(library.getClazz(), false, library.getRelocation() == null ? isolatedClassLoader : JARLibraryLoader.getInstance());
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	public static void load(Library library) throws LibraryException {
		if (!isLoaded(library)) {
			try {
				File file = getTarget(library);
				
				if (file.exists()) {
					if (ConfigurationManager.getInstance().getLastVersionChange().isMinor())
						downloadFreshCopy("Updating {0} library (new plugin version detected)...", 0, library);
					else if (!DatatypeConverter.printHexBinary(MessageDigest.getInstance("MD5").digest(Files.readAllBytes(getTarget(library).toPath()))).equals(library.getMD5Hash()))
						downloadFreshCopy("The {0} library's file is corrupted; downloading a fresh copy...", 1, library);
				} else {
					file.getParentFile().mkdirs();
					file.createNewFile();
					download(library);
				} if (library.getRelocation() == null)
					isolatedClassLoader.load(getTarget(library));
				else JARLibraryLoader.getInstance().load(getTarget(library));
			} catch (Throwable e) {
				throw new LibraryException(e, library);
			}
		}
	}
	
	private static void downloadFreshCopy(String message, int logLevel, Library library) throws Throwable {
		LogManager.log(message, logLevel, library.getName());
		delete(library);
		download(library);
	}
	
	public static void delete(Library library) {
		File jar = getTarget(library);
		
		jar.delete();
		
		if (library.getRelocation() != null)
			new File(jar.getPath() + ".tmp").delete();
		
	}
	
	public static void download(Library library) throws Throwable {
		long ms = System.currentTimeMillis();
		File target = getTarget(library);
		
		if (!target.exists()) {
			target.getParentFile().mkdirs();
			target.createNewFile();
		} FileOutputStream output = new FileOutputStream(target);
		
		output.getChannel().transferFrom(Channels.newChannel(Utils.download(library.getURL())), 0, Long.MAX_VALUE);
		output.close();
		
		boolean megabyte = target.length() > Math.pow(1024, 2);
		
		LogManager.log("{0} library ({1}) downloaded successfully in {2} ms.", 0, library.getName(), MemoryUtils.formatMemory(target.length(), megabyte ? MemoryUtils.MEGABYTE : MemoryUtils.KILOBYTE) + (megabyte ? " MB" : " KB"), System.currentTimeMillis() - ms);
		relocate(library);
	}
	
	public static void relocate(Library library) throws Throwable {
		if (library.getRelocation() == null)
			return;
		long ms = System.currentTimeMillis();
		File jar = getTarget(library);
		File tmp = new File(jar.getPath() + ".tmp");
		List<Object> rules = new ArrayList<>();
		Class<?> clazz = isolatedClassLoader.loadClass("me.remigio07.jarrelocator.JarRelocator");
		
		tmp.delete();
		
		for (String oldPackage : library.getRelocation().getOldPackages())
			rules.add(isolatedClassLoader.loadClass("me.remigio07.jarrelocator.Relocation").getConstructor(String.class, String.class).newInstance(oldPackage, Relocation.PREFIX + oldPackage));
		Files.move(jar.toPath(), tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
		clazz.getMethod("run").invoke(clazz.getConstructors()[0].newInstance(tmp, jar, rules));
		tmp.delete();
		LogManager.log("{0} relocated successfully in {1} ms.", 0, library.getName(), System.currentTimeMillis() - ms);
	}
	
	public static File getTarget(Library library) {
		return new File(new File(ChatPlugin.getInstance().getDataFolder(), "libraries"), library.getFileName());
	}
	
}
