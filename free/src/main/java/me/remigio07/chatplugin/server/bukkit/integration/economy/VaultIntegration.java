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

package me.remigio07.chatplugin.server.bukkit.integration.economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.permission.PermissionIntegration;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.server.integration.economy.EconomyIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class VaultIntegration extends ChatPluginBukkitIntegration<EconomyIntegration> implements EconomyIntegration, PermissionIntegration {
	
	private Object api2;
	
	public VaultIntegration() {
		super(IntegrationType.VAULT);
	}
	
	@Override
	protected void loadAPI() {
		RegisteredServiceProvider<Permission> permissionService = Bukkit.getServicesManager().getRegistration(Permission.class);
		RegisteredServiceProvider<Economy> economyService = Bukkit.getServicesManager().getRegistration(Economy.class);
		
		if (permissionService != null)
			api = permissionService.getProvider();
		if (economyService != null)
			api2 = economyService.getProvider();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public double getBalance(OfflinePlayer player) {
		return api2 == null ? Double.MIN_VALUE : ((Economy) api2).getBalance(player.getName());
	}
	
	@Override
	public String formatBalance(double balance) {
		return api2 == null ? Utils.NOT_APPLICABLE : ((Economy) api2).format(balance);
	}
	
	@Override
	public boolean hasPermission(OfflinePlayer player, String permission) {
		return ((Permission) api).playerHas(Bukkit.getWorlds().get(0).getName(), Bukkit.getOfflinePlayer(player.getUUID()), permission);
	}
	
}
