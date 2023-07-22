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

import me.remigio07_.chatplugin.api.common.event.player.ChatPluginPlayerEvent;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a {@link ChatPluginServerPlayer}-related event.
 */
public interface ChatPluginServerPlayerEvent extends ChatPluginPlayerEvent {
	
	/**
	 * Gets the player involved with this event.
	 * 
	 * @return Player involved
	 */
	public ChatPluginServerPlayer getPlayer();
	
}
