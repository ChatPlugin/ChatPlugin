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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.motd;

import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.motd.MoTD;
import me.remigio07.chatplugin.api.common.motd.MoTDManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageDetector;
import me.remigio07.chatplugin.api.server.util.GameFeature;

/**
 * Server abstraction of the {@link MoTDManager}.
 */
@GameFeature(
		name = "motd",
		availableOnBukkit = true,
		availableOnSponge = true,
		spigotRequired = false,
		minimumBukkitVersion = Version.V1_7_2,
		minimumSpongeVersion = Version.V1_8
		)
public abstract class ServerMoTDManager extends MoTDManager {
	
	protected boolean fixedValuePlusOneEnabled,
			unknownPlayerHoversEnabled, unknownPlayerVersionNamesEnabled,
			storedPlayerHoversEnabled, storedPlayerVersionNamesEnabled,
			bannedPlayerMoTDEnabled, bannedPlayerHoversEnabled, bannedPlayerVersionNamesEnabled,
			outdatedVersionMoTDEnabled, outdatedVersionHoversEnabled, outdatedVersionVersionNamesEnabled;
	protected int minimumSupportedVersionProtocol, serverSocketPort, maxPlayersFixedValue;
	protected InetAddress serverSocketAddress;
	protected URL unknownPlayerIconURL, storedPlayerIconURL, bannedPlayerIconURL, outdatedVersionIconURL;
	protected Map<Language, List<String>> unknownPlayerDescriptions = new HashMap<>(), unknownPlayerHovers = new HashMap<>(), unknownPlayerVersionNames = new HashMap<>(),
			storedPlayerDescriptions = new HashMap<>(), storedPlayerHovers = new HashMap<>(), storedPlayerVersionNames = new HashMap<>(),
			bannedPlayerDescriptions = new HashMap<>(), bannedPlayerHovers = new HashMap<>(), bannedPlayerVersionNames = new HashMap<>(),
			outdatedVersionDescriptions = new HashMap<>(), outdatedVersionHovers = new HashMap<>(), outdatedVersionVersionNames = new HashMap<>();
	
	/**
	 * Checks if one player more than the online players' amount should
	 * be displayed instead of {@link #getMaxPlayersFixedValue()} as
	 * the max players' count when version names are not displayed.
	 * 
	 * <p><strong>Found at:</strong> "motd.max-players.one-more-instead-of-fixed-value" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether max players' fixed value plus one is enabled
	 */
	public boolean isFixedValuePlusOneEnabled() {
		return fixedValuePlusOneEnabled;
	}
	
	/**
	 * Checks if {@link #getUnknownPlayerHovers()} should be displayed to unknown players.
	 * 
	 * <p><strong>Found at:</strong> "motd.unknown-player.hovers.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display hovers to unknown players
	 */
	public boolean areUnknownPlayerHoversEnabled() {
		return unknownPlayerHoversEnabled;
	}
	
	/**
	 * Checks if {@link #getUnknownPlayerVersionNames()} should be displayed to unknown players.
	 * 
	 * <p><strong>Found at:</strong> "motd.unknown-player.version-names.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display version names to unknown players
	 */
	public boolean areUnknownPlayerVersionNamesEnabled() {
		return unknownPlayerVersionNamesEnabled;
	}
	
	/**
	 * Checks if {@link #getStoredPlayerHovers()} should be displayed to stored players.
	 * 
	 * <p><strong>Found at:</strong> "motd.stored-player.hovers.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display hovers to stored players
	 */
	public boolean areStoredPlayerHoversEnabled() {
		return storedPlayerHoversEnabled;
	}
	
	/**
	 * Checks if {@link #getStoredPlayerVersionNames()} should be displayed to stored players.
	 * 
	 * <p><strong>Found at:</strong> "motd.stored-player.version-names.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display version names to stored players
	 */
	public boolean areStoredPlayerVersionNamesEnabled() {
		return storedPlayerVersionNamesEnabled;
	}
	
	/**
	 * Checks if a custom MoTD should be displayed to banned players.
	 * 
	 * <p><strong>Found at:</strong> "motd.banned-player.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display a custom MoTD to banned players
	 */
	public boolean isBannedPlayerMoTDEnabled() {
		return bannedPlayerMoTDEnabled;
	}
	
	/**
	 * Checks if {@link #getBannedPlayerHovers()} should be displayed to banned players.
	 * 
	 * <p><strong>Found at:</strong> "motd.banned-player.hovers.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display hovers to banned players
	 */
	public boolean areBannedPlayerHoversEnabled() {
		return bannedPlayerHoversEnabled;
	}
	
	/**
	 * Checks if {@link #getBannedPlayerVersionNames()} should be displayed to banned players.
	 * 
	 * <p><strong>Found at:</strong> "motd.banned-player.version-names.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display version names to banned players
	 */
	public boolean areBannedPlayerVersionNamesEnabled() {
		return bannedPlayerVersionNamesEnabled;
	}
	
	/**
	 * Checks if a custom MoTD should be displayed to players with an outdated version.
	 * 
	 * <p><strong>Found at:</strong> "motd.outdated-version.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display a custom MoTD to players running an outdated version
	 */
	public boolean isOutdatedVersionMoTDEnabled() {
		return outdatedVersionMoTDEnabled;
	}
	
	/**
	 * Checks if {@link #getOutdatedVersionHovers()} should be displayed to players with an outdated version.
	 * 
	 * <p><strong>Found at:</strong> "motd.outdated-version.hovers.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display hovers to players running an outdated version
	 */
	public boolean areOutdatedVersionHoversEnabled() {
		return outdatedVersionHoversEnabled;
	}
	
	/**
	 * Checks if {@link #getOutdatedVersionVersionNames()} should be displayed to players with an outdated version.
	 * 
	 * <p><strong>Found at:</strong> "motd.outdated-version.version-names.enabled" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Whether to display version names to players running an outdated version
	 */
	public boolean areOutdatedVersionVersionNamesEnabled() {
		return outdatedVersionVersionNamesEnabled;
	}
	
	/**
	 * Gets the minimum supported version's protocol.
	 * 
	 * <p><strong>Found at:</strong> "motd.minimum-supported-version-protocol" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Minimum supported version's protocol
	 */
	public int getMinimumSupportedVersionProtocol() {
		return minimumSupportedVersionProtocol;
	}
	
	/**
	 * Gets the server socket's port.
	 * 
	 * <p><strong>Found at:</strong> "motd.server-socket.port" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Server socket's port [0-65535]
	 */
	public int getServerSocketPort() {
		return serverSocketPort;
	}
	
	/**
	 * Gets the max players' fixed value.
	 * 
	 * <p><strong>Found at:</strong> "motd.max-players.fixed-value" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Max players' fixed value
	 */
	public int getMaxPlayersFixedValue() {
		return maxPlayersFixedValue;
	}
	
	/**
	 * Gets the server socket's address.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "motd.server-socket.address" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Server socket's address
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public InetAddress getServerSocketAddress() {
		return serverSocketAddress;
	}
	
	/**
	 * Gets the URL of the icon displayed to unknown players.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "motd.unknown-player.icon-url" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Unknown player's icon's URL
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public URL getUnknownPlayerIconURL() {
		return unknownPlayerIconURL;
	}
	
	/**
	 * Gets the URL of the icon displayed to stored players.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "motd.stored-player.icon-url" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Stored player's icon's URL
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public URL getStoredPlayerIconURL() {
		return storedPlayerIconURL;
	}
	
	/**
	 * Gets the URL of the icon displayed to banned players.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "motd.banned-player.icon-url" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Banned player's icon's URL
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public URL getBannedPlayerIconURL() {
		return bannedPlayerIconURL;
	}
	
	/**
	 * Gets the URL of the icon displayed to players running an outdated version.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "motd.outdated-version.icon-url" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Outdated version player's icon's URL
	 */
	@Nullable(why = "Null if this manager is not enabled")
	public URL getOutdatedVersionIconURL() {
		return outdatedVersionIconURL;
	}
	
	/**
	 * Gets the MoTD's descriptions for an unknown player.
	 * 
	 * <p><strong>Found at:</strong> "motd.unknown-player.descriptions" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Unknown player's descriptions
	 */
	public Map<Language, List<String>> getUnknownPlayerDescriptions() {
		return unknownPlayerDescriptions;
	}
	
	/**
	 * Gets the MoTD's hovers for an unknown player.
	 * 
	 * <p><strong>Found at:</strong> "motd.unknown-player.hovers.values" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Unknown player's hovers
	 */
	public Map<Language, List<String>> getUnknownPlayerHovers() {
		return unknownPlayerHovers;
	}
	
	/**
	 * Gets the MoTD's version names for an unknown player.
	 * 
	 * <p><strong>Found at:</strong> "motd.unknown-player.version-names.values" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Unknown player's version names
	 */
	public Map<Language, List<String>> getUnknownPlayerVersionNames() {
		return unknownPlayerVersionNames;
	}
	
	/**
	 * Gets the MoTD's descriptions for a stored player.
	 * 
	 * <p><strong>Found at:</strong> "motd.stored-player.descriptions" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Stored player's descriptions
	 */
	public Map<Language, List<String>> getStoredPlayerDescriptions() {
		return storedPlayerDescriptions;
	}
	
	/**
	 * Gets the MoTD's hovers for a stored player.
	 * 
	 * <p><strong>Found at:</strong> "motd.stored-player.hovers.values" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Stored player's hovers
	 */
	public Map<Language, List<String>> getStoredPlayerHovers() {
		return storedPlayerHovers;
	}
	
	/**
	 * Gets the MoTD's version names for a stored player.
	 * 
	 * <p><strong>Found at:</strong> "motd.stored-player.version-names.values" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Stored player's version names
	 */
	public Map<Language, List<String>> getStoredPlayerVersionNames() {
		return storedPlayerVersionNames;
	}
	
	/**
	 * Gets the MoTD's descriptions for a banned player.
	 * 
	 * <p><strong>Found at:</strong> "motd.banned-player.descriptions" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Banned player's descriptions
	 */
	public Map<Language, List<String>> getBannedPlayerDescriptions() {
		return bannedPlayerDescriptions;
	}
	
	/**
	 * Gets the MoTD's hovers for a banned player.
	 * 
	 * <p><strong>Found at:</strong> "motd.banned-player.hovers.values" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Banned player's hovers
	 */
	public Map<Language, List<String>> getBannedPlayerHovers() {
		return bannedPlayerHovers;
	}
	
	/**
	 * Gets the MoTD's version names for a banned player.
	 * 
	 * <p><strong>Found at:</strong> "motd.banned-player.version-names.values" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Banned player's version names
	 */
	public Map<Language, List<String>> getBannedPlayerVersionNames() {
		return bannedPlayerVersionNames;
	}
	
	/**
	 * Gets the MoTD's descriptions for a player with an outdated version.
	 * 
	 * <p><strong>Found at:</strong> "motd.outdated-version.descriptions" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Outdated version player's descriptions
	 */
	public Map<Language, List<String>> getOutdatedVersionDescriptions() {
		return outdatedVersionDescriptions;
	}
	
	/**
	 * Gets the MoTD's hovers for a player with an outdated version.
	 * 
	 * <p><strong>Found at:</strong> "motd.outdated-version.hovers.values" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Outdated version player's hovers
	 */
	public Map<Language, List<String>> getOutdatedVersionHovers() {
		return outdatedVersionHovers;
	}
	
	/**
	 * Gets the MoTD's version names for a player with an outdated version.
	 * 
	 * <p><strong>Found at:</strong> "motd.outdated-version.version-names.values" in {@link ConfigurationType#MOTD}</p>
	 * 
	 * @return Outdated version player's version names
	 */
	public Map<Language, List<String>> getOutdatedVersionVersionNames() {
		return outdatedVersionVersionNames;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ServerMoTDManager getInstance() {
		return (ServerMoTDManager) instance;
	}
	
	/**
	 * Gets a new MoTD for an IP address translated using
	 * {@link LanguageDetector#detectUsingGeolocalization(IPLookup)}
	 * if necessary (if it is not associated to a player's language).
	 * 
	 * <p>Note that this method might take some time
	 * to be executed: async calls are recommended.</p>
	 * 
	 * @param ipAddress The player's IP address
	 * @param version The player's version
	 * @return New MoTD
	 */
	public abstract MoTD getMoTD(InetAddress ipAddress, Version version);
	
	/**
	 * Gets a new MoTD for an IP address
	 * translated in the specified language.
	 * 
	 * <p>Note that this method might take some time
	 * to be executed: async calls are recommended.</p>
	 * 
	 * @param ipAddress The player's IP address
	 * @param version The player's version
	 * @param language Language used to translate the MoTD
	 * @return New MoTD
	 */
	public abstract MoTD getMoTD(InetAddress ipAddress, Version version, Language language);
	
}
