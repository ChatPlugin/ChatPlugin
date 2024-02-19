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

import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;

/**
 * Represents an {@link IPLookup}-related event.
 */
public abstract class IPLookupEvent implements ChatPluginEvent {
	
	protected IPLookup ipLookup;
	
	protected IPLookupEvent(IPLookup ipLookup) {
		this.ipLookup = ipLookup;
	}
	
	/**
	 * Gets the IP lookup involved with this event.
	 * 
	 * @return IP lookup involved
	 */
	public IPLookup getIpLookup() {
		return ipLookup;
	}
	
}
