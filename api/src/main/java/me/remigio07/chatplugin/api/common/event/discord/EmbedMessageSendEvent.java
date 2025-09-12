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
 * Represents the event called just before an embed
 * message is sent through the {@link DiscordBot}.
 * 
 * @see DiscordBot#sendEmbedMessage(long, Object)
 */
public class EmbedMessageSendEvent implements ChatPluginEvent, CancellableEvent {
	
	private boolean cancelled;
	private long channelID;
	private Object embedMessage;
	
	/**
	 * Constructs a new embed message send event.
	 * 
	 * @param channelID Event's channel's ID
	 * @param embedMessage Embed message involved
	 */
	public EmbedMessageSendEvent(long channelID, Object embedMessage) {
		this.channelID = channelID;
		this.embedMessage = embedMessage;
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
	 * Gets the ID of the channel the embed message has been sent through.
	 * 
	 * @return Event's channel's ID
	 */
	public long getChannelID() {
		return channelID;
	}
	
	/**
	 * Gets this event's embed message.
	 * 
	 * @return Embed message involved
	 */
	public Object getEmbedMessage() {
		return embedMessage;
	}
	
}
