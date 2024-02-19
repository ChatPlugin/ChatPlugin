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

package me.remigio07.chatplugin.common.storage.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseConnector;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.bootstrap.IsolatedClassLoader;
import me.remigio07.chatplugin.common.util.LibrariesUtils;

public class H2Connector extends DatabaseConnector {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		
		try {
			LibrariesUtils.load(Library.H2_DATABASE_ENGINE);
			
			boolean serverMode = ConfigurationType.CONFIG.get().getBoolean("storage.database.use-server-mode");
			
			if (serverMode)
				LogManager.log("H2 is selected as storage method with the server mode (\"storage.database.use-server-mode\" in config.yml) enabled. This may require extra time to start the database if no connections are open.", 0);
			connection = (Connection) IsolatedClassLoader.getInstance().loadClass("org.h2.jdbc.JdbcConnection").getConstructor(String.class, Properties.class, String.class, Object.class, boolean.class).newInstance(
					"jdbc:h2:file:"
					+ DatabaseManager.getInstance().getFolder().getAbsolutePath()
					+ File.separator + ConfigurationType.CONFIG.get().getString("storage.database.file-name")
					+ ";LOCK_MODE=2;AUTO_RECONNECT=TRUE;MODE=MySQL" + (serverMode ? ";AUTO_SERVER=TRUE" : ""),
					new Properties(),
					null,
					null,
					false
					);
			
			executeUpdate("SET IGNORECASE TRUE");
		} catch (Exception e) {
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
					+ "`id` INT PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36), "
					+ "`player_name` VARCHAR(16), "
					+ "`player_ip` VARCHAR(15), "
					+ "`staff_member` VARCHAR(16) NOT NULL, "
					+ "`who_unbanned` VARCHAR(16), "
					+ "`reason` VARCHAR(255), "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`type` ENUM('ACCOUNT', 'IP') NOT NULL, "
					+ "`date` BIGINT NOT NULL, "
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
					+ "`id` INT PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36) NOT NULL, "
					+ "`player_name` VARCHAR(16) NOT NULL, "
					+ "`staff_member` VARCHAR(16) NOT NULL, "
					+ "`who_unwarned` VARCHAR(16), "
					+ "`reason` VARCHAR(255), "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`date` BIGINT NOT NULL, "
					+ "`unwarn_date` BIGINT UNSIGNED, "
					+ "`duration` BIGINT NOT NULL, "
					+ "`global` BOOLEAN DEFAULT FALSE, "
					+ "`silent` BOOLEAN DEFAULT FALSE, "
					+ "`active` BOOLEAN DEFAULT TRUE, "
					+ "`unwarned` BOOLEAN DEFAULT FALSE"
					+ ")";
			break;
		case KICKS:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INT PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36) NOT NULL, "
					+ "`player_name` VARCHAR(16) NOT NULL, "
					+ "`player_ip` VARCHAR(15) NOT NULL, "
					+ "`staff_member` VARCHAR(16) NOT NULL, "
					+ "`reason` VARCHAR(255), "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`type` ENUM('KICK', 'CHAT') NOT NULL, "
					+ "`date` BIGINT NOT NULL, "
					+ "`silent` BOOLEAN DEFAULT FALSE"
					+ ")";
			break;
		case MUTES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INT PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36) NOT NULL, "
					+ "`player_name` VARCHAR(16) NOT NULL, "
					+ "`staff_member` VARCHAR(16) NOT NULL, "
					+ "`who_unmuted` VARCHAR(16), "
					+ "`reason` VARCHAR(255), "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`date` BIGINT NOT NULL, "
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
					+ "`id` INT PRIMARY KEY AUTO_INCREMENT, "
					+ "`player_uuid` VARCHAR(36) NOT NULL, "
					+ "`player_name` VARCHAR(16) NOT NULL, "
					+ "`player_ip` VARCHAR(15), "
					+ "`language` VARCHAR(32), "
					+ "`last_logout` BIGINT DEFAULT 0, "
					+ "`time_played` BIGINT DEFAULT 0, "
					+ "`messages_sent` MEDIUMINT DEFAULT 0, "
					+ "`bans` SMALLINT DEFAULT 0, "
					+ "`warnings` SMALLINT DEFAULT 0, "
					+ "`kicks` SMALLINT DEFAULT 0, "
					+ "`mutes` SMALLINT DEFAULT 0, "
					+ "`ignored_players` VARCHAR(255)"
					+ ")";
			break;
		case CHAT_MESSAGES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`sender_uuid` VARCHAR(36) NOT NULL, "
					+ "`sender_name` VARCHAR(16) NOT NULL, "
					+ "`rank_id` VARCHAR(14) NOT NULL, "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`world` VARCHAR(248) NOT NULL, "
					+ "`content` VARCHAR(256) NOT NULL, "
					+ "`date` BIGINT NOT NULL, "
					+ "`deny_chat_reason` ENUM('CAPS', 'FLOOD', 'FORMAT', 'IP_ADDRESS', 'MUTE', 'MUTEALL', 'SPAM', 'SWEAR', 'URL', 'VANISH')"
					+ ")";
			break;
		case PRIVATE_MESSAGES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`sender_uuid` VARCHAR(36) NOT NULL, "
					+ "`sender_name` VARCHAR(16) NOT NULL, "
					+ "`recipient_uuid` VARCHAR(36) NOT NULL, "
					+ "`recipient_name` VARCHAR(16) NOT NULL, "
					+ "`rank_id` VARCHAR(14) NOT NULL, "
					+ "`server` VARCHAR(36) NOT NULL, "
					+ "`world` VARCHAR(248) NOT NULL, "
					+ "`content` VARCHAR(256) NOT NULL, "
					+ "`date` BIGINT NOT NULL, "
					+ "`deny_chat_reason` ENUM('CAPS', 'FLOOD', 'IP_ADDRESS', 'SPAM', 'SWEAR', 'URL')"
					+ ")";
			break;
		case IP_ADDRESSES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`player_id` INT PRIMARY KEY NOT NULL, "
					+ "`ip_addresses` VARCHAR(255) NOT NULL"
					+ ")";
			break;
		} executeUpdate(update.replace("{table_id}", container.getDatabaseTableID()));
	}
	
	@Override
	public int getNextID(DataContainer table) throws SQLException {
		if (table == DataContainer.CHAT_MESSAGES || table == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to get next ID in table " + table.getDatabaseTableID() + " since that table does not have IDs");
		if (table == DataContainer.IP_ADDRESSES)
			table = DataContainer.PLAYERS;
		Number id = get("SELECT identity_base FROM information_schema.columns WHERE table_name = '" + table.getDatabaseTableID() + "' AND column_name = ?", "identity_base", Number.class, table.getIDColumn());
		return id == null ? 1 : id.intValue();
	}
	
	@Override
	public String getEngineName() {
		return Library.H2_DATABASE_ENGINE.getName();
	}
	
	@Override
	public String getEngineVersion() throws SQLException {
		return get("SELECT setting_value FROM information_schema.settings WHERE setting_name = ?", "setting_value", String.class, "info.VERSION");
	}
	
}
