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

package me.remigio07.chatplugin.api.server.integration.cosmetics;

import java.util.List;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a {@link ChatPluginIntegration} able to check an {@link OfflinePlayer}'s permissions.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#GADGETSMENU}, {@link IntegrationType#PLAYERPARTICLES}, {@link IntegrationType#ULTRACOSMETICS}]</p>
 */
public interface CosmeticsIntegration extends ChatPluginIntegration {
	
	/**
	 * Removes a player's active cosmetics.
	 * 
	 * @param player Player whose cosmetics need to be removed
	 * @return Removed cosmetics' names
	 */
	public List<String> removeActiveCosmetics(ChatPluginServerPlayer player);
	
}
