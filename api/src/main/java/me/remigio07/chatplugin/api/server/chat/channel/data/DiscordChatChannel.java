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

package me.remigio07.chatplugin.api.server.chat.channel.data;

import me.remigio07.chatplugin.api.common.discord.DiscordBot;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelType;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a Discord-linked {@link ChatChannel}'s data.
 * 
 * <p><strong>Type:</strong> {@link ChatChannelType#DISCORD}</p>
 */
public class DiscordChatChannel extends SocialChatChannel {
	
	/**
	 * Array containing all available placeholders that can
	 * be translated with a Discord channel's information.
	 * 
	 * <p><strong>Content:</strong> ["channel_discord_channel_id", "channel_discord_channel_name"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#placeholders">ChatPlugin wiki/Modules/Chat/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "channel_discord_channel_id", "channel_discord_channel_name" };
	private String discordFormat;
	private long channelID;
	
	/**
	 * Constructs a new Discord chat channel.
	 * 
	 * @param minecraftFormat Minecraft messages' format
	 * @param discordFormat Discord messages' format
	 * @param channelID Discord channel's ID
	 */
	public DiscordChatChannel(
			String minecraftFormat,
			String discordFormat,
			long channelID
			) {
		super(minecraftFormat);
		this.discordFormat = discordFormat;
		this.channelID = channelID;
	}
	
	@Override
	public ChatChannelType getType() {
		return ChatChannelType.DISCORD;
	}
	
	@Override
	public String formatPlaceholders(String input, Language language) {
		return input
				.replace("{channel_discord_channel_id}", String.valueOf(channelID))
				.replace("{channel_discord_channel_name}", DiscordBot.getInstance().getChannelName(channelID));
	}
	
	/**
	 * Gets the format used by in-game
	 * messages sent to the Discord channel.
	 * 
	 * @return Discord messages' format
	 */
	public String getDiscordFormat() {
		return discordFormat;
	}
	
	/**
	 * Gets the Discord channel's ID.
	 * 
	 * @return Discord channel's ID
	 */
	public long getChannelID() {
		return channelID;
	}
	
}
