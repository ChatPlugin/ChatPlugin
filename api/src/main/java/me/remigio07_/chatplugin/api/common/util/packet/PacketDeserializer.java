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

package me.remigio07_.chatplugin.api.common.util.packet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;

/**
 * Util class used to deserialize packets sent through the proxy.
 * 
 * @see PacketSerializer
 * @see Packets
 */
public class PacketDeserializer {
	
	private DataInputStream input;
	
	/**
	 * Initializes this packet deserializer.
	 * 
	 * @param bytes Packet to read
	 * @throws IllegalArgumentException If array's length exceeds {@link Short#MAX_VALUE}
	 */
	public PacketDeserializer(byte[] bytes) {
		if (bytes.length > Short.MAX_VALUE)
			throw new IllegalArgumentException("Packet exceeds maximum length of 32767");
		input = new DataInputStream(new ByteArrayInputStream(bytes));
	}
	
	/**
	 * Gets the data input stream associated with this packet deserializer.
	 * 
	 * @return Packet's data input stream
	 */
	public DataInputStream getInput() {
		return input;
	}
	
	/**
	 * Reads the next boolean value.
	 * 
	 * @return Boolean read
	 */
	public boolean readBoolean() {
		try {
			return input.readBoolean();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Reads the next byte value.
	 * 
	 * @return Byte read
	 */
	public byte readByte() {
		try {
			return input.readByte();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Reads the next character value.
	 * 
	 * @return Character read
	 */
	public char readChar() {
		try {
			return input.readChar();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Reads the next short value.
	 * 
	 * @return Short read
	 */
	public short readShort() {
		try {
			return input.readShort();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Reads the next integer value.
	 * 
	 * @return Integer read
	 */
	public int readInt() {
		try {
			return input.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Reads the next long value.
	 * 
	 * @return Long read
	 */
	public long readLong() {
		try {
			return input.readLong();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Reads the next float value.
	 * 
	 * @return Float read
	 */
	public float readFloat() {
		try {
			return input.readFloat();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Reads the next double value.
	 * 
	 * @return Double read
	 */
	public double readDouble() {
		try {
			return input.readDouble();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Reads the next UTF string value.
	 * May return a <code>null</code> string.
	 * 
	 * @return UTF string read
	 */
	@Nullable(why = "Null strings are sent as placeholders but read as null by this method")
	public String readUTF() {
		try {
			String utf = input.readUTF();
			return utf.equals("${null_string}") ? null : utf;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Reads the next UTF string array value.
	 * The array may contain <code>null</code> elements.
	 * 
	 * @return UTF string array read
	 */
	@NotNull
	public String[] readUTFArray() {
		String[] array = new String[readInt()];
		
		for (int i = 0; i < array.length; i++)
			array[i] = readUTF();
		return array;
	}
	
	/**
	 * Reads the next UUID value.
	 * Calls {@link UUID#fromString(String)} with {@link #readUTF()}.
	 * May return a <code>null</code> UUID.
	 * 
	 * @return UUID read
	 */
	@Nullable(why = "Null UUIDs are sent as placeholders but read as null by this method")
	public UUID readUUID() {
		String uuid = readUTF();
		return uuid == null ? null : UUID.fromString(uuid);
	}
	
}
