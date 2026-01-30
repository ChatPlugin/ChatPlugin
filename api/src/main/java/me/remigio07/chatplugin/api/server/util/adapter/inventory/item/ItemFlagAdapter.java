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

import org.bukkit.inventory.ItemFlag;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) item flag adapter.
 */
public class ItemFlagAdapter extends PseudoEnum<ItemFlagAdapter> {
	
	/**
	 * Hides enchantments applied to an item.
	 * 
	 * <p><strong>Component:</strong> <a href="https://minecraft.wiki/w/Data_component_format#enchantments">enchantments</a> ("Sharpness III")</p>
	 */
	public static final ItemFlagAdapter HIDE_ENCHANTMENTS = new ItemFlagAdapter("HIDE_ENCHANTMENTS", "HIDE_ENCHANTS", "HIDE_ENCHANTMENTS", "enchantments");
	
	/**
	 * Hides attributes applied to an item.
	 * 
	 * <p><strong>Component:</strong> <a href="https://minecraft.wiki/w/Data_component_format#attribute_modifiers">attribute_modifiers</a> (["", "When on Head:", "+2 Armor"])</p>
	 */
	public static final ItemFlagAdapter HIDE_ATTRIBUTES = new ItemFlagAdapter("HIDE_ATTRIBUTES", "HIDE_ATTRIBUTES", "HIDE_ATTRIBUTES", "attribute_modifiers");
	
	/**
	 * Hides the "Unbreakable" state of an item.
	 * 
	 * <p><strong>Component:</strong> <a href="https://minecraft.wiki/w/Data_component_format#unbreakable">unbreakable</a> ("Unbreakable")</p>
	 */
	public static final ItemFlagAdapter HIDE_UNBREAKABLE = new ItemFlagAdapter("HIDE_UNBREAKABLE", "HIDE_UNBREAKABLE", "HIDE_UNBREAKABLE", "unbreakable");
	
	/**
	 * Hides what the item can destroy.
	 * 
	 * <p><strong>Component:</strong> <a href="https://minecraft.wiki/w/Data_component_format#can_break">can_break</a> (["", "Can break:", "Stone"])</p>
	 */
	public static final ItemFlagAdapter HIDE_CAN_DESTROY = new ItemFlagAdapter("HIDE_CAN_DESTROY", "HIDE_DESTROYS", "HIDE_CAN_DESTROY", "can_break");
	
	/**
	 * Hides what the item can be placed on.
	 * 
	 * <p><strong>Component:</strong> <a href="https://minecraft.wiki/w/Data_component_format#can_place_on">can_place_on</a> (["", "Can be placed on:", "Stone"])</p>
	 */
	public static final ItemFlagAdapter HIDE_CAN_BE_PLACED_ON = new ItemFlagAdapter("HIDE_CAN_BE_PLACED_ON", "HIDE_PLACED_ON", "HIDE_CAN_PLACE", "can_place_on");
	
	/**
	 * Hides an item's potion effects, book and firework information, map
	 * tooltips, patterns of banners, enchantments of enchanted books, etc.
	 * 
	 * <p>The following components are hidden:
	 * 	<ul>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#banner_patterns">banner_patterns</a>: patterns applied to a banner or shield ("Red Inverted Chevron")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#bees">bees</a>: amount of bees in a beehive or bee nest ("Bees: 2 / 3")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#block_entity_data">block_entity_data</a>: only used for spawners (["Interact with Spawn Egg:", " Sets Mob Type"])</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#block_state">block_state</a>: only used for honey level of beehives and bee nests ("Honey: 3 / 5")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#bundle_contents">bundle_contents</a>: items contained in a bundle's slots ("Can hold a mixed stack of items")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#charged_projectiles">charged_projectiles</a>: projectiles loaded into a crossbow ("Projectile: 2 x [Arrow]")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#container">container</a>: items contained in a container's slots ("Apple x1")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#container_loot">container_loot</a>: items contained in a loot table-generated container ("???????")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#firework_explosion">firework_explosion</a>: explosion effect stored by a firework star (["Star-shaped", "Blue"])</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#fireworks">fireworks</a>: explosion effects and flight duration of a firework ("Flight Duration: 2")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#instrument">instrument</a>: only used for goat horns ("Yearn")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#intangible_projectile">intangible_projectile</a>: projectiles that cannot be picked up ("Intangible")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#jukebox_playable">jukebox_playable</a>: artist and title of a song ("C418 - stal")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#map_id">map_id</a>: ID and info of a filled map (["Id #0", "Scaling at 1:1", "(Level 0/4)"])</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#ominous_bottle_amplifier">ominous_bottle_amplifier</a>: amplifier of the Bad Omen effect ("Bad Omen II (01:40:00)")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#painting/variant">painting/variant</a>: title, author and size of a painting (["Orb", "Kristoffer Zetterstrand", "4x4"])</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#pot_decorations">pot_decorations</a>: list of sherds of a pot (["", "Skull Pottery Sherd", "Brick", "Brick", "Brick"])</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#potion_contents">potion_contents</a>: list of potion effects (["Speed (03:00)", "", "When Applied:", "+20% Speed"])</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#profile">profile</a>: shown if either the UUID or name of a player head's profile is specified ("Dynamic")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#stored_enchantments">stored_enchantments</a>: enchantments stored by an enchanted book ("Sharpness III")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#suspicious_stew_effects">suspicious_stew_effects</a>: list of potion effects given upon consumption ("Saturation")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#tropical_fish/pattern">tropical_fish/pattern</a>: pattern <em>and color</em> of a tropical fish (["<em>Sunstreak</em>", "<em>White, Red</em>"])</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#written_book_content">written_book_content</a>: author and generation of a written book (["by Keresael", "Original"])</li>
	 * 	</ul>
	 * 
	 * <p>The following (remaining) components are not hidden:
	 * 	<ul>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#damage">damage</a>: amount of uses of an item, shown as durability ("Durability: 31 / 32")</li>
	 * 		<li><a href="https://minecraft.wiki/w/Data_component_format#lore">lore</a>: additional lines (max: 256) displayed in the tooltip (["<em>This is an</em>", "<em>example lore.</em>"])</li>
	 * 		<li>type of fragment: only used for the disc fragment (5) ("Music Disc - 5")</li>
	 * 		<li>smithing templates: description of a smithing template (["Smithing Template", "", "Applies to:", " Armor"/" Diamond Equipment", "Ingredients:", " Ingots &amp; Crystals"/" Netherite Ingot"])</li>
	 * 	</ul>
	 * 
	 * <p>The last two entries are not actual components and their tooltips are applied without any kind of check performed by Minecraft, therefore it is impossible to hide them.</p>
	 */
	public static final ItemFlagAdapter HIDE_MISCELLANEOUS = new ItemFlagAdapter(
			new String[] { VersionUtils.getVersion().isAtLeast(Version.V1_20_5) ? "HIDE_ADDITIONAL_TOOLTIP" : "HIDE_POTION_EFFECTS", "HIDE_MISCELLANEOUS", VersionUtils.getVersion().isAtLeast(Version.V1_21_5) ? "tooltip_display" : "hide_additional_tooltip" },
			"banner_patterns", "bees", "block_entity_data", "block_state", "bundle_contents", "charged_projectiles", "container", "container_loot", "firework_explosion", "fireworks", "instrument", "intangible_projectile",
			"jukebox_playable", "map_id", "ominous_bottle_amplifier", "painting/variant", "pot_decorations", "potion_contents", "profile", "stored_enchantments", "suspicious_stew_effects", "tropical_fish/pattern", "written_book_content"
			);
	
	/**
	 * Hides a leather armor's color information.
	 * 
	 * <p><strong>Component:</strong> <a href="https://minecraft.wiki/w/Data_component_format#dyed_color">dyed_color</a> ("Color: #FFFFFF")
	 * <br><strong>Minimum version:</strong> {@linkplain Version#V1_16_2 1.16.2}</p>
	 */
	public static final ItemFlagAdapter HIDE_DYE = new ItemFlagAdapter(Version.V1_16_2, "HIDE_DYE", "HIDE_MISCELLANEOUS", "dyed_color");
	
	/**
	 * Hides trim upgrades applied to an armor.
	 * 
	 * <p><strong>Component:</strong> <a href="https://minecraft.wiki/w/Data_component_format#trim">trim</a> (["Upgrade:", " Host Armor Trim", " Emerald Material"])
	 * <br><strong>Minimum version:</strong> {@linkplain Version#V1_19_4 1.19.4}</p>
	 */
	public static final ItemFlagAdapter HIDE_ARMOR_TRIM = new ItemFlagAdapter(Version.V1_19_4, "HIDE_ARMOR_TRIM", "HIDE_MISCELLANEOUS", "trim");
	private static final ItemFlagAdapter[] VALUES = new ItemFlagAdapter[] { HIDE_ENCHANTMENTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_CAN_DESTROY, HIDE_CAN_BE_PLACED_ON, HIDE_MISCELLANEOUS, HIDE_DYE, HIDE_ARMOR_TRIM };
	private static int ordinal = 0;
	private String[] ids, components;
	private Version minimumVersion;
	
	// first 5 item flags
	private ItemFlagAdapter(String name, String... ids) {
		this(name, ids, new String[] { ids[2] }, null);
	}
	
	// ad hoc for HIDE_MISCELLANEOUS
	private ItemFlagAdapter(String[] ids, String... components) {
		this(ids[1], ids, components, null);
	}
	
	// last 2 item flags
	private ItemFlagAdapter(Version minimumVersion, String... ids) {
		this(ids[0], ids, new String[] { ids[2] }, minimumVersion);
	}
	
	private ItemFlagAdapter(String name, String[] ids, String[] components, Version minimumVersion) {
		super(name, ordinal++);
		this.ids = ids;
		this.components = components;
		this.minimumVersion = minimumVersion;
	}
	
	/**
	 * Gets the item flag adapted for Bukkit environments.
	 * 
	 * <p>If {@link VersionUtils#getVersion()} does not support this item flag,
	 * the default value of {@link #HIDE_ATTRIBUTES} will be returned.</p>
	 * 
	 * @return Bukkit-adapted item flag
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public ItemFlag bukkitValue() {
		if (Environment.isBukkit())
			try {
				return ItemFlag.valueOf(ids[0]);
			} catch (IllegalArgumentException e) {
				return ItemFlag.HIDE_ATTRIBUTES;
			}
		throw new UnsupportedOperationException("Unable to adapt item flag to a Bukkit's ItemFlag on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the item flag adapted for Sponge environments.
	 * 
	 * <p>If {@link VersionUtils#getVersion()} does not support this item flag,
	 * the default value of {@link #HIDE_ATTRIBUTES} will be returned.</p>
	 * 
	 * @return Sponge-adapted item flag
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	@SuppressWarnings("unchecked")
	public Key<Value<Boolean>> spongeValue() {
		if (Environment.isSponge())
			try {
				return (Key<Value<Boolean>>) Keys.class.getField(ids[1]).get(null);
			} catch (NullPointerException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
				return Keys.HIDE_ATTRIBUTES;
			}
		throw new UnsupportedOperationException("Unable to adapt item flag to a Sponge's Key<Value<Boolean>> on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the item flag's bit modifier,
	 * used by ChatPlugin to apply item flags
	 * on older (1.14-1.16.1) Fabric versions.
	 * 
	 * @return Item flag's bit modifier
	 */
	public byte bitModifierValue() {
		return (byte) (1 << ordinal());
	}
	
	/**
	 * Gets the item flag adapted for old Fabric environments.
	 * 
	 * <p>This method returns an {@link Object} but it is an instance
	 * of <code>net.minecraft.item.ItemStack.TooltipSection</code>.</p>
	 * 
	 * <p>If {@link VersionUtils#getVersion()} does not support this item flag,
	 * the default value of {@link #HIDE_ATTRIBUTES} will be returned.</p>
	 * 
	 * <p><strong>Versions:</strong> {@linkplain Version#V1_16_2 1.16.2}-{@linkplain Version#V1_20_4 1.20.4}</p>
	 * 
	 * @return Old Fabric-adapted item flag
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 * or run on an incompatible version
	 */
	public Object oldFabricValue() {
		if (Environment.isFabric())
			try {
				return this == HIDE_ARMOR_TRIM && VersionUtils.getVersion().isOlderThan(Version.V1_19_4) ? HIDE_ATTRIBUTES : Class.forName("net.minecraft.class_1799$class_5422").getEnumConstants()[ordinal()];
			} catch (ClassNotFoundException cnfe) {
				throw new UnsupportedOperationException("Unable to call this 1.16.2-1.20.4 method on a " + VersionUtils.getVersion().getName() + " server");
			}
		throw new UnsupportedOperationException("Unable to adapt item flag to an old Fabric's ItemStack.TooltipSection on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the item flag adapted for Fabric environments.
	 * 
	 * <p>Will return {@link DataComponentTypes#TOOLTIP_DISPLAY}
	 * if <code>this == {@link #HIDE_MISCELLANEOUS}</code> and
	 * the version is at least {@linkplain Version#V1_21_5 1.21.5},
	 * indicating that multiple component types will be hidden.</p>
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_20_5 1.20.5}</p>
	 * 
	 * @return Fabric-adapted item flag
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 * or run on an incompatible version
	 */
	public ComponentType<?> fabricValue() {
		if (Environment.isFabric()) {
			if (VersionUtils.getVersion().isAtLeast(Version.V1_20_5)) {
				if (VersionUtils.getVersion().isAtLeast(Version.V1_21_2))
					return Registries.DATA_COMPONENT_TYPE.get(Identifier.ofVanilla(ids[2]));
				try {
					return (ComponentType<?>) Registry.class.getMethod("method_10223", Identifier.class).invoke(Registries.DATA_COMPONENT_TYPE, Identifier.tryParse("minecraft", ids[2]));
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
					return null;
				}
			} throw new UnsupportedOperationException("Unable to call this 1.20.5+ method on a " + VersionUtils.getVersion().getName() + " server");
		} throw new UnsupportedOperationException("Unable to adapt item flag to a Fabric's ComponentType<?> on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this item flag's IDs.
	 * 
	 * @return Item flag's IDs
	 */
	public String[] getIDs() {
		return ids;
	}
	
	/**
	 * Gets the component(s) hidden by this item flag.
	 * 
	 * <p>Will return a 1-string array unless
	 * <code>this == {@link #HIDE_MISCELLANEOUS}</code>.</p>
	 * 
	 * @return Item flag's component(s)
	 */
	public String[] getComponents() {
		return components;
	}
	
	/**
	 * Gets the minimum supported version for this item flag.
	 * 
	 * <p>Will return <code>null</code> if all versions
	 * supported by ChatPlugin support this item flag.</p>
	 * 
	 * @return Item flag's minimum version
	 */
	public @Nullable(why = "Null if all versions support this item flag") Version getMinimumVersion() {
		return minimumVersion;
	}
	
	/**
	 * Checks if this item flag is supported on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this item flag is supported
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
	public static ItemFlagAdapter valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static ItemFlagAdapter[] values() {
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
	public static ItemFlagAdapter value(String name) {
		name = name.toUpperCase();
		
		for (ItemFlagAdapter value : VALUES) {
			String[] ids;
			
			if (value.name().equals(name) || (ids = value.getIDs())[0].equals(name) || ids[1].equals(name))
				return value;
		} return null;
	}
	
}
