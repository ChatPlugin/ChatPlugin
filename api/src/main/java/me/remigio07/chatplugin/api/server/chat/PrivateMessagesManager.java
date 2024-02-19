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

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.event.chat.AllowPrivateMessageEvent;
import me.remigio07.chatplugin.api.server.event.chat.DenyPrivateMessageEvent;
import me.remigio07.chatplugin.api.server.event.chat.PrePrivateMessageEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Manager that handles private messages.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#private-messages">ChatPlugin wiki/Modules/Chat/Private messages</a>
 */
public abstract class PrivateMessagesManager implements ChatPluginManager {
	
	protected static PrivateMessagesManager instance;
	protected boolean enabled, soundEnabled, socialspyOnJoinEnabled;
	protected String sentChatFormat, sentTerminalFormat, receivedChatFormat, receivedTerminalFormat, socialspyChatFormat, socialspyTerminalFormat;
	protected SoundAdapter sound;
	protected List<DenyChatReason<AntispamManager>> bypassAntispamChecks = Collections.emptyList();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if players should hear a sound when receiving a private message.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.sound.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether players should hear a sound when receiving a private message
	 */
	public boolean isSoundEnabled() {
		return soundEnabled;
	}
	
	/**
	 * Checks if socialspy should be enabled for players who join the
	 * server and have the permission "chatplugin.commands.socialspy".
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.socialspy-on-join-enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether socialspy should be enabled on join
	 */
	public boolean isSocialspyOnJoinEnabled() {
		return socialspyOnJoinEnabled;
	}
	
	/**
	 * Gets the chat format displayed to players when
	 * they send a private message to someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.sent.chat"</p>
	 * 
	 * @return Sent messages' chat format
	 */
	public String getSentChatFormat() {
		return sentChatFormat;
	}
	
	/**
	 * Gets the chat format displayed in the terminal when
	 * the console sends a private message to someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.sent.terminal"</p>
	 * 
	 * @return Sent messages' terminal format
	 */
	public String getSentTerminalFormat() {
		return sentTerminalFormat;
	}
	
	/**
	 * Gets the chat format displayed to players when
	 * they receive a private message from someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.received.chat"</p>
	 * 
	 * @return Received messages' chat format
	 */
	public String getReceivedChatFormat() {
		return receivedChatFormat;
	}
	
	/**
	 * Gets the chat format displayed in the terminal when
	 * the console receives a private message from someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.received.terminal"</p>
	 * 
	 * @return Received messages' terminal format
	 */
	public String getReceivedTerminalFormat() {
		return receivedTerminalFormat;
	}
	
	/**
	 * Gets the chat format displayed to Staff members when
	 * players send a private message to someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.socialspy.chat"</p>
	 * 
	 * @return Socialspy's chat format
	 */
	public String getSocialspyChatFormat() {
		return socialspyChatFormat;
	}
	
	/**
	 * Gets the chat format displayed in the terminal when
	 * players send a private message to someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.socialspy.terminal"</p>
	 * 
	 * @return Socialspy's terminal format
	 */
	public String getSocialspyTerminalFormat() {
		return socialspyTerminalFormat;
	}
	
	/**
	 * Gets the sound that private messages will produce.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.sound" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Private messages' sound
	 */
	public SoundAdapter getSound() {
		return sound;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static PrivateMessagesManager getInstance() {
		return instance;
	}
	
	/**
	 * Sends a private message.
	 * 
	 * <p>Specify <code>null</code> as either the sender
	 * or the recipient to indicate the console.</p>
	 * 
	 * <p>This method will <strong>not</strong> consider that
	 * some players may be ignored by other players.</p>
	 * 
	 * @param sender Private message's sender
	 * @param recipient Private message's recipient
	 * @param privateMessage Private message to send
	 * @throws IllegalArgumentException If the sender and the recipient correspond
	 * @see PrePrivateMessageEvent
	 * @see AllowPrivateMessageEvent
	 * @see DenyPrivateMessageEvent
	 */
	public abstract void sendPrivateMessage(
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer sender,
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer recipient,
			String privateMessage
			);
	
}
