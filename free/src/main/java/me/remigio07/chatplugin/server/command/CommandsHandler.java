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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.server.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.command.admin.ClearChatCommand;
import me.remigio07.chatplugin.server.command.admin.IPLookupCommand;
import me.remigio07.chatplugin.server.command.admin.LastSeenCommand;
import me.remigio07.chatplugin.server.command.admin.MuteAllCommand;
import me.remigio07.chatplugin.server.command.admin.StaffChatCommand;
import me.remigio07.chatplugin.server.command.misc.AdCommand;
import me.remigio07.chatplugin.server.command.misc.BroadcastCommand;
import me.remigio07.chatplugin.server.command.misc.BroadcastRawCommand;
import me.remigio07.chatplugin.server.command.misc.TPSCommand;
import me.remigio07.chatplugin.server.command.user.LanguageCommand;
import me.remigio07.chatplugin.server.command.user.PingCommand;
import me.remigio07.chatplugin.server.command.user.PlayerListCommand;
import me.remigio07.chatplugin.server.command.user.RankInfoCommand;
import me.remigio07.chatplugin.server.command.vanish.VanishCommand;

public abstract class CommandsHandler {
	
	protected static Map<String, BaseCommand[]> commands = new HashMap<String, BaseCommand[]>();
	protected static int total;
	
	protected static void init() {
		// main
		put("chatplugin",
				new ChatPluginCommand.Debug(),
				new ChatPluginCommand.Help(),
				new ChatPluginCommand.Info(),
				new ChatPluginCommand.Language(),
				new ChatPluginCommand.Reload(),
				new ChatPluginCommand.Status(),
				new ChatPluginCommand.Version(),
				new ChatPluginCommand()
				);
		// user
		if (!ChatPlugin.getInstance().isPremium())
			put("language", new LanguageCommand());
		put("ping", new PingCommand());
		put("rankinfo", new RankInfoCommand());
		put("playerlist", new PlayerListCommand());
		// admin
		put("staffchat", new StaffChatCommand());
		put("iplookup", new IPLookupCommand());
		put("lastseen", new LastSeenCommand());
		put("clearchat", new ClearChatCommand());
		put("muteall", new MuteAllCommand());
		// vanish
		put("vanish", new VanishCommand());
		// misc
		put("tps", new TPSCommand());
		put("ad",
				new AdCommand.List(),
				new AdCommand.Send(),
				new AdCommand()
				);
		put("broadcast", new BroadcastCommand());
		put("broadcastraw", new BroadcastRawCommand());
	}
	
	private static void put(String name, BaseCommand... commands) {
		CommandsHandler.commands.put(name, commands);
		
		total += commands.length;
	}
	
	protected static void printTotalLoaded(long ms) {
		LogManager.log("Loaded {0} commands in {1} ms.", 4, total, System.currentTimeMillis() - ms);
	}
	
	public static void executeCommands(ChatPluginServerPlayer player, List<String> commands, long delay) {
		for (String command : commands) {
			if (command.isEmpty() || command.equals(Utils.STRING_NOT_FOUND))
				continue;
			TaskManager.runSync(() -> {
				if (player != null && command.startsWith("p:"))
					if (player.isOnline())
						player.executeCommand(command.substring(2).trim());
					else return;
				else ChatPlugin.getInstance().runConsoleCommand(command.replace("{0}", player == null ? "" : player.getName()));
			}, delay);
		}
	}
	
	public static Map<String, BaseCommand[]> getCommands() {
		return commands;
	}
	
	public static int getTotal() {
		return total;
	}
	
	public static void setTotal(int total) {
		CommandsHandler.total = total;
	}
	
}
