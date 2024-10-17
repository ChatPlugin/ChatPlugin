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

import me.remigio07.chatplugin.api.server.event.gui.EmptySlotClickEvent;
import me.remigio07.chatplugin.api.server.event.gui.GUIOpenEvent;
import me.remigio07.chatplugin.api.server.event.gui.IconClickEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a GUI obtained through {@link GUIManager#createPerPlayerGUI(GUILayout, ChatPluginServerPlayer)}
 * that features a player and a task ID that unloads the GUI after {@link GUIManager#getPerPlayerGUIsUnloadTime()}
 * of inactivity ({@link GUIOpenEvent}, {@link EmptySlotClickEvent}, {@link IconClickEvent}).
 */
public interface PerPlayerGUI {
	
	/**
	 * Gets this GUI's player.
	 * 
	 * @return GUI's player
	 */
	public ChatPluginServerPlayer getPlayer();
	
	/**
	 * Gets this GUI's unload task's ID.
	 * 
	 * @return GUI's unload task's ID
	 */
	public long getUnloadTaskID();
	
	/**
	 * Manually unloads this GUI and removes
	 * it from {@link GUIManager#getGUIs()}.
	 * 
	 * @deprecated Internal use only.
	 * @param quit Whether the player quit the server
	 */
	@Deprecated
	public void unload(boolean quit);
	
}
