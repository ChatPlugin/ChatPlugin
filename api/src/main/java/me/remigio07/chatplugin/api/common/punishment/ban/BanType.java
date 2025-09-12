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

package me.remigio07.chatplugin.api.common.punishment.ban;

/**
 * Represents the type of a ban: account (username/UUID) or IP address.
 * 
 * @see BanManager
 */
public enum BanType {
	
	/**
	 * Represents a username/UUID based ban.
	 */
	ACCOUNT,
	
	/**
	 * Represents an IP address based ban.
	 */
	IP_ADDRESS;
	
}