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

import java.util.List;
import java.util.TimerTask;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;

/**
 * Represents a {@link Scoreboard}'s numbers.
 */
public abstract class ScoreboardNumbers extends TimerTask {
	
	protected Scoreboard scoreboard;
	protected NumbersDisplayMode displayMode;
	protected String customTextValue;
	protected long customTextColorsCycleTimeout;
	protected int customTextColorsInterpolations, timerIndex;
	protected List<ChatColor> customTextColorsGradient, customTextColorsInterpolated;
	
	/**
	 * Gets the scoreboard associated with these numbers.
	 * 
	 * @return Associated scoreboard
	 */
	@NotNull
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	/**
	 * Gets these numbers' display mode.
	 * 
	 * <p><strong>Found at:</strong> "settings.numbers.display-mode" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return Numbers' display mode
	 */
	public NumbersDisplayMode getDisplayMode() {
		return displayMode;
	}
	
	/**
	 * Gets these numbers' custom text's value.
	 * 
	 * <p><strong>Found at:</strong> "settings.numbers.custom-text.value" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return Numbers' custom text's value
	 */
	public String getCustomTextValue() {
		return customTextValue;
	}
	
	/**
	 * Gets the timeout between color cycles, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "settings.numbers.custom-text.colors.cycle-timeout" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return Time between color cycles
	 */
	public long getCustomTextColorsCycleTimeout() {
		return customTextColorsCycleTimeout;
	}
	
	/**
	 * Gets the amount of interpolations (or variations)
	 * between each color of {@link #customTextColorsGradient}.
	 * 
	 * <p><strong>Found at:</strong> "settings.numbers.custom-text.colors.interpolations" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return Interpolations between provided colors
	 */
	public int getCustomTextColorsInterpolations() {
		return customTextColorsInterpolations;
	}
	
	/**
	 * Gets the colors used to create the gradient.
	 * 
	 * <p>Do <strong>not</strong> modify the returned list.</p>
	 * 
	 * <p><strong>Found at:</strong> "settings.numbers.custom-text.colors.gradient" in {@link Scoreboard#getConfiguration()}</p>
	 * 
	 * @return Colors to cycle through
	 */
	public List<ChatColor> getCustomTextColorsGradient() {
		return customTextColorsGradient;
	}
	
	/**
	 * Gets the colors interpolated using variants of the original colors.
	 * 
	 * <p>Do <strong>not</strong> modify the returned list.</p>
	 * 
	 * @return Interpolated colors
	 */
	public List<ChatColor> getCustomTextColorsInterpolated() {
		return customTextColorsInterpolated;
	}
	
	/**
	 * Gets the {@link #run()}'s timer's index of {@link #getCustomTextColorsInterpolated()}.
	 * 
	 * @return Numbers' timer's index
	 */
	public int getTimerIndex() {
		return timerIndex;
	}
	
	/**
	 * Automatic color updater, called once every {@link #getCustomTextColorsCycleTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
}
