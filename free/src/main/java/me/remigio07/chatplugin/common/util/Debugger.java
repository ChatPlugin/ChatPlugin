/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
		
		sb.append("Environment: " + VersionUtils.getImplementationName() + " " + VersionUtils.getImplementationVersion() + "\n");
		sb.append("Minecraft version: " + VersionUtils.getVersion().format() + " (protocol: " + VersionUtils.getVersion().getProtocol() + ")\n");
		sb.append("ChatPlugin version: " + ChatPlugin.VERSION + "\n");
		sb.append("\n");
		sb.append("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + ", " + System.getProperty("os.arch") + "\n");
		sb.append("Java version: " + System.getProperty("java.version") + "\n");
		sb.append("CPU threads: " + runtime.availableProcessors() + "x\n");
		sb.append("Active threads: " + Thread.activeCount() + "x\n");
		sb.append("\n");
		sb.append("Uptime: " + Utils.formatTime(ManagementFactory.getRuntimeMXBean().getUptime()) + "\n");
		sb.append("Startup time: " + ChatPlugin.getInstance().getStartupTime() + " ms\n");
		sb.append("Last reload time: " + ChatPlugin.getInstance().getLastReloadTime() + " ms\n");
		sb.append("\n");
		
		if (!Environment.isProxy()) {
			double[] tps = TPSManager.getInstance().getRecentTPS();
			
			sb.append("TPS: " + Utils.truncate(tps[0], 2) + ", " + Utils.truncate(tps[1], 2) + ", " + Utils.truncate(tps[2], 2) + "\n");
			sb.append("\n");
		} sb.append("Players:\n");
		sb.append(TWO_SPACES + "online: " + PlayerAdapter.getOnlinePlayers().size() + "/" + Utils.getMaxPlayers() + "\n");
		sb.append(TWO_SPACES + "loaded: " + PlayerManager.getInstance().getTotalPlayers() + "\n");
		sb.append("\n");
		sb.append("Memory:\n");
		sb.append(TWO_SPACES + "used: " + MemoryUtils.formatMemory(runtime.totalMemory() - runtime.freeMemory(), MemoryUtils.MEGABYTE) + "/" + MemoryUtils.formatMemory(runtime.maxMemory(), MemoryUtils.MEGABYTE) + " MB\n");
		sb.append(TWO_SPACES + "allocated: " + MemoryUtils.formatMemory(runtime.totalMemory(), MemoryUtils.MEGABYTE) + " MB\n");
		sb.append(TWO_SPACES + "free: " + MemoryUtils.formatMemory(runtime.freeMemory(), MemoryUtils.MEGABYTE) + " MB\n");
		sb.append("\n");
		sb.append("Storage:\n");
		sb.append(TWO_SPACES + "used: " + MemoryUtils.formatMemory(Utils.getTotalStorage() - Utils.getFreeStorage(), MemoryUtils.GIGABYTE) + "/" + MemoryUtils.formatMemory(Utils.getTotalStorage(), MemoryUtils.GIGABYTE) + " GB\n");
		sb.append(TWO_SPACES + "free: " + MemoryUtils.formatMemory(Utils.getFreeStorage(), MemoryUtils.GIGABYTE) + " GB\n");
		sb.append("\n");
		sb.append("Managers:\n");
		sb.append(TWO_SPACES + "enabled: " + Utils.getStringFromList(getEnabledManagersNames(), false, true) + "\n");
		sb.append(TWO_SPACES + "amount: " + managers.getEnabledManagers().size() + "/" + managers.getManagers().size() + "\n");
		sb.append(TWO_SPACES + "values:\n");
		managers.getManagers().keySet().stream().filter(clazz -> managers.getManager(clazz).isEnabled()).forEach(clazz -> sb.append(FOUR_SPACES + clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Manager")) + ":\n" + getContent(clazz)));
		return ChatColor.stripColor(sb.toString());
	}
	
	// why not just return the stream using .collect(Collectors.toList())?
	public static List<String> getEnabledManagersNames() {
		List<String> names = new ArrayList<>();
		
		ChatPluginManagers.getInstance().getManagers().keySet().stream().filter(clazz -> ChatPluginManagers.getInstance().getManager(clazz).isEnabled()).forEach(clazz -> names.add(clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Manager"))));
		return names;
	}
	
	public static String getContent(Class<? extends ChatPluginManager> clazz) {
		return formatFields(getInstanceFields(clazz, ChatPluginManagers.getInstance().getManager(clazz))) + "\n";
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
			String type = field.getKey().substring(field.getKey().indexOf(':') + 1);
			String name = field.getKey().substring(0, field.getKey().indexOf(':'));
			
			if (type.length() > SPACES - 1)
				type = type.substring(0, SPACES - 4) + "...";
			if (name.length() > SPACES - 1)
				name = name.substring(0, SPACES - 4) + "...";
			sb.append(SIX_SPACES + type + getSpaces(SPACES - type.length()) + "\u00A7e" + name + getSpaces(SPACES - name.length()) + toString(field.getValue()) + "\n");
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
//					} fields.put(field.getName() + (value == null ? ":" + field.getGenericType().getTypeName() : ""), field.isAnnotationPresent(SensitiveData.class) ? "<hidden>" : value);
				} fields.put(field.getName() + ":" + (field.getType().isPrimitive() ? "\u00A7c" + (field.getClass().equals(Integer.class) ? "int" : field.getClass().equals(Character.class) ? "char" : field.getType().getSimpleName().toLowerCase()) : field.getType().isEnum() ? "\u00A75" + field.getType().getSimpleName() : "\u00A79" + field.getType().getSimpleName()), field.isAnnotationPresent(SensitiveData.class) ? "<hidden>" : value);
				
				if (!accessible)
					field.setAccessible(false);
			}
		} return fields;
	}
	
	public static String toString(Object object) {
		if (object == null)
			return "\u00A74null";
		if (object instanceof Boolean)
			return "\u00A76" + String.valueOf((boolean) object);
		if (object instanceof Byte)
			return "\u00A7d" + String.valueOf((byte) object);
		if (object instanceof Character)
			return "\u00A7d" + String.valueOf((char) object);
		if (object instanceof Short)
			return "\u00A7d" + String.valueOf((short) object);
		if (object instanceof Integer)
			return "\u00A7d" + String.valueOf((int) object);
		if (object instanceof Long)
			return "\u00A7d" + String.valueOf((long) object) + "L";
		if (object instanceof Float)
			return "\u00A7d" + String.valueOf(Utils.truncate((float) object, 2)) + "F";
		if (object instanceof Double)
			return "\u00A7d" + String.valueOf(Utils.truncate((double) object, 2)) + "D";
		if (object instanceof String)
			return "\u00A73\"" + object + "\u00A73\"";
		if (object instanceof Enum)
			return "\u00A7a" + ((Enum<?>) object).name();
		if (object.getClass().isArray()) {
			return "\u00A7a" + Array.getLength(object) + " elements";
		} if (object instanceof List)
			return "\u00A7a" + ((List<?>) object).size() + " elements";
		if (object instanceof Map)
			return "\u00A7a" + ((Map<?, ?>) object).size() + " entries";
		try {
			return "\u00A72" + (String) object.getClass().getDeclaredMethod("toString").invoke(object);
		} catch (NoClassDefFoundError | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return "\u00A7b" + (object.getClass().isAnonymousClass() ? object.getClass().getTypeName() : object.getClass().getSimpleName()) + " object";
		}
	}
	
	public static String getSpaces(int amount) {
		StringBuilder sb = new StringBuilder(amount);
		
		for (int i = 0; i < amount; i++)
			sb.append(" ");
		return sb.toString();
	}
	
}
