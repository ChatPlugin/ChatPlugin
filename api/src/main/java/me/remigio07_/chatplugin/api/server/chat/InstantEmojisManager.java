/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.api.server.chat;

import java.util.HashMap;
import java.util.Map;

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;

/**
 * Manager that handles instant emojis in the chat. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Chat/instant-emojis">ChatPlugin wiki/Chat/Instant emojis</a>
 * 
 * @see me.remigio07_.chatplugin.api.server.chat Chat-related managers
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
	 * You may modify the returned map.
	 * 
	 * @return Loaded emojis' map
	 */
	public Map<String, Character> getEmojis() {
		return emojis;
	}
	
	/**
	 * Gets an emoji from {@link #getEmojis()} by its ID.
	 * Will return <code>null</code> if the emoji is not loaded.
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
