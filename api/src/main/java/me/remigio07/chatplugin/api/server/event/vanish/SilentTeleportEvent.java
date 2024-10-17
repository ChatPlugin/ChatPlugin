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

package me.remigio07.chatplugin.api.server.event.vanish;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called after a successful <code>/silentteleport</code>.
 * 
 * <p><strong>Note:</strong> a {@link VanishEnableEvent} is always called before this event.</p>
 */
public class SilentTeleportEvent extends VanishEvent {
	
	private OfflinePlayer targetPlayer;
	private String targetServer;
	
	/**
	 * Constructs a new silent teleport event.
	 * 
	 * @param player Player involved
	 * @param targetPlayer Target player
	 * @param targetServer Target server
	 */
	public SilentTeleportEvent(
			ChatPluginServerPlayer player,
			@Nullable(why = "Null to represent a server-silent teleport") OfflinePlayer targetPlayer,
			@Nullable(why = "Null to represent a player-silent teleport") String targetServer
			) {
		super(player);
		this.targetPlayer = targetPlayer;
		this.targetServer = targetServer;
	}
	
	/**
	 * Gets the target player of the teleport.
	 * 
	 * <p>Will return <code>null</code> if
	 * this is a server-silent teleport.</p>
	 * 
	 * @return Target player
	 */
	@Nullable(why = "Null to represent a server-silent teleport")
	public OfflinePlayer getTargetPlayer() {
		return targetPlayer;
	}
	
	/**
	 * Gets the target server of the teleport.
	 * 
	 * <p>Will return <code>null</code> if
	 * this is a player-silent teleport.</p>
	 * 
	 * @return Target server
	 */
	@Nullable(why = "Null to represent a player-silent teleport")
	public String getTargetServer() {
		return targetServer;
	}
	
}
