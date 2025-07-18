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

package me.remigio07.chatplugin.server.bukkit.integration.placeholder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.integration.placeholder.PlaceholderIntegration;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

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
		try {
			return PlaceholderAPI.setPlaceholders(player.toAdapter().bukkitValue(), input);
		} catch (Throwable t) {
			String message = t.getLocalizedMessage();
			
			LogManager.log("{0} occurred while translating placeholders using PlaceholderAPI{1}.", 2, t.getClass().getSimpleName(), message == null ? "" : (": " + message));
			return "§c" + (message == null ? t.getClass().getSimpleName() : (t.getClass().getSimpleName() + ": " + message)) + "§r";
		}
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
			return "Remigio07";
		}
		
		@Override
		public boolean persist() {
			return true;
		}
		
		@Override
		public List<String> getPlaceholders() {
			return Stream.concat(
					Stream.of(PlaceholderType.PLAYER.getPlaceholders()),
					Stream.of(PlaceholderType.SERVER.getPlaceholders())
					).collect(Collectors.toList());
		}
		
		@Override
		public String onPlaceholderRequest(Player player, String identifier) {
			return onRequest(player, identifier);
		}
		
		@Override
		public String onRequest(OfflinePlayer offlinePlayer, String identifier) {
			if (offlinePlayer == null)
				return PlaceholderManager.getInstance().translateServerPlaceholders('{' + identifier + '}', Language.getMainLanguage());
			ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(offlinePlayer.getUniqueId());
			return player == null ? "§f" + (offlinePlayer.getName() == null ? offlinePlayer.getUniqueId().toString() : offlinePlayer.getName()) + " §cis not loaded.§r" : PlaceholderManager.getInstance().translatePlaceholders('{' + identifier + '}', player, Arrays.asList(PlaceholderType.getPlaceholderType(identifier)));
		}
		
	}
	
}
