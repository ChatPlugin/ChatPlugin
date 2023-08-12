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

package me.remigio07.chatplugin.api.common.player;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
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
	protected long loadTime;
	
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
	 * Gets the loaded players' amount.
	 * 
	 * @return Loaded players' amount
	 * @see #getPlayers()
	 */
	public int getTotalPlayers() {
		return getPlayers().size();
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
	 * Do not modify the returned map.
	 * 
	 * @return Loaded players' map
	 * @see ServerPlayerManager#getPlayers()
	 * @see ProxyPlayerManager#getPlayers()
	 */
	public abstract Map<UUID, ? extends ChatPluginPlayer> getPlayers();
	
	/**
	 * Gets the list of loaded {@link ChatPluginPlayer}s with the specified IP address.
	 * 
	 * @param ipAddress IP address to check
	 * @return Loaded players' map
	 * @see ServerPlayerManager#getPlayers(InetAddress)
	 * @see ProxyPlayerManager#getPlayers(InetAddress)
	 */
	public abstract List<? extends ChatPluginPlayer> getPlayers(InetAddress ipAddress);
	
	/**
	 * Gets a player from {@link #getPlayers()} by their UUID.
	 * Will return <code>null</code> if the player is not loaded.
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
	 * Will return <code>null</code> if the player is not loaded.
	 * 
	 * @deprecated Names should not be used to identify players. Use {@link #getPlayer(UUID)} instead.
	 * @param name Player to get
	 * @param checkPattern Whether to check the name against {@link Utils#USERNAME_PATTERN}
	 * @param ignoreCase Whether to ignore case when checking online players
	 * @return Loaded {@link ChatPluginPlayer}
	 * @throws IllegalArgumentException If <code>checkPattern</code> and specified name <code>!{@link Utils#isValidUsername(String)}</code>
	 * @see ServerPlayerManager#getPlayer(String, boolean)
	 * @see ProxyPlayerManager#getPlayer(String, boolean)
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
