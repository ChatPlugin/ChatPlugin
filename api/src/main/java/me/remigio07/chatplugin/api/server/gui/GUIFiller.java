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

import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a {@link FillableGUI}'s slots filler.
 * Implement the methods of this class to fully
 * customize {@link IconType#GENERATED} icons.
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
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public String formatPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string list containing placeholders for the specified language.
	 * It calls {@link #formatPlaceholders(String, Language)} for every element in the list by default.
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
	 * Returns {@link #getGUI()}'s {@link FillableGUI#getLayout()}'s
	 * {@link FillableGUILayout#getIconsLayouts()}'s first element by default.
	 * 
	 * @return Filler's icon's layout
	 */
	public default IconLayout getIconLayout() {
		return getGUI().getLayout().getIconsLayouts().get(0);
	}
	
	/**
	 * Gets this filler's icon.
	 * Returns <code>icon</code> by default.
	 * If you implement this method, make sure to
	 * return an icon with the same ID.
	 * 
	 * @param icon Original icon
	 * @return Filler's icon
	 * @see Icon#Icon(Icon)
	 */
	public default Icon getIcon(Icon icon) {
		return icon;
	}
	
}
