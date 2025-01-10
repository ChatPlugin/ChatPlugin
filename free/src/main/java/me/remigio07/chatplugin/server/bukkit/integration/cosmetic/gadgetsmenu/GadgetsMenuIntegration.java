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

package me.remigio07.chatplugin.server.bukkit.integration.cosmetic.gadgetsmenu;

import java.util.List;

import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.integration.cosmetics.CosmeticsIntegration;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

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
	
	public boolean isRenamingPet(ChatPluginServerPlayer player) {
		return integration instanceof GadgetsMenuPremiumIntegration ? GadgetsMenuAPI.getPlayerManager(player.toAdapter().bukkitValue()).isRenamingPet() : false;
	}
	
}
