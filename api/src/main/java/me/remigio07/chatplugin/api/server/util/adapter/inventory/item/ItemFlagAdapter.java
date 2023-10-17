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

import org.bukkit.inventory.ItemFlag;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.value.mutable.Value;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) item flag adapter.
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 */
public class ItemFlagAdapter {
	
	/**
	 * Hides enchantments applied to an item.
	 */
	public static final ItemFlagAdapter HIDE_ENCHANTMENTS = new ItemFlagAdapter("HIDE_ENCHANTMENTS", new String[] { "HIDE_ENCHANTS", "HIDE_ENCHANTMENTS" });
	
	/**
	 * Hides attributes applied to an item.
	 */
	public static final ItemFlagAdapter HIDE_ATTRIBUTES = new ItemFlagAdapter("HIDE_ATTRIBUTES", new String[] { "HIDE_ATTRIBUTES", "HIDE_ATTRIBUTES" });
	
	/**
	 * Hides a leather armor's color information.
	 * 
	 * <p>Not available on Sponge yet: it is considered
	 * equal to {@link #HIDE_MISCELLANEOUS} if
	 * running on a Sponge environment.</p>
	 */
	public static final ItemFlagAdapter HIDE_DYE = new ItemFlagAdapter("HIDE_DYE", new String[] { "HIDE_DYE", "HIDE_MISCELLANEOUS" });
	
	/**
	 * Hides the "Unbreakable" state of an item.
	 */
	public static final ItemFlagAdapter HIDE_UNBREAKABLE = new ItemFlagAdapter("HIDE_UNBREAKABLE", new String[] { "HIDE_UNBREAKABLE", "HIDE_UNBREAKABLE" });
	
	/**
	 * Hides what the item can destroy.
	 */
	public static final ItemFlagAdapter HIDE_CAN_DESTROY = new ItemFlagAdapter("HIDE_CAN_DESTROY", new String[] { "HIDE_DESTROYS", "HIDE_CAN_DESTROY" });
	
	/**
	 * Hides what the item can be placed on.
	 */
	public static final ItemFlagAdapter HIDE_CAN_BE_PLACED_ON = new ItemFlagAdapter("HIDE_CAN_BE_PLACED_ON", new String[] { "HIDE_PLACED_ON", "HIDE_CAN_PLACE" });
	
	/**
	 * Hides an item's potion effects, book and firework information, map tooltips, patterns of banners and enchantments of enchanted books.
	 */
	public static final ItemFlagAdapter HIDE_MISCELLANEOUS = new ItemFlagAdapter("HIDE_MISCELLANEOUS", new String[] { "HIDE_POTION_EFFECTS", "HIDE_MISCELLANEOUS" });
	private static final ItemFlagAdapter[] VALUES = new ItemFlagAdapter[] { HIDE_ENCHANTMENTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_CAN_DESTROY, HIDE_CAN_BE_PLACED_ON, HIDE_MISCELLANEOUS };
	private String name;
	private String[] ids;
	
	private ItemFlagAdapter(String name, String[] ids) {
		this.name = name;
		this.ids = ids;
	}
	
	/**
	 * Gets the item flag adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted item flag
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public ItemFlag bukkitValue() {
		if (Environment.isBukkit())
			return ItemFlag.valueOf(ids[0]);
		else throw new UnsupportedOperationException("Unable to adapt item flag to a Bukkit's ItemFlag on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the item flag adapted for Sponge environments.
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
				return Keys.HIDE_ATTRIBUTES; // default
			}
		else throw new UnsupportedOperationException("Unable to adapt item flag to a Sponge's Key<Value<Boolean>> on a " + Environment.getCurrent().getName() + " environment");
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
	 * Gets this item flag's IDs.
	 * 
	 * <p>The first element in the array is the Bukkit-compatible
	 * ID, the second one is the Sponge-compatible ID.</p>
	 * 
	 * @return Item flag's IDs
	 */
	public String[] getIDs() {
		return ids;
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
	public static ItemFlagAdapter valueOf(String name) {
		switch (name) {
		case "HIDE_ENCHANTMENTS":
		case "HIDE_ENCHANTS":
			return HIDE_ENCHANTMENTS;
		case "HIDE_ATTRIBUTES":
			return HIDE_ATTRIBUTES;
		case "HIDE_DYE":
			return HIDE_DYE;
		case "HIDE_UNBREAKABLE":
			return HIDE_UNBREAKABLE;
		case "HIDE_CAN_DESTROY":
		case "HIDE_DESTROYS":
			return HIDE_CAN_DESTROY;
		case "HIDE_CAN_BE_PLACED_ON":
		case "HIDE_PLACED_ON":
			return HIDE_CAN_BE_PLACED_ON;
		case "HIDE_POTION_EFFECTS":
		case "HIDE_MISCELLANEOUS":
			return HIDE_MISCELLANEOUS;
		} return null;
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static ItemFlagAdapter[] values() {
		return VALUES;
	}
	
	/**
	 * Util method used to get item flags at the
	 * specified path inside of a configuration.
	 * 
	 * @param configuration Configuration containing the values
	 * @param path Path containing the values
	 * @return Values specified at given path
	 */
	public static ItemFlagAdapter[] valuesOf(Configuration configuration, String path) {
		List<ItemFlagAdapter> values = new ArrayList<>();
		
		for (String id : configuration.getStringList(path)) {
			ItemFlagAdapter flag = valueOf(id.toUpperCase());
			
			if (flag == null)
				LogManager.log("Invalid item flag specified at " + path + " in " + configuration.getFile().getName() + ": " + id + "; skipping.", 2);
			else values.add(flag);
		} return values.toArray(new ItemFlagAdapter[0]);
	}
	
}
