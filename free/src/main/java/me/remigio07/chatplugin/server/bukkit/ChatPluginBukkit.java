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

package me.remigio07.chatplugin.server.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.HandlerList;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginCrashEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginLoadEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginReloadEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginUnloadEvent;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.common.util.manager.JavaLogManager;
import me.remigio07.chatplugin.server.bukkit.manager.BukkitEventManager;
import me.remigio07.chatplugin.server.storage.configuration.ServerConfigurationManager;
import me.remigio07.chatplugin.server.util.bstats.ServerMetrics;
import me.remigio07.chatplugin.server.util.manager.ChatPluginServerManagers;

public class ChatPluginBukkit extends ChatPlugin {
	
	private ServerMetrics metrics;
	
	public ChatPluginBukkit() {
		instance = this;
	}
	
	@SuppressWarnings("deprecation")
	public int load(Logger logger, File dataFolder) {
		long ms = System.currentTimeMillis();
		this.logger = logger;
		this.dataFolder = dataFolder;
		
		try {
			VersionUtils.initVersionUtils();
			printStartMessage();
			BukkitReflection.initReflection();
			(managers = new ChatPluginServerManagers()).addManager(LogManager.class, new JavaLogManager());
			managers.addManager(ConfigurationManager.class, new ServerConfigurationManager());
			Utils.initUtils();
			
			if (VersionUtils.getVersion().isPreNettyRewrite())
				LogManager.log("This server is running a pre-Netty rewrite Minecraft version. Note that this software is {0} old. Even though it is still supported, fixing any bugs is not a priority and a lot of features are not available.", 1, Utils.formatTime(System.currentTimeMillis() - VersionUtils.getVersion().getReleaseDate()));
			else if (VersionUtils.getVersion().isOlderThan(Version.V1_9))
				LogManager.log("This server is running an old Minecraft version. Note that this software is {0} old. Even though it is still supported, fixing any bugs is not a priority. It's recommended to upgrade to a newer version.", 1, Utils.formatTime(System.currentTimeMillis() - VersionUtils.getVersion().getReleaseDate()));
			managers.loadManagers();
			BukkitCommandsHandler.registerCommands();
			Utils.startUpdateChecker();
			TaskManager.scheduleAsync(() -> LogManager.log(Utils.FREE_VERSION_ADS[ThreadLocalRandom.current().nextInt(Utils.FREE_VERSION_ADS.length)], 0), 3600000L, 3600000L); // yeah, I've put it right here... remove it if you want, I guess 🙄
			TaskManager.runAsync(() -> {
				long ms2 = System.currentTimeMillis();
				
				if ((metrics = new BukkitMetrics(BukkitBootstrapper.getInstance(), 12757)).load().areMetricsEnabled());
					LogManager.log("[ASYNC] Metrics loaded in {0} ms.", 4, System.currentTimeMillis() - ms2);
			}, 0L);
		} catch (ChatPluginManagerException e) {
			String message = e.getMessage() + ". Contact support if you are unable to solve the issue.";
			
			if (LogManager.getInstance() == null)
				System.err.println(message);
			else LogManager.log(message, 2);
			return -1;
		} LogManager.log("Ready. Plugin loaded successfully in {0} ms.", 0, startupTime = (int) (System.currentTimeMillis() - ms));
		new ChatPluginLoadEvent(startupTime).call();
		
		started = loaded = true;
		return startupTime;
	}
	
	@Override
	public synchronized int reload() {
		if (reloading || !loaded)
			return 0;
		reloading = true;
		long ms = System.currentTimeMillis();
		
		try {
			LogManager.log("Reloading ChatPlugin...", 0);
			managers.reloadManagers();
			BukkitCommandsHandler.registerCommands();
			LogManager.log("Plugin reloaded successfully in {0} ms.", 0, lastReloadTime = (int) (System.currentTimeMillis() - ms));
			new ChatPluginReloadEvent(lastReloadTime).call();
			return lastReloadTime;
		} catch (ChatPluginManagerException e) {
			LogManager.log(e.getMessage() + "; unloading...", 2);
			new ChatPluginCrashEvent(e.getMessage()).call();
			
			if (unload() != -1)
				performRecovery();
			return -1;
		} finally {
			reloading = false;
		}
	}
	
	@Deprecated
	@Override
	public synchronized int unload() {
		if (!started || !loaded)
			return 0;
		try {
			long ms = System.currentTimeMillis();
			boolean reload = !(boolean) BukkitReflection.getFieldValue("MinecraftServer", BukkitReflection.invokeMethod("MinecraftServer", "getServer", null), "hasStopped");
			loaded = false;
			
			if (reload)
				try {
					File file = new File(getDataFolder(), "online-players-data.yml");
					
					if (file.exists())
						file.delete();
					Configuration onlinePlayersData = new Configuration(file);
					
					file.deleteOnExit();
					onlinePlayersData.load();
					
					for (UUID player : ServerPlayerManager.getPlayersVersions().keySet()) {
						String path = player.toString() + ".";
						
						onlinePlayersData.set(path + "version", ServerPlayerManager.getPlayerVersion(player).getName());
						onlinePlayersData.set(path + "login-time", ServerPlayerManager.getPlayerLoginTime(player).longValue());
						onlinePlayersData.set(path + "bedrock", ServerPlayerManager.isBedrockPlayer(player));
					} onlinePlayersData.save();
				} catch (IOException e) {
					LogManager.log("IOException occurred while saving online players' data during server reload: {0}; players will be kicked.", 2, e.getMessage());
				}
			LogManager.log("Unloading ChatPlugin{0}...", 0, reload ? " (server reload)" : "");
			new ChatPluginUnloadEvent().call();
			// Bukkit's crash-proof stuff
			BukkitCommandsHandler.unregisterCommands(true);
			HandlerList.unregisterAll(((BukkitEventManager) BukkitEventManager.getInstance()).getListener());
			// ChatPlugin's stuff which might crash
			managers.unloadManagers();
			LogManager.log("Plugin unloaded successfully in {0} ms.", 3, ms = System.currentTimeMillis() - ms);
			return (int) ms;
		} catch (NoClassDefFoundError e) {
			System.err.println("You cannot replace the plugin JAR while the server is running. Reloads are supported but not in this case; shutting down...");
			Bukkit.shutdown();
		} catch (ChatPluginManagerException e) {
			LogManager.log(e.getMessage() + "; performing recovery...", 2);
			performRecovery();
		} return -1;
	}
	
	public void performRecovery() {
		CommandExecutor executor = new CommandExecutor() {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
				sender.sendMessage("\u00A7cChatPlugin is disabled because an error occurred.");
				return true;
			}
			
		};
		
		for (String command : BukkitCommandsHandler.getCommands().keySet()) {
			PluginCommand bukkitCommand = BukkitCommandsHandler.registerCommand(command);
			
			if (command.equals("chatplugin")) {
				bukkitCommand.setExecutor(new CommandExecutor() {
					
					@Override
					public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
						if (args.length == 1 && args[0].equalsIgnoreCase("recover")) {
							if (sender.hasPermission("chatplugin.commands.recover")) {
								sender.sendMessage("\u00A7eTrying to recover ChatPlugin... Don't get your hopes up.");
								Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("\u00A7ePerforming ChatPlugin recovery..."));
								
								int startupTime = load((Logger) logger, dataFolder);
								
								if (startupTime == -1)
									sendConsoleMessage("\u00A7cFailed to load. Check above for the error message.", false);
								else sendConsoleMessage("\u00A7aChatPlugin has been loaded successfully in \u00A7f" + startupTime + " ms\u00A7a. You should anyway restart as soon as possible.", false);
							} else sender.sendMessage("\u00A7cYou do not have the permission to execute this command.");
						} else sender.sendMessage("\u00A7cThe syntax is wrong. Usage: \u00A7f/chatplugin recover\u00A7c.");
						return true;
					}
					
				});
				bukkitCommand.setTabCompleter(new TabCompleter() {
					
					@Override
					public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
						if (args.length == 1 && "recover".startsWith(args[0].toLowerCase()))
							return Arrays.asList("recover");
						return Collections.emptyList();
					}
					
				});
			} else {
				bukkitCommand.setExecutor(executor);
				bukkitCommand.setTabCompleter(null);
			}
		} BukkitCommandsHandler.syncCommands();
		
		try {
			TaskManager.getInstance().unload();
			StorageConnector.getInstance().unload();
			LogManager.log("Recovery performed successfully. You can try to load ChatPlugin using /chatplugin recover, but don't get your hopes up: it may be necessary to restart the server.", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void runConsoleCommand(String command) {
		TaskManager.runSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command), 0L);
	}
	
	@Override
	public void sendConsoleMessage(String message, boolean log) {
		Bukkit.getConsoleSender().sendMessage(message);
		
		if (log && LogManager.getInstance() != null)
			LogManager.getInstance().writeToFile(message);
	}
	
	@Override
	public void printStartMessage() {
		CommandSender console = Bukkit.getConsoleSender();
		
		console.sendMessage( "   \u00A7c__  \u00A7f__   ");
		console.sendMessage( "  \u00A7c/   \u00A7f|__)  \u00A7aRunning \u00A7cChat\u00A7fPlugin \u00A72Free \u00A7aversion \u00A7f" + VERSION + " \u00A7aon \u00A7f" + VersionUtils.getImplementationName());
		console.sendMessage("  \u00A7c\\__ \u00A7f|     \u00A78Detected server version: " + VersionUtils.getVersion().getName() + " (protocol: " + VersionUtils.getVersion().getProtocol() + ")");
		console.sendMessage("");
	}
	
	@Override
	public boolean isOnlineMode() {
		if (ProxyManager.getInstance() == null || ConfigurationType.CONFIG.get() == null)
			throw new IllegalStateException("Unable to call ChatPlugin#isOnlineMode() as the plugin has not finished loading yet");
		return ProxyManager.getInstance().isEnabled() ? ConfigurationType.CONFIG.get().getBoolean("multi-instance-mode.proxy-online-mode") : Bukkit.getServer().getOnlineMode();
	}
	
	@Override
	public boolean isPremium() {
		return false; // just for you to know, changing this will not unlock the premium features :)
	}
	
	public ServerMetrics getMetrics() {
		return metrics;
	}
	
}
