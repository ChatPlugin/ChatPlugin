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

package me.remigio07_.chatplugin.api.server.event.player;

import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;

/**
 * Represents the event called just before a {@link ChatPluginServerPlayer} is unloaded.
 * 
 * @see ServerPlayerManager#unloadPlayer(PlayerAdapter)
 */
public class ServerPlayerUnloadEvent implements ChatPluginServerPlayerEvent {
	
	private ChatPluginServerPlayer player;
	
	/**
	 * Constructs a new server player unload event.
	 * 
	 * @param player Player involved
	 */
	public ServerPlayerUnloadEvent(ChatPluginServerPlayer player) {
		this.player = player;
	}
	
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
}
