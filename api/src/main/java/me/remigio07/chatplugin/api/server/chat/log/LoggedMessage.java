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

package me.remigio07.chatplugin.api.server.chat.log;

import java.util.List;

import me.remigio07.chatplugin.api.common.chat.DenyChatReasonHandler;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a logged message.
 * 
 * @see LoggedPublicMessage
 * @see LoggedPrivateMessage
 * @see ChatLogManager
 */
public interface LoggedMessage extends Comparable<LoggedMessage> {
	
	/**
	 * Compares two logged messages based on their
	 * {@link #getDate()}; older messages first.
	 */
	@Override
	public default int compareTo(LoggedMessage o) {
		return getDate() < o.getDate() ? -1 : getDate() == o.getDate() ? 0 : 1;
	}
	
	/**
	 * Gets this message's sender.
	 * 
	 * @return Message's sender
	 */
	public OfflinePlayer getSender();
	
	/**
	 * Gets this message's sender's rank's ID.
	 * 
	 * @return Sender's rank's ID
	 */
	public String getRankID();
	
	/**
	 * Gets this message's origin server.
	 * 
	 * @return Message's origin server
	 */
	public String getServer();
	
	/**
	 * Gets this message's origin world.
	 * 
	 * @return Message's origin world
	 */
	public String getWorld();
	
	/**
	 * Gets the message's content.
	 * 
	 * @return Message's content
	 */
	public String getContent();
	
	/**
	 * Gets this message's sending date, in milliseconds.
	 * 
	 * @return Message's sending date
	 */
	public long getDate();
	
	/**
	 * Gets the reason why the message has been
	 * blocked by a {@link DenyChatReasonHandler}.
	 * 
	 * <p>Will return <code>null</code> if it has not been blocked.</p>
	 * 
	 * @return Message's deny chat reason
	 * @see #isDenied()
	 */
	@Nullable(why = "Message may not have been blocked")
	public DenyChatReason<?> getDenyChatReason();
	
	/**
	 * Checks if the message has been blocked
	 * by a {@link DenyChatReasonHandler}.
	 * 
	 * @return Whether the message has been denied
	 * @see #getDenyChatReason()
	 */
	public default boolean isDenied() {
		return getDenyChatReason() != null;
	}
	
	/**
	 * Translates an input string with this message's specific placeholders.
	 * 
	 * <p>Every logged message has different placeholders available. Check the following fields:
	 * 	<ul>
	 * 		<li>{@link LoggedPublicMessage#PLACEHOLDERS} - public messages' placeholders</li>
	 * 		<li>{@link LoggedPrivateMessage#PLACEHOLDERS} - private messages' placeholders</li>
	 * 	</ul>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public String formatPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string list with this message's specific placeholders.
	 * 
	 * <p>Every logged message has different placeholders available. Check the following fields:
	 * 	<ul>
	 * 		<li>{@link LoggedPublicMessage#PLACEHOLDERS} - public messages' placeholders</li>
	 * 		<li>{@link LoggedPrivateMessage#PLACEHOLDERS} - private messages' placeholders</li>
	 * 	</ul>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public List<String> formatPlaceholders(List<String> input, Language language);
	
}
