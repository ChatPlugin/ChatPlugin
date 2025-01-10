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

package me.remigio07.chatplugin.api.server.integration.combatlog;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a {@link ChatPluginIntegration} able to check if a {@link ChatPluginServerPlayer} is in combat.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#COMBATLOGX}]</p>
 */
public interface CombatLogIntegration extends ChatPluginIntegration {
	
	/**
	 * Checks if a player is currently in combat (tagged).
	 * 
	 * @param player Player to check
	 * @return Whether the player is in combat
	 */
	public boolean isInCombat(ChatPluginServerPlayer player);
	
}
