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

package me.remigio07.chatplugin.api.server.util.manager;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArrayList;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.GameFeature;

/**
 * Manager that handles the server's TPS.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/TPS">ChatPlugin wiki/Modules/TPS</a>
 */
@GameFeature(
		name = "tps",
		availableOnBukkit = true,
		availableOnSponge = true,
		spigotRequired = true,
		paperRequired = false,
		minimumBukkitVersion = Version.V1_8,
		minimumSpongeVersion = Version.V1_8
		)
public abstract class TPSManager implements ChatPluginManager, Runnable {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with the TPS's information.
	 * 
	 * <p><strong>Content:</strong> ["tps_1_min", "tps_5_min", "tps_15_min", "tps_1_min_format", "tps_5_min_format", "tps_15_min_format"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/TPS#placeholders">ChatPlugin wiki/Modules/TPS/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "tps_1_min", "tps_5_min", "tps_15_min", "tps_1_min_format", "tps_5_min_format", "tps_15_min_format" };
	protected static TPSManager instance;
	protected boolean enabled;
	protected List<TPSQuality> qualities = new CopyOnWriteArrayList<>();
	protected double[] recentTPS = { 20D, 20D, 20D };
	protected long updateTimeout, timerTaskID = -1, loadTime;
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p><strong>Found at:</strong> "tps.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the loaded TPS qualities list.
	 * 
	 * <p>You may modify the returned list, but it needs to be
	 * sorted using {@link TPSQuality#compareTo(TPSQuality)}.</p>
	 * 
	 * <p><strong>Found at:</strong> "tps.qualities" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return TPS qualities
	 * @see Collections#sort(List)
	 */
	public List<TPSQuality> getQualities() {
		return qualities;
	}
	
	/**
	 * Gets the recent TPS in the last 1m, 5m, 15m.
	 * 
	 * @return Server's recent TPS
	 */
	public double[] getRecentTPS() {
		return recentTPS;
	}
	
	/**
	 * Gets the timeout between TPS updates, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "tps.update-timeout" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Timeout between updates
	 */
	public long getUpdateTimeout() {
		return updateTimeout;
	}
	
	/**
	 * Gets the update task's ID.
	 * 
	 * <p>You can interact with it through {@link TaskManager}'s methods.</p>
	 * 
	 * @return Update task's ID
	 */
	public long getTimerTaskID() {
		return timerTaskID;
	}
	
	/**
	 * Gets this module's manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static TPSManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic server's TPS updater, called once every {@link #getUpdateTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Gets the TPS for the specified time interval.
	 * 
	 * @param interval Time interval to check
	 * @return Server's TPS
	 */
	public abstract double getTPS(TPSTimeInterval interval);
	
	/**
	 * Establishes which {@link TPSQuality} a TPS value belongs to.
	 * 
	 * @param tps TPS to check
	 * @return Resulting TPS quality
	 * @throws IllegalArgumentException If <code>tps &lt; 0</code>
	 */
	public abstract TPSQuality getTPSQuality(double tps);
	
	/**
	 * Formats the specified TPS using its {@link TPSQuality}'s colors.
	 * 
	 * @param tps TPS to format
	 * @param language Language to get the message for
	 * @return Formatted TPS with colors
	 * @throws IllegalArgumentException If <code>tps &lt; 0</code>
	 */
	public abstract String formatTPS(double tps, Language language);
	
	/**
	 * Formats the specified interval's TPS
	 * using its {@link TPSQuality}'s colors.
	 * 
	 * @param interval Time interval to format for
	 * @param language Language to get the message for
	 * @return Formatted TPS with colors
	 */
	public abstract String formatTPS(TPSTimeInterval interval, Language language);
	
	/**
	 * Translates an input string with the TPS's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to know the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract String formatPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string list with the TPS's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to know the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract List<String> formatPlaceholders(List<String> input, Language language);
	
	/**
	 * Represents a TPS quality.
	 * 
	 * <p>You can specify different intervals for every quality through
	 * the config at section <code>tps</code>. Every interval has a
	 * different color which can be specified in the messages' files.</p>
	 */
	public class TPSQuality implements Comparable<TPSQuality> {
		
		private String id;
		private double minimumTPS;
		
		/**
		 * Constructs a new TPS quality.
		 * 
		 * @param id Quality's ID
		 * @param minimumTPS Quality's minimum TPS
		 * @throws IllegalArgumentException If <code>minimumTPS &lt; 0</code>
		 */
		public TPSQuality(String id, double minimumTPS) {
			if (minimumTPS < 0)
				throw new IllegalArgumentException("Specified minimum TPS is less than 0");
			this.id = id;
			this.minimumTPS = minimumTPS;
		}
		
		/**
		 * Compares two TPS qualities based on their
		 * {@link #getMinimumTPS()}; lower TPS first.
		 */
		@Override
		public int compareTo(TPSQuality o) {
			return minimumTPS < o.getMinimumTPS() ? -1 : minimumTPS == o.getMinimumTPS() ? 0 : 1;
		}
		
		@Override
		public String toString() {
			return new StringJoiner(", ", "TPSQuality{", "}")
					.add("id=\"" + id + "\"")
					.add("minimumTPS=" + minimumTPS)
					.toString();
		}
		
		/**
		 * Gets this quality's ID.
		 * 
		 * @return Quality's ID
		 */
		public String getID() {
			return id;
		}
		
		/**
		 * Gets this quality's minimum TPS.
		 * 
		 * @return Quality's minimum TPS
		 */
		public double getMinimumTPS() {
			return minimumTPS;
		}
		
		/**
		 * Gets this quality's color.
		 * 
		 * @param language Language to get the color for
		 * @return Quality's color
		 */
		public String getColor(Language language) {
			return language.getMessage("tps-qualities." + id);
		}
		
	}
	
	/**
	 * Represents a time interval (1m, 5m, 15m).
	 */
	public enum TPSTimeInterval {
		
		/**
		 * Represents a one minute time interval (1m).
		 */
		ONE_MINUTE,
		
		/**
		 * Represents a five minutes time interval (5m).
		 */
		FIVE_MINUTES,
		
		/**
		 * Represents a fifteen minutes time interval (15m).
		 */
		FIFTEEN_MINUTES;
		
	}
	
}
