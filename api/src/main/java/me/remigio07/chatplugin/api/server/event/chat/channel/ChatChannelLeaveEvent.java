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

package me.remigio07.chatplugin.api.server.event.chat.channel;

import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called before a player leaves a channel.
 * 
 * @see ChatPluginServerPlayer#leaveChannel(ChatChannel)
 */
public class ChatChannelLeaveEvent extends ChatChannelEvent {
	
	/**
	 * Constructs a new channel leave event.
	 * 
	 * @param player Player involved
	 * @param channel Channel involved
	 */
	public ChatChannelLeaveEvent(ChatPluginServerPlayer player, ChatChannel<? extends ChatChannelData> channel) {
		super(player, channel);
	}
	
}
