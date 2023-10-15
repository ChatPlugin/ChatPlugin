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

package me.remigio07.chatplugin.api.server.chat.antispam;

import java.util.List;

import me.remigio07.chatplugin.api.common.chat.DenyChatReasonHandler;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.FormattedChatManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;

/**
 * Represents the reason why a message may be prevented from being sent.
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 * 
 * @param <H> Reason's handler
 */
public class DenyChatReason<H extends DenyChatReasonHandler> {
	
	/**
	 * The message {@link AntispamManager#exceedsMaxCapsLength(String)} and
	 * {@link AntispamManager#exceedsMaxCapsPercentage(String)} and the sender does not
	 * have the permission "chatplugin.antispam.caps" or "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-caps"</p>
	 */
	public static final DenyChatReason<AntispamManager> CAPS = new DenyChatReason<>("CAPS", "chat.antispam.no-caps", AntispamManager.class);
	
	/**
	 * The list {@link AntispamManager#getFloodCache()} contains the sender and they do not
	 * have the permission "chatplugin.antispam.flood" or "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-flood"</p>
	 */
	public static final DenyChatReason<AntispamManager> FLOOD = new DenyChatReason<>("FLOOD", "chat.antispam.no-flood", AntispamManager.class);
	
	/**
	 * When the message {@link FormattedChatManager#containsFormattedText(String, List, boolean)}
	 * and the sender does not have the permission "chatplugin.formatted-chat".
	 * 
	 * <p><strong>Handler:</strong> {@link FormattedChatManager}
	 * <br><strong>Message path:</strong> "chat.no-format"</p>
	 */
	public static final DenyChatReason<FormattedChatManager> FORMAT = new DenyChatReason<>("FORMAT", "chat.no-format", FormattedChatManager.class);
	
	/**
	 * When the message {@link AntispamManager#containsDisallowedIP(String)}
	 * and the sender does not have the permission "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-ip"</p>
	 */
	public static final DenyChatReason<AntispamManager> IP_ADDRESS = new DenyChatReason<>("IP_ADDRESS", "chat.antispam.no-ip", AntispamManager.class);
	
	/**
	 * When the sender {@link MuteManager#isMuted(OfflinePlayer, String)}.
	 * 
	 * <p><strong>Handler:</strong> {@link MuteManager}
	 * <br><strong>Message path:</strong> "mute.no-chat"</p>
	 */
	public static final DenyChatReason<MuteManager> MUTE = new DenyChatReason<>("MUTE", "mute.no-chat", MuteManager.class);
	
	/**
	 * When {@link ChatManager#isChatMuted()}<code> == true</code>.
	 * 
	 * <p><strong>Handler:</strong> {@link ChatManager}
	 * <br><strong>Message path:</strong> "commands.muteall.muted"</p>
	 */
	public static final DenyChatReason<ChatManager> MUTEALL = new DenyChatReason<>("MUTEALL", "commands.muteall.muted", ChatManager.class);
	
	/**
	 * The map {@link AntispamManager#getSpamCache()} contains the sender and they do not
	 * have the permission "chatplugin.antispam.spam" or "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-spam"</p>
	 */
	public static final DenyChatReason<AntispamManager> SPAM = new DenyChatReason<>("SPAM", "chat.antispam.no-spam", AntispamManager.class);
	
	/**
	 * When the message {@link AntispamManager#containsBlacklistedWord(String)}
	 * and the sender does not have the permission "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-swear"</p>
	 */
	public static final DenyChatReason<AntispamManager> SWEAR = new DenyChatReason<>("SWEAR", "chat.antispam.no-swear", AntispamManager.class);
	
	/**
	 * When the message {@link AntispamManager#containsDisallowedURL(String)}
	 * and the sender does not have the permission "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-url"</p>
	 */
	public static final DenyChatReason<AntispamManager> URL = new DenyChatReason<>("URL", "chat.antispam.no-url", AntispamManager.class);
	
	/**
	 * When the sender {@link VanishManager#isVanished(ChatPluginServerPlayer)}.
	 * 
	 * <p><strong>Handler:</strong> {@link VanishManager}
	 * <br><strong>Message path:</strong> "vanish.no-chat"</p>
	 */
	public static final DenyChatReason<VanishManager> VANISH = new DenyChatReason<>("VANISH", "vanish.no-chat", VanishManager.class);
	private static final DenyChatReason<?>[] VALUES = new DenyChatReason<?>[] { CAPS, FLOOD, FORMAT, IP_ADDRESS, MUTE, MUTEALL, SPAM, SWEAR, URL, VANISH };
	private String name, messagePath;
	private Class<H> handlerClass;
	
	private DenyChatReason(String name, String messagePath, Class<H> handlerClass) {
		this.name = name;
		this.messagePath = messagePath;
		this.handlerClass = handlerClass;
	}
	
	/**
	 * Equivalent of {@link Enum#name()}.
	 * 
	 * @return Constant's name
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Equivalent of {@link Enum#ordinal()}.
	 * 
	 * @return Constant's ordinal
	 */
	public int ordinal() {
		for (int i = 0; i < VALUES.length; i++)
			if (this == VALUES[i])
				return i;
		return -1;
	}
	
	/**
	 * Gets this deny chat reason's message's path in the messages' files.
	 * 
	 * @return Reason's message's path
	 */
	public String getMessagePath() {
		return messagePath;
	}
	
	/**
	 * Gets this deny chat reason's message translated for the specified language.
	 * 
	 * @param language Language used to translate the message
	 * @return Translated message
	 */
	public String getMessage(Language language) {
		return language.getMessage(messagePath);
	}
	
	/**
	 * Gets this deny chat reason's handler's class.
	 * 
	 * @return Reason's handler's class
	 */
	public Class<H> getHandlerClass() {
		return handlerClass;
	}
	
	/**
	 * Equivalent of <code>Enum#valueOf(String)</code>,
	 * with the only difference that instead of throwing
	 * {@link IllegalArgumentException} <code>null</code>
	 * is returned if the constant's name is invalid.
	 * 
	 * @param name Constant's name
	 * @return Enum constant
	 */
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the constant's name is invalid")
	public static DenyChatReason<?> valueOf(String name) {
		for (DenyChatReason<?> reason : VALUES)
			if (reason.name().equals(name))
				return reason;
		return null;
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static DenyChatReason<?>[] values() {
		return VALUES;
	}
	
}
