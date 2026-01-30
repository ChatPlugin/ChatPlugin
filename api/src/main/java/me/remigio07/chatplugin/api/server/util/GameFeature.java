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

package me.remigio07.chatplugin.api.server.util;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;

/**
 * Annotation used to indicate that a Minecraft feature handled by a
 * {@link ChatPluginManager} is not available under certain circumstances.
 * 
 * <p>It should be applied to managers' class declarations and checked
 * using {@link ChatPluginManager#checkAvailability(boolean)}.</p>
 * 
 * <p><strong>Note:</strong> the check is processed on the plugin's
 * server implementations only (Bukkit, Sponge and Fabric).</p>
 */
@Target(TYPE)
@Retention(RUNTIME)
@ServerImplementationOnly(why = ServerImplementationOnly.GAME_FEATURE)
public @interface GameFeature {
	
	/**
	 * Gets this feature's name.
	 * 
	 * <p><strong>Example:</strong> "actionbar", "bossbar"...</p>
	 * 
	 * @return Feature's name
	 */
	public String name();
	
	/**
	 * Checks if Spigot (or a fork) is required to run this feature.
	 * 
	 * <p>This applies to Bukkit environments only.</p>
	 * 
	 * <p><strong>Default:</strong> <code>false</code></p>
	 * 
	 * @return Whether Spigot is required to run this feature
	 */
	public boolean spigotRequired() default false;
	
	/**
	 * Checks if Paper (or a fork) is required to run this feature.
	 * 
	 * <p>This applies to Bukkit environments only.</p>
	 * 
	 * <p><strong>Default:</strong> <code>false</code></p>
	 * 
	 * @return Whether Paper is required to run this feature
	 */
	public boolean paperRequired() default false;
	
	/**
	 * Gets the minimum Vanilla version required
	 * to run this feature on a Bukkit environment.
	 * 
	 * <p>Will return {@link Version#UNSUPPORTED} if
	 * this feature is not supported on Bukkit.</p>
	 * 
	 * <p><strong>Default:</strong> {@linkplain Version#V1_8 1.8}</p>
	 * 
	 * @return Minimum Vanilla version required to run on Bukkit
	 */
	@NotNull
	public Version minimumBukkitVersion() default Version.V1_8;
	
	/**
	 * Gets the minimum Vanilla version required
	 * to run this feature on a Sponge environment.
	 * 
	 * <p>Will return {@link Version#UNSUPPORTED} if
	 * this feature is not supported on Sponge.</p>
	 * 
	 * <p><strong>Default:</strong> {@linkplain Version#V1_8 1.8}</p>
	 * 
	 * @return Minimum Vanilla version required to run on Sponge
	 */
	@NotNull
	public Version minimumSpongeVersion() default Version.V1_8;
	
	/**
	 * Gets the minimum Vanilla version required
	 * to run this feature on a Fabric environment.
	 * 
	 * <p>Will return {@link Version#UNSUPPORTED} if
	 * this feature is not supported on Fabric.</p>
	 * 
	 * <p><strong>Default:</strong> {@linkplain Version#V1_14 1.14}</p>
	 * 
	 * @return Minimum Vanilla version required to run on Fabric
	 */
	@NotNull
	public Version minimumFabricVersion() default Version.V1_14;
	
}
