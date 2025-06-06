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

package me.remigio07.chatplugin.api.common.punishment.mute;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.Punishment;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents a mute handled by the {@link MuteManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#mutes">ChatPlugin wiki/Modules/Punishments/Mutes</a>
 */
public abstract class Mute extends Punishment {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with a mute's information.
	 * 
	 * <p><strong>Content:</strong> ["id", "player", "player_uuid", "staff_member", "who_unmuted", "reason", "server", "date", "unmute_date", "expiration_date", "duration", "remaining_time", "active", "global", "silent"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#placeholders-4">ChatPlugin wiki/Punishments/Mutes/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "id",  "player", "player_uuid", "staff_member", "who_unmuted", "reason", "server", "date", "unmute_date", "expiration_date", "duration", "remaining_time", "active", "global", "silent" };
	protected String whoUnmuted;
	protected long unmuteDate, duration, taskID = -1;
	protected boolean global;
	
	protected Mute(int id, OfflinePlayer player, String staffMember, String whoUnmuted, String reason, String server, long date, long unmuteDate, long duration, boolean global, boolean silent) {
		super(id, player, staffMember, reason, server, date, silent);
		this.whoUnmuted = whoUnmuted;
		this.unmuteDate = unmuteDate;
		this.duration = duration;
		this.global = global;
	}
	
	/**
	 * Gets this mute's player.
	 * 
	 * @return Mute's player
	 */
	@NotNull
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets who unmuted {@link #getPlayer()}.
	 * 
	 * <p>Will return <code>null</code> if the player has not been manually unmuted.</p>
	 * 
	 * @return Who unmuted the player
	 */
	@Nullable(why = "Player may not have been manually unmuted")
	public String getWhoUnmuted() {
		return whoUnmuted;
	}
	
	/**
	 * Sets who unmuted {@link #getPlayer()}.
	 * 
	 * @param whoUnmuted Who unmuted the player
	 */
	public void setWhoUnmuted(@NotNull String whoUnmuted) {
		this.whoUnmuted = whoUnmuted;
	}
	
	/**
	 * Gets this mute's unmute date, in milliseconds.
	 * 
	 * <p>Will return -1 if the player has not been manually unmuted.</p>
	 * 
	 * @return Unmute's date
	 */
	public long getUnmuteDate() {
		return unmuteDate;
	}
	
	/**
	 * Sets this mute's unmute date, in milliseconds.
	 * 
	 * @param unmuteDate Unmute's date
	 */
	public void setUnmuteDate(long unmuteDate) {
		this.unmuteDate = unmuteDate;
	}
	
	/**
	 * Gets this mute's duration, in milliseconds.
	 * 
	 * <p>Will return -1 if this mute is permanent.</p>
	 * 
	 * @return Mute's duration
	 */
	public long getDuration() {
		return duration;
	}
	
	/**
	 * Sets this mute's duration, in milliseconds.
	 * 
	 * <p>You can specify -1 for a permanent mute.</p>
	 * 
	 * @param duration Mute's duration
	 */
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	/**
	 * Gets this mute's remaining time, in milliseconds.
	 * 
	 * <p>Will return 0 if the player has been unmuted
	 * otherwise -1 if the mute is permanent.</p>
	 * 
	 * @return Mute's remaining time
	 */
	public long getRemainingTime() {
		return getWhoUnmuted() == null ? duration == -1 ? -1 : System.currentTimeMillis() > date + duration ? 0 : date + duration - System.currentTimeMillis() : 0;
	}
	
	/**
	 * Gets this mute's expiration task's ID.
	 * 
	 * <p>May be used with {@link TaskManager#cancelAsync(long)}.</p>
	 * 
	 * <p>Will return -1 if this mute is permanent, if it has already expired or if
	 * <code>!{@link Environment#isProxy()} &amp;&amp; {@link ProxyManager#isEnabled()}</code>.</p>
	 * 
	 * @return Mute's expiration task's ID
	 */
	public long getTaskID() {
		return taskID;
	}
	
	/**
	 * Checks if this mute affects all servers inside of the network.
	 * 
	 * <p>Only applies on multi instance setups with a proxy.</p>
	 * 
	 * @return Whether this mute is global
	 */
	public boolean isGlobal() {
		return global;
	}
	
	/**
	 * Sets if this mute should affect all servers inside of the network.
	 * 
	 * <p>Only applies on multi instance setups with a proxy.</p>
	 * 
	 * @param global Whether this mute is global
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}
	
	/**
	 * Checks if this mute is active at the moment.
	 * 
	 * @return Whether this mute is active
	 */
	public boolean isActive() {
		return getRemainingTime() != 0;
	}
	
}
