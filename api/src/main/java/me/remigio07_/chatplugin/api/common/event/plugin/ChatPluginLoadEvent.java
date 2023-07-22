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

package me.remigio07_.chatplugin.api.common.event.plugin;

import me.remigio07_.chatplugin.api.ChatPlugin;
import me.remigio07_.chatplugin.api.common.event.ChatPluginEvent;

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
	 * This value may be obtained later using {@link ChatPlugin#getStartupTime()}.
	 * 
	 * @return Time elapsed, in milliseconds
	 */
	public int getStartupTime() {
		return startupTime;
	}
	
}
