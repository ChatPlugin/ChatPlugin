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

package me.remigio07.chatplugin.api.common.punishment.kick;

/**
 * Represents a kick message type.
 * 
 * <p>There are two types of kick messages:
 * actual kick message and chat message.</p>
 * 
 * @see KickManager
 */
public enum KickType {
	
	/**
	 * Represents a kick/ban message displayed when the player gets kicked.
	 */
	KICK,
	
	/**
	 * Represents a kick/ban message displayed when the player tries to
	 * connect to the target server (via the proxy) but cannot join
	 * or the message displayed when they are sent to the lobby using
	 * the chat kick mode.
	 */
	CHAT;
	
	/**
	 * Gets the other kick type.
	 * 
	 * @return Other kick type
	 */
	public KickType getOther() {
		return this == KICK ? CHAT : KICK;
	}
	
}
