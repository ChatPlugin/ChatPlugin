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

package me.remigio07.chatplugin.api.server.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.event.gui.GUIClickEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIOpenEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

/**
 * Represents a single-paged GUI.
 */
public abstract class SinglePageGUI extends GUI {
	
	protected List<ChatPluginServerPlayer> viewers = new ArrayList<>();
	protected Map<Language, InventoryAdapter> inventories = new HashMap<>();
	protected BiFunction<SinglePageGUI, Language, String> titlesTranslator;
	
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
	 * 		<li>{@link SinglePageGUI} - this GUI's instance</li>
	 * 		<li>{@link Language} - the language used to translate the title</li>
	 * 	</ol>
	 * 
	 * @return GUI's titles' translator
	 */
	@Nullable(why = "Titles' translator may not have been set")
	public BiFunction<SinglePageGUI, Language, String> getTitlesTranslator() {
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
	 * 		<li>{@link SinglePageGUI} - this GUI's instance</li>
	 * 		<li>{@link Language} - the language used to translate the title</li>
	 * 	</ol>
	 * 
	 * @param titlesTranslator GUI's titles' translator
	 * @return This GUI
	 */
	public SinglePageGUI setTitlesTranslator(@Nullable(why = "Titles' translator is removed when null") BiFunction<SinglePageGUI, Language, String> titlesTranslator) {
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
		titlesTranslator = (t, u) -> PlaceholderManager.getInstance().translateServerPlaceholders(t.getLayout().getTitle(u, true), u);
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
	 * <p>Will do nothing if the slot is invalid.</p>
	 * 
	 * @param player Player involved
	 * @param slot Slot involved
	 * @return Whether the click event completed successfully, with no cancellation
	 * @see GUIClickEvent
	 */
	public abstract boolean handleClickEvent(ChatPluginServerPlayer player, int slot);
	
}
