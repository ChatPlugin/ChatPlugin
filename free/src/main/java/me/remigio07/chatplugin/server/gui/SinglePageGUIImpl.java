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

package me.remigio07.chatplugin.server.gui;

import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.event.gui.GUIClickEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIOpenEvent;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.gui.PerPlayerGUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUILayout;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.command.CommandsHandler;
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
	
	public String getTitle(Language language) {
		return ChatColor.translate(titlesTranslator == null ? layout.getTitle(language, true) : titlesTranslator.apply(this, language));
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
			if (openActions)
				layout.getOpenActions().perform(player);
			if (this instanceof PerPlayer)
				((PerPlayer) this).refreshUnloadTask();
			TaskManager.runSync(() -> {
				player.openInventory(getInventory(player.getLanguage()));
				Utils.setTitle(player, getTitle(player.getLanguage()), layout.getRows());
				viewers.add(player);
			}, 0L);
			return true;
		} else player.sendTranslatedMessage("guis.still-loading", getTitle(player.getLanguage()));
		return false;
	}
	
	@Override
	public boolean handleClickEvent(ChatPluginServerPlayer player, int slot) {
		if (player.hasPermission("chatplugin.guis." + layout.getID())) {
			try {
				Icon icon = layout.getIcons().get(slot);
				
				if (icon != null) {
					GUIClickEvent event = new GUIClickEvent(this, player, icon, 0);
					
					event.call();
					
					if (event.isCancelled())
						return false;
					if (this instanceof PerPlayer)
						((PerPlayer) this).refreshUnloadTask();
					TaskManager.runSync(() -> {
						if (!icon.isKeepOpen())
							player.closeInventory();
						CommandsHandler.executeCommands(player, icon.formatPlaceholders(icon.getCommands().stream().map(command -> command.replace("{viewer}", player.getName())).collect(Collectors.toList()), this, player.getLanguage(), false));
					}, 0L);
					player.playSound(layout.getClickSound());
					return true;
				}
			} catch (IndexOutOfBoundsException e) {
				
			}
		} else player.sendTranslatedMessage("guis.no-permission");
		return false;
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
		public void unload() {
			for (ChatPluginServerPlayer viewer : new ArrayList<>(viewers)) {
				viewer.closeInventory();
				viewer.sendTranslatedMessage("guis.player-went-offline", player.getName(), getTitle(viewer.getLanguage()));
			} viewers.clear();
			skullOwnerFutures.forEach(future -> future.cancel(false));
			TaskManager.cancelSync(unloadTaskID);
			GUIManager.getInstance().getGUIs().remove(this);
		}
		
		public void refreshUnloadTask() {
			if (unloadTaskID != -1)
				TaskManager.cancelSync(unloadTaskID);
			unloadTaskID = TaskManager.runSync(() -> {
				for (ChatPluginServerPlayer viewer : new ArrayList<>(viewers)) {
					viewer.closeInventory();
					viewer.sendTranslatedMessage(
							"guis.unloaded",
							super.getTitle(viewer.getLanguage()),
							Utils.formatTime(GUIManager.getInstance().getPerPlayerGUIsUnloadTime(), viewer.getLanguage(), false, true)
							);
				}
			}, GUIManager.getInstance().getPerPlayerGUIsUnloadTime());
		}
		
	}
	
}
