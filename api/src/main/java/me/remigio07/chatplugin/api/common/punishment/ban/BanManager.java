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

package me.remigio07.chatplugin.api.common.punishment.ban;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.remigio07.chatplugin.api.common.event.punishment.ban.BanEvent;
import me.remigio07.chatplugin.api.common.event.punishment.ban.BanUpdateEvent;
import me.remigio07.chatplugin.api.common.event.punishment.ban.UnbanEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.PunishmentManager;
import me.remigio07.chatplugin.api.common.punishment.kick.KickType;
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
 * Manager that handles {@link Ban}s and interacts with the storage.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#bans">ChatPlugin wiki/Modules/Punishments/Bans</a>
 */
public abstract class BanManager extends PunishmentManager {
	
	protected static BanManager instance;
	protected List<Ban> bans = new CopyOnWriteArrayList<>();
	protected boolean defaultGlobal;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "ban.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the active bans' list.
	 * 
	 * <p>Do <strong>not</strong> modify the returned list.</p>
	 * 
	 * @return Active bans' list
	 */
	public List<Ban> getActiveBans() {
		return bans;
	}
	
	/**
	 * Checks if bans should be global by default.
	 * 
	 * <p><strong>Found at:</strong> "ban.default-global" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Whether bans should be global
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public boolean isDefaultGlobal() {
		return defaultGlobal;
	}
	
	/**
	 * Checks if a player is banned in the specified server.
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to check global bans.</p>
	 * 
	 * @param player Player to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return Whether the player is banned
	 */
	public boolean isBanned(OfflinePlayer player, @Nullable(why = "Null to check global bans") String server) {
		return getActiveBan(player, server) != null;
	}
	
	/**
	 * Checks if an IP address is banned in the specified server.
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to check global bans.</p>
	 * 
	 * @param ipAddress IP address to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return Whether the IP address is banned
	 */
	public boolean isBanned(InetAddress ipAddress, @Nullable(why = "Null to check global bans") String server) {
		return getActiveBan(ipAddress, server) != null;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static BanManager getInstance() {
		return instance;
	}
	
	/**
	 * Bans a player.
	 * 
	 * @param player Player to ban
	 * @param staffMember Who banned the player
	 * @param reason Ban's reason, nullable
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param duration Duration, in milliseconds; -1 = permanent
	 * @param global Whether this ban is global
	 * @param silent Whether this ban is silent
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @throws UnsupportedOperationException If <code>{@link OfflinePlayer#isOnline()}
	 * &amp;&amp; !{@link OfflinePlayer#isLoaded()}</code>
	 * @see BanEvent
	 * @see BanUpdateEvent
	 */
	public abstract void ban(
			OfflinePlayer player,
			@NotNull String staffMember,
			@Nullable(why = "Reason may not be specified") String reason,
			@NotNull String server,
			long duration,
			boolean global,
			boolean silent
			);
	
	/**
	 * Bans a player by their IP address.
	 * 
	 * @param player Player to ban
	 * @param staffMember Who banned the player
	 * @param reason Ban's reason, nullable
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param duration Duration, in milliseconds; -1 = permanent
	 * @param global Whether this ban is global
	 * @param silent Whether this ban is silent
	 * @throws IllegalArgumentException If <code>!</code>{@link OfflinePlayer#hasPlayedBefore()}
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @throws UnsupportedOperationException If <code>{@link OfflinePlayer#isOnline()}
	 * &amp;&amp; !{@link OfflinePlayer#isLoaded()}</code>
	 * @see BanEvent
	 * @see BanUpdateEvent
	 */
	public abstract void banIP(
			OfflinePlayer player,
			@NotNull String staffMember,
			@Nullable(why = "Reason may not be specified") String reason,
			@NotNull String server,
			long duration,
			boolean global,
			boolean silent
			);
	
	/**
	 * Bans an IP address.
	 * 
	 * @param ipAddress IP address to ban
	 * @param staffMember Who banned the IP address
	 * @param reason Ban's reason, nullable
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param duration Duration, in milliseconds; -1 = permanent
	 * @param global Whether this ban is global
	 * @param silent Whether this ban is silent
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @throws UnsupportedOperationException If <code>{@link OfflinePlayer#isOnline()}
	 * &amp;&amp; !{@link OfflinePlayer#isLoaded()}</code>
	 * @see BanEvent
	 * @see BanUpdateEvent
	 */
	public abstract void banIP(
			InetAddress ipAddress,
			@NotNull String staffMember,
			@Nullable(why = "Reason may not be specified") String reason,
			@NotNull String server,
			long duration,
			boolean global,
			boolean silent
			);
	
	/**
	 * Unbans a player.
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to disactive a global ban.</p>
	 * 
	 * @param player Player to unban
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param whoUnbanned Who unbanned the player
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @throws UnsupportedOperationException If <code>{@link OfflinePlayer#isOnline()}
	 * &amp;&amp; !{@link OfflinePlayer#isLoaded()}</code>
	 * @see UnbanEvent
	 */
	public abstract void unban(
			OfflinePlayer player,
			@Nullable(why = "Null to disactive a global ban") String server,
			@NotNull String whoUnbanned
			);
	
	/**
	 * Unbans an IP address.
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to disactive a global ban.</p>
	 * 
	 * @param ipAddress IP address to unban
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param whoUnbanned Who unbanned the IP address
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @see UnbanEvent
	 */
	public abstract void unbanIP(
			InetAddress ipAddress,
			@Nullable(why = "Null to disactive a global ban") String server,
			@NotNull String whoUnbanned
			);
	
	/**
	 * Unbans a player based on their ban's ID.
	 * 
	 * @param id Ban's ID
	 * @param whoUnbanned Who unbanned the player
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 * @see UnbanEvent
	 */
	public abstract void unban(
			int id,
			@NotNull String whoUnbanned
			);
	
	/**
	 * Gets the active bans' list for the specified player.
	 * 
	 * <p>Will return an empty list if the player is not banned in any server.</p>
	 * 
	 * @param player Player to check
	 * @return Player's bans
	 */
	@NotNull
	public abstract List<Ban> getActiveBans(OfflinePlayer player);
	
	/**
	 * Gets the active bans' list for the specified IP address.
	 * 
	 * <p>Will return an empty list if the IP address is not banned in any server.</p>
	 * 
	 * @param ipAddress IP address to check
	 * @return IP address' bans
	 */
	@NotNull
	public abstract List<Ban> getActiveBans(InetAddress ipAddress);
	
	/**
	 * Gets the active ban for the specified player and server.
	 * 
	 * <p>Will return <code>null</code> if the player is not banned in that server.</p>
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to check global bans.</p>
	 * 
	 * @param player Player to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return Player's ban
	 */
	@Nullable(why = "Player may not be banned in the specified server")
	public abstract Ban getActiveBan(OfflinePlayer player, @Nullable(why = "Null to check global bans") String server);
	
	/**
	 * Gets the active ban for the specified IP address and server.
	 * 
	 * <p>Will return <code>null</code> if the IP address is not banned in that server.</p>
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to check global bans.</p>
	 * 
	 * @param ipAddress IP address to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return IP address' ban
	 */
	@Nullable(why = "IP address may not be banned in the specified server")
	public abstract Ban getActiveBan(InetAddress ipAddress, @Nullable(why = "Null to check global bans") String server);
	
	/**
	 * Gets the active ban for the specified ID.
	 * 
	 * <p>Will return <code>null</code> if the ban is not active.</p>
	 * 
	 * @param id Ban's ID
	 * @return Ban object
	 */
	@Nullable(why = "Specified ban may not be active")
	public abstract Ban getActiveBan(int id);
	
	/**
	 * Gets a ban from the database.
	 * 
	 * <p>Will return <code>null</code> if the ban does not exist.</p>
	 * 
	 * <p>This method is equivalent to {@link StorageConnector#getBan(int)}
	 * with the difference that it suppresses {@link SQLException}s.</p>
	 * 
	 * @param id Ban's ID
	 * @return Ban object
	 */
	@Nullable(why = "Specified ban may not exist")
	public abstract Ban getBan(int id);
	
	/**
	 * Formats a ban's type message using the message
	 * set in the specified language's messages file.
	 * 
	 * @param type Type to format
	 * @param language Language used to translate the message
	 * @return Formatted type message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatTypeMessage(BanType type, Language language);
	
	/**
	 * Formats a ban's active message using the message
	 * set in the specified language's messages file.
	 * 
	 * @param active Whether the ban is active
	 * @param language Language used to translate the message
	 * @return Formatted type message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatActiveMessage(boolean active, Language language);
	
	/**
	 * Formats a ban's global message using the message
	 * set in the specified language's messages file.
	 * 
	 * @param global Whether the ban is global
	 * @param language Language used to translate the message
	 * @return Formatted global message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatGlobalMessage(boolean global, Language language);
	
	/**
	 * Formats a ban's silent message using the message
	 * set in the specified language's messages file.
	 * 
	 * @param silent Whether the ban is silent
	 * @param language Language used to translate the message
	 * @return Formatted silent message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatSilentMessage(boolean silent, Language language);
	
	/**
	 * Formats a ban's kick message using the message
	 * set in the specified language's messages file.
	 * 
	 * @param type Type to format
	 * @param language Language used to translate the message
	 * @return Formatted kick message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatKickMessage(KickType type, Language language);
	
}
