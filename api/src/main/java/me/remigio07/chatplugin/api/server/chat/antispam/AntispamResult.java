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

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents the result of {@link AntispamManager#check(ChatPluginServerPlayer, String, List)}
 */
public abstract class AntispamResult {
	
	protected DenyChatReason<AntispamManager> reason;
	protected String disallowedText, highlightedMessage;
	
	/**
	 * Gets the reason why the message should be blocked.
	 * 
	 * <p>Will return <code>null</code> if {@link #isAllowed()}.</p>
	 * 
	 * @return Reason why the message should be blocked
	 */
	@Nullable(why = "The message may not have been blocked")
	public DenyChatReason<AntispamManager> getReason() {
		return reason;
	}
	
	/**
	 * Gets the disallowed text in the message that triggered the antispam.
	 * 
	 * <p>Will return <code>null</code> if {@link #isAllowed()}.</p>
	 * 
	 * @return Disallowed text in the message
	 */
	@Nullable(why = "The message may not have been blocked")
	public String getDisallowedText() {
		return disallowedText;
	}
	
	/**
	 * Gets the message with highlighted disallowed text.
	 * 
	 * <p>Will return <code>null</code> if {@link #isAllowed()}.</p>
	 * 
	 * @return Message with highlighted disallowed text
	 */
	@Nullable(why = "The message may not have been blocked")
	public String getHighlightedMessage() {
		return highlightedMessage;
	}
	
	/**
	 * Checks if the message is allowed by the antispam.
	 * 
	 * @return Whether the message is allowed
	 */
	public boolean isAllowed() {
		return reason == null;
	}
	
}
