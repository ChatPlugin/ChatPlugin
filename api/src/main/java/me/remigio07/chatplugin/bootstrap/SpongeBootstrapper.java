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
import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;

import com.google.inject.Inject;

import me.remigio07.chatplugin.api.ChatPlugin;

/**
 * Represents the Sponge's bootstrapper.
 */
@Plugin(
		id = "chatplugin",
		name = "ChatPlugin",
		version = ChatPlugin.VERSION,
		url = "https://remigio07.me/chatplugin",
		description = "A feature-rich and modular chat ecosystem, lightweight and efficient by design.",
		authors = "Remigio07",
		dependencies = {
				@Dependency(
						id = "negativity",
						version = "[1.9.0,)",
						optional = true
						),
				@Dependency(
						id = "viaversion",
						version = "[4.0.0,)",
						optional = true
						)
		})
public class SpongeBootstrapper {
	
	private static SpongeBootstrapper instance;
	@Inject
	private Logger logger;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path dataFolder;
	
	/**
	 * Event called automatically on server startup.
	 * 
	 * @param event Startup event
	 */
	@Listener
	public void onGameStartedServer(GameStartedServerEvent event) {
		instance = this;
		
		JARLibraryLoader.getInstance().open(
				Environment.currentEnvironment = Environment.SPONGE,
				logger,
				dataFolder,
				null
				);
	}
	
	/**
	 * Event called automatically on server shutdown.
	 * 
	 * @param event Shutdown event
	 */
	@SuppressWarnings("deprecation")
	@Listener
	public void onGameStoppedServer(GameStoppedServerEvent event) { // should we hide this like Velocity does?
		ChatPlugin.getInstance().unload();
		
		try {
			JARLibraryLoader.getInstance().close();
			IsolatedClassLoader.getInstance().close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Gets this Sponge plugin's instance.
	 * 
	 * @return Plugin's instance
	 */
	public static SpongeBootstrapper getInstance() {
		return instance;
	}
	
}
