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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.server.bukkit.integration.economy;

import org.bukkit.Bukkit;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.permission.PermissionIntegration;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
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
		api = Bukkit.getServicesManager().getRegistration(Economy.class).getProvider();
		api2 = Bukkit.getServicesManager().getRegistration(Permission.class).getProvider();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public double getBalance(OfflinePlayer player) {
		return ((Economy) api).getBalance(player.getName());
	}
	
	@Override
	public boolean hasPermission(OfflinePlayer player, String permission) {
		return ((Permission) api2).playerHas(Bukkit.getWorlds().get(0).getName(), Bukkit.getOfflinePlayer(player.getUUID()), permission);
	}
	
}
