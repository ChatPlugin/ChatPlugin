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

package me.remigio07.chatplugin.api.server.gui;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

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
 * Represents a single-paged GUI.
 */
public abstract class SinglePageGUI extends GUI {
	
	protected List<ChatPluginServerPlayer> viewers = new CopyOnWriteArrayList<>();
	protected Map<Language, InventoryAdapter> inventories = new ConcurrentHashMap<>();
	protected BiFunction<String, Language, String> titlesTranslator;
	
	protected SinglePageGUI(GUILayout layout) {
		super(layout);
	}
	
	@Override
	public SinglePageGUILayout getLayout() {
		return (SinglePageGUILayout) layout;
	}
	
	/**
	 * Gets this GUI's viewers.
	 * 
	 * @return GUI's viewers
	 */
	public List<ChatPluginServerPlayer> getViewers() {
		return viewers;
	}
	
	/**
	 * Gets this GUI's inventories.
	 * 
	 * @return GUI's inventories
	 */
	public Map<Language, InventoryAdapter> getInventories() {
		return inventories;
	}
	
	/**
	 * Gets this GUI's inventory for the specified language.
	 * 
	 * @param language GUI's language
	 * @return GUI's inventory
	 */
	public InventoryAdapter getInventory(Language language) {
		return inventories.get(language);
	}
	
	/**
	 * Gets this GUI's titles' translator function.
	 * 
	 * <p>Will return <code>null</code> if no title's translator has been set.</p>
	 * 
	 * <p>The GUI's title (a String) is set to
	 * {@link BiFunction#apply(Object, Object)}'s result every time
	 * the GUI is loaded or a {@link GUI#refresh()} is performed.</p>
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link String} - the title to format with custom placeholders</li>
	 * 		<li>{@link Language} - the language used to translate the title</li>
	 * 	</ol>
	 * 
	 * @return GUI's titles' translator
	 */
	@Nullable(why = "Titles' translator may not have been set")
	public BiFunction<String, Language, String> getTitlesTranslator() {
		return titlesTranslator;
	}
	
	/**
	 * Sets this GUI's titles' translator function.
	 * 
	 * <p>You can specify <code>null</code> to remove the title's translator.</p>
	 * 
	 * <p>The GUI's title (a String) is set to
	 * {@link BiFunction#apply(Object, Object)}'s result every time
	 * the GUI is loaded or a {@link GUI#refresh()} is performed.
	 * You may also want to {@link #refresh()} the GUI after this operation.</p>
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link String} - the title to format with custom placeholders</li>
	 * 		<li>{@link Language} - the language used to translate the title</li>
	 * 	</ol>
	 * 
	 * @param titlesTranslator GUI's titles' translator
	 * @return This GUI
	 */
	public SinglePageGUI setTitlesTranslator(@Nullable(why = "Titles' translator is removed when null") BiFunction<String, Language, String> titlesTranslator) {
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
	public SinglePageGUI setServerTitlesTranslator() {
		titlesTranslator = (t, u) -> PlaceholderManager.getInstance().translateServerPlaceholders(t, u);
		return this;
	}
	
	/**
	 * Opens this GUI to the specified player.
	 * 
	 * @param player Target player
	 * @param openActions Whether to perform {@link SinglePageGUILayout#getOpenActions()}
	 * @return Whether the open event completed successfully, with no cancellation
	 * @see GUIOpenEvent
	 */
	public abstract boolean open(ChatPluginServerPlayer player, boolean openActions);
	
	/**
	 * Handles and processes a click event.
	 * 
	 * <p>In order:
	 * 	<ol>
	 * 		<li>checks if {@link ChatPluginServerPlayer#hasPermission(String)} to use this GUI</li>
	 * 		<li>if the clicked icon is valid, checks if it requires a permission to be clicked</li>
	 * 		<li>if {@link IconClickEvent#shouldPerformActions()}, executes {@link Icon#getCommands()} and plays {@link GUILayout#getClickSound()}</li>
	 * 		<li>returns <code>true</code> to cancel the click event, unless <code>!</code>{@link EmptySlotClickEvent#isCancelled()} or <code>!</code>{@link IconClickEvent#isCancelled()}</li>
	 * 	</ol>
	 * 
	 * @param player Player involved
	 * @param clickEvent Click event to handle
	 * @return Whether to cancel the click event
	 * @see EmptySlotClickEvent
	 * @see IconClickEvent
	 */
	public abstract boolean handleClickEvent(ChatPluginServerPlayer player, ClickEventAdapter clickEvent);
	
	/**
	 * Handles and processes a drag event.
	 * 
	 * <p>Returns <code>true</code> to cancel the event by default,
	 * unless <code>!</code>{@link GUIDragEvent#isCancelled()}.</p>
	 * 
	 * @param player Player involved
	 * @param dragEvent Drag event to handle
	 * @return Whether to cancel the drag event
	 * @see GUIDragEvent
	 */
	public abstract boolean handleDragEvent(ChatPluginServerPlayer player, DragEventAdapter dragEvent);
	
	/**
	 * Gets this GUI's title using {@link #getTitlesTranslator()}.
	 * 
	 * @param language Language used to translate the title
	 * @return Formatted title
	 */
	public abstract String getTitle(Language language);
	
}
