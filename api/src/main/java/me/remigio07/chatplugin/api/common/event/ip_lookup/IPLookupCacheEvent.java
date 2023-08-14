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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.event.ip_lookup;

import java.net.InetAddress;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;

/**
 * Represents the event called after an {@link IPLookup} is cached into {@link IPLookupManager#getCache()}.
 * 
 * @see IPLookupManager#putInCache(InetAddress, IPLookup)
 */
public class IPLookupCacheEvent extends IPLookupEvent {
	
	/**
	 * Constructs a new IP lookup cache event.
	 * 
	 * @param ipLookup IP lookup involved
	 */
	public IPLookupCacheEvent(IPLookup ipLookup) {
		super(ipLookup);
	}
	
}
