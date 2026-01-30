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

package me.remigio07.chatplugin.server.fabric;

import static net.minecraft.server.command.CommandManager.literal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginCrashEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginLoadEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginReloadEvent;
import me.remigio07.chatplugin.api.common.event.plugin.ChatPluginUnloadEvent;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.common.util.LibrariesUtils;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.common.util.manager.JavaLogManager;
import me.remigio07.chatplugin.mixin.extension.ServerCommandSourceExtension;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.storage.configuration.ServerConfigurationManager;
import me.remigio07.chatplugin.server.util.manager.ChatPluginServerManagers;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class ChatPluginFabric extends ChatPlugin {
	
	protected static MinecraftServer server;
	private static Gson GSON;
	
	public ChatPluginFabric() {
		instance = this;
	}
	
	@SuppressWarnings("deprecation")
	public int load(Logger logger, Path dataFolder, MinecraftServer server) {
		state.set(ChatPluginState.STARTING);
		
		long ms = System.currentTimeMillis();
		this.logger = logger;
		this.dataFolder = dataFolder;
		ChatPluginFabric.server = server;
		
		try {
			VersionUtils.initVersionUtils();
			printStartMessage();
			(managers = new ChatPluginServerManagers()).addManager(LogManager.class, new JavaLogManager());
			LibrariesUtils.loadYAMLForFabric();
			managers.addManager(ConfigurationManager.class, new ServerConfigurationManager());
			Utils.initUtils();
			
			if (VersionUtils.getVersion() == Version.UNSUPPORTED)
				LogManager.log("This server is running an unsupported Minecraft version. Is ChatPlugin up to date? Compatible versions: 1.14-{0}. Note: snapshots, pre-releases and release candidates are not supported. Proceed at your own risk.", 1, Version.values()[Version.values().length - 2].getName());
			loadTextStuff();
			managers.loadManagers();
			FabricCommandsHandler.registerCommands();
			Utils.startUpdateChecker();
//			TaskManager.runAsync(() -> {
//				long ms2 = System.currentTimeMillis();
//				
//				ServerMetrics.load(metrics = new Metrics(BukkitBootstrapper.getInstance(), 12757));
//				LogManager.log("[ASYNC] bStats' metrics loaded in {0} ms.", 4, System.currentTimeMillis() - ms2);
//			}, 0L);
		} catch (ChatPluginManagerException cpme) {
			if (LogManager.getInstance() == null)
				System.err.println(cpme.getLocalizedMessage() + ". Contact support if you are unable to solve the issue.");
			else LogManager.log("{0}. Contact support if you are unable to solve the issue.", 2, cpme.getLocalizedMessage());
			return -1;
		} LogManager.log("Ready. Plugin loaded successfully in {0} ms.", 0, startupTime = (int) (System.currentTimeMillis() - ms));
		state.set(ChatPluginState.LOADED);
		new ChatPluginLoadEvent(startupTime).call();
		return startupTime;
	}
	
	protected static void loadTextStuff() {
		try {
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20_3)) {
				Field field = Class.forName("net.minecraft.class_2561$class_2562").getDeclaredField("field_11754");
				
				field.setAccessible(true);
				
				GSON = (Gson) field.get(null);
			}
		} catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
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
			FabricCommandsHandler.unregisterCommands(false);
			FabricCommandsHandler.registerCommands();
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
			
			LogManager.log("Unloading ChatPlugin...", 0);
			new ChatPluginUnloadEvent().call();
			// Fabric's crash-proof stuff
			FabricCommandsHandler.unregisterCommands(true);
			// ChatPlugin's stuff which might crash
			managers.unloadManagers();
			LogManager.log("Plugin unloaded successfully in {0} ms.", 3, ms = System.currentTimeMillis() - ms);
			state.set(ChatPluginState.UNLOADED);
			return (int) ms;
		} catch (NoClassDefFoundError ncdfe) {
			System.err.println("You cannot replace the plugin JAR while the server is running; shutting down...");
			server.shutdown();
		} catch (ChatPluginManagerException cpme) {
			LogManager.log("{0}; performing recovery...", 2, cpme.getLocalizedMessage());
			performRecovery();
		} return -1;
	}
	
	public void performRecovery() {
		state.set(ChatPluginState.RECOVERY);
		
		RootCommandNode<ServerCommandSource> root = server.getCommandManager().getDispatcher().getRoot();
		Command<ServerCommandSource> executor = new Command<ServerCommandSource>() {
			
			@Override
			public int run(CommandContext<ServerCommandSource> context) {
				sendMessage(context.getSource(), toFabricComponent("§cChatPlugin is disabled because an error occurred."));
				return 1;
			}
			
		};
		
		for (String command : FabricCommandsHandler.getCommands().keySet()) {
			BaseCommand[] subCommands = FabricCommandsHandler.getCommands().get(command);
			BaseCommand mainCommand = subCommands[subCommands.length - 1];
			List<String> aliases = mainCommand.getMainArgs().stream().filter(alias -> !FabricCommandsHandler.getDisabledCommands().contains(alias)).collect(Collectors.toList());
			
			if (aliases.isEmpty())
				continue;
			LiteralCommandNode<ServerCommandSource> brigadierCommand;
			
			if (command.equals("chatplugin")) {
				brigadierCommand = literal(aliases.get(0)).executes(new Command<ServerCommandSource>() {
					
					@Override
					public int run(CommandContext<ServerCommandSource> context) {
						sendMessage(context.getSource(), toFabricComponent("§cThe syntax is wrong. Usage: §f/chatplugin recover§c."));
						return 1;
					}
					
				}).then(literal("recover").executes(new Command<ServerCommandSource>() {
					
					@Override
					public int run(CommandContext<ServerCommandSource> context) {
						CommandSenderAdapter sender = new CommandSenderAdapter(context.getSource());
						
						if (sender.hasPermission("chatplugin.commands.recover")) {
							sender.sendMessage("§eTrying to recover ChatPlugin... Don't get your hopes up.");
							server.getPlayerManager().getPlayerList().forEach(player -> player.networkHandler.disconnect(toFabricComponent("§ePerforming ChatPlugin recovery...")));
							
							int startupTime = load((Logger) logger, dataFolder, server);
							
							sendConsoleMessage(startupTime == -1
									? "§cFailed to load. Check above for the error message."
									: "§aChatPlugin has been loaded successfully in §f" + startupTime + " ms§a. Regardless, you should restart as soon as possible.",
									false);
						} else sender.sendMessage("§cYou do not have the permission to execute this command.");
						return 1;
					}
					
				})).build();
			} else brigadierCommand = literal(aliases.get(0)).executes(executor).build();
			
			root.addChild(brigadierCommand);
			
			if (aliases.size() != 1)
				aliases.stream().skip(1).forEach(alias -> root.addChild(FabricCommandsHandler.buildRedirect(alias, brigadierCommand)));
		} FabricCommandsHandler.syncCommands();
		
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
		else CommandExecutor.runCommand(server.getCommandSource(), command);
	}
	
	@Override
	public void sendConsoleMessage(String message, boolean log) {
		sendMessage(server.getCommandSource(), toFabricComponent(message));
		
		if (log && LogManager.getInstance() != null)
			LogManager.getInstance().writeToFile(message);
	}
	
	@Override
	public void printStartMessage() {
		ServerCommandSourceExtension console = (ServerCommandSourceExtension) server.getCommandSource();
		
		try {
			console.chatPlugin$sendMessage(getText(new String[] {  "   __", "  __" }, Formatting.RED, Formatting.WHITE));
			console.chatPlugin$sendMessage(getText(new String[] {  "  /", "   |__)  ", "Running ", "Chat", "Plugin ", "Free ", "version ", VERSION, " on ", "Fabric" }, Formatting.RED, Formatting.WHITE, Formatting.GREEN, Formatting.RED, Formatting.WHITE, Formatting.DARK_GREEN, Formatting.GREEN, Formatting.WHITE, Formatting.GREEN, Formatting.WHITE));
			console.chatPlugin$sendMessage(getText(new String[] { "  \\__", " |     ", "Detected server version: ", VersionUtils.getVersion().getName() + " (protocol: " + VersionUtils.getVersion().getProtocol() + ")" }, Formatting.RED, Formatting.WHITE, Formatting.DARK_GRAY, Formatting.WHITE));
			console.chatPlugin$sendMessage(getText(new String[] { "" }, Formatting.RESET));
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	protected static Text getText(String[] literals, Formatting... formatting) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Text text = null;
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_16))
			for (int i = 0; i < literals.length; i++) {
				Text arg = (Text) Text.class.getMethod("method_10854", Formatting.class).invoke(Class.forName("net.minecraft.class_2585").getConstructor(String.class).newInstance(literals[i]), formatting[i]); // new LiteralText(literal).formatted(formatting)
				text = i == 0 ? arg : (Text) Text.class.getMethod("method_10852", Text.class).invoke(text, arg);
			}
		else if (VersionUtils.getVersion().isOlderThan(Version.V1_19))
			for (int i = 0; i < literals.length; i++) {
				Text arg = (Text) MutableText.class.getMethod("method_27692", Formatting.class).invoke(Class.forName("net.minecraft.class_2585").getConstructor(String.class).newInstance(literals[i]), formatting[i]); // new LiteralText(literal).formatted(formatting)
				text = i == 0 ? arg : (Text) MutableText.class.getMethod("method_10852", Text.class).invoke(text, arg);
			}
		else for (int i = 0; i < literals.length; i++) {
			Text arg = Text.literal(literals[i]).formatted(formatting[i]);
			text = i == 0 ? arg : ((MutableText) text).append(arg);
		} return text;
	}
	
	@Override
	public boolean isOnlineMode() {
		if (ProxyManager.getInstance() == null || ConfigurationType.CONFIG.get() == null)
			throw new IllegalStateException("Unable to call ChatPlugin#isOnlineMode() as the plugin has not finished loading yet");
		return ProxyManager.getInstance().isEnabled() ? ConfigurationType.CONFIG.get().getBoolean("multi-instance-mode.proxy-online-mode") : server.isOnlineMode();
	}
	
	@Override
	public boolean isPremium() {
		return false; // just for you to know, changing this will not unlock the premium features :)
	}
	
	public static void sendMessage(ServerCommandSource target, Text message) {
		((ServerCommandSourceExtension) target).chatPlugin$sendMessage(message);
	}
	
	public static ServerCommandSource getCommandSource(ServerPlayerEntity player) {
		if (VersionUtils.getVersion().isOlderThan(Version.V1_21_2))
			try {
				return (ServerCommandSource) Entity.class.getMethod("method_5671").invoke(player);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		return player.getCommandSource();
	}
	
	public static String toLegacyText(Text fabricComponent) {
		return Utils.toLegacyText(toBungeeCordComponent(fabricComponent));
	}
	
	public static JsonElement toJSON(Text fabricComponent) {
		return VersionUtils.getVersion().isAtLeast(Version.V1_20_3)
				? NewText.toJSON(fabricComponent)
				: GSON.toJsonTree(fabricComponent);
	}
	
	public static Text toFabricComponent(String legacyText) {
		return toFabricComponent(Utils.toBungeeCordComponent(legacyText));
	}
	
	public static Text toFabricComponent(JsonElement json) {
		return VersionUtils.getVersion().isAtLeast(Version.V1_20_3)
				? NewText.toFabricComponent(json)
				: GSON.fromJson(json, Text.class);
	}
	
	public static Text toFabricComponent(BaseComponent bungeeCordComponent) {
		return toFabricComponent(Utils.toJSON(bungeeCordComponent));
	}
	
	public static BaseComponent toBungeeCordComponent(Text fabricComponent) {
		return Utils.toBungeeCordComponent(toJSON(fabricComponent));
	}
	
	public static class CommandExecutor {
		
		public static void runCommand(ServerCommandSource source, String command) {
			try {
				server.getCommandManager().getDispatcher().execute(command, source);
			} catch (CommandSyntaxException cse) {
				source.sendError(Texts.toText(cse.getRawMessage()));
				
				if (cse.getInput() != null && cse.getCursor() >= 0) {
					int i = Math.min(cse.getInput().length(), cse.getCursor());
					BaseComponent error = new TextComponent();
					
					error.setColor(ChatColor.GRAY);
					error.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command));
					
					if (i > 10)
						error.addExtra("…");
					error.addExtra(cse.getInput().substring(Math.max(0, i - 10), i));
					
					if (i < cse.getInput().length()) {
						BaseComponent text = new TextComponent(cse.getInput().substring(i));
						
						text.setColor(ChatColor.RED);
						text.setUnderlined(true);
						error.addExtra(text);
					} BaseComponent translation = new TranslatableComponent("command.context.here");
					
					translation.setColor(ChatColor.RED);
					translation.setItalic(true);
					error.addExtra(translation);
					source.sendError(toFabricComponent(error));
				}
			}
		}
		
	}
	
	private static class NewText { // 1.20.3+
		
		public static JsonElement toJSON(Text fabricComponent) {
			if (VersionUtils.getVersion().isAtLeast(Version.V1_20_5))
				return TextCodecs.CODEC.encodeStart(server.getRegistryManager().getOps(JsonOps.INSTANCE), fabricComponent).getOrThrow();
			try {
				return (JsonElement) Util.class.getMethod("method_47526", DataResult.class, Function.class).invoke(null, TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, fabricComponent), (Function<String, JsonParseException>) JsonParseException::new);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public static Text toFabricComponent(JsonElement json) {
			if (VersionUtils.getVersion().isAtLeast(Version.V1_20_5))
				return TextCodecs.CODEC.parse(server.getRegistryManager().getOps(JsonOps.INSTANCE), json).getOrThrow();
			try {
				return (Text) Util.class.getMethod("method_47526", DataResult.class, Function.class).invoke(null, TextCodecs.CODEC.parse(JsonOps.INSTANCE, json), (Function<String, JsonParseException>) JsonParseException::new);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
}
