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

package me.remigio07_.chatplugin.api.server.util.manager;

import java.util.List;
import java.util.stream.Collectors;

import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07_.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles ChatPlugin's integrated (and its integrations') placeholders. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Placeholders">ChatPlugin wiki/Placeholders</a>
 * 
 * @see PlaceholderType
 */
public abstract class PlaceholderManager implements ChatPluginManager, Runnable {
	
	protected static PlaceholderManager instance;
	protected boolean enabled;
	protected long timerTaskID = -1, loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the update task's ID. You can interact
	 * with it through {@link TaskManager}'s methods.
	 * 
	 * @return Update task's ID
	 */
	public long getTimerTaskID() {
		return timerTaskID;
	}
	
	/**
	 * Translates an input string with placeholders, formatted for the
	 * specified player and translated in the player's language. You have to
	 * indicate what placeholder types you need for the string to be translated.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param placeholderTypes Placeholder types present in the input
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public String translatePlaceholders(String input, ChatPluginServerPlayer player, List<PlaceholderType> placeholderTypes) {
		return translatePlaceholders(input, player, player.getLanguage(), placeholderTypes);
	}
	
	/**
	 * Translates an input string list with placeholders, formatted for the
	 * specified player and translated in the player's language. You have to
	 * indicate what placeholder types you need for the string to be translated.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param placeholderTypes Placeholder types present in the input
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public List<String> translatePlaceholders(List<String> input, ChatPluginServerPlayer player, List<PlaceholderType> placeholderTypes) {
		return translatePlaceholders(input, player, player.getLanguage(), placeholderTypes);
	}
	
	/**
	 * Translates an input string list with placeholders, formatted for the
	 * specified player and translated in the specified language. You have to
	 * indicate what placeholder types you need for the string to be translated.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @param placeholderTypes Placeholder types present in the input
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public List<String> translatePlaceholders(List<String> input, ChatPluginServerPlayer player, Language language, List<PlaceholderType> placeholderTypes) {
		return input.stream().map(string -> translatePlaceholders(string, player, language, placeholderTypes)).collect(Collectors.toList());
	}
	
	/**
	 * Translates an input string with {@link PlaceholderType#PLAYER} placeholders,
	 * formatted for the specified player and translated in the player's langauge.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public String translatePlayerPlaceholders(String input, ChatPluginServerPlayer player) {
		return translatePlayerPlaceholders(input, player, player.getLanguage());
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#PLAYER} placeholders,
	 * formatted for the specified player and translated in the player's langauge.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public List<String> translatePlayerPlaceholders(List<String> input, ChatPluginServerPlayer player) {
		return translatePlayerPlaceholders(input, player, player.getLanguage());
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#PLAYER} placeholders,
	 * formatted for the specified player and translated in the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public List<String> translatePlayerPlaceholders(List<String> input, ChatPluginServerPlayer player, Language language) {
		return input.stream().map(string -> translatePlayerPlaceholders(string, player, language)).collect(Collectors.toList());
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#SERVER}
	 * placeholders, translated in the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public List<String> translateServerPlaceholders(List<String> input, Language language) {
		return input.stream().map(string -> translateServerPlaceholders(string, language)).collect(Collectors.toList());
	}
	
	/**
	 * Translates an input string with {@link PlaceholderType#INTEGRATIONS} placeholders,
	 * formatted for the specified player and translated in the player's language.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public String translateIntegrationsPlaceholders(String input, ChatPluginServerPlayer player) {
		return translateIntegrationsPlaceholders(input, player, player.getLanguage());
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#INTEGRATIONS} placeholders,
	 * formatted for the specified player and translated in the player's language.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public List<String> translateIntegrationsPlaceholders(List<String> input, ChatPluginServerPlayer player) {
		return translateIntegrationsPlaceholders(input, player, player.getLanguage());
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#INTEGRATIONS} placeholders,
	 * formatted for the specified player and translated in the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public List<String> translateIntegrationsPlaceholders(List<String> input, ChatPluginServerPlayer player, Language language) {
		return input.stream().map(string -> translateIntegrationsPlaceholders(string, player, language)).collect(Collectors.toList());
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static PlaceholderManager getInstance() {
		return instance;
	}
	
	/**
	 * Storage's placeholders updater, called once every
	 * <code>settings.storage-placeholders-update-timeout</code> in config.yml.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Translates an input string with placeholders, formatted for the
	 * specified player and translated in the specified language. You have to
	 * indicate what placeholder types you need for the string to be translated.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @param placeholderTypes Placeholder types present in the input
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public abstract String translatePlaceholders(String input, ChatPluginServerPlayer player, Language language, List<PlaceholderType> placeholderTypes);
	
	/**
	 * Translates an input string with {@link PlaceholderType#PLAYER} placeholders,
	 * formatted for the specified player and translated in the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public abstract String translatePlayerPlaceholders(String input, ChatPluginServerPlayer player, Language language);
	
	/**
	 * Translates an input string with {@link PlaceholderType#SERVER}
	 * placeholders, translated in the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public abstract String translateServerPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string with {@link PlaceholderType#INTEGRATIONS} placeholders,
	 * formatted for the specified player and translated in the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 * @see PlaceholderType
	 */
	public abstract String translateIntegrationsPlaceholders(String input, ChatPluginServerPlayer player, Language language);
	
}
