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

package me.remigio07.chatplugin.api.server.util.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles loaded players' pings.
 */
public abstract class PingManager extends TimerTask implements ChatPluginManager {
	
	protected static PingManager instance;
	protected boolean enabled;
	protected List<PingQuality> qualities = new ArrayList<>();
	protected long updateTimeout, timerTaskID = -1, loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the loaded ping qualities list.
	 * 
	 * @return Ping qualities
	 * @see PingQuality
	 */
	public List<PingQuality> getQualities() {
		return qualities;
	}
	
	/**
	 * Gets the timeout between ping updates, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "ping.update-timeout-ms" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Time between updates
	 */
	public long getUpdateTimeout() {
		return updateTimeout;
	}
	
	/**
	 * Gets the update task's ID. You can interact
	 * with it through {@link TaskManager}'s methods.
	 * 
	 * @return Update task's ID
	 */
	public long getTimerTaskID() {
		return timerTaskID;
	}
	
	/**
	 * Returns a player's cached ping. Latencies are cached in
	 * a map updated once every {@link #getUpdateTimeout()} ms.
	 * 
	 * If you need a real time ping (just a little more precise), you can use
	 * {@link #getRealTimePing(ChatPluginServerPlayer)}, but it's not recommended,
	 * as many calls of that method could be heavy to handle.
	 * 
	 * @param player Player to get the ping for
	 * @return Player's cached ping, in milliseconds
	 */
	public int getCachedPing(ChatPluginServerPlayer player) {
		return player.getPing();
	}
	
	/**
	 * Calls {@link #formatPing(int, Language)} specifying arguments
	 * {@link ChatPluginServerPlayer#getPing()} and
	 * {@link ChatPluginServerPlayer#getLanguage()} of given player.
	 * 
	 * @param player Player to get the ping for
	 * @return Formatted ping with colors
	 */
	public String formatPing(ChatPluginServerPlayer player) {
		return formatPing(player.getPing(), player.getLanguage());
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static PingManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic online players' ping updater, called once every {@link #getUpdateTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Gets a player's real time ping.
	 * 
	 * @deprecated This method will return the ping directly from the player's connection class
	 * using reflection. You should use {@link #getCachedPing(ChatPluginServerPlayer)} instead for
	 * a cached and less resource demanding ping request if performing multiple calls of this method.
	 * @param player Player to get the ping for
	 * @return Player's real time ping, in milliseconds
	 */
	@Deprecated
	public abstract int getRealTimePing(ChatPluginServerPlayer player);
	
	/**
	 * Establishes what {@link PingQuality} a ping belongs to
	 * according to the response time, expressed in milliseconds.
	 * 
	 * @param ms Ping to check
	 * @return Resulting ping quality
	 */
	public abstract PingQuality getPingQuality(int ms);
	
	/**
	 * Returns a string formatted with given ping's {@link PingQuality}'s colors. If you want just
	 * a player's ping in milliseconds, use {@link #getCachedPing(ChatPluginServerPlayer)}.
	 * 
	 * @param ms Latency, in milliseconds
	 * @param language Language to get the message for
	 * @return Formatted ping with colors
	 */
	public abstract String formatPing(int ms, Language language);
	
	/**
	 * Represents a ping quality. You can specify different intervals for every quality
	 * through the config at section <code>ping</code>. Every interval has a different
	 * color which can be specified in the messages' files.
	 */
	public class PingQuality {
		
		private String id;
		private int maxMs;
		
		/**
		 * Constructs a new ping quality.
		 * 
		 * @param id Quality's ID
		 * @param maxMs Quality's maximum milliseconds
		 */
		public PingQuality(String id, int maxMs) {
			this.id = id;
			this.maxMs = maxMs;
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
		 * Gets this quality's maximum milliseconds.
		 * 
		 * @return Quality's maximum milliseconds
		 */
		public int getMaxMs() {
			return maxMs;
		}
		
		/**
		 * Sets this quality's max milliseconds.
		 * 
		 * @param maxMs Quality's max milliseconds
		 */
		public void setMaxMs(int maxMs) {
			this.maxMs = maxMs;
		}
		
		/**
		 * Gets this quality's color.
		 * 
		 * @param language Language to get the color for
		 * @return Quality's color
		 */
		public String getColor(Language language) {
			return language.getMessage("ping." + id + ".color");
		}
		
		/**
		 * Gets this quality's text.
		 * 
		 * @param language Language to get the text for
		 * @return Quality's text
		 */
		public String getText(Language language) {
			return language.getMessage("ping." + id + ".text");
		}
		
	}
	
}
