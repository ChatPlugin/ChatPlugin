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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.common.util.text.ComponentTranslator;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamResult;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.URLValidator;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class AntispamManagerImpl extends AntispamManager {
	
	private static final Pattern DOMAIN_PATTERN = Pattern.compile("((?!-)[A-Za-z0-9-]{1,63}([^A-Za-z0-9\\s\\/]+))+[A-Za-z]{2,6}");
	private static final Pattern IPV4_PATTERN = Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\D+){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
	private static final String[] NOTIFICATION_PLACEHOLDERS = new String[] { "player", "location", "reason", "message" };
	private List<Pattern> wordsBlacklistPatterns = new ArrayList<>();
	private String translatedHighlightColor;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.antispam.enabled"))
			return;
		leetFilterEnabled = ConfigurationType.CHAT.get().getBoolean("chat.antispam.leet-filter-enabled");
		urlsPreventionEnabled = ConfigurationType.CHAT.get().getBoolean("chat.antispam.prevention.urls.enabled");
		ipsPreventionEnabled = ConfigurationType.CHAT.get().getBoolean("chat.antispam.prevention.ips.enabled");
		secondsBetweenMessages = ConfigurationType.CHAT.get().getInt("chat.antispam.seconds-between-messages");
		secondsBetweenSameMessages = ConfigurationType.CHAT.get().getInt("chat.antispam.seconds-between-same-messages");
		maxCapsLength = ConfigurationType.CHAT.get().getInt("chat.antispam.max-caps-length");
		maxCapsPercentage = ConfigurationType.CHAT.get().getFloat("chat.antispam.max-caps-percentage");
		highlightColor = ConfigurationType.CHAT.get().getString("chat.antispam.highlight-color");
		messagesWhitelist = lowerCase(ConfigurationType.CHAT.get().getStringList("chat.antispam.messages-whitelist"));
		wordsBlacklist = lowerCase(ConfigurationType.CHAT.get().getStringList("chat.antispam.words-blacklist"));
		wordsBlacklistPatterns = patterns(wordsBlacklist);
		translatedHighlightColor = ChatColor.translate(highlightColor);
		
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
			StringBuilder sb = new StringBuilder("(?i)");
			char[] array = word.trim().toCharArray();
			
			if (word.startsWith(" "))
				sb.append("(?<![a-z])");
			for (int i = 0; i < array.length; i++) {
				sb.append(array[i]);
				
				if (i != array.length - 1)
					sb.append("+(\\W|\\d|_)*");
			} if (word.endsWith(" "))
				sb.append("(?![a-z])");
			patterns.add(Pattern.compile(sb.toString()));
		} return patterns;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = leetFilterEnabled = urlsPreventionEnabled = ipsPreventionEnabled = false;
		
		allowedDomains.clear();
		urlsWhitelist.clear();
		ipsWhitelist.clear();
		messagesWhitelist.clear();
		wordsBlacklist.clear();
		wordsBlacklistPatterns.clear();
		spamCache.clear();
		floodCache.clear();
		
		secondsBetweenMessages = secondsBetweenSameMessages = maxCapsLength = (int) (maxCapsPercentage = 0);
		highlightColor = translatedHighlightColor = null;
	}
	
	@Override
	public AntispamResult check(ChatPluginServerPlayer player, String message, List<DenyChatReason<AntispamManager>> bypassChecks) {
		if (!enabled || player.hasPermission("chatplugin.antispam.bypass"))
			return new AntispamResultImpl();
		int capsLength = getCapsLength(message);
		
		if (!bypassChecks.contains(DenyChatReason.CAPS) && !player.hasPermission("chatplugin.antispam.caps") && capsLength > maxCapsLength && getCapsPercentage(message) > maxCapsPercentage) {
			StringBuilder disallowedText = new StringBuilder();
			StringBuilder highlightedMessage = new StringBuilder();
			List<String> playersNames = ServerPlayerManager.getInstance().getPlayersNames();
			List<String> words = Arrays.asList(message.split(" "));
			int extra = 0;
			
			Collections.reverse(words);
			
			for (String word : words) {
				String tmp = word.startsWith("@") && word.length() > 1 ? word.substring(1) : word;
				
				if (!playersNames.contains(isAllowedInPlayerNames(tmp.charAt(tmp.length() - 1)) ? tmp : tmp.substring(0, tmp.length() - 1))) {
					for (char character : new StringBuilder(word).reverse().toString().toCharArray()) {
						if (Character.isUpperCase(character) && extra < capsLength - maxCapsPercentage / 100F * word.length()) {
							disallowedText.insert(0, character);
							highlightedMessage.insert(0, translatedHighlightColor + character + "\u00A7f");
							extra++;
						} else highlightedMessage.insert(0, character);
					}
				} else highlightedMessage.insert(0, word);
				
				disallowedText.insert(0, ' ');
				highlightedMessage.insert(0, ' ');
			} return new AntispamResultImpl(
					DenyChatReason.CAPS,
					disallowedText.deleteCharAt(0).toString().trim().replaceAll("\\s+", " "),
					highlightedMessage.deleteCharAt(0).toString()
					);
		} if (urlsPreventionEnabled && !bypassChecks.contains(DenyChatReason.URL)) {
			MatchResult disallowedURL = getDisallowedURL(message);
			
			try {
				return new AntispamResultImpl(DenyChatReason.URL, disallowedURL.group(), getHighlightedMessage(message, disallowedURL.group()));
			} catch (IllegalStateException ise) {
				// no disallowed URLs
			}
		} if (ipsPreventionEnabled && !bypassChecks.contains(DenyChatReason.IP_ADDRESS)) {
			MatchResult disallowedIPAddress = getDisallowedIPAddress(message);
			
			try {
				return new AntispamResultImpl(DenyChatReason.IP_ADDRESS, disallowedIPAddress.group(), getHighlightedMessage(message, disallowedIPAddress.group()));
			} catch (IllegalStateException ise) {
				// no disallowed IP addresses
			}
		} if (!bypassChecks.contains(DenyChatReason.SWEAR) && !player.hasPermission("chatplugin.antispam.swear")) {
			if (leetFilterEnabled) {
				String filtered = filterLeet(message);
				MatchResult disallowedWord = getDisallowedWord(filtered);
				
				try {
					String group = disallowedWord.group();
					String[] originalWords = message.split(" ");
					StringBuilder sb = new StringBuilder();
					int leadingSpaces = (int) filtered.chars().limit(disallowedWord.start()).filter(Character::isSpaceChar).count();
					int innerSpaces = (int) group.chars().filter(Character::isSpaceChar).count();
					
					if (leadingSpaces != 0)
						sb.append(String.join(" ", Arrays.copyOfRange(originalWords, 0, leadingSpaces))).append(' ');
					sb.append(String.join(" ", Arrays.copyOfRange(filtered.split(" "), leadingSpaces, leadingSpaces + innerSpaces + 1)));
					
					if (filtered.chars().filter(Character::isSpaceChar).count() - leadingSpaces - innerSpaces != 0)
						sb.append(' ').append(String.join(" ", Arrays.copyOfRange(originalWords, leadingSpaces + innerSpaces + 1, originalWords.length)));
					return new AntispamResultImpl(DenyChatReason.SWEAR, group, getHighlightedMessage(sb.toString(), group));
				} catch (IllegalStateException ise) {
					// no disallowed words
				}
			} else {
				MatchResult disallowedWord = getDisallowedWord(message);
				
				try {
					return new AntispamResultImpl(DenyChatReason.SWEAR, disallowedWord.group(), getHighlightedMessage(message, disallowedWord.group()));
				} catch (IllegalStateException ise) {
					// no disallowed words
				}
			}
		} UUID uuid = player.getUUID();
		
		if (!bypassChecks.contains(DenyChatReason.FLOOD) && !player.hasPermission("chatplugin.antispam.flood")) {
			if (floodCache.contains(uuid))
				return new AntispamResultImpl(DenyChatReason.FLOOD, message, translatedHighlightColor + message + "\u00A7f");
			floodCache.add(uuid);
			TaskManager.runAsync(() -> floodCache.remove(uuid), secondsBetweenMessages * 1000L);
		} if (!bypassChecks.contains(DenyChatReason.SPAM) && !player.hasPermission("chatplugin.antispam.spam") && !isMessageWhitelisted(message)) {
			if (spamCache.containsKey(uuid) && spamCache.get(uuid).contains(message))
				return new AntispamResultImpl(DenyChatReason.SPAM, message, translatedHighlightColor + message + "\u00A7f");
			final String finalMessage = message;
			
			if (!spamCache.containsKey(uuid))
				spamCache.put(uuid, new ArrayList<>());
			spamCache.get(uuid).add(message);
			TaskManager.runAsync(() -> {
				List<String> messages = spamCache.getOrDefault(uuid, Collections.emptyList());
				
				messages.remove(finalMessage);
				
				if (messages.isEmpty())
					spamCache.remove(uuid);
			}, secondsBetweenSameMessages * 1000L);
		} return new AntispamResultImpl();
	}
	
	private String getHighlightedMessage(String message, String disallowedText) {
		String[] array = message.split(Pattern.quote(disallowedText), 2);
		return array[0] + translatedHighlightColor + disallowedText + "\u00A7f" + array[1];
	}
	
	@Override
	public String filterLeet(String message) {
		for (LeetLetter letter : LeetLetter.values())
			message = letter.replace(message);
		return message;
	}
	
	@Override
	public MatchResult getDisallowedURL(String message) {
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
					break;
			}
		} return matcher.toMatchResult();
	}
	
	@Override
	public MatchResult getDisallowedIPAddress(String message) {
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
				break;
		} return matcher.toMatchResult();
	}
	
	private boolean isNumber(char character) {
		return character > 47 && character < 58;
	}
	
	@Override
	public MatchResult getDisallowedWord(String message) {
		for (Pattern pattern : wordsBlacklistPatterns) {
			Matcher matcher = pattern.matcher(message);
			
			if (matcher.find())
				return matcher.toMatchResult();
		} return Pattern.compile("").matcher(message);
	}
	
	@Override
	public boolean isMessageWhitelisted(String message) {
		return messagesWhitelist.contains(message.toLowerCase());
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
	public float getCapsPercentage(String message) {
		return getCapsLength(message) * 100F / message.length();
	}
	
	public void sendNotification(ChatPluginServerPlayer player, AntispamResult result) {
		ClickEvent clickEvent = ClickEvent.suggestCommand("/mute " + player.getName() + " ");
		
		if (ProxyManager.getInstance().isEnabled())
			ProxyManager.getInstance().sendPluginMessage(Packets.Messages.playerMessage(
					"ALL",
					"ALL LOADED OUTSIDE " + ProxyManager.getInstance().getServerID(),
					"chatplugin.antispam.notification",
					true,
					true,
					GsonComponentSerializer.gson().serialize(
							getComponent(player, result, "chat.antispam.notification-format.text")
							.clickEvent(clickEvent)
							.hoverEvent(HoverEvent.showText(getComponent(player, result, "chat.antispam.notification-format.hover")))
							)
					));
		else ChatPlugin.getInstance().sendConsoleMessage(formatPlaceholders(Language.getMainLanguage().getMessage("chat.antispam.notification-format.text"), player, result), false);
		
		for (Language language : LanguageManager.getInstance().getLanguages()) {
			String[] lines = formatPlaceholders(language.getMessage("chat.antispam.notification-format.text"), player, result).split("\n");
			TextComponent[] components = new TextComponent[lines.length];
			HoverEvent<Component> hoverEvent = HoverEvent.showText(Utils.deserializeLegacy(formatPlaceholders(language.getMessage("chat.antispam.notification-format.hover"), player, result), false));
			
			for (int i = 0; i < lines.length; i++)
				components[i] = Utils.deserializeLegacy(lines[i], false)
						.clickEvent(clickEvent)
						.hoverEvent(hoverEvent);
			for (ChatPluginServerPlayer other : language.getOnlinePlayers()) {
				if (other.hasPermission("chatplugin.antispam.notification"))
					((BaseChatPluginServerPlayer) other).sendMessage(components);
			}
		}
	}
	
	private Component getComponent(ChatPluginServerPlayer player, AntispamResult result, String path) {
		return Component.text(ComponentTranslator.getInstance().createJSON(
				ComponentTranslator.Component.MESSAGE_CUSTOM_PLACEHOLDERS,
				path,
				NOTIFICATION_PLACEHOLDERS,
				new Object[] {
						player.getName(),
						ProxyManager.getInstance().getServerDisplayName(),
						result.getReason().name(),
						result.getHighlightedMessage()	
				}));
	}
	
	private String formatPlaceholders(String input, ChatPluginServerPlayer player, AntispamResult result) {
		return input
				.replace("{player}", player.getName())
				.replace("{location}", player.getWorld())
				.replace("{reason}", result.getReason().name())
				.replace("{message}", result.getHighlightedMessage());
	}
	
}
