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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.scoreboard;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
	protected boolean displayOnlyOneNumberEnabled;
	protected int displayOnlyOneNumberValue;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected List<ChatPluginServerPlayer> players = new CopyOnWriteArrayList<>();
	
	static {
		if (Environment.isBukkit())
			for (int i = 0; i < 15; i++)
				SCORES[i] = ChatColor.values()[i].toString() + "\u00A7r";
		else for (int i = 0; i < 15; i++)
			SCORES[i] = (i == 0 ? "" : SCORES[i - 1]) + "\u00A7\u00A7";
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
	 * Will return <code>null</code> if this scoreboard was created using
	 * {@link ScoreboardManager#createScoreboardBuilder(String, boolean, int, List)}.
	 * 
	 * @return Associated configuration
	 */
	@Nullable(why = "Will return null if this scoreboard was created using ScoreboardManager#createScoreboardBuilder(String, boolean, int, List)")
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
	 * Checks if only {@link #getDisplayOnlyOneNumberValue()} should be displayed
	 * as a red number in the right side of the scoreboard instead of values 0 - 15.
	 * 
	 * <p><strong>Found at:</strong> "settings.display-only-one-number.enabled" in {@link #getConfiguration()}</p>
	 * 
	 * @return Whether to display only one number
	 */
	public boolean isDisplayOnlyOneNumberEnabled() {
		return displayOnlyOneNumberEnabled;
	}
	
	/**
	 * Gets the red number displayed in the right side of the scoreboard.
	 * Will work when {@link #isDisplayOnlyOneNumberEnabled()}.
	 * 
	 * <p><strong>Found at:</strong> "settings.display-only-one-number.value" in {@link #getConfiguration()}</p>
	 * 
	 * @return Number displayed in the scoreboard
	 */
	public int getDisplayOnlyOneNumberValue() {
		return displayOnlyOneNumberValue;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate {@link #getTitles()} and {@link #getLines()}.
	 * 
	 * <p><strong>Found at:</strong> "settings.placeholder-types" in {@link #getConfiguration()}</p>
	 * 
	 * @return Placeholders used to translate titles and lines
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the list of players this scoreboard is being displayed to.
	 * Do not modify the returned list.
	 * Use {@link #addPlayer(ChatPluginServerPlayer)} and {@link #removePlayer(ChatPluginServerPlayer)}
	 * to interact with it.
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
	 * {@link ScoreboardManager#createScoreboardBuilder(String, boolean, int, List)}.
	 */
	public static abstract class Builder {
		
		/**
		 * Sets this builder's titles.
		 * 
		 * @param values Titles' values
		 * @param randomOrder Whether to use a random order
		 * @param sendingTimeout Time between sendings
		 * @return This builder
		 * @throws IllegalArgumentException If <code>values.get({@link Language#getMainLanguage()}) == null</code>
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
		 * @throws IllegalArgumentException If <code>values.get({@link Language#getMainLanguage()}) == null</code>
		 * @throws IndexOutOfBoundsException If <code>lineIndex &lt; 0 || lineIndex &gt; 14</code>
		 */
		public abstract Builder setLine(Map<Language, List<String>> values, int lineIndex, boolean randomOrder, long sendingTimeout);
		
		/**
		 * Builds the scoreboard.
		 * 
		 * @return New scoreboard
		 * @throws IllegalStateException If one of the following has not been called yet:
		 * 	<ul>
		 * 		<li>{@link #setTitles(Map, boolean, long)}</li>
		 * 		<li>{@link #setLine(Map, int, boolean, long)}</li>
		 * 	</ul>
		 */
		public abstract Scoreboard build();
		
	}
	
}
