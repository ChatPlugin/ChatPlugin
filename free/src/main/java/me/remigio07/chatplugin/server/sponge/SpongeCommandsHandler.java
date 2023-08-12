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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.server.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;

import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.command.CommandsHandler;

public class SpongeCommandsHandler extends CommandsHandler {
	
	public static void registerCommands() {
		long ms = System.currentTimeMillis();
		CommandManager manager = Sponge.getCommandManager();
		
		init();
		
		for (String command : commands.keySet()) {
			BaseCommand[] subcommands = commands.get(command);
			BaseCommand mainCommand = subcommands[subcommands.length - 1];
			
			manager.register(SpongeBootstrapper.getInstance(), new SpongeCommandAdapter(mainCommand), mainCommand.getMainArgs());
		} printTotalLoaded(ms);
	}
	
}
