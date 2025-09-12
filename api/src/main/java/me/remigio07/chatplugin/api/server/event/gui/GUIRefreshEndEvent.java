/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.api.server.event.gui;

import me.remigio07.chatplugin.api.server.gui.GUI;

/**
 * Represents an event called after a GUI has been refreshed.
 * 
 * @see GUI#refresh()
 */
public class GUIRefreshEndEvent extends GUIEvent {
	
	private int refreshTime;
	
	/**
	 * Constructs a new GUI refresh end event.
	 * 
	 * @param gui GUI involved
	 * @param refreshTime Time elapsed, in milliseconds
	 */
	public GUIRefreshEndEvent(GUI gui, int refreshTime) {
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
