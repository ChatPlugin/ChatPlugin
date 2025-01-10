/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
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

import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called after a player closes a {@link GUI} in a forced way or not.
 */
public class GUICloseEvent extends GUIEvent implements ChatPluginServerPlayerEvent {
	
	private ChatPluginServerPlayer player;
	
	/**
	 * Constructs a new GUI close event.
	 * 
	 * @param gui GUI involved
	 * @param player Player involved
	 */
	public GUICloseEvent(GUI gui, ChatPluginServerPlayer player) {
		super(gui);
		this.player = player;
	}
	
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
}
