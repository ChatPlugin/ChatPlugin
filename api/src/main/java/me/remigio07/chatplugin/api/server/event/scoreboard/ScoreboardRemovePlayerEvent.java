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

import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;

/**
 * Represents the event called just before a player is removed from a {@link Scoreboard}.
 * 
 * @see Scoreboard#removePlayer(ChatPluginServerPlayer)
 */
public class ScoreboardRemovePlayerEvent extends ScoreboardEvent {
	
	/**
	 * Constructs a new scoreboard remove player event.
	 * 
	 * @param player Player involved
	 * @param scoreboard Scoreboard involved
	 */
	public ScoreboardRemovePlayerEvent(ChatPluginServerPlayer player, Scoreboard scoreboard) {
		super(player, scoreboard);
	}
	
}
