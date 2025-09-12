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

import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.DragEventAdapter;

/**
 * Represents an event called when a player drags an item in a {@link GUI}.
 * 
 * @see SinglePageGUI#handleDragEvent(ChatPluginServerPlayer, DragEventAdapter)
 * @see FillableGUI#handleDragEvent(ChatPluginServerPlayer, DragEventAdapter, int)
 */
public class GUIDragEvent extends GUIInteractEvent {
	
	private DragEventAdapter dragEvent;
	
	/**
	 * Constructs a new GUI drag event.
	 * 
	 * @param gui GUI involved
	 * @param player Player involved
	 * @param page GUI's page, counting from 0
	 * @param dragEvent Drag event involved
	 */
	public GUIDragEvent(GUI gui, ChatPluginServerPlayer player, int page, DragEventAdapter dragEvent) {
		super(gui, player, page);
		this.dragEvent = dragEvent;
	}
	
	/**
	 * Gets the drag event involved with this event.
	 * 
	 * @return Drag event involved
	 */
	public DragEventAdapter getDragEvent() {
		return dragEvent;
	}
	
}
