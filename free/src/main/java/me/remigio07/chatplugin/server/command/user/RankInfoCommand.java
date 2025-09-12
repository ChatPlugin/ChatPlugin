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

package me.remigio07.chatplugin.server.command.user;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class RankInfoCommand extends BaseCommand {
	
	private static final Set<PlaceholderType> PLACEHOLDER_TYPES = EnumSet.of(PlaceholderType.PLAYER);
	
	public RankInfoCommand() {
		super("/rankinfo <player>");
		tabCompletionArgs.put(0, players);
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("rankinfo", "rinfo");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (args.length == 1) {
			if (PlayerAdapter.getPlayer(args[0], false) != null) {
				@SuppressWarnings("deprecation")
				ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(args[0], false, true);
				
				if (player != null)
					if (player.isVanished() && !sender.hasPermission(VanishManager.VANISH_PERMISSION))
						sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
					else sender.sendMessage(PlaceholderManager.getInstance().translatePlaceholders(language.getMessage("commands.rankinfo"), player, language, PLACEHOLDER_TYPES));
				else sender.sendMessage(language.getMessage("misc.disabled-world"));
			} else sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
		} else sendUsage(sender, language);
	}
	
}
