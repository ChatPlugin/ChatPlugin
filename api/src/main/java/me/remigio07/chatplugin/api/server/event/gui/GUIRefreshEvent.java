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

package me.remigio07.chatplugin.api.server.event.gui;

import me.remigio07.chatplugin.api.server.gui.GUI;

/**
 * Represents the event called after a GUI is refreshed.
 * 
 * @see GUI#refresh()
 */
public class GUIRefreshEvent extends GUIEvent {
	
	private int refreshTime;
	
	/**
	 * Constructs a new GUI refresh event.
	 * 
	 * @param gui GUI involved
	 * @param refreshTime Time elapsed, in milliseconds
	 */
	public GUIRefreshEvent(GUI gui, int refreshTime) {
		super(gui);
		this.refreshTime = refreshTime;
	}
	
	/**
	 * Gets the time spent refreshing the GUI.
	 * 
	 * @return Time elapsed, in milliseconds
	 */
	public int getRefreshTime() {
		return refreshTime;
	}
	
}
