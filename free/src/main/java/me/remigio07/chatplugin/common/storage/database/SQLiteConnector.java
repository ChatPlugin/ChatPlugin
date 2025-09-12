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

package me.remigio07.chatplugin.common.storage.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.StorageManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseConnector;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.bootstrap.IsolatedClassLoader;
import me.remigio07.chatplugin.common.util.LibrariesUtils;

public class SQLiteConnector extends DatabaseConnector {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long serverModeTask = -1;
		
		try {
			LibrariesUtils.load(Library.SQLITE_JDBC);
			
			String file = StorageManager.getInstance().getFolder().resolve(ConfigurationType.CONFIG.get().getString("storage.database.file-name") + ".db").toAbsolutePath().toString();
			boolean serverMode = ConfigurationType.CONFIG.get().getBoolean("storage.database.use-server-mode");
			
			if (serverMode)
				serverModeTask = TaskManager.runAsync(() -> {
					if (connection == null)
						LogManager.log("SQLite is selected as storage method with the server mode (\"storage.database.use-server-mode\" in config.yml) enabled. This may require extra time to start the database if no connections are open.", 0);
				}, 1000L);
			connection = (Connection) IsolatedClassLoader.getInstance().loadClass("org.sqlite.jdbc4.JDBC4Connection").getConstructor(String.class, String.class, Properties.class).newInstance(
					"jdbc:sqlite:" + file + (serverMode ? "?cache=shared" : ""),
					file,
					new Properties()
					);
			
			if (serverMode)
				prepareStatement("PRAGMA journal_mode=WAL").executeQuery().close();
		} catch (Exception e) {
			TaskManager.cancelAsync(serverModeTask);
			throw new ChatPluginManagerException(DatabaseManager.getInstance(), e);
		}
	}
	
	@Override
	public Set<DataContainer> getMissingDataContainers() throws SQLException {
		Set<DataContainer> tables = EnumSet.allOf(DataContainer.class);
		
		try (
				PreparedStatement statement = prepareStatement("SELECT name FROM sqlite_schema WHERE type = 'table'");
				ResultSet result = statement.executeQuery();
				) {
			while (result.next())
				Optional.ofNullable(DataContainer.getDataContainer(result.getString(1))).ifPresent(tables::remove);
			return tables;
		}
	}
	
	@Override
	public void createDataContainer(DataContainer container) throws SQLException, IOException {
		String update = null;
		
		LogManager.log("Creating default database table \"{0}\"...", 0, container.getDatabaseTableID());
		
		switch (container) {
		case BANS:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "`player_uuid` TEXT, "
					+ "`player_name` TEXT, "
					+ "`player_ip` TEXT, "
					+ "`staff_member` TEXT NOT NULL, "
					+ "`who_unbanned` TEXT, "
					+ "`reason` TEXT, "
					+ "`server` TEXT NOT NULL, "
					+ "`type` NUMERIC NOT NULL, "
					+ "`date` INTEGER NOT NULL, "
					+ "`unban_date` INTEGER, "
					+ "`duration` INTEGER, "
					+ "`global` NUMERIC DEFAULT FALSE, "
					+ "`silent` NUMERIC DEFAULT FALSE, "
					+ "`active` NUMERIC DEFAULT TRUE, "
					+ "`unbanned` NUMERIC DEFAULT FALSE"
					+ ")";
			break;
		case WARNINGS:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "`player_uuid` TEXT NOT NULL, "
					+ "`player_name` TEXT NOT NULL, "
					+ "`staff_member` TEXT NOT NULL, "
					+ "`who_unwarned` TEXT, "
					+ "`reason` TEXT, "
					+ "`server` TEXT NOT NULL, "
					+ "`date` INTEGER NOT NULL, "
					+ "`unwarn_date` INTEGER, "
					+ "`duration` INTEGER NOT NULL, "
					+ "`global` NUMERIC DEFAULT FALSE, "
					+ "`silent` NUMERIC DEFAULT FALSE, "
					+ "`active` NUMERIC DEFAULT TRUE, "
					+ "`unwarned` NUMERIC DEFAULT FALSE "
					+ ")";
			break;
		case KICKS:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "`player_uuid` TEXT NOT NULL, "
					+ "`player_name` TEXT NOT NULL, "
					+ "`player_ip` TEXT NOT NULL, "
					+ "`staff_member` TEXT NOT NULL, "
					+ "`reason` TEXT, "
					+ "`server` TEXT NOT NULL, "
					+ "`type` NUMERIC NOT NULL, "
					+ "`date` INTEGER NOT NULL, "
					+ "`silent` NUMERIC DEFAULT FALSE"
					+ ")";
			break;
		case MUTES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "`player_uuid` TEXT NOT NULL, "
					+ "`player_name` TEXT NOT NULL, "
					+ "`staff_member` TEXT NOT NULL, "
					+ "`who_unmuted` TEXT, "
					+ "`reason` TEXT, "
					+ "`server` TEXT NOT NULL, "
					+ "`date` INTEGER NOT NULL, "
					+ "`unmute_date` INTEGER, "
					+ "`duration` INTEGER, "
					+ "`global` NUMERIC DEFAULT FALSE, "
					+ "`silent` NUMERIC DEFAULT FALSE, "
					+ "`active` NUMERIC DEFAULT TRUE, "
					+ "`unmuted` NUMERIC DEFAULT FALSE"
					+ ")";
			break;
		case PLAYERS:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`id` INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "`player_uuid` TEXT NOT NULL, "
					+ "`player_name` TEXT NOT NULL, "
					+ "`player_ip` TEXT, "
					+ "`language` TEXT, "
					+ "`ignored_players` TEXT, "
					+ "`chat_color` INTEGER, "
					+ "`emojis_tone` INTEGER, "
					+ "`last_logout` INTEGER, "
					+ "`time_played` INTEGER DEFAULT 0, "
					+ "`messages_sent` INTEGER DEFAULT 0, "
					+ "`antispam_infractions` INTEGER DEFAULT 0, "
					+ "`bans` INTEGER DEFAULT 0, "
					+ "`warnings` INTEGER DEFAULT 0, "
					+ "`kicks` INTEGER DEFAULT 0, "
					+ "`mutes` INTEGER DEFAULT 0"
					+ ")";
			break;
		case PUBLIC_MESSAGES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`sender_uuid` TEXT NOT NULL, "
					+ "`sender_name` TEXT NOT NULL, "
					+ "`rank_id` TEXT NOT NULL, "
					+ "`server` TEXT NOT NULL, "
					+ "`world` TEXT NOT NULL, "
					+ "`channel_id` TEXT, "
					+ "`content` TEXT NOT NULL, "
					+ "`date` INTEGER NOT NULL, "
					+ "`deny_chat_reason` NUMERIC"
					+ ")";
			break;
		case PRIVATE_MESSAGES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`sender_uuid` TEXT NOT NULL, "
					+ "`sender_name` TEXT NOT NULL, "
					+ "`recipient_uuid` TEXT NOT NULL, "
					+ "`recipient_name` TEXT NOT NULL, "
					+ "`rank_id` TEXT NOT NULL, "
					+ "`server` TEXT NOT NULL, "
					+ "`world` TEXT NOT NULL, "
					+ "`content` TEXT NOT NULL, "
					+ "`date` INTEGER NOT NULL, "
					+ "`deny_chat_reason` NUMERIC"
					+ ")";
			break;
		case IP_ADDRESSES:
			update = "CREATE TABLE IF NOT EXISTS {table_id} ("
					+ "`player_id` INTEGER PRIMARY KEY NOT NULL, "
					+ "`ip_addresses` TEXT NOT NULL"
					+ ")";
			break;
		} executeUpdate(update.replace("{table_id}", container.getDatabaseTableID()));
	}
	
	@Override
	public int getNextID(DataContainer table) throws SQLException {
		if (table == DataContainer.PUBLIC_MESSAGES || table == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to get next ID in table " + table.getDatabaseTableID() + " since that table does not have IDs");
		if (table == DataContainer.IP_ADDRESSES)
			table = DataContainer.PLAYERS;
		Number id = get("SELECT seq FROM sqlite_sequence WHERE name = ?", "seq", Number.class, table.getDatabaseTableID());
		return id == null ? 1 : (id.intValue() + 1);
	}
	
	@Override
	public String getEngineName() {
		return Library.SQLITE_JDBC.getName();
	}
	
	@Override
	public String getEngineVersion() throws SQLException {
		return get("SELECT sqlite_version()", 1, String.class);
	}
	
}
