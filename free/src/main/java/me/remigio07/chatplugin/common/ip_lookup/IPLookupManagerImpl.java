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

package me.remigio07.chatplugin.common.ip_lookup;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupMethod;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;

public class IPLookupManagerImpl extends BaseIPLookupManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		if (load0()) {
			if (method == IPLookupMethod.LOCAL)
				throw new ChatPluginManagerException(this, "LOCAL cannot be selected as IP lookup method on the free version; only REMOTE is allowed");
			enabled = true;
			loadTime = System.currentTimeMillis() - ms;
		}
	}
	
}
