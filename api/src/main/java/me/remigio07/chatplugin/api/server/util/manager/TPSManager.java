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

package me.remigio07.chatplugin.api.server.util.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Manager that handles the server's TPS.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/TPS">ChatPlugin wiki/Modules/TPS</a>
 */
public abstract class TPSManager extends TimerTask implements ChatPluginManager {
	
	protected static TPSManager instance;
	protected boolean enabled;
	protected List<TPSQuality> qualities = new ArrayList<>();
	protected double[] recentTPS = new double[] { 20D, 20D, 20D };
	protected long updateTimeout, timerTaskID = -1, loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the loaded TPS qualities list.
	 * 
	 * @return TPS qualities
	 * @see TPSQuality
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
	 * <p><strong>Found at:</strong> "tps.update-timeout-ms" in {@link ConfigurationType#CONFIG}</p>
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
	 * Establishes what {@link TPSQuality} a TPS value belongs to.
	 * 
	 * @param tps TPS to check
	 * @return Resulting TPS quality
	 */
	public abstract TPSQuality getTPSQuality(double tps);
	
	/**
	 * Returns a string formatted with given time interval's {@link TPSQuality}'s colors. 
	 * 
	 * @param interval Time interval to format
	 * @param language Language to get the message for
	 * @return Formatted TPS with colors
	 */
	public abstract String formatTPS(TPSTimeInterval interval, Language language);
	
	/**
	 * Returns a string formatted with the given TPS' {@link TPSQuality}'s colors.
	 * 
	 * @param tps TPS to format
	 * @param language Language to get the message for
	 * @return Formatted TPS with colors
	 */
	public abstract String formatTPS(double tps, Language language);
	
	/**
	 * Represents a TPS quality.
	 * 
	 * <p>You can specify different intervals for every quality through
	 * the config at section <code>tps</code>. Every interval has a
	 * different color which can be specified in the messages' files.</p>
	 */
	public class TPSQuality {
		
		private String id;
		private double minTPS;
		
		/**
		 * Constructs a new TPS quality.
		 * 
		 * @param id Quality's ID
		 * @param minTPS Quality's minimum TPS
		 */
		public TPSQuality(String id, double minTPS) {
			this.id = id;
			this.minTPS = minTPS;
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
		public double getMinTPS() {
			return minTPS;
		}
		
		/**
		 * Gets this quality's minimum TPS.
		 * 
		 * @param minTPS Quality's minimum TPS
		 */
		public void setMinTPS(double minTPS) {
			this.minTPS = minTPS;
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
