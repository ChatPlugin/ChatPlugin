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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.integration.anticheat;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a {@link ChatPluginIntegration} that provides a information about a {@link ChatPluginServerPlayer}'s violations.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#MATRIX}, {@link IntegrationType#NEGATIVITY}, {@link IntegrationType#VULCAN}]</p>
 */
public interface AnticheatIntegration extends ChatPluginIntegration {
	
	/**
	 * Gets a player's violations' amount for the specified cheat's ID.
	 * Will return 0 if this integration does not recognize that ID.
	 * 
	 * @param player Player to check
	 * @param cheatID Integration's cheat's ID
	 * @return Player's violations' amount
	 */
	public int getViolations(ChatPluginServerPlayer player, String cheatID);
	
}
