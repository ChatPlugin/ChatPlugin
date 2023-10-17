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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.punishment;

import java.util.List;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.ban.Ban;
import me.remigio07.chatplugin.api.common.punishment.kick.Kick;
import me.remigio07.chatplugin.api.common.punishment.mute.Mute;
import me.remigio07.chatplugin.api.common.punishment.warning.Warning;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.common.util.text.ComponentTranslator;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a punishment (ban, warning, kick,
 * mute) handled by a {@link PunishmentManager}.
 */
public abstract class Punishment {
	
	protected static ComponentTranslator componentTranslator = ComponentTranslator.getInstance();
	protected int id;
	protected OfflinePlayer player;
	protected String staffMember, reason, server;
	protected long date;
	protected boolean silent;
	
	protected Punishment(int id, OfflinePlayer player, String staffMember, String reason, String server, long date, boolean silent) {
		this.id = id;
		this.player = player;
		this.staffMember = staffMember;
		this.reason = reason;
		this.server = server;
		this.date = date;
		this.silent = silent;
	}
	
	/**
	 * Gets this punishment's ID.
	 * 
	 * @return Punishment's ID
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Gets who punished the player.
	 * 
	 * @return Punishment's staff member
	 */
	@NotNull
	public String getStaffMember() {
		return staffMember;
	}
	
	/**
	 * Sets who punished the player.
	 * 
	 * @param staffMember Punishment's staff member
	 */
	public void setStaffMember(@NotNull String staffMember) {
		this.staffMember = staffMember;
	}
	
	/**
	 * Gets this punishment's reason.
	 * 
	 * <p>Will return <code>null</code> if no reason was specified.</p>
	 * 
	 * @return Punishment's reason
	 */
	@Nullable(why = "Reason may not have been specified")
	public String getReason() {
		return reason;
	}
	
	/**
	 * Sets this punishment's reason.
	 * 
	 * <p>You can specify <code>null</code> to reset the reason.</p>
	 * 
	 * @param reason Punishment's reason
	 */
	public void setReason(@Nullable(why = "Reason may not have been specified") String reason) {
		this.reason = reason;
	}
	
	/**
	 * Gets this punishment's origin server.
	 * 
	 * @return Punishment's origin server
	 */
	@NotNull
	public String getServer() {
		return server;
	}
	
	/**
	 * Sets this punishment's origin server.
	 * 
	 * @param server Punishment's origin server
	 */
	public void setServer(@NotNull String server) {
		this.server = server;
	}
	
	/**
	 * Gets this punishment's creation or
	 * modification date, in milliseconds.
	 * 
	 * @return Punishment's creation or modification date
	 */
	public long getDate() {
		return date;
	}
	
	/**
	 * Sets this punishment's modification date.
	 * 
	 * @param date Punishment's modification date
	 */
	public void setDate(long date) {
		this.date = date;
	}
	
	/**
	 * Checks if this punishment is silent.
	 * 
	 * <p>If <code>true</code>, only Staff members will receive the announcement.</p>
	 * 
	 * @return Whether this punishment is silent
	 */
	public boolean isSilent() {
		return silent;
	}
	
	/**
	 * Sets if this punishment should be silent.
	 * 
	 * <p>If <code>true</code>, only Staff members will receive the announcement.</p>
	 * 
	 * @param silent Whether this punishment is silent
	 */
	public void setSilent(boolean silent) {
		this.silent = silent;
	}
	
	/**
	 * Translates an input string with this punishment's specific placeholders.
	 * 
	 * <p>Every punishment has different placeholders available. Check the following fields:
	 * 	<ul>
	 * 		<li>{@link Ban#PLACEHOLDERS} - bans' placeholders</li>
	 * 		<li>{@link Warning#PLACEHOLDERS} - warnings' placeholders</li>
	 * 		<li>{@link Kick#PLACEHOLDERS} - kicks' placeholders</li>
	 * 		<li>{@link Mute#PLACEHOLDERS} - mutes' placeholders</li>
	 * 	</ul>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	public abstract String formatPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string list with this punishment's specific placeholders.
	 * 
	 * <p>Every punishment has different placeholders available. Check the following fields:
	 * 	<ul>
	 * 		<li>{@link Ban#PLACEHOLDERS} - bans' placeholders</li>
	 * 		<li>{@link Warning#PLACEHOLDERS} - warnings' placeholders</li>
	 * 		<li>{@link Kick#PLACEHOLDERS} - kicks' placeholders</li>
	 * 		<li>{@link Mute#PLACEHOLDERS} - mutes' placeholders</li>
	 * 	</ul>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	public abstract List<String> formatPlaceholders(List<String> input, Language language);
	
}
