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

package me.remigio07.chatplugin.server.bukkit.integration.cosmetic.gadgetsmenu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.yapzhenyie.GadgetsMenu.api.GadgetsMenuAPI;
import com.yapzhenyie.GadgetsMenu.utils.WorldUtils;

import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

public class GadgetsMenuPremiumIntegration implements GadgetsMenuCommonIntegration {
	
	@Override
	public List<String> removeActiveCosmetics(ChatPluginServerPlayer player) {
		List<String> removedCosmetics = new ArrayList<>();
		
		if (WorldUtils.isWorldEnabled(player.toAdapter().bukkitValue().getWorld())) {
			com.yapzhenyie.GadgetsMenu.player.PlayerManager manager = GadgetsMenuAPI.getPlayerManager(player.toAdapter().bukkitValue());
			
			try {
				Class<?> Category = Class.forName("com.yapzhenyie.GadgetsMenu.cosmetics.Category");
				Object[] values = Category.getEnumConstants();
				Method hasActiveCosmetic = manager.getClass().getMethod("hasActiveCosmetic", Category);
				
				if (manager != null) {
					if ((boolean) hasActiveCosmetic.invoke(manager, values[10])) {
						removedCosmetics.add("cloak");
						manager.unequipCloak();
					} if ((boolean) hasActiveCosmetic.invoke(manager, values[9])) {
						removedCosmetics.add("emote");
						manager.unequipEmote();
					} if ((boolean) hasActiveCosmetic.invoke(manager, values[6])) {
						removedCosmetics.add("miniature");
						manager.getClass().getMethod("unequipMiniature").invoke(manager);
					} if ((boolean) hasActiveCosmetic.invoke(manager, values[7])) {
						removedCosmetics.add("morph");
						manager.unequipMorph();
					} if ((boolean) hasActiveCosmetic.invoke(manager, values[2])) {
						removedCosmetics.add("particles");
						manager.unequipParticle();
					} if ((boolean) hasActiveCosmetic.invoke(manager, values[5])) {
						removedCosmetics.add("pet");
						manager.unequipPet();
					}
				}
			} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		} return removedCosmetics;
	}
	
}
