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

package me.remigio07.chatplugin.api.common.punishment.ban;

import java.net.InetAddress;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.Punishment;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents a ban handled by the {@link BanManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#bans">ChatPlugin wiki/Modules/Punishments/Bans</a>
 * @see BanType
 */
public abstract class Ban extends Punishment {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with a ban's information.
	 * 
	 * <p><strong>Content:</strong> ["id", "player", "player_uuid", "ip_address", "staff_member", "who_unbanned", "reason", "server", "type", "date", "unban_date", "expiration_date", "duration", "remaining_time", "active", "global", "silent"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#placeholders">ChatPlugin wiki/Punishments/Bans/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "id", "player", "player_uuid", "ip_address", "staff_member", "who_unbanned", "reason", "server", "type", "date", "unban_date", "expiration_date", "duration", "remaining_time", "active", "global", "silent" };
	protected InetAddress ipAddress;
	protected String whoUnbanned;
	protected BanType type;
	protected long unbanDate, duration, taskID = -1;
	protected boolean global;
	
	protected Ban(int id, OfflinePlayer player, InetAddress ipAddress, String staffMember, String whoUnbanned, String reason, String server, BanType type, long date, long unbanDate, long duration, boolean global, boolean silent) {
		super(id, player, staffMember, reason, server, date, silent);
		this.ipAddress = ipAddress;
		this.whoUnbanned = whoUnbanned;
		this.type = type;
		this.unbanDate = unbanDate;
		this.duration = duration;
		this.global = global;
	}
	
	/**
	 * Gets this ban's player.
	 * 
	 * <p>Will return <code>null</code> if they have not been specified
	 * ({@link BanType#IP_ADDRESS} with no given player).</p>
	 * 
	 * @return Ban's player
	 */
	@Nullable(why = "Player may not have been specified")
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the IP address the player had when this ban occurred.
	 * 
	 * <p>Will return <code>null</code> if the IP address is unknown.</p>
	 * 
	 * @return Ban's IP address
	 */
	@Nullable(why = "IP address may be unknown")
	public InetAddress getIPAddress() {
		return ipAddress;
	}
	
	/**
	 * Gets who unbanned {@link #getPlayer()}.
	 * 
	 * <p>Will return <code>null</code> if the player has not been manually unbanned.</p>
	 * 
	 * @return Who unbanned the player
	 */
	@Nullable(why = "Player may not have been manually unbanned")
	public String getWhoUnbanned() {
		return whoUnbanned;
	}
	
	/**
	 * Sets who unbanned {@link #getPlayer()}.
	 * 
	 * @param whoUnbanned Who unbanned the player
	 */
	public void setWhoUnbanned(@NotNull String whoUnbanned) {
		this.whoUnbanned = whoUnbanned;
	}
	
	/**
	 * Gets this ban's type.
	 * 
	 * @return Ban's type
	 */
	public BanType getType() {
		return type;
	}
	
	/**
	 * Gets this ban's unban date, in milliseconds.
	 * 
	 * <p>Will return -1 if the player has not been manually unbanned.</p>
	 * 
	 * @return Unban's date
	 */
	public long getUnbanDate() {
		return unbanDate;
	}
	
	/**
	 * Sets this ban's unban date, in milliseconds.
	 * 
	 * @param unbanDate Unban's date
	 */
	public void setUnbanDate(long unbanDate) {
		this.unbanDate = unbanDate;
	}
	
	/**
	 * Gets this ban's duration, in milliseconds.
	 * 
	 * <p>Will return -1 if this ban is permanent.</p>
	 * 
	 * @return Ban's duration
	 */
	public long getDuration() {
		return duration;
	}
	
	/**
	 * Sets this ban's duration, in milliseconds.
	 * 
	 * <p>You can specify -1 for a permanent ban.</p>
	 * 
	 * @param duration Ban's duration
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	/**
	 * Gets this ban's remaining time, in milliseconds.
	 * 
	 * <p>Will return 0 if the player has been unbanned
	 * otherwise -1 if the ban is permanent.</p>
	 * 
	 * @return Ban's remaining time
	 */
	public long getRemainingTime() {
		return getWhoUnbanned() == null ? duration == -1 ? -1 : System.currentTimeMillis() > date + duration ? 0 : date + duration - System.currentTimeMillis() : 0;
	}
	
	/**
	 * Gets this ban's expiration task's ID.
	 * 
	 * <p>May be used with {@link TaskManager#cancelAsync(long)}.</p>
	 * 
	 * <p>Will return -1 if this ban is permanent, if it has already expired or if
	 * <code>!{@link Environment#isProxy()} &amp;&amp; {@link ProxyManager#isEnabled()}</code>.</p>
	 * 
	 * @return Ban's expiration task's ID
	 */
	public long getTaskID() {
		return taskID;
	}
	
	/**
	 * Checks if this ban affects all servers inside of the network.
	 * 
	 * <p>Only applies on multi instance setups with a proxy.</p>
	 * 
	 * @return Whether this ban is global
	 */
	public boolean isGlobal() {
		return global;
	}
	
	/**
	 * Sets if this ban should affect all servers inside of the network.
	 * 
	 * <p>Only applies on multi instance setups with a proxy.</p>
	 * 
	 * @param global Whether this ban is global
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}
	
	/**
	 * Checks if this ban is active at the moment.
	 * 
	 * @return Whether this ban is active
	 */
	public boolean isActive() {
		return getRemainingTime() != 0;
	}
	
}
