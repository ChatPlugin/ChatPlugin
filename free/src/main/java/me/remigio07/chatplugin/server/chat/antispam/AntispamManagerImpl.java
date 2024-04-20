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

package me.remigio07.chatplugin.server.chat.antispam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.URLValidator;

public class AntispamManagerImpl extends AntispamManager {
	
	private static final Pattern DOMAIN_PATTERN = Pattern.compile("((?!-)[A-Za-z0-9-]{1,63}([^A-Za-z0-9\\s\\/]+))+[A-Za-z]{2,6}");
	private static final Pattern IPV4_PATTERN = Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\D+){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
	private List<Pattern> wordsBlacklistPatterns = new ArrayList<>();
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.antispam.enabled"))
			return;
		leetFilterEnabled = ConfigurationType.CHAT.get().getBoolean("chat.antispam.leet-filter-enabled");
		urlsPreventionEnabled = ConfigurationType.CHAT.get().getBoolean("chat.antispam.prevention.urls.enabled");
		ipsPreventionEnabled = ConfigurationType.CHAT.get().getBoolean("chat.antispam.prevention.ips.enabled");
		maxCapsLength = ConfigurationType.CHAT.get().getInt("chat.antispam.max-caps-length");
		maxCapsPercent = ConfigurationType.CHAT.get().getInt("chat.antispam.max-caps-percent");
		secondsBetweenMsg = ConfigurationType.CHAT.get().getInt("chat.antispam.seconds-between-msg");
		secondsBetweenSameMsg = ConfigurationType.CHAT.get().getInt("chat.antispam.seconds-between-same-msg");
		wordsBlacklist = lowerCase(ConfigurationType.CHAT.get().getStringList("chat.antispam.words-blacklist"));
		messagesWhitelist = lowerCase(ConfigurationType.CHAT.get().getStringList("chat.antispam.messages-whitelist"));
		wordsBlacklistPatterns = patterns(wordsBlacklist);
		
		for (String allowedDomain : ConfigurationType.CHAT.get().getStringList("chat.antispam.prevention.urls.allowed-domains")) {
			String domainName = URLValidator.getDomainName(allowedDomain);
			
			if (domainName == null)
				LogManager.log("Invalid domain (\"{0}\") specified at \"chat.antispam.prevention.urls.allowed-domains\" in chat.yml; skipping it.", 1, allowedDomain);
			else allowedDomains.add(domainName);
		} for (String whitelistedURL : ConfigurationType.CHAT.get().getStringList("chat.antispam.prevention.urls.whitelist")) {
			if (!whitelistedURL.contains(" ")) {
				String domainName = URLValidator.getDomainName(whitelistedURL);
				
				if (domainName == null)
					LogManager.log("Invalid URL (\"{0}\") specified at \"chat.antispam.prevention.urls.whitelist\" in chat.yml; skipping it.", 1, whitelistedURL);
				else urlsWhitelist.add(URLValidator.stripProtocol((whitelistedURL.endsWith("/") ? whitelistedURL.substring(0, whitelistedURL.length() - 1) : whitelistedURL).toLowerCase()));
			} else LogManager.log("Invalid URL (\"{0}\") specified at \"chat.antispam.prevention.urls.whitelist\" in chat.yml: URLs cannot contain spaces; skipping it.", 1);
		} for (String whitelistedIP : ConfigurationType.CHAT.get().getStringList("chat.antispam.prevention.ips.whitelist")) {
			if (Utils.isValidIPv4(whitelistedIP))
				ipsWhitelist.add(whitelistedIP);
			else LogManager.log("Invalid IPv4 (\"{0}\") specified at \"chat.antispam.prevention.ips.whitelist\" in chat.yml; skipping it.", 1, whitelistedIP);
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	private static List<String> lowerCase(List<String> list) {
		return list.stream().map(String::toLowerCase).collect(Collectors.toCollection(ArrayList::new));
	}
	
	private static List<Pattern> patterns(List<String> list) {
		List<Pattern> patterns = new ArrayList<>();
		
		for (String word : list) {
			StringBuilder sb = new StringBuilder((word.startsWith(" ") ? "\\b" : "") + "(");
			boolean endsWithSpace = word.endsWith(" ");
			word = word.trim();
			
			for (char character : word.toCharArray())
				sb.append(character + "+(\\W|\\d|_)*");
			sb.append(")" + (endsWithSpace ? "\\b" : ""));
			patterns.add(Pattern.compile(sb.toString()));
		} return patterns;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = leetFilterEnabled = urlsPreventionEnabled = ipsPreventionEnabled = false;
		
		allowedDomains.clear();
		urlsWhitelist.clear();
		ipsWhitelist.clear();
		wordsBlacklist.clear();
		messagesWhitelist.clear();
		spamCache.clear();
		floodCache.clear();
		wordsBlacklistPatterns.clear();
		
		maxCapsLength = maxCapsPercent = secondsBetweenMsg = secondsBetweenSameMsg = 0;
	}
	
	@Override
	public DenyChatReason<AntispamManager> getDenyChatReason(ChatPluginServerPlayer player, String message, List<DenyChatReason<AntispamManager>> bypassChecks) {
		if (!enabled || player.hasPermission("chatplugin.antispam.bypass"))
			return null;
		if (urlsPreventionEnabled && !bypassChecks.contains(DenyChatReason.URL) && containsDisallowedURL(message))
			return DenyChatReason.URL;
		if (ipsPreventionEnabled && !bypassChecks.contains(DenyChatReason.IP_ADDRESS) && containsDisallowedIP(message))
			return DenyChatReason.IP_ADDRESS;
		if (!bypassChecks.contains(DenyChatReason.SWEAR) && !player.hasPermission("chatplugin.antispam.swear") && containsBlacklistedWord(message))
			return DenyChatReason.SWEAR;
		if (!bypassChecks.contains(DenyChatReason.CAPS) && !player.hasPermission("chatplugin.antispam.caps") && exceedsMaxCapsLength(message) && exceedsMaxCapsPercentage(message))
			return DenyChatReason.CAPS;
		UUID uuid = player.getUUID();
		
		if (!bypassChecks.contains(DenyChatReason.FLOOD) && !player.hasPermission("chatplugin.antispam.flood")) {
			if (floodCache.contains(uuid))
				return DenyChatReason.FLOOD;
			floodCache.add(uuid);
			TaskManager.runAsync(() -> floodCache.remove(uuid), secondsBetweenMsg * 1000L);
		} if (!bypassChecks.contains(DenyChatReason.SPAM) && !player.hasPermission("chatplugin.antispam.spam") && !isMessageWhitelisted(message)) {
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
		Matcher matcher = DOMAIN_PATTERN.matcher(message);
		
		while (matcher.find()) {
			String dirtyDomain = matcher.group();
			String domainName = matcher.group(1);
			String tld = dirtyDomain.substring(dirtyDomain.lastIndexOf(domainName.charAt(domainName.length() - 1)) + 1).toLowerCase();
			
			if (ChatManager.getInstance().getRecognizedTLDs().contains(tld)) {
				StringBuilder domain = new StringBuilder();
				
				for (int i = 0; i < domainName.length(); i++) {
					char character = domainName.charAt(i);
					
					if (isNumber(character) || Character.isLetter(character) || character == '-') {
						domain.append(character);
					} else if (domain.charAt(domain.length() - 1) != '.')
						domain.append('.');
				} domain.append(tld);
				
				String finalDomain = domain.toString().toLowerCase();
				String rest = message.substring(matcher.end());
				
				if (!allowedDomains.contains(finalDomain) && !urlsWhitelist.contains(finalDomain + (rest.contains(" ") ? rest.substring(0, rest.indexOf(' ')) : rest).toLowerCase()))
					return true;
			}
		} return false;
	}
	
	@Override
	public boolean containsDisallowedIP(String message) {
		Matcher matcher = IPV4_PATTERN.matcher(message);
		
		while (matcher.find()) {
			StringBuilder ipAddress = new StringBuilder();
			String dirtyIPAddress = matcher.group();
			
			for (int i = 0; i < dirtyIPAddress.length(); i++) {
				char character = dirtyIPAddress.charAt(i);
				
				if (isNumber(character))
					ipAddress.append(character);
				else if (ipAddress.charAt(ipAddress.length() - 1) != '.')
					ipAddress.append('.');
			} if (!ipsWhitelist.contains(ipAddress.toString()))
				return true;
		} return false;
	}
	
	private boolean isNumber(char character) {
		return character > 47 && character < 58;
	}
	
	@Override
	public boolean containsBlacklistedWord(String message) {
		if (leetFilterEnabled)
			for (LeetLetter letter : LeetLetter.values())
				message = letter.replace(message);
		return containsBlacklistedWord0(message);
	}
	
	private boolean containsBlacklistedWord0(String message) {
		for (int i = 0; i < wordsBlacklist.size(); i++)
			if (wordsBlacklistPatterns.get(i).matcher(message.toLowerCase()).find())
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
		List<String> playersNames = ServerPlayerManager.getInstance().getPlayersNames();
		int length = 0;
		
		for (String word : message.split(" "))
			if (!playersNames.contains(isAllowedInPlayerNames((word = word.startsWith("@") && word.length() > 1 ? word.substring(1) : word).charAt(word.length() - 1)) ? word : word.substring(0, word.length() - 1)))
				length += word.chars().filter(Character::isUpperCase).count();
		return length;
	}
	
	private boolean isAllowedInPlayerNames(char character) {
		return isNumber(character) || Character.isLetter(character) || character == '_';
	}
	
	@Override
	public int getCapsPercentage(String message) {
		return getCapsLength(message) * 100 / message.length();
	}
	
}
