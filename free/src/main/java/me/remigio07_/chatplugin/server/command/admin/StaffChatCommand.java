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

import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07_.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07_.chatplugin.server.command.BaseCommand;

public class StaffChatCommand extends BaseCommand {
	
	public StaffChatCommand() {
		super("/staffchat [message]");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("staffchat", "staffc", "schat", "sch", "sc");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (!StaffChatManager.getInstance().isEnabled()) {
			sender.sendMessage(language.getMessage("misc.disabled-feature"));
			return;
		} if (args.length == 0) {
			if (reportOnlyPlayers(sender, language)) {
				if (ServerPlayerManager.getInstance().getPlayer(sender.getUUID()) != null) {
					if (StaffChatManager.getInstance().isUsingStaffChat(sender.getUUID())) {
						sender.sendMessage(language.getMessage("commands.staff-chat.disabled"));
						StaffChatManager.getInstance().removePlayer(sender.getUUID());
					} else {
						sender.sendMessage(language.getMessage("commands.staff-chat.enabled"));
						StaffChatManager.getInstance().addPlayer(sender.getUUID());
					}
				} else sender.sendMessage(language.getMessage("misc.disabled-world"));
			}
		} else if (sender.isPlayer()) {
			if (ServerPlayerManager.getInstance().getPlayer(sender.getUUID()) != null)
				StaffChatManager.getInstance().sendPlayerMessage(ServerPlayerManager.getInstance().getPlayer(sender.getUUID()), String.join(" ", args));
			else sender.sendMessage(language.getMessage("misc.disabled-world"));
		} else if (PlayerAdapter.getOnlinePlayers().size() > 0)
			StaffChatManager.getInstance().sendConsoleMessage(String.join(" ", args));
		else sender.sendMessage(language.getMessage("misc.at-least-one-online"));
	}
	
}
