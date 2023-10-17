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

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called before a player's private message is blocked.
 * 
 * @see PrivateMessagesManager#sendMessage(ChatPluginServerPlayer, ChatPluginServerPlayer, String)
 */
public class DenyPrivateMessageEvent extends PrivateMessageEvent {
	
	private DenyChatReason<?> reason;
	
	/**
	 * Constructs a new deny private message event.
	 * 
	 * <p>Specify <code>null</code> as either the sender
	 * or the recipient to indicate the console.</p>
	 * 
	 * @param player Player involved
	 * @param recipient Recipient involved
	 * @param privateMessage Private message involved
	 * @param reason Deny chat reason
	 */
	public DenyPrivateMessageEvent(
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer player,
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer recipient,
			String privateMessage,
			DenyChatReason<?> reason
			) {
		super(player, recipient, privateMessage);
		this.reason = reason;
	}
	
	/**
	 * Gets the reason why the private message has been denied.
	 * 
	 * @return Deny chat reason
	 */
	public DenyChatReason<?> getReason() {
		return reason;
	}
	
}
