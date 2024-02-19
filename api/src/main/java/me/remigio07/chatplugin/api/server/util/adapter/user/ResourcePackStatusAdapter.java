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

package me.remigio07.chatplugin.api.server.util.adapter.user;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) resource pack status adapter.
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 */
public class ResourcePackStatusAdapter {
	
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
	 * The pack URI was successfully loaded. This does not necessarily mean that the pack was loaded,
	 * as the Vanilla client sends this even when encountering a 404 error or similar.
	 */
	public static final ResourcePackStatusAdapter SUCCESSFULLY_DOWNLOADED = new ResourcePackStatusAdapter("SUCCESSFULLY_DOWNLOADED");
	private static final ResourcePackStatusAdapter[] VALUES = new ResourcePackStatusAdapter[] { ACCEPTED, DECLINED, FAILED, SUCCESSFULLY_DOWNLOADED };
	private String name;
	
	private ResourcePackStatusAdapter(String name) {
		this.name = name;
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
		else throw new UnsupportedOperationException("Unable to adapt resource pack status to a Bukkit's PlayerResourcePackStatusEvent.Status on a " + Environment.getCurrent().getName() + " environment");
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
		else throw new UnsupportedOperationException("Unable to adapt resource pack status to a Sponge's ResourcePackStatusEvent.ResourcePackStatus on a " + Environment.getCurrent().getName() + " environment");
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
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the name is invalid")
	public static ResourcePackStatusAdapter valueOf(String name) {
		switch (name) {
		case "ACCEPTED":
			return ACCEPTED;
		case "DECLINED":
			return DECLINED;
		case "FAILED":
		case "FAILED_DOWNLOAD":
			return FAILED;
		case "SUCCESSFULLY_DOWNLOADED":
			return SUCCESSFULLY_DOWNLOADED;
		} return null;
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static ResourcePackStatusAdapter[] values() {
		return VALUES;
	}
	
}
