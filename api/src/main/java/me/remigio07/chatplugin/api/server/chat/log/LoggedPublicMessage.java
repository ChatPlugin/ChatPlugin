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

package me.remigio07.chatplugin.api.server.chat.log;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;

/**
 * Represents a logged public message handled by the {@link ChatManager}.
 */
public interface LoggedPublicMessage extends LoggedMessage {
	
	/**
	 * Array containing all available placeholders that can
	 * be translated with a logged public message's information.
	 * 
	 * <p><strong>Content:</strong> ["sender", "sender_uuid", "rank_id", "server", "world", "channel_id", "content", "date", "denied", "deny_chat_reason"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#placeholders-1">ChatPlugin wiki/Modules/Chat/Chat log/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "sender", "sender_uuid", "rank_id", "server", "world", "channel_id", "content", "date", "denied", "deny_chat_reason" };
	
	/**
	 * Gets the ID of channel this message was sent to.
	 * 
	 * <p>Will return <code>null</code> if {@link ChatChannelsManager}
	 * was not enabled when this message has been sent.</p>
	 * 
	 * @return Message's channel's ID
	 */
	@Nullable(why = "Null if ChatChannelsManager was not enabled")
	public String getChannelID();
	
}
