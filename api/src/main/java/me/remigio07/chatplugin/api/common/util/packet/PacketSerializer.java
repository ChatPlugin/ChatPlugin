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

package me.remigio07.chatplugin.api.common.util.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;

/**
 * Util class used to serialize packets sent through the proxy.
 * 
 * @see PacketDeserializer
 * @see Packets
 */
public class PacketSerializer {
	
	private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	private DataOutputStream output = new DataOutputStream(bytes);
	
	/**
	 * Initializes this packet serializer.
	 * 
	 * @param subchannel Subchannel to send the packet through
	 */
	public PacketSerializer(String subchannel) {
		try {
			output.writeUTF(subchannel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes a boolean value.
	 * 
	 * @param arg Boolean to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeBoolean(boolean arg) {
		try {
			output.writeBoolean(arg);
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Writes a byte value.
	 * 
	 * @param arg Byte to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeByte(byte arg) {
		try {
			output.writeByte(arg);
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Writes a character value.
	 * 
	 * @param arg Character to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeChar(char arg) {
		try {
			output.writeChar(arg);
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Writes a short value.
	 * 
	 * @param arg Short to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeShort(short arg) {
		try {
			output.writeShort(arg);
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Writes an integer value.
	 * 
	 * @param arg Integer to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeInt(int arg) {
		try {
			output.writeInt(arg);
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Writes a long value.
	 * 
	 * @param arg Long to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeLong(long arg) {
		try {
			output.writeLong(arg);
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Writes a float value.
	 * 
	 * @param arg Float to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeFloat(float arg) {
		try {
			output.writeFloat(arg);
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Writes a double value.
	 * 
	 * @param arg Double to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeDouble(double arg) {
		try {
			output.writeDouble(arg);
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Writes an UTF string value.
	 * 
	 * <p>You may specify a <code>null</code> string.</p>
	 * 
	 * @param arg UTF string to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeUTF(@Nullable(why = "Null strings are sent as placeholders but read as null by PacketDeserializer#readUTF()") String arg) {
		try {
			output.writeUTF(arg == null ? "${null_string}" : arg);
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Writes an UTF string array value.
	 * 
	 * <p>The array may contain <code>null</code> elements.</p>
	 * 
	 * @param args UTF string array to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeUTFArray(@NotNull String... args) {
		writeInt(args.length);
		
		for (String utf : args)
			writeUTF(utf);
		return this;
	}
	
	/**
	 * Writes an UUID value.
	 * 
	 * <p>You may specify a <code>null</code> UUID.</p>
	 * 
	 * @param arg UUID to write
	 * @return This packet serializer
	 */
	public PacketSerializer writeUUID(@Nullable(why = "Null UUIDs are sent as placeholders but read as null by PacketDeserializer#readUUID()") UUID arg) {
		writeUTF(arg == null ? null : arg.toString());
		return this;
	}
	
	/**
	 * Writes this server's ID.
	 * 
	 * @return This packet serializer
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public PacketSerializer writeServerID() {
		try {
			output.writeUTF(ProxyManager.getInstance().getServerID());
		} catch (IOException e) {
			e.printStackTrace();
		} return this;
	}
	
	/**
	 * Converts this packet to a bytes array
	 * ready to be sent through the proxy.
	 * 
	 * @return Packet's bytes array
	 * @throws IllegalArgumentException If array's length exceeds {@link Short#MAX_VALUE}
	 */
	public byte[] toArray() {
		byte[] array = bytes.toByteArray();
		
		if (array.length > Short.MAX_VALUE)
			throw new IllegalArgumentException("Packet exceeds maximum length of 32767");
		return array;
	}
	
}