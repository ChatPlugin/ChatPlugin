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

import java.util.List;

import me.remigio07.chatplugin.api.common.chat.DenyChatReasonHandler;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.server.util.URLValidator;

/**
 * Manager that handles formatted and colored messages in the chat.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#formatted-chat">ChatPlugin wiki/Modules/Chat/Formatted chat</a>
 */
public abstract class FormattedChatManager implements DenyChatReasonHandler {
	
	protected static FormattedChatManager instance;
	protected boolean enabled, sendAnyway;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.formatted-chat.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if formatted messages should be always sent, even if
	 * players do not have the permission "chatplugin.formatted-chat".
	 * 
	 * <p>In that case, the message will be sent but not
	 * formatted and they will receive a warning.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.formatted-chat.send-anyway" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether a not allowed formatted message will be sent anyway
	 */
	public boolean isSendAnyway() {
		return sendAnyway;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static FormattedChatManager getInstance() {
		return instance;
	}
	
	/**
	 * Checks if the specified String contains '&amp;'-formatted text.
	 * 
	 * <p>Specify <code>true</code> as <code>considerURLs</code> to consider the possibility that
	 * the specified input may also contain URLs which may contain '&amp;' characters (example:
	 * "<a href="https://duckduckgo.com/?q=jynxzi&va=n">https://duckduckgo.com/?q=jynxzi&amp;va=n</a>").
	 * In that case, they will not be considered format characters.</p>
	 * 
	 * @param input Input String
	 * @param urls URLs contained in the input
	 * @param considerURLs Whether to consider URLs in the input
	 * @return Whether the input contains formatted text
	 * @see URLValidator#getURLs(String)
	 */
	public abstract boolean containsFormattedText(String input, List<String> urls, boolean considerURLs);
	
	/**
	 * Translates the specified String containing colored or formatted text.
	 * 
	 * <p>Specify <code>true</code> as <code>considerURLs</code> to consider the possibility that
	 * the specified input may also contain URLs which may contain '&amp;' characters (example:
	 * "<a href="https://duckduckgo.com/?q=jynxzi&va=n">https://duckduckgo.com/?q=jynxzi&amp;va=n</a>").
	 * In that case, they will not be counted as format characters.</p>
	 * 
	 * @param input Input String
	 * @param urls URLs contained in the input
	 * @param considerURLs Whether to consider URLs in the input
	 * @return Translated String containing formatted text
	 * @see URLValidator#getURLs(String)
	 */
	public abstract String translate(String input, List<String> urls, boolean considerURLs);
	
}
