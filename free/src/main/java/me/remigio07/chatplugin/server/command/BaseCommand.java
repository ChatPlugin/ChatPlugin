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

package me.remigio07.chatplugin.server.command;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.api.server.ad.Ad;
import me.remigio07.chatplugin.api.server.ad.AdManager;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;

public abstract class BaseCommand {
	
	protected static List<String> numbers = Arrays.asList("10", "20", "30", "40", "50");
	protected static List<String> players = Arrays.asList("{players}");
	protected static List<String> servers = Arrays.asList("{servers}");
	protected static List<String> timestamps = Arrays.asList("1d", "1d,10h", "1d,10h,30m", "1d,10h,30m,5s");
	protected Map<Integer, List<String>> tabCompletionArgs = new HashMap<>();
	protected String name, usage;
	
	protected BaseCommand(String usage) {
		name = usage.substring(1, usage.contains(" ") ? usage.indexOf(' ') : usage.length()).toLowerCase();
		this.usage = usage;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUsage() {
		return usage;
	}
	
	public String getPermission() {
		return "chatplugin.commands." + getMainArgs().get(0);
	}
	
	public boolean hasSubCommands() {
		return false;
	}
	
	public boolean isSubCommand() {
		return false;
	}
	
	public List<String> getTabCompletionArgs(CommandSenderAdapter sender, String startsWith, int index) {
		if (tabCompletionArgs.get(index) == null || (getPermission() != null && !sender.hasPermission(getPermission())))
			return null;
		List<String> args = new ArrayList<>(tabCompletionArgs.get(index));
		
		if (args.contains("{players}")) {
			args.remove("{players}");
			args.addAll(PlayerManager.getInstance().getPlayersNames().stream().filter(name -> sender.hasPermission("chatplugin.commands.vanish") || !VanishManager.getInstance().getVanishedNames().contains(name)).collect(Collectors.toList()));
		} if (args.contains("{ips}")) {
			args.remove("{ips}");
			args.addAll(PlayerManager.getInstance().getPlayersIPs().stream().map(InetAddress::getHostAddress).collect(Collectors.toList()));
		} if (args.contains("{ranks}")) {
			args.remove("{ranks}");
			args.addAll(RankManager.getInstance().getRanks().stream().map(Rank::getID).collect(Collectors.toList()));
		} if (args.contains("{servers}")) {
			args.remove("{servers}");
			args.addAll(ProxyManager.getInstance().getServersIDs());
		} if (args.contains("{worlds}")) {
			args.remove("{worlds}");
			args.addAll(Utils.getWorlds());
		} if (args.contains("{ads}")) {
			args.remove("{ads}");
			args.addAll(AdManager.getInstance().isEnabled() ? AdManager.getInstance().getAds().stream().map(Ad::getID).collect(Collectors.toList()) : Collections.emptyList());
		} if (args.contains("{guis}")) {
			args.remove("{guis}");
			args.addAll(GUIManager.getInstance().isEnabled() ? GUIManager.getInstance().getGUIs().stream().map(GUI::getID).collect(Collectors.toList()) : Collections.emptyList());
		} if (args.contains("{managers}")) {
			args.remove("{managers}");
			args.addAll(ChatPluginManagers.getInstance().getManagers().keySet().stream().map(clazz -> clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Manager"))).collect(Collectors.toList()));
		} if (args.contains("{languages}")) {
			args.remove("{languages}");
			args.addAll(LanguageManager.getInstance().getLanguages().stream().map(Language::getID).collect(Collectors.toList()));
		} if (startsWith.isEmpty())
			return args;
		for (String str : new ArrayList<>(args))
			if (!str.toLowerCase().startsWith(startsWith.toLowerCase()))
				args.remove(str);
		return args;
	}
	
	protected void sendUsage(CommandSenderAdapter sender, Language language) {
		sender.sendMessage(language.getMessage("misc.wrong-syntax", usage));
	}
	
	protected static boolean reportOnlyPlayers(CommandSenderAdapter sender, Language language) {
		if (!sender.isPlayer()) {
			sender.sendMessage(language.getMessage("misc.only-players"));
			return false;
		} return true;
	}
	
	public abstract List<String> getMainArgs();
	
	public abstract void execute(CommandSenderAdapter sender, Language language, String[] args);
	
}
