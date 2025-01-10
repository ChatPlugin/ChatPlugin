/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
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

package me.remigio07.chatplugin.api.common.motd;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.proxy.motd.ProxyMoTDManager;
import me.remigio07.chatplugin.api.server.motd.ServerMoTDManager;

/**
 * Manager that handles {@link MoTD}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/MoTDs">ChatPlugin wiki/Modules/MoTDs</a>
 * @see ServerMoTDManager
 * @see ProxyMoTDManager
 */
public abstract class MoTDManager implements ChatPluginManager {
	
	protected static MoTDManager instance;
	protected boolean enabled;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "motd.enabled" in {@link ConfigurationType#MOTD}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static MoTDManager getInstance() {
		return instance;
	}
	
}
