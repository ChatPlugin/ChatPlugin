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

package me.remigio07.chatplugin.server.command.user;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.command.PlayerCommand;

public class LanguageCommand extends PlayerCommand {
	
	public LanguageCommand() {
		super("/language <language>");
		tabCompletionArgs.put(0, Arrays.asList("{languages}"));
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("language", "languages", "lang", "langs", "locale");
	}
	
	@Override
	public void execute(ChatPluginServerPlayer player, String[] args) {
		if (args.length == 1)
			player.executeCommand("chatplugin language " + args[0]);
		else sendUsage(player);
	}
	
}
