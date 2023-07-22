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
import me.remigio07_.chatplugin.api.server.chat.ChatManager;

/**
 * Represents an event called before the chat gets globally (un)muted.
 * 
 * @see ChatManager#setChatMuted(boolean)
 */
public class ToggleChatMuteEvent implements CancellableEvent {
	
	private boolean cancelled, wasChatMuted;
	
	/**
	 * Constructs a new toggle chat mute event.
	 * 
	 * @param wasChatMuted Whether the chat was muted
	 */
	public ToggleChatMuteEvent(boolean wasChatMuted) {
		this.wasChatMuted = wasChatMuted;
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
	 * Checks if the chat was globally muted when this event was called.
	 * 
	 * @return Whether the chat was muted
	 */
	public boolean wasChatMuted() {
		return wasChatMuted;
	}
	
}
