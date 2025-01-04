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

package me.remigio07.chatplugin.api.server.language;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.event.language.LanguageChangeEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;

/**
 * Manager that handles {@link Language}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Languages">ChatPlugin wiki/Modules/Languages</a>
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
	protected List<Language> languages = new CopyOnWriteArrayList<>();
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
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * @return Loaded languages' list
	 */
	public List<Language> getLanguages() {
		return languages;
	}
	
	/**
	 * Gets a language from {@link #getLanguages()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the language is not loaded.</p>
	 * 
	 * @param id Language's ID, case insensitive
	 * @return Loaded language
	 */
	@Nullable(why = "Specified language may not be loaded")
	public Language getLanguage(String id) {
		return languages.stream().filter(language -> language.getID().equalsIgnoreCase(id)).findAny().orElse(null);
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
	 * Gets a player's language using
	 * {@link ChatPluginServerPlayer#getLanguage()}
	 * if they are loaded, otherwise from the storage.
	 * 
	 * <p>Will return {@link #getMainLanguage()} if they are not stored.</p>
	 * 
	 * @param player Player to check
	 * @return Player's language
	 */
	@NotNull
	public abstract Language getLanguage(OfflinePlayer player);
	
	/**
	 * Sets a player's language, updates it in the storage
	 * and reloads the player if they were loaded.
	 * 
	 * <p><strong>Note:</strong> if you pass a {@link ChatPluginServerPlayer} as <code>player</code>,
	 * their reference will be invalidated because they will be unloaded. After calling this method, obtain
	 * a new instance of that player using {@link ServerPlayerManager#getPlayer(java.util.UUID)}.</p>
	 * 
	 * <p><strong>Note:</strong> this method might take some
	 * time to be executed: async calls are recommended.</p>
	 * 
	 * @param player Player to set the language for
	 * @param language Language to set
	 * @throws IllegalArgumentException If the new specified language corresponds to the old one
	 * @see LanguageChangeEvent
	 */
	public abstract void setLanguage(OfflinePlayer player, @NotNull Language language);
	
}
