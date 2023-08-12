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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.server.command;

import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;

public abstract class PlayerCommand extends BaseCommand {
	
	protected PlayerCommand(String usage) {
		super(usage);
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		execute(ServerPlayerManager.getInstance().getPlayer(sender.getUUID()), args);
	}
	
	public void sendUsage(ChatPluginServerPlayer player) {
		player.sendTranslatedMessage("misc.wrong-syntax", usage);
	}
	
	public abstract void execute(ChatPluginServerPlayer player, String[] args);
	
}
