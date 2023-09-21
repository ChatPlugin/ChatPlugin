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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.join_quit;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles join titles.
 */
public abstract class JoinTitleManager implements ChatPluginManager {
	
	protected static JoinTitleManager instance;
	protected boolean enabled;
	protected long fadeIn, stay, fadeOut, delay;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected Map<Language, String> titles = new HashMap<>();
	protected Map<Language, String> subtitles = new HashMap<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-titles.settings.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the time for the join title
	 * to fade in, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-titles.settings.fade-in-ms" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Time to fade in
	 */
	public long getFadeIn() {
		return fadeIn;
	}
	
	/**
	 * Gets the time for the join title
	 * to stay, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-titles.settings.stay-ms" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Time to stay
	 */
	public long getStay() {
		return stay;
	}
	
	/**
	 * Gets the time for the join title
	 * to fade out, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-titles.settings.fade-out-ms" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Time to fade out
	 */
	public long getFadeOut() {
		return fadeOut;
	}
	
	/**
	 * Gets the delay to wait before sending
	 * the join title, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-titles.settings.delay-ms" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Delay to wait
	 */
	public long getDelay() {
		return delay;
	}
	
	/**
	 * Gets the list of placeholder types used to translate
	 * {@link #getTitles()} and {@link #getSubtitles()}.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-titles.settings.placeholder-types" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Placeholders used to translate join titles
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the map of loaded titles.
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-titles.titles" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Loaded titles' map
	 */
	public Map<Language, String> getTitles() {
		return titles;
	}
	
	/**
	 * Gets the title for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s title if no title is present for the specified language.
	 * Will return <code>null</code> if {@link #getTitles()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.
	 * 
	 * @param language Language used to translate the title
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Loaded title
	 */
	@Nullable(why = "No title may be present for the specified language")
	public String getTitle(Language language, boolean avoidNull) {
		return titles.get(language) == null ? avoidNull ? titles.get(Language.getMainLanguage()) : null : titles.get(language);
	}
	
	/**
	 * Gets the map of loaded subtitles.
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-titles.subtitles" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Loaded subtitles' map
	 */
	public Map<Language, String> getSubtitles() {
		return subtitles;
	}
	
	/**
	 * Gets the subtitle for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s subtitle if no subtitle is present for the specified language.
	 * Will return <code>null</code> if {@link #getSubtitles()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.
	 * 
	 * @param language Language used to translate the subtitle
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Loaded subtitle
	 */
	@Nullable(why = "No subtitle may be present for the specified language")
	public String getSubtitle(Language language, boolean avoidNull) {
		return subtitles.get(language) == null ? avoidNull ? subtitles.get(Language.getMainLanguage()) : null : subtitles.get(language);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static JoinTitleManager getInstance() {
		return instance;
	}
	
	/**
	 * Sends the join title to the specified player.
	 * 
	 * @param player Player to send the join title to
	 * @param applyDelay Whether to wait {@link #getDelay()}
	 */
	public abstract void sendJoinTitle(ChatPluginServerPlayer player, boolean applyDelay);
	
}
