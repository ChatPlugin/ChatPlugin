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

import org.bukkit.boss.BarStyle;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.BossBarOverlays;

import me.remigio07.chatplugin.api.common.util.PseudoEnum;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) bossbar style adapter.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Bossbars#styles">ChatPlugin wiki/Modules/Bossbars/Styles</a>
 */
public class BossbarStyleAdapter extends PseudoEnum<BossbarStyleAdapter> {
	
	/**
	 * Displays the bossbar split into 6 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarStyleAdapter SEGMENTED_6 = new BossbarStyleAdapter("SEGMENTED_6", "NOTCHED_6");
	
	/**
	 * Displays the bossbar split into 10 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarStyleAdapter SEGMENTED_10 = new BossbarStyleAdapter("SEGMENTED_10", "NOTCHED_10");
	
	/**
	 * Displays the bossbar split into 12 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarStyleAdapter SEGMENTED_12 = new BossbarStyleAdapter("SEGMENTED_12", "NOTCHED_12");
	
	/**
	 * Displays the bossbar split into 20 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
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
	private static int ordinal = 0;
	private static Map<String, Object> spongeStyles;
	private String spongeID;
	
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
		super(name, ordinal++);
		this.spongeID = spongeID;
	}
	
	/**
	 * Gets the bossbar style adapted for Bukkit environments.
	 * 
	 * <p>{@link #RANDOM} may only return a style that {@link #isSupported()}.</p>
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 * 
	 * @return Bukkit-adapted bossbar style
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public BarStyle bukkitValue() {
		if (Environment.isBukkit())
			return this == RANDOM ? BarStyle.values()[VersionUtils.getVersion().isAtLeast(Version.V1_9) ? ThreadLocalRandom.current().nextInt(BarStyle.values().length) : 4] : BarStyle.valueOf(name()); // .values()[4] = SOLID
		throw new UnsupportedOperationException("Unable to adapt bossbar style to a Bukkit's BarStyle on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the bossbar style adapted for Sponge environments.
	 * 
	 * <p>{@link #RANDOM} may only return a style that {@link #isSupported()}.</p>
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 * 
	 * @return Sponge-adapted bossbar style
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public BossBarOverlay spongeValue() {
		if (Environment.isSponge())
			return (BossBarOverlay) (this == RANDOM ? new ArrayList<>(spongeStyles.values()).get(VersionUtils.getVersion().isAtLeast(Version.V1_9) ? ThreadLocalRandom.current().nextInt(VALUES.length - 1) : 0) : spongeStyles.get(name())); // .values().get(0) = SOLID
		throw new UnsupportedOperationException("Unable to adapt bossbar style to a Sponge's BossBarOverlay on a " + Environment.getCurrent().getName() + " environment");
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
	 * Checks if this bossbar style is supported
	 * on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this bossbar style is supported
	 */
	public boolean isSupported() {
		return this == SOLID || this == RANDOM || VersionUtils.getVersion().isAtLeast(Version.V1_9);
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
	public static BossbarStyleAdapter valueOf(String name) {
		return valueOf(name, VALUES);
	}
	
	/**
	 * Equivalent of <code>values()</code>.
	 * 
	 * @return Pseudo-enum's constants
	 */
	public static BossbarStyleAdapter[] values() {
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
	public static BossbarStyleAdapter value(String name) {
		name = name.toUpperCase();
		
		for (BossbarStyleAdapter value : VALUES)
			if (value.name().equals(name) || value.getSpongeID().equals(name))
				return value;
		return null;
	}
	
}
