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

package me.remigio07.chatplugin.api.proxy.player;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.proxy.event.player.ProxyPlayerLoadEvent;
import me.remigio07.chatplugin.api.proxy.event.player.ProxyPlayerUnloadEvent;

/**
 * Manager that handles {@link ChatPluginProxyPlayer}s.
 */
public abstract class ProxyPlayerManager extends PlayerManager {
	
	protected Map<UUID, ChatPluginProxyPlayer> players = new HashMap<>();
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		enabled = true;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		new ArrayList<>(players.keySet()).forEach(player -> unloadPlayer(player));
		players.clear();
	}
	
	/**
	 * Gets the loaded {@link ChatPluginProxyPlayer}s' map.
	 * 
	 * <p>Do <strong>not</strong> modify the returned map.</p>
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
	 * @param checkPattern Whether to check the name against {@link Utils#USERNAME_PATTERN}
	 * @param ignoreCase Whether to ignore case when checking online players
	 * @return Loaded {@link ChatPluginProxyPlayer}
	 * @throws IllegalArgumentException If <code>checkPattern</code> and specified name <code>!{@link Utils#isValidUsername(String)}</code>
	 */
	@Nullable(why = "Specified player may not be loaded")
	@Deprecated
	@Override
	public ChatPluginProxyPlayer getPlayer(String name, boolean checkPattern, boolean ignoreCase) {
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
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
