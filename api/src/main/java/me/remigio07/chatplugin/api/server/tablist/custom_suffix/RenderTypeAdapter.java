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

package me.remigio07.chatplugin.api.server.tablist.custom_suffix;

import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.bootstrap.Environment;
import net.minecraft.scoreboard.ScoreboardCriterion.RenderType;

/**
 * Environment indipendent (Bukkit, Sponge and Fabric) render type adapter.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Tablists#render-types">ChatPlugin wiki/Modules/Tablists/Custom suffix/Render types</a>
 * @see CustomSuffixManager
 */
public class RenderTypeAdapter extends PseudoEnum<RenderTypeAdapter> {
	
	/**
	 * Displays hearts after the player's name; supports the "{health}" placeholder only.
	 * 
	 * <p>The following colors are used:
	 * 	<ul>
	 * 		<li>red: normal hearts</li>
	 * 		<li>yellow: absorption effect hearts</li>
	 * 		<li>hollow: missing hearts</li>
	 * 	</ul>
	 */
	public static final RenderTypeAdapter HEARTS = new RenderTypeAdapter("HEARTS");
	
	/**
	 * Displays a yellow {@link Integer} after the player's name.
	 */
	public static final RenderTypeAdapter INTEGER = new RenderTypeAdapter("INTEGER");
	private static final RenderTypeAdapter[] VALUES = new RenderTypeAdapter[] { HEARTS, INTEGER };
	private static int ordinal = 0;
	
	private RenderTypeAdapter(String name) {
		super(name, ordinal++);
	}
	
	/**
	 * Gets the render type adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted render type
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.scoreboard.RenderType bukkitValue() {
		if (Environment.isBukkit())
			return org.bukkit.scoreboard.RenderType.valueOf(name());
		throw new UnsupportedOperationException("Unable to adapt render type to a Bukkit's RenderType on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the render type adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted render type
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public ObjectiveDisplayMode spongeValue() {
		if (Environment.isSponge())
			try {
				return (ObjectiveDisplayMode) ObjectiveDisplayModes.class.getField(name()).get(null);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException e) {
				return null;
			}
		throw new UnsupportedOperationException("Unable to adapt render type to a Sponge's ObjectiveDisplayMode on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the render type adapted for Fabric environments.
	 * 
	 * @return Fabric-adapted render type
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public RenderType fabricValue() {
		if (Environment.isFabric())
			return RenderType.valueOf(name());
		throw new UnsupportedOperationException("Unable to adapt render type to a Fabric's RenderType on a " + Environment.getCurrent().getName() + " environment");
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
	public static RenderTypeAdapter valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static RenderTypeAdapter[] values() {
		return VALUES;
	}
	
}
