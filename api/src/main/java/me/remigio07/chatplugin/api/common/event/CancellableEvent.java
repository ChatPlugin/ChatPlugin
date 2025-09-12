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

package me.remigio07.chatplugin.api.common.event;

/**
 * Represents a cancellable event.
 */
public interface CancellableEvent extends ChatPluginEvent {
	
	/**
	 * Checks if this event has been cancelled and will not be executed.
	 * 
	 * @return Whether this event is cancelled
	 */
	public boolean isCancelled();
	
	/**
	 * Sets the cancellation state of this event.
	 * 
	 * <p>It will be passed through other registered listeners
	 * even if it is cancelled, but it will not be executed.</p>
	 * 
	 * @param cancelled Whether this event should be cancelled
	 */
	public void setCancelled(boolean cancelled);
	
}
