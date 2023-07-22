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

import java.util.ArrayList;
import java.util.List;

import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;
import com.yapzhenyie.GadgetsMenu.utils.WorldUtils;

import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;

public class GadgetsMenuFreeIntegration implements GadgetsMenuCommonIntegration {
	
	@Override
	public List<String> removeActiveCosmetics(ChatPluginServerPlayer player) {
		List<String> removedCosmetics = new ArrayList<>();
		
		if (WorldUtils.isWorldEnabled(player.toAdapter().bukkitValue().getWorld())) {
			com.yapzhenyie.GadgetsMenu.player.PlayerManager manager = GadgetsMenuAPI.getPlayerManager(player.toAdapter().bukkitValue());
			
			if (manager != null) {
				if (manager.getSelectedCloak() != null) {
					removedCosmetics.add("cloak");
					manager.unequipCloak();
				} if (manager.getSelectedEmote() != null) {
					removedCosmetics.add("emote");
					manager.unequipEmote();
				} if (manager.getSelectedMorph() != null) {
					removedCosmetics.add("morph");
					manager.unequipMorph();
				} if (manager.getSelectedParticle() != null) {
					removedCosmetics.add("particles");
					manager.unequipParticle();
				} if (manager.getCurrentPet() != null) {
					removedCosmetics.add("pet");
					manager.unequipPet();
				}
			}
		} return removedCosmetics;
	}
	
}
