/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

import java.util.Map;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.item.ItemStackAdapter;

/**
 * Environment indipendent (Bukkit and Sponge) drag event adapter.
 */
public class DragEventAdapter {
	
	private Map<Integer, ItemStackAdapter> items;
	private ItemStackAdapter oldCursor, cursor;
	private boolean single;
	
	/**
	 * Constructs a new drag event adapter.
	 * 
	 * @param items Items to be added
	 * @param oldCursor Item on the old cursor
	 * @param cursor Item on the cursor
	 * @param single Whether one single item is placed in each slot
	 */
	public DragEventAdapter(Map<Integer, ItemStackAdapter> items, ItemStackAdapter oldCursor, @Nullable(why = "Cursor may not be holding any items") ItemStackAdapter cursor, boolean single) {
		this.items = items;
		this.oldCursor = oldCursor;
		this.cursor = cursor;
		this.single = single;
	}
	
	/**
	 * Gets the items to be added to the inventory in this drag.
	 * 
	 * <p>Keys represent slot IDs, values their corresponding items.</p>
	 * 
	 * @return Items to be added
	 */
	public Map<Integer, ItemStackAdapter> getItems() {
		return items;
	}
	
	/**
	 * Gets the item on the old cursor.
	 * 
	 * @return Item on the old cursor
	 */
	@NotNull
	public ItemStackAdapter getOldCursor() {
		return oldCursor;
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
	 * Sets the item on the cursor.
	 * 
	 * <p>Specify <code>null</code> if the cursor should not be holding any items.</p>
	 * 
	 * @param cursor Item on the cursor
	 */
	public void setCursor(@Nullable(why = "Cursor may not be holding any items") ItemStackAdapter cursor) {
		this.cursor = cursor;
	}
	
	/**
	 * Checks if one single item from the cursor is placed in each selected slot.
	 * 
	 * <p>Will return <code>false</code> if the cursor's
	 * item is split evenly across all selected slots.</p>
	 * 
	 * @return Whether one single item is placed in each slot
	 */
	public boolean isSingle() {
		return single;
	}
	
}
