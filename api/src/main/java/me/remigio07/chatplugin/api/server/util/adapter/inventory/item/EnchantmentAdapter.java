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

package me.remigio07.chatplugin.api.server.util.adapter.inventory.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.enchantments.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) enchantment adapter.
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 */
public class EnchantmentAdapter {
	
	/**
	 * Increases the speed at which a player may mine underwater.
	 */
	public static final EnchantmentAdapter AQUA_AFFINITY = new EnchantmentAdapter("AQUA_AFFINITY", new String[] { "aqua_affinity", "aqua_aff", "aqua", "water_work", "water", "WATER_WORKER" });
	
	/**
	 * Increases damage against arthropod targets.
	 */
	public static final EnchantmentAdapter BANE_OF_ARTHROPODS = new EnchantmentAdapter("BANE_OF_ARTHROPODS", new String[] { "bane_of_arthropods", "art_damage", "bane", "DAMAGE_ARTHROPODS" });
	
	/**
	 * Item cannot be removed.
	 */
	public static final EnchantmentAdapter BINDING_CURSE = new EnchantmentAdapter("BINDING_CURSE", new String[] { "binding_curse", "binding" });
	
	/**
	 * Provides protection against explosive damage.
	 */
	public static final EnchantmentAdapter BLAST_PROTECTION = new EnchantmentAdapter("BLAST_PROTECTION", new String[] { "blast_protection", "blast_protect", "blast_prot", "explosions_protection", "explosions_protect", "explosions_prot", "explosions", "explode", "expl", "PROTECTION_EXPLOSIONS" });
	
	/**
	 * Reduces armor effectiveness against maces.
	 */
	public static final EnchantmentAdapter BREACH = new EnchantmentAdapter("BREACH", new String[] { "breach" });
	
	/**
	 * Strikes lightning when a mob is hit with a trident if conditions are stormy.
	 */
	public static final EnchantmentAdapter CHANNELING = new EnchantmentAdapter("CHANNELING", new String[] { "channeling" });
	
	/**
	 * Increases fall damage of maces.
	 */
	public static final EnchantmentAdapter DENSITY = new EnchantmentAdapter("DENSITY", new String[] { "density" });
	
	/**
	 * Increases walking speed while in water.
	 */
	public static final EnchantmentAdapter DEPTH_STRIDER = new EnchantmentAdapter("DEPTH_STRIDER", new String[] { "depth_strider" });
	
	/**
	 * Increases the rate at which you mine/dig.
	 */
	public static final EnchantmentAdapter EFFICIENCY = new EnchantmentAdapter("EFFICIENCY", new String[] { "efficiency", "eff", "haste", "dig", "dig_speed" });
	
	/**
	 * Provides protection against fall damage.
	 */
	public static final EnchantmentAdapter FEATHER_FALLING = new EnchantmentAdapter("FEATHER_FALLING", new String[] { "feather_falling", "feather", "feather_fall", "fall", "fall_damage", "protection_fall" });
	
	/**
	 * When attacking a target, has a chance to set them on fire.
	 */
	public static final EnchantmentAdapter FIRE_ASPECT = new EnchantmentAdapter("FIRE_ASPECT", new String[] { "fire_aspect", "fire" });
	
	/**
	 * Provides protection against fire damage.
	 */
	public static final EnchantmentAdapter FIRE_PROTECTION = new EnchantmentAdapter("FIRE_PROTECTION", new String[] { "fire_protection", "fire_protect", "fire_prot", "burn_protection", "burn_protect", "burn_prot", "burn", "protection_fire" });
	
	/**
	 * Sets entities on fire when hit by arrows shot from a bow.
	 */
	public static final EnchantmentAdapter FLAME = new EnchantmentAdapter("FLAME", new String[] { "flame", "arrow_fire" });
	
	/**
	 * Provides a chance of gaining extra loot when destroying blocks.
	 */
	public static final EnchantmentAdapter FORTUNE = new EnchantmentAdapter("FORTUNE", new String[] { "fortune", "loot_bonus_blocks" });
	
	/**
	 * Freezes any still water adjacent to ice / frost which player is walking on.
	 */
	public static final EnchantmentAdapter FROST_WALKER = new EnchantmentAdapter("FROST_WALKER", new String[] { "frost_walker", "frost" });
	
	/**
	 * Deals more damage to mobs that live in the ocean.
	 */
	public static final EnchantmentAdapter IMPALING = new EnchantmentAdapter("IMPALING", new String[] { "impaling" });
	
	/**
	 * Provides infinite arrows when shooting a bow.
	 */
	public static final EnchantmentAdapter INFINITY = new EnchantmentAdapter("INFINITY", new String[] { "infinity", "inf", "arrow_infinite" });
	
	/**
	 * All damage to other targets will knock them back when hit.
	 */
	public static final EnchantmentAdapter KNOCKBACK = new EnchantmentAdapter("KNOCKBACK", new String[] { "knockback", "knock", "kback" });
	
	/**
	 * Provides a chance of gaining extra loot when killing monsters.
	 */
	public static final EnchantmentAdapter LOOTING = new EnchantmentAdapter("LOOTING", new String[] { "looting", "loot_bonus_mobs" });
	
	/**
	 * Causes a thrown trident to return to the player who threw it.
	 */
	public static final EnchantmentAdapter LOYALTY = new EnchantmentAdapter("LOYALTY", new String[] { "loyalty", "loyal" });
	
	/**
	 * Decreases odds of catching worthless junk.
	 */
	public static final EnchantmentAdapter LUCK_OF_THE_SEA = new EnchantmentAdapter("LUCK_OF_THE_SEA", new String[] { "luck_of_the_sea", "luck_of_sea", "luck_sea", "sea_luck", "luck" });
	
	/**
	 * Increases rate of fish biting your hook.
	 */
	public static final EnchantmentAdapter LURE = new EnchantmentAdapter("LURE", new String[] { "lure" });
	
	/**
	 * Allows mending the item using experience orbs.
	 */
	public static final EnchantmentAdapter MENDING = new EnchantmentAdapter("MENDING", new String[] { "mending", "mend" });
	
	/**
	 * Shoot multiple arrows from crossbows.
	 */
	public static final EnchantmentAdapter MULTISHOT = new EnchantmentAdapter("MULTISHOT", new String[] { "multishot", "mshot" });
	
	/**
	 * Crossbow projectiles pierce entities.
	 */
	public static final EnchantmentAdapter PIERCING = new EnchantmentAdapter("PIERCING", new String[] { "piercing" });
	
	/**
	 * Provides extra damage when shooting arrows from bows.
	 */
	public static final EnchantmentAdapter POWER = new EnchantmentAdapter("POWER", new String[] { "power", "pow", "arrow_damage" });
	
	/**
	 * Provides protection against projectile damage.
	 */
	public static final EnchantmentAdapter PROJECTILE_PROTECTION = new EnchantmentAdapter("PROJECTILE_PROTECTION", new String[] { "projectile_protection", "projectile_protect", "projectile_prot", "proj_protection", "proj_protect", "proj_prot", "projectiles", "proj", "protection_projectile" });
	
	/**
	 * Provides protection against environmental damage.
	 */
	public static final EnchantmentAdapter PROTECTION = new EnchantmentAdapter("PROTECTION", new String[] { "protection", "protect", "prot", "protection_environmental" });
	
	/**
	 * Provides a knockback when an entity is hit by an arrow from a bow.
	 */
	public static final EnchantmentAdapter PUNCH = new EnchantmentAdapter("PUNCH", new String[] { "punch", "arrow_knockback" });
	
	/**
	 * Charges crossbows quickly.
	 */
	public static final EnchantmentAdapter QUICK_CHARGE = new EnchantmentAdapter("QUICK_CHARGE", new String[] { "quick_charge", "quick", "charge" });
	
	/**
	 * Decreases the rate of air loss whilst underwater.
	 */
	public static final EnchantmentAdapter RESPIRATION = new EnchantmentAdapter("RESPIRATION", new String[] { "respiration", "resp", "o2", "oxygen" });
	
	/**
	 * When it is rainy, launches the player in the direction their trident is thrown.
	 */
	public static final EnchantmentAdapter RIPTIDE = new EnchantmentAdapter("RIPTIDE", new String[] { "riptide", "riptiding" });
	
	/**
	 * Increases damage against all targets.
	 */
	public static final EnchantmentAdapter SHARPNESS = new EnchantmentAdapter("SHARPNESS", new String[] { "sharpness", "sharp", "damage_all" });
	
	/**
	 * Allows blocks to drop themselves instead of fragments (for example, stone instead of cobblestone).
	 */
	public static final EnchantmentAdapter SILK_TOUCH = new EnchantmentAdapter("SILK_TOUCH", new String[] { "silk_touch", "silk", "touch" });
	
	/**
	 * Increases damage against undead targets.
	 */
	public static final EnchantmentAdapter SMITE = new EnchantmentAdapter("SMITE", new String[] { "smite", "undead", "damage_undead" });
	
	/**
	 * Walk quicker on soul blocks.
	 */
	public static final EnchantmentAdapter SOUL_SPEED = new EnchantmentAdapter("SOUL_SPEED", new String[] { "soul_speed", "soul", "soul_sp" });
	
	/**
	 * Increases damage against targets when using a sweep attack.
	 */
	public static final EnchantmentAdapter SWEEPING_EDGE = new EnchantmentAdapter("SWEEPING_EDGE", new String[] { "sweeping_edge", "sweeping" });
	
	/**
	 * Walk quicker while sneaking.
	 */
	public static final EnchantmentAdapter SWIFT_SNEAK = new EnchantmentAdapter("SWIFT_SNEAK", new String[] { "swift_sneak", "swift", "sneak" });
	
	/**
	 * Damages the attacker.
	 */
	public static final EnchantmentAdapter THORNS = new EnchantmentAdapter("THORNS", new String[] { "thorns" });
	
	/**
	 * Decreases the rate at which a tool looses durability.
	 */
	public static final EnchantmentAdapter UNBREAKING = new EnchantmentAdapter("UNBREAKING", new String[] { "unbreaking", "durability" });
	
	/**
	 * Item disappears instead of dropping.
	 */
	public static final EnchantmentAdapter VANISHING_CURSE = new EnchantmentAdapter("VANISHING_CURSE", new String[] { "vanishing_curse", "vanishing", "vanish_curse" });
	
	/**
	 * Emits wind burst upon hitting enemy.
	 */
	public static final EnchantmentAdapter WIND_BURST = new EnchantmentAdapter("WIND_BURST", new String[] { "wind_burst", "wind" });
	private static final EnchantmentAdapter[] VALUES = new EnchantmentAdapter[] { AQUA_AFFINITY, BANE_OF_ARTHROPODS, BINDING_CURSE, BLAST_PROTECTION, BREACH, CHANNELING, DENSITY,DEPTH_STRIDER, EFFICIENCY, FEATHER_FALLING, FIRE_ASPECT, FIRE_PROTECTION, FLAME, FORTUNE, FROST_WALKER, IMPALING, INFINITY, KNOCKBACK, LOOTING, LOYALTY, LUCK_OF_THE_SEA, LURE, MENDING, MULTISHOT, PIERCING, POWER, PROJECTILE_PROTECTION, PROTECTION, PUNCH, QUICK_CHARGE, RESPIRATION, RIPTIDE, SHARPNESS, SILK_TOUCH, SMITE, SOUL_SPEED, SWEEPING_EDGE, SWIFT_SNEAK, THORNS, UNBREAKING, VANISHING_CURSE, WIND_BURST };
	private String name;
	private String[] aliases;
	
	private EnchantmentAdapter(String name, String[] aliases) {
		this.name = name;
		this.aliases = aliases;
	}
	
	/**
	 * Gets the enchantment adapted for Bukkit environments.
	 * 
	 * <p>If the current version does not support this enchantment,
	 * the default value of {@link #UNBREAKING} will be returned.</p>
	 * 
	 * @return Bukkit-adapted enchantment
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	@SuppressWarnings("deprecation")
	public Enchantment bukkitValue() {
		if (Environment.isBukkit()) {
			Enchantment enchantment = Enchantment.getByName(aliases[aliases.length - 1].toUpperCase());
			return enchantment == null ? Enchantment.UNBREAKING : enchantment;
		} else throw new UnsupportedOperationException("Unable to adapt enchantment to a Bukkit's Enchantment on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the enchantment adapted for Sponge environments.
	 * 
	 * <p>If the current version does not support this enchantment,
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
		else throw new UnsupportedOperationException("Unable to adapt enchantment to a Sponge's EnchantmentType on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Equivalent of {@link Enum#name()}.
	 * 
	 * @return Constant's name
	 */
	public String name() {
		return name;
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
	 * Gets this enchantment's aliases.
	 * 
	 * @return Enchantment's aliases
	 */
	public String[] getAliases() {
		return aliases;
	}
	
	/**
	 * Equivalent of <code>Enum#valueOf(String)</code>,
	 * with the only difference that instead of throwing
	 * {@link IllegalArgumentException} <code>null</code>
	 * is returned if the constant's name is invalid.
	 * 
	 * <p>This method recognizes both Bukkit's and Sponge's IDs.</p>
	 * 
	 * @param name Constant's name
	 * @return Enum constant
	 */
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the constant's name is invalid")
	public static EnchantmentAdapter valueOf(String name) {
		for (EnchantmentAdapter enchantment : VALUES) {
			String[] aliases = enchantment.getAliases();
			
			for (int i = 0; i < aliases.length; i++) {
				if (aliases[i].equalsIgnoreCase(name) || lookupWithoutUnderscore(aliases[i], name))
					return enchantment;
			}
		} return null;
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static EnchantmentAdapter[] values() {
		return VALUES;
	}
	
	/**
	 * Gets the enchantments' Vanilla names.
	 * 
	 * @return Enchantments' Vanilla names
	 */
	public static List<String> getVanillaNames() {
		List<String> list = new ArrayList<>();
		
		for (EnchantmentAdapter enchantment : values())
			list.add(enchantment.getAliases()[0]);
		return list;
	}
	
	private static boolean lookupWithoutUnderscore(String alias, String stringToCheck) {
		return alias.contains("_") && alias.replace("_", "").equalsIgnoreCase(stringToCheck);
	}
	
}

