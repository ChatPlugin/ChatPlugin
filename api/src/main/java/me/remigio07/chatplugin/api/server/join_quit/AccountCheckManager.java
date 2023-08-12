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

package me.remigio07.chatplugin.api.server.join_quit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Manager that handles account checks.
 */
public abstract class AccountCheckManager implements ChatPluginManager {
	
	protected static AccountCheckManager instance;
	protected boolean enabled, performOnFirstJoin, ipLookupEnabled, antiBanEvadingSystemEnabled;
	protected long timeoutBetweenChecks, maxTimePlayed;
	protected int maxAccuracyRadius;
	protected Map<Language, List<String>> antiBanEvadingCommands = new HashMap<>();
	protected Map<Integer, Map<Language, List<String>>> punishCommands = new HashMap<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.account-check.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if an account check should be performed the first time a (new) player joins.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.account-check.perform-on-join" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Whether a check is performed on the first join
	 */
	public boolean isPerformOnFirstJoin() {
		return performOnFirstJoin;
	}
	
	/**
	 * Checks if IP lookups of every IP address should be performed when checking accounts.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.account-check.ip-lookup.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Whether the IP lookup mode is enabled
	 */
	public boolean isIPLookupEnabled() {
		return ipLookupEnabled;
	}
	
	/**
	 * Checks if the anti ban evading system is enabled.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.account-check.anti-ban-evading-system.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Whether the anti ban evading system is enabled
	 */
	public boolean isAntiBanEvadingSystemEnabled() {
		return antiBanEvadingSystemEnabled;
	}
	
	/**
	 * Gets the timeout to wait between each check, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.account-check.timeout-between-checks-ms" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Timeout between checks
	 */
	public long getTimeoutBetweenChecks() {
		return timeoutBetweenChecks;
	}
	
	/**
	 * Gets a player's max {@link PlayersDataType#TIME_PLAYED} allowed,
	 * in milliseconds, to be considered reliable for a comparison.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.account-check.max-time-played" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Players' max time played
	 */
	public long getMaxTimePlayed() {
		return maxTimePlayed;
	}
	
	/**
	 * Gets an IP lookup's max {@link IPLookup#getAccuracyRadius()}
	 * allowed, in kilometers, to be considered reliable for a comparison.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.account-check.ip-lookup.max-accuracy-radius-km" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return IP lookups' max accuracy radius
	 */
	public int getMaxAccuracyRadius() {
		return maxAccuracyRadius;
	}
	
	/**
	 * Gets the commands executed when a ban is detected.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.account-check.anti-ban-evading-system.commands" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Anti ban evading system's commands' map
	 */
	public Map<Language, List<String>> getAntiBanEvadingCommands() {
		return antiBanEvadingCommands;
	}
	
	/**
	 * Gets the punish commands' map.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.account-check.punish-commands" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Punish commands' map
	 */
	public Map<Integer, Map<Language, List<String>>> getPunishCommands() {
		return punishCommands;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static AccountCheckManager getInstance() {
		return instance;
	}
	
	/**
	 * Checks if the specified player owns multiple accounts
	 * according to the module's current configuration.
	 * This method is run asynchronously and may execute {@link #getPunishCommands()}.
	 * Will do nothing if <code>!</code>{@link OfflinePlayer#hasPlayedBefore()}.
	 * 
	 * @param player Player to check
	 */
	public abstract void check(OfflinePlayer player);
	
	/**
	 * Gets a list of the accounts owned by the specified player,
	 * including it at the first position of the list held by the future.
	 * Will do nothing if <code>!</code>{@link OfflinePlayer#hasPlayedBefore()}.
	 * 
	 * @param player Player to check
	 * @param maxTimePlayed A player's max time played allowed to be considered reliable for a comparison, in milliseconds
	 * @param useIPLookup Whether to compare every IP address' lookup (requires extra time)
	 * @return Player's accounts, including given one
	 */
	public abstract CompletableFuture<List<OfflinePlayer>> getAccounts(OfflinePlayer player, long maxTimePlayed, boolean useIPLookup);
	
}
