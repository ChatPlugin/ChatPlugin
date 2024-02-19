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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import me.remigio07.chatplugin.api.common.chat.DenyChatReasonHandler;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles the antispam.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#antispam">ChatPlugin wiki/Modules/Chat/Antispam</a>
 */
public abstract class AntispamManager implements DenyChatReasonHandler {
	
	protected static AntispamManager instance;
	protected boolean enabled, urlsPreventionEnabled, ipsPreventionEnabled;
	protected int maxCapsLength, maxCapsPercent, secondsBetweenMsg, secondsBetweenSameMsg;
	protected List<String> allowedDomains = new ArrayList<>(), urlsWhitelist = new ArrayList<>(), ipsWhitelist = new ArrayList<>(), wordsBlacklist = new ArrayList<>(), messagesWhitelist = new ArrayList<>();
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
	 * Gets the max caps length allowed before checking if the message exceeds {@link #getMaxCapsPercent()}.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.max-caps-length" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Max caps length allowed
	 */
	public int getMaxCapsLength() {
		return maxCapsLength;
	}
	
	/**
	 * Gets the max caps percent allowed after checking if the message exceeds {@link #getMaxCapsLength()}.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.max-caps-percent" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Max caps percent allowed
	 */
	public int getMaxCapsPercent() {
		return maxCapsPercent;
	}
	
	/**
	 * Gets the seconds every player has to wait between two consecutive sendings.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.seconds-between-msg" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Seconds to wait between two messages
	 */
	public int getSecondsBetweenMsg() {
		return secondsBetweenMsg;
	}
	
	/**
	 * Gets the seconds every player has to wait between sendings of two identical messages.
	 * 
	 * <p><strong>Found at:</strong> "chat.antispam.seconds-between-same-msg" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Seconds to wait between two identical messages
	 */
	public int getSecondsBetweenSameMsg() {
		return secondsBetweenSameMsg;
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
	 * Gets the spam cache's map, whose keys are players' UUIDs
	 * and values are the message they have just sent.
	 * 
	 * <p>Do <strong>not</strong> modify the returned map.</p>
	 * 
	 * @return Spam cache's map
	 */
	public Map<UUID, List<String>> getSpamCache() {
		return spamCache;
	}
	
	/**
	 * Gets the flood cache set, whose elements are players' UUIDs.
	 * 
	 * <p>Do <strong>not</strong> modify the returned set.</p>
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
	 * Gets the reason why the specified message should be blocked.
	 * 
	 * <p>Will return <code>null</code> if <code>player</code> has the permission to send the message.</p>
	 * 
	 * <p><strong>Note:</strong> this method returns only reasons handled by the {@link AntispamManager}.
	 * Check a {@link DenyChatReason}'s doc to know what manager handles it.</p>
	 * 
	 * @param player Player involved
	 * @param message Message to check
	 * @param bypassChecks Checks not to be performed
	 * @return The reason why the message should be blocked
	 */
	@Nullable(why = "Player may have the permission to send the message")
	public abstract DenyChatReason<AntispamManager> getDenyChatReason(ChatPluginServerPlayer player, String message, List<DenyChatReason<AntispamManager>> bypassChecks);
	
	/**
	 * Checks if the specified message contains a disallowed URL.
	 * 
	 * @param message Message to check
	 * @return Whether the message contains a disallowed URL
	 */
	public abstract boolean containsDisallowedURL(String message);
	
	/**
	 * Checks if the specified message contains a disallowed IP address.
	 * 
	 * @param message Message to check
	 * @return Whether the message contains a disallowed IP
	 */
	public abstract boolean containsDisallowedIP(String message);
	
	/**
	 * Checks if the specified message contains a blacklisted word.
	 * 
	 * @param message Message to check
	 * @return Whether the message contains a disallowed word
	 */
	public abstract boolean containsBlacklistedWord(String message);
	
	/**
	 * Checks if a message is whitelisted.
	 * 
	 * <p>This means that the antispam will not prevent it from being sent
	 * more than once every {@link #getSecondsBetweenSameMsg()} seconds.</p>
	 * 
	 * @param message Message to check
	 * @return Whether the message is whitelisted
	 */
	public abstract boolean isMessageWhitelisted(String message);
	
	/**
	 * Checks if the specified message exceeds the maximum caps length allowed.
	 * 
	 * @param message Message to check
	 * @return Whether the message exceeds the max caps length
	 */
	public abstract boolean exceedsMaxCapsLength(String message);
	
	/**
	 * Checks if the specified message exceeds the maximum caps percentage allowed.
	 * 
	 * @param message Message to check
	 * @return Whether the message exceeds the max caps percentage
	 */
	public abstract boolean exceedsMaxCapsPercentage(String message);
	
	/**
	 * Gets the amount of caps characters in the specified message.
	 * 
	 * @param message Message to check
	 * @return Caps characters in the message
	 */
	public abstract int getCapsLength(String message);
	
	/**
	 * Gets the caps percentage amount of a message.
	 * 
	 * @param message Message to check
	 * @return Caps percentage in the message
	 */
	public abstract int getCapsPercentage(String message);
	
}
