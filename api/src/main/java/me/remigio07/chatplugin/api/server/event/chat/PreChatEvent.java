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

package me.remigio07.chatplugin.api.server.event.chat;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called before a player's message gets processed.
 * 
 * @see ChatManager#handleChatEvent(ChatPluginServerPlayer, String)
 */
public class PreChatEvent extends ChatEvent implements CancellableEvent {
	
	private boolean cancelled;
	
	/**
	 * Constructs a new pre chat event.
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 */
	public PreChatEvent(ChatPluginServerPlayer player, String message) {
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
	
}
