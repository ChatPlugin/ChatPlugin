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

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.SensitiveData;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.util.manager.MSPTManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07.chatplugin.bootstrap.Environment;

public class Debugger {
	
	private static final String HEADER =
			"-----------------------------------------------------------------------------------------------------\n"
			+ "#     __       __  ___  __            __            __      __   __  __        __   __   __  __     #\n"
			+ "#    /   |__| |__|  |  |__) |   |  | | _  | |\\ | ' |__     |  \\ |_  |__) |  | | _  | _  |_  |__)    #\n"
			+ "#    \\__ |  | |  |  |  |    |__ |__| |__| | | \\|    __|    |__/ |__ |__) |__| |__| |__| |__ | \\     #\n"
			+ "#                                                                                                   #\n"
			+ "-----------------------------------------------------------------------------------------------------\n\n";
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
		
		sb
				.append("Environment: ").append(VersionUtils.getImplementationName()).append(' ').append(VersionUtils.getImplementationVersion()).append('\n')
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
				.append("Last reload time: ").append(ChatPlugin.getInstance().getLastReloadTime()).append(" ms\n");
		
		if (!Environment.isProxy()) {
			double[] tps = TPSManager.getInstance().getRecentTPS();
			
			sb
					.append("\nTPS: ")
					.append(Utils.truncate(tps[0], 2)).append(", ")
					.append(Utils.truncate(tps[1], 2)).append(", ")
					.append(Utils.truncate(tps[2], 2)).append('\n');
			
			if (MSPTManager.getInstance().isEnabled()) {
				double[] avg = MSPTManager.getInstance().getAverageMSPT(), min = MSPTManager.getInstance().getMinimumMSPT(), max = MSPTManager.getInstance().getMaximumMSPT();
				
				sb
						.append("\nMSPT:\n")
						.append(TWO_SPACES).append("5s: ").append(Utils.truncate(avg[0], 2)).append(" ms/").append(Utils.truncate(min[0], 2)).append(" ms/").append(Utils.truncate(max[0], 2)).append(" ms\n")
						.append(TWO_SPACES).append("10s: ").append(Utils.truncate(avg[1], 2)).append(" ms/").append(Utils.truncate(min[1], 2)).append(" ms/").append(Utils.truncate(max[1], 2)).append(" ms\n")
						.append(TWO_SPACES).append("1m: ").append(Utils.truncate(avg[2], 2)).append(" ms/").append(Utils.truncate(min[2], 2)).append(" ms/").append(Utils.truncate(max[2], 2)).append(" ms\n");
			}
		} sb
				.append("\nPlayers:\n")
				.append(TWO_SPACES).append("online: ").append(PlayerAdapter.getOnlinePlayers().size()).append('/').append(Utils.getMaxPlayers()).append('\n')
				.append(TWO_SPACES).append("loaded: ").append(PlayerManager.getInstance().getPlayers().size()).append('\n')
				.append("\nMemory:\n")
				.append(TWO_SPACES).append("used: ").append(MemoryUtils.formatMemory(runtime.totalMemory() - runtime.freeMemory(), MemoryUtils.MEGABYTE)).append('/').append(MemoryUtils.formatMemory(runtime.maxMemory(), MemoryUtils.MEGABYTE)).append(" MB\n")
				.append(TWO_SPACES).append("allocated: ").append(MemoryUtils.formatMemory(runtime.totalMemory(), MemoryUtils.MEGABYTE)).append(" MB\n")
				.append(TWO_SPACES).append("free: ").append(MemoryUtils.formatMemory(runtime.freeMemory(), MemoryUtils.MEGABYTE)).append(" MB\n")
				.append("\nStorage:\n")
				.append(TWO_SPACES).append("used: ").append(MemoryUtils.formatMemory(Utils.getTotalStorage() - Utils.getFreeStorage(), MemoryUtils.GIGABYTE)).append('/').append(MemoryUtils.formatMemory(Utils.getTotalStorage(), MemoryUtils.GIGABYTE)).append(" GB\n")
				.append(TWO_SPACES).append("unallocated: ").append(MemoryUtils.formatMemory(Utils.getUnallocatedStorage(), MemoryUtils.GIGABYTE)).append(" GB\n")
				.append(TWO_SPACES).append("free: ").append(MemoryUtils.formatMemory(Utils.getFreeStorage(), MemoryUtils.GIGABYTE)).append(" GB\n")
				.append("\nPlugins:\n")
				.append(TWO_SPACES).append("amount: ").append(plugins.size()).append('\n')
				.append(TWO_SPACES).append("values:\n");
		plugins.stream().forEach(plugin -> sb.append(FOUR_SPACES).append(plugin.getName()).append(plugin.isEnabled() ? "" : "*").append(getSpaces(SPACES - plugin.getName().length() - (plugin.isEnabled() ? 0 : 1))).append(plugin.getVersion()).append(getSpaces(SPACES - plugin.getVersion().length())).append(Utils.getStringFromList(plugin.getAuthors(), false, false)).append('\n'));
		sb
				.append("\nManagers:\n")
				.append(TWO_SPACES).append("enabled: ").append(Utils.getStringFromList(getEnabledManagersNames(), false, true)).append('\n')
				.append(TWO_SPACES).append("amount: ").append(managers.getEnabledManagers().size()).append('/').append(managers.getManagers().size()).append('\n')
				.append(TWO_SPACES).append("values:\n");
		managers.getManagers().keySet().stream().filter(clazz -> managers.getManager(clazz).isEnabled()).forEach(clazz -> sb.append(FOUR_SPACES).append(clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Manager"))).append(":\n").append(getFileContent(clazz)));
		return sb.toString();
	}
	
	public static String getFileContent(Class<? extends ChatPluginManager> clazz) {
		StringBuilder sb = new StringBuilder();
		
		for (Entry<Field, Object> field : getFields(clazz, ChatPluginManagers.getInstance().getManager(clazz)).entrySet()) {
			String type = Utils.abbreviate(field.getKey().getType().getSimpleName(), SPACES - 1, false);
			String name = Utils.abbreviate(field.getKey().getName(), SPACES - 1, false);
			
			sb.append(SIX_SPACES).append(type).append(getSpaces(SPACES - type.length())).append(name).append(getSpaces(SPACES - name.length())).append(getValue(field.getValue())).append('\n');
		} return sb.append('\n').toString();
	}
	
	public static String getChatContent(Class<? extends ChatPluginManager> clazz) {
		StringBuilder sb = new StringBuilder();
		
		for (Entry<Field, Object> field : getFields(clazz, ChatPluginManagers.getInstance().getManager(clazz)).entrySet())
			sb.append(formatVariable(field.getKey())).append(formatValue(field.getValue())).append('\n');
		return sb.append('\n').toString();
	}
	
	public static String writeToFile() {
		Path folder = ChatPlugin.getInstance().getDataFolder().resolve("debug");
		Path path = folder.resolve(DATE_FORMAT.format(new Date()) + ".txt");
		
		try {
			if (Files.exists(path))
				return Utils.NOT_APPLICABLE;
			else if (!Files.exists(folder))
				Files.createDirectories(folder);
			try (BufferedWriter writer = Files.newBufferedWriter(path)) {
				writer.write(getFileContent());
			} return path.getFileName().toString();
		} catch (IOException ioe) {
			LogManager.log("IOException occurred while creating or writing to debug file \"{0}\": {1}", 2, path.getFileName().toString(), ioe.getLocalizedMessage());
			return Utils.NOT_APPLICABLE;
		}
	}
	
	@SuppressWarnings("all") // ...used to avoid "Unnecessary @SuppressWarnings("deprecation")" for the annotation below when using Java 8 on IDEs like Eclipse
	public static Map<Field, Object> getFields(Class<? extends ChatPluginManager> clazz, ChatPluginManager manager) {
		Map<Field, Object> fields = new LinkedHashMap<>();
		Class<?> managerClass = manager.getClass();
		
		while (managerClass != clazz)
			managerClass = managerClass.getSuperclass();
		for (Field field : managerClass.getDeclaredFields()) {
			if (field.getName().startsWith("$"))
				continue;
			try {
				field.get(null);
			} catch (NoClassDefFoundError ncdfe) {
				continue;
			} catch (NullPointerException | IllegalArgumentException | IllegalAccessException e) {
				@SuppressWarnings("deprecation")
				boolean accessible = field.isAccessible();
				
				if (!accessible)
					field.setAccessible(true);
				Object value;
				
				try {
					value = field.get(manager);
				} catch (NoClassDefFoundError ncdfe) {
					continue;
				} catch (IllegalArgumentException | IllegalAccessException e2) {
					value = null;
				} fields.put(field, field.isAnnotationPresent(SensitiveData.class) ? "<hidden>" : value);
				
				if (!accessible)
					field.setAccessible(false);
			}
		} return fields;
	}
	
	public static String formatVariable(Field field) {
		StringBuilder sb = new StringBuilder();
		Class<?> type = field.getType();
		
		if (type.isPrimitive() || (type.isArray() && type.getComponentType().isPrimitive()))
			sb.append("§6§l");
		else if (type.isInterface())
			sb.append("§b");
		else if (Modifier.isAbstract(type.getModifiers()))
			sb.append("§9");
		else if (type.isEnum() || PseudoEnum.class.isAssignableFrom(type))
			sb.append("§d");
		else sb.append("§f");
		
		sb.append(type.getSimpleName().replace("[]", "§8[]")).append(" §e");
		
		if (Modifier.isStatic(field.getModifiers())) {
			if (Modifier.isFinal(field.getModifiers()))
				sb.append("§l");
			sb.append("§o");
		} return sb.append(field.getName()).append(" §8= ").toString();
	}
	
	public static String getValue(Object object) {
		if (object == null)
			return "null";
		if (object instanceof Boolean)
			return String.valueOf((boolean) object);
		if (object instanceof Byte)
			return String.valueOf((byte) object);
		if (object instanceof Character)
			return "'" + Utils.escape(String.valueOf((char) object), false) + "'";
		if (object instanceof Short)
			return String.valueOf((short) object);
		if (object instanceof Integer)
			return String.valueOf((int) object);
		if (object instanceof Long)
			return String.valueOf((long) object) + "L";
		if (object instanceof Float)
			return String.valueOf(Utils.truncate((float) object, 2)) + "F";
		if (object instanceof Double)
			return String.valueOf(Utils.truncate((double) object, 2)) + "D";
		if (object instanceof CharSequence)
			return "\"" + Utils.escape(((CharSequence) object).toString(), false) + "\"";
		if (object instanceof Enum)
			return ((Enum<?>) object).name();
		if (object instanceof PseudoEnum)
			return ((PseudoEnum<?>) object).name();
		if (object.getClass().isArray())
			return getArrayValue(object);
		if (object instanceof Collection)
			return getValue((Collection<?>) object);
		if (object instanceof Map)
			return getValue((Map<?, ?>) object);
		try {
			return (String) object.getClass().getDeclaredMethod("toString").invoke(object);
		} catch (NoClassDefFoundError | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return (object.getClass().isAnonymousClass() ? object.getClass().getTypeName() : object.getClass().getSimpleName()) + " object";
		}
	}
	
	public static String getArrayValue(Object array) {
		StringJoiner sj = new StringJoiner(", ", "" + array.getClass().getSimpleName() + " (" + Array.getLength(array) + "x) [", "]");
		
		for (int i = 0; i < Array.getLength(array); i++) {
			Object o = Array.get(array, i);
			
			sj.add(o == array ? "[…]" : getValue(o));
		} return sj.toString();
	}
	
	public static String getValue(Collection<?> collection) {
		StringJoiner sj = new StringJoiner(", ", "" + collection.getClass().getSimpleName() + " (" + collection.size() + "x) [", "]");
		
		for (Object e : collection)
			sj.add(e == collection ? "[…]" : getValue(e));
		return sj.toString();
	}
	
	public static String getValue(Map<?, ?> map) {
		StringJoiner sj = new StringJoiner(", ", "" + map.getClass().getSimpleName() + " (" + map.size() + "x) [", "]");
		
		for (Entry<?, ?> entry : map.entrySet())
			sj.add((entry.getKey() == map ? "[…]" : getValue(entry.getKey())) + ": " + (entry.getValue() == map ? "[…]" : getValue(entry.getValue())));
		return sj.toString();
	}
	
	public static String formatValue(Object object) {
		if (object == null)
			return "§1§lnull";
		if (object instanceof Boolean)
			return (boolean) object ? "§2true" : "§4false";
		if (object instanceof Byte)
			return "§a" + String.valueOf((byte) object);
		if (object instanceof Character)
			return "§3'" + Utils.escape(String.valueOf((char) object), false) + "'";
		if (object instanceof Short)
			return "§a" + String.valueOf((short) object);
		if (object instanceof Integer)
			return "§a" + String.valueOf((int) object);
		if (object instanceof Long)
			return "§a" + String.valueOf((long) object) + "L";
		if (object instanceof Float)
			return "§a" + String.valueOf(Utils.truncate((float) object, 2)) + "F";
		if (object instanceof Double)
			return "§a" + String.valueOf(Utils.truncate((double) object, 2)) + "D";
		if (object instanceof CharSequence)
			return "§3\"" + Utils.escape(ChatColor.translate(((CharSequence) object).toString()), false) + "§3\"";
		if (object instanceof Enum)
			return "§c" + ((Enum<?>) object).name();
		if (object instanceof PseudoEnum)
			return "§c" + ((PseudoEnum<?>) object).name();
		if (object.getClass().isArray())
			return formatArrayValue(object);
		if (object instanceof Collection)
			return formatValue((Collection<?>) object);
		if (object instanceof Map)
			return formatValue((Map<?, ?>) object);
		try {
			return "§5" + (String) object.getClass().getDeclaredMethod("toString").invoke(object);
		} catch (NoClassDefFoundError | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			return "§f" + (object.getClass().isAnonymousClass() ? object.getClass().getTypeName() : object.getClass().getSimpleName()) + " object";
		}
	}
	
	public static String formatArrayValue(Object array) {
		StringJoiner sj = new StringJoiner("§8, ", "§f" + (array.getClass().getComponentType().isPrimitive() ? "§6§l" : "") + array.getClass().getSimpleName().replace("[]", "§8[]") + " §8(§f" + Array.getLength(array) + "x§8) [", "§8]");
		
		for (int i = 0; i < Array.getLength(array); i++) {
			Object o = Array.get(array, i);
			
			sj.add(o == array ? "[§f…§8]" : formatValue(o));
		} return sj.toString();
	}
	
	public static String formatValue(Collection<?> collection) {
		StringJoiner sj = new StringJoiner("§8, ", "§f" + collection.getClass().getSimpleName() + " §8(§f" + collection.size() + "x§8) [", "§8]");
		
		for (Object e : collection)
			sj.add(e == collection ? "[§f…§8]" : formatValue(e));
		return sj.toString();
	}
	
	public static String formatValue(Map<?, ?> map) {
		StringJoiner sj = new StringJoiner("§8, ", "§f" + map.getClass().getSimpleName() + " §8(§f" + map.size() + "x§8) [", "§8]");
		
		for (Entry<?, ?> entry : map.entrySet())
			sj.add((entry.getKey() == map ? "[§f…§8]" : formatValue(entry.getKey())) + "§8: " + (entry.getValue() == map ? "[§f…§8]" : formatValue(entry.getValue())));
		return sj.toString();
	}
	
	public static String getSpaces(int amount) {
		StringBuilder sb = new StringBuilder(amount = amount < 0 ? 0 : amount);
		
		for (int i = 0; i < amount; i++)
			sb.append(" ");
		return sb.toString();
	}
	
	public static List<String> getEnabledManagersNames() {
		return ChatPluginManagers.getInstance().getManagers().keySet().stream().filter(clazz -> ChatPluginManagers.getInstance().getManager(clazz).isEnabled()).map(clazz -> clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Manager"))).collect(Collectors.toList());
	}
	
}
