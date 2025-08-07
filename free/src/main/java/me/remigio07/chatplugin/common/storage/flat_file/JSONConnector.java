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

package me.remigio07.chatplugin.common.storage.flat_file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Statistic;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.statistic.Statistics;

import com.github.cliftonlabs.json_simple.JsonException;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageManager;
import me.remigio07.chatplugin.api.common.storage.flat_file.FlatFileConnector;
import me.remigio07.chatplugin.api.common.storage.flat_file.FlatFileManager;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.bootstrap.Environment;

public class JSONConnector extends FlatFileConnector {
	
	public static final List<String> UPPER_CASE_POSITIONS = Arrays.asList("id", "uuid", "ip");
	protected Map<DataContainer, JsonObject> jsons = new HashMap<>();
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		
		try {
			for (DataContainer container : DataContainer.values()) {
				try (BufferedReader reader = Files.newBufferedReader(container.getFlatFile())) {
					jsons.put(container, (JsonObject) Jsoner.deserialize(reader));
				} catch (JsonException jsone) {
					jsons.put(container, new JsonObject());
				}
			}
		} catch (IOException ioe) {
			throw new ChatPluginManagerException(FlatFileManager.getInstance(), ioe);
		}
	}
	
	@Override
	public void unload() throws IOException {
		for (DataContainer container : jsons.keySet())
			save(container);
		jsons.clear();
	}
	
	protected void save(DataContainer container) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(container.getFlatFile())) {
			writer.write(Jsoner.prettyPrint(jsons.get(container).toJson()) + "\n");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T select(DataContainer container, String position, Class<T> type, WhereCondition... conditions) {
		JsonObject mappings = jsons.get(container);
		
		for (String id : mappings.keySet())
			if (Utils.isPositiveInteger(id) && checkConditions(id, mappings, conditions))
				return (T) (container.getIDColumn().equals(adaptPosition(position)) ? Integer.valueOf(id) : ((JsonObject) mappings.get(id)).getOrDefault(adaptPosition(position), null));
		return null;
	}
	
	@Override
	public Number count(DataContainer container, WhereCondition... conditions) {
		JsonObject mappings = jsons.get(container);
		int amount = 0;
		
		for (String id : mappings.keySet())
			if (Utils.isPositiveInteger(id) && checkConditions(id, mappings, conditions))
				amount++;
		return amount;
	}
	
	@Override
	public int update(DataContainer container, String position, Object data, WhereCondition... conditions) throws IOException {
		JsonObject mappings = jsons.get(container);
		int amount = 0;
		
		for (String id : mappings.keySet())
			if (Utils.isPositiveInteger(id) && checkConditions(id, mappings, conditions)) {
				((JsonObject) mappings.get(id)).put(adaptPosition(position), adaptNumber(data));
				amount++;
			}
		if (amount != 0)
			save(container);
		return amount;
	}
	
	@Override
	public int delete(DataContainer container, WhereCondition... conditions) throws IOException {
		JsonObject mappings = jsons.get(container);
		int amount = 0;
		
		for (String id : new ArrayList<>(mappings.keySet()))
			if (Utils.isPositiveInteger(id) && checkConditions(id, mappings, conditions)) {
				mappings.remove(id);
				amount++;
			}
		if (amount != 0)
			save(container);
		return amount;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> @NotNull List<T> getColumnValues(DataContainer container, String position, Class<T> type, WhereCondition... conditions) {
		JsonObject mappings = jsons.get(container);
		List<T> values = new ArrayList<>();
		
		for (String id : mappings.keySet())
			if (Utils.isPositiveInteger(id) && checkConditions(id, mappings, conditions))
				values.add((T) (container.getIDColumn().equals(adaptPosition(position)) ? Integer.valueOf(id) : ((JsonObject) mappings.get(id)).getOrDefault(adaptPosition(position), null)));
		return values;
	}
	
	private boolean checkConditions(String id, JsonObject mappings, WhereCondition[] conditions) {
		for (WhereCondition condition : conditions) {
			Object firstTerm = ((JsonObject) mappings.get(id)).getOrDefault(adaptPosition(condition.getFirstTermPosition()), null);
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
		if (container == DataContainer.PUBLIC_MESSAGES || container == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to get row values in container " + container.getName() + " using an ID since that container does not have IDs");
		JsonObject mappings = jsons.get(container);
		List<Object> values = new ArrayList<>();
		
		if (!mappings.containsKey(String.valueOf(id)))
			return values;
		for (String column : container.getColumns())
			values.add(((JsonObject) mappings.get(String.valueOf(id))).getOrDefault(column, null));
		values.set(0, id);
		return values;
	}
	
	@Override
	public void setData(DataContainer container, String position, int id, @Nullable(why = "Data will become SQL NULL if null") Object data) throws IOException {
		if (container == DataContainer.PUBLIC_MESSAGES || container == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to set data to container " + container.getName() + " using an ID since that container does not have IDs");
		if (jsons.get(container).get(String.valueOf(id)) == null)
			jsons.get(container).put(String.valueOf(id), new JsonObject());
		((JsonObject) jsons.get(container).get(String.valueOf(id))).put(adaptPosition(position), adaptNumber(data));
		save(container);
	}
	
	@Override
	public @NotNull List<Integer> getIDs(DataContainer container) {
		if (container == DataContainer.PUBLIC_MESSAGES || container == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to get IDs in container " + container.getName() + " since that container does not have IDs");
		return jsons.get(container).keySet().stream().filter(Utils::isPositiveInteger).map(Integer::valueOf).collect(Collectors.toList());
	}
	
	@Override
	public int getNextID(DataContainer container) {
		if (container == DataContainer.PUBLIC_MESSAGES || container == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to get next ID in container " + container.getName() + " since that container does not have IDs");
		if (container == DataContainer.IP_ADDRESSES)
			container = DataContainer.PLAYERS;
		return getNextID0(container);
	}
	
	protected int getNextID0(DataContainer container) {
		return ((BigDecimal) jsons.get(container).getOrDefault("currentID", BigDecimal.ZERO)).intValue() + 1;
	}
	
	@Override
	public void removeEntry(DataContainer container, int id) throws IOException {
		if (container == DataContainer.PUBLIC_MESSAGES || container == DataContainer.PRIVATE_MESSAGES)
			throw new IllegalArgumentException("Unable to remove entry in container " + container.getName() + " using an ID since that container does not have IDs");
		jsons.get(container).remove(String.valueOf(id));
		save(container);
	}
	
	@Override
	public <T> @Nullable(why = "Stored data may be SQL NULL") T getPlayerData(PlayersDataType<T> type, OfflinePlayer player) {
		JsonObject players = jsons.get(DataContainer.PLAYERS);
		String uuid = player.getUUID().toString();
		
		for (String id : players.keySet())
			if (Utils.isPositiveInteger(id) && uuid.equals(((JsonObject) players.get(id)).get("playerUUID")))
				return convertNumber(type == PlayersDataType.ID ? Integer.valueOf(id) : ((JsonObject) players.get(id)).getOrDefault(adaptPosition(type.getName()), null), type);
		return null;
	}
	
	@Override
	public <T> @Nullable(why = "Stored data may be SQL NULL") T getPlayerData(PlayersDataType<T> type, int playerID) {
		JsonObject players = jsons.get(DataContainer.PLAYERS);
		
		for (int id : players.keySet().stream().filter(Utils::isPositiveInteger).map(Integer::valueOf).collect(Collectors.toList()))
			if (id == playerID)
				return convertNumber(type == PlayersDataType.ID ? Integer.valueOf(id) : ((JsonObject) players.get(String.valueOf(id))).getOrDefault(adaptPosition(type.getName()), null), type);
		return null;
	}
	
	@Override
	public void setPlayerData(PlayersDataType<?> type, OfflinePlayer player, @Nullable(why = "Data will become SQL NULL if null") Object data) throws IOException {
		if (type == PlayersDataType.ID)
			throw new IllegalArgumentException("Unable to change a player's ID");
		try {
			if (isPlayerStored(player)) {
				JsonObject players = jsons.get(DataContainer.PLAYERS);
				String uuid = player.getUUID().toString();
				
				for (String id : players.keySet()) {
					if (!Utils.isPositiveInteger(id))
						continue;
					JsonObject map = (JsonObject) players.get(id);
					
					if (uuid.equals(map.get("playerUUID"))) {
						map.put(adaptPosition(type.getName()), adaptNumber(data));
						save(DataContainer.PLAYERS);
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
				JsonObject players = jsons.get(DataContainer.PLAYERS);
				
				for (int id : players.keySet().stream().filter(Utils::isPositiveInteger).map(Integer::valueOf).collect(Collectors.toList())) {
					if (id == playerID) {
						((JsonObject) players.get(String.valueOf(id))).put(adaptPosition(type.getName()), adaptNumber(data));
						save(DataContainer.PLAYERS);
						break;
					}
				}
			} else LogManager.log("The plugin tried to write data into the storage (container: {0}, data type: {1}) for a player (ID: #{2}) who has never played on the server. Data: \"{3}\".", 2, DataContainer.PLAYERS.getName(), type.getName(), playerID, String.valueOf(data));
		} catch (SQLException e) {
			// never called
		}
	}
	
	private Object adaptNumber(Object value) {
		if (value == null)
			return null;
		if (value.getClass() == boolean.class)
			return value;
		if (value instanceof Boolean)
			return ((Boolean) value).booleanValue();
		if (value.getClass() == byte.class)
			return new BigDecimal((byte) value);
		if (value instanceof Byte)
			return new BigDecimal(((Byte) value).byteValue());
		if (value.getClass() == short.class)
			return new BigDecimal((short) value);
		if (value instanceof Short)
			return new BigDecimal(((Short) value).shortValue());
		if (value.getClass() == int.class)
			return new BigDecimal((int) value);
		if (value instanceof Integer)
			return new BigDecimal(((Integer) value).intValue());
		if (value.getClass() == long.class)
			return new BigDecimal((long) value);
		if (value instanceof Long)
			return new BigDecimal(((Long) value).longValue());
		if (value.getClass() == float.class)
			return new BigDecimal((float) value);
		if (value instanceof Float)
			return new BigDecimal(((Float) value).floatValue());
		if (value.getClass() == double.class)
			return new BigDecimal((double) value);
		if (value instanceof Double)
			return new BigDecimal(((Double) value).doubleValue());
		return value.toString();
	}
	
	private String adaptPosition(String position) {
		String[] parts = position.toLowerCase().replace('-', '_').split("_");
		String adaptedPosition = "";
		
		for (int i = 0; i < parts.length; i++)
			adaptedPosition += i == 0 ? parts[i] : UPPER_CASE_POSITIONS.contains(parts[i]) ? parts[i].toUpperCase() : Utils.capitalizeEveryWord(parts[i]);
		return adaptedPosition;
	}
	
	@Override
	public @NotNull List<OfflinePlayer> getPlayers(InetAddress ipAddress, boolean includeOlder) {
		JsonObject playersMappings = jsons.get(DataContainer.PLAYERS), ipAddressesMappings = jsons.get(DataContainer.IP_ADDRESSES);
		List<OfflinePlayer> players = new ArrayList<>();
		String hostAddress = ipAddress.getHostAddress();
		
		try {
			for (String id : playersMappings.keySet()) {
				if (!Utils.isPositiveInteger(id))
					continue;
				JsonObject playerMap = (JsonObject) playersMappings.get(id), ipAddressMap = (JsonObject) ipAddressesMappings.get(id);
				
				if (playerMap.containsKey("playerIP") && hostAddress.equals(playerMap.get("playerIP")))
					players.add(new OfflinePlayer(
							UUID.fromString((String) playerMap.getOrDefault("playerUUID", "")),
							(String) playerMap.getOrDefault("playerName", "")
							));
				else if (includeOlder && (ipAddressMap.containsKey("ipAddresses") && Utils.getListFromString((String) ipAddressMap.get("ipAddresses")).contains(ipAddress.getHostAddress())))
					players.add(getPlayer(Integer.valueOf(id)));
			}
		} catch (SQLException | IllegalArgumentException e) {
			// never called
		} return players;
	}
	
	@Override
	public void insertNewPlayer(OfflinePlayer player) throws IOException {
		JsonObject players = jsons.get(DataContainer.PLAYERS);
		int id = getNextID(DataContainer.PLAYERS);
		JsonObject map = new JsonObject();
		
		map.put("playerUUID", player.getUUID().toString());
		map.put("playerName", player.getName());
		
		if (player.isOnline()) {
			map.put("playerIP", player.getIPAddress().getHostAddress());
			
			if (Environment.isBukkit())
				map.put("timePlayed", new BigDecimal(player.toAdapter().bukkitValue().getStatistic(Statistic.valueOf(VersionUtils.getVersion().getProtocol() < 341 ? "PLAY_ONE_TICK" : "PLAY_ONE_MINUTE")) * 50));
			else if (Environment.isSponge())
				map.put("timePlayed", new BigDecimal(player.toAdapter().spongeValue().getStatisticData().get(Keys.STATISTICS).get().getOrDefault(Statistics.TIME_PLAYED, 0L) * 50));
		} else map.put("timePlayed", 0L);
		
		map.put("messagesSent", 0);
		map.put("antispamInfractions", 0);
		map.put("bans", (short) 0);
		map.put("warnings", (short) 0);
		map.put("kicks", (short) 0);
		map.put("mutes", (short) 0);
		players.put(String.valueOf(id), map);
		players.put("currentID", new BigDecimal(id));
		save(DataContainer.PLAYERS);
	}
	
	@Override
	public void cleanOldPlayers() {
		if (StorageManager.getInstance().getPlayersAutoCleanerPeriod() != -1)
			TaskManager.runAsync(() -> {
				JsonObject players = jsons.get(DataContainer.PLAYERS), ipAddresses = jsons.get(DataContainer.IP_ADDRESSES);
				long ms = System.currentTimeMillis();
				int old = 0;
				
				for (String id : new ArrayList<>(players.keySet())) {
					if (Utils.isPositiveInteger(id) && ((JsonObject) players.get(id)).containsKey("lastLogout") && ((BigDecimal) ((JsonObject) players.get(id)).get("lastLogout")).longValue() < System.currentTimeMillis() - StorageManager.getInstance().getPlayersAutoCleanerPeriod()) {
						players.remove(id);
						ipAddresses.remove(id);
						old++;
					}
				} try {
					save(DataContainer.PLAYERS);
					save(DataContainer.IP_ADDRESSES);
				} catch (IOException e) {
					LogManager.log("IOException occurred while cleaning old players from the database: {0}", 2, e.getMessage());
				} if (old > 0)
					LogManager.log("[ASYNC] Cleaned {0} old player{1} from the storage in {2} ms.", 4, old, old == 1 ? "" : "s", System.currentTimeMillis() - ms);
			}, 0L);
	}
	
	@Override
	public String getEngineName() {
		return Library.JSON_SIMPLE.getName();
	}
	
	@Override
	public String getEngineVersion() {
		String url = Library.JSON_SIMPLE.getURL().toString();
		return url.substring(url.indexOf("simple-") + 7, url.indexOf(".jar"));
	}
	
}
