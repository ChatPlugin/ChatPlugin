/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.util.packet.type;

import me.remigio07.chatplugin.api.common.discord.DiscordBot;

/**
 * Represents the type of a packet sent through the <code>DiscordMessage</code> subchannel.
 */
public enum DiscordMessagePacketType {
	
	/**
	 * Represents a plain message sent through the bot using
	 * {@link DiscordBot#sendPlainMessage(long, String)}.
	 */
	PLAIN,
	
	/**
	 * Represents an embed message sent through the bot using
	 * {@link DiscordBot#sendEmbedMessage(long, Object)}.
	 * The message is serialized and represented as a JSON string.
	 */
	EMBED;
	
}
