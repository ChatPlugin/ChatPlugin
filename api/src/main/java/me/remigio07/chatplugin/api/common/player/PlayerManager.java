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

package me.remigio07.chatplugin.api.common.player;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.integration.multiplatform.MultiPlatformIntegration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.proxy.player.ProxyPlayerManager;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;

/**
 * Manager that handles {@link ChatPluginPlayer}s and {@link OfflinePlayer}s.
 * 
 * @see ServerPlayerManager
 * @see ProxyPlayerManager
 * @see PlayerAdapter
 */
public abstract class PlayerManager implements ChatPluginManager {
	
	protected static PlayerManager instance;
	protected boolean enabled;
	protected String floodgateUsernamePrefix;
	protected Pattern usernamePattern;
	protected TimeZone displayedTimeZone;
	protected long loadTime;
	
	@Override
	public void load() throws ChatPluginManagerException {
		usernamePattern = Pattern.compile("^(?=.{2,16}$)(?:" + Pattern.quote(floodgateUsernamePrefix) + "\\w{1," + (16 - floodgateUsernamePrefix.length()) + "}|\\w{2,})$");
		String tz = ConfigurationType.CONFIG.get().getString("settings.displayed-time-zone");
		displayedTimeZone = tz.isEmpty() ? TimeZone.getDefault() : TimeZone.getTimeZone(tz); // TODO: add validation
	}
	
	@Override
	public void unload() {
		floodgateUsernamePrefix = null;
		usernamePattern = null;
		displayedTimeZone = null;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the list of loaded players' names.
	 * 
	 * @return Enabled players' names
	 * @see #getPlayers()
	 */
	public List<String> getPlayersNames() {
		return getPlayers().values().stream().map(ChatPluginPlayer::getName).collect(Collectors.toList());
	}
	
	/**
	 * Gets the list of loaded players' IP addresses.
	 * 
	 * @return Enabled players' IPs
	 * @see #getPlayers()
	 */
	public List<InetAddress> getPlayersIPs() {
		List<InetAddress> ips = new ArrayList<>();
		
		for (ChatPluginPlayer player : getPlayers().values())
			if (!ips.contains(player.getIPAddress()))
				ips.add(player.getIPAddress());
		return ips;
	}
	
	/**
	 * Gets the prefix added in front of Bedrock
	 * players' usernames to prevent duplicates.
	 * 
	 * <p>This method retrieves the prefix using Floodgate's
	 * {@link MultiPlatformIntegration#getUsernamePrefix()} or from
	 * "settings.floodgate-username-prefix" in {@link ConfigurationType#CONFIG}
	 * depending on the current environment.</p>
	 * 
	 * @return Bedrock players' prefix
	 */
	public String getFloodgateUsernamePrefix() {
		return floodgateUsernamePrefix;
	}
	
	/**
	 * Gets the pattern used to verify valid usernames.
	 * 
	 * <p>This will consider Bedrock players if Floodgate
	 * is installed or {@link #getFloodgateUsernamePrefix()}
	 * depending on the current environment.</p>
	 * 
	 * @return Username's pattern
	 */
	public Pattern getUsernamePattern() {
		return usernamePattern;
	}
	
	/**
	 * Checks if the specified String is a valid username.
	 * 
	 * @param username Username to check
	 * @return Whether the specified username is valid
	 * @see #getUsernamePattern()
	 */
	public boolean isValidUsername(String username) {
		return usernamePattern.matcher(username).matches();
	}
	
	/**
	 * Gets the time zone displayed to players in messages.
	 * 
	 * <p><strong>Found at:</strong> "settings.displayed-time-zone" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Displayed time zone
	 */
	public TimeZone getDisplayedTimeZone() {
		return displayedTimeZone;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static PlayerManager getInstance() {
		return instance;
	}
	
	/**
	 * Gets the loaded {@link ChatPluginPlayer}s' map.
	 * 
	 * <p>Do <em>not</em> modify the returned map.</p>
	 * 
	 * @return Loaded players' map
	 * @see ServerPlayerManager#getPlayers()
	 * @see ProxyPlayerManager#getPlayers()
	 */
	public abstract Map<UUID, ? extends ChatPluginPlayer> getPlayers();
	
	/**
	 * Gets the list of loaded {@link ChatPluginPlayer}s
	 * with the specified IP address.
	 * 
	 * @param ipAddress IP address to check
	 * @return Loaded players' map
	 * @see ServerPlayerManager#getPlayers(InetAddress)
	 * @see ProxyPlayerManager#getPlayers(InetAddress)
	 */
	public abstract List<? extends ChatPluginPlayer> getPlayers(InetAddress ipAddress);
	
	/**
	 * Gets a player from {@link #getPlayers()} by their UUID.
	 * 
	 * <p>Will return <code>null</code> if the player is not loaded.</p>
	 * 
	 * @param uuid Player to get
	 * @return Loaded {@link ChatPluginPlayer}
	 * @see ServerPlayerManager#getPlayer(UUID)
	 * @see ProxyPlayerManager#getPlayer(UUID)
	 */
	@Nullable(why = "Specified player may not be loaded")
	public abstract ChatPluginPlayer getPlayer(UUID uuid);
	
	/**
	 * Gets a player from {@link #getPlayers()} by their name.
	 * 
	 * <p>Will return <code>null</code> if the player is not loaded.</p>
	 * 
	 * @deprecated Names should not be used to identify players. Use {@link #getPlayer(UUID)} instead.
	 * @param name Player to get
	 * @param checkPattern Whether to check the name against {@link #getUsernamePattern()}
	 * @param ignoreCase Whether to ignore case when checking online players
	 * @return Loaded {@link ChatPluginPlayer}
	 * @throws IllegalArgumentException If <code>checkPattern</code> and specified name <code>!{@link #isValidUsername(String)}</code>
	 * @see ServerPlayerManager#getPlayer(String, boolean, boolean)
	 * @see ProxyPlayerManager#getPlayer(String, boolean, boolean)
	 */
	@Nullable(why = "Specified player may not be loaded")
	@Deprecated
	public abstract ChatPluginPlayer getPlayer(String name, boolean checkPattern, boolean ignoreCase);
	
	/**
	 * Loads all online players in the network (if run on
	 * a proxy) or in an enabled world (if run on a server).
	 */
	public abstract void loadOnlinePlayers();
	
}
