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

package me.remigio07.chatplugin.server.sponge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.command.CommandsHandler;
import me.remigio07.chatplugin.server.command.PlayerCommand;

public class SpongeCommandAdapter implements CommandCallable {
	
	private BaseCommand command;
	
	public SpongeCommandAdapter(BaseCommand command) {
		this.command = command;
	}
	
	@Override
	public CommandResult process(CommandSource sender, String text) throws CommandException {
		String[] args = text.isEmpty() ? new String[0] : text.split(" ");
		BaseCommand[] commands = CommandsHandler.getCommands().get(command.getName());
		
		for (int i = 0; i < commands.length; i++) {
			BaseCommand command = commands[i];
			
			if (!command.isSubCommand() || (args.length > 0 && command.getMainArgs().contains(args[0].toLowerCase()))) {
				CommandSenderAdapter senderAdapter = new CommandSenderAdapter(sender);
				Language language = senderAdapter.isConsole() ? Language.getMainLanguage() : LanguageManager.getInstance().getLanguage(new OfflinePlayer(new PlayerAdapter((Player) sender)));
				
				if (sender instanceof Player) {
					if (senderAdapter.toServerPlayer() == null) {
						sender.sendMessage(Utils.serializeSpongeText(language.getMessage("misc.disabled-world"), false));
						return CommandResult.success();
					}
				} else if (command instanceof PlayerCommand) {
					sender.sendMessage(Utils.serializeSpongeText(language.getMessage("misc.only-players"), false));
					return CommandResult.success();
				} if (command.getPermission() != null && !sender.hasPermission(command.getPermission())) {
					sender.sendMessage(Utils.serializeSpongeText(language.getMessage("misc.no-permission"), false));
					return CommandResult.success();
				} if (sender instanceof CommandBlockSource) {
					Location<World> location = ((CommandBlockSource) sender).getLocation();
					
					if (CommandsHandler.shouldLogCommandBlocksCommands())
						LogManager.log("@ ({0}, {1} {2} {3}) issued command: /{4} {5}", 3, location.getExtent().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), command.getName(), text);
				} else LogManager.log("{0} issued command: /{1} {2}", 3, sender.getName(), command.getName(), text);
				command.execute(senderAdapter, language, args);
				return CommandResult.success();
			}
		} return CommandResult.success();
	}
	
	@Override
	public List<String> getSuggestions(CommandSource sender, String text, Location<World> location) throws CommandException {
		List<String> list = new ArrayList<>();
		String[] args = text.split(" ");
		BaseCommand[] commands = CommandsHandler.getCommands().get(command.getName());
		
		if (text.endsWith(" ")) {
			List<String> arrayList = new ArrayList<>(Arrays.asList(args));
			
			arrayList.add("");
			args = arrayList.toArray(new String[0]);
		} for (int i = 0; i < commands.length; i++) {
			BaseCommand command = commands[i];
			
			if (command.hasSubCommands() && args.length == 1) {
				list = command.getTabCompletionArgs(new CommandSenderAdapter(sender), args[0], 0);
				break;
			} else if ((!command.isSubCommand() && command.getMainArgs().contains(command.getName().toLowerCase()))
					|| (command.isSubCommand() && command.getMainArgs().contains(args[0].toLowerCase()))) {
				list = command.getTabCompletionArgs(new CommandSenderAdapter(sender), args[args.length - 1], args.length - 1);
				break;
			}
		} return list == null ? Collections.emptyList() : list;
	}
	
	@Override
	public Text getUsage(CommandSource sender) {
		return Utils.serializeSpongeText(command.getUsage(), false);
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
	
	public BaseCommand getCommand() {
		return command;
	}
	
}
