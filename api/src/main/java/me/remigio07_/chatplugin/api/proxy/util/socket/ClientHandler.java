/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.api.proxy.util.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Pattern;

import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.api.common.util.packet.PacketSerializer;
import me.remigio07_.chatplugin.api.common.util.packet.Packets;
import me.remigio07_.chatplugin.api.proxy.event.socket.ClientConnectionEvent;
import me.remigio07_.chatplugin.api.proxy.event.socket.ClientDisconnectionEvent;
import me.remigio07_.chatplugin.api.proxy.event.socket.ServerReceivePacketEvent;

/**
 * Client socket handler. Obtain instances of this
 * class by listening to {@link ClientConnectionEvent}.
 */
public class ClientHandler extends Thread {
	
	/**
	 * Pattern representing the allowed client IDs.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,36}$"</p>
	 * 
	 * @see #isValidClientID(String)
	 */
	public static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	private Server server;
	private Socket socket;
	private String id, disconnectionReason;
	private DataInputStream input;
	private DataOutputStream output;
	
	ClientHandler(Server server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		input = new DataInputStream(socket.getInputStream());
		output = new DataOutputStream(socket.getOutputStream());
		
		new Thread(() -> {
			try {
				for (int i = 0; i < 49; i++) {
					if (id != null) {
						if (!isValidClientID(id)) {
							new PrintWriter(output, true).println("INVALID_ID");
							LogManager.log("[SOCKETS] Client {0} has just tried to connect using ID \"{1}\" but it does not respect the required pattern: \"{2}\".", 4, socket.getInetAddress().getHostAddress() + ":" + socket.getPort(), id, CLIENT_ID_PATTERN.pattern());
							socket.close();
							return;
						} for (ClientHandler clientHandler : server.getClientHandlers()) {
							if (clientHandler.getID().equals(id)) {
								new PrintWriter(output, true).println("ID_ALREADY_IN_USE");
								LogManager.log("[SOCKETS] Client {0} has just tried to connect using ID \"{1}\" but it was already in use by {2}.", 4, socket.getInetAddress().getHostAddress() + ":" + socket.getPort(), id, clientHandler.getSocket().getInetAddress().getHostAddress() + ":" + clientHandler.getSocket().getPort());
								socket.close();
								return;
							}
						} new PrintWriter(output, true).println("SUCCESS");
						server.getClientHandlers().add(this);
						new ClientConnectionEvent(this).call();
						LogManager.log("[SOCKETS] Client {0} has just connected using ID \"{1}\".", 4, socket.getInetAddress().getHostAddress() + ":" + socket.getPort(), id);
						start();
						return;
					} else try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						LogManager.log("[SOCKETS] The identification task for client {0} has been suddenly interrupted: {1}", 2, socket.getInetAddress().getHostAddress() + ":" + socket.getPort(), e.getMessage());
					}
				} LogManager.log("[SOCKETS] Client {0} did not send its ID within 5000ms so it was disconnected.", 4, socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
				socket.close();
			} catch (IOException e) {
				LogManager.log("[SOCKETS] IOException occurred while closing socket for client \"{0}\": {1}", 2, id, e.getMessage());
			}
		}).start();
		new Thread(() -> {
			try {
				id = new BufferedReader(new InputStreamReader(input)).readLine();
			} catch (Exception e) { // NPE || IOE
				// handled by the for loop
			}
		}).start();
	}
	
	/**
	 * Automatic packet listener.
	 * 
	 * @deprecated Internal use only; do not call this method.
	 * @see ServerReceivePacketEvent
	 */
	@Deprecated
	@Override
	public void run() {
		short bytesRead;
		
		try {
			while ((bytesRead = input.readShort()) != -1) {
				byte[] data = new byte[bytesRead];
				
				input.readFully(data);
				new ServerReceivePacketEvent(this, data).call();
			}
		} catch (SocketException | EOFException e) {
			// disconnection
		} catch (IOException e) {
			LogManager.log("[SOCKETS] IOException occurred while reading a packet received from client \"{0}\": {1}", 2, id, e.getMessage());
		} try {
			socket.close();
		} catch (IOException e) {
			LogManager.log("[SOCKETS] IOException occurred while closing socket for client \"{0}\"): {1}", 2, id, e.getMessage());
		} new ClientDisconnectionEvent(this).call();
		server.getClientHandlers().remove(this);
		LogManager.log("[SOCKETS] Client \"{0}\" has just disconnected from the server{1}", 4, id, disconnectionReason == null ? "." : ": " + disconnectionReason);
	}
	
	/**
	 * Sends a packet to this client handler.
	 * 
	 * @param packet Packet to send
	 * @param async Whether the call should be asynchronous
	 */
	public void sendPacket(PacketSerializer packet, boolean async) {
		if (async) {
			new Thread(() -> {
				sendPacket(packet, false);
			}).start();
			return;
		} byte[] data = packet.toArray();
		
		try {
			output.write(data.length);
			output.write(data);
		} catch (IOException e) {
			LogManager.log("[SOCKETS] IOException occurred while writing a packet to send to client \"{0}\": {1}", 2, id, e.getMessage());
		}
	}
	
	/**
	 * Manually disconnects this client from the server.
	 * 
	 * @param reason Disconnection's reason
	 * @throws IOException If something goes wrong
	 * @see ClientDisconnectionEvent
	 */
	@SuppressWarnings("deprecation")
	public void disconnect(String reason) throws IOException {
		disconnectionReason = reason;
		
		sendPacket(Packets.Misc.clientDisconnection(reason), false);
		socket.close();
	}
	
	/**
	 * Gets this client handler's server.
	 * 
	 * @return Client handler's server
	 */
	public Server getServer() {
		return server;
	}
	
	/**
	 * Gets this client handler's socket.
	 * 
	 * @return Client handler's socket
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Gets this client handler's ID.
	 * 
	 * @return Client handler's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this client handler's data input stream.
	 * 
	 * @return Client handler's input stream
	 */
	public DataInputStream getInput() {
		return input;
	}
	
	/**
	 * Gets this client handler's data output stream.
	 * 
	 * @return Client handler's output stream
	 */
	public DataOutputStream getOutput() {
		return output;
	}
	
	/**
	 * Checks if the specified String is a valid client ID.
	 * 
	 * @param clientID Client ID to check
	 * @return Whether the specified client ID is valid
	 * @see #CLIENT_ID_PATTERN
	 */
	public static boolean isValidClientID(String clientID) {
		return CLIENT_ID_PATTERN.matcher(clientID).matches();
	}
	
}
