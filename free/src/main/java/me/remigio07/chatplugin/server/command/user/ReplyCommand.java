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

import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.command.PlayerCommand;

public class ReplyCommand extends PlayerCommand {
	
	public ReplyCommand() {
		super("/reply <message>");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("reply", "r");
	}
	
	@Override
	public void execute(ChatPluginServerPlayer player, String[] args) {
		if (PrivateMessagesManager.getInstance().isEnabled())
			if (args.length != 0)
				try {
					PrivateMessagesManager.getInstance().sendReply(player, String.join(" ", args));
				} catch (IllegalArgumentException e) {
					player.sendTranslatedMessage("commands.reply.not-found");
				}
			else sendUsage(player);
		else player.sendTranslatedMessage("misc.disabled-feature");
	}
	
}
