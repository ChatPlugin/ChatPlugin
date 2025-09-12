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

package me.remigio07.chatplugin.api.server.integration.anticheat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.remigio07.chatplugin.api.common.integration.IntegrationManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;

/**
 * Manager that handles an {@link AnticheatIntegration}'s {@link Violation}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Integrations#anticheats">ChatPlugin wiki/Modules/Integrations/Anticheats</a>
 */
public abstract class AnticheatManager implements ChatPluginManager {
	
	protected static AnticheatManager instance;
	protected Map<OfflinePlayer, List<Violation>> violations = new ConcurrentHashMap<>();
	protected List<String> reasonsStartWith = new ArrayList<>();
	protected long violationsExpirationTimeout = -1L, loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p>Will return {@link IntegrationManager#isAtLeastOneAnticheatEnabled()}.</p>
	 */
	@Override
	public boolean isEnabled() {
		return IntegrationManager.getInstance().isAtLeastOneAnticheatEnabled();
	}
	
	/**
	 * Gets current violations' map.
	 * 
	 * <p>Every entry is composed of a player and
	 * the list of their {@link Violation}s.</p>
	 * 
	 * <p>Do <em>not</em> modify the returned map.
	 * Use {@link #addViolation(OfflinePlayer, IntegrationType, String, String, String, int, int, double, Version, boolean)
	 * addViolation(OfflinePlayer, IntegrationType, String, String, String, int, int, double, Version, boolean)},
	 * {@link #removeViolation(OfflinePlayer, IntegrationType, String)} and {@link #clearViolations(OfflinePlayer)}
	 * to interact with it.</p>
	 * 
	 * @return Current violations' map
	 */
	public Map<OfflinePlayer, List<Violation>> getViolations() {
		return violations;
	}
	
	/**
	 * Gets a list of violations from {@link #getViolations()}.
	 * 
	 * <p>Will return {@link Collections#emptyList()} if
	 * {@link #getViolations()} does not contain the specified player.</p>
	 * 
	 * <p>Do <em>not</em> modify the returned list.
	 * Use {@link #addViolation(OfflinePlayer, IntegrationType, String, String, String, int, int, double, Version, boolean)
	 * addViolation(OfflinePlayer, IntegrationType, String, String, String, int, int, double, Version, boolean)},
	 * {@link #removeViolation(OfflinePlayer, IntegrationType, String)} and {@link #clearViolations(OfflinePlayer)}
	 * to interact with it.</p>
	 * 
	 * @param player Player to check
	 * @return Player's current violations
	 */
	@NotNull
	public List<Violation> getViolations(OfflinePlayer player) {
		return violations.getOrDefault(player, Collections.emptyList());
	}
	
	/**
	 * Gets the list of strings a punishment's reason has to start
	 * with to be considered created automatically by the anticheat.
	 * 
	 * <p><strong>Found at:</strong> "settings.anticheat-integration.reasons-start-with" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Anticheat integration's reasons
	 */
	public List<String> getReasonsStartWith() {
		return reasonsStartWith;
	}
	
	/**
	 * Gets the time required for a violation to expire, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "settings.anticheat-integration.violations-expiration-timeout" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Violations' expiration timeout
	 */
	public long getViolationsExpirationTimeout() {
		return violationsExpirationTimeout;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static AnticheatManager getInstance() {
		return instance;
	}
	
	/**
	 * Adds a violation to {@link #getViolations()}.
	 * 
	 * @param cheater Violation's cheater
	 * @param anticheat Anticheat that flagged the player
	 * @param cheatID Violation's cheat's ID
	 * @param component Violation's cheat's display name
	 * @param server Violation's origin server
	 * @param amount Amount of times the player got flagged
	 * @param ping Cheater's ping
	 * @param tps Origin server's TPS
	 * @param version Cheater's version
	 * @param bedrockEdition Whether the cheater is playing on Bedrock Edition
	 */
	public abstract void addViolation(
			OfflinePlayer cheater,
			IntegrationType<AnticheatIntegration> anticheat,
			String cheatID,
			String component,
			@NotNull String server,
			int amount,
			int ping,
			double tps,
			Version version,
			boolean bedrockEdition
			);
	
	/**
	 * Removes a violation from {@link #getViolations()}.
	 * 
	 * @param cheater Violation's cheater
	 * @param anticheat Anticheat that flagged the player
	 * @param cheatID Violation's cheat's ID
	 */
	public abstract void removeViolation(
			OfflinePlayer cheater,
			IntegrationType<AnticheatIntegration> anticheat,
			String cheatID
			);
	
	/**
	 * Clears violations for the specified player.
	 * 
	 * @param cheater Player to clear the violations for
	 */
	public abstract void clearViolations(OfflinePlayer cheater);
	
	/**
	 * Checks if the specified punishment's reason is contained in {@link #getReasonsStartWith()}.
	 * 
	 * <p>Case will be lowered and colors will be stripped using {@link ChatColor#stripColor(String)}.</p>
	 * 
	 * <p>Will return <code>false</code> if <code>reason == null</code>.</p>
	 * 
	 * @param reason Punishment's reason
	 * @return Whether the reason is an anticheat's reason
	 */
	public abstract boolean isAnticheatReason(@Nullable(why = "Reason may not be specified") String reason);
	
}
