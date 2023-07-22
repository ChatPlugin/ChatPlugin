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

package me.remigio07_.chatplugin.api.server.integration.anticheat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07_.chatplugin.api.common.integration.IntegrationManager;
import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07_.chatplugin.api.common.util.text.ChatColor;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles an {@link AnticheatIntegration}'s {@link Violation}s. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Plugin-integrations#violations-placeholders">ChatPlugin wiki/Plugin integrations/Anticheats/Violations' placeholders</a>
 */
public abstract class AnticheatManager implements ChatPluginManager {
	
	protected static AnticheatManager instance;
	protected Map<ChatPluginServerPlayer, List<Violation>> violations = new HashMap<>();
	protected List<String> reasonsStartWith = new ArrayList<>();
	protected long violationsExpirationTimeout = -1L, loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * Will return {@link IntegrationManager#isAtLeastOneAnticheatEnabled()}.
	 */
	@Override
	public boolean isEnabled() {
		return IntegrationManager.getInstance().isAtLeastOneAnticheatEnabled();
	}
	
	/**
	 * Gets current violations' map. Every entry is composed
	 * of a player and a list of {@link Violation}s.
	 * 
	 * @return Current violations' map
	 */
	public Map<ChatPluginServerPlayer, List<Violation>> getViolations() {
		return violations;
	}
	
	/**
	 * Gets a list of violations from {@link #getViolations()}.
	 * Will return {@link Collections#emptyList()} if {@link #getViolations()}
	 * does not contain the specified <code>player</code>.
	 * 
	 * @param player Player to check
	 * @return Player's current violations
	 */
	@NotNull
	public List<Violation> getViolations(ChatPluginServerPlayer player) {
		return violations.getOrDefault(player, Collections.emptyList());
	}
	
	/**
	 * Gets the list of strings a punishment's reason has to start with
	 * to be considered created automatically by the anticheat.
	 * 
	 * @return Anticheat integration's reasons
	 */
	public List<String> getReasonsStartWith() {
		return reasonsStartWith;
	}
	
	/**
	 * Gets the time required for a violation to expire, specified at
	 * <code>settings.violations-expiration-timeout</code> in config.yml,
	 * expressed in milliseconds.
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
	 * This operation will remove any previous violation
	 * with the same {@link Violation#getCheatID()}.
	 * 
	 * @param violation Violation to add
	 */
	public abstract void addViolation(Violation violation);
	
	/**
	 * Removes a violation from {@link #getViolations()}.
	 * 
	 * @param anticheat Violation's {@link Violation#getAntiCheat()}
	 * @param player Violation's {@link Violation#getPlayerUUID()}
	 * @param cheatID Violation's {@link Violation#getCheatID()}
	 */
	public abstract void removeViolation(IntegrationType<AnticheatIntegration> anticheat, ChatPluginServerPlayer player, String cheatID);
	
	/**
	 * Clears violations for the specified player.
	 * 
	 * @param player Player to clear the violations for
	 */
	public abstract void clearViolations(ChatPluginServerPlayer player);
	
	/**
	 * Checks if the specified punishment's reason is contained in {@link #getReasonsStartWith()}.
	 * Case will be lowered and colors will be stripped using {@link ChatColor#stripColor(String)}. See wiki for more info:
	 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Plugin-integrations#anticheats">ChatPlugin wiki/Plugin integrations/Anticheats</a>
	 * 
	 * @param reason Punishment's reason
	 * @return Whether the reason is an anticheat's reason
	 */
	public abstract boolean isAnticheatReason(String reason);
	
}
