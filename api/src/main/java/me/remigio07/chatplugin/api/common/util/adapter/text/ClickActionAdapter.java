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

package me.remigio07.chatplugin.api.common.util.adapter.text;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.minecraft.text.ClickEvent;

/**
 * Environment-indipendent (Bukkit, Sponge, Fabric, BungeeCord and Velocity) click action adapter.
 */
public class ClickActionAdapter extends PseudoEnum<ClickActionAdapter> {
	
	/**
	 * Opens the specified URL in the player's default web browser.
	 */
	public static final ClickActionAdapter OPEN_URL = new ClickActionAdapter("OPEN_URL");
	
	/**
	 * Opens the specified file on the player's computer.
	 * 
	 * <p><strong>Note:</strong> this cannot be sent by servers for security reasons.</p>
	 */
	public static final ClickActionAdapter OPEN_FILE = new ClickActionAdapter("OPEN_FILE");
	
	/**
	 * Makes the player run the specified command.
	 */
	public static final ClickActionAdapter RUN_COMMAND = new ClickActionAdapter("RUN_COMMAND");
	
	/**
	 * Opens chat and fills in the specified text or command.
	 */
	public static final ClickActionAdapter SUGGEST_COMMAND = new ClickActionAdapter("SUGGEST_COMMAND");
	
	/**
	 * Changes the player's open book page to the specified page, if it exists.
	 * 
	 * <p><strong>Note:</strong> this can only be used in written books.</p>
	 */
	public static final ClickActionAdapter CHANGE_PAGE = new ClickActionAdapter("CHANGE_PAGE");
	
	/**
	 * Copies text to the player's clipboard.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_15 1.15}
	 * <br><strong>Minimum version (BungeeCord API):</strong> {@linkplain Version#V1_15_2 1.15.2}</p>
	 */
	public static final ClickActionAdapter COPY_TO_CLIPBOARD = new ClickActionAdapter("COPY_TO_CLIPBOARD");
	
	/**
	 * Opens the specified dialog to the player.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_21_6 1.21.6}</p>
	 */
	public static final ClickActionAdapter SHOW_DIALOG = new ClickActionAdapter("SHOW_DIALOG");
	
	/**
	 * Sends a custom event to the server.
	 * 
	 * <p><strong>Note:</strong> this has no effect on Vanilla servers.
	 * <br><strong>Minimum version:</strong> {@linkplain Version#V1_21_6 1.21.6}</p>
	 */
	public static final ClickActionAdapter CUSTOM = new ClickActionAdapter("CUSTOM");
	private static final ClickActionAdapter[] VALUES = new ClickActionAdapter[] { OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, COPY_TO_CLIPBOARD, SHOW_DIALOG, CUSTOM };
	private static int ordinal = 0;
	
	private ClickActionAdapter(String name) {
		super(name, ordinal++);
	}
	
	/**
	 * Gets the click action adapted for Fabric environments.
	 * 
	 * <p>If the current version does not support this click action, the
	 * default value of {@link #SUGGEST_COMMAND} will be returned.</p>
	 * 
	 * @return Fabric-adapted click action
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public ClickEvent.Action fabricValue() {
		if (Environment.isFabric())
			try {
				return ClickEvent.Action.valueOf(name);
			} catch (IllegalArgumentException iae) {
				return ClickEvent.Action.SUGGEST_COMMAND;
			}
		throw new UnsupportedOperationException("Unable to adapt click action to a Fabric's ClickEvent.Action on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the click action adapted for BungeeCord environments.
	 * 
	 * <p>If the current version does not support this click action, the
	 * default value of {@link #SUGGEST_COMMAND} will be returned.</p>
	 * 
	 * @return BungeeCord-adapted click action
	 * @throws UnsupportedOperationException If <code>!{@link Environment#isBungeeCord()} &amp;&amp; !{@link VersionUtils#isSpigot()}</code>
	 */
	public Action bungeeCordValue() {
		if (Environment.isBungeeCord() || VersionUtils.isSpigot())
			try {
				return Action.valueOf(name);
			} catch (IllegalArgumentException iae) {
				return Action.SUGGEST_COMMAND;
			}
		throw new UnsupportedOperationException("Unable to adapt click action to a BungeeCord's ClickEvent.Action on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this action's Vanilla ID.
	 * 
	 * @return Action's ID
	 */
	public String getID() {
		return name.toLowerCase();
	}
	
	/**
	 * Gets the minimum supported version for this click action.
	 * 
	 * <p>Will return <code>null</code> if all versions
	 * supported by ChatPlugin support this click action.</p>
	 * 
	 * @return Click action's minimum version
	 */
	public @Nullable(why = "Null if all versions support this click action") Version getMinimumVersion() {
		return this == COPY_TO_CLIPBOARD ? Environment.isBukkit() || Environment.isBungeeCord() ? Version.V1_15_2 : Version.V1_15 : ordinal() > 5 ? Version.V1_21_6 : null;
	}
	
	/**
	 * Checks if this click action is supported on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this click action is supported
	 */
	public boolean isSupported() {
		Version minimumVersion = getMinimumVersion();
		return minimumVersion == null || VersionUtils.getVersion().isAtLeast(minimumVersion);
	}
	
	/**
	 * Equivalent of <code>valueOf(String)</code>.
	 * 
	 * @param name Constant's name
	 * @return Pseudo-enum's constant
	 * @throws NullPointerException If <code>name == null</code>
	 * @throws IllegalArgumentException If {@link #values()}
	 * does not contain a constant with the specified name
	 */
	public static ClickActionAdapter valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static ClickActionAdapter[] values() {
		return VALUES;
	}
	
}
