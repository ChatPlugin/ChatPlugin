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

package me.remigio07.chatplugin.api.common.util.packet.type;

import me.remigio07.chatplugin.api.common.util.Utils;

/**
 * Represents the type of a packet sent through the <code>PlayerMessage</code>
 * or the <code>PlayerDisconnect</code> subchannel.
 */
public enum MessagePacketType {
	
	/**
	 * Represents a plain message.
	 */
	PLAIN,
	
	/**
	 * Represents a message composed of a path
	 * (which points to a message in messages' files)
	 * and placeholders that will be translated using
	 * {@link Utils#numericPlaceholders(String, Object...)}.
	 */
	NUMERIC_PLACEHOLDERS,
	
	/**
	 * Represents a message composed of a path
	 * (which points to a message in messages' files)
	 * and placeholders that will be translated using
	 * custom identifiers.
	 */
	CUSTOM_PLACEHOLDERS;
	
}
