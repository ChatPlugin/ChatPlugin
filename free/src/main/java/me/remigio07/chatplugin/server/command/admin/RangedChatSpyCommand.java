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

package me.remigio07.chatplugin.server.command.admin;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.server.chat.RangedChatManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.command.PlayerCommand;

public class RangedChatSpyCommand extends PlayerCommand {
	
	public RangedChatSpyCommand() {
		super("/rangedchatspy");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("rangedchatspy", "rcs");
	}
	
	@Override
	public void execute(ChatPluginServerPlayer player, String[] args) {
		if (RangedChatManager.getInstance().isEnabled()) {
			if (player.hasRangedChatSpyEnabled()) {
				player.setRangedChatSpyEnabled(false);
				player.sendTranslatedMessage("commands.rangedchatspy.disabled");
			} else {
				player.setRangedChatSpyEnabled(true);
				player.sendTranslatedMessage("commands.rangedchatspy.enabled");
			}
		} else player.sendTranslatedMessage("misc.disabled-feature");
	}
	
}
