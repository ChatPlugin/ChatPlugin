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

package me.remigio07.chatplugin.server.gui;

import java.util.StringJoiner;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.event.gui.EmptySlotClickEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIDragEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIOpenEvent;
import me.remigio07.chatplugin.api.server.event.gui.IconClickEvent;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.gui.PerPlayerGUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUILayout;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.DragEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.util.Utils;

public class SinglePageGUIImpl extends SinglePageGUI {
	
	public SinglePageGUIImpl(SinglePageGUILayout layout) {
		super(layout);
		init();
	}
	
	private void init() {
		for (Language language : LanguageManager.getInstance().getLanguages())
			inventories.put(language, new InventoryAdapter(layout.getRows()));
		load();
	}
	
	@Override
	protected int load() {
		long ms = System.currentTimeMillis();
		
		skullOwnerFutures.forEach(future -> future.cancel(false));
		
		for (Language language : LanguageManager.getInstance().getLanguages()) {
			InventoryAdapter inventory = getInventory(language);
			String title = getTitle(language);
			
			for (ChatPluginServerPlayer viewer : viewers)
				if (viewer.isOnline() && viewer.getLanguage().equals(language) && !title.equals(Utils.getTitle(viewer))) // TODO remove isOnline()
					Utils.setTitle(viewer, title, layout.getRows());
			for (Icon icon : layout.getIcons())
				if (icon != null) {
					ItemStackAdapter itemStack = icon.toItemStackAdapter(this, language);
					
					if (itemStack.isPlayerHead() && itemStack.getSkullTextureURL() == null) {
						CompletableFuture<ItemStackAdapter> future = icon.updateSkullOwner(itemStack, this, language);
						
						if (future.isDone()) {
							inventory.setItem(future.join(), icon.getPosition());
							continue;
						} else TaskManager.runAsync(() -> {
							try {
								skullOwnerFutures.add(future);
								inventory.setItem(future.get(5L, TimeUnit.SECONDS), icon.getPosition());
							} catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
								
							}
						}, 0L);
					} if (Environment.isBukkit() && inventory.getItem(icon.getPosition()) != null && inventory.getItem(icon.getPosition()).getType() == itemStack.getType())
						new ItemStackAdapter(inventory.bukkitValue().getItem(icon.getPosition())).importData(itemStack);
					else inventory.setItem(itemStack, icon.getPosition());
				}
		} loaded = true;
		return (int) (System.currentTimeMillis() - ms);
	}
	
	@Override
	public boolean open(ChatPluginServerPlayer player, boolean openActions) {
		if (isLoaded()) {
			GUIOpenEvent event = new GUIOpenEvent(this, player, 0, openActions);
			
			event.call();
			
			if (event.isCancelled())
				return false;
			if (this instanceof PerPlayer)
				((PerPlayer) this).refreshUnloadTask();
			TaskManager.runSync(() -> {
				viewers.add(player);
				player.openInventory(getInventory(player.getLanguage()));
				Utils.setTitle(player, getTitle(player.getLanguage()), layout.getRows());
				
				if (openActions)
					layout.getOpenActions().perform(player, this);
			}, 0L);
			return true;
		} else player.sendTranslatedMessage("guis.still-loading", getTitle(player.getLanguage()));
		return false;
	}
	
	@Override
	public boolean handleClickEvent(ChatPluginServerPlayer player, ClickEventAdapter clickEvent) {
		if (player.hasPermission("chatplugin.guis." + layout.getID())) {
			if (this instanceof PerPlayer)
				((PerPlayer) this).refreshUnloadTask();
			try {
				Icon icon = layout.getIcons().get(clickEvent.getSlot());
				
				if (icon != null) {
					IconClickEvent event = new IconClickEvent(this, player, 0, clickEvent, icon, false);
					
					event.call();
					
					if (icon.getPermission() == null || player.hasPermission(icon.getPermission())) {
						if (event.shouldPerformActions()) {
							TaskManager.runSync(() -> {
								if (!icon.isKeepOpen())
									player.closeInventory();
								GUIManagerImpl.executeCommands(player, icon.formatPlaceholders(
										icon.getCommands().stream().map(command -> command
												.replace("{viewer}", player.getName())
												).collect(Collectors.toList()),
										this,
										player.getLanguage(),
										false
										), clickEvent.getClickType());
							}, 0L);
							player.playSound(layout.getClickSound());
						}
					} else player.sendTranslatedMessage("guis.no-permission-icon");
					return event.isCancelled();
				}
			} catch (IndexOutOfBoundsException e) {
				
			} EmptySlotClickEvent event = new EmptySlotClickEvent(this, player, 0, clickEvent);
			
			event.call();
			return event.isCancelled();
		} else player.sendTranslatedMessage("guis.no-permission");
		return true;
	}
	
	@Override
	public boolean handleDragEvent(ChatPluginServerPlayer player, DragEventAdapter dragEvent) {
		GUIDragEvent event = new GUIDragEvent(this, player, 0, dragEvent);
		
		event.call();
		return event.isCancelled();
	}
	
	@Override
	public String getTitle(Language language) {
		String title = layout.getTitle(language, true);
		return ChatColor.translate(titlesTranslator == null ? title : titlesTranslator.apply(title, language));
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "SinglePageGUIImpl{", "}")
				.add("id=\"" + id + "\"")
				.add("loaded=" + loaded)
				.toString();
	}
	
	public static class PerPlayer extends SinglePageGUIImpl implements PerPlayerGUI {
		
		private ChatPluginServerPlayer player;
		private long unloadTaskID;
		
		protected PerPlayer(SinglePageGUILayout layout, ChatPluginServerPlayer player) {
			super(layout);
			id = id + "-" + player.getName();
			this.player = player;
			
			refreshUnloadTask();
		}
		
		@Override
		public ChatPluginServerPlayer getPlayer() {
			return player;
		}
		
		@Override
		public long getUnloadTaskID() {
			return unloadTaskID;
		}
		
		@Deprecated
		@Override
		public void unload(boolean quit) {
			for (ChatPluginServerPlayer viewer : viewers) {
				if (quit)
					viewer.sendTranslatedMessage("guis.player-went-offline", player.getName(), getTitle(viewer.getLanguage()));
				else viewer.sendTranslatedMessage("guis.unloaded", getTitle(viewer.getLanguage()), Utils.formatTime(GUIManager.getInstance().getPerPlayerGUIsUnloadTime(), viewer.getLanguage(), false, true));
				
				viewer.closeInventory();
			} skullOwnerFutures.forEach(future -> future.cancel(false));
			TaskManager.cancelSync(unloadTaskID);
			GUIManager.getInstance().getGUIs().remove(this);
		}
		
		@Override
		public String toString() {
			return new StringJoiner(", ", "SinglePageGUIImpl.PerPlayer{", "}")
					.add("id=\"" + id + "\"")
					.add("loaded=" + loaded)
					.add("player=" + player)
					.toString();
		}
		
		public void refreshUnloadTask() {
			if (unloadTaskID != -1)
				TaskManager.cancelSync(unloadTaskID);
			unloadTaskID = TaskManager.runSync(() -> unload(false), GUIManager.getInstance().getPerPlayerGUIsUnloadTime());
		}
		
	}
	
}
