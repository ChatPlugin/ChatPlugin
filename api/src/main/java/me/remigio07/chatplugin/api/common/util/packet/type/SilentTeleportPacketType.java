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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.util.packet.type;

/**
 * Represents the type of a packet sent through the <code>SilentTeleport</code> subchannel.
 */
public enum SilentTeleportPacketType {
	
	/**
	 * Represents a packet used to teleport a player to another player.
	 */
	PLAYER,
	
	/**
	 * Represents a packet used to teleport a player to another server.
	 */
	SERVER;
	
}
