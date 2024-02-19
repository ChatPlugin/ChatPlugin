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

package me.remigio07.chatplugin.server.bukkit.integration.version;

import org.bukkit.entity.Player;

import com.viaversion.viaversion.ViaVersionPlugin;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.version.VersionIntegration;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class ViaVersionIntegration extends ChatPluginBukkitIntegration<VersionIntegration> implements VersionIntegration {
	
	public ViaVersionIntegration() {
		super(IntegrationType.VIAVERSION);
	}
	
	@Override
	protected void loadAPI() {
		api = ((ViaVersionPlugin) plugin).getApi();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Version getVersion(PlayerAdapter player) {
		Version version = ServerPlayerManager.getPlayerVersion(player.getUUID());
		return version == null ? Version.getVersion(ProtocolVersion.getProtocol(((ViaAPI<Player>) api).getPlayerVersion(player.bukkitValue())).getName()) : version;
	}
	
}
