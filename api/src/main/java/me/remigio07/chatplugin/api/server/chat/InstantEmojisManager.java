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

package me.remigio07.chatplugin.api.server.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles {@link InstantEmoji}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#instant-emojis">ChatPlugin wiki/Modules/Chat/Instant emojis</a>
 */
public abstract class InstantEmojisManager implements ChatPluginManager {
	
	protected static InstantEmojisManager instance;
	protected boolean enabled;
	protected List<InstantEmoji> instantEmojis = new ArrayList<>();
	protected List<ChatColor> tones = new ArrayList<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.instant-emojis.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the list of loaded instant emojis.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.instant-emojis.values" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Loaded instant emojis' list
	 */
	public List<InstantEmoji> getInstantEmojis() {
		return instantEmojis;
	}
	
	/**
	 * Gets an instant emoji from {@link #getInstantEmojis()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the instant emoji is not loaded.</p>
	 * 
	 * @param id Instant emoji's ID, case insensitive
	 * @return Loaded instant emoji
	 */
	@Nullable(why = "Specified instant emoji may not be loaded")
	public InstantEmoji getInstantEmoji(String id) {
		return instantEmojis.stream().filter(instantEmoji -> instantEmoji.getID().equalsIgnoreCase(id)).findAny().orElse(null);
	}
	
	/**
	 * Gets the list of loaded instant emojis' tones.
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.instant-emojis.tones" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Loaded instant emojis' tones' list
	 * @see ChatPluginServerPlayer#setEmojisTone(ChatColor)
	 */
	public List<ChatColor> getTones() {
		return tones;
	}
	
	/**
	 * Gets the default instant emojis' tone.
	 * 
	 * <p>Returns the first element of {@link #getTones()}.</p>
	 * 
	 * @return Default instant emojis' tone
	 */
	public ChatColor getDefaultTone() {
		return tones.isEmpty() ? null : tones.get(0);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static InstantEmojisManager getInstance() {
		return instance;
	}
	
	/**
	 * Translates every loaded instant emoji contained in <code>instantEmojis</code>.
	 * 
	 * <p>Specify <code>null</code> as <code>channel</code> if
	 * <code>!</code>{@link ChatChannelsManager#isEnabled()}.</p>
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @param channel Channel the message has been sent on
	 * @param instantEmojis Instant emojis' list ({@link #getInstantEmojis(ChatPluginServerPlayer, String)})
	 * @return Translated message
	 */
	public abstract String translateInstantEmojis(
			ChatPluginServerPlayer player,
			String message,
			@Nullable(why = "Null if !ChatChannelsManager#isEnabled()") ChatChannel<? extends ChatChannelData> channel,
			List<InstantEmoji> instantEmojis
			);
	
	/**
	 * Gets the list of every loaded instant emoji contained in <code>message</code>.
	 * 
	 * <p>The returned list will contain duplicate elements
	 * if the same emoji has been typed more than once.
	 * There are no guarantees on the order of the elements.</p>
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @return Instant emojis' list
	 */
	public abstract List<InstantEmoji> getInstantEmojis(ChatPluginServerPlayer player, String message);
	
	/**
	 * Represents an instant emoji handled by the {@link InstantEmojisManager}.
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#instant-emojis">ChatPlugin wiki/Modules/Chat/Instant emojis</a>
	 */
	public static class InstantEmoji {
		
		private String id, string, literalPattern;
		
		/**
		 * Constructs a new instant emoji.
		 * 
		 * @param id Instant emoji's ID
		 * @param string Instant emoji's string
		 * @throws IllegalArgumentException If <code>string.contains(" ")</code>
		 */
		public InstantEmoji(String id, String string) {
			if (string.contains(" "))
				throw new IllegalArgumentException("Instant emoji's string cannot contain spaces");
			this.id = id;
			this.string = ChatColor.translate(string);
			literalPattern = Pattern.quote(id);
		}
		
		@Override
		public String toString() {
			return new StringJoiner(", ", "InstantEmoji{", "}")
					.add("id=\"" + id + "\"")
					.add("string=\"" + string + "\"")
					.toString();
		}
		
		/**
		 * Gets this emoji's ID.
		 * 
		 * @return Emoji's ID
		 */
		public String getID() {
			return id;
		}
		
		/**
		 * Gets this emoji's string, may
		 * contain translated colors.
		 * 
		 * @return Emoji's string
		 */
		public String getString() {
			return string;
		}
		
		/**
		 * Gets this emoji's literal pattern
		 * used with regular expressions.
		 * 
		 * @return Emoji's literal pattern
		 */
		public String getLiteralPattern() {
			return literalPattern;
		}
		
	}
	
}
