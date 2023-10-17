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

package me.remigio07.chatplugin.api.common.event.plugin;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;

/**
 * Represents the event called after ChatPlugin is reloaded.
 * 
 * @see ChatPlugin#reload()
 */
public class ChatPluginReloadEvent implements ChatPluginEvent {
	
	private int reloadTime;
	
	/**
	 * Constructs a new reload event.
	 * 
	 * @param reloadTime Time elapsed, in milliseconds
	 */
	public ChatPluginReloadEvent(int reloadTime) {
		this.reloadTime = reloadTime;
	}
	
	/**
	 * Gets the time elapsed during the last reload, in milliseconds.
	 * 
	 * <p>This value may be obtained later using {@link ChatPlugin#getLastReloadTime()}.</p>
	 * 
	 * @return Time elapsed, in milliseconds
	 */
	public int getReloadTime() {
		return reloadTime;
	}
	
}
