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

package me.remigio07.chatplugin.api.server.scoreboard;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.server.scoreboard.event.ScoreboardEvent;

/**
 * Represents the type of a scoreboard: default, event or custom.
 */
public enum ScoreboardType {
	
	/**
	 * Represents the default scoreboard.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/scoreboards/default.yml</p>
	 * 
	 * @see ConfigurationType#DEFAULT_SCOREBOARD
	 */
	DEFAULT,
	
	/**
	 * Represents a scoreboard triggered by a {@link ScoreboardEvent}.
	 */
	EVENT,
	
	/**
	 * Represents a custom scoreboard.
	 */
	CUSTOM;
	
	/**
	 * Gets a scoreboard's type by its ID.
	 * 
	 * @param scoreboardID Scoreboard's ID
	 * @return Scoreboard's type
	 */
	public static ScoreboardType getType(String scoreboardID) {
		if (scoreboardID.equals("default"))
			return DEFAULT;
		if (scoreboardID.endsWith("-event"))
			return EVENT;
		return CUSTOM;
	}
	
}
