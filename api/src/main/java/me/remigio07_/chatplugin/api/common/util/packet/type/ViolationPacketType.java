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

package me.remigio07_.chatplugin.api.common.util.packet.type;

/**
 * Represents the type of a packet sent through the <code>PlayerViolation</code> subchannel.
 */
public enum ViolationPacketType {
	
	/**
	 * Represents a packet used to add a violation to a player's violations.
	 */
	ADD,
	
	/**
	 * Represents a packet used to remove a violation from a player's violations.
	 */
	REMOVE,
	
	/**
	 * Represents a packet used to clear a player's violations.
	 */
	CLEAR;
	
}
