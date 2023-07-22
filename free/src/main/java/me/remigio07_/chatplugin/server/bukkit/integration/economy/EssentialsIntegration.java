/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.server.bukkit.integration.economy;

import com.earth2me.essentials.Essentials;

import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.server.integration.economy.EconomyIntegration;
import me.remigio07_.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class EssentialsIntegration extends ChatPluginBukkitIntegration<EconomyIntegration> implements EconomyIntegration {
	
	public EssentialsIntegration() {
		super(IntegrationType.ESSENTIALS);
	}
	
	@Override
	public double getBalance(OfflinePlayer player) {
		return ((Essentials) plugin).getUser(player.getUUID()).getMoney().doubleValue();
	}
	
}
