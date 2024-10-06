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
import me.remigio07.chatplugin.api.server.gui.GUILayout;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.gui.IconType;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.ClickEventAdapter.ClickTypeAdapter;

/**
 * Represents an event called when a player clicks an {@link Icon} in a {@link GUI}.
 * 
 * @see SinglePageGUI#handleClickEvent(ChatPluginServerPlayer, ClickEventAdapter)
 * @see FillableGUI#handleClickEvent(ChatPluginServerPlayer, ClickEventAdapter, int)
 */
public class IconClickEvent extends EmptySlotClickEvent {
	
	private Icon icon;
	private boolean hidden, performActions;
	
	/**
	 * Constructs a new icon click event.
	 * 
	 * @param gui GUI involved
	 * @param player Player involved
	 * @param page GUI's page, counting from 0
	 * @param clickEvent Click event involved
	 * @param icon Clicked icon
	 * @param hidden Whether the icon is hidden in the specified page
	 */
	public IconClickEvent(GUI gui, ChatPluginServerPlayer player, int page, ClickEventAdapter clickEvent, Icon icon, boolean hidden) {
		super(gui, player, page, clickEvent);
		this.icon = icon;
		this.hidden = hidden;
		performActions = clickEvent.getClickType() != ClickTypeAdapter.DOUBLE_CLICK;
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
	 * Checks if {@link #getIcon()} is a {@link IconType#PAGE_SWITCHER}
	 * and it is hidden because it is in the first or last page.
	 * 
	 * @return Whether the icon is hidden in the specified page
	 */
	public boolean isHidden() {
		return hidden;
	}
	
	/**
	 * Checks if {@link Icon#getCommands()} should be executed,
	 * {@link GUILayout#getClickSound()} played and {@link #getPlayer()}'s
	 * inventory closed if <code>!</code>{@link Icon#isKeepOpen()}.
	 * 
	 * <p>{@link Icon#getPermission()} is required in any case, if present.</p>
	 * 
	 * @return Whether to execute commands, play click sound and possibly close inventory
	 */
	public boolean shouldPerformActions() {
		return performActions;
	}
	
	/**
	 * Sets if {@link Icon#getCommands()} should be executed,
	 * {@link GUILayout#getClickSound()} played and {@link #getPlayer()}'s
	 * inventory closed if <code>!</code>{@link Icon#isKeepOpen()}.
	 * 
	 * <p>{@link Icon#getPermission()} is required in any case, if present.</p>
	 * 
	 * @param performActions Whether to execute commands, play click sound and possibly close inventory
	 */
	public void setPerformActions(boolean performActions) {
		this.performActions = performActions;
	}
	
}
