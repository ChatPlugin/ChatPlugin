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

import java.util.HashMap;
import java.util.Map;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;

/**
 * Manager that handles instant emojis in the chat.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#instant-emojis">ChatPlugin wiki/Modules/Chat/Instant emojis</a>
 */
public abstract class InstantEmojisManager implements ChatPluginManager {
	
	protected static InstantEmojisManager instance;
	protected boolean enabled;
	protected Map<String, Character> emojis = new HashMap<>();
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
	 * Gets the map of loaded emojis.
	 * 
	 * <p>You may modify the returned map.</p>
	 * 
	 * @return Loaded emojis' map
	 */
	public Map<String, Character> getEmojis() {
		return emojis;
	}
	
	/**
	 * Gets an emoji from {@link #getEmojis()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the emoji is not loaded.</p>
	 * 
	 * @param id Emoji's ID
	 * @return Loaded emoji
	 */
	@Nullable(why = "Specified emoji may not be loaded")
	public char getEmoji(String id) {
		return emojis.get(id);
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
	 * @param input Input String
	 * @return Translated message
	 */
	public abstract String format(String input);
	
}
