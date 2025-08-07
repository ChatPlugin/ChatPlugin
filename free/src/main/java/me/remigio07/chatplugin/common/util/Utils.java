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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.VersionChange;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import me.remigio07.chatplugin.bootstrap.VelocityBootstrapper;
import me.remigio07.chatplugin.common.util.text.ComponentTranslatorImpl;
import net.md_5.bungee.api.ProxyServer;

public class Utils extends me.remigio07.chatplugin.api.common.util.Utils {
	
	public static final Library[] RELOCATION_LIBS = new Library[] {
			Library.ASM,
			Library.ASM_COMMONS,
			Library.JAR_RELOCATOR
			};
	public static final Library[] ADVENTURE_LIBS = new Library[] {
			Library.ADVENTURE_API,
			Library.ADVENTURE_KEY,
			Library.ADVENTURE_NBT,
			Library.ADVENTURE_PLATFORM_API,
			Library.ADVENTURE_PLATFORM_FACET,
			Library.ADVENTURE_TEXT_SERIALIZER_GSON,
			Library.ADVENTURE_TEXT_SERIALIZER_GSON_LEGACY_IMPL,
			Library.ADVENTURE_TEXT_SERIALIZER_JSON,
			Library.ADVENTURE_TEXT_SERIALIZER_JSON_LEGACY_IMPL,
			Library.ADVENTURE_TEXT_SERIALIZER_LEGACY,
			Library.EXAMINATION_API,
			Library.EXAMINATION_STRING,
			Library.OPTION,
			Library.GSON,
			Library.JETBRAINS_ANNOTATIONS
	};
	
	@SuppressWarnings("deprecation")
	public static void initUtils() throws ChatPluginManagerException {
		try {
			for (Library library : RELOCATION_LIBS)
				LibrariesUtils.load(library);
			if (!Environment.isProxy()) {
				for (Library library : ADVENTURE_LIBS)
					LibrariesUtils.load(library);
				if (Environment.isBukkit())
					LibrariesUtils.load(Library.ADVENTURE_PLATFORM_BUKKIT);
				else if (Environment.isSponge())
					LibrariesUtils.load(Library.ADVENTURE_PLATFORM_SPONGEAPI);
			} LibrariesUtils.load(Library.JSON_SIMPLE);
		} catch (LibraryException e) {
			throw new ChatPluginManagerException("libraries utils", e.getMessage());
		} UUIDFetcherImpl.setInstance(new UUIDFetcherImpl());
		ComponentTranslatorImpl.setInstance(new ComponentTranslatorImpl());
	}
	
	public static void startUpdateChecker() {
		TaskManager.scheduleAsync(() -> {
			String latestVersion = getLatestVersion();
			
			if (latestVersion != null)
				LogManager.log("You are running an outdated version of ChatPlugin. It is recommended to update to the latest version ({0}) to avoid bugs and incompatibilities.", 1, latestVersion);
		}, 0L, 14400000L);
	}
	
	public static String getLatestVersion() {
		try (Scanner scanner = new Scanner(new URI("https://api.spigotmc.org/legacy/update.php?resource=115169/~").toURL().openStream())) {
			if (scanner.hasNext()) {
				String latestVersion = scanner.next();
				
				if (!latestVersion.equals(ChatPlugin.VERSION) && VersionChange.getVersionChange(ChatPlugin.VERSION, latestVersion).isSupported())
					return latestVersion;
			}
		} catch (Exception e) {
			LogManager.log("Unable to check for updates using SpigotMC's API: {0}", 2, e.getMessage());
		} return null;
	}
	
	public static boolean isPrivateEdition() {
		try {
			Class.forName("me.remigio07.chatplugin.ChatPluginPrivate", false, JARLibraryLoader.getInstance());
			return true;
		} catch (ClassNotFoundException cnfe) {
			return false;
		}
	}
	
	public static boolean isGeyserPlayer(UUID player) {
		try {
			Class<?> GeyserApi = Class.forName("org.geysermc.geyser.api.GeyserApi");
			return (boolean) GeyserApi.getMethod("isBedrockPlayer", UUID.class).invoke(GeyserApi.getMethod("api").invoke(null), player);
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} return false;
	}
	
	public static boolean isFloodgatePlayer(UUID player) {
		try {
			Class<?> FloodgateApi = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
			return (boolean) FloodgateApi.getMethod("isFloodgatePlayer", UUID.class).invoke(FloodgateApi.getMethod("getInstance").invoke(null), player);
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getFloodgateUsernamePrefix() {
		try {
			Class<?> FloodgateApi = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
			return (String) FloodgateApi.getMethod("getPlayerPrefix").invoke(FloodgateApi.getMethod("getInstance").invoke(null));
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			return ".";
		}
	}
	
	public static InputStream download(URL url) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		
		connection.setRequestProperty("User-Agent", USER_AGENT);
		connection.setConnectTimeout(5000);
		return connection.getInputStream();
	}
	
	public static String formatDate(long ms, String format) {
		return new SimpleDateFormat(format).format(new Date(ms));
	}
	
	public static String formatTime(long totalMilliseconds) {
		long totalSeconds = (totalMilliseconds + 999) / 1000L;
		
		if (totalSeconds < 1)
			return "0 seconds";
		StringBuilder sb = new StringBuilder();
		
		int years = (int) (totalSeconds / SECONDS_IN_A_YEAR);
		totalSeconds -= years * SECONDS_IN_A_YEAR;
		int months = (int) (totalSeconds / SECONDS_IN_A_MONTH);
		totalSeconds -= months * SECONDS_IN_A_MONTH;
		int days = (int) (totalSeconds / SECONDS_IN_A_DAY);
		totalSeconds -= days * SECONDS_IN_A_DAY;
		int weeks = 0;
		
		if (ConfigurationType.CONFIG.get().getBoolean("settings.use-week-timestamp")) {
			weeks = (int) (totalSeconds / SECONDS_IN_A_WEEK);
			totalSeconds -= weeks * SECONDS_IN_A_WEEK;
		} int hours = (int) (totalSeconds / SECONDS_IN_AN_HOUR);
		totalSeconds -= hours * SECONDS_IN_AN_HOUR;
		int minutes = (int) (totalSeconds / SECONDS_IN_A_MINUTE);
		totalSeconds -= minutes * SECONDS_IN_A_MINUTE;
		
		if (years != 0)
			sb.append(years + (years == 1 ? " year, " : " years, "));
		if (months != 0)
			sb.append(months + (months == 1 ? " month, " : " months, "));
		if (days != 0)
			sb.append(days + (days == 1 ? " day, " : " days, "));
		if (hours != 0)
			sb.append(hours + (hours == 1 ? " hour, " : " hours, "));
		if (minutes != 0)
			sb.append(minutes + (minutes == 1 ? " minute, " : " minutes, "));
		if (totalSeconds != 0)
			sb.append(totalSeconds + (totalSeconds == 1 ? " second" : " seconds"));
		else sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}
	
	public static String abbreviate(String input, int length, boolean stripColor) {
		String stripped = stripColor ? ChatColor.stripColor(input) : null;
		
		if ((stripColor ? stripped : input).length() <= length)
			return input;
		return input.substring(0, length + (stripColor ? input.length() - stripped.length() : 0) - 1) + '…';
	}
	
	public static void debugPrint(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String str : list)
			sb.append("\"" + str + "\", ");
		try {
			sb.delete(sb.length() - 2, sb.length());
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println("empty string");
			return;
		} System.out.println("[" + sb.toString() + "]");
	}
	
	public static List<PluginInfo> getPluginsInfo() {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			return Arrays.asList(Bukkit.getPluginManager().getPlugins()).stream().map(plugin -> new PluginInfo(plugin.getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthors(), plugin.isEnabled())).collect(Collectors.toList());
		case SPONGE:
			return Sponge.getPluginManager().getPlugins().stream().map(plugin -> new PluginInfo(plugin.getId(), plugin.getVersion().orElse("unknown"), plugin.getAuthors(), true)).collect(Collectors.toList());
		case BUNGEECORD:
			return ProxyServer.getInstance().getPluginManager().getPlugins().stream().map(plugin -> new PluginInfo(plugin.getDescription().getName(), plugin.getDescription().getVersion(), Arrays.asList(plugin.getDescription().getAuthor()), true)).collect(Collectors.toList());
		case VELOCITY:
			return VelocityBootstrapper.getInstance().getProxy().getPluginManager().getPlugins().stream().map(plugin -> new PluginInfo(plugin.getDescription().getId(), plugin.getDescription().getVersion().orElse("unknown"), plugin.getDescription().getAuthors(), true)).collect(Collectors.toList());
		} return null;
	}
	
	// https://gist.github.com/jjfiv/2ac5c081e088779f49aa
	public static String unescapeJSON(String json) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		
		while (i < json.length()) {
			char delimiter = json.charAt(i);
			i++;
			
			if (delimiter == '\\' && i < json.length()) {
				char ch = json.charAt(i);
				i++;
				
				switch (ch) {
				case '\\':
				case '/':
				case '"':
				case '\'':
					sb.append(ch);
					break;
				case 'n':
					sb.append('\n');
					break;
				case 'r':
					sb.append('\r');
					break;
				case 't':
					sb.append('\t');
					break;
				case 'b':
					sb.append('\b');
					break;
				case 'f':
					sb.append('\f');
					break;
				case 'u':
					StringBuilder hex = new StringBuilder();
					
					for (char x : json.substring(i, i + 4).toCharArray())
						hex.append(Character.toLowerCase(x));
					i += 4;
					
					sb.append((char) Integer.parseInt(hex.toString(), 16));
					break;
				}
			} else sb.append(delimiter);
		} return sb.toString();
	}
	
}
