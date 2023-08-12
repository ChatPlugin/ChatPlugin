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

package me.remigio07.chatplugin.api.common.integration.multiplatform;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;

/**
 * Represents a {@link ChatPluginIntegration} able to check if a {@link PlayerAdapter} is connected through a Bedrock client.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#GEYSERMC}]</p>
 */
public interface MultiPlatformIntegration extends ChatPluginIntegration {
	
	/**
	 * Checks if the specified player is connected
	 * through {@link IntegrationType#GEYSERMC}.
	 * 
	 * @param player Player to check
	 * @return Whether the player is using the BE
	 */
	public boolean isBedrockPlayer(PlayerAdapter player);
	
}
