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

package me.remigio07.chatplugin.api.server.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.event.chat.StaffChatEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Manager that handles the Staff chat. See wiki for more info:
 * <br><a href="https://github.com/ChatPlugin/ChatPlugin/wiki/Chat/staff-chat">ChatPlugin wiki/Chat/Staff chat</a>
 */
public abstract class StaffChatManager implements ChatPluginManager {
	
	protected static StaffChatManager instance;
	protected boolean enabled;
	protected String playerChatFormat, playerTerminalFormat, consoleChatFormat, consoleTerminalFormat;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected List<UUID> players = new ArrayList<>();
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
	 * Gets the chat format displayed to Staff members
	 * when a player sends a message using the Staff chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.staff-chat.format.player.chat" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Players' chat format
	 */
	public String getPlayerChatFormat() {
		return playerChatFormat;
	}
	
	/**
	 * Gets the chat format displayed in the terminal
	 * when a player sends a message using the Staff chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.staff-chat.format.player.terminal" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Players' terminal format
	 */
	public String getPlayerTerminalFormat() {
		return playerTerminalFormat;
	}
	
	/**
	 * Gets the chat format displayed to Staff members
	 * when the console sends a message using the Staff chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.staff-chat.format.console.chat" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Console's chat format
	 */
	public String getConsoleChatFormat() {
		return consoleChatFormat;
	}
	
	/**
	 * Gets the chat format displayed in the terminal
	 * when the console sends a message using the Staff chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.staff-chat.format.console.terminal" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Console's terminal format
	 */
	public String getConsoleTerminalFormat() {
		return consoleTerminalFormat;
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
	 * Do not modify the returned list.
	 * Use {@link #addPlayer(UUID)}, {@link #removePlayer(UUID)},
	 * {@link #clearPlayers()} and {@link #isUsingStaffChat(UUID)}
	 * to interact with it.
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
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @see StaffChatEvent
	 */
	public abstract void sendConsoleMessage(String message);
	
}
