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

package me.remigio07_.chatplugin.server.command.vanish;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07_.chatplugin.api.server.util.Utils;
import me.remigio07_.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07_.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07_.chatplugin.server.command.BaseCommand;

public class VanishCommand extends BaseCommand {
	
	public VanishCommand() {
		super("/vanish [player]");
		tabCompletionArgs.put(0, players);
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("vanish", "hide", "fade");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (!VanishManager.getInstance().isEnabled()) {
			sender.sendMessage(language.getMessage("misc.disabled-feature")); 
			return;
		} Map<String, List<ChatPluginServerPlayer>> vanished = VanishManager.getInstance().getVanishedMap();
		
		if (args.length == 0) {
			if (reportOnlyPlayers(sender, language)) {
				ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(sender.getUUID());
				
				if (player == null) {
					sender.sendMessage(language.getMessage("misc.disabled-world"));
					return;
				} String str, world = player.getWorld();
				
				if (VanishManager.getInstance().isVanished(player)) {
					if (QuitMessageManager.getInstance().hasFakeQuit(player.getUUID())) {
						sender.sendMessage(language.getMessage("vanish.fakequit.already-fakequit"));
						return;
					} vanished.put(world, Utils.removeAndGet(VanishManager.getInstance().getVanishedList(world), Arrays.asList(player)));
					VanishManager.getInstance().show(player);
					str = "vanish.disabled.self";
				} else {
					vanished.put(world, Utils.addAndGet(VanishManager.getInstance().getVanishedList(world), Arrays.asList(player)));
					VanishManager.getInstance().hide(player);
					str = "vanish.enabled.self";
				} player.sendMessage(language.getMessage(str));
			}
		} else if (sender.hasPermission(getPermission() + ".others")) {
			if (PlayerAdapter.getPlayer(args[0], false) != null) {
				@SuppressWarnings("deprecation")
				ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(args[0], false, true);
				
				if (player == null) {
					sender.sendMessage(language.getMessage("misc.disabled-world"));
					return;
				} String str1, str2, world = player.getWorld();
				
				if (!player.hasPermission(getPermission())) {
					sender.sendMessage(language.getMessage("vanish.no-permission", player.getName()));
					return;
				} if (VanishManager.getInstance().isVanished(player)) {
					if (QuitMessageManager.getInstance().hasFakeQuit(player.getUUID())) {
						sender.sendMessage(language.getMessage("vanish.fakequit.already-fakequit"));
						return;
					} vanished.put(world, Utils.removeAndGet(VanishManager.getInstance().getVanishedList(world), Arrays.asList(player)));
					VanishManager.getInstance().show(player);
					str1 = "vanish.disabled.self";
					str2 = "vanish.disabled.other";
				} else {
					vanished.put(world, Utils.addAndGet(VanishManager.getInstance().getVanishedList(world), Arrays.asList(player)));
					VanishManager.getInstance().hide(player);
					str1 = "vanish.enabled.self";
					str2 = "vanish.enabled.other";
				} player.sendMessage(language.getMessage(str1));
				sender.sendMessage(language.getMessage(str2, player.getName()));
			} else sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
		} else sender.sendMessage(language.getMessage("misc.no-permission"));
	}
	
}
