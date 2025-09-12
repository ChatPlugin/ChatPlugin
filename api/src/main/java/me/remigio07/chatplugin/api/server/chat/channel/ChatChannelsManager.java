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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;

/**
 * Manager that handles {@link ChatChannel}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#channels">ChatPlugin wiki/Modules/Chat/Channels</a>
 */
public abstract class ChatChannelsManager implements ChatPluginManager {
	
	/**
	 * Pattern representing the allowed channel IDs.
	 * 
	 * <p><strong>Regex:</strong> <a href="https://regex101.com/r/9iSnkI/1"><code>^[a-zA-Z0-9-_]{2,36}$</code></a></p>
	 * 
	 * @see #isValidChannelID(String)
	 */
	public static final Pattern CHANNEL_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	protected static ChatChannelsManager instance;
	protected boolean enabled, readingNotificationEnabled, spyOnJoinEnabled;
	protected String spyFormat;
	protected List<String> defaultListeningChannelsIDs = Collections.emptyList();
	protected ChatChannel<? extends ChatChannelData> defaultWritingChannel;
	protected List<ChatChannel<? extends ChatChannelData>> channels = new CopyOnWriteArrayList<>();
	protected long loadTime;
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p><strong>Found at:</strong> "chat.channels.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the chat channel spy should be enabled for players who join
	 * the server and have the permission "chatplugin.commands.chatchannelspy".
	 * 
	 * <p><strong>Found at:</strong> "chat.channels.spy.on-join-enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether the chat channel spy should be enabled on join
	 */
	public boolean isSpyOnJoinEnabled() {
		return spyOnJoinEnabled;
	}
	
	/**
	 * Checks if the "chat.nobody-read" notification should be sent
	 * to players when they send a message to an empty channel.
	 * 
	 * <p><strong>Found at:</strong> "chat.channels.reading-notification-enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether the reading notification should be enabled
	 */
	public boolean isReadingNotificationEnabled() {
		return readingNotificationEnabled;
	}
	
	/**
	 * Gets the chat channel spy's format.
	 * 
	 * <p><strong>Found at:</strong> "chat.channels.spy.format" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Chat channel spy's format
	 */
	public String getSpyFormat() {
		return spyFormat;
	}
	
	/**
	 * Gets the default listening channels' IDs.
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.channels.default.listening" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Default listening channels' IDs
	 */
	public List<String> getDefaultListeningChannelsIDs() {
		return defaultListeningChannelsIDs;
	}
	
	/**
	 * Gets the default writing channel.
	 * 
	 * <p>Its {@link ChatChannel#getID()} is always be included
	 * in {@link #getDefaultListeningChannelsIDs()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.channels.default.writing" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Default writing channel
	 */
	public ChatChannel<? extends ChatChannelData> getDefaultWritingChannel() {
		return defaultWritingChannel;
	}
	
	/**
	 * Gets the list of loaded channels.
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * @return Loaded channels' list
	 */
	public List<ChatChannel<? extends ChatChannelData>> getChannels() {
		return channels;
	}
	
	/**
	 * Gets a channel from {@link #getChannels()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the channel is not loaded.</p>
	 * 
	 * @param id Channel's ID, case insensitive
	 * @param reckonAliases Whether to also reckon {@link ChatChannel#getAliases()}
	 * @return Loaded channel
	 */
	@Nullable(why = "Specified channel may not be loaded")
	public ChatChannel<? extends ChatChannelData> getChannel(String id, boolean reckonAliases) {
		return channels.stream().filter(channel -> channel.getID().equalsIgnoreCase(id) || (reckonAliases && Utils.arrayContains(channel.getAliases().toArray(new String[0]), id, true))).findAny().orElse(null);
	}
	
	/**
	 * Checks if the specified String is a valid channel ID.
	 * 
	 * @param channelID Channel ID to check
	 * @return Whether the specified channel ID is valid
	 * @see #CHANNEL_ID_PATTERN
	 */
	public boolean isValidChannelID(String channelID) {
		return CHANNEL_ID_PATTERN.matcher(channelID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ChatChannelsManager getInstance() {
		return instance;
	}
	
	/**
	 * Adds a channel to {@link #getChannels()}.
	 * 
	 * <p><strong>Note:</strong> the channel will be removed on {@link #unload()}.</p>
	 * 
	 * @param channel Channel to add
	 * @throws IllegalArgumentException If {@link ChatChannel#getID()} is already in use, case insensitive
	 */
	public abstract void addChannel(ChatChannel<? extends ChatChannelData> channel);
	
}
