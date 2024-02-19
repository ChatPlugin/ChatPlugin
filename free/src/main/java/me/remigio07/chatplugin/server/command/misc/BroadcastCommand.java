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

package me.remigio07.chatplugin.server.command.misc;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.ChatPluginPlayer;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class BroadcastCommand extends BaseCommand {
	
	public BroadcastCommand() {
		super("/broadcast <message>");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("broadcast", "bcast", "shout", "local", "bc");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (args.length == 0) {
			sendUsage(sender, language);
			return;
		} String message = String.join(" ", args);
		
		for (Language language2 : LanguageManager.getInstance().getLanguages()) {
			String msg = language2.getMessage("chat.broadcast-format.local", message);
			
			for (ChatPluginPlayer player : language2.getOnlinePlayers())
				player.sendMessage(msg);
		} ChatPlugin.getInstance().sendConsoleMessage(Language.getMainLanguage().getMessage("chat.broadcast-format.local", message), true);
	}
	
}
