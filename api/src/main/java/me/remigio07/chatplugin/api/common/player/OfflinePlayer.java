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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.player;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.UUID;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.integration.permission.PermissionIntegration;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition.WhereOperator;
import me.remigio07.chatplugin.api.common.util.UUIDFetcher;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.proxy.player.ChatPluginProxyPlayer;
import me.remigio07.chatplugin.api.proxy.player.ProxyPlayerManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents an offline player. This object contains just two values:
 * an {@link UUID} and a name. You can manually create offline players to interact with
 * the plugin's code (for example with the punishment system) from an online player
 * or just an UUID and a name. They may also be used to interact with the storage.
 * 
 * @see ChatPluginPlayer
 * @see PlayerAdapter
 */
public class OfflinePlayer {
	
	protected UUID uuid;
	protected String name;
	
	/**
	 * Constructs an offline player using an
	 * existing {@link PlayerAdapter}'s UUID and name.
	 * 
	 * @param player Online player
	 */
	public OfflinePlayer(PlayerAdapter player) {
		uuid = player.getUUID();
		name = player.getName();
	}
	
	/**
	 * Constructs an offline player using an
	 * existing {@link CommandSenderAdapter}'s UUID and name.
	 * 
	 * @param sender Command sender
	 */
	public OfflinePlayer(CommandSenderAdapter sender) {
		uuid = sender.getUUID();
		name = sender.getName();
	}
	
	/**
	 * Constructs an offline player using given
	 * UUID and name. Note that this constructor
	 * is not checked and you should use it only
	 * if you are sure that the specified UUID
	 * and name corresponds to an existing account,
	 * which could be either premium or cracked.
	 * 
	 * @param uuid Player's UUID
	 * @param name Player's name
	 */
	public OfflinePlayer(@NotNull UUID uuid, @NotNull String name) {
		this.uuid = uuid;
		this.name = name;
	}
	
	/**
	 * Gets an offline player by their UUID. In order to obtain
	 * their name, the following operations will be performed:
	 * 
	 * 	<ul>
	 * 		<li>if they are online, use the online player's name</li>
	 * 		<li>check if they are saved in {@link DataContainer#PLAYERS}</li>
	 * 		<li>check the {@link UUID#version()}:
	 * 			<br>&emsp;if == 3, throw {@link UnsupportedOperationException}
	 * 			<br>&emsp;if == 4, check if the UUID belongs to a premium account:
	 * 				<br>&emsp;&emsp;if true, fetch the name from Mojang's servers
	 * 				<br>&emsp;&emsp;if false, set name to <code>null</code> and UUID to {@link Utils#NIL_UUID}
	 * 		</li>
	 * 	</ul>
	 * 
	 * Note that this constructor might take some time to be executed: async calls are recommended.
	 * 
	 * @param uuid Player's UUID
	 * @throws UnsupportedOperationException When trying to obtain the name using an offline UUID
	 * @throws IOException If name fetching is required and could not be completed
	 */
	public OfflinePlayer(@NotNull UUID uuid) throws IOException {
		PlayerAdapter player = PlayerAdapter.getPlayer(uuid);
		
		if (player == null) {
			String name = StorageConnector.getInstance().safeSelect(DataContainer.PLAYERS, "player_name", null, new WhereCondition("player_uuid", WhereOperator.EQUAL, uuid.toString()));
			
			if (name == null) {
				if (uuid.version() == 4) {
					this.name = UUIDFetcher.getInstance().getOnlineName(uuid);
					
					if (this.name == null)
						this.uuid = Utils.NIL_UUID;
				} else throw new UnsupportedOperationException("Unable to obtain a player name from an offline UUID");
			} else this.name = name;
		} else name = player.getName();
		this.uuid = uuid;
	}
	
	/**
	 * Gets an offline player by their name. In order to obtain
	 * their UUID, the following operations will be performed:
	 * 
	 * 	<ul>
	 * 		<li>if specified name <code>!</code>{@link Utils#isValidUsername(String)}, throw {@link IllegalArgumentException}</li>
	 * 		<li>if they are online, use the online player's UUID and name</li>
	 * 		<li>check if they are saved in {@link DataContainer#PLAYERS}:
	 * 			<br>&emsp;if true, use the UUID and name stored in the storage
	 * 			<br>&emsp;if false, check if {@link ChatPlugin#isOnlineMode()}:
	 * 				<br>&emsp;&emsp;if false, use {@link UUIDFetcher#getOfflineUUID(String)}
	 * 				<br>&emsp;&emsp;if true, check if the name belongs to a premium account:
	 * 					<br>&emsp;&emsp;&emsp;if true, fetch the name from Mojang's servers
	 * 					<br>&emsp;&emsp;&emsp;if false, set name to <code>null</code> and UUID to {@link Utils#NIL_UUID}
	 * 		</li>
	 * 	</ul>
	 * 
	 * Note that this constructor might take some time to be executed: async calls are recommended.
	 * 
	 * @param name Player's name
	 * @throws IllegalArgumentException If specified name <code>!</code>{@link Utils#isValidUsername(String)}
	 * @throws IOException If name or UUID fetching is required and could not be completed
	 */
	public OfflinePlayer(@NotNull String name) throws IOException { // I've finally realized why developers hate offline mode - it's just a mess
		if (!Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		PlayerAdapter player = PlayerAdapter.getPlayer(name, false);
		
		if (player == null) {
			String uuid = StorageConnector.getInstance().safeSelect(DataContainer.PLAYERS, "player_uuid", null, new WhereCondition("player_name", WhereOperator.EQUAL, name));
			
			if (uuid == null) {
				if (ChatPlugin.getInstance().isOnlineMode()) {
					UUID onlineUUID = UUIDFetcher.getInstance().getOnlineUUID(name);
					
					if (onlineUUID.equals(Utils.NIL_UUID)) {
						this.uuid = onlineUUID;
						this.name = null;
					} else this.name = UUIDFetcher.getInstance().getOnlineName(onlineUUID);
				} else {
					this.uuid = UUIDFetcher.getInstance().getOfflineUUID(name);
					this.name = name;
				}
			} else {
				this.uuid = UUID.fromString(uuid);
				this.name = StorageConnector.getInstance().safeSelect(DataContainer.PLAYERS, "player_name", null, new WhereCondition("player_uuid", WhereOperator.EQUAL, uuid));
			}
		} else {
			uuid = player.getUUID();
			this.name = player.getName();
		}
	}
	
	/**
	 * Checks if the specified player corresponds
	 * to a registered premium username.
	 * 
	 * @return Whether this player is premium
	 */
	public boolean isPremiumAccount() {
		return uuid.version() == 4;
	}
	
	/**
	 * Checks if an object is equal this offline player.
	 * An offline player is considered equal to another one
	 * if they have the same UUID if running on online mode or
	 * if they have the same name ignoring case if running on offline mode.
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof OfflinePlayer ? ChatPlugin.getInstance().isOnlineMode() ? ((OfflinePlayer) obj).getUUID().equals(uuid) : ((OfflinePlayer) obj).getName() == null ? false : ((OfflinePlayer) obj).getName().equalsIgnoreCase(name) : false;
	}
	
	/**
	 * Gets this player's UUID. Will return {@link Utils#NIL_UUID} if
	 * {@link ChatPlugin#isOnlineMode()} and the UUID or the name specified
	 * at construction did not belong to a premium player.
	 * 
	 * @return Player's UUID
	 */
	@NotNull
	public UUID getUUID() {
		return uuid;
	}
	
	/**
	 * Gets this player's name. Will return <code>null</code> if
	 * {@link ChatPlugin#isOnlineMode()} and the UUID or the name specified
	 * at construction did not belong to a premium player.
	 * 
	 * @return Player's name
	 */
	@Nullable(why = "Null when online mode is enabled and the UUID does not belong to a premium player")
	public String getName() {
		return name;
	}
	
	/**
	 * Gets this player's IP address. Will grab it from the storage if they are offline,
	 * but it will return <code>null</code> if they have never joined the server before.
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
		else return player.getIPAddress();
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
	 * Will always return <code>true</code> unless {@link #isOnline()}
	 * or a permission integration (LuckPerms, Vault) is enabled:
	 * in those cases, it will be really checked.
	 * 
	 * <p>Note: this method may take some time to check the
	 * permission. It is recommended to call it asynchronously.</p>
	 * 
	 * @param permission Permission to check
	 * @return Whether this player has the permission
	 */
	public boolean hasPermission(String permission) {
		return isOnline()
				? toAdapter().hasPermission(permission)
				: IntegrationType.VAULT.isEnabled()
				? ((PermissionIntegration) IntegrationType.VAULT.get()).hasPermission(this, permission)
				: IntegrationType.LUCKPERMS.isEnabled()
				? IntegrationType.LUCKPERMS.get().hasPermission(this, permission)
				: true;
	}
	
	/**
	 * Gets this player's corresponding {@link PlayerAdapter} object.
	 * Will return <code>null</code> if <code>!</code>{@link #isOnline()}.
	 * 
	 * @return Corresponding {@link PlayerAdapter}
	 */
	@Nullable(why = "Player may not be online")
	public PlayerAdapter toAdapter() {
		return PlayerAdapter.getPlayer(uuid);
	}
	
	/**
	 * Gets this player's corresponding {@link ChatPluginServerPlayer} object.
	 * Will return <code>null</code> if <code>!</code>{@link #isLoaded()}.
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
	 * Will return <code>null</code> if <code>!</code>{@link #isLoaded()}.
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
	
}
