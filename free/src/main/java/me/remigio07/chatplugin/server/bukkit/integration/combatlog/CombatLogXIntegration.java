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

package me.remigio07.chatplugin.server.bukkit.integration.combatlog;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

import com.github.sirblobman.combatlogx.api.event.PlayerPreTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.integration.combatlog.CombatLogIntegration;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.scoreboard.event.EventScoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.event.ScoreboardEvent;
import me.remigio07.chatplugin.api.server.util.adapter.entity.LivingEntityAdapter;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;
import me.remigio07.chatplugin.server.bukkit.manager.BukkitEventManager;

public class CombatLogXIntegration extends ChatPluginBukkitIntegration<CombatLogIntegration> implements CombatLogIntegration {
	
	public CombatLogXIntegration() {
		super(IntegrationType.COMBATLOGX);
	}
	
	@Override
	protected void loadAPI() {
		Bukkit.getPluginManager().registerEvent(PlayerPreTagEvent.class, (Listener) EventManager.getInstance(), EventPriority.NORMAL, new EventExecutor() {
			
			@Override
			public void execute(Listener listener, Event event) throws EventException {
				PlayerPreTagEvent playerPreTagEvent = (PlayerPreTagEvent) event;
				
				if (playerPreTagEvent.isCancelled())
					return;
				ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(playerPreTagEvent.getPlayer().getUniqueId());
				
				if (player != null && player.isVanished())
					playerPreTagEvent.setCancelled(true);
				else if (playerPreTagEvent.getEnemy() instanceof Player)
					((BukkitEventManager) EventManager.getInstance()).applyScoreboard(ScoreboardEvent.COMBAT_TAG, playerPreTagEvent.getPlayer(), new LivingEntityAdapter(playerPreTagEvent.getEnemy()));
			}
		}, BukkitBootstrapper.getInstance());
		Bukkit.getPluginManager().registerEvent(PlayerUntagEvent.class, (Listener) EventManager.getInstance(), EventPriority.NORMAL, new EventExecutor() {
			
			@Override
			public void execute(Listener listener, Event event) throws EventException {
				PlayerUntagEvent playerUntagEvent = (PlayerUntagEvent) event;
				ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(playerUntagEvent.getPlayer().getUniqueId());
				
				if (player != null) {
					Scoreboard scoreboard = ScoreboardManager.getInstance().getScoreboard("combat-tag-event");
					
					if (scoreboard != null && scoreboard.getPlayers().contains(player)) {
						scoreboard.removePlayer(player);
						
						if ((scoreboard = ((EventScoreboard) scoreboard).getLastScoreboards().get(player)) != null)
							scoreboard.addPlayer(player);
					}
				}
			}
			
		}, BukkitBootstrapper.getInstance());
	}
	
	@Override
	public boolean isInCombat(ChatPluginServerPlayer player) {
		try {
			return (boolean) Class.forName("com.github.sirblobman.combatlogx.api.manager.ICombatManager")
					.getMethod("isInCombat", Player.class)
					.invoke(Class.forName("com.github.sirblobman.combatlogx.CombatPlugin")
							.getMethod("getCombatManager")
							.invoke(plugin), player.toAdapter().bukkitValue());
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
			e.printStackTrace();
		} return false;
	}
	
}
