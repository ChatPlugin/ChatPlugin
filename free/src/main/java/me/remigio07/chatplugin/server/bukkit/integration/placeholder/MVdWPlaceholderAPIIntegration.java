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

package me.remigio07.chatplugin.server.bukkit.integration.placeholder;

import java.util.Arrays;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.server.integration.placeholder.PlaceholderIntegration;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class MVdWPlaceholderAPIIntegration extends ChatPluginBukkitIntegration<PlaceholderIntegration> implements PlaceholderIntegration {
	
	public MVdWPlaceholderAPIIntegration() {
		super(IntegrationType.MVDWPLACEHOLDERAPI);
	}
	
	@Override
	protected void loadAPI() {
		new Expansion().register();
	}
	
	@Override
	public String translatePlaceholders(String input, ChatPluginServerPlayer player) {
		try {
			return PlaceholderAPI.replacePlaceholders(player.toAdapter().bukkitValue(), input);
		} catch (Throwable t) {
			String message = t.getLocalizedMessage();
			return "\u00A7c" + (message == null ? t.getClass().getSimpleName() : (t.getClass().getSimpleName() + ": " + message)) + "\u00A7r";
		}
	}
	
	public class Expansion {
		
		public void register() {
			PlaceholderAPI.registerPlaceholder(BukkitBootstrapper.getInstance(), "chatplugin", new PlaceholderReplacer() {
				
				@Override
				public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
					ChatPluginServerPlayer player = (ChatPluginServerPlayer) PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId());
					return player == null ? "\u00A7f" + event.getPlayer().getName() + " \u00A7cis not loaded." : event.isOnline() ? PlaceholderManager.getInstance().translatePlaceholders("{" + event.getPlaceholder() + "}", player, Arrays.asList(PlaceholderType.getPlaceholderType(event.getPlaceholder()))) : "\u00A7f" + player.getName() + " \u00A7cis offline.";
				}
			});
		}
		
	}
	
}
