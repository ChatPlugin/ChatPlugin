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

package me.remigio07.chatplugin.api.server.gui;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Represents a GUI's layout.
 * 
 * @see SinglePageGUILayout
 * @see FillableGUILayout
 */
public class GUILayout {
	
	protected String id;
	protected int rows;
	protected Configuration configuration;
	protected OpenActions openActions;
	protected SoundAdapter clickSound;
	protected Map<Language, String> titles;
	protected List<Icon> icons;
	
	protected GUILayout(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
		if (!GUIManager.getInstance().isValidGUIID(id))
			throw new IllegalArgumentException("GUI ID \"" + id + "\" does not respect the following pattern: \"" + GUIManager.GUI_ID_PATTERN.pattern() + "\"");
		if (rows < 1 || rows > 6)
			throw new IndexOutOfBoundsException("Specified GUI's rows (" + rows + ") are not inside of range 1-6");
		if (titles.get(Language.getMainLanguage()) == null)
			throw new IllegalArgumentException("Specified titles' map does not contain a translation for the main language");
		this.id = id;
		this.rows = rows;
		this.openActions = openActions;
		this.clickSound = clickSound;
		this.titles = titles;
		icons = new CopyOnWriteArrayList<>(Collections.nCopies(getSize(), null));
	}
	
	protected GUILayout(Configuration configuration) {
		this(
				configuration.getPath().getFileName().toString().substring(0, configuration.getPath().getFileName().toString().lastIndexOf('.')),
				configuration.getInt("settings.rows"),
				new OpenActions(configuration),
				new SoundAdapter(configuration, "settings.click-sound"),
				LanguageManager.getInstance().getLanguages().stream().collect(ConcurrentHashMap::new, (map, language) -> map.put(language, configuration.getString("settings.titles." + language.getID(), null)), ConcurrentHashMap::putAll)
				);
		this.configuration = configuration;
	}
	
	/**
	 * Gets this GUI layout's ID.
	 * 
	 * @return GUI's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this GUI layout's rows.
	 * 
	 * @return GUI layout's rows
	 * @see #getSize()
	 */
	public int getRows() {
		return rows;
	}
	
	/**
	 * Gets this GUI layout's size.
	 * 
	 * @return GUI layout's size
	 * @see #getRows()
	 */
	public int getSize() {
		return rows * 9;
	}
	
	/**
	 * Gets the configuration associated with this GUI layout.
	 * 
	 * <p>Will return <code>null</code> if this GUI layout was <em>not</em>
	 * created using {@link GUIManager#createGUILayout(Configuration)}.</p>
	 * 
	 * @return Associated configuration
	 */
	@Nullable(why = "Will return null if this GUI layout was not created using GUIManager#createGUILayout(Configuration)")
	public Configuration getConfiguration() {
		return configuration;
	}
	
	/**
	 * Gets this GUI layout's open actions.
	 * 
	 * @return GUI layout's open actions
	 */
	public OpenActions getOpenActions() {
		return openActions;
	}
	
	/**
	 * Gets this GUI layout's click sound.
	 * 
	 * @return GUI layout's click sound
	 */
	public SoundAdapter getClickSound() {
		return clickSound;
	}
	
	/**
	 * Gets this GUI layout's titles.
	 * 
	 * @return GUI layout's titles
	 */
	public Map<Language, String> getTitles() {
		return titles;
	}
	
	/**
	 * Gets this GUI layout's title for the specified language.
	 * 
	 * <p>Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s title if no title is present for the specified language.
	 * Will return <code>null</code> if {@link #getTitles()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.</p>
	 * 
	 * @param language Language used to translate the title
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return GUI layout's title
	 */
	@Nullable(why = "No title may be present for the specified language")
	public String getTitle(Language language, boolean avoidNull) {
		return titles.get(language) == null ? avoidNull ? titles.get(Language.getMainLanguage()) : null : titles.get(language);
	}
	
	/**
	 * Gets this GUI layout's icons.
	 * 
	 * <p>When created, this list contains {@link #getSize()}
	 * <code>null</code> elements, then the {@link Icon}
	 * instances are set at their {@link Icon#getPosition()}s.</p>
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return GUI's icons
	 */
	public List<Icon> getIcons() {
		return icons;
	}
	
	/**
	 * Represents the builder used to create {@link GUILayout}s.
	 * 
	 * @see SinglePageGUILayout.Builder
	 * @see FillableGUILayout.Builder
	 */
	public static abstract class Builder {
		
		protected GUILayout layout;
		
		/**
		 * Sets one of this builder's icons.
		 * 
		 * @param icon Icon to set
		 * @return This builder
		 * @throws IndexOutOfBoundsException If {@link Icon#getPosition()} is invalid
		 * @throws IllegalArgumentException If {@link Icon#getID()} is already in use
		 * by another icon at a different {@link Icon#getPosition()}
		 */
		public GUILayout.Builder setIcon(Icon icon) {
			for (Icon otherIcon : layout.getIcons())
				if (otherIcon != null && otherIcon.getID().equals(icon.getID()) && otherIcon.getPosition() != icon.getPosition())
					throw new IllegalArgumentException("Icon's ID (" + icon.getID() + ") is already in use by icon at position " + otherIcon.getPosition());
			try {
				layout.getIcons().set(icon.getPosition(), icon);
			} catch (IndexOutOfBoundsException e) {
				throw new IndexOutOfBoundsException("Icon's position (" + icon.getPosition() + ") is outside of range 0 - " + (layout.getSize() - 1));
			} return this;
		}
		
		/**
		 * Removes one of this builder's icons.
		 * 
		 * @param position Icon's position [0 - ({@link InventoryAdapter#getSize()} - 1)]
		 * @return This builder
		 * @throws IndexOutOfBoundsException If <code>position</code> is invalid
		 */
		public GUILayout.Builder removeIcon(int position) {
			try {
				layout.getIcons().set(position, null);
			} catch (IndexOutOfBoundsException e) {
				throw new IndexOutOfBoundsException("Icon's position (" + position + ") is outside of range 0 - " + (layout.getSize() - 1));
			} return this;
		}
		
	}
	
}
