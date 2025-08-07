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

package me.remigio07.chatplugin.api.common.util;

import java.util.Set;
import java.util.StringJoiner;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

/**
 * Util class used to contain a value that can be either
 * a fixed number or a placeholder's variable value.
 * 
 * @param <T> Value type
 */
public class ValueContainer<T extends Number> {
	
	private String placeholder;
	private T value;
	
	/**
	 * Constructs a value container using given object, which can
	 * either extend {@link Number} or be a {@link String} placeholder.
	 * 
	 * @param value Object value
	 */
	@SuppressWarnings("unchecked")
	public ValueContainer(Object value) {
		if (value instanceof String)
			placeholder = (String) value;
		else this.value = value == null ? null : (T) value;
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "ValueContainer{", "}")
				.add("placeholder=" + (placeholder == null ? placeholder : "\"" + placeholder + "\""))
				.add("value=" + value)
				.toString();
	}
	
	/**
	 * Gets this container's placeholder.
	 * 
	 * <p>Will return <code>null</code> if a fixed
	 * value was specified at construction.</p>
	 * 
	 * @return Container's placeholder
	 */
	@Nullable(why = "Value may be a fixed number")
	public String placeholder() {
		return placeholder;
	}
	
	/**
	 * Gets this container's fixed value.
	 * 
	 * <p>Will return <code>null</code> if a
	 * placeholder was specified at construction.</p>
	 * 
	 * @return Container's fixed value
	 */
	@Nullable(why = "Value may be a variable placeholder")
	public T value() {
		return value;
	}
	
	/**
	 * Translates {@link #value()} or {@link #placeholder()}
	 * into a string containing a byte value.
	 * 
	 * @param placeholderTypes Placeholder types containing {@link #placeholder()}
	 * @param player Player whose placeholders need to be translated
	 * @return Translated value or placeholder
	 * @throws NumberFormatException If the placeholder is invalid
	 */
	public byte byteValue(Set<PlaceholderType> placeholderTypes, ChatPluginServerPlayer player) throws NumberFormatException {
		return value == null ? placeholder == null ? 0 : Byte.valueOf(format(placeholderTypes, player)) : value.byteValue();
	}
	
	/**
	 * Translates {@link #value()} or {@link #placeholder()}
	 * into a string containing a short value.
	 * 
	 * @param placeholderTypes Placeholder types containing {@link #placeholder()}
	 * @param player Player whose placeholders need to be translated
	 * @return Translated value or placeholder
	 * @throws NumberFormatException If the placeholder is invalid
	 */
	public short shortValue(Set<PlaceholderType> placeholderTypes, ChatPluginServerPlayer player) throws NumberFormatException {
		return value == null ? placeholder == null ? 0 : Short.valueOf(format(placeholderTypes, player)) : value.shortValue();
	}
	
	/**
	 * Translates {@link #value()} or {@link #placeholder()}
	 * into a string containing an integer value.
	 * 
	 * @param placeholderTypes Placeholder types containing {@link #placeholder()}
	 * @param player Player whose placeholders need to be translated
	 * @return Translated value or placeholder
	 * @throws NumberFormatException If the placeholder is invalid
	 */
	public int intValue(Set<PlaceholderType> placeholderTypes, ChatPluginServerPlayer player) throws NumberFormatException {
		return value == null ? placeholder == null ? 0 : Integer.valueOf(format(placeholderTypes, player)) : value.intValue();
	}
	
	/**
	 * Translates {@link #value()} or {@link #placeholder()}
	 * into a string containing a long value.
	 * 
	 * @param placeholderTypes Placeholder types containing {@link #placeholder()}
	 * @param player Player whose placeholders need to be translated
	 * @return Translated value or placeholder
	 * @throws NumberFormatException If the placeholder is invalid
	 */
	public long longValue(Set<PlaceholderType> placeholderTypes, ChatPluginServerPlayer player) throws NumberFormatException {
		return value == null ? placeholder == null ? 0L : Long.valueOf(format(placeholderTypes, player)) : value.longValue();
	}
	
	/**
	 * Translates {@link #value()} or {@link #placeholder()}
	 * into a string containing a float value.
	 * 
	 * @param placeholderTypes Placeholder types containing {@link #placeholder()}
	 * @param player Player whose placeholders need to be translated
	 * @return Translated value or placeholder
	 * @throws NumberFormatException If the placeholder is invalid
	 */
	public float floatValue(Set<PlaceholderType> placeholderTypes, ChatPluginServerPlayer player) throws NumberFormatException {
		return value == null ? placeholder == null ? 0F : Float.valueOf(format(placeholderTypes, player)) : value.floatValue();
	}
	
	/**
	 * Translates {@link #value()} or {@link #placeholder()}
	 * into a string containing a double value.
	 * 
	 * @param placeholderTypes Placeholder types containing {@link #placeholder()}
	 * @param player Player whose placeholders need to be translated
	 * @return Translated value or placeholder
	 * @throws NumberFormatException If the placeholder is invalid
	 */
	public double doubleValue(Set<PlaceholderType> placeholderTypes, ChatPluginServerPlayer player) throws NumberFormatException {
		return value == null ? placeholder == null ? 0D : Double.valueOf(format(placeholderTypes, player)) : value.doubleValue();
	}
	
	private String format(Set<PlaceholderType> placeholderTypes, ChatPluginServerPlayer player) {
		return ChatColor.stripColor(PlaceholderManager.getInstance().translatePlaceholders(placeholder, player, placeholderTypes).trim());
	}
	
}
