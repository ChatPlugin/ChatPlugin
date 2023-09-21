/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.event;

/**
 * Represents a ChatPlugin's event.
 * 
 * @see EventManager
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
