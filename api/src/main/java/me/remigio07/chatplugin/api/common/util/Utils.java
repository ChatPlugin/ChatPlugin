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

package me.remigio07.chatplugin.api.common.util;

import java.awt.Color;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.VelocityBootstrapper;

/**
 * Common utils class. Not all methods here are documented.
 */
public class Utils {
	
	/**
	 * String containing the "string not found" text indicator.
	 * 
	 * <p><strong>Content:</strong> "string_not_found"</p>
	 */
	public static final String STRING_NOT_FOUND = "string_not_found";
	
	/**
	 * String containing the "not applicable" text indicator.
	 * 
	 * <p><strong>Content:</strong> "N/A"</p>
	 */
	public static final String NOT_APPLICABLE = "N/A";
	
	/**
	 * UUID representing the <a href=https://wikipedia.org/wiki/Universally_unique_identifier#Nil_UUID>nil UUID</a>.
	 * 
	 * <p><strong>UUID:</strong> "00000000-0000-0000-0000-000000000000"</p>
	 */
	public static final UUID NIL_UUID = new UUID(0L, 0L);
	
	/**
	 * File that indicates the current drive's main folder.
	 * 
	 * <p><strong>Path:</strong> {@link File#separator}</p>
	 */
	public static final File MAIN_FOLDER = new File(File.separator);
	
	/**
	 * Pattern representing the allowed usernames.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9_]{2,16}$"</p>
	 * 
	 * @see #isValidUsername(String)
	 */
	public static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{2,16}$");
	
	/**
	 * Pattern representing the allowed IPv4s.
	 * 
	 * <p><strong>Regex:</strong> "^((25[0-5]|(2[0-4]|1\d|[1-9]|)\d)\.?\b){4}$"</p>
	 * 
	 * @see #isValidIPv4(String)
	 */
	public static final Pattern IPV4_PATTERN = Pattern.compile("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");
	
	/**
	 * Seconds in a year.
	 * 
	 * <p><strong>Value:</strong> 31536000 (0x1e13380)</p>
	 */
	public static final int SECONDS_IN_A_YEAR = 31536000;
	
	/**
	 * Seconds in a month.
	 * 
	 * <p><strong>Value:</strong> 2592000 (0x278d00)</p>
	 */
	public static final int SECONDS_IN_A_MONTH = 2592000;
	
	/**
	 * Seconds in a week.
	 * 
	 * <p><strong>Value:</strong> 604800 (0x93a80)</p>
	 */
	public static final int SECONDS_IN_A_WEEK = 604800;
	
	/**
	 * Seconds in a day.
	 * 
	 * <p><strong>Value:</strong> 86400 (0x15180)</p>
	 */
	public static final int SECONDS_IN_A_DAY = 86400;
	
	/**
	 * Seconds in an hour.
	 * 
	 * <p><strong>Value:</strong> 3600 (0xe10)</p>
	 */
	public static final int SECONDS_IN_AN_HOUR = 3600;
	
	/**
	 * Seconds in a minute.
	 * 
	 * <p><strong>Value:</strong> 60 (0x3c)</p>
	 */
	public static final int SECONDS_IN_A_MINUTE = 60;
	
	public static boolean isNumber(String number) {
		try {
			Double.parseDouble(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isFloat(String number) {
		try {
			Float.parseFloat(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isInteger(String number) {
		try {
			Integer.parseInt(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isPositiveInteger(String number) {
		try {
			int amount = Integer.parseInt(number);
			
			if (amount <= 0)
				throw new NumberFormatException();
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isPositiveLong(String number) {
		try {
			long amount = Long.parseLong(number);
			
			if (amount <= 0)
				throw new NumberFormatException();
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public static double truncate(double number, int decimalPlaces) {
		double scale = Math.pow(10, decimalPlaces);
		
		return Math.round(number * scale) / scale;
	}
	
	public static String formatBalance(double balance, int decimalPlaces) {
		if (balance >= 1000000000)
			return String.valueOf(truncate(balance / 1000000000, decimalPlaces)) + 'B';
		if (balance >= 1000000)
			return String.valueOf(truncate(balance / 1000000, decimalPlaces)) + 'M';
		if (balance >= 1000)
			return String.valueOf(truncate(balance / 1000, decimalPlaces)) + 'K';
		return String.valueOf(truncate(balance, decimalPlaces));
	}
	
	// 1 mi = 1609.344 m
	public static long kilometersToMiles(long kilometers) {
		return (long) (kilometers / 1.609344);
	}
	
	// 1 km = 0.53995680345572 nm
	public static long kilometersToNauticalMiles(long kilometers) {
		return (long) (kilometers * 0.53995680345572);
	}
	
	public static int getMaxPlayers() {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			return Bukkit.getMaxPlayers();
		case SPONGE:
			return Sponge.getServer().getMaxPlayers();
		case BUNGEECORD:
			try {
				return (int) Class.forName("net.md-5.bungee.conf.Configuration").getMethod("getPlayerLimit").invoke(invokeBungeeCordMethod("getConfig", null));
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		case VELOCITY:
			return VelocityBootstrapper.getInstance().getProxy().getConfiguration().getShowMaxPlayers();
		} return -1;
	}
	
	public static Object invokeBungeeCordMethod(String name, Class<?>[] types, Object... args) {
		try {
			Class<?> clazz = Class.forName("net.md_5.bungee.BungeeCord");
			return clazz.getMethod(name, types != null && types.length == 0 ? null : types).invoke(clazz.getMethod("getInstance").invoke(null), args);
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		} return null;
	}
	
	/**
	 * Checks if the specified String is a valid username.
	 * 
	 * @param username Username to check
	 * @return Whether the specified username is valid
	 * @see #USERNAME_PATTERN
	 */
	public static boolean isValidUsername(String username) {
		return USERNAME_PATTERN.matcher(username).matches();
	}
	
	/**
	 * Checks if the specified String is a valid IPv4.
	 * 
	 * @param ipv4 IPv4's raw representation (xxx.xxx.xxx.xxx)
	 * @return Whether the specified IPv4 is valid
	 * @see #IPV4_PATTERN
	 */
	public static boolean isValidIPv4(String ipv4) {
		return IPV4_PATTERN.matcher(ipv4).matches();
	}
	
	public static InetAddress getInetAddress(String ipv4) {
		if (isValidIPv4(ipv4)) {
			String[] bytes = ipv4.split("\\.");
			try {
				return InetAddress.getByAddress(new byte[] { Integer.valueOf(bytes[0]).byteValue(), Integer.valueOf(bytes[1]).byteValue(), Integer.valueOf(bytes[2]).byteValue(), Integer.valueOf(bytes[3]).byteValue() });
			} catch (UnknownHostException e) {
				return null; // never called
			}
		} else throw new IllegalArgumentException("Specified IPv4 is invalid as it does not respect the following pattern: \"" + IPV4_PATTERN.pattern() + "\"");
	}
	
	public static <T> List<T> addAndGet(Collection<T> list, Collection<T> args) {
		List<T> list2 = new ArrayList<>(list);
		
		for (T e : args) {
			if (!list2.contains(e))
				list2.add(e);
		} return list2;
	}
	
	public static <T> List<T> removeAndGet(Collection<T> list, Collection<T> args) {
		List<T> list2 = new ArrayList<>(list);
		
		for (T e : args) {
			if (list2.contains(e))
				list2.remove(e);
		} return list2;
	}
	
	public static String getStringFromList(List<?> list, boolean translateColorCodes, boolean retainNewLines) {
		if (list.isEmpty())
			return "[]";
		StringBuilder sb = new StringBuilder("[");
		
		for (Object s : list)
			if (translateColorCodes)
				sb.append(ChatColor.translate(s + "&r, ", retainNewLines));
			else sb.append(s + ", ");
		sb.setLength(sb.length() - 2);
		sb.append(translateColorCodes ? "\u00A7r]" : ']');
		return sb.toString();
	}
	
	public static List<String> getListFromString(String string) {
		List<String> list = new ArrayList<>();
		
		if (string == null || (!string.startsWith("[") || !string.endsWith("]")) || string.length() == 2)
			return list;
		string = string.substring(1, string.length() - 1);
		
		for (String str : string.split(", "))
			list.add(str);
		return list;
	}
	
	public static List<String> integerListToStringList(List<Integer> list) {
		return list.stream().map(i -> String.valueOf(i)).collect(Collectors.toList());
	}
	
	public static List<Integer> numberListToIntegerList(List<Number> list) {
		return list.stream().map(Number::intValue).collect(Collectors.toList());
	}
	
	public static void reverse(char[] array) {
		int i = 0;
		int j = Math.min(array.length, array.length - 1);
		char temp;
		
		while (j > i) {
			temp = array[j];
			array[i] = temp;
			
			j--;
			i++;
		}
	}
	
	public static int arrayIndexOf(String[] array, String string, boolean ignoreCase) {
		for (int i = 0; i < array.length; i++)
			if (ignoreCase ? array[i].equalsIgnoreCase(string) : array[i].equals(string))
				return i;
		return -1;
	}
	
	public static boolean arrayContains(String[] array, String string, boolean ignoreCase) {
		return arrayIndexOf(array, string, ignoreCase) != -1;
	}
	
	public static String[] removeFromArray(String[] array, String string, boolean ignoreCase) {
		int index = arrayIndexOf(array, string, ignoreCase);
		
		if (array.length == 0 || index == -1)
			return array;
		String[] newArray = new String[array.length - 1];
		int destinationIndex = 0;
		
		for (int i = 0; i < array.length; i++)
			if (i != index)
				newArray[destinationIndex++] = array[i];
		return newArray;
	}
	
	public static String capitalizeEveryWord(String input) {
		return Stream
				.of(input.toLowerCase().split("\\s"))
				.filter(word -> word.length() > 0)
				.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1)).collect(Collectors.joining(" "));
	}
	
	/**
	 * Calculates the time in milliseconds expressed by an input string.
	 * Input string must not contain spaces and timestamps must be divided by a comma.
	 * Will return -1 if the input is invalid or the time is &#60;= 0s.
	 * 
	 * 	<br><br><table class="borderless">
	 * 		<caption>Recognized time indicators:</caption>
	 * 		<tr>
	 * 			<th>Identifier</th><th>Value</th>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>s/S</td><td>Second</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>m</td><td>Minute</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>h/H</td><td>Hour</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>d/D</td><td>Day</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>w/W</td><td>Week</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>M</td><td>Month</td>
	 * 		</tr>
	 * 		<tr>
	 * 			<td>y/Y</td><td>Year</td>
	 * 		</tr>
	 *	</table>
	 * 
	 * <p><strong>Example:</strong> "23d,7h,39s"</p>
	 * 
	 * @param input Input string
	 * @param addCurrent Whether to add current time
	 * @return Time in milliseconds or -1 if invalid input or time &#60;= 0s
	 */
	public static long getTime(String input, boolean addCurrent) {
		String[] array = input.split(",");
		int seconds = 0;
		int minutes = 0;
		int hours = 0;
		int days = 0;
		int weeks = 0;
		int months = 0;
		int years = 0;
		
		try {
			for (int i = 0; i < array.length; i++) {
				char[] chars = array[i].toCharArray();
				
				reverse(chars);
				
				switch (chars[0]) {
				case 'y':
				case 'Y':
					years = Integer.valueOf(array[i].substring(0, array[i].toLowerCase().indexOf('y')));
					break;
				case 'M':
					months = Integer.valueOf(array[i].substring(0, array[i].indexOf('M')));
					break;
				case 'w':
				case 'W':
					weeks = Integer.valueOf(array[i].substring(0, array[i].toLowerCase().indexOf('w')));
					break;
				case 'd':
				case 'D':
					days = Integer.valueOf(array[i].substring(0, array[i].toLowerCase().indexOf('d')));
					break;
				case 'h':
				case 'H':
					hours = Integer.valueOf(array[i].substring(0, array[i].toLowerCase().indexOf('h')));
					break;
				case 'm':
					minutes = Integer.valueOf(array[i].substring(0, array[i].indexOf('m')));
					break;
				case 's':
				case 'S':
					seconds = Integer.valueOf(array[i].substring(0, array[i].toLowerCase().indexOf('s')));
					break;
				}
			} long time = getTime(years, months, weeks, days, hours, minutes, seconds, addCurrent);
			return time <= 0 ? -1 : time;
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}
	
	/**
	 * Converts the specified years, months, days, hours, minutes and seconds to their milliseconds value.
	 * 
	 * @param years Years amount
	 * @param months Months amount
	 * @param weeks Weeks amount
	 * @param days Days amount
	 * @param hours Hours amount
	 * @param minutes Minutes amount
	 * @param seconds Seconds amount
	 * @param addCurrent Whether to add current time
	 * @return Time in milliseconds
	 */
	public static long getTime(int years, int months, int weeks, int days, int hours, int minutes, int seconds, boolean addCurrent) {
		return (addCurrent ? System.currentTimeMillis() : 0) + (
				((long) years < 0 ? 0 : years * SECONDS_IN_A_YEAR)
				+ ((long) months < 0 ? 0 : months * SECONDS_IN_A_MONTH)
				+ ((long) weeks < 0 ? 0 : weeks * SECONDS_IN_A_WEEK)
				+ ((long) days < 0 ? 0 : days * SECONDS_IN_A_DAY)
				+ ((long) hours < 0 ? 0 : hours * SECONDS_IN_AN_HOUR)
				+ ((long) minutes < 0 ? 0 : minutes * SECONDS_IN_A_MINUTE)
				+ ((long) seconds < 0 ? 0 : seconds)
				) * 1000L;
	}
	
	/**
	 * Replaces numeric placeholders in a string with the given arguments:
	 * 	<ul>
	 * 		<li><code>{0}</code> will be replaced with <code>args[0]</code></li>
	 * 		<li><code>{1}</code> will be replaced with <code>args[1]</code></li>
	 * 		<li><code>{7}</code> will be replaced with <code>args[7]</code></li>
	 * 	</ul>
	 * 
	 * ...and so on.
	 * 
	 * @param string String containing placeholders
	 * @param args Optional arguments
	 * @return String with placeholders translated
	 */
	public static String numericPlaceholders(String string, Object... args) {
		for (int i = 0; i < args.length; i++)
			if (string.contains("{" + i + "}"))
				string = string.replace("{" + String.valueOf(i) + "}", String.valueOf(args[i]));
		return string;
	}
	
	public static List<String> numericPlaceholders(List<String> list, Object... args) {
		return list.stream().map(string -> numericPlaceholders(string, args)).collect(Collectors.toList());
	}
	
	public static String colorToString(Color color) {
		return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public static long getTotalStorage() {
		return MAIN_FOLDER.getTotalSpace();
	}
	
	public static long getFreeStorage() {
		return MAIN_FOLDER.getFreeSpace();
	}
	
}
