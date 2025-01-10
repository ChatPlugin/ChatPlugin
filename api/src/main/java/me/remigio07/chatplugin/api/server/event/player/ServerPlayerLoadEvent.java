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

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;

/**
 * Represents the event called after a {@link ChatPluginServerPlayer} is loaded.
 * 
 * @see ServerPlayerManager#loadPlayer(PlayerAdapter)
 */
public class ServerPlayerLoadEvent implements ChatPluginServerPlayerEvent {
	
	private ChatPluginServerPlayer player;
	private int loadTime;
	
	/**
	 * Constructs a new server player load event.
	 * 
	 * @param player Player involved
	 * @param loadTime Time elapsed, in milliseconds
	 */
	public ServerPlayerLoadEvent(ChatPluginServerPlayer player, int loadTime) {
		this.player = player;
		this.loadTime = loadTime;
	}
	
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the time spent loading the player.
	 * 
	 * @return Time elapsed, in milliseconds
	 */
	public int getLoadTime() {
		return loadTime;
	}
	
}
