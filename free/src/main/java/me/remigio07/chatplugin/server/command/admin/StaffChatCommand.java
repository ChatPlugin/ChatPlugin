/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.server.command.admin;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class StaffChatCommand extends BaseCommand {
	
	public StaffChatCommand() {
		super("/staffchat [message]");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("staffchat", "schat","sc");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (!StaffChatManager.getInstance().isEnabled()) {
			sender.sendMessage(language.getMessage("misc.disabled-feature"));
			return;
		} if (args.length == 0) {
			if (reportOnlyPlayers(sender)) {
				if (StaffChatManager.getInstance().isUsingStaffChat(sender.getUUID())) {
					sender.sendMessage(language.getMessage("commands.staff-chat.disabled"));
					StaffChatManager.getInstance().removePlayer(sender.getUUID());
				} else {
					sender.sendMessage(language.getMessage("commands.staff-chat.enabled"));
					StaffChatManager.getInstance().addPlayer(sender.getUUID());
				}
			}
		} else if (sender.isPlayer())
			StaffChatManager.getInstance().sendPlayerMessage(sender.toServerPlayer(), String.join(" ", args));
		else try {
			StaffChatManager.getInstance().sendConsoleMessage(String.join(" ", args));
		} catch (IllegalStateException e) {
			sender.sendMessage(language.getMessage("misc.at-least-one-online"));
		}
	}
	
}
