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

package me.remigio07.chatplugin.api.common.util;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;

/**
 * Represents a major, minor or patch change between two
 * <a href="https://semver.org/">Semantic Versioning</a>-compliant
 * versions (example: 1.8.0 ➝ 1.9.0).
 */
public enum VersionChange {
	
	/**
	 * Represents a null version change (example: 2.5.1 ➝ 2.5.1).
	 */
	NULL,
	
	/**
	 * Represents a major version upgrade (example: 1.x.x ➝ 4.x.x).
	 */
	MAJOR_UPGRADE,
	
	/**
	 * Represents a major version downgrade (example: 4.x.x ➝ 1.x.x).
	 */
	MAJOR_DOWNGRADE,
	
	/**
	 * Represents a minor version upgrade (example: 1.7.x ➝ 1.9.x).
	 */
	MINOR_UPGRADE,
	
	/**
	 * Represents a minor version downgrade (example: 1.9.x ➝ 1.7.x).
	 */
	MINOR_DOWNGRADE,
	
	/**
	 * Represents a patch version upgrade (example: 1.4.6 ➝ 1.4.7).
	 */
	PATCH_UPGRADE,
	
	/**
	 * Represents a patch version downgrade (example: 1.4.7 ➝ 1.4.6).
	 */
	PATCH_DOWNGRADE,
	
	/**
	 * Represents a snapshot version upgrade (example: 7.8.3-SNAPSHOT ➝ 7.8.3)
	 */
	SNAPSHOT_UPGRADE,
	
	/**
	 * Represents a snapshot version downgrade (example: 7.8.3 ➝ 7.8.3-SNAPSHOT)
	 */
	SNAPSHOT_DOWNGRADE;
	
	/**
	 * Gets this version change's index. Example: x.x.X ➝ 2.
	 * 
	 * <p>Will return -1 if <code>this == {@link #NULL}</code>.</p>
	 * 
	 * @return Version change's index
	 */
	public int getIndex() {
		return (ordinal() + 1) / 2 - 1;
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
		return getIndex() == 0;
	}
	
	/**
	 * Checks if this is a minor (x.X.x) version change.
	 * 
	 * @return Whether this is a minor version change
	 */
	public boolean isMinor() {
		return getIndex() == 1;
	}
	
	/**
	 * Checks if this is a patch (x.x.X) version change.
	 * 
	 * @return Whether this is a patch version change
	 */
	public boolean isPatch() {
		return getIndex() == 2;
	}
	
	/**
	 * Checks if this is a snapshot (x.x.x-SNAPSHOT) version change.
	 * 
	 * @return Whether this is a snapshot version change
	 */
	public boolean isSnapshot() {
		return getIndex() == 3;
	}
	
	/**
	 * Gets the version change by comparing
	 * the specified version to another one.
	 * 
	 * <p>Will return {@link #NULL} if the two versions match. "-SNAPSHOT" is the
	 * only supported <a href="https://semver.org/#spec-item-9">identifier</a>.</p>
	 * 
	 * @param version Version to compare
	 * @param newVersion New version to compare
	 * @return Current version change
	 * @throws IndexOutOfBoundsException If specified versions do not follow <a href="https://semver.org/">Semantic Versioning</a>
	 * @throws NumberFormatException If specified versions do not follow <a href="https://semver.org/">Semantic Versioning</a>
	 */
	public static VersionChange getVersionChange(String version, String newVersion) {
		if (!version.equals(newVersion)) {
			boolean snapshotUpgrade = false;
			
			if (version.endsWith("-SNAPSHOT")) {
				version = version.substring(0, version.lastIndexOf('-'));
				snapshotUpgrade = true;
			} if (newVersion.endsWith("-SNAPSHOT"))
				newVersion = newVersion.substring(0, newVersion.lastIndexOf('-'));
			String[] numbers = version.split("\\.");
			String[] newNumbers = newVersion.split("\\.");
			
			for (int i = 0; i < 3; i++)
				if (!numbers[i].equals(newNumbers[i]))
					return getVersionChange(i, Integer.valueOf(newNumbers[i]) > Integer.valueOf(numbers[i]));
			return snapshotUpgrade ? SNAPSHOT_UPGRADE : SNAPSHOT_DOWNGRADE;
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
	 * <p>Will return {@link #NULL} if the two versions match. "-SNAPSHOT" is the
	 * only supported <a href="https://semver.org/#spec-item-9">identifier</a>.</p>
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
