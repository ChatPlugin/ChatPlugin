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

package me.remigio07.chatplugin.api.proxy.event.socket;

import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.proxy.util.socket.Server;

/**
 * Represents a {@link Server}-related event.
 */
public abstract class ServerEvent implements ChatPluginEvent {
	
	protected Server server;
	
	protected ServerEvent(Server server) {
		this.server = server;
	}
	
	/**
	 * Gets the server involved with this event.
	 * 
	 * @return Server involved
	 */
	public Server getServer() {
		return server;
	}
	
}
