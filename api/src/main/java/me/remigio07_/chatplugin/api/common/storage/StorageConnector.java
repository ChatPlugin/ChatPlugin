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

package me.remigio07_.chatplugin.api.common.storage;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import me.remigio07_.chatplugin.api.ChatPlugin;
import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.punishment.ban.Ban;
import me.remigio07_.chatplugin.api.common.punishment.kick.Kick;
import me.remigio07_.chatplugin.api.common.punishment.mute.Mute;
import me.remigio07_.chatplugin.api.common.punishment.warning.Warning;
import me.remigio07_.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07_.chatplugin.api.common.storage.database.DatabaseConnector;
import me.remigio07_.chatplugin.api.common.storage.flat_file.FlatFileConnector;
import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.server.chat.log.ChatLogManager;
import me.remigio07_.chatplugin.api.server.chat.log.LoggedMessage;

/**
 * Represents the storage connector used by the plugin.
 * 
 * @see DatabaseConnector
 * @see FlatFileConnector
 */
public abstract class StorageConnector {
	
	protected static StorageConnector instance;
	
	/**
	 * Increments a player's statistic using
	 * {@link #setPlayerData(PlayersDataType, OfflinePlayer, Object)}.
	 * 
	 * @param type Data's type
	 * @param player Player to set data for
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws IllegalArgumentException If <code>type</code> is not one of the following: {@link PlayersDataType#MESSAGES_SENT MESSAGES_SENT},
	 * {@link PlayersDataType#BANS BANS}, {@link PlayersDataType#WARNINGS WARNINGS}, {@link PlayersDataType#KICKS KICKS}, {@link PlayersDataType#MUTES MUTES}
	 */
	public void incrementPlayerStat(PlayersDataType<? extends Number> type, OfflinePlayer player) throws SQLException, IOException {
		if (type.ordinal() > PlayersDataType.MESSAGES_SENT.ordinal() - 1) {
			Number stat = getPlayerData(type, player);
			
			setPlayerData(type, player, stat == null ? 0 : ((stat instanceof Short ? stat.shortValue() : stat.intValue()) + 1));
		} else throw new IllegalArgumentException("Specified column type does not represent a stat: " + type.name());
	}
	
	/**
	 * Checks if a player is stored in the storage.
	 * 
	 * @param player Player to check
	 * @return Whether the player is stored
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	public boolean isPlayerStored(OfflinePlayer player) throws SQLException {
		return getPlayerData(PlayersDataType.PLAYER_NAME, player) != null;
	}
	
	/**
	 * Checks if a player is stored in the storage.
	 * 
	 * @param playerID ID of the player to check
	 * @return Whether the player is stored
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	public boolean isPlayerStored(int playerID) throws SQLException {
		return getPlayerData(PlayersDataType.PLAYER_NAME, playerID) != null;
	}
	
	/**
	 * Checks if a player's IP address is stored in the storage.
	 * 
	 * @param player Player to check
	 * @return Whether the IP is stored
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	public boolean isPlayerIPStored(OfflinePlayer player) throws SQLException {
		return getPlayerData(PlayersDataType.PLAYER_IP, player) != null;
	}
	
	/**
	 * Gets a player from the storage.
	 * Will return <code>null</code> if the player is not stored.
	 * 
	 * @param playerID Player's ID
	 * @return Stored player
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	@Nullable(why = "Player may not be stored")
	public OfflinePlayer getPlayer(int playerID) throws SQLException {
		return isPlayerStored(playerID) ? new OfflinePlayer(
				UUID.fromString(getPlayerData(PlayersDataType.PLAYER_UUID, playerID)),
				getPlayerData(PlayersDataType.PLAYER_NAME, playerID)
				) : null;
	}
	
	/**
	 * Disactives all active warnings in the storage for the specified player.
	 * 
	 * @param player Player to clear warnings for
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 */
	public void clearWarnings(OfflinePlayer player) throws SQLException, IOException {
		for (Warning warning : WarningManager.getInstance().getActiveWarnings(player))
			disactiveWarning(warning);
	}
	
	/**
	 * Calls {@link #select(DataContainer, String, Class, WhereCondition...)}
	 * and returns <code>def</code> if a {@link SQLException} gets thrown.
	 * 
	 * @param <T> Data's type
	 * @param container Data's container
	 * @param position Data's position (path or column)
	 * @param def Default value
	 * @param conditions Optional conditions
	 * @return Requested data
	 */
	@SuppressWarnings("unchecked")
	@Nullable(why = "Default value may be null or query's result SQL NULL")
	public <T> T safeSelect(DataContainer container, String position, T def, WhereCondition... conditions) {
		try {
			return (T) select(container, position, Object.class, conditions);
		} catch (SQLException e) {
			return def;
		}
	}
	
	/**
	 * Inserts a new ban in the storage. If you wish to just update
	 * an existing ban with new values use {@link #updateBan(Ban, Ban)} instead.
	 * 
	 * @param ban Ban object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void insertNewBan(Ban ban) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to insert a ban in the storage on the free version");
	}
	
	/**
	 * Updates an existing ban in the storage. The old ban's ID is the only thing
	 * that remains unchanged: other values are replaced with the new ban's ones.
	 * 
	 * @param oldBan Old ban to update data for
	 * @param newBan New ban with new values
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void updateBan(Ban oldBan, Ban newBan) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to update a ban in the storage on the free version");
	}
	
	/**
	 * Disactives a ban in the storage.
	 * 
	 * @param ban Ban object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void disactiveBan(Ban ban) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to disactive a ban in the storage on the free version");
	}
	
	/**
	 * Gets a {@link Ban} object from the storage.
	 * Will return <code>null</code> if the ban does not exist.
	 * 
	 * @param id Ban's ID
	 * @return Ban object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	@Nullable(why = "Specified ban may not exist")
	public Ban getBan(int id) throws SQLException {
		throw new UnsupportedOperationException("Unable to get a ban from the storage on the free version");
	}
	
	/**
	 * Inserts a new warning in the storage.
	 * 
	 * @param warning Warning object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void insertNewWarning(Warning warning) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to insert a warning in the storage on the free version");
	}
	
	/**
	 * Disactives a warning in the storage.
	 * 
	 * @param warning Warning object
	 * @throws SQLException If something goes wrong {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void disactiveWarning(Warning warning) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to disactive a warning in the storage on the free version");
	}
	
	/**
	 * Gets a {@link Warning} object from the storage.
	 * Will return <code>null</code> if the warning does not exist.
	 * 
	 * @param id Warning's ID
	 * @return Warning object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	@Nullable(why = "Specified warning may not exist")
	public Warning getWarning(int id) throws SQLException {
		throw new UnsupportedOperationException("Unable to get a warning from the storage on the free version");
	}
	
	/**
	 * Inserts a new kick in the storage.
	 * 
	 * @param kick Kick object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void insertNewKick(Kick kick) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to insert a kick in the storage on the free version");
	}
	
	/**
	 * Gets a {@link Kick} object from the storage.
	 * Will return <code>null</code> if the kick does not exist.
	 * 
	 * @param id Kick's ID
	 * @return Kick object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	@Nullable(why = "Specified kick may not exist")
	public Kick getKick(int id) throws SQLException {
		throw new UnsupportedOperationException("Unable to get a kick from the storage on the free version");
	}
	
	/**
	 * Inserts a new mute in the storage. If you wish to just update
	 * an existing mute with new values use {@link #updateMute(Mute, Mute)} instead.
	 * 
	 * @param mute Mute object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void insertNewMute(Mute mute) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to insert a mute in the storage on the free version");
	}
	
	/**
	 * Updates an existing mute in the storage. The old mute's ID is the only thing
	 * that remains unchanged: other values are replaced with the new mute's ones.
	 * 
	 * @param oldMute Old mute to update data for
	 * @param newMute New mute with new values
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void updateMute(Mute oldMute, Mute newMute) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to update a mute in the storage on the free version");
	}
	
	/**
	 * Disactives a mute in the storage.
	 * 
	 * @param mute Mute object
	 * @throws SQLException If something goes wrong {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void disactiveMute(Mute mute) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to disactive a mute in the storage on the free version");
	}
	
	/**
	 * Gets a {@link Mute} object from the storage.
	 * Will return <code>null</code> if the mute does not exist.
	 * 
	 * @param id Mute's ID
	 * @return Mute object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	@Nullable(why = "Specified mute may not exist")
	public Mute getMute(int id) throws SQLException {
		throw new UnsupportedOperationException("Unable to get a mute from the storage on the free version");
	}
	
	/**
	 * Inserts a new logged message in the storage.
	 * 
	 * @param message Logged message object
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void insertNewMessage(LoggedMessage message) throws SQLException, IOException {
		throw new UnsupportedOperationException("Unable to insert a message in the storage on the free version");
	}
	
	/**
	 * Gets a list of logged messages from the storage.
	 * 
	 * @param player Target player
	 * @param timeAgo Maximum time elapsed
	 * @param query Text to search
	 * @return List of logged messages
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	@NotNull
	public List<LoggedMessage> getMessages(OfflinePlayer player, long timeAgo, String query) throws SQLException {
		throw new UnsupportedOperationException("Unable to get messages from the storage on the free version");
	}
	
	/**
	 * Cleans messages older than {@link ChatLogManager#getMessagesAutoCleanerPeriod()} from the storage.
	 * Will do nothing if called on a proxy environment.
	 * 
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void cleanOldMessages() {
		throw new UnsupportedOperationException("Unable to clean old messages from the storage on the free version");
	}
	
	/**
	 * Gets the current connector's instance.
	 * 
	 * @return Connector's instance
	 */
	public static StorageConnector getInstance() {
		return instance;
	}
	
	/**
	 * Loads (or reloads) this connector.
	 * 
	 * @throws ChatPluginManagerException If something goes wrong
	 */
	public abstract void load() throws ChatPluginManagerException;
	
	/**
	 * Unloads this connector.
	 * 
	 * @throws ChatPluginManagerException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 */
	public abstract void unload() throws SQLException, IOException;
	
	/**
	 * Selects the data in the specified position if the given conditions are met.
	 * It is recommended to pass {@link Number} as <code>type</code>
	 * if you are trying to read a number to avoid {@link ClassCastException}s.
	 * 
	 * @param <T> Data's type
	 * @param container Data's container
	 * @param position Data's position (path or column)
	 * @param type Data's type
	 * @param conditions Optional conditions
	 * @return Requested data
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	public abstract <T> T select(DataContainer container, String position, Class<T> type, WhereCondition... conditions) throws SQLException;
	
	/**
	 * Counts the entries in the specified position if the given conditions are met.
	 * 
	 * @param container Entries' container
	 * @param conditions Optional conditions
	 * @return Entries' count
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	public abstract Number count(DataContainer container, WhereCondition... conditions) throws SQLException;
	
	/**
	 * Updates the data in the specified position if the given conditions are met.
	 * 
	 * @param container Data's container
	 * @param position Data's position (path or column)
	 * @param data Data to update
	 * @param conditions Optional conditions
	 * @return Affected rows' amount
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 */
	public abstract int update(DataContainer container, String position, Object data, WhereCondition... conditions) throws SQLException, IOException;
	
	/**
	 * Deletes the data in the specified position if the given conditions are met.
	 * 
	 * @param container Data's container
	 * @param conditions Optional conditions
	 * @return Affected rows' amount
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 */
	public abstract int delete(DataContainer container, WhereCondition... conditions) throws SQLException, IOException;
	
	/**
	 * Gets the list of data in the specified position if the given conditions are met.
	 * It is recommended to pass {@link Number} as <code>type</code>
	 * if you are trying to read a number to avoid {@link ClassCastException}s.
	 * 
	 * @param <T> Data's type
	 * @param container Data's container
	 * @param position Data's position
	 * @param type Data's type
	 * @param conditions Optional conditions
	 * @return Requested list of data
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	@NotNull
	public abstract <T> List<T> getColumnValues(DataContainer container, String position, Class<T> type, WhereCondition... conditions) throws SQLException;
	
	/**
	 * Gets the list of data in the specified row.
	 * 
	 * @param container Data's container
	 * @param id Data's ID
	 * @return Requested list of data
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#MESSAGES}
	 */
	@NotNull
	public abstract List<Object> getRowValues(DataContainer container, int id) throws SQLException;
	
	/**
	 * Gets the missing data containers in the storage.
	 * 
	 * @return Missing data containers
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	@NotNull
	public abstract List<DataContainer> getMissingDataContainers() throws SQLException;
	
	/**
	 * Creates the specified data container if it does not exist already.
	 * 
	 * @param container Data container to create
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 */
	public abstract void createDataContainer(DataContainer container) throws SQLException, IOException;
	
	/**
	 * Sets data in the storage.
	 * You can specify <code>null</code> as <code>data</code>.
	 * 
	 * @param container Container to insert data into
	 * @param position Data's position (path or column)
	 * @param id Entry's ID
	 * @param data Data to set or <code>null</code>
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#MESSAGES}
	 */
	public abstract void setData(DataContainer container, String position, int id, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException, IOException;
	
	/**
	 * Gets all entries' IDs in the specified data container.
	 * 
	 * @param container Container to check
	 * @return All entries' IDs
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#MESSAGES}
	 */
	@NotNull
	public abstract List<Integer> getIDs(DataContainer container) throws SQLException;
	
	/**
	 * Gets the next entry's ID in the specified data container.
	 * 
	 * @param container Container to check
	 * @return Next entry's ID
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#MESSAGES}
	 */
	public abstract int getNextID(DataContainer container) throws SQLException;
	
	/**
	 * Removes an entry from the specified data container by its ID.
	 * 
	 * @param container Container to remove entry from
	 * @param id Entry's ID
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#MESSAGES}
	 */
	public abstract void removeEntry(DataContainer container, int id) throws SQLException, IOException;
	
	/**
	 * Gets a player's data from the storage.
	 * Will return <code>null</code> if the stored data is a SQL <code>NULL</code>.
	 * 
	 * @param <T> Data's type
	 * @param type Data's type
	 * @param player Player to get data for
	 * @return Data if found, <code>null</code> otherwise
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	@Nullable(why = "Stored data may be SQL NULL")
	public abstract <T> T getPlayerData(PlayersDataType<T> type, OfflinePlayer player) throws SQLException;
	
	/**
	 * Gets a player's data from the storage.
	 * Will return <code>null</code> if the stored data is SQL <code>NULL</code>.
	 * 
	 * @param <T> Data's type
	 * @param type Data's type
	 * @param playerID ID of the player to get data for
	 * @return Data if found, <code>null</code> otherwise
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	public abstract <T> T getPlayerData(PlayersDataType<T> type, int playerID) throws SQLException;
	
	/**
	 * Sets data for a player in the storage.
	 * You can specify <code>null</code> as <code>data</code>.
	 * 
	 * @param type Data's type
	 * @param player Player to set data for
	 * @param data Data to set or <code>null<code>
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws IllegalArgumentException If <code>type == </code>{@link PlayersDataType#ID}
	 */
	public abstract void setPlayerData(PlayersDataType<?> type, OfflinePlayer player, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException, IOException;
	
	/**
	 * Sets data for a player in the storage.
	 * You can specify <code>null</code> as <code>data</code>.
	 * 
	 * @param type Data's type
	 * @param playerID ID of the player to set data for
	 * @param data Data to set or <code>null</code>
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws IllegalArgumentException If <code>type == </code>{@link PlayersDataType#ID}
	 */
	public abstract void setPlayerData(PlayersDataType<?> type, int playerID, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException, IOException;
	
	/**
	 * Gets all players with the specified IP address stored in {@link DataContainer#PLAYERS}.
	 * Note that this method might take some time to be executed: async calls are recommended.
	 * Specify <code>true</code> as <code>includeOlder</code> to check players' IP addresses
	 * stored in {@link DataContainer#IP_ADDRESSES}. This operation will take extra time.
	 * 
	 * @param ipAddress IP address to check
	 * @param includeOlder Whether to include players stored in {@link DataContainer#IP_ADDRESSES}
	 * @return Players with that IP address
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	@NotNull
	public abstract List<OfflinePlayer> getPlayers(InetAddress ipAddress, boolean includeOlder) throws SQLException;
	
	/**
	 * Inserts a new player in the storage.
	 * 
	 * @param player Player to insert
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 */
	public abstract void insertNewPlayer(OfflinePlayer player) throws SQLException, IOException;
	
	/**
	 * Cleans old players who haven't played for at least
	 * {@link #getPlayersAutoCleanerPeriod()} from the storage.
	 */
	public abstract void cleanOldPlayers();
	
	/**
	 * Represents a <code>WHERE</code> condition.
	 */
	public static class WhereCondition {
		
		private String firstTermPosition;
		private WhereOperator operator;
		private Object secondTermValue;
		
		/**
		 * Constructs a new <code>WHERE</code> condition.
		 * 
		 * @param firstTermPosition <code>WHERE</code>'s first term's position (path or column)
		 * @param operator <code>WHERE</code>'s operator
		 * @param secondTermValue <code>WHERE</code>'s second term's value
		 */
		public WhereCondition(String firstTermPosition, WhereOperator operator, Object secondTermValue) {
			this.firstTermPosition = firstTermPosition;
			this.operator = operator;
			this.secondTermValue = secondTermValue;
		}
		
		/**
		 * Gets this condition's first term's position.
		 * 
		 * @return Condition's first term's position
		 */
		public String getFirstTermPosition() {
			return firstTermPosition;
		}
		
		/**
		 * Gets this condition's operator.
		 * 
		 * @return Condition's operator
		 */
		public WhereOperator getOperator() {
			return operator;
		}
		
		/**
		 * Gets this condition's second term's value.
		 * 
		 * @return Condition's second term's value
		 */
		public Object getSecondTermValue() {
			return secondTermValue;
		}
		
		/**
		 * Represents the operators supported by {@link WhereCondition}.
		 */
		public static enum WhereOperator {
			
			EQUAL("="),
			NOT_EQUAL("<>"),
			GREATER_THAN(">"),
			LESS_THAN("<"),
			GREATER_THAN_OR_EQUAL(">="),
			LESS_THAN_OR_EQUAL("<=");
			
			private String toString;
			
			private WhereOperator(String toString) {
				this.toString = toString;
			}
			
			@Override
			public String toString() {
				return toString;
			}
			
		}
		
	}
	
}
