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
import java.util.UUID;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.integration.multiplatform.MultiPlatformIntegration;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.proxy.player.ChatPluginProxyPlayer;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a player handled by the {@link PlayerManager}
 * loaded into the ChatPlugin system.
 * 
 * @see PlayerManager
 * @see ChatPluginServerPlayer
 * @see ChatPluginProxyPlayer
 */
public interface ChatPluginPlayer {
	
	/**
	 * Gets this player's name.
	 * 
	 * @return Player's name
	 */
	public String getName();
	
	/**
	 * Gets this player's UUID.
	 * 
	 * @return Player's UUID
	 */
	public UUID getUUID();
	
	/**
	 * Gets this player's IP address.
	 * 
	 * <p><strong>Note:</strong> BungeeCord supports connections via
	 * <a href="https://en.wikipedia.org/wiki/Unix_domain_socket">Unix domain sockets</a>.
	 * If this method is called on a player connected through a Unix domain socket,
	 * {@link InetAddress#getLoopbackAddress()} is returned.</p>
	 * 
	 * @return Player's IP address
	 */
	@NotNull
	public InetAddress getIPAddress();
	
	/**
	 * Gets this player's version.
	 * 
	 * @return Player's version
	 */
	public Version getVersion();
	
	/**
	 * Checks if this player is connected through
	 * a {@link MultiPlatformIntegration}.
	 * 
	 * @return Whether this player is using the Bedrock Edition
	 */
	public boolean isBedrockPlayer();
	
	/**
	 * Sends a plain message to this player.
	 * 
	 * @param message Message to send
	 */
	public void sendMessage(String message);
	
	/**
	 * Connects this player to the specified server.
	 * 
	 * <p>Will do nothing if called on a server with a single instance setup.</p>
	 * 
	 * @param server Server to connect the player to
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void connect(String server);
	
	/**
	 * Disconnects this player with the specified reason.
	 * 
	 * <p>On server (Bukkit/Sponge) implementations, the player will
	 * be kicked immediately if this method is called by the main
	 * thread, otherwise it will be executed on the next tick.</p>
	 * 
	 * @param reason Reason to kick the player for
	 */
	public void disconnect(String reason);
	
}
