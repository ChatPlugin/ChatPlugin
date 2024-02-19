/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.api.proxy.event.player;

import me.remigio07.chatplugin.api.proxy.player.ChatPluginProxyPlayer;
import me.remigio07.chatplugin.api.proxy.player.ProxyPlayerManager;

/**
 * Represents the event called just before a {@link ChatPluginProxyPlayer} is unloaded.
 * 
 * @see ProxyPlayerManager#unloadPlayer(java.util.UUID)
 */
public class ProxyPlayerUnloadEvent implements ChatPluginProxyPlayerEvent {
	
	private ChatPluginProxyPlayer player;
	
	/**
	 * Constructs a new proxy player unload event.
	 * 
	 * @param player Player involved
	 */
	public ProxyPlayerUnloadEvent(ChatPluginProxyPlayer player) {
		this.player = player;
	}
	
	@Override
	public ChatPluginProxyPlayer getPlayer() {
		return player;
	}
	
}
