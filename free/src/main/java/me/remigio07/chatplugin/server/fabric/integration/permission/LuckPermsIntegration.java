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

package me.remigio07.chatplugin.server.fabric.integration.permission;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.permission.PermissionIntegration;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.server.fabric.integration.ChatPluginFabricIntegration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class LuckPermsIntegration extends ChatPluginFabricIntegration<PermissionIntegration> implements PermissionIntegration {
	
	public LuckPermsIntegration() {
		super(IntegrationType.LUCKPERMS);
	}
	
	@Override
	protected void loadAPI() {
		api = LuckPermsProvider.get();
	}
	
	@Override
	public boolean hasPermission(OfflinePlayer player, String permission) {
		return ((LuckPerms) api).getUserManager().loadUser(player.getUUID(), player.getName()).join().getCachedData().getPermissionData().checkPermission(permission).asBoolean();
	}
	
}
