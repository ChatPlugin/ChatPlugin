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

package me.remigio07.chatplugin.server.command.gui;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.gui.InternalGUIs;

public class PlayerInfoCommand extends BaseCommand {
	
	private static final Set<PlaceholderType> PLACEHOLDER_TYPES = EnumSet.allOf(PlaceholderType.class);
	
	public PlayerInfoCommand() {
		super("/playerinfo <player> [-chat]");
		tabCompletionArgs.put(0, players);
		tabCompletionArgs.put(1, Arrays.asList("-chat", "-c"));
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("playerinfo", "pinfo", "whois", "pi");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		switch (args.length) {
		case 1:
			if (reportOnlyPlayers(sender)) {
				if (GUIManager.getInstance().isEnabled() && InternalGUIs.getPlayerInfoLayout() != null) {
					if (PlayerAdapter.getPlayer(args[0], false) != null) {
						ChatPluginServerPlayer target = ServerPlayerManager.getInstance().getPlayer(args[0], false, true);
						
						if (target != null) {
							SinglePageGUI gui = (SinglePageGUI) GUIManager.getInstance().getGUI("player-info-" + target.getName());
							
							TaskManager.runAsync(() -> (gui == null ? InternalGUIs.createPlayerInfo(target) : gui).open(sender.toServerPlayer(), true), 0L);
						} else sender.sendMessage(language.getMessage("misc.disabled-world"));
					} else sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
				} else sender.sendMessage(language.getMessage("misc.disabled-feature"));
			} break;
		case 2:
			if (args[1].equalsIgnoreCase("-chat") || args[1].equalsIgnoreCase("-c")) {
				if (PlayerAdapter.getPlayer(args[0], false) != null) {
					ChatPluginServerPlayer target = ServerPlayerManager.getInstance().getPlayer(args[0], false, true);
					
					if (target != null) {
						TaskManager.runAsync(() -> {
							target.getIPLookup(true);
							sender.sendMessage(PlaceholderManager.getInstance().translatePlaceholders(language.getMessage("commands.playerinfo"), target, language, PLACEHOLDER_TYPES));
						}, 0L);
					} else sender.sendMessage(language.getMessage("misc.disabled-world"));
				} else sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
			} else sendUsage(sender, language);
			break;
		default:
			sendUsage(sender, language);
			break;
		}
	}
	
}
