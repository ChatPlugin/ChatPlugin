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

package me.remigio07_.chatplugin.api.common.event.plugin;

import me.remigio07_.chatplugin.api.ChatPlugin;
import me.remigio07_.chatplugin.api.common.event.CancellableEvent;

/**
 * Represents the event called when ChatPlugin crashes during a reload.
 * Hopefully this event gets never fired, but if it does, you can easily cancel it using {@link #setCancelled(boolean)}.
 * 
 * @see ChatPlugin#reload()
 */
public class ChatPluginCrashEvent implements CancellableEvent {
	
	private boolean cancelled;
	private String message;
	
	/**
	 * Constructs a new crash event.
	 * 
	 * @param message Crash message
	 */
	public ChatPluginCrashEvent(String message) {
		this.message = message;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		if (cancelled)
			System.out.println("Yeah, good try... A plugin tried to cancel ChatPluginCrashEvent.");
	}
	
	/**
	 * Gets the reason why the plugin crashed.
	 * 
	 * @return Crash message
	 */
	public String getMessage() {
		return message;
	}
	
}
