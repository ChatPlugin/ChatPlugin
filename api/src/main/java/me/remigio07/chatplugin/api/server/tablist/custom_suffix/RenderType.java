/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.server.tablist.custom_suffix;

import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents the tablist's custom suffixes' render type. See wiki for more info:
 * <br><a href="https://github.com/ChatPlugin/ChatPlugin/wiki/Tablist#custom-suffix">ChatPlugin wiki/Tablist#Custom suffix</a>
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 * 
 * @see CustomSuffixManager
 */
public class RenderType {
	
	/**
	 * Displays red hearts after the player's name; supports the "{health}" placeholder only.
	 */
	public static final RenderType HEARTS = new RenderType("HEARTS");
	
	/**
	 * Displays a yellow {@link Integer} after the player's name.
	 */
	public static final RenderType INTEGER = new RenderType("INTEGER");
	private static final RenderType[] VALUES = new RenderType[] { HEARTS, INTEGER };
	private String name;
	
	private RenderType(String name) {
		this.name = name;
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
		else throw new UnsupportedOperationException("Unable to adapt render type to a Bukkit's RenderType on a " + Environment.getCurrent().getName() + " environment");
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
		else throw new UnsupportedOperationException("Unable to adapt render type to a Sponge's ObjectiveDisplayMode on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * {@link Enum#name()}'s equivalent.
	 * 
	 * @return Type's name
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
		return this == HEARTS ? 0 : 1;
	}
	
	/**
	 * Equivalent of <code>Enum#valueOf(String)</code>,
	 * with the only difference that instead of throwing
	 * {@link IllegalArgumentException} <code>null</code>
	 * is returned if the constant's name is invalid.
	 * This method recognizes both Bukkit's and Sponge's IDs.
	 * 
	 * @param name Constant's name
	 * @return Enum constant
	 */
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the constant's name is invalid")
	public static RenderType valueOf(String name) {
		switch (name) {
		case "HEARTS":
			return HEARTS;
		case "INTEGER":
			return INTEGER;
		default:
			return null;
		}
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static RenderType[] values() {
		return VALUES;
	}
	
}
