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

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a private message-related event.
 */
public abstract class PrivateMessageEvent implements ChatPluginServerPlayerEvent {
	
	protected ChatPluginServerPlayer player;
	protected OfflinePlayer recipient;
	protected String privateMessage;
	
	protected PrivateMessageEvent(ChatPluginServerPlayer player, OfflinePlayer recipient, String privateMessage) {
		this.player = player;
		this.recipient = recipient;
		this.privateMessage = privateMessage;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p>Will return <code>null</code> to indicate the console.</p>
	 */
	@Nullable(why = "Null to represent the console")
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets this private message's recipient.
	 * 
	 * <p>Will return <code>null</code> to indicate the console.</p>
	 * 
	 * @return Recipient involved
	 */
	@Nullable(why = "Null to represent the console")
	public OfflinePlayer getRecipient() {
		return recipient;
	}
	
	/**
	 * Gets the private message involved with this event
	 * 
	 * @return Private message involved
	 */
	public String getPrivateMessage() {
		return privateMessage;
	}
	
}
