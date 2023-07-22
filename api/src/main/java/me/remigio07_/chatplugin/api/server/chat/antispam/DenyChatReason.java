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

package me.remigio07_.chatplugin.api.server.chat.antispam;

import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07_.chatplugin.api.server.chat.ChatManager;
import me.remigio07_.chatplugin.api.server.chat.FormattedChatManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.manager.VanishManager;

/**
 * Represents the reason why a message may be prevented from being sent.
 */
public enum DenyChatReason {
	
	/**
	 * The message {@link AntiSpamManager#exceedsMaxCapsLength(String)} and
	 * {@link AntiSpamManager#exceedsMaxCapsPercentage(String)} and the sender does not
	 * have the permission "chatplugin.antispam.caps" or "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntiSpamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-caps"</p>
	 */
	CAPS("chat.antispam.no-caps"),
	
	/**
	 * The list {@link AntiSpamManager#getFloodCache()} contains the sender and they do not
	 * have the permission "chatplugin.antispam.flood" or "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntiSpamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-flood"</p>
	 */
	FLOOD("chat.antispam.no-flood"),
	
	/**
	 * When the message {@link FormattedChatManager#containsFormattedText(String)}
	 * and the sender does not have the permission "chatplugin.formatted-chat".
	 * 
	 * <p><strong>Handler:</strong> {@link FormattedChatManager}
	 * <br><strong>Message path:</strong> "chat.no-format"</p>
	 */
	FORMAT("chat.no-format"),
	
	/**
	 * When the message {@link AntiSpamManager#containsDisallowedIP(String)}
	 * and the sender does not have the permission "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntiSpamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-ip"</p>
	 */
	IP_ADDRESS("chat.antispam.no-ip"),
	
	/**
	 * When the sender {@link MuteManager#isMuted(OfflinePlayer, String)}.
	 * 
	 * <p><strong>Handler:</strong> {@link MuteManager}
	 * <br><strong>Message path:</strong> "mute.no-chat"</p>
	 */
	MUTE("mute.no-chat"),
	
	/**
	 * When {@link ChatManager#isChatMuted()}<code> == true</code>.
	 * 
	 * <p><strong>Handler:</strong> {@link ChatManager}
	 * <br><strong>Message path:</strong> "commands.muteall.muted"</p>
	 */
	MUTEALL("commands.muteall.muted"),
	
	/**
	 * The map {@link AntiSpamManager#getSpamCache()} contains the sender and they do not
	 * have the permission "chatplugin.antispam.spam" or "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntiSpamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-spam"</p>
	 */
	SPAM("chat.antispam.no-spam"),
	
	/**
	 * When the message {@link AntiSpamManager#containsBlacklistedWord(String)}
	 * and the sender does not have the permission "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntiSpamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-swear"</p>
	 */
	SWEAR("chat.antispam.no-swear"),
	
	/**
	 * When the message {@link AntiSpamManager#containsDisallowedURL(String)}
	 * and the sender does not have the permission "chatplugin.antispam.bypass".
	 * 
	 * <p><strong>Handler:</strong> {@link AntiSpamManager}
	 * <br><strong>Message path:</strong> "chat.antispam.no-url"</p>
	 */
	URL("chat.antispam.no-url"),
	
	/**
	 * When the sender {@link VanishManager#isVanished(ChatPluginServerPlayer)}.
	 * 
	 * <p><strong>Handler:</strong> {@link VanishManager}
	 * <br><strong>Message path:</strong> "vanish.no-chat"</p>
	 */
	VANISH("vanish.no-chat");
	
	private String messagePath;
	
	private DenyChatReason(String messagePath) {
		this.messagePath = messagePath;
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
	
}
