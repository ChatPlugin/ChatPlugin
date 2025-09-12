/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.event.scoreboard.ScoreboardAddPlayerEvent;
import me.remigio07.chatplugin.api.server.event.scoreboard.ScoreboardRemovePlayerEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents a scoreboard handled by the {@link ScoreboardManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Scoreboards">ChatPlugin wiki/Modules/Scoreboards</a>
 */
public abstract class Scoreboard {
	
	/**
	 * Represents the scores' names used by the scoreboard.
	 */
	public static final String[] SCORES = new String[15];
	protected String id;
	protected ScoreboardType type;
	protected Configuration configuration;
	protected ScoreboardTitles titles;
	protected ScoreboardLines lines;
	protected ScoreboardNumbers numbers;
	protected boolean abbreviateLongText;
	protected Set<PlaceholderType> placeholderTypes = Collections.emptySet();
	protected List<ChatPluginServerPlayer> players = new CopyOnWriteArrayList<>();
	
	static {
		if (Environment.isBukkit())
			for (int i = 0; i < 15; i++)
				SCORES[i] = ChatColor.values()[i].toString() + "§r";
		else for (int i = 0; i < 15; i++)
			SCORES[i] = (i == 0 ? "" : SCORES[i - 1]) + "§§";
	}
	
	/**
	 * Gets this scoreboard's ID.
	 * 
	 * @return Scoreboard's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this scoreboard's type.
	 * 
	 * @return Scoreboard's type
	 */
	public ScoreboardType getType() {
		return type;
	}
	
	/**
	 * Gets the configuration associated with this scoreboard.
	 * 
	 * <p>Will return <code>null</code> if this scoreboard was created using
	 * {@link ScoreboardManager#createScoreboardBuilder(String, boolean, Set)}.</p>
	 * 
	 * @return Associated configuration
	 */
	@Nullable(why = "Will return null if this scoreboard was created using ScoreboardManager#createScoreboardBuilder(String, boolean, List)")
	public Configuration getConfiguration() {
		return configuration;
	}
	
	/**
	 * Gets this scoreboard's titles.
	 * 
	 * @return Scoreboard's titles
	 */
	public ScoreboardTitles getTitles() {
		return titles;
	}
	
	/**
	 * Gets this scoreboard's lines.
	 * 
	 * @return Scoreboard's lines
	 */
	public ScoreboardLines getLines() {
		return lines;
	}
	
	/**
	 * Gets this scoreboard's numbers.
	 * 
	 * @return Scoreboard's numbers
	 */
	public ScoreboardNumbers getNumbers() {
		return numbers;
	}
	
	/**
	 * Checks if text (scoreboards' lines and titles)
	 * should be abbreviated by adding '…' at the end
	 * when it is too long to be displayed instead
	 * of {@link ScoreboardTitles#TITLE_TOO_LONG}
	 * or {@link ScoreboardLines#LINE_TOO_LONG}.
	 * 
	 * <p><strong>Found at:</strong> "settings.abbreviate-long-text" in {@link #getConfiguration()}</p>
	 * 
	 * @return Whether to abbreviate long text
	 */
	public boolean shouldAbbreviateLongText() {
		return abbreviateLongText;
	}
	
	/**
	 * Gets the set of placeholder types used
	 * to translate {@link #getTitles()} and {@link #getLines()}.
	 * 
	 * <p><strong>Found at:</strong> "settings.placeholder-types" in {@link #getConfiguration()}</p>
	 * 
	 * @return Placeholders used to translate titles and lines
	 */
	public Set<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the list of players this scoreboard is being displayed to.
	 * 
	 * <p>Do <em>not</em> modify the returned list.
	 * Use {@link #addPlayer(ChatPluginServerPlayer)}
	 * and {@link #removePlayer(ChatPluginServerPlayer)}
	 * to interact with it.</p>
	 * 
	 * @return Scoreboard's players
	 */
	public List<ChatPluginServerPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Adds a player to {@link #getPlayers()}.
	 * 
	 * @param player Player to add
	 * @see ScoreboardAddPlayerEvent
	 */
	public abstract void addPlayer(ChatPluginServerPlayer player);
	
	/**
	 * Removes a player from {@link #getPlayers()}.
	 * 
	 * @param player Player to remove
	 * @see ScoreboardRemovePlayerEvent
	 */
	public abstract void removePlayer(ChatPluginServerPlayer player);
	
	/**
	 * Represents the builder used to create {@link Scoreboard}s using
	 * {@link ScoreboardManager#createScoreboardBuilder(String, boolean, Set)}.
	 */
	public static abstract class Builder {
		
		/**
		 * Sets this builder's titles.
		 * 
		 * @param values Titles' values
		 * @param randomOrder Whether to use a random order
		 * @param sendingTimeout Time between sendings
		 * @return This builder
		 * @throws NoSuchElementException If <code>values.get({@link Language#getMainLanguage()}) == null</code>
		 */
		public abstract Builder setTitles(Map<Language, List<String>> values, boolean randomOrder, long sendingTimeout);
		
		/**
		 * Sets one of this builder's lines.
		 * 
		 * @param values Line's values
		 * @param lineIndex Line's index (0 - 14)
		 * @param randomOrder Whether to use a random order
		 * @param sendingTimeout Time between sendings
		 * @return This builder
		 * @throws NoSuchElementException If <code>values.get({@link Language#getMainLanguage()}) == null</code>
		 * @throws IndexOutOfBoundsException If <code>lineIndex &lt; 0 || lineIndex &gt; 14</code>
		 */
		public abstract Builder setLine(Map<Language, List<String>> values, int lineIndex, boolean randomOrder, long sendingTimeout);
		
		/**
		 * Sets this builder's numbers.
		 * 
		 * @param displayMode Numbers' display mode
		 * @param customTextValue Numbers' custom text's value
		 * @param customTextColorsCycleTimeout Time between color cycles
		 * @param customTextColorsInterpolations Interpolations between provided colors
		 * @param customTextColorsGradient Colors to cycle through
		 * @return This builder
		 * @throws UnsupportedOperationException If <code>!</code>{@link NumbersDisplayMode#isSupported()}
		 * @throws IllegalArgumentException If <code>customTextColorsCycleTimeout &lt; 1 ||
		 * customTextColorsInterpolations &lt; 0 || customTextColorsGradient.isEmpty()</code>
		 */
		public abstract Builder setNumbers(
				NumbersDisplayMode displayMode,
				String customTextValue,
				long customTextColorsCycleTimeout,
				int customTextColorsInterpolations,
				List<ChatColor> customTextColorsGradient
				);
		
		/**
		 * Builds the scoreboard.
		 * 
		 * @return New scoreboard
		 * @throws IllegalStateException If one of the following has not been called yet:
		 * 	<ul>
		 * 		<li>{@link #setTitles(Map, boolean, long)}</li>
		 * 		<li>{@link #setLine(Map, int, boolean, long)}</li>
		 * 		<li>{@link #setNumbers(NumbersDisplayMode, String, long, int, List)}</li>
		 * 	</ul>
		 */
		public abstract Scoreboard build();
		
	}
	
}
