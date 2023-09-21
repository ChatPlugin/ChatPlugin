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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.util.adapter.text;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents an action performed on a click event.
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 */
public class ClickActionAdapter {
	
	/**
	 * Copies text to the player's clipboard.
	 * Only available on 1.15.2+ servers.
	 */
	public static final ClickActionAdapter COPY_TEXT = new ClickActionAdapter("COPY_TEXT", "copy_to_clipboard");
	
	/**
	 * Opens the URL in the player's browser.
	 */
	public static final ClickActionAdapter OPEN_URL = new ClickActionAdapter("OPEN_URL", "open_url");
	
	/**
	 * Makes the player send a message.
	 */
	public static final ClickActionAdapter SEND_MESSAGE = new ClickActionAdapter("SEND_MESSAGE", "run_command");
	
	/**
	 * Suggests text in the player's chat.
	 */
	public static final ClickActionAdapter SUGGEST_TEXT = new ClickActionAdapter("SUGGEST_TEXT", "suggest_command");
	
	private static final ClickActionAdapter[] VALUES = new ClickActionAdapter[] { COPY_TEXT, OPEN_URL, SEND_MESSAGE, SUGGEST_TEXT };
	private String name, id;
	
	private ClickActionAdapter(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	/**
	 * Equivalent of {@link Enum#name()}.
	 * 
	 * @return Constant's name
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Equivalent of {@link Enum#ordinal()}.
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
	 * Gets this action's ID.
	 * 
	 * @return Action's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Equivalent of <code>Enum#valueOf(String)</code>,
	 * with the only difference that instead of throwing
	 * {@link IllegalArgumentException} <code>null</code>
	 * is returned if the constant's name is invalid.
	 * This method recognizes Bukkit's, Sponge's,
	 * BungeeCord's and Velocity's IDs.
	 * 
	 * @param name Constant's name
	 * @return Enum constant
	 */
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the constant's name is invalid")
	public static ClickActionAdapter valueOf(String name) {
		switch (name) {
		case "COPY_TEXT":
			return COPY_TEXT;
		case "OPEN_URL":
			return OPEN_URL;
		case "SEND_MESSAGE":
			return SEND_MESSAGE;
		case "SUGGEST_TEXT":
			return SUGGEST_TEXT;
		default:
			return null;
		}
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static ClickActionAdapter[] values() {
		return VALUES;
	}
	
}
