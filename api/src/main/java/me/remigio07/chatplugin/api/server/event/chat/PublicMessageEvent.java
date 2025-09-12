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

package me.remigio07.chatplugin.api.server.event.chat;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a public message-related event.
 */
public abstract class PublicMessageEvent extends ChatEvent {
	
	protected ChatChannel<? extends ChatChannelData> channel;
	
	protected PublicMessageEvent(
			ChatPluginServerPlayer player,
			String message,
			ChatChannel<? extends ChatChannelData> channel
			) {
		super(player, message);
		this.channel = channel;
	}
	
	/**
	 * Gets the channel the message has been sent on.
	 * 
	 * <p>Will return <code>null</code> if
	 * <code>!</code>{@link ChatChannelsManager#isEnabled()}.</p>
	 * 
	 * @return Channel involved
	 */
	@Nullable(why = "Null if !ChatChannelsManager#isEnabled()")
	public ChatChannel<? extends ChatChannelData> getChannel() {
		return channel;
	}
	
}
