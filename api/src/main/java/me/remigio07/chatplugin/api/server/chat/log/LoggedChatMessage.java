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

package me.remigio07.chatplugin.api.server.chat.log;

import me.remigio07.chatplugin.api.server.chat.ChatManager;

/**
 * Represents a logged chat message handled by the {@link ChatManager}.
 */
public interface LoggedChatMessage extends LoggedMessage {
	
	/**
	 * Array containing all available placeholders that can
	 * be translated with a logged chat message's information. See wiki for more info:
	 * <br><a href="https://github.com/ChatPlugin/ChatPlugin/wiki/Chat#placeholders">ChatPlugin wiki/Chat/Log/Placeholders</a>
	 * 
	 * <p><strong>Content:</strong> ["sender", "sender_uuid", "rank_id", "server", "world", "content", "date", "denied", "deny_chat_reason"]</p>
	 */
	public static final String[] PLACEHOLDERS = new String[] { "sender", "sender_uuid", "rank_id", "server", "world", "content", "date", "denied", "deny_chat_reason" };
	
}
