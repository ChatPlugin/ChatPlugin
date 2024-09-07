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

package me.remigio07.chatplugin.server.sponge.manager;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent.Teleport;
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent.Close;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageChannelEvent.Chat;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.event.gui.GUICloseEvent;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
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
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;
import me.remigio07.chatplugin.server.bossbar.NativeBossbar;
import me.remigio07.chatplugin.server.chat.ChatManagerImpl;
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
		
		if (ChatManager.getInstance().isEnabled())
			manager.registerListener(instance, MessageChannelEvent.Chat.class, Order.valueOf(ChatManager.getInstance().getChatEventPriority()), listener);
		manager.registerListener(instance, ClientConnectionEvent.Join.class, Order.EARLY, listener);
		manager.registerListener(instance, ClientConnectionEvent.Disconnect.class, Order.EARLY, listener);
		manager.registerListener(instance, PlayerChangeClientSettingsEvent.class, Order.POST, listener);
		manager.registerListener(instance, ClickInventoryEvent.class, Order.DEFAULT, listener);
		manager.registerListener(instance, InteractInventoryEvent.Close.class, Order.DEFAULT, listener);
		
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
		case "PlayerChangeClientSettings":
			onPlayerChangeClientSettings((PlayerChangeClientSettingsEvent) event);
			break;
		case "ClickInventory$Double":
		case "ClickInventory$Drag$Middle":
		case "ClickInventory$Drag$Primary":
		case "ClickInventory$Drag$Secondary":
		case "ClickInventory$Drop$Single":
		case "ClickInventory$Drop$Full":
		case "ClickInventory$Drop$Outside$Primary":
		case "ClickInventory$Drop$Outside$Secondary":
		case "ClickInventory$Middle":
		case "ClickInventory$NumberPress":
		case "ClickInventory$Primary":
		case "ClickInventory$Secondary":
		case "ClickInventory$Shift$Primary":
		case "ClickInventory$Shift$Secondary":
			onClickInventory((ClickInventoryEvent) event);
			break;
		case "InteractInventory$Close":
			onInteractInventory$Close((Close) event);
			break;
		case "DisplaceEntity$Teleport":
			onDisplaceEntityEvent$Teleport(event);
			break;
		}
	}
	
	public void onMessageChannel$Chat(MessageChannelEvent.Chat event) {
		Player player = event.getCause().first(Player.class).get();
		ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUniqueId());
		
		if (event.isCancelled() || serverPlayer == null)
			return;
		String[] args = { event.getRawMessage().toPlain(), Utils.deserializeSpongeText(event.getFormatter().format()) };
		
		if (!((ChatManagerImpl) ChatManager.getInstance()).handleChatEvent(serverPlayer, args)) {
			applyScoreboard(ScoreboardEvent.CHAT, player);
			
			if (!ChatManager.getInstance().shouldOverrideChatEvent()) {
				event.setMessage(Utils.serializeSpongeText(args[0], false));
				event.getFormatter().setHeader(Utils.serializeSpongeText(args[1], false));
				return;
			}
		} event.setCancelled(true);
	}
	
	public void onClientConnection$Join(ClientConnectionEvent.Join event) {
		if (ServerPlayerManager.getInstance().isWorldEnabled(event.getTargetEntity().getWorld().getName())) {
			event.setMessageCancelled(true);
			
			if (ProxyManager.getInstance().isEnabled())
				return;
			PlayerAdapter player = new PlayerAdapter(event.getTargetEntity());
			
			ServerPlayerManager.getPlayersVersions().put(player.getUUID(), IntegrationType.VIAVERSION.isEnabled() ? IntegrationType.VIAVERSION.get().getVersion(player) : IntegrationType.PROTOCOLSUPPORT.isEnabled() ? IntegrationType.PROTOCOLSUPPORT.get().getVersion(player) : VersionUtils.getVersion());
			ServerPlayerManager.getPlayersLoginTimes().put(player.getUUID(), System.currentTimeMillis());
			
			if (IntegrationType.GEYSERMC.isEnabled() && IntegrationType.GEYSERMC.get().isBedrockPlayer(player))
				ServerPlayerManager.getBedrockPlayers().add(player.getUUID());
			processJoinEvent(player, false);
		}
	}
	
	public void processJoinEvent(PlayerAdapter playerAdapter, boolean vanished) {
		ServerPlayerManager.getInstance().loadPlayer(playerAdapter);
		
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(playerAdapter.getUUID());
		
		VanishManager.getInstance().update(player, true);
		SuggestedVersionManager.getInstance().check(player);
		JoinTitleManager.getInstance().sendJoinTitle(player, true);
		WelcomeMessageManager.getInstance().sendWelcomeMessage(player, true);
		
		if (vanished) {
			VanishManager.getInstance().hide(player);
			QuitMessageManager.getInstance().getFakeQuits().add(player.getUUID());
		} else JoinMessageManager.getInstance().sendJoinMessage(player);
		
		if (ConfigurationType.CONFIG.get().getBoolean("settings.enable-update-notification") && player.hasPermission("chatplugin.update-notification"))
			TaskManager.runAsync(() -> {
				if (player.isLoaded()) {
					String latestVersion = me.remigio07.chatplugin.common.util.Utils.getLatestVersion();
					
					if (latestVersion != null)
						player.sendTranslatedMessage("misc.update-notification", latestVersion);
				}
			}, 1000L);
	}
	
	public void onClientConnection$Disconnect(ClientConnectionEvent.Disconnect event) {
		if (ServerPlayerManager.getInstance().isWorldEnabled(event.getTargetEntity().getWorld().getName()))
			event.setMessageCancelled(true);
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getTargetEntity().getUniqueId());
		
		if (player != null) {
			if (!ProxyManager.getInstance().isEnabled()) {
				QuitMessageManager.getInstance().sendQuitMessage(QuitMessageManager.getInstance().getQuitPackets().get(player.getUUID()));
				QuitMessageManager.getInstance().getQuitPackets().remove(player.getUUID());
			} AnticheatManager.getInstance().clearViolations(player);
			ServerPlayerManager.getInstance().unloadPlayer(player.getUUID());
		} ServerPlayerManager.getPlayersVersions().remove(event.getTargetEntity().getUniqueId());
		ServerPlayerManager.getPlayersLoginTimes().remove(event.getTargetEntity().getUniqueId());
		ServerPlayerManager.getBedrockPlayers().remove(event.getTargetEntity().getUniqueId());
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
	
	public void onClickInventory(ClickInventoryEvent event) {
		if (event.getTransactions().isEmpty())
			return;
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getCause().first(Player.class).get().getUniqueId());
		
		if (player == null)
			return;
		GUI gui = GUIManager.getInstance().getOpenGUI(player);
		
		if (gui != null) {
			for (SlotTransaction transaction : event.getTransactions())
				if (gui instanceof SinglePageGUI)
					((SinglePageGUI) gui).handleClickEvent(player, transaction.getSlot().getInventoryProperty(SlotIndex.class).get().getValue());
				else ((FillableGUI<?>) gui).handleClickEvent(player, ((FillableGUI<?>) gui).getViewers().get(player), transaction.getSlot().getInventoryProperty(SlotIndex.class).get().getValue());
			event.setCancelled(true);
		}
	}
	
	public void onInteractInventory$Close(InteractInventoryEvent.Close event) {
		if (event.isCancelled())
			return;
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getCause().first(Player.class).get().getUniqueId());
		
		if (player == null)
			return;
		for (GUI gui : GUIManager.getInstance().getGUIs()) {
			if (gui instanceof SinglePageGUI && ((SinglePageGUI) gui).getViewers().contains(player)) {
				((SinglePageGUI) gui).getViewers().remove(player);
				new GUICloseEvent(gui, player).call();
				break;
			} if (gui instanceof FillableGUI && ((FillableGUI<?>) gui).getViewers().containsKey(player)) {
				((FillableGUI<?>) gui).getViewers().remove(player);
				new GUICloseEvent(gui, player).call();
				break;
			}
		}
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
		
		if (oldWorld == playerManager.isWorldEnabled(player.getWorld())) {
			if (BossbarManager.getInstance().isEnabled()) {
				boolean oldWorld2 = BossbarManager.getInstance().isWorldEnabled(event.getFromTransform().getExtent().getName());
				
				if (oldWorld2 == BossbarManager.getInstance().isWorldEnabled(player.getWorld()))
					return;
				if (oldWorld2) {
					player.getBossbar().unregister();
					((BaseChatPluginServerPlayer) player).setBossbar(null);
				} else {
					((BaseChatPluginServerPlayer) player).setBossbar(new NativeBossbar(player));
					
					if (BossbarManager.getInstance().isLoadingBossbarEnabled())
						BossbarManager.getInstance().startLoading(player);
					else BossbarManager.getInstance().sendBossbar(BossbarManager.getInstance().getBossbars().get(BossbarManager.getInstance().getTimerIndex() == -1 ? 0 : BossbarManager.getInstance().getTimerIndex()), player);
				}
			} return;
		} if (oldWorld) {
			VanishManager.getInstance().update(player, false);
			playerManager.unloadPlayer(player.getUUID());
		} else playerManager.loadPlayer(player.toAdapter());
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
	
	public static class SpongeListener implements EventListener<Event> {
		
		@Override
		public void handle(Event event) throws Exception {
			((SpongeEventManager) EventManager.getInstance()).execute(this, event);
		}
		
	}
	
}
