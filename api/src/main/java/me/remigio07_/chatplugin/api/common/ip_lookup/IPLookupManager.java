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

package me.remigio07_.chatplugin.api.common.ip_lookup;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import me.remigio07_.chatplugin.api.common.event.ip_lookup.IPLookupCacheEvent;
import me.remigio07_.chatplugin.api.common.event.ip_lookup.IPLookupCleanCacheEvent;
import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.Library;
import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.annotation.SensitiveData;
import me.remigio07_.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07_.chatplugin.bootstrap.Environment;

/**
 * Manager that handles {@link IPLookup}s. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/IP-lookup">ChatPlugin wiki/IP lookup</a>
 * 
 * @see #getIPLookup(String)
 */
public abstract class IPLookupManager implements ChatPluginManager {
	
	/**
	 * Array containing all the libraries required for this module to work.
	 * 
	 * <p><strong>Content:</strong> [{@link Library#MAXMIND_DB_READER}, {@link Library#MAXMIND_GEOIP2_API}, {@link Library#APACHE_HTTPCLIENT}, {@link Library#JACKSON_CORE}, {@link Library#JACKSON_ANNOTATIONS}, {@link Library#JACKSON_DATABIND}, {@link Library#ACTIVATION_FRAMEWORK}, {@link Library#ICE_TAR}]</p>
	 */
	public static final Library[] LIBRARIES = new Library[] { Library.MAXMIND_DB_READER, Library.MAXMIND_GEOIP2_API, Library.APACHE_HTTPCLIENT, Library.JACKSON_CORE, Library.JACKSON_ANNOTATIONS, Library.JACKSON_DATABIND, Library.ACTIVATION_FRAMEWORK, Library.ICE_TAR };
	protected static IPLookupManager instance;
	protected static final String URL = "https://geolite.info/geoip/v2.1/city/{0}";
	protected boolean enabled, loadOnJoin;
	protected IPLookupMethod method;
	protected long cacheTime;
	@SensitiveData(warning = "MaxMind account's personal information")
	protected String maxMindUserID, base64AuthString;
	protected int maxIPsStored;
	protected IPLookup disabledFeatureConstructor;
	protected Map<InetAddress, IPLookup> cache = new HashMap<>();
	protected Map<InetAddress, CompletableFuture<IPLookup>> pendingFutures = new HashMap<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "ip-lookup.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if a lookup should be performed every time a player joins.
	 * 
	 * <p><strong>Found at:</strong> "ip-lookup.load-on-join" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Whether a lookup is loaded on every join
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public boolean isLoadOnJoin() {
		return loadOnJoin;
	}
	
	/**
	 * Gets the IP lookup method currently in use.
	 * 
	 * <p><strong>Found at:</strong> "ip-lookup.method" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Current IP lookup method
	 */
	public IPLookupMethod getMethod() {
		return method;
	}
	
	/**
	 * Gets the time a lookup will be cached for, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "ip-lookup.cache-time" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return IP lookups' cache time
	 */
	public long getCacheTime() {
		return cacheTime;
	}
	
	/**
	 * Gets the current MaxMind account's user ID.
	 * 
	 * <p><strong>Found at:</strong> "ip-lookup.maxmind-account.user-id" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return MaxMind account's user ID
	 */
	@SensitiveData(warning = "MaxMind account's personal information")
	public String getMaxMindUserID() {
		return maxMindUserID;
	}
	
	/**
	 * Gets the Base64 string used for authentication on MaxMind's website.
	 * 
	 * @return Base64 auth string
	 */
	@SensitiveData(warning = "MaxMind account's personal information")
	public String getBase64AuthString() {
		return base64AuthString;
	}
	
	/**
	 * Gets the maximum amount of IP addresses stored for each player.
	 * 
	 * <p><strong>Found at:</strong> "ip-lookup.max-ips-stored" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Max IPs stored
	 */
	public int getMaxIPsStored() {
		return maxIPsStored;
	}
	
	/**
	 * Gets a fake IP lookup used to replace every placeholder when this module is disabled.
	 * 
	 * @return IP lookup with default values
	 */
	public IPLookup getDisabledFeatureConstructor() {
		return disabledFeatureConstructor;
	}
	
	/**
	 * Gets the URL used for remote IP lookups (through MaxMind's website).
	 * You should replace "{0}" with the IP address you want to get the lookup for.
	 * 
	 * @return Lookup request URL
	 */
	public String getURL() {
		return URL;
	}
	
	/**
	 * Gets the cached IP lookups. The returned map's keys are IPv4
	 * addresses and the values are the corresponding IP lookups.
	 * 
	 * @return Cached lookups
	 */
	public Map<InetAddress, IPLookup> getCache() {
		return cache;
	}
	
	/**
	 * Gets a cached IP lookup. Will return <code>null</code>
	 * if the specified IP address is not cached.
	 * 
	 * @param ipAddress Cached IP address 
	 * @return Cached lookup
	 */
	@Nullable(why = "IP address may not be cached")
	public IPLookup getFromCache(InetAddress ipAddress) {
		return cache.get(ipAddress);
	}
	
	/**
	 * Gets the pending futures' map. Every entry is composed of an {@link InetAddress}
	 * which represents an IPv4 and a {@link CompletableFuture} holding an {@link IPLookup}.
	 * 
	 * @deprecated Internal use only.
	 * @return Pending futures' map
	 */
	@Deprecated
	public Map<InetAddress, CompletableFuture<IPLookup>> getPendingFutures() {
		return pendingFutures;
	}
	
	/**
	 * Calls {@link #getIPLookup(InetAddress, String)} specifying
	 * <code>null</code> as <code>requesterName</code>.
	 * 
	 * @param ipAddress IP address to check
	 * @return Lookup for the IP address
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} && {@link ProxyManager#isEnabled()}
	 * && {@link #getFromCache(String)} == null && {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 */
	public CompletableFuture<IPLookup> getIPLookup(InetAddress ipAddress) {
		return getIPLookup(ipAddress, null);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static IPLookupManager getInstance() {
		return instance;
	}
	
	/**
	 * Gets an IP lookup for the specified IPv4 address.
	 * Returned lookup will be also {@link #putInCache(String, IPLookup)}.
	 * 
	 * <p>You can specify <code>null</code> as <code>requesterName</code>
	 * to prevent the proxy to send them the message found at "misc.disabled-feature" if its
	 * manager is not enabled, otherwise it will try to do it. Specify "CONSOLE" for the console.</p>
	 * 
	 * <p>Note that this method might take some time to be executed: async calls are recommended.</p>
	 * 
	 * @param ipAddress IP address to check
	 * @return Lookup for the IP address
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} && {@link ProxyManager#isEnabled()}
	 * && {@link #getFromCache(String)} == null && {@link PlayerAdapter#getOnlinePlayers()}.size() == 0</code>
	 */
	public abstract CompletableFuture<IPLookup> getIPLookup(InetAddress ipAddress, @Nullable(why = "Requester may not be specified") String requesterName);
	
	/**
	 * Adds an IP address to {@link #getCache()}. Will overwrite an
	 * existing entry identified by the specified IP if already present.
	 * 
	 * @param ipAddress IP address to add
	 * @param lookup IP lookup to cache
	 * @see IPLookupCacheEvent
	 */
	public abstract void putInCache(InetAddress ipAddress, @NotNull IPLookup lookup);
	
	/**
	 * Removes an IP address from {@link #getCache()}.
	 * Will do nothing if it was not cached.
	 * 
	 * @param ipAddress Cached IP address
	 * @see IPLookupCleanCacheEvent
	 */
	public abstract void removeFromCache(InetAddress ipAddress);
	
	/**
	 * Reads the specified URL's content. Designed to read MaxMind website's IP lookups only.
	 * 
	 * @param url The URL to check
	 * @return The URL's content (should be a JSON string)
	 * @throws IOException If something goes wrong
	 */
	public abstract String readURL(String url) throws IOException;
	
}
