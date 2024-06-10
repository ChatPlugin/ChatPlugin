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

package me.remigio07.chatplugin.server.bukkit.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.server.bukkit.integration.anticheat.MatrixIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.anticheat.VulcanIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.anticheat.negativity.NegativityIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.combatlog.CombatLogXIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.cosmetic.PlayerParticlesIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.cosmetic.UltraCosmeticsIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.cosmetic.gadgetsmenu.GadgetsMenuIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.economy.EssentialsXIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.economy.VaultIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.multiplatform.GeyserMCIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.permission.LuckPermsIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.placeholder.MVdWPlaceholderAPIIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.placeholder.PlaceholderAPIIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.region.WorldGuardIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.social.DiscordSRVIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.version.ProtocolSupportIntegration;
import me.remigio07.chatplugin.server.bukkit.integration.version.ViaVersionIntegration;

public class BukkitIntegrationManager extends IntegrationManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		Collection<ChatPluginIntegration> integrations = this.integrations.values();
		List<String> hooked = new ArrayList<>();
		
		putIntegration(new CombatLogXIntegration());
		putIntegration(new DiscordSRVIntegration());
		putIntegration(new EssentialsXIntegration());
		putIntegration(new GadgetsMenuIntegration());
		putIntegration(new GeyserMCIntegration());
		putIntegration(new LuckPermsIntegration());
		putIntegration(new MatrixIntegration());
		putIntegration(new MVdWPlaceholderAPIIntegration());
		putIntegration(new NegativityIntegration());
		putIntegration(new PlaceholderAPIIntegration());
		putIntegration(new PlayerParticlesIntegration());
		putIntegration(new ProtocolSupportIntegration());
		putIntegration(new UltraCosmeticsIntegration());
		putIntegration(new VaultIntegration());
		putIntegration(new ViaVersionIntegration());
		putIntegration(new VulcanIntegration());
		putIntegration(new WorldGuardIntegration());
		
		integrations.forEach(ChatPluginIntegration::load);
		integrations.stream().filter(ChatPluginIntegration::isEnabled).forEach(integration -> hooked.add(integration.getType().getPlugin()));
		
		enabled = true;
		
		if (hooked.size() > 0)
			LogManager.log("Hooked plugins: [" + String.join(", ", hooked.toArray(new String[0])) + "]; took " + (loadTime = System.currentTimeMillis() - ms) + "ms.", 4);
	}
	
}
