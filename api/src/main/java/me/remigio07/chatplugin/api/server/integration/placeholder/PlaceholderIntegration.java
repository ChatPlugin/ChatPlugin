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

package me.remigio07.chatplugin.api.server.integration.placeholder;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a {@link ChatPluginIntegration} that translates placeholders for a {@link ChatPluginServerPlayer}.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#PLACEHOLDERAPI}, {@link IntegrationType#MVDWPLACEHOLDERAPI}]</p>
 */
public interface PlaceholderIntegration extends ChatPluginIntegration {
	
	/**
	 * Translates an input string with placeholders,
	 * formatted for the specified player.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @return Translated placeholders
	 */
	public String translatePlaceholders(String input, ChatPluginServerPlayer player);
	
}
