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

package me.remigio07.chatplugin.server.command.user;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class WhisperCommand extends BaseCommand {
	
	public WhisperCommand() {
		super("/whisper <recipient> <message>");
		tabCompletionArgs.put(0, Arrays.asList("{players_excluding_self}"));
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("whisper", "message", "msg", "tell", "w", "dm", "m", "t");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (PrivateMessagesManager.getInstance().isEnabled()) {
			if (args.length > 1) {
				TaskManager.runAsync(() -> {
					String message = String.join(" ", Arrays.asList(args).subList(1, args.length).toArray(new String[args.length - 1]));
					
					try {
						if (args[0].equalsIgnoreCase("console"))
							if (sender.hasPermission(getPermission() + ".console"))
								PrivateMessagesManager.getInstance().sendPrivateMessage(sender.toServerPlayer(), null, message);
							else sender.sendMessage(language.getMessage("misc.no-permission"));
						else if (PlayerManager.getInstance().isValidUsername(args[0])) {
							OfflinePlayer recipient = OfflinePlayer.get(args[0]).get();
							
							if (recipient.hasPlayedBefore())
								PrivateMessagesManager.getInstance().sendPrivateMessage(sender.toServerPlayer(), recipient, message);
							else sender.sendMessage(language.getMessage("misc.player-not-stored", recipient.getName()));
						} else sender.sendMessage(language.getMessage("misc.invalid-player-name"));
					} catch (IllegalArgumentException iae) {
						sender.sendMessage(language.getMessage("commands.whisper.self"));
					} catch (InterruptedException | ExecutionException e) {
						sender.sendMessage(e.getCause() instanceof NoSuchElementException
								? language.getMessage("misc.inexistent-player", args[0])
								: language.getMessage("misc.error-occurred", e.getClass().getSimpleName(), e.getLocalizedMessage())
								);
					}
				}, 0L);
			} else sendUsage(sender, language);
		} else sender.sendMessage(language.getMessage("misc.disabled-feature"));
	}
	
}
