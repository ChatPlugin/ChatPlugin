/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.server.bukkit.integration.multiplatform;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.multiplatform.MultiPlatformIntegration;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class GeyserMCIntegration extends ChatPluginBukkitIntegration<MultiPlatformIntegration> implements MultiPlatformIntegration {
	
	public GeyserMCIntegration() {
		super(IntegrationType.GEYSERMC);
	}
	
	@Override
	public boolean isBedrockPlayer(PlayerAdapter player) {
		return Utils.isBedrockPlayer(player.getUUID());
	}
	
}
