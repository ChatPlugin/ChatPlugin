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

import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;

import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.command.CommandsHandler;

public class SpongeCommandsHandler extends CommandsHandler {
	
	public static void registerCommands() {
		CommandManager manager = Sponge.getCommandManager();
		
		registerCommands0();
		
		for (String command : commands.keySet()) {
			BaseCommand[] subcommands = commands.get(command);
			BaseCommand mainCommand = subcommands[subcommands.length - 1];
			
			if (disabledCommands.containsAll(mainCommand.getMainArgs()))
				continue;
			manager.getAll(command).forEach(Sponge.getCommandManager()::removeMapping);
			manager.register(SpongeBootstrapper.getInstance(), new SpongeCommandAdapter(mainCommand), mainCommand.getMainArgs().stream().filter(alias -> !disabledCommands.contains(alias)).collect(Collectors.toList()));
		} printTotalLoaded();
	}
	
	public static void unregisterCommands() {
		Sponge.getCommandManager().getOwnedBy(SpongeBootstrapper.getInstance()).forEach(Sponge.getCommandManager()::removeMapping);
	}
	
}
