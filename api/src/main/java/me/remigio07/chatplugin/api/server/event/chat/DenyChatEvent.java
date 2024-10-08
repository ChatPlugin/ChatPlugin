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

package me.remigio07.chatplugin.api.server.event.chat;

import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called before a player's message is blocked.
 */
public class DenyChatEvent extends PublicMessageEvent {
	
	private DenyChatReason<?> reason;
	
	/**
	 * Constructs a new deny chat event.
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @param global Whether the message is global
	 * @param reason Deny chat reason
	 */
	public DenyChatEvent(ChatPluginServerPlayer player, String message, boolean global, DenyChatReason<?> reason) {
		super(player, message, global);
		this.reason = reason;
	}
	
	/**
	 * Gets the reason why the message has been denied.
	 * 
	 * @return Deny chat reason
	 */
	public DenyChatReason<?> getReason() {
		return reason;
	}
	
}
