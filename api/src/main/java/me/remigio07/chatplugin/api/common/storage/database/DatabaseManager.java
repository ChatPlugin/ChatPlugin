/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.api.common.storage.database;

import me.remigio07.chatplugin.api.common.storage.StorageManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;

/**
 * Manager that handles connections to the database.
 * 
 * @see DatabaseConnector
 */
public abstract class DatabaseManager extends StorageManager {
	
	@Override
	public DatabaseConnector getConnector() {
		return (DatabaseConnector) connector;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static DatabaseManager getInstance() {
		return (DatabaseManager) instance;
	}
	
	/**
	 * Gets the database's table prefix.
	 * 
	 * <p><strong>Found at:</strong> "storage.database.table-prefix" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Database's table prefix
	 */
	public static String getTablePrefix() {
		return ConfigurationType.CONFIG.get().getString("storage.database.table-prefix");
	}
	
}
