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

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called before a player's private message gets processed.
 * 
 * @see PrivateMessagesManager#sendMessage(ChatPluginServerPlayer, ChatPluginServerPlayer, String)
 */
public class PrePrivateMessageEvent extends PrivateMessageEvent implements CancellableEvent {
	
	private boolean cancelled;
	
	/**
	 * Constructs a new pre private message event.
	 * Specify <code>null</code> as either the sender
	 * or the recipient to indicate the console.
	 * 
	 * @param player Player involved
	 * @param recipient Recipient involved
	 * @param privateMessage Private message involved
	 */
	public PrePrivateMessageEvent(
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer player,
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer recipient,
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