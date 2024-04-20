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

package me.remigio07.chatplugin.api.server.chat.log;

import java.util.List;

import me.remigio07.chatplugin.api.common.chat.DenyChatReasonHandler;
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
 * Manager that handles {@link LoggedMessage}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#chat-log">ChatPlugin wiki/Modules/Chat/Chat log</a>
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
	 * Gets a logged public messages' list for the specified sender and text.
	 * 
	 * @param sender Public messages' sender
	 * @param timeAgo Public messages' maximum age
	 * @param query Text to search
	 * @return Sender's logged public messages
	 */
	@NotNull
	public abstract List<LoggedPublicMessage> getLoggedPublicMessages(OfflinePlayer sender, long timeAgo, String query);
	
	/**
	 * Gets a logged private messages' list for the specified sender and text.
	 * 
	 * @param sender Private messages' sender
	 * @param timeAgo Private messages' maximum age
	 * @param query Text to search
	 * @return Sender's logged private messages
	 */
	@NotNull
	public abstract List<LoggedPrivateMessage> getLoggedPrivateMessages(OfflinePlayer sender, long timeAgo, String query);
	
	/**
	 * Logs a player's public message and inserts it into {@link DataContainer#PUBLIC_MESSAGES}.
	 * 
	 * <p>Specify <code>null</code> as <code>denyChatReason</code> if the chat
	 * message has not been blocked by a {@link DenyChatReasonHandler}.</p>
	 * 
	 * @param sender Public message's sender
	 * @param publicMessage Public message to log
	 * @param global Whether the message is global
	 * @param denyChatReason Public message's deny chat reason
	 * @throws IllegalArgumentException If the public message's length exceeds 256 characters
	 */
	public abstract void logPublicMessage(
			ChatPluginServerPlayer sender,
			String publicMessage,
			boolean global,
			@Nullable(why = "Public message may not have been blocked") DenyChatReason<?> denyChatReason
			);
	
	/**
	 * Logs a player's private message and inserts it into {@link DataContainer#PRIVATE_MESSAGES}.
	 * 
	 * <p><strong>Note:</strong> messages sent or received by the console cannot be logged.</p>
	 * 
	 * <p>Specify <code>null</code> as <code>denyChatReason</code> if the private
	 * message has not been blocked by a {@link DenyChatReasonHandler}.</p>
	 * 
	 * @param sender Private message's sender
	 * @param recipient Private message's recipient
	 * @param privateMessage Private message to log
	 * @param denyChatReason Private message's deny chat reason
	 * @throws IllegalArgumentException If the private message's length exceeds 256 characters
	 */
	public abstract void logPrivateMessage(
			@NotNull ChatPluginServerPlayer sender,
			@NotNull ChatPluginServerPlayer recipient,
			String privateMessage,
			@Nullable(why = "Private message may not have been blocked") DenyChatReason<?> denyChatReason
			);
	
}
