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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import me.remigio07.chatplugin.api.common.util.TriFunction;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.event.gui.GUIClickEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIRefreshEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

/**
 * Represents a GUI handled by the {@link GUIManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/GUIs">ChatPlugin wiki/Modules/GUIs</a>
 * @see SinglePageGUI
 * @see FillableGUI
 */
public abstract class GUI {
	
	protected String id;
	protected boolean loaded;
	protected GUILayout layout;
	protected List<CompletableFuture<ItemStackAdapter>> skullOwnerFutures = new ArrayList<>();
	private TriFunction<Icon, String, Language, String> stringPlaceholdersTranslator;
	private TriFunction<Icon, List<String>, Language, List<String>> stringListPlaceholdersTranslator;
	
	protected GUI(GUILayout layout) {
		this.id = layout.getID();
		this.layout = layout;
	}
	
	/**
	 * Gets this GUI's ID.
	 * 
	 * @return GUI's ID
	 */
	public synchronized String getID() {
		return id;
	}
	
	/**
	 * Sets this GUI's ID.
	 * 
	 * @param id GUI's ID
	 * @return This GUI
	 * @throws IllegalArgumentException If specified ID <code>!</code>{@link GUIManager#isValidGUIID(String)}
	 * or this GUI is loaded into {@link GUIManager#getGUIs()} and the ID is already in use
	 */
	public synchronized GUI setID(String id) {
		if (!GUIManager.getInstance().isValidGUIID(id))
			throw new IllegalArgumentException("GUI ID \"" + id + "\" is invalid as it does not respect the following pattern: \"" + GUIManager.GUI_ID_PATTERN.pattern() + "\"");
		if (GUIManager.getInstance().getGUI(id) != null)
			throw new IllegalArgumentException("Specified ID (" + id + ") is already in use");
		this.id = id;
		return this;
	}
	
	/**
	 * Checks if this GUI is loaded and
	 * is ready to be shown to players.
	 * 
	 * @return Whether this GUI is loaded
	 */
	public synchronized boolean isLoaded() {
		return loaded;
	}
	
	/**
	 * Gets this GUI's layout.
	 * 
	 * @return GUI's layout
	 */
	@NotNull
	public GUILayout getLayout() {
		return layout;
	}
	
	/**
	 * Refreshes this GUI.
	 * 
	 * @return Time elapsed in milliseconds or 0 if not loaded
	 * @see GUIRefreshEvent
	 */
	public synchronized int refresh() {
		if (isLoaded()) {
			int ms = load();
			
			new GUIRefreshEvent(this, ms).call();
			return ms;
		} return 0;
	}
	
	/**
	 * Gets this GUI's string placeholders' translator function.
	 * 
	 * <p>Will return <code>null</code> if no placeholders' translator has been set.</p>
	 * 
	 * <p>The required value (a <code>String</code>) is set to
	 * {@link TriFunction#apply(Object, Object, Object)}'s result every time
	 * {@link Icon#toItemStackAdapter(GUI, Language)} is called or a {@link GUIClickEvent} occurs.</p>
	 * 
	 * <p>It translates the following values:
	 * 	<ul>
	 * 		<li>{@link Icon#getDisplayNames()}</li>
	 * 		<li>{@link Icon#getSkullOwner()}</li>
	 * 		<li>{@link Icon#getSkullTextureURL()}</li>
	 * 	</ul>
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link Icon} - this icon's instance</li>
	 * 		<li>{@link String} - the text to format with custom placeholders</li>
	 * 		<li>{@link Language} - the language used to translate the value</li>
	 * 	</ol>
	 * 
	 * If {@link #getStringPlaceholdersTranslator()}<code> != null</code> it will be also used to translate string lists when
	 * {@link #getStringListPlaceholdersTranslator()}<code> == null</code>, passing every element to the function's method.
	 * 
	 * @return GUI's placeholders' translator
	 */
	@Nullable(why = "Placeholders' translator may not have been set")
	public TriFunction<Icon, String, Language, String> getStringPlaceholdersTranslator() {
		return stringPlaceholdersTranslator;
	}
	
	/**
	 * Sets this GUI's string placeholders' translator function.
	 * 
	 * <p>You can specify <code>null</code> to remove the placeholders' translator.</p>
	 * 
	 * <p>The required value (a <code>String</code>) is set to
	 * {@link TriFunction#apply(Object, Object, Object)}'s result every time
	 * {@link Icon#toItemStackAdapter(GUI, Language)} is called or a {@link GUIClickEvent} occurs.
	 * You may also want to {@link #refresh()} the GUI after this operation.</p>
	 * 
	 * <p>It translates the following values:
	 * 	<ul>
	 * 		<li>{@link Icon#getDisplayNames()}</li>
	 * 		<li>{@link Icon#getSkullOwner()}</li>
	 * 		<li>{@link Icon#getSkullTextureURL()}</li>
	 * 	</ul>
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link Icon} - this icon's instance</li>
	 * 		<li>{@link String} - the text to format with custom placeholders</li>
	 * 		<li>{@link Language} - the language used to translate the value</li>
	 * 	</ol>
	 * 
	 * If {@link #getStringPlaceholdersTranslator()}<code> != null</code> it will be also used to translate string lists when
	 * {@link #getStringListPlaceholdersTranslator()}<code> == null</code>, passing every element to the function's method.
	 * 
	 * @param stringPlaceholdersTranslator GUI's placeholders' translator
	 * @return This GUI
	 */
	public GUI setStringPlaceholdersTranslator(TriFunction<Icon, String, Language, String> stringPlaceholdersTranslator) {
		this.stringPlaceholdersTranslator = stringPlaceholdersTranslator;
		return this;
	}
	
	/**
	 * Sets a string placeholders' translator that simply returns
	 * {@link PlaceholderManager#translateServerPlaceholders(String, Language)}
	 * as its function's result.
	 * 
	 * @return This GUI
	 */
	public GUI setServerStringPlaceholdersTranslator() {
		stringPlaceholdersTranslator = (t, u, v) -> PlaceholderManager.getInstance().translateServerPlaceholders(u, v);
		return this;
	}
	
	/**
	 * Gets this GUI's string list placeholders' translator function.
	 * 
	 * <p>Will return <code>null</code> if no placeholders' translator has been set.</p>
	 * 
	 * <p>The required value (a <code>List&lt;String&gt;</code>) is set to
	 * {@link TriFunction#apply(Object, Object, Object)}'s result every time
	 * {@link Icon#toItemStackAdapter(GUI, Language)} is called or a {@link GUIClickEvent} occurs.</p>
	 * 
	 * <p>It translates the following values:
	 * 	<ul>
	 * 		<li>{@link Icon#getLores()}</li>
	 * 		<li>{@link Icon#getCommands()}</li>
	 * 	</ul>
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link Icon} - this icon's instance</li>
	 * 		<li>{@link List}<code>&lt;{@link String}&gt;</code> - the text to format with custom placeholders</li>
	 * 		<li>{@link Language} - the language used to translate the value</li>
	 * 	</ol>
	 * 
	 * If {@link #getStringPlaceholdersTranslator()}<code> != null</code> it will be also used to translate string lists when
	 * {@link #getStringListPlaceholdersTranslator()}<code> == null</code>, passing every element to the function's method.
	 * 
	 * @return GUI's placeholders' translator
	 */
	@Nullable(why = "Placeholders' translator may not have been set")
	public TriFunction<Icon, List<String>, Language, List<String>> getStringListPlaceholdersTranslator() {
		return stringListPlaceholdersTranslator;
	}
	
	/**
	 * Sets this GUI's string list placeholders' translator function.
	 * 
	 * <p>You can specify <code>null</code> to remove the placeholders' translator.</p>
	 * 
	 * <p>The required value (a <code>List&lt;String&gt;</code>) is set to
	 * {@link TriFunction#apply(Object, Object, Object)}'s result every time
	 * {@link Icon#toItemStackAdapter(GUI, Language)} is called or a {@link GUIClickEvent} occurs.
	 * You may also want to {@link #refresh()} the GUI after this operation.</p>
	 * 
	 * <p>It translates the following values:
	 * 	<ul>
	 * 		<li>{@link Icon#getLores()}</li>
	 * 		<li>{@link Icon#getCommands()}</li>
	 * 	</ul>
	 * 
	 * <p>The function is composed of the following arguments:
	 * 	<ol>
	 * 		<li>{@link Icon} - this icon's instance</li>
	 * 		<li>{@link List}<code>&lt;{@link String}&gt;</code> - the text to format with custom placeholders</li>
	 * 		<li>{@link Language} - the language used to translate the value</li>
	 * 	</ol>
	 * 
	 * If {@link #getStringPlaceholdersTranslator()}<code> != null</code> it will be also used to translate string lists when
	 * {@link #getStringListPlaceholdersTranslator()}<code> == null</code>, passing every element to the function's method.
	 * 
	 * @param stringListPlaceholdersTranslator GUI's placeholders' translator
	 * @return This GUI
	 */
	public GUI setStringListPlaceholdersTranslator(TriFunction<Icon, List<String>, Language, List<String>> stringListPlaceholdersTranslator) {
		this.stringListPlaceholdersTranslator = stringListPlaceholdersTranslator;
		return this;
	}
	
	protected abstract int load();
	
}
