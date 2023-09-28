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

package me.remigio07.chatplugin.common.storage.flat_file;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.ban.Ban;
import me.remigio07.chatplugin.api.common.punishment.kick.Kick;
import me.remigio07.chatplugin.api.common.punishment.mute.Mute;
import me.remigio07.chatplugin.api.common.punishment.warning.Warning;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.flat_file.FlatFileConnector;
import me.remigio07.chatplugin.api.common.storage.flat_file.FlatFileManager;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.chat.log.LoggedMessage;

public class JSONConnector extends FlatFileConnector {
	
	private Map<DataContainer, JsonObject> jsons;
	
	@Override
	public void load() throws ChatPluginManagerException {
		try {
			for (DataContainer container : DataContainer.values()) {
				try (FileReader reader = new FileReader(container.getFlatFile())) {
					jsons.put(container, (JsonObject) Jsoner.deserialize(reader));
				}
			}
		} catch (IOException | JsonException e) {
			throw new ChatPluginManagerException(FlatFileManager.getInstance(), e);
		}
	}
	
	@Override
	public void unload() throws SQLException, IOException {
		for (Entry<DataContainer, JsonObject> entry : jsons.entrySet()) {
			try (FileWriter writer = new FileWriter(entry.getKey().getFlatFile())) {
				entry.getValue().toJson(writer);
			}
		}
	}
	
	@Override
	public <T> T select(DataContainer container, String position, Class<T> type, WhereCondition... conditions) throws SQLException {
		return null;
	}
	
	@Override
	public Number count(DataContainer container, WhereCondition... conditions) throws SQLException {
		return null;
	}
	
	@Override
	public int update(DataContainer container, String position, Object data, WhereCondition... conditions) throws SQLException {
		return 0;
	}
	
	@Override
	public int delete(DataContainer container, WhereCondition... conditions) throws SQLException {
		return 0;
	}
	
	@Override
	public <T> @NotNull List<T> getColumnValues(DataContainer container, String position, Class<T> type, WhereCondition... conditions) throws SQLException {
		return null;
	}
	
	@Override
	public @NotNull List<Object> getRowValues(DataContainer container, int id) throws SQLException {
		return null;
	}
	
	@Override
	public void setData(DataContainer container, String position, int id, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException {
		
	}
	
	@Override
	public @NotNull List<Integer> getIDs(DataContainer container) throws SQLException {
		return null;
	}
	
	@Override
	public int getNextID(DataContainer container) throws SQLException {
		return 0;
	}
	
	@Override
	public void removeEntry(DataContainer container, int id) throws SQLException {
		
	}
	
	@Override
	public <T> @Nullable(why = "Stored data may be SQL NULL") T getPlayerData(PlayersDataType<T> type, OfflinePlayer player) throws SQLException {
		return null;
	}
	
	@Override
	public <T> T getPlayerData(PlayersDataType<T> type, int playerID) throws SQLException {
		return null;
	}
	
	@Override
	public void setPlayerData(PlayersDataType<?> type, OfflinePlayer player, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException {
		
	}
	
	@Override
	public void setPlayerData(PlayersDataType<?> type, int playerID, @Nullable(why = "Data will become SQL NULL if null") Object data) throws SQLException, IOException {
		
	}
	
	@Override
	public @NotNull List<OfflinePlayer> getPlayers(InetAddress ipAddress, boolean includeOlder) throws SQLException {
		return null;
	}
	
	@Override
	public void insertNewPlayer(OfflinePlayer player) throws SQLException {
		
	}
	
	@Override
	public void insertNewBan(Ban ban) throws SQLException {
		
	}
	
	@Override
	public void updateBan(Ban oldBan, Ban newBan) throws SQLException {
		
	}
	
	@Override
	public void disactiveBan(Ban ban) throws SQLException {
		
	}
	
	@Override
	public @Nullable(why = "Specified ban may not exist") Ban getBan(int id) throws SQLException {
		return null;
	}
	
	@Override
	public void insertNewWarning(Warning warning) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void disactiveWarning(Warning warning) throws SQLException {
		
	}
	
	@Override
	public @Nullable(why = "Specified warning may not exist") Warning getWarning(int id) throws SQLException {
		return null;
	}
	
	@Override
	public void insertNewKick(Kick kick) throws SQLException {
		
	}
	
	@Override
	public @Nullable(why = "Specified kick may not exist") Kick getKick(int id) throws SQLException {
		return null;
	}
	
	@Override
	public void insertNewMute(Mute mute) throws SQLException {
		
	}
	
	@Override
	public void updateMute(Mute oldMute, Mute newMute) throws SQLException {
		
	}
	
	@Override
	public void disactiveMute(Mute mute) throws SQLException {
		
	}
	
	@Override
	public @Nullable(why = "Specified mute may not exist") Mute getMute(int id) throws SQLException {
		return null;
	}
	
	@Override
	public void insertNewMessage(LoggedMessage message) throws SQLException {
		
	}
	
	@Override
	public void cleanOldPlayers() {
		
	}
	
	@Override
	public void cleanOldMessages() {
		
	}
	
}
