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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Server utils class. Methods here are not documented.
 */
public class Utils extends me.remigio07.chatplugin.api.common.util.Utils {
	
	public static Map<Language, String> getLanguageStringMap(Configuration configuration, String path) {
		LanguageManager languageManager = LanguageManager.getInstance();
		HashMap<Language, String> map = new HashMap<>();
		
		for (String key : configuration.getKeys(path))
			if (languageManager.getLanguage(key) != null)
				map.put(languageManager.getLanguage(key), configuration.translateString(path + "." + key));
		return map;
	}
	
	public static Map<Language, List<String>> getLanguageStringListMap(Configuration configuration, String path) {
		LanguageManager languageManager = LanguageManager.getInstance();
		HashMap<Language, List<String>> map = new HashMap<>();
		
		for (String key : configuration.getKeys(path))
			if (languageManager.getLanguage(key) != null)
				map.put(languageManager.getLanguage(key), configuration.translateStringList(path + "." + key));
		return map;
	}
	
	public static List<String> getWorlds() {
		return (Environment.isBukkit() ? Bukkit.getWorlds().stream().map(org.bukkit.World::getName) : Sponge.getServer().getWorlds().stream().map(org.spongepowered.api.world.World::getName)).collect(Collectors.toList());
	}
	
	public static int getOnlineWorld(String world) {
		return (int) (Environment.isBukkit() ? Bukkit.getWorld(world).getPlayers().size() : Sponge.getServer().getWorld(world).get().getEntities().stream().filter(entity -> entity instanceof Player).count()); // Sponge v4.2
	}
	
	public static String formatDate(long ms, Language language, DateFormat format) {
		return new SimpleDateFormat(language.getMessage("misc.simple-date-format." + format.name().toLowerCase())).format(new Date(ms));
	}
	
	public static String formatTime(long totalMilliseconds, Language language, boolean everInsteadOfNever, boolean useZeroSecondsInstead) {
		StringBuilder sb = new StringBuilder();
		long totalSeconds = (totalMilliseconds + 999) / 1000L;
		
		if (totalMilliseconds == -1)
			return language.getMessage(everInsteadOfNever ? "timestamps.ever" : "timestamps.never");
		if (totalSeconds < 1 && !useZeroSecondsInstead)
			return language.getMessage("timestamps.now");
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
	
}
