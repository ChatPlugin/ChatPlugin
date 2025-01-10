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

package me.remigio07.chatplugin.server.integration.anticheat;

import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07.chatplugin.api.server.integration.anticheat.Violation;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.PingManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;

public class ViolationImpl extends Violation {
	
	public ViolationImpl(OfflinePlayer cheater, IntegrationType<AnticheatIntegration> anticheat, String cheatID, Version version, boolean bedrockEdition) {
		super(cheater, anticheat, cheatID, version, bedrockEdition);
	}
	
	@Override
	public String formatPlaceholders(String input, Language language) {
		return input
				.replace("{cheater}", cheater.getName())
				.replace("{cheater_uuid}", cheater.getUUID().toString())
				.replace("{anticheat}", anticheat.getPlugin())
				.replace("{cheat_id}", cheatID)
				.replace("{cheat_display_name}", getCheatDisplayName())
				.replace("{component}", component)
				.replace("{server}", server)
				.replace("{amount}", String.valueOf(amount))
				.replace("{ping}", String.valueOf(ping))
				.replace("{ping_format}", PingManager.getInstance().formatPing(ping, language))
				.replace("{tps}", TPSManager.getInstance().formatTPS(tps, language))
				.replace("{version}", version.format())
				.replace("{version_protocol}", String.valueOf(version.getProtocol()))
				.replace("{client_edition}", bedrockEdition ? "Bedrock" : "Java")
				.replace("{last_time}", Utils.formatTime(System.currentTimeMillis() - lastTime, language, false, true));
	}
	
	@Override
	public List<String> formatPlaceholders(List<String> input, Language language) {
		return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
	}
	
	public void updateData(String component, String server, int amount, int ping, double tps) {
		this.component = component;
		this.server = server;
		this.amount = amount;
		this.ping = ping;
		this.tps = tps;
		lastTime = System.currentTimeMillis();
	}
	
}
