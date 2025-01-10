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

package me.remigio07.chatplugin.api.server.scoreboard.event;

import java.util.Map;

import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;

/**
 * Represents a scoreboard triggered by a {@link ScoreboardEvent}.
 */
public interface EventScoreboard {
	
	/**
	 * Prepares an event for this scoreboard.
	 * 
	 * @param player Target player
	 * @param args Event's arguments ({@link EventArguments#types()})
	 */
	public default void prepareEvent(ChatPluginServerPlayer player, Object... args) {
		// some events do not need to be prepared
	}
	
	/**
	 * Translates an input string with this event scoreboard's specific placeholders.
	 * 
	 * <p>Check {@link ScoreboardEvent#getPlaceholders()} to find out the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param player Target player
	 * @return Translated placeholders
	 */
	public default String formatPlaceholders(String input, ChatPluginServerPlayer player) {
		return input;
	}
	
	/**
	 * Gets the event that triggers this scoreboard.
	 * 
	 * @return Scoreboard's event
	 */
	public ScoreboardEvent getEvent();
	
	/**
	 * Gets the time this scoreboard should be displayed for
	 * before showing again the previous one, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "settings.on-screen-time-ms" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return On screen time
	 */
	public long getOnScreenTime();
	
	/**
	 * Gets the scoreboards that players had before triggering {@link #getEvent()}.
	 * 
	 * <p>The map contains <code>null</code> values for players who had no active scoreboards.</p>
	 * 
	 * @return Players' last scoreboards
	 */
	public Map<ChatPluginServerPlayer, Scoreboard> getLastScoreboards();
	
}
