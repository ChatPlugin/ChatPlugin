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

package me.remigio07_.chatplugin.server.command.admin;

import java.util.Arrays;
import java.util.List;

import me.remigio07_.chatplugin.api.ChatPlugin;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07_.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07_.chatplugin.server.command.BaseCommand;

public class ClearChatCommand extends BaseCommand {
	
	public ClearChatCommand() {
		super("/clearchat");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("clearchat", "clrchat", "cchat");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
			player.sendMessage("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n \n" + player.getLanguage().getMessage("commands.clearchat"));
		ChatPlugin.getInstance().sendConsoleMessage(language.getMessage("commands.clearchat"), true);
	}
	
}
