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

import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter;

/**
 * Represents an event called when a player clicks an empty slot in a {@link GUI}.
 * 
 * @see SinglePageGUI#handleClickEvent(ChatPluginServerPlayer, ClickEventAdapter)
 * @see FillableGUI#handleClickEvent(ChatPluginServerPlayer, ClickEventAdapter, int)
 */
public class EmptySlotClickEvent extends GUIInteractEvent {
	
	private ClickEventAdapter clickEvent;
	
	/**
	 * Constructs a new GUI click event.
	 * 
	 * @param gui GUI involved
	 * @param player Player involved
	 * @param page GUI's page, counting from 0
	 * @param clickEvent Click event involved
	 */
	public EmptySlotClickEvent(GUI gui, ChatPluginServerPlayer player, int page, ClickEventAdapter clickEvent) {
		super(gui, player, page);
		this.clickEvent = clickEvent;
	}
	
	/**
	 * Gets the click event involved with this event.
	 * 
	 * @return Click event involved
	 */
	public ClickEventAdapter getClickEvent() {
		return clickEvent;
	}
	
}
