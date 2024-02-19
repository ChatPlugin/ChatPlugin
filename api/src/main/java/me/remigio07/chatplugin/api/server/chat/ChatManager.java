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

package me.remigio07.chatplugin.api.server.chat;

import java.util.Collections;
import java.util.List;

import me.remigio07.chatplugin.api.common.chat.DenyChatReasonHandler;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.event.chat.AllowChatEvent;
import me.remigio07.chatplugin.api.server.event.chat.DenyChatEvent;
import me.remigio07.chatplugin.api.server.event.chat.PreChatEvent;
import me.remigio07.chatplugin.api.server.event.chat.ToggleChatMuteEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles the chat.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat">ChatPlugin wiki/Modules/Chat</a>
 */
public abstract class ChatManager implements DenyChatReasonHandler {
	
	protected static ChatManager instance;
	protected boolean enabled, chatMuted;
	protected String format, consoleFormat;
	protected List<String> recognizedTLDs = Collections.emptyList();
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the chat is globally muted.
	 * 
	 * @return Whether the chat is muted
	 */
	public boolean isChatMuted() {
		return chatMuted;
	}
	
	/**
	 * Gets the format used to send messages to players.
	 * 
	 * <p><strong>Found at:</strong> "chat.format" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Chat's format
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * Gets the format used to send messages to the console.
	 * 
	 * <p><strong>Found at:</strong> "chat.console-format" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Chat's console format
	 */
	public String getConsoleFormat() {
		return consoleFormat;
	}
	
	/**
	 * Gets the list of recognized <a href="https://en.wikipedia.org/wiki/Top-level_domain">TLD</a>s in the chat.
	 * 
	 * <p>The returned list is used by the {@link AntispamManager} and the {@link HoverInfoManager}.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.recognized-tlds" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Recognized TLDs in the chat
	 */
	public List<String> getRecognizedTLDs() {
		return recognizedTLDs;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate messages sent in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.placeholder-types" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Placeholders used to translate messages
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ChatManager getInstance() {
		return instance;
	}
	
	/**
	 * Sets whether the chat should be globally muted.
	 * 
	 * @param chatMuted Whether the chat should be muted
	 * @see ToggleChatMuteEvent
	 */
	public abstract void setChatMuted(boolean chatMuted);
	
	/**
	 * Handles and processes a chat event.
	 * 
	 * <p>This method will consider that some
	 * players may be ignored by other players.</p>
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @see PreChatEvent
	 * @see AllowChatEvent
	 * @see DenyChatEvent
	 */
	public abstract void handleChatEvent(ChatPluginServerPlayer player, String message);
	
}
