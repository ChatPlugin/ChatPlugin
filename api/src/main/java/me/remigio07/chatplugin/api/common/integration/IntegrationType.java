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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.remigio07.chatplugin.api.common.integration.multiplatform.MultiPlatformIntegration;
import me.remigio07.chatplugin.api.common.integration.permission.PermissionIntegration;
import me.remigio07.chatplugin.api.common.integration.version.VersionIntegration;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
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
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 * 
 * @param <T> Integration's interface
 * @see IntegrationManager
 */
public class IntegrationType<T extends ChatPluginIntegration> {
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/combatlogx.31689/">CombatLogX</a> integration.
	 */
	public static final IntegrationType<CombatLogIntegration> COMBATLOGX = new IntegrationType<>("CombatLogX", "com.github.sirblobman.combatlogx.CombatPlugin", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/discordsrv.18494/">DiscordSRV</a> integration.
	 */
	public static final IntegrationType<SocialIntegration> DISCORDSRV = new IntegrationType<>("DiscordSRV", "github.scarsz.discordsrv.DiscordSRV", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://essentialsx.net/">EssentialsX</a> integration.
	 */
	public static final IntegrationType<EconomyIntegration> ESSENTIALSX = new IntegrationType<>("Essentials", "com.earth2me.essentials.Essentials", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://www.gadgetsmenu.net/">GadgetsMenu</a> integration.
	 */
	public static final IntegrationType<CosmeticsIntegration> GADGETSMENU = new IntegrationType<>("GadgetsMenu", "com.yapzhenyie.GadgetsMenu.GadgetsMenu", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://geysermc.org/">GeyserMC</a> integration.
	 */
	public static final IntegrationType<MultiPlatformIntegration> GEYSERMC = new IntegrationType<>("GeyserMC", "org.geysermc.geyser.GeyserMain", Arrays.asList(Environment.values()));
	
	/**
	 * Represents the <a href="https://luckperms.net/">LuckPerms</a> integration.
	 */
	public static final IntegrationType<PermissionIntegration> LUCKPERMS = new IntegrationType<>("LuckPerms", "net.luckperms.api.LuckPerms", Arrays.asList(Environment.values()));
	
	/**
	 * Represents the <a href="https://matrix.rip/">Matrix</a> integration.
	 */
	public static final IntegrationType<AnticheatIntegration> MATRIX = new IntegrationType<>("Matrix", "me.rerere.matrix.Matrix", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/mvdwplaceholderapi.11182/">MVdWPlaceholderAPI</a> integration.
	 */
	public static final IntegrationType<PlaceholderIntegration> MVDWPLACEHOLDERAPI = new IntegrationType<>("MVdWPlaceholderAPI", "be.maximvdw.placeholderapi.PlaceholderAPI", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://github.com/Elikill58/Negativity">Negativity</a> integration.
	 */
	public static final IntegrationType<AnticheatIntegration> NEGATIVITY = new IntegrationType<>("Negativity", "com.elikill58.negativity.universal.Version", Arrays.asList(Environment.BUKKIT, Environment.SPONGE));
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/placeholderapi.6245/">PlaceholderAPI</a> integration.
	 */
	public static final IntegrationType<PlaceholderIntegration> PLACEHOLDERAPI = new IntegrationType<>("PlaceholderAPI", "me.clip.placeholderapi.PlaceholderAPI", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/playerparticles.40261/">PlayerParticles</a> integration.
	 */
	public static final IntegrationType<CosmeticsIntegration> PLAYERPARTICLES = new IntegrationType<>("PlayerParticles", "dev.esophose.playerparticles.PlayerParticles", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/protocolsupport.7201/">ProtocolSupport</a> integration.
	 */
	public static final IntegrationType<VersionIntegration> PROTOCOLSUPPORT = new IntegrationType<>("ProtocolSupport", "protocolsupport.ProtocolSupport", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/1-8-8-1-20-1-ultra-cosmetics-opensource-free.10905/">UltraCosmetics</a> integration.
	 */
	public static final IntegrationType<CosmeticsIntegration> ULTRACOSMETICS = new IntegrationType<>("UltraCosmetics", "be.isach.ultracosmetics.UltraCosmetics", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/vault.34315/">Vault</a> integration.
	 * 
	 * <p>This is considered an {@link EconomyIntegration} but it is
	 * used internally by the plugin also to check permissions.</p>
	 */
	public static final IntegrationType<EconomyIntegration> VAULT = new IntegrationType<>("Vault", "net.milkbowl.vault.Vault", Arrays.asList(Environment.BUKKIT)); // Vault is also a PermissionIntegration, but we consider it as an EconomyIntegration
	
	/**
	 * Represents the <a href="https://github.com/ViaVersion/ViaVersion">ViaVersion</a> integration.
	 */
	public static final IntegrationType<VersionIntegration> VIAVERSION = new IntegrationType<>("ViaVersion", "com.viaversion.viaversion.api.Via", Arrays.asList(Environment.values()));
	
	/**
	 * Represents the <a href="https://www.spigotmc.org/resources/vulcan-anti-cheat-advanced-cheat-detection-1-7-1-20-1.83626/">Vulcan</a> integration.
	 */
	public static final IntegrationType<AnticheatIntegration> VULCAN = new IntegrationType<>("Vulcan", "me.frep.vulcan.spigot.VulcanPlugin", Arrays.asList(Environment.BUKKIT));
	
	/**
	 * Represents the <a href="https://enginehub.org/worldguard">WorldGuard</a> integration.
	 */
	public static final IntegrationType<RegionIntegration> WORLDGUARD = new IntegrationType<>("WorldGuard", "com.sk89q.worldguard.WorldGuard", Arrays.asList(Environment.BUKKIT, Environment.SPONGE));
	private static final IntegrationType<?>[] VALUES = new IntegrationType[] { COMBATLOGX, DISCORDSRV, ESSENTIALSX, GADGETSMENU, GEYSERMC, LUCKPERMS, MATRIX, MVDWPLACEHOLDERAPI, NEGATIVITY, PLACEHOLDERAPI, PLAYERPARTICLES, PROTOCOLSUPPORT, ULTRACOSMETICS, VAULT, VIAVERSION, VULCAN, WORLDGUARD };
	private String plugin, clazz;
	private List<Environment> supportedEnvironments;
	
	private IntegrationType(String plugin, String clazz, List<Environment> supportedEnvironments) {
		this.plugin = plugin;
		this.clazz = clazz;
		this.supportedEnvironments = supportedEnvironments;
	}
	
	/**
	 * Equivalent of {@link Enum#name()}.
	 * 
	 * @return Constant's name
	 */
	public String name() {
		return plugin.toUpperCase();
	}
	
	/**
	 * Equivalent of {@link Enum#ordinal()}.
	 * 
	 * @return Constant's ordinal
	 */
	public int ordinal() {
		for (int i = 0; i < VALUES.length; i++)
			if (this == VALUES[i])
				return i;
		return -1;
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
	 * Gets a list containing the environments
	 * this integrations can run on.
	 * 
	 * @return Integration's supported environments
	 */
	public List<Environment> getSupportedEnvironments() {
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
	 * Equivalent of <code>Enum#valueOf(String)</code>,
	 * with the only difference that instead of throwing
	 * {@link IllegalArgumentException} <code>null</code>
	 * is returned if the constant's name is invalid.
	 * 
	 * @param name Constant's name
	 * @return Enum constant
	 */
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the constant's name is invalid")
	public static IntegrationType<?> valueOf(String name) {
		for (IntegrationType<?> integration : VALUES)
			if (integration.name().equals(name))
				return integration;
		return null;
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
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
