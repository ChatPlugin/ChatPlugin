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

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Statistic;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.statistic.Statistics;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageManager;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.flat_file.FlatFileConnector;
import me.remigio07.chatplugin.api.common.storage.flat_file.FlatFileManager;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.bootstrap.Environment;

public class YAMLConnector extends FlatFileConnector {
	
	private Map<DataContainer, Configuration> yamls = new HashMap<>();
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		
		try {
			for (DataContainer container : DataContainer.values()) {
				Configuration configuration = new Configuration(container.getFlatFile());
				
				configuration.load();
				yamls.put(container, configuration);
			}
		} catch (IOException e) {
			throw new ChatPluginManagerException(FlatFileManager.getInstance(), e);
		}
	}
	
	@Override
	public void unload() throws IOException {
		for (Configuration configuration : yamls.values())
			configuration.save();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T select(DataContainer container, String position, Class<T> type, WhereCondition... conditions) {
		Configuration configuration = yamls.get(container);
		
		for (String id : configuration.getKeys())
			if (checkConditions(id, configuration, conditions))
				return container.getIDColumn().equals(adaptPosition(position)) ? (T) Integer.valueOf(id) : configuration.getMappings().get(id + "." + adaptPosition(position), null);
		return null;
	}
	
	@Override
	public Number count(DataContainer container, WhereCondition... conditions) {
		Configuration configuration = yamls.get(container);
		int amount = 0;
		
		for (String id : configuration.getKeys())
			if (checkConditions(id, configuration, conditions))
				amount++;
		return amount;
	}
	
	@Override
	public int update(DataContainer container, String position, Object data, WhereCondition... conditions) throws IOException {
		Configuration configuration = yamls.get(container);
		int amount = 0;
		
		for (String id : configuration.getKeys())
			if (checkConditions(id, configuration, conditions)) {
				configuration.set(id + "." + adaptPosition(position), data);
				amount++;
			}
		if (amount != 0)
			configuration.save();
		return amount;
	}
	
	@Override
	public int delete(DataContainer container, WhereCondition... conditions) throws IOException {
		Configuration configuration = yamls.get(container);
		Map<String, Object> mappings = configuration.getMappings().getMappings();
		int amount = 0;
		
		for (String id : configuration.getKeys())
			if (checkConditions(id, configuration, conditions)) {
				mappings.remove(id);
				amount++;
			}
		if (amount != 0)
			configuration.save();
		return amount;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> @NotNull List<T> getColumnValues(DataContainer container, String position, Class<T> type, WhereCondition... conditions) {
		Configuration configuration = yamls.get(container);
		List<T> values = new ArrayList<>();
		
		for (String id : configuration.getKeys())
			if (checkConditions(id, configuration, conditions))
				values.add(container.getIDColumn().equals(adaptPosition(position)) ? (T) Integer.valueOf(id) : configuration.getMappings().get(id + "." + adaptPosition(position), null));
		return values;
	}
	
	private boolean checkConditions(String id, Configuration configuration, WhereCondition[] conditions) {
		for (WhereCondition condition : conditions) {
			Object firstTerm = configuration.getMappings().get(id + "." + adaptPosition(condition.getFirstTermPosition()), null);
			Object secondTerm = condition.getSecondTermValue();
			
			if (firstTerm == null)
				return false;
			switch (condition.getOperator()) {
			case EQUAL:
				if (secondTerm instanceof String ? ((String) firstTerm).equalsIgnoreCase((String) secondTerm) : secondTerm instanceof Number ? compare((Number) firstTerm, (Number) secondTerm) == 0 : firstTerm.equals(secondTerm))
					continue;
				return false;
			case NOT_EQUAL:
				if (secondTerm instanceof String ? ((String) firstTerm).equalsIgnoreCase((String) secondTerm) : secondTerm instanceof Number ? compare((Number) firstTerm, (Number) secondTerm) == 0 : firstTerm.equals(secondTerm))
					return false;
				break;
			case GREATER_THAN:
				if (compare((Number) firstTerm, (Number) secondTerm) == 1)
					continue;
				return false;
			case LESS_THAN:
				if (compare((Number) firstTerm, (Number) secondTerm) == -1)
					continue;
				return false;
			case GREATER_THAN_OR_EQUAL:
				if (compare((Number) firstTerm, (Number) secondTerm) != -1)
					continue;
				return false;
			case LESS_THAN_OR_EQUAL:
				if (compare((Number) firstTerm, (Number) secondTerm) != 1)
					continue;
				return false;
			}
		} return true;
	}
	
	private int compare(Number first, Number second) { // does not support NaN and +/- Infinity
		return new BigDecimal(first.toString()).compareTo(new BigDecimal(second.toString()));
	}
	
	@Override
	public @NotNull List<Object> getRowValues(DataContainer container, int id) {
		if (container == DataContainer.MESSAGES)
			throw new IllegalArgumentException("Unable to get row values in container " + container.getName() + " using an ID since that container does not have IDs");
		Configuration configuration = yamls.get(container);
		List<Object> values = new ArrayList<>();
		
		if (!configuration.contains(String.valueOf(id)))
			return values;
		for (String column : container.getColumns())
			values.add(configuration.getMappings().get(id + "." + column, null));
		values.set(0, id);
		return values;
	}
	
	@Override
	public void setData(DataContainer container, String position, int id, @Nullable(why = "Data will become SQL NULL if null") Object data) throws IOException {
		if (container == DataContainer.MESSAGES)
			throw new IllegalArgumentException("Unable to set data to container " + container.getName() + " using an ID since that container does not have IDs");
		yamls.get(container).set(id + "." + adaptPosition(position), data);
		yamls.get(container).save();
	}
	
	@Override
	public @NotNull List<Integer> getIDs(DataContainer container) {
		if (container == DataContainer.MESSAGES)
			throw new IllegalArgumentException("Unable to get IDs in container " + container.getName() + " since that container does not have IDs");
		return getIDs0(container);
	}
	
	private List<Integer> getIDs0(DataContainer container) {
		return yamls.get(container).getKeys().stream().filter(id -> Utils.isPositiveInteger(id)).map(Integer::parseInt).collect(Collectors.toList());
	}
	
	@Override
	public int getNextID(DataContainer container) {
		if (container == DataContainer.MESSAGES)
			throw new IllegalArgumentException("Unable to get next ID in container " + container.getName() + " since that container does not have IDs");
		return getNextID0(container);
	}
	
	private int getNextID0(DataContainer container) {
		List<Integer> ids = getIDs0(container);
		
		return ids.isEmpty() ? 1 : Collections.max(ids) + 1; 
	}
	
	@Override
	public void removeEntry(DataContainer container, int id) throws IOException {
		if (container == DataContainer.MESSAGES)
			throw new IllegalArgumentException("Unable to remove entry in container " + container.getName() + " using an ID since that container does not have IDs");
		Configuration configuration = yamls.get(container);
		
		configuration.getMappings().getMappings().remove(String.valueOf(id));
		configuration.save();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> @Nullable(why = "Stored data may be SQL NULL") T getPlayerData(PlayersDataType<T> type, OfflinePlayer player) {
		Configuration players = yamls.get(DataContainer.PLAYERS);
		String uuid = player.getUUID().toString();
		
		for (String id : players.getKeys())
			if (uuid.equals(players.getString(id + ".player-uuid")))
				return type == PlayersDataType.ID ? (T) Integer.valueOf(id) : players.getMappings().get(id + "." + type.getName(), null);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getPlayerData(PlayersDataType<T> type, int playerID) {
		Configuration players = yamls.get(DataContainer.PLAYERS);
		
		for (int id : players.getKeys().stream().map(Integer::valueOf).collect(Collectors.toList()))
			if (id == playerID)
				return type == PlayersDataType.ID ? (T) Integer.valueOf(id) : players.getMappings().get(id + "." + type.getName(), null);
		return null;
	}
	
	@Override
	public void setPlayerData(PlayersDataType<?> type, OfflinePlayer player, @Nullable(why = "Data will become SQL NULL if null") Object data) throws IOException {
		if (type == PlayersDataType.ID)
			throw new IllegalArgumentException("Unable to change a player's ID");
		try {
			if (isPlayerStored(player)) {
				Configuration players = yamls.get(DataContainer.PLAYERS);
				String uuid = player.getUUID().toString();
				
				for (String id : players.getKeys()) {
					if (uuid.equals(players.getString(id + ".player-uuid"))) {
						players.set(id + "." + type.getName(), data);
						players.save();
						break;
					}
				}
			} else LogManager.log("The plugin tried to write data into the storage (container: {0}, data type: {1}) for a player ({2}) who has never played on the server. Data: \"{3}\".", 2, DataContainer.PLAYERS.getName(), type.getName(), player.getName(), String.valueOf(data));
		} catch (SQLException e) {
			// never called
		}
	}
	
	@Override
	public void setPlayerData(PlayersDataType<?> type, int playerID, @Nullable(why = "Data will become SQL NULL if null") Object data) throws IOException {
		if (type == PlayersDataType.ID)
			throw new IllegalArgumentException("Unable to change a player's ID");
		try {
			if (isPlayerStored(playerID)) {
				Configuration players = yamls.get(DataContainer.PLAYERS);
				
				for (int id : players.getKeys().stream().map(Integer::valueOf).collect(Collectors.toList())) {
					if (id == playerID) {
						players.set(id + "." + type.getName(), data);
						players.save();
						break;
					}
				}
			} else LogManager.log("The plugin tried to write data into the storage (container: {0}, data type: {1}) for a player (ID: #{2}) who has never played on the server. Data: \"{3}\".", 2, DataContainer.PLAYERS.getName(), type.getName(), playerID, String.valueOf(data));
		} catch (SQLException e) {
			// never called
		}
	}
	
	@Override
	public @NotNull List<OfflinePlayer> getPlayers(InetAddress ipAddress, boolean includeOlder) {
		Configuration playersConfiguration = yamls.get(DataContainer.PLAYERS);
		Configuration ipAddressesConfiguration = yamls.get(DataContainer.IP_ADDRESSES);
		List<OfflinePlayer> players = new ArrayList<>();
		String hostAddress = ipAddress.getHostAddress();
		
		try {
			for (String id : playersConfiguration.getKeys())
				if (playersConfiguration.contains(id + ".player-ip") && hostAddress.equals(playersConfiguration.getString(id + ".player-ip")))
					players.add(new OfflinePlayer(
							UUID.fromString(playersConfiguration.getString(id + ".player-uuid")),
							playersConfiguration.getString(id + ".player-name")
							));
				else if (includeOlder && (ipAddressesConfiguration.contains(id + ".ip-addresses") && Utils.getListFromString(ipAddressesConfiguration.getString(id + ".ip-addresses")).contains(ipAddress.getHostAddress())))
					players.add(getPlayer(Integer.valueOf(id)));
		} catch (SQLException e) {
			// never called
		} return players;
	}
	
	@Override
	public void insertNewPlayer(OfflinePlayer player) throws IOException {
		Configuration players = yamls.get(DataContainer.PLAYERS);
		int id = getNextID(DataContainer.PLAYERS);
		
		players.set(id + ".player-uuid", player.getUUID().toString());
		players.set(id + ".player-name", player.getName());
		
		if (player.isOnline()) {
			players.set(id + ".player-ip", player.getIPAddress().getHostAddress());
			
			if (Environment.isBukkit())
				players.set(id + ".time-played", player.toAdapter().bukkitValue().getStatistic(Statistic.valueOf(VersionUtils.getVersion().getProtocol() < 341 ? "PLAY_ONE_TICK" : "PLAY_ONE_MINUTE")) * 50);
			else if (Environment.isSponge())
				players.set(id + ".time-played", player.toAdapter().spongeValue().getStatisticData().get(Keys.STATISTICS).get().get(Statistics.TIME_PLAYED) * 50);
		} players.save();
	}
	
	@Override
	public void cleanOldPlayers() {
		if (StorageManager.getInstance().getPlayersAutoCleanerPeriod() != -1)
			TaskManager.runAsync(() -> {
				Configuration players = yamls.get(DataContainer.PLAYERS), ipAddresses = yamls.get(DataContainer.IP_ADDRESSES);
				Map<String, Object> playersMappings = players.getMappings().getMappings(), ipAddressesMappings = ipAddresses.getMappings().getMappings();
				long ms = System.currentTimeMillis();
				int old = 0;
				
				for (String id : new ArrayList<>(players.getKeys()))
					if (players.getLong(id + ".last-logout") < System.currentTimeMillis() - StorageManager.getInstance().getPlayersAutoCleanerPeriod()) {
						playersMappings.remove(id);
						ipAddressesMappings.remove(id);
						old++;
					}
				
				try {
					players.save();
					ipAddresses.save();
				} catch (IOException e) {
					LogManager.log("IOException occurred while cleaning old players from the database: {0}", 2, e.getMessage());
				} if (old > 0)
					LogManager.log("[ASYNC] Cleaned {0} old player{1} from the storage in {2} ms.", 4, old, old == 1 ? "" : "s", System.currentTimeMillis() - ms);
			}, 0L);
	}
	
}
