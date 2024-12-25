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

package me.remigio07.chatplugin.api.server.chat.antispam;

import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.MatchResult;

import me.remigio07.chatplugin.api.common.chat.DenyChatReasonHandler;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles the antispam.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#antispam">ChatPlugin wiki/Modules/Chat/Antispam</a>
 */
public abstract class AntispamManager implements DenyChatReasonHandler {
	
	protected static AntispamManager instance;
	protected boolean enabled, leetFilterEnabled, urlsPreventionEnabled, ipsPreventionEnabled;
	protected int secondsBetweenMessages, secondsBetweenSameMessages, maxCapsLength;
	protected float maxCapsPercentage;
	protected String highlightColor;
	protected List<String> allowedDomains = new ArrayList<>(), urlsWhitelist = new ArrayList<>(),
			ipsWhitelist = new ArrayList<>(), messagesWhitelist = new ArrayList<>(), wordsBlacklist = new ArrayList<>();
	protected Map<UUID, List<String>> spamCache = new HashMap<>();
	protected Set<UUID> floodCache = new HashSet<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the antispam should consider
	 * <a href="https://en.wikipedia.org/wiki/Leet">leetspeak</a>
	 * when checking messages.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.leet-filter-enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether the leet filter is enabled
	 * @see #filterLeet(String)
	 */
	public boolean isLeetFilterEnabled() {
		return leetFilterEnabled;
	}
	
	/**
	 * Checks if the antispam should prevent disallowed URLs from being sent.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.prevention.urls.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether URLs prevention is enabled
	 */
	public boolean isURLsPreventionEnabled() {
		return urlsPreventionEnabled;
	}
	
	/**
	 * Checks if the antispam should prevent disallowed IP addresses from being sent.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.prevention.ips.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether IPs prevention is enabled
	 */
	public boolean isIPsPreventionEnabled() {
		return ipsPreventionEnabled;
	}
	
	/**
	 * Gets the seconds every player has to wait between two consecutive sendings.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.seconds-between-messages" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Seconds to wait between two messages
	 */
	public int getSecondsBetweenMessages() {
		return secondsBetweenMessages;
	}
	
	/**
	 * Gets the seconds every player has to wait between sendings of two identical messages.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.seconds-between-same-messages" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Seconds to wait between two identical messages
	 */
	public int getSecondsBetweenSameMessages() {
		return secondsBetweenSameMessages;
	}
	
	/**
	 * Gets the max caps length allowed before checking if the message exceeds {@link #getMaxCapsPercentage()}.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.max-caps-length" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Max caps length allowed
	 */
	public int getMaxCapsLength() {
		return maxCapsLength;
	}
	
	/**
	 * Gets the max caps percentage allowed after checking if the message exceeds {@link #getMaxCapsLength()}.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.max-caps-percentage" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Max caps percentage allowed
	 */
	public float getMaxCapsPercentage() {
		return maxCapsPercentage;
	}
	
	/**
	 * Gets the color used to highlight disallowed text in antispam notifications.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.highlight-color" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Highlight color in notifications
	 */
	public String getHighlightColor() {
		return highlightColor;
	}
	
	/**
	 * Gets the list containing the URLs' allowed domains.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.prevention.urls.allowed-domains" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return URLs' allowed domains
	 */
	public List<String> getAllowedDomains() {
		return allowedDomains;
	}
	
	/**
	 * Gets the list containing the whitelisted URLs.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.prevention.urls.whitelist" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return URLs' whitelist
	 */
	public List<String> getURLsWhitelist() {
		return urlsWhitelist;
	}
	
	/**
	 * Gets the list containing the whitelisted IP addresses.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.prevention.ips.whitelist" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return IPs' whitelist
	 */
	public List<String> getIPsWhitelist() {
		return ipsWhitelist;
	}
	
	/**
	 * Gets the list containing the whitelisted messages.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.messages-whitelist" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Messages' whitelist
	 */
	public List<String> getMessagesWhitelist() {
		return messagesWhitelist;
	}
	
	/**
	 * Gets the list containing the blacklisted words.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.words-blacklist" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Words' blacklist
	 */
	public List<String> getWordsBlacklist() {
		return wordsBlacklist;
	}
		
	/**
	 * Gets the spam cache's map, whose keys are players' UUIDs
	 * and values are the message they have just sent.
	 * 
	 * <p>Do <em>not</em> modify the returned map.</p>
	 * 
	 * @return Spam cache's map
	 */
	public Map<UUID, List<String>> getSpamCache() {
		return spamCache;
	}
	
	/**
	 * Gets the flood cache set, whose elements are players' UUIDs.
	 * 
	 * <p>Do <em>not</em> modify the returned set.</p>
	 * 
	 * @return Flood cache's map
	 */
	public Set<UUID> getFloodCache() {
		return floodCache;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static AntispamManager getInstance() {
		return instance;
	}
	
	/**
	 * Checks if the specified message should be blocked
	 * according to the module's current configuration.
	 * 
	 * @param player Player involved
	 * @param message Message to check
	 * @param bypassChecks Checks not to be performed
	 * @return The check's result
	 */
	public abstract AntispamResult check(ChatPluginServerPlayer player, String message, List<DenyChatReason<AntispamManager>> bypassChecks);
	
	/**
	 * Filters out <a href="https://en.wikipedia.org/wiki/Leet">leetspeak</a>
	 * in the specified message.
	 * 
	 * @param message Message to filter
	 * @return Filtered message
	 */
	public abstract String filterLeet(String message);
	
	/**
	 * Gets the first disallowed URL found in the specified message.
	 * 
	 * <p>You can obtain the result of the matcher using {@link MatchResult#group()}.
	 * If it throws an {@link IllegalStateException}, no disallowed URLs were found.</p>
	 * 
	 * <p>It is not safe to convert the result to a {@link URL}.</p>
	 * 
	 * @param message Message to check
	 * @return First disallowed URL in the message
	 */
	public abstract MatchResult getDisallowedURL(String message);
	
	/**
	 * Gets the first disallowed IP address found in the specified message.
	 * 
	 * <p>You can obtain the result of the matcher using {@link MatchResult#group()}.
	 * If it throws an {@link IllegalStateException}, no disallowed IP addresses were found.</p>
	 * 
	 * <p>It is not safe to convert the result to a {@link InetAddress}.</p>
	 * 
	 * @param message Message to check
	 * @return First disallowed IP address in the message
	 */
	public abstract MatchResult getDisallowedIPAddress(String message);
	
	/**
	 * Gets the first disallowed word in the specified message.
	 * 
	 * <p>You can obtain the result of the matcher using {@link MatchResult#group()}.
	 * If it throws an {@link IllegalStateException}, no disallowed words were found.</p>
	 * 
	 * @param message Message to check
	 * @return First disallowed word in the message
	 */
	public abstract MatchResult getDisallowedWord(String message);
	
	/**
	 * Checks if a message is whitelisted, ignoring case.
	 * 
	 * <p>This means that the antispam will not prevent it from being sent
	 * more than once every {@link #getSecondsBetweenSameMessages()} seconds.</p>
	 * 
	 * @param message Message to check
	 * @return Whether the message is whitelisted
	 */
	public abstract boolean isMessageWhitelisted(String message);
	
	/**
	 * Gets the amount of caps characters in the specified message.
	 * 
	 * <p>This will consider player names as lower case.</p>
	 * 
	 * @param message Message to check
	 * @return Caps characters in the message
	 */
	public abstract int getCapsLength(String message);
	
	/**
	 * Gets the caps percentage amount of a message.
	 * 
	 * <p>This will consider player names as lower case.</p>
	 * 
	 * @param message Message to check
	 * @return Caps percentage in the message
	 */
	public abstract float getCapsPercentage(String message);
	
}
