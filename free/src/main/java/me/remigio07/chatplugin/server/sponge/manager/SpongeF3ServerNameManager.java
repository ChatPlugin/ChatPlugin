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

package me.remigio07.chatplugin.server.sponge.manager;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerName;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

public class SpongeF3ServerNameManager extends F3ServerNameManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		
		if (ConfigurationType.F3_SERVER_NAMES.get().getBoolean("f3-server-names.settings.enabled"))
			checkAvailability(true);
	}
	
	@Override
	public void run() {
		throw new UnsupportedOperationException("F3 server name is not available on Sponge");
	}
	
	@Override
	public void sendF3ServerName(F3ServerName f3ServerName, ChatPluginServerPlayer player) {
		throw new UnsupportedOperationException("F3 server name is not available on Sponge");
	}
	
}
