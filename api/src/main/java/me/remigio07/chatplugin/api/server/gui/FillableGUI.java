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

package me.remigio07.chatplugin.api.server.gui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

import me.remigio07.chatplugin.api.common.util.TriFunction;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.event.gui.EmptySlotClickEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIDragEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIOpenEvent;
import me.remigio07.chatplugin.api.server.event.gui.IconClickEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.DragEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

/**
 * Represents a GUI fillable with {@link IconType#GENERATED} icons.
 * 
 * @param <T> Fillers' type
 */
public abstract class FillableGUI<T> extends GUI {
	
	protected List<GUIFiller<T>> fillers = new CopyOnWriteArrayList<>();
	protected List<List<Icon>> generatedIcons = new CopyOnWriteArrayList<>();
	protected Map<Language, List<InventoryAdapter>> inventories = new ConcurrentHashMap<>();
	protected Map<ChatPluginServerPlayer, Integer> viewers = new ConcurrentHashMap<>();
	protected TriFunction<String, Language, Integer, String> titlesTranslator;
	
	protected FillableGUI(GUILayout layout) {
		super(layout);
	}
	
	@Override
	public FillableGUILayout getLayout() {
		return (FillableGUILayout) layout;
	}
	
	/**
	 * Gets this GUI's fillers.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return GUI's fillers
	 */
	@NotNull
	public List<GUIFiller<T>> getFillers() {
		return fillers;
	}
	
	/**
	 * Gets a filler from this GUI.
	 * 
	 * @param filler Filler to get {@link GUIFiller#getFiller()}
	 * @return Loaded filler
	 */
	@Nullable(why = "Specified filler may not be loaded")
	@SuppressWarnings("unchecked")
	public GUIFiller<T> getFiller(@NotNull T filler) {
		for (GUIFiller<?> guiFiller : fillers)
			if (guiFiller.getFiller().equals(filler))
				return (GUIFiller<T>) guiFiller;
		return null;
	}
	
	/**
	 * Sets this GUI's fillers.
	 * 
	 * @param fillers GUI's fillers
	 * @param refresh Whether to {@link #refresh()} this GUI
	 */
	public void setFillers(@NotNull List<GUIFiller<T>> fillers, boolean refresh) {
		this.fillers = fillers;
		
		if (refresh)
			refresh();
	}
	
	/**
	 * Adds a filler to this GUI.
	 * 
	 * <p>Unlike {@link #addFiller(Object, BiFunction, boolean)},
	 * this method supports the GUI filler's full customization.</p>
	 * 
	 * @param filler Filler to add
	 * @param refresh Whether to {@link #refresh()} this GUI
	 */
	public void addFiller(@NotNull GUIFiller<T> filler, boolean refresh) {
		fillers.add(filler);
		
		if (refresh)
			refresh();
	}
	
	/**
	 * Adds a filler to this GUI.
	 * 
	 * <p>Unlike {@link #addFiller(GUIFiller, boolean)}, this method
	 * does not support the GUI filler's full customization.</p>
	 * 
	 * @param filler Filler to add ({@link GUIFiller#getFiller()})
	 * @param formatPlaceholdersFunction {@link GUIFiller#formatPlaceholders(String, Language)}'s function
	 * @param refresh Whether to {@link #refresh()} this GUI
	 * @see GUIFiller#getIconLayout() Default icon layout used
	 */
	public void addFiller(@NotNull T filler, BiFunction<String, Language, String> formatPlaceholdersFunction, boolean refresh) {
		FillableGUI<T> thisGUI = this;
		addFiller(new GUIFiller<T>() {
			
			@Override
			public T getFiller() {
				return filler;
			}
			
			@Override
			public FillableGUI<T> getGUI() {
				return thisGUI;
			}
			
			@Override
			public String formatPlaceholders(String input, Language language) {
				return formatPlaceholdersFunction.apply(input, language);
			}
			
		}, refresh);
	}
	
	/**
	 * Removes a filler from this GUI.
	 * 
	 * @param filler Filler to remove ({@link GUIFiller#getFiller()})
	 * @param refresh Whether to {@link #refresh()} this GUI
	 */
	public void removeFiller(@NotNull T filler, boolean refresh) {
		GUIFiller<T> guiFiller = getFiller(filler);
		
		if (guiFiller != null)
			fillers.remove(guiFiller);
		if (refresh)
			refresh();
	}
	
	/**
	 * Clears this GUI's fillers.
	 * 
	 * @param refresh Whether to {@link #refresh()} this GUI
	 */
	public void clearFillers(boolean refresh) {
		fillers.clear();
		
		if (refresh)
			refresh();
	}
	
	/**
	 * Gets this GUI's generated icons.
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * @return GUI's generated icons
	 */
	@NotNull
	public List<List<Icon>> getGeneratedIcons() {
		return generatedIcons;
	}
	
	/**
	 * Gets this GUI's generated icons for the specified page.
	 * 
	 * <p>Will return <code>null</code> if the page is not present.</p>
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * @param page GUI's page
	 * @return GUI's generated icons
	 */
	@Nullable(why = "Page may not be present")
	public List<Icon> getGeneratedIcons(int page) {
		try {
			return generatedIcons.get(page);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * Gets this GUI's inventories.
	 * 
	 * @return GUI's inventories
	 */
	@NotNull
	public Map<Language, List<InventoryAdapter>> getInventories() {
		return inventories;
	}
	
	/**
	 * Gets this GUI's inventories for the specified language.
	 * 
	 * @param language GUI's language
	 * @return GUI's inventories
	 */
	@NotNull
	public List<InventoryAdapter> getInventories(Language language) {
		return inventories.get(language);
	}
	
	/**
	 * Gets this GUI's inventory for the specified language and page.
	 * 
	 * <p>Will return <code>null</code> if the page is not present.</p>
	 * 
	 * @param language GUI's language
	 * @param page GUI's page
	 * @return GUI's inventory
	 */
	@Nullable(why = "Page may not be present")
	public InventoryAdapter getInventory(Language language, int page) {
		try {
			return getInventories(language).get(page);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	/**
	 * Gets this GUI's viewers.
	 * 
	 * <p>Do <em>not</em> modify the returned map.</p>
	 * 
	 * @return GUI's viewers
	 */
	public Map<ChatPluginServerPlayer, Integer> getViewers() {
		return viewers;
	}
	
	/**
	 * Gets this GUI's titles' translator function.
	 * 
	 * <p>Will return <code>null</code> if no title's translator has been set.</p>
	 * 
	 * <p>The GUI's title (a String) is set to
	 * {@link TriFunction#apply(Object, Object, Object)}'s result every
	 * time the GUI is loaded or a {@link GUI#refresh()} is performed.</p>
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link String} - the title to format with custom placeholders</li>
	 * 		<li>{@link Language} - the language used to translate the title</li>
	 * 		<li>{@link Integer} - the inventory's page whose title needs to be translated</li>
	 * 	</ol>
	 * 
	 * Titles' translators are useful to translate a GUI's placeholders, but you
	 * may not need them; the only two placeholders automatically translated - even
	 * without a title translator set - are "{current_page}" and "{max_page}".
	 * 
	 * @return GUI's titles' translator
	 */
	@Nullable(why = "Titles' translator may not have been set")
	public TriFunction<String, Language, Integer, String> getTitlesTranslator() {
		return titlesTranslator;
	}
	
	/**
	 * Sets this GUI's titles' translator function.
	 * 
	 * <p>You can specify <code>null</code> to remove the title's translator.</p>
	 * 
	 * <p>The GUI's title (a String) is set to
	 * {@link TriFunction#apply(Object, Object, Object)}'s result every
	 * time the GUI is loaded or a {@link GUI#refresh()} is performed.
	 * You may also want to {@link #refresh()} the GUI after this operation.</p>
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link String} - the title to format with custom placeholders</li>
	 * 		<li>{@link Language} - the language used to translate the title</li>
	 * 		<li>{@link Integer} - the inventory's page whose title needs to be translated</li>
	 * 	</ol>
	 * 
	 * Titles' translators are useful to translate a GUI's placeholders, but you
	 * may not need them; the only two placeholders automatically translated - even
	 * without a title translator set - are "{current_page}" and "{max_page}".
	 * 
	 * @param titlesTranslator GUI's titles' translator
	 * @return This GUI
	 */
	public FillableGUI<T> setTitlesTranslator(@Nullable(why = "Titles' translator is removed when null") TriFunction<String, Language, Integer, String> titlesTranslator) {
		this.titlesTranslator = titlesTranslator;
		return this;
	}
	
	/**
	 * Sets a titles' translator that simply returns
	 * {@link PlaceholderManager#translateServerPlaceholders(String, Language)}
	 * as its function's result.
	 * 
	 * @return This GUI
	 */
	public FillableGUI<T> setServerTitlesTranslator() {
		titlesTranslator = (t, u, v) -> PlaceholderManager.getInstance().translateServerPlaceholders(t, u);
		return this;
	}
	
	/**
	 * Opens this GUI to the specified player.
	 * 
	 * @param player Target player
	 * @param page GUI's page
	 * @param openActions Whether to perform {@link FillableGUILayout#getOpenActions()}
	 * @throws IndexOutOfBoundsException If the page is not present
	 * @return Whether the open event completed successfully, with no cancellation
	 * @see GUIOpenEvent
	 */
	public abstract boolean open(ChatPluginServerPlayer player, int page, boolean openActions);
	
	/**
	 * Handles and processes a click event.
	 * 
	 * 	<p>In order:
	 * 		<ol>
	 * 			<li>checks if {@link ChatPluginServerPlayer#hasPermission(String)} to use this GUI</li>
	 * 			<li>if the clicked icon is valid, checks if it requires a permission to be clicked</li>
	 * 			<li>if {@link IconClickEvent#shouldPerformActions()}, executes {@link Icon#getCommands()} and plays {@link GUILayout#getClickSound()}</li>
	 * 			<li>returns <code>true</code> to cancel the click event, unless <code>!</code>{@link EmptySlotClickEvent#isCancelled()} or <code>!</code>{@link IconClickEvent#isCancelled()}</li>
	 * 		</ol>
	 * 
	 * @param player Player involved
	 * @param clickEvent Click event to handle
	 * @param page Page involved
	 * @return Whether to cancel the click event
	 * @see EmptySlotClickEvent
	 * @see IconClickEvent
	 */
	public abstract boolean handleClickEvent(ChatPluginServerPlayer player, ClickEventAdapter clickEvent, int page);
	
	/**
	 * Handles and processes a drag event.
	 * 
	 * <p>Returns <code>true</code> to cancel the event by default,
	 * unless <code>!</code>{@link GUIDragEvent#isCancelled()}.</p>
	 * 
	 * @param player Player involved
	 * @param dragEvent Drag event to handle
	 * @param page Page involved
	 * @return Whether to cancel the drag event
	 * @see GUIDragEvent
	 */
	public abstract boolean handleDragEvent(ChatPluginServerPlayer player, DragEventAdapter dragEvent, int page);
	
	/**
	 * Gets this GUI's title using {@link #getTitlesTranslator()}.
	 * 
	 * @param language Language used to translate the title
	 * @param page Page involved
	 * @return Formatted title
	 */
	public abstract String getTitle(Language language, int page);
	
}
