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

package me.remigio07.chatplugin.api.common.storage;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.ban.Ban;
import me.remigio07.chatplugin.api.common.punishment.kick.Kick;
import me.remigio07.chatplugin.api.common.punishment.mute.Mute;
import me.remigio07.chatplugin.api.common.punishment.warning.Warning;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseConnector;
import me.remigio07.chatplugin.api.common.storage.flat_file.FlatFileConnector;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.chat.log.ChatLogManager;
import me.remigio07.chatplugin.api.server.chat.log.LoggedMessage;
import me.remigio07.chatplugin.api.server.chat.log.LoggedPrivateMessage;
import me.remigio07.chatplugin.api.server.chat.log.LoggedPublicMessage;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents the storage connector used by the plugin.
 * 
 * @see DatabaseConnector
 * @see FlatFileConnector
 */
public abstract class StorageConnector {
	
	protected static StorageConnector instance;
	
	/**
	 * Increments a player's statistic in the storage.
	 * 
	 * @param type Data's type
	 * @param player Player to set data for
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws IllegalArgumentException If <code>type</code> is not one of the following: {@link PlayersDataType#MESSAGES_SENT MESSAGES_SENT}, {@link PlayersDataType#ANTISPAM_INFRACTIONS ANTISPAM_INFRACTIONS},
	 * {@link PlayersDataType#BANS BANS}, {@link PlayersDataType#WARNINGS WARNINGS}, {@link PlayersDataType#KICKS KICKS}, {@link PlayersDataType#MUTES MUTES}
	 * @throws IllegalStateException If <code>!</code>{@link Environment#isProxy()}
	 * and ChatPlugin has not finished loading yet
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
	 * @throws IllegalStateException If <code>!</code>{@link Environment#isProxy()}
	 * and ChatPlugin has not finished loading yet
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
	 * @throws IllegalStateException If <code>!</code>{@link Environment#isProxy()}
	 * and ChatPlugin has not finished loading yet
	 */
	public boolean isPlayerIPStored(OfflinePlayer player) throws SQLException {
		return getPlayerData(PlayersDataType.PLAYER_IP, player) != null;
	}
	
	/**
	 * Gets a player from the storage.
	 * 
	 * <p>Will return <code>null</code> if the player is not stored.</p>
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
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void clearWarnings(OfflinePlayer player) throws SQLException, IOException {
		for (Warning warning : WarningManager.getInstance().getActiveWarnings(player))
			disactiveWarning(warning);
	}
	
	/**
	 * Inserts a new ban in the storage.
	 * 
	 * <p>If you wish to just update an existing ban with new
	 * values use {@link #updateBan(Ban, Ban)} instead.</p>
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
	 * Updates an existing ban in the storage.
	 * 
	 * <p>The old ban's ID is the only thing that remains unchanged:
	 * other values are replaced with the new ban's ones.</p>
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
	 * 
	 * <p>Will return <code>null</code> if the ban does not exist.</p>
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
	 * 
	 * <p>Will return <code>null</code> if the warning does not exist.</p>
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
	 * 
	 * <p>Will return <code>null</code> if the kick does not exist.</p>
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
	 * Inserts a new mute in the storage.
	 * 
	 * <p>If you wish to just update an existing mute with new
	 * values use {@link #updateMute(Mute, Mute)} instead.</p>
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
	 * Updates an existing mute in the storage.
	 * 
	 * <p>The old mute's ID is the only thing that remains unchanged:
	 * other values are replaced with the new mute's ones.</p>
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
	 * 
	 * <p>Will return <code>null</code> if the mute does not exist.</p>
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
	 * Gets a list of logged public messages from the storage.
	 * 
	 * @param sender Public messages' sender
	 * @param timeAgo Maximum time elapsed
	 * @param query Text to search
	 * @return List of logged messages
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	@NotNull
	public List<LoggedPublicMessage> getPublicMessages(@NotNull OfflinePlayer sender, long timeAgo, String query) throws SQLException {
		throw new UnsupportedOperationException("Unable to get public messages from the storage on the free version");
	}
	
	/**
	 * Gets a list of logged private messages from the storage.
	 * 
	 * @param sender Private messages' sender
	 * @param timeAgo Maximum time elapsed
	 * @param query Text to search
	 * @return List of logged private messages
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	@NotNull
	public List<LoggedPrivateMessage> getPrivateMessages(@NotNull OfflinePlayer sender, long timeAgo, String query) throws SQLException {
		throw new UnsupportedOperationException("Unable to get private messages from the storage on the free version");
	}
	
	/**
	 * Cleans messages older than {@link ChatLogManager#getMessagesAutoCleanerPeriod()}
	 * from {@link DataContainer#PUBLIC_MESSAGES} and {@link DataContainer#PRIVATE_MESSAGES}.
	 * 
	 * <p>Will do nothing if called on a proxy environment.</p>
	 * 
	 * @throws UnsupportedOperationException If <code>!</code>{@link ChatPlugin#isPremium()}
	 */
	public void cleanOldMessages() {
		throw new UnsupportedOperationException("Unable to clean old messages from the storage on the free version");
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T convertNumber(Object data, PlayersDataType<T> type) {
		if (data instanceof Number) {
			if (type.getType() == long.class)
				return (T) Long.valueOf(((Number) data).longValue());
			if (type.getType() == int.class)
				return (T) Integer.valueOf(((Number) data).intValue());
			else return (T) Short.valueOf(((Number) data).shortValue());
		} else return (T) data; // String
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
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 */
	public abstract void unload() throws SQLException, IOException;
	
	/**
	 * Selects the data in the specified position if the given conditions are met.
	 * 
	 * <p>It is recommended to pass {@link Number} as <code>type</code> if you
	 * are trying to read a number to avoid {@link ClassCastException}s.</p>
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
	 * 
	 * <p>It is recommended to pass {@link Number} as <code>type</code> if you
	 * are trying to read a number to avoid {@link ClassCastException}s.</p>
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
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#PUBLIC_MESSAGES}
	 * <code>|| container == </code>{@link DataContainer#PRIVATE_MESSAGES}
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
	 * 
	 * <p>You can specify <code>null</code> as <code>data</code>.</p>
	 * 
	 * @param container Container to insert data into
	 * @param position Data's position (path or column)
	 * @param id Entry's ID
	 * @param data Data to set or <code>null</code>
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#PUBLIC_MESSAGES}
	 * <code>|| container == </code>{@link DataContainer#PRIVATE_MESSAGES}
	 */
	public abstract void setData(DataContainer container, String position, int id, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException, IOException;
	
	/**
	 * Gets all entries' IDs in the specified data container.
	 * 
	 * @param container Container to check
	 * @return All entries' IDs
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#PUBLIC_MESSAGES}
	 * <code>|| container == </code>{@link DataContainer#PRIVATE_MESSAGES}
	 */
	@NotNull
	public abstract List<Integer> getIDs(DataContainer container) throws SQLException;
	
	/**
	 * Gets the next entry's ID in the specified data container.
	 * 
	 * @param container Container to check
	 * @return Next entry's ID
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#PUBLIC_MESSAGES}
	 * <code>|| container == </code>{@link DataContainer#PRIVATE_MESSAGES}
	 */
	public abstract int getNextID(DataContainer container) throws SQLException;
	
	/**
	 * Removes an entry from the specified data container by its ID.
	 * 
	 * @param container Container to remove entry from
	 * @param id Entry's ID
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws IllegalArgumentException If <code>container == </code>{@link DataContainer#PUBLIC_MESSAGES}
	 * <code>|| container == </code>{@link DataContainer#PRIVATE_MESSAGES}
	 */
	public abstract void removeEntry(DataContainer container, int id) throws SQLException, IOException;
	
	/**
	 * Gets a player's data from the storage.
	 * 
	 * <p>Will return <code>null</code> if the stored data is a SQL <code>NULL</code>.</p>
	 * 
	 * <p>Unlike {@link #setPlayerData(PlayersDataType, int, Object)}, this method gets data
	 * based on the player's UUID or name depending on {@link ChatPlugin#isOnlineMode()}.
	 * This method is slightly slower and might throw {@link IllegalStateException}.</p>
	 * 
	 * @param <T> Data's type
	 * @param type Data's type
	 * @param player Player to get data for
	 * @return Data if found, <code>null</code> otherwise
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IllegalStateException If <code>!</code>{@link Environment#isProxy()}
	 * and ChatPlugin has not finished loading yet
	 */
	@Nullable(why = "Stored data may be SQL NULL")
	public abstract <T> T getPlayerData(PlayersDataType<T> type, OfflinePlayer player) throws SQLException;
	
	/**
	 * Gets a player's data from the storage.
	 * 
	 * <p>Will return <code>null</code> if the stored data is SQL <code>NULL</code>.</p>
	 * 
	 * @param <T> Data's type
	 * @param type Data's type
	 * @param playerID ID of the player to get data for
	 * @return Data if found, <code>null</code> otherwise
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	@Nullable(why = "Stored data may be SQL NULL")
	public abstract <T> T getPlayerData(PlayersDataType<T> type, int playerID) throws SQLException;
	
	/**
	 * Sets data for a player in the storage.
	 * 
	 * <p>You can specify <code>null</code> as <code>data</code>.</p>
	 * 
	 * <p>Unlike {@link #setPlayerData(PlayersDataType, int, Object)}, this method sets data
	 * based on the player's UUID or name depending on {@link ChatPlugin#isOnlineMode()}.
	 * This method is slightly slower and might throw {@link IllegalStateException}.</p>
	 * 
	 * @param type Data's type
	 * @param player Player to set data for
	 * @param data Data to set or <code>null</code>
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 * @throws IOException If something goes wrong and {@link StorageMethod#isFlatFile()}
	 * @throws IllegalArgumentException If <code>type == </code>{@link PlayersDataType#ID}
	 * @throws IllegalStateException If <code>!</code>{@link Environment#isProxy()}
	 * and ChatPlugin has not finished loading yet
	 */
	public abstract void setPlayerData(PlayersDataType<?> type, OfflinePlayer player, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException, IOException;
	
	/**
	 * Sets data for a player in the storage.
	 * 
	 * <p>You can specify <code>null</code> as <code>data</code>.</p>
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
	 * 
	 * <p>Specify <code>true</code> as <code>includeOlder</code> to check players' IP addresses
	 * stored in {@link DataContainer#IP_ADDRESSES}. This operation will take extra time.</p>
	 * 
	 * <p><strong>Note:</strong> this method might take some
	 * time to be executed: async calls are recommended.</p>
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
	 * {@link StorageManager#getPlayersAutoCleanerPeriod()} from the storage.
	 */
	public abstract void cleanOldPlayers();
	
	/**
	 * Gets the connector's engine's name.
	 * 
	 * @return Engine's name
	 */
	public abstract String getEngineName();
	
	/**
	 * Gets the connector's engine's version.
	 * 
	 * @return Engine's version
	 * @throws SQLException If something goes wrong and {@link StorageMethod#isDatabase()}
	 */
	public abstract String getEngineVersion() throws SQLException;
	
	/**
	 * Represents a <a href="https://www.w3schools.com/SQl/sql_where.asp"><code>WHERE</code></a> SQL condition.
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
			
			/**
			 * Checks for equality between two values.
			 */
			EQUAL("="),
			
			/**
			 * Checks for inequality between two values.
			 */
			NOT_EQUAL("<>"),
			
			/**
			 * Checks if the first value is bigger than the second one.
			 */
			GREATER_THAN(">"),
			
			/**
			 * Checks if the first value is smaller than the second one.
			 */
			LESS_THAN("<"),
			
			/**
			 * Checks if the first value is bigger than or equal to the second one.
			 */
			GREATER_THAN_OR_EQUAL(">="),
			
			/**
			 * Checks if the first value is smaller than or equal to the second one.
			 */
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
