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

package me.remigio07.chatplugin.api.common.punishment.mute;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.remigio07.chatplugin.api.common.event.punishment.mute.MuteEvent;
import me.remigio07.chatplugin.api.common.event.punishment.mute.MuteUpdateEvent;
import me.remigio07.chatplugin.api.common.event.punishment.mute.UnmuteEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.PunishmentManager;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Manager that handles {@link Mute}s and interacts with the database.
 */
public abstract class MuteManager extends PunishmentManager {
	
	protected static MuteManager instance;
	protected List<Mute> mutes = new ArrayList<>();
	protected boolean defaultGlobal;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "mute.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the active mutes' list.
	 * Do not modify the returned list.
	 * 
	 * @return Active mutes' list
	 */
	public List<Mute> getActiveMutes() {
		return mutes;
	}
	
	/**
	 * Checks if mutes should be global by default.
	 * 
	 * <p><strong>Found at:</strong> "mute.default-global" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Whether mutes should be global
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public boolean isDefaultGlobal() {
		return defaultGlobal;
	}
	
	/**
	 * Checks if a player is muted in the specified server.
	 * Specify <code>null</code> as <code>server</code> to check global mutes.
	 * 
	 * @param player Player to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return Whether the player is muted
	 */
	public boolean isMuted(OfflinePlayer player, @Nullable(why = "Null to check global mutes") String server) {
		return getActiveMute(player, server) != null;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static MuteManager getInstance() {
		return instance;
	}
	
	/**
	 * Mutes a player.
	 * 
	 * @param player Player to mute
	 * @param staffMember Who muted the player
	 * @param reason Mute's reason, nullable
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param duration Duration, in milliseconds; -1 = permanent
	 * @param global Whether this mute is global
	 * @param silent Whether this mute is silent
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @throws UnsupportedOperationException If <code>{@link OfflinePlayer#isOnline()}
	 * &amp;&amp; !{@link OfflinePlayer#isLoaded()}</code>
	 * @see MuteEvent
	 * @see MuteUpdateEvent
	 */
	public abstract void mute(
			OfflinePlayer player,
			@NotNull String staffMember,
			@Nullable(why = "Reason may not be specified") String reason,
			@NotNull String server,
			long duration,
			boolean global,
			boolean silent
			);
	
	/**
	 * Unmutes a player.
	 * Specify <code>null</code> as <code>server</code> to disactive a global mute.
	 * 
	 * @param player Player to unmute
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param whoUnmuted Who unmuted the player
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @throws UnsupportedOperationException If <code>{@link OfflinePlayer#isOnline()}
	 * &amp;&amp; !{@link OfflinePlayer#isLoaded()}</code>
	 * @see UnmuteEvent
	 */
	public abstract void unmute(
			OfflinePlayer player,
			@Nullable(why = "Null to disactive a global mute") String server,
			@NotNull String whoUnmuted
			);
	
	/**
	 * Unmutes a player based on their mute's ID.
	 * 
	 * @param id Mute's ID
	 * @param whoUnmuted Who unmuted the player
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @see UnmuteEvent
	 */
	public abstract void unmute(
			int id,
			@NotNull String whoUnmuted
			);
	
	/**
	 * Gets the active mutes' list for the specified player.
	 * Will return an empty list if the player is not muted in any server.
	 * 
	 * @param player Player to check
	 * @return Player's mutes
	 */
	@NotNull
	public abstract List<Mute> getActiveMutes(OfflinePlayer player);
	
	/**
	 * Gets the active mute for the specified player and server.
	 * Will return <code>null</code> if the player is not muted in that server.
	 * Specify <code>null</code> as <code>server</code> to check global mutes.
	 * 
	 * @param player Player to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return Player's mute
	 */
	@Nullable(why = "Player may not be muted in the specified server")
	public abstract Mute getActiveMute(OfflinePlayer player, @Nullable(why = "Null to check global mutes") String server);
	
	/**
	 * Gets the active mute for the specified ID.
	 * Will return <code>null</code> if the mute is not active.
	 * 
	 * @param id Mute's ID
	 * @return Mute object
	 */
	@Nullable(why = "Specified mute may not be active")
	public abstract Mute getActiveMute(int id);
	
	/**
	 * Gets a mute from the database.
	 * Will return <code>null</code> if the mute does not exist.
	 * This method is equivalent to {@link StorageConnector#getMute(int)}
	 * with the difference that it suppresses {@link SQLException}s.
	 * 
	 * @param id Mute's ID
	 * @return Mute object
	 */
	@Nullable(why = "Specified mute may not exist")
	public abstract Mute getMute(int id);
	
	/**
	 * Formats a mute's active message using the message set in the specified language's messages file.
	 * 
	 * @param active Whether the mute is active
	 * @param language Language used to translate the message
	 * @return Formatted type message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatActiveMessage(boolean active, Language language);
	
	/**
	 * Formats a mute's global message using the message set in the specified language's messages file.
	 * 
	 * @param global Whether the mute is global
	 * @param language Language used to translate the message
	 * @return Formatted global message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatGlobalMessage(boolean global, Language language);
	
	/**
	 * Formats a mute's silent message using the message set in the specified language's messages file.
	 * 
	 * @param silent Whether the mute is silent
	 * @param language Language used to translate the message
	 * @return Formatted silent message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatSilentMessage(boolean silent, Language language);
	
}
