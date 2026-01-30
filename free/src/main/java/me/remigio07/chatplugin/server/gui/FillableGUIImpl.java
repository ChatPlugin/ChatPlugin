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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.event.gui.EmptySlotClickEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIDragEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIOpenEvent;
import me.remigio07.chatplugin.api.server.event.gui.IconClickEvent;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.FillableGUILayout;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.GUIFiller;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.gui.IconLayout;
import me.remigio07.chatplugin.api.server.gui.IconType;
import me.remigio07.chatplugin.api.server.gui.PerPlayerGUI;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.DragEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.util.Utils;

public class FillableGUIImpl<T> extends FillableGUI<T> {
	
	public FillableGUIImpl(FillableGUILayout layout) {
		super(layout);
		init();
	}
	
	private void init() {
		for (Language language : LanguageManager.getInstance().getLanguages())
			inventories.put(language, Stream.of(new InventoryAdapter(layout.getRows(), UUID.randomUUID().toString())).collect(Collectors.toCollection(CopyOnWriteArrayList::new)));
		load();
	}
	
	@Override
	protected synchronized int load() { // *chaos starts*
		long ms = System.currentTimeMillis();
		int pages = 1, fillersPerPage = 0, previousPageIconPosition = -1, nextPageIconPosition = -1;
		Map<Language, ItemStackAdapter[]> items = new HashMap<>();
		
		skullOwnerFutures.forEach(future -> future.cancel(false));
		
		if (fillers.isEmpty()) {
			for (Language language : LanguageManager.getInstance().getLanguages()) {
				InventoryAdapter inventory = getInventory(language, 0);
				
				inventory.clear();
				setItem(inventory, getLayout().getEmptyListIcon(), language);
			}
		} else pages = (fillers.size() - 1) / (fillersPerPage = getLayout().getEndSlot() - getLayout().getStartSlot() + 1 - (int) getLayout().getIcons()
				.stream()
				.filter(icon -> icon != null && icon.getPosition() >= getLayout().getStartSlot() && icon.getPosition() <= getLayout().getEndSlot())
				.count()) + 1;
		
		if (pages < generatedIcons.size())
			for (int page = pages; page < generatedIcons.size(); page++)
				generatedIcons.remove(page);
		for (Language language : LanguageManager.getInstance().getLanguages()) {
			ItemStackAdapter[] array = new ItemStackAdapter[layout.getSize()];
			List<InventoryAdapter> inventories = getInventories(language);
			
			for (Icon icon : layout.getIcons()) {
				if (icon == null)
					continue;
				if (icon.getID().equals("previous-page"))
					previousPageIconPosition = icon.getPosition();
				else if (icon.getID().equals("next-page"))
					nextPageIconPosition = icon.getPosition();
				array[icon.getPosition()] = icon.toItemStackAdapter(this, language);
			} if (pages < inventories.size())
				for (int page = pages; page < inventories.size(); page++) {
					for (ChatPluginServerPlayer viewer : inventories.get(page).getViewers())
						open(viewer, page - 1, false);
					inventories.remove(page);
				}
			items.put(language, array);
		} for (int page = 0; page < pages; page++) {
			List<Icon> generatedIcons = getGeneratedIcons(page);
			
			if (generatedIcons == null)
				this.generatedIcons.add(generatedIcons = new CopyOnWriteArrayList<>(Collections.nCopies(getLayout().getEndSlot() + 1, null)));
			else this.generatedIcons.set(page, generatedIcons = new CopyOnWriteArrayList<>(Collections.nCopies(getLayout().getEndSlot() + 1, null)));
			
			for (int index = page * fillersPerPage, position = getLayout().getStartSlot(); index < (page + 1) * fillersPerPage; index++) {
				if (index == fillers.size())
					break;
				while (layout.getIcons().get(position) != null)
					position++;
				GUIFiller<?> filler = fillers.get(index);
				
				generatedIcons.set(position, filler.getIcon(new GeneratedIcon(filler, filler.getIconLayout(), position)));
				position++;
			} for (Language language : LanguageManager.getInstance().getLanguages()) {
				ItemStackAdapter[] array = items.get(language);
				InventoryAdapter inventory = getInventory(language, page);
				
				if (inventory == null)
					getInventories(language).add(inventory = new InventoryAdapter(layout.getRows(), UUID.randomUUID().toString()));
				if (fillersPerPage != 0)
					inventory.clear();
				for (int position = 0; position < getLayout().getSize(); position++) {
					ItemStackAdapter item = array[position];
					
					if (item != null && (previousPageIconPosition != position || page != 0) && (nextPageIconPosition != position || page != pages - 1))
						setItem(inventory, item, layout.getIcons().get(position), language, position);
					else if (position < getLayout().getEndSlot() + 1) {
						Icon generatedIcon = generatedIcons.get(position);
						
						if (generatedIcon != null)
							setItem(inventory, generatedIcon, language);
					}
				}
			}
		} loaded = true;
		
		for (Entry<ChatPluginServerPlayer, Integer> viewer : viewers.entrySet()) {
			String title = getTitle(viewer.getKey().getLanguage(), viewer.getValue());
			
			if (viewer.getKey().isOnline() && !title.equals(Utils.getTitle(viewer.getKey()))) // TODO remove isOnline()
				Utils.setTitle(viewer.getKey(), title, layout.getRows());
		} return (int) (System.currentTimeMillis() - ms);
	}
	
	private void setItem(InventoryAdapter inventory, Icon icon, Language language) {
		setItem(inventory, icon.toItemStackAdapter(this, language), icon, language, icon.getPosition());
	}
	
	private void setItem(InventoryAdapter inventory, ItemStackAdapter itemStack, Icon icon, Language language, int position) {
		if (itemStack.isPlayerHead() && itemStack.getSkullTextureURL() == null) {
			CompletableFuture<ItemStackAdapter> future = icon.updateSkullOwner(itemStack, this, language);
			
			if (future.isDone()) {
				inventory.setItem(future.join(), position);
				return;
			} else TaskManager.runAsync(() -> {
				try {
					skullOwnerFutures.add(future);
					inventory.setItem(future.get(5L, TimeUnit.SECONDS), position);
				} catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
					
				}
			}, 0L);
		} if (Environment.isBukkit() && inventory.getItem(icon.getPosition()) != null && inventory.getItem(icon.getPosition()).getType().equals(itemStack.getType()))
			new ItemStackAdapter(inventory.bukkitValue().getItem(icon.getPosition())).importData(itemStack);
		else inventory.setItem(itemStack, icon.getPosition());
	}
	
	@Override
	public boolean open(ChatPluginServerPlayer player, int page, boolean openActions) {
		if (page < 0 || page >= inventories.get(Language.getMainLanguage()).size())
			throw new IndexOutOfBoundsException("Specified page (" + page + ") is not present (there are " + inventories.get(Language.getMainLanguage()).size() + " pages");
		if (isLoaded()) {
			GUIOpenEvent event = new GUIOpenEvent(this, player, page, openActions);
			
			event.call();
			
			if (event.isCancelled())
				return false;
			if (this instanceof PerPlayer)
				((PerPlayer<T>) this).refreshUnloadTask();
			TaskManager.runSync(() -> {
				viewers.put(player, page);
				player.openInventory(getInventory(player.getLanguage(), page));
				Utils.setTitle(player, getTitle(player.getLanguage(), page), layout.getRows());
				
				if (openActions)
					layout.getOpenActions().perform(player, this);
			}, 0L);
			return true;
		} else player.sendTranslatedMessage("guis.still-loading", getTitle(player.getLanguage(), page));
		return false;
	}
	
	@Override
	public boolean handleClickEvent(ChatPluginServerPlayer player, ClickEventAdapter clickEvent, int page) {
		if (player.hasPermission("chatplugin.guis." + layout.getID())) {
			if (this instanceof PerPlayer)
				((PerPlayer<T>) this).refreshUnloadTask();
			Icon icon = null;
			
			try {
				icon = layout.getIcons().get(clickEvent.getSlot());
			} catch (IndexOutOfBoundsException e) {
				
			} if (icon == null)
				if (fillers.isEmpty()) {
					if (getLayout().getEmptyListIcon().getPosition() == clickEvent.getSlot())
						icon = getLayout().getEmptyListIcon();
				} else try {
					icon = generatedIcons.get(page).get(clickEvent.getSlot());
				} catch (IndexOutOfBoundsException e) {
					
				}
			if (icon != null) {
				boolean hidden = (icon.getID().equals("previous-page") && page == 0) || (icon.getID().equals("next-page") && page == generatedIcons.size() - 1);
				IconClickEvent event = new IconClickEvent(this, player, page, clickEvent, icon, hidden);
				
				event.call();
				
				if (icon.getPermission() == null || player.hasPermission(icon.getPermission())) {
					if (!hidden && event.shouldPerformActions()) {
						Icon icon2 = icon;
						
						TaskManager.runSync(() -> {
							if (!icon2.isKeepOpen())
								player.closeInventory();
							GUIManagerImpl.executeCommands(player, icon2.formatPlaceholders(
									icon2.getCommands().stream().map(command -> formatPagePlaceholders(command.replace("{viewer}", player.getName()), page)).collect(Collectors.toList()),
									this,
									player.getLanguage(),
									false
									), clickEvent.getClickType());
						}, 0L);
						player.playSound(layout.getClickSound());
					}
				} else player.sendTranslatedMessage("guis.no-permission-icon");
				return event.isCancelled();
			} EmptySlotClickEvent event = new EmptySlotClickEvent(this, player, page, clickEvent);
			
			event.call();
			return event.isCancelled();
		} else player.sendTranslatedMessage("guis.no-permission");
		return true;
	}
	
	public String formatPagePlaceholders(String input, int page) {
		return input
				.replace("{previous_page}", String.valueOf(page))
				.replace("{current_page}", String.valueOf(page + 1))
				.replace("{next_page}", String.valueOf(page + 2))
				.replace("{max_page}", String.valueOf(generatedIcons.size()));
	}
	
	@Override
	public boolean handleDragEvent(ChatPluginServerPlayer player, DragEventAdapter dragEvent, int page) {
		GUIDragEvent event = new GUIDragEvent(this, player, page, dragEvent);
		
		event.call();
		return event.isCancelled();
	}
	
	@Override
	public String getTitle(Language language, int page) {
		String title = layout.getTitle(language, true);
		return ChatColor.translate(formatPagePlaceholders(titlesTranslator == null ? title : titlesTranslator.apply(title, language, page), page));
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "FillableGUIImpl{", "}")
				.add("id=\"" + id + "\"")
				.add("loaded=" + loaded)
				.toString();
	}
	
	public static class PerPlayer<T> extends FillableGUIImpl<T> implements PerPlayerGUI {
		
		private ChatPluginServerPlayer player;
		private long unloadTaskID;
		
		protected PerPlayer(FillableGUILayout layout, ChatPluginServerPlayer player) {
			super(layout);
			id = id + "-" + player.getName();
			this.player = player;
			
			refreshUnloadTask();
		}
		
		@Deprecated
		@Override
		public void unload(boolean quit) {
			for (Entry<ChatPluginServerPlayer, Integer> viewer : new HashSet<>(viewers.entrySet())) {
				if (quit)
					viewer.getKey().sendTranslatedMessage("guis.player-went-offline", player.getName(), getTitle(viewer.getKey().getLanguage(), viewer.getValue().intValue()));
				else viewer.getKey().sendTranslatedMessage("guis.unloaded", getTitle(viewer.getKey().getLanguage(), viewer.getValue().intValue()), Utils.formatTime(GUIManager.getInstance().getPerPlayerGUIsUnloadTime(), viewer.getKey().getLanguage(), false, true));
				
				viewer.getKey().closeInventory();
			} skullOwnerFutures.forEach(future -> future.cancel(false));
			TaskManager.cancelSync(unloadTaskID);
			GUIManager.getInstance().getGUIs().remove(this);
		}
		
		@Override
		public ChatPluginServerPlayer getPlayer() {
			return player;
		}
		
		@Override
		public long getUnloadTaskID() {
			return unloadTaskID;
		}
		
		@Override
		public String toString() {
			return new StringJoiner(", ", "FillableGUIImpl.PerPlayer{", "}")
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
	
	public static class GeneratedIcon extends Icon {
		
		private GUIFiller<?> filler;
		
		@SuppressWarnings("deprecation")
		public GeneratedIcon(GUIFiller<?> filler, IconLayout iconLayout, int position) {
			super(
					"generated-icon-" + position,
					IconType.GENERATED,
					iconLayout.getMaterial(),
					iconLayout.getAmount(),
					iconLayout.getDamage(),
					iconLayout.isKeepOpen(),
					iconLayout.isGlowing(),
					position,
					iconLayout.getSkullOwner(),
					iconLayout.getSkullTextureURL(),
					iconLayout.getLeatherArmorColor() == null ? null : new Color(iconLayout.getLeatherArmorColor().getRGB(), true),
					iconLayout.getPermission(),
					new ArrayList<>(iconLayout.getCommands()),
					new ArrayList<>(iconLayout.getItemFlags()),
					new HashMap<>(iconLayout.getDisplayNames()),
					new HashMap<>(iconLayout.getLores()),
					new HashMap<>(iconLayout.getEnchantments())
					);
			this.filler = filler;
		}
		
		@Override
		public String formatPlaceholders(String input, GUI gui, Language language, boolean translateColors) {
			return super.formatPlaceholders(filler.formatPlaceholders(input, language), gui, language, translateColors);
		}
		
		@Override
		public List<String> formatPlaceholders(List<String> input, GUI gui, Language language, boolean translateColors) {
			return super.formatPlaceholders(filler.formatPlaceholders(input, language), gui, language, translateColors);
		}
		
		public GUIFiller<?> getFiller() {
			return filler;
		}
		
	}
	
}
