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

package me.remigio07.chatplugin.api.common.integration.permission;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;

/**
 * Represents a {@link ChatPluginIntegration} able
 * to check an {@link OfflinePlayer}'s permissions.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#LUCKPERMS}]</p>
 */
public interface PermissionIntegration extends ChatPluginIntegration {
	
	/**
	 * Checks if an offline player has the specified permission.
	 * 
	 * <p><strong>Note:</strong> this method might take some
	 * time to be executed: async calls are recommended.</p>
	 * 
	 * @param player Offline player to check
	 * @param permission Permission to check
	 * @return Whether the player has the permission
	 */
	public boolean hasPermission(OfflinePlayer player, String permission);
	
}
