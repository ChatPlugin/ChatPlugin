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

package me.remigio07.chatplugin.api.proxy.event.socket;

import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.proxy.util.socket.ClientHandler;

/**
 * Represents a {@link ClientHandler}-related event.
 */
public abstract class ClientHandlerEvent implements ChatPluginEvent {
	
	protected ClientHandler clientHandler;
	
	protected ClientHandlerEvent(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}
	
	/**
	 * Gets the client handler involved with this event.
	 * 
	 * @return Client handler involved
	 */
	public ClientHandler getClientHandler() {
		return clientHandler;
	}
	
}
