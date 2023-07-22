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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles welcome messages.
 */
public abstract class WelcomeMessageManager implements ChatPluginManager {
	
	protected static WelcomeMessageManager instance;
	protected boolean enabled;
	protected long delay;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected Map<Language, String> welcomeMessages = new HashMap<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.welcome-messages.settings.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the delay to wait before sending
	 * the welcome message, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.welcome-messages.settings.delay-ms" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Delay to wait
	 */
	public long getDelay() {
		return delay;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate {@link #getWelcomeMessages()}.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.welcome-messages.settings.placeholder-types" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Placeholders used to translate welcome messages
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the map of loaded welcome messages.
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.welcome-messages.values" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Loaded welcome messages' map
	 */
	public Map<Language, String> getWelcomeMessages() {
		return welcomeMessages;
	}
	
	/**
	 * Gets the welcome message for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s welcome message if no welcome message is present for the specified language.
	 * Will return <code>null</code> if {@link #getWelcomeMessages()}<code>.get(language) == null && !avoidNull</code>.
	 * 
	 * @param language Language used to translate the welcome message
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Loaded welcome message
	 */
	@Nullable(why = "No welcome message may be present for the specified language")
	public String getWelcomeMessage(Language language, boolean avoidNull) {
		return welcomeMessages.get(language) == null ? avoidNull ? welcomeMessages.get(Language.getMainLanguage()) : null : welcomeMessages.get(language);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static WelcomeMessageManager getInstance() {
		return instance;
	}
	
	/**
	 * Sends the welcome message to the specified player.
	 * 
	 * @param player Player to send the welcome message to
	 * @param applyDelay Whether to wait {@link #getDelay()}
	 */
	public abstract void sendWelcomeMessage(ChatPluginServerPlayer player, boolean applyDelay);
	
}
