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

package me.remigio07.chatplugin.api.common.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.spongepowered.api.Sponge;

import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.VelocityBootstrapper;

/**
 * Common utils class.
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
	 * UUID representing the <a href=https://wikipedia.org/wiki/Universally_unique_identifier#Special_UUIDs>nil UUID</a>.
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
	
	/**
	 * Checks if the specified input is a valid number
	 * using {@link Double#parseDouble(String)}.
	 * 
	 * @param number Number to check
	 * @return Whether input is a valid number
	 */
	public static boolean isNumber(String number) {
		try {
			Double.parseDouble(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the specified input is a valid float
	 * using {@link Float#parseFloat(String)}.
	 * 
	 * @param number Number to check
	 * @return Whether input is a valid float
	 */
	public static boolean isFloat(String number) {
		try {
			Float.parseFloat(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the specified input is a valid integer
	 * using {@link Integer#parseInt(String)}.
	 * 
	 * @param number Number to check
	 * @return Whether input is a valid integer
	 */
	public static boolean isInteger(String number) {
		try {
			Integer.parseInt(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the specified input is a valid long
	 * using {@link Long#parseLong(String)}.
	 * 
	 * @param number Number to check
	 * @return Whether input is a valid long
	 */
	public static boolean isLong(String number) {
		try {
			Long.parseLong(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the specified input is a positive
	 * integer using {@link Integer#parseInt(String)}.
	 * 
	 * @param number Number to check
	 * @return Whether input is a positive integer
	 */
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
	
	/**
	 * Checks if the specified input is a positive
	 * long using {@link Long#parseLong(String)}.
	 * 
	 * @param number Number to check
	 * @return Whether input is a positive long
	 */
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
	
	/**
	 * Truncates the specified double to a certain approximation.
	 * 
	 * @param number Number to truncate
	 * @param decimalPlaces Decimal places to keep
	 * @return Truncated number
	 */
	public static double truncate(double number, int decimalPlaces) {
		double scale = Math.pow(10, decimalPlaces);
		return Math.round(number * scale) / scale;
	}
	
	/**
	 * Formats the specified balance to a certain approximation like the following:
	 * 	<ul>
	 * 		<li>1,500 -&gt; 1.5K</li>
	 * 		<li>1,500,000 -&gt; 1.5M</li>
	 * 		<li>1,500,000 -&gt; 1.5B</li>
	 * 	</ul>
	 * 
	 * @param balance Balance to format
	 * @param decimalPlaces Decimal places to keep
	 * @return Formatted balance
	 */
	public static String formatBalance(double balance, int decimalPlaces) {
		if (balance >= 1000000000)
			return String.valueOf(truncate(balance / 1000000000, decimalPlaces)) + 'B';
		if (balance >= 1000000)
			return String.valueOf(truncate(balance / 1000000, decimalPlaces)) + 'M';
		if (balance >= 1000)
			return String.valueOf(truncate(balance / 1000, decimalPlaces)) + 'K';
		return String.valueOf(truncate(balance, decimalPlaces));
	}
	
	/**
	 * Converts the specified kilometers to miles.
	 * 
	 * <p>1 mi = 1609.344 m</p>
	 * 
	 * @param kilometers Kilometers to convert
	 * @return Corresponding miles
	 */
	public static long kilometersToMiles(long kilometers) {
		return (long) (kilometers / 1.609344);
	}
	
	/**
	 * Converts the specified kilometers to nautical miles.
	 * 
	 * <p>1 km = 0.53995680345572 nm</p>
	 * 
	 * @param kilometers Kilometers to convert
	 * @return Corresponding nautical miles
	 */
	public static long kilometersToNauticalMiles(long kilometers) {
		return (long) (kilometers * 0.53995680345572);
	}
	
	/**
	 * Gets the maximum online players' amount.
	 * 
	 * @return Max players' amount
	 */
	public static int getMaxPlayers() {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			return Bukkit.getMaxPlayers();
		case SPONGE:
			return Sponge.getServer().getMaxPlayers();
		case BUNGEECORD:
			try {
				return (int) Class.forName("net.md_5.bungee.conf.Configuration").getMethod("getPlayerLimit").invoke(me.remigio07.chatplugin.api.proxy.util.Utils.invokeBungeeCordMethod("getConfig", null));
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
				e.printStackTrace();
				return -1;
			}
		case VELOCITY:
			return VelocityBootstrapper.getInstance().getProxy().getConfiguration().getShowMaxPlayers();
		} return -1;
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
	
	/**
	 * Evaluates an IP address using {@link InetAddress#getByAddress(byte[])} to avoid
	 * pinging it, making this method faster than {@link InetAddress#getByName(String)}.
	 * 
	 * @param ipv4 IPv4's representation
	 * @return Corresponding IP address
	 * @throws IllegalArgumentException If specified IPv4 <code>!</code>{@link #isValidIPv4(String)}
	 */
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
	
	/**
	 * Gets the original (abstract, API) class of the specified manager's instance.
	 * 
	 * @param manager Manager's instance
	 * @return Manager's original class
	 */
	public static Class<? extends ChatPluginManager> getOriginalClass(ChatPluginManager manager) {
		for (Entry<Class<? extends ChatPluginManager>, ChatPluginManager> entry : ChatPluginManagers.getInstance().getManagers().entrySet())
			if (entry.getValue().equals(manager))
				return entry.getKey();
		return null;
	}
	
	/**
	 * Creates a copy of the input collection, adds the
	 * specified collection's elements and then returns it.
	 * 
	 * @param <T> Collection's type
	 * @param input Input collection
	 * @param args Collection to add
	 * @return Copy of <code>input</code> + <code>args</code>
	 */
	public static <T> List<T> addAndGet(Collection<T> input, Collection<T> args) {
		List<T> list2 = new CopyOnWriteArrayList<>(input);
		
		for (T e : args) {
			if (!list2.contains(e))
				list2.add(e);
		} return list2;
	}
	
	/**
	 * Creates a copy of the input collection, removes the
	 * specified collection's elements and then returns it.
	 * 
	 * @param <T> Collection's type
	 * @param input Input collection
	 * @param args Collection to remove
	 * @return Copy of <code>input</code> - <code>args</code>
	 */
	public static <T> List<T> removeAndGet(Collection<T> input, Collection<T> args) {
		List<T> list2 = new CopyOnWriteArrayList<>(input);
		
		for (T e : args) {
			if (list2.contains(e))
				list2.remove(e);
		} return list2;
	}
	
	/**
	 * Creates a string representation of the specified list like the following:
	 * <br>"[random string, 3, that was a number as a string]"
	 * 
	 * @param list List to transform
	 * @param translateColorCodes Whether to translate colors
	 * @param retainNewLines Whether to retain new lines or to replace them with spaces
	 * @return String representing the list
	 */
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
	
	/**
	 * Creates a list from a string representation obtained
	 * using {@link #getStringFromList(List, boolean, boolean)}.
	 * 
	 * @param string String representing the list
	 * @return List from the string representation
	 */
	public static List<String> getListFromString(String string) {
		List<String> list = new ArrayList<>();
		
		if (string == null || (!string.startsWith("[") || !string.endsWith("]")) || string.length() == 2)
			return list;
		string = string.substring(1, string.length() - 1);
		
		for (String str : string.split(", "))
			list.add(str);
		return list;
	}
	
	/**
	 * Calls {@link String#valueOf(Object)} for every element in the specified list.
	 * 
	 * @param list List to transform
	 * @return String list
	 */
	public static List<String> integerListToStringList(List<Integer> list) {
		return list.stream().map(String::valueOf).collect(Collectors.toList());
	}
	
	/**
	 * Calls {@link Number#intValue()} for every element in the specified list.
	 * 
	 * @param list List to transform
	 * @return Integer list
	 */
	public static List<Integer> numberListToIntegerList(List<Number> list) {
		return list.stream().map(Number::intValue).collect(Collectors.toList());
	}
	
	/**
	 * Reverses the specified char array.
	 * 
	 * @param array Array to reverse
	 */
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
	
	/**
	 * Gets the index of the specified string in the given array.
	 * 
	 * <p>Will return -1 if the string is not present.</p>
	 * 
	 * @param array Array to check
	 * @param string String to check
	 * @param ignoreCase Whether to ignore case
	 * @return String's index in the array
	 */
	public static int arrayIndexOf(String[] array, String string, boolean ignoreCase) {
		for (int i = 0; i < array.length; i++)
			if (ignoreCase ? array[i].equalsIgnoreCase(string) : array[i].equals(string))
				return i;
		return -1;
	}
	
	/**
	 * Checks if {@link #arrayIndexOf(String[], String, boolean)}<code> != -1</code>.
	 * 
	 * @param array Array to check
	 * @param string String to check
	 * @param ignoreCase Whether to ignore case
	 * @return Whether the array contains the string
	 */
	public static boolean arrayContains(String[] array, String string, boolean ignoreCase) {
		return arrayIndexOf(array, string, ignoreCase) != -1;
	}
	
	/**
	 * Creates a copy of the specified array,
	 * removes the given string and then returns it.
	 * 
	 * @param array Input array
	 * @param string String to remove
	 * @param ignoreCase Whether to ignore case
	 * @return New array
	 */
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
	
	/**
	 * Capitalizes every word in the specified string.
	 * 
	 * @param input String to capitalize
	 * @return Capitalized string
	 */
	public static String capitalizeEveryWord(String input) {
		return Stream
				.of(input.toLowerCase().split("\\s"))
				.filter(word -> word.length() > 0)
				.map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1)).collect(Collectors.joining(" "));
	}
	
	/**
	 * Calculates the time in milliseconds expressed by an input string.
	 * 
	 * <p>Will return -1 if the input is invalid or the time is
	 * &#60; 0s or if <code>!allow0s</code> and the time is 0s.
	 * Input string must not contain spaces and timestamps must be divided by a comma.</p>
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
	 * @param allow0s Whether to allow the "0s" timestamp
	 * @return Time in milliseconds or -1 if invalid input
	 */
	public static long getTime(String input, boolean addCurrent, boolean allow0s) {
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
			return time < 0 || (time == 0 && !allow0s) ? -1 : time;
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			return -1;
		}
	}
	
	/**
	 * Converts the specified years, months, days, hours,
	 * minutes and seconds to their milliseconds value.
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
	
	/**
	 * Calls {@link #numericPlaceholders(String, Object...)} for every string in the specified list.
	 * 
	 * @param list List containing placeholders
	 * @param args Optional arguments
	 * @return List with placeholders translated
	 */
	public static List<String> numericPlaceholders(List<String> list, Object... args) {
		return list.stream().map(string -> numericPlaceholders(string, args)).collect(Collectors.toList());
	}
	
	/**
	 * Gets the total storage installed on <code>/</code>, in bytes.
	 * 
	 * @return Total storage
	 */
	public static long getTotalStorage() {
		return MAIN_FOLDER.getTotalSpace();
	}
	
	/**
	 * Gets the free storage available on <code>/</code>, in bytes.
	 * 
	 * @return Free storage
	 */
	public static long getFreeStorage() {
		return MAIN_FOLDER.getFreeSpace();
	}
	
}
