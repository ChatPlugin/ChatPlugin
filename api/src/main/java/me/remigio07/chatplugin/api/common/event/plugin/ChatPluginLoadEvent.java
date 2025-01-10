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

package me.remigio07.chatplugin.api.common.event.plugin;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;

/**
 * Represents the event called after ChatPlugin is loaded.
 */
public class ChatPluginLoadEvent implements ChatPluginEvent {
	
	private int startupTime;
	
	/**
	 * Constructs a new load event.
	 * 
	 * @param startupTime Time elapsed, in milliseconds
	 */
	public ChatPluginLoadEvent(int startupTime) {
		this.startupTime = startupTime;
	}
	
	/**
	 * Gets the time elapsed during the startup, in milliseconds.
	 * 
	 * <p>This value may be obtained later using {@link ChatPlugin#getStartupTime()}.</p>
	 * 
	 * @return Time elapsed, in milliseconds
	 */
	public int getStartupTime() {
		return startupTime;
	}
	
}
