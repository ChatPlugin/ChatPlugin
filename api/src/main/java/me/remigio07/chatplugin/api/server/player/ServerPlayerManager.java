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

package me.remigio07.chatplugin.api.server.player;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.multiplatform.MultiPlatformIntegration;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.server.event.player.ServerPlayerLoadEvent;
import me.remigio07.chatplugin.api.server.event.player.ServerPlayerUnloadEvent;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;

/**
 * Manager that handles {@link ChatPluginServerPlayer}s and enabled worlds.
 */
public abstract class ServerPlayerManager extends PlayerManager {
	
	protected static Map<UUID, Version> playersVersions = new ConcurrentHashMap<>();
	protected static Map<UUID, Long> playersLoginTimes = new ConcurrentHashMap<>();
	protected static List<UUID> bedrockPlayers = new CopyOnWriteArrayList<>();
	protected Map<UUID, ChatPluginServerPlayer> players = new ConcurrentHashMap<>();
	protected List<String> enabledWorlds = Collections.emptyList();
	protected int storageCount;
	
	@Override
	public void load() throws ChatPluginManagerException {
		try {
			storageCount = StorageConnector.getInstance().count(DataContainer.PLAYERS).intValue();
		} catch (SQLException sqle) {
			throw new ChatPluginManagerException(this, sqle);
		} if (IntegrationType.FLOODGATE.isEnabled()) {
			if ((floodgateUsernamePrefix = IntegrationType.FLOODGATE.get().getUsernamePrefix()).isEmpty())
				LogManager.log("Floodgate is installed but the username prefix set at \"username-prefix\" in its config.yml is empty: this is not recommended as ChatPlugin will not be able to distinguish Java players from Bedrock ones.", 1);
			else if (!Pattern.matches("^[^ \\w]$", floodgateUsernamePrefix))
				throw new ChatPluginManagerException(this, "invalid Floodgate username prefix ({0}) set at \"username-prefix\" in Floodgate's config.yml: it cannot be longer than 1 character and cannot be a letter, a number, a space or an underscore", floodgateUsernamePrefix);
		} else if (!Pattern.matches("^[^ \\w]?$", floodgateUsernamePrefix = ConfigurationType.CONFIG.get().getString("settings.floodgate-username-prefix")))
			throw new ChatPluginManagerException(this, "invalid Floodgate username prefix ({0}) set at \"settings.floodgate-username-prefix\" in config.yml: it cannot be longer than 1 character and cannot be a letter, a number, a space or an underscore", floodgateUsernamePrefix);
		super.load();
		
		enabledWorlds = new ArrayList<>(ConfigurationType.CONFIG.get().getStringList("settings.enabled-worlds"));
	}
	
	@Override
	public void unload() {
		enabled = false;
		
		players.keySet().forEach(this::unloadPlayer);
		players.clear();
		enabledWorlds.clear();
		super.unload();
		
		storageCount = 0;
	}
	
	/**
	 * Gets the loaded {@link ChatPluginServerPlayer}s' map.
	 * 
	 * <p>Do <em>not</em> modify the returned map.</p>
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
	 * @param checkPattern Whether to check the name against {@link #getUsernamePattern()}
	 * @param ignoreCase Whether to ignore case when checking online players
	 * @return Loaded {@link ChatPluginServerPlayer}
	 * @throws IllegalArgumentException If <code>checkPattern</code> and specified name <code>!{@link #isValidUsername(String)}</code>
	 */
	@Nullable(why = "Specified player may not be loaded")
	@Deprecated
	@Override
	public ChatPluginServerPlayer getPlayer(String name, boolean checkPattern, boolean ignoreCase) {
		if (checkPattern && !isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" does not respect the following pattern: \"" + usernamePattern.pattern() + "\"");
		for (ChatPluginServerPlayer player : players.values())
			if (ignoreCase ? player.getName().equalsIgnoreCase(name) : player.getName().equals(name))
				return player;
		return null;
	}
	
	/**
	 * Gets the list of the enabled worlds.
	 * 
	 * <p><strong>Found at:</strong> "settings.enabled-worlds" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return List of enabled worlds
	 * @see #isWorldEnabled(String)
	 */
	public List<String> getEnabledWorlds() {
		return enabledWorlds;
	}
	
	/**
	 * Checks if a world is contained in the enabled worlds' list.
	 * 
	 * <p>
	 * <strong>Note:</strong> do not use this method to check if players are loaded.
	 * Players may be loaded in disabled worlds and unloaded in enabled worlds. Use
	 * {@link #getPlayer(UUID)} to check whether a player is loaded or not.
	 * </p>
	 * 
	 * @param world Name of the world to check
	 * @return Whether the world is enabled
	 */
	public boolean isWorldEnabled(String world) {
		return enabledWorlds.contains("*") || enabledWorlds.contains(world) || ConfigurationType.CONFIG.get().getBoolean("settings.enable-every-world"); // compatibility with older ChatPlugin versions
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
	
	protected void verifyAndRun(Runnable runnable) {
		if (ChatPlugin.getState() == ChatPluginState.RELOADING || ChatPlugin.getState() == ChatPluginState.UNLOADING)
			runnable.run();
		else TaskManager.runAsync(runnable, 0L);
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
	 * through a {@link MultiPlatformIntegration}.
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
