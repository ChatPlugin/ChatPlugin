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
import java.util.List;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class GUICommand extends BaseCommand {
	
	public GUICommand() {
		super("/gui <open|refresh> <ID> [player] [page] [-openactions]");
		tabCompletionArgs.put(0, Arrays.asList("open", "refresh"));
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("gui", "customgui");
	}
	
	@Override
	public boolean hasSubCommands() {
		return true;
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		sendUsage(sender, language);
	}
	
	public static class Open extends BaseCommand {
		
		public Open() {
			super("/gui open <ID> [player] [page] [-openactions]");
			tabCompletionArgs.put(1, Arrays.asList("{guis}"));
			tabCompletionArgs.put(2, players);
			tabCompletionArgs.put(3, numbers);
			tabCompletionArgs.put(4, Arrays.asList("-openactions", "-oa"));
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("open", "show", "o");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.gui.open";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			if (GUIManager.getInstance().isEnabled()) {
				boolean openActions = Utils.arrayContains(args, "-openactions", true) || Utils.arrayContains(args, "-oa", true);
				
				if (openActions) {
					args = Utils.removeFromArray(args, "-openactions", true);
					args = Utils.removeFromArray(args, "-oa", true);
				} switch (args.length) {
				case 2:
					if (reportOnlyPlayers(sender)) {
						GUI gui = GUIManager.getInstance().getGUI(args[1]);
						
						if (gui != null) {
							if (gui instanceof SinglePageGUI)
								((SinglePageGUI) gui).open(sender.toServerPlayer(), openActions);
							else ((FillableGUI<?>) gui).open(sender.toServerPlayer(), 0, openActions);
						} else sender.sendMessage(language.getMessage("misc.inexistent-id"));
					} break;
				case 3:
					if (PlayerAdapter.getPlayer(args[2], false) != null) {
						ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(args[2], false, true);
						
						if (player != null) {
							GUI gui = GUIManager.getInstance().getGUI(args[1]);
							
							if (gui != null) {
								if (gui instanceof SinglePageGUI)
									((SinglePageGUI) gui).open(player, openActions);
								else ((FillableGUI<?>) gui).open(player, 0, openActions);
							} else sender.sendMessage(language.getMessage("misc.inexistent-id"));
						} else sender.sendMessage(language.getMessage("misc.disabled-world"));
					} else sender.sendMessage(language.getMessage("misc.player-not-found", args[2]));
					break;
				case 4:
					if (PlayerAdapter.getPlayer(args[2], false) != null) {
						ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(args[2], false, true);
						
						if (player != null) {
							GUI gui = GUIManager.getInstance().getGUI(args[1]);
							
							if (gui != null) {
								try {
									int page = Integer.parseInt(args[3]);
									
									if (gui instanceof SinglePageGUI)
										((SinglePageGUI) gui).open(player, openActions);
									else ((FillableGUI<?>) gui).open(player, page - 1, openActions);
								} catch (NumberFormatException | IndexOutOfBoundsException e) {
									sender.sendMessage(language.getMessage("misc.invalid-number", args[3]));
								}
							} else sender.sendMessage(language.getMessage("misc.inexistent-id"));
						} else sender.sendMessage(language.getMessage("misc.disabled-world"));
					} else sender.sendMessage(language.getMessage("misc.player-not-found", args[2]));
					break;
				default:
					sendUsage(sender, language);
					break;
				}
			} else sender.sendMessage(language.getMessage("misc.disabled-feature"));
		}
		
	}
	
	public static class Refresh extends BaseCommand {
		
		public Refresh() {
			super("/gui refresh <ID>");
			tabCompletionArgs.put(1, Arrays.asList("{guis}"));
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("refresh", "reload", "r");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.gui.refresh";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			if (GUIManager.getInstance().isEnabled()) {
				if (args.length == 2) {
					GUI gui = GUIManager.getInstance().getGUI(args[1]);
					
					if (gui != null)
						gui.refresh();
					else sender.sendMessage(language.getMessage("misc.inexistent-id"));
				} else sendUsage(sender, language);
			} else sender.sendMessage(language.getMessage("misc.disabled-feature"));
		}
		
	}
	
}
