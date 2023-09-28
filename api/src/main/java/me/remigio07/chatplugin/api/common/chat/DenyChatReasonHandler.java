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

package me.remigio07.chatplugin.api.common.chat;

import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.FormattedChatManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;

/**
 * Interface used to indicate that a manager contains at least a method used to check if messages may be sent.
 * Refer to {@link DenyChatReason} to check the reasons why messages may be blocked and their respective handlers.
 * 
 * <p>This interface is only used by the server implementations but is contained in the <code>common</code> package
 * because {@link MuteManager}, which is present on both the server and proxy implementations, implements it.</p>
 * 
 * <p><strong>Known implementing classes:</strong> [{@link AntispamManager}, {@link FormattedChatManager}, {@link MuteManager}, {@link ChatManager}, {@link VanishManager}]</p>
 */
public interface DenyChatReasonHandler extends ChatPluginManager {
	
}
