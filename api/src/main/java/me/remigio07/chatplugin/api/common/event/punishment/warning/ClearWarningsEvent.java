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

package me.remigio07.chatplugin.api.common.event.punishment.warning;

import me.remigio07.chatplugin.api.common.event.player.OfflinePlayerEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;

/**
 * Represents the event called after
 * a player's warnings get cleared.
 * 
 * @see WarningManager#clearWarnings(OfflinePlayer, String, String)
 */
public class ClearWarningsEvent implements OfflinePlayerEvent {
	
	private OfflinePlayer player;
	private String server, whoUnwarned;
	
	/**
	 * Constructs a new clear warnings event.
	 * 
	 * @param player Player involved
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param whoUnwarned Staff member involved
	 */
	public ClearWarningsEvent(OfflinePlayer player, @Nullable(why = "Null to disactive global warnings") String server, @NotNull String whoUnwarned) {
		this.player = player;
		this.server = server;
		this.whoUnwarned = whoUnwarned;
	}
	
	@Override
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets this event's origin server.
	 * Will return <code>null</code> if this is a global clear warnings.
	 * 
	 * @return Origin server ({@link ProxyManager#getServerID()})
	 */
	@Nullable(why = "Server may not have been specified for global clear warnings")
	public String getServer() {
		return server;
	}
	
	/**
	 * Gets the staff member who unwarned the player.
	 * 
	 * @return Who unwarned the player
	 */
	@NotNull
	public String getWhoUnwarned() {
		return whoUnwarned;
	}
	
}
