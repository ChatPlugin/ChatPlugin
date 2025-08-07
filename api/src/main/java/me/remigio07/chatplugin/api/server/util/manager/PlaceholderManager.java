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

package me.remigio07.chatplugin.api.server.util.manager;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles ChatPlugin's integrated (and its integrations') placeholders.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Placeholders">ChatPlugin wiki/Modules/Placeholders</a>
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
	 * Gets the update task's ID.
	 * 
	 * <p>You can interact with it through {@link TaskManager}'s methods.</p>
	 * 
	 * @return Update task's ID
	 */
	public long getTimerTaskID() {
		return timerTaskID;
	}
	
	/**
	 * Translates an input string with placeholders, formatted for the
	 * specified player and translated for the player's language.
	 * 
	 * <p>You have to indicate what placeholder types you need for the string to be translated.
	 * Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param placeholderTypes Placeholder types present in the input
	 * @return Translated placeholders
	 */
	public String translatePlaceholders(String input, ChatPluginServerPlayer player, Set<PlaceholderType> placeholderTypes) {
		return translatePlaceholders(input, player, player.getLanguage(), placeholderTypes);
	}
	
	/**
	 * Translates an input string with placeholders, formatted for the
	 * specified player and translated for the specified language.
	 * 
	 * <p>You have to indicate what placeholder types you need for the string to be translated.
	 * Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @param placeholderTypes Placeholder types present in the input
	 * @return Translated placeholders
	 */
	public String translatePlaceholders(String input, ChatPluginServerPlayer player, Language language, Set<PlaceholderType> placeholderTypes) {
		return translatePlaceholders(input, player, language, placeholderTypes, true);
	}
	
	/**
	 * Translates an input string list with placeholders, formatted for the
	 * specified player and translated for the player's language.
	 * 
	 * <p>You have to indicate what placeholder types you need for the string to be translated.
	 * Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param placeholderTypes Placeholder types present in the input
	 * @return Translated placeholders
	 */
	public List<String> translatePlaceholders(List<String> input, ChatPluginServerPlayer player, Set<PlaceholderType> placeholderTypes) {
		return translatePlaceholders(input, player, player.getLanguage(), placeholderTypes);
	}
	
	/**
	 * Translates an input string list with placeholders, formatted for the
	 * specified player and translated for the specified language.
	 * 
	 * <p>You have to indicate what placeholder types you need for the string to be translated.
	 * Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @param placeholderTypes Placeholder types present in the input
	 * @return Translated placeholders
	 */
	public List<String> translatePlaceholders(List<String> input, ChatPluginServerPlayer player, Language language, Set<PlaceholderType> placeholderTypes) {
		return input.stream().map(string -> translatePlaceholders(string, player, language, placeholderTypes)).collect(Collectors.toList());
	}
	
	/**
	 * Translates an input string with {@link PlaceholderType#PLAYER} placeholders,
	 * formatted for the specified player and translated for the player's language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @return Translated placeholders
	 */
	public String translatePlayerPlaceholders(String input, ChatPluginServerPlayer player) {
		return translatePlayerPlaceholders(input, player, player.getLanguage());
	}
	
	/**
	 * Translates an input string with {@link PlaceholderType#PLAYER} placeholders,
	 * specified player and translated for the specified language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public String translatePlayerPlaceholders(String input, ChatPluginServerPlayer player, Language language) {
		return translatePlayerPlaceholders(input, player, language, true);
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#PLAYER} placeholders,
	 * formatted for the specified player and translated for the player's language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @return Translated placeholders
	 */
	public List<String> translatePlayerPlaceholders(List<String> input, ChatPluginServerPlayer player) {
		return translatePlayerPlaceholders(input, player, player.getLanguage());
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#PLAYER} placeholders,
	 * formatted for the specified player and translated for the specified language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public List<String> translatePlayerPlaceholders(List<String> input, ChatPluginServerPlayer player, Language language) {
		return input.stream().map(string -> translatePlayerPlaceholders(string, player, language)).collect(Collectors.toList());
	}
	
	/**
	 * Translates an input string with {@link PlaceholderType#SERVER}
	 * placeholders, translated for the specified language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public String translateServerPlaceholders(String input, Language language) {
		return translateServerPlaceholders(input, language, true);
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#SERVER}
	 * placeholders, translated for the specified language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public List<String> translateServerPlaceholders(List<String> input, Language language) {
		return input.stream().map(string -> translateServerPlaceholders(string, language)).collect(Collectors.toList());
	}
	
	/**
	 * Translates an input string with {@link PlaceholderType#INTEGRATIONS} placeholders,
	 * formatted for the specified player and translated for the player's language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @return Translated placeholders
	 */
	public String translateIntegrationsPlaceholders(String input, ChatPluginServerPlayer player) {
		return translateIntegrationsPlaceholders(input, player, player.getLanguage());
	}
	
	/**
	 * Translates an input string with {@link PlaceholderType#INTEGRATIONS} placeholders,
	 * formatted for the specified player and translated for the specified language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public String translateIntegrationsPlaceholders(String input, ChatPluginServerPlayer player, Language language) {
		return translateIntegrationsPlaceholders(input, player, player.getLanguage(), true);
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#INTEGRATIONS} placeholders,
	 * formatted for the specified player and translated for the player's language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @return Translated placeholders
	 */
	public List<String> translateIntegrationsPlaceholders(List<String> input, ChatPluginServerPlayer player) {
		return translateIntegrationsPlaceholders(input, player, player.getLanguage());
	}
	
	/**
	 * Translates an input string list with {@link PlaceholderType#INTEGRATIONS} placeholders,
	 * formatted for the specified player and translated for the specified language.
	 * 
	 * <p>Color and formatting codes are already translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
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
	 * <code>settings.storage-placeholders-update-timeout</code>
	 * in {@link ConfigurationType#CONFIG}.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Translates an input string with placeholders, formatted for the
	 * specified player and translated for the specified language.
	 * 
	 * <p>You have to indicate what placeholder types you need for the string to be translated.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @param placeholderTypes Placeholder types present in the input
	 * @param translateColors Whether to {@link ChatColor#translate(String)} the output
	 * @return Translated placeholders
	 */
	public abstract String translatePlaceholders(String input, ChatPluginServerPlayer player, Language language, Set<PlaceholderType> placeholderTypes, boolean translateColors);
	
	/**
	 * Translates an input string with {@link PlaceholderType#PLAYER} placeholders,
	 * formatted for the specified player and translated for the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @param translateColors Whether to {@link ChatColor#translate(String)} the output
	 * @return Translated placeholders
	 */
	public abstract String translatePlayerPlaceholders(String input, ChatPluginServerPlayer player, Language language, boolean translateColors);
	
	/**
	 * Translates an input string with {@link PlaceholderType#SERVER}
	 * placeholders, translated for the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @param translateColors Whether to {@link ChatColor#translate(String)} the output
	 * @return Translated placeholders
	 */
	public abstract String translateServerPlaceholders(String input, Language language, boolean translateColors);
	
	/**
	 * Translates an input string with {@link PlaceholderType#INTEGRATIONS} placeholders,
	 * formatted for the specified player and translated for the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param player Player whose placeholders need to be translated
	 * @param language Language used to translate the placeholders
	 * @param translateColors Whether to {@link ChatColor#translate(String)} the output
	 * @return Translated placeholders
	 */
	public abstract String translateIntegrationsPlaceholders(String input, ChatPluginServerPlayer player, Language language, boolean translateColors);
	
}
