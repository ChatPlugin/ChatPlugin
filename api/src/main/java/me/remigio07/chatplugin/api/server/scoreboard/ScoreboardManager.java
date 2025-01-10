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

import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.GameFeature;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles {@link Scoreboard}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Scoreboards">ChatPlugin wiki/Modules/Scoreboards</a>
 */
@GameFeature(
		name = "scoreboard",
		availableOnBukkit = true,
		availableOnSponge = true,
		spigotRequired = false,
		minimumBukkitVersion = Version.V1_5,
		minimumSpongeVersion = Version.V1_8
		)
public abstract class ScoreboardManager implements ChatPluginManager {
	
	/**
	 * Pattern representing the allowed scoreboard IDs.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,36}$"</p>
	 * 
	 * @see #isValidScoreboardID(String)
	 */
	public static final Pattern SCOREBOARD_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	protected static ScoreboardManager instance;
	protected boolean enabled;
	protected List<Scoreboard> scoreboards = new CopyOnWriteArrayList<>();
	protected Timer timer;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "settings.enabled" in {@link ConfigurationType#DEFAULT_SCOREBOARD}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the list of loaded scoreboards.
	 * 
	 * <p>You may modify the returned list.</p>
	 * 
	 * @return Loaded scoreboards' list
	 */
	public List<Scoreboard> getScoreboards() {
		return scoreboards;
	}
	
	/**
	 * Adds a scoreboard to {@link #getScoreboards()}.
	 * 
	 * <p><strong>Note:</strong> the scoreboard will be removed on {@link #unload()}.</p>
	 * 
	 * @param scoreboard Scoreboard to add
	 * @throws IllegalArgumentException If {@link Scoreboard#getID()} is already in use, case insensitive
	 */
	public void addScoreboard(Scoreboard scoreboard) {
		if (scoreboards.stream().anyMatch(other -> other.getID().equalsIgnoreCase(scoreboard.getID())))
			throw new IllegalArgumentException("Specified ID (" + scoreboard.getID() + ") is already in use");
		else scoreboards.add(scoreboard);
	}
	
	/**
	 * Gets a scoreboard from {@link #getScoreboards()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the scoreboard is not loaded.</p>
	 * 
	 * @param id Scoreboard's ID, case insensitive
	 * @return Loaded scoreboard
	 */
	@Nullable(why = "Specified scoreboard may not be loaded")
	public Scoreboard getScoreboard(String id) {
		return scoreboards.stream().filter(scoreboard -> scoreboard.getID().equalsIgnoreCase(id)).findAny().orElse(null);
	}
	
	/**
	 * Gets the timer used to schedule scoreboard updates.
	 * 
	 * @return Timer used
	 */
	public Timer getTimer() {
		return timer;
	}
	
	/**
	 * Checks if the specified String is a valid scoreboard ID.
	 * 
	 * @param scoreboardID Scoreboard ID to check
	 * @return Whether the specified scoreboard ID is valid
	 * @see #SCOREBOARD_ID_PATTERN
	 */
	public boolean isValidScoreboardID(String scoreboardID) {
		return SCOREBOARD_ID_PATTERN.matcher(scoreboardID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ScoreboardManager getInstance() {
		return instance;
	}
	
	/**
	 * Creates a new scoreboard builder.
	 * 
	 * @param id Scoreboard's ID
	 * @param abbreviateLongText Whether to abbreviate long text ({@link Scoreboard#shouldAbbreviateLongText()})
	 * @param placeholderTypes Placeholders used to translate titles and lines ({@link Scoreboard#getPlaceholderTypes()})
	 * @return New scoreboard builder
	 * @throws IllegalArgumentException If specified ID <code>!</code>{@link #isValidScoreboardID(String)}
	 */
	public abstract Scoreboard.Builder createScoreboardBuilder(
			String id,
			boolean abbreviateLongText,
			List<PlaceholderType> placeholderTypes
			);
	
	/**
	 * Reads a scoreboard from the specified configuration.
	 * 
	 * <p>The scoreboard's ID is obtained by removing {@link Configuration#getFile()}'s
	 * extension and has to match {@link #SCOREBOARD_ID_PATTERN}.</p>
	 * 
	 * @param configuration Configuration to read
	 * @return New scoreboard
	 * @throws IllegalArgumentException If at least one of the following is true:
	 * 	<ul>
	 * 		<li>scoreboard's ID <code>!</code>{@link #isValidScoreboardID(String)}</li>
	 * 		<li>translations for {@link Language#getMainLanguage()} are not present</li>
	 * 		<li>no valid lines have been specified at the "lines" section</li>
	 * 		<li>the {@link NumbersDisplayMode} at "settings.numbers.display-mode" is invalid</li>
	 * 		<li>"settings.numbers.custom-text.colors.cycle-timeout" is not positive or invalid</li>
	 * 		<li>"settings.numbers.custom-text.colors.interpolations" is negative</li>
	 * 		<li>the list at "settings.numbers.custom-text.colors.gradient" is empty</li>
	 * 	</ul>
	 * @throws IndexOutOfBoundsException If line indexes outside of range 0 - 14 are specified
	 * @throws UnsupportedOperationException If <code>!</code>{@link NumbersDisplayMode#isSupported()}
	 * @throws NumberFormatException If colors of the gradient use an invalid format
	 */
	public abstract Scoreboard createScoreboard(Configuration configuration); // TODO: add documentation for config's format
	
}
