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

package me.remigio07.chatplugin.api.common.event.telegram;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.common.telegram.TelegramBot;

/**
 * Represents the event called just before the
 * {@link TelegramBot}'s status is changed.
 * 
 * @see TelegramBot#setStatus(String)
 */
public class StatusUpdateEvent implements ChatPluginEvent, CancellableEvent {
	
	private boolean cancelled;
	private String value;
	
	/**
	 * Constructs a new status update event.
	 * 
	 * @param value Status' value
	 */
	public StatusUpdateEvent(String value) {
		this.value = value;
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
	 * Gets the new status' value.
	 * 
	 * @return Status' value
	 */
	public String getValue() {
		return value;
	}
	
}
