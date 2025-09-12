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

package me.remigio07.chatplugin.api.server.chat.channel;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.chat.channel.data.DiscordChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.LocalChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.TelegramChatChannel;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a chat channel handled by {@link ChatChannelsManager}.
 * 
 * @param <T> Channel's data
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#channels">ChatPlugin wiki/Modules/Chat/Channels</a>
 */
public class ChatChannel<T extends ChatChannelData> {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with a channel's information.
	 * 
	 * <p><strong>Content:</strong> ["channel_id", "channel_display_name", "channel_prefix", "channel_type", "channel_aliases", "channel_languages", "channel_access", "channel_writing", "channel_listeners", "channel_listeners_amount"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#placeholders">ChatPlugin wiki/Modules/Chat/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "channel_id", "channel_display_name", "channel_prefix", "channel_type", "channel_aliases", "channel_languages", "channel_access", "channel_writing", "channel_listeners", "channel_listeners_amount" };
	protected String id, displayName, prefix, format;
	protected boolean accessRestricted, writingRestricted, consoleIncluded;
	protected List<String> aliases;
	protected List<Language> languages;
	protected T data;
	protected List<ChatPluginServerPlayer> listeners = new CopyOnWriteArrayList<>();
	
	/**
	 * Constructs a new chat channel.
	 * 
	 * @param id Channel's ID
	 * @param displayName Channel's display name
	 * @param prefix Channel's prefix
	 * @param format Channel's format
	 * @param accessRestricted Whether access should be restricted
	 * @param writingRestricted Whether writing should be restricted
	 * @param consoleIncluded Whether the console should receive messages
	 * @param aliases Channel's aliases
	 * @param languages Channel's languages
	 * @param data Channel's data
	 */
	public ChatChannel(
			String id,
			@Nullable(why = "Display name may not be set") String displayName,
			@Nullable(why = "Prefix may not be set") String prefix,
			String format,
			boolean accessRestricted,
			boolean writingRestricted,
			boolean consoleIncluded,
			List<String> aliases,
			List<Language> languages,
			T data
			) {
		this.id = id;
		this.displayName = displayName;
		this.prefix = prefix;
		this.format = format;
		this.accessRestricted = accessRestricted;
		this.writingRestricted = writingRestricted;
		this.consoleIncluded = consoleIncluded;
		this.aliases = aliases;
		this.languages = languages;
		this.data = data;
	}
	
	/**
	 * Gets this channel's ID.
	 * 
	 * @return Channel's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this channel's display name.
	 * 
	 * <p>Will return <code>null</code> if
	 * its display name has not been set.</p>
	 * 
	 * @return Channel's display name
	 */
	@Nullable(why = "Display name may not be set")
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Sets this channel's display name.
	 * 
	 * <p>Specify <code>null</code>
	 * to reset its display name.</p>
	 * 
	 * @param displayName Channel's display name
	 */
	public void setDisplayName(@Nullable(why = "Display name may not be set") String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * Gets this channel's prefix in messages.
	 * 
	 * <p>Will return <code>null</code>
	 * if its prefix has not been set.</p>
	 * 
	 * @return Channel's prefix
	 */
	@Nullable(why = "Prefix may not be set")
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Gets this channel's format in the chat.
	 * 
	 * @return Channel's format
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * Checks if this channel should require the
	 * <code>"chatplugin.channels." + </code>{@link #getID()}
	 * permission in order for players to join and read.
	 * 
	 * @return Whether access should be restricted
	 * @see #canAccess(ChatPluginServerPlayer)
	 */
	public boolean isAccessRestricted() {
		return accessRestricted;
	}
	
	/**
	 * Checks if this channel should require the
	 * <code>"chatplugin.channels." + {@link #getID()} + ".write"</code>
	 * permission in order for players to write.
	 * 
	 * @return Whether writing should be restricted
	 * @see #canWrite(ChatPluginServerPlayer)
	 */
	public boolean isWritingRestricted() {
		return writingRestricted;
	}
	
	/**
	 * Checks if the console should receive
	 * messages sent through this channel.
	 * 
	 * @return Whether the console should receive messages
	 */
	public boolean isConsoleIncluded() {
		return consoleIncluded;
	}
	
	/**
	 * Gets this channel's aliases.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return Channel's aliases
	 */
	public List<String> getAliases() {
		return aliases;
	}
	
	/**
	 * Gets the list of languages spoken in this channel.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return Channel's languages
	 */
	public List<Language> getLanguages() {
		return languages;
	}
	
	/**
	 * Gets this channel's data.
	 * 
	 * @return Channel's data
	 */
	public T getData() {
		return data;
	}
	
	/**
	 * Gets this channel's listeners.
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * @return Channel's listeners
	 * @see ChatPluginServerPlayer#joinChannel(ChatChannel)
	 * @see ChatPluginServerPlayer#leaveChannel(ChatChannel)
	 */
	public List<ChatPluginServerPlayer> getListeners() {
		return listeners;
	}
	
	/**
	 * Checks if the specified player can access this channel.
	 * 
	 * <p>This will check if access is not restricted.
	 * If it is, it will check if the player has the required permission
	 * <code>"chatplugin.channels." + </code>{@link #getID()}.</p>
	 * 
	 * @param player Player to check
	 * @return Whether the player can access this channel
	 */
	public boolean canAccess(ChatPluginServerPlayer player) {
		return !accessRestricted || player.hasPermission("chatplugin.channels." + id);
	}
	
	/**
	 * Checks if the specified player can write in this channel.
	 * 
	 * <p>This will check if writing is not restricted.
	 * If it is, it will check if the player has the required permission
	 * <code>"chatplugin.channels." + {@link #getID()} + ".write"</code>.</p>
	 * 
	 * @param player Player to check
	 * @return Whether the player can write in this channel
	 */
	public boolean canWrite(ChatPluginServerPlayer player) {
		return !writingRestricted || player.hasPermission("chatplugin.channels." + id + ".write");
	}
	
	/**
	 * Gets this channel's type.
	 * 
	 * @return Channel's type
	 */
	public ChatChannelType getType() {
		return data.getType();
	}
	
	/**
	 * Gets the recipients of a message sent
	 * to this channel by the specified player.
	 * 
	 * @param sender Message's sender
	 * @param excludeVanished Whether to exclude vanished players
	 * @return Message's recipients
	 */
	public List<ChatPluginServerPlayer> getRecipients(ChatPluginServerPlayer sender, boolean excludeVanished) {
		return data.getRecipients(listeners, sender, excludeVanished);
	}
	
	/**
	 * Translates an input string with this channel's specific placeholders.
	 * 
	 * <p>Every type of channel has different placeholders available. Check the following fields:
	 * 	<ul>
	 * 		<li>{@link #PLACEHOLDERS} - placeholders common to every channel</li>
	 * 		<li>{@link LocalChatChannel#PLACEHOLDERS} - local channels' placeholders</li>
	 * 		<li>{@link DiscordChatChannel#PLACEHOLDERS} - Discord channels' placeholders</li>
	 * 		<li>{@link TelegramChatChannel#PLACEHOLDERS} - Telegram channels' placeholders</li>
	 * 	</ul>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public String formatPlaceholders(String input, Language language) {
		return data.formatPlaceholders(input, language)
				.replace("{channel_id}", id)
				.replace("{channel_display_name}", displayName == null ? id : ChatColor.translate(displayName))
				.replace("{channel_prefix}", language.getMessage(prefix == null ? "placeholders.not-present" : prefix))
				.replace("{channel_type}", language.getMessage("chat.channel.types." + getType().name().toLowerCase()))
				.replace("{channel_aliases}", Utils.getStringFromList(aliases, false, true))
				.replace("{channel_languages}", Utils.getStringFromList(languages.stream().map(Language::getDisplayName).collect(Collectors.toList()), false, true))
				.replace("{channel_access}", language.getMessage("chat.channel.restrictions." + (accessRestricted ? "restricted" : "free")))
				.replace("{channel_writing}", language.getMessage("chat.channel.restrictions." + (accessRestricted ? "restricted" : "free")))
				.replace("{channel_listeners}", Utils.getStringFromList(listeners.stream().map(ChatPluginServerPlayer::getName).collect(Collectors.toList()), false, true))
				.replace("{channel_listeners_amount}", String.valueOf(listeners.size()));
	}
	
	/**
	 * Translates an input string list with this channel's specific placeholders.
	 * 
	 * 	<ul>
	 * 		<li>{@link #PLACEHOLDERS} - placeholders common to every channel</li>
	 * 		<li>{@link LocalChatChannel#PLACEHOLDERS} - local channels' placeholders</li>
	 * 		<li>{@link DiscordChatChannel#PLACEHOLDERS} - Discord channels' placeholders</li>
	 * 		<li>{@link TelegramChatChannel#PLACEHOLDERS} - Telegram channels' placeholders</li>
	 * 	</ul>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public List<String> formatPlaceholders(List<String> input, Language language) {
		return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
	}
	
}
