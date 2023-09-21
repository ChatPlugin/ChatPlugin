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

package me.remigio07.chatplugin.server.sponge.manager;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent.Teleport;
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageChannelEvent.Chat;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07.chatplugin.api.server.join_quit.JoinMessageManager;
import me.remigio07.chatplugin.api.server.join_quit.JoinTitleManager;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07.chatplugin.api.server.join_quit.SuggestedVersionManager;
import me.remigio07.chatplugin.api.server.join_quit.WelcomeMessageManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageDetector;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.scoreboard.event.EventScoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.event.ScoreboardEvent;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;

public class SpongeEventManager extends EventManager {
	
	private SpongeListener listener = new SpongeListener();
	
	@SuppressWarnings("unchecked")
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		SpongeBootstrapper instance = SpongeBootstrapper.getInstance();
		org.spongepowered.api.event.EventManager manager = Sponge.getEventManager();
		
		manager.registerListener(instance, MessageChannelEvent.Chat.class, Order.EARLY, listener);
		manager.registerListener(instance, ClientConnectionEvent.Join.class, Order.EARLY, listener);
		manager.registerListener(instance, ClientConnectionEvent.Disconnect.class, Order.EARLY, listener);
		manager.registerListener(instance, PlayerChangeClientSettingsEvent.class, Order.POST, listener);
		
		try { // Sponge v4.2
			manager.registerListener(instance, (Class<? extends Event>) Class.forName("org.spongepowered.api.event.entity.DisplaceEntityEvent$Teleport"), Order.EARLY, listener);
		} catch (ClassNotFoundException e) {
			manager.registerListener(instance, MoveEntityEvent.Teleport.class, Order.EARLY, listener);
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	private void execute(EventListener<Event> listener, Event event) {
		switch (String.join("", event.getClass().getSimpleName().substring(0, event.getClass().getSimpleName().length() - 5).split("Event"))) {
		case "MessageChannel$Chat":
			onMessageChannel$Chat((Chat) event);
			break;
		case "ClientConnection$Join":
			onClientConnection$Join((Join) event);
			break;
		case "ClientConnection$Disconnect":
			onClientConnection$Disconnect((Disconnect) event);
			break;
		case "MoveEntity$Teleport":
			onMoveEntity$Teleport((Teleport) event);
			break;
		case "DisplaceEntity$Teleport":
			onDisplaceEntityEvent$Teleport(event);
			break;
		case "PlayerChangeClientSettings":
			onPlayerChangeClientSettings((PlayerChangeClientSettingsEvent) event);
			break;
		}
	}
	
	public void onMessageChannel$Chat(MessageChannelEvent.Chat event) {
		Player player = event.getCause().first(Player.class).get();
		ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUniqueId());
		
		if (event.isCancelled() || serverPlayer == null)
			return;
		ChatManager.getInstance().handleChatEvent(serverPlayer, event.getRawMessage().toPlain());
		applyScoreboard(ScoreboardEvent.CHAT, player);
		event.setCancelled(true);
	}
	
	public void onClientConnection$Join(ClientConnectionEvent.Join event) {
		if (ServerPlayerManager.getInstance().isWorldEnabled(event.getTargetEntity().getWorld().getName()))
			event.setMessageCancelled(true);
		if (ProxyManager.getInstance().isEnabled())
			return;
		PlayerAdapter player = new PlayerAdapter(event.getTargetEntity());
		
		ServerPlayerManager.getInstance().getPlayersVersions().put(player.getUUID(), IntegrationType.VIAVERSION.isEnabled() ? IntegrationType.VIAVERSION.get().getVersion(player) : IntegrationType.PROTOCOLSUPPORT.isEnabled() ? IntegrationType.PROTOCOLSUPPORT.get().getVersion(player) : VersionUtils.getVersion());
		
		if (IntegrationType.GEYSERMC.isEnabled() && IntegrationType.GEYSERMC.get().isBedrockPlayer(player))
			ServerPlayerManager.getInstance().getBedrockPlayers().add(player.getUUID());
		processJoinEvent(player, false);
	}
	
	public void processJoinEvent(PlayerAdapter playerAdapter, boolean vanished) {
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
	
	public void onClientConnection$Disconnect(ClientConnectionEvent.Disconnect event) {
		if (ServerPlayerManager.getInstance().isWorldEnabled(event.getTargetEntity().getWorld().getName()))
			event.setMessageCancelled(true);
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getTargetEntity().getUniqueId());
		
		if (player != null) {
			if (VanishManager.getInstance().isVanished(player)) {
				VanishManager.getInstance().show(player);
				player.toAdapter().spongeValue().getOrCreate(PotionEffectData.class).get().asList().stream().filter(effect -> effect.getType().equals(PotionEffectTypes.INVISIBILITY)).forEach(effect -> player.toAdapter().spongeValue().get(PotionEffectData.class).get().remove(effect));
			} else if (!ProxyManager.getInstance().isEnabled()) {
				QuitMessageManager.getInstance().sendQuitMessage(QuitMessageManager.getInstance().getQuitPackets().get(player.getUUID()));
				QuitMessageManager.getInstance().getQuitPackets().remove(player.getUUID());
			} AnticheatManager.getInstance().clearViolations(player);
			ServerPlayerManager.getInstance().unloadPlayer(player.getUUID());
		} ServerPlayerManager.getInstance().getPlayersVersions().remove(event.getTargetEntity().getUniqueId());
		ServerPlayerManager.getInstance().getBedrockPlayers().remove(event.getTargetEntity().getUniqueId());
	}
	
	// Sponge v4.2
	public void onDisplaceEntityEvent$Teleport(Object event) {
		onMoveEntity$Teleport((MoveEntityEvent.Teleport) event);
	}
	
	public void onMoveEntity$Teleport(MoveEntityEvent.Teleport event) {
		if (!(event.getTargetEntity() instanceof Player))
			return;
		ServerPlayerManager playerManager = ServerPlayerManager.getInstance();
		ChatPluginServerPlayer player = playerManager.getPlayer(event.getTargetEntity().getUniqueId());
		boolean oldWorld = playerManager.isWorldEnabled(event.getFromTransform().getExtent().getName());
		
		if (oldWorld == playerManager.isWorldEnabled(player.getWorld()))
			return;
		if (oldWorld) {
			VanishManager.getInstance().update(player, false);
			playerManager.unloadPlayer(player.getUUID());
		} else playerManager.loadPlayer(player.toAdapter());
	}
	
	public void onPlayerChangeClientSettings(PlayerChangeClientSettingsEvent event) {
		ChatPluginServerPlayer player = (ChatPluginServerPlayer) PlayerManager.getInstance().getPlayer(event.getTargetEntity().getUniqueId());
		
		if (player != null && System.currentTimeMillis() - player.getLoginTime() > 15000L && !player.getLocale().getLanguage().equals(event.getLocale().getLanguage())) {
			LanguageDetector detector = LanguageManager.getInstance().getDetector();
			
			if (detector.isEnabled())
				TaskManager.runAsync(() -> {
					if (player.isLoaded()) {
						Language detected = detector.detectUsingClientLocale(player);
						
						if (!detected.equals(player.getLanguage()))
							((BaseChatPluginServerPlayer) player).sendLanguageDetectedMessage(detected);
					}
				}, detector.getDelay());
			applyScoreboard(ScoreboardEvent.LOCALE_CHANGE, event.getTargetEntity(), player.getLocale().getDisplayLanguage());
		}
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
	
	public SpongeListener getListener() {
		return listener;
	}
	
	private static class SpongeListener implements EventListener<Event> {
		
		@Override
		public void handle(Event event) throws Exception {
			((SpongeEventManager) EventManager.getInstance()).execute(this, event);
		}
		
	}
	
}
