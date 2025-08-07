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

package me.remigio07.chatplugin.api.server.chat;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.event.chat.AllowPrivateMessageEvent;
import me.remigio07.chatplugin.api.server.event.chat.DenyPrivateMessageEvent;
import me.remigio07.chatplugin.api.server.event.chat.PrePrivateMessageEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Manager that handles private messages.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#private-messages">ChatPlugin wiki/Modules/Chat/Private messages</a>
 */
public abstract class PrivateMessagesManager implements ChatPluginManager {
	
	protected static PrivateMessagesManager instance;
	protected boolean enabled, soundEnabled, advancementsEnabled, advancementsIconGlowing, socialspyOnJoinEnabled, mutedPlayersBlocked, replyToLastSender;
	protected String sentFormat, receivedFormat, socialspyFormat, senderPlaceholderFormat, recipientPlaceholderFormat, advancementsFormat;
	protected Set<PlaceholderType> placeholderPlaceholderTypes = Collections.emptySet();
	protected SoundAdapter sound;
	protected int advancementsMaxMessageLength;
	protected MaterialAdapter advancementsIconMaterial;
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
	 * Checks if private messages received by players
	 * should also be displayed as custom advancements.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.advancements.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether advancements are enabled
	 */
	public boolean areAdvancementsEnabled() {
		return advancementsEnabled;
	}
	
	/**
	 * Checks if the glowing effect should be applied to
	 * the icon of the advancement displayed to players.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.advancements.icon.glowing" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether the advancements' icon should have the glowing effect
	 */
	public boolean isAdvancementsIconGlowing() {
		return advancementsIconGlowing;
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
	 * Checks if private messages sent by muted players should be blocked.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.muted-players-blocked" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether to block private messages sent by muted players
	 */
	public boolean areMutedPlayersBlocked() {
		return mutedPlayersBlocked;
	}
	
	/**
	 * Checks if <code>/reply</code> should send a message to the last sender.
	 * 
	 * <p>Will return <code>false</code> if it should send a message to the last recipient.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.reply-to-last-sender" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether to reply to the last sender
	 */
	public boolean isReplyToLastSender() {
		return replyToLastSender;
	}
	
	/**
	 * Gets the chat format displayed to players and the console
	 * when they send a private message to someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.sent" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Sent messages' format
	 */
	public String getSentFormat() {
		return sentFormat;
	}
	
	/**
	 * Gets the chat format displayed to players and the console
	 * when they receive a private message from someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.received" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Received messages' format
	 */
	public String getReceivedFormat() {
		return receivedFormat;
	}
	
	/**
	 * Gets the chat format displayed to Staff members and the
	 * console when players send a private message to someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.socialspy" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Socialspy's format
	 */
	public String getSocialspyFormat() {
		return socialspyFormat;
	}
	
	/**
	 * Gets the format of the "{sender}" placeholder.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.placeholder.sender" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Sender placeholder's format
	 */
	public String getSenderPlaceholderFormat() {
		return senderPlaceholderFormat;
	}
	
	/**
	 * Gets the format of the "{recipient}" placeholder.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.placeholder.recipient" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Recipient placeholder's format
	 */
	public String getRecipientPlaceholderFormat() {
		return recipientPlaceholderFormat;
	}
	
	/**
	 * Gets the advancement format displayed to players when
	 * they receive a private message from someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.advancements.format" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Received private messages' advancement format
	 */
	public String getAdvancementsFormat() {
		return advancementsFormat;
	}
	
	/**
	 * Gets the set of placeholder types used to
	 * translate {@link #getSenderPlaceholderFormat()}
	 * and {@link #getRecipientPlaceholderFormat()}.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.format.placeholder.placeholder-types" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Placeholders used to translate sender and recipient placeholders
	 */
	public Set<PlaceholderType> getPlaceholderPlaceholderTypes() {
		return placeholderPlaceholderTypes;
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
	 * Gets the max length of private messages displayed as custom advancements.
	 * 
	 * <p>Longer messages will be abbreviated by adding "..." at the end.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.advancements.max-message-length" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Private messages' max length for advancements
	 */
	public int getAdvancementsMaxMessageLength() {
		return advancementsMaxMessageLength;
	}
	
	/**
	 * Gets the material of the icon of the advancement displayed to
	 * players when they receive a private message from someone else.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.advancements.icon.material" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Advancements' icon's material
	 */
	public MaterialAdapter getAdvancementsIconMaterial() {
		return advancementsIconMaterial;
	}
	
	/**
	 * Gets the antispam's checks to bypass
	 * when checking the private messages.
	 * 
	 * <p><strong>Found at:</strong> "chat.private-messages.bypass-antispam-checks" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Private messages' antispam's checks
	 */
	public List<DenyChatReason<AntispamManager>> getBypassAntispamChecks() {
		return bypassAntispamChecks;
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
	 * <p>This method will consider that some players may
	 * be ignored by other players, vanished or muted.</p>
	 * 
	 * @param sender Private message's sender
	 * @param recipient Private message's recipient
	 * @param privateMessage Private message to send
	 * @throws IllegalArgumentException If the sender and the recipient
	 * correspond or if the private message's length exceeds 505 characters
	 * @see PrePrivateMessageEvent
	 * @see AllowPrivateMessageEvent
	 * @see DenyPrivateMessageEvent
	 */
	public abstract void sendPrivateMessage(
			@Nullable(why = "Null to represent the console") ChatPluginServerPlayer sender,
			@Nullable(why = "Null to represent the console") OfflinePlayer recipient,
			String privateMessage
			);
	
	/**
	 * Sends a reply to {@link ChatPluginServerPlayer#getLastCorrespondent()}.
	 * 
	 * <p>This method will consider that some players may
	 * be ignored by other players, vanished or muted.</p>
	 * 
	 * @param sender Private message's sender
	 * @param privateMessage Private message to send.
	 * @throws IllegalStateException If <code>{@link ChatPluginServerPlayer#getLastCorrespondent()} == null</code>
	 * @throws IllegalArgumentException If the private message's length exceeds 505 characters
	 * @see PrePrivateMessageEvent
	 * @see AllowPrivateMessageEvent
	 * @see DenyPrivateMessageEvent
	 */
	public abstract void sendReply(
			@NotNull ChatPluginServerPlayer sender,
			String privateMessage
			);
	
}
