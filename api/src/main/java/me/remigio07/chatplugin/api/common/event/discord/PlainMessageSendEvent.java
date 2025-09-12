/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.api.common.event.discord;

import me.remigio07.chatplugin.api.common.discord.DiscordBot;
import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;

/**
 * Represents the event called just before a plain
 * message is sent through the {@link DiscordBot}.
 * 
 * @see DiscordBot#sendPlainMessage(long, String)
 */
public class PlainMessageSendEvent implements ChatPluginEvent, CancellableEvent {
	
	private boolean cancelled;
	private long channelID;
	private String plainMessage;
	
	/**
	 * Constructs a new plain message send event.
	 * 
	 * @param channelID Event's channel's ID
	 * @param plainMessage Plain message involved
	 */
	public PlainMessageSendEvent(long channelID, String plainMessage) {
		this.channelID = channelID;
		this.plainMessage = plainMessage;
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
	 * Gets the ID of the channel the plain message has been sent through.
	 * 
	 * @return Event's channel's ID
	 */
	public long getChannelID() {
		return channelID;
	}
	
	/**
	 * Gets this event's plain message.
	 * 
	 * @return Plain message involved
	 */
	public String getPlainMessage() {
		return plainMessage;
	}
	
}
