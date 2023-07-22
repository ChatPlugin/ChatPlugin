/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.server.bukkit;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.language.LanguageManager;
import me.remigio07_.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07_.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07_.chatplugin.server.command.BaseCommand;
import me.remigio07_.chatplugin.server.command.CommandsHandler;
import me.remigio07_.chatplugin.server.command.PlayerCommand;

public class BukkitCommandsHandler extends CommandsHandler implements CommandExecutor, TabCompleter {
	
	public static void registerCommands() {
		long ms = System.currentTimeMillis();
		BukkitCommandsHandler executor = new BukkitCommandsHandler();
		
		init();
		commands.keySet().forEach(command -> BukkitBootstrapper.getInstance().getCommand(command).setExecutor(executor));
		printTotalLoaded(ms);
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
						
						if (command instanceof PlayerCommand) {
							if (!(sender instanceof Player)) {
								sender.sendMessage(language.getMessage("misc.only-players"));
								return true;
							} if (senderAdapter.toServerPlayer() == null) {
								sender.sendMessage(language.getMessage("misc.disabled-world"));
								return true;
							}
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
