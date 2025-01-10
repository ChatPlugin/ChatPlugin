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
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.SensitiveData;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07.chatplugin.bootstrap.Environment;

public class Debugger {
	
	private static final String HEADER = "----------------------------------------------------------------------------------------------------\n#     __       __  ___  __            __            __      __   __  __        __   __   __  __    #\n#    /   |__| |__|  |  |__) |   |  | | _  | |\\ | ' |__     |  \\ |_  |__) |  | | _  | _  |_  |__)   #\n#    \\__ |  | |  |  |  |    |__ |__| |__| | | \\|    __|    |__/ |__ |__) |__| |__| |__| |__ | \\    #\n#                                                                                                  #\n----------------------------------------------------------------------------------------------------\n\n";
	private static final String TWO_SPACES = "  ";
	private static final String FOUR_SPACES = TWO_SPACES + TWO_SPACES;
	private static final String SIX_SPACES = FOUR_SPACES + TWO_SPACES;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd_HH.mm.ss");
	private static final int SPACES = 32;
	
	public static String getFileContent() {
		ChatPluginManagers managers = ChatPluginManagers.getInstance();
		StringBuilder sb = new StringBuilder(HEADER);
		Runtime runtime = Runtime.getRuntime();
		List<PluginInfo> plugins = Utils.getPluginsInfo();
		
		sb.append("Environment: ").append(VersionUtils.getImplementationName()).append(' ').append(VersionUtils.getImplementationVersion()).append('\n')
		.append("Minecraft version: ").append(VersionUtils.getVersion().format()).append(" (protocol: ").append(VersionUtils.getVersion().getProtocol()).append(")\n")
		.append("ChatPlugin version: ").append(ChatPlugin.VERSION).append(" (").append(ChatPlugin.getInstance().isPremium() ? "Premium" : "Free").append(")\n")
		.append('\n')
		.append("OS: ").append(System.getProperty("os.name")).append(' ').append(System.getProperty("os.version")).append(", ").append(System.getProperty("os.arch")).append('\n')
		.append("Java version: ").append(System.getProperty("java.version")).append('\n')
		.append("CPU threads: ").append(runtime.availableProcessors()).append("x\n")
		.append("Active threads: ").append(Thread.activeCount()).append("x\n")
		.append('\n')
		.append("Uptime: ").append(Utils.formatTime(ManagementFactory.getRuntimeMXBean().getUptime())).append('\n')
		.append("Startup time: ").append(ChatPlugin.getInstance().getStartupTime()).append(" ms\n")
		.append("Last reload time: ").append(ChatPlugin.getInstance().getLastReloadTime()).append(" ms\n")
		.append('\n');
		
		if (!Environment.isProxy()) {
			double[] tps = TPSManager.getInstance().getRecentTPS();
			
			sb.append("TPS: ").append(Utils.truncate(tps[0], 2)).append(", ").append(Utils.truncate(tps[1], 2)).append(", ").append(Utils.truncate(tps[2], 2)).append('\n');
			sb.append('\n');
		} sb.append("Players:\n");
		sb.append(TWO_SPACES).append("online: ").append(PlayerAdapter.getOnlinePlayers().size()).append('/').append(Utils.getMaxPlayers()).append('\n');
		sb.append(TWO_SPACES).append("loaded: ").append(PlayerManager.getInstance().getTotalPlayers()).append('\n');
		sb.append('\n');
		sb.append("Memory:\n");
		sb.append(TWO_SPACES).append("used: ").append(MemoryUtils.formatMemory(runtime.totalMemory() - runtime.freeMemory(), MemoryUtils.MEGABYTE)).append('/').append(MemoryUtils.formatMemory(runtime.maxMemory(), MemoryUtils.MEGABYTE)).append(" MB\n");
		sb.append(TWO_SPACES).append("allocated: ").append(MemoryUtils.formatMemory(runtime.totalMemory(), MemoryUtils.MEGABYTE)).append(" MB\n");
		sb.append(TWO_SPACES).append("free: ").append(MemoryUtils.formatMemory(runtime.freeMemory(), MemoryUtils.MEGABYTE)).append(" MB\n");
		sb.append('\n');
		sb.append("Storage:\n");
		sb.append(TWO_SPACES).append("used: ").append(MemoryUtils.formatMemory(Utils.getTotalStorage() - Utils.getFreeStorage(), MemoryUtils.GIGABYTE)).append('/').append(MemoryUtils.formatMemory(Utils.getTotalStorage(), MemoryUtils.GIGABYTE)).append(" GB\n");
		sb.append(TWO_SPACES).append("free: ").append(MemoryUtils.formatMemory(Utils.getFreeStorage(), MemoryUtils.GIGABYTE)).append(" GB\n");
		sb.append('\n');
		sb.append("Managers:\n");
		sb.append(TWO_SPACES).append("enabled: ").append(Utils.getStringFromList(getEnabledManagersNames(), false, true)).append('\n');
		sb.append(TWO_SPACES).append("amount: ").append(managers.getEnabledManagers().size()).append('/').append(managers.getManagers().size()).append('\n');
		sb.append(TWO_SPACES).append("values:\n");
		managers.getManagers().keySet().stream().filter(clazz -> managers.getManager(clazz).isEnabled()).forEach(clazz -> sb.append(FOUR_SPACES).append(clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Manager"))).append(":\n").append(getContent(clazz)));
		sb.append("Plugins:\n");
		sb.append(TWO_SPACES).append("amount: ").append(plugins.size()).append('\n');
		sb.append(TWO_SPACES).append("values:\n");
		plugins.stream().forEach(plugin -> sb.append(FOUR_SPACES).append(plugin.getName()).append(plugin.isEnabled() ? "" : "*").append(getSpaces(SPACES - plugin.getName().length() - (plugin.isEnabled() ? 0 : 1))).append(plugin.getVersion()).append(getSpaces(SPACES - plugin.getVersion().length())).append(Utils.getStringFromList(plugin.getAuthors(), false, false)).append('\n'));
		return ChatColor.stripColor(sb.toString());
	}
	
	public static List<String> getEnabledManagersNames() {
		return ChatPluginManagers.getInstance().getManagers().keySet().stream().filter(clazz -> ChatPluginManagers.getInstance().getManager(clazz).isEnabled()).map(clazz -> clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Manager"))).collect(Collectors.toList());
	}
	
	public static String getContent(Class<? extends ChatPluginManager> clazz) {
		return formatFields(getInstanceFields(clazz, ChatPluginManagers.getInstance().getManager(clazz))) + '\n';
	}
	
	public static String writeToFile() {
		File file = new File(ChatPlugin.getInstance().getDataFolder().getAbsolutePath() + File.separator + "debug", DATE_FORMAT.format(new Date()) + ".txt");
		
		if (file.exists())
			return Utils.NOT_APPLICABLE;
		try {
			if (file.getParentFile() != null)
				file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return Utils.NOT_APPLICABLE;
		} try (FileWriter writer = new FileWriter(file) ) {
			file.createNewFile();
			writer.write(getFileContent());
		} catch (IOException e) {
			e.printStackTrace();
		} return file.getName();
	}
	
	public static String formatFields(LinkedHashMap<String, Object> fields) {
		StringBuilder sb = new StringBuilder();
		
		for (Entry<String, Object> field : fields.entrySet()) {
			String type = Utils.abbreviate(field.getKey().substring(field.getKey().indexOf(':') + 1), SPACES, false);
			String name = Utils.abbreviate(field.getKey().substring(0, field.getKey().indexOf(':')), SPACES, false);
			
			sb.append(SIX_SPACES).append(type).append(getSpaces(SPACES - type.length())).append("§e").append(name).append(getSpaces(SPACES - name.length())).append(toString(field.getValue())).append('\n');
		} return sb.toString();
	}
	
	@SuppressWarnings("all") // ...used to avoid "Unnecessary @SuppressWarnings("deprecation")" for the annotation below when using Java 8 on IDEs like Eclipse
	public static LinkedHashMap<String, Object> getInstanceFields(Class<? extends ChatPluginManager> clazz, ChatPluginManager manager) {
		LinkedHashMap<String, Object> fields = new LinkedHashMap<>();
		Class<?> clazz2 = manager.getClass();
		
		while (clazz2 != clazz)
			clazz2 = clazz2.getSuperclass();
		for (Field field : clazz2.getDeclaredFields()) {
			if (field.getName().startsWith("$"))
				continue;
			try {
				field.get(null);
			} catch (NoClassDefFoundError e) {
				continue;
			} catch (NullPointerException | IllegalArgumentException | IllegalAccessException e) {
				@SuppressWarnings("deprecation")
				boolean accessible = field.isAccessible();
				
				if (!accessible)
					field.setAccessible(true);
				Object value;
				
				try {
					value = field.get(manager);
				} catch (NoClassDefFoundError e2) {
					continue;
				} catch (IllegalArgumentException | IllegalAccessException e2) {
					value = null;
				} fields.put(field.getName() + ":" + (field.getType().isPrimitive() ? "§c" + (field.getClass().equals(Integer.class) ? "int" : field.getClass().equals(Character.class) ? "char" : field.getType().getSimpleName().toLowerCase()) : field.getType().isEnum() ? "§5" + field.getType().getSimpleName() : "§9" + field.getType().getSimpleName()), field.isAnnotationPresent(SensitiveData.class) ? "<hidden>" : value);
				
				if (!accessible)
					field.setAccessible(false);
			}
		} return fields;
	}
	
	public static String toString(Object object) {
		if (object == null)
			return "§4null";
		if (object instanceof Boolean)
			return "§6" + String.valueOf((boolean) object);
		if (object instanceof Byte)
			return "§d" + String.valueOf((byte) object);
		if (object instanceof Character)
			return "§d" + String.valueOf((char) object);
		if (object instanceof Short)
			return "§d" + String.valueOf((short) object);
		if (object instanceof Integer)
			return "§d" + String.valueOf((int) object);
		if (object instanceof Long)
			return "§d" + String.valueOf((long) object) + "L";
		if (object instanceof Float)
			return "§d" + String.valueOf(Utils.truncate((float) object, 2)) + "F";
		if (object instanceof Double)
			return "§d" + String.valueOf(Utils.truncate((double) object, 2)) + "D";
		if (object instanceof String)
			return "§3\"" + ((String) object).replace("\n", "\\n") + "§3\"";
		if (object instanceof Enum)
			return "§a" + ((Enum<?>) object).name();
		if (object.getClass().isArray()) {
			return "§a" + Array.getLength(object) + " elements";
		} if (object instanceof List)
			return "§a" + ((List<?>) object).size() + " elements";
		if (object instanceof Map)
			return "§a" + ((Map<?, ?>) object).size() + " entries";
		try {
			return "§2" + (String) object.getClass().getDeclaredMethod("toString").invoke(object);
		} catch (NoClassDefFoundError | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return "§b" + (object.getClass().isAnonymousClass() ? object.getClass().getTypeName() : object.getClass().getSimpleName()) + " object";
		}
	}
	
	public static String getSpaces(int amount) {
		StringBuilder sb = new StringBuilder(amount = amount < 0 ? 0 : amount);
		
		for (int i = 0; i < amount; i++)
			sb.append(" ");
		return sb.toString();
	}
	
}
