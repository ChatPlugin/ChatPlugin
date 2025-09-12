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

package me.remigio07.chatplugin.api.common.event.plugin;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.CancellableEvent;

/**
 * Represents the event called when ChatPlugin crashes during a reload.
 * 
 * <p>Hopefully this event gets never fired, but if it does, you
 * can easily cancel it using {@link #setCancelled(boolean)}.</p>
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
			System.out.println("Yeah, nice try... A plugin tried to cancel ChatPluginCrashEvent.");
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
