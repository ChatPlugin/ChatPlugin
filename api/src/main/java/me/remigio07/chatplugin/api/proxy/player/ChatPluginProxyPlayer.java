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

package me.remigio07.chatplugin.api.proxy.player;

import me.remigio07.chatplugin.api.common.player.ChatPluginPlayer;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;

/**
 * Represents a {@link ChatPluginPlayer} loaded on a proxy (BungeeCord/Velocity) environment.
 * 
 * @see ProxyPlayerManager
 */
public abstract class ChatPluginProxyPlayer extends OfflinePlayer implements ChatPluginPlayer {
	
	protected String server;
	protected Version version;
	protected boolean bedrockPlayer;
	
	/**
	 * Constructs a new proxy player with the given {@link PlayerAdapter} object.
	 * 
	 * @deprecated Internal use only. Use {@link ProxyPlayerManager#loadPlayer(PlayerAdapter)} to load a player and {@link ProxyPlayerManager#getPlayer(java.util.UUID)} to obtain their instance.
	 * @param player Player object
	 */
	@Deprecated
	public ChatPluginProxyPlayer(PlayerAdapter player) {
		super(player);
	}
	
	@Override
	public Version getVersion() {
		return version;
	}
	
	@Override
	public boolean isBedrockPlayer() {
		return bedrockPlayer;
	}
	
	/**
	 * Gets this player's current server.
	 * 
	 * @return Player's server
	 */
	public String getServer() {
		return server;
	}
	
}
