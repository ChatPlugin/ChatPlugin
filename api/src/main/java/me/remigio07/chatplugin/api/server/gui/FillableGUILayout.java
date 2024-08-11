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
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Represents a fillable GUI's layout.
 */
public class FillableGUILayout extends GUILayout {
	
	protected int startSlot = -1, endSlot = -1;
	protected List<IconLayout> iconsLayouts = new CopyOnWriteArrayList<>();
	protected Icon emptyListIcon;
	
	protected FillableGUILayout(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
		super(id, rows, openActions, clickSound, titles);
	}
	
	protected FillableGUILayout(Configuration configuration) {
		super(configuration);
	}
	
	/**
	 * Gets the filling function's start slot's position in this GUI layout.
	 * 
	 * @return Filling function's start slot, inclusive
	 */
	public int getStartSlot() {
		return startSlot;
	}
	
	/**
	 * Sets the filling function's start slot's position in this GUI layout.
	 * 
	 * @param startSlot Filling function's start slot, inclusive [0 - ({@link #getSize()} - 1)]
	 * @throws IllegalArgumentException If the position is outside of
	 * valid range or <code>startSlot &gt; </code>{@link #getEndSlot()}.
	 */
	public void setStartSlot(int startSlot) {
		if (startSlot < 0 || startSlot > getSize() - 1 || (endSlot != -1 && startSlot > endSlot))
			throw new IllegalArgumentException("Specified start slot's position (" + startSlot + ") is invalid as it is outside of range 0 - " + (getSize() - 1) + " or bigger than end slot's position (" + endSlot + ")");
		this.startSlot = startSlot;
	}
	
	/**
	 * Gets the filling function's end slot's position in this GUI layout.
	 * 
	 * @return Filling function's end slot, inclusive
	 */
	public int getEndSlot() {
		return endSlot;
	}
	
	/**
	 * Sets the filling function's end slot's position in this GUI layout.
	 * 
	 * @param endSlot Filling function's end slot, inclusive [0 - ({@link #getSize()} - 1)]
	 * @throws IllegalArgumentException If the position is outside of
	 * valid range or <code>startSlot &gt; </code>{@link #getEndSlot()}.
	 */
	public void setEndSlot(int endSlot) {
		if (endSlot < 0 || endSlot > getSize() - 1 || (startSlot != -1 && startSlot > endSlot))
			throw new IllegalArgumentException("Specified start slot's position (" + startSlot + ") is invalid as it is outside of range 0 - " + (getSize() - 1) + " or bigger than end slot's position (" + endSlot + ")");
		this.endSlot = endSlot;
	}
	
	/**
	 * Gets the filling function's icons' layouts.
	 * 
	 * <p>Do <strong>not</strong> modify the returned list.
	 * Use {@link #setIconLayout(IconLayout)} and
	 * {@link #removeIconLayout(String)} to interact with it.</p>
	 * 
	 * @return Filler icons' layouts
	 */
	@NotNull
	public List<IconLayout> getIconsLayouts() {
		return iconsLayouts;
	}
	
	/**
	 * Gets an icon layout from {@link #getIconsLayouts()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the icon layout is not loaded.</p>
	 * 
	 * @param id Icon layout's ID
	 * @return Loaded icon layout
	 */
	@Nullable(why = "Specified icon layout may not be loaded")
	public IconLayout getIconLayout(@NotNull String id) {
		for (IconLayout iconLayout : iconsLayouts)
			if (iconLayout.getID().equals(id))
				return iconLayout;
		return null;
	}
	
	/**
	 * Sets one of the filling function's filler icons' layouts.
	 * 
	 * @param iconLayout Icon layout to set
	 */
	public void setIconLayout(@NotNull IconLayout iconLayout) {
		int size = iconsLayouts.size();
		
		if (size == 0)
			iconsLayouts.add(iconLayout);
		else for (int i = 0; i < size; i++)
			if (iconsLayouts.get(i).getID().equals(iconLayout.getID()))
				iconsLayouts.set(i, iconLayout);
			else iconsLayouts.add(iconLayout);
	}
	
	/**
	 * Removes one of the filling function's filler icons' layouts.
	 * 
	 * @param id Icon layout's ID
	 */
	public void removeIconLayout(@NotNull String id) {
		for (IconLayout iconLayout : new ArrayList<>(iconsLayouts))
			if (iconLayout.getID().equals(id))
				iconsLayouts.remove(iconLayout);
	}
	
	/**
	 * Gets the icon displayed when {@link FillableGUI#getFillers()} is empty.
	 * 
	 * @return Empty fillers list icon
	 */
	@NotNull
	public Icon getEmptyListIcon() {
		return emptyListIcon;
	}
	
	/**
	 * Sets the icon displayed when {@link FillableGUI#getFillers()} is empty.
	 * 
	 * @param emptyListIcon Empty fillers list icon
	 */
	public void setEmptyListIcon(@NotNull Icon emptyListIcon) {
		this.emptyListIcon = emptyListIcon;
	}
	
	/**
	 * Represents the builder used to create {@link FillableGUILayout}s using
	 * {@link GUIManager#createFillableGUILayoutBuilder(String, int, OpenActions, SoundAdapter, Map)}.
	 */
	public static abstract class Builder extends GUILayout.Builder {
		
		/**
		 * Sets the filling function's start and end slots' positions in the GUI layout.
		 * 
		 * @param startSlot Filling function's start slot [0 - ({@link GUILayout#getSize()} -1)]
		 * @param endSlot Filling function's end slot [0 - ({@link GUILayout#getSize()} -1)]
		 * @return This builder
		 * @throws IllegalArgumentException If a position is outside of
		 * valid range or <code>startSlot &gt; endSlot</code>.
		 */
		public abstract Builder setSlots(int startSlot, int endSlot);
		
		/**
		 * Sets one of this builder's icons' layouts.
		 * 
		 * @param iconLayout Icon layout to set
		 * @return This builder
		 */
		public abstract Builder setIconLayout(IconLayout iconLayout);
		
		/**
		 * Sets the icon displayed when {@link FillableGUI#getFillers()} is empty.
		 * 
		 * @param emptyListIcon Empty fillers list icon
		 * @return This builder
		 */
		public abstract Builder setEmptyListIcon(Icon emptyListIcon);
		
		/**
		 * Builds the GUI layout.
		 * 
		 * @return New GUI layout
		 * @throws IllegalStateException If one of the following has not been called yet:
		 * 	<ul>
		 * 		<li>{@link #setSlots(int, int)}</li>
		 * 		<li>{@link #setIconLayout(IconLayout)}</li>
		 * 		<li>{@link #setEmptyListIcon(Icon)}</li>
		 * 	</ul>
		 * or not all {@link IconType#PAGE_SWITCHER_ICONS_IDS} have been specified ({@link #setIcon(Icon)})
		 * @throws IndexOutOfBoundsException If {@link IconType#PAGE_SWITCHER} icons' positions are
		 * inside of range {@link FillableGUILayout#getStartSlot()} - {@link FillableGUILayout#getEndSlot()}
		 */
		public abstract FillableGUILayout build();
		
	}
	
}
