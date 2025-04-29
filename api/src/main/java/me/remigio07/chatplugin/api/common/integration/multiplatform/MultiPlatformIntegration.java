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

package me.remigio07.chatplugin.api.common.integration.multiplatform;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;

/**
 * Represents a {@link ChatPluginIntegration} able to check if a
 * {@link PlayerAdapter} is connected through a Bedrock client.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#GEYSER}, {@link IntegrationType#FLOODGATE}]</p>
 */
public interface MultiPlatformIntegration extends ChatPluginIntegration {
	
	/**
	 * Checks if the specified player is
	 * connected through Bedrock Edition.
	 * 
	 * @param player Player to check
	 * @return Whether the player is on Bedrock Edition
	 */
	public boolean isBedrockPlayer(PlayerAdapter player);
	
	/**
	 * Gets the prefix added in front of Bedrock
	 * players' usernames to prevent duplicates.
	 * 
	 * @return Bedrock usernames' prefix
	 */
	@NotNull
	public String getUsernamePrefix();
	
}
