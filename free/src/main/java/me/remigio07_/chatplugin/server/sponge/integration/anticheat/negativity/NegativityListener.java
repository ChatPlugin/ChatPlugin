/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.server.sponge.integration.anticheat.negativity;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.negativity.PlayerCheatEvent;

import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.common.util.packet.PacketSerializer;
import me.remigio07_.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07_.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07_.chatplugin.api.server.util.manager.TPSManager.TPSTimeInterval;
import me.remigio07_.chatplugin.server.integration.anticheat.ViolationImpl;

public class NegativityListener implements Listeners {
	
	@EventListener
	public void onCheat(PlayerCheatEvent event) {
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
		
		if (player == null)
			return;
		int violations = IntegrationType.NEGATIVITY.get().getViolations(player, event.getCheat().getName());
		
		if (violations < 1)
			return;
		if (ProxyManager.getInstance().isEnabled())
			ProxyManager.getInstance().sendPluginMessage(
					new PacketSerializer("PlayerViolation")
					.writeServerID()															// server
					.writeUTF(player.getUUID().toString())										// player
					.writeUTF("ADD")															// violationPacketType
					.writeUTF(IntegrationType.NEGATIVITY.name())								// anticheat
					.writeUTF(event.getCheat().getName())										// cheatID
					.writeUTF(event.getCheat().getCheatCategory().getName())					// component
					.writeInt(violations)														// amount
					.writeInt(player.getPing())													// ping
					.writeInt(player.getVersion().getProtocol())								// versionProtocol
					.writeBoolean(false)														// versionPreNettyRewrite
					.writeDouble(TPSManager.getInstance().getTPS(TPSTimeInterval.ONE_MINUTE))	// tps
					);
		else AnticheatManager.getInstance().addViolation(new ViolationImpl(
				player,
				ProxyManager.getInstance().getServerID(),
				IntegrationType.NEGATIVITY,
				event.getCheat().getName(),
				event.getCheat().getCheatCategory().getName(),
				violations,
				player.getPing(),
				player.getVersion().getProtocol(),
				false,
				TPSManager.getInstance().getTPS(TPSTimeInterval.ONE_MINUTE)
				));
	}
	
}
