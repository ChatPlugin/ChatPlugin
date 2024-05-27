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

package me.remigio07.chatplugin.server.f3servername;

import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;

public abstract class BaseF3ServerNameManager extends F3ServerNameManager {
	
	protected long ms;
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(timerTaskID);
		f3ServerNames.clear();
		placeholderTypes.clear();
		
		randomOrder = false;
		sendingTimeout = 0;
		timerTaskID = timerIndex = -1;
	}
	
}
