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

package me.remigio07_.chatplugin.api.common.ip_lookup;

/**
 * Represents an IP lookup method. There are just two methods:
 * 	<ul>
 * 		<li>{@link #LOCAL} - a local request handled by the local database</li>
 * 		<li>{@link #REMOTE} - a remote request from MaxMind's website</li>
 * 	</ul>
 * 
 * @see IPLookupManager
 */
public enum IPLookupMethod {
	
	/**
	 * A local request handled by the local database.
	 */
	LOCAL,
	
	/**
	 * A remote request from MaxMind's website.
	 */
	REMOTE;
	
}