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

import org.bukkit.block.Block;
import org.spongepowered.api.block.BlockSnapshot;

import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) block adapter.
 */
public class BlockAdapter {
	
	private Object block;
	private MaterialAdapter material;
	
	/**
	 * Constructs a block adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.block.Block} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.block.BlockSnapshot} for Sponge environments</li>
	 * 	</ul>
	 * 
	 * @param block Block object
	 */
	public BlockAdapter(Object block) {
		this.block = block;
		material = new MaterialAdapter(Environment.isBukkit() ? bukkitValue().getType().name() : spongeValue().getState().getType().getItem().get().getId().substring(10)); // spongeValue().getState().getType() // Sponge.getRegistry().getType(ItemType.class, spongeValue().getState().getType().getId()
	}
	
	/**
	 * Gets the block adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted block
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public Block bukkitValue() {
		if (Environment.isBukkit())
			return (Block) block;
		else throw new UnsupportedOperationException("Unable to adapt block to a Bukkit's Block on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the block adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted block
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public BlockSnapshot spongeValue() {
		if (Environment.isSponge())
			return (BlockSnapshot) block;
		else throw new UnsupportedOperationException("Unable to adapt block to a Sponge's BlockSnapshot on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this block's type.
	 * 
	 * @return Block's type
	 */
	public MaterialAdapter getType() {
		return material;
	}
	
	/**
	 * Gets this block's X coordinate.
	 * 
	 * @return Block's X
	 */
	public int getX() {
		return Environment.isBukkit() ? bukkitValue().getX() : spongeValue().getLocation().get().getBlockX();
	}
	
	/**
	 * Gets this block's Y coordinate.
	 * 
	 * @return Block's Y
	 */
	public int getY() {
		return Environment.isBukkit() ? bukkitValue().getY() : spongeValue().getLocation().get().getBlockY();
	}
	
	/**
	 * Gets this block's Z coordinate.
	 * 
	 * @return Block's Z
	 */
	public int getZ() {
		return Environment.isBukkit() ? bukkitValue().getZ() : spongeValue().getLocation().get().getBlockZ();
	}
	
}
