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

package me.remigio07.chatplugin.api.server.util.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.packet.PacketDeserializer;
import me.remigio07.chatplugin.api.common.util.packet.PacketSerializer;
import me.remigio07.chatplugin.api.server.event.socket.ClientConnectionEvent;
import me.remigio07.chatplugin.api.server.event.socket.ClientDisconnectionEvent;
import me.remigio07.chatplugin.api.server.event.socket.ClientReceivePacketEvent;

/**
 * Client socket.
 * 
 * <p>Obtain instances of this class with
 * {@link #Client(InetAddress, int)}.</p>
 */
public class Client {
	
	private InetAddress serverAddress;
	private int serverPort;
	private Socket socket;
	private String id, disconnectionReason;
	private DataInputStream input;
	private DataOutputStream output;
	private ConnectionOutcome temp;
	
	/**
	 * Constructs a new client socket.
	 * 
	 * <p>Call {@link #connect(String)} to connect it.</p>
	 * 
	 * @param serverAddress Server's address
	 * @param serverPort Server's port [0 - 65535]
	 * @throws IllegalArgumentException If port is outside of range [0 - 65535]
	 */
	public Client(InetAddress serverAddress, int serverPort) {
		if (serverPort < 0 || serverPort > 0xFFFF)
			throw new IllegalArgumentException("Port value out of range: " + serverPort);
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}
	
	/**
	 * Connects this client to its server using the given ID.
	 * 
	 * @param id Client's ID
	 * @return Connection's outcome
	 * @throws IOException If something goes wrong
	 * @throws InterruptedException If the identification task gets interrupted
	 * @see ClientConnectionEvent
	 */
	public ConnectionOutcome connect(String id) throws IOException, InterruptedException {
		if (isConnected())
			return ConnectionOutcome.ALREADY_CONNECTED;
		Socket socket = new Socket(serverAddress, serverPort);
		DataInputStream tempInput = new DataInputStream(socket.getInputStream());
		DataOutputStream tempOutput = new DataOutputStream(socket.getOutputStream());
		
		tempOutput.writeUTF(id);
		LogManager.log("[SOCKETS] Connection accepted for client \"{0}\"; waiting for the server to validate the ID...", 4, id);
		new Thread(() -> {
			try {
				temp = ConnectionOutcome.valueOf(tempInput.readUTF());
			} catch (Exception e) { // NPE | IOE | IAE
				// handled by the for loop
			}
		}).start();
		
		for (int i = 0; i < 49; i++) {
			if (temp == null) {
				try {
					Thread.sleep(100L);
				} catch (InterruptedException ie) {
					LogManager.log("[SOCKETS] The identification task for client \"{0}\" has been suddenly interrupted: {1}", 2, id, ie.getLocalizedMessage());
					socket.close();
					tempInput.close();
					tempOutput.close();
					throw ie;
				}
			} else {
				if (temp == ConnectionOutcome.SUCCESS) {
					this.socket = socket;
					this.id = id;
					input = tempInput;
					output = tempOutput;
					new Thread(() -> run()).start();
					new ClientConnectionEvent(this).call();
					LogManager.log("[SOCKETS] Client \"{0}\" has just connected to the server using address {1}.", 4, id, socket.getInetAddress().getHostAddress() + ":" + socket.getLocalPort());
				} else {
					socket.close();
					tempInput.close();
					tempOutput.close();
					LogManager.log("[SOCKETS] Client \"{0}\" has just tried to connect but the {1}.", 2, id, temp.getMessage());
				} ConnectionOutcome temp2 = temp;
				temp = null;
				return temp2;
			}
		} socket.close();
		tempInput.close();
		tempOutput.close();
		LogManager.log("[SOCKETS] Client \"{0}\" did not receive a response within 5000 ms so it was disconnected.", 4, id);
		return ConnectionOutcome.TIMEOUT;
	}
	
	/**
	 * Manually disconnects this client from the server.
	 * 
	 * @throws IOException If something goes wrong
	 * @see ClientDisconnectionEvent
	 */
	public void disconnect() throws IOException { // TODO: implement reason here, too (see ClientHandler)
		if (!isConnected())
			return;
		output.writeShort(-1);
		socket.close();
	}
	
	/**
	 * Automatic packet listener.
	 * 
	 * @deprecated Internal use only; do not call this method.
	 * @see ClientReceivePacketEvent
	 */
	@Deprecated
	public void run() {
		short bytesRead;
		
		try {
			synchronized (input) {
				while ((bytesRead = input.readShort()) != -1) {
					byte[] data = new byte[bytesRead];
					
					input.readFully(data);
					new ClientReceivePacketEvent(this, data).call();
					
					PacketDeserializer packet = new PacketDeserializer(data);
					
					if (packet.readUTF().equals("ClientDisconnection"))
						disconnectionReason = packet.readUTF();
				}
			}
		} catch (SocketException | EOFException e) {
			// disconnection
		} catch (IOException ioe) {
			LogManager.log("[SOCKETS] IOException occurred while reading a packet for client \"{0}\": {1}", 2, id, ioe.getLocalizedMessage());
		} try {
			socket.close();
		} catch (IOException ioe) {
			LogManager.log("[SOCKETS] IOException occurred while closing socket for client \"{0}\": {1}", 2, id, ioe.getLocalizedMessage());
		} new ClientDisconnectionEvent(this).call();
		LogManager.log("[SOCKETS] Client \"{0}\" has just disconnected from the server{1}", 4, id, disconnectionReason == null ? "." : ": " + disconnectionReason);
		
		socket = null;
		id = null;
		input = null;
		output = null;
	}
	
	/**
	 * Sends a packet to the server.
	 * 
	 * <p>Will do nothing if <code>!</code>{@link #isConnected()}.</p>
	 * 
	 * @param packet Packet to send
	 * @throws IllegalStateException If {@link PacketSerializer#toArray()} fails
	 */
	public void sendPacket(PacketSerializer packet) {
		if (!isConnected())
			return;
		byte[] data = packet.toArray();
		
		try {
			synchronized (output) {
				output.writeShort(data.length);
				output.write(data);
			}
		} catch (IOException ioe) {
			LogManager.log("[SOCKETS] IOException occurred while writing a packet for client \"{0}\": {1}", 2, id, ioe.getLocalizedMessage());
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
	 * 
	 * <p>Will return <code>null</code> if
	 * <code>!</code>{@link #isConnected()}.</p>
	 * 
	 * @return Client's socket
	 */
	@Nullable(why = "Client may not be connected")
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Gets this client's ID.
	 * 
	 * <p>Will return <code>null</code> if
	 * <code>!</code>{@link #isConnected()}.</p>
	 * 
	 * @return Client's ID
	 */
	@Nullable(why = "Client may not be connected")
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this client's data input stream.
	 * 
	 * <p>Will return <code>null</code> if
	 * <code>!</code>{@link #isConnected()}.</p>
	 * 
	 * @return Client's input stream
	 */
	@Nullable(why = "Client may not be connected")
	public DataInputStream getInput() {
		return input;
	}
	
	/**
	 * Gets this client's data output stream.
	 * 
	 * <p>Will return <code>null</code> if
	 * <code>!</code>{@link #isConnected()}.</p>
	 * 
	 * @return Client's output stream
	 */
	@Nullable(why = "Client may not be connected")
	public DataOutputStream getOutput() {
		return output;
	}
	
}
