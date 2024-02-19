/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;

/**
 * Represents a logged private message handled by the {@link PrivateMessagesManager}.
 */
public interface LoggedPrivateMessage extends LoggedMessage {
	
	/**
	 * Array containing all available placeholders that can be
	 * translated with a logged private message's information.
	 * 
	 * <p><strong>Content:</strong> ["sender", "sender_uuid", "recipient", "recipient_uuid", "rank_id", "server", "world", "content", "date", "denied", "deny_chat_reason"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#placeholders">ChatPlugin wiki/Modules/Chat/Chat log/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = new String[] { "sender", "sender_uuid", "recipient", "recipient_uuid", "rank_id", "server", "world", "content", "date", "denied", "deny_chat_reason" };
	
	/**
	 * Gets this private message's recipient.
	 * 
	 * @return Private message's recipient
	 */
	public OfflinePlayer getRecipient();
	
}
