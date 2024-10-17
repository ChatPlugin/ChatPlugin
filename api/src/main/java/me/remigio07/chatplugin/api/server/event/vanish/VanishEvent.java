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

package me.remigio07.chatplugin.api.server.event.vanish;

import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a vanish-related event.
 */
abstract class VanishEvent implements ChatPluginServerPlayerEvent { // not public because name might be misleading
	
	protected ChatPluginServerPlayer player;
	
	protected VanishEvent(ChatPluginServerPlayer player) {
		this.player = player;
	}
	
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
}
