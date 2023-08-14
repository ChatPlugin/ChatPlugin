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

package me.remigio07.chatplugin.api.server.integration.anticheat;

import java.util.List;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an {@link AnticheatIntegration}'s violation handled by the {@link AnticheatManager}.
 */
public abstract class Violation {
	
	/**
	 * Array containing all available placeholders that can
	 * be translated with a violation's information. See wiki for more info:
	 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Plugin-integrations#violations-placeholders">ChatPlugin wiki/Plugin integrations/Anticheats/Violations' placeholders</a>
	 * 
	 * <p><strong>Content:</strong> ["cheater", "cheater_uuid", "server", "anticheat", "cheat_id", "component", "amount", "ping", "ping_format", "version", "version_protocol", "tps", "last_time"]</p>
	 */
	public static final String[] PLACEHOLDERS = new String[] { "cheater", "cheater_uuid", "server", "anticheat", "cheat_id", "component", "amount", "ping", "ping_format", "version", "version_protocol", "tps", "last_time" };
	protected ChatPluginServerPlayer cheater;
	protected String server, cheatID, component;
	protected IntegrationType<AnticheatIntegration> anticheat;
	protected int amount, versionProtocol, ping;
	protected boolean versionPreNettyRewrite;
	protected double tps;
	protected long lastTime = System.currentTimeMillis();
	
	protected Violation(ChatPluginServerPlayer cheater, String server, IntegrationType<AnticheatIntegration> anticheat, String cheatID, String component, int amount, int ping, int versionProtocol, boolean versionPreNettyRewrite, double tps) {
		this.server = server;
		this.cheater = cheater;
		this.anticheat = anticheat;
		this.cheatID = cheatID;
		this.component = component;
		this.amount = amount;
		this.ping = ping;
		this.versionProtocol = versionProtocol;
		this.versionPreNettyRewrite = versionPreNettyRewrite;
		this.tps = tps;
	}
	
	/**
	 * Gets this violation's cheater.
	 * 
	 * @return Violation's cheater
	 */
	public ChatPluginServerPlayer getCheater() {
		return cheater;
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
	 * Gets this violation's component.
	 * 
	 * @return Violation's component
	 */
	public String getComponent() {
		return component;
	}
	
	/**
	 * Gets this violation's amount.
	 * 
	 * @return Violation's amount
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * Gets the cheater's latency, in milliseconds.
	 * 
	 * @return Cheater's ping
	 */
	public int getPing() {
		return ping;
	}
	
	/**
	 * Gets the cheater's version's protocol.
	 * 
	 * @return Cheater's version's protocol
	 */
	public int getVersionProtocol() {
		return versionProtocol;
	}
	
	/**
	 * Checks if the cheater's version is a pre-Netty rewrite version.
	 * 
	 * @return Whether the cheater's version is a pre-Netty rewrite version
	 */
	public boolean isVersionPreNettyRewrite() {
		return versionPreNettyRewrite;
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
	 * Gets the last time the anticheat flagged this {@link #getCheatID()}.
	 * 
	 * @return Last time this violation got flagged, in milliseconds
	 */
	public long getLastTime() {
		return lastTime;
	}
	
	/**
	 * Translates an input string with {@link #PLACEHOLDERS}
	 * placeholders, translated in the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract String formatPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string list with {@link #PLACEHOLDERS}
	 * placeholders, translated in the specified language.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract List<String> formatPlaceholders(List<String> input, Language language);
	
}
