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

package me.remigio07.chatplugin.api.proxy.motd;

import java.net.InetAddress;
import java.net.URL;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import me.remigio07.chatplugin.api.common.motd.MoTD;
import me.remigio07.chatplugin.api.common.motd.MoTDManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.adapter.motd.FaviconAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.proxy.util.socket.Server;

/**
 * Proxy abstraction of the {@link MoTDManager}.
 */
public abstract class ProxyMoTDManager extends MoTDManager {
	
	protected boolean useLoopbackAddress;
	protected int serverSocketPort;
	protected String providerServerID, serverUnreachableDescription;
	protected URL serverUnreachableIconURL;
	protected Map<InetAddress, Queue<CompletableFuture<MoTD>>> pendingFutures = new ConcurrentHashMap<>();
	protected Server server;
	protected FaviconAdapter serverUnreachableFavicon;
	
	/**
	 * Checks if the server socket's address should be
	 * {@link InetAddress#getLoopbackAddress()}
	 * instead of {@link InetAddress#getLocalHost()}.
	 * 
	 * <p><strong>Found at:</strong> "motd.use-loopback-address" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to use the loopback address
	 */
	public boolean isUseLoopbackAddress() {
		return useLoopbackAddress;
	}
	
	/**
	 * Gets the port number used by the server socket
	 * to make MoTD requests to the provider server.
	 * 
	 * <p><strong>Found at:</strong> "motd.server-socket-port" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Server socket's port
	 */
	public int getServerSocketPort() {
		return serverSocketPort;
	}
	
	/**
	 * Gets the MoTD provider server's ID.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "motd.provider-server-id" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Provider server's ID
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public String getProviderServerID() {
		return providerServerID;
	}
	
	/**
	 * Gets the description displayed to players when the MoTD
	 * provider server is not reachable within 5 seconds.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "motd.server-unreachable.description" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Server unreachable description
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public String getServerUnreachableDescription() {
		return serverUnreachableDescription;
	}
	
	/**
	 * Gets the URL of the icon displayed to players when the
	 * MoTD provider server is not reachable within 5 seconds.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "motd.server-unreachable.url" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Server unreachable icon's URL
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public URL getServerUnreachableIconURL() {
		return serverUnreachableIconURL;
	}
	
	/**
	 * Gets the pending futures' map.
	 * 
	 * <p>Every entry is composed of a {@link InetAddress} which represents a connection
	 * and a {@link Queue} of {@link CompletableFuture}s holding a {@link MoTD}.</p>
	 * 
	 * @deprecated Internal use only.
	 * @return Pending futures' map
	 */
	@Deprecated
	public Map<InetAddress, Queue<CompletableFuture<MoTD>>> getPendingFutures() {
		return pendingFutures;
	}
	
	/**
	 * Gets the MoTD packet server socket.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * @return MoTD's server
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public Server getServer() {
		return server;
	}
	
	/**
	 * Gets the favicon displayed to players when the
	 * MoTD provider server is not reachable within 5 seconds.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * @return Server unreachable's favicon
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public FaviconAdapter getServerUnreachableFavicon() {
		return serverUnreachableFavicon;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ProxyMoTDManager getInstance() {
		return (ProxyMoTDManager) instance;
	}
	
}
