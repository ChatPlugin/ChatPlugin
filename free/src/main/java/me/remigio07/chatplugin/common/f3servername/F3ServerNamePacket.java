/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.common.f3servername;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class F3ServerNamePacket {
	
	private ByteBuf buffer = Unpooled.buffer();
	private byte[] array;
	
	public F3ServerNamePacket(String value) {
		value += "§r";
		array = value.getBytes(StandardCharsets.UTF_8);
		int length = array.length;
		
		do {
			int part = length & 0x7F;
			length >>>= 7;
			
			if (length != 0)
				part |= 0x80;
			buffer.writeByte(part);
		} while (length != 0);
		
		buffer.writeBytes(array);
		
		byte[] ret = new byte[buffer.readableBytes()];
		
		buffer.readBytes(ret);
		
		array = ret;
		
		buffer.release();
	}
	
	public byte[] toArray() {
		return array;
	}
	
}
