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
import net.minecraft.entity.boss.BossBar.Style;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric bossbar style adapter.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Bossbars#styles">ChatPlugin wiki/Modules/Bossbars/Styles</a>
 */
public class BossbarStyleAdapter extends PseudoEnum<BossbarStyleAdapter> {
	
	/**
	 * Displays the bossbar split into 6 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarStyleAdapter NOTCHED_6 = new BossbarStyleAdapter("NOTCHED_6", "SEGMENTED_6");
	
	/**
	 * Displays the bossbar split into 10 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarStyleAdapter NOTCHED_10 = new BossbarStyleAdapter("NOTCHED_10", "SEGMENTED_10");
	
	/**
	 * Displays the bossbar split into 12 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarStyleAdapter NOTCHED_12 = new BossbarStyleAdapter("NOTCHED_12", "SEGMENTED_12");
	
	/**
	 * Displays the bossbar split into 20 segments.
	 * 
	 * <p><strong>Minimum version:</strong> {@linkplain Version#V1_9 1.9}</p>
	 */
	public static final BossbarStyleAdapter NOTCHED_20 = new BossbarStyleAdapter("NOTCHED_20", "SEGMENTED_20");
	
	/**
	 * Displays the bossbar as a solid segment.
	 */
	public static final BossbarStyleAdapter PROGRESS = new BossbarStyleAdapter("PROGRESS", "SOLID");
	
	/**
	 * Displays a random style.
	 */
	public static final BossbarStyleAdapter RANDOM = new BossbarStyleAdapter("RANDOM", "RANDOM");
	
	private static final BossbarStyleAdapter[] VALUES = new BossbarStyleAdapter[] { NOTCHED_6, NOTCHED_10, NOTCHED_12, NOTCHED_20, PROGRESS, RANDOM };
	private static int ordinal = 0;
	private static Map<String, Object> spongeStyles;
	private String bukkitID;
	
	static { // TODO improve w/ reflection
		if (Environment.isSponge())
			spongeStyles = (Map<String, Object>) Stream.of(new Object[][] {
				{ "PROGRESS", BossBarOverlays.PROGRESS },
				{ "NOTCHED_6", BossBarOverlays.NOTCHED_6 },
				{ "NOTCHED_10", BossBarOverlays.NOTCHED_10 },
				{ "NOTCHED_12", BossBarOverlays.NOTCHED_12 },
				{ "NOTCHED_20", BossBarOverlays.NOTCHED_20 }
			}).collect(Collectors.toMap(color -> (String) color[0], color -> (Object) color[1], (x, y) -> y, LinkedHashMap::new));
	}
	
	private BossbarStyleAdapter(String name, String bukkitID) {
		super(name, ordinal++);
		this.bukkitID = bukkitID;
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
			return this == RANDOM ? BarStyle.values()[VersionUtils.getVersion().isAtLeast(Version.V1_9) ? ThreadLocalRandom.current().nextInt(BarStyle.values().length) : 4] : BarStyle.valueOf(bukkitID);
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
			return (BossBarOverlay) (this == RANDOM ? new ArrayList<>(spongeStyles.values()).get(VersionUtils.getVersion().isAtLeast(Version.V1_9) ? ThreadLocalRandom.current().nextInt(VALUES.length - 1) : 0) : spongeStyles.get(name()));
		throw new UnsupportedOperationException("Unable to adapt bossbar style to a Sponge's BossBarOverlay on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the bossbar style adapted for Fabric environments.
	 * 
	 * @return Fabric-adapted bossbar style
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public Style fabricValue() {
		if (Environment.isFabric())
			return this == RANDOM ? Style.values()[ThreadLocalRandom.current().nextInt(Style.values().length)] : Style.valueOf(name());
		throw new UnsupportedOperationException("Unable to adapt bossbar style to a Fabric's BossBar.Style on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the {@link org.bukkit.boss.BarStyle}-compatible ID.
	 * 
	 * @return Bukkit-compatible ID
	 */
	public String getBukkitID() {
		return bukkitID;
	}
	
	/**
	 * Checks if this bossbar style is supported
	 * on {@link VersionUtils#getVersion()}.
	 * 
	 * @return Whether this bossbar style is supported
	 */
	public boolean isSupported() {
		return Environment.isFabric() || this == PROGRESS || this == RANDOM || VersionUtils.getVersion().isAtLeast(Version.V1_9);
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
	public static BossbarStyleAdapter value(String name) {
		name = name.toUpperCase();
		
		for (BossbarStyleAdapter value : VALUES)
			if (value.name().equals(name) || value.getBukkitID().equals(name))
				return value;
		return null;
	}
	
}
