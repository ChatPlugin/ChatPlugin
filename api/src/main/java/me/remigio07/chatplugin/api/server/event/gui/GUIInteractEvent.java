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

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called when a player interacts with a slot in a {@link GUI}.
 */
public abstract class GUIInteractEvent extends GUIEvent implements CancellableEvent, ChatPluginServerPlayerEvent {
	
	protected boolean cancelled = true;
	protected ChatPluginServerPlayer player;
	protected int page;
	
	protected GUIInteractEvent(GUI gui, ChatPluginServerPlayer player, int page) {
		super(gui);
		this.player = player;
		this.page = page;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the page containing the clicked slot.
	 * 
	 * <p>Will return 0 for {@link SinglePageGUI}s.</p>
	 * 
	 * @return GUI's page, counting from 0
	 */
	public int getPage() {
		return page;
	}
	
}
