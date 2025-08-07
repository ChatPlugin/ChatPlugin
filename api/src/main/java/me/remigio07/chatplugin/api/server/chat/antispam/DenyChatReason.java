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

package me.remigio07.chatplugin.api.server.chat.antispam;

import java.util.List;

import me.remigio07.chatplugin.api.common.chat.DenyChatReasonHandler;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.FormattedChatManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;

/**
 * Represents the reason why a message may be prevented from being sent.
 * 
 * @param <H> Reason's handler
 */
public class DenyChatReason<H extends DenyChatReasonHandler> extends PseudoEnum<DenyChatReason<H>> {
	
	/**
	 * The message {@link String#isEmpty()} after stripping colors and removing spaces.
	 * 
	 * <p><strong>Handler:</strong> {@link ChatManager}
	 * <br><strong>Message path:</strong> "chat.no-blank-messages"</p>
	 */
	public static final DenyChatReason<ChatManager> BLANK_MESSAGE = new DenyChatReason<>("BLANK_MESSAGE", "chat.no-blank-messages", ChatManager.class);
	
	/**
	 * When the message exceeds both {@link AntispamManager#getMaxCapsLength()} and
	 * {@link AntispamManager#getMaxCapsPercentage()} and the sender does not have
	 * the permission "chatplugin.antispam.caps" or "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-caps"</p>
	 */
	public static final DenyChatReason<AntispamManager> CAPS = new DenyChatReason<>("CAPS", "chat.antispam.no-caps", AntispamManager.class);
	
	/**
	 * When {@link AntispamManager#getFloodCache()} contains the sender and they do not
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
	 * When {@link AntispamManager#getDisallowedIPAddress(String)} finds a match
	 * and the sender does not have the permission "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-ip-address"</p>
	 */
	public static final DenyChatReason<AntispamManager> IP_ADDRESS = new DenyChatReason<>("IP_ADDRESS", "chat.antispam.no-ip-address", AntispamManager.class);
	
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
	 * When {@link AntispamManager#getSpamCache()} contains the sender and they do not
	 * have the permission "chatplugin.antispam.spam" or "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-spam"</p>
	 */
	public static final DenyChatReason<AntispamManager> SPAM = new DenyChatReason<>("SPAM", "chat.antispam.no-spam", AntispamManager.class);
	
	/**
	 * When {@link AntispamManager#getDisallowedWord(String)} finds a match and
	 * the sender does not have the permission "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-swear"</p>
	 */
	public static final DenyChatReason<AntispamManager> SWEAR = new DenyChatReason<>("SWEAR", "chat.antispam.no-swear", AntispamManager.class);
	
	/**
	 * When {@link AntispamManager#getDisallowedURL(String)} finds a match and
	 * the sender does not have the permission "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntispamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-url"</p>
	 */
	public static final DenyChatReason<AntispamManager> URL = new DenyChatReason<>("URL", "chat.antispam.no-url", AntispamManager.class);
	
	/**
	 * When the sender {@link ChatPluginServerPlayer#isVanished()}.
	 * 
	 * <p><strong>Handler:</strong> {@link VanishManager}
	 * <br><strong>Message path:</strong> "vanish.no-chat"</p>
	 */
	public static final DenyChatReason<VanishManager> VANISH = new DenyChatReason<>("VANISH", "vanish.no-chat", VanishManager.class);
	private static final DenyChatReason<?>[] VALUES = new DenyChatReason<?>[] { BLANK_MESSAGE, CAPS, FLOOD, FORMAT, IP_ADDRESS, MUTE, MUTEALL, SPAM, SWEAR, URL, VANISH };
	private static int ordinal = 0;
	private String messagePath;
	private Class<H> handlerClass;
	
	private DenyChatReason(String name, String messagePath, Class<H> handlerClass) {
		super(name, ordinal++);
		this.messagePath = messagePath;
		this.handlerClass = handlerClass;
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
	 * Equivalent of <code>valueOf(String)</code>.
	 * 
	 * @param name Constant's name
	 * @return Pseudo-enum's constant
	 * @throws NullPointerException If <code>name == null</code>
	 * @throws IllegalArgumentException If {@link #values()}
	 * does not contain a constant with the specified name
	 */
	public static DenyChatReason<? extends DenyChatReasonHandler> valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static DenyChatReason<? extends DenyChatReasonHandler>[] values() {
		return VALUES;
	}
	
}
