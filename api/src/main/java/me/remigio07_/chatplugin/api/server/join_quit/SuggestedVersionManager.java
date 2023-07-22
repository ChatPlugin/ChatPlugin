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

package me.remigio07_.chatplugin.api.server.join_quit;

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles version suggestions.
 */
public abstract class SuggestedVersionManager implements ChatPluginManager {
	
	protected static SuggestedVersionManager instance;
	protected boolean enabled;
	protected Version version;
	protected long delay, loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.suggested-version.settings.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the version suggested to players.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.suggested-version.settings.version" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Suggested version
	 */
	public Version getVersion() {
		return version;
	}
	
	/**
	 * Gets the delay to wait before sending the
	 * suggested version message, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.suggested-version.settings.delay-ms" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Delay to wait
	 */
	public long getDelay() {
		return delay;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static SuggestedVersionManager getInstance() {
		return instance;
	}
	
	/**
	 * Checks if {@link ChatPluginServerPlayer#getVersion()}<code>.isAtLeast({@link #getVersion()})</code>.
	 * If <code>true</code>, waits {@link #getDelay()} and then sends the suggested version message.
	 * 
	 * @param player Player to check
	 */
	public abstract void check(ChatPluginServerPlayer player);
	
}
