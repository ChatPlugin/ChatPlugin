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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.common.ip_lookup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.HttpsURLConnection;

import me.remigio07.chatplugin.api.common.event.ip_lookup.IPLookupCacheEvent;
import me.remigio07.chatplugin.api.common.event.ip_lookup.IPLookupCleanCacheEvent;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupMethod;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

public abstract class BaseIPLookupManager extends IPLookupManager {
	
	protected long ms;
	
	protected boolean load0() throws ChatPluginManagerException {
		instance = this;
		ms = System.currentTimeMillis();
		disabledFeatureConstructor = new IPLookupImpl();
		
		if (!ConfigurationType.CONFIG.get().getBoolean("ip-lookup.enabled"))
			return false;
		loadOnJoin = ConfigurationType.CONFIG.get().getBoolean("ip-lookup.load-on-join");
		
		if (Environment.isProxy() || !ProxyManager.getInstance().isEnabled()) {
			try {
				method = IPLookupMethod.valueOf(ConfigurationType.CONFIG.get().getString("ip-lookup.method").toUpperCase());
			} catch (IllegalArgumentException e) {
				LogManager.log(
						"Invalid IP lookup method (\"{0}\") set at \"ip-lookup.method\" in config.yml: only LOCAL and REMOTE are allowed; setting to default value of REMOTE.",
						2,
						ConfigurationType.CONFIG.get().getString("ip-lookup.method")
						);
				
				method = IPLookupMethod.REMOTE;
			} cacheTime = Utils.getTime(ConfigurationType.CONFIG.get().getString("ip-lookup.cache-time"), false);
			maxMindUserID = ConfigurationType.CONFIG.get().getString("ip-lookup.maxmind-account.user-id");
			maxIPsStored = ConfigurationType.CONFIG.get().getInt("ip-lookup.max-ips-stored");
			
			if (maxMindUserID.isEmpty()) {
				LogManager.log("IP lookup is enabled but the MaxMind user ID set in the config at \"ip-lookup.maxmind-account.user-id\" is empty. Insert one and reload the plugin.", 1);
				unload();
				return false;
			} else base64AuthString = Base64.getEncoder().encodeToString((maxMindUserID + ":" + ConfigurationType.CONFIG.get().getString("ip-lookup.maxmind-account.key")).getBytes());
			
			if (maxIPsStored < 1 || maxIPsStored > 15) {
				LogManager.log("Invalid max IPs stored amount ({0}) set at \"ip-lookup.max-stored-ips\" in config.yml: only values between 1 and 15 are permitted; setting to default value of 5.", 1, maxIPsStored);
				maxIPsStored = 5;
			}
		} return true;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		cache.clear();
		
		loadOnJoin = false;
		method = null;
		cacheTime = 0L;
		maxMindUserID = base64AuthString = null;
		disabledFeatureConstructor = null;
	}
	
	@Override
	public CompletableFuture<IPLookup> getIPLookup(InetAddress ipAddress, @Nullable(why = "Requester may not be specified") String requesterName) {
		CompletableFuture<IPLookup> ipLookup = new CompletableFuture<>();
		
		if (!enabled)
			ipLookup.complete(disabledFeatureConstructor);
		else if (cache.containsKey(ipAddress))
			ipLookup.complete(cache.get(ipAddress));
		else if (Environment.isProxy() || !ProxyManager.getInstance().isEnabled())
			TaskManager.runAsync(() -> ipLookup.complete(new IPLookupImpl(ipAddress, true)), 0L);
		else if (PlayerAdapter.getOnlinePlayers().size() != 0) {
			pendingFutures.put(ipAddress, ipLookup);
			ProxyManager.getInstance().sendPluginMessage(Packets.Sync.ipLookupRequest(
					ProxyManager.getInstance().getServerID(),
					ipAddress,
					requesterName
					));
		} else throw new IllegalStateException("Unable send IPLookupRequest plugin message with no players online");
		return ipLookup;
	}
	
	@Override
	public void putInCache(InetAddress ipAddress, @NotNull IPLookup lookup) {
		cache.put(ipAddress, lookup);
		new IPLookupCacheEvent(lookup).call();
	}
	
	@Override
	public void removeFromCache(InetAddress ipAddress) {
		if (!cache.containsKey(ipAddress))
			return;
		IPLookupCleanCacheEvent event = new IPLookupCleanCacheEvent(getFromCache(ipAddress));
		
		event.call();
		
		if (!event.isCancelled())
			cache.remove(ipAddress);
	}
	
	@Override
	public String readURL(String url) throws IOException {
		HttpsURLConnection con = (HttpsURLConnection) new URL(url).openConnection();
		
		con.setRequestMethod("GET");
		con.addRequestProperty("Authorization", "Basic " + base64AuthString);
		con.addRequestProperty("Content-Type", "application/json");
		
		if (con.getResponseCode() != 200) {
			if (con.getResponseCode() != 400)
				LogManager.log(con.getResponseCode() + " - " + con.getResponseMessage() + " returned while querying MaxMind's APIs to geolocate an IP address.", 2);
			return "Error: " + con.getResponseCode() + " - " + con.getResponseMessage();
		} StringBuilder output = new StringBuilder();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		
		while ((line = in.readLine()) != null)
			output.append(line);
		in.close();
		con.disconnect();
		return output.toString();
	}
	
}
