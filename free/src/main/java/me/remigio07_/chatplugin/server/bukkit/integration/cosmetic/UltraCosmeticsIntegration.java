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

package me.remigio07_.chatplugin.server.bukkit.integration.cosmetic;

import java.util.ArrayList;
import java.util.List;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.cosmetics.Category;
import be.isach.ultracosmetics.player.UltraPlayer;
import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.server.integration.cosmetics.CosmeticsIntegration;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class UltraCosmeticsIntegration extends ChatPluginBukkitIntegration<CosmeticsIntegration> implements CosmeticsIntegration {
	
	public UltraCosmeticsIntegration() {
		super(IntegrationType.ULTRACOSMETICS);
	}
	
	@Override
	public List<String> removeActiveCosmetics(ChatPluginServerPlayer player) {
		List<String> removedCosmetics = new ArrayList<>();
		UltraPlayer ultraPlayer = ((UltraCosmetics) plugin).getPlayerManager().getUltraPlayer(player.toAdapter().bukkitValue());
		
		if (ultraPlayer != null) {
			if (ultraPlayer.hasCosmetic(Category.EFFECTS)) {
				removedCosmetics.add("particles");
				ultraPlayer.unsetCosmetic(Category.EFFECTS);
			} if (ultraPlayer.hasCosmetic(Category.GADGETS)) {
				removedCosmetics.add("gadget");
				ultraPlayer.unsetCosmetic(Category.GADGETS);
			} if (ultraPlayer.hasCosmetic(Category.MORPHS)) {
				removedCosmetics.add("morph");
				ultraPlayer.unsetCosmetic(Category.MORPHS);
			} if (ultraPlayer.hasCosmetic(Category.MOUNTS)) {
				removedCosmetics.add("mount");
				ultraPlayer.unsetCosmetic(Category.MOUNTS);
			} if (ultraPlayer.hasCosmetic(Category.PETS)) {
				removedCosmetics.add("pet");
				ultraPlayer.unsetCosmetic(Category.PETS);
			}
		} return removedCosmetics;
	}
	
}
