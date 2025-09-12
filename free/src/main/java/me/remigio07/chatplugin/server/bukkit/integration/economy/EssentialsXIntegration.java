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

package me.remigio07.chatplugin.server.bukkit.integration.economy;

import java.math.BigDecimal;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.utils.NumberUtil;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.server.integration.economy.EconomyIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class EssentialsXIntegration extends ChatPluginBukkitIntegration<EconomyIntegration> implements EconomyIntegration {
	
	public EssentialsXIntegration() {
		super(IntegrationType.ESSENTIALSX);
	}
	
	@Override
	public double getBalance(OfflinePlayer player) {
		return isEcoDisabled() ? Double.MIN_VALUE : ((Essentials) plugin).getUser(player.getUUID()).getMoney().doubleValue();
	}
	
	@Override
	public String formatBalance(double balance) {
		return isEcoDisabled() ? Utils.NOT_APPLICABLE : Bridge.formatBalance(balance, plugin);
	}
	
	private boolean isEcoDisabled() {
		return ((Essentials) plugin).getSettings().isEcoDisabled();
	}
	
	private static class Bridge {
		
		public static String formatBalance(double balance, Object plugin) {
			return NumberUtil.displayCurrency(BigDecimal.valueOf(balance), (Essentials) plugin);
		}
		
	}
	
}
