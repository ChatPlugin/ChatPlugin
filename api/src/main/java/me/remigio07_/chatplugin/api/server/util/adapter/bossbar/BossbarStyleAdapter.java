/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.api.server.util.adapter.bossbar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.boss.BarStyle;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.BossBarOverlays;

import io.netty.util.internal.ThreadLocalRandom;
import me.remigio07_.chatplugin.api.common.util.VersionUtils;
import me.remigio07_.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) bossbar style adapter. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Bossbars#styles">ChatPlugin wiki/Bossbars#Styles</a>
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 */
public class BossbarStyleAdapter {
	
	/**
	 * Displays the bossbar split into 6 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@link Version#V1_9}</p>
	 */
	public static final BossbarStyleAdapter SEGMENTED_6 = new BossbarStyleAdapter("SEGMENTED_6", "NOTCHED_6");
	
	/**
	 * Displays the bossbar split into 10 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@link Version#V1_9}</p>
	 */
	public static final BossbarStyleAdapter SEGMENTED_10 = new BossbarStyleAdapter("SEGMENTED_10", "NOTCHED_10");
	
	/**
	 * Displays the bossbar split into 12 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@link Version#V1_9}</p>
	 */
	public static final BossbarStyleAdapter SEGMENTED_12 = new BossbarStyleAdapter("SEGMENTED_12", "NOTCHED_12");
	
	/**
	 * Displays the bossbar split into 20 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@link Version#V1_9}</p>
	 */
	public static final BossbarStyleAdapter SEGMENTED_20 = new BossbarStyleAdapter("SEGMENTED_20", "NOTCHED_20");
	
	/**
	 * Displays the bossbar as a solid segment.
	 */
	public static final BossbarStyleAdapter SOLID = new BossbarStyleAdapter("SOLID", "PROGRESS");
	
	/**
	 * Displays a random style.
	 */
	public static final BossbarStyleAdapter RANDOM = new BossbarStyleAdapter("RANDOM", "RANDOM");
	
	private static final BossbarStyleAdapter[] VALUES = new BossbarStyleAdapter[] { SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20, SOLID, RANDOM };
	private static Map<String, Object> spongeStyles;
	private String name, spongeID;
	
	static {
		if (Environment.isSponge())
			spongeStyles = (Map<String, Object>) Stream.of(new Object[][] {
				{ "SOLID", BossBarOverlays.PROGRESS },
				{ "SEGMENTED_6", BossBarOverlays.NOTCHED_6 },
				{ "SEGMENTED_10", BossBarOverlays.NOTCHED_10 },
				{ "SEGMENTED_12", BossBarOverlays.NOTCHED_12 },
				{ "SEGMENTED_20", BossBarOverlays.NOTCHED_20 }
			}).collect(Collectors.toMap(color -> (String) color[0], color -> (Object) color[1], (x, y) -> y, LinkedHashMap::new));
	}
	
	private BossbarStyleAdapter(String name, String spongeID) {
		this.name = name;
		this.spongeID = spongeID;
	}
	
	/**
	 * Gets the bossbar style adapted for Bukkit environments.
	 * {@link #RANDOM} may only return a style that {@link #isSupported()}.
	 * 
	 * @return Bukkit-adapted bossbar style
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public BarStyle bukkitValue() {
		if (Environment.isBukkit())
			return this == RANDOM ? BarStyle.values()[VersionUtils.getVersion().isAtLeast(Version.V1_9) ? ThreadLocalRandom.current().nextInt(BarStyle.values().length) : 4] : BarStyle.valueOf(name()); // .values()[4] = SOLID
		else throw new UnsupportedOperationException("Unable to adapt bossbar style to a Bukkit's BarStyle on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the bossbar style adapted for Sponge environments.
	 * {@link #RANDOM} may only return a style that {@link #isSupported()}.
	 * 
	 * @return Sponge-adapted bossbar style
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public BossBarOverlay spongeValue() {
		if (Environment.isSponge())
			return (BossBarOverlay) (this == RANDOM ? new ArrayList<>(spongeStyles.values()).get(VersionUtils.getVersion().isAtLeast(Version.V1_9) ? ThreadLocalRandom.current().nextInt(VALUES.length - 1) : 0) : spongeStyles.get(name())); // .values().get(0) = SOLID
		else throw new UnsupportedOperationException("Unable to adapt bossbar style to a Sponge's BossBarOverlay on a " + Environment.getCurrent().getName() + " environment");
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
	 * Get the corresponding {@link org.spongepowered.api.boss.BossBarOverlays}' ID.
	 * 
	 * @return Sponge corresponding ID
	 */
	public String getSpongeID() {
		return spongeID;
	}
	
	/**
	 * Checks if this style is supported on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this style is supported
	 */
	public boolean isSupported() {
		return VersionUtils.getVersion().isAtLeast(Version.V1_9) || this == SOLID || this == RANDOM;
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
	public static BossbarStyleAdapter valueOf(String name) {
		for (BossbarStyleAdapter style : VALUES)
			if (style.name().equals(name))
				return style;
		return null;
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static BossbarStyleAdapter[] values() {
		return VALUES;
	}
	
}
