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

import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelType;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a local (ranged) {@link ChatChannel}'s data.
 * 
 * <p><strong>Type:</strong> {@link ChatChannelType#LOCAL}</p>
 */
public class LocalChatChannel extends ChatChannelData {
	
	/**
	 * Array containing all available placeholders that can
	 * be translated with a local channel's information.
	 * 
	 * <p><strong>Content:</strong> ["channel_range"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#placeholders">ChatPlugin wiki/Modules/Chat/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "channel_range" };
	private int range;
	
	/**
	 * Constructs a new local chat channel.
	 * 
	 * @param range Channel's range, in blocks
	 */
	public LocalChatChannel(int range) {
		this.range = range;
	}
	
	@Override
	public ChatChannelType getType() {
		return ChatChannelType.LOCAL;
	}
	
	@Override
	public List<ChatPluginServerPlayer> getRecipients(List<ChatPluginServerPlayer> players, ChatPluginServerPlayer sender, boolean excludeVanished) {
		return super.getRecipients(players, sender, excludeVanished)
				.stream()
				.filter(other -> sender.getWorld().equals(other.getWorld()) && sender.getDistance(other.getX(), other.getY(), other.getZ()) < range)
				.collect(Collectors.toList());
	}
	
	@Override
	public String formatPlaceholders(String input, Language language) {
		return input.replace("{channel_range}", String.valueOf(range));
	}
	
	/**
	 * Gets this channel's spherical range.
	 * 
	 * @return Channel's range, in blocks
	 */
	public int getRange() {
		return range;
	}
	
}
