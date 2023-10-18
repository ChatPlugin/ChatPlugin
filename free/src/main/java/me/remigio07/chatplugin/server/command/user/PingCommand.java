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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.server.command.user;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PingManager;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class PingCommand extends BaseCommand {
	
	public PingCommand() {
		super("/ping [player]");
		tabCompletionArgs.put(0, players);
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("ping", "pong", "latency");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (args.length == 0) {
			if (sender.isPlayer()) {
				ChatPluginServerPlayer player = sender.toServerPlayer();
				
				player.sendTranslatedMessage("ping.self", PingManager.getInstance().formatPing(player), PingManager.getInstance().getPingQuality(player.getPing()).getText(language));
			} else sender.sendMessage(language.getMessage("ping.self", PingManager.getInstance().formatPing(0, language), PingManager.getInstance().getPingQuality(0).getText(language)));
		} else if (sender.hasPermission(getPermission() + ".others")) {
			if (PlayerAdapter.getPlayer(args[0], false) != null) {
				@SuppressWarnings("deprecation")
				ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(args[0], false, true);
				
				if (player != null) {
					sender.sendMessage(language.getMessage("ping.other", PingManager.getInstance().formatPing(player), player.getName(), PingManager.getInstance().getPingQuality(player.getPing()).getText(language)));
				} else sender.sendMessage(language.getMessage("misc.disabled-world"));
			} else sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
		} else sender.sendMessage(language.getMessage("misc.no-permission"));
	}
	
}
