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

package me.remigio07.chatplugin.bootstrap;

import me.remigio07.chatplugin.api.ChatPlugin;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Represents the BungeeCord's bootstrapper.
 */
public class BungeeCordBootstrapper extends Plugin {
	
	private static BungeeCordBootstrapper instance;
	
	@Override
	public void onEnable() {
		instance = this;
		Environment.currentEnvironment = Environment.BUNGEECORD;
		
		JARLibraryLoader.getInstance().initialize(getLogger(), getDataFolder());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable() {
		ChatPlugin.getInstance().unload();
	}
	
	/**
	 * Gets this BungeeCord plugin's instance.
	 * 
	 * @return Plugin's instance
	 */
	public static BungeeCordBootstrapper getInstance() {
		return instance;
	}
	
}
