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

package me.remigio07.chatplugin.api.server.event.chat;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.event.player.OfflinePlayerEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;

/**
 * Represents an event called before a player starts ignoring another player.
 * 
 * @see PlayerIgnoreManager#ignore(OfflinePlayer, OfflinePlayer)
 */
public class IgnoreEvent implements OfflinePlayerEvent, CancellableEvent {
	
	private boolean cancelled;
	protected OfflinePlayer player, ignoredPlayer;
	
	/**
	 * Constructs a new ignore event.
	 * 
	 * @param player Player involved
	 * @param ignoredPlayer Ignored player
	 */
	public IgnoreEvent(OfflinePlayer player, OfflinePlayer ignoredPlayer) {
		this.player = player;
		this.ignoredPlayer = ignoredPlayer;
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
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the ignored player.
	 * 
	 * @return Ignored player
	 */
	public OfflinePlayer getIgnoredPlayer() {
		return ignoredPlayer;
	}
	
}
