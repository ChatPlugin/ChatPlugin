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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.chat.log;

import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a logged message handled by the {@link ChatLogManager}.
 */
public abstract class LoggedMessage {
	
	/**
	 * Array containing all available placeholders that can
	 * be translated with a logged message's information. See wiki for more info:
	 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Chat#placeholders">ChatPlugin wiki/Chat/Log/Placeholders</a>
	 * 
	 * <p><strong>Content:</strong> ["player", "player_uuid", "rank_id", "server", "world", "message", "date", "denied", "deny_chat_reason"]</p>
	 */
	public static final String[] PLACEHOLDERS = new String[] { "player", "player_uuid", "rank_id", "server", "world", "message", "date", "denied", "deny_chat_reason" };
	protected OfflinePlayer player;
	protected String rankID, server, world, message;
	protected long date;
	protected DenyChatReason denyChatReason;
	
	/**
	 * Gets this message's sender.
	 * 
	 * @return Message's sender
	 */
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets this message's sender's rank's ID.
	 * 
	 * @return Sender's rank's ID
	 */
	public String getRankID() {
		return rankID;
	}
	
	/**
	 * Gets this message's origin server.
	 * 
	 * @return Message's origin server
	 */
	public String getServer() {
		return server;
	}
	
	/**
	 * Gets this message's origin world.
	 * 
	 * @return Message's origin world
	 */
	public String getWorld() {
		return world;
	}
	
	/**
	 * Gets the message's content.
	 * 
	 * @return Message's content
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Gets this message's sending date, in milliseconds.
	 * 
	 * @return Message's sending date
	 */
	public long getDate() {
		return date;
	}
	
	/**
	 * Gets the reason why the message has been blocked by the antispam.
	 * Will return <code>null</code> if it has not been blocked.
	 * 
	 * @return Message's deny chat reason
	 * @see #isDenied()
	 */
	@Nullable(why = "Message may not have been blocked")
	public DenyChatReason getDenyChatReason() {
		return denyChatReason;
	}
	
	/**
	 * Checks if the message has been blocked by the antispam.
	 * 
	 * @return Whether the message has been denied
	 * @see #getDenyChatReason()
	 */
	public boolean isDenied() {
		return denyChatReason != null;
	}
	
	/**
	 * Translates an input string with this message's specific placeholders.
	 * Check {@link #PLACEHOLDERS} to know the available placeholders.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract String formatPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string list with this message's specific placeholders.
	 * Check {@link #PLACEHOLDERS} to know the available placeholders.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract List<String> formatPlaceholders(List<String> input, Language language);
	
}
