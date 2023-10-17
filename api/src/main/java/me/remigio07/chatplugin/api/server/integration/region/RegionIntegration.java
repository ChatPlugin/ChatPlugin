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

package me.remigio07.chatplugin.api.server.integration.region;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a {@link ChatPluginIntegration} able to determine if a {@link ChatPluginServerPlayer} is inside of a protected region.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#WORLDGUARD}]</p>
 */
public interface RegionIntegration extends ChatPluginIntegration {
	
	/**
	 * Checks if a player is inside of a protected region.
	 * 
	 * @param player Player to check
	 * @return Whether the player is inside of a region
	 */
	public default boolean isInRegion(ChatPluginServerPlayer player) {
		return getRegionID(player) != null;
	}
	
	/**
	 * Gets the ID of the protected region a player is currently inside of.
	 * 
	 * <p>Will return <code>null</code> if they are not inside of any region.</p>
	 * 
	 * @param player Player to check
	 * @return Protected region's ID
	 */
	@Nullable(why = "Player may not be inside of any region")
	public String getRegionID(ChatPluginServerPlayer player);
	
}
