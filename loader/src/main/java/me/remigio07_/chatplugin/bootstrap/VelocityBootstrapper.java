/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.bootstrap;

import java.nio.file.Path;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

/**
 * Represents the Velocity's bootstrapper.
 */
@Plugin(
		id = "chatplugin",
		name = "ChatPlugin",
		version = "${version}",
		url = "https://megaproserver.com/chatplugin",
		description = "A lightweight yet complete plugin which handles just too many features! Check the wiki for info: https://github.com/Remigio07/ChatPlugin",
		authors = "Remigio07_",
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
	 * Constructor automatically called on proxy startup.
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
	 * Event automatically called on proxy startup.
	 * 
	 * @param event Startup event
	 */
	@Subscribe
	@SuppressWarnings("deprecation")
	public void onProxyInitialize(ProxyInitializeEvent event) {
		Environment.setCurrent(Environment.VELOCITY);
		JARLibraryLoader.getInstance().initialize(proxy, logger, dataFolder);
		proxy.getEventManager().register(this, new VelocityEventsAdapter());
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
	
	private static class VelocityEventsAdapter {
		
		@Subscribe
		public void onProxyShutdown(ProxyShutdownEvent event) {
			JARLibraryLoader.getInstance().disable();
		}
		
		@Subscribe
		public void onPluginMessage(PluginMessageEvent event) {
			adapt(event);
		}
		
		@Subscribe
		public void onPostLogin(PostLoginEvent event) {
			adapt(event);
		}
		
		@Subscribe
		public void onServerPreConnect(ServerPreConnectEvent event) {
			adapt(event);
		}
		
		@Subscribe
		public void onServerConnected(ServerConnectedEvent event) {
			adapt(event);
		}
		
		@Subscribe
		public void onServerPostConnect(ServerPostConnectEvent event) {
			adapt(event);
		}
		
		@Subscribe
		public void onDisconnect(DisconnectEvent event) {
			adapt(event);
		}
		
		@Subscribe
		public void onProxyPing(ProxyPingEvent event) {
			adapt(event);
		}
		
		private static void adapt(Object event) {
			try {
				Class.forName("me.remigio07_.chatplugin.ChatPluginPremiumImpl$VelocityEventsAdapter", false, JARLibraryLoader.getInstance()).getMethod("on" + event.getClass().getSimpleName().substring(0, event.getClass().getSimpleName().indexOf("Event")), event.getClass()).invoke(null, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
