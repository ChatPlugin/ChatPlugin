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

package me.remigio07.chatplugin.server.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.ad.AdManager;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.chat.RangedChatManager;
import me.remigio07.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.command.admin.ClearChatCommand;
import me.remigio07.chatplugin.server.command.admin.IPLookupCommand;
import me.remigio07.chatplugin.server.command.admin.LastSeenCommand;
import me.remigio07.chatplugin.server.command.admin.MuteAllCommand;
import me.remigio07.chatplugin.server.command.admin.RangedChatSpyCommand;
import me.remigio07.chatplugin.server.command.admin.SocialspyCommand;
import me.remigio07.chatplugin.server.command.admin.StaffChatCommand;
import me.remigio07.chatplugin.server.command.misc.AdCommand;
import me.remigio07.chatplugin.server.command.misc.BroadcastCommand;
import me.remigio07.chatplugin.server.command.misc.BroadcastRawCommand;
import me.remigio07.chatplugin.server.command.misc.TPSCommand;
import me.remigio07.chatplugin.server.command.user.ChatColorCommand;
import me.remigio07.chatplugin.server.command.user.IgnoreCommand;
import me.remigio07.chatplugin.server.command.user.LanguageCommand;
import me.remigio07.chatplugin.server.command.user.PingCommand;
import me.remigio07.chatplugin.server.command.user.PlayerListCommand;
import me.remigio07.chatplugin.server.command.user.RankInfoCommand;
import me.remigio07.chatplugin.server.command.user.ReplyCommand;
import me.remigio07.chatplugin.server.command.user.WhisperCommand;
import me.remigio07.chatplugin.server.command.vanish.VanishCommand;

public abstract class CommandsHandler {
	
	protected static Map<String, BaseCommand[]> commands = new HashMap<String, BaseCommand[]>();
	protected static int total;
	protected static long ms;
	
	protected static void registerCommands0() {
		if (ms == 0) {
			ms = System.currentTimeMillis();
			total = 0;
			
			commands.clear();
		}
		
		// main
		put(
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
			put(new LanguageCommand());
		if (PrivateMessagesManager.getInstance().isEnabled()) {
			put(new WhisperCommand());
			put(new ReplyCommand());
		} if (PlayerIgnoreManager.getInstance().isEnabled())
			put(
					new IgnoreCommand.Add(),
					new IgnoreCommand.Clear(),
					new IgnoreCommand.List(),
					new IgnoreCommand.Remove(),
					new IgnoreCommand()
					);
		put(new PingCommand());
		put(new RankInfoCommand());
		put(new PlayerListCommand());
		
		if (!ChatPlugin.getInstance().isPremium())
			put(new ChatColorCommand());
		
		// admin
		if (StaffChatManager.getInstance().isEnabled())
			put(new StaffChatCommand());
		if (PrivateMessagesManager.getInstance().isEnabled())
			put(new SocialspyCommand());
		if (RangedChatManager.getInstance().isEnabled())
			put(new RangedChatSpyCommand());
		if (IPLookupManager.getInstance().isEnabled())
			put(new IPLookupCommand());
		put(new LastSeenCommand());
		put(new ClearChatCommand());
		put(new MuteAllCommand());
		
		// vanish
		put(new VanishCommand());
		
		// misc
		put(new TPSCommand());
		
		if (AdManager.getInstance().isEnabled())
			put(
					new AdCommand.List(),
					new AdCommand.Send(),
					new AdCommand()
					);
		put(new BroadcastCommand());
		put(new BroadcastRawCommand());
	}
	
	private static void put(BaseCommand... commands) {
		CommandsHandler.commands.put(commands[0].name, commands);
		
		total += commands.length;
	}
	
	protected static void printTotalLoaded() {
		LogManager.log("Loaded {0} commands in {1} ms.", 4, total, System.currentTimeMillis() - ms);
	}
	
	public static void executeCommands(ChatPluginServerPlayer player, List<String> commands) {
		for (String command : commands) {
			if (command.isEmpty() || command.equals(Utils.STRING_NOT_FOUND))
				continue;
			if (player != null && command.startsWith("p:"))
				if (player.isOnline())
					player.executeCommand(command.substring(2).trim());
				else return;
			else ChatPlugin.getInstance().runConsoleCommand(command.replace("{0}", player == null ? "" : player.getName()));
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
	
	public static long getMs() {
		return ms;
	}
	
	public static void setMs(long ms) {
		CommandsHandler.ms = ms;
	}
	
}
