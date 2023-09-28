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

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class WhisperCommand extends BaseCommand {
	
	public WhisperCommand() {
		super("/whisper <recipient> <message>");
		tabCompletionArgs.put(0, players);
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("whisper", "message", "msg", "tell", "w", "dm");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (PrivateMessagesManager.getInstance().isEnabled()) {
			if (args.length > 1) {
				@SuppressWarnings("deprecation")
				ChatPluginServerPlayer recipient = ServerPlayerManager.getInstance().getPlayer(args[0], false, true);
				String message = String.join(" ", Arrays.asList(args).subList(1, args.length).toArray(new String[args.length - 1]));
				
				try {
					if (recipient == null)
						if (args[0].equalsIgnoreCase("console"))
							if (sender.hasPermission(getPermission() + ".console"))
								PrivateMessagesManager.getInstance().sendMessage(sender.toServerPlayer(), null, message);
							else sender.sendMessage(language.getMessage("misc.no-permission"));
						else sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
					else {
						if (VanishManager.getInstance().isEnabled() && VanishManager.getInstance().isVanished(recipient)) {
							if (!sender.hasPermission("chatplugin.commands.vanish")) {
								sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
								return;
							}
						} if (PlayerIgnoreManager.getInstance().isEnabled() && recipient.getIgnoredPlayers().contains(sender.toServerPlayer())) {
							sender.sendMessage(language.getMessage("commands.whisper.ignored", recipient.getName()));
							return;
						} PrivateMessagesManager.getInstance().sendMessage(sender.toServerPlayer(), recipient, message);
					}
				} catch (IllegalArgumentException e) {
					sender.sendMessage(language.getMessage("commands.whisper.self"));
				}
			} else sendUsage(sender, language);
		} else sender.sendMessage(language.getMessage("misc.disabled-feature"));
	}
	
}
