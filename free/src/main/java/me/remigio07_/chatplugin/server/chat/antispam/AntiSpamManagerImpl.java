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

package me.remigio07_.chatplugin.server.chat.antispam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.net.InetAddresses;

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07_.chatplugin.api.server.chat.ChatManager;
import me.remigio07_.chatplugin.api.server.chat.antispam.AntiSpamManager;
import me.remigio07_.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.URLValidator;

public class AntiSpamManagerImpl extends AntiSpamManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.antispam.enabled"))
			return;
		urlsPreventionEnabled = ConfigurationType.CHAT.get().getBoolean("chat.antispam.prevention.urls.enabled");
		ipsPreventionEnabled = ConfigurationType.CHAT.get().getBoolean("chat.antispam.prevention.ips.enabled");
		maxCapsLength = ConfigurationType.CHAT.get().getInt("chat.antispam.max-caps-length");
		maxCapsPercent = ConfigurationType.CHAT.get().getInt("chat.antispam.max-caps-percent");
		secondsBetweenMsg = ConfigurationType.CHAT.get().getInt("chat.antispam.seconds-between-msg");
		secondsBetweenSameMsg = ConfigurationType.CHAT.get().getInt("chat.antispam.seconds-between-same-msg");
		wordsBlacklist = lowerCase(ConfigurationType.CHAT.get().getStringList("chat.antispam.words-blacklist"));
		messagesWhitelist = lowerCase(ConfigurationType.CHAT.get().getStringList("chat.antispam.messages-whitelist"));
		
		for (String allowedDomain : ConfigurationType.CHAT.get().getStringList("chat.antispam.prevention.urls.allowed-domains")) {
			String domainName = URLValidator.getDomainName(allowedDomain);
			
			if (domainName == null)
				LogManager.log("Invalid domain (\"{0}\") specified at \"chat.antispam.prevention.urls.allowed-domains\" in chat.yml; skipping it.", 1, allowedDomain);
			else allowedDomains.add(domainName);
		} for (String whitelistedURL : ConfigurationType.CHAT.get().getStringList("chat.antispam.prevention.urls.whitelist")) {
			String domainName = URLValidator.getDomainName(whitelistedURL);
			
			if (domainName == null)
				LogManager.log("Invalid URL (\"{0}\") specified at \"chat.antispam.prevention.urls.whitelist\" in chat.yml; skipping it.", 1, whitelistedURL);
			else urlsWhitelist.add(URLValidator.stripProtocol((whitelistedURL.endsWith("/") ? whitelistedURL.substring(0, whitelistedURL.length() - 1) : whitelistedURL).toLowerCase()));
		} for (String whitelistedIP : ConfigurationType.CHAT.get().getStringList("chat.antispam.prevention.ips.whitelist")) {
			if (InetAddresses.isInetAddress(whitelistedIP))
				ipsWhitelist.add(whitelistedIP);
			else LogManager.log("Invalid IPv4 (\"{0}\") specified at \"chat.antispam.prevention.ips.whitelist\" in chat.yml; skipping it.", 1, whitelistedIP);
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = urlsPreventionEnabled = ipsPreventionEnabled = false;
		
		allowedDomains.clear();
		urlsWhitelist.clear();
		ipsWhitelist.clear();
		wordsBlacklist.clear();
		messagesWhitelist.clear();
		spamCache.clear();
		floodCache.clear();
		
		maxCapsLength = maxCapsPercent = secondsBetweenMsg = secondsBetweenSameMsg = 0;
	}
	
	@Override
	public DenyChatReason getDenyChatReason(ChatPluginServerPlayer player, String message) {
		if (!enabled || player.hasPermission("chatplugin.antispam.bypass"))
			return null;
		if (urlsPreventionEnabled && containsDisallowedURL(message))
			return DenyChatReason.URL;
		if (ipsPreventionEnabled && containsDisallowedIP(message))
			return DenyChatReason.IP_ADDRESS;
		if (containsBlacklistedWord(message) && !player.hasPermission("chatplugin.antispam.swear"))
			return DenyChatReason.SWEAR;
		if (exceedsMaxCapsLength(message) && exceedsMaxCapsPercentage(message) && !player.hasPermission("chatplugin.antispam.caps"))
			return DenyChatReason.CAPS;
		UUID uuid = player.getUUID();
		
		if (!player.hasPermission("chatplugin.antispam.flood")) {
			if (floodCache.contains(uuid))
				return DenyChatReason.FLOOD;
			floodCache.add(uuid);
			TaskManager.runAsync(() -> floodCache.remove(uuid), secondsBetweenMsg * 1000L);
		} if (!isMessageWhitelisted(message) && !player.hasPermission("chatplugin.antispam.spam")) {
			if (spamCache.containsKey(uuid) && spamCache.get(uuid).contains(message))
				return DenyChatReason.SPAM;
			if (!spamCache.containsKey(uuid))
				spamCache.put(uuid, new ArrayList<>());
			spamCache.get(uuid).add(message);
			
			TaskManager.runAsync(() -> {
				List<String> messages = spamCache.getOrDefault(uuid, Collections.emptyList());
				
				messages.remove(message);
				
				if (messages.size() == 0)
					spamCache.remove(uuid);
			}, secondsBetweenSameMsg * 1000L);
		} return null;
	}
	
	@Override
	public boolean containsDisallowedURL(String message) {
		List<String> urls = URLValidator.getURLs(message);
		
		if (urls.isEmpty())
			return false;
		for (String url : urls) {
			if (!allowedDomains.contains(URLValidator.getDomainName(url)) && !urlsWhitelist.contains(URLValidator.stripProtocol((url.endsWith("/") ? url.substring(0, url.length() - 1) : url).toLowerCase())))
				return true;
		} return false;
	}
	
	@Override
	public boolean containsDisallowedIP(String message) {
		for (String arg : message.split(" "))
			if (InetAddresses.isInetAddress(arg) && !ipsWhitelist.contains(arg))
				return true;
		return false;
	}
	
	@Override
	public boolean containsBlacklistedWord(String message) {
		for (String word : message.toLowerCase().split(" "))
			if (wordsBlacklist.contains(word))
				return true;
		return false;
	}
	
	@Override
	public boolean isMessageWhitelisted(String message) {
		return messagesWhitelist.contains(message.toLowerCase());
	}
	
	@Override
	public boolean exceedsMaxCapsLength(String message) {
		return getCapsLength(message) > maxCapsLength;
	}
	
	@Override
	public boolean exceedsMaxCapsPercentage(String message) {
		return getCapsPercentage(message) > maxCapsPercent;
	}
	
	@Override
	public int getCapsLength(String message) {
		int i = 0;
		
		for (Character character : message.toCharArray())
			if (Character.isUpperCase(character))
				i++;
		return i;
	}
	
	@Override
	public int getCapsPercentage(String message) {
		return getCapsLength(message) * 100 / message.length();
	}
	
	private static List<String> lowerCase(List<String> list) {
		return list.stream().map(String::toLowerCase).collect(Collectors.toList());
	}
	
}
