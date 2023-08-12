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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.event.chat;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.server.chat.PlayerPingManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called before a player gets pinged by another player in the chat.
 * 
 * @see PlayerPingManager#performPing(ChatPluginServerPlayer, String)
 */
public class PlayerPingEvent extends ChatEvent implements CancellableEvent {
	
	private ChatPluginServerPlayer target;
	private boolean cancelled;
	
	/**
	 * Constructs a new player ping event.
	 * 
	 * @param player Player involved
	 * @param target Target player
	 * @param message Message involved
	 */
	public PlayerPingEvent(ChatPluginServerPlayer player, ChatPluginServerPlayer target, String message) {
		super(player, message);
		this.target = target;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	/**
	 * Gets the target player of the ping
	 * specified in {@link #getMessage()}.
	 * 
	 * @return Target player
	 */
	public ChatPluginServerPlayer getTarget() {
		return target;
	}
	
}
