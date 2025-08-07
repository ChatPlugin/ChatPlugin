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

package me.remigio07.chatplugin.api.common.integration;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.remigio07.chatplugin.api.common.integration.multiplatform.MultiPlatformIntegration;
import me.remigio07.chatplugin.api.common.integration.permission.PermissionIntegration;
import me.remigio07.chatplugin.api.common.integration.version.VersionIntegration;
import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07.chatplugin.api.server.integration.combatlog.CombatLogIntegration;
import me.remigio07.chatplugin.api.server.integration.cosmetics.CosmeticsIntegration;
import me.remigio07.chatplugin.api.server.integration.economy.EconomyIntegration;
import me.remigio07.chatplugin.api.server.integration.placeholder.PlaceholderIntegration;
import me.remigio07.chatplugin.api.server.integration.region.RegionIntegration;
import me.remigio07.chatplugin.api.server.integration.social.SocialIntegration;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Class that contains information about plugins which ChatPlugin can interact with.
 * 
 * @param <T> Integration's interface
 * @see IntegrationManager
 */
public class IntegrationType<T extends ChatPluginIntegration> extends PseudoEnum<IntegrationType<T>> {
	
	private static final Set<Environment> BUKKIT = EnumSet.of(Environment.BUKKIT);
	private static final Set<Environment> NO_SPONGE = EnumSet.of(Environment.BUKKIT, Environment.BUNGEECORD, Environment.VELOCITY);
	private static final Set<Environment> ALL = EnumSet.allOf(Environment.class);
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/combatlogx.31689/">CombatLogX</a> integration.
	 */
	public static final IntegrationType<CombatLogIntegration> COMBATLOGX = new IntegrationType<>("CombatLogX", "com.github.sirblobman.combatlogx.CombatPlugin", BUKKIT);
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/discordsrv.18494/">DiscordSRV</a> integration.
	 */
	public static final IntegrationType<SocialIntegration> DISCORDSRV = new IntegrationType<>("DiscordSRV", "github.scarsz.discordsrv.DiscordSRV", BUKKIT);
	
	/**
	 * Represents the <a href="https://essentialsx.net/">EssentialsX</a> integration.
	 */
	public static final IntegrationType<EconomyIntegration> ESSENTIALSX = new IntegrationType<>("Essentials", "com.earth2me.essentials.Essentials", BUKKIT);
	
	/**
	 * Represents the <a href="https://geysermc.org/wiki/floodgate/">Floodgate</a> integration.
	 */
	public static final IntegrationType<MultiPlatformIntegration> FLOODGATE = new IntegrationType<>("Floodgate", "org.geysermc.floodgate.api.FloodgateApi", NO_SPONGE);
	
	/**
	 * Represents the <a href="https://www.gadgetsmenu.net/">GadgetsMenu</a> integration.
	 */
	public static final IntegrationType<CosmeticsIntegration> GADGETSMENU = new IntegrationType<>("GadgetsMenu", "com.yapzhenyie.GadgetsMenu.GadgetsMenu", BUKKIT);
	
	/**
	 * Represents the <a href="https://geysermc.org/wiki/geyser/">Geyser</a> integration.
	 */
	public static final IntegrationType<MultiPlatformIntegration> GEYSER = new IntegrationType<>("Geyser", "org.geysermc.geyser.api.GeyserApi", NO_SPONGE);
	
	/**
	 * Represents the <a href="https://luckperms.net/">LuckPerms</a> integration.
	 */
	public static final IntegrationType<PermissionIntegration> LUCKPERMS = new IntegrationType<>("LuckPerms", "net.luckperms.api.LuckPerms", ALL);
	
	/**
	 * Represents the <a href="https://matrix.rip/">Matrix</a> integration.
	 */
	public static final IntegrationType<AnticheatIntegration> MATRIX = new IntegrationType<>("Matrix", "me.rerere.matrix.Matrix", BUKKIT);
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/mvdwplaceholderapi.11182/">MVdWPlaceholderAPI</a> integration.
	 */
	public static final IntegrationType<PlaceholderIntegration> MVDWPLACEHOLDERAPI = new IntegrationType<>("MVdWPlaceholderAPI", "be.maximvdw.placeholderapi.PlaceholderAPI", BUKKIT);
	
	/**
	 * Represents the <a href="https://github.com/Elikill58/Negativity">Negativity</a> integration.
	 */
	public static final IntegrationType<AnticheatIntegration> NEGATIVITY = new IntegrationType<>("Negativity", "com.elikill58.negativity.universal.Version", EnumSet.of(Environment.BUKKIT, Environment.SPONGE));
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/placeholderapi.6245/">PlaceholderAPI</a> integration.
	 */
	public static final IntegrationType<PlaceholderIntegration> PLACEHOLDERAPI = new IntegrationType<>("PlaceholderAPI", "me.clip.placeholderapi.PlaceholderAPI", BUKKIT);
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/playerparticles.40261/">PlayerParticles</a> integration.
	 */
	public static final IntegrationType<CosmeticsIntegration> PLAYERPARTICLES = new IntegrationType<>("PlayerParticles", "dev.esophose.playerparticles.PlayerParticles", BUKKIT);
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/protocolsupport.7201/">ProtocolSupport</a> integration.
	 */
	public static final IntegrationType<VersionIntegration> PROTOCOLSUPPORT = new IntegrationType<>("ProtocolSupport", "protocolsupport.ProtocolSupport", BUKKIT);
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/1-8-8-1-20-1-ultra-cosmetics-opensource-free.10905/">UltraCosmetics</a> integration.
	 */
	public static final IntegrationType<CosmeticsIntegration> ULTRACOSMETICS = new IntegrationType<>("UltraCosmetics", "be.isach.ultracosmetics.UltraCosmetics", BUKKIT);
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/vault.34315/">Vault</a> integration.
	 * 
	 * <p>This is considered an {@link EconomyIntegration} but it is also a
	 * {@link PermissionIntegration} used to check permissions internally.</p>
	 */
	public static final IntegrationType<EconomyIntegration> VAULT = new IntegrationType<>("Vault", "net.milkbowl.vault.Vault", BUKKIT);
	
	/**
	 * Represents the <a href="https://github.com/ViaVersion/ViaVersion">ViaVersion</a> integration.
	 */
	public static final IntegrationType<VersionIntegration> VIAVERSION = new IntegrationType<>("ViaVersion", "com.viaversion.viaversion.api.Via", ALL);
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/vulcan-anti-cheat-advanced-cheat-detection-1-7-1-20-1.83626/">Vulcan</a> integration.
	 */
	public static final IntegrationType<AnticheatIntegration> VULCAN = new IntegrationType<>("Vulcan", "me.frep.vulcan.spigot.VulcanPlugin", BUKKIT);
	
	/**
	 * Represents the <a href="https://enginehub.org/worldguard">WorldGuard</a> integration.
	 */
	public static final IntegrationType<RegionIntegration> WORLDGUARD = new IntegrationType<>("WorldGuard", "com.sk89q.worldguard.WorldGuard", BUKKIT);
	private static final IntegrationType<?>[] VALUES = new IntegrationType[] { COMBATLOGX, DISCORDSRV, ESSENTIALSX, FLOODGATE, GADGETSMENU, GEYSER, LUCKPERMS, MATRIX, MVDWPLACEHOLDERAPI, NEGATIVITY, PLACEHOLDERAPI, PLAYERPARTICLES, PROTOCOLSUPPORT, ULTRACOSMETICS, VAULT, VIAVERSION, VULCAN, WORLDGUARD };
	private static int ordinal = 0;
	private String plugin, clazz;
	private Set<Environment> supportedEnvironments;
	
	private IntegrationType(String plugin, String clazz, Set<Environment> supportedEnvironments) {
		super(plugin.toUpperCase(), ordinal++);
		this.plugin = plugin;
		this.clazz = clazz;
		this.supportedEnvironments = supportedEnvironments;
	}
	
	/**
	 * Gets the instance of the class used by
	 * ChatPlugin to interact with the plugin.
	 * 
	 * @return Integration's class' instance
	 */
	public T get() {
		return IntegrationManager.getInstance().getIntegration(this);
	}
	
	/**
	 * Gets this integration's plugin's name.
	 * 
	 * @return Integration's plugin name
	 */
	public String getPlugin() {
		return plugin;
	}
	
	/**
	 * Gets this integration's main class or a random
	 * class if a main class is not applicable.
	 * 
	 * @return A class of this integration
	 */
	public String getClazz() {
		return clazz;
	}
	
	/**
	 * Gets a set containing the environments
	 * this integrations can run on.
	 * 
	 * @return Integration's supported environments
	 */
	public Set<Environment> getSupportedEnvironments() {
		return supportedEnvironments;
	}
	
	/**
	 * Checks if this integration is enabled.
	 * 
	 * @return Whether this integration is enabled
	 */
	public boolean isEnabled() {
		T integration = get();
		return integration != null && integration.isEnabled();
	}
	
	/**
	 * Equivalent of <code>valueOf(String)</code>.
	 * 
	 * @param name Constant's name
	 * @return Pseudo-enum's constant
	 * @throws NullPointerException If <code>name == null</code>
	 * @throws IllegalArgumentException If {@link #values()}
	 * does not contain a constant with the specified name
	 */
	public static IntegrationType<?> valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static IntegrationType<?>[] values() {
		return VALUES;
	}
	
	/**
	 * Gets the integrations types supported
	 * on {@link Environment#getCurrent()}.
	 * 
	 * @return Supported integrations types
	 */
	public static List<IntegrationType<?>> getSupportedIntegrations() {
		return Stream.of(values()).filter(type -> type.getSupportedEnvironments().contains(Environment.getCurrent())).collect(Collectors.toList());
	}
	
}
