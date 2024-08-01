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

package me.remigio07.chatplugin.api.common.util.text;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit, Sponge, BungeeCord and Velocity) chat color API with 1.16+ hex color codes support.
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 */
public class ChatColor {
	
	private static final Map<Character, ChatColor> BY_CHAR = new HashMap<>();
	private static final Map<String, ChatColor> BY_NAME = new HashMap<>();
	
	/**
	 * Black.
	 * 
	 * <p><strong>Code:</strong> 0
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #000000</p>
	 */
	public static final ChatColor BLACK = new ChatColor("BLACK", '0', 0x000000);
	
	/**
	 * Dark blue.
	 * 
	 * <p><strong>Code:</strong> 1
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #0000AA</p>
	 */
	public static final ChatColor DARK_BLUE = new ChatColor("DARK_BLUE", '1', 0x0000AA);
	
	/**
	 * Dark green.
	 * 
	 * <p><strong>Code:</strong> 2
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #00AA00</p>
	 */
	public static final ChatColor DARK_GREEN = new ChatColor("DARK_GREEN", '2', 0x00AA00);
	
	/**
	 * Dark aqua.
	 * 
	 * <p><strong>Code:</strong> 3
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #00AAAA</p>
	 */
	public static final ChatColor DARK_AQUA = new ChatColor("DARK_AQUA", '3', 0x00AAAA);
	
	/**
	 * Dark red.
	 * 
	 * <p><strong>Code:</strong> 4
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #AA0000</p>
	 */
	public static final ChatColor DARK_RED = new ChatColor("DARK_RED", '4', 0xAA0000);
	
	/**
	 * Dark purple.
	 * 
	 * <p><strong>Code:</strong> 5
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #AA00AA</p>
	 */
	public static final ChatColor DARK_PURPLE = new ChatColor("DARK_PURPLE", '5', 0xAA00AA);
	
	/**
	 * Gold.
	 * 
	 * <p><strong>Code:</strong> 6
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #FFAA00</p>
	 */
	public static final ChatColor GOLD = new ChatColor("GOLD", '6', 0xFFAA00);
	
	/**
	 * Gray.
	 * 
	 * <p><strong>Code:</strong> 7
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #AAAAAA</p>
	 */
	public static final ChatColor GRAY = new ChatColor("GRAY", '7', 0xAAAAAA);
	
	/**
	 * Dark gray.
	 * 
	 * <p><strong>Code:</strong> 8
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #555555</p>
	 */
	public static final ChatColor DARK_GRAY = new ChatColor("DARK_GRAY", '8', 0x555555);
	
	/**
	 * Blue.
	 * 
	 * <p><strong>Code:</strong> 9
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #5555FF</p>
	 */
	public static final ChatColor BLUE = new ChatColor("BLUE", '9', 0x5555FF);
	
	/**
	 * Green.
	 * 
	 * <p><strong>Code:</strong> a/A
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #55FF55</p>
	 */
	public static final ChatColor GREEN = new ChatColor("GREEN", 'a', 0x55FF55);
	
	/**
	 * Aqua.
	 * 
	 * <p><strong>Code:</strong> b/B
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #55FFFF</p>
	 */
	public static final ChatColor AQUA = new ChatColor("AQUA", 'b', 0x55FFFF);
	
	/**
	 * Red.
	 * 
	 * <p><strong>Code:</strong> c/C
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #FF5555</p>
	 */
	public static final ChatColor RED = new ChatColor("RED", 'c', 0xFF5555);
	
	/**
	 * Light purple.
	 * 
	 * <p><strong>Code:</strong> d/D
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #FF55FF</p>
	 */
	public static final ChatColor LIGHT_PURPLE = new ChatColor("LIGHT_PURPLE", 'd', 0xFF55FF);
	
	/**
	 * Yellow.
	 * 
	 * <p><strong>Code:</strong> e/E
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #FFFF55</p>
	 */
	public static final ChatColor YELLOW = new ChatColor("YELLOW", 'e', 0xFFFF55);
	
	/**
	 * White.
	 * 
	 * <p><strong>Code:</strong> f/F
	 * <br><strong>Type:</strong> color
	 * <br><strong>Hex:</strong> #FFFFFF</p>
	 */
	public static final ChatColor WHITE = new ChatColor("WHITE", 'f', 0xFFFFFF);
	
	/**
	 * Obfuscated/magic.
	 * 
	 * <p><strong>Code:</strong> k/K
	 * <br><strong>Type:</strong> format</p>
	 */
	public static final ChatColor OBFUSCATED = new ChatColor("OBFUSCATED", 'k');
	
	/**
	 * Bold.
	 * 
	 * <p><strong>Code:</strong> l/L
	 * <br><strong>Type:</strong> format</p>
	 */
	public static final ChatColor BOLD = new ChatColor("BOLD", 'l');
	
	/**
	 * Strikethrough.
	 * 
	 * <p><strong>Code:</strong> m/M
	 * <br><strong>Type:</strong> format</p>
	 */
	public static final ChatColor STRIKETHROUGH = new ChatColor("STRIKETHROUGH", 'm');
	
	/**
	 * Underline.
	 * 
	 * <p><strong>Code:</strong> n/N
	 * <br><strong>Type:</strong> format</p>
	 */
	public static final ChatColor UNDERLINE = new ChatColor("UNDERLINE", 'n');
	
	/**
	 * Italic.
	 * 
	 * <p><strong>Code:</strong> o/O
	 * <br><strong>Type:</strong> format</p>
	 */
	public static final ChatColor ITALIC = new ChatColor("ITALIC", 'o');
	
	/**
	 * Reset.
	 * 
	 * <p><strong>Code:</strong> r/R
	 * <br><strong>Type:</strong> color</p>
	 */
	public static final ChatColor RESET = new ChatColor("RESET", 'r');
	
	/**
	 * Minecraft's character used as prefix of color codes.
	 * 
	 * <p><strong>Value:</strong> '§' ('\u00A7')</p>
	 */
	public static final char SECTION_SIGN = '\u00A7';
	
	/**
	 * String containing all default color codes.
	 * 
	 * <p><strong>Content:</strong> "0123456789AaBbCcDdEeFfRr"</p>
	 */
	public static final String COLOR_CODES = "0123456789AaBbCcDdEeFfRr";
	
	/**
	 * String containing all default format codes.
	 * 
	 * <p><strong>Content:</strong> "KkLlMmNnOo"</p>
	 */
	public static final String FORMAT_CODES = "KkLlMmNnOo";
	
	/**
	 * String containing all default color and format codes.
	 * 
	 * <p><strong>Content:</strong> "0123456789AaBbCcDdEeFfRrKkLlMmNnOo"</p>
	 */
	public static final String CODES = COLOR_CODES + FORMAT_CODES;
	
	/**
	 * Pattern used to strip color from a string.
	 * 
	 * <p><strong>Regex:</strong> "(?i)\u00A7[0-9A-FK-ORX]"</p>
	 */
	public static final Pattern STRIP_COLOR = Pattern.compile("(?i)" + SECTION_SIGN + "[0-9A-FK-ORX]");
	
	/**
	 * Patterns used to identify hex colors.
	 * 
	 * <p><strong>Contents:</strong> ["&amp;#([A-Fa-f0-9]){6}", "#([A-Fa-f0-9]){6}"]</p>
	 */
	public static final Pattern[] HEX_COLORS = new Pattern[] { Pattern.compile("&#([A-Fa-f0-9]){6}"), Pattern.compile("#([A-Fa-f0-9]){6}") };
	private static final Pattern TRANSLATED_HEX_COLORS = Pattern.compile("(?i)§X(§[A-F0-9]){6}");
	private static final ChatColor[] VALUES = new ChatColor[] { BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE, OBFUSCATED, BOLD, STRIKETHROUGH, UNDERLINE, ITALIC, RESET };
	private String name, toString;
	private Character code;
	private Color color;
	
	private ChatColor(String name, char code) {
		this(name, code, -1);
	}
	
	private ChatColor(String name, char code, int rgb) {
		this.name = name;
		this.code = code;
		toString = SECTION_SIGN + String.valueOf(code);
		color = rgb == -1 ? null : new Color(rgb);
		
		BY_CHAR.put(code, this);
		BY_NAME.put(name, this);
	}
	
	private ChatColor(String name, String toString, int rgb) {
		this.name = name;
		this.toString = toString;
		color = new Color(rgb);
	}
	
	/**
	 * Gets the string representation of this color.
	 * 
	 * <p>Will return {@link #SECTION_SIGN}<code> + </code>{@link #getCode()} if {@link #isDefaultColor()} and
	 * a hex string (example: "&sect;x&sect;f&sect;f&sect;5&sect;5&sect;f&sect;f" - translated) otherwise.</p>
	 * 
	 * @see #getClosestDefaultColor() Pre-1.16 method to safely convert to default colors
	 */
	@Override
	public String toString() {
		return toString;
	}
	
	/**
	 * Checks if another object is an instance of {@link ChatColor} and if this
	 * color's {@link #toString()} value is equal to the other object's one.
	 * 
	 * @param obj Object to compare
	 * @return Whether the two objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ChatColor && ((ChatColor) obj).toString().equals(toString());
	}
	
	/**
	 * Gets this color's hash code.
	 * 
	 * <p>Will return {@link #toString()}'s hash code.</p>
	 * 
	 * @return Color's hash code
	 */
	@Override
	public int hashCode() {
		return toString.hashCode();
	}
	
	/**
	 * Gets the chat color adapted for Bukkit environments.
	 * 
	 * <p>Will return {@link org.bukkit.ChatColor#RESET}
	 * if <code>!</code>{@link #isDefaultColor()}.</p>
	 * 
	 * @return Bukkit-adapted chat color
	 * @throws UnsupportedOperationException If !{@link Environment#isBukkit()}
	 */
	public org.bukkit.ChatColor bukkitValue() {
		if (Environment.isBukkit())
			return org.bukkit.ChatColor.valueOf(isDefaultColor() ? name.equals(OBFUSCATED.name) ? "MAGIC" : name : RESET.name);
		else throw new UnsupportedOperationException("Unable to adapt chat color to a Bukkit's ChatColor on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the chat color adapted for Sponge environments.
	 * 
	 * <p>Will return {@link org.spongepowered.api.text.format.TextColors#RESET}
	 * if <code>!</code>{@link #isDefaultColor()}.</p>
	 * 
	 * @return Sponge-adapted chat color
	 * @throws UnsupportedOperationException If !{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.text.TextElement spongeValue() {
		if (Environment.isSponge())
			return SpongeChatColor.adapt(this);
		else throw new UnsupportedOperationException("Unable to adapt chat color to a Sponge's TextElement on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the chat color adapted for BungeeCord environments.
	 * 
	 * <p>This method supports hex colors (<code>!</code>{@link #isDefaultColor()}).</p>
	 * 
	 * @return BungeeCord-adapted chat color
	 * @throws UnsupportedOperationException If !{@link Environment#isBungeeCord()}
	 */
	@SuppressWarnings("deprecation")
	public net.md_5.bungee.api.ChatColor bungeeValue() {
		if (Environment.isBungeeCord())
			return isDefaultColor() ? net.md_5.bungee.api.ChatColor.valueOf(name) : net.md_5.bungee.api.ChatColor.of(color);
		else throw new UnsupportedOperationException("Unable to adapt chat color to a BungeeCord's ChatColor on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the chat color adapted for Velocity environments.
	 * 
	 * <p>This method supports hex colors (<code>!</code>{@link #isDefaultColor()}).</p>
	 * 
	 * @return Velocity-adapted chat color
	 * @throws UnsupportedOperationException If !{@link Environment#isVelocity()}
	 */
	public net.kyori.adventure.text.format.TextFormat velocityValue() {
		if (Environment.isVelocity())
			return AdventureChatColor.adapt(this);
		else throw new UnsupportedOperationException("Unable to adapt chat color to a Velocity's TextFormat on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Equivalent of {@link Enum#name()}.
	 * 
	 * <p>Will return a hex string (example: "#FF55FF") if <code>!</code>{@link #isDefaultColor()}.</p>
	 * 
	 * @return Constant's name
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Equivalent of {@link Enum#ordinal()}.
	 * 
	 * <p>Will return -1 if <code>!</code>{@link #isDefaultColor()}.</p>
	 * 
	 * @return Constant's ordinal
	 */
	public int ordinal() {
		for (int i = 0; i < VALUES.length; i++)
			if (this == VALUES[i])
				return i;
		return -1;
	}
	
	/**
	 * Gets this color's code.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isDefaultColor()}.</p>
	 * 
	 * @return Color's code
	 */
	@Nullable(why = "Color may not be a default color")
	public Character getCode() {
		return code;
	}
	
	/**
	 * Gets this color's {@link java.awt.Color} value.
	 * 
	 * <p>Will return <code>null</code> if {@link #isFormatCode()}.</p>
	 * 
	 * <p><strong>Note:</strong> returned color has an alpha value of 255.</p>
	 * 
	 * @return Color's value
	 */
	@Nullable(why = "Color may be a format code")
	public Color getColor() {
		return color;
	}
	
	/**
	 * Checks if this is a default color.
	 * 
	 * @return Whether this is a default color
	 */
	public boolean isDefaultColor() {
		return ordinal() != -1;
	}
	
	/**
	 * Checks if this is a format code.
	 * 
	 * @return Whether this is a format code
	 */
	public boolean isFormatCode() {
		return isDefaultColor() && FORMAT_CODES.contains(String.valueOf(code));
	}
	
	/**
	 * Gets the default color closest to this color.
	 * 
	 * <p>Will return <code>this</code> if <code>{@link #isDefaultColor()} || {@link #isFormatCode()}</code>.</p>
	 * 
	 * <p><strong>Note:</strong> this method uses the same algorithm of ViaVersion.</p>
	 * 
	 * @return Closest default color
	 */
	public ChatColor getClosestDefaultColor() {
		if (isDefaultColor() || isFormatCode())
			return this;
		ChatColor closest = null;
		int smallestDiff = 0;
		int r = (color.getRGB() >> 16) & 0xFF;
		int g = (color.getRGB() >> 8) & 0xFF;
		int b = color.getRGB() & 0xFF;
		
		for (ChatColor defaultColor : VALUES) {
			if (defaultColor.getColor() != null) {
				int rgb = defaultColor.getColor().getRGB();
				
				if (rgb == color.getRGB())
					return defaultColor;
				int rAverage = (((rgb >> 16) & 0xFF) + r) / 2;
				int rDiff = ((rgb >> 16) & 0xFF) - r;
				int gDiff = ((rgb >> 8) & 0xFF) - g;
				int bDiff = (rgb & 0xFF) - b;
				int diff = ((2 + (rAverage >> 8)) * rDiff * rDiff)
						+ (4 * gDiff * gDiff)
						+ ((2 + ((255 - rAverage) >> 8)) * bDiff * bDiff);
				
				if (closest == null || diff < smallestDiff) {
					closest = defaultColor;
					smallestDiff = diff;
				}
			}
		} return closest;
	}
	
	/**
	 * Gets a chat color from given {@link java.awt.Color}.
	 * 
	 * <p>May return one of the 16 default colors.</p>
	 * 
	 * @param color Color to transform
	 * @return Chat color equivalent
	 */
	public static ChatColor of(@NotNull Color color) {
		return of("#" + String.format("%08x", color.getRGB()).substring(2));
	}
	
	/**
	 * Gets a chat color from given hex color code.
	 * 
	 * <p>May return one of the 16 default colors.</p>
	 * 
	 * <p>The following formats are supported: "#rrggbb", "rrggbb".</p>
	 * 
	 * @param hex Color's hex representation, with or without '#'
	 * @return Chat color equivalent
	 * @throws NumberFormatException If the specified string's format is invalid
	 */
	public static ChatColor of(@NotNull String hex) {
		hex = hex.startsWith("#") ? hex : ("#" + hex);
		
		if (hex.length() != 7)
			throw new NumberFormatException("Specified string's format is invalid");
		Color.decode(hex);
		
		int rgb = Integer.parseInt(hex.substring(1), 16) | 0xFF000000;
		
		for (ChatColor defaultColor : VALUES)
			if (defaultColor.getColor() != null && defaultColor.getColor().getRGB() == rgb)
				return defaultColor;
		StringBuilder sb = new StringBuilder(SECTION_SIGN + "x");
		
		for (char x : hex.substring(1).toCharArray())
			sb.append(SECTION_SIGN).append(x);
		return new ChatColor(hex.toUpperCase(), sb.toString().toLowerCase(), rgb);
	}
	
	/**
	 * Calls {@link #translate(String, boolean)},
	 * specifying <code>true</code> as the second argument.
	 * 
	 * @param string String to translate
	 * @return Translated string
	 */
	public static String translate(@NotNull String string) {
		return translate(string, true);
	}
	
	/**
	 * Translates given string applying default ("&amp;x")
	 * and hex ("&amp;#rrggbb", "#rrggbb") color codes.
	 * 
	 * @param string String to translate
	 * @param retainNewLines Whether to retain new lines or to replace them with spaces
	 * @return Translated string
	 */
	public static String translate(@NotNull String string, boolean retainNewLines) {
		String message = string;
		
		if (VersionUtils.getVersion().isAtLeast(Version.V1_16)) {
			for (Pattern pattern : HEX_COLORS) {
				Matcher matcher = pattern.matcher(message);
				
				while (matcher.find())
					matcher = pattern.matcher(message = message.substring(0, matcher.start()) + of(matcher.group().substring(1, matcher.group().length())) + message.substring(matcher.end()));
			}
		} char[] array = message.toCharArray();
		
		for (int i = 0; i < array.length - 1; i++) {
			if (array[i] == '&' && CODES.indexOf(array[i + 1]) != -1) {
				array[i] = SECTION_SIGN;
				array[i + 1] = Character.toLowerCase(array[i + 1]);
			}
		} return retainNewLines ? new String(array) : new String(array).replace("\n", " ").replace("\r\n", " ").replace("\r", " ");
	}
	
	/**
	 * Translates given string list.
	 * 
	 * @param list List to translate
	 * @return Translated string list
	 * @see #translate(String)
	 */
	public static List<String> translate(@NotNull List<String> list) {
		return translate(list, true);
	}
	
	/**
	 * Translates given string list.
	 * 
	 * @param list List to translate
	 * @param retainNewLines Whether to retain new lines or to replace them with spaces
	 * @return Translated string list
	 * @see #translate(String, boolean)
	 */
	public static List<String> translate(@NotNull List<String> list, boolean retainNewLines) {
		return list.stream().map(string -> translate(string, retainNewLines)).collect(Collectors.toList());
	}
	
	/**
	 * Strips a string from any color and/or formatting codes.
	 * 
	 * @param string String to strip
	 * @return Stripped string
	 */
	@NotNull
	public static String stripColor(@NotNull String string) {
		return STRIP_COLOR.matcher(string).replaceAll("");
	}
	
	/**
	 * Gets the last colors contained in a string.
	 * 
	 * <p>Will return an empty string if there are no colors in the string.</p>
	 * 
	 * @param string String to check
	 * @return Last colors in string
	 */
	@NotNull
	public static String getLastColors(@NotNull String string) {
		String colors = "";
		
		if (VersionUtils.getVersion().isAtLeast(Version.V1_16)) {
			Matcher matcher = TRANSLATED_HEX_COLORS.matcher(string);
			int index = 0;
			
			while (matcher.find()) {
				colors = matcher.group();
				index = matcher.end();
			} if (index != 0)
				string = string.substring(index);
		} char[] array = string.toCharArray();
		
		for (int i = 0; i < array.length - 1; i++)
			if (array[i] == '§' && CODES.indexOf(array[i + 1]) != -1)
				if (COLOR_CODES.indexOf(array[i + 1]) != -1)
					colors = "§" + array[i + 1];
				else colors += "§" + array[i + 1];
		return colors;
	}
	
	/**
	 * Checks if the specified character is a valid color code.
	 * 
	 * @param character Character to check
	 * @return Whether the character is a color code
	 * @see #isFormatCode(char)
	 */
	public static boolean isColorCode(char character) {
		return COLOR_CODES.contains(String.valueOf(character));
	}
	
	/**
	 * Checks if the specified character is a valid format code.
	 * 
	 * @param character Character to check
	 * @return Whether the character is a format code
	 * @see #isColorCode(char)
	 */
	public static boolean isFormatCode(char character) {
		return FORMAT_CODES.contains(String.valueOf(character));
	}
	
	/**
	 * Checks if the specified string contains color codes.
	 * 
	 * @param string String to check
	 * @return Whether the string contains colors
	 */
	public static boolean isColorString(@NotNull String string) {
		string = string.trim();
		
		if (string.length() % 2 != 0)
			return false;
		int index = string.length() - 1;
		boolean colorCodeExpected = true;
		
		while (index > 0) {
			if (colorCodeExpected) {
				if (!isColorCode(string.charAt(index)) && !isFormatCode(string.charAt(index)))
					return false;
			} else if (string.charAt(index) != SECTION_SIGN)
				return false;
			colorCodeExpected = !colorCodeExpected;
			index--;
		} return true;
	}
	
	/**
	 * Gets a random color, format codes not included.
	 * 
	 * <p>Hex colors will be returned on 1.16+ environments.</p>
	 * 
	 * @return Random color
	 */
	public static ChatColor getRandomColor() {
		return VersionUtils.getVersion().isAtLeast(Version.V1_16) ? of(String.format("%06x", ThreadLocalRandom.current().nextInt(16777216))) : VALUES[ThreadLocalRandom.current().nextInt(16)];
	}
	
	/**
	 * Gets a default chat color by its code.
	 * 
	 * <p>Will return <code>null</code> if the code is invalid.</p>
	 * 
	 * @param code Color's code
	 * @return Corresponding color
	 */
	@Nullable(why = "Code may be invalid")
	public static ChatColor getByChar(char code) {
		return BY_CHAR.get(Character.toLowerCase(code));
	}
	
	/**
	 * Equivalent of <code>Enum#valueOf(String)</code>,
	 * with the only difference that instead of throwing
	 * {@link IllegalArgumentException} <code>null</code>
	 * is returned if the constant's name is invalid.
	 * 
	 * <p>This method recognizes Bukkit's, Sponge's BungeeCord's and Velocity's IDs.</p>
	 * 
	 * @param name Constant's name
	 * @return Enum constant
	 */
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the constant's name is invalid")
	public static ChatColor valueOf(String name) {
		return BY_NAME.get(name);
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static ChatColor[] values() {
		return VALUES;
	}
	
	private static class SpongeChatColor {
		
		public static TextElement adapt(ChatColor chatColor) {
			try {
				return (TextColor) TextColors.class.getField(chatColor.isDefaultColor() ? chatColor.name : RESET.name).get(null);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
				try {
					return (TextStyle) TextStyles.class.getField(chatColor.name).get(null);
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e2) {
					e.printStackTrace();
					return TextColors.RESET;
				}
			}
		}
		
	}
	
	private static class AdventureChatColor {
		
		public static net.kyori.adventure.text.format.TextFormat adapt(ChatColor chatColor) {
			return chatColor.isFormatCode() ? net.kyori.adventure.text.format.TextDecoration.NAMES.value(chatColor == UNDERLINE ? "UNDERLINED" : chatColor.name) : net.kyori.adventure.text.format.TextColor.color(chatColor.color.getRed(), chatColor.color.getGreen(), chatColor.color.getBlue());
		}
		
	}
	
}
