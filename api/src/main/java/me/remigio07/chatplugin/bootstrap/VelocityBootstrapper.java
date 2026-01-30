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

import java.nio.file.Path;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import me.remigio07.chatplugin.api.ChatPlugin;

/**
 * Represents the Velocity's bootstrapper.
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
						id = "ViaVersion",
						optional = true
						),
				@Dependency(
						id = "Geyser-Velocity",
						optional = true
						)
		})
public class VelocityBootstrapper {
	
	private static VelocityBootstrapper instance;
	private ProxyServer proxy;
	private Logger logger;
	private Path dataFolder;
	
	/**
	 * Constructor called automatically on proxy startup.
	 * 
	 * @param proxy Current proxy instance
	 * @param logger ChatPlugin's logger
	 * @param dataFolder ChatPlugin's data folder
	 */
	@Inject
	public VelocityBootstrapper(ProxyServer proxy, Logger logger, @DataDirectory Path dataFolder) {
		instance = this;
		this.proxy = proxy;
		this.logger = logger;
		this.dataFolder = dataFolder;
	}
	
	/**
	 * Event called automatically on proxy startup.
	 * 
	 * @param event Startup event
	 */
	@Subscribe
	public void onProxyInitialize(ProxyInitializeEvent event) {
		JARLibraryLoader.getInstance().open(
				Environment.currentEnvironment = Environment.VELOCITY,
				logger,
				dataFolder,
				proxy
				);
	}
	
	/**
	 * Gets the current {@link ProxyServer} instance.
	 * 
	 * @return Current proxy instance
	 */
	public ProxyServer getProxy() {
		return proxy;
	}
	
	/**
	 * Gets this Velocity plugin's instance.
	 * 
	 * @return Plugin's instance
	 */
	public static VelocityBootstrapper getInstance() {
		return instance;
	}
	
}
