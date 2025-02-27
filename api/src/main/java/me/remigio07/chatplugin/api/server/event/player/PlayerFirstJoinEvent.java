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

package me.remigio07.chatplugin.api.server.event.player;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;

/**
 * Represents the event called after a {@link ChatPluginServerPlayer} has joined for the first time.
 * 
 * <p><strong>Note:</strong> this event will be fired during {@link ServerPlayerManager#loadPlayer(PlayerAdapter)}
 * only if <code>!</code>{@link OfflinePlayer#hasPlayedBefore()}. Because of this, if {@link ProxyManager#isEnabled()},
 * this event is fired only on the first server the player joins. This does not include the proxy.</p>
 */
public class PlayerFirstJoinEvent implements ChatPluginServerPlayerEvent {
	
	private ChatPluginServerPlayer player;
	
	/**
	 * Constructs a new player first join event.
	 * 
	 * @param player Player involved
	 */
	public PlayerFirstJoinEvent(ChatPluginServerPlayer player) {
		this.player = player;
	}
	
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
}
