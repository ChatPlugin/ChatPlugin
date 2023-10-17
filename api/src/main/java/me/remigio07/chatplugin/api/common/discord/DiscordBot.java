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

package me.remigio07.chatplugin.api.common.discord;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import javax.security.auth.login.LoginException;

import me.remigio07.chatplugin.api.common.event.discord.EmbedMessageSendEvent;
import me.remigio07.chatplugin.api.common.event.discord.PlainMessageSendEvent;
import me.remigio07.chatplugin.api.common.event.discord.StatusUpdateEvent;

/**
 * Represents the Discord bot handled by the {@link DiscordIntegrationManager}.
 */
public interface DiscordBot {
	
	/**
	 * Gets a new embed message.
	 * 
	 * <p>This method returns an {@link Object} because libraries' classes
	 * cannot be accessed directly from the API, but it is an instance
	 * of <code>net.dv8tion.jda.api.entities.MessageEmbed</code>.</p>
	 * 
	 * <p>The first list should contain 11 elements: it will call
	 * {@link #newEmbedMessage(String, String, String, List, String, String, String, String, String, String, String, Color)}
	 * passing every element in the list as an argument and <code>fields</code> as the fourth argument.
	 * The last element in the first list (<code>color</code>) will be transformed into a {@link Color} using
	 * {@link Color#decode(String)}. The two lists and their elements may be <code>null</code>.</p>
	 * 
	 * @param values Message's values
	 * @param fields Message's fields
	 * @return New embed message
	 * @throws IndexOutOfBoundsException If the first list does not contain at least 11 elements
	 * @see FieldAdapter
	 */
	public default Object newEmbedMessage(List<String> values, List<FieldAdapter> fields) {
		return newEmbedMessage(
				values.get(0),
				values.get(1),
				values.get(2),
				fields == null ? Collections.emptyList() : fields,
				values.get(3),
				values.get(4),
				values.get(5),
				values.get(6),
				values.get(7),
				values.get(8),
				values.get(9),
				values.get(10) == null ? Color.WHITE : Color.decode("#" + values.get(10))
				);
	}
	
	/**
	 * Sends a punishment to the {@link DiscordIntegrationManager#getPunishmentsChannelID()} channel.
	 * 
	 * @param message Message to send
	 * @param args Message's specific arguments
	 */
	public default void sendPunishment(DiscordMessage message, Object... args) {
		sendEmbedMessage(DiscordIntegrationManager.getInstance().getPunishmentsChannelID(), message, args);
	}
	
	/**
	 * Sends a Staff notification to the {@link DiscordIntegrationManager#getStaffNotificationsChannelID()} channel.
	 * 
	 * @param message Message to send
	 * @param args Message's specific arguments
	 */
	public default void sendStaffNotification(DiscordMessage message, Object... args) {
		sendEmbedMessage(DiscordIntegrationManager.getInstance().getStaffNotificationsChannelID(), message, args);
	}
	
	/**
	 * Gets the bot's instance.
	 * 
	 * <p>This method calls {@link DiscordIntegrationManager#getBot()}.</p>
	 * 
	 * @return Bot's instance
	 */
	public static DiscordBot getInstance() {
		return DiscordIntegrationManager.getInstance().getBot();
	}
	
	/**
	 * Loads the Discord bot.
	 * 
	 * @throws LoginException If the provided token is invalid
	 * @throws InterruptedException If the bot is killed while waiting
	 */
	public void load() throws LoginException, InterruptedException;
	
	/**
	 * Unloads the Discord bot.
	 */
	public void unload();
	
	/**
	 * Sends a plain message to the specified channel.
	 * 
	 * @param channelID Channel's ID
	 * @param message Message to send
	 * @see PlainMessageSendEvent
	 */
	public void sendPlainMessage(long channelID, String message);
	
	/**
	 * Sends an embed message to the specified channel.
	 * 
	 * @param channelID Channel's ID
	 * @param embed Embed to send
	 * @param args Embed's specific arguments
	 * @see #sendEmbedMessage(long, Object)
	 */
	public void sendEmbedMessage(long channelID, DiscordMessage embed, Object... args);
	
	/**
	 * Sends an embed message to the specified channel.
	 * 
	 * <p>The parameter is an {@link Object} because libraries' classes
	 * cannot be accessed directly from the API, but it is an instance
	 * of <code>net.dv8tion.jda.api.entities.MessageEmbed</code>.</p>
	 * 
	 * @param channelID Channel's ID
	 * @param embed Embed to send
	 * @see #sendEmbedMessage(long, DiscordMessage, Object...)
	 * @see EmbedMessageSendEvent
	 */
	public void sendEmbedMessage(long channelID, Object embed);
	
	/**
	 * Gets the bot's JDA instance.
	 * 
	 * <p>This method returns an {@link Object} because libraries'
	 * classes cannot be accessed directly from the API, but it is
	 * an instance of <code>net.dv8tion.jda.api.JDA</code>.</p>
	 * 
	 * @return JDA instance
	 */
	public Object getJDA();
	
	/**
	 * Gets the amount of users currently
	 * online on the Discord server.
	 * 
	 * @return Online users' amount
	 */
	public int getOnlineUsers();
	
	/**
	 * Gets a new embed message.
	 * 
	 * <p>This method returns an {@link Object} because libraries' classes
	 * cannot be accessed directly from the API, but it is an instance
	 * of <code>net.dv8tion.jda.api.entities.MessageEmbed</code>.</p>
	 * 
	 * <p>Every passed argument may be <code>null</code>: in this case its value
	 * will not be set, except for the color that defaults to {@link Color#WHITE}.</p>
	 * 
	 * @param title Message's title
	 * @param titleURL Message's title's URL
	 * @param description Message's description
	 * @param fields Message's fields
	 * @param imageURL Message's image's URL
	 * @param thumbnailIconURL Message's thumbnail icon's URL
	 * @param author Message's author
	 * @param authorURL Message's author's URL
	 * @param authorIconURL Message's author's icon's URL
	 * @param footer Message's footer
	 * @param footerIconURL Message's footer's icon's URL
	 * @param color Message's side color
	 * @return New embed message
	 * @see FieldAdapter
	 */
	public Object newEmbedMessage(String title, String titleURL, String description, List<FieldAdapter> fields, String imageURL, String thumbnailIconURL, String author, String authorURL, String authorIconURL, String footer, String footerIconURL, Color color);
	
	/**
	 * Serializes the specified <code>net.dv8tion.jda.api.entities.MessageEmbed</code> to a JSON string.
	 * 
	 * <p>All the options ({@link DiscordMessages#EMBED_OPTIONS}) are currently supported.
	 * The parameter is an {@link Object} because libraries' classes cannot be accessed
	 * directly from the API: just ignore that and consider it an embed.</p>
	 * 
	 * @param embed Message to serialize
	 * @return Serialized JSON string
	 * @throws IllegalArgumentException If passed object is not
	 * a <code>net.dv8tion.jda.api.entities.MessageEmbed</code>
	 * @see #deserializeEmbedMessage(String)
	 */
	public String serializeEmbedMessage(Object embed);
	
	/**
	 * Deserializes the specified JSON string to a <code>net.dv8tion.jda.api.entities.MessageEmbed</code>.
	 * 
	 * <p>All the options ({@link DiscordMessages#EMBED_OPTIONS}) are currently supported.
	 * This method returns an {@link Object} because libraries' classes cannot be accessed
	 * directly from the API: just ignore that and consider it an embed.</p>
	 * 
	 * @param json JSON string to deserialize
	 * @return Deserialized message
	 * @throws Exception If the string's format is invalid
	 * @see #serializeEmbedMessage(Object)
	 */
	public Object deserializeEmbedMessage(String json) throws Exception;
	
	/**
	 * Updates the bot's current status.
	 * 
	 * @param activityType The activity type
	 * @param value Text to display
	 * @see StatusUpdateEvent
	 */
	public void setStatus(ActivityTypeAdapter activityType, String value);
	
}
