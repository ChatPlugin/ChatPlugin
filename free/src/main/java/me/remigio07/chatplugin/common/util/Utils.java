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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.VersionChange;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import me.remigio07.chatplugin.bootstrap.VelocityBootstrapper;
import me.remigio07.chatplugin.common.util.text.ComponentTranslatorImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ChatVersion;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.chat.VersionedComponentSerializer;

public class Utils extends me.remigio07.chatplugin.api.common.util.Utils {
	
	public static final Library[] RELOCATION_LIBS = {
			Library.ASM,
			Library.ASM_COMMONS,
			Library.JAR_RELOCATOR
			};
	public static final Library[] BUNGEECORD_LIBS = {
			Library.BUNGEECORD_CHAT,
			Library.BUNGEECORD_DIALOG,
			Library.BUNGEECORD_SERIALIZER,
			Library.GSON
			};
	private static Gson GSON;
	
	@SuppressWarnings("deprecation")
	public static void initUtils() throws ChatPluginManagerException {
		try {
			for (Library library : RELOCATION_LIBS)
				LibrariesUtils.load(library);
			if (Environment.isFabric() && ConfigurationManager.getInstance().getLastVersionChange() != VersionChange.NULL)
				try {
					LibrariesUtils.downloadFreshCopy("Updating {0} library (new plugin version detected)...", 0, Library.SNAKEYAML);
				} catch (Throwable t) {
					throw new LibraryException(t, Library.SNAKEYAML);
				}
			if (!VersionUtils.isSpigot() && !Environment.isBungeeCord())
				for (Library library : BUNGEECORD_LIBS)
					LibrariesUtils.load(library);
			LibrariesUtils.load(Library.JSON_SIMPLE);
			
			Field gson = ComponentSerializer.class.getDeclaredField("gson");
			
			gson.setAccessible(true);
			
			GSON = (Gson) gson.get(null);
		} catch (LibraryException le) {
			throw new ChatPluginManagerException("libraries utils", le.getMessage());
		} catch (NoSuchFieldException | IllegalAccessException e) {
			GSON = VersionedComponentSerializer.getDefault().getGson();
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
	
	public static String getLatestVersion() { // TODO check this...
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
	
	public static List<PluginInfo> getPluginsInfo() {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			return Arrays.asList(Bukkit.getPluginManager().getPlugins()).stream().map(plugin -> new PluginInfo(plugin.getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthors(), plugin.isEnabled())).collect(Collectors.toList());
		case SPONGE:
			return Sponge.getPluginManager().getPlugins().stream().map(plugin -> new PluginInfo(plugin.getId(), plugin.getVersion().orElse("unknown"), plugin.getAuthors(), true)).collect(Collectors.toList());
		case FABRIC:
			return FabricLoader.getInstance().getAllMods().stream().map(mod -> mod.getMetadata()).map(data -> new PluginInfo(data.getName(), data.getVersion().getFriendlyString(), data.getAuthors().stream().map(Person::getName).collect(Collectors.toList()), true)).collect(Collectors.toList());
		case BUNGEECORD:
			return ProxyServer.getInstance().getPluginManager().getPlugins().stream().map(plugin -> new PluginInfo(plugin.getDescription().getName(), plugin.getDescription().getVersion(), Arrays.asList(plugin.getDescription().getAuthor()), true)).collect(Collectors.toList());
		case VELOCITY:
			return VelocityBootstrapper.getInstance().getProxy().getPluginManager().getPlugins().stream().map(plugin -> new PluginInfo(plugin.getDescription().getId(), plugin.getDescription().getVersion().orElse("unknown"), plugin.getDescription().getAuthors(), true)).collect(Collectors.toList());
		} return null;
	}
	
	// source of following 2 methods: https://gist.github.com/jjfiv/2ac5c081e088779f49aa - thank you jjfiv!
	
	public static String escape(String input, boolean escapeUnicode) {
		StringBuilder output = new StringBuilder();
		
		for (int i = 0; i < input.length(); i++) {
			char ch = input.charAt(i);
			int chx = (int) ch;
			
			switch (ch) {
			case '\n':
				output.append("\\n");
				break;
			case '\t':
				output.append("\\t");
				break;
			case '\r':
				output.append("\\r");
				break;
			case '\\':
				output.append("\\\\");
				break;
			case '"':
				output.append("\\\"");
				break;
			case '\b':
				output.append("\\b");
				break;
			case '\f':
				output.append("\\f");
				break;
			default:
				output.append(escapeUnicode && chx > 127 ? String.format("\\u%04x", chx) : ch);
				break;
			}
		} return output.toString();
	}
	
	public static String unescape(String input) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		
		while (i < input.length()) {
			char delimiter = input.charAt(i);
			i++;
			
			if (delimiter == '\\' && i < input.length()) {
				char ch = input.charAt(i);
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
					
					for (char x : input.substring(i, i + 4).toCharArray())
						hex.append(Character.toLowerCase(x));
					i += 4;
					
					sb.append((char) Integer.parseInt(hex.toString(), 16));
					break;
				}
			} else sb.append(delimiter);
		} return sb.toString();
	}
	
	public static String toLegacyText(BaseComponent bungeeCordComponent) {
		return BaseComponent.toLegacyText(bungeeCordComponent);
	}
	
	public static String toLegacyText(JsonElement json) {
		return toLegacyText(toBungeeCordComponent(json));
	}
	
	@SuppressWarnings("deprecation")
	public static JsonElement toJSON(BaseComponent bungeeCordComponent) {
		return VersionUtils.getVersion().isAtLeast(Version.V1_21_5)
				? VersionedComponentSerializer.forVersion(ChatVersion.V1_21_5).toJson(bungeeCordComponent)
				: new JsonParser().parse(ComponentSerializer.toString(bungeeCordComponent));
	}
	
	public static JsonElement toJSON(String legacyText) {
		return toJSON(toBungeeCordComponent(legacyText));
	}
	
	public static BaseComponent toBungeeCordComponent(JsonElement json) {
		return VersionUtils.getVersion().isAtLeast(Version.V1_21_5)
				? VersionedComponentSerializer.forVersion(ChatVersion.V1_21_5).deserialize(json)
				: json.isJsonArray()
				? GSON.fromJson(json, BaseComponent[].class)[0]
				: GSON.fromJson(json, BaseComponent.class); 
	}
	
	public static BaseComponent toBungeeCordComponent(String legacyText) {
		return BungeeCordComponent.toBungeeCordComponent(legacyText);
	}
	
	public static HoverEvent getHoverEvent(HoverEvent.Action action, String value) {
		return BungeeCordComponent.getHoverEvent(action, value);
	}
	
	private static class BungeeCordComponent {
		
		@SuppressWarnings("deprecation")
		public static BaseComponent toBungeeCordComponent(String legacyText) {
			BaseComponent[] components = TextComponent.fromLegacyText(legacyText);
			
			if (components.length == 1)
				return checkClickEvent(components[0]);
			return checkClickEvent(new TextComponent(components));
		}
		
		// prevent URISyntaxExceptions when converting to Vanilla components
		private static BaseComponent checkClickEvent(BaseComponent component) {
			ClickEvent clickEvent = component.getClickEvent();
			
			if (clickEvent != null && clickEvent.getAction() == Action.OPEN_URL)
				try {
					new URI(clickEvent.getValue());
				} catch (URISyntaxException urise) {
					component.setClickEvent(null);
				}
			if (component.getExtra() != null)
				for (BaseComponent extra : component.getExtra())
					checkClickEvent(extra);
			return component;
		}
		
		@SuppressWarnings("deprecation")
		public static HoverEvent getHoverEvent(HoverEvent.Action action, String value) {
			return new HoverEvent(action, new BaseComponent[] { toBungeeCordComponent(value) });
		}
		
	}
	
}
