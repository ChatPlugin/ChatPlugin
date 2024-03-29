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

package me.remigio07.chatplugin.server.command.user;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
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
		tabCompletionArgs.put(0, Arrays.asList("{players_excluding_self}"));
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
					if (recipient == null) {
						if (args[0].equalsIgnoreCase("console"))
							if (sender.hasPermission(getPermission() + ".console"))
								PrivateMessagesManager.getInstance().sendPrivateMessage(sender.toServerPlayer(), null, message);
							else sender.sendMessage(language.getMessage("misc.no-permission"));
						else if (PlayerAdapter.getPlayer(args[0], false) == null)
							sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
						else sender.sendMessage(language.getMessage("misc.disabled-world"));
					} else {
						if (VanishManager.getInstance().isEnabled() && recipient.isVanished()) {
							if (!sender.hasPermission(VanishManager.VANISH_PERMISSION)) {
								sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
								return;
							}
						} if (PlayerIgnoreManager.getInstance().isEnabled() && recipient.getIgnoredPlayers().contains(sender.toServerPlayer())) {
							sender.sendMessage(language.getMessage("commands.whisper.ignored", recipient.getName()));
							return;
						} PrivateMessagesManager.getInstance().sendPrivateMessage(sender.toServerPlayer(), recipient, message);
					}
				} catch (IllegalArgumentException e) {
					sender.sendMessage(language.getMessage("commands.whisper.self"));
				}
			} else sendUsage(sender, language);
		} else sender.sendMessage(language.getMessage("misc.disabled-feature"));
	}
	
}
