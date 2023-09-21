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

package me.remigio07.chatplugin.server.bukkit.integration.permission;

import org.bukkit.Bukkit;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.permission.PermissionIntegration;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;
import net.luckperms.api.LuckPerms;

public class LuckPermsIntegration extends ChatPluginBukkitIntegration<PermissionIntegration> implements PermissionIntegration {
	
	public LuckPermsIntegration() {
		super(IntegrationType.LUCKPERMS);
	}
	
	@Override
	protected void loadAPI() {
		api = Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider();
	}
	
	@Override
	public boolean hasPermission(OfflinePlayer player, String permission) {
		return ((LuckPerms) api).getUserManager().loadUser(player.getUUID(), player.getName()).join().getCachedData().getPermissionData().checkPermission(permission).asBoolean();
	}
	
}
