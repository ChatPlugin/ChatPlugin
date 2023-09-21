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

package me.remigio07.chatplugin.api.common.storage;

/**
 * Represents the supported storage methods.
 */
public enum StorageMethod {
	
	/**
	 * H2 database storage method. Default setting; recommended over SQLite.
	 */
	H2("H2"),
	
	/**
	 * SQLite database storage method.
	 */
	SQLITE("SQLite"),
	
	/**
	 * MySQL database storage method.
	 */
	MYSQL("MySQL"),
	
	/**
	 * YAML flat-file storage method.
	 */
	YAML("YAML"),
	
	/**
	 * JSON flat-file storage method.
	 */
	JSON("JSON");
	
	private String name;
	
	private StorageMethod(String name) {
		this.name = name;
	}
	
	/**
	 * Gets this storage method's name.
	 * 
	 * <p><strong>Example:</strong> {@link #SQLITE} -&gt; "SQLite"</p>
	 * 
	 * @return Storage method's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Checks if this is a database storage method.
	 * 
	 * @return Whether this is a database method
	 */
	public boolean isDatabase() {
		return !isFlatFile();
	}
	
	/**
	 * Checks if this is a flat-file storage method.
	 * 
	 * @return Whether this is a flat-file method
	 */
	public boolean isFlatFile() {
		return this == YAML || this == JSON;
	}
	
}
