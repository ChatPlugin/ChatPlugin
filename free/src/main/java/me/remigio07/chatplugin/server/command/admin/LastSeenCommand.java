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

package me.remigio07.chatplugin.server.command.admin;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class LastSeenCommand extends BaseCommand {
	
	public LastSeenCommand() {
		super("/lastseen <player>");
		tabCompletionArgs.put(0, players);
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("lastseen", "seen", "lastlogout", "ls");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (args.length == 1) {
			PlayerAdapter player = PlayerAdapter.getPlayer(args[0], false);
			
			if (player == null) {
				TaskManager.runAsync(() -> {
					try {
						OfflinePlayer offlinePlayer = OfflinePlayer.get(args[0]).get();
						
						if (StorageConnector.getInstance().isPlayerStored(offlinePlayer)) {
							Long lastLogout = StorageConnector.getInstance().getPlayerData(PlayersDataType.LAST_LOGOUT, offlinePlayer);
							
							if (lastLogout == null)
								sender.sendMessage(language.getMessage("commands.lastseen.never-joined", offlinePlayer.getName()));
							else sender.sendMessage(language.getMessage("commands.lastseen.offline", offlinePlayer.getName(), Utils.formatTime(System.currentTimeMillis() - lastLogout, language, false, false), StorageConnector.getInstance().getPlayerData(PlayersDataType.PLAYER_IP, offlinePlayer)));
						} else sender.sendMessage(language.getMessage("misc.player-not-stored", args[0]));
					} catch (IllegalArgumentException iae) {
						sender.sendMessage(language.getMessage("misc.invalid-player-name"));
					} catch (InterruptedException | ExecutionException | SQLException e) {
						sender.sendMessage(e.getCause() instanceof NoSuchElementException
								? language.getMessage("misc.inexistent-player", args[0])
								: language.getMessage("misc.error-occurred", e.getClass().getSimpleName(), e.getLocalizedMessage())
								);
					}
				}, 0L);
			} else if (player.isLoaded()) {
				sender.sendMessage(language.getMessage("commands.lastseen.online", player.getName(), Utils.formatTime(System.currentTimeMillis() - ((ChatPluginServerPlayer) player.chatPluginValue()).getLoginTime(), language, false, false)));
			} else sender.sendMessage(language.getMessage("misc.disabled-world"));
		} else sendUsage(sender, language);
	}
	
}
