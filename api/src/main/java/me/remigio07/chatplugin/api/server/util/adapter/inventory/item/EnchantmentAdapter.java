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

package me.remigio07.chatplugin.api.server.util.adapter.inventory.item;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) enchantment adapter.
 */
public class EnchantmentAdapter extends PseudoEnum<EnchantmentAdapter> { // TODO: add support for custom enchantments - read them from the registry instead of loading from these fields?
	
	/**
	 * Increases the speed at which a player may mine underwater.
	 */
	public static final EnchantmentAdapter AQUA_AFFINITY = new EnchantmentAdapter("AQUA_AFFINITY", new String[] { "aqua_affinity", "aqua_aff", "aqua", "water_work", "water", "WATER_WORKER" });
	
	/**
	 * Increases damage against arthropod targets.
	 */
	public static final EnchantmentAdapter BANE_OF_ARTHROPODS = new EnchantmentAdapter("BANE_OF_ARTHROPODS", new String[] { "bane_of_arthropods", "art_damage", "bane", "DAMAGE_ARTHROPODS" }, 5);
	
	/**
	 * Item cannot be removed.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_11 1.11}</p>
	 */
	public static final EnchantmentAdapter BINDING_CURSE = new EnchantmentAdapter("BINDING_CURSE", new String[] { "binding_curse", "binding" }, 1, Version.V1_11);
	
	/**
	 * Provides protection against explosive damage.
	 */
	public static final EnchantmentAdapter BLAST_PROTECTION = new EnchantmentAdapter("BLAST_PROTECTION", new String[] { "blast_protection", "blast_protect", "blast_prot", "explosions_protection", "explosions_protect", "explosions_prot", "explosions", "explode", "expl", "PROTECTION_EXPLOSIONS" }, 4);
	
	/**
	 * Reduces armor effectiveness against maces.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_21 1.21}</p>
	 */
	public static final EnchantmentAdapter BREACH = new EnchantmentAdapter("BREACH", new String[] { "breach" }, 4, Version.V1_21);
	
	/**
	 * Strikes lightning when a mob is hit with a trident if conditions are stormy.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_13 1.13}</p>
	 */
	public static final EnchantmentAdapter CHANNELING = new EnchantmentAdapter("CHANNELING", new String[] { "channeling" }, 1, Version.V1_13);
	
	/**
	 * Increases fall damage of maces.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_21 1.21}</p>
	 */
	public static final EnchantmentAdapter DENSITY = new EnchantmentAdapter("DENSITY", new String[] { "density" }, 5, Version.V1_21);
	
	/**
	 * Increases walking speed while in water.
	 */
	public static final EnchantmentAdapter DEPTH_STRIDER = new EnchantmentAdapter("DEPTH_STRIDER", new String[] { "depth_strider" }, 3);
	
	/**
	 * Increases the rate at which you mine/dig.
	 */
	public static final EnchantmentAdapter EFFICIENCY = new EnchantmentAdapter("EFFICIENCY", new String[] { "efficiency", "eff", "haste", "dig", "dig_speed" }, 5);
	
	/**
	 * Provides protection against fall damage.
	 */
	public static final EnchantmentAdapter FEATHER_FALLING = new EnchantmentAdapter("FEATHER_FALLING", new String[] { "feather_falling", "feather", "feather_fall", "fall", "fall_damage", "protection_fall" }, 4);
	
	/**
	 * When attacking a target, has a chance to set them on fire.
	 */
	public static final EnchantmentAdapter FIRE_ASPECT = new EnchantmentAdapter("FIRE_ASPECT", new String[] { "fire_aspect", "fire" }, 2);
	
	/**
	 * Provides protection against fire damage.
	 */
	public static final EnchantmentAdapter FIRE_PROTECTION = new EnchantmentAdapter("FIRE_PROTECTION", new String[] { "fire_protection", "fire_protect", "fire_prot", "burn_protection", "burn_protect", "burn_prot", "burn", "protection_fire" }, 4);
	
	/**
	 * Sets entities on fire when hit by arrows shot from a bow.
	 */
	public static final EnchantmentAdapter FLAME = new EnchantmentAdapter("FLAME", new String[] { "flame", "arrow_fire" });
	
	/**
	 * Provides a chance of gaining extra loot when destroying blocks.
	 */
	public static final EnchantmentAdapter FORTUNE = new EnchantmentAdapter("FORTUNE", new String[] { "fortune", "loot_bonus_blocks" }, 3);
	
	/**
	 * Freezes any still water adjacent to ice / frost which player is walking on.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final EnchantmentAdapter FROST_WALKER = new EnchantmentAdapter("FROST_WALKER", new String[] { "frost_walker", "frost" }, 2, Version.V1_9);
	
	/**
	 * Deals more damage to mobs that live in the ocean.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_13 1.13}</p>
	 */
	public static final EnchantmentAdapter IMPALING = new EnchantmentAdapter("IMPALING", new String[] { "impaling" }, 5, Version.V1_13);
	
	/**
	 * Provides infinite arrows when shooting a bow.
	 */
	public static final EnchantmentAdapter INFINITY = new EnchantmentAdapter("INFINITY", new String[] { "infinity", "inf", "arrow_infinite" });
	
	/**
	 * All damage to other targets will knock them back when hit.
	 */
	public static final EnchantmentAdapter KNOCKBACK = new EnchantmentAdapter("KNOCKBACK", new String[] { "knockback", "knock", "kback" }, 2);
	
	/**
	 * Provides a chance of gaining extra loot when killing monsters.
	 */
	public static final EnchantmentAdapter LOOTING = new EnchantmentAdapter("LOOTING", new String[] { "looting", "loot_bonus_mobs" }, 3);
	
	/**
	 * Causes a thrown trident to return to the player who threw it.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_13 1.13}</p>
	 */
	public static final EnchantmentAdapter LOYALTY = new EnchantmentAdapter("LOYALTY", new String[] { "loyalty", "loyal" }, 3, Version.V1_13);
	
	/**
	 * Decreases odds of catching worthless junk.
	 */
	public static final EnchantmentAdapter LUCK_OF_THE_SEA = new EnchantmentAdapter("LUCK_OF_THE_SEA", new String[] { "luck_of_the_sea", "luck_of_sea", "luck_sea", "sea_luck", "luck" }, 3);
	
	/**
	 * Increases rate of fish biting your hook.
	 */
	public static final EnchantmentAdapter LURE = new EnchantmentAdapter("LURE", new String[] { "lure" }, 3);
	
	/**
	 * Allows mending the item using experience orbs.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final EnchantmentAdapter MENDING = new EnchantmentAdapter("MENDING", new String[] { "mending", "mend" }, 1, Version.V1_9);
	
	/**
	 * Shoot multiple arrows from crossbows.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_14 1.14}</p>
	 */
	public static final EnchantmentAdapter MULTISHOT = new EnchantmentAdapter("MULTISHOT", new String[] { "multishot", "mshot" }, 1, Version.V1_14);
	
	/**
	 * Crossbow projectiles pierce entities.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_14 1.14}</p>
	 */
	public static final EnchantmentAdapter PIERCING = new EnchantmentAdapter("PIERCING", new String[] { "piercing" }, 4, Version.V1_14);
	
	/**
	 * Provides extra damage when shooting arrows from bows.
	 */
	public static final EnchantmentAdapter POWER = new EnchantmentAdapter("POWER", new String[] { "power", "pow", "arrow_damage" }, 5);
	
	/**
	 * Provides protection against projectile damage.
	 */
	public static final EnchantmentAdapter PROJECTILE_PROTECTION = new EnchantmentAdapter("PROJECTILE_PROTECTION", new String[] { "projectile_protection", "projectile_protect", "projectile_prot", "proj_protection", "proj_protect", "proj_prot", "projectiles", "proj", "protection_projectile" }, 4);
	
	/**
	 * Provides protection against environmental damage.
	 */
	public static final EnchantmentAdapter PROTECTION = new EnchantmentAdapter("PROTECTION", new String[] { "protection", "protect", "prot", "protection_environmental" }, 4);
	
	/**
	 * Provides a knockback when an entity is hit by an arrow from a bow.
	 */
	public static final EnchantmentAdapter PUNCH = new EnchantmentAdapter("PUNCH", new String[] { "punch", "arrow_knockback" }, 2);
	
	/**
	 * Charges crossbows quickly.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_14 1.14}</p>
	 */
	public static final EnchantmentAdapter QUICK_CHARGE = new EnchantmentAdapter("QUICK_CHARGE", new String[] { "quick_charge", "quick", "charge" }, 3, Version.V1_14);
	
	/**
	 * Decreases the rate of air loss whilst underwater.
	 */
	public static final EnchantmentAdapter RESPIRATION = new EnchantmentAdapter("RESPIRATION", new String[] { "respiration", "resp", "o2", "oxygen" }, 3);
	
	/**
	 * When it is rainy, launches the player in the direction their trident is thrown.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_13 1.13}</p>
	 */
	public static final EnchantmentAdapter RIPTIDE = new EnchantmentAdapter("RIPTIDE", new String[] { "riptide", "riptiding" }, 3, Version.V1_13);
	
	/**
	 * Increases damage against all targets.
	 */
	public static final EnchantmentAdapter SHARPNESS = new EnchantmentAdapter("SHARPNESS", new String[] { "sharpness", "sharp", "damage_all" }, 5);
	
	/**
	 * Allows blocks to drop themselves instead of fragments (for example, stone instead of cobblestone).
	 */
	public static final EnchantmentAdapter SILK_TOUCH = new EnchantmentAdapter("SILK_TOUCH", new String[] { "silk_touch", "silk", "touch" });
	
	/**
	 * Increases damage against undead targets.
	 */
	public static final EnchantmentAdapter SMITE = new EnchantmentAdapter("SMITE", new String[] { "smite", "undead", "damage_undead" }, 5);
	
	/**
	 * Walk quicker on soul blocks.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_16 1.16}</p>
	 */
	public static final EnchantmentAdapter SOUL_SPEED = new EnchantmentAdapter("SOUL_SPEED", new String[] { "soul_speed", "soul", "soul_sp" }, 3, Version.V1_16);
	
	/**
	 * Increases damage against targets when using a sweep attack.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_11_1 1.11.1}</p>
	 */
	public static final EnchantmentAdapter SWEEPING_EDGE = new EnchantmentAdapter("SWEEPING_EDGE", new String[] { "sweeping_edge", "sweeping" }, 3, Version.V1_11_1);
	
	/**
	 * Walk quicker while sneaking.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_19 1.19}</p>
	 */
	public static final EnchantmentAdapter SWIFT_SNEAK = new EnchantmentAdapter("SWIFT_SNEAK", new String[] { "swift_sneak", "swift", "sneak" }, 3, Version.V1_19);
	
	/**
	 * Damages the attacker.
	 */
	public static final EnchantmentAdapter THORNS = new EnchantmentAdapter("THORNS", new String[] { "thorns" }, 3);
	
	/**
	 * Decreases the rate at which a tool looses durability.
	 */
	public static final EnchantmentAdapter UNBREAKING = new EnchantmentAdapter("UNBREAKING", new String[] { "unbreaking", "durability" }, 3);
	
	/**
	 * Item disappears instead of dropping.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_11 1.11}</p>
	 */
	public static final EnchantmentAdapter VANISHING_CURSE = new EnchantmentAdapter("VANISHING_CURSE", new String[] { "vanishing_curse", "vanishing", "vanish_curse" }, 1, Version.V1_11);
	
	/**
	 * Emits wind burst upon hitting enemy.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_20_5 1.20.5}</p>
	 */
	public static final EnchantmentAdapter WIND_BURST = new EnchantmentAdapter("WIND_BURST", new String[] { "wind_burst", "wind" }, 3, Version.V1_20_5);
	private static final EnchantmentAdapter[] VALUES = new EnchantmentAdapter[] { AQUA_AFFINITY, BANE_OF_ARTHROPODS, BINDING_CURSE, BLAST_PROTECTION, BREACH, CHANNELING, DENSITY, DEPTH_STRIDER, EFFICIENCY, FEATHER_FALLING, FIRE_ASPECT, FIRE_PROTECTION, FLAME, FORTUNE, FROST_WALKER, IMPALING, INFINITY, KNOCKBACK, LOOTING, LOYALTY, LUCK_OF_THE_SEA, LURE, MENDING, MULTISHOT, PIERCING, POWER, PROJECTILE_PROTECTION, PROTECTION, PUNCH, QUICK_CHARGE, RESPIRATION, RIPTIDE, SHARPNESS, SILK_TOUCH, SMITE, SOUL_SPEED, SWEEPING_EDGE, SWIFT_SNEAK, THORNS, UNBREAKING, VANISHING_CURSE, WIND_BURST };
	private static int ordinal = 0;
	private String[] aliases;
	private int maximumLevel;
	private Version minimumVersion;
	
	private EnchantmentAdapter(String name, String[] aliases) {
		this(name, aliases, 1);
	}
	
	private EnchantmentAdapter(String name, String[] aliases, int maximumLevel) {
		this(name, aliases, maximumLevel, null);
	}
	
	private EnchantmentAdapter(String name, String[] aliases, int maximumLevel, Version minimumVersion) {
		super(name, ordinal++);
		this.aliases = aliases;
		this.maximumLevel = maximumLevel;
		this.minimumVersion = minimumVersion;
	}
	
	/**
	 * Gets the enchantment adapted for Bukkit environments.
	 * 
	 * <p>If {@link VersionUtils#getVersion()} does not support this enchantment,
	 * the default value of {@link #UNBREAKING} will be returned.</p>
	 * 
	 * @return Bukkit-adapted enchantment
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	@SuppressWarnings("deprecation")
	public org.bukkit.enchantments.Enchantment bukkitValue() {
		if (Environment.isBukkit()) {
			org.bukkit.enchantments.Enchantment enchantment = org.bukkit.enchantments.Enchantment.getByName(aliases[aliases.length - 1].toUpperCase());
			return enchantment == null ? org.bukkit.enchantments.Enchantment.UNBREAKING : enchantment;
		} throw new UnsupportedOperationException("Unable to adapt enchantment to a Bukkit's Enchantment on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the enchantment adapted for Sponge environments.
	 * 
	 * <p>If {@link VersionUtils#getVersion()} does not support this enchantment,
	 * the default value of {@link #UNBREAKING} will be returned.</p>
	 * 
	 * @return Sponge-adapted enchantment
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public EnchantmentType spongeValue() {
		if (Environment.isSponge())
			try {
				return (EnchantmentType) EnchantmentTypes.class.getField(name()).get(null);
			} catch (NullPointerException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
				return EnchantmentTypes.UNBREAKING;
			}
		throw new UnsupportedOperationException("Unable to adapt enchantment to a Sponge's EnchantmentType on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the enchantment adapted for Fabric environments.
	 * 
	 * <p>If {@link VersionUtils#getVersion()} does not support this enchantment,
	 * the default value of {@link #UNBREAKING} will be returned.</p>
	 * 
	 * @return Fabric-adapted enchantment
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public Enchantment fabricValue() {
		if (Environment.isFabric())
			try {
				if (VersionUtils.getVersion().isAtLeast(Version.V1_21)) {
					Registry<net.minecraft.enchantment.Enchantment> registry = FabricBootstrapper.getInstance().getServer().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
					return VersionUtils.getVersion().isAtLeast(Version.V1_21_2) ? registry.get(Identifier.ofVanilla(aliases[0])) : (Enchantment) Registry.class.getMethod("method_10223", Identifier.class).invoke(registry, Identifier.ofVanilla(aliases[0]));
				} return (Enchantment) Registry.class.getMethod("method_10223", Identifier.class).invoke(VersionUtils.getVersion().isAtLeast(Version.V1_19_3)
						? Registries.class.getField("field_41176").get(null)
						: Registry.class.getField("field_11160").get(null),
						Identifier.tryParse("minecraft:" + aliases[0]));
			} catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
				return UNBREAKING.fabricValue();
			}
		throw new UnsupportedOperationException("Unable to adapt enchantment to a Fabric's Enchantment on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this enchantment's aliases.
	 * 
	 * @return Enchantment's aliases
	 */
	public String[] getAliases() {
		return aliases;
	}
	
	/**
	 * Gets the maximum Vanilla level for this enchantment.
	 * 
	 * @return Enchantment's maximum level
	 */
	public int getMaximumLevel() {
		return maximumLevel;
	}
	
	/**
	 * Gets the minimum supported version for this enchantment.
	 * 
	 * <p>Will return <code>null</code> if all versions
	 * supported by ChatPlugin support this enchantment.</p>
	 * 
	 * @return Enchantment's minimum version
	 */
	public @Nullable(why = "Null if all versions support this enchantment") Version getMinimumVersion() {
		return minimumVersion;
	}
	
	/**
	 * Checks if this enchantment is supported on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this enchantment is supported
	 */
	public boolean isSupported() {
		return minimumVersion == null || VersionUtils.getVersion().isAtLeast(minimumVersion);
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
	public static EnchantmentAdapter valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static EnchantmentAdapter[] values() {
		return VALUES;
	}
	
	/**
	 * Equivalent of {@link #valueOf(String)}, but:
	 * 	<ul>
	 * 		<li>case insensitive</li>
	 * 		<li>returns <code>null</code> instead of throwing {@link IllegalArgumentException}</li>
	 * 		<li>also recognizes Bukkit-, Sponge- and Fabric-compatible IDs</li>
	 * 	</ul>
	 * 
	 * <p>Will return <code>null</code> if the specified name is invalid.</p>
	 * 
	 * @param name Constant's name, case insensitive
	 * @return Pseudo-enum's constant
	 * @throws NullPointerException If <code>name == null</code>
	 */
	@Nullable(why = "Specified name may be invalid")
	public static EnchantmentAdapter value(String name) {
		String lowerCase = name.toLowerCase();
		
		for (EnchantmentAdapter enchantment : VALUES) {
			String[] aliases = enchantment.getAliases();
			
			for (int i = 0; i < aliases.length; i++) {
				if (lowerCase.equals(aliases[i]) || lookupWithoutUnderscore(aliases[i], lowerCase))
					return enchantment;
			}
		} return null;
	}
	
	private static boolean lookupWithoutUnderscore(String alias, String stringToCheck) {
		return alias.contains("_") && alias.replace("_", "").equals(stringToCheck);
	}
	
	/**
	 * Gets the enchantments' Vanilla names.
	 * 
	 * @param onlySupported Whether to only include {@linkplain #isSupported() supported} enchantments
	 * @return Enchantments' Vanilla names
	 */
	public static List<String> getVanillaNames(boolean onlySupported) {
		List<String> list = new ArrayList<>();
		
		for (EnchantmentAdapter enchantment : values())
			if (!onlySupported || enchantment.isSupported())
				list.add(enchantment.getAliases()[0]);
		return list;
	}
	
}

