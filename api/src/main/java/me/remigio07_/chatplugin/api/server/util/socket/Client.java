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

package me.remigio07_.chatplugin.api.server.util.socket;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.api.common.util.packet.PacketDeserializer;
import me.remigio07_.chatplugin.api.common.util.packet.PacketSerializer;
import me.remigio07_.chatplugin.api.server.event.socket.ClientConnectionEvent;
import me.remigio07_.chatplugin.api.server.event.socket.ClientDisconnectionEvent;
import me.remigio07_.chatplugin.api.server.event.socket.ClientReceivePacketEvent;

/**
 * Client socket. Obtain instances of this class
 * with {@link #Client(InetAddress, int)}.
 */
public class Client {
	
	private InetAddress serverAddress;
	private int serverPort, byteRead;
	private Socket socket;
	private String id, disconnectionReason;
	private DataInputStream input;
	private DataOutputStream output;
	private ConnectionOutcome temp;
	
	/**
	 * Constructs a new client socket.
	 * Call {@link #connect(String)} to connect it.
	 * 
	 * @param serverAddress Server's address
	 * @param serverPort Server's port [0 - 65535]
	 */
	public Client(InetAddress serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}
	
	/**
	 * Connects this client to its server using the given ID.
	 * 
	 * @param id Client's ID
	 * @return Connection's outcome
	 * @throws IOException If something goes wrong
	 * @see ClientConnectionEvent
	 */
	public ConnectionOutcome connect(String id) throws IOException {
		if (isConnected())
			return ConnectionOutcome.ALREADY_CONNECTED;
		Socket socket = new Socket(serverAddress, serverPort);
		
		new PrintWriter(socket.getOutputStream(), true).println(id);
		LogManager.log("[SOCKETS] Connection accepted for client \"{0}\"; waiting for the server to validate the ID...", 4, id);
		new Thread(() -> {
			try {
				temp = ConnectionOutcome.valueOf(new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine());
			} catch (Exception e) { // NPE || IOE || IAE
				// handled by the for loop
			}
		}).start();
		
		for (int i = 0; i < 49; i++) {
			if (temp == null) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException e) {
					LogManager.log("[SOCKETS] The identification task for client \"{0}\" has been suddenly interrupted: {1}", 2, id, e.getMessage());
				}
			} else {
				if (temp == ConnectionOutcome.SUCCESS) {
					this.socket = socket;
					this.id = id;
					input = new DataInputStream(socket.getInputStream());
					output = new DataOutputStream(socket.getOutputStream());
					new Thread(() -> run()).start();
					new ClientConnectionEvent(this).call();
					LogManager.log("[SOCKETS] Client \"{0}\" has just connected to the server using address {1}.", 4, id, socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
				} else {
					socket.close();
					LogManager.log("[SOCKETS] Client \"{0}\" has just tried to connect but the {1}.", 2, id, temp.getMessage());
				} ConnectionOutcome temp2 = temp;
				temp = null;
				return temp2;
			}
		} socket.close();
		LogManager.log("[SOCKETS] Client \"{0}\" did not receive a response within 5000ms so it was disconnected.", 4, id);
		return ConnectionOutcome.TIMEOUT;
	}
	
	/**
	 * Manually disconnects this client from the server.
	 * 
	 * @throws IOException If something goes wrong
	 * @see ClientDisconnectionEvent
	 */
	public void disconnect() throws IOException {
		if (!isConnected())
			return;
		output.writeShort(-1);
		socket.close();
		LogManager.log("[SOCKETS] Client \"{0}\" has just disconnected from the server", 4, id);
	}
	
	/**
	 * Automatic packet listener.
	 * 
	 * @deprecated Internal use only; do not call this method.
	 * @see ClientReceivePacketEvent
	 */
	@Deprecated
	public void run() {
		try {
			while ((byteRead = input.read()) != -1) {
				byte[] data = new byte[byteRead];
				
				input.readFully(data);
				new ClientReceivePacketEvent(this, data).call();
				
				PacketDeserializer packet = new PacketDeserializer(data);
				
				if (packet.readUTF().equals("ClientDisconnection"))
					disconnectionReason = packet.readUTF();
			}
		} catch (SocketException e) {
			// disconnection
		} catch (IOException e) {
			LogManager.log("[SOCKETS] IOException occurred while reading a packet for client \"{0}\": {1}", 2, id, e.getMessage());
		} try {
			socket.close();
		} catch (IOException e) {
			LogManager.log("[SOCKETS] IOException occurred while closing socket for client \"{0}\": {1}", 2, id, e.getMessage());
		} new ClientDisconnectionEvent(this).call();
		LogManager.log("[SOCKETS] Client \"{0}\" has just disconnected from the server{1}", 4, id, disconnectionReason == null ? "." : ": " + disconnectionReason);
		
		socket = null;
		id = null;
		input = null;
		output = null;
	}
	
	/**
	 * Sends a packet to the server.
	 * Will do nothing if <code>!</code>{@link #isConnected()}.
	 * 
	 * @param packet Packet to send
	 * @param async Whether the call should be asynchronous
	 */
	public void sendPacket(PacketSerializer packet, boolean async) {
		if (!isConnected())
			return;
		if (async) {
			new Thread(() -> sendPacket(packet, false)).start();
			return;
		} byte[] data = packet.toArray();
		
		try {
			output.writeShort(data.length);
			output.write(data);
		} catch (IOException e) {
			LogManager.log("[SOCKETS] IOException occurred while writing a packet for client \"{0}\": {1}", 2, id, e.getMessage());
		}
	}
	
	/**
	 * Checks if this client is currently
	 * connected to the server.
	 * 
	 * @return Whether this client is connected
	 */
	public boolean isConnected() {
		return socket != null;
	}
	
	/**
	 * Gets this client's server's address.
	 * 
	 * @return Server's address
	 */
	public InetAddress getServerAddress() {
		return serverAddress;
	}
	
	/**
	 * Gets this client's server's port.
	 * 
	 * @return Server's port
	 */
	public int getServerPort() {
		return serverPort;
	}
	
	/**
	 * Gets this client's socket.
	 * Will return <code>null</code> if
	 * <code>!</code>{@link #isConnected()}.
	 * 
	 * @return Client's socket
	 */
	@Nullable(why = "Client may not be connected")
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Gets this client's ID.
	 * Will return <code>null</code> if
	 * <code>!</code>{@link #isConnected()}.
	 * 
	 * @return Client's ID
	 */
	@Nullable(why = "Client may not be connected")
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this client's data input stream.
	 * Will return <code>null</code> if
	 * <code>!</code>{@link #isConnected()}.
	 * 
	 * @return Client's input stream
	 */
	@Nullable(why = "Client may not be connected")
	public DataInputStream getInput() {
		return input;
	}
	
	/**
	 * Gets this client's data output stream.
	 * Will return <code>null</code> if
	 * <code>!</code>{@link #isConnected()}.
	 * 
	 * @return Client's output stream
	 */
	@Nullable(why = "Client may not be connected")
	public DataOutputStream getOutput() {
		return output;
	}
	
}
