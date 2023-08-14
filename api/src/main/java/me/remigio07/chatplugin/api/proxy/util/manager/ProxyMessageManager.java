/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
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

package me.remigio07.chatplugin.api.proxy.util.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import me.remigio07.chatplugin.api.common.util.ServerInformation;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.packet.PacketDeserializer;
import me.remigio07.chatplugin.api.common.util.packet.PacketSerializer;
import me.remigio07.chatplugin.api.common.util.packet.Packets;

/**
 * Manager used to send plugin messages to the servers under the proxy.
 */
public abstract class ProxyMessageManager implements ChatPluginManager {
	
	protected static ProxyMessageManager instance;
	protected boolean enabled;
	protected Map<String, ServerInformation> serversInformation = new HashMap<>();
	protected Map<String, Queue<PacketSerializer>> packetsQueue = new ConcurrentHashMap<>();
	protected long taskID, loadTime;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		taskID = TaskManager.scheduleAsync(() -> {
			for (String server : new HashSet<>(serversInformation.keySet()))
				if (serversInformation.get(server).getLastEdit() < System.currentTimeMillis() - 305000L) // max: 5m,5s
					serversInformation.remove(server);
		}, 0L, 30000L);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(taskID);
		serversInformation.clear();
		packetsQueue.clear();
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public boolean isReloadable() {
		return false;
	}
	
	/**
	 * Gets a map containing information
	 * about servers under the network.
	 * The keys represent the servers' ID.
	 * 
	 * @return Servers under the network
	 */
	public Map<String, ServerInformation> getServersInformation() {
		return serversInformation;
	}
	
	/**
	 * Gets the packets queued for sending.
	 * Do not modify the returned map.
	 * 
	 * @deprecated Internal use only.
	 * @return Packets' queue
	 */
	@Deprecated
	public Map<String, Queue<PacketSerializer>> getPacketsQueue() {
		return packetsQueue;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ProxyMessageManager getInstance() {
		return instance;
	}
	
	/**
	 * Sends a plugin message to the specified server. If the target is
	 * not reachable or has 0 players online, the message will not be sent.
	 * 
	 * @param server Target server, "ALL" for all servers
	 * @param packet Plugin message to send
	 * @see PacketDeserializer
	 * @see Packets
	 */
	public abstract void sendPluginMessage(String server, PacketSerializer packet);
	
	/**
	 * Sends a plugin message to the specified server, even if the
	 * server has 0 players online. In that case the packet will
	 * be read as soon as a player will join that server.
	 * 
	 * <p>You can specify a max timeout to wait before discarding
	 * the packet if the target server has 0 players online.
	 * Specify -1 to (potentially; until the proxy stops) wait for ever.</p>
	 * 
	 * @param server Target server, "ALL" for all servers
	 * @param packet Plugin message to send
	 * @param timeout Max timeout to wait, in milliseconds
	 * @see PacketDeserializer
	 * @see Packets
	 */
	public abstract void sendOrQueuePluginMessage(String server, PacketSerializer packet, long timeout);
	
}
