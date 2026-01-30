/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

import org.bukkit.plugin.java.JavaPlugin;

import me.remigio07.chatplugin.api.ChatPlugin;

/**
 * Represents the Bukkit's bootstrapper.
 */
public class BukkitBootstrapper extends JavaPlugin {
	
	private static BukkitBootstrapper instance;
	
	@Override
	public void onEnable() {
		instance = this;
		java.util.logging.Logger logger;
		
		try {
			logger = getLogger();
		} catch (NoSuchMethodError nsme) { // older Bukkit versions
			logger = java.util.logging.Logger.getLogger("ChatPlugin");
		} JARLibraryLoader.getInstance().open(
				Environment.currentEnvironment = Environment.BUKKIT,
				logger,
				getDataFolder().toPath(),
				null
				);
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
	 * Gets this Bukkit plugin's instance.
	 * 
	 * @return Plugin's instance
	 */
	public static BukkitBootstrapper getInstance() {
		return instance;
	}
	
}
