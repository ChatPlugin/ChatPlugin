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

package me.remigio07.chatplugin.api.proxy.util.manager;

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
	protected Map<String, ServerInformation> serversInformation = new ConcurrentHashMap<>();
	protected Map<String, Queue<PacketSerializer>> packetsQueue = new ConcurrentHashMap<>();
	protected long taskID, loadTime;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		taskID = TaskManager.scheduleAsync(() -> {
			for (String server : new HashSet<>(serversInformation.keySet()))
				if (serversInformation.get(server).getLastEdit() < System.currentTimeMillis() - 31000L) // max: 31s
					serversInformation.remove(server);
		}, 0L, 5000L);
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
	 * Gets a map containing information about servers under the network.
	 * 
	 * <p>The keys represent the servers' ID.</p>
	 * 
	 * @return Servers under the network
	 */
	public Map<String, ServerInformation> getServersInformation() {
		return serversInformation;
	}
	
	/**
	 * Gets the amount of online players in the specified server.
	 * 
	 * <p>Specify "ALL" to get the amount of online players under the proxy.
	 * Provided information may not be accurate when there are no players online.</p>
	 * 
	 * @param server Server to check
	 * @param hideVanished Whether to subtract {@link #getVanishedPlayers(String)} from the amount
	 * @return Online players amount
	 */
	public int getOnlinePlayers(String server, boolean hideVanished) {
		if (server.equals("ALL")) {
			int total = 0;
			
			for (ServerInformation info : serversInformation.values())
				total += (info.getOnlinePlayers() - (hideVanished ? info.getVanishedPlayers() : 0));
			return total;
		} return serversInformation.containsKey(server) ? serversInformation.get(server).getOnlinePlayers() - (hideVanished ? serversInformation.get(server).getVanishedPlayers() : 0) : 0;
	}
	
	/**
	 * Gets the amount of vanished players in the specified server.
	 * 
	 * <p>Specify "ALL" to get the amount of vanished players under the proxy.</p>
	 * 
	 * @param server Server to check
	 * @return Vanished players amount
	 */
	public int getVanishedPlayers(String server) {
		if (server.equals("ALL")) {
			int total = 0;
			
			for (ServerInformation info : serversInformation.values())
				total += info.getVanishedPlayers();
			return total;
		} return serversInformation.containsKey(server) ? serversInformation.get(server).getVanishedPlayers() : 0;
	}
	
	/**
	 * Translates "{online@server}" and "{vanished@server}" with
	 * the specified server's online and vanished players' amounts.
	 * 
	 * @param input Input containing placeholders
	 * @param hideVanished Whether to subtract {@link #getVanishedPlayers(String)} from the amount
	 * @return Translated placeholders
	 */
	public String formatOnlineAndVanishedPlaceholders(String input, boolean hideVanished) {
		for (ServerInformation info : serversInformation.values()) {
			input = input
					.replace("{online@" + info.getID() + "}", String.valueOf(info.getOnlinePlayers() - (hideVanished ? info.getVanishedPlayers() : 0)))
					.replace("{vanished@" + info.getID() + "}", String.valueOf(info.getVanishedPlayers()));
		} return input;
	}
	
	/**
	 * Gets the packets queued for sending.
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
	 * Sends a plugin message to the specified server.
	 * 
	 * <p>If the target is not reachable or has 0
	 * players online, the message will not be sent.</p>
	 * 
	 * @param server Target server, "ALL" for all servers
	 * @param packet Plugin message to send
	 * @throws IllegalStateException If {@link PacketSerializer#toArray()} fails
	 * @see PacketDeserializer
	 * @see Packets
	 */
	public abstract void sendPluginMessage(String server, PacketSerializer packet);
	
	/**
	 * Sends a plugin message to the specified server,
	 * even if the server has 0 players online.
	 * 
	 * <p>In that case the packet will be read as
	 * soon as a player will join that server.</p>
	 * 
	 * <p>You can specify a max timeout to wait before discarding
	 * the packet if the target server has 0 players online.
	 * Specify -1 to (potentially; until the proxy stops) wait for ever.</p>
	 * 
	 * @param server Target server, "ALL" for all servers
	 * @param packet Plugin message to send
	 * @param timeout Max timeout to wait, in milliseconds
	 * @throws IllegalStateException If {@link PacketSerializer#toArray()} fails
	 * @see PacketDeserializer
	 * @see Packets
	 */
	public abstract void sendOrQueuePluginMessage(String server, PacketSerializer packet, long timeout);
	
}
