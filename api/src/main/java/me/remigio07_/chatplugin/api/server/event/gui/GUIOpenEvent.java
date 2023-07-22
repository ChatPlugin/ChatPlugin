/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.api.server.event.gui;

import me.remigio07_.chatplugin.api.common.event.CancellableEvent;
import me.remigio07_.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07_.chatplugin.api.server.gui.FillableGUI;
import me.remigio07_.chatplugin.api.server.gui.GUI;
import me.remigio07_.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called just before a {@link GUI} is opened to a player.
 * 
 * @see SinglePageGUI#open(ChatPluginServerPlayer, boolean)
 * @see FillableGUI#open(ChatPluginServerPlayer, int, boolean)
 */
public class GUIOpenEvent extends GUIEvent implements CancellableEvent, ChatPluginServerPlayerEvent {
	
	private boolean cancelled, openActions;
	private ChatPluginServerPlayer player;
	private int page;
	
	/**
	 * Constructs a new GUI open event.
	 * 
	 * @param gui GUI involved
	 * @param player Player involved
	 * @param page Page involved
	 * @param openActions Whether open actions have been performed
	 */
	public GUIOpenEvent(GUI gui, ChatPluginServerPlayer player, int page, boolean openActions) {
		super(gui);
		this.player = player;
		this.page = page;
		this.openActions = openActions;
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
	 * Gets the page opened to the player.
	 * Will return 0 for {@link SinglePageGUI}s.
	 * 
	 * @return GUI's page
	 */
	public int getPage() {
		return page;
	}
	
	/**
	 * Checks if open actions have been performed while opening.
	 * 
	 * @return Whether open actions have been performed
	 */
	public boolean isOpenActions() {
		return openActions;
	}
	
}
