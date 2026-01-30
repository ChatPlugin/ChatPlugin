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

import java.util.Date;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.google.common.collect.ImmutableMap;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

/**
 * Represents the Fabric's bootstrapper.
 */
public class FabricBootstrapper implements DedicatedServerModInitializer {
	
	private static FabricBootstrapper instance;
	private MinecraftServer server;
	
	/**
	 * Event called automatically on server startup.
	 */
	@Override
	public void onInitializeServer() {
		instance = this;
		Logger logger = Logger.getLogger("ChatPlugin");
		ConsoleHandler handler = new ConsoleHandler();
		Map<Level, String> logLevels = ImmutableMap.of(Level.INFO, "INFO", Level.WARNING, "WARN", Level.SEVERE, "ERROR");
		
		handler.setFormatter(new Formatter() {
			
			@Override
			public String format(LogRecord record) {
				return String.format("[%1$tT %2$s]: [ChatPlugin] %3$s%n", new Date(record.getMillis()), logLevels.get(record.getLevel()), record.getMessage());
			}
			
		});
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		ServerLifecycleEvents.SERVER_STARTING.register(server -> JARLibraryLoader.getInstance().open(
				Environment.currentEnvironment = Environment.FABRIC,
				logger,
				FabricLoader.getInstance().getConfigDir().resolve("chatplugin"),
				this.server = server
				));
	}
	
	/**
	 * Gets the current {@link MinecraftServer} instance.
	 * 
	 * @return Current server instance
	 */
	public MinecraftServer getServer() {
		return server;
	}
	
	/**
	 * Gets this Fabric plugin's instance.
	 * 
	 * @return Plugin's instance
	 */
	public static FabricBootstrapper getInstance() {
		return instance;
	}
	
}
