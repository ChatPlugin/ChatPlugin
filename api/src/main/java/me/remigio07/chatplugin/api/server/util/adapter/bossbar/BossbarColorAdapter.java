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

package me.remigio07.chatplugin.api.server.util.adapter.bossbar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.boss.BarColor;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) bossbar color adapter.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Bossbars#colors">ChatPlugin wiki/Modules/Bossbars/Colors</a>
 */
public class BossbarColorAdapter extends PseudoEnum<BossbarColorAdapter> {
	
	/**
	 * Displays a blue color.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarColorAdapter BLUE = new BossbarColorAdapter("BLUE");
	
	/**
	 * Displays a green color.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarColorAdapter GREEN = new BossbarColorAdapter("GREEN");
	
	/**
	 * Displays a pink color.
	 */
	public static final BossbarColorAdapter PINK = new BossbarColorAdapter("PINK");
	
	/**
	 * Displays a purple color.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarColorAdapter PURPLE = new BossbarColorAdapter("PURPLE");
	
	/**
	 * Displays a red color.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarColorAdapter RED = new BossbarColorAdapter("RED");
	
	/**
	 * Displays a white color.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarColorAdapter WHITE = new BossbarColorAdapter("WHITE");
	
	/**
	 * Displays a yellow color.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarColorAdapter YELLOW = new BossbarColorAdapter("YELLOW");
	
	/**
	 * Displays a random color.
	 */
	public static final BossbarColorAdapter RANDOM = new BossbarColorAdapter("RANDOM");
	private static final BossbarColorAdapter[] VALUES = new BossbarColorAdapter[] { BLUE, GREEN, PINK, PURPLE, RED, WHITE, YELLOW, RANDOM };
	private static int ordinal = 0;
	private static Map<String, Object> spongeColors;
	
	static {
		if (Environment.isSponge())
			spongeColors = (Map<String, Object>) Stream.of(new Object[][] {
				{ "BLUE", BossBarColors.BLUE },
				{ "GREEN", BossBarColors.GREEN },
				{ "PINK", BossBarColors.PINK },
				{ "PURPLE", BossBarColors.PURPLE },
				{ "RED", BossBarColors.RED },
				{ "WHITE", BossBarColors.WHITE },
				{ "YELLOW", BossBarColors.YELLOW }
			}).collect(Collectors.toMap(color -> (String) color[0], color -> (Object) color[1], (x, y) -> y, LinkedHashMap::new));
	}
	
	private BossbarColorAdapter(String name) {
		super(name, ordinal++);
	}
	
	/**
	 * Gets the bossbar color adapted for Bukkit environments.
	 * 
	 * <p>{@link #RANDOM} may only return a color that {@link #isSupported()}.</p>
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 * 
	 * @return Bukkit-adapted bossbar color
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public BarColor bukkitValue() {
		if (Environment.isBukkit())
			return this == RANDOM ? BarColor.values()[VersionUtils.getVersion().isAtLeast(Version.V1_9) ? ThreadLocalRandom.current().nextInt(BarColor.values().length) : 2] : BarColor.valueOf(name()); // .values()[2] = PINK
		throw new UnsupportedOperationException("Unable to adapt bossbar color to a Bukkit's BarColor on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the bossbar color adapted for Sponge environments.
	 * 
	 * <p>{@link #RANDOM} may only return a color that {@link #isSupported()}.</p>
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 * 
	 * @return Sponge-adapted bossbar color
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public BossBarColor spongeValue() {
		if (Environment.isSponge())
			return (BossBarColor) (this == RANDOM ? new ArrayList<>(spongeColors.values()).get(VersionUtils.getVersion().isAtLeast(Version.V1_9) ? ThreadLocalRandom.current().nextInt(VALUES.length - 1) : 2) : spongeColors.get(name())); // .values().get(2) = PINK
		throw new UnsupportedOperationException("Unable to adapt bossbar color to a Sponge's BossBarColor on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Checks if this bossbar color is supported
	 * on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this bossbar color is supported
	 */
	public boolean isSupported() {
		return this == PINK || this == RANDOM || VersionUtils.getVersion().isAtLeast(Version.V1_9);
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
	public static BossbarColorAdapter valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static BossbarColorAdapter[] values() {
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
	public static BossbarColorAdapter value(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (IllegalArgumentException iae) {
			return null;
		}
	}
	
}
