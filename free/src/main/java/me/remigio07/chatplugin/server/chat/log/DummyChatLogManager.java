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

package me.remigio07.chatplugin.server.chat.log;

import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.chat.log.ChatLogManager;
import me.remigio07.chatplugin.api.server.chat.log.LoggedPrivateMessage;
import me.remigio07.chatplugin.api.server.chat.log.LoggedPublicMessage;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

public class DummyChatLogManager extends ChatLogManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public @NotNull List<LoggedPublicMessage> getLoggedPublicMessages(OfflinePlayer sender, long timeAgo, String query) {
		return null;
	}
	
	@Override
	public @NotNull List<LoggedPrivateMessage> getLoggedPrivateMessages(OfflinePlayer sender, long timeAgo, String query) {
		return null;
	}
	
	@Override
	public void logPublicMessage(
			ChatPluginServerPlayer sender,
			String message,
			boolean global,
			@Nullable(why = "Public message may not have been blocked") DenyChatReason<?> denyChatReason
			) {
		
	}
	
	@Override
	public void logPrivateMessage(
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer sender,
			@Nullable(why = "Null to represent the console") OfflinePlayer recipient,
			String privateMessage,
			@Nullable(why = "Private message may not have been blocked") DenyChatReason<?> denyChatReason
			) {
		
	}
	
}
