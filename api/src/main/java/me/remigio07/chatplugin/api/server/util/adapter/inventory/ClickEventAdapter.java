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

package me.remigio07.chatplugin.api.server.util.adapter.inventory;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) click event adapter.
 */
public class ClickEventAdapter {
	
	private ClickTypeAdapter clickType;
	private ClickActionAdapter clickAction;
	private ItemStackAdapter cursor;
	private int slot, numberKey;
	
	/**
	 * Constructs a new click event adapter.
	 * 
	 * @param clickType Click's type
	 * @param clickAction Click's action
	 * @param cursor Item on the cursor
	 * @param slot Clicked slot, -1 if invalid
	 * @param numberKey Clicked number key [0 - 8], -1 if none
	 */
	public ClickEventAdapter(ClickTypeAdapter clickType, ClickActionAdapter clickAction, @Nullable(why = "Cursor may not be holding any items") ItemStackAdapter cursor, int slot, int numberKey) {
		this.clickType = clickType;
		this.clickAction = clickAction;
		this.cursor = cursor;
		this.slot = slot;
		this.numberKey = numberKey;
	}
	
	/**
	 * Gets the click's type.
	 * 
	 * @return Click's type
	 */
	public ClickTypeAdapter getClickType() {
		return clickType;
	}
	
	/**
	 * Gets the click's action.
	 * 
	 * @return Click's action
	 */
	public ClickActionAdapter getClickAction() {
		return clickAction;
	}
	
	/**
	 * Gets the item on the cursor.
	 * 
	 * <p>Will return <code>null</code> if the cursor is not holding any items.</p>
	 * 
	 * @return Item on the cursor
	 */
	@Nullable(why = "Cursor may not be holding any items")
	public ItemStackAdapter getCursor() {
		return cursor;
	}
	
	/**
	 * Gets the clicked slot.
	 * 
	 * @return Clicked slot, -1 if invalid
	 */
	public int getSlot() {
		return slot;
	}
	
	/**
	 * Gets the clicked number key.
	 * 
	 * @return Clicked number key [0 - 8], -1 if none
	 */
	public int getNumberKey() {
		return numberKey;
	}
	
	/**
	 * Environment indipendent (Bukkit and Sponge) click type adapter.
	 */
	public static class ClickTypeAdapter extends PseudoEnum<ClickTypeAdapter> {
		
		/**
		 * The left (or primary) mouse button.
		 */
		public static final ClickTypeAdapter LEFT = new ClickTypeAdapter("LEFT");
		
		/**
		 * Holding shift while pressing the left mouse button.
		 */
		public static final ClickTypeAdapter SHIFT_LEFT = new ClickTypeAdapter("SHIFT_LEFT");
		
		/**
		 * The right mouse button.
		 */
		public static final ClickTypeAdapter RIGHT = new ClickTypeAdapter("RIGHT");
		
		/**
		 * Holding shift while pressing the right mouse button.
		 */
		public static final ClickTypeAdapter SHIFT_RIGHT = new ClickTypeAdapter("SHIFT_RIGHT");
		
		/**
		 * Clicking the left mouse button on the grey area around the inventory.
		 * 
		 * <p><strong>Note:</strong> only used by Bukkit pre-1.9 servers.
		 * <br><strong>Maximum version:</strong> {@linkplain Version#V1_8_9 1.8.9}</p>
		 */
		public static final ClickTypeAdapter WINDOW_BORDER_LEFT = new ClickTypeAdapter("WINDOW_BORDER_LEFT");
		
		/**
		 * Clicking the right mouse button on the grey area around the inventory.
		 * 
		 * <p><strong>Note:</strong> only used by Bukkit pre-1.9 servers.
		 * <br><strong>Maximum version:</strong> {@linkplain Version#V1_8_9 1.8.9}</p>
		 */
		public static final ClickTypeAdapter WINDOW_BORDER_RIGHT = new ClickTypeAdapter("WINDOW_BORDER_RIGHT");
		
		/**
		 * The middle mouse button, or a "scrollwheel click".
		 */
		public static final ClickTypeAdapter MIDDLE = new ClickTypeAdapter("MIDDLE");
		
		/**
		 * One of the number keys 1-9, correspond to slots on the hotbar.
		 */
		public static final ClickTypeAdapter NUMBER_KEY = new ClickTypeAdapter("NUMBER_KEY");
		
		/**
		 * Pressing the left mouse button twice in quick succession.
		 */
		public static final ClickTypeAdapter DOUBLE_CLICK = new ClickTypeAdapter("DOUBLE_CLICK");
		
		/**
		 * The "Drop" key (defaults to Q).
		 */
		public static final ClickTypeAdapter DROP = new ClickTypeAdapter("DROP");
		
		/**
		 * Holding Ctrl while pressing the "Drop" key (defaults to Q).
		 */
		public static final ClickTypeAdapter CONTROL_DROP = new ClickTypeAdapter("CONTROL_DROP");
		
		/**
		 * Any action done with the Creative inventory open.
		 */
		public static final ClickTypeAdapter CREATIVE = new ClickTypeAdapter("CREATIVE");
		
		/**
		 * The "swap item with offhand" key (defaults to F).
		 * 
		 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
		 */
		public static final ClickTypeAdapter SWAP_OFFHAND = new ClickTypeAdapter("SWAP_OFFHAND");
		
		/**
		 * A type of inventory manipulation not yet recognized by the current environment.
		 * 
		 * <p><strong>Note:</strong> This is only for transitional purposes
		 * on a new Minecraft update, and should never be relied upon.</p>
		 */
		public static final ClickTypeAdapter UNKNOWN = new ClickTypeAdapter("UNKNOWN");
		private static final ClickTypeAdapter[] VALUES = new ClickTypeAdapter[] { LEFT, SHIFT_LEFT, RIGHT, SHIFT_RIGHT, WINDOW_BORDER_LEFT, WINDOW_BORDER_RIGHT, MIDDLE, NUMBER_KEY, DOUBLE_CLICK, DROP, CONTROL_DROP, CREATIVE, SWAP_OFFHAND, UNKNOWN };
		private static int ordinal = 0;
		
		private ClickTypeAdapter(String name) {
			super(name, ordinal++);
		}
		
		/**
		 * Gets the click type adapted for Bukkit environments.
		 * 
		 * @return Bukkit-adapted click type
		 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
		 */
		public ClickType bukkitValue() {
			if (Environment.isBukkit())
				return ClickType.valueOf(name);
			throw new UnsupportedOperationException("Unable to adapt click type to a Bukkit's ClickType on a " + Environment.getCurrent().getName() + " environment");
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
		public static ClickTypeAdapter valueOf(String name) {
			return valueOf(name, VALUES);
		}
		
		/**
		 * Equivalent of <code>values()</code>.
		 * 
		 * @return Pseudo-enum's constants
		 */
		public static ClickTypeAdapter[] values() {
			return VALUES;
		}
		
	}
	
	/**
	 * Environment indipendent (Bukkit and Sponge) click action adapter.
	 */
	public static class ClickActionAdapter extends PseudoEnum<ClickActionAdapter> {
		
		/**
		 * Nothing will happen from the click.
		 * 
		 * <p><strong>Note:</strong> There may be cases where nothing
		 * will happen and this is value is not provided, but it is
		 * guaranteed that this value is accurate when given.</p>
		 */
		public static final ClickActionAdapter NOTHING = new ClickActionAdapter("NOTHING");
		
		/**
		 * All of the items on the clicked slot are moved to the cursor.
		 */
		public static final ClickActionAdapter PICKUP_ALL = new ClickActionAdapter("PICKUP_ALL");
		
		/**
		 * Some of the items on the clicked slot are moved to the cursor.
		 */
		public static final ClickActionAdapter PICKUP_SOME = new ClickActionAdapter("PICKUP_SOME");
		
		/**
		 * Half of the items on the clicked slot are moved to the cursor.
		 */
		public static final ClickActionAdapter PICKUP_HALF = new ClickActionAdapter("PICKUP_HALF");
		
		/**
		 * One of the items on the clicked slot are moved to the cursor.
		 */
		public static final ClickActionAdapter PICKUP_ONE = new ClickActionAdapter("PICKUP_ONE");
		
		/**
		 * All of the items on the cursor are moved to the clicked slot.
		 */
		public static final ClickActionAdapter PLACE_ALL = new ClickActionAdapter("PLACE_ALL");
		
		/**
		 * Some of the items from the cursor are moved to the
		 * clicked slot (usually up to the max stack size).
		 */
		public static final ClickActionAdapter PLACE_SOME = new ClickActionAdapter("PLACE_SOME");
		
		/**
		 * A single item from the cursor is moved to the clicked slot.
		 */
		public static final ClickActionAdapter PLACE_ONE = new ClickActionAdapter("PLACE_ONE");
		
		/**
		 * The clicked item and the cursor are exchanged.
		 */
		public static final ClickActionAdapter SWAP_WITH_CURSOR = new ClickActionAdapter("SWAP_WITH_CURSOR");
		
		/**
		 * The entire cursor item is dropped.
		 */
		public static final ClickActionAdapter DROP_ALL_CURSOR = new ClickActionAdapter("DROP_ALL_CURSOR");
		
		/**
		 * One item is dropped from the cursor.
		 */
		public static final ClickActionAdapter DROP_ONE_CURSOR = new ClickActionAdapter("DROP_ONE_CURSOR");
		
		/**
		 * The entire clicked slot is dropped.
		 */
		public static final ClickActionAdapter DROP_ALL_SLOT = new ClickActionAdapter("DROP_ALL_SLOT");
		
		/**
		 * One item is dropped from the clicked slot.
		 */
		public static final ClickActionAdapter DROP_ONE_SLOT = new ClickActionAdapter("DROP_ONE_SLOT");
		
		/**
		 * The item is moved to the opposite inventory if a space is found.
		 */
		public static final ClickActionAdapter MOVE_TO_OTHER_INVENTORY = new ClickActionAdapter("MOVE_TO_OTHER_INVENTORY");
		
		/**
		 * The clicked item is moved to the hotbar, and the item
		 * currently there is re-added to the player's inventory.
		 * 
		 * <p><strong>Note:</strong> the hotbar includes the off hand.</p>
		 */
		public static final ClickActionAdapter HOTBAR_MOVE_AND_READD = new ClickActionAdapter("HOTBAR_MOVE_AND_READD");
		
		/**
		 * The clicked slot and the picked hotbar slot are swapped.
		 * 
		 * <p><strong>Note:</strong> the hotbar includes the off hand.</p>
		 */
		public static final ClickActionAdapter HOTBAR_SWAP = new ClickActionAdapter("HOTBAR_SWAP");
		
		/**
		 * A max-size stack of the clicked item is put on the cursor.
		 */
		public static final ClickActionAdapter CLONE_STACK = new ClickActionAdapter("CLONE_STACK");
		
		/**
		 * The inventory is searched for the same material, and
		 * they are put on the cursor up to its max stack size.
		 */
		public static final ClickActionAdapter COLLECT_TO_CURSOR = new ClickActionAdapter("COLLECT_TO_CURSOR");
		
		/**
		 * An unrecognized click type.
		 */
		public static final ClickActionAdapter UNKNOWN = new ClickActionAdapter("UNKNOWN");
		private static final ClickActionAdapter[] VALUES = new ClickActionAdapter[] { NOTHING, PICKUP_ALL, PICKUP_SOME, PICKUP_HALF, PICKUP_ONE, PLACE_ALL, PLACE_SOME, PLACE_ONE, SWAP_WITH_CURSOR, DROP_ALL_CURSOR, DROP_ONE_CURSOR, DROP_ALL_SLOT, DROP_ONE_SLOT, MOVE_TO_OTHER_INVENTORY, HOTBAR_MOVE_AND_READD, HOTBAR_SWAP, CLONE_STACK, COLLECT_TO_CURSOR, UNKNOWN };
		private static int ordinal = 0;
		
		private ClickActionAdapter(String name) {
			super(name, ordinal++);
		}
		
		/**
		 * Gets the click type adapted for Bukkit environments.
		 * 
		 * @return Bukkit-adapted click type
		 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
		 */
		public InventoryAction bukkitValue() {
			if (Environment.isBukkit())
				return InventoryAction.valueOf(name);
			throw new UnsupportedOperationException("Unable to adapt click action to a Bukkit's InventoryAction on a " + Environment.getCurrent().getName() + " environment");
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
		public static ClickActionAdapter valueOf(String name) {
			return valueOf(name, VALUES);
		}
		
		/**
		 * Equivalent of <code>values()</code>.
		 * 
		 * @return Pseudo-enum's constants
		 */
		public static ClickActionAdapter[] values() {
			return VALUES;
		}
		
	}
	
}
