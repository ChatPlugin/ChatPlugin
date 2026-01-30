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

import java.lang.reflect.InvocationTargetException;
import java.util.StringJoiner;

import org.spongepowered.api.block.BlockSnapshot;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) block adapter.
 */
public class BlockAdapter {
	
	private Object block;
	private MaterialAdapter type;
	
	/**
	 * Constructs a block adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.block.Block} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.block.BlockSnapshot} for Sponge environments</li>
	 * 		<li>{@link BlockAdapter.FabricBlock} for Fabric environments</li>
	 * 	</ul>
	 * 
	 * @param block Block object
	 */
	public BlockAdapter(Object block) {
		this.block = block;
		String id = null;
		
		if (Environment.isBukkit())
			id = bukkitValue().getType().name();
		else if (Environment.isSponge())
			id = spongeValue().getState().getType().getItem().get().getId().substring(10);
		else if (VersionUtils.getVersion().isOlderThan(Version.V1_19_3))
			try { // Registry.ITEM.getId(fabricValue().getBlock().asItem()).toString()
				id = Registry.class.getMethod("method_10221", Object.class).invoke(Registry.class.getField("field_11142").get(null), fabricValue().getBlock().asItem()).toString();
			} catch (NoSuchMethodException | IllegalAccessException | NoSuchFieldException | InvocationTargetException e) {
				e.printStackTrace();
			}
		else id = Registries.ITEM.getId(fabricValue().getBlock().asItem()).toString();
		type = new MaterialAdapter(id);
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "BlockAdapter{", "}")
				.add("type=" + getType())
				.add("x=" + getX())
				.add("y=" + getY())
				.add("z=" + getZ())
				.toString();
	}
	
	/**
	 * Gets the block adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted block
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.block.Block bukkitValue() {
		if (Environment.isBukkit())
			return (org.bukkit.block.Block) block;
		throw new UnsupportedOperationException("Unable to adapt block to a Bukkit's Block on a " + Environment.getCurrent().getName() + " environment");
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
		throw new UnsupportedOperationException("Unable to adapt block to a Sponge's BlockSnapshot on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the block adapted for Fabric environments.
	 * 
	 * @return Fabric-adapted block
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public FabricBlock fabricValue() {
		if (Environment.isFabric())
			return (FabricBlock) block;
		throw new UnsupportedOperationException("Unable to adapt block to a Fabric's FabricBlock on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets this block's type.
	 * 
	 * @return Block's type
	 */
	public MaterialAdapter getType() {
		return type;
	}
	
	/**
	 * Gets this block's X coordinate.
	 * 
	 * @return Block's X
	 */
	public int getX() {
		return Environment.isBukkit() ? bukkitValue().getX() : Environment.isSponge() ? spongeValue().getLocation().get().getBlockX() : fabricValue().getBlockPos().getX();
	}
	
	/**
	 * Gets this block's Y coordinate.
	 * 
	 * @return Block's Y
	 */
	public int getY() {
		return Environment.isBukkit() ? bukkitValue().getY() : Environment.isSponge() ? spongeValue().getLocation().get().getBlockY() : fabricValue().getBlockPos().getY();
	}
	
	/**
	 * Gets this block's Z coordinate.
	 * 
	 * @return Block's Z
	 */
	public int getZ() {
		return Environment.isBukkit() ? bukkitValue().getZ() : Environment.isSponge() ? spongeValue().getLocation().get().getBlockZ() : fabricValue().getBlockPos().getZ();
	}
	
	/**
	 * Represents a Fabric block composed of:
	 * 	<ul>
	 * 		<li>a {@link net.minecraft.block.Block}</li>
	 * 		<li>a {@link net.minecraft.util.math.BlockPos}</li>
	 * 	</ul>
	 */
	public static class FabricBlock {
		
		private Block block;
		private BlockPos blockPos;
		
		/**
		 * Constructs a new Fabric block.
		 * 
		 * @param block Block's instance
		 * @param blockPos Block's position
		 */
		public FabricBlock(Block block, BlockPos blockPos) {
			this.block = block;
			this.blockPos = blockPos;
		}
		
		/**
		 * Gets this block's instance.
		 * 
		 * @return Block's instance
		 */
		public Block getBlock() {
			return block;
		}
		
		/**
		 * Gets this block's position.
		 * 
		 * @return Block's position
		 */
		public BlockPos getBlockPos() {
			return blockPos;
		}
		
	}
	
}
