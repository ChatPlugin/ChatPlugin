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

package me.remigio07.chatplugin.api.server.event.socket;

import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.server.util.socket.Client;

/**
 * Represents a {@link Client}-related event.
 */
public abstract class ClientEvent implements ChatPluginEvent {
	
	protected Client client;
	
	protected ClientEvent(Client client) {
		this.client = client;
	}
	
	/**
	 * Gets the client involved with this event.
	 * 
	 * @return Client involved
	 */
	public Client getClient() {
		return client;
	}
	
}
