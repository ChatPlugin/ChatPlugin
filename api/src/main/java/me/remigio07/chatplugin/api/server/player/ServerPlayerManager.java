/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.api.server.player;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.server.event.player.ServerPlayerLoadEvent;
import me.remigio07.chatplugin.api.server.event.player.ServerPlayerUnloadEvent;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;

/**
 * Manager that handles {@link ChatPluginServerPlayer}s and enabled worlds.
 */
public abstract class ServerPlayerManager extends PlayerManager {
	
	private static Map<UUID, Version> playersVersions = new ConcurrentHashMap<>();
	private static Map<UUID, Long> playersLoginTimes = new ConcurrentHashMap<>();
	private static List<UUID> bedrockPlayers = new CopyOnWriteArrayList<>();
	protected Map<UUID, ChatPluginServerPlayer> players = new ConcurrentHashMap<>();
	protected List<String> enabledWorlds = new ArrayList<>();
	protected int storageCount;
	
	@Override
	public void load() throws ChatPluginManagerException {
		try {
			storageCount = StorageConnector.getInstance().count(DataContainer.PLAYERS).intValue();
		} catch (SQLException e) {
			throw new ChatPluginManagerException(this, e);
		} if (ConfigurationType.CONFIG.get().getStringList("settings.enabled-worlds").contains("*")
				|| ConfigurationType.CONFIG.get().getBoolean("settings.enable-every-world")) // compatibility with older ChatPlugin versions
			enabledWorlds = Utils.getWorlds();
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		players.keySet().forEach(this::unloadPlayer);
		players.clear();
		enabledWorlds.clear();
		
		storageCount = 0;
	}
	
	/**
	 * Gets the loaded {@link ChatPluginServerPlayer}s' map.
	 * 
	 * <p>Do <strong>not</strong> modify the returned map.</p>
	 * 
	 * @return Loaded players' map
	 */
	@Override
	public Map<UUID, ChatPluginServerPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Gets the list of loaded {@link ChatPluginServerPlayer}s with the specified IP address.
	 * 
	 * @param ipAddress IP address to check
	 * @return Loaded players' map
	 */
	@Override
	public List<ChatPluginServerPlayer> getPlayers(InetAddress ipAddress) {
		return players.values().stream().filter(player -> player.getIPAddress().equals(ipAddress)).collect(Collectors.toList());
	}
	
	/**
	 * Gets a player from {@link #getPlayers()} by their UUID.
	 * 
	 * <p>Will return <code>null</code> if the player is not loaded.</p>
	 * 
	 * @param uuid Player to get
	 * @return Loaded {@link ChatPluginServerPlayer}
	 */
	@Nullable(why = "Specified player may not be loaded")
	@Override
	public ChatPluginServerPlayer getPlayer(UUID uuid) {
		return players.get(uuid);
	}
	
	/**
	 * Gets a player from {@link #getPlayers()} by their name.
	 * 
	 * <p>Will return <code>null</code> if the player is not loaded.</p>
	 * 
	 * @deprecated Names should not be used to identify players. Use {@link #getPlayer(UUID)} instead.
	 * @param name Player to get
	 * @param checkPattern Whether to check the name against {@link Utils#USERNAME_PATTERN}
	 * @param ignoreCase Whether to ignore case when checking online players
	 * @return Loaded {@link ChatPluginServerPlayer}
	 * @throws IllegalArgumentException If <code>checkPattern</code> and specified name <code>!{@link Utils#isValidUsername(String)}</code>
	 */
	@Nullable(why = "Specified player may not be loaded")
	@Deprecated
	@Override
	public ChatPluginServerPlayer getPlayer(String name, boolean checkPattern, boolean ignoreCase) {
		if (checkPattern && !Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		for (ChatPluginServerPlayer player : players.values())
			if (ignoreCase ? player.getName().equalsIgnoreCase(name) : player.getName().equals(name))
				return player;
		return null;
	}
	
	/**
	 * Gets the list of the enabled worlds.
	 * 
	 * <p>Every feature of this plugin only
	 * applies to players in enabled worlds.</p>
	 * 
	 * <p><strong>Found at:</strong> "settings.enabled-worlds" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return List of enabled worlds
	 */
	public List<String> getEnabledWorlds() {
		return enabledWorlds;
	}
	
	/**
	 * Checks if a world is contained in the enabled worlds' list.
	 * 
	 * @param world Name of the world to check
	 * @return Whether the world is enabled
	 */
	public boolean isWorldEnabled(String world) {
		return enabledWorlds.contains(world);
	}
	
	/**
	 * Gets the total amount of players stored in the storage.
	 * 
	 * <p>This value is only used to translate <code>{total_players}</code>.</p>
	 * 
	 * @return Amount of players
	 */
	public int getStorageCount() {
		return storageCount;
	}
	
	/**
	 * Sets the amount of players stored in the storage.
	 * 
	 * <p>This value is only used to translate <code>{total_players}</code>.</p>
	 * 
	 * @param storageCount Amount of players
	 */
	public void setStorageCount(int storageCount) {
		this.storageCount = storageCount;
	}
	
	/**
	 * Gets the online players' versions.
	 * 
	 * @return Online players' versions
	 */
	public static Map<UUID, Version> getPlayersVersions() {
		return playersVersions;
	}
	
	/**
	 * Gets an online player's version.
	 * 
	 * <p>Will return <code>null</code> if they are not online.</p>
	 * 
	 * @param player Player's UUID
	 * @return Player's version
	 */
	@Nullable(why = "Player may not be online")
	public static Version getPlayerVersion(UUID player) {
		return playersVersions.get(player);
	}
	
	/**
	 * Gets the online players' login times.
	 * 
	 * @return Online players' login times
	 */
	public static Map<UUID, Long> getPlayersLoginTimes() {
		return playersLoginTimes;
	}
	
	/**
	 * Gets an online player's login time.
	 * 
	 * <p>Will return <code>null</code> if they are not online.</p>
	 * 
	 * @param player Player's UUID
	 * @return Player's login time
	 */
	@Nullable(why = "Player may not be online")
	public static Long getPlayerLoginTime(UUID player) {
		return playersLoginTimes.get(player);
	}
	
	/**
	 * Gets the online Bedrock players' UUIDs.
	 * 
	 * @return Online Bedrock players' UUIDs
	 */
	public static List<UUID> getBedrockPlayers() {
		return bedrockPlayers;
	}
	
	/**
	 * Checks if the specified player is connected
	 * through {@link IntegrationType#GEYSERMC}.
	 * 
	 * @param player Player's UUID
	 * @return Whether the player is using the BE
	 */
	public static boolean isBedrockPlayer(UUID player) {
		return bedrockPlayers.contains(player);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ServerPlayerManager getInstance() {
		return (ServerPlayerManager) instance;
	}
	
	/**
	 * Adds a player to {@link #getPlayers()}.
	 * 
	 * <p>Will do nothing and return 0 if they are already loaded.</p>
	 * 
	 * @param player Player to load
	 * @return Time elapsed, in milliseconds
	 * @throws IllegalStateException If {@link ProxyManager#isEnabled()} and this server has not received a
	 * <code>PlayerJoin</code> packet from the proxy containing information about the specified player's version
	 * @see ServerPlayerLoadEvent
	 * @see Packets.JoinQuit#playerJoin(String, UUID, int, boolean, boolean, boolean)
	 */
	public abstract int loadPlayer(PlayerAdapter player);
	
	/**
	 * Removes a player from {@link #getPlayers()}.
	 * 
	 * <p>Will do nothing and return 0 if they are not loaded.</p>
	 * 
	 * @param player Player to unload
	 * @return Time elapsed, in milliseconds
	 * @see ServerPlayerUnloadEvent
	 */
	public abstract int unloadPlayer(UUID player);
	
}
