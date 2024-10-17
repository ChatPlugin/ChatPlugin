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
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a {@link FillableGUI}'s slots filler.
 * 
 * <p>Implement the methods of this class to fully
 * customize {@link IconType#GENERATED} icons.</p>
 * 
 * @param <T> Filler's type
 */
public interface GUIFiller<T> {
	
	/**
	 * Gets the filler's instance.
	 * 
	 * @return Filler's instance
	 */
	public T getFiller();
	
	/**
	 * Gets the GUI containing the filler.
	 * 
	 * @return Filler's GUI
	 */
	public FillableGUI<T> getGUI();
	
	/**
	 * Translates an input string containing placeholders for the specified language.
	 * 
	 * <p>It returns <code>input</code> by default.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public default String formatPlaceholders(String input, Language language) {
		return input;
	}
	
	/**
	 * Translates an input string list containing placeholders for the specified language.
	 * 
	 * <p>It calls {@link #formatPlaceholders(String, Language)} for every element in the list by default.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public default List<String> formatPlaceholders(List<String> input, Language language) {
		return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
	}
	
	/**
	 * Gets this filler's icon's layout.
	 * 
	 * <p>Returns {@link #getGUI()}'s {@link FillableGUI#getLayout()}'s
	 * {@link FillableGUILayout#getIconsLayouts()}'s first element or
	 * {@link IconLayout#EMPTY_ICON_LAYOUT} if it is empty, by default.</p>
	 * 
	 * @return Filler's icon's layout
	 */
	public default IconLayout getIconLayout() {
		try {
			return getGUI().getLayout().getIconsLayouts().get(0);
		} catch (IndexOutOfBoundsException e) {
			return IconLayout.EMPTY_ICON_LAYOUT;
		}
	}
	
	/**
	 * Gets this filler's icon.
	 * 
	 * <p>Returns <code>icon</code> by default.</p>
	 * 
	 * <p>If you implement this method, make sure
	 * to return an icon with the same ID.</p>
	 * 
	 * @param icon Original icon
	 * @return Filler's icon
	 * @see Icon#Icon(Icon)
	 */
	public default Icon getIcon(Icon icon) {
		return icon;
	}
	
}
