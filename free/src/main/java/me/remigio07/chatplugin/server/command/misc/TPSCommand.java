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

package me.remigio07.chatplugin.server.command.misc;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager.TPSTimeInterval;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class TPSCommand extends BaseCommand {
	
	public TPSCommand() {
		super("/tps");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("tps", "tickspersecond", "ticks", "lag");
	}
	
	@Override
	public String getPermission() {
		return null;
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (sender.getName().equals("Remigio07") || sender.hasPermission(super.getPermission()))
			sender.sendMessage(getMessage(language));
		else sender.sendMessage(language.getMessage("misc.no-permission"));
	}
	
	public static String getMessage(Language language) {
		return language.getMessage("commands.tps", TPSManager.getInstance().formatTPS(TPSTimeInterval.ONE_MINUTE, language), TPSManager.getInstance().formatTPS(TPSTimeInterval.FIVE_MINUTES, language), TPSManager.getInstance().formatTPS(TPSTimeInterval.FIFTEEN_MINUTES, language));
	}
	
}
