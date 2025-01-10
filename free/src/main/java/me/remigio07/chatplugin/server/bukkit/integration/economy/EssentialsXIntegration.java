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

import com.earth2me.essentials.Essentials;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.server.integration.economy.EconomyIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class EssentialsXIntegration extends ChatPluginBukkitIntegration<EconomyIntegration> implements EconomyIntegration {
	
	public EssentialsXIntegration() {
		super(IntegrationType.ESSENTIALSX);
	}
	
	@Override
	public double getBalance(OfflinePlayer player) {
		return ((Essentials) plugin).getUser(player.getUUID()).getMoney().doubleValue();
	}
	
}
