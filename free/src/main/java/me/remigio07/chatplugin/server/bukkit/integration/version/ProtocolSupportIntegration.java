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

package me.remigio07.chatplugin.server.bukkit.integration.version;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.version.VersionIntegration;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class ProtocolSupportIntegration extends ChatPluginBukkitIntegration<VersionIntegration> implements VersionIntegration {
	
	private Method getProtocolVersion = null, getId = null;
	
	public ProtocolSupportIntegration() {
		super(IntegrationType.PROTOCOLSUPPORT);
		
		try {
			getProtocolVersion = Class.forName("protocolsupport.api.ProtocolSupportAPI").getMethod("getProtocolVersion", Player.class);
			getId = Class.forName("protocolsupport.api.ProtocolVersion").getMethod("getId");
		} catch (NoSuchMethodException | ClassNotFoundException e) {
			
		}
	}
	
	@Override
	public Version getVersion(PlayerAdapter player) {
		Version version = ServerPlayerManager.getPlayerVersion(player.getUUID());
		
		if (version != null)
			return version;
		try {
			Enum<?> ver = (Enum<?>) getProtocolVersion.invoke(null, player.bukkitValue());
			return Version.getVersion((int) getId.invoke(ver), ver.ordinal() > 33);
		} catch (Exception e) {
			e.printStackTrace();
			return Version.UNSUPPORTED;
		}
	}
	
}
