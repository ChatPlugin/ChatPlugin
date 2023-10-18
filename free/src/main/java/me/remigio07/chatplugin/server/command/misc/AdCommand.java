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

package me.remigio07.chatplugin.server.command.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.ad.Ad;
import me.remigio07.chatplugin.api.server.ad.AdManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class AdCommand extends BaseCommand {
	
	public AdCommand() {
		super("/ad <send|list> [player] [ID]");
		tabCompletionArgs.put(0, Arrays.asList("send", "list"));
	}
	
	@Override
	public java.util.List<String> getMainArgs() {
		return Arrays.asList("ad", "ads", "sendad");
	}
	
	@Override
	public boolean hasSubCommands() {
		return true;
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		sendUsage(sender, language);
	}
	
	public static class Send extends BaseCommand {
		
		public Send() {
			super("/ad send <player> <ID>");
			tabCompletionArgs.put(1, Arrays.asList("{players}", "all"));
			tabCompletionArgs.put(2, Arrays.asList("{ads}"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("send", "s");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.ad.send";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			if (!AdManager.getInstance().isEnabled()) {
				sender.sendMessage(language.getMessage("misc.disabled-feature"));
				return;
			} if (args.length == 3) {
				if (args[1].equalsIgnoreCase("all")) {
					AdManager manager = AdManager.getInstance();
					Ad ad = manager.getAd(args[2]);
					
					if (ad != null) {
						int players = 0;
						
						for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values()) {
							Ad ad2 = AdManager.getInstance().getAd(args[2]);
							
							if (ad2 != null) {
								manager.sendAd(ad2, player);
								players++;
							}
						} sender.sendMessage(language.getMessage("commands.ad.send.all", ad.getID(), players));
					} else sender.sendMessage(language.getMessage("misc.inexistent-id"));
				} else {
					PlayerAdapter player = PlayerAdapter.getPlayer(args[1], false);
					
					if (player != null) {
						ChatPluginServerPlayer serverPlayer = (ChatPluginServerPlayer) player.chatPluginValue();
						
						if (serverPlayer != null) {
							Ad ad = AdManager.getInstance().getAd(args[2]);
							
							if (ad != null) {
								AdManager.getInstance().sendAd(ad, serverPlayer);
								sender.sendMessage(language.getMessage("commands.ad.send.one", ad.getID(), serverPlayer.getName()));
							} else sender.sendMessage(language.getMessage("misc.inexistent-id"));
						} else sender.sendMessage(language.getMessage("misc.disabled-world"));
					} else sender.sendMessage(language.getMessage("misc.player-not-found", args[1]));
				}
			} else sendUsage(sender, language);
		}
		
	}
	
	public static class List extends BaseCommand {
		
		public List() {
			super("/ad list");
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("list", "ls", "l");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.ad.list";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			sender.sendMessage(language.getMessage("commands.ad.list", Utils.getStringFromList(new ArrayList<>(AdManager.getInstance().getAds().stream().map(Ad::getID).collect(Collectors.toList())), false, true)));
		}
		
	}
	
}
