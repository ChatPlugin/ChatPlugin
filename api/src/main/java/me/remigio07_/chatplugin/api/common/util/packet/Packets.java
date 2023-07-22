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

package me.remigio07_.chatplugin.api.common.util.packet;

import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.punishment.ban.BanType;
import me.remigio07_.chatplugin.api.common.punishment.kick.KickType;
import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.packet.PacketScope.Scope;
import me.remigio07_.chatplugin.api.common.util.packet.type.DiscordMessagePacketType;
import me.remigio07_.chatplugin.api.common.util.packet.type.MessagePacketType;
import me.remigio07_.chatplugin.api.common.util.packet.type.PunishmentPacketType;
import me.remigio07_.chatplugin.api.common.util.packet.type.SilentTeleportPacketType;
import me.remigio07_.chatplugin.api.common.util.packet.type.ViolationPacketType;
import me.remigio07_.chatplugin.api.common.util.text.ComponentTranslator;
import me.remigio07_.chatplugin.api.common.util.text.ComponentTranslator.Component;
import me.remigio07_.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;

/**
 * Class used to create packets using {@link PacketSerializer}.
 * Every inner class of this class represents a category of packets.
 * Deprecated methods and inner classes are internal use only, thus not documented.
 * 
 * @see PacketDeserializer
 */
public class Packets {
	
	/**
	 * Contains packets used to send messages to players.
	 */
	public static class Messages {
		
		/**
		 * Sends a {@link MessagePacketType#PLAIN} message to the specified target(s).
		 * You can specify <code>null</code> as <code>permission</code> if no permission is required to receive the message.
		 * 
		 * 	<p>Available targets:
		 * 		<ul>
		 * 			<li>"ALL CONNECTED" - every player connected to specified <code>server</code>(s)</li>
		 * 			<li>"ALL ENABLED" - every loaded player ({@link OfflinePlayer#isLoaded()}) connected</li>
		 * 			<li>"ALL CONNECTED EXCEPT &lt;player&gt;" - every connected player except the specified one</li>
		 * 			<li>"ALL CONNECTED EXCEPT &lt;IP address&gt;" - every connected player except the ones with given IP</li>
		 * 			<li>"ALL ENABLED EXCEPT &lt;player&gt;" - every loaded player except the specified one</li>
		 * 			<li>"ALL ENABLED EXCEPT &lt;IP address&gt;" - every loaded player except the ones with given IP</li>
		 * 			<li>"ENABLED &lt;player&gt;" - a player; they will receive the message only if they are loaded</li>
		 * 			<li>"ENABLED &lt;IP address&gt;" - players with given IP; they will receive the message only if they are loaded</li>
		 * 			<li>"&lt;player&gt;" - a player; they will receive the message even if they are not loaded</li>
		 * 			<li>"&lt;IP address&gt;" - players with given IP; they will receive the message even if they are not loaded</li>
		 * 			<li>"CONSOLE" - console only; specify <code>false</code> as <code>includeConsole</code> to avoid double messages</li>
		 * 		</ul>
		 * 	</p>
		 * 
		 * @param server Target server
		 * @param targets Target player(s) or console
		 * @param permission Required permission
		 * @param includeConsole Whether to include the console
		 * @param message Message to send
		 * @return <code>PlayerMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer plainPlayerMessage(
				@NotNull String server,
				@NotNull String targets,
				@Nullable(why = "Permission may not be required") String permission,
				boolean includeConsole,
				@NotNull String message
				) {
			return new PacketSerializer("PlayerMessage")
					.writeUTF(server)
					.writeUTF(targets)
					.writeUTF(permission)
					.writeBoolean(includeConsole)
					.writeUTF(MessagePacketType.PLAIN.name())
					.writeUTF(message);
		}
		
		/**
		 * Sends a {@link MessagePacketType#NUMERIC_PLACEHOLDERS} message to the specified target(s).
		 * You can specify <code>null</code> as <code>permission</code> if no permission is required to receive the message.
		 * Refer to {@link #plainPlayerMessage(String, String, String, boolean, String)} to know the available <code>targets</code>.
		 * 
		 * <p>This method supports {@link Component}s: the <code>args</code> array may be composed of strings obtained using
		 * {@link ComponentTranslator#createJSON(Component, Object...)}.</p>
		 * 
		 * <p>The <code>args</code> array cannot contain <code>null</code> elements.</p>
		 * 
		 * @param server Target server
		 * @param targets Target player(s) or console
		 * @param permission Required permission
		 * @param includeConsole Whether to include the console
		 * @param messagePath Message's path
		 * @param args Message's arguments
		 * @return <code>PlayerMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer numericPlaceholdersPlayerMessage(
				@NotNull String server,
				@NotNull String targets,
				@Nullable(why = "Permission may not be required") String permission,
				boolean includeConsole,
				@NotNull String messagePath,
				@NotNull Object... args
				) {
			return new PacketSerializer("PlayerMessage")
					.writeUTF(server)
					.writeUTF(targets)
					.writeUTF(permission)
					.writeBoolean(includeConsole)
					.writeUTF(MessagePacketType.NUMERIC_PLACEHOLDERS.name())
					.writeUTF(messagePath)
					.writeUTFArray(Arrays.asList(args)
							.stream()
							.map(obj -> String.valueOf(obj))
							.collect(Collectors.toList())
							.toArray(new String[0])
							);
		}
		
		/**
		 * Sends a {@link MessagePacketType#CUSTOM_PLACEHOLDERS} message to the specified target(s).
		 * You can specify <code>null</code> as <code>permission</code> if no permission is required to receive the message.
		 * Refer to {@link #plainPlayerMessage(String, String, String, boolean, String)} to know the available <code>targets</code>.
		 * 
		 * <p>This method supports {@link Component}s: the <code>args</code> array may be composed of strings obtained using
		 * {@link ComponentTranslator#createJSON(Component, Object...)}.</p>
		 * 
		 * <p>The <code>placeholders</code> and the <code>args</code> arrays cannot contain <code>null</code> elements.</p>
		 * 
		 * @param server Target server
		 * @param targets Target player(s) or console
		 * @param permission Required permission
		 * @param includeConsole Whether to include the console
		 * @param messagePath Message's path
		 * @param placeholders Message's placeholders
		 * @param args Message's arguments
		 * @return <code>PlayerMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer customPlaceholdersPlayerMessage(
				@NotNull String server,
				@NotNull String targets,
				@Nullable(why = "Permission may not be required") String permission,
				boolean includeConsole,
				@NotNull String messagePath,
				@NotNull String[] placeholders,
				@NotNull Object... args
				) {
			return new PacketSerializer("PlayerMessage")
					.writeUTF(server)
					.writeUTF(targets)
					.writeUTF(permission)
					.writeBoolean(includeConsole)
					.writeUTF(MessagePacketType.CUSTOM_PLACEHOLDERS.name())
					.writeUTF(messagePath)
					.writeUTFArray(placeholders)
					.writeUTFArray(Arrays.asList(args)
							.stream()
							.map(obj -> String.valueOf(obj))
							.collect(Collectors.toList())
							.toArray(new String[0])
							);
		}
		
		/**
		 * Disconnects a player specifying a {@link MessagePacketType#PLAIN} message as the kick's reason.
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @param reason Kick's reason
		 * @return <code>PlayerDisconnect</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer plainPlayerDisconnect(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull String reason
				) {
			return new PacketSerializer("PlayerDisconnect")
					.writeUTF(server)
					.writeUTF(player.toString())
					.writeUTF(MessagePacketType.PLAIN.name())
					.writeUTF(reason);
		}
		
		/**
		 * Disconnects a player specifying a {@link MessagePacketType#NUMERIC_PLACEHOLDERS} message as the kick's reason.
		 * 
		 * <p>This method supports {@link Component}s: the <code>args</code> array may be composed of strings obtained using
		 * {@link ComponentTranslator#createJSON(Component, Object...)}.</p>
		 * 
		 * <p>The <code>args</code> array cannot contain <code>null</code> elements.</p>
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @param reasonPath Kick's reason's path
		 * @param args Kick's reason's arguments
		 * @return <code>PlayerDisconnect</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer numericPlaceholdersPlayerDisconnect(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull String reasonPath,
				@NotNull Object... args
				) {
			return new PacketSerializer("PlayerDisconnect")
					.writeUTF(server)
					.writeUTF(player.toString())
					.writeUTF(MessagePacketType.NUMERIC_PLACEHOLDERS.name())
					.writeUTF(reasonPath)
					.writeUTFArray(Arrays.asList(args)
							.stream()
							.map(obj -> String.valueOf(obj))
							.collect(Collectors.toList())
							.toArray(new String[0])
							);
		}
		
		/**
		 * Disconnects a player specifying a {@link MessagePacketType#CUSTOM_PLACEHOLDERS} message as the kick's reason.
		 * 
		 * <p>This method supports {@link Component}s: the <code>args</code> array may be composed of strings obtained using
		 * {@link ComponentTranslator#createJSON(Component, Object...)}.</p>
		 * 
		 * <p>The <code>args</code> array cannot contain <code>null</code> elements.</p>
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @param reasonPath Kick's reason's path
		 * @param placeholders Kick's reason's placeholders
		 * @param args Kick's reason's packet
		 * @return <code>PlayerDisconnect</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer customPlaceholdersPlayerDisconnect(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull String reasonPath,
				@NotNull String[] placeholders,
				@NotNull Object... args
				) {
			return new PacketSerializer("PlayerDisconnect")
					.writeUTF(server)
					.writeUTF(player.toString())
					.writeUTF(MessagePacketType.CUSTOM_PLACEHOLDERS.name())
					.writeUTF(reasonPath)
					.writeUTFArray(placeholders)
					.writeUTFArray(Arrays.asList(args)
							.stream()
							.map(obj -> String.valueOf(obj))
							.collect(Collectors.toList())
							.toArray(new String[0])
							);
		}
		
		/**
		 * Updates a player's F3 server name.
		 * 
		 * @param server Origin server
		 * @param player Player's UUID
		 * @param value F3 server name's value
		 * @return <code>F3ServerName</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_PROXY)
		public static PacketSerializer f3ServerName(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull String value
				) {
			return new PacketSerializer("F3ServerName")
					.writeUTF(server)
					.writeUUID(player)
					.writeUTF(value);
		}
		
		/**
		 * Sends a {@link DiscordMessagePacketType#PLAIN} message to the specified Discord channel.
		 * 
		 * @param server Origin server
		 * @param sender Message's sender
		 * @param channel Channel to send the message to
		 * @param message Message to send
		 * @return <code>DiscordMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_PROXY)
		public static PacketSerializer plainDiscordMessage(
				@NotNull String server,
				@NotNull String sender,
				long channel,
				@NotNull String message
				) {
			return new PacketSerializer("DiscordMessage")
					.writeUTF(server)
					.writeUTF(sender)
					.writeLong(channel)
					.writeUTF(DiscordMessagePacketType.PLAIN.name())
					.writeUTF(message);
		}
		
		/**
		 * Sends a {@link DiscordMessagePacketType#EMBED} message to the specified Discord channel.
		 * 
		 * @param server Origin server
		 * @param sender Message's sender
		 * @param channel Channel to send the message to
		 * @param json Embed message's JSON representation
		 * @return <code>DiscordMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_PROXY)
		public static PacketSerializer embedDiscordMessage(
				@NotNull String server,
				@NotNull String sender,
				long channel,
				@NotNull String json
				) {
			return new PacketSerializer("DiscordMessage")
					.writeUTF(server)
					.writeUTF(sender)
					.writeLong(channel)
					.writeUTF(DiscordMessagePacketType.EMBED.name())
					.writeUTF(json);
		}
		
	}
	
	/**
	 * Contains packets used to inform the servers about players' joins and quits.
	 */
	public static class JoinQuit {
		
		/**
		 * Makes the target server load the specified player and send their join's message.
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @param versionProtocol Player's version's protocol
		 * @param versionPreNettyRewrite Whether the player is using a pre-Netty rewrite version
		 * @param bedrockEdition Whether the player is playing on Bedrock Edition
		 * @param vanished Whether the player should be vanished and no join message should be sent
		 * @return <code>PlayerJoin</code> packet
		 * @see ServerPlayerManager#loadPlayer(PlayerAdapter) <code>IllegalStateException</code> of this method
		 */
		@PacketScope(Scope.PROXY_TO_SERVER)
		public static PacketSerializer playerJoin(
				@NotNull String server,
				@NotNull UUID player,
				int versionProtocol,
				boolean versionPreNettyRewrite,
				boolean bedrockEdition,
				boolean vanished
				) {
			return new PacketSerializer("PlayerJoin")
					.writeUTF(server)
					.writeUUID(player)
					.writeInt(versionProtocol)
					.writeBoolean(versionPreNettyRewrite)
					.writeBoolean(bedrockEdition)
					.writeBoolean(vanished);
		}
		
		@PacketScope(Scope.PROXY_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerSwitch(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull String newServerID,
				@Nullable(why = "Server's display name may not be specified") String newServerDisplayName
				) {
			return new PacketSerializer("PlayerSwitch")
					.writeUTF(server)
					.writeUUID(player)
					.writeUTF(newServerID)
					.writeUTF(newServerDisplayName);
		}
		
		@PacketScope(Scope.PROXY_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerQuit(
				@NotNull String server,
				@NotNull UUID player
				) {
			return new PacketSerializer("PlayerQuit")
					.writeUTF(server)
					.writeUUID(player);
		}
		
		/**
		 * Makes the target server teleport the specified player to
		 * another player and enable vanish as soon as they switch servers.
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @param targetPlayer Target to teleport the player to
		 * @return <code>SilentTeleport</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer playerSilentTeleport(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull String targetPlayer
				) {
			return new PacketSerializer("SilentTeleport")
					.writeUTF(server)
					.writeUUID(player)
					.writeUTF(SilentTeleportPacketType.PLAYER.name())
					.writeUTF(targetPlayer);
		}
		
		/**
		 * Makes the target server enable vanish for the
		 * specified player as soon as they switch servers.
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @param targetServer Target to teleport the player to
		 * @return <code>SilentTeleport</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer serverSilentTeleport(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull String targetServer
				) {
			return new PacketSerializer("SilentTeleport")
					.writeUTF(server)
					.writeUUID(player)
					.writeUTF(SilentTeleportPacketType.SERVER.name())
					.writeUTF(targetServer);
		}
		
	}
	
	/**
	 * Contains packets used to synchronize servers with the proxy.
	 */
	public static class Sync {
		
		/**
		 * Makes an IP lookup request to the proxy.
		 * 
		 * @param server Target server
		 * @param ipAddress IP address to lookup
		 * @param requesterName Requester's name
		 * @return <code>IPLookupRequest</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_PROXY)
		public static PacketSerializer ipLookupRequest(
				@NotNull String server,
				@NotNull InetAddress ipAddress,
				@Nullable(why = "Requester may not be specified") String requesterName
				) {
			return new PacketSerializer("IPLookupRequest")
					.writeUTF(server)
					.writeUTF(ipAddress.getHostAddress())
					.writeUTF(requesterName);
		}
		
		@PacketScope(Scope.PROXY_TO_SERVER)
		@Deprecated
		public static PacketSerializer ipLookupResponse(
				@NotNull String server,
				@NotNull InetAddress ipAddress,
				@NotNull String isp,
				@NotNull String json
				) {
			return new PacketSerializer("IPLookupResponse")
					.writeUTF(server)
					.writeUTF(ipAddress.getHostAddress())
					.writeUTF(isp)
					.writeUTF(json);
		}
		
		/**
		 * Makes a MoTD request to the proxy for the specified IP address.
		 * 
		 * @param server Target server
		 * @param ipAddress Player's IP address
		 * @param versionProtocol Player's version's protocol
		 * @param versionPreNettyRewrite Whether the player is using a pre-Netty rewrite version
		 * @return <code>MoTDRequest</code> packet
		 */
		@SocketChannelPacket
		@PacketScope(Scope.PROXY_TO_SERVER)
		public static PacketSerializer motdRequest(
				@NotNull String server,
				@NotNull InetAddress ipAddress,
				int versionProtocol,
				boolean versionPreNettyRewrite
				) {
			return new PacketSerializer("MoTDRequest")
					.writeUTF(server)
					.writeUTF(ipAddress.getHostAddress())
					.writeInt(versionProtocol)
					.writeBoolean(versionPreNettyRewrite);
		}
		
		@SocketChannelPacket
		@PacketScope(Scope.SERVER_TO_PROXY)
		@Deprecated
		public static PacketSerializer motdResponse(
				@NotNull String server,
				@NotNull InetAddress ipAddress,
				@NotNull String description,
				@NotNull String hover,
				@NotNull String versionName,
				@NotNull URL customIconURL,
				boolean hoverDisplayed,
				boolean versionNameDisplayed,
				boolean customIconDisplayed,
				int onlinePlayers,
				int maxPlayers
				) {
			return new PacketSerializer("MoTDResponse")
					.writeUTF(server)
					.writeUTF(ipAddress.getHostAddress())
					.writeUTF(description)
					.writeUTF(hover)
					.writeUTF(versionName)
					.writeUTF(customIconURL.toExternalForm())
					.writeBoolean(hoverDisplayed)
					.writeBoolean(versionNameDisplayed)
					.writeBoolean(customIconDisplayed)
					.writeInt(onlinePlayers)
					.writeInt(maxPlayers);
		}
		
		/**
		 * Adds a violation to the specified player's violations
		 * using a packet of type {@link ViolationPacketType#ADD}.
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @param anticheat Anticheat that detected the violations
		 * @param cheatID Cheat's ID
		 * @param component Cheat's component
		 * @param amount Violations' amount
		 * @param ping Player's ping, in milliseconds
		 * @param versionProtocol Player's version's protocol
		 * @param versionPreNettyRewrite Whether the player is using a pre-Netty rewrite version
		 * @param tps Server's ticks per second
		 * @return <code>PlayerViolation</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer addPlayerViolation(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull IntegrationType<AnticheatIntegration> anticheat,
				@NotNull String cheatID,
				@NotNull String component,
				int amount,
				int ping,
				int versionProtocol,
				boolean versionPreNettyRewrite,
				double tps
				) {
			return new PacketSerializer("PlayerViolation")
					.writeUTF(server)
					.writeUUID(player)
					.writeUTF(ViolationPacketType.ADD.name())
					.writeUTF(anticheat.name())
					.writeUTF(cheatID)
					.writeUTF(component)
					.writeInt(amount)
					.writeInt(ping)
					.writeInt(versionProtocol)
					.writeBoolean(versionPreNettyRewrite)
					.writeDouble(tps);
		}
		
		/**
		 * Removes violations of a certain type for the specified player
		 * using a packet of type {@link ViolationPacketType#REMOVE}.
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @param anticheat Anticheat that detected the violations
		 * @param cheatID Cheat's ID
		 * @return <code>PlayerViolation</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer removePlayerViolation(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull IntegrationType<AnticheatIntegration> anticheat,
				@NotNull String cheatID
				) {
			return new PacketSerializer("PlayerViolation")
					.writeUTF(server)
					.writeUUID(player)
					.writeUTF(ViolationPacketType.REMOVE.name())
					.writeUTF(anticheat.name())
					.writeUTF(cheatID);
		}
		
		/**
		 * Clears all violations for the specified player
		 * using a packet of type {@link ViolationPacketType#CLEAR}.
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @return <code>PlayerViolation</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer clearPlayerViolation(
				@NotNull String server,
				@NotNull UUID player
				) {
			return new PacketSerializer("PlayerViolation")
					.writeUTF(server)
					.writeUUID(player)
					.writeUTF(ViolationPacketType.CLEAR.name());
		}
		
		@PacketScope(Scope.PROXY_TO_SERVER)
		@Deprecated
		public static PacketSerializer proxyPluginLoad(
				@NotNull String server,
				long time
				) {
			return new PacketSerializer("ProxyPluginLoad")
					.writeUTF(server)
					.writeLong(time);
		}
		
	}
	
	@Deprecated
	public static class Punishments {
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerBan(
				@NotNull String server,
				int id,
				@Nullable(why = "Player may not be specified when banning an IP address") UUID playerUUID,
				@Nullable(why = "Player may not be specified when banning an IP address") String playerName,
				@Nullable(why = "IP address may not be specified when banning a player") InetAddress ipAddress,
				@NotNull String staffMember,
				@Nullable(why = "Reason may not be specified") String reason,
				@NotNull BanType type,
				long date,
				long duration,
				boolean global,
				boolean silent
				) {
			return new PacketSerializer("PlayerBan")
					.writeUTF(server)
					.writeInt(id)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(ipAddress == null ? null : ipAddress.getHostAddress())
					.writeUTF(staffMember)
					.writeUTF(reason)
					.writeUTF(type.name())
					.writeLong(date)
					.writeLong(duration)
					.writeBoolean(global)
					.writeBoolean(silent);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerBasedPlayerUnban(
				@Nullable(why = "Null to disactive a global ban") String server,
				@Nullable(why = "Null for naturally expired bans") String whoUnbanned,
				long unbanDate,
				@Nullable(why = "Player may not be specified when unbanning an IP address") UUID playerUUID,
				@Nullable(why = "Player may not be specified when unbanning an IP address") String playerName,
				@Nullable(why = "IP address may not be specified when unbanning a player") InetAddress ipAddress,
				@NotNull BanType type
				) {
			return new PacketSerializer("PlayerUnban")
					.writeUTF(server)
					.writeUTF(whoUnbanned)
					.writeLong(unbanDate)
					.writeUTF(PunishmentPacketType.PLAYER_BASED.name())
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(ipAddress == null ? null : ipAddress.getHostAddress())
					.writeUTF(type.name());
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer idBasedPlayerUnban(
				@NotNull String server,
				@Nullable(why = "Null for naturally expired bans") String whoUnbanned,
				long unbanDate,
				int id
				) {
			return new PacketSerializer("PlayerUnban")
					.writeUTF(server)
					.writeUTF(whoUnbanned)
					.writeLong(unbanDate)
					.writeUTF(PunishmentPacketType.ID_BASED.name())
					.writeInt(id);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer addBanwaveEntry(
				@NotNull String server,
				@Nullable(why = "Player may not be specified when banning an IP address") UUID playerUUID,
				@Nullable(why = "Player may not be specified when banning an IP address") String playerName,
				@Nullable(why = "IP address may not be specified when banning a player") InetAddress ipAddress,
				@NotNull String staffMember,
				@Nullable(why = "Reason may not be specified") String reason,
				@NotNull BanType type,
				long date,
				long duration,
				boolean global,
				boolean silent
				) {
			return new PacketSerializer("AddBanwaveEntry")
					.writeUTF(server)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(ipAddress == null ? null : ipAddress.getHostAddress())
					.writeUTF(staffMember)
					.writeUTF(reason)
					.writeUTF(type.name())
					.writeLong(date)
					.writeLong(duration)
					.writeBoolean(global)
					.writeBoolean(silent);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer removeBanwaveEntry(
				@Nullable(why = "Null to remove a global entry") String server,
				@Nullable(why = "Player may not be specified when removing an IP address entry") UUID playerUUID,
				@Nullable(why = "Player may not be specified when removing an IP address entry") String playerName,
				@Nullable(why = "IP address may not be specified when removing a player entry") InetAddress ipAddress,
				@NotNull String whoRemoved,
				@NotNull BanType type,
				long removalDate
				) {
			return new PacketSerializer("RemoveBanwaveEntry")
					.writeUTF(server)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(ipAddress == null ? null : ipAddress.getHostAddress())
					.writeUTF(whoRemoved)
					.writeUTF(type.name())
					.writeLong(removalDate);
		}
		
		@PacketScope(Scope.PROXY_TO_SERVER)
		@Deprecated
		public static PacketSerializer banwaveStart(
				@NotNull String server,
				long estimatedDuration,
				boolean forwardProxyCommands,
				@NotNull String[] startCommands
				) {
			return new PacketSerializer("BanwaveStart")
					.writeUTF(server)
					.writeLong(estimatedDuration)
					.writeBoolean(forwardProxyCommands)
					.writeUTFArray(startCommands);
		}
		
		
		@PacketScope(Scope.PROXY_TO_SERVER)
		@Deprecated
		public static PacketSerializer banwaveEnd(
				@NotNull String server,
				int bannedPlayers,
				boolean forwardProxyCommands,
				@NotNull String[] endCommands
				) {
			return new PacketSerializer("BanwaveEnd")
					.writeUTF(server)
					.writeInt(bannedPlayers)
					.writeBoolean(forwardProxyCommands)
					.writeUTFArray(endCommands);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerWarning(
				@NotNull String server,
				int id,
				@NotNull UUID playerUUID,
				@NotNull String playerName,
				@NotNull String staffMember,
				@Nullable(why = "Reason may not be specified") String reason,
				long date,
				long duration,
				boolean global,
				boolean silent
				) {
			return new PacketSerializer("PlayerWarning")
					.writeUTF(server)
					.writeInt(id)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(staffMember)
					.writeUTF(reason)
					.writeLong(date)
					.writeLong(duration)
					.writeBoolean(global)
					.writeBoolean(silent);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerUnwarn(
				@NotNull String server,
				int id,
				@Nullable(why = "Null for naturally expired warnings") String whoUnwarned,
				long unwarnDate
				) {
			return new PacketSerializer("PlayerUnwarn")
					.writeUTF(server)
					.writeInt(id)
					.writeUTF(whoUnwarned)
					.writeLong(unwarnDate);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerRemoveLastWarning(
				@Nullable(why = "Null to disactive a global warning") String server,
				@NotNull UUID playerUUID,
				@NotNull String playerName,
				@NotNull String whoUnwarned,
				long unwarnDate
				) {
			return new PacketSerializer("PlayerRemoveLastWarning")
					.writeUTF(server)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(whoUnwarned)
					.writeLong(unwarnDate);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerClearWarnings(
				@Nullable(why = "Null to disactive a global warnings") String server,
				@NotNull UUID playerUUID,
				@NotNull String playerName,
				@NotNull String whoUnwarned,
				long unwarnDate
				) {
			return new PacketSerializer("PlayerClearWarnings")
					.writeUTF(server)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(whoUnwarned)
					.writeLong(unwarnDate);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerKick(
				@NotNull String server,
				int id,
				@NotNull UUID playerUUID,
				@NotNull String playerName,
				@NotNull InetAddress ipAddress,
				@NotNull String staffMember,
				@Nullable(why = "Reason may not be specified") String reason,
				@NotNull String lobbyServer,
				@NotNull KickType type,
				long date,
				boolean silent
				) {
			return new PacketSerializer("PlayerKick")
					.writeUTF(server)
					.writeInt(id)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(ipAddress.getHostAddress())
					.writeUTF(staffMember)
					.writeUTF(reason)
					.writeUTF(lobbyServer)
					.writeUTF(type.name())
					.writeLong(date)
					.writeBoolean(silent);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerMute(
				@NotNull String server,
				int id,
				@NotNull UUID playerUUID,
				@NotNull String playerName,
				@NotNull String staffMember,
				@Nullable(why = "Reason may not be specified") String reason,
				long date,
				long duration,
				boolean global,
				boolean silent
				) {
			return new PacketSerializer("PlayerMute")
					.writeUTF(server)
					.writeInt(id)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(staffMember)
					.writeUTF(reason)
					.writeLong(date)
					.writeLong(duration)
					.writeBoolean(global)
					.writeBoolean(silent);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer playerBasedPlayerUnmute(
				@Nullable(why = "Null to disactive a global mute") String server,
				@Nullable(why = "Null for naturally expired mutes") String whoUnmuted,
				long unmuteDate,
				@NotNull UUID playerUUID,
				@NotNull String playerName
				) {
			return new PacketSerializer("PlayerUnmute")
					.writeUTF(server)
					.writeUTF(whoUnmuted)
					.writeLong(unmuteDate)
					.writeUTF(PunishmentPacketType.PLAYER_BASED.name())
					.writeUUID(playerUUID)
					.writeUTF(playerName);
		}
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer idBasedPlayerUnmute(
				@NotNull String server,
				@Nullable(why = "Null for naturally expired mutes") String whoUnmuted,
				long unmuteDate,
				int id
				) {
			return new PacketSerializer("PlayerUnmute")
					.writeUTF(server)
					.writeUTF(whoUnmuted)
					.writeLong(unmuteDate)
					.writeUTF(PunishmentPacketType.ID_BASED.name())
					.writeInt(id);
		}
		
	}
	
	@Deprecated
	public static class Misc {
		
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer serverInformation(
				@NotNull String server,
				int onlinePlayers,
				int vanishedPlayers
				) {
			return new PacketSerializer("ServerInformation")
					.writeUTF(server)
					.writeInt(onlinePlayers)
					.writeInt(vanishedPlayers);
		}
		
		@SocketChannelPacket
		@PacketScope(Scope.PROXY_TO_SERVER)
		@Deprecated
		public static PacketSerializer clientDisconnection(
				@NotNull String reason
				) {
			return new PacketSerializer("ClientDisconnection")
					.writeUTF(reason);
		}
		
	}
	
}
