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

package me.remigio07.chatplugin.api.common.event.telegram;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.common.telegram.TelegramBot;

/**
 * Represents the event called just before a
 * message is sent through the {@link TelegramBot}.
 * 
 * @see TelegramBot#sendMessage(long, String)
 * @see TelegramBot#sendMessage(String)
 */
public class MessageSendEvent implements ChatPluginEvent, CancellableEvent {
	
	private boolean cancelled;
	private long chatID;
	private String message;
	
	/**
	 * Constructs a new message send event.
	 * 
	 * @param chatID Event's chat's ID
	 * @param message Message involved
	 */
	public MessageSendEvent(long chatID, String message) {
		this.chatID = chatID;
		this.message = message;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	/**
	 * Gets the ID of the chat the message has been sent through.
	 * 
	 * @return Event's chat's ID
	 */
	public long getChatID() {
		return chatID;
	}
	
	/**
	 * Gets this event's message.
	 * 
	 * @return Message involved
	 */
	public String getMessage() {
		return message;
	}
	
}
