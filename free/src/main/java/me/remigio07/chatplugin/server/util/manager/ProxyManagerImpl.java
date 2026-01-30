/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.server.util.manager;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ServerInformation;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.common.util.Utils;

public class ProxyManagerImpl extends ProxyManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		serverID = ConfigurationType.CONFIG.get().getString("multi-instance-mode.server-id");
		serverDisplayName = ConfigurationType.CONFIG.get().getString("multi-instance-mode.server-display-name");
		
		if (serverID.equals(DEFAULT_SERVER_ID))
			LogManager.log("Server ID at \"multi-instance-mode.server-id\" in config.yml is still set to the default value. You should change it even if you are not using multi instance mode.", 1);
		else if (!isValidServerID(serverID)) {
			serverID = "invalid-ID";
			LogManager.log("Invalid server ID set at \"multi-instance-mode.server-id\" in config.yml: the ID can only contain alphanumeric values, hypens or underscores and it must be between 3 and 36 characters long and it cannot be \"proxy\". You should change it even if you are not using multi instance mode.", 1);
		} serversInformation.put(serverID, new ServerInformation(serverID, 0, 0));
		
		long serverInformationUpdateTimeout = Utils.getTime(ConfigurationType.CONFIG.get().getString("multi-instance-mode.server-information-update-timeout"), false, false);
		taskIDs[0] = TaskManager.scheduleAsync(() -> {
			if (VanishManager.getInstance() != null)
				serversInformation.get(serverID)
				.setOnlinePlayers(PlayerAdapter.getOnlinePlayers().size())
				.setVanishedPlayers(VanishManager.getInstance().getOnlineServer());
		}, 1000L, serverInformationUpdateTimeout == -1 || serverInformationUpdateTimeout > 300000L ? 60000L : serverInformationUpdateTimeout);
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		TaskManager.cancelAsync(taskIDs[0]);
		serversInformation.clear();
		
		serverID = serverDisplayName = null;
	}
	
}
