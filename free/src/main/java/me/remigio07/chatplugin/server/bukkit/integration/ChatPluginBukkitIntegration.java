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

import org.bukkit.Bukkit;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.common.integration.BaseIntegration;

public abstract class ChatPluginBukkitIntegration<T extends ChatPluginIntegration> extends BaseIntegration<T> {
	
	public ChatPluginBukkitIntegration(IntegrationType<T> type) {
		super(type);
	}
	
	public void load() {
		try {
			Class.forName(type.getClazz());
			
			plugin = Bukkit.getServer().getPluginManager().getPlugin(type.getPlugin());
			enabled = true;
			
			loadAPI();
		} catch (ClassNotFoundException e) {
			
		}
	}
	
}
