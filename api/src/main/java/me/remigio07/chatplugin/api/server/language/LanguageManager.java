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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.language;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles {@link Language}s. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Languages">ChatPlugin wiki/Languages</a>
 * 
 * @see LanguageDetector
 */
public abstract class LanguageManager implements ChatPluginManager {
	
	/**
	 * Pattern representing the allowed language IDs.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,36}$"</p>
	 * 
	 * @see #isValidLanguageID(String)
	 */
	public static final Pattern LANGUAGE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	protected static LanguageManager instance;
	protected boolean enabled;
	protected LanguageDetector detector;
	protected List<Language> languages = new ArrayList<>();
	protected Language mainLanguage;
	protected long loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the language detector currently in use.
	 * 
	 * @return Current language detector
	 */
	public LanguageDetector getDetector() {
		return detector;
	}
	
	/**
	 * Gets the list of loaded languages.
	 * Do not modify the returned list.
	 * 
	 * @return Loaded languages' list
	 */
	public List<Language> getLanguages() {
		return languages;
	}
	
	/**
	 * Gets a language from {@link #getLanguages()} by its ID.
	 * Will return <code>null</code> if the language is not loaded.
	 * 
	 * @param id Language's ID
	 * @return Loaded language
	 */
	@Nullable(why = "Specified language may not be loaded")
	public Language getLanguage(String id) {
		return languages.stream().filter(language -> language.getID().equals(id)).findAny().orElse(null);
	}
	
	/**
	 * Gets the plugin's main and default language.
	 * 
	 * <p><strong>Found at:</strong> "languages.main-language-id" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Main language
	 */
	public Language getMainLanguage() {
		return mainLanguage;
	}
	
	/**
	 * Checks if the specified String is a valid language ID.
	 * 
	 * @param languageID Language ID to check
	 * @return Whether the specified language ID is valid
	 * @see #LANGUAGE_ID_PATTERN
	 */
	public boolean isValidLanguageID(String languageID) {
		return LANGUAGE_ID_PATTERN.matcher(languageID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static LanguageManager getInstance() {
		return instance;
	}
	
	/**
	 * Gets a player's language using {@link ChatPluginServerPlayer#getLanguage()}
	 * if they are loaded or from the storage if they are offline. Will return
	 * {@link #getMainLanguage()} if they are not stored.
	 * 
	 * @param player Player to check
	 * @return Player's language
	 */
	public abstract Language getLanguage(OfflinePlayer player);
	
	/**
	 * Sets a player's language, updates it in the storage
	 * and reloads the player if they are loaded.
	 * 
	 * @param player Player to set the language for
	 * @param language Language to set
	 */
	public abstract void setLanguage(OfflinePlayer player, Language language);
	
}
