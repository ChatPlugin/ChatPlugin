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

package me.remigio07.chatplugin.api.server.scoreboard;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;

/**
 * Represents the display mode of {@link ScoreboardNumbers}:
 * default, only zeros and custom text.
 */
public enum NumbersDisplayMode {
	
	/**
	 * Scores will use red numbers from 0
	 * to 14 based on the number of lines.
	 * 
	 * <p>This format is used for scoreboards sent
	 * to players connected through Bedrock.</p>
	 */
	DEFAULT, // TODO: also include 1.7 players
	
	/**
	 * Scores will use red zeros for every line.
	 * 
	 * <p>This format is used for scoreboards sent
	 * to players connected using 1.20.2 or lower
	 * when {@link #CUSTOM_TEXT} is selected.</p>
	 */
	ONLY_ZEROS,
	
	/**
	 * Scores will use custom text which may
	 * contain formatting codes and be animated.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_20_3 1.20.3}
	 * <br><strong>Found at:</strong> "settings.numbers.custom-text" in {@link Scoreboard#getConfiguration()}</p>
	 */
	CUSTOM_TEXT;
	
	/**
	 * Checks if this numbers display mode is
	 * supported on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this display mode is supported
	 */
	public boolean isSupported () {
		return this != CUSTOM_TEXT || VersionUtils.getVersion().isAtLeast(Version.V1_20_3);
	}
	
}
