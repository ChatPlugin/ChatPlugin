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

package me.remigio07.chatplugin.server.integration.anticheat;

import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07.chatplugin.api.server.integration.anticheat.Violation;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.PingManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;

public class ViolationImpl extends Violation {
	
	public ViolationImpl(ChatPluginServerPlayer cheater, String server, IntegrationType<AnticheatIntegration> anticheat, String cheatID, String component, int amount, int ping, int versionProtocol, boolean versionPreNettyRewrite, double tps) {
		super(cheater, server, anticheat, cheatID, component, amount, ping, versionProtocol, versionPreNettyRewrite, tps);
	}
	
	public String formatPlaceholders(String input, Language language) {
		return input
				.replace("{player}", cheater.getName())
				.replace("{player_uuid}", cheater.getUUID().toString())
				.replace("{server}", server)
				.replace("{anticheat}", anticheat.getPlugin())
				.replace("{cheat_id}", ConfigurationType.VIOLATIONS_ICONS.get().translateString(anticheat.name().toLowerCase() + "." + cheatID.toLowerCase() +  ".name"))
				.replace("{component}", component)
				.replace("{amount}", String.valueOf(amount))
				.replace("{ping}", String.valueOf(ping))
				.replace("{ping_format}", PingManager.getInstance().formatPing(ping, language))
				.replace("{version}", Version.getVersion(versionProtocol, versionPreNettyRewrite).format())
				.replace("{version_protocol}", String.valueOf(versionProtocol))
				.replace("{tps}", TPSManager.getInstance().formatTPS(tps, language))
				.replace("{last_time}", Utils.formatTime(System.currentTimeMillis() - lastTime, language, false, true));
	}
	
	public List<String> formatPlaceholders(List<String> input, Language language) {
		return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
	}
	
}
