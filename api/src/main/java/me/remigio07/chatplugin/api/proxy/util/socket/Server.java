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

package me.remigio07.chatplugin.api.proxy.util.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.proxy.event.socket.ClientConnectionEvent;
import me.remigio07.chatplugin.api.proxy.event.socket.ServerShutdownEvent;
import me.remigio07.chatplugin.api.proxy.event.socket.ServerStartupEvent;

/**
 * Server socket. Obtain instances of this class
 * with {@link #Server(InetAddress, int)}.
 */
public class Server extends Thread {
	
	private ServerSocket socket;
	private List<ClientHandler> clientHandlers = new ArrayList<>();
	
	/**
	 * Constructs a new server socket.
	 * 
	 * <p>Call {@link #start()} to start it up.</p>
	 * 
	 * @param address Server's address
	 * @param port Port's number [0 - 65535]
	 * @throws IOException If something goes wrong
	 * @throws IllegalArgumentException If port is outside of range [0 - 65535]
	 */
	public Server(InetAddress address, int port) throws IOException {
		socket = new ServerSocket(port, 50, address);
	}
	
	/**
	 * Starts this server.
	 * 
	 * <p>Will do nothing if {@link Socket#isClosed()}.</p>
	 * 
	 * @see ServerStartupEvent
	 */
	@Override
	public synchronized void start() {
		if (socket.isClosed())
			return;
		LogManager.log("[SOCKETS] New server started up; listening on {0}...", 4, socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
		super.start();
		new ServerStartupEvent(this).call();
	}
	
	/**
	 * Automatic client listener.
	 * 
	 * @deprecated Internal use only; do not call this method.
	 * @see ClientConnectionEvent
	 */
	@Deprecated
	@Override
	public void run() {
		try {
			while (true) {
				Socket client = socket.accept();
				
				LogManager.log("[SOCKETS] Connection accepted from client {0}; waiting for the ID to identify it...", 4, client.getInetAddress().getHostAddress() + ":" + client.getPort());
				new ClientHandler(this, client);
			}
		} catch (SocketException e) {
			// shutdown
		} catch (IOException e) {
			LogManager.log("[SOCKETS] IOException occurred while waiting for new connections on {0}: {1}", 2, socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort(), e.getMessage());
		}
	}
	
	/**
	 * Shuts down this server and closes its socket.
	 * 
	 * <p>Will do nothing if {@link Socket#isClosed()}.</p>
	 * 
	 * @throws IOException If something goes wrong
	 * @see ServerShutdownEvent
	 */
	public void shutdown() throws IOException {
		if (socket.isClosed())
			return;
		LogManager.log("[SOCKETS] Server shut down; closing {0} connections...", 4, clientHandlers.size());
		new ServerShutdownEvent(this).call();
		
		for (ClientHandler clientHandler : clientHandlers)
			clientHandler.disconnect("Server shutdown");
		socket.close();
	}
	
	/**
	 * Gets the socket associated with this server.
	 * 
	 * @return Server's socket
	 */
	public ServerSocket getSocket() {
		return socket;
	}
	
	/**
	 * Gets the list of the connected client handlers.
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * @return Client handlers' list
	 */
	public List<ClientHandler> getClientHandlers() {
		return clientHandlers;
	}
	
}
