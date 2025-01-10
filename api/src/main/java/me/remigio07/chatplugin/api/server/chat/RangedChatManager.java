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

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;

/**
 * Manager that handles the ranged chat.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#ranged-chat">ChatPlugin wiki/Modules/Chat/Ranged chat</a>
 */
public abstract class RangedChatManager implements ChatPluginManager {
	
	protected static RangedChatManager instance;
	protected boolean enabled, spyOnJoinEnabled, globalModeEnabled;
	protected int range;
	protected String spyFormat, globalModePrefix, globalModeFormat;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.ranged-chat.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the ranged chat spy should be enabled for players who join
	 * the server and have the permission "chatplugin.commands.rangedchatspy".
	 * 
	 * <p><strong>Found at:</strong> "chat.ranged-chat.spy.on-join-enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether the ranged chat spy should be enabled on join
	 */
	public boolean isSpyOnJoinEnabled() {
		return spyOnJoinEnabled;
	}
	
	/**
	 * Checks if messages will be considered global when
	 * they start with {@link #getGlobalModePrefix()}.
	 * 
	 * <p><strong>Found at:</strong> "chat.ranged-chat.global-mode.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether the global mode is enabled
	 */
	public boolean isGlobalModeEnabled() {
		return globalModeEnabled;
	}
	
	/**
	 * Gets the ranged chat's range, in blocks.
	 * 
	 * <p><strong>Found at:</strong> "chat.ranged-chat.range" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Chat's range
	 */
	public int getRange() {
		return range;
	}
	
	/**
	 * Gets the chat format displayed to Staff
	 * members when players send a local message.
	 * 
	 * <p><strong>Found at:</strong> "chat.ranged-chat.spy.format" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Ranged chat spy's format
	 */
	public String getSpyFormat() {
		return spyFormat;
	}
	
	/**
	 * Gets the prefix required to treat a message as global.
	 * 
	 * <p><strong>Found at:</strong> "chat.ranged-chat.global-mode.prefix" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Global mode's prefix
	 */
	public String getGlobalModePrefix() {
		return globalModePrefix;
	}
	
	/**
	 * Gets the format used to send global messages to players.
	 * 
	 * <p><strong>Found at:</strong> "chat.ranged-chat.global-mode.format" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Global mode chat's format
	 */
	public String getGlobalModeFormat() {
		return globalModeFormat;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static RangedChatManager getInstance() {
		return instance;
	}
	
}
