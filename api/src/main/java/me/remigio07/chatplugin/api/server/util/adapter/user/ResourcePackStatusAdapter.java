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

package me.remigio07.chatplugin.api.server.util.adapter.user;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) resource pack status adapter.
 */
public class ResourcePackStatusAdapter extends PseudoEnum<ResourcePackStatusAdapter> {
	
	/**
	 * The client accepted the pack and is beginning a download of it.
	 */
	public static final ResourcePackStatusAdapter ACCEPTED = new ResourcePackStatusAdapter("ACCEPTED");
	
	/**
	 * The client declined to download the resource pack.
	 */
	public static final ResourcePackStatusAdapter DECLINED = new ResourcePackStatusAdapter("DECLINED");
	
	/**
	 * The client accepted the pack, but download failed.
	 */
	public static final ResourcePackStatusAdapter FAILED = new ResourcePackStatusAdapter("FAILED");
	
	/**
	 * The pack URI was successfully loaded.
	 * 
	 * <p><strong>Note:</strong> This does not necessarily mean that the pack was loaded,
	 * as the Vanilla client sends this even when encountering a 404 error or similar.</p>
	 */
	public static final ResourcePackStatusAdapter SUCCESSFULLY_DOWNLOADED = new ResourcePackStatusAdapter("SUCCESSFULLY_DOWNLOADED");
	private static final ResourcePackStatusAdapter[] VALUES = new ResourcePackStatusAdapter[] { ACCEPTED, DECLINED, FAILED, SUCCESSFULLY_DOWNLOADED };
	private static int ordinal = 0;
	
	private ResourcePackStatusAdapter(String name) {
		super(name, ordinal++);
	}
	
	/**
	 * Gets the resource pack status adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted resource pack status
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.event.player.PlayerResourcePackStatusEvent.Status bukkitValue() {
		if (Environment.isBukkit())
			return org.bukkit.event.player.PlayerResourcePackStatusEvent.Status.valueOf(name);
		throw new UnsupportedOperationException("Unable to adapt resource pack status to a Bukkit's PlayerResourcePackStatusEvent.Status on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the resource pack status adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted resource pack status
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.event.entity.living.humanoid.player.ResourcePackStatusEvent.ResourcePackStatus spongeValue() {
		if (Environment.isSponge())
			return org.spongepowered.api.event.entity.living.humanoid.player.ResourcePackStatusEvent.ResourcePackStatus.valueOf(name);
		throw new UnsupportedOperationException("Unable to adapt resource pack status to a Sponge's ResourcePackStatusEvent.ResourcePackStatus on a " + Environment.getCurrent().getName() + " environment");
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
	public static ResourcePackStatusAdapter valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static ResourcePackStatusAdapter[] values() {
		return VALUES;
	}
	
	/**
	 * Equivalent of {@link #valueOf(String)}, but:
	 * 	<ul>
	 * 		<li>case insensitive</li>
	 * 		<li>returns <code>null</code> instead of throwing {@link IllegalArgumentException}</li>
	 * 		<li>also recognizes Bukkit- and Sponge-compatible IDs</li>
	 * 	</ul>
	 * 
	 * <p>Will return <code>null</code> if the specified name is invalid.</p>
	 * 
	 * @param name Constant's name, case insensitive
	 * @return Pseudo-enum's constant
	 * @throws NullPointerException If <code>name == null</code>
	 */
	@Nullable(why = "Specified name may be invalid")
	public static ResourcePackStatusAdapter value(String name) {
		if (name.equalsIgnoreCase("FAILED_DOWNLOAD"))
			return FAILED;
		try {
			return valueOf(name.toUpperCase());
		} catch (IllegalArgumentException iae) {
			return null;
		}
	}
	
}
