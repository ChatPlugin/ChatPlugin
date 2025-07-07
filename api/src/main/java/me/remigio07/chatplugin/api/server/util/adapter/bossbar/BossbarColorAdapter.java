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

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) bossbar color adapter.
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Bossbars#colors">ChatPlugin wiki/Modules/Bossbars/Colors</a>
 */
public class BossbarColorAdapter {
	
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
	private static Map<String, Object> spongeColors;
	private String name;
	
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
		this.name = name;
	}
	
	/**
	 * Gets the bossbar color adapted for Bukkit environments.
	 * 
	 * <p>{@link #RANDOM} may only return a color that {@link #isSupported()}.</p>
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
	 * @return Sponge-adapted bossbar color
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public BossBarColor spongeValue() {
		if (Environment.isSponge())
			return (BossBarColor) (this == RANDOM ? new ArrayList<>(spongeColors.values()).get(VersionUtils.getVersion().isAtLeast(Version.V1_9) ? ThreadLocalRandom.current().nextInt(VALUES.length - 1) : 2) : spongeColors.get(name())); // .values().get(2) = PINK
		throw new UnsupportedOperationException("Unable to adapt bossbar color to a Sponge's BossBarColor on a " + Environment.getCurrent().getName() + " environment");
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
	 * Checks if this bossbar color is supported on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this bossbar color is supported
	 */
	public boolean isSupported() {
		return VersionUtils.getVersion().isAtLeast(Version.V1_9) || this == PINK || this == RANDOM;
	}
	
	/**
	 * Equivalent of <code>Enum#valueOf(String)</code>,
	 * with the only difference that instead of throwing
	 * {@link IllegalArgumentException} <code>null</code>
	 * is returned if the constant's name is invalid.
	 * 
	 * @param name Constant's name
	 * @return Enum constant
	 */
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the constant's name is invalid")
	public static BossbarColorAdapter valueOf(String name) {
		for (BossbarColorAdapter color : VALUES)
			if (color.name().equals(name))
				return color;
		return null;
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static BossbarColorAdapter[] values() {
		return VALUES;
	}
	
}
