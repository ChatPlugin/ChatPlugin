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

package me.remigio07.chatplugin.api.server.event.chat;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called when a message is sent to the staff chat.
 * 
 * @see StaffChatManager#sendPlayerMessage(ChatPluginServerPlayer, String)
 * @see StaffChatManager#sendConsoleMessage(String)
 */
public class StaffChatEvent extends ChatEvent implements CancellableEvent {
	
	private boolean cancelled;
	
	/**
	 * Constructs a new staff chat event.
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 */
	public StaffChatEvent(@Nullable(why = "Null to represent the console") ChatPluginServerPlayer player, String message) {
		super(player, message);
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
	 * {@inheritDoc}
	 * 
	 * <p>Will return <code>null</code> to indicate the console.</p>
	 */
	@Nullable(why = "Null to represent the console")
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
}
