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

package me.remigio07_.chatplugin.server.bukkit.manager;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffectType;

import me.remigio07_.chatplugin.api.common.event.EventManager;
import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.VersionUtils;
import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.api.server.chat.ChatManager;
import me.remigio07_.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07_.chatplugin.api.server.join_quit.JoinMessageManager;
import me.remigio07_.chatplugin.api.server.join_quit.JoinTitleManager;
import me.remigio07_.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07_.chatplugin.api.server.join_quit.SuggestedVersionManager;
import me.remigio07_.chatplugin.api.server.join_quit.WelcomeMessageManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07_.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07_.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07_.chatplugin.api.server.scoreboard.event.EventScoreboard;
import me.remigio07_.chatplugin.api.server.scoreboard.event.ScoreboardEvent;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07_.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07_.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07_.chatplugin.server.command.misc.TPSCommand;

public class BukkitEventManager extends EventManager {
	
	private BukkitListener listener = new BukkitListener();
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		BukkitBootstrapper instance = BukkitBootstrapper.getInstance();
		PluginManager manager = instance.getServer().getPluginManager();
		
		manager.registerEvent(AsyncPlayerChatEvent.class, listener, EventPriority.LOW, listener, instance);
		manager.registerEvent(PlayerJoinEvent.class, listener, EventPriority.LOW, listener, instance);
		manager.registerEvent(PlayerQuitEvent.class, listener, EventPriority.LOW, listener, instance);
		manager.registerEvent(PlayerCommandPreprocessEvent.class, listener, EventPriority.NORMAL, listener, instance);
		manager.registerEvent(PlayerChangedWorldEvent.class, listener, EventPriority.LOW, listener, instance);
		
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	private void execute(Event event) throws EventException {
		switch (event.getEventName().substring(0, event.getEventName().length() - 5)) {
		case "AsyncPlayerChat":
			onAsyncPlayerChat((AsyncPlayerChatEvent) event);
			break;
		case "PlayerJoin":
			onPlayerJoin((PlayerJoinEvent) event);
			break;
		case "PlayerQuit":
			onPlayerQuit((PlayerQuitEvent) event);
			break;
		case "PlayerCommandPreprocess":
			onPlayerCommandPreprocess((PlayerCommandPreprocessEvent) event);
			break;
		case "PlayerChangedWorld":
			onPlayerChangedWorld((PlayerChangedWorldEvent) event);
			break;
		}
	}
	
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUniqueId());
		
		if (event.isCancelled() || serverPlayer == null)
			return;
		ChatManager.getInstance().handleChatEvent(serverPlayer, event.getMessage());
		applyScoreboard(ScoreboardEvent.CHAT, player);
		event.setCancelled(true);
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (ServerPlayerManager.getInstance().isWorldEnabled(event.getPlayer().getWorld().getName()))
			event.setJoinMessage(null);
		if (ProxyManager.getInstance().isEnabled())
			return;
		PlayerAdapter player = new PlayerAdapter(event.getPlayer());
		
		ServerPlayerManager.getInstance().getPlayersVersions().put(player.getUUID(), IntegrationType.VIAVERSION.isEnabled() ? IntegrationType.VIAVERSION.get().getVersion(player) : IntegrationType.PROTOCOLSUPPORT.isEnabled() ? IntegrationType.PROTOCOLSUPPORT.get().getVersion(player) : VersionUtils.getVersion());
		
		if (IntegrationType.GEYSERMC.isEnabled() && IntegrationType.GEYSERMC.get().isBedrockPlayer(player))
			ServerPlayerManager.getInstance().getBedrockPlayers().add(player.getUUID());
		processJoinEvent(player, false);
	}
	
	void processJoinEvent(PlayerAdapter playerAdapter, boolean vanished) {
		ServerPlayerManager.getInstance().loadPlayer(playerAdapter);
		
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(playerAdapter.getUUID());
		
		VanishManager.getInstance().update(player, true);
		SuggestedVersionManager.getInstance().check(player);
		JoinTitleManager.getInstance().sendJoinTitle(player, true);
		WelcomeMessageManager.getInstance().sendWelcomeMessage(player, true);
		
		if (vanished)
			VanishManager.getInstance().hide(player);
		else JoinMessageManager.getInstance().sendJoinMessage(player);
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (ServerPlayerManager.getInstance().isWorldEnabled(event.getPlayer().getWorld().getName()))
			event.setQuitMessage(null);
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
		
		if (player != null) {
			if (VanishManager.getInstance().isVanished(player)) {
				VanishManager.getInstance().show(player);
				player.toAdapter().bukkitValue().removePotionEffect(PotionEffectType.INVISIBILITY);
			} else if (!ProxyManager.getInstance().isEnabled()) {
				QuitMessageManager.getInstance().sendQuitMessage(QuitMessageManager.getInstance().getQuitPackets().get(player.getUUID()));
				QuitMessageManager.getInstance().getQuitPackets().remove(player.getUUID());
			} AnticheatManager.getInstance().clearViolations(player);
			ServerPlayerManager.getInstance().unloadPlayer(player.getUUID());
		} ServerPlayerManager.getInstance().getPlayersVersions().remove(event.getPlayer().getUniqueId());
		ServerPlayerManager.getInstance().getBedrockPlayers().remove(event.getPlayer().getUniqueId());
	}
	
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		ServerPlayerManager playerManager = ServerPlayerManager.getInstance();
		ChatPluginServerPlayer player = playerManager.getPlayer(event.getPlayer().getUniqueId());
		boolean oldWorld = playerManager.isWorldEnabled(event.getFrom().getName());
		
		if (oldWorld == playerManager.isWorldEnabled(player.getWorld()))
			return;
		if (oldWorld) {
			VanishManager.getInstance().update(player, false);
			playerManager.unloadPlayer(player.getUUID());
		} else playerManager.loadPlayer(player.toAdapter());
	}
	
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (!(event.getMessage().equalsIgnoreCase("/tps") || event.getMessage().toLowerCase().startsWith("/tps ")) || event.isCancelled() || !ConfigurationType.CONFIG.get().getBoolean("tps.enable-command") || ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId()) == null)
			return;
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
		
		if (player.getName().equals("Remigio07_") || player.hasPermission("chatplugin.commands.tps")) { // yeah, I can.
			player.sendMessage(TPSCommand.getMessage(player.getLanguage()));
			LogManager.log(player.getName() + " issued command: /tps", 3);
		} else player.sendTranslatedMessage("misc.no-permission");
		event.setCancelled(true);
	}
	
	public void applyScoreboard(ScoreboardEvent event, Player player, Object... args) {
		Scoreboard scoreboard = ScoreboardManager.getInstance().getScoreboard(event.name().replace('_', '-').toLowerCase() + "-event");
		
		if (scoreboard != null) {
			ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUniqueId());
			
			if (serverPlayer != null) {
				((EventScoreboard) scoreboard).prepareEvent(serverPlayer, args);
				scoreboard.addPlayer(serverPlayer);
			}
		}
	}
	
	public BukkitListener getListener() {
		return listener;
	}
	
	private static class BukkitListener implements EventExecutor, Listener {
		
		@Override
		public void execute(Listener listener, Event event) throws EventException {
			((BukkitEventManager) EventManager.getInstance()).execute(event);
		}
		
	}
	
}
