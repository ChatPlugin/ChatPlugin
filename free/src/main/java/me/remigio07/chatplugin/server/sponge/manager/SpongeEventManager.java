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

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent.Teleport;
import org.spongepowered.api.event.entity.living.humanoid.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.Drag;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent.NumberPress;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent.Close;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageChannelEvent.Chat;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect;
import org.spongepowered.api.event.network.ClientConnectionEvent.Join;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

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
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter.ClickActionAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter.ClickTypeAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.DragEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;
import me.remigio07.chatplugin.server.bossbar.NativeBossbar;
import me.remigio07.chatplugin.server.chat.BaseChatManager;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.util.manager.VanishManagerImpl;

public class SpongeEventManager extends EventManager {
	
	private SpongeListener listener = new SpongeListener();
	
	@SuppressWarnings("unchecked")
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		SpongeBootstrapper instance = SpongeBootstrapper.getInstance();
		org.spongepowered.api.event.EventManager manager = Sponge.getEventManager();
		
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
			onClickInventory((ClickInventoryEvent) event, ClickTypeAdapter.DOUBLE_CLICK);
			break;
		case "ClickInventory$Drag$Middle":
		case "ClickInventory$Drag$Primary":
			onDragInventory((Drag) event, false);
			break;
		case "ClickInventory$Drag$Secondary":
			onDragInventory((Drag) event, true);
			break;
		case "ClickInventory$Drop$Full":
			onClickInventory((ClickInventoryEvent) event, ClickTypeAdapter.CONTROL_DROP);
			break;
		case "ClickInventory$Drop$Single":
			onClickInventory((ClickInventoryEvent) event, ClickTypeAdapter.DROP);
			break;
		case "ClickInventory$Middle":
			onClickInventory((ClickInventoryEvent) event, ClickTypeAdapter.MIDDLE);
			break;
		case "ClickInventory$NumberPress":
			onClickInventory((ClickInventoryEvent) event, ClickTypeAdapter.NUMBER_KEY);
			break;
		case "ClickInventory$Primary":
		case "ClickInventory$Drop$Outside$Primary":
			onClickInventory((ClickInventoryEvent) event, ClickTypeAdapter.LEFT);
			break;
		case "ClickInventory$Secondary":
		case "ClickInventory$Drop$Outside$Secondary":
			onClickInventory((ClickInventoryEvent) event, ClickTypeAdapter.RIGHT);
			break;
		case "ClickInventory$Shift$Primary":
			onClickInventory((ClickInventoryEvent) event, ClickTypeAdapter.SHIFT_LEFT);
			break;
		case "ClickInventory$Shift$Secondary":
			onClickInventory((ClickInventoryEvent) event, ClickTypeAdapter.SHIFT_RIGHT);
			break;
		case "InteractInventory$Close":
			onInteractInventory$Close((Close) event);
			break;
		case "DisplaceEntity$Teleport":
			onDisplaceEntityEvent$Teleport(event);
			break;
		}
	}
	
	public void onMessageChannel$Chat(MessageChannelEvent.Chat event) { // see SpongeChatManager
		Player player = event.getCause().first(Player.class).get();
		ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUniqueId());
		
		if (event.isCancelled() || serverPlayer == null)
			return;
		String[] args = { event.getRawMessage().toPlain(), Utils.deserializeSpongeText(event.getFormatter().format()) };
		
		if (!((BaseChatManager) ChatManager.getInstance()).handleChatEvent(serverPlayer, args)) {
			applyScoreboard(ScoreboardEvent.CHAT, player);
			
			if (!ChatManager.getInstance().shouldOverrideChatEvent()) {
				event.setMessage(Utils.serializeSpongeText(args[0], false));
				event.getFormatter().setHeader(Utils.serializeSpongeText(args[1], false));
				return;
			}
		} event.setCancelled(true);
	}
	
	public void onClientConnection$Join(ClientConnectionEvent.Join event) {
		PlayerAdapter player = new PlayerAdapter(event.getTargetEntity());
		
		ServerPlayerManager.getPlayersVersions().put(player.getUUID(), IntegrationType.VIAVERSION.isEnabled() ? IntegrationType.VIAVERSION.get().getVersion(player) : IntegrationType.PROTOCOLSUPPORT.isEnabled() ? IntegrationType.PROTOCOLSUPPORT.get().getVersion(player) : VersionUtils.getVersion());
		ServerPlayerManager.getPlayersLoginTimes().put(player.getUUID(), System.currentTimeMillis());
		
		if (IntegrationType.GEYSERMC.isEnabled() && IntegrationType.GEYSERMC.get().isBedrockPlayer(player))
			ServerPlayerManager.getBedrockPlayers().add(player.getUUID());
		if (ServerPlayerManager.getInstance().isWorldEnabled(event.getTargetEntity().getWorld().getName())) {
			if (!ProxyManager.getInstance().isEnabled())
				processJoinEvent(player, false);
			event.setMessageCancelled(true);
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
					String latestVersion = me.remigio07.chatplugin.common.util.Utils.getLatestVersion();
					
					if (latestVersion != null)
						player.sendTranslatedMessage("misc.update-notification", latestVersion);
				}
			}, 1000L);
	}
	
	public void onClientConnection$Disconnect(ClientConnectionEvent.Disconnect event) {
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getTargetEntity().getUniqueId());
		
		if (player != null) {
			if (!ProxyManager.getInstance().isEnabled()) {
				QuitMessageManager.getInstance().sendQuitMessage(QuitMessageManager.getInstance().getQuitPackets().get(player.getUUID()));
				QuitMessageManager.getInstance().getQuitPackets().remove(player.getUUID());
			} AnticheatManager.getInstance().clearViolations(player);
			ServerPlayerManager.getInstance().unloadPlayer(player.getUUID());
			event.setMessageCancelled(true);
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
	
	public void onClickInventory(ClickInventoryEvent event, ClickTypeAdapter clickType) {
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getCause().first(Player.class).get().getUniqueId());
		
		if (player == null)
			return;
		GUI gui = GUIManager.getInstance().getOpenGUI(player);
		
		if (gui != null) {
			ItemStack cursor = event.getCursorTransaction().getOriginal().createStack();
			int slot = event.getSlot().isPresent() ? event.getSlot().get().getInventoryProperty(SlotIndex.class).get().getValue() : -1;
			ClickActionAdapter clickAction = ClickActionAdapter.NOTHING;
			
			/*
			 * Sponge does not provide any API to detect the click action, so we have to calculate it ourselves.
			 * 
			 * logic: https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/nms-patches/net/minecraft/server/network/PlayerConnection.patch#1343-1504
			 * 
			 * some the following statements do not check if the other inventory has free space to receive the moved items.
			 * this is intentional to keep consistency between the Bukkit and the Sponge implementations.
			 */
			if (clickType == ClickTypeAdapter.LEFT) {
				if (slot != -1) {
					ItemStack clicked = event.getTransactions().get(0).getOriginal().createStack();
					
					if (!clicked.isEmpty()) {
						if (!cursor.isEmpty()) {
							try {
								JsonObject cursorJSON = (JsonObject) Jsoner.deserialize(DataFormats.JSON.write(cursor.toContainer()));
								JsonObject clickedJSON = (JsonObject) Jsoner.deserialize(DataFormats.JSON.write(clicked.toContainer()));
								
								cursorJSON.remove("Count");
								clickedJSON.remove("Count");
								
								if (cursorJSON.equals(clickedJSON)) {
									int toPlace = cursor.getQuantity();
									toPlace = Math.min(toPlace, clicked.getMaxStackQuantity() - clicked.getQuantity());
									toPlace = Math.min(toPlace, 64 - clicked.getQuantity());
									
									if (toPlace == 1)
										clickAction = ClickActionAdapter.PLACE_ONE;
									else if (toPlace == cursor.getQuantity())
										clickAction = ClickActionAdapter.PLACE_ALL;
									else if (toPlace < 0)
										clickAction = toPlace != -1 ? ClickActionAdapter.PICKUP_SOME : ClickActionAdapter.PICKUP_ONE;
									else if (toPlace != 0)
										clickAction = ClickActionAdapter.PLACE_SOME;
								} else if (cursor.getQuantity() <= clicked.getMaxStackQuantity())
									clickAction = ClickActionAdapter.SWAP_WITH_CURSOR;
							} catch (IOException | JsonException e) {
								e.printStackTrace();
							}
						} else clickAction = ClickActionAdapter.PICKUP_ALL;
					} else if (!cursor.isEmpty())
						clickAction = ClickActionAdapter.PLACE_ALL;
				} else if (!cursor.isEmpty())
					clickAction = ClickActionAdapter.DROP_ALL_CURSOR;
			} else if (clickType == ClickTypeAdapter.SHIFT_LEFT || clickType == ClickTypeAdapter.SHIFT_RIGHT) {
				if (event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).peek().isPresent())
					clickAction = ClickActionAdapter.MOVE_TO_OTHER_INVENTORY;
			} else if (clickType == ClickTypeAdapter.RIGHT) {
				if (slot != -1) {
					ItemStack clicked = event.getTransactions().get(0).getOriginal().createStack();
					
					if (!clicked.isEmpty()) {
						if (!cursor.isEmpty()) {
							try {
								JsonObject cursorJSON = (JsonObject) Jsoner.deserialize(DataFormats.JSON.write(cursor.toContainer()));
								JsonObject clickedJSON = (JsonObject) Jsoner.deserialize(DataFormats.JSON.write(clicked.toContainer()));
								
								cursorJSON.remove("Count");
								clickedJSON.remove("Count");
								
								if (cursorJSON.equals(clickedJSON)) {
									int toPlace = 1;
									toPlace = Math.min(toPlace, clicked.getMaxStackQuantity() - clicked.getQuantity());
									toPlace = Math.min(toPlace, 64 - clicked.getQuantity());
									
									if (toPlace == 1)
										clickAction = ClickActionAdapter.PLACE_ONE;
									else if (toPlace == cursor.getQuantity())
										clickAction = ClickActionAdapter.PLACE_ALL;
									else if (toPlace < 0)
										clickAction = toPlace != -1 ? ClickActionAdapter.PICKUP_SOME : ClickActionAdapter.PICKUP_ONE;
									else if (toPlace != 0)
										clickAction = ClickActionAdapter.PLACE_SOME;
								} else if (cursor.getQuantity() <= clicked.getMaxStackQuantity())
									clickAction = ClickActionAdapter.SWAP_WITH_CURSOR;
							} catch (IOException | JsonException e) {
								e.printStackTrace();
							}
						} else clickAction = ClickActionAdapter.PICKUP_HALF;
					} else if (!cursor.isEmpty())
						clickAction = ClickActionAdapter.PLACE_ONE;
				} else if (!cursor.isEmpty())
					clickAction = ClickActionAdapter.DROP_ONE_CURSOR;
			} else if (clickType == ClickTypeAdapter.MIDDLE) {
				if (cursor.isEmpty() && event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).peek().isPresent() && player.toAdapter().spongeValue().gameMode().get() == GameModes.CREATIVE)
					clickAction = ClickActionAdapter.CLONE_STACK;
			} else if (clickType == ClickTypeAdapter.NUMBER_KEY) {
				Optional<ItemStack> hotbar = player.toAdapter().spongeValue().getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(((NumberPress) event).getNumber()))).peek();
				clickAction = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).peek().isPresent()
						? !hotbar.isPresent() || slot >= gui.getLayout().getSize()
						? ClickActionAdapter.HOTBAR_SWAP
						: ClickActionAdapter.HOTBAR_MOVE_AND_READD
						: hotbar.isPresent() && !hotbar.get().isEmpty()
						? ClickActionAdapter.HOTBAR_SWAP
						: ClickActionAdapter.NOTHING;
			} else if (clickType == ClickTypeAdapter.DOUBLE_CLICK) {
				if (slot >= 0 && !cursor.isEmpty() && event.getTargetInventory().containsAny(cursor))
					clickAction = ClickActionAdapter.COLLECT_TO_CURSOR;
			} else if (clickType == ClickTypeAdapter.DROP) {
				if (!event.getTransactions().isEmpty())
					slot = event.getTransactions().get(0).getSlot().getInventoryProperty(SlotIndex.class).get().getValue();
				if (slot != -1 && !event.getTransactions().get(0).getOriginal().createStack().isEmpty())
					clickAction = ClickActionAdapter.DROP_ONE_SLOT;
			} else if (clickType == ClickTypeAdapter.CONTROL_DROP) {
				if (!event.getTransactions().isEmpty())
					slot = event.getTransactions().get(0).getSlot().getInventoryProperty(SlotIndex.class).get().getValue();
				if (slot != -1 && !event.getTransactions().get(0).getOriginal().createStack().isEmpty())
					clickAction = ClickActionAdapter.DROP_ALL_SLOT;
			} event.setCancelled(gui instanceof SinglePageGUI
					? ((SinglePageGUI) gui).handleClickEvent(player, new ClickEventAdapter(
							clickType,
							clickAction,
							new ItemStackAdapter(cursor),
							slot,
							event instanceof NumberPress ? ((NumberPress) event).getNumber() : -1
							))
					: ((FillableGUI<?>) gui).handleClickEvent(player, new ClickEventAdapter(
							clickType,
							clickAction,
							new ItemStackAdapter(cursor),
							slot,
							event instanceof NumberPress ? ((NumberPress) event).getNumber() : -1
							), ((FillableGUI<?>) gui).getViewers().get(player)));
		}
	}
	
	public void onDragInventory(ClickInventoryEvent.Drag event, boolean single) {
		if (event.isCancelled())
			return;
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(event.getCause().first(Player.class).get().getUniqueId());
		
		if (player == null)
			return;
		GUI gui = GUIManager.getInstance().getOpenGUI(player);
		
		if (gui != null) {
			ItemStackAdapter cursor = new ItemStackAdapter(event.getCursorTransaction().getDefault().createStack());
			DragEventAdapter dragEvent = new DragEventAdapter(
					event.getTransactions().stream().collect(Collectors.toMap(transaction -> transaction.getSlot().getInventoryProperty(SlotIndex.class).get().getValue(), transaction -> new ItemStackAdapter(transaction.getOriginal().createStack()))),
					new ItemStackAdapter(event.getCursorTransaction().getOriginal().createStack()),
					cursor,
					single
					);
			
			event.setCancelled(gui instanceof SinglePageGUI
					? ((SinglePageGUI) gui).handleDragEvent(player, dragEvent)
					: ((FillableGUI<?>) gui).handleDragEvent(player, dragEvent, ((FillableGUI<?>) gui).getViewers().get(player))
					);
			
			if (cursor != dragEvent.getCursor())
				event.getCursorTransaction().setCustom(dragEvent.getCursor().spongeValue().createSnapshot());
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
		if (event.isCancelled() || !(event.getTargetEntity() instanceof Player))
			return;
		ServerPlayerManager playerManager = ServerPlayerManager.getInstance();
		ChatPluginServerPlayer player = playerManager.getPlayer(event.getTargetEntity().getUniqueId());
		
		if (player != null) {
			if (playerManager.isWorldEnabled(player.getWorld())) { // enabled -> enabled
				BossbarManager bossbarManager = BossbarManager.getInstance();
				
				if (bossbarManager.isEnabled()) {
					if (bossbarManager.isWorldEnabled(event.getFromTransform().getExtent().getName())) {
						if (!bossbarManager.isWorldEnabled(player.getWorld())) { // bossbar: enabled -> disabled
							player.getBossbar().unregister();
							((BaseChatPluginServerPlayer) player).setBossbar(null);
						}
					} else if (bossbarManager.isWorldEnabled(player.getWorld())) { // bossbar: disabled -> enabled
						((BaseChatPluginServerPlayer) player).setBossbar(new NativeBossbar(player));
						
						if (bossbarManager.isLoadingBossbarEnabled())
							bossbarManager.startLoading(player);
						else bossbarManager.sendBossbar(bossbarManager.getBossbars().get(bossbarManager.getTimerIndex() == -1 ? 0 : bossbarManager.getTimerIndex()), player);
					}
				}
			} else { // enabled -> disabled
				((VanishManagerImpl) VanishManager.getInstance()).update(player, false);
				playerManager.unloadPlayer(player.getUUID());
			}
		} else if (playerManager.isWorldEnabled(event.getTargetEntity().getWorld().getName())) // disabled -> enabled
			playerManager.loadPlayer(new PlayerAdapter(event.getTargetEntity()));
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
