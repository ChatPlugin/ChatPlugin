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
 * Represents the type of a packet sent through the <code>PlayerUnban</code>, the <code>PlayerUnwarn</code> or the <code>PlayerUnmute</code> subchannel.
 */
public enum PunishmentPacketType {
	
	/**
	 * Represents a packet used to remove a punishment based on its player.
	 */
	PLAYER_BASED,
	
	/**
	 * Represents a packet used to remove a punishment based on its ID.
	 */
	ID_BASED;
	
}
