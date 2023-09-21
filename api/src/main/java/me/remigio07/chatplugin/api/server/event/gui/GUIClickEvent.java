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

package me.remigio07.chatplugin.api.server.event.gui;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called when a player clicks a slot in a {@link GUI}.
 * 
 * @see SinglePageGUI#handleClickEvent(ChatPluginServerPlayer, int)
 * @see FillableGUI#handleClickEvent(ChatPluginServerPlayer, int, int)
 */
public class GUIClickEvent extends GUIEvent implements CancellableEvent, ChatPluginServerPlayerEvent {
	
	private boolean cancelled;
	private ChatPluginServerPlayer player;
	private Icon icon;
	private int page;
	
	/**
	 * Constructs a new GUI click event.
	 * 
	 * @param gui GUI involved
	 * @param player Player involved
	 * @param icon Icon involved
	 * @param page Page involved
	 */
	public GUIClickEvent(GUI gui, ChatPluginServerPlayer player, Icon icon, int page) {
		super(gui);
		this.player = player;
		this.icon = icon;
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
	 * Gets the clicked icon.
	 * 
	 * @return Clicked icon
	 */
	public Icon getIcon() {
		return icon;
	}
	
	/**
	 * Gets the page containing {@link #getIcon()}.
	 * Will return 0 for {@link SinglePageGUI}s.
	 * 
	 * @return GUI's page
	 */
	public int getPage() {
		return page;
	}
	
}
