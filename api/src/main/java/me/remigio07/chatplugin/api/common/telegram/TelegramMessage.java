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

package me.remigio07.chatplugin.api.common.telegram;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;

/**
 * Represents a message type sent by the {@link TelegramBot}.
 * 
 * @see TelegramMessages
 */
public interface TelegramMessage {
	
	/**
	 * Checks if this message is enabled and should be sent.
	 * Will return true if nothing is specified in {@link ConfigurationType#TELEGRAM_INTEGRATION}
	 * at path <code>"{@link #getPath()} + enabled"</code>; example: "messages.ban.info.enabled".
	 * 
	 * @return Whether this message is enabled
	 */
	public default boolean isEnabled() {
		return ConfigurationType.TELEGRAM_INTEGRATION.get().getBoolean(getPath() + "enabled", true);
	}
	
	/**
	 * Gets this message's path in {@link ConfigurationType#TELEGRAM_INTEGRATION}.
	 * Does not include a dot at the end; example: "messages.ban.info".
	 * 
	 * @return Message's path
	 * @throws UnsupportedOperationException If this message has no path
	 */
	public String getPath();
	
	/**
	 * Gets this message's value.
	 * 
	 * @param args Message's specific arguments
	 * @return Resulting message
	 * @throws UnsupportedOperationException If this message has no path
	 */
	public String getValue(Object... args);
	
}
