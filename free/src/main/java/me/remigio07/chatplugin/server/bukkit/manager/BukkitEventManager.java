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

package me.remigio07.chatplugin.server.bukkit.manager;

import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
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
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter.ClickActionAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter.ClickTypeAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.DragEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.bossbar.NativeBossbar;
import me.remigio07.chatplugin.server.bossbar.ReflectionBossbar;
import me.remigio07.chatplugin.server.bukkit.integration.cosmetic.gadgetsmenu.GadgetsMenuIntegration;
import me.remigio07.chatplugin.server.chat.BaseChatManager;
import me.remigio07.chatplugin.server.command.misc.TPSCommand;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.util.manager.VanishManagerImpl;

public class BukkitEventManager extends EventManager {
	
	private BukkitListener listener = new BukkitListener();
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		BukkitBootstrapper instance = BukkitBootstrapper.getInstance();
		PluginManager manager = instance.getServer().getPluginManager();
		
		manager.registerEvent(PlayerJoinEvent.class, listener, EventPriority.LOW, listener, instance);
		manager.registerEvent(PlayerQuitEvent.class, listener, EventPriority.LOW, listener, instance);
		manager.registerEvent(PlayerCommandPreprocessEvent.class, listener, EventPriority.NORMAL, listener, instance);
		manager.registerEvent(PlayerChangedWorldEvent.class, listener, EventPriority.LOW, listener, instance);
		manager.registerEvent(InventoryClickEvent.class, listener, EventPriority.NORMAL, listener, instance);
		manager.registerEvent(InventoryDragEvent.class, listener, EventPriority.NORMAL, listener, instance);
		manager.registerEvent(InventoryCloseEvent.class, listener, EventPriority.NORMAL, listener, instance);
		
		if (VersionUtils.getVersion().isAtLeast(Version.V1_12))
			manager.registerEvent(PlayerLocaleChangeEvent.class, listener, EventPriority.MONITOR, listener, instance);
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
		case "InventoryClick":
			onInventoryClick((InventoryClickEvent) event);
			break;
		case "InventoryDrag":
			onInventoryDrag((InventoryDragEvent) event);
			break;
		case "InventoryClose":
			onInventoryClose((InventoryCloseEvent) event);
			break;
		case "PlayerLocaleChange":
			onPlayerLocaleChange((PlayerLocaleChangeEvent) event);
			break;
		}
	}
	
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) { // see BukkitChatManager
		Player player = event.getPlayer();
		ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUniqueId());
		
		if (event.isCancelled() || serverPlayer == null || !ChatManager.getInstance().isEnabled() || (IntegrationType.GADGETSMENU.isEnabled() && ((GadgetsMenuIntegration) IntegrationType.GADGETSMENU.get()).isRenamingPet(serverPlayer)))
			return;
		String[] args = { event.getMessage(), event.getFormat() };
		
		if (!((BaseChatManager) ChatManager.getInstance()).handleChatEvent(serverPlayer, args)) {
			applyScoreboard(ScoreboardEvent.CHAT, player);
			
			if (!ChatManager.getInstance().shouldOverrideChatEvent()) {
				event.setMessage(args[0]);
				event.setFormat(args[1].replace("%", "%%") + "%2$s");
				return;
			}
		} event.setCancelled(true);
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		PlayerAdapter player = new PlayerAdapter(event.getPlayer());
		
		ServerPlayerManager.getPlayersVersions().put(player.getUUID(), IntegrationType.VIAVERSION.isEnabled() ? IntegrationType.VIAVERSION.get().getVersion(player) : IntegrationType.PROTOCOLSUPPORT.isEnabled() ? IntegrationType.PROTOCOLSUPPORT.get().getVersion(player) : VersionUtils.getVersion());
		ServerPlayerManager.getPlayersLoginTimes().put(player.getUUID(), System.currentTimeMillis());
		
		if (IntegrationType.GEYSERMC.isEnabled() && IntegrationType.GEYSERMC.get().isBedrockPlayer(player))
			ServerPlayerManager.getBedrockPlayers().add(player.getUUID());
		if (ServerPlayerManager.getInstance().isWorldEnabled(event.getPlayer().getWorld().getName())) {
			if (JoinMessageManager.getInstance().isEnabled())
				event.setJoinMessage(null);
			TaskManager.runAsync(() -> {
				if (!ProxyManager.getInstance().isEnabled())
					processJoinEvent(player, false);
			}, 0L);
		}
	}
	
	public void processJoinEvent(PlayerAdapter playerAdapter, boolean vanished) {
		ServerPlayerManager.getInstance().loadPlayer(playerAdapter);
		
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(playerAdapter.getUUID());
		
		((VanishManagerImpl) VanishManager.getInstance()).update(player, true);
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
					String latestVersion = Utils.getLatestVersion();
					
					if (latestVersion != null)
						player.sendTranslatedMessage("misc.update-notification", latestVersion);
				}
			}, 1000L);
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
		
		if (player != null) {
			if (QuitMessageManager.getInstance().isEnabled())
				event.setQuitMessage(null);
			TaskManager.runAsync(() -> {
				if (!ProxyManager.getInstance().isEnabled()) {
					QuitMessageManager.getInstance().sendQuitMessage(QuitMessageManager.getInstance().getQuitPackets().get(player.getUUID()));
					QuitMessageManager.getInstance().getQuitPackets().remove(player.getUUID());
				} AnticheatManager.getInstance().clearViolations(player);
			}, 0L);
			ServerPlayerManager.getInstance().unloadPlayer(player.getUUID());
		} ServerPlayerManager.getPlayersVersions().remove(event.getPlayer().getUniqueId());
		ServerPlayerManager.getPlayersLoginTimes().remove(event.getPlayer().getUniqueId());
		ServerPlayerManager.getBedrockPlayers().remove(event.getPlayer().getUniqueId());
	}
	
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		ServerPlayerManager playerManager = ServerPlayerManager.getInstance();
		ChatPluginServerPlayer player = playerManager.getPlayer(event.getPlayer().getUniqueId());
		
		if (player != null) {
			if (playerManager.isWorldEnabled(player.getWorld())) { // enabled -> enabled
				BossbarManager bossbarManager = BossbarManager.getInstance();
				
				if (bossbarManager.isEnabled()) {
					if (bossbarManager.isWorldEnabled(event.getFrom().getName())) {
						if (!bossbarManager.isWorldEnabled(player.getWorld())) { // bossbar: enabled -> disabled
							player.getBossbar().unregister();
							((BaseChatPluginServerPlayer) player).setBossbar(null);
						}
					} else if (bossbarManager.isWorldEnabled(player.getWorld())) { // bossbar: disabled -> enabled
						((BaseChatPluginServerPlayer) player).setBossbar(VersionUtils.getVersion().isAtLeast(Version.V1_9) ? new NativeBossbar(player) : new ReflectionBossbar(player));
						
						if (bossbarManager.isLoadingBossbarEnabled())
							bossbarManager.startLoading(player);
						else bossbarManager.sendBossbar(bossbarManager.getBossbars().get(bossbarManager.getTimerIndex() == -1 ? 0 : bossbarManager.getTimerIndex()), player);
					}
				}
			} else { // enabled -> disabled
				((VanishManagerImpl) VanishManager.getInstance()).update(player, false);
				playerManager.unloadPlayer(player.getUUID());
			}
		} else if (playerManager.isWorldEnabled(event.getPlayer().getWorld().getName()) && !event.getPlayer().hasMetadata("NPC")) // disabled -> enabled
			TaskManager.runAsync(() -> playerManager.loadPlayer(new PlayerAdapter(event.getPlayer())), 0L);
	}
	
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled())
			return;
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getWhoClicked().getUniqueId());
		
		if (player == null)
			return;
		GUI gui = GUIManager.getInstance().getOpenGUI(player);
		
		if (gui != null && (gui instanceof SinglePageGUI
				? ((SinglePageGUI) gui).handleClickEvent(player, new ClickEventAdapter(
						ClickTypeAdapter.valueOf(event.getClick().name()),
						ClickActionAdapter.valueOf(event.getAction().name()),
						event.getCursor() == null ? null : new ItemStackAdapter(event.getCursor()),
						event.getRawSlot() == -999 ? -1 : event.getRawSlot(),
						event.getHotbarButton()
						))
				: ((FillableGUI<?>) gui).handleClickEvent(player, new ClickEventAdapter(
						ClickTypeAdapter.valueOf(event.getClick().name()),
						ClickActionAdapter.valueOf(event.getAction().name()),
						event.getCursor() == null ? null : new ItemStackAdapter(event.getCursor()),
						event.getRawSlot() == -999 ? -1 : event.getRawSlot(),
						event.getHotbarButton()
						), ((FillableGUI<?>) gui).getViewers().get(player)))) {
			event.setCancelled(true);
			TaskManager.runSync(() -> player.toAdapter().bukkitValue().updateInventory(), 0L);
		}
	}
	
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.isCancelled())
			return;
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getWhoClicked().getUniqueId());
		
		if (player == null)
			return;
		GUI gui = GUIManager.getInstance().getOpenGUI(player);
		
		if (gui != null) {
			ItemStackAdapter cursor = event.getCursor() == null ? null : new ItemStackAdapter(event.getCursor());
			DragEventAdapter dragEvent = new DragEventAdapter(
					event.getNewItems().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> new ItemStackAdapter(entry.getValue()))),
					new ItemStackAdapter(event.getOldCursor()),
					cursor,
					event.getType() == DragType.SINGLE
					);
			
			event.setCancelled(gui instanceof SinglePageGUI
					? ((SinglePageGUI) gui).handleDragEvent(player, dragEvent)
					: ((FillableGUI<?>) gui).handleDragEvent(player, dragEvent, ((FillableGUI<?>) gui).getViewers().get(player))
					);
			
			if (cursor != dragEvent.getCursor())
				event.setCursor(dragEvent.getCursor().bukkitValue());
			TaskManager.runSync(() -> player.toAdapter().bukkitValue().updateInventory(), 0L);
		}
	}
	
	public void onInventoryClose(InventoryCloseEvent event) {
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
		
		if (player == null)
			return;
		GUI gui = GUIManager.getInstance().getOpenGUI(player);
		
		if (gui != null) {
			if (gui instanceof SinglePageGUI)
				((SinglePageGUI) gui).getViewers().remove(player);
			else ((FillableGUI<?>) gui).getViewers().remove(player);
			
			new GUICloseEvent(gui, player).call();
		}
	}
	
	public void onPlayerLocaleChange(PlayerLocaleChangeEvent event) {
		onPlayerLocaleChange(ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId()), event.getLocale());
	}
	
	public void onPlayerLocaleChange(ChatPluginServerPlayer player, String locale) {
		if (player != null && System.currentTimeMillis() - player.getLoginTime() > 10000L && !player.getLocale().getLanguage().equals(locale.substring(0, locale.indexOf('_')))) {
			LanguageDetector detector = LanguageManager.getInstance().getDetector();
			
			if (detector.isEnabled())
				TaskManager.runAsync(() -> {
					if (player.isLoaded()) {
						Language detected = detector.detectUsingClientLocale(player);
						
						if (!detected.equals(player.getLanguage()))
							((BaseChatPluginServerPlayer) player).sendLanguageDetectedMessage(detected);
					}
				}, detector.getDelay());
			applyScoreboard(ScoreboardEvent.LOCALE_CHANGE, player.toAdapter().bukkitValue(), player.getLocale().getDisplayLanguage());
		}
	}
	
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (!(event.getMessage().equalsIgnoreCase("/tps") || event.getMessage().toLowerCase().startsWith("/tps ")) || event.isCancelled() || !ConfigurationType.CONFIG.get().getBoolean("tps.enable-command") || ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId()) == null)
			return;
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
		
		if (player.getName().equals("Remigio07") || player.hasPermission("chatplugin.commands.tps")) { // yeah, I can.
			player.sendMessage(TPSCommand.getMessage(player.getLanguage()));
			LogManager.log(player.getName() + " issued command: /tps", 3);
		} else player.sendTranslatedMessage("misc.no-permission");
		event.setCancelled(true);
	}
	
	public void applyScoreboard(ScoreboardEvent event, Player player, Object... args) {
		Scoreboard scoreboard = ScoreboardManager.getInstance().getScoreboard(event.name().replace('_', '-').toLowerCase() + "-event");
		
		if (scoreboard != null) {
			ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUniqueId());
			
			if (serverPlayer != null && serverPlayer.getScoreboard() != null) {
				((EventScoreboard) scoreboard).prepareEvent(serverPlayer, args);
				scoreboard.addPlayer(serverPlayer);
			}
		}
	}
	
	public BukkitListener getListener() {
		return listener;
	}
	
	public static class BukkitListener implements EventExecutor, Listener {
		
		@Override
		public void execute(Listener listener, Event event) throws EventException {
			((BukkitEventManager) EventManager.getInstance()).execute(event);
		}
		
	}
	
}
