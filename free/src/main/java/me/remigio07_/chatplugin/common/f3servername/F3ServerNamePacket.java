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

package me.remigio07_.chatplugin.common.f3servername;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class F3ServerNamePacket {
	
	private ByteBuf buffer = Unpooled.buffer();
	private byte[] array;
	
	public F3ServerNamePacket(String value) {
		writeString(value);
		
		array = buffer.array();
		
		buffer.release();
	}
	
	private void writeString(String value) {
		byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
		
		writeInt(bytes.length);
		buffer.writeBytes(bytes);
	}
	
	private void writeInt(int value) {
		do {
			int part = value & 0x7F;
			
			value >>>= 7;
			
			if (value != 0)
				part |= 0x80;
			buffer.writeByte(part);
		} while (value != 0);
	}
	
	public byte[] toArray() {
		return array;
	}
	
}
