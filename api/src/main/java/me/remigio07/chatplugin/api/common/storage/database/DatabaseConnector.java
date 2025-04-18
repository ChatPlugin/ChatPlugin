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

package me.remigio07.chatplugin.api.common.storage.database;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Statistic;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.statistic.Statistics;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.StorageManager;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents the database connector used by the plugin.
 */
public abstract class DatabaseConnector extends StorageConnector {
	
	protected Connection connection;
	
	@Override
	public void unload() throws SQLException {
		if (connection != null)
			connection.close();
		connection = null;
	}
	
	@Override
	public <T> T select(DataContainer container, String position, Class<T> type, WhereCondition... conditions) throws SQLException {
		return get("SELECT " + position + " FROM " + container.getDatabaseTableID() + combineWhereConditions(conditions), position, type, combineSecondTermValues(conditions));
	}
	
	@Override
	public Number count(DataContainer container, WhereCondition... conditions) throws SQLException {
		return get("SELECT COUNT(*) FROM " + container.getDatabaseTableID() + combineWhereConditions(conditions), 1, Number.class, combineSecondTermValues(conditions));
	}
	
	@Override
	public int update(DataContainer container, String position, Object data, WhereCondition... conditions) throws SQLException {
		Object[] args = Arrays.copyOf(new Object[] { data }, conditions.length + 1);
		
		System.arraycopy(combineSecondTermValues(conditions), 0, args, 1, conditions.length);
		return executeUpdate("UPDATE " + container.getDatabaseTableID() + " SET " + position + " = ?" + combineWhereConditions(conditions), args);
	}
	
	@Override
	public int delete(DataContainer container, WhereCondition... conditions) throws SQLException {
		return executeUpdate("DELETE FROM " + container.getDatabaseTableID() + combineWhereConditions(conditions), combineSecondTermValues(conditions));
	}
	
	@Override
	public <T> @NotNull List<T> getColumnValues(DataContainer container, String position, Class<T> type, WhereCondition... conditions) throws SQLException {
		return getColumnValues("SELECT " + position + " FROM " + container.getDatabaseTableID() + combineWhereConditions(conditions), position, type, combineSecondTermValues(conditions));
	}
	
	private String combineWhereConditions(WhereCondition... conditions) {
		if (conditions.length == 0)
			return "";
		StringBuilder sb = new StringBuilder(" WHERE ");
		
		for (WhereCondition condition : conditions)
			sb.append(condition.getFirstTermPosition() + " " + condition.getOperator().toString() + " ? AND ");
		sb.delete(sb.length() - 5, sb.length());
		return sb.toString();
	}
	
	private Object[] combineSecondTermValues(WhereCondition... conditions) {
		Object[] values = new Object[conditions.length];
		
		for (int i = 0; i < conditions.length; i++)
			values[i] = conditions[i].getSecondTermValue();
		return values;
	}
	
	@Override
	public @NotNull List<Object> getRowValues(DataContainer table, int id) throws SQLException {
		if (table == DataContainer.PUBLIC_MESSAGES || table == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to get row values in table " + table.getDatabaseTableID() + " using an ID since that table does not have IDs");
		List<Object> list = new ArrayList<>();
		
		try (PreparedStatement statement = prepareStatement("SELECT * FROM " + table.getDatabaseTableID() + " WHERE " + table.getIDColumn() + " = ?", id)) {
			statement.execute();
			
			ResultSet result = statement.getResultSet();
			
			if (!result.next())
				return null;
			for (int i = 1; i < table.getColumns().length + 1; i++)
				list.add(result.getObject(i));
			statement.clearParameters();
		} return list;
	}
	
	@Override
	public void setData(DataContainer table, String column, int id, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException {
		if (table == DataContainer.PUBLIC_MESSAGES || table == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to set data to table " + table.getDatabaseTableID() + " using an ID since that table does not have IDs");
		executeUpdate("UPDATE " + table.getDatabaseTableID() + " SET " + column + " = ? WHERE " + table.getIDColumn() + " = ?", data, id);
	}
	
	@NotNull
	@Override
	public List<Integer> getIDs(DataContainer table) throws SQLException {
		if (table == DataContainer.PUBLIC_MESSAGES || table == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to get IDs in table " + table.getDatabaseTableID() + " since that table does not have IDs");
		return Utils.numberListToIntegerList(getColumnValues("SELECT " + table.getIDColumn() + " FROM " + table.getDatabaseTableID(), 1, Number.class));
	}
	
	@Override
	public void removeEntry(DataContainer table, int id) throws SQLException {
		if (table == DataContainer.PUBLIC_MESSAGES || table == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to remove entry in table " + table.getDatabaseTableID() + " using an ID since that table does not have IDs");
		executeUpdate("DELETE FROM " + table.getDatabaseTableID() + " WHERE " + table.getIDColumn() + " = ?", id);
	}
	
	@Override
	@Nullable(why = "Stored data may be SQL NULL")
	public <T> T getPlayerData(PlayersDataType<T> type, OfflinePlayer player) throws SQLException {
		return convertNumber(get("SELECT " + type.getDatabaseTableID() + " FROM " + DataContainer.PLAYERS.getDatabaseTableID() + " WHERE player_uuid = ?", type.getDatabaseTableID(), type.getType(), player.getUUID().toString()), type);
	}
	
	@Override
	public <T> T getPlayerData(PlayersDataType<T> type, int playerID) throws SQLException {
		return convertNumber(get("SELECT " + type.getDatabaseTableID() + " FROM " + DataContainer.PLAYERS.getDatabaseTableID() + " WHERE id = ?", type.getDatabaseTableID(), type.getType(), playerID), type);
	}
	
	@Override
	public void setPlayerData(PlayersDataType<?> type, OfflinePlayer player, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException {
		if (type == PlayersDataType.ID)
			throw new IllegalArgumentException("Unable to change a player's ID");
		if (isPlayerStored(player))
			executeUpdate("UPDATE " + DataContainer.PLAYERS.getDatabaseTableID() + " SET " + type.getDatabaseTableID() + " = ? WHERE player_uuid = ?", data, player.getUUID().toString());
		else LogManager.log("The plugin tried to write data into the database (table: {0}, column: {1}) for a player ({2}) who has never played on the server. Data: \"{3}\".", 2, DataContainer.PLAYERS.getDatabaseTableID(), type.getDatabaseTableID(), player.getName(), String.valueOf(data));
	}
	
	@Override
	public void setPlayerData(PlayersDataType<?> type, int playerID, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException, IOException {
		if (type == PlayersDataType.ID)
			throw new IllegalArgumentException("Unable to change a player's ID");
		if (isPlayerStored(playerID))
			executeUpdate("UPDATE " + DataContainer.PLAYERS.getDatabaseTableID() + " SET " + type.getDatabaseTableID() + " = ? WHERE id = ?", data, playerID);
		else LogManager.log("The plugin tried to write data into the database (table: {0}, column: {1}) for a player (ID: #{2}) who has never played on the server. Data: \"{3}\".", 2, DataContainer.PLAYERS.getDatabaseTableID(), type.getDatabaseTableID(), playerID, String.valueOf(data));
	}
	
	@NotNull
	@Override
	public List<OfflinePlayer> getPlayers(InetAddress ipAddress, boolean includeOlder) throws SQLException {
		List<OfflinePlayer> list = new ArrayList<>();
		List<String> uuids = getColumnValues("SELECT player_uuid FROM " + DataContainer.PLAYERS.getDatabaseTableID() + " WHERE player_ip = ?", "player_uuid", String.class, ipAddress.getHostAddress());
		List<String> names = getColumnValues("SELECT player_name FROM " + DataContainer.PLAYERS.getDatabaseTableID() + " WHERE player_ip = ?", "player_name", String.class, ipAddress.getHostAddress());
		
		for (int i = 0; i < uuids.size(); i++)
			list.add(new OfflinePlayer(UUID.fromString(uuids.get(i)), names.get(i)));
		if (includeOlder)
			for (Number id : getColumnValues("SELECT player_id FROM " + DataContainer.IP_ADDRESSES.getDatabaseTableID() + " WHERE player_ip LIKE ?", "player_id", Number.class, "%" + ipAddress.getHostAddress() + "%"))
				list.add(getPlayer(id.intValue()));
		return list;
	}
	
	@Override
	public void insertNewPlayer(OfflinePlayer player) throws SQLException {
		executeUpdate("INSERT INTO " + DataContainer.PLAYERS.getDatabaseTableID() + " (player_uuid, player_name) VALUES (?, ?)",
				player.getUUID().toString(),			// player_uuid
				player.getName()						// player_name
				);
		
		if (player.isOnline()) {
			executeUpdate("UPDATE " + DataContainer.PLAYERS.getDatabaseTableID() + " SET player_ip = ? WHERE player_uuid = ?", player.getIPAddress().getHostAddress(), player.getUUID().toString());
			
			if (Environment.isBukkit())
				executeUpdate("UPDATE " + DataContainer.PLAYERS.getDatabaseTableID() + " SET time_played = ? WHERE player_uuid = ?", player.toAdapter().bukkitValue().getStatistic(Statistic.valueOf(VersionUtils.getVersion().getProtocol() < 341 ? "PLAY_ONE_TICK" : "PLAY_ONE_MINUTE")) * 50, player.getUUID().toString());
			else if (Environment.isSponge() && player.toAdapter().spongeValue().getStatisticData().get(Keys.STATISTICS).isPresent())
				executeUpdate("UPDATE " + DataContainer.PLAYERS.getDatabaseTableID() + " SET time_played = ? WHERE player_uuid = ?", player.toAdapter().spongeValue().getStatisticData().get(Keys.STATISTICS).get().getOrDefault(Statistics.TIME_PLAYED, 0L) * 50, player.getUUID().toString());
		}
	}
	
	@Override
	public void cleanOldPlayers() {
		if (StorageManager.getInstance().getPlayersAutoCleanerPeriod() != -1)
			TaskManager.runAsync(() -> {
				long ms = System.currentTimeMillis();
				int old = 0;
				
				try {
					for (Number id : getColumnValues("SELECT id FROM " + DataContainer.PLAYERS.getDatabaseTableID() + " WHERE last_logout IS NOT NULL AND last_logout < ?", "id", Number.class, System.currentTimeMillis() - StorageManager.getInstance().getPlayersAutoCleanerPeriod())) {
						executeUpdate("DELETE FROM " + DataContainer.PLAYERS.getDatabaseTableID() + " WHERE id = ?", id.intValue());
						executeUpdate("DELETE FROM " + DataContainer.IP_ADDRESSES.getDatabaseTableID() + " WHERE id = ?", id.intValue());
						old++;
					}
				} catch (SQLException e) {
					LogManager.log("SQLException occurred while cleaning old players from the database: {0}", 2, e.getMessage());
				} if (old > 0)
					LogManager.log("[ASYNC] Cleaned {0} old player{1} from the database in {2} ms.", 4, old, old == 1 ? "" : "s", System.currentTimeMillis() - ms);
			}, 0L);
	}
	
	/**
	 * Gets the connection to the database currently in use.
	 * 
	 * @return Current connection
	 */
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Checks if the connection is still open and valid
	 * and tries to open it again if it is closed.
	 */
	@SuppressWarnings("deprecation")
	public void checkConnection() {
		try {
			if (connection.isClosed() || !connection.isValid(0)) {
				LogManager.log("Connection to database timed out; reconnecting.", 3);
				load();
			} return;
		} catch (SQLException e) {
			LogManager.log("SQLException occurred while trying to access the database: {0}", 2, e.getMessage());
		} catch (ChatPluginManagerException e) {
			LogManager.log("Error occurred while reloading the database connector after a period of inactivity: {0}", 2, e.getMessage());
		} ChatPlugin.getInstance().unload();
	}
	
	/**
	 * Prepares a statement and passes given parameters to it.
	 * 
	 * <p>The <code>params</code> array can contain <code>null</code> values.</p>
	 * 
	 * <p>Refer to {@link Connection#prepareStatement(String)} for more information.</p>
	 * 
	 * @param sql SQL statement to execute
	 * @param params Parameters to pass
	 * @return Compiled SQL statement
	 * @throws SQLException If something goes wrong
	 */
	public PreparedStatement prepareStatement(String sql, Object... params) throws SQLException {
		checkConnection();
		
		PreparedStatement statement = connection.prepareStatement(sql);
		
		for (int i = 0; i < params.length; i++)
			statement.setObject(i + 1, params[i]);
		return statement;
	}
	
	/**
	 * Executes a SQL statement which must be a DML or a statement that returns nothing.
	 * 
	 * <p>The <code>params</code> array can contain <code>null</code> values.</p>
	 * 
	 * <p>Refer to {@link PreparedStatement#executeUpdate()} for more information.</p>
	 * 
	 * @param sql SQL statement to execute
	 * @param params Parameters to pass
	 * @return Affected rows' amount
	 * @throws SQLException If something goes wrong
	 */
	public int executeUpdate(String sql, Object... params) throws SQLException {
		try (PreparedStatement statement = prepareStatement(sql, params)) {
			int i = statement.executeUpdate();
			
			statement.clearParameters();
			return i;
		}
	}
	
	/**
	 * Gets the data in the first row returned by the specified query.
	 * 
	 * <p>Will return <code>null</code> if the query's result is a SQL <code>NULL</code>.</p>
	 * 
	 * <p>The <code>params</code> array can contain <code>null</code> values.</p>
	 * 
	 * <p>It is recommended to pass {@link Number} as <code>type</code> if you
	 * are trying to read a number to avoid {@link ClassCastException}s.</p>
	 * 
	 * @param <T> Data's type
	 * @param query SQL statement
	 * @param columnLabel Column's label
	 * @param type Data's class
	 * @param params Parameters to pass
	 * @return Requested data
	 * @throws SQLException If something goes wrong
	 */
	@SuppressWarnings("unchecked")
	@Nullable(why = "Query's result may be SQL NULL")
	public <T> T get(String query, String columnLabel, Class<T> type, Object... params) throws SQLException {
		try (PreparedStatement statement = prepareStatement(query, params)) {
			statement.execute();
			
			ResultSet result = statement.getResultSet();
			
			if (!result.next())
				return null;
			Object t = result.getObject(columnLabel);
			
			statement.clearParameters();
			return (T) t;
		}
	}
	
	/**
	 * Calls {@link #get(String, String, Class, Object...)} and
	 * returns <code>def</code> if a {@link SQLException} gets thrown.
	 * 
	 * @param <T> Data's type
	 * @param query SQL statement
	 * @param columnLabel Column's label
	 * @param def Default value
	 * @param params Parameters to pass
	 * @return Requested data
	 */
	@SuppressWarnings("unchecked")
	@Nullable(why = "Default value may be null or query's result SQL NULL")
	public <T> T safeGet(String query, String columnLabel, T def, Object... params) {
		try {
			return (T) get(query, columnLabel, Object.class, params);
		} catch (SQLException e) {
			return def;
		}
	}
	
	/**
	 * Gets the data in the first row returned by the specified query.
	 * 
	 * <p>Will return <code>null</code> if the query's result is a SQL <code>NULL</code>.</p>
	 * 
	 * <p>The <code>params</code> array can contain <code>null</code> values.</p>
	 * 
	 * <p>It is recommended to pass {@link Number} as <code>type</code> if you
	 * are trying to read a number to avoid {@link ClassCastException}s.</p>
	 * 
	 * @param <T> Data's type
	 * @param query SQL statement
	 * @param columnIndex Column's index, starting from 1
	 * @param type Data's class
	 * @param params Parameters to pass
	 * @return Requested data
	 * @throws SQLException If something goes wrong
	 */
	@SuppressWarnings("unchecked")
	@Nullable(why = "Query's result may be SQL NULL")
	public <T> T get(String query, int columnIndex, Class<T> type, Object... params) throws SQLException {
		try (PreparedStatement statement = prepareStatement(query, params)) {
			statement.execute();
			
			ResultSet result = statement.getResultSet();
			
			if (!result.next())
				return null;
			Object t = result.getObject(columnIndex);
			
			statement.clearParameters();
			return (T) t;
		}
	}
	
	/**
	 * Calls {@link #get(String, int, Class, Object...)} and
	 * returns <code>def</code> if a {@link SQLException} gets thrown.
	 * 
	 * @param <T> Data's type
	 * @param query SQL statement
	 * @param columnIndex Column's index
	 * @param def Default value
	 * @param params Parameters to pass
	 * @return Requested data
	 */
	@SuppressWarnings("unchecked")
	@Nullable(why = "Default value may be null or query's result SQL NULL")
	public <T> T safeGet(String query, int columnIndex, T def, Object... params) {
		try {
			return (T) get(query, columnIndex, Object.class, params);
		} catch (SQLException e) {
			return def;
		}
	}
	
	/**
	 * Gets the list of data in the column returned by the specified query.
	 * 
	 * <p>Will return a new {@link ArrayList} if the query has no results.</p>
	 * 
	 * <p>The <code>params</code> array can contain <code>null</code> values.</p>
	 * 
	 * <p>It is recommended to pass {@link Number} as <code>type</code> if you
	 * are trying to read a number to avoid {@link ClassCastException}s.</p>
	 * 
	 * @param <T> Data's type
	 * @param query SQL statement
	 * @param columnLabel Column's label
	 * @param type Data's class
	 * @param params Parameters to pass
	 * @return Requested list of data
	 * @throws SQLException If something goes wrong
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <T> List<T> getColumnValues(String query, String columnLabel, Class<T> type, Object... params) throws SQLException {
		List<T> list = new ArrayList<>();
		
		try (PreparedStatement statement = prepareStatement(query, params)) {
			statement.execute();
			
			ResultSet result = statement.getResultSet();
			
			while (result.next())
				list.add((T) result.getObject(columnLabel));
			statement.clearParameters();
		} return list;
	}
	
	/**
	 * Gets the list of data in the column returned by the specified query.
	 * 
	 * <p>Will return a new {@link ArrayList} if the query has no results.</p>
	 * 
	 * <p>The <code>params</code> array can contain <code>null</code> values.</p>
	 * 
	 * <p>It is recommended to pass {@link Number} as <code>type</code> if you
	 * are trying to read a number to avoid {@link ClassCastException}s.</p>
	 * 
	 * @param <T> Data's type
	 * @param query SQL statement
	 * @param columnIndex Column's index, starting from 1
	 * @param type Data's class
	 * @param params Parameters to pass
	 * @return Requested list of data
	 * @throws SQLException If something goes wrong
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public <T> List<T> getColumnValues(String query, int columnIndex, Class<T> type, Object... params) throws SQLException {
		List<T> list = new ArrayList<>();
		
		try (PreparedStatement statement = prepareStatement(query, params)) {
			statement.execute();
			
			ResultSet result = statement.getResultSet();
			
			while (result.next())
				list.add((T) result.getObject(columnIndex));
			statement.clearParameters();
		} return list;
	}
	
	/**
	 * Gets the current connector's instance.
	 * 
	 * @return Connector's instance
	 */
	public static DatabaseConnector getInstance() {
		return (DatabaseConnector) instance;
	}
	
}
