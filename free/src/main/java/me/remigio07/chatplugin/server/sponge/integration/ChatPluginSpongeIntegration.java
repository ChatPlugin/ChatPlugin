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

package me.remigio07.chatplugin.server.sponge.integration;

import org.spongepowered.api.Sponge;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.common.integration.BaseIntegration;

public abstract class ChatPluginSpongeIntegration<T extends ChatPluginIntegration> extends BaseIntegration<T> {
	
	public ChatPluginSpongeIntegration(IntegrationType<T> type) {
		super(type);
	}
	
	public void load() {
		enabled = false;
		
		try {
			Class.forName(type.getClazz());
			
			plugin = Sponge.getPluginManager().getPlugin(type.getPlugin()).get();
			enabled = true;
			
			loadAPI();
		} catch (ClassNotFoundException | NoClassDefFoundError e) {
			
		}
	}
	
}
