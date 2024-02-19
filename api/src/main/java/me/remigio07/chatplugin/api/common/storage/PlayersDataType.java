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

package me.remigio07.chatplugin.api.common.storage;

import java.util.UUID;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents the data types of {@link DataContainer#PLAYERS}.
 * 
 * <p>This class is a pseudo-{@link Enum}. It contains the following methods:
 * {@link #name()}, {@link #ordinal()}, {@link #valueOf(String)} and {@link #values()}.</p>
 * 
 * @param <T> Data's type
 */
public class PlayersDataType<T> {
	
	/**
	 * Player's ID.
	 */
	public static final PlayersDataType<Integer> ID = new PlayersDataType<>("ID", int.class);
	
	/**
	 * Player's UUID.
	 * 
	 * <p>It is a {@link String} which uses the format supported by {@link UUID#fromString(String)}.</p>
	 */
	public static final PlayersDataType<String> PLAYER_UUID = new PlayersDataType<>("PLAYER_UUID", String.class);
	
	/**
	 * Player's name.
	 */
	public static final PlayersDataType<String> PLAYER_NAME = new PlayersDataType<>("PLAYER_NAME", String.class);
	
	/**
	 * Player's last IP address.
	 */
	public static final PlayersDataType<String> PLAYER_IP = new PlayersDataType<>("PLAYER_IP", String.class);
	
	/**
	 * Player's language's ID.
	 * 
	 * <p>It is a {@link String} which represents a {@link Language#getID()}.</p>
	 */
	public static final PlayersDataType<String> LANGUAGE = new PlayersDataType<>("LANGUAGE", String.class);
	
	/**
	 * Player's last logout time, in milliseconds.
	 */
	public static final PlayersDataType<Long> LAST_LOGOUT = new PlayersDataType<>("LAST_LOGOUT", long.class);
	
	/**
	 * Player's time played, in milliseconds.
	 */
	public static final PlayersDataType<Long> TIME_PLAYED = new PlayersDataType<>("TIME_PLAYED", long.class);
	
	/**
	 * Player's messages sent amount.
	 */
	public static final PlayersDataType<Integer> MESSAGES_SENT = new PlayersDataType<>("MESSAGES_SENT", int.class);
	
	/**
	 * Player's bans' amount.
	 */
	public static final PlayersDataType<Short> BANS = new PlayersDataType<>("BANS", short.class);
	
	/**
	 * Player's warnings' amount.
	 */
	public static final PlayersDataType<Short> WARNINGS = new PlayersDataType<>("WARNINGS", short.class);
	
	/**
	 * Player's kicks' amount.
	 */
	public static final PlayersDataType<Short> KICKS = new PlayersDataType<>("KICKS", short.class);
	
	/**
	 * Player's mutes' amount.
	 */
	public static final PlayersDataType<Short> MUTES = new PlayersDataType<>("MUTES", short.class);
	
	/**
	 * Player's ignored players' IDs.
	 */
	public static final PlayersDataType<String> IGNORED_PLAYERS = new PlayersDataType<String>("IGNORED_PLAYERS", String.class);
	private static final PlayersDataType<?>[] VALUES = new PlayersDataType[] { ID, PLAYER_UUID, PLAYER_NAME, PLAYER_IP, LANGUAGE, LAST_LOGOUT, TIME_PLAYED, MESSAGES_SENT, BANS, WARNINGS, KICKS, MUTES, IGNORED_PLAYERS };
	private String name;
	private Class<T> type;
	
	private PlayersDataType(String name, Class<T> type) {
		this.name = name;
		this.type = type;
	}
	
	/**
	 * Equivalent of {@link Enum#name()}.
	 * 
	 * @return Constant's name
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Equivalent of {@link Enum#ordinal()}.
	 * 
	 * @return Constant's ordinal
	 */
	public int ordinal() {
		for (int i = 0; i < VALUES.length; i++)
			if (this == VALUES[i])
				return i;
		return -1;
	}
	
	/**
	 * Returns <code>name().toLowerCase().replace('_', '-')</code>.
	 * 
	 * @return Data type's name
	 */
	public String getName() {
		return getDatabaseTableID().replace('_', '-');
	}
	
	/**
	 * Gets this data type's database table's ID.
	 * 
	 * @return Database table's ID
	 */
	public String getDatabaseTableID() {
		return name().toLowerCase();
	}
	
	/**
	 * Gets this data type's content's class.
	 * 
	 * @return Data type's content's class
	 */
	public Class<T> getType() {
		return type;
	}
	
	/**
	 * Equivalent of <code>Enum#valueOf(String)</code>,
	 * with the only difference that instead of throwing
	 * {@link IllegalArgumentException} <code>null</code>
	 * is returned if the constant's name is invalid.
	 * 
	 * @param name Constant's name
	 * @return Enum constant
	 */
	@Nullable(why = "Instead of throwing IllegalArgumentException null is returned if the constant's name is invalid")
	public static PlayersDataType<?> valueOf(String name) {
		for (PlayersDataType<?> column : VALUES)
			if (column.name().equals(name))
				return column;
		return null;
	}
	
	/**
	 * Equivalent of <code>Enum#values()</code>.
	 * 
	 * @return Enum constants
	 */
	public static PlayersDataType<?>[] values() {
		return VALUES;
	}
	
}
