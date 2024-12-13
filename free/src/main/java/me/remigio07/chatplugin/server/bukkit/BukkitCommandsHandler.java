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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.plugin.Plugin;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.command.CommandsHandler;
import me.remigio07.chatplugin.server.command.PlayerCommand;

@SuppressWarnings("unchecked")
public class BukkitCommandsHandler extends CommandsHandler implements CommandExecutor, TabCompleter {
	
	public static final BukkitCommandsHandler HANDLER = new BukkitCommandsHandler();
	private static Constructor<PluginCommand> pluginCommandConstructor;
	private static CommandMap commandMap;
	private static Map<String, Command> knownCommands;
	
	static {
		try {
			pluginCommandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
			
			pluginCommandConstructor.setAccessible(true);
			commandMapField.setAccessible(true);
			knownCommandsField.setAccessible(true);
			
			commandMap = ((CommandMap) commandMapField.get(Bukkit.getServer()));
			knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
		} catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	public static void registerCommands() {
		registerCommands0();
		
		for (String command : commands.keySet()) {
			PluginCommand bukkitCommand = registerCommand(command);
			
			if (bukkitCommand != null) {
				bukkitCommand.setExecutor(HANDLER);
				bukkitCommand.setTabCompleter(HANDLER);
			}
		} syncCommands();
		printTotalLoaded();
	}
	
	public static PluginCommand registerCommand(String command) {
		try {
			BaseCommand[] subcommands = commands.get(command);
			List<String> aliases = subcommands[subcommands.length - 1].getMainArgs();
			
			if (disabledCommands.containsAll(aliases))
				return null;
			aliases = aliases.stream().filter(alias -> !disabledCommands.contains(alias)).collect(Collectors.toList());
			PluginCommand bukkitCommand = (PluginCommand) pluginCommandConstructor.newInstance(aliases.get(0), BukkitBootstrapper.getInstance());
			
			if (aliases.size() != 1)
				bukkitCommand.setAliases(aliases.subList(1, aliases.size()));
			commandMap.register("chatplugin", bukkitCommand);
			return bukkitCommand;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LogManager.log("Unable to register the /{0} command: {1}", 2, command, e.getMessage());
			return null;
		}
	}
	
	public static void unregisterCommands(boolean sync) {
		for (String command : commands.keySet()) {
			BaseCommand[] subcommands = commands.get(command);
			
			for (String alias : subcommands[subcommands.length - 1].getMainArgs()) {
				if (!disabledCommands.contains(alias)) {
					knownCommands.remove(alias);
					knownCommands.remove("chatplugin:" + alias);
				}
			}
		} if (sync)
			syncCommands();
	}
	
	public static void syncCommands() {
		if (VersionUtils.getVersion().isAtLeast(Version.V1_13))
			BukkitReflection.invokeMethod("CraftServer", "syncCommands", Bukkit.getServer());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command bukkitCommand, String label, String[] args) {
		if (label.contains(":"))
			label = label.substring(11);
		for (BaseCommand[] commands : commands.values()) {
			if (commands[commands.length - 1].getMainArgs().contains(label.toLowerCase())) {
				for (int i = 0; i < commands.length; i++) {
					BaseCommand command = commands[i];
					
					if (!command.isSubCommand() || (args.length > 0 && command.getMainArgs().contains(args[0].toLowerCase()))) {
						CommandSenderAdapter senderAdapter = new CommandSenderAdapter(sender);
						Language language = senderAdapter.isConsole() ? Language.getMainLanguage() : LanguageManager.getInstance().getLanguage(new OfflinePlayer(new PlayerAdapter((Player) sender)));
						
						if (sender instanceof Player) {
							if (senderAdapter.toServerPlayer() == null) {
								sender.sendMessage(language.getMessage("misc.disabled-world"));
								return true;
							}
						} else if (command instanceof PlayerCommand) {
							sender.sendMessage(language.getMessage("misc.only-players"));
							return true;
						} if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
							sender.sendMessage(language.getMessage("misc.no-permission"));
							return true;
						} if (sender instanceof BlockCommandSender) {
							Block block = ((BlockCommandSender) sender).getBlock();
							
							if (logCommandBlocksCommands)
								LogManager.log("@ ({0}, {1} {2} {3}) issued command: /{4} {5}", 3, block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), command.getName(), String.join(" ", args));
						} else if (sender instanceof CommandMinecart) {
							Block block = ((CommandMinecart) sender).getLocation().getBlock();
							
							if (logCommandBlocksCommands)
								LogManager.log("@ ({0}, {1} {2} {3}) issued command: /{4} {5}", 3, block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), command.getName(), String.join(" ", args));
						} else LogManager.log("{0} issued command: /{1} {2}", 3, sender.getName(), command.getName(), String.join(" ", args));
						command.execute(senderAdapter, language, args);
						return true;
					}
				}
			}
		} return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command bukkitCommand, String label, String[] args) {
		List<String> list = null;
		
		if (label.contains(":"))
			label = label.substring(11);
		for (BaseCommand[] commands : commands.values()) {
			if (commands[commands.length - 1].getMainArgs().contains(label.toLowerCase())) {
				for (int i = 0; i < commands.length; i++) {
					BaseCommand command = commands[i];
					
					if (command.hasSubCommands() && args.length == 1) {
						list = command.getTabCompletionArgs(new CommandSenderAdapter(sender), args[0], 0);
						break;
					} else if ((!command.isSubCommand() && command.getMainArgs().contains(bukkitCommand.getName().toLowerCase()))
							|| (command.isSubCommand() && command.getMainArgs().contains(args[0].toLowerCase()))) {
						list = command.getTabCompletionArgs(new CommandSenderAdapter(sender), args[args.length - 1], args.length - 1);
						break;
					}
				}
			}
		} return list == null ? Collections.emptyList() : list;
	}
	
}
