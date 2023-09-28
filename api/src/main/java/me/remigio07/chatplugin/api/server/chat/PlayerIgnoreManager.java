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

import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.event.chat.IgnoreEvent;
import me.remigio07.chatplugin.api.server.event.chat.UnignoreEvent;

/**
 * Manager that manages messages sent by players ignored by other players.
 */
public abstract class PlayerIgnoreManager implements ChatPluginManager {
	
	protected static PlayerIgnoreManager instance;
	protected boolean enabled;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ignore.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static PlayerIgnoreManager getInstance() {
		return instance;
	}
	
	/**
	 * Makes the specified player ignore another player.
	 * Will return <code>false</code> if one of the following is verified:
	 * 
	 * 	<ul>
	 * 		<li><code>!</code>{@link #isEnabled()}</li>
	 * 		<li>they are already ignoring that player </li>
	 * 		<li>{@link IgnoreEvent} was cancelled</li>
	 * 		<li>storage read/write operations failed</li>
	 * 	</ul>
	 * 
	 * @param player Target player
	 * @param ignoredPlayer Player to ignore
	 * @return Whether the player has been ignored
	 * @throws IndexOutOfBoundsException If they are already ignoring 25 players
	 * @throws IllegalArgumentException If the player and the ignored player correspond
	 * @see IgnoreEvent
	 */
	public abstract boolean ignore(OfflinePlayer player, OfflinePlayer ignoredPlayer);
	
	/**
	 * Makes the specified player unignore another player.
	 * Will return <code>false</code> if one of the following is verified:
	 * 
	 * 	<ul>
	 * 		<li><code>!</code>{@link #isEnabled()}</li>
	 * 		<li>they are not ignoring that player</li>
	 * 		<li>{@link UnignoreEvent} was cancelled</li>
	 * 		<li>storage read/write operations failed</li>
	 * 	</ul>
	 * 
	 * @param player Target player
	 * @param ignoredPlayer Player to unignore
	 * @return Whether the player has been unignored
	 * @throws IllegalArgumentException If the player and the ignored player correspond
	 * @see UnignoreEvent
	 */
	public abstract boolean unignore(OfflinePlayer player, OfflinePlayer ignoredPlayer);
	
	/**
	 * Gets a list of players ignored by the specified player.
	 * 
	 * @param player Target player
	 * @return Ignored players' list
	 */
	public abstract List<OfflinePlayer> getIgnoredPlayers(OfflinePlayer player);
	
}
