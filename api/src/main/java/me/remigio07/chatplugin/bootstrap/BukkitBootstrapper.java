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
		Environment.currentEnvironment = Environment.BUKKIT;
		java.util.logging.Logger logger;
		
		try {
			logger = getLogger();
		} catch (NoSuchMethodError e) { // older Bukkit versions
			logger = java.util.logging.Logger.getLogger("ChatPlugin");
		} JARLibraryLoader.getInstance().initialize(logger, getDataFolder());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable() {
		ChatPlugin.getInstance().unload();
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
