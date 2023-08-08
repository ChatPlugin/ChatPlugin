/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.api.common.punishment.warning;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07_.chatplugin.api.common.event.punishment.warning.ClearWarningsEvent;
import me.remigio07_.chatplugin.api.common.event.punishment.warning.RemoveLastWarningEvent;
import me.remigio07_.chatplugin.api.common.event.punishment.warning.UnwarnEvent;
import me.remigio07_.chatplugin.api.common.event.punishment.warning.WarningEvent;
import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.punishment.PunishmentManager;
import me.remigio07_.chatplugin.api.common.storage.StorageConnector;
import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07_.chatplugin.bootstrap.Environment;

/**
 * Manager that handles {@link Warning}s and interacts with the database.
 */
public abstract class WarningManager extends PunishmentManager {
	
	protected static WarningManager instance;
	protected List<Warning> warnings = new ArrayList<>();
	protected Map<Integer, Map<Language, List<String>>> punishCommands = new HashMap<>();
	protected boolean defaultGlobal;
	protected long duration;
	protected int maxAmount;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "warning.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the active warnings' list.
	 * Do not modify the returned list.
	 * 
	 * @return Active warnings' list
	 */
	public List<Warning> getActiveWarnings() {
		return warnings;
	}
	
	/**
	 * Gets the punish commands' map.
	 * 
	 * <p><strong>Found at:</strong> "warning.punish-commands" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Punish commands' map
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public Map<Integer, Map<Language, List<String>>> getPunishCommands() {
		return punishCommands;
	}
	
	/**
	 * Checks if the specified amount of warnings should be punished.
	 * 
	 * @param amount Warnings' amount
	 * @return Whether the amount of warnings should be punished
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public boolean isAmountPunished(int amount) {
		return punishCommands.containsKey(amount);
	}
	
	/**
	 * Gets the max amount of warnings executed before calling
	 * {@link #clearWarnings(OfflinePlayer, String, String)}.
	 * 
	 * @return Max warnings' amount
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public int getMaxAmount() {
		return maxAmount;
	}
	
	/**
	 * Checks if warnings should be global by default.
	 * 
	 * <p><strong>Found at:</strong> "warning.default-global" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Whether warnings should be global
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public boolean isDefaultGlobal() {
		return defaultGlobal;
	}
	
	/**
	 * Gets the warnings' duration, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "warning.duration" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Warnings' duration
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public long getDuration() {
		return duration;
	}
	
	/**
	 * Checks if a player is warned in the specified server.
	 * Specify <code>null</code> as <code>server</code> to check global bans.
	 * 
	 * @param player Player to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return Whether the player is warned
	 */
	public boolean isWarned(OfflinePlayer player, @Nullable(why = "Null to check global bans") String server) {
		return !getActiveWarnings(player, server).isEmpty();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static WarningManager getInstance() {
		return instance;
	}
	
	/**
	 * Warns a player.
	 * 
	 * @param player Player to warn
	 * @param staffMember Who warned the player
	 * @param reason Warning's reason, nullable
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param duration Duration, in milliseconds
	 * @param global Whether this warning is global
	 * @param silent Whether this warning is silent
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &&
	 * {@link ProxyManager#isEnabled()} && {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @throws UnsupportedOperationException If <code>{@link OfflinePlayer#isOnline()}
	 * && !{@link OfflinePlayer#isLoaded()}</code>
	 * @see WarningEvent
	 */
	public abstract void warn(
			OfflinePlayer player,
			@NotNull String staffMember,
			@Nullable(why = "Reason may not be specified") String reason,
			@NotNull String server,
			long duration,
			boolean global,
			boolean silent
			);
	
	/**
	 * Unwarns a player based on their warning's ID.
	 * 
	 * @param id Warning's ID
	 * @param whoUnwarned Who unwarned the player
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &&
	 * {@link ProxyManager#isEnabled()} && {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @see UnwarnEvent
	 */
	public abstract void unwarn(
			int id,
			@NotNull String whoUnwarned
			);
	
	/**
	 * Removes a player's last active warning.
	 * Specify <code>null</code> as <code>server</code> to disactive a global warning.
	 * 
	 * @param player Player to unwarn
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param whoUnwarned Who unwarned the player
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &&
	 * {@link ProxyManager#isEnabled()} && {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @throws UnsupportedOperationException If <code>{@link OfflinePlayer#isOnline()}
	 * && !{@link OfflinePlayer#isLoaded()}</code>
	 * @see UnwarnEvent
	 * @see RemoveLastWarningEvent
	 */
	public abstract void removeLastWarning(
			OfflinePlayer player,
			@Nullable(why = "Null to disactive a global warning") String server,
			@NotNull String whoUnwarned
			);
	
	/**
	 * Clears a player's active warnings.
	 * Specify <code>null</code> as <code>server</code> to disactive global warnings.
	 * 
	 * @param player Player to unwarn
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param whoUnwarned Who unwarned the player
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &&
	 * {@link ProxyManager#isEnabled()} && {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @throws UnsupportedOperationException If <code>{@link OfflinePlayer#isOnline()}
	 * && !{@link OfflinePlayer#isLoaded()}</code>
	 * @see UnwarnEvent
	 * @see ClearWarningsEvent
	 */
	public abstract void clearWarnings(
			OfflinePlayer player,
			@Nullable(why = "Null to disactive global warnings") String server,
			@NotNull String whoUnwarned
			);
	
	/**
	 * Gets the active warnings' list for the specified player.
	 * The returned list is sorted with the oldest warning in the first position.
	 * Will return an empty list if the player is not warned in any server.
	 * 
	 * @param player Player to check
	 * @return Player's warnings
	 */
	@NotNull
	public abstract List<Warning> getActiveWarnings(OfflinePlayer player);
	
	/**
	 * Gets the active warnings' list for the specified player and server.
	 * The returned list is sorted with the oldest warning in the first position.
	 * Will return an empty list if the player is not warned in that server.
	 * Specify <code>null</code> as <code>server</code> to check global warnings.
	 * 
	 * @param player Player to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return Player's warnings
	 */
	@NotNull
	public abstract List<Warning> getActiveWarnings(OfflinePlayer player, @Nullable(why = "Null to check global warnings") String server);
	
	/**
	 * Gets the active warning for the specified ID.
	 * Will return <code>null</code> if the warning is not active.
	 * 
	 * @param id Warning's ID
	 * @return Warning object
	 */
	@Nullable(why = "Specified warning may not be active")
	public abstract Warning getActiveWarning(int id);
	
	/**
	 * Gets a warning from the database.
	 * Will return <code>null</code> if the warning does not exist.
	 * This method is equivalent to {@link StorageConnector#getWarning(int)}
	 * with the difference that it suppresses {@link SQLException}s.
	 * 
	 * @param id Warning's ID
	 * @return Warning object
	 */
	@Nullable(why = "Specified warning may not exist")
	public abstract Warning getWarning(int id);
	
	/**
	 * Gets the punish commands that should be executed for the
	 * specified warnings' amount translated for the given language.
	 * 
	 * @param warnings Warnings' amount
	 * @param language Language for which to get the commands
	 * @return Punish commands' list
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	@NotNull
	public abstract List<String> getPunishCommands(int warnings, Language language);
	
	/**
	 * Formats a warning's active message using the message set in the specified language's messages file.
	 * 
	 * @param active Whether the warning is active
	 * @param language Language used to translate the message
	 * @return Formatted type message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatActiveMessage(boolean active, Language language);
	
	/**
	 * Formats a warning's global message using the message set in the specified language's messages file.
	 * 
	 * @param global Whether the warning is global
	 * @param language Language used to translate the message
	 * @return Formatted global message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatGlobalMessage(boolean global, Language language);
	
	/**
	 * Formats a warning's silent message using the message set in the specified language's messages file.
	 * 
	 * @param silent Whether the warning is silent
	 * @param language Language used to translate the message
	 * @return Formatted silent message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatSilentMessage(boolean silent, Language language);
	
}
