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

package me.remigio07.chatplugin.api.proxy.player;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.proxy.event.player.ProxyPlayerLoadEvent;
import me.remigio07.chatplugin.api.proxy.event.player.ProxyPlayerUnloadEvent;

/**
 * Manager that handles {@link ChatPluginProxyPlayer}s.
 */
public abstract class ProxyPlayerManager extends PlayerManager {
	
	protected Map<UUID, ChatPluginProxyPlayer> players = new ConcurrentHashMap<>();
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (IntegrationType.FLOODGATE.isEnabled()) {
			if ((floodgateUsernamePrefix = IntegrationType.FLOODGATE.get().getUsernamePrefix()).isEmpty())
				LogManager.log("Floodgate is installed but the username prefix set at \"username-prefix\" in its config.yml is empty: this is not recommended as ChatPlugin will not be able to distinguish Java players from Bedrock ones.", 1);
			else if (!Pattern.matches("^[^ \\w]$", floodgateUsernamePrefix))
				throw new ChatPluginManagerException(this, "invalid Floodgate username prefix ({0}) set at \"username-prefix\" in Floodgate's config.yml: it cannot be longer than 1 character and cannot be a letter, a number, a space or an underscore", floodgateUsernamePrefix);
		} else floodgateUsernamePrefix = "";
		super.load();
		
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() {
		enabled = false;
		
		players.keySet().forEach(this::unloadPlayer);
		players.clear();
		super.unload();
	}
	
	/**
	 * Gets the loaded {@link ChatPluginProxyPlayer}s' map.
	 * 
	 * <p>Do <em>not</em> modify the returned map.</p>
	 * 
	 * @return Loaded players' map
	 */
	@Override
	public Map<UUID, ChatPluginProxyPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Gets the list of loaded {@link ChatPluginProxyPlayer}s with the specified IP address.
	 * 
	 * @param ipAddress IP address to check
	 * @return Loaded players' map
	 */
	@Override
	public List<ChatPluginProxyPlayer> getPlayers(InetAddress ipAddress) {
		return players.values().stream().filter(player -> player.getIPAddress().equals(ipAddress)).collect(Collectors.toList());
	}
	
	/**
	 * Gets a player from {@link #getPlayers()} by their UUID.
	 * 
	 * <p>Will return <code>null</code> if the player is not loaded.</p>
	 * 
	 * @param uuid Player to get
	 * @return Loaded {@link ChatPluginProxyPlayer}
	 */
	@Nullable(why = "Specified player may not be loaded")
	@Override
	public ChatPluginProxyPlayer getPlayer(UUID uuid) {
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
	 * @return Loaded {@link ChatPluginProxyPlayer}
	 * @throws IllegalArgumentException If <code>checkPattern</code> and specified name <code>!{@link #isValidUsername(String)}</code>
	 */
	@Nullable(why = "Specified player may not be loaded")
	@Deprecated
	@Override
	public ChatPluginProxyPlayer getPlayer(String name, boolean checkPattern, boolean ignoreCase) {
		if (!isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" does not respect the following pattern: \"" + usernamePattern.pattern() + "\"");
		for (ChatPluginProxyPlayer player : getPlayers().values())
			if (ignoreCase ? player.getName().equalsIgnoreCase(name) : player.getName().equals(name))
				return player;
		return null;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ProxyPlayerManager getInstance() {
		return (ProxyPlayerManager) instance;
	}
	
	/**
	 * Adds a player to {@link #getPlayers()}.
	 * 
	 * <p>Will do nothing and return 0 if they are already loaded.</p>
	 * 
	 * @param player Player to load
	 * @return Time elapsed, in milliseconds
	 * @see ProxyPlayerLoadEvent
	 */
	public abstract int loadPlayer(PlayerAdapter player);
	
	/**
	 * Removes a player from {@link #getPlayers()}.
	 * 
	 * <p>Will do nothing and return 0 if they are not loaded.</p>
	 * 
	 * @param player Player to unload
	 * @return Time elapsed, in milliseconds
	 * @see ProxyPlayerUnloadEvent
	 */
	public abstract int unloadPlayer(UUID player);
	
}
