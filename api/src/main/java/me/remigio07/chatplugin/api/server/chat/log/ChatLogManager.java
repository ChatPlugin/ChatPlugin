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

package me.remigio07.chatplugin.api.server.chat.log;

import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles {@link LoggedMessage}s. See wiki for more info:
 * <br><a href="https://github.com/ChatPlugin/ChatPlugin/wiki/Chat#log">ChatPlugin wiki/Chat/Log</a>
 */
public abstract class ChatLogManager implements ChatPluginManager {
	
	protected static ChatLogManager instance;
	protected boolean enabled, printToLogFile;
	protected long messagesAutoCleanerPeriod = -1, loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.log.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if messages should be printed to {@link LogManager#getFile()}.
	 * 
	 * <p><strong>Found at:</strong> "chat.log.print-to-log-file" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether messages should be printed to log file
	 */
	public boolean isPrintToLogFile() {
		return printToLogFile;
	}
	
	/**
	 * Gets the period of time after which old messages will be
	 * removed from the storage by the auto cleaner, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "chat.log.messages-auto-cleaner-period" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Messages' auto cleaner's period
	 */
	public long getMessagesAutoCleanerPeriod() {
		return messagesAutoCleanerPeriod;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ChatLogManager getInstance() {
		return instance;
	}
	
	/**
	 * Gets a logged messages' list for the specified player and text.
	 * 
	 * @param player Target player
	 * @param timeAgo Messages' maximum age
	 * @param query Text to search
	 * @return Player's logged messages
	 */
	@NotNull
	public abstract List<LoggedMessage> getLoggedMessages(OfflinePlayer player, long timeAgo, String query);
	
	/**
	 * Logs a player's message and inserts it into {@link DataContainer#MESSAGES}.
	 * Specify <code>null</code> as <code>denyChatReason</code>
	 * if the message has not been blocked by the antispam.
	 * 
	 * @param player Target player
	 * @param message Message to log
	 * @param denyChatReason Message's deny chat reason
	 */
	public abstract void logMessage(ChatPluginServerPlayer player, String message, @Nullable(why = "Message may not have been blocked") DenyChatReason denyChatReason);
	
}
