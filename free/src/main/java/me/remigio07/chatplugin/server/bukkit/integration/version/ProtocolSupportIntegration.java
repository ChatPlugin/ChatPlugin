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

package me.remigio07.chatplugin.server.bukkit.integration.version;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.version.VersionIntegration;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class ProtocolSupportIntegration extends ChatPluginBukkitIntegration<VersionIntegration> implements VersionIntegration {
	
	private Method getProtocolVersion = null, getName = null;
	
	public ProtocolSupportIntegration() {
		super(IntegrationType.PROTOCOLSUPPORT);
		
		try {
			getProtocolVersion = Class.forName("protocolsupport.api.ProtocolSupportAPI").getMethod("getProtocolVersion", Player.class);
			getName = Class.forName("protocolsupport.api.ProtocolVersion").getMethod("getName");
		} catch (NoSuchMethodException | ClassNotFoundException e) {
			
		}
	}
	
	@Override
	public Version getVersion(PlayerAdapter player) {
		try {
			return Version.getVersion((String) getName.invoke(getProtocolVersion.invoke(null, player.bukkitValue())));
		} catch (Exception e) {
			e.printStackTrace();
			return Version.UNSUPPORTED;
		}
	}
	
}
