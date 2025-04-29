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

package me.remigio07.chatplugin.server.sponge.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.server.sponge.integration.anticheat.negativity.NegativityIntegration;
import me.remigio07.chatplugin.server.sponge.integration.permission.LuckPermsIntegration;
import me.remigio07.chatplugin.server.sponge.integration.version.ViaVersionIntegration;

public class SpongeIntegrationManager extends IntegrationManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		Collection<ChatPluginIntegration> integrations = this.integrations.values();
		List<String> hooked = new ArrayList<>();
		
		putIntegration(new LuckPermsIntegration());
		putIntegration(new NegativityIntegration());
		putIntegration(new ViaVersionIntegration());
		
		integrations.forEach(ChatPluginIntegration::load);
		integrations.stream().filter(ChatPluginIntegration::isEnabled).forEach(integration -> hooked.add(integration.getType().getPlugin()));
		
		enabled = true;
		
		if (hooked.size() > 0)
			LogManager.log("Hooked plugins: [" + String.join(", ", hooked.toArray(new String[0])) + "]; took " + (loadTime = System.currentTimeMillis() - ms) + "ms.", 4);
	}
	
}
