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

package me.remigio07.chatplugin.api.server.util.adapter.inventory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) inventory adapter.
 */
public class InventoryAdapter {
	
	private ItemStackAdapter[] items;
	private Object inventory;
	private int size;
	private String title;
	
	/**
	 * Constructs an item stack with the given title and rows.
	 * 
	 * @param rows Inventory's rows [1 - 6]
	 * @param title Inventory's title
	 * @throws IndexOutOfBoundsException If <code>rows &lt; 1 || rows &gt; 6</code>
	 */
	public InventoryAdapter(int rows, String title) {
		if (rows < 1 || rows > 6)
			throw new IndexOutOfBoundsException("Specified inventory's rows (" + rows + ") are invalid as they are not inside of range 1-6");
		size = rows * 9;
		this.title = title;
		inventory = Environment.isBukkit() ? Bukkit.createInventory(null, size, title) : Environment.isSponge() ? SpongeInventory.get(rows, title) : new SimpleInventory(size);
		items = new ItemStackAdapter[size];
	}
	
	/**
	 * Gets the inventory adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted inventory
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.inventory.Inventory bukkitValue() {
		if (Environment.isBukkit())
			return (org.bukkit.inventory.Inventory) inventory;
		throw new UnsupportedOperationException("Unable to adapt inventory to a Bukkit's Inventory on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the inventory adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted inventory
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public Inventory spongeValue() {
		if (Environment.isSponge())
			return (Inventory) inventory;
		throw new UnsupportedOperationException("Unable to adapt inventory to a Sponge's Inventory on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the inventory adapted for Fabric environments.
	 * 
	 * @return Fabric-adapted inventory
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public SimpleInventory fabricValue() {
		if (Environment.isFabric())
			return (SimpleInventory) inventory;
		throw new UnsupportedOperationException("Unable to adapt inventory to a Fabric's SimpleInventory on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Checks if this inventory contains an item at the specified position.
	 * 
	 * @param position Position to check
	 * @return Whether an item is present
	 */
	public boolean containsItem(int position) {
		try {
			items[position].toString();
			return true;
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}
	
	/**
	 * Gets the item at the specified position.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #containsItem(int)} at that position.</p>
	 * 
	 * @param position Position to check.
	 * @return Item at specified position
	 */
	@Nullable(why = "There may be no items at the specified position")
	public ItemStackAdapter getItem(int position) {
		return containsItem(position) ? items[position] : null;
	}
	
	/**
	 * Sets an item stack at the specified position.
	 * 
	 * <p>Will do nothing if <code>position</code> is outside of allowed range.</p>
	 * 
	 * <p>Specify an item of type {@link MaterialAdapter#AIR} if you want to remove the icon.</p>
	 * 
	 * @param itemStack Item stack to set
	 * @param position Position to set [0 - ({@link #getSize()} - 1)]
	 */
	public void setItem(@NotNull ItemStackAdapter itemStack, int position) {
		if (position < 0 || position > getSize() - 1)
			return;
		if (Environment.isBukkit())
			((org.bukkit.inventory.Inventory) inventory).setItem(position, itemStack.bukkitValue());
		else if (Environment.isSponge())
			spongeValue().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(position))).set(itemStack.spongeValue());
		else fabricValue().setStack(position, itemStack.fabricValue());
		
		items[position] = itemStack;
	}
	
	/**
	 * Clears this inventory.
	 */
	public void clear() {
		if (Environment.isBukkit())
			bukkitValue().clear();
		else if (Environment.isSponge())
			spongeValue().clear();
		else fabricValue().clear();
		
		items = new ItemStackAdapter[size];
	}
	
	/**
	 * Gets this inventory's size.
	 * 
	 * @return Inventory's size
	 */
	public int getSize() {
		return size;
	}
	
	/**
	 * Gets this inventory's title.
	 * 
	 * @return Inventory's title
	 */
	public String getTitle() { // TODO [for a future setTitle(String)]: can we update the inventory's title on Sponge by setting the InventoryTitle property?
		return title;
	}
	
	/**
	 * Gets the list of the loaded players who have this inventory open.
	 * 
	 * @return Inventory's viewers
	 */
	public List<ChatPluginServerPlayer> getViewers() {
		if (Environment.isFabric()) {
			SimpleInventory fabricValue = fabricValue();
			return ServerPlayerManager.getInstance().getPlayers().values().stream().filter(player -> {
				ScreenHandler inventory = player.toAdapter().fabricValue().currentScreenHandler;
				return inventory instanceof GenericContainerScreenHandler && ((GenericContainerScreenHandler) inventory).getInventory().equals(fabricValue);
				}).collect(Collectors.toList());
		} return (Environment.isBukkit() ? bukkitValue().getViewers().stream().map(HumanEntity::getUniqueId)
				: ((Container) inventory).getViewers().stream().map(Player::getUniqueId))
				.map(ServerPlayerManager.getInstance()::getPlayer)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
	
	private static class SpongeInventory {
		
		public static Inventory get(int rows, String title) {
			return Inventory
					.builder()
					.property(InventoryTitle.of(Utils.toSpongeComponent(title)))
					.property(InventoryDimension.of(9, rows))
					.build(SpongeBootstrapper.getInstance());
		}
		
	}
	
}
