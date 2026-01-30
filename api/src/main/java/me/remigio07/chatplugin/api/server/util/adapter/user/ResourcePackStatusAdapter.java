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

import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.spongepowered.api.event.entity.living.humanoid.player.ResourcePackStatusEvent.ResourcePackStatus;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) resource pack status adapter.
 */
public class ResourcePackStatusAdapter extends PseudoEnum<ResourcePackStatusAdapter> {
	
	/**
	 * The client accepted the pack and is beginning a download of it.
	 */
	public static final ResourcePackStatusAdapter ACCEPTED = new ResourcePackStatusAdapter("ACCEPTED");
	
	/**
	 * The client refused to accept the resource pack.
	 */
	public static final ResourcePackStatusAdapter DECLINED = new ResourcePackStatusAdapter("DECLINED");
	
	/**
	 * The pack was discarded by the client.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_20_3 1.20.3}
	 * <br><strong>Fallback:</strong> {@link #DECLINED}</p>
	 */
	public static final ResourcePackStatusAdapter DISCARDED = new ResourcePackStatusAdapter("DISCARDED", DECLINED);
	
	/**
	 * The client successfully downloaded the pack.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_20_3 1.20.3}
	 * <br><strong>Fallback:</strong> {@link #ACCEPTED}</p>
	 */
	public static final ResourcePackStatusAdapter DOWNLOADED = new ResourcePackStatusAdapter("DOWNLOADED");
	
	/**
	 * The client accepted the pack, but download failed.
	 */
	public static final ResourcePackStatusAdapter FAILED_DOWNLOAD = new ResourcePackStatusAdapter("FAILED_DOWNLOAD");
	
	/**
	 * The client was unable to reload the pack.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_20_3 1.20.3}
	 * <br><strong>Fallback:</strong> {@link #DECLINED}</p>
	 */
	public static final ResourcePackStatusAdapter FAILED_RELOAD = new ResourcePackStatusAdapter("FAILED_RELOAD", DECLINED);
	
	/**
	 * The pack URL was invalid.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_20_3 1.20.3}
	 * <br><strong>Fallback:</strong> {@link #FAILED_DOWNLOAD}</p>
	 */
	public static final ResourcePackStatusAdapter INVALID_URL = new ResourcePackStatusAdapter("INVALID_URL", FAILED_DOWNLOAD);
	
	/**
	 * The resource pack has been successfully downloaded and applied to the client.
	 * 
	 * <p><strong>Note:</strong> For old versions, this does not necessarily mean that the pack was loaded
	 * correctly, as the Vanilla client used to send this even when encountering a 404 error or similar.</p>
	 */
	public static final ResourcePackStatusAdapter SUCCESSFULLY_LOADED = new ResourcePackStatusAdapter("SUCCESSFULLY_LOADED");
	private static final ResourcePackStatusAdapter[] VALUES = new ResourcePackStatusAdapter[] { ACCEPTED, DECLINED, DISCARDED, DOWNLOADED, FAILED_DOWNLOAD, FAILED_RELOAD, INVALID_URL, SUCCESSFULLY_LOADED };
	private static int ordinal = 0;
	private ResourcePackStatusAdapter fallback;
	
	private ResourcePackStatusAdapter(String name) {
		this(name, null);
	}
	
	private ResourcePackStatusAdapter(String name, ResourcePackStatusAdapter fallback) {
		super(name, ordinal++);
		this.fallback = fallback;
	}
	
	/**
	 * Gets the resource pack status adapted for Bukkit environments.
	 * 
	 * <p>If the current version does not support this resource pack
	 * status, its {@link #getFallback()} will be returned.</p>
	 * 
	 * @return Bukkit-adapted resource pack status
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public Status bukkitValue() {
		if (Environment.isBukkit())
			return Status.valueOf(isSupported() ? name : fallback.name());
		throw new UnsupportedOperationException("Unable to adapt resource pack status to a Bukkit's PlayerResourcePackStatusEvent.Status on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the resource pack status adapted for Sponge environments.
	 * 
	 * <p>If the current version does not support this resource pack
	 * status, its {@link #getFallback()} will be returned.</p>
	 * 
	 * @return Sponge-adapted resource pack status
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public ResourcePackStatus spongeValue() {
		if (Environment.isSponge())
			return this == FAILED_DOWNLOAD ? ResourcePackStatus.FAILED : ResourcePackStatus.valueOf(isSupported() ? name : fallback.name());
		throw new UnsupportedOperationException("Unable to adapt resource pack status to a Sponge's ResourcePackStatusEvent.ResourcePackStatus on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the resource pack status adapted for Fabric environments.
	 * 
	 * <p>If {@link VersionUtils#getVersion()} does not support this resource
	 * pack status, its {@link #getFallback()} will be returned.</p>
	 * 
	 * @return Fabric-adapted resource pack status
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public ResourcePackStatusC2SPacket.Status fabricValue() {
		if (Environment.isFabric())
			return ResourcePackStatusC2SPacket.Status.valueOf(isSupported() ? name : fallback.name());
		throw new UnsupportedOperationException("Unable to adapt resource pack status to a Fabric's ResourcePackStatusC2SPacket.Status on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the fallback of this resource pack status, used when
	 * it is not supported on {@link VersionUtils#getVersion()}
	 * to ensure compatibility with older versions.
	 * 
	 * @return Resource pack status' fallback
	 */
	@Nullable(why = "Null if all versions support this resource pack status")
	public ResourcePackStatusAdapter getFallback() {
		return fallback;
	}
	
	/**
	 * Checks if this resource pack status is
	 * supported on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this resource pack status is supported
	 */
	public boolean isSupported() {
		return fallback == null || VersionUtils.getVersion().isAtLeast(Version.V1_20_3);
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
	public static ResourcePackStatusAdapter value(String name) {
		if (name.equalsIgnoreCase("FAILED"))
			return FAILED_DOWNLOAD;
		try {
			return valueOf(name.toUpperCase());
		} catch (IllegalArgumentException iae) {
			return null;
		}
	}
	
}
