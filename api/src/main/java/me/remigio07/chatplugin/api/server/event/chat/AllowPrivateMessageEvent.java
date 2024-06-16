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

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called before a player's private message is sent.
 * 
 * @see PrivateMessagesManager#sendPrivateMessage(ChatPluginServerPlayer, OfflinePlayer, String)
 * @see PrivateMessagesManager#sendReply(ChatPluginServerPlayer, String)
 */
public class AllowPrivateMessageEvent extends PrivateMessageEvent implements CancellableEvent {
	
	private boolean cancelled;
	
	/**
	 * Constructs a new allow private message event.
	 * 
	 * <p>Specify <code>null</code> as either the sender
	 * or the recipient to indicate the console.</p>
	 * 
	 * @param player Player involved
	 * @param recipient Recipient involved
	 * @param privateMessage Private message involved
	 */
	public AllowPrivateMessageEvent(
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer player,
			@Nullable(why = "Null to represent the console") OfflinePlayer recipient,
			String privateMessage
			) {
		super(player, recipient, privateMessage);
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
