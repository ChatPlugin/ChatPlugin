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

package me.remigio07.chatplugin.api.common.util.manager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;

/**
 * Manager that handles logging, the log file and the debug mode.
 * 
 * <p>This is the first manager loaded by ChatPlugin at startup!</p>
 */
public abstract class LogManager implements ChatPluginManager {
	
	protected static LogManager instance;
	protected boolean enabled;
	protected Boolean debug = null;
	protected LoggerType loggerType;
	protected Path file;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("[MM-dd HH:mm:ss]: ");
	protected long loadTime;
	
	@Override
	public void load() throws ChatPluginManagerException {
		try {
			if (!Files.exists(file = ChatPlugin.getInstance().getDataFolder().resolve("chatplugin.log")))
				Files.createFile(file);
			else if (Files.size(file) > 10 * MemoryUtils.MEGABYTE.getToBytesRatio())
				logMessage("Log file chatplugin.log's size is over 10 MB (currently " + MemoryUtils.formatMemory(Files.size(file), MemoryUtils.MEGABYTE) + " MB). This might impact performance. It is recommended to stop the server and rename or delete the file before starting again.", LogLevel.WARNING);	
		} catch (IOException ioe) {
			throw new ChatPluginManagerException(this, ioe);
		} enabled = true;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		debug = null;
		loggerType = null;
		file = null;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the debug mode is enabled.
	 * 
	 * @return Whether debug mode is enabled
	 */
	public boolean isDebug() {
		return debug == null ? false : debug;
	}
	
	/**
	 * Gets the current logger type.
	 * 
	 * @return Logger type
	 */
	public LoggerType getLoggerType() {
		return loggerType;
	}
	
	/**
	 * Sets the current logger type.
	 * 
	 * @param loggerType Logger type
	 */
	public void setLoggerType(LoggerType loggerType) {
		this.loggerType = loggerType;
	}
	
	/**
	 * Enables or disables the debug mode and writes changes to config.yml.
	 * 
	 * @param debug Whether the debug should be enabled
	 * @throws IOException If something goes wrong while saving config.yml
	 */
	public void setDebug(boolean debug) throws IOException {
		if (this.debug == null)
			this.debug = debug;
		if (this.debug == debug)
			return;
		Configuration config = ConfigurationType.CONFIG.get();
		this.debug = debug;
		
		config.set("settings.debug", debug);
		config.save();
		logMessage("Debug mode " + (debug ? "enabled" : "disabled") + ".", LogLevel.INFO);
	}
	
	/**
	 * Gets the log file's path.
	 * 
	 * @return Log file's path
	 */
	public Path getFile() {
		return file;
	}
	
	/**
	 * Gets the date format used to write logs to file.
	 * 
	 * @return Date format
	 */
	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}
	
	/**
	 * Writes a line of text and the current date into {@link #getFile()}.
	 * 
	 * <p>A {@link ChatColor#stripColor(String)} is performed before writing the message.</p>
	 * 
	 * @param message Message to write
	 */
	public void writeToFile(String message) {
		if (file == null)
			return;
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardOpenOption.APPEND)) {
			writer.write(dateFormat.format(new Date()) + ChatColor.stripColor(message) + "\n");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Logs a message and writes it to file.
	 * 
	 * <p>This static method calls the instance method
	 * {@link #logMessage(String, LogLevel, Object...)} using the manager's current instance.
	 * It uses the log level's ID instead of the enum value, but they do the same thing.</p>
	 * 
	 * @param message Message to send and write to file
	 * @param logLevel Log level's ID (0, 1, 2, 3, 4)
	 * @param args Optional arguments ({@link Utils#replaceNumericPlaceholders(String, Object...)})
	 * @see LogLevel
	 */
	public static void log(String message, int logLevel, Object... args) {
		instance.logMessage(message, LogLevel.values()[logLevel < 0 || logLevel > 4 ? 0 : logLevel], args);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static LogManager getInstance() {
		return instance;
	}
	
	/**
	 * Logs a message and writes it to file.
	 * 
	 * @param message Message to send and write to file
	 * @param logLevel Log level
	 * @param args Optional arguments ({@link Utils#replaceNumericPlaceholders(String, Object...)})
	 */
	public abstract void logMessage(String message, LogLevel logLevel, Object... args);
	
	/**
	 * Represents a log level.
	 * 
	 * <p>There are currently 5 log levels available and they are used
	 * to send different messages of various priority and importance.</p>
	 */
	public enum LogLevel {
		
		/**
		 * Represents the information level.
		 */
		INFO("[INFO] "),
		
		/**
		 * Represents the warning level. Used for important messages.
		 */
		WARNING("[WARN] "),
		
		/**
		 * Represents the error level. Used for error messages, problems and issues.
		 */
		ERROR("[ERROR] "),
		
		/**
		 * Represents the debug level. These messages will be sent to
		 * the console only if {@link LogManager#isDebug()} is true.
		 */
		DEBUG("[DEBUG] "),
		
		/**
		 * Represents the second debug level. Unlike {@link #DEBUG} messages, these ones
		 * will not be sent to the console, but just written to {@link LogManager#getFile()}.
		 */
		DEBUG_FILE("[DEBUG] ");
		
		private String prefix;
		
		private LogLevel(String prefix) {
			this.prefix = prefix;
		}
		
		/**
		 * Gets this log level's prefix.
		 * 
		 * <p><strong>Example:</strong> {@link #INFO} ➝ "[INFO] "</p>
		 * 
		 * @return This log level's prefix
		 */
		public String getPrefix() {
			return prefix;
		}
		
		/**
		 * Checks whether this log level's messages should be sent to the console.
		 * 
		 * <p>Will return true unless <code>this == {@link #DEBUG_FILE}</code> or
		 * <code>this == {@link #DEBUG} &amp;&amp; !{@link LogManager#isDebug()}</code>.</p>
		 * 
		 * @return Whether this log level's messages should be sent
		 */
		public boolean shouldBeSent() {
			return !(this == DEBUG_FILE || (this == DEBUG && !LogManager.getInstance().isDebug()));
		}
		
	}
	
	/**
	 * Represents a logger type. There are just two possible values: Java's and SLF4J's.
	 */
	public enum LoggerType {
		
		/**
		 * Represents the Java integrated logger ({@link java.util.logging.Logger}).
		 * 
		 * <p>This is used by the Bukkit and BungeeCord implementations of ChatPlugin.</p>
		 */
		JAVA,
		
		/**
		 * Represents the SLF4J logger ({@link org.slf4j.Logger}).
		 * 
		 * <p>This is used by the Sponge and Velocity implementations of ChatPlugin.</p>
		 */
		SLF4J;
		
	}
	
}
