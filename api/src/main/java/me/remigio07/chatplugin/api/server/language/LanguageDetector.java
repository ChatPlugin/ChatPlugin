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

package me.remigio07.chatplugin.api.server.language;

import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents the language detector used by ChatPlugin.
 * 
 * @see LanguageManager
 */
public abstract class LanguageDetector {
	
	protected boolean enabled;
	protected LanguageDetectionMethod method;
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
	 * Gets the language detection method currently in use.
	 * 
	 * <p><strong>Found at:</strong> "languages.detector.method" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Current language detector method
	 */
	public LanguageDetectionMethod getMethod() {
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
	 * 
	 * <p>Will fallback to {@link LanguageManager#getMainLanguage()}
	 * if was not possible to detect the player's language.</p>
	 * 
	 * <p>The future is not instantly completed if it is necessary to call
	 * {@link IPLookupManager#getIPLookup(InetAddress)}. It will take a
	 * maximum of 5 seconds and will never be completed exceptionally.</p>
	 * 
	 * @param player Target player
	 * @return Player's language
	 */
	@NotNull
	public CompletableFuture<Language> detect(ChatPluginServerPlayer player) {
		if (enabled) {
			switch (method) {
			case CLIENT_LOCALE:
				return CompletableFuture.completedFuture(detectUsingClientLocale(player));
			case GEOLOCALIZATION:
				CompletableFuture<Language> future = new CompletableFuture<>();
				
				TaskManager.runAsync(() -> future.complete(detectUsingGeolocalization(player.getIPLookup(true).join())), 0L);
				return future;
			}
		} return CompletableFuture.completedFuture(Language.getMainLanguage()); // TODO find an universal solution for methods of disabled managers
	}
	
	/**
	 * Loads the language detector.
	 */
	public abstract void load();
	
	/**
	 * Detects a player's language using {@link LanguageDetectionMethod#CLIENT_LOCALE}.
	 * 
	 * <p>Will fallback to {@link LanguageManager#getMainLanguage()}
	 * if was not possible to detect the player's language.</p>
	 * 
	 * @param player Target player
	 * @return Player's language
	 */
	@NotNull
	public abstract Language detectUsingClientLocale(ChatPluginServerPlayer player);
	
	/**
	 * Detects an IP lookup's language using {@link LanguageDetectionMethod#GEOLOCALIZATION}.
	 * 
	 * <p>Will fallback to {@link LanguageManager#getMainLanguage()}
	 * if was not possible to detect the player's language.</p>
	 * 
	 * @param ipLookup Target IP lookup
	 * @return IP lookup's language
	 */
	@NotNull
	public abstract Language detectUsingGeolocalization(IPLookup ipLookup);
	
}
