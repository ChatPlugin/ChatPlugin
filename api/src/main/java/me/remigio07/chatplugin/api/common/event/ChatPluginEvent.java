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

package me.remigio07.chatplugin.api.common.event;

/**
 * Represents a ChatPlugin's event handled by the {@link EventManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/development/API#events">ChatPlugin wiki/Development/API/Events</a>
 */
public interface ChatPluginEvent {
	
	/**
	 * Calls this event and passes it to its subscribers
	 * using {@link EventManager#call(ChatPluginEvent)}.
	 */
	public default void call() {
		EventManager.getInstance().call(this);
	}
	
}
