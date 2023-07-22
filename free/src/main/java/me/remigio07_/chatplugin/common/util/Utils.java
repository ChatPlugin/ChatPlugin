/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.Library;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.common.util.text.ComponentTranslatorImpl;

public class Utils extends me.remigio07_.chatplugin.api.common.util.Utils {
	
	public static final String[] STARTUP_MESSAGES = new String[] {
			"loading 0s and 1s",
			"loading stuff",
			"hold tight",
			"ready to launch"
	};
	public static final String[] FREE_VERSION_ADS = new String[] {
			"You are running the free version of the plugin. Buy premium to unlock new features and get rid of ads.",
			"Did you know the premium version includes custom GUIs? Buy premium to unlock new features and get rid of ads.",
			"The paid version supports multi-instance synchronization. Buy premium to unlock new features and get rid of ads."
	};
	
	@SuppressWarnings("deprecation")
	public static void initUtils() throws ChatPluginManagerException {
		try {
			for (Library library : LibrariesUtils.RELOCATION_LIBS)
				LibrariesUtils.load(library);
			LibrariesUtils.load(Library.JSON_SIMPLE);
		} catch (Exception e) {
			throw new ChatPluginManagerException("libraries utils", e);
		} UUIDFetcherImpl.setInstance(new UUIDFetcherImpl());
		ComponentTranslatorImpl.setInstance(new ComponentTranslatorImpl());
	}
	
	public static Object getGeyserConnectorPlayer(UUID player) {
		try {
			Class<?> clazz = Class.forName("org.geysermc.connector.GeyserConnector");
			return clazz.getMethod("getPlayerByUuid", UUID.class).invoke(clazz.getMethod("getInstance").invoke(null, player));
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} return null;
	}
	
	public static InputStream download(URL url) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		
		connection.setConnectTimeout(10000);
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
	
}
