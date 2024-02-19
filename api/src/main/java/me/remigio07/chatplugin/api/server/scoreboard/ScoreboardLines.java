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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a {@link Scoreboard}'s lines.
 */
public abstract class ScoreboardLines {
	
	/**
	 * Value representing the max line length.
	 * 
	 * <p><strong>Value:</strong> 128 on &gt; 1.12.2, 32 otherwise</p>
	 */
	public static final int MAX_LINE_LENGTH = VersionUtils.getVersion().getProtocol() > 340 ? 128 : 32;
	
	/**
	 * String containing the "line too long" text indicator.
	 * 
	 * <p><strong>Content:</strong> "&sect;cLine exceeds &sect;f" + {@link #MAX_LINE_LENGTH} + " &sect;cchars"</p>
	 */
	public static final String LINE_TOO_LONG = "\u00A7cLine exceeds \u00A7f" + MAX_LINE_LENGTH + " \u00A7cchars";
	protected Scoreboard scoreboard;
	protected List<ScoreboardLine> lines = new ArrayList<>(Collections.nCopies(15, null));
	
	/**
	 * Gets the scoreboard associated with these lines.
	 * 
	 * @return Associated scoreboard
	 */
	@NotNull
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	/**
	 * Gets {@link #getScoreboard()}'s lines.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * <p><strong>Found at:</strong> "lines" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return Scoreboard's lines
	 */
	public List<ScoreboardLine> getLines() {
		return lines;
	}
	
	/**
	 * Gets {@link #getScoreboard()}'s line for the specified line index.
	 * 
	 * <p>Will return <code>null</code> if {@link #getLines()}<code>.get(lineIndex) == null</code>.</p>
	 * 
	 * @param lineIndex Line's index (0 - 14)
	 * @return Scoreboard's line
	 * @throws IndexOutOfBoundsException If <code>lineIndex &lt; 0 || lineIndex &gt; 14</code>
	 */
	@Nullable(why = "No line may be present for the specified line index")
	public ScoreboardLine getLine(int lineIndex) {
		if (lineIndex < 0 || lineIndex > 14)
			throw new IndexOutOfBoundsException("Specified line index (" + lineIndex + ") is invalid as it is not inside of range 0 - 14");
		return lines.get(lineIndex);
	}
	
	/**
	 * Represents a {@link Scoreboard}'s line.
	 */
	public static abstract class ScoreboardLine extends TimerTask {
		
		protected ScoreboardLines lines;
		protected Map<Language, List<String>> values = new HashMap<>();
		protected Map<Language, Integer> timerIndexes = new HashMap<>();
		protected int lineIndex;
		protected boolean randomOrder;
		protected long sendingTimeout;
		
		/**
		 * Gets the lines associated with this line.
		 * 
		 * @return Associated lines
		 */
		@NotNull
		public ScoreboardLines getLines() {
			return lines;
		}
		
		/**
		 * Gets this line's values.
		 * 
		 * <p>You may modify the returned map, but it cannot point to a
		 * <code>null</code> value for {@link Language#getMainLanguage()}
		 * and empty Lists are not allowed.</p>
		 * 
		 * <p><strong>Found at:</strong> "lines." + {@link #getLineIndex()} + ".values" in {@link Scoreboard#getConfiguration()}</p>
		 * 
		 * @return Line's values
		 */
		public Map<Language, List<String>> getValues() {
			return values;
		}
		
		/**
		 * Gets this line's values for the specified language.
		 * 
		 * <p>Specify <code>true</code> as <code>avoidNull</code> to fall back to
		 * {@link Language#getMainLanguage()}'s values if no values are present for the specified language.
		 * Will return <code>null</code> if {@link #getValues()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.</p>
		 * 
		 * @param language Language used to translate the values
		 * @param avoidNull Whether to avoid returning <code>null</code>
		 * @return Line's values
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
		 * Gets this line's index in {@link ScoreboardLines#getLines()}.
		 * 
		 * @return Line's index (0 - 14)
		 */
		public int getLineIndex() {
			return lineIndex;
		}
		
		/**
		 * Checks if the values should be sent in a random order.
		 * 
		 * <p><strong>Found at:</strong> "lines." + {@link #getLineIndex()} + ".random-order" in {@link Scoreboard#getConfiguration()}</p>
		 * 
		 * @return Whether to use a random order
		 */
		public boolean isRandomOrder() {
			return randomOrder;
		}
		
		/**
		 * Gets the timeout between sendings, in milliseconds.
		 * 
		 * <p><strong>Found at:</strong> "lines." + {@link #getLineIndex()} + ".sending-timeout-ms" in {@link Scoreboard#getConfiguration()}</p>
		 * 
		 * @return Time between sendings
		 */
		public long getSendingTimeout() {
			return sendingTimeout;
		}
		
		/**
		 * Automatic line sender, called once every {@link #getSendingTimeout()} ms.
		 */
		@Override
		public abstract void run();
		
	}
	
}
