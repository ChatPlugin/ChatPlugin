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
 * Manager that handles the server's MSPT.
 * 
 * <p><strong>Minimum version:</strong> Paper {@linkplain Version#V1_16 1.16}</p>
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/MSPT">ChatPlugin wiki/Modules/MSPT</a>
 */
@GameFeature(
		name = "MSPT",
		paperRequired = true,
		minimumBukkitVersion = Version.V1_16,
		minimumSpongeVersion = Version.UNSUPPORTED,
		minimumFabricVersion = Version.UNSUPPORTED
		)
public abstract class MSPTManager implements ChatPluginManager, Runnable {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with the MSPT's information.
	 * 
	 * <p><strong>Content:</strong> ["mspt_5_sec_avg", "mspt_5_sec_min", "mspt_5_sec_max", "mspt_10_sec_avg", "mspt_10_sec_min", "mspt_10_sec_max", "mspt_1_min_avg", "mspt_1_min_min", "mspt_1_min_max", "mspt_5_sec_avg_format", "mspt_5_sec_min_format", "mspt_5_sec_max_format", "mspt_10_sec_avg_format", "mspt_10_sec_min_format", "mspt_10_sec_max_format", "mspt_1_min_avg_format", "mspt_1_min_min_format", "mspt_1_min_max_format"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/MSPT#placeholders">ChatPlugin wiki/Modules/MSPT/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "mspt_5_sec_avg", "mspt_5_sec_min", "mspt_5_sec_max", "mspt_10_sec_avg", "mspt_10_sec_min", "mspt_10_sec_max", "mspt_1_min_avg", "mspt_1_min_min", "mspt_1_min_max", "mspt_5_sec_avg_format", "mspt_5_sec_min_format", "mspt_5_sec_max_format", "mspt_10_sec_avg_format", "mspt_10_sec_min_format", "mspt_10_sec_max_format", "mspt_1_min_avg_format", "mspt_1_min_min_format", "mspt_1_min_max_format" };
	protected static MSPTManager instance;
	protected boolean enabled;
	protected List<MSPTQuality> qualities = new CopyOnWriteArrayList<>();
	protected double[] averageMSPT = array(), minimumMSPT = array(), maximumMSPT = array();
	protected long updateTimeout, timerTaskID = -1, loadTime;
	
	protected double[] array() {
		return new double[] { 0D, 0D, 0D};
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p><strong>Found at:</strong> "mspt.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the loaded MSPT qualities list.
	 * 
	 * <p>You may modify the returned list, but it needs to be
	 * sorted using {@link MSPTQuality#compareTo(MSPTQuality)}.</p>
	 * 
	 * <p><strong>Found at:</strong> "mspt.qualities" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return MSPT qualities
	 * @see Collections#sort(List)
	 */
	public List<MSPTQuality> getQualities() {
		return qualities;
	}
	
	/**
	 * Gets the average MSPT in the last 5s, 10s, 1m.
	 * 
	 * @return Server's average MSPT
	 */
	public double[] getAverageMSPT() {
		return averageMSPT;
	}
	
	/**
	 * Gets the minimum MSPT in the last 5s, 10s, 1m.
	 * 
	 * @return Server's minimum MSPT
	 */
	public double[] getMinimumMSPT() {
		return minimumMSPT;
	}
	
	/**
	 * Gets the maximum MSPT in the last 5s, 10s, 1m.
	 * 
	 * @return Server's maximum MSPT
	 */
	public double[] getMaximumMSPT() {
		return maximumMSPT;
	}
	
	/**
	 * Gets the timeout between MSPT updates, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "mspt.update-timeout" in {@link ConfigurationType#CONFIG}</p>
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
	public static MSPTManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic server's MSPT updater, called once every {@link #getUpdateTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Gets the average MSPT for the specified time interval.
	 * 
	 * @param interval Time interval to check
	 * @return Server's average MSPT
	 */
	public abstract double getAverageMSPT(MSPTTimeInterval interval);
	
	/**
	 * Gets the minimum MSPT for the specified time interval.
	 * 
	 * @param interval Time interval to check
	 * @return Server's minimum MSPT
	 */
	public abstract double getMinimumMSPT(MSPTTimeInterval interval);
	
	/**
	 * Gets the maximum MSPT for the specified time interval.
	 * 
	 * @param interval Time interval to check
	 * @return Server's maximum MSPT
	 */
	public abstract double getMaximumMSPT(MSPTTimeInterval interval);
	
	/**
	 * Establishes which {@link MSPTQuality} a MSPT value belongs to.
	 * 
	 * @param mspt MSPT to check
	 * @return Resulting MSPT quality
	 * @throws IllegalArgumentException If <code>mspt &lt; 0</code>
	 */
	public abstract MSPTQuality getMSPTQuality(double mspt);
	
	/**
	 * Formats the specified MSPT using its {@link MSPTQuality}'s colors.
	 * 
	 * @param mspt MSPT to format
	 * @param language Language to get the message for
	 * @return Formatted MSPT with colors
	 * @throws IllegalArgumentException If <code>mspt &lt; 0</code>
	 */
	public abstract String formatMSPT(double mspt, Language language);
	
	/**
	 * Formats the specified interval's average
	 * MSPT using its {@link MSPTQuality}'s colors. 
	 * 
	 * @param interval Time interval to format for
	 * @param language Language to get the message for
	 * @return Formatted average MSPT with colors
	 */
	public abstract String formatAverageMSPT(MSPTTimeInterval interval, Language language);
	
	/**
	 * Formats the specified interval's minimum
	 * MSPT using its {@link MSPTQuality}'s colors. 
	 * 
	 * @param interval Time interval to format for
	 * @param language Language to get the message for
	 * @return Formatted minimum MSPT with colors
	 */
	public abstract String formatMinimumMSPT(MSPTTimeInterval interval, Language language);
	
	/**
	 * Formats the specified interval's maximum
	 * MSPT using its {@link MSPTQuality}'s colors. 
	 * 
	 * @param interval Time interval to format for
	 * @param language Language to get the message for
	 * @return Formatted maximum MSPT with colors
	 */
	public abstract String formatMaximumMSPT(MSPTTimeInterval interval, Language language);
	
	/**
	 * Translates an input string with the MSPT's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to know the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract String formatPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string list with the MSPT's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to know the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract List<String> formatPlaceholders(List<String> input, Language language);
	
	/**
	 * Represents a MSPT quality.
	 * 
	 * <p>You can specify different intervals for every quality through
	 * the config at section <code>mspt</code>. Every interval has a
	 * different color which can be specified in the messages' files.</p>
	 */
	public class MSPTQuality implements Comparable<MSPTQuality> {
		
		private String id;
		private double maximumMSPT;
		
		/**
		 * Constructs a new MSPT quality.
		 * 
		 * @param id Quality's ID
		 * @param maximumMSPT Quality's maximum MSPT
		 * @throws IllegalArgumentException If <code>maximumMSPT &lt; 0</code>
		 */
		public MSPTQuality(String id, double maximumMSPT) {
			if (maximumMSPT < 0)
				throw new IllegalArgumentException("Specified maximum MSPT is less than 0");
			this.id = id;
			this.maximumMSPT = maximumMSPT;
		}
		
		/**
		 * Compares two MSPT qualities based on their
		 * {@link #getMaximumMSPT()}; lower MSPT first.
		 */
		@Override
		public int compareTo(MSPTQuality o) {
			return maximumMSPT < o.getMaximumMSPT() ? -1 : maximumMSPT == o.getMaximumMSPT() ? 0 : 1;
		}
		
		@Override
		public String toString() {
			return new StringJoiner(", ", "MSPTQuality{", "}")
					.add("id=\"" + id + "\"")
					.add("maximumMSPT=" + maximumMSPT)
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
		 * Gets this quality's maximum MSPT.
		 * 
		 * @return Quality's maximum MSPT
		 */
		public double getMaximumMSPT() {
			return maximumMSPT;
		}
		
		/**
		 * Gets this quality's color.
		 * 
		 * @param language Language to get the color for
		 * @return Quality's color
		 */
		public String getColor(Language language) {
			return language.getMessage("mspt-qualities." + id);
		}
		
	}
	
	/**
	 * Represents a time interval (5s, 10s, 1m).
	 */
	public enum MSPTTimeInterval {
		
		/**
		 * Represents a five seconds time interval (5s).
		 */
		FIVE_SECONDS,
		
		/**
		 * Represents a ten seconds time interval (10s).
		 */
		TEN_SECONDS,
		
		/**
		 * Represents a one minute time interval (1m).
		 */
		ONE_MINUTE;
		
	}
	
}
