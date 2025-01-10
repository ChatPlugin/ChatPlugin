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

package me.remigio07.chatplugin.server.join_quit;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager.QuitPacket;
import me.remigio07.chatplugin.api.server.join_quit.SwitchMessageManager;
import me.remigio07.chatplugin.api.server.language.Language;

public class DummySwitchMessageManager extends SwitchMessageManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public @NotNull String getSwitchMessage(QuitPacket packet, Language language) {
		return null;
	}
	
	@Override
	public void sendSwitchMessage(QuitPacket packet, String newServerDisplayName) {
		
	}
	
}
