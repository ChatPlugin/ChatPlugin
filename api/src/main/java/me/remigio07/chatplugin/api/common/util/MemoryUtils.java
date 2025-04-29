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

package me.remigio07.chatplugin.api.common.util;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;

/**
 * Util class that contains methods to convert memory units of measure.
 */
public enum MemoryUtils {
	
	/**
	 * Represents a terabyte (1024 * 1024 * 1024 * 1024 bytes).
	 */
	TERABYTE(1024 * 1024 * 1024 * 1024, "TB"),
	
	/**
	 * Represents a gigabyte (1024 * 1024 * 1024 bytes).
	 */
	GIGABYTE(1024 * 1024 * 1024, "GB"),
	
	/**
	 * Represents a megabyte (1024 * 1024 bytes).
	 */
	MEGABYTE(1024 * 1024, "MB"),
	
	/**
	 * Represents a kilobyte (1024 bytes).
	 */
	KILOBYTE(1024, "KB"),
	
	/**
	 * Represents a byte.
	 */
	BYTE(1, "B");
	
	private int toBytesRatio;
	private String unit;
	
	private MemoryUtils(int toBytesRatio, String unit) {
		this.toBytesRatio = toBytesRatio;
		this.unit = unit;
	}
	
	/**
	 * Gets this unit's to bytes ratio.
	 * 
	 * @return To bytes ratio
	 */
	public int getToBytesRatio() {
		return toBytesRatio;
	}
	
	/**
	 * Gets this unit's string representation.
	 * 
	 * <p><strong>Example:</strong> {@link #MEGABYTE} ➝ "MB"</p>
	 * 
	 * @return Unit's string representation
	 */
	public String getUnit() {
		return unit;
	}
	
	/**
	 * Gets the default displayed memory unit.
	 * 
	 * <p><strong>Found at:</strong> "settings.displayed-memory.unit" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Displayed memory unit
	 */
	public static MemoryUtils getDisplayedUnit() {
		return MemoryUtils.valueOf(ConfigurationType.CONFIG.get().getString("settings.displayed-memory.unit", "MEGABYTE"));
	}
	
	/**
	 * Formats the given bytes amount using {@link #getDisplayedUnit()}
	 * and truncating to the decimals places specified at
	 * "settings.displayed-memory.decimals" in {@link ConfigurationType#CONFIG}.
	 * 
	 * @param bytes Bytes amount
	 * @return Formatted memory
	 */
	public static String formatMemory(long bytes) {
		int decimals = ConfigurationType.CONFIG.get().getInt("settings.displayed-memory.decimals");
		double adjusted = (double) bytes / getDisplayedUnit().getToBytesRatio();
		return decimals == 0 ? String.valueOf((long) adjusted) : String.valueOf(Utils.truncate(adjusted, decimals));
	}
	
	/**
	 * Formats the given bytes amount using the specified scale.
	 * 
	 * @param bytes Bytes amount
	 * @param scale Scale to use
	 * @return Formatted memory
	 */
	public static String formatMemory(long bytes, MemoryUtils scale) {
		double adjusted = (double) bytes / scale.getToBytesRatio();
		return Double.valueOf(Utils.truncate(adjusted, 2)) / (long) adjusted == 1 ? String.valueOf((long) adjusted) : String.valueOf(Utils.truncate(adjusted, 2));
	}
	
}
