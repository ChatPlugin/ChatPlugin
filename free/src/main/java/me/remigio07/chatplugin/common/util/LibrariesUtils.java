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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;

public class LibrariesUtils {
	
	public static final List<Library> RELOCATION_LIBS = Arrays.asList(Library.ASM, Library.ASM_COMMONS, Library.JAR_RELOCATOR);
	
	public static boolean isLoaded(Library library) {
		try {
			Class.forName(library.getClazz(), false, JARLibraryLoader.getInstance());
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	public static void load(Library library) throws Exception {
		if (!isLoaded(library)) {
			File file = getTarget(library);
			
			if (file.exists()) {
				if (ConfigurationManager.getInstance().getLastVersionChange().isMinor())
					update(library);
			} else {
				file.getParentFile().mkdirs();
				file.createNewFile();
				download(library);
			} JARLibraryLoader.getInstance().load(getTarget(library));
		}
	}
	
	public static void update(Library library) throws Exception {
		LogManager.log("Updating {0} library (new plugin version detected)...", 0, library.getName());
		delete(library);
		download(library);
	}
	
	public static void delete(Library library) {
		getTarget(library).delete();
	}
	
	public static void download(Library library) throws Exception {
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
	
	public static void relocate(Library library) throws Exception {
		if (library.getRelocation() == null)
			return;
		long ms = System.currentTimeMillis();
		File jar = getTarget(library);
		File tmp = new File(jar.getPath() + ".tmp");
		List<Object> rules = new ArrayList<>();
		Class<?> clazz = Class.forName("me.remigio07.jarrelocator.JarRelocator");
		
		for (String oldPackage : library.getRelocation().getOldPackages())
			rules.add(Class.forName("me.remigio07.jarrelocator.Relocation").getConstructor(String.class, String.class).newInstance(oldPackage, Library.PREFIX + oldPackage));
		Files.move(jar.toPath(), tmp.toPath(), StandardCopyOption.REPLACE_EXISTING);
		clazz.getMethod("run").invoke(clazz.getConstructors()[0].newInstance(tmp, jar, rules));
		tmp.delete();
		LogManager.log("{0} relocated successfully in {1} ms.", 0, library.getName(), System.currentTimeMillis() - ms);
	}
	
	public static File getTarget(Library library) {
		return new File(new File(ChatPlugin.getInstance().getDataFolder(), "libraries"), library.getFileName());
	}
	
}
