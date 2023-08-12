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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.scoreboard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a {@link Scoreboard}'s titles.
 */
public abstract class ScoreboardTitles extends TimerTask {
	
	/**
	 * Value representing the max title length.
	 * 
	 * <p><strong>Value:</strong> 128 on > 1.12.2, 32 otherwise</p>
	 */
	public static final int MAX_TITLE_LENGTH = VersionUtils.getVersion().getProtocol() > 340 ? 128 : 32;
	
	/**
	 * String containing the "title too long" text indicator.
	 * 
	 * <p><strong>Content:</strong> "&#167cTitle exceeds &#167f" + {@link #MAX_TITLE_LENGTH} + " &#167cchars"</p>
	 */
	public static final String TITLE_TOO_LONG = "\u00A7cTitle exceeds \u00A7f" + MAX_TITLE_LENGTH + " \u00A7cchars";
	protected Scoreboard scoreboard;
	protected Map<Language, List<String>> values = new HashMap<>();
	protected Map<Language, Integer> timerIndexes = new HashMap<>();
	protected boolean randomOrder;
	protected long sendingTimeout;
	
	/**
	 * Gets the scoreboard associated with these titles.
	 * 
	 * @return Associated scoreboard
	 */
	@NotNull
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	/**
	 * Gets these titles' values.
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}
	 * and empty Lists are not allowed.
	 * 
	 * <p><strong>Found at:</strong> "titles.values" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return Titles' values
	 */
	public Map<Language, List<String>> getValues() {
		return values;
	}
	
	/**
	 * Gets these titles's values for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s values if no values are present for the specified language.
	 * Will return <code>null</code> if {@link #getValues()}<code>.get(language) == null && !avoidNull</code>.
	 * 
	 * @param language Language used to translate the values
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Titles' values
	 */
	@Nullable(why = "No values may be present for the specified language")
	public List<String> getValues(Language language, boolean avoidNull) {
		return values.get(language) == null ? avoidNull ? values.get(Language.getMainLanguage()) : null : values.get(language);
	}
	
	/**
	 * Gets the {@link #run()}'s timer's indexes of {@link #getValues()}.
	 * 
	 * @return Titles' timer's indexes
	 */
	public Map<Language, Integer> getTimerIndexes() {
		return timerIndexes;
	}
	
	/**
	 * Checks if the values should be sent in a random order.
	 * 
	 * <p><strong>Found at:</strong> "titles.random-order" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return Whether to use a random order
	 */
	public boolean isRandomOrder() {
		return randomOrder;
	}
	
	/**
	 * Gets the timeout between sendings, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "titles.sending-timeout-ms" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return Time between sendings
	 */
	public long getSendingTimeout() {
		return sendingTimeout;
	}
	
	/**
	 * Automatic title sender, called once every {@link #getSendingTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
}
