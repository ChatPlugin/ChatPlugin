/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.api.server.chat.channel.data;

import me.remigio07.chatplugin.api.common.telegram.TelegramBot;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelType;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a Telegram-linked {@link ChatChannel}'s data.
 * 
 * <p><strong>Type:</strong> {@link ChatChannelType#TELEGRAM}</p>
 */
public class TelegramChatChannel extends SocialChatChannel {
	
	/**
	 * Array containing all available placeholders that can
	 * be translated with a Telegram channel's information.
	 * 
	 * <p><strong>Content:</strong> ["channel_telegram_chat_id", "channel_telegram_chat_title"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#placeholders">ChatPlugin wiki/Modules/Chat/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "channel_telegram_chat_id", "channel_telegram_chat_title" };
	private String telegramFormat;
	private long chatID;
	
	/**
	 * Constructs a new Telegram chat channel.
	 * 
	 * @param minecraftFormat Minecraft messages' format
	 * @param telegramFormat Telegram messages' format
	 * @param chatID Telegram chat's ID
	 */
	public TelegramChatChannel(
			String minecraftFormat,
			String telegramFormat,
			long chatID
			) {
		super(minecraftFormat);
		this.telegramFormat = telegramFormat;
		this.chatID = chatID;
	}
	
	@Override
	public ChatChannelType getType() {
		return ChatChannelType.TELEGRAM;
	}
	
	@Override
	public String formatPlaceholders(String input, Language language) {
		return input
				.replace("{channel_telegram_chat_id}", String.valueOf(chatID))
				.replace("{channel_telegram_chat_title}", TelegramBot.getInstance().getChatTitle(chatID));
	}
	
	/**
	 * Gets the format used by in-game
	 * messages sent to the Telegram chat.
	 * 
	 * @return Telegram messages' format
	 */
	public String getTelegramFormat() {
		return telegramFormat;
	}
	
	/**
	 * Gets the Telegram chat's ID.
	 * 
	 * @return Telegram chat's ID
	 */
	public long getChatID() {
		return chatID;
	}
	
}
