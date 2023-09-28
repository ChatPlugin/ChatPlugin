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

import java.io.File;

import me.remigio07.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents the storage's data's containers.
 * They are equivalent to the database's tables if {@link StorageMethod#isDatabase()}
 * or to the files contained in {@link StorageManager#getFolder()} otherwise.
 */
public enum DataContainer {
	
	/**
	 * Represents the bans' data.
	 */
	BANS("id", "player_uuid", "player_name", "player_ip", "staff_member", "who_unbanned", "reason", "server", "type", "date", "unban_date", "duration", "global", "silent", "active", "unbanned"),
	
	/**
	 * Represents the warnings' data.
	 */
	WARNINGS("id", "player_uuid", "player_name", "staff_member", "who_unwarned", "reason", "server", "date", "unwarn_date", "duration", "global", "silent", "active", "unwarned"),
	
	/**
	 * Represents the kicks' data.
	 */
	KICKS("id", "player_uuid", "player_name", "player_ip", "staff_member", "reason", "server", "type", "date", "silent"),
	
	/**
	 * Represents the mutes' data.
	 */
	MUTES("id", "player_uuid", "player_name", "staff_member", "who_unmuted", "reason", "server", "date", "unmute_date", "duration", "global", "silent", "active", "unmuted"),
	
	/**
	 * Represents the players' data.
	 */
	PLAYERS("id", "player_uuid", "player_name", "player_ip", "language", "last_logout", "time_played", "messages_sent", "bans", "warnings", "kicks", "mutes", "ignored_players"),
	
	/**
	 * Represents the chat messages' data.
	 */
	CHAT_MESSAGES("player_uuid", "player_name", "rank_id", "server", "world", "message", "date", "deny_chat_reason"),
	
	/**
	 * Represents the private messages' data.
	 */
	PRIVATE_MESSAGES("player_uuid", "player_name", "recipient", "rank_id", "server", "world", "message", "date", "deny_chat_reason"),
	
	/**
	 * Represents the players' IP addresses.
	 */
	IP_ADDRESSES("player_id", "ip_addresses");
	
	private String[] columns;
	
	private DataContainer(String... columns) {
		this.columns = columns;
	}
	
	/**
	 * Returns <code>name().toLowerCase().replace('_', '-')</code>.
	 * 
	 * @return Data container's name
	 */
	public String getName() {
		return name().toLowerCase().replace('_', '-');
	}
	
	/**
	 * Gets this data type's database table's ID, preceded by {@link DatabaseManager#getTablePrefix()}.
	 * 
	 * @return Database table's ID
	 */
	public String getDatabaseTableID() {
		return DatabaseManager.getTablePrefix() + name().toLowerCase();
	}
	
	/**
	 * Gets this data type's ID column's label.
	 * Will return {@link Utils#NOT_APPLICABLE} if called on {@link #CHAT_MESSAGES}
	 * or {@link #PRIVATE_MESSAGES}, "player_id" if called on {@link #IP_ADDRESSES}
	 * and "id" otherwise.
	 * 
	 * @return ID column's label
	 */
	public String getIDColumn() {
		return this == CHAT_MESSAGES || this == PRIVATE_MESSAGES ? Utils.NOT_APPLICABLE : this == IP_ADDRESSES ? "player_id" : "id";
	}
	
	/**
	 * Gets this data type's columns' labels.
	 * 
	 * @return Columns' labels
	 */
	public String[] getColumns() {
		return columns;
	}
	
	/**
	 * Gets this data container's flat-file.
	 * 
	 * @return Data container's file
	 * @throws UnsupportedOperationException If {@link StorageMethod#isDatabase()}
	 */
	public File getFlatFile() {
		StorageMethod method = StorageManager.getInstance().getMethod();
		
		if (method.isFlatFile())
			return new File(StorageManager.getInstance().getFolder(), getName() + (method == StorageMethod.YAML ? ".yml" : ".json"));
		throw new UnsupportedOperationException("Unable to obtain a data container's file when using " + method.getName() + " as storage method.");
	}
	
	/**
	 * Gets a data container by its database table's ID with the prefix.
	 * Will return <code>null</code> if the specified ID is invalid.
	 * 
	 * <p><strong>Example:</strong> (with default settings) <code>"chatplugin_mutes"</code> -&gt; {@link #MUTES}</p>
	 * 
	 * @param databaseTableID Database table's ID
	 * @return Corresponding data container
	 * @see DatabaseManager#getTablePrefix()
	 */
	@Nullable(why = "Specified ID may be invalid")
	public static DataContainer getDataContainer(String databaseTableID) {
		for (DataContainer type : values())
			if (type.getDatabaseTableID().equalsIgnoreCase(databaseTableID))
				return type;
		return null;
	}
	
}
