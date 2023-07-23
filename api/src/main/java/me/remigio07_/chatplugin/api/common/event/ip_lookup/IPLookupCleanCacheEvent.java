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

package me.remigio07_.chatplugin.api.common.event.ip_lookup;

import me.remigio07_.chatplugin.api.common.event.CancellableEvent;
import me.remigio07_.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07_.chatplugin.api.common.ip_lookup.IPLookupManager;

/**
 * Represents the event called just before an {@link IPLookup} is removed from {@link IPLookupManager#getCache()}.
 * 
 * @see IPLookupManager#removeFromCache(String)
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