/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
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

import java.util.List;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents an {@link AnticheatIntegration}'s violation handled by the {@link AnticheatManager}.
 */
public abstract class Violation {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with a violation's information.
	 * 
	 * <p><strong>Content:</strong> ["cheater", "cheater_uuid", "anticheat", "cheat_id", "cheat_display_name", "component", "server", "amount", "ping", "ping_format", "tps", "version", "version_protocol", "client_edition", "last_time"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Integrations#placeholders-1">ChatPlugin wiki/Modules/Integrations/Anticheats/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = new String[] { "cheater", "cheater_uuid", "anticheat", "cheat_id", "cheat_display_name", "component", "server", "amount", "ping", "ping_format", "tps", "version", "version_protocol", "client_edition", "last_time" };
	protected OfflinePlayer cheater;
	protected IntegrationType<AnticheatIntegration> anticheat;
	protected String cheatID, component, server;
	protected int amount, ping;
	protected double tps;
	protected Version version;
	protected boolean bedrockEdition;
	protected long lastTime = System.currentTimeMillis();
	
	protected Violation(OfflinePlayer cheater, IntegrationType<AnticheatIntegration> anticheat, String cheatID, Version version, boolean bedrockEdition) {
		this.cheater = cheater;
		this.anticheat = anticheat;
		this.cheatID = cheatID;
		this.version = version;
		this.bedrockEdition = bedrockEdition;
	}
	
	/**
	 * Gets this violation's cheater.
	 * 
	 * @return Violation's cheater
	 */
	public OfflinePlayer getCheater() {
		return cheater;
	}
	
	/**
	 * Gets the {@link IntegrationType} that flagged the player.
	 * 
	 * @return Anticheat that flagged the player
	 */
	public IntegrationType<AnticheatIntegration> getAnticheat() {
		return anticheat;
	}
	
	/**
	 * Gets this violation's cheat's ID.
	 * 
	 * @return Violation's cheat's ID
	 */
	public String getCheatID() {
		return cheatID;
	}
	
	/**
	 * Gets this violation's cheat's display name found
	 * in {@link ConfigurationType#VIOLATIONS_ICONS}.
	 * 
	 * @return Violation's cheat's display name
	 */
	public String getCheatDisplayName() {
		return ConfigurationType.VIOLATIONS_ICONS.get().translateString(anticheat.name().toLowerCase() + "." + cheatID.toLowerCase() +  ".name");
	}
	
	/**
	 * Gets this violation's component.
	 * 
	 * @return Violation's component
	 */
	public String getComponent() {
		return component;
	}
	
	/**
	 * Gets this violation's origin server.
	 * 
	 * @return Violation's origin server
	 */
	public String getServer() {
		return server;
	}
	
	/**
	 * Gets the amount of times the player has been flagged.
	 * 
	 * @return Amount of times the player got flagged
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * Gets the cheater's latency at {@link #getLastTime()}, in milliseconds.
	 * 
	 * @return Cheater's ping
	 */
	public int getPing() {
		return ping;
	}
	
	/**
	 * Gets the {@link #getServer()}'s TPS at {@link #getLastTime()}.
	 * 
	 * @return Origin server's TPS
	 */
	public double getTPS() {
		return tps;
	}
	
	/**
	 * Gets the cheater's version.
	 * 
	 * @return Cheater's version
	 */
	public Version getVersion() {
		return version;
	}
	
	/**
	 * Checks if the cheater is connected
	 * through {@link IntegrationType#GEYSERMC}.
	 * 
	 * @return Whether the cheater is using the Bedrock Edition
	 */
	public boolean isBedrockPlayer() {
		return bedrockEdition;
	}
	
	/**
	 * Gets the last time the anticheat flagged this {@link #getCheatID()}, in milliseconds.
	 * 
	 * @return Last time this violation got flagged
	 */
	public long getLastTime() {
		return lastTime;
	}
	
	/**
	 * Translates an input string with this violation's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to find out the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract String formatPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string with this violation's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to find out the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract List<String> formatPlaceholders(List<String> input, Language language);
	
}
