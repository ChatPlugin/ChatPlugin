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

package me.remigio07.chatplugin.api.server.event.f3servername;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerName;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called when an {@link F3ServerName} is sent to a player.
 * 
 * @see F3ServerNameManager#sendF3ServerName(F3ServerName, ChatPluginServerPlayer)
 */
public class F3ServerNameSendEvent implements CancellableEvent, ChatPluginServerPlayerEvent {
	
	private boolean cancelled;
	private ChatPluginServerPlayer player;
	private F3ServerName f3ServerName;
	
	/**
	 * Constructs a new F3 server name send event.
	 * 
	 * @param f3ServerName F3 server name involved
	 * @param player Player involved
	 */
	public F3ServerNameSendEvent(F3ServerName f3ServerName, ChatPluginServerPlayer player) {
		this.f3ServerName = f3ServerName;
		this.player = player;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the F3 server name sent to the player.
	 * 
	 * @return F3 server name sent
	 */
	public F3ServerName getF3ServerName() {
		return f3ServerName;
	}
	
}
