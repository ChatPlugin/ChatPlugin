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

package me.remigio07.chatplugin.api.server.util.adapter.block;

import org.bukkit.Material;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) material adapter.
 */
public class MaterialAdapter implements Cloneable {
	
	/**
	 * Material adapter with ID "AIR" or "NONE"
	 * for pre-1.11 Sponge versions (v6.0.0).
	 */
	public static final MaterialAdapter AIR = new MaterialAdapter(Environment.isBukkit() || VersionUtils.getVersion().isAtLeast(Version.V1_11) ? "AIR" : "NONE");
	private Object material;
	private String id;
	
	/**
	 * Constructs a material adapter that accepts a Bukkit's {@link org.bukkit.Material} or
	 * Sponge's {@link org.spongepowered.api.item.ItemTypes} compatible material ID as input.
	 * 
	 * @param id Material's ID
	 * @throws IllegalArgumentException If the specified material's ID is invalid
	 */
	public MaterialAdapter(String id) {
		try {
			String upperCase = id.toUpperCase();
			material = Environment.isBukkit() ? Material.getMaterial(upperCase) : (ItemType) ItemTypes.class.getField(upperCase).get(null);
			material.toString();
			this.id = upperCase;
		} catch (NullPointerException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
			throw new IllegalArgumentException("Unknown material ID: " + id);
		}
	}
	
	@Override
	public MaterialAdapter clone() {
		return new MaterialAdapter(id);
	}
	
	@Override
	public String toString() {
		return "MaterialAdapter{id=\"" + id + "\"}";
	}
	
	/**
	 * Gets the material adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted material
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.Material bukkitValue() {
		if (Environment.isBukkit())
			return (Material) material;
		throw new UnsupportedOperationException("Unable to adapt material to a Bukkit's Material on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the material adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted material
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.item.ItemType spongeValue() {
		if (Environment.isSponge())
			return (ItemType) material;
		throw new UnsupportedOperationException("Unable to adapt material to a Sponge's ItemType on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this material's ID.
	 * 
	 * @return Material's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Checks if another object is an instance of {@link MaterialAdapter} and if
	 * this material's {@link #getID()} value is equal to the other object's one.
	 * 
	 * @param obj Object to compare
	 * @return Whether the two objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof MaterialAdapter && ((MaterialAdapter) obj).getID().equals(id);
	}
	
	/**
	 * Gets this material's hash code.
	 * 
	 * <p>Will return {@link #getID()}'s hash code.</p>
	 * 
	 * @return Material's hash code
	 */
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
}
