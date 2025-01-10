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

package me.remigio07.chatplugin.server.command.user;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.actionbar.ActionbarManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class ActionbarCommand extends BaseCommand {
	
	public ActionbarCommand() {
		super("/actionbar [player]");
		tabCompletionArgs.put(0, players);
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("actionbar", "abar", "ab");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (!ActionbarManager.getInstance().isEnabled()) {
			sender.sendMessage(language.getMessage("misc.disabled-feature"));
			return;
		} ChatPluginServerPlayer player;
		
		if (args.length == 0) {
			if (reportOnlyPlayers(sender)) {
				player = sender.toServerPlayer();
				
				if (player.hasActionbarEnabled()) {
					player.setActionbarEnabled(false);
					player.sendTranslatedMessage("commands.actionbar.disabled");
				} else {
					player.setActionbarEnabled(true);
					player.sendTranslatedMessage("commands.actionbar.enabled");
				}
			}
		} else if (sender.hasPermission(getPermission() + ".others")) {
			if (PlayerAdapter.getPlayer(args[0], false) != null) {
				player = ServerPlayerManager.getInstance().getPlayer(args[0], false, true);
				
				if (player != null) {
					if (player.hasActionbarEnabled()) {
						player.setActionbarEnabled(false);
						sender.sendMessage(language.getMessage("commands.actionbar.disabled"));
					} else {
						player.setActionbarEnabled(true);
						sender.sendMessage(language.getMessage("commands.actionbar.enabled"));
					}
				} else sender.sendMessage(language.getMessage("misc.disabled-world"));
			} else sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
		} else sender.sendMessage(language.getMessage("misc.no-permission"));
	}
	
}
