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

package me.remigio07.chatplugin.api.server.event.chat;

import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamResult;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called before a player's private message is blocked.
 * 
 * @see PrivateMessagesManager#sendPrivateMessage(ChatPluginServerPlayer, OfflinePlayer, String)
 * @see PrivateMessagesManager#sendReply(ChatPluginServerPlayer, String)
 */
public class DenyPrivateMessageEvent extends PrivateMessageEvent {
	
	private DenyChatReason<?> denyChatReason;
	private AntispamResult antispamResult;
	
	/**
	 * Constructs a new deny private message event.
	 * 
	 * <p>Specify <code>null</code> as either the sender
	 * or the recipient to indicate the console.
	 * Specify <code>null</code> as the antispam result to indicate
	 * that the antispam has allowed this private message.</p>
	 * 
	 * @param player Player involved
	 * @param recipient Recipient involved
	 * @param privateMessage Private message involved
	 * @param denyChatReason Reason why the private message has been blocked
	 * @param antispamResult Result of the antispam's check
	 */
	public DenyPrivateMessageEvent(
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer player,
			@Nullable(why = "Null to represent the console") OfflinePlayer recipient,
			String privateMessage,
			DenyChatReason<?> denyChatReason,
			@Nullable(why = "Null if getDenyChatReason().getHandlerClass() != AntispamManager.class") AntispamResult antispamResult
			) {
		super(player, recipient, privateMessage);
		this.denyChatReason = denyChatReason;
		this.antispamResult = antispamResult;
	}
	
	/**
	 * Gets the reason why the private message has been blocked.
	 * 
	 * @return Reason why the private message has been blocked
	 */
	public DenyChatReason<?> getDenyChatReason() {
		return denyChatReason;
	}
	
	/**
	 * Gets the result of {@link AntispamManager#check(ChatPluginServerPlayer, String, List)}.
	 * 
	 * <p>Will return <code>null</code> if the antispam has allowed this private message.
	 * 
	 * @return Result of the antispam's check
	 */
	@Nullable(why = "Null if getDenyChatReason().getHandlerClass() != AntispamManager.class")
	public AntispamResult getAntispamResult() {
		return antispamResult;
	}
	
}
