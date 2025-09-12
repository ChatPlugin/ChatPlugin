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

package me.remigio07.chatplugin.api.common.player;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.permission.PermissionIntegration;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition.WhereOperator;
import me.remigio07.chatplugin.api.common.storage.StorageMethod;
import me.remigio07.chatplugin.api.common.util.UUIDFetcher;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.proxy.player.ChatPluginProxyPlayer;
import me.remigio07.chatplugin.api.proxy.player.ProxyPlayerManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents an offline player.
 * 
 * <p>This object contains just two values: a {@link UUID} and a name.
 * You can manually create offline players to interact with the plugin's code
 * (for example with the punishment system) from an online player or just a
 * UUID and a name. They may also be used to interact with the storage.</p>
 * 
 * <p><strong>Note:</strong> "offline player" here has
 * nothing to do with non-paid accounts, it simply indicates
 * a player who may or may not be online at the moment.</p>
 * 
 * @see ChatPluginPlayer
 * @see PlayerAdapter
 */
public class OfflinePlayer {
	
	protected UUID uuid;
	protected String name;
	
	/**
	 * Constructs an offline player using a
	 * {@link PlayerAdapter}'s UUID and name.
	 * 
	 * @param player Online player
	 */
	public OfflinePlayer(PlayerAdapter player) {
		this(player.getUUID(), player.getName());
	}
	
	/**
	 * Constructs an offline player using a
	 * {@link CommandSenderAdapter}'s UUID and name.
	 * 
	 * @param sender Command sender
	 */
	public OfflinePlayer(CommandSenderAdapter sender) {
		this(sender.getUUID(), sender.getName());
	}
	
	/**
	 * Constructs an offline player using given UUID and name.
	 * 
	 * <p><strong>Note:</strong> this constructor
	 * is not checked and you should use it only
	 * if you are sure that the specified UUID and
	 * name correspond to an existing account.</p>
	 * 
	 * @param uuid Player's UUID
	 * @param name Player's name
	 */
	public OfflinePlayer(@NotNull UUID uuid, @NotNull String name) {
		this.uuid = uuid;
		this.name = name;
	}
	
	/**
	 * Gets this player's hash code.
	 * 
	 * <p>Will return {@link #getUUID()}'s (if running on online mode)
	 * or {@link #getName()}'s (if running on offline mode) hash code
	 * or -1 if the name is <code>null</code>.</p>
	 * 
	 * @return Player's hash code
	 * @throws IllegalStateException If {@link ChatPlugin#isOnlineMode()} cannot be run yet
	 */
	@Override
	public int hashCode() {
		return ChatPlugin.getInstance().isOnlineMode() ? uuid.hashCode() : name == null ? -1 : name.hashCode();
	}
	
	/**
	 * Checks if another object is an instance of {@link OfflinePlayer} and if this
	 * player's {@link #getUUID()} (if running on online mode) or {@link #getName()}
	 * (if running on offline mode) value is equal to the other object's one.
	 * 
	 * @param obj Object to compare
	 * @return Whether the two objects are equal
	 * @throws IllegalStateException If {@link ChatPlugin#isOnlineMode()} cannot be run yet
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof OfflinePlayer ? ChatPlugin.getInstance().isOnlineMode() ? ((OfflinePlayer) obj).getUUID().equals(uuid) : ((OfflinePlayer) obj).getName() == null ? false : ((OfflinePlayer) obj).getName().equalsIgnoreCase(name) : false;
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "OfflinePlayer{", "}")
				.add("uuid=" + uuid.toString())
				.add("name=\"" + name + "\"")
				.toString();
	}
	
	/**
	 * Checks if this player corresponds
	 * to a paid Java or Bedrock account.
	 * 
	 * @return Whether this is a paid account
	 */
	public boolean isPaidAccount() {
		return uuid.version() == 4 || uuid.version() == 0;
	}
	
	/**
	 * Gets this player's UUID.
	 * 
	 * @return Player's UUID
	 */
	@NotNull
	public UUID getUUID() {
		return uuid;
	}
	
	/**
	 * Gets this player's name.
	 * 
	 * @return Player's name
	 */
	@NotNull
	public String getName() {
		return name;
	}
	
	/**
	 * Gets this player's IP address.
	 * 
	 * <p>Will grab it from the storage if they are offline, but it will return
	 * <code>null</code> if they have never joined the server before.</p>
	 * 
	 * <p><strong>Note:</strong> BungeeCord supports connections via <a href="https://en.wikipedia.org/wiki/Unix_domain_socket">Unix domain sockets</a>.
	 * If this method is called on a player connected through a Unix domain socket, "127.0.0.1" is returned.</p>
	 * 
	 * @return Player's IP address
	 */
	@Nullable(why = "IP address may not be stored")
	public InetAddress getIPAddress() {
		PlayerAdapter player = PlayerAdapter.getPlayer(uuid);
		
		if (player == null)
			try {
				return Utils.getInetAddress(StorageConnector.getInstance().getPlayerData(PlayersDataType.PLAYER_IP, this).toString());
			} catch (Exception e) {
				return null;
			}
		return player.getIPAddress();
	}
	
	/**
	 * Checks if this player is online.
	 * 
	 * @return Whether this player is online
	 */
	public boolean isOnline() {
		return PlayerAdapter.getPlayer(uuid) != null;
	}
	
	/**
	 * Checks if this player is loaded.
	 * 
	 * @return Whether this player is loaded
	 * @see PlayerManager
	 */
	public boolean isLoaded() {
		return isOnline() && PlayerManager.getInstance().getPlayer(uuid) != null;
	}
	
	/**
	 * Checks if this player is contained in the {@link DataContainer#PLAYERS}
	 * data container using {@link StorageConnector#isPlayerStored(OfflinePlayer)}.
	 * 
	 * @return Whether this player has played before
	 */
	public boolean hasPlayedBefore() {
		try {
			return StorageConnector.getInstance().isPlayerStored(this);
		} catch (SQLException e) {
			return false;
		}
	}
	
	/**
	 * Checks if this player has the specified permission.
	 * 
	 * <p>Will always return <code>false</code> unless
	 * {@link #isOnline()} or a {@link PermissionIntegration} is
	 * enabled: in those cases, it will be actually checked.</p>
	 * 
	 * <p><strong>Note:</strong> this method might take some
	 * time to be executed: async calls are recommended.</p>
	 * 
	 * @param permission Permission to check
	 * @return Whether this player has the permission
	 */
	public boolean hasPermission(String permission) {
		return isOnline()
				? toAdapter().hasPermission(permission)
				: IntegrationType.VAULT.isEnabled() && IntegrationType.VAULT.get().getAPI() != null
				? ((PermissionIntegration) IntegrationType.VAULT.get()).hasPermission(this, permission)
				: IntegrationType.LUCKPERMS.isEnabled()
				? IntegrationType.LUCKPERMS.get().hasPermission(this, permission)
				: false;
	}
	
	/**
	 * Gets this player's corresponding {@link PlayerAdapter} object.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isOnline()}.</p>
	 * 
	 * @return Corresponding {@link PlayerAdapter}
	 */
	@Nullable(why = "Player may not be online")
	public PlayerAdapter toAdapter() {
		return PlayerAdapter.getPlayer(uuid);
	}
	
	/**
	 * Gets this player's corresponding {@link ChatPluginServerPlayer} object.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isLoaded()}.</p>
	 * 
	 * @return Corresponding {@link ChatPluginServerPlayer}
	 * @throws UnsupportedOperationException If {@link Environment#isProxy()}
	 */
	@Nullable(why = "Player may not be loaded")
	public ChatPluginServerPlayer toServerPlayer() {
		if (Environment.isProxy())
			throw new UnsupportedOperationException("Unable to get a ChatPluginServerPlayer on a " + Environment.getCurrent().getName() + " environment");
		return ServerPlayerManager.getInstance().getPlayer(uuid);
	}
	
	/**
	 * Gets this player's corresponding {@link ChatPluginProxyPlayer} object.
	 * 
	 * <p>Will return <code>null</code> if <code>!</code>{@link #isLoaded()}.</p>
	 * 
	 * @return Corresponding {@link ChatPluginProxyPlayer}
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isProxy()}
	 */
	@Nullable(why = "Player may not be loaded")
	public ChatPluginProxyPlayer toProxyPlayer() {
		if (Environment.isProxy())
			return ProxyPlayerManager.getInstance().getPlayer(uuid);
		throw new UnsupportedOperationException("Unable to get a ChatPluginProxyPlayer on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets an offline player, possibly from storage, based on the input:
	 * 	<ul>
	 * 		<li>a name starting with {@link PlayerManager#getFloodgateUsernamePrefix()} for a Bedrock account</li>
	 * 		<li>any other name while {@link ChatPlugin#isOnlineMode()} for a paid Java account</li>
	 * 		<li>any other name while <code>!</code>{@link ChatPlugin#isOnlineMode()} for a non-paid Java account</li>
	 * 	</ul>
	 * 
	 * <p>The future is instantly completed and will not throw any exception if:
	 * 	<ul>
	 * 		<li>a player with the specified name is online</li>
	 * 		<li>specified name is not a Bedrock username and <code>!</code>{@link ChatPlugin#isOnlineMode()}</li>
	 * 	</ul>
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link SQLException} if the storage lookup fails and {@link StorageMethod#isDatabase()}</li>
	 * 		<li>{@link InterruptedException} if the future is suddenly interrupted</li>
	 * 		<li>{@link NoSuchElementException} if the name does not belong to a paid Java (if {@link ChatPlugin#isOnlineMode()}) or Bedrock account</li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs while fetching</li>
	 * 	</ul>
	 * 
	 * @param name Player's name, case insensitive
	 * @return New offline player
	 * @throws IllegalArgumentException If specified name <code>!</code>{@link PlayerManager#isValidUsername(String)}
	 */
	public static CompletableFuture<OfflinePlayer> get(String name) {
		PlayerAdapter player = PlayerAdapter.getPlayer(name, true);
		
		if (player == null) {
			if ((!PlayerManager.getInstance().getFloodgateUsernamePrefix().isEmpty() && name.startsWith(PlayerManager.getInstance().getFloodgateUsernamePrefix())) || ChatPlugin.getInstance().isOnlineMode()) {
				CompletableFuture<OfflinePlayer> future = new CompletableFuture<>();
				
				TaskManager.runAsync(() -> {
					try {
						String storageUUID = StorageConnector.getInstance().select(DataContainer.PLAYERS, "player_uuid", String.class, new WhereCondition("player_name", WhereOperator.EQUAL, name));
						UUID uuid;
						final String finalName;
						
						if (storageUUID == null) { // TODO: we could make a single request to fetch UUID and name at the same time
							uuid = UUIDFetcher.getInstance().getOnlineUUID(name).get(); // timeout is 10K ms from UUIDFetcherImpl#readURL(String)
							finalName = UUIDFetcher.getInstance().getName(uuid).get();
						} else {
							uuid = UUID.fromString(storageUUID);
							finalName = StorageConnector.getInstance().select(DataContainer.PLAYERS, "player_name", String.class, new WhereCondition("player_uuid", WhereOperator.EQUAL, uuid));
						} future.complete(new OfflinePlayer(uuid, finalName));
					} catch (SQLException | InterruptedException e) {
						future.completeExceptionally(e);
					} catch (ExecutionException ee) {
						future.completeExceptionally(ee.getCause()); // NSEE | IOE
					}
				}, 0L);
				return future;
			} return CompletableFuture.completedFuture(new OfflinePlayer(UUIDFetcher.getInstance().getOfflineUUID(name), name));
		} return CompletableFuture.completedFuture(new OfflinePlayer(player));
	}
	
	/**
	 * Gets an offline player, possibly from storage, based on the input:
	 * 	<ul>
	 * 		<li>{@link UUID}v0 for a Bedrock account</li>
	 * 		<li>{@link UUID}v3 for a non-paid Java account</li>
	 * 		<li>{@link UUID}v4 for a paid Java account</li>
	 * 	</ul>
	 * 
	 * <p>The future throws:
	 * 	<ul>
	 * 		<li>{@link UnsupportedOperationException} if {@link UUID#version()}<code> == 3</code> and no name is stored for the specified UUID</li>
	 * 		<li>{@link SQLException} if the storage lookup fails and {@link StorageMethod#isDatabase()}</li>
	 * 		<li>{@link InterruptedException} if the future is suddenly interrupted</li>
	 * 		<li>{@link NoSuchElementException} if {@link UUID#version()}<code> != 3</code> and it does not belong to a paid Java or Bedrock account</li>
	 * 		<li>{@link IOException} if a connection or JSON error occurs while fetching</li>
	 * 	</ul>
	 * 
	 * @param uuid Player's UUID
	 * @return New offline player
	 * @throws IllegalArgumentException If {@link UUID#version()} is not 0, 3 or 4
	 */
	public static CompletableFuture<OfflinePlayer> get(UUID uuid) {
		switch (uuid.version()) {
		case 0:
		case 3:
		case 4:
			CompletableFuture<OfflinePlayer> future = new CompletableFuture<>();
			
			TaskManager.runAsync(() -> {
				try {
					String name = StorageConnector.getInstance().select(DataContainer.PLAYERS, "player_name", String.class, new WhereCondition("player_uuid", WhereOperator.EQUAL, uuid.toString()));
					
					future.complete(new OfflinePlayer(uuid, name == null ? UUIDFetcher.getInstance().getName(uuid).get() : name));
				} catch (IllegalArgumentException iae) {
					future.completeExceptionally(new UnsupportedOperationException("Unable to obtain a player name from an offline UUIDv3"));
				} catch (SQLException | InterruptedException e) {
					future.completeExceptionally(e);
				} catch (ExecutionException ee) {
					future.completeExceptionally(ee.getCause()); // NSEE | IOE
				}
			}, 0L);
			return future;
		default:
			throw new IllegalArgumentException("Specified UUIDv" + uuid.version() + " is not a valid Bedrock (v0), Java offline (v3) or Java online (v4) UUID");
		}
	}
	
}
