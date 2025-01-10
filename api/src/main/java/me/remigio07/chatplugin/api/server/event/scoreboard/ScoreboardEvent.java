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

package me.remigio07.chatplugin.api.server.event.scoreboard;

import me.remigio07.chatplugin.api.common.event.player.ChatPluginPlayerEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;

/**
 * Represents a {@link Scoreboard}-related event.
 */
public abstract class ScoreboardEvent implements ChatPluginPlayerEvent {
	
	protected boolean cancelled;
	protected ChatPluginServerPlayer player;
	protected Scoreboard scoreboard;
	
	protected ScoreboardEvent(ChatPluginServerPlayer player, Scoreboard scoreboard) {
		this.player = player;
		this.scoreboard = scoreboard;
	}
	
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the scoreboard involved with this event
	 * 
	 * @return Scoreboard involved
	 */
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
}
