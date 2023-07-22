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

package me.remigio07_.chatplugin.api.common.util.adapter.text;

import java.net.MalformedURLException;
import java.net.URL;

import org.spongepowered.api.text.action.TextActions;

import me.remigio07_.chatplugin.api.common.util.VersionUtils;
import me.remigio07_.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.bootstrap.Environment;
import net.kyori.adventure.text.event.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

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
	 * Suggests text in the player's chat.
	 */
	public static final ClickActionAdapter SUGGEST_TEXT = new ClickActionAdapter("SUGGEST_TEXT", "suggest_command");
	
	/**
	 * Makes the player send a message.
	 */
	public static final ClickActionAdapter SEND_MESSAGE = new ClickActionAdapter("SEND_MESSAGE", "run_command");
	private static final ClickActionAdapter[] VALUES = new ClickActionAdapter[] { COPY_TEXT, OPEN_URL, SUGGEST_TEXT, SEND_MESSAGE };
	private String name, id;
	
	private ClickActionAdapter(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	/**
	 * Gets the click action adapted for Bukkit environments.
	 * This method returns an {@link Action} as {@link #bungeeCordValue()} does
	 * as they use the same API, but you cannot call it on Bukkit environments.
	 * 
	 * @return Bukkit-adapted click action
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public net.md_5.bungee.api.chat.ClickEvent.Action bukkitValue() {
		if (Environment.isBukkit())
			return Action.valueOf(id.toUpperCase());
		else throw new UnsupportedOperationException("Unable to adapt click action to a Bukkit's Action on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the click action adapted for Sponge environments.
	 * 
	 * @param value Click event's value
	 * @return Sponge-adapted click action
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public Object spongeValue(String value) {
		if (Environment.isSponge())
			switch (name) {
			case "COPY_TEXT":
				return null;
			case "OPEN_URL":
				try {
					return TextActions.openUrl(new URL(value));
				} catch (MalformedURLException e) {
					return null;
				}
			case "SUGGEST_TEXT":
				return TextActions.suggestCommand(value);
			case "SEND_MESSAGE":
				return TextActions.runCommand(value);
			default:
				return null;
			}
		else throw new UnsupportedOperationException("Unable to adapt click action to a Sponge's ClickAction<R> on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the click action adapted for BungeeCord environments.
	 * This method returns an {@link Action} as {@link #bukkitValue()} does
	 * as they use the same API, but you cannot call it on BungeeCord environments.
	 * 
	 * @return BungeeCord-adapted click action
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBungeeCord()}
	 */
	public Action bungeeCordValue() {
		if (Environment.isBungeeCord())
			return Action.valueOf(id.toUpperCase());
		else throw new UnsupportedOperationException("Unable to adapt click action to a BungeeCord's Action on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the click action adapted for Velocity environments.
	 * 
	 * @param value Click event's value
	 * @return Velocity-adapted click action
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isVelocity()}
	 */
	public ClickEvent velocityValue(String value) {
		if (Environment.isVelocity())
			switch (name) {
			case "COPY_TEXT":
				return VersionUtils.getVersion().isAtLeast(Version.V1_15_2) ? ClickEvent.copyToClipboard(value) : null;
			case "OPEN_URL":
				try {
					return ClickEvent.openUrl(new URL(value));
				} catch (MalformedURLException e) {
					return null;
				}
			case "SUGGEST_TEXT":
				return ClickEvent.suggestCommand(value);
			case "SEND_MESSAGE":
				return ClickEvent.runCommand(value);
			default:
				return null;
			}
		else throw new UnsupportedOperationException("Unable to adapt click action to a Velocity's ClickEvent on a " + Environment.getCurrent().getName() + " environment");
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
		case "SUGGEST_TEXT":
			return SUGGEST_TEXT;
		case "SEND_MESSAGE":
			return SEND_MESSAGE;
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
