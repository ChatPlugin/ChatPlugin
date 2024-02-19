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

import java.util.Arrays;
import java.util.List;

/**
 * Represents an icon's type.
 */
public enum IconType {
	
	/**
	 * Represents an icon automatically generated with a {@link FillableGUI}'s filling function.
	 * 
	 * @deprecated Internal use only.
	 */
	@Deprecated
	GENERATED,
	
	/**
	 * Represents a custom icon specified by the user.
	 */
	CUSTOM,
	
	/**
	 * Represents one of the {@link #PAGE_SWITCHER_ICONS_IDS}.
	 */
	PAGE_SWITCHER;
	
	/**
	 * List containing page switcher icons' IDs in {@link FillableGUI}s.
	 * 
	 * <p><strong>Content:</strong> ["previous-page", "next-page"]</p>
	 */
	public static final List<String> PAGE_SWITCHER_ICONS_IDS = Arrays.asList(
			"previous-page",
			"next-page"
			);
	
}
