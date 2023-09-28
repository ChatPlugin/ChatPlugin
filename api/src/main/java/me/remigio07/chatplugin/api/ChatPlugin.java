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

package me.remigio07.chatplugin.api;

import java.io.File;
import java.util.Scanner;

import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.LogManager.LoggerType;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * ChatPlugin's main class' abstraction.
 */
public abstract class ChatPlugin {
	
	/**
	 * String containing the plugin's current version.
	 * 
	 * <p><strong>Content:</strong> ?</p>
	 */
	public static final String VERSION;
	protected static ChatPlugin instance;
	protected ChatPluginManagers managers;
	protected Object logger;
	protected File dataFolder;
	protected boolean loaded, started, reloading;
	protected int startupTime, lastReloadTime = -1;
	
	static {
		try (Scanner scanner = new Scanner(ChatPlugin.class.getResourceAsStream("/plugin.yml"), "UTF-8")) {
			scanner.nextLine();
			scanner.nextLine();
			
			String version = scanner.nextLine();
			VERSION = version.substring(10, version.indexOf('\'', 10));
		}
	}
	
	/**
	 * Checks if ChatPlugin has finished (re)loading.
	 * 
	 * @return Whether the plugin is loaded
	 */
	public boolean isLoaded() {
		return loaded;
	}
	
	/**
	 * Checks if ChatPlugin has started correctly.
	 * 
	 * @return Whether the plugin has started
	 */
	public boolean hasStarted() {
		return started;
	}
	
	/**
	 * Checks if a reload is being performed.
	 * 
	 * @return Whether the plugin is reloading
	 */
	public synchronized boolean isReloading() {
		return reloading;
	}
	
	/**
	 * Gets the managers' manager. Yeah...
	 * 
	 * @return Managers' manager
	 */
	public ChatPluginManagers getManagers() {
		return managers;
	}
	
	/**
	 * Gets the plugin's logger. There are two loggers supported.
	 * 
	 * @return Plugin's logger
	 * @see LoggerType
	 */
	public Object getLogger() {
		return logger;
	}
	
	/**
	 * Gets ChatPlugin's data folder in plugins folder.
	 * 
	 * @return Data folder
	 */
	public File getDataFolder() {
		return dataFolder;
	}
	
	/**
	 * Gets the time elapsed during ChatPlugin's startup, in milliseconds.
	 * 
	 * @return Time elapsed in milliseconds
	 */
	public int getStartupTime() {
		return startupTime;
	}
	
	/**
	 * Gets the time elapsed during ChatPlugin's last reload, in milliseconds.
	 * 
	 * @return Time elapsed in milliseconds
	 */
	public int getLastReloadTime() {
		return lastReloadTime;
	}
	
	/**
	 * Gets ChatPlugin's instance, the main one.
	 * 
	 * @return Main instance
	 */
	public static ChatPlugin getInstance() {
		return instance;
	}
	
	/**
	 * Reloads ChatPlugin. Totally.
	 * If the reload fails the plugin will be safely disabled
	 * and an error message will be sent to the console.
	 * 
	 * @return Time elapsed in milliseconds or 0 if not loaded or -1 if failed
	 */
	public abstract int reload();
	
	/**
	 * Unloads ChatPlugin.
	 * If the unload fails the plugin will be safely disabled
	 * and an error message will be sent to the console.
	 * 
	 * @deprecated Internal use only. You should never need to manually unload ChatPlugin.
	 * @return Time elapsed in milliseconds or 0 if already unloaded or -1 if failed
	 */
	@Deprecated
	public abstract int unload();
	
	/**
	 * Runs a command from the console.
	 * 
	 * @param command Command to run
	 */
	public abstract void runConsoleCommand(String command);
	
	/**
	 * Sends a message to the console.
	 * 
	 * @param message Message to send
	 * @param log Whether to {@link LogManager#writeToFile(String)} the message
	 */
	public abstract void sendConsoleMessage(String message, boolean log);
	
	/**
	 * Prints the beautiful start message.
	 */
	public abstract void printStartMessage();
	
	/**
	 * Checks if ChatPlugin is running on online mode.
	 * 
	 * @return Whether online mode is enabled
	 * @throws IllegalStateException If <code>!</code>{@link Environment#isProxy()} and <code>!</code>{@link #isLoaded()}
	 */
	public abstract boolean isOnlineMode();
	
	/**
	 * Checks if a licensed copy of ChatPlugin is running.
	 * 
	 * @return Whether the plugin is premium
	 */
	public abstract boolean isPremium();
	
}
