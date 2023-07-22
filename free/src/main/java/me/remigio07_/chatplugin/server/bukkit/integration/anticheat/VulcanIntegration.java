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

package me.remigio07_.chatplugin.server.bukkit.integration.anticheat;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import me.frep.vulcan.api.VulcanAPI;
import me.frep.vulcan.api.VulcanAPI.Factory;
import me.frep.vulcan.api.check.Check;
import me.frep.vulcan.api.event.VulcanFlagEvent;
import me.remigio07_.chatplugin.api.common.event.EventManager;
import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.common.util.packet.PacketSerializer;
import me.remigio07_.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07_.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07_.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07_.chatplugin.api.server.util.manager.TPSManager.TPSTimeInterval;
import me.remigio07_.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07_.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;
import me.remigio07_.chatplugin.server.bukkit.manager.BukkitEventManager;
import me.remigio07_.chatplugin.server.integration.anticheat.ViolationImpl;

public class VulcanIntegration extends ChatPluginBukkitIntegration<AnticheatIntegration> implements AnticheatIntegration {
	
	public static final List<String> CHEATS_IDS = Arrays.asList("aim", "autoblock", "autoclicker", "fastbow", "criticals", "hitbox", "killaura", "reach", "velocity", "boatfly", "antilevitation", "nosaddle", "entityspeed", "entityflight", "elytra", "fastclimb", "flight", "jesus", "jump", "motion", "noslow", "speed", "step", "sprint", "strafe", "wallclimb", "ghosthand", "crash", "baritone", "badpackets", "fastplace", "fastbreak", "fastuse", "groundspoof", "interact", "improbable", "invalid", "boatglitch", "inventory", "scaffold", "timer", "tower", "hackedclient");
	
	public VulcanIntegration() {
		super(IntegrationType.VULCAN);
	}
	
	@Override
	protected void loadAPI() {
		api = Factory.getApi();
		
		Bukkit.getPluginManager().registerEvent(VulcanFlagEvent.class, ((BukkitEventManager) EventManager.getInstance()).getListener(), EventPriority.NORMAL, new EventExecutor() {
			
			@Override
			public void execute(Listener listener, Event event) throws EventException {
				VulcanFlagEvent flagEvent = (VulcanFlagEvent) event;
				ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(flagEvent.getPlayer().getUniqueId());
				
				if (player == null)
					return;
				int violations = getViolations(player, flagEvent.getCheck().getName());
				
				if (violations < 1)
					return;
				if (ProxyManager.getInstance().isEnabled())
					ProxyManager.getInstance().sendPluginMessage(
							new PacketSerializer("PlayerViolation")
							.writeServerID()															// server
							.writeUTF(player.getUUID().toString())										// player
							.writeUTF("ADD")															// violationPacketType
							.writeUTF(IntegrationType.VULCAN.name())									// anticheat
							.writeUTF(flagEvent.getCheck().getName())									// cheatID
							.writeUTF((flagEvent.getCheck().getType() + "").toUpperCase()) 				// component
							.writeInt(violations)														// amount
							.writeInt(player.getPing())													// ping
							.writeInt(player.getVersion().getProtocol())								// versionProtocol
							.writeBoolean(false)														// versionPreNettyRewrite
							.writeDouble(TPSManager.getInstance().getTPS(TPSTimeInterval.ONE_MINUTE))	// tps
							);
				else AnticheatManager.getInstance().addViolation(new ViolationImpl(
						player,
						ProxyManager.getInstance().getServerID(),
						IntegrationType.VULCAN,
						flagEvent.getCheck().getName(),
						(flagEvent.getCheck().getType() + "").toUpperCase(),
						violations,
						player.getPing(),
						player.getVersion().getProtocol(),
						false,
						TPSManager.getInstance().getTPS(TPSTimeInterval.ONE_MINUTE)
						));
			}
		}, BukkitBootstrapper.getInstance());
	}
	
	@Override
	public int getViolations(ChatPluginServerPlayer player, String cheatID) {
		for (Check check : ((VulcanAPI) api).getChecks(player.toAdapter().bukkitValue()))
			if (check.getName().equals(cheatID))
				return check.getVl();
		return 0;
	}
	
}
