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

package me.remigio07.chatplugin.api.server.util.socket;

import me.remigio07.chatplugin.api.proxy.util.socket.ClientHandler;

/**
 * Represents the possible outcomes that may
 * occur when {@link Client#connect(String)} is called.
 */
public enum ConnectionOutcome {
	
	/**
	 * The connection was successful.
	 * The client and the server are ready to communicate.
	 */
	SUCCESS("Connection successful"),
	
	/**
	 * The client was already connected to the server.
	 * Nothing will happen; the client may communicate with the server.
	 */
	ALREADY_CONNECTED("The client is already connected to the server"),
	
	/**
	 * The specified ID was already in use by another client.
	 * The connection will be terminated and no events will be called.
	 */
	ID_ALREADY_IN_USE("The specified ID is already in use by another client"),
	
	/**
	 * The specified ID did not respect {@link ClientHandler#CLIENT_ID_PATTERN}.
	 * The connection will be terminated and no events will be called.
	 */
	INVALID_ID("The specified ID does not respect the following pattern: \"" + ClientHandler.CLIENT_ID_PATTERN.pattern() + "\""),
	
	/**
	 * The client was not able to reach the server within 5000ms.
	 * The connection will be terminated and no events will be called.
	 */
	TIMEOUT("Connection timed out");
	
	private String message;
	
	private ConnectionOutcome(String message) {
		this.message = message;
	}
	
	/**
	 * Gets this connection outcome's message.
	 * 
	 * @return Outcome's message
	 */
	public String getMessage() {
		return message;
	}
	
}
