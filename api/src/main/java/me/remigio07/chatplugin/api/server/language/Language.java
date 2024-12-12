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

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;

/**
 * Represents a language handled by the {@link LanguageManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Languages">ChatPlugin wiki/Modules/Languages</a>
 */
public abstract class Language {
	
	private String id, displayName;
	private List<String> countryCodes;
	private Configuration configuration;
	
	protected Language(String id, String displayName, List<String> countryCodes) throws IOException {
		this.id = id;
		this.displayName = displayName;
		this.countryCodes = countryCodes;
		configuration = id.equals("english") ? ConfigurationType.MESSAGES.get() : new Configuration(new File(ChatPlugin.getInstance().getDataFolder().getAbsolutePath() + File.separator + "custom-messages", "messages-" + id + ".yml"));
	}
	
	/**
	 * Gets this language's ID.
	 * 
	 * @return Language's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this language's display name. May include format codes ("§x").
	 * 
	 * @return Language's display name
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Gets this language's list of <a href="https://en.wikipedia.org/wiki/ISO_3166-2#Current_codes">ISO 3166-2</a> country codes.
	 * 
	 * <p>A player's language may be determined using {@link LanguageDetectionMethod#GEOLOCALIZATION}
	 * if this list contains their {@link IPLookup#getCountryCode()}.</p>
	 * 
	 * @return Language's country codes
	 */
	public List<String> getCountryCodes() {
		return countryCodes;
	}
	
	/**
	 * Gets the messages configuration associated with this language.
	 * 
	 * @return Associated configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
	
	/**
	 * Gets a message from this language's {@link #getConfiguration()} and translates
	 * its placeholders using {@link Utils#numericPlaceholders(String, Object...)}.
	 * 
	 * <p>The "{pfx}" placeholder will be translated with the message found at "misc.prefix".</p>
	 * 
	 * @param path Message's path
	 * @param args Optional arguments (translated using {@link Utils#numericPlaceholders(String, Object...)})
	 * @return Translated message
	 */
	public String getMessage(String path, Object... args) {
		return Utils.numericPlaceholders(configuration.translateString(path, ConfigurationType.MESSAGES.get().translateString(path)).replace("{pfx}", configuration.translateString("misc.prefix", ConfigurationType.MESSAGES.get().translateString("misc.prefix"))), args);
	}
	
	/**
	 * Gets a list containing all online players with
	 * this language set as their current language.
	 * 
	 * @return Language's online players
	 */
	public List<ChatPluginServerPlayer> getOnlinePlayers() {
		return ServerPlayerManager.getInstance().getPlayers().values().stream().filter(player -> player.getLanguage().equals(this)).collect(Collectors.toList());
	}
	
	/**
	 * Gets the plugin's main and default language.
	 * 
	 * <p>This method calls {@link LanguageManager#getMainLanguage()}.</p>
	 * 
	 * @return Main language
	 */
	public static Language getMainLanguage() {
		return LanguageManager.getInstance().getMainLanguage();
	}
	
}
