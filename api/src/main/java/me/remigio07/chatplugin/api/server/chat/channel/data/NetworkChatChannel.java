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

package me.remigio07.chatplugin.api.server.chat.channel.data;

import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelType;

/**
 * Represents a network (proxy-wide) {@link ChatChannel}'s data.
 * 
 * <p><strong>Type:</strong> {@link ChatChannelType#NETWORK}</p>
 */
public class NetworkChatChannel extends ChatChannelData {
	
	/**
	 * Constructs a new network chat channel.
	 */
	public NetworkChatChannel() {
		
	}
	
	@Override
	public ChatChannelType getType() {
		return ChatChannelType.NETWORK;
	}
	
}
