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

package me.remigio07.chatplugin.api.common.punishment.kick;

import java.net.InetAddress;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.Punishment;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;

/**
 * Represents a kick handled by the {@link KickManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#kicks">ChatPlugin wiki/Modules/Punishments/Kicks</a>
 * @see KickType
 */
public abstract class Kick extends Punishment {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with a kick's information.
	 * 
	 * <p><strong>Content:</strong> ["id", "player", "player_uuid", "ip_address", "staff_member", "reason", "server", "type", "date", "silent"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#placeholders-3">ChatPlugin wiki/Punishments/Kicks/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = new String[] { "id", "player", "player_uuid", "ip_address", "staff_member", "reason", "server", "type", "date", "silent" };
	protected InetAddress ipAddress;
	protected KickType type;
	
	protected Kick(int id, OfflinePlayer player, InetAddress ipAddress, String staffMember, String reason, String server, KickType type, long date, boolean silent) {
		super(id, player, staffMember, reason, server, date, silent);
		this.ipAddress = ipAddress;
		this.type = type;
	}
	
	/**
	 * Gets this kick's player.
	 * 
	 * @return Kick's player
	 */
	@NotNull
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the IP address the player had when this kick occurred.
	 * 
	 * @return Kick's IP address
	 */
	@NotNull
	public InetAddress getIPAddress() {
		return ipAddress;
	}
	
	/**
	 * Gets this kick's type.
	 * 
	 * @return Kick's type
	 */
	public KickType getType() {
		return type;
	}
	
}
