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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles loaded players' pings.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Ping">ChatPlugin wiki/Modules/Ping</a>
 */
public abstract class PingManager implements ChatPluginManager, Runnable {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with a ping's information.
	 * 
	 * <p><strong>Content:</strong> ["ping", "ping_format", "ping_quality_color", "ping_quality_text"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Ping#placeholders">ChatPlugin wiki/Modules/Ping/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "ping", "ping_format", "ping_quality_color", "ping_quality_text" };
	protected static PingManager instance;
	protected boolean enabled;
	protected List<PingQuality> qualities = new ArrayList<>();
	protected long updateTimeout, timerTaskID = -1, loadTime;
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p><strong>Found at:</strong> "ping.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the loaded ping qualities list.
	 * 
	 * <p>You may modify the returned list, but it needs to be
	 * sorted using {@link PingQuality#compareTo(PingQuality)}.</p>
	 * 
	 * <p><strong>Found at:</strong> "ping.qualities" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Ping qualities
	 * @see Collections#sort(List)
	 */
	public List<PingQuality> getQualities() {
		return qualities;
	}
	
	/**
	 * Gets the timeout between ping updates, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "ping.update-timeout" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Time between updates
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
	 * Returns a player's cached ping.
	 * 
	 * @param player Player to get the ping for
	 * @return Player's cached ping, in milliseconds
	 */
	public int getCachedPing(ChatPluginServerPlayer player) {
		return player.getPing();
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
	 * Automatic online players' ping updater, called
	 * once every {@link #getUpdateTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Gets a player's real time ping.
	 * 
	 * @deprecated This method will fetch the ping from the player's
	 * connection class, using slow reflection methods. If possible, use
	 * {@link #getCachedPing(ChatPluginServerPlayer)} for cached results.
	 * @param player Player to get the ping of
	 * @return Player's real time ping, in milliseconds
	 */
	@Deprecated
	public abstract int getRealTimePing(ChatPluginServerPlayer player);
	
	/**
	 * Establishes which {@link PingQuality} a ping
	 * belongs to according to the response time.
	 * 
	 * @param ping Ping to check, in milliseconds
	 * @return Resulting ping quality
	 * @throws IllegalArgumentException If <code>ping &lt; 0</code>
	 */
	public abstract PingQuality getPingQuality(int ping);
	
	/**
	 * Formats the specified ping using its {@link PingQuality}'s colors.
	 * 
	 * @param ping Ping to format, in milliseconds
	 * @param language Language to get the message for
	 * @return Formatted ping with colors
	 * @throws IllegalArgumentException If <code>ping &lt; 0</code>
	 */
	public abstract String formatPing(int ping, Language language);
	
	/**
	 * Translates an input string with the ping's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to know the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param ping Ping to format, in milliseconds
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 * @throws IllegalArgumentException If <code>ping &lt; 0</code>
	 */
	public abstract String formatPlaceholders(String input, int ping, Language language);
	
	/**
	 * Translates an input string list with the ping's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to know the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param ping Ping to format, in milliseconds
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 * @throws IllegalArgumentException If <code>ping &lt; 0</code>
	 */
	public abstract List<String> formatPlaceholders(List<String> input, int ping, Language language);
	
	/**
	 * Represents a ping quality.
	 * 
	 * <p>You can specify different intervals for every quality through
	 * the config at section <code>ping</code>. Every interval has a
	 * different color which can be specified in the messages' files.</p>
	 */
	public class PingQuality implements Comparable<PingQuality> {
		
		private String id;
		private int maximumPing;
		
		/**
		 * Constructs a new ping quality.
		 * 
		 * @param id Quality's ID
		 * @param maximumPing Quality's maximum ping, in milliseconds
		 * @throws IllegalArgumentException If <code>maximumPing &lt; 0</code>
		 */
		public PingQuality(String id, int maximumPing) {
			if (maximumPing < 0)
				throw new IllegalArgumentException("Specified maximum ping is less than 0");
			this.id = id;
			this.maximumPing = maximumPing;
		}
		
		/**
		 * Compares two ping qualities based on their
		 * {@link #getMaximumPing()}; lower pings first.
		 */
		@Override
		public int compareTo(PingQuality o) {
			return maximumPing < o.getMaximumPing() ? -1 : maximumPing == o.getMaximumPing() ? 0 : 1;
		}
		
		@Override
		public String toString() {
			return new StringJoiner(", ", "PingQuality{", "}")
					.add("id=\"" + id + "\"")
					.add("maximumPing=" + maximumPing)
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
		 * Gets this quality's maximum ping.
		 * 
		 * @return Quality's maximum ping, in milliseconds
		 */
		public int getMaximumPing() {
			return maximumPing;
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
