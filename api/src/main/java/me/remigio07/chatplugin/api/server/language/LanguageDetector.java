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

package me.remigio07.chatplugin.api.server.language;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents the language detector used by ChatPlugin.
 * 
 * @see LanguageManager
 */
public abstract class LanguageDetector {
	
	protected boolean enabled;
	protected LanguageDetectorMethod method;
	protected long delay;
	
	/**
	 * Unloads the language detector.
	 */
	public void unload() {
		enabled = false;
		
		method = null;
		delay = 0L;
	}
	
	/**
	 * Checks if the language detector is enabled.
	 * 
	 * <p><strong>Found at:</strong> "languages.detector.enabled" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Whether the detector is enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the language detector method currently in use.
	 * 
	 * <p><strong>Found at:</strong> "languages.detector.method" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Current language detector method
	 */
	public LanguageDetectorMethod getMethod() {
		return method;
	}
	
	/**
	 * Gets the delay to wait before detecting
	 * a new player's language, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "languages.detector.delay-ms" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Delay to wait
	 */
	public long getDelay() {
		return delay;
	}
	
	/**
	 * Detects a player's language using {@link #getMethod()}.
	 * Will fallback to {@link LanguageManager#getMainLanguage()}
	 * if was not possible to detect the player's language.
	 * 
	 * @param player Target player
	 * @return Player's language
	 */
	@NotNull
	public Language detect(ChatPluginServerPlayer player) {
		if (enabled) {
			switch (method) {
			case CLIENT_LOCALE:
				return detectUsingClientLocale(player);
			case GEOLOCALIZATION:
				return detectUsingGeolocalization(player.getIPLookup(true));
			}
		} return Language.getMainLanguage();
	}
	
	/**
	 * Loads the language detector.
	 */
	public abstract void load();
	
	/**
	 * Detects a player's language using {@link LanguageDetectorMethod#CLIENT_LOCALE}.
	 * Will fallback to {@link LanguageManager#getMainLanguage()}
	 * if was not possible to detect the player's language.
	 * 
	 * @param player Target player
	 * @return Player's language
	 */
	@NotNull
	public abstract Language detectUsingClientLocale(ChatPluginServerPlayer player);
	
	/**
	 * Detects an IP lookup's language using {@link LanguageDetectorMethod#GEOLOCALIZATION}.
	 * Will fallback to {@link LanguageManager#getMainLanguage()}
	 * if was not possible to detect the player's language.
	 * 
	 * @param ipLookup Target IP lookup
	 * @return IP lookup's language
	 */
	@NotNull
	public abstract Language detectUsingGeolocalization(IPLookup ipLookup);
	
}
