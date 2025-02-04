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

package me.remigio07.chatplugin.api.common.telegram;

import me.remigio07.chatplugin.api.common.event.telegram.MessageSendEvent;
import me.remigio07.chatplugin.api.common.event.telegram.StatusUpdateEvent;
import me.remigio07.chatplugin.api.common.util.Utils;

/**
 * Represents the Telegram bot handled by the {@link TelegramIntegrationManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Telegram-integration">ChatPlugin wiki/Modules/Telegram integration</a>
 */
public interface TelegramBot {
	
	/**
	 * Loads the Telegram bot.
	 * 
	 * @throws Exception If something goes wrong
	 */
	public void load() throws Exception;
	
	/**
	 * Unloads the Telegram bot.
	 */
	public void unload();
	
	/**
	 * Gets the bot's instance.
	 * 
	 * <p>This method calls {@link TelegramIntegrationManager#getBot()}.</p>
	 * 
	 * @return Bot's instance
	 */
	public static TelegramBot getInstance() {
		return TelegramIntegrationManager.getInstance().getBot();
	}
	
	/**
	 * Sends a message to the specified chat.
	 * 
	 * @param chatID Chat's ID
	 * @param message Message to send
	 * @see MessageSendEvent
	 */
	public void sendMessage(long chatID, String message);
	
	/**
	 * Calls {@link #sendMessage(long, String)} specifying
	 * {@link TelegramIntegrationManager#getChatID()} as the first argument.
	 * 
	 * @param message Message to send
	 * @see MessageSendEvent
	 */
	public default void sendMessage(String message) {
		sendMessage(TelegramIntegrationManager.getInstance().getChatID(), message);
	}
	
	/**
	 * Gets the amount of users in the Telegram group.
	 * 
	 * @return Users' amount
	 */
	public int getUsers();
	
	/**
	 * Gets the title of the specified chat.
	 * 
	 * <p>Will return {@link Utils#NOT_APPLICABLE} if it is invalid.</p>
	 * 
	 * @param chatID Chat's ID
	 * @return Chat's title
	 */
	public String getChatTitle(long chatID);
	
	/**
	 * Updates the bot's current status.
	 * 
	 * @param value Text to display
	 * @see StatusUpdateEvent
	 */
	public void setStatus(String value);
	
}
