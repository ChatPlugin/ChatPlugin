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

package me.remigio07_.chatplugin.api.server.event.chat;

import me.remigio07_.chatplugin.api.common.event.CancellableEvent;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called when a message is sent to the staff chat.
 * 
 * @see StaffChatManager#sendPlayerMessage(ChatPluginServerPlayer, String)
 * @see StaffChatManager#sendConsoleMessage(String)
 */
public class StaffChatEvent extends ChatEvent implements CancellableEvent {
	
	private boolean cancelled;
	
	/**
	 * Constructs a new staff chat event.
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 */
	public StaffChatEvent(ChatPluginServerPlayer player, String message) {
		super(player, message);
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	/**
	 * Gets the player involved with this event.
	 * Will return <code>null</code> if the message has been sent by the console.
	 * 
	 * @return Player involved or <code>null</code> for console
	 */
	@Nullable(why = "Null if the message has been sent by the console")
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
}
