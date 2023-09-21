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

package me.remigio07.chatplugin.api.server.event.player;

import java.util.UUID;

import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;

/**
 * Represents the event called just before a {@link ChatPluginServerPlayer} is unloaded.
 * 
 * @see ServerPlayerManager#unloadPlayer(UUID)
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
