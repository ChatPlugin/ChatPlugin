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

package me.remigio07_.chatplugin.server.bukkit.integration.cosmetic.gadgetsmenu;

import java.util.List;

import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.server.integration.cosmetics.CosmeticsIntegration;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class GadgetsMenuIntegration extends ChatPluginBukkitIntegration<CosmeticsIntegration> implements CosmeticsIntegration {
	
	private GadgetsMenuCommonIntegration integration;
	
	public GadgetsMenuIntegration() {
		super(IntegrationType.GADGETSMENU);
		try {
			Class.forName("com.yapzhenyie.GadgetsMenu.cosmetics.Category");
			integration = new GadgetsMenuPremiumIntegration();
		} catch (ClassNotFoundException e) {
			integration = new GadgetsMenuFreeIntegration();
		}
	}
	
	@Override
	public List<String> removeActiveCosmetics(ChatPluginServerPlayer player) {
		return integration.removeActiveCosmetics(player);
	}
	
}
