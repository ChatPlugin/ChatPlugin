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

package me.remigio07.chatplugin.server.bukkit.integration.multiplatform;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.multiplatform.MultiPlatformIntegration;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class FloodgateIntegration extends ChatPluginBukkitIntegration<MultiPlatformIntegration> implements MultiPlatformIntegration {
	
	public FloodgateIntegration() {
		super(IntegrationType.FLOODGATE);
	}
	
	@Override
	public boolean isBedrockPlayer(PlayerAdapter player) {
		return Utils.isFloodgatePlayer(player.getUUID());
	}
	
	@Override
	public @NotNull String getUsernamePrefix() {
		return Utils.getFloodgateUsernamePrefix();
	}
	
}
