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

package me.remigio07.chatplugin.api.common.punishment.warning;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.Punishment;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents a warning handled by the {@link WarningManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#warnings">ChatPlugin wiki/Modules/Punishments/Warnings</a>
 */
public abstract class Warning extends Punishment {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with a warning's information.
	 * 
	 * <p><strong>Content:</strong> ["id", "player", "player_uuid", "staff_member", "who_unwarned", "reason", "server", "date", "unwarn_date", "expiration_date", "duration", "remaining_time", "amount", "max_amount", "active", "global", "silent"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#placeholders-2">ChatPlugin wiki/Punishments/Warnings/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "id", "player", "player_uuid", "staff_member", "who_unwarned", "reason", "server", "date", "unwarn_date", "expiration_date", "duration", "remaining_time", "amount", "max_amount", "active", "global", "silent" };
	protected String whoUnwarned;
	protected long unwarnDate, duration, taskID = -1;
	protected boolean global;
	
	protected Warning(int id, OfflinePlayer player, String staffMember, String whoUnwarned, String reason, String server, long date, long unwarnDate, long duration, boolean global, boolean silent) {
		super(id, player, staffMember, reason, server, date, silent);
		this.whoUnwarned = whoUnwarned;
		this.unwarnDate = unwarnDate;
		this.duration = duration;
		this.global = global;
	}
	
	/**
	 * Gets this warning's player.
	 * 
	 * @return Warning's player
	 */
	@NotNull
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets who unwarned {@link #getPlayer()}.
	 * 
	 * <p>Will return <code>null</code> if the player has not been manually unwarned.</p>
	 * 
	 * @return Who unwarned the player
	 */
	@Nullable(why = "Player may not have been manually unwarned")
	public String getWhoUnwarned() {
		return whoUnwarned;
	}
	
	/**
	 * Sets who unwarned {@link #getPlayer()}.
	 * 
	 * @param whoUnwarned Who unwarned the player
	 */
	public void setWhoUnwarned(@NotNull String whoUnwarned) {
		this.whoUnwarned = whoUnwarned;
	}
	
	/**
	 * Gets this warning's unwarn date, in milliseconds.
	 * 
	 * <p>Will return -1 if the player has not been unwarned.</p>
	 * 
	 * @return Unwarn's date
	 */
	public long getUnwarnDate() {
		return unwarnDate;
	}
	
	/**
	 * Sets this warning's unwarn date, in milliseconds.
	 * 
	 * @param unwarnDate Unwarn's date
	 */
	public void setUnwarnDate(long unwarnDate) {
		this.unwarnDate = unwarnDate;
	}
	
	/**
	 * Gets this warning's duration, in milliseconds.
	 * 
	 * @return Warning's duration
	 */
	public long getDuration() {
		return duration;
	}
	
	/**
	 * Gets this warning's remaining time, in milliseconds.
	 * 
	 * <p>Will return 0 if the player has been unwarned.</p>
	 * 
	 * @return Warning's remaining time
	 */
	public long getRemainingTime() {
		return getWhoUnwarned() == null ? duration == -1 ? -1 : System.currentTimeMillis() > date + duration ? 0 : date + duration - System.currentTimeMillis() : 0;
	}
	
	/**
	 * Gets this warning's expiration task's ID.
	 * 
	 * <p>May be used with {@link TaskManager#cancelAsync(long)}.</p>
	 * 
	 * <p>Will return -1 if this warning has already expired or if
	 * <code>!{@link Environment#isProxy()} &amp;&amp; {@link ProxyManager#isEnabled()}</code>.</p>
	 * 
	 * @return Warning's expiration task's ID
	 */
	public long getTaskID() {
		return taskID;
	}
	
	/**
	 * Checks if this warning affects all servers inside of the network.
	 * 
	 * <p>Only applies on multi instance setups with a proxy.</p>
	 * 
	 * @return Whether this warning is global
	 */
	public boolean isGlobal() {
		return global;
	}
	
	/**
	 * Sets if this warning should affect all servers inside of the network.
	 * 
	 * <p>Only applies on multi instance setups with a proxy.</p>
	 * 
	 * @param global Whether this warning is global
	 */
	public void setGlobal(boolean global) {
		this.global = global;
	}
	
	/**
	 * Checks if this warning is active at the moment.
	 * 
	 * @return Whether this warning is active
	 */
	public boolean isActive() {
		return getRemainingTime() != 0;
	}
	
}
