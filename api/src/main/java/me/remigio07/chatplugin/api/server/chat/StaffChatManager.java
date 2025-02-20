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

package me.remigio07.chatplugin.api.server.chat;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.event.chat.StaffChatEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Manager that handles the Staff chat.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#staff-chat">ChatPlugin wiki/Modules/Chat/Staff chat</a>
 */
public abstract class StaffChatManager implements ChatPluginManager {
	
	protected static StaffChatManager instance;
	protected boolean enabled;
	protected String playerFormat, consoleFormat;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected List<UUID> players = new CopyOnWriteArrayList<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.staff-chat.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the chat format displayed to Staff members and the
	 * console when players send a message using the Staff chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.staff-chat.format.player" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Players' format
	 */
	public String getPlayerFormat() {
		return playerFormat;
	}
	
	/**
	 * Gets the chat format displayed to Staff members and the
	 * console when the console sends a message using the Staff chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.staff-chat.format.console" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Console's format
	 */
	public String getConsoleFormat() {
		return consoleFormat;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate messages sent using the Staff chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.staff-chat.placeholder-types" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Placeholders used to translate messages
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the list of who has the Staff chat mode enabled.
	 * 
	 * <p>Do <em>not</em> modify the returned list.
	 * Use {@link #addPlayer(UUID)}, {@link #removePlayer(UUID)},
	 * {@link #clearPlayers()} and {@link #isUsingStaffChat(UUID)}
	 * to interact with it.</p>
	 * 
	 * @return Staff chat's users' list
	 */
	public List<UUID> getPlayers() {
		return players;
	}
	
	/**
	 * Adds a player to {@link #getPlayers()}.
	 * 
	 * @param player Player to add
	 */
	public void addPlayer(UUID player) {
		if (!players.contains(player))
			players.add(player);
	}
	
	/**
	 * Removes a player from {@link #getPlayers()}.
	 * 
	 * @param player Player to remove
	 */
	public void removePlayer(UUID player) {
		players.remove(player);
	}
	
	/**
	 * Clears {@link #getPlayers()}.
	 */
	public void clearPlayers() {
		players.clear();
	}
	
	/**
	 * Checks if the specified player has the Staff chat mode enabled.
	 * 
	 * @param player Player to check
	 * @return Whether the specified player is using the Staff chat
	 */
	public boolean isUsingStaffChat(UUID player) {
		return players.contains(player);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static StaffChatManager getInstance() {
		return instance;
	}
	
	/**
	 * Makes a player send a message using the Staff chat.
	 * 
	 * @param player Message's sender
	 * @param message Message to send
	 * @see StaffChatEvent
	 */
	public abstract void sendPlayerMessage(ChatPluginServerPlayer player, String message);
	
	/**
	 * Makes the console send a message using the Staff chat.
	 * 
	 * @param message Message to send
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.isEmpty()</code>
	 * @see StaffChatEvent
	 */
	public abstract void sendConsoleMessage(String message);
	
}
