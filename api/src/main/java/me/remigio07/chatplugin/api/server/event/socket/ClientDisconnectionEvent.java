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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.event.socket;

import me.remigio07.chatplugin.api.server.util.socket.Client;

/**
 * Represents an event called after a {@link Client} is disconnected from a server.
 * 
 * @see Client#disconnect()
 */
public class ClientDisconnectionEvent extends ClientEvent {
	
	/**
	 * Constructs a new client disconnection event.
	 * 
	 * @param client Client involved
	 */
	public ClientDisconnectionEvent(Client client) {
		super(client);
	}
	
}
