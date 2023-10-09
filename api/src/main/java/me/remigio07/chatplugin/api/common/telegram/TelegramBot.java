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

import me.remigio07.chatplugin.api.common.event.telegram.MessageSendEvent;
import me.remigio07.chatplugin.api.common.event.telegram.StatusUpdateEvent;

/**
 * Represents the Telegram bot handled by the {@link TelegramIntegrationManager}.
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
	 * This method calls {@link TelegramIntegrationManager#getBot()}.
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
	 * Gets the amount of users in the Telegram group.
	 * 
	 * @return Users' amount
	 */
	public int getUsersAmount();
	
	/**
	 * Updates the bot's current status.
	 * 
	 * @param value Text to display
	 * @see StatusUpdateEvent
	 */
	public void setStatus(String value);
	
}
