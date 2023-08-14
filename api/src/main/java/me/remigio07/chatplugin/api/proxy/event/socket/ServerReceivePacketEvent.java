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

package me.remigio07.chatplugin.api.proxy.event.socket;

import me.remigio07.chatplugin.api.common.util.packet.PacketDeserializer;
import me.remigio07.chatplugin.api.proxy.util.socket.ClientHandler;
import me.remigio07.chatplugin.api.proxy.util.socket.Server;

/**
 * Represents the event called after a {@link Server} has received a {@link PacketDeserializer} from a {@link ClientHandler}.
 * 
 * @see ClientHandler#run()
 */
public class ServerReceivePacketEvent extends ClientHandlerEvent {
	
	private byte[] packet;
	
	/**
	 * Constructs a new server receive packet event.
	 * 
	 * @param clientHandler Client handler involved
	 * @param packet Packet received
	 */
	public ServerReceivePacketEvent(ClientHandler clientHandler, byte[] packet) {
		super(clientHandler);
		this.packet = packet;
	}
	
	/**
	 * Gets the packet received by the server.
	 * 
	 * @return Packet received
	 * @see PacketDeserializer#PacketDeserializer(byte[]) new PacketSerializer(byte[])
	 */
	public byte[] getPacket() {
		return packet;
	}
	
}
