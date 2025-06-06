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

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;

/**
 * Represents a major, minor or patch change in a version (example: 1.8.0 -&gt; 1.9.0).
 */
public enum VersionChange {
	
	/**
	 * Represents a major version upgrade (X.x.x).
	 */
	MAJOR_UPGRADE(0),
	
	/**
	 * Represents a major version downgrade (X.x.x).
	 */
	MAJOR_DOWNGRADE(0),
	
	/**
	 * Represents a minor version upgrade (x.X.x).
	 */
	MINOR_UPGRADE(1),
	
	/**
	 * Represents a minor version downgrade (x.X.x).
	 */
	MINOR_DOWNGRADE(1),
	
	/**
	 * Represents a patch version upgrade (x.x.X).
	 */
	PATCH_UPGRADE(2),
	
	/**
	 * Represents a patch version downgrade (x.x.X).
	 */
	PATCH_DOWNGRADE(2),
	
	/**
	 * Represents a null version change (x.x.x).
	 */
	NULL(-1);
	
	private int index;
	
	private VersionChange(int index) {
		this.index = index;
	}
	
	/**
	 * Gets this version change's index. Example: x.x.X ➝ 2.
	 * 
	 * @return Version change's index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Checks if this version change is supported.
	 * 
	 * @return Whether this version change is supported
	 */
	public boolean isSupported() {
		return !name().contains("DOWNGRADE");
	}
	
	/**
	 * Checks if this is a major (X.x.x) version change.
	 * 
	 * @return Whether this is a major version change
	 */
	public boolean isMajor() {
		return index == 0;
	}
	
	/**
	 * Checks if this is a minor (x.X.x) version change.
	 * 
	 * @return Whether this is a minor version change
	 */
	public boolean isMinor() {
		return index == 1;
	}
	
	/**
	 * Checks if this is a patch (x.x.X) version change.
	 * 
	 * @return Whether this is a patch version change
	 */
	public boolean isPatch() {
		return index == 2;
	}
	
	/**
	 * Gets the version change by comparing
	 * the specified version to another one.
	 * 
	 * <p>Will return {@link #NULL} if the two versions match.
	 * Modifiers (like "-SNAPSHOT") will not be considered.</p>
	 * 
	 * @param version Version to compare
	 * @param newVersion New version to compare
	 * @return Current version change
	 * @throws IndexOutOfBoundsException If specified versions do not follow <a href="https://semver.org/">Semantic Versioning</a>
	 * @throws NumberFormatException If specified versions do not follow <a href="https://semver.org/">Semantic Versioning</a>
	 */
	public static VersionChange getVersionChange(String version, String newVersion) {
		if (version.contains("-"))
			version = version.substring(0, version.indexOf('-'));
		if (newVersion.contains("-"))
			newVersion = newVersion.substring(0, newVersion.indexOf('-'));
		if (!version.equals(newVersion)) {
			String[] numbers = version.split("\\.");
			String[] newNumbers = newVersion.split("\\.");
			
			for (int i = 0; i < 3; i++)
				if (!numbers[i].equals(newNumbers[i]))
					return getVersionChange(i, Integer.valueOf(newNumbers[i]) > Integer.valueOf(numbers[i]));
		} return NULL;
	}
	
	private static VersionChange getVersionChange(int index, boolean upgrade) {
		for (VersionChange versionChange : values())
			if (index == versionChange.getIndex() && (upgrade ? versionChange.isSupported() : !versionChange.isSupported()))
				return versionChange;
		return NULL;
	}
	
	/**
	 * Gets the version change by comparing the version
	 * in the specified configuration to another one.
	 * 
	 * <p>Will return {@link #NULL} if the two versions match.
	 * Modifiers (like "-SNAPSHOT") will not be considered.</p>
	 * 
	 * @param configuration Configuration to check
	 * @param path Path containing the version to compare
	 * @param newVersion New version to compare
	 * @return Current version change
	 * @throws IndexOutOfBoundsException If specified versions do not follow <a href="https://semver.org/">Semantic Versioning</a>
	 * @throws NumberFormatException If specified versions do not follow <a href="https://semver.org/">Semantic Versioning</a>
	 */
	@NotNull
	public static VersionChange getVersionChange(Configuration configuration, String path, String newVersion) {
		return getVersionChange(configuration.getString(path, "0.0.1"), newVersion); // according to SemVer, 0.1.0 should be a program's first version, but here we use 0.0.1 as default just in case
	}
	
}
