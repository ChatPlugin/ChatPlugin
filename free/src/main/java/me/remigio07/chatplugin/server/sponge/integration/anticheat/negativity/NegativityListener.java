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

package me.remigio07.chatplugin.server.sponge.integration.anticheat.negativity;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.negativity.PlayerCheatAlertEvent;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager.TPSTimeInterval;

public class NegativityListener implements Listeners {
	
	@EventListener
	public void onCheat(PlayerCheatAlertEvent event) {
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
		
		if (event.isCancelled() || player == null)
			return;
		int violations = IntegrationType.NEGATIVITY.get().getViolations(player, event.getCheat().getName());
		
		if (violations < 1)
			return;
		if (ProxyManager.getInstance().isEnabled())
			ProxyManager.getInstance().sendPluginMessage(Packets.Sync.addPlayerViolation(
					ProxyManager.getInstance().getServerID(),
					player.getUUID(),
					player.getName(),
					IntegrationType.NEGATIVITY,
					event.getCheat().getName(),
					event.getCheckName(),
					violations,
					player.getPing(),
					TPSManager.getInstance().getTPS(TPSTimeInterval.ONE_MINUTE),
					player.getVersion().getProtocol(),
					player.getVersion().isPreNettyRewrite(),
					player.isBedrockPlayer()
					));
		else AnticheatManager.getInstance().addViolation(
				player,
				IntegrationType.NEGATIVITY,
				event.getCheat().getName(),
				event.getCheckName(),
				ProxyManager.getInstance().getServerID(),
				violations,
				player.getPing(),
				TPSManager.getInstance().getTPS(TPSTimeInterval.ONE_MINUTE),
				player.getVersion(),
				player.isBedrockPlayer()
				);
	}
	
}
