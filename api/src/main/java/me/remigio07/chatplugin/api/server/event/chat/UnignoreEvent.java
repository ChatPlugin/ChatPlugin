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

package me.remigio07.chatplugin.api.server.event.chat;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;

/**
 * Represents an event called before a player stops ignoring another player.
 * 
 * @see PlayerIgnoreManager#unignore(OfflinePlayer, OfflinePlayer)
 */
public class UnignoreEvent extends IgnoreEvent {
	
	/**
	 * Constructs a new unignore event.
	 * 
	 * @param player Player involved
	 * @param ignoredPlayer Ignored player
	 */
	public UnignoreEvent(OfflinePlayer player, OfflinePlayer ignoredPlayer) {
		super(player, ignoredPlayer);
	}
	
}
