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

package me.remigio07.chatplugin.api.common.event.ip_lookup;

import java.net.InetAddress;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;

/**
 * Represents the event called just before an {@link IPLookup}
 * is removed from {@link IPLookupManager#getCache()}.
 * 
 * @see IPLookupManager#removeFromCache(InetAddress)
 */
public class IPLookupCleanCacheEvent extends IPLookupEvent implements CancellableEvent {
	
	private boolean cancelled;
	
	/**
	 * Constructs a new IP lookup clean cache event.
	 * 
	 * @param ipLookup IP lookup involved
	 */
	public IPLookupCleanCacheEvent(IPLookup ipLookup) {
		super(ipLookup);
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
