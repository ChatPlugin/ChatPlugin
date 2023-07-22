/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.server.chat.log;

import java.util.List;

import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07_.chatplugin.api.server.chat.log.ChatLogManager;
import me.remigio07_.chatplugin.api.server.chat.log.LoggedMessage;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;

public class DummyChatLogManager extends ChatLogManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public @NotNull List<LoggedMessage> getLoggedMessages(OfflinePlayer player, long timeAgo, String query) {
		return null;
	}
	
	@Override
	public void logMessage(ChatPluginServerPlayer player, String message, @Nullable(why = "Message may not have been blocked") DenyChatReason denyChatReason) {
		
	}
	
}
