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

package me.remigio07.chatplugin.server.bukkit.integration.cosmetic;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.integration.cosmetics.CosmeticsIntegration;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.bukkit.integration.ChatPluginBukkitIntegration;

public class PlayerParticlesIntegration extends ChatPluginBukkitIntegration<CosmeticsIntegration> implements CosmeticsIntegration {
	
	public PlayerParticlesIntegration() {
		super(IntegrationType.PLAYERPARTICLES);
	}
	
	@Override
	public List<String> removeActiveCosmetics(ChatPluginServerPlayer player) {
		return PlayerParticlesAPI.getInstance().resetActivePlayerParticles(player.toAdapter().bukkitValue()) == 0 ? Collections.emptyList() : Arrays.asList("particles");
	}
	
}
