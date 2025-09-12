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

package me.remigio07.chatplugin.api.common.punishment.ban.banwave;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.remigio07.chatplugin.api.common.event.punishment.ban.banwave.BanwaveEndEvent;
import me.remigio07.chatplugin.api.common.event.punishment.ban.banwave.BanwaveEntryAddEvent;
import me.remigio07.chatplugin.api.common.event.punishment.ban.banwave.BanwaveEntryRemoveEvent;
import me.remigio07.chatplugin.api.common.event.punishment.ban.banwave.BanwaveEntryUpdateEvent;
import me.remigio07.chatplugin.api.common.event.punishment.ban.banwave.BanwaveStartEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.annotation.ProxyImplementationOnly;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.common.util.text.ComponentTranslator;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Manager that handles {@link BanwaveEntry}(i)es and interacts with the storage.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#banwaves">ChatPlugin wiki/Punishments/Bans/Banwaves</a>
 */
public abstract class BanwaveManager implements ChatPluginManager, Runnable {
	
	protected static BanwaveManager instance;
	protected static ComponentTranslator componentTranslator;
	protected boolean enabled, running, announce, forwardProxyCommands;
	protected long timeout, delay;
	protected List<String> reasonsStartWith, startCommands, endCommands;
	protected List<BanwaveEntry> entries = new CopyOnWriteArrayList<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "ban.banwave.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if a banwave is being executed.
	 * 
	 * @return Whether a banwave is running
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Checks if banwave start messages should be announced.
	 * 
	 * <p><strong>Found at:</strong> "ban.banwave.announce" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Whether start messages should be announced
	 */
	public boolean isAnnounce() {
		return announce;
	}
	
	/**
	 * Checks if commands set in the proxy's config should
	 * be forwarded to the servers under the network.
	 * 
	 * <p><strong>Found at:</strong> "ban.banwave.forward-proxy-commands" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Whether proxy's commands should be forwarded
	 */
	@ProxyImplementationOnly(why = ProxyImplementationOnly.SETTINGS_NOT_PRESENT)
	public boolean isForwardProxyCommands() {
		return forwardProxyCommands;
	}
	
	/**
	 * Gets the timeout between banwaves, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "ban.banwave.timeout" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Time between banwaves
	 */
	public long getTimeout() {
		return timeout;
	}
	
	/**
	 * Gets the delay to wait after each ban, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "ban.banwave.delay-ms" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Time between bans
	 */
	public long getDelay() {
		return delay;
	}
	
	/**
	 * Gets the list of strings a ban's reason has to start with
	 * to be considered a banwave entry instead of a normal ban.
	 * 
	 * <p><strong>Found at:</strong> "ban.banwave.reasons-start-with" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Banwave's reasons
	 * @see #isBanwaveReason(String)
	 */
	public List<String> getReasonsStartWith() {
		return reasonsStartWith;
	}
	
	/**
	 * Gets the list of commands executed when starting a banwave.
	 * 
	 * <p><strong>Found at:</strong> "ban.banwave.commands.start" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Banwave's start commands
	 */
	public List<String> getStartCommands() {
		return startCommands;
	}
	
	/**
	 * Gets the list of commands executed when a banwave has executed all bans and stopped.
	 * 
	 * <p><strong>Found at:</strong> "ban.banwave.commands.end" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Banwave's end commands
	 */
	public List<String> getEndCommands() {
		return endCommands;
	}
	
	/**
	 * Gets the banwave entries' list.
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * @return Banwave entries' list
	 */
	public List<BanwaveEntry> getEntries() {
		return entries;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static BanwaveManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic banwave executor, called once every {@link #getTimeout()} ms.
	 * 
	 * @see BanwaveStartEvent
	 * @see BanwaveEndEvent
	 */
	@Override
	public abstract void run();
	
	/**
	 * Adds a new banwave entry which will be executed by the next banwave.
	 * 
	 * @param player Player to ban
	 * @param staffMember Who banned the player
	 * @param reason Ban's reason, nullable
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param duration Duration, in milliseconds; -1 = permanent
	 * @param global Whether this ban is global
	 * @param silent Whether this ban is silent
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.isEmpty()</code>
	 * @see BanwaveEntryAddEvent
	 * @see BanwaveEntryUpdateEvent
	 */
	public abstract void addEntry(
			OfflinePlayer player,
			@NotNull String staffMember,
			@Nullable(why = "Reason may not be specified") String reason,
			@NotNull String server,
			long duration,
			boolean global,
			boolean silent
			);
	
	/**
	 * Adds a new banwave IP entry which will be executed by the next banwave.
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
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.isEmpty()</code>
	 * @see BanwaveEntryAddEvent
	 * @see BanwaveEntryUpdateEvent
	 */
	public abstract void addIPEntry(
			OfflinePlayer player,
			@NotNull String staffMember,
			@Nullable(why = "Reason may not be specified") String reason,
			@NotNull String server,
			long duration,
			boolean global,
			boolean silent
			);
	
	/**
	 * Adds a new banwave IP entry which will be executed by the next banwave.
	 * 
	 * @param ipAddress IP address to ban
	 * @param staffMember Who banned the IP address
	 * @param reason Ban's reason, nullable
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param duration Duration, in milliseconds; -1 = permanent
	 * @param global Whether this ban is global
	 * @param silent Whether this ban is silent
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.isEmpty()</code>
	 * @see BanwaveEntryAddEvent
	 * @see BanwaveEntryUpdateEvent
	 */
	public abstract void addIPEntry(
			InetAddress ipAddress,
			@NotNull String staffMember,
			@Nullable(why = "Reason may not be specified") String reason,
			@NotNull String server,
			long duration,
			boolean global,
			boolean silent
			);
	
	/**
	 * Removes a banwave entry not yet executed.
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to disactive a global entry.</p>
	 * 
	 * @param player Player of the entry to remove
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param whoRemoved Who removed the entry
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.isEmpty()</code>
	 * @see BanwaveEntryRemoveEvent
	 */
	public abstract void removeEntry(
			OfflinePlayer player,
			@Nullable(why = "Null to disactive a global entry") String server,
			@NotNull String whoRemoved
			);
	
	/**
	 * Removes a banwave entry not yet executed.
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to disactive a global entry.</p>
	 * 
	 * @param ipAddress Entry's IP address to remove
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param whoRemoved Who removed the entry
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.isEmpty()</code>
	 * @see BanwaveEntryRemoveEvent
	 */
	public abstract void removeIPEntry(
			InetAddress ipAddress,
			@Nullable(why = "Null to disactive a global entry") String server,
			@NotNull String whoRemoved
			);
	
	/**
	 * Gets the active entries' list for the specified player.
	 * 
	 * <p>Will return an empty list if the player is not about to be banned in any server.</p>
	 * 
	 * @param player Player to check
	 * @return Player's entries
	 */
	@NotNull
	public abstract List<BanwaveEntry> getEntries(OfflinePlayer player);
	
	/**
	 * Gets the active entries' list for the specified IP address.
	 * 
	 * <p>Will return an empty list if the IP address is not about to be banned in any server.</p>
	 * 
	 * @param ipAddress IP address to check
	 * @return IP address' entries
	 */
	@NotNull
	public abstract List<BanwaveEntry> getEntries(InetAddress ipAddress);
	
	/**
	 * Gets the entry for the specified player and server.
	 * 
	 * <p>Will return <code>null</code> if the player is not about to be banned in that server.</p>
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to check global entries.</p>
	 * 
	 * @param player Player to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return Player's entry
	 */
	@Nullable(why = "Player may not be banned in the specified server")
	public abstract BanwaveEntry getEntry(OfflinePlayer player, @NotNull String server);
	
	/**
	 * Gets the entry for the specified IP address and server.
	 * 
	 * <p>Will return <code>null</code> if the IP address is not about to be banned in that server.</p>
	 * 
	 * <p>Specify <code>null</code> as <code>server</code> to check global entries.</p>
	 * 
	 * @param ipAddress IP address to check
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @return Player's entry
	 */
	@Nullable(why = "IP address may not be banned in the specified server")
	public abstract BanwaveEntry getEntry(InetAddress ipAddress, @NotNull String server);
	
	/**
	 * Checks if the specified banwave entry's reason is contained in {@link #getReasonsStartWith()}.
	 * 
	 * <p>Case will be lowered and colors will be stripped using {@link ChatColor#stripColor(String)}.</p>
	 * 
	 * <p>Will return <code>false</code> if <code>reason == null</code>.</p>
	 * 
	 * @param reason Ban's reason
	 * @return Whether the reason is a banwave's reason
	 */
	public abstract boolean isBanwaveReason(@Nullable(why = "Reason may not be specified") String reason);
	
}
