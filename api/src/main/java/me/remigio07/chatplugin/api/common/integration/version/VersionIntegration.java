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

package me.remigio07.chatplugin.api.common.integration.version;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;

/**
 * Represents a {@link ChatPluginIntegration} that provides a {@link PlayerAdapter}'s version.
 * 
 * <p><strong>Types:</strong> [{@link IntegrationType#PROTOCOLSUPPORT}, {@link IntegrationType#VIAVERSION}]<p>
 */
public interface VersionIntegration extends ChatPluginIntegration {
	
	/**
	 * Gets a player's version.
	 * 
	 * @param player Player to get the version of
	 * @return Player's version
	 */
	public Version getVersion(PlayerAdapter player);
	
}
