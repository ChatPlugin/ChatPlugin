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

package me.remigio07_.chatplugin.api.server.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import me.remigio07_.chatplugin.api.common.util.TriFunction;
import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.server.event.gui.GUIClickEvent;
import me.remigio07_.chatplugin.api.server.event.gui.GUIOpenEvent;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07_.chatplugin.api.server.util.manager.PlaceholderManager;

/**
 * Represents a GUI fillable with {@link IconType#GENERATED} icons.
 * 
 * @param <T> Fillers' type
 */
public abstract class FillableGUI<T> extends GUI {
	
	protected List<GUIFiller<T>> fillers = new ArrayList<>();
	protected List<List<Icon>> generatedIcons = new ArrayList<>();
	protected Map<Language, List<InventoryAdapter>> inventories = new HashMap<>();
	protected Map<ChatPluginServerPlayer, Integer> viewers = new HashMap<>();
	protected TriFunction<FillableGUI<T>, Language, Integer, String> titlesTranslator;
	
	protected FillableGUI(GUILayout layout) {
		super(layout);
	}
	
	@Override
	public FillableGUILayout getLayout() {
		return (FillableGUILayout) layout;
	}
	
	/**
	 * Gets this GUI's fillers.
	 * You may modify the returned list.
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
	 * Unlike {@link #addFiller(Object, BiFunction, boolean)},
	 * this method supports the GUI filler's full customization.
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
	 * Unlike {@link #addFiller(GUIFiller, boolean)}, this method
	 * does not support the GUI filler's full customization.
	 * 
	 * @param filler Filler to add ({@link GUIFiller#getFiller()})
	 * @param formatPlaceholdersFunction {@link GUIFiller#formatPlaceholders(String, Language)}'s function
	 * @param refresh Whether to {@link #refresh()} this GUI
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
	 * Do not modify the returned list.
	 * 
	 * @return GUI's generated icons
	 */
	@NotNull
	public List<List<Icon>> getGeneratedIcons() {
		return generatedIcons;
	}
	
	/**
	 * Gets this GUI's generated icons for the specified page.
	 * Will return <code>null</code> if the page is not present.
	 * Do not modify the returned list.
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
	 * Will return <code>null</code> if the page is not present.
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
	 * Do not modify the returned map.
	 * 
	 * @return GUI's viewers
	 */
	public Map<ChatPluginServerPlayer, Integer> getViewers() {
		return viewers;
	}
	
	/**
	 * Gets this GUI's titles' translator function. The GUI's title (a String)
	 * is set to {@link TriFunction#apply(Object, Object, Object)}'s result
	 * every time the GUI is loaded or a {@link GUI#refresh()} is performed.
	 * Will return <code>null</code> if no title's translator has been set.
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link FillableGUI} - this GUI's instance</li>
	 * 		<li>{@link Language} - the language used to translate the title</li>
	 * 		<li>{@link Integer} - the inventory's page whose title needs to be translated</li>
	 * 	</ol>
	 * </p>
	 * 
	 * Titles' translators are useful to translate a GUI's placeholders, but you
	 * may not need them; the only two placeholders automatically translated - even
	 * without a title translator set - are "{current_page}" and "{max_page}".
	 * 
	 * @return GUI's titles' translator
	 */
	@Nullable(why = "Titles' translator may not have been set")
	public TriFunction<FillableGUI<T>, Language, Integer, String> getTitlesTranslator() {
		return titlesTranslator;
	}
	
	/**
	 * Sets this GUI's titles' translator function. The GUI's title (a String)
	 * is set to {@link TriFunction#apply(Object, Object, Object)}'s result
	 * every time the GUI is loaded or a {@link GUI#refresh()} is performed.
	 * You can specify <code>null</code> to remove the title's translator.
	 * You may also want to {@link #refresh()} the GUI after this operation.
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link FillableGUI} - this GUI's instance</li>
	 * 		<li>{@link Language} - the language used to translate the title</li>
	 * 		<li>{@link Integer} - the inventory's page whose title needs to be translated</li>
	 * 	</ol>
	 * </p>
	 * 
	 * Titles' translators are useful to translate a GUI's placeholders, but you
	 * may not need them; the only two placeholders automatically translated - even
	 * without a title translator set - are "{current_page}" and "{max_page}".
	 * 
	 * @param titlesTranslator GUI's titles' translator
	 * @return This GUI
	 */
	public FillableGUI<T> setTitlesTranslator(@Nullable(why = "Titles' translator is removed when null") TriFunction<FillableGUI<T>, Language, Integer, String> titlesTranslator) {
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
		titlesTranslator = (t, u, v) -> PlaceholderManager.getInstance().translateServerPlaceholders(t.getLayout().getTitle(u, true), u);
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
	 * @param player Player involved
	 * @param page Page involved
	 * @param slot Slot involved
	 * @return Whether the click event completed successfully, with no cancellation
	 * @see GUIClickEvent
	 */
	public abstract boolean handleClickEvent(ChatPluginServerPlayer player, int page, int slot);
	
}
