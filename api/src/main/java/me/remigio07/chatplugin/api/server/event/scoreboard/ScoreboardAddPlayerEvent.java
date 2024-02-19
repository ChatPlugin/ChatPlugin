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

package me.remigio07.chatplugin.api.server.event.scoreboard;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;

/**
 * Represents the event called called just before a player is added to a {@link Scoreboard}.
 * 
 * @see Scoreboard#addPlayer(ChatPluginServerPlayer)
 */
public class ScoreboardAddPlayerEvent extends ScoreboardEvent implements CancellableEvent {
	
	/**
	 * Constructs a new scoreboard add player event.
	 * 
	 * @param player Player involved
	 * @param scoreboard Scoreboard involved
	 */
	public ScoreboardAddPlayerEvent(ChatPluginServerPlayer player, Scoreboard scoreboard) {
		super(player, scoreboard);
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
