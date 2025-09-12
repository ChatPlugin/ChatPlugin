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

package me.remigio07.chatplugin.api.server.util;

/**
 * Represents a date format (full, day only, hour only).
 */
public enum DateFormat {
	
	/**
	 * Represents the full date format, specified at
	 * <code>misc.simple-date-format.full</code> in the messages' files.
	 */
	FULL,
	
	/**
	 * Represents the day only date format, specified at
	 * <code>misc.simple-date-format.day</code> in the messages' files.
	 */
	DAY,
	
	/**
	 * Represents the hour only date format, specified at
	 * <code>misc.simple-date-format.hour</code> in the messages' files.
	 */
	HOUR;
	
}
