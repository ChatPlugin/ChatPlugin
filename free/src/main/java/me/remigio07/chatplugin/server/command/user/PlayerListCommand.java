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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class PlayerListCommand extends BaseCommand {
	
	public PlayerListCommand() {
		super("/playerlist [rank]");
		tabCompletionArgs.put(0, Arrays.asList("{ranks}"));
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("playerlist", "plist", "list", "online");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		boolean hideVanished = VanishManager.getInstance().isEnabled() && !sender.hasPermission(VanishManager.VANISH_PERMISSION);
		final String vanishedColor, notVanishedColor;
		
		if (args.length == 0) {
			if (PlayerAdapter.getOnlinePlayers().size() == 0) {
				sender.sendMessage(language.getMessage("commands.playerlist.all.no-players-online"));
			} else {
				HashMap<Rank, List<String>> temp = new HashMap<>();
				vanishedColor = language.getMessage("commands.playerlist.name-format.vanished");
				notVanishedColor = language.getMessage("commands.playerlist.name-format.not-vanished");
				
				sender.sendMessage(language.getMessage("commands.playerlist.all.message", hideVanished ? VanishManager.getInstance().getOnlineServer() : PlayerAdapter.getOnlinePlayers().size(), Utils.getMaxPlayers()));
				
				for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
					if (!hideVanished || !player.isVanished() || player.getUUID().equals(sender.getUUID()))
						temp.put(player.getRank(), Utils.addAndGet(temp.getOrDefault(player.getRank(), new ArrayList<>()), Arrays.asList((player.isVanished() ? vanishedColor : notVanishedColor) + player.getName())));
				for (Entry<Rank, List<String>> rank : temp.entrySet())
					sender.sendMessage(rank.getKey().formatPlaceholders(language.getMessage("commands.playerlist.all.rank-format", rank.getValue().size(), String.join(", " + notVanishedColor, rank.getValue().toArray(new String[0])) + "\u00A7r"), language));
			}
		} else {
			Rank rank = RankManager.getInstance().getRank(args[0]);
			
			if (rank != null) {
				List<String> players = new ArrayList<>();
				vanishedColor = language.getMessage("commands.playerlist.name-format.vanished");
				notVanishedColor = language.getMessage("commands.playerlist.name-format.not-vanished");
				
				for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
					if (player.getRank().equals(rank) && (!hideVanished || !player.isVanished() || player.getUUID().equals(sender.getUUID())))
						players.add((player.isVanished() ? vanishedColor : notVanishedColor) + player.getName());
				if (players.size() == 0)
					sender.sendMessage(language.getMessage("commands.playerlist.rank.no-players-online"));
				else sender.sendMessage(language.getMessage("commands.playerlist.rank.message", players.size(), rank.getDisplayName(), String.join(", " + notVanishedColor, players.toArray(new String[0])) + "\u00A7r"));
			} else sender.sendMessage(language.getMessage("misc.invalid-rank", args[0]));
		}
	}
	
}
