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

package me.remigio07.chatplugin.server.bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.google.gson.JsonElement;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginCrashEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginLoadEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginReloadEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginUnloadEvent;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.common.util.manager.JavaLogManager;
import me.remigio07.chatplugin.server.storage.configuration.ServerConfigurationManager;
import me.remigio07.chatplugin.server.util.ServerMetrics;
import me.remigio07.chatplugin.server.util.manager.ChatPluginServerManagers;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

public class ChatPluginBukkit extends ChatPlugin {
	
	private Metrics metrics;
	
	public ChatPluginBukkit() {
		instance = this;
	}
	
	@SuppressWarnings("deprecation")
	public int load(Logger logger, Path dataFolder) {
		state.set(ChatPluginState.STARTING);
		
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
			TaskManager.runAsync(() -> {
				long ms2 = System.currentTimeMillis();
				
				ServerMetrics.load(metrics = new Metrics(BukkitBootstrapper.getInstance(), 12757));
				LogManager.log("[ASYNC] bStats' metrics loaded in {0} ms.", 4, System.currentTimeMillis() - ms2);
			}, 0L);
		} catch (ChatPluginManagerException cpme) {
			if (LogManager.getInstance() == null)
				System.err.println(cpme.getLocalizedMessage() + ". Do not reload the server; contact support if you are unable to solve the issue.");
			else LogManager.log("{0}. Do not reload the server; contact support if you are unable to solve the issue.", 2, cpme.getLocalizedMessage());
			return -1;
		} LogManager.log("Ready. Plugin loaded successfully in {0} ms.", 0, startupTime = (int) (System.currentTimeMillis() - ms));
		state.set(ChatPluginState.LOADED);
		new ChatPluginLoadEvent(startupTime).call();
		return startupTime;
	}
	
	@Override
	public synchronized int reload() {
		if (state.get() != ChatPluginState.LOADED)
			return 0;
		state.set(ChatPluginState.RELOADING);
		
		long ms = System.currentTimeMillis();
		
		try {
			LogManager.log("Reloading ChatPlugin...", 0);
			managers.reloadManagers();
			BukkitCommandsHandler.unregisterCommands(false);
			BukkitCommandsHandler.registerCommands();
			LogManager.log("Plugin reloaded successfully in {0} ms.", 0, lastReloadTime = (int) (System.currentTimeMillis() - ms));
			state.set(ChatPluginState.LOADED);
			new ChatPluginReloadEvent(lastReloadTime).call();
			return lastReloadTime;
		} catch (ChatPluginManagerException cpme) {
			LogManager.log("{0}; unloading...", 2, cpme.getLocalizedMessage());
			new ChatPluginCrashEvent(cpme.getLocalizedMessage()).call();
			
			if (unload() != -1)
				performRecovery();
			return -1;
		}
	}
	
	@Deprecated
	@Override
	public synchronized int unload() {
		if (state.get() != ChatPluginState.LOADED && state.get() != ChatPluginState.RELOADING)
			return 0;
		state.set(ChatPluginState.UNLOADING);
		
		try {
			long ms = System.currentTimeMillis();
			boolean reload = !(boolean) BukkitReflection.getFieldValue("MinecraftServer", BukkitReflection.invokeMethod("MinecraftServer", "getServer", null), "hasStopped");
			
			if (reload)
				try {
					Path path = dataFolder.resolve("files" + File.separator + "online-players-data.yml");
					Configuration onlinePlayersData = new Configuration(path);
					
					Files.deleteIfExists(path);
					path.toFile().deleteOnExit();
					onlinePlayersData.load();
					
					for (UUID player : ServerPlayerManager.getPlayersVersions().keySet()) {
						String path2 = player.toString() + ".";
						
						onlinePlayersData.set(path2 + "version", ServerPlayerManager.getPlayerVersion(player).getName());
						onlinePlayersData.set(path2 + "login-time", ServerPlayerManager.getPlayerLoginTime(player).longValue());
						onlinePlayersData.set(path2 + "bedrock", ServerPlayerManager.isBedrockPlayer(player));
					} onlinePlayersData.save();
				} catch (IOException ioe) {
					LogManager.log("IOException occurred while saving online players' data during server reload: {0}; players will be kicked.", 2, ioe.getLocalizedMessage());
				}
			LogManager.log("Unloading ChatPlugin{0}...", 0, reload ? " (server reload)" : "");
			new ChatPluginUnloadEvent().call();
			// Bukkit's crash-proof stuff
			BukkitCommandsHandler.unregisterCommands(true);
			HandlerList.unregisterAll(BukkitBootstrapper.getInstance());
			// ChatPlugin's stuff which might crash
			managers.unloadManagers();
			LogManager.log("Plugin unloaded successfully in {0} ms.", 3, ms = System.currentTimeMillis() - ms);
			state.set(ChatPluginState.UNLOADED);
			return (int) ms;
		} catch (NoClassDefFoundError ncdfe) {
			System.err.println("You cannot replace the plugin JAR while the server is running. Reloads are supported but not in this case; shutting down...");
			Bukkit.shutdown();
		} catch (ChatPluginManagerException cpme) {
			LogManager.log("{0}; performing recovery...", 2, cpme.getLocalizedMessage());
			performRecovery();
		} return -1;
	}
	
	public void performRecovery() {
		state.set(ChatPluginState.RECOVERY);
		
		CommandExecutor executor = new CommandExecutor() {
			
			@Override
			public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
				sender.sendMessage("§cChatPlugin is disabled because an error occurred.");
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
								sender.sendMessage("§eTrying to recover ChatPlugin... Don't get your hopes up.");
								Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§ePerforming ChatPlugin recovery..."));
								
								int startupTime = load((Logger) logger, dataFolder);
								
								if (startupTime == -1)
									sendConsoleMessage("§cFailed to load. Check above for the error message.", false);
								else sendConsoleMessage("§aChatPlugin has been loaded successfully in §f" + startupTime + " ms§a. You should anyway restart as soon as possible.", false);
							} else sender.sendMessage("§cYou do not have the permission to execute this command.");
						} else sender.sendMessage("§cThe syntax is wrong. Usage: §f/chatplugin recover§c.");
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
	public void runConsoleCommand(String command, boolean forceSync) {
		if (forceSync)
			TaskManager.runSync(() -> runConsoleCommand(command, false), 0L);
		else Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
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
		
		console.sendMessage( "   §c__  §f__   ");
		console.sendMessage( "  §c/   §f|__)  §aRunning §cChat§fPlugin §2Free §aversion §f" + VERSION + " §aon §f" + VersionUtils.getImplementationName());
		console.sendMessage("  §c\\__ §f|     §8Detected server version: " + VersionUtils.getVersion().getName() + " (protocol: " + VersionUtils.getVersion().getProtocol() + ")");
		console.sendMessage(" ");
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
	
	public Metrics getMetrics() {
		return metrics;
	}
	
	public static String toLegacyText(Object bukkitComponent) {
		return Utils.toLegacyText(toBungeeCordComponent(bukkitComponent));
	}
	
	public static JsonElement toJSON(Object bukkitComponent) {
		return BukkitReflection.toJSON(bukkitComponent);
	}
	
	public static Object toBukkitComponent(String legacyText) {
		return toBukkitComponent(Utils.toBungeeCordComponent(legacyText));
	}
	
	public static Object toBukkitComponent(JsonElement json) {
		return BukkitReflection.toIChatBaseComponent(json.toString());
	}
	
	public static Object toBukkitComponent(BaseComponent bungeeCordComponent) {
		return toBukkitComponent(Utils.toJSON(bungeeCordComponent));
	}
	
	public static BaseComponent toBungeeCordComponent(Object bukkitComponent) {
		return Utils.toBungeeCordComponent(toJSON(bukkitComponent));
	}
	
	@SuppressWarnings("deprecation")
	public static void sendMessage(Player player, boolean actionbar, BaseComponent... bungeeCordComponents) { // TODO is it possible to send colored actionbars in 1.8-1.10.2?
		if (VersionUtils.isSpigot() && VersionUtils.getVersion().isAtLeast(Version.V1_9_1))
			player.spigot().sendMessage(actionbar ? ChatMessageType.ACTION_BAR : ChatMessageType.SYSTEM, bungeeCordComponents);
		else {
			ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUniqueId());
			List<Object> packets = new ArrayList<>();
			
			for (BaseComponent component : bungeeCordComponents) {
				packets.add(actionbar
						? VersionUtils.getVersion().isAtLeast(Version.V1_17)
								? BukkitReflection.getInstance("ClientboundSetActionBarTextPacket", new Class[] { BukkitReflection.getLoadedClass("IChatBaseComponent") }, toBukkitComponent(component))
								: VersionUtils.getVersion().isAtLeast(Version.V1_11)
								? BukkitReflection.getInstance("PacketPlayOutTitle", new Class[] { BukkitReflection.getLoadedClass("EnumTitleAction"), BukkitReflection.getLoadedClass("IChatBaseComponent") }, BukkitReflection.getEnum("EnumTitleAction", 2), toBukkitComponent(component))
								: BukkitReflection.getInstance("PacketPlayOutChat", new Class[] { BukkitReflection.getLoadedClass("IChatBaseComponent"), byte.class }, toBukkitComponent(component), (byte) 2)
						: VersionUtils.getVersion().isAtLeast(Version.V1_19)
						? BukkitReflection.getInstance("ClientboundSystemChatPacket", new Class[] { BukkitReflection.getLoadedClass("IChatBaseComponent"), VersionUtils.getVersion() == Version.V1_19 ? int.class : boolean.class }, toBukkitComponent(component), VersionUtils.getVersion() == Version.V1_19 ? actionbar ? 2 : 1 : actionbar)
						: BukkitReflection.getInstance("PacketPlayOutChat", VersionUtils.getVersion().isAtLeast(Version.V1_16)
						? new Class[] { BukkitReflection.getLoadedClass("IChatBaseComponent"), BukkitReflection.getLoadedClass("ChatMessageType"), UUID.class }
						: VersionUtils.getVersion().isAtLeast(Version.V1_12)
						? new Class[] { BukkitReflection.getLoadedClass("IChatBaseComponent"), BukkitReflection.getLoadedClass("ChatMessageType") }
						: new Class[] { BukkitReflection.getLoadedClass("IChatBaseComponent"), byte.class },
						VersionUtils.getVersion().isAtLeast(Version.V1_16)
						? new Object[] { toBukkitComponent(component), BukkitReflection.getEnum("ChatMessageType", "SYSTEM"), null }
						: VersionUtils.getVersion().isAtLeast(Version.V1_12)
						? new Object[] { toBukkitComponent(component), BukkitReflection.getEnum("ChatMessageType", "SYSTEM") }
						: new Object[] { toBukkitComponent(component), (byte) 1 })
						);
			}if (serverPlayer == null)
				packets.forEach(packet -> BukkitReflection.invokeMethod("PlayerConnection", "sendPacket", BukkitReflection.getFieldValue("EntityPlayer", BukkitReflection.invokeMethod("CraftPlayer", "getHandle", BukkitReflection.getLoadedClass("CraftPlayer").cast(player)), "playerConnection", "connection", VersionUtils.getVersion().isAtLeast(Version.V1_20) ? VersionUtils.getVersion().isAtLeast(Version.V1_21_3) ? "f" : "c" : "b"), packet));
			else packets.forEach(packet -> serverPlayer.sendPacket(packet));
		}
	}
	
}
