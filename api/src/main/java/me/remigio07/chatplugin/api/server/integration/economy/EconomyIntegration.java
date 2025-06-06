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

package me.remigio07.chatplugin.api.server.integration.economy;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.Utils;

/**
 * Represents a {@link ChatPluginIntegration} able to check an {@link OfflinePlayer}'s balance.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#ESSENTIALSX}, {@link IntegrationType#VAULT}]</p>
 */
public interface EconomyIntegration extends ChatPluginIntegration {
	
	/**
	 * Gets an offline player's balance.
	 * 
	 * <p>Will return {@link Double#MIN_VALUE} if this integration
	 * is not able to provide the player's balance.</p>
	 * 
	 * @param player Offline player to check
	 * @return Player's balance
	 */
	public double getBalance(OfflinePlayer player);
	
	/**
	 * Formats the specified amount of money
	 * based on this integration's settings.
	 * 
	 * <p>Will return {@link Utils#NOT_APPLICABLE} if this
	 * integration is not able to provide the balance's format.</p>
	 * 
	 * @param balance Balance to format
	 * @return Formatted balance
	 */
	public String formatBalance(double balance);
	
}
