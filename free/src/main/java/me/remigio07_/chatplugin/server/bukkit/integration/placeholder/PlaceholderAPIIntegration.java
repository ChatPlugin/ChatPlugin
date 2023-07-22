/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.server.bukkit.integration.placeholder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.remigio07_.chatplugin.api.ChatPlugin;
import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.common.player.PlayerManager;
import me.remigio07_.chatplugin.api.server.integration.placeholder.PlaceholderIntegration;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.PlaceholderType;
import me.remigio07_.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07_.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class PlaceholderAPIIntegration extends ChatPluginBukkitIntegration<PlaceholderIntegration> implements PlaceholderIntegration {
	
	public PlaceholderAPIIntegration() {
		super(IntegrationType.PLACEHOLDERAPI);
	}
	
	@Override
	protected void loadAPI() {
		new Expansion().register();
	}
	
	@Override
	public String translatePlaceholders(String input, ChatPluginServerPlayer player) {
		return PlaceholderAPI.setPlaceholders(player.toAdapter().bukkitValue(), input);
	}
	
	public class Expansion extends PlaceholderExpansion {
		
		@Override
		public String getIdentifier() {
			return "chatplugin";
		}
		
		@Override
		public String getName() {
			return "ChatPlugin";
		}
		
		@Override
		public String getVersion() {
			return ChatPlugin.VERSION;
		}
		
		@Override
		public String getAuthor() {
			return "Remigio07_";
		}
		
		@Override
		public List<String> getPlaceholders() {
			List<String> placeholders = new ArrayList<>(Arrays.asList(PlaceholderType.PLAYER.getPlaceholders()));
			
			placeholders.addAll(Arrays.asList(PlaceholderType.SERVER.getPlaceholders()));
			return placeholders;
		}
		
		@Override
		public String onPlaceholderRequest(Player player, String identifier) {
			return onRequest(player, identifier);
		}
		
		@Override
		public String onRequest(org.bukkit.OfflinePlayer offlinePlayer, String identifier) {
			ChatPluginServerPlayer player = (ChatPluginServerPlayer) PlayerManager.getInstance().getPlayer(offlinePlayer.getUniqueId());
			
			return player == null ? "\u00A7f" + offlinePlayer.getName() + " \u00A7cis not loaded." : PlaceholderManager.getInstance().translatePlaceholders("{" + identifier + "}", player, Arrays.asList(PlaceholderType.getPlaceholderType(identifier)));
		}
		
	}
	
}
