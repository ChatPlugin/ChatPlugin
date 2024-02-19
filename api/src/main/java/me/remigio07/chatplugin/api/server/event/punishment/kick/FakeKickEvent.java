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

package me.remigio07.chatplugin.api.server.event.punishment.kick;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.common.punishment.kick.KickType;
import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents the event called after a fake kick gets processed.
 */
public class FakeKickEvent implements CancellableEvent, ChatPluginServerPlayerEvent {
	
	private boolean cancelled;
	private ChatPluginServerPlayer player;
	private String reason;
	private KickType type;
	
	/**
	 * Constructs a new fake kick event.
	 * 
	 * @param player Player involved
	 * @param reason Fake kick's reason
	 * @param type Fake kick's type
	 */
	public FakeKickEvent(ChatPluginServerPlayer player, String reason, KickType type) {
		this.player = player;
		this.reason = reason;
		this.type = type;
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
	 * Gets the fake kick's reason.
	 * 
	 * @return Kick's reason
	 */
	public String getReason() {
		return reason;
	}
	
	/**
	 * Gets the fake kick's type.
	 * 
	 * @return Kick's type
	 */
	public KickType getType() {
		return type;
	}
	
}
