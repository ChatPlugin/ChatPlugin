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

package me.remigio07.chatplugin.server.bukkit.integration.social;

import github.scarsz.discordsrv.DiscordSRV;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.integration.social.SocialIntegration;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class DiscordSRVIntegration extends ChatPluginBukkitIntegration<SocialIntegration> implements SocialIntegration {
	
	public DiscordSRVIntegration() {
		super(IntegrationType.DISCORDSRV);
	}
	
	@Override
	public void handleChatEvent(ChatPluginServerPlayer player, String message) {
		DiscordSRV.getPlugin().processChatMessage(player.toAdapter().bukkitValue(), message, DiscordSRV.getPlugin().getOptionalChannel("global"), false, null);
	}
	
}
