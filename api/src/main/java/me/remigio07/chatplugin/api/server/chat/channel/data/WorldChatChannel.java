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

package me.remigio07.chatplugin.api.server.chat.channel.data;

import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelType;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a world-based {@link ChatChannel}'s data.
 * 
 * <p><strong>Type:</strong> {@link ChatChannelType#WORLD}</p>
 */
public class WorldChatChannel extends ChatChannelData {
	
	/**
	 * Constructs a new world chat channel.
	 */
	public WorldChatChannel() {
		
	}
	
	@Override
	public ChatChannelType getType() {
		return ChatChannelType.WORLD;
	}
	
	@Override
	public List<ChatPluginServerPlayer> getRecipients(List<ChatPluginServerPlayer> players, ChatPluginServerPlayer sender, boolean excludeVanished) {
		return super.getRecipients(players, sender, excludeVanished)
				.stream()
				.filter(other -> sender.getWorld().equals(other.getWorld()))
				.collect(Collectors.toList());
	}
	
}
