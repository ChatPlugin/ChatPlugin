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

package me.remigio07.chatplugin.server.bukkit.integration.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager.TPSTimeInterval;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;
import me.remigio07.chatplugin.server.bukkit.manager.BukkitEventManager;
import me.rerere.matrix.api.HackType;
import me.rerere.matrix.api.MatrixAPI;
import me.rerere.matrix.api.MatrixAPIProvider;
import me.rerere.matrix.api.events.PlayerViolationEvent;

public class MatrixIntegration extends ChatPluginBukkitIntegration<AnticheatIntegration> implements AnticheatIntegration {
	
	public MatrixIntegration() {
		super(IntegrationType.MATRIX);
	}
	
	@Override
	protected void loadAPI() {
		api = MatrixAPIProvider.getAPI();
		
		Bukkit.getPluginManager().registerEvent(PlayerViolationEvent.class, ((BukkitEventManager) EventManager.getInstance()).getListener(), EventPriority.NORMAL, new EventExecutor() {
			
			@Override
			public void execute(Listener listener, Event event) throws EventException {
				PlayerViolationEvent violationEvent = (PlayerViolationEvent) event;
				ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(violationEvent.getPlayer().getUniqueId());
				
				if (violationEvent.isCancelled() || player == null)
					return;
				int violations = getViolations(player, violationEvent.getHackType().name());
				
				if (violations < 1)
					return;
				if (ProxyManager.getInstance().isEnabled())
					ProxyManager.getInstance().sendPluginMessage(Packets.Sync.addPlayerViolation(
							ProxyManager.getInstance().getServerID(),
							player.getUUID(),
							player.getName(),
							IntegrationType.MATRIX,
							violationEvent.getHackType().name(),
							violationEvent.getComponent(),
							violations,
							player.getPing(),
							TPSManager.getInstance().getTPS(TPSTimeInterval.ONE_MINUTE),
							player.getVersion().getProtocol(),
							player.getVersion().isPreNettyRewrite(),
							player.isBedrockPlayer()
							));
				else AnticheatManager.getInstance().addViolation(
						player,
						IntegrationType.MATRIX,
						violationEvent.getHackType().name(),
						violationEvent.getComponent(),
						ProxyManager.getInstance().getServerID(),
						violations,
						player.getPing(),
						TPSManager.getInstance().getTPS(TPSTimeInterval.ONE_MINUTE),
						player.getVersion(),
						player.isBedrockPlayer()
						);
			}
		}, BukkitBootstrapper.getInstance());
	}
	
	@Override
	public int getViolations(ChatPluginServerPlayer player, String cheatID) {
		try {
			return ((MatrixAPI) api).getViolations(player.toAdapter().bukkitValue(), HackType.valueOf(cheatID));
		} catch (IllegalArgumentException e) {
			return 0;
		}
	}
	
}
