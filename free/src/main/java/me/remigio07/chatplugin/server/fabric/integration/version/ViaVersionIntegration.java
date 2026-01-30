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

package me.remigio07.chatplugin.server.fabric.integration.version;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.version.VersionIntegration;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.server.fabric.integration.ChatPluginFabricIntegration;
import net.minecraft.server.network.ServerPlayerEntity;

public class ViaVersionIntegration extends ChatPluginFabricIntegration<VersionIntegration> implements VersionIntegration {
	
	public ViaVersionIntegration() {
		super(IntegrationType.VIAVERSION);
	}
	
	@Override
	protected void loadAPI() {
		api = Via.getAPI();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Version getVersion(PlayerAdapter player) {
		Version version = ServerPlayerManager.getPlayerVersion(player.getUUID());
		return version == null ? Version.getVersion(((ViaAPI<ServerPlayerEntity>) api).getPlayerVersion(player.fabricValue()), false) : version;
	}
	
}
