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

package me.remigio07_.chatplugin.common.storage.database;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.remigio07_.chatplugin.api.common.storage.DataContainer;
import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.storage.database.DatabaseConnector;
import me.remigio07_.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07_.chatplugin.api.common.util.Library;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.common.util.LibrariesUtils;

public class H2Connector extends DatabaseConnector {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		
		try {
			LibrariesUtils.load(Library.H2_DRIVER);
			Class.forName(Library.PREFIX + "org.h2.Driver").getDeclaredMethod("load").invoke(null);
			
			boolean serverMode = ConfigurationType.CONFIG.get().getBoolean("storage.database.use-server-mode");
			
			if (serverMode)
				LogManager.log("H2 is selected as storage method with the server mode (\"storage.database.use-server-mode\" in config.yml) enabled. This may require extra time to start the database if no connections are open.", 0);
			connection = DriverManager.getConnection(
					"jdbc:h2:file:"
					+ DatabaseManager.getInstance().getFolder().getAbsolutePath()
					+ File.separator + ConfigurationType.CONFIG.get().getString("storage.database.file-name")
					+ ";LOCK_MODE=2;AUTO_RECONNECT=TRUE;MODE=MySQL" + (serverMode ? ";AUTO_SERVER=TRUE" : ""),
					"sa",
					""
					);
			
			executeUpdate("SET IGNORECASE TRUE");
		} catch (ClassNotFoundException e) {
			LogManager.log("Unable to find the H2 JDBC class. ChatPlugin will disable.", 2);
			throw new ChatPluginManagerException(DatabaseManager.getInstance(), e);
		} catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
			LogManager.log("Unable to load the H2 database using the current driver.", 2);
			throw new ChatPluginManagerException(DatabaseManager.getInstance(), e);
		} catch (SQLException e) {
			LogManager.log("Unable to access the H2 database.", 2);
			throw new ChatPluginManagerException(DatabaseManager.getInstance(), e);
		} catch (Exception e) {
			LogManager.log("Unable to load the H2 driver's JAR file.", 2);
			throw new ChatPluginManagerException(DatabaseManager.getInstance(), e);
		}
	}
	
	@Override
	public List<DataContainer> getMissingDataContainers() throws SQLException {
		List<DataContainer> tables = new ArrayList<>(Arrays.asList(DataContainer.values()));
		
		try (PreparedStatement statement = prepareStatement("SHOW TABLES")) {
			statement.execute();
			
			ResultSet result = statement.getResultSet();
			
			while (result.next()) {
				DataContainer table = DataContainer.getDataContainer(result.getString(1));
				
				if (table != null)
					tables.remove(table);
			} return tables;
		}
	}
	
	@Override
	public void createDataContainer(DataContainer container) throws SQLException, IOException {
		String update = null;
		
		LogManager.log("Creating default database table \"" + container.getDatabaseTableID() + "\"...", 0);
		
		switch (container) {
		case BANS:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36), "
					+ "`player_name` VARCHAR(16), "
					+ "`player_ip` VARCHAR(15), "
					+ "`staff_member` VARCHAR(16) NOT NULL, "
					+ "`who_unbanned` VARCHAR(16), "
					+ "`reason` VARCHAR(255), "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`type` ENUM('ACCOUNT', 'IP') NOT NULL, "
					+ "`date` BIGINT UNSIGNED NOT NULL, "
					+ "`unban_date` BIGINT UNSIGNED, "
					+ "`duration` BIGINT UNSIGNED, "
					+ "`global` BOOLEAN DEFAULT FALSE, "
					+ "`silent` BOOLEAN DEFAULT FALSE, "
					+ "`active` BOOLEAN DEFAULT TRUE, "
					+ "`unbanned` BOOLEAN DEFAULT FALSE"
					+ ")";
			break;
		case WARNINGS:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36) NOT NULL, "
					+ "`player_name` VARCHAR(16) NOT NULL, "
					+ "`staff_member` VARCHAR(16) NOT NULL, "
					+ "`who_unwarned` VARCHAR(16), "
					+ "`reason` VARCHAR(255), "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`date` BIGINT UNSIGNED NOT NULL, "
					+ "`unwarn_date` BIGINT UNSIGNED, "
					+ "`duration` BIGINT UNSIGNED NOT NULL, "
					+ "`global` BOOLEAN DEFAULT FALSE, "
					+ "`silent` BOOLEAN DEFAULT FALSE, "
					+ "`active` BOOLEAN DEFAULT TRUE, "
					+ "`unwarned` BOOLEAN DEFAULT FALSE"
					+ ")";
			break;
		case KICKS:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36) NOT NULL, "
					+ "`player_name` VARCHAR(16) NOT NULL, "
					+ "`player_ip` VARCHAR(15) NOT NULL, "
					+ "`staff_member` VARCHAR(16) NOT NULL, "
					+ "`reason` VARCHAR(255), "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`type` ENUM('KICK', 'CHAT') NOT NULL, "
					+ "`date` BIGINT UNSIGNED NOT NULL, "
					+ "`silent` BOOLEAN DEFAULT FALSE"
					+ ")";
			break;
		case MUTES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36) NOT NULL, "
					+ "`player_name` VARCHAR(16) NOT NULL, "
					+ "`staff_member` VARCHAR(16) NOT NULL, "
					+ "`who_unmuted` VARCHAR(16), "
					+ "`reason` VARCHAR(255), "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`date` BIGINT UNSIGNED NOT NULL, "
					+ "`unmute_date` BIGINT UNSIGNED, "
					+ "`duration` BIGINT UNSIGNED, "
					+ "`global` BOOLEAN DEFAULT FALSE, "
					+ "`silent` BOOLEAN DEFAULT FALSE, "
					+ "`active` BOOLEAN DEFAULT TRUE, "
					+ "`unmuted` BOOLEAN DEFAULT FALSE"
					+ ")";
			break;
		case PLAYERS:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INT UNSIGNED PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36) NOT NULL, "
					+ "`player_name` VARCHAR(16) NOT NULL, "
					+ "`player_ip` VARCHAR(15), "
					+ "`language` VARCHAR(32), "
					+ "`last_logout` BIGINT UNSIGNED DEFAULT 0, "
					+ "`time_played` BIGINT UNSIGNED DEFAULT 0, "
					+ "`messages_sent` MEDIUMINT UNSIGNED DEFAULT 0, "
					+ "`bans` SMALLINT UNSIGNED DEFAULT 0, "
					+ "`warnings` SMALLINT UNSIGNED DEFAULT 0, "
					+ "`kicks` SMALLINT UNSIGNED DEFAULT 0, "
					+ "`mutes` SMALLINT UNSIGNED DEFAULT 0"
					+ ")";
			break;
		case MESSAGES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`player_uuid` VARCHAR(36) NOT NULL, "
					+ "`player_name` VARCHAR(16) NOT NULL, "
					+ "`rank` VARCHAR(14) NOT NULL, "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`world` VARCHAR(255) NOT NULL, "
					+ "`message` VARCHAR(256) NOT NULL, "
					+ "`date` BIGINT UNSIGNED NOT NULL, "
					+ "`deny_chat_reason` ENUM('CAPS', 'FLOOD', 'FORMAT', 'IP_ADDRESS', 'MUTE', 'MUTEALL', 'SPAM', 'SWEAR', 'URL', 'VANISH')"
					+ ")";
			break;
		case IP_ADDRESSES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`player_id` INT UNSIGNED PRIMARY KEY NOT NULL, "
					+ "`ip_addresses` VARCHAR(255) NOT NULL"
					+ ")";
			break;
		} executeUpdate(update.replace("{table_id}", container.getDatabaseTableID()));
	}
	
	@Override
	public String getEngineVersion() throws SQLException {
		return get("SELECT value FROM information_schema.settings WHERE name = ?", "value", String.class, "info.VERSION");
	}
	
}
