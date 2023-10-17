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
	public static final EnchantmentAdapter BINDING_CURSE = new EnchantmentAdapter("BINDING_CURSE", new String[] { "binding", "BINDING_CURSE" });
	
	/**
	 * Provides protection against explosive damage.
	 */
	public static final EnchantmentAdapter BLAST_PROTECTION = new EnchantmentAdapter("BLAST_PROTECTION", new String[] { "blast_protection", "blast_protect", "blast_prot", "explosions_protection", "explosions_protect", "explosions_prot", "explosions", "explode", "expl", "PROTECTION_EXPLOSIONS" });
	
	/**
	 * Strikes lightning when a mob is hit with a trident if conditions are stormy.
	 */
	public static final EnchantmentAdapter CHANNELING = new EnchantmentAdapter("CHANNELING", new String[] { "CHANNELING" });
	
	/**
	 * Increases walking speed while in water.
	 */
	public static final EnchantmentAdapter DEPTH_STRIDER = new EnchantmentAdapter("DEPTH_STRIDER", new String[] { "DEPTH_STRIDER" });
	
	/**
	 * Increases the rate at which you mine/dig.
	 */
	public static final EnchantmentAdapter EFFICIENCY = new EnchantmentAdapter("EFFICIENCY", new String[] { "efficiency", "eff", "haste", "dig", "DIG_SPEED" });
	
	/**
	 * Provides protection against fall damage.
	 */
	public static final EnchantmentAdapter FEATHER_FALLING = new EnchantmentAdapter("FEATHER_FALLING", new String[] { "feather_falling", "feather", "feather_fall", "fall", "fall_damage", "PROTECTION_FALL" });
	
	/**
	 * When attacking a target, has a chance to set them on fire.
	 */
	public static final EnchantmentAdapter FIRE_ASPECT = new EnchantmentAdapter("FIRE_ASPECT", new String[] { "fire", "FIRE_ASPECT" });
	
	/**
	 * Provides protection against fire damage.
	 */
	public static final EnchantmentAdapter FIRE_PROTECTION = new EnchantmentAdapter("FIRE_PROTECTION", new String[] { "fire_protection", "fire_protect", "fire_prot", "burn_protection", "burn_protect", "burn_prot", "burn", "PROTECTION_FIRE" });
	
	/**
	 * Sets entities on fire when hit by arrows shot from a bow.
	 */
	public static final EnchantmentAdapter FLAME = new EnchantmentAdapter("FLAME", new String[] { "flame", "ARROW_FIRE" });
	
	/**
	 * Provides a chance of gaining extra loot when destroying blocks.
	 */
	public static final EnchantmentAdapter FORTUNE = new EnchantmentAdapter("FORTUNE", new String[] { "fortune", "LOOT_BONUS_BLOCKS" });
	
	/**
	 * Freezes any still water adjacent to ice / frost which player is walking on.
	 */
	public static final EnchantmentAdapter FROST_WALKER = new EnchantmentAdapter("FROST_WALKER", new String[] { "frost", "FROST_WALKER" });
	
	/**
	 * Deals more damage to mobs that live in the ocean.
	 */
	public static final EnchantmentAdapter IMPALING = new EnchantmentAdapter("IMPALING", new String[] { "IMPALING" });
	
	/**
	 * Provides infinite arrows when shooting a bow.
	 */
	public static final EnchantmentAdapter INFINITY = new EnchantmentAdapter("INFINITY", new String[] { "infinity", "inf", "ARROW_INFINITE" });
	
	/**
	 * All damage to other targets will knock them back when hit.
	 */
	public static final EnchantmentAdapter KNOCKBACK = new EnchantmentAdapter("KNOCKBACK", new String[] { "knock", "kback", "KNOCKBACK" });
	
	/**
	 * Provides a chance of gaining extra loot when killing monsters.
	 */
	public static final EnchantmentAdapter LOOTING = new EnchantmentAdapter("LOOTING", new String[] { "looting", "LOOT_BONUS_MOBS" });
	
	/**
	 * Causes a thrown trident to return to the player who threw it.
	 */
	public static final EnchantmentAdapter LOYALTY = new EnchantmentAdapter("LOYALTY", new String[] { "loyal", "LOYALTY" });
	
	/**
	 * Decreases odds of catching worthless junk.
	 */
	public static final EnchantmentAdapter LUCK_OF_THE_SEA = new EnchantmentAdapter("LUCK_OF_THE_SEA", new String[] { "luck_of_the_sea", "luck_of_sea", "luck_sea", "sea_luck", "LUCK" });
	
	/**
	 * Increases rate of fish biting your hook.
	 */
	public static final EnchantmentAdapter LURE = new EnchantmentAdapter("LURE", new String[] { "LURE" });
	
	/**
	 * Allows mending the item using experience orbs.
	 */
	public static final EnchantmentAdapter MENDING = new EnchantmentAdapter("MENDING", new String[] { "mend", "MENDING" });
	
	/**
	 * Shoot multiple arrows from crossbows.
	 */
	public static final EnchantmentAdapter MULTISHOT = new EnchantmentAdapter("MULTISHOT", new String[] { "mshot", "MULTISHOT" });
	
	/**
	 * Crossbow projectiles pierce entities.
	 */
	public static final EnchantmentAdapter PIERCING = new EnchantmentAdapter("PIERCING", new String[] { "PIERCING" });
	
	/**
	 * Provides extra damage when shooting arrows from bows.
	 */
	public static final EnchantmentAdapter POWER = new EnchantmentAdapter("POWER", new String[] { "power", "pow", "ARROW_DAMAGE" });
	
	/**
	 * Provides protection against projectile damage.
	 */
	public static final EnchantmentAdapter PROJECTILE_PROTECTION = new EnchantmentAdapter("PROJECTILE_PROTECTION", new String[] { "projectile_protection", "projectile_protect", "projectile_prot", "proj_protection", "proj_protect", "proj_prot", "projectiles", "proj", "PROTECTION_PROJECTILE" });
	
	/**
	 * Provides protection against environmental damage.
	 */
	public static final EnchantmentAdapter PROTECTION = new EnchantmentAdapter("PROTECTION", new String[] { "protection", "protect", "prot", "PROTECTION_ENVIRONMENTAL" });
	
	/**
	 * Provides a knockback when an entity is hit by an arrow from a bow.
	 */
	public static final EnchantmentAdapter PUNCH = new EnchantmentAdapter("PUNCH", new String[] { "punch", "ARROW_KNOCKBACK" });
	
	/**
	 * Charges crossbows quickly.
	 */
	public static final EnchantmentAdapter QUICK_CHARGE = new EnchantmentAdapter("QUICK_CHARGE", new String[] { "quick", "charge", "QUICK_CHARGE" });
	
	/**
	 * Decreases the rate of air loss whilst underwater.
	 */
	public static final EnchantmentAdapter RESPIRATION = new EnchantmentAdapter("RESPIRATION", new String[] { "respiration", "resp", "o2", "OXYGEN" });
	
	/**
	 * When it is rainy, launches the player in the direction their trident is thrown.
	 */
	public static final EnchantmentAdapter RIPTIDE = new EnchantmentAdapter("RIPTIDE", new String[] { "riptiding", "RIPTIDE" });
	
	/**
	 * Increases damage against all targets.
	 */
	public static final EnchantmentAdapter SHARPNESS = new EnchantmentAdapter("SHARPNESS", new String[] { "sharpness", "sharp", "DAMAGE_ALL" });
	
	/**
	 * Allows blocks to drop themselves instead of fragments (for example, stone instead of cobblestone).
	 */
	public static final EnchantmentAdapter SILK_TOUCH = new EnchantmentAdapter("SILK_TOUCH", new String[] { "silk", "touch", "SILK_TOUCH" });
	
	/**
	 * Increases damage against undead targets.
	 */
	public static final EnchantmentAdapter SMITE = new EnchantmentAdapter("SMITE", new String[] { "smite", "undead", "DAMAGE_UNDEAD" });
	
	/**
	 * Walk quicker on soul blocks.
	 */
	public static final EnchantmentAdapter SOUL_SPEED = new EnchantmentAdapter("SOUL_SPEED", new String[] { "soul", "soul_sp", "SOUL_SPEED" });
	
	/**
	 * Increases damage against targets when using a sweep attack.
	 */
	public static final EnchantmentAdapter SWEEPING_EDGE = new EnchantmentAdapter("SWEEPING_EDGE", new String[] { "sweeping_edge", "SWEEPING" });
	
	/**
	 * Walk quicker while sneaking.
	 */
	public static final EnchantmentAdapter SWIFT_SNEAK = new EnchantmentAdapter("SWIFT_SNEAK", new String[] { "swift", "sneak", "SWIFT_SNEAK" });
	
	/**
	 * Damages the attacker.
	 */
	public static final EnchantmentAdapter THORNS = new EnchantmentAdapter("THORNS", new String[] { "THORNS" });
	
	/**
	 * Decreases the rate at which a tool looses durability.
	 */
	public static final EnchantmentAdapter UNBREAKING = new EnchantmentAdapter("UNBREAKING", new String[] { "unbreaking", "DURABILITY" });
	
	/**
	 * Item disappears instead of dropping.
	 */
	public static final EnchantmentAdapter VANISHING_CURSE = new EnchantmentAdapter("VANISHING_CURSE", new String[] { "vanishing", "vanish_curse", "VANISHING_CURSE" });
	private static final EnchantmentAdapter[] VALUES = new EnchantmentAdapter[] { AQUA_AFFINITY, BANE_OF_ARTHROPODS, BINDING_CURSE, BLAST_PROTECTION, CHANNELING, DEPTH_STRIDER, EFFICIENCY, FEATHER_FALLING, FIRE_ASPECT, FIRE_PROTECTION, FLAME, FORTUNE, FROST_WALKER, IMPALING, INFINITY, KNOCKBACK, LOOTING, LOYALTY, LUCK_OF_THE_SEA, LURE, MENDING, MULTISHOT, PIERCING, POWER, PROJECTILE_PROTECTION, PROTECTION, PUNCH, QUICK_CHARGE, RESPIRATION, RIPTIDE, SHARPNESS, SILK_TOUCH, SMITE, SOUL_SPEED, SWEEPING_EDGE, SWIFT_SNEAK, THORNS, UNBREAKING, VANISHING_CURSE };
	private String name;
	private String[] aliases;
	
	private EnchantmentAdapter(String name, String[] aliases) {
		this.name = name;
		this.aliases = aliases;
	}
	
	/**
	 * Gets the enchantment adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted enchantment
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	@SuppressWarnings("deprecation")
	public Enchantment bukkitValue() {
		if (Environment.isBukkit())
			return Enchantment.getByName(aliases[aliases.length - 1].toUpperCase());
		else throw new UnsupportedOperationException("Unable to adapt enchantment to a Bukkit's Enchantment on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the enchantment adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted enchantment
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public EnchantmentType spongeValue() {
		if (Environment.isSponge())
			try {
				return (EnchantmentType) EnchantmentTypes.class.getField(name()).get(null);
			} catch (NullPointerException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
				return null;
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

