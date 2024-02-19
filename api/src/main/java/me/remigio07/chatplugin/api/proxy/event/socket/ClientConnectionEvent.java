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

package me.remigio07.chatplugin.api.proxy.event.socket;

import me.remigio07.chatplugin.api.proxy.util.socket.ClientHandler;
import me.remigio07.chatplugin.api.proxy.util.socket.Server;

/**
 * Represents an event called after a {@link ClientHandler} is connected to a {@link Server}.
 * 
 * @see Server#run()
 */
public class ClientConnectionEvent extends ClientHandlerEvent {
	
	/**
	 * Constructs a new client connection event.
	 * 
	 * @param clientHandler Client handler involved
	 */
	public ClientConnectionEvent(ClientHandler clientHandler) {
		super(clientHandler);
	}
	
}
