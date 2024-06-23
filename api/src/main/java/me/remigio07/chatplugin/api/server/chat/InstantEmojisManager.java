/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
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
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static InstantEmojisManager getInstance() {
		return instance;
	}
	
	/**
	 * Formats a message translating all the instant emojis loaded.
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @param globalChat Whether the message has been sent to the global chat
	 * @return Translated message
	 */
	public abstract String translateInstantEmojis(ChatPluginServerPlayer player, String message, boolean globalChat);
	
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
		 */
		public InstantEmoji(String id, String string) {
			this.id = id;
			this.string = ChatColor.translate(string);
			literalPattern = Pattern.quote(id);
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
