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

package me.remigio07.chatplugin.server.command.admin;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class IPLookupCommand extends BaseCommand {
	
	public IPLookupCommand() {
		super("/iplookup <player|IP address>");
		tabCompletionArgs.put(0, Arrays.asList("{players}", "{ips}"));
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("iplookup", "lookup", "geolocate", "ipl");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (!IPLookupManager.getInstance().isEnabled()) {
			sender.sendMessage(language.getMessage("misc.disabled-feature"));
			return;
		} if (args.length == 0) {
			sendUsage(sender, language);
			return;
		} InetAddress ipAddress;
		
		try {
			ipAddress = Utils.getInetAddress(args[0]);
		} catch (IllegalArgumentException e) {
			PlayerAdapter player = PlayerAdapter.getPlayer(args[0], false);
			ipAddress = player == null ? null : player.getIPAddress();
		} if (ipAddress != null) {
			if (!ProxyManager.getInstance().isEnabled() || PlayerAdapter.getOnlinePlayers().size() != 0) {
				IPLookupManager.getInstance().getIPLookup(ipAddress, sender.getName()).thenAccept(ipLookup -> {
					if (ipLookup != null && ipLookup.isValid())
						sender.sendMessage(ipLookup.formatPlaceholders(language.getMessage("commands.iplookup")));
					else sender.sendMessage(language.getMessage("misc.invalid-ip-address", args[0]));
				});
			} else sender.sendMessage(language.getMessage("misc.at-least-one-online"));
		} else sender.sendMessage(language.getMessage("misc.player-not-found", args[0]));
	}
	
}
