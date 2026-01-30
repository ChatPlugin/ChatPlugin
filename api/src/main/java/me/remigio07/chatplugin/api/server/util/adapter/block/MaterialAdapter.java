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

package me.remigio07.chatplugin.api.server.util.adapter.block;

import java.lang.reflect.Method;

import org.bukkit.Material;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) material adapter.
 */
public class MaterialAdapter implements Cloneable {
	
	/**
	 * Material adapter with ID "AIR" or "NONE"
	 * for pre-1.11 Sponge versions (v6.0.0).
	 */
	public static final MaterialAdapter AIR = new MaterialAdapter();
	private Object material;
	private String id;
	
	private MaterialAdapter() {
		material = Environment.isBukkit()
				? Material.AIR
				: Environment.isSponge()
				? VersionUtils.getVersion().isAtLeast(Version.V1_11)
						? ItemTypes.AIR
						: ItemTypes.NONE
				: Environment.isFabric() // here we explicitly check if we are on Fabric (instead of a simple else) so that if the class is initialized by the proxy environment it doesn't crash
				? Items.AIR
				: null;
		id = "minecraft:air";
	}
	
	/**
	 * Constructs a material adapter that accepts one of the following specified as <code>id</code>:
	 * 	<ul>
	 * 		<li>Vanilla-compliant IDs ("namespace:id_example") for all environments</li>
	 * 		<li>{@link org.bukkit.Material}-compatible IDs for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.item.ItemTypes}-compatible IDs for Sponge environments</li>
	 * 		<li>{@link net.minecraft.item.Items}-compatible IDs for Fabric environments</li>
	 * 	</ul>
	 * 
	 * @param id Material's ID
	 * @throws IllegalArgumentException If the specified material's ID is invalid
	 */
	public MaterialAdapter(String id) {
		switch (id) {
		case "AIR":
		case "air":
		case "minecraft:air":
			break;
		case "NONE":
			if (!Environment.isSponge() || VersionUtils.getVersion().isAtLeast(Version.V1_11))
				throw new IllegalArgumentException("Unknown material ID: " + id);
			break;
		default:
			try {
				if (Environment.isBukkit()) {
					if (id.equals(id.toUpperCase()))
						material = Material.valueOf(id);
					if (material == null) {
						if (id.startsWith("minecraft:"))
							id = id.substring(10);
						if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
							Class<?> Item = Class.forName("net.minecraft.server." + VersionUtils.getNMSVersion() + ".Item");
							material = Material.class.getMethod("getMaterial", int.class).invoke(null, Item.getMethod("getId", Item).invoke(null, Item.getMethod(VersionUtils.getVersion().isAtLeast(Version.V1_11) ? "b" : "d", String.class).invoke(null, id)));
							
							if (material == Material.AIR)
								throw new IllegalArgumentException();
						} else material = Material.valueOf(id.toUpperCase());
					}
				} else if (Environment.isSponge()) {
					if (id.equals(id.toUpperCase()))
						material = ItemTypes.class.getField(id).get(null);
					if (material == null)
						material = Sponge.getRegistry().getType(ItemType.class, id).orElse(null);
				} else {
					if (VersionUtils.getVersion().isAtLeast(Version.V1_19_3)) {
						if (VersionUtils.getVersion().isAtLeast(Version.V1_21_2)) {
							if (id.equals(id.toUpperCase()))
								material = Registries.ITEM.get(Identifier.ofVanilla(id.toLowerCase()));
							if (material == null)
								material = Registries.ITEM.get(id.contains(":") ? Identifier.of(id) : Identifier.ofVanilla(id));
						} else {
							Method get = Registry.class.getMethod("method_10223", Identifier.class);
							
							if (id.equals(id.toUpperCase()))
								material = get.invoke(Registries.ITEM, Identifier.tryParse("minecraft", id.toLowerCase()));
							if (material == null)
								material = get.invoke(Registries.ITEM, id.contains(":") ? Identifier.tryParse(id) : Identifier.tryParse("minecraft", id));
						}
					} else {
						Method get = Registry.class.getMethod("method_10223", Identifier.class);
						Object ITEM = Registry.class.getField("field_11142").get(null);
						
						if (id.equals(id.toUpperCase()))
							material = get.invoke(ITEM, Identifier.tryParse("minecraft:" + id.toLowerCase()));
						if (material == null)
							material = get.invoke(ITEM, id.contains(":") ? Identifier.tryParse(id) : Identifier.tryParse("minecraft:" + id));
					}
				} material.toString(); // validation
				
				this.id = id;
				return;
			} catch (Exception e) {
				throw new IllegalArgumentException("Unknown material ID: " + id);
			}
		} material = AIR.material;
		this.id = id;
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
	public Material bukkitValue() {
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
	public ItemType spongeValue() {
		if (Environment.isSponge())
			return (ItemType) material;
		throw new UnsupportedOperationException("Unable to adapt material to a Sponge's ItemType on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the material adapted for Fabric environments.
	 * 
	 * @return Fabric-adapted material
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public Item fabricValue() {
		if (Environment.isFabric())
			return (Item) material;
		throw new UnsupportedOperationException("Unable to adapt material to a Fabric's Item on a " + Environment.getCurrent().getName() + " environment");
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
	 * Checks if another object is an instance of {@link MaterialAdapter}
	 * and if this material's value is equal to the other object's one.
	 * 
	 * @param obj Object to compare
	 * @return Whether the two objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof MaterialAdapter && ((MaterialAdapter) obj).material.equals(material);
	}
	
	/**
	 * Gets this material's hash code.
	 * 
	 * <p>Will return the hash code of this material's value.</p>
	 * 
	 * @return Material's hash code
	 */
	@Override
	public int hashCode() {
		return material.hashCode();
	}
	
}
