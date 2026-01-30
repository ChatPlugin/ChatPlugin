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

package me.remigio07.chatplugin.api.server.util;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.serializer.TextSerializers;

import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import net.minecraft.text.Text;

/**
 * Server utils class.
 */
public class Utils extends me.remigio07.chatplugin.api.common.util.Utils {
	
	/**
	 * Gets the online players in the specified world.
	 * 
	 * @param world World to check
	 * @return Online players
	 */
	public static int getOnlineWorld(String world) {
		return (int) (Environment.isBukkit() ? Bukkit.getWorld(world).getPlayers().size() : Sponge.getServer().getWorld(world).get().getEntities().stream().filter(entity -> entity instanceof Player).count()); // Sponge v4.2
	}
	
	/**
	 * Formats the specified date according to one of the formats specified
	 * at "misc.simple-date-format" in the given language's messages' file.
	 * 
	 * @param ms Date to format
	 * @param language Language used to translate the date
	 * @param format Format to use
	 * @return Formatted date
	 */
	public static String formatDate(long ms, Language language, DateFormat format) { // TODO: associate a Map<DateFormat, SimpleDateFormat> to every Language; use zoned dates instead
		try {
			long now = System.currentTimeMillis();
			return new SimpleDateFormat(language.getMessage("misc.simple-date-format." + format.name().toLowerCase())).format(new Date(ms + PlayerManager.getInstance().getDisplayedTimeZone().getOffset(now) - TimeZone.getDefault().getOffset(now)));
		} catch (IllegalArgumentException iae) {
			LogManager.log("Invalid date format specified at \"{0}\" in {1}: {2}", 2, "misc.simple-date-format." + format.name().toLowerCase(), language.getConfiguration().getPath().getFileName().toString(), iae.getLocalizedMessage());
			return "Invalid date format specified at \"misc.simple-date-format.\"" + format.name().toLowerCase();
		}
	}
	
	/**
	 * Formats the specified time according to the messages specified
	 * at "timestamps" in the given language's messages' file.
	 * 
	 * @param totalMilliseconds Time to format
	 * @param language Language used to translate the time
	 * @param everInsteadOfNever Whether to use "timestamps.ever" instead of "timestamps.never"
	 * @param useZeroSecondsInstead Whether to use "0s" instead of "timestamps.ever"
	 * @return Formatted time
	 */
	public static String formatTime(long totalMilliseconds, Language language, boolean everInsteadOfNever, boolean useZeroSecondsInstead) {
		StringBuilder sb = new StringBuilder();
		long totalSeconds = (totalMilliseconds + 999) / 1000L;
		
		if (totalMilliseconds == -1)
			return language.getMessage(everInsteadOfNever ? "timestamps.ever" : "timestamps.never");
		if (totalSeconds < 1)
			return useZeroSecondsInstead ? "0" + language.getMessage("timestamps.seconds") : language.getMessage("timestamps.now");
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
			sb.append(years + (years == 1 ? "{year}, " : "{years}, "));
		if (months != 0)
			sb.append(months + (months == 1 ? "{month}, " : "{months}, "));
		if (weeks != 0)
			sb.append(weeks + (weeks == 1 ? "{week}, " : "{weeks}, "));
		if (days != 0)
			sb.append(days + (days == 1 ? "{day}, " : "{days}, "));
		if (hours != 0)
			sb.append(hours + (hours == 1 ? "{hour}, " : "{hours}, "));
		if (minutes != 0)
			sb.append(minutes + (minutes == 1 ? "{minute}, " : "{minutes}, "));
		if (totalSeconds != 0 || (useZeroSecondsInstead && minutes == 0 && hours == 0 && days == 0 && weeks == 0 && months == 0 && years == 0))
			sb.append(totalSeconds + (totalSeconds == 1 ? "{second}" : "{seconds}"));
		else sb.delete(sb.length() - 2, sb.length());
		
		return sb.toString()
				.replace("{second}", language.getMessage("timestamps.second"))
				.replace("{seconds}", language.getMessage("timestamps.seconds"))
				.replace("{minute}", language.getMessage("timestamps.minute"))
				.replace("{minutes}", language.getMessage("timestamps.minutes"))
				.replace("{hour}", language.getMessage("timestamps.hour"))
				.replace("{hours}", language.getMessage("timestamps.hours"))
				.replace("{day}", language.getMessage("timestamps.day"))
				.replace("{days}", language.getMessage("timestamps.days"))
				.replace("{week}", language.getMessage("timestamps.week"))
				.replace("{weeks}", language.getMessage("timestamps.weeks"))
				.replace("{month}", language.getMessage("timestamps.month"))
				.replace("{months}", language.getMessage("timestamps.months"))
				.replace("{year}", language.getMessage("timestamps.year"))
				.replace("{years}", language.getMessage("timestamps.years"));
	}
	
	/**
	 * Converts the specified Sponge component to legacy text.
	 * 
	 * @param spongeComponent Sponge component to convert
	 * @return Resulting legacy text
	 */
	@SuppressWarnings("deprecation")
	public static String toLegacyText(org.spongepowered.api.text.Text spongeComponent) {
		return TextSerializers.LEGACY_FORMATTING_CODE.serialize(spongeComponent);
	}
	
	/**
	 * Converts the specified Fabric component to legacy text.
	 * 
	 * @param fabricComponent Fabric component to convert
	 * @return Resulting legacy text
	 */
	public static String toLegacyText(Text fabricComponent) {
		try {
			return (String) Class.forName("me.remigio07.chatplugin.server.fabric.ChatPluginFabric", false, JARLibraryLoader.getInstance()).getMethod("toLegacyText", Text.class).invoke(null, fabricComponent);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			throw new IllegalStateException("Unable to call Utils#toLegacyText(Text) as the plugin has not finished loading yet");
		}
	}
	
	/**
	 * Converts the specified legacy text to a Sponge component.
	 * 
	 * @param legacyText Legacy text to convert
	 * @return Resulting Sponge component
	 */
	@SuppressWarnings("deprecation")
	public static org.spongepowered.api.text.Text toSpongeComponent(String legacyText) {
		return TextSerializers.LEGACY_FORMATTING_CODE.deserialize(legacyText);
	}
	
	/**
	 * Converts the specified legacy text to a Fabric component.
	 * 
	 * @param legacyText Legacy text to convert
	 * @return Resulting Fabric component
	 */
	public static Text toFabricComponent(String legacyText) {
		try {
			return (Text) Class.forName("me.remigio07.chatplugin.server.fabric.ChatPluginFabric", false, JARLibraryLoader.getInstance()).getMethod("toFabricComponent", String.class).invoke(null, legacyText);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			throw new IllegalStateException("Unable to call Utils#toFabricComponent(String) as the plugin has not finished loading yet");
		}
	}
	
}
