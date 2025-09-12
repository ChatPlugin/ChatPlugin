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

package me.remigio07.chatplugin.api.common.storage.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.remigio07.chatplugin.api.common.storage.DataContainer;
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
	
	// compatibility with older ChatPlugin versions
	protected void ensureCompatibility() throws SQLException {
		try (PreparedStatement statement = getConnector().getConnection().prepareStatement("SHOW COLUMNS FROM " + DataContainer.PUBLIC_MESSAGES.getDatabaseTableID())) {
			statement.execute();
			
			try (ResultSet result = statement.getResultSet()) {
				String columnName = null;
				
				while (result.next())
					columnName = result.getString("FIELD");
				if ("global".equalsIgnoreCase(columnName)) {
					getConnector().executeUpdate("ALTER TABLE " + DataContainer.PRIVATE_MESSAGES.getDatabaseTableID() + " MODIFY COLUMN `rank_id` VARCHAR(36) NOT NULL");
					getConnector().executeUpdate("ALTER TABLE " + DataContainer.PUBLIC_MESSAGES.getDatabaseTableID() + " MODIFY COLUMN `rank_id` VARCHAR(36) NOT NULL");
					getConnector().executeUpdate("ALTER TABLE " + DataContainer.PUBLIC_MESSAGES.getDatabaseTableID() + " ADD `channel_id` VARCHAR(36) AFTER `world`");
					getConnector().executeUpdate("ALTER TABLE " + DataContainer.PUBLIC_MESSAGES.getDatabaseTableID() + " DROP COLUMN `global`");
				}
			}
		}
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
