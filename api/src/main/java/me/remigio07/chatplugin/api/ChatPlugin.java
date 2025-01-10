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

package me.remigio07.chatplugin.api;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginCrashEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginReloadEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginUnloadEvent;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.LogManager.LoggerType;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.bootstrap.BungeeCordBootstrapper;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;
import me.remigio07.chatplugin.bootstrap.VelocityBootstrapper;

/**
 * ChatPlugin's main class's abstraction.
 */
public abstract class ChatPlugin {
	
	/**
	 * String containing the plugin's current version.
	 * 
	 * <p><strong>Content:</strong> 🤷</p>
	 */
	public static final String VERSION;
	protected static ChatPlugin instance;
	protected static AtomicReference<ChatPluginState> state = new AtomicReference<>(ChatPluginState.NOT_STARTED_YET);
	protected ChatPluginManagers managers;
	protected Object logger;
	protected File dataFolder;
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
	 * Gets the managers' manager. Yeah...
	 * 
	 * @return Managers' manager
	 */
	public ChatPluginManagers getManagers() {
		return managers;
	}
	
	/**
	 * Gets the plugin's logger.
	 * 
	 * <p>There are two loggers supported:
	 * 	<ul>
	 * 		<li><a href="https://docs.oracle.com/javase/8/docs/api/java/util/logging/Logger.html"><code>java.util.logging.Logger</code></a> - Bukkit and BungeeCord environments</li>
	 * 		<li><a href="https://www.slf4j.org/api/org/slf4j/Logger.html"><code>org.slf4j.Logger</code></a> - Sponge and Velocity environments</li>
	 * 	</ul>
	 * 
	 * @return Plugin's logger
	 * @see LoggerType
	 */
	public Object getLogger() {
		return logger;
	}
	
	/**
	 * Gets the plugin's data folder in the plugins
	 * (Bukkit/BungeeCord/Velocity) or mods (Sponge) folder.
	 * 
	 * @return Plugin's data folder
	 */
	public File getDataFolder() {
		return dataFolder;
	}
	
	/**
	 * Gets the time elapsed during the plugin's startup, in milliseconds.
	 * 
	 * @return Startup's time, in milliseconds
	 */
	public int getStartupTime() {
		return startupTime;
	}
	
	/**
	 * Gets the time elapsed during the plugin's last reload, in milliseconds.
	 * 
	 * <p>Will return -1 if no reloads have been performed yet.</p>
	 * 
	 * @return Last reload's time, in milliseconds
	 */
	public int getLastReloadTime() {
		return lastReloadTime;
	}
	
	/**
	 * Gets the main instance of the plugin.
	 * 
	 * <p>You may need instances of the bootstrappers
	 * instead: check the following methods, too.</p>
	 * 
	 * @return Main instance
	 * @see BukkitBootstrapper#getInstance()
	 * @see SpongeBootstrapper#getInstance()
	 * @see BungeeCordBootstrapper#getInstance()
	 * @see VelocityBootstrapper#getInstance()
	 */
	public static ChatPlugin getInstance() {
		return instance;
	}
	
	/**
	 * Gets the plugin's state.
	 * 
	 * @return Plugin's state
	 */
	public static ChatPluginState getState() {
		return state.get();
	}
	
	/**
	 * Reloads ChatPlugin. Totally.
	 * 
	 * <p>If the reload fails the plugin will be safely disabled, enter
	 * recovery mode if possible and send an error message to the console.</p>
	 * 
	 * @return Time elapsed in milliseconds or 0 if not loaded or -1 if failed
	 * @see ChatPluginReloadEvent
	 * @see ChatPluginCrashEvent
	 */
	public abstract int reload();
	
	/**
	 * Unloads ChatPlugin.
	 * 
	 * <p>If the unload fails the plugin will be safely disabled, enter
	 * recovery mode if possible and send an error message to the console.</p>
	 * 
	 * @deprecated Internal use only. You should never need to manually unload ChatPlugin.
	 * @return Time elapsed in milliseconds or 0 if not loaded or -1 if failed
	 * @see ChatPluginUnloadEvent
	 */
	@Deprecated
	public abstract int unload();
	
	/**
	 * Runs a command from the console synchronously.
	 * 
	 * <p>It will run on the next server tick if <code>forceSync</code>.
	 * Otherwise, make sure the call comes from the server's main thread, unless
	 * {@link Environment#isProxy()}, where everything is asynchronous.</p>
	 * 
	 * @param command Command to run
	 * @param forceSync Whether to force synchronization
	 */
	public abstract void runConsoleCommand(String command, boolean forceSync);
	
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
	 * Checks if the plugin is running on online mode.
	 * 
	 * @return Whether online mode is enabled
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()}
	 * &amp;&amp; {@link #getState()} != {@link ChatPluginState#LOADED LOADED}</code>
	 */
	public abstract boolean isOnlineMode();
	
	/**
	 * Checks if a premium copy of the plugin is running.
	 * 
	 * @return Whether the plugin is premium
	 */
	public abstract boolean isPremium();
	
}
