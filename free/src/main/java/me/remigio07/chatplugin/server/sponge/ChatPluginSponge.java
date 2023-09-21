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

package me.remigio07.chatplugin.server.sponge;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginCrashEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginLoadEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginReloadEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginUnloadEvent;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;
import me.remigio07.chatplugin.common.util.manager.SLF4JLogManager;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.storage.configuration.ServerConfigurationManager;
import me.remigio07.chatplugin.server.util.bstats.ServerMetrics;
import me.remigio07.chatplugin.server.util.manager.ChatPluginServerManagers;

public class ChatPluginSponge extends ChatPlugin {

	private ServerMetrics metrics;
	
	public ChatPluginSponge() {
		instance = this;
	}
	
	@SuppressWarnings("deprecation")
	public int load(Logger logger, Path dataFolder) {
		long ms = System.currentTimeMillis();
		this.logger = logger;
		this.dataFolder = dataFolder.toFile();
		
		try {
			VersionUtils.initVersionUtils();
			printStartMessage();
			SpongeReflection.initReflection();
			(managers = new ChatPluginServerManagers()).addManager(LogManager.class, new SLF4JLogManager());
			managers.addManager(ConfigurationManager.class, new ServerConfigurationManager());
			me.remigio07.chatplugin.common.util.Utils.initUtils();
			
			if (VersionUtils.getVersion().isOlderThan(Version.V1_9))
				LogManager.log("This server is running an old Minecraft version. Note that this software is {0} old. Even though it is still supported, fixing any bugs is not a priority. It's recommended to upgrade to a newer version.", 1, me.remigio07.chatplugin.common.util.Utils.formatTime(System.currentTimeMillis() - VersionUtils.getVersion().getReleaseDate()));
			managers.loadManagers();
			SpongeCommandsHandler.registerCommands();
			TaskManager.scheduleAsync(() -> LogManager.log(me.remigio07.chatplugin.common.util.Utils.FREE_VERSION_ADS[ThreadLocalRandom.current().nextInt(me.remigio07.chatplugin.common.util.Utils.FREE_VERSION_ADS.length)], 0), 3600000L, 3600000L);
			TaskManager.runAsync(() -> {
				long ms2 = System.currentTimeMillis();
				
				if ((metrics = new SpongeMetrics(Sponge.getPluginManager().getPlugin("chatplugin").get(), 12758)).load().areMetricsEnabled())
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
			CommandManager manager = Sponge.getCommandManager();
			loaded = false;
			
			LogManager.log("Unloading ChatPlugin...", 0);
			new ChatPluginUnloadEvent().call();
			// Sponge's crash-proof stuff
			manager.getOwnedBy(SpongeBootstrapper.getInstance()).forEach(mapping -> manager.removeMapping(mapping));
			Sponge.getEventManager().unregisterPluginListeners(SpongeBootstrapper.getInstance());
			// ChatPlugin's stuff which might crash
			ChatPluginSpongePlayer.closeAudiences();
			managers.unloadManagers();
			LogManager.log("Plugin unloaded successfully in {0} ms.", 3, ms = System.currentTimeMillis() - ms); // XXX might not work (called after .unloadManagers())
			return (int) ms;
		} catch (NoClassDefFoundError e) {
			System.err.println("You cannot replace the plugin JAR while the server is running. Reloads are supported but not in this case; shutting down...");
			Sponge.getServer().shutdown();
		} catch (ChatPluginManagerException e) {
			LogManager.log(e.getMessage() + "; performing recovery...", 2);
			performRecovery();
		} return -1;
	}
	
	public void performRecovery() {
		CommandCallable callable = new CommandCallable() {
			
			@Override
			public CommandResult process(CommandSource sender, String text) throws CommandException {
				sender.sendMessage(Utils.serializeSpongeText("\u00A7cChatPlugin is disabled because an error occurred."));
				return CommandResult.success();
			}
			
			@Override
			public List<String> getSuggestions(CommandSource sender, String text, Location<World> location) throws CommandException {
				return Collections.emptyList();
			}
			
			@Override
			public Text getUsage(CommandSource sender) {
				return Utils.serializeSpongeText("/chatplugin recover");
			}
			
			@Override
			public boolean testPermission(CommandSource sender) {
				return true;
			}
			
			@Override
			public Optional<Text> getHelp(CommandSource sender) {
				return Optional.empty(); // fuck it
			}
			
			@Override
			public Optional<Text> getShortDescription(CommandSource sender) {
				return Optional.empty(); // fuck it
			}
			
		};
		
		for (String command : SpongeCommandsHandler.getCommands().keySet()) {
			BaseCommand[] subcommands = SpongeCommandsHandler.getCommands().get(command);
			BaseCommand mainCommand = subcommands[subcommands.length - 1];
			
			if (command.equals("chatplugin"))
				Sponge.getCommandManager().register(SpongeBootstrapper.getInstance(), new CommandCallable() {
					
					@Override
					public CommandResult process(CommandSource sender, String text) throws CommandException {
						String[] args = text.isEmpty() ? new String[0] : text.split(" ");
						
						if (args.length == 1 && args[0].equalsIgnoreCase("recover")) {
							if (sender.hasPermission("chatplugin.commands.recover")) {
								sender.sendMessage(Utils.serializeSpongeText("\u00A7eTrying to recover ChatPlugin... Don't get your hopes up."));
								
								int startupTime = load((Logger) logger, dataFolder.toPath());
								
								if (startupTime == -1)
									sender.sendMessage(Utils.serializeSpongeText("\u00A7cFailed to load. Check the console for the error message."));
								else sender.sendMessage(Utils.serializeSpongeText("\u00A7aChatPlugin has been loaded successfully in \u00A7f" + startupTime + " ms\u00A7a. You should anyway restart as soon as possible."));
							} else sender.sendMessage(Utils.serializeSpongeText("\u00A7cYou do not have the permission to execute this command."));
						} else sender.sendMessage(Utils.serializeSpongeText("\u00A7cThe syntax is wrong. Usage: \u00A7f/chatplugin recover\u00A7c."));
						return CommandResult.success();
					}
					
					@Override
					public List<String> getSuggestions(CommandSource sender, String text, Location<World> location) throws CommandException {
						String[] args = text.split(" ");
						
						if (args.length == 1 && "recover".startsWith(args[0].toLowerCase()))
							return Arrays.asList("recover");
						return Collections.emptyList();
					}
					
					@Override
					public Text getUsage(CommandSource source) {
						return Utils.serializeSpongeText("/chatplugin recover");
					}
					
					@Override
					public boolean testPermission(CommandSource source) {
						return true;
					}
					
					@Override
					public Optional<Text> getHelp(CommandSource sender) {
						return Optional.empty(); // fuck it
					}
					
					@Override
					public Optional<Text> getShortDescription(CommandSource sender) {
						return Optional.empty(); // fuck it
					}
					
				}, mainCommand.getMainArgs());
			else Sponge.getCommandManager().register(command, callable, mainCommand.getMainArgs());
		} try {
			TaskManager.getInstance().unload();
			StorageConnector.getInstance().unload();
			LogManager.log("Recovery performed successfully. You can try to load ChatPlugin using /chatplugin recover, but don't get your hopes up: it may be necessary to restart the server.", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void runConsoleCommand(String command) {
		TaskManager.runSync(() -> Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command), 0L);
	}
	
	@Override
	public void sendConsoleMessage(String message, boolean log) {
		Sponge.getServer().getConsole().sendMessage(Utils.serializeSpongeText(message));
		
		if (log && LogManager.getInstance() != null)
			LogManager.getInstance().writeToFile(message);
	}
	
	@Override
	public void printStartMessage() {
		CommandSource console = Sponge.getServer().getConsole();
		
		console.sendMessage(Utils.serializeSpongeText( "   \u00A7c__  \u00A7f__   "));
		console.sendMessage(Utils.serializeSpongeText( "  \u00A7c/   \u00A7f|__)  \u00A7aRunning \u00A7cChat\u00A7fPlugin \u00A72Free \u00A7aversion \u00A7f" + VERSION + " \u00A7aon Sponge"));
		console.sendMessage(Utils.serializeSpongeText("  \u00A7c\\__ \u00A7f|     \u00A78Detected server version: " + VersionUtils.getVersion().getName() + " (protocol: " + VersionUtils.getVersion().getProtocol() + ")"));
		console.sendMessage(Utils.serializeSpongeText(""));
	}
	
	@Override
	public boolean isOnlineMode() {
		if (ProxyManager.getInstance() == null || ConfigurationType.CONFIG.get() == null)
			throw new IllegalStateException("Unable to call ChatPlugin#isOnlineMode() as the plugin has not finished loading yet");
		return ProxyManager.getInstance().isEnabled() ? ConfigurationType.CONFIG.get().getBoolean("multi-instance-mode.proxy-online-mode") : Sponge.getServer().getOnlineMode();
	}
	
	@Override
	public boolean isPremium() {
		return false; // just for you to know, changing this will not unlock the premium features :)
	}
	
	public ServerMetrics getMetrics() {
		return metrics;
	}
	
}
