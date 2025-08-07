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

package me.remigio07.chatplugin.bootstrap;

import java.io.IOException;

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
		
		JARLibraryLoader.getInstance().open(getLogger(), getDataFolder().toPath());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable() {
		ChatPlugin.getInstance().unload();
		
		try {
			JARLibraryLoader.getInstance().close();
			IsolatedClassLoader.getInstance().close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
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
