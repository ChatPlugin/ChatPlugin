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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.util.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ServerInformation;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.packet.PacketDeserializer;
import me.remigio07.chatplugin.api.common.util.packet.PacketSerializer;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles connections and integrations with the proxy.
 */
public abstract class ProxyManager implements ChatPluginManager {
	
	/**
	 * String containing the default server ID.
	 * 
	 * <p><strong>Content:</strong> "set-as-server-id-in-proxy-config"</p>
	 */
	public static final String DEFAULT_SERVER_ID = "set-as-server-id-in-proxy-config";
	
	/**
	 * Pattern representing the allowed server IDs.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,36}$"</p>
	 * 
	 * @see #isValidServerID(String)
	 */
	public static final Pattern SERVER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,36}$");
	protected static ProxyManager instance;
	protected boolean enabled;
	protected String serverID, serverDisplayName;
	protected List<String> logFilteredPackets = Collections.emptyList();
	protected Map<String, ServerInformation> serversInformation = new HashMap<>();
	protected long[] taskIDs = new long[2];
	protected long loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets this server's ID.
	 * 
	 * <p><strong>Found at:</strong> "multi-instance-mode.server-id" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return This server's ID
	 */
	public String getServerID() {
		return serverID;
	}
	
	/**
	 * Gets this server's display name.
	 * 
	 * <p><strong>Found at:</strong> "multi-instance-mode.server-display-name" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return This server's display name
	 */
	public String getServerDisplayName() {
		return serverDisplayName;
	}
	
	/**
	 * Gets the IDs of the packets filtered by the {@link LogManager}.
	 * 
	 * <p><strong>Found at:</strong> "multi-instance-mode.packets-logging.filter" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Log filtered packets' IDs
	 */
	public List<String> getLogFilteredPackets() {
		return logFilteredPackets;
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
	 * Gets a list containing the IDs
	 * of the servers under the network.
	 * 
	 * @return IDs of servers under the network
	 */
	public List<String> getServersIDs() {
		return new ArrayList<>(serversInformation.keySet());
	}
	
	/**
	 * Gets the amount of online players in the specified server.
	 * Specify "ALL" to get the amount of online players under the proxy.
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
	 * Specify "ALL" to get the amount of vanished players under the proxy.
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
	 * Checks if the specified String is a valid server ID.
	 * Will return <code>false</code> if you specify "proxy".
	 * 
	 * @param serverID Server ID to check
	 * @return Whether the specified server ID is valid
	 * @see #SERVER_ID_PATTERN
	 */
	public boolean isValidServerID(String serverID) {
		return serverID.equalsIgnoreCase("proxy") ? false : SERVER_ID_PATTERN.matcher(serverID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ProxyManager getInstance() {
		return instance;
	}
	
	/**
	 * Sends a plugin message to the proxy.
	 * Will do nothing if no players are online.
	 * 
	 * @param packet Plugin message to send
	 * @see PacketDeserializer
	 * @see Packets
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void sendPluginMessage(PacketSerializer packet) {
		throw new UnsupportedOperationException("Unable to send a plugin message on the free version");
	}
	
	/**
	 * Receives a plugin message.
	 * 
	 * @param message Plugin message to receive
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void receivePluginMessage(byte[] message) {
		throw new UnsupportedOperationException("Unable to receive a plugin message on the free version");
	}
	
	/**
	 * Connects the specified player to the given server.
	 * 
	 * @param player Player to connect
	 * @param server Server to connect the player to
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void connect(ChatPluginServerPlayer player, String server) {
		throw new UnsupportedOperationException("Unable to connect a player to another server on the free version");
	}
	
}
