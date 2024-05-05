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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.command.CommandsHandler;
import me.remigio07.chatplugin.server.command.PlayerCommand;

public class BukkitCommandsHandler extends CommandsHandler implements CommandExecutor, TabCompleter {
	
	public static final BukkitCommandsHandler HANDLER = new BukkitCommandsHandler();
	private static Constructor<PluginCommand> pluginCommandconstructor;
	private static CommandMap commandMap;
	private static Map<String, Command> knownCommands;
	
	static {
		init();
	}
	
	@SuppressWarnings({ "all", "unchecked" })
	private static void init() {
		try {
			pluginCommandconstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
			Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
			
			pluginCommandconstructor.setAccessible(true);
			commandMapField.setAccessible(true);
			knownCommandsField.setAccessible(true);
			
			commandMap = ((CommandMap) commandMapField.get(Bukkit.getServer()));
			knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);
		} catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	public static void registerCommands() {
		long ms = System.currentTimeMillis();
		
		registerCommands0();
		
		for (String command : commands.keySet()) {
			try {
				BaseCommand[] subcommands = commands.get(command);
				List<String> aliases = subcommands[subcommands.length - 1].getMainArgs();
				PluginCommand bukkitCommand = (PluginCommand) pluginCommandconstructor.newInstance(command, BukkitBootstrapper.getInstance()).setAliases(aliases.subList(1, aliases.size()));
				
				commandMap.register("chatplugin", bukkitCommand);
				bukkitCommand.setExecutor(HANDLER);
				bukkitCommand.setTabCompleter(HANDLER);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LogManager.log("Unable to register the /{0} command: {1}", 2, command, e.getMessage());
			}
		} BukkitReflection.invokeMethod("CraftServer", "syncCommands", Bukkit.getServer());
		printTotalLoaded(ms);
	}
	
	public static void unregisterCommands(boolean sync) {
		for (String command : commands.keySet()) {
			BaseCommand[] subcommands = commands.get(command);
			
			for (String alias : subcommands[subcommands.length - 1].getMainArgs()) {
				knownCommands.remove(alias);
				knownCommands.remove("chatplugin:" + alias);
			}
		} if (sync)
			BukkitReflection.invokeMethod("CraftServer", "syncCommands", Bukkit.getServer());
		commands.clear();
		
		total = 0;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command bukkitCommand, String label, String[] args) {
		String name = bukkitCommand.getName().toLowerCase();
		
		for (String s : commands.keySet()) {
			if (s.equals(name)) {
				for (int i = 0; i < commands.get(s).length; i++) {
					BaseCommand command = commands.get(s)[i];
					
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
						} LogManager.log(sender.getName() + " issued command: /" + name + " " + String.join(" ", args), 3);
						command.execute(senderAdapter, language, args);
						return true;
					}
				}
			}
		} return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command bukkitCommand, String alias, String[] args) {
		List<String> list = null;
		String name = bukkitCommand.getName().toLowerCase();
		
		for (String s : commands.keySet()) {
			if (s.equals(name)) {
				for (int i = 0; i < commands.get(s).length; i++) {
					BaseCommand command = commands.get(s)[i];
					
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
