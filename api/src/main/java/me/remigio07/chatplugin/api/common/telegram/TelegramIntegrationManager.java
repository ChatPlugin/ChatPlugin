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

package me.remigio07.chatplugin.api.common.telegram;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.annotation.SensitiveData;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;

/**
 * Manager that handles the plugin's {@link TelegramBot}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Telegram-integration">ChatPlugin wiki/Modules/Telegram integration</a>
 */
public abstract class TelegramIntegrationManager implements ChatPluginManager, Runnable {
	
	/**
	 * Array containing all the libraries required for this module to work.
	 * 
	 * <p><strong>Content:</strong> [
	 * {@link Library#JAVA_TELEGRAM_BOT_API}, {@link Library#OKIO}, {@link Library#OKHTTP},
	 * {@link Library#KOTLIN_STDLIB}, {@link Library#LOGGING_INTERCEPTOR}, {@link Library#GSON}]</p>
	 */
	public static final Library[] LIBRARIES = new Library[] {
			Library.JAVA_TELEGRAM_BOT_API, Library.OKIO, Library.OKHTTP, Library.KOTLIN_STDLIB, Library.LOGGING_INTERCEPTOR,
			Library.GSON
			};
	protected static TelegramIntegrationManager instance;
	protected boolean enabled;
	protected long chatID, statusUpdateTimeout, statusUpdateTaskID = -1;
	protected String username, statusValue;
	@SensitiveData(warning = "Telegram integration's bot's private token")
	protected String token;
	protected TelegramBot bot;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "settings.enabled" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the bot's chat's ID.
	 * 
	 * <p><strong>Found at:</strong> "settings.chat-id" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 * 
	 * @return Bot's chat's ID
	 */
	public long getChatID() {
		return chatID;
	}
	
	/**
	 * Gets the bot's username.
	 * 
	 * <p><strong>Found at:</strong> "settings.username" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 * 
	 * @return Bot's username
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * Gets the bot's token.
	 * 
	 * <p><strong>Found at:</strong> "settings.token" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 * 
	 * @return Bot's token
	 */
	@SensitiveData(warning = "Telegram integration's bot's private token")
	public String getToken() {
		return token;
	}
	
	/**
	 * Gets the bot's status' value.
	 * 
	 * <p><strong>Found at:</strong> "settings.status.value" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 * 
	 * @return Bot's status' value
	 */
	public String getStatusValue() {
		return statusValue;
	}
	
	/**
	 * Gets the timeout between status updates, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "settings.status.update-timeout-ms" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 * 
	 * @return Time between status updates
	 */
	public long getStatusUpdateTimeout() {
		return statusUpdateTimeout;
	}
	
	/**
	 * Gets the {@link #run()}'s timer's task's ID.
	 * 
	 * <p>You can interact with it using {@link TaskManager}'s methods.</p>
	 * 
	 * @return Updating task's ID
	 */
	public long getStatusUpdateTaskID() {
		return statusUpdateTaskID;
	}
	
	/**
	 * Gets the bot's instance.
	 * 
	 * @return Bot's instance
	 */
	public TelegramBot getBot() {
		return bot;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static TelegramIntegrationManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic status updater, called once every {@link #getStatusUpdateTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Gets the <a href="https://github.com/pengrad/java-telegram-bot-api">Java Telegram Bot API</a>'s version.
	 * 
	 * @return Java Telegram Bot API's version
	 */
	public abstract String getJavaTelegramBotAPIVersion();
	
}
