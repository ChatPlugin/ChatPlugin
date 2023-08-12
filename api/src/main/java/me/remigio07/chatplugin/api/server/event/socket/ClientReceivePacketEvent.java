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

import me.remigio07.chatplugin.api.common.util.packet.PacketDeserializer;
import me.remigio07.chatplugin.api.server.util.socket.Client;

/**
 * Represents the event called after a {@link Client} has received a {@link PacketDeserializer}.
 * 
 * @see Client#run()
 */
public class ClientReceivePacketEvent extends ClientEvent {
	
	private byte[] packet;
	
	/**
	 * Constructs a new client receive packet event.
	 * 
	 * @param client Client involved
	 * @param packet Packet received
	 */
	public ClientReceivePacketEvent(Client client, byte[] packet) {
		super(client);
		this.packet = packet;
	}
	
	/**
	 * Gets the packet received by the client.
	 * 
	 * @return Packet received
	 * @see PacketDeserializer#PacketDeserializer(byte[]) new PacketDeserializer(byte[])
	 */
	public byte[] getPacket() {
		return packet;
	}
	
}
