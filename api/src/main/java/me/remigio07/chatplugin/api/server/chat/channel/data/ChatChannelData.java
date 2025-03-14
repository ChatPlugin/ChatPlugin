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
 * Represents the common data associated to a {@link ChatChannel}.
 */
public abstract class ChatChannelData {
	
	/**
	 * Gets the recipients of a message sent
	 * to this channel by the specified player.
	 * 
	 * @param players Players to search among
	 * @param sender Message's sender
	 * @param excludeVanished Whether to exclude vanished players
	 * @return Message's recipients
	 * @see ChatChannel#getRecipients(ChatPluginServerPlayer, boolean)
	 */
	public List<ChatPluginServerPlayer> getRecipients(List<ChatPluginServerPlayer> players, ChatPluginServerPlayer sender, boolean excludeVanished) {
		return players
				.stream()
				.filter(other -> !excludeVanished || !other.isVanished())
				.collect(Collectors.toList());
	}
	
	/**
	 * Translates {@link #getType()}-specific placeholders only.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 * @see ChatChannel#formatPlaceholders(String, Language)
	 */
	public String formatPlaceholders(String input, Language language) {
		return input;
	}
	
	/**
	 * Gets this channel's type.
	 * 
	 * @return Channel's type
	 */
	public abstract ChatChannelType getType();
	
}
