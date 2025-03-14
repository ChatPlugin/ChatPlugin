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

package me.remigio07.chatplugin.server.command.admin;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.command.PlayerCommand;

public class ChatChannelSpyCommand extends PlayerCommand {
	
	public ChatChannelSpyCommand() {
		super("/chatchannelspy");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("chatchannelspy", "ccs");
	}
	
	@Override
	public void execute(ChatPluginServerPlayer player, String[] args) {
		if (ChatChannelsManager.getInstance().isEnabled()) {
			if (player.hasChatChannelSpyEnabled()) {
				player.setChatChannelSpyEnabled(false);
				player.sendTranslatedMessage("chat.channel.spy.disabled");
			} else {
				player.setChatChannelSpyEnabled(true);
				player.sendTranslatedMessage("chat.channel.spy.enabled");
			}
		} else player.sendTranslatedMessage("misc.disabled-feature");
	}
	
}
