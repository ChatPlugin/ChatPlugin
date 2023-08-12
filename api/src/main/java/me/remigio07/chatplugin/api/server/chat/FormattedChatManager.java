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

package me.remigio07.chatplugin.api.server.chat;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;

/**
 * Manager that handles formatted and colored messages in the chat. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Chat#formatting-codes">ChatPlugin wiki/Chat/Formatting codes</a>
 */
public abstract class FormattedChatManager implements ChatPluginManager {
	
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
	 * Checks if formatted messages should be always sent, even if players
	 * do not have the permission "chatplugin.formatted-chat". In that case,
	 * the message will be sent not formatted and they will receive a warning.
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
	 * Checks if the specified String contains colored or formatted text.
	 * 
	 * @param input Input String
	 * @return Whether the input contains formatted text
	 */
	public abstract boolean containsFormattedText(String input);
	
}
