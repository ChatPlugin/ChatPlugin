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

package me.remigio07.chatplugin.server.sponge.integration.anticheat.negativity;

import java.lang.reflect.InvocationTargetException;

import com.elikill58.negativity.api.NegativityPlayer;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.sponge.integration.ChatPluginSpongeIntegration;

public class NegativityIntegration extends ChatPluginSpongeIntegration<AnticheatIntegration> implements AnticheatIntegration {
	
	public NegativityIntegration() {
		super(IntegrationType.NEGATIVITY);
	}
	
	@Override
	protected void loadAPI() {
		try {
			Class.forName("com.elikill58.negativity.api.events.EventManager").getMethod("registerEvent", Class.forName("com.elikill58.negativity.api.events.Listeners")).invoke(null, Class.forName("me.remigio07.chatplugin.server.sponge.integration.anticheat.negativity.NegativityListener").getDeclaredConstructor().newInstance());
		} catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public int getViolations(ChatPluginServerPlayer player, String cheatID) {
		return (int) NegativityPlayer.getCached(player.getUUID()).getAccount().getWarn(cheatID);
	}
	
}
