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

package me.remigio07.chatplugin.api.common.util.packet;

import java.net.InetAddress;
import java.net.URL;
import java.util.UUID;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.ban.BanType;
import me.remigio07.chatplugin.api.common.punishment.kick.KickType;
import me.remigio07.chatplugin.api.common.util.ValueContainer;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.packet.PacketScope.Scope;
import me.remigio07.chatplugin.api.common.util.packet.type.DiscordMessagePacketType;
import me.remigio07.chatplugin.api.common.util.packet.type.PunishmentPacketType;
import me.remigio07.chatplugin.api.common.util.packet.type.SilentTeleportPacketType;
import me.remigio07.chatplugin.api.common.util.packet.type.ViolationPacketType;
import me.remigio07.chatplugin.api.common.util.text.ComponentTranslator;
import me.remigio07.chatplugin.api.common.util.text.ComponentTranslator.Component;
import me.remigio07.chatplugin.api.proxy.util.socket.ClientHandler;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.api.server.util.socket.Client;

/**
 * Class used to create packets using {@link PacketSerializer}.
 * 
 * <p>Every inner class of this class represents a category of packets.
 * Deprecated methods and inner classes are internal use only.</p>
 * 
 * @see PacketDeserializer
 */
public class Packets {
	
	/**
	 * Contains packets used to send messages to players.
	 */
	public static class Messages {
		
		/**
		 * Sends a message to the specified target(s).
		 * 
		 * <p>Available targets:
		 * 	<ul>
		 * 		<li>"ALL CONNECTED" - every player connected to specified <code>server</code>(s)</li>
		 * 		<li>"ALL LOADED" - every loaded player ({@link OfflinePlayer#isLoaded()}) connected</li>
		 * 		<li>"ALL CONNECTED EXCEPT &lt;player&gt;" - every connected player except the specified one</li>
		 * 		<li>"ALL CONNECTED EXCEPT &lt;IP address&gt;" - every connected player except the ones with given IP</li>
		 * 		<li>"ALL CONNECTED OUTSIDE &lt;server&gt;" - every connected player outside of the specified server</li>
		 * 		<li>"ALL LOADED EXCEPT &lt;player&gt;" - every loaded player except the specified one</li>
		 * 		<li>"ALL LOADED EXCEPT &lt;IP address&gt;" - every loaded player except the ones with given IP</li>
		 * 		<li>"ALL LOADED OUTSIDE &lt;server&gt;" - every loaded player outside of the specified server</li>
		 * 		<li>"LOADED &lt;player&gt;" - a player; they will receive the message only if they are loaded</li>
		 * 		<li>"LOADED &lt;IP address&gt;" - players with given IP; they will receive the message only if they are loaded</li>
		 * 		<li>"&lt;player&gt;" - a player; they will receive the message even if they are not loaded</li>
		 * 		<li>"&lt;IP address&gt;" - players with given IP; they will receive the message even if they are not loaded</li>
		 * 		<li>"CONSOLE" - console only; specify <code>false</code> as <code>includeConsole</code> to avoid double messages</li>
		 * 	</ul>
		 * 
		 * <p>You can specify <code>null</code> as <code>permission</code>
		 * if no permission is required to receive the message.</p>
		 * 
		 * <p>If <code>json</code> the message will be treated as a JSON message.</p>
		 * 
		 * <p>This method supports {@link Component}s: the message's content may be
		 * obtained using {@link ComponentTranslator#createJSON(Component, Object...)}.</p>
		 * 
		 * @param server Target server
		 * @param targets Target player(s) or console
		 * @param permission Required permission
		 * @param includeConsole Whether to include the console
		 * @param json Whether this is a JSON message
		 * @param message Message to send
		 * @return <code>PlayerMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer playerMessage(
				@NotNull String server,
				@NotNull String targets,
				@Nullable(why = "Permission may not be required") String permission,
				boolean includeConsole,
				boolean json,
				@NotNull String message
				) {
			return new PacketSerializer("PlayerMessage")
					.writeUTF(server)
					.writeUTF(targets)
					.writeUTF(permission)
					.writeBoolean(includeConsole)
					.writeBoolean(json)
					.writeUTF(message);
		}
		
		/**
		 * Disconnects the specified player player
		 * specifying a message as the kick's reason.
		 * 
		 * <p>This method supports {@link Component}s: the message's content may be
		 * obtained using {@link ComponentTranslator#createJSON(Component, Object...)}.</p>
		 * 
		 * @param server Target server
		 * @param player Player's UUID
		 * @param reason Kick's reason
		 * @return <code>PlayerDisconnect</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer playerDisconnect(
				@NotNull String server,
				@NotNull UUID player,
				@NotNull String reason
				) {
			return new PacketSerializer("PlayerDisconnect")
					.writeUTF(server)
					.writeUUID(player)
					.writeUTF(reason);
		}
		
		/**
		 * Carries information about a private message.
		 * 
		 * @deprecated Internal use only.
		 * @param server Destination server
		 * @param targetServer Target server
		 * @param senderUUID Sender's UUID
		 * @param senderName Sender's name
		 * @param recipientUUID Recipient's UUID
		 * @param recipientName Recipient's name
		 * @param canSeeVanished Whether the sender has {@link VanishManager#VANISH_PERMISSION}
		 * @param allowSocialspy Whether socialspy notifications will be sent
		 * @param placeholders Private message's sender and recipient placeholders
		 * @param privateMessage Private message to send
		 * @return <code>PrivateMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer privateMessage(
				@NotNull String server,
				@NotNull String targetServer,
				@NotNull UUID senderUUID,
				@NotNull String senderName,
				@NotNull UUID recipientUUID,
				@NotNull String recipientName,
				boolean canSeeVanished,
				boolean allowSocialspy,
				@NotNull String[] placeholders,
				@NotNull String privateMessage
				) {
			return new PacketSerializer("PrivateMessage")
					.writeUTF(server)
					.writeUTF(targetServer)
					.writeUUID(senderUUID)
					.writeUTF(senderName)
					.writeUUID(recipientUUID)
					.writeUTF(recipientName)
					.writeBoolean(canSeeVanished)
					.writeBoolean(allowSocialspy)
					.writeUTFArray(placeholders)
					.writeUTF(privateMessage);
		}
		
		/**
		 * Sends a message to the specified chat channel.
		 * 
		 * <p>If <code>json</code> the message will be treated as a JSON message.</p>
		 * 
		 * @param server Origin server
		 * @param targetServer Target server
		 * @param senderUUID Sender's UUID
		 * @param senderName Sender's name
		 * @param chatChannelID Chat channel's ID
		 * @param languageID Language's ID
		 * @param json Whether this is a JSON message
		 * @param message Message to send
		 * @return <code>ChannelMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer chatChannelMessage(
				@NotNull String server,
				@NotNull String targetServer,
				@NotNull UUID senderUUID,
				@NotNull String senderName,
				@NotNull String chatChannelID,
				@NotNull String languageID,
				boolean json,
				@NotNull String message
				) {
			return new PacketSerializer("ChatChannelMessage")
					.writeUTF(server)
					.writeUTF(targetServer)
					.writeUUID(senderUUID)
					.writeUTF(senderName)
					.writeUTF(chatChannelID)
					.writeUTF(languageID)
					.writeBoolean(json)
					.writeUTF(message);
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
		 * Sends a {@link DiscordMessagePacketType#PLAIN}
		 * message to the specified Discord channel.
		 * 
		 * @param server Origin server
		 * @param sender Message's sender
		 * @param channelID Channel to send the message to
		 * @param message Message to send
		 * @return <code>DiscordMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_PROXY)
		public static PacketSerializer plainDiscordMessage(
				@NotNull String server,
				@NotNull String sender,
				long channelID,
				@NotNull String message
				) {
			return new PacketSerializer("DiscordMessage")
					.writeUTF(server)
					.writeUTF(sender)
					.writeLong(channelID)
					.writeUTF(DiscordMessagePacketType.PLAIN.name())
					.writeUTF(message);
		}
		
		/**
		 * Sends a {@link DiscordMessagePacketType#EMBED}
		 * message to the specified Discord channel.
		 * 
		 * @param server Origin server
		 * @param sender Message's sender
		 * @param channelID Channel to send the message to
		 * @param json Embed message's JSON representation
		 * @return <code>DiscordMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_PROXY)
		public static PacketSerializer embedDiscordMessage(
				@NotNull String server,
				@NotNull String sender,
				long channelID,
				@NotNull String json
				) {
			return new PacketSerializer("DiscordMessage")
					.writeUTF(server)
					.writeUTF(sender)
					.writeLong(channelID)
					.writeUTF(DiscordMessagePacketType.EMBED.name())
					.writeUTF(json);
		}
		
		/**
		 * Sends a message to the specified Telegram chat.
		 * 
		 * @param server Origin server
		 * @param sender Message's sender
		 * @param chatID Chat to send the message to
		 * @param message Message to send
		 * @return <code>TelegramMessage</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_PROXY)
		public static PacketSerializer telegramMessage(
				@NotNull String server,
				@NotNull String sender,
				long chatID,
				@NotNull String message
				) {
			return new PacketSerializer("TelegramMessage")
					.writeUTF(server)
					.writeUTF(sender)
					.writeLong(chatID)
					.writeUTF(message);
		}
		
	}
	
	/**
	 * Contains packets used to inform the
	 * servers about players' joins and quits.
	 */
	public static class JoinQuit {
		
		/**
		 * Makes the target server load the specified
		 * player and send their join's message.
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
		
		/**
		 * Makes the target server send a switch
		 * message for the specified player.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param player Player's UUID
		 * @param newServerID New server's ID
		 * @param newServerDisplayName New server's display name
		 * @return <code>PlayerSwitch</code> packet
		 */
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
		
		/**
		 * Makes the target server send a quit
		 * message for the specified player.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param player Player's UUID
		 * @return <code>PlayerQuit</code> packet
		 */
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
		
		/**
		 * Responds to a {@link #ipLookupRequest(String, InetAddress, String)}.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param ipAddress IP lookup's IP address
		 * @param valid Whether this IP lookup is valid
		 * @param isp IP lookup' ISP
		 * @param json IP lookup's JSON representation
		 * @return <code>IPLookupResponse</code> packet
		 */
		@PacketScope(Scope.PROXY_TO_SERVER)
		@Deprecated
		public static PacketSerializer ipLookupResponse(
				@NotNull String server,
				@NotNull InetAddress ipAddress,
				boolean valid,
				@NotNull String isp,
				@NotNull String json
				) {
			return new PacketSerializer("IPLookupResponse")
					.writeUTF(server)
					.writeUTF(ipAddress.getHostAddress())
					.writeBoolean(valid)
					.writeUTF(isp)
					.writeUTF(json);
		}
		
		/**
		 * Makes a MoTD request to the proxy
		 * for the specified IP address.
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
		
		/**
		 * Responds to a {@link #motdRequest(String, InetAddress, int, boolean)}.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param ipAddress MoTD's IP address
		 * @param description MoTD's description
		 * @param hover MoTD's hover
		 * @param versionName MoTD's version name
		 * @param customIconURL MoTD's custom icon's URL
		 * @param hoverDisplayed Whether the hover is visible
		 * @param versionNameDisplayed Whether the version name is visible
		 * @param customIconDisplayed Whether the custom icon is visible
		 * @param onlinePlayers MoTD's online players
		 * @param maxPlayers MoTD's max players
		 * @return <code>MoTDResponse</code> packet
		 */
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
				ValueContainer<Integer> onlinePlayers,
				ValueContainer<Integer> maxPlayers
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
					.writeUTF(onlinePlayers.value() == null ? onlinePlayers.placeholder() : onlinePlayers.value().toString())
					.writeUTF(maxPlayers.value() == null ? maxPlayers.placeholder() : maxPlayers.value().toString());
		}
		
		/**
		 * Adds a violation to the specified player's violations
		 * using a packet of type {@link ViolationPacketType#ADD}.
		 * 
		 * @param server Target server
		 * @param playerUUID Player's UUID
		 * @param playerName Player's name
		 * @param anticheat Anticheat that flagged the player
		 * @param cheatID Cheat's ID
		 * @param component Cheat's component
		 * @param amount Amount of times the player got flagged
		 * @param ping Player's ping, in milliseconds
		 * @param tps Server's ticks per second
		 * @param versionProtocol Player's version's protocol
		 * @param versionPreNettyRewrite Whether the player is using a pre-Netty rewrite version
		 * @param bedrockEdition Whether the player is playing on Bedrock Edition
		 * @return <code>PlayerViolation</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer addPlayerViolation(
				@NotNull String server,
				@NotNull UUID playerUUID,
				@NotNull String playerName,
				@NotNull IntegrationType<AnticheatIntegration> anticheat,
				@NotNull String cheatID,
				@NotNull String component,
				int amount,
				int ping,
				double tps,
				int versionProtocol,
				boolean versionPreNettyRewrite,
				boolean bedrockEdition
				) {
			return new PacketSerializer("PlayerViolation")
					.writeUTF(server)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(ViolationPacketType.ADD.name())
					.writeUTF(anticheat.name())
					.writeUTF(cheatID)
					.writeUTF(component)
					.writeInt(amount)
					.writeInt(ping)
					.writeDouble(tps)
					.writeInt(versionProtocol)
					.writeBoolean(versionPreNettyRewrite)
					.writeBoolean(bedrockEdition);
		}
		
		/**
		 * Removes violations of a certain type for the specified player
		 * using a packet of type {@link ViolationPacketType#REMOVE}.
		 * 
		 * @param server Target server
		 * @param playerUUID Player's UUID
		 * @param playerName Player's name
		 * @param anticheat Anticheat that detected the violations
		 * @param cheatID Cheat's ID
		 * @return <code>PlayerViolation</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer removePlayerViolation(
				@NotNull String server,
				@NotNull UUID playerUUID,
				@NotNull String playerName,
				@NotNull IntegrationType<AnticheatIntegration> anticheat,
				@NotNull String cheatID
				) {
			return new PacketSerializer("PlayerViolation")
					.writeUTF(server)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(ViolationPacketType.REMOVE.name())
					.writeUTF(anticheat.name())
					.writeUTF(cheatID);
		}
		
		/**
		 * Clears all violations for the specified player
		 * using a packet of type {@link ViolationPacketType#CLEAR}.
		 * 
		 * @param server Target server
		 * @param playerUUID Player's UUID
		 * @param playerName Player's name
		 * @return <code>PlayerViolation</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		public static PacketSerializer clearPlayerViolation(
				@NotNull String server,
				@NotNull UUID playerUUID,
				@NotNull String playerName
				) {
			return new PacketSerializer("PlayerViolation")
					.writeUTF(server)
					.writeUUID(playerUUID)
					.writeUTF(playerName)
					.writeUTF(ViolationPacketType.CLEAR.name());
		}
		
		/**
		 * Makes the target server reload certain managers.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param time Reload's time, in milliseconds
		 * @return <code>ProxyPluginLoad</code> packet
		 */
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
	
	/**
	 * Contains packets used to synchronize punishments.
	 * 
	 * @deprecated Internal use only.
	 */
	@Deprecated
	public static class Punishments {
		
		/**
		 * Carries information about a player's ban.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param id Ban's ID
		 * @param playerUUID Banned player's UUID
		 * @param playerName Banned player's name
		 * @param ipAddress Banned IP address
		 * @param staffMember Ban's Staff member
		 * @param reason Ban's reason
		 * @param type Ban's type
		 * @param date Ban's creation or modification date
		 * @param duration Ban's duration
		 * @param global Whether this ban is global
		 * @param silent Whether this ban is silent
		 * @return <code>PlayerBan</code> packet
		 */
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
		
		/**
		 * Carries information about a player's unban.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param whoUnbanned Who unbanned the player
		 * @param unbanDate Unban's date
		 * @param playerUUID Banned player's UUID
		 * @param playerName Banned player's name
		 * @param ipAddress Banned IP address
		 * @param type Ban's type
		 * @return <code>PlayerUnban</code> packet
		 */
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
		
		/**
		 * Carries information about a player's unban.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param whoUnbanned Who unbanned the player
		 * @param unbanDate Unban's date
		 * @param id Ban's ID
		 * @return <code>PlayerUnban</code> packet
		 */
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
		
		/**
		 * Carries information about a banwave's entry.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param playerUUID Entry's player's UUID
		 * @param playerName Entry's player's name
		 * @param ipAddress Entry's IP address
		 * @param staffMember Entry's staff member
		 * @param reason Entry's reason
		 * @param type Entry's type
		 * @param date Entry's creation or modification date
		 * @param duration Entry's duration
		 * @param global Whether this entry is global
		 * @param silent Whether this entry is silent
		 * @return <code>AddBanwaveEntry</code> packet
		 */
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
		
		/**
		 * Carries information about a banwave's entry's removal.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param playerUUID Entry's player's UUID
		 * @param playerName Entry's player's name
		 * @param ipAddress Entry's IP address
		 * @param whoRemoved Who removed the entry
		 * @param type Entry's type
		 * @param removalDate Entry's removal date
		 * @return <code>RemoveBanwaveEntry</code> packet
		 */
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
		
		/**
		 * Makes the target server execute the banwave's start commands.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param estimatedDuration Banwave's estimated duration
		 * @param forwardProxyCommands Whether proxy's commands should be forwarded
		 * @param startCommands Banwave's start commands
		 * @return <code>BanwaveStart</code> packet
		 */
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
		
		/**
		 * Makes the target server execute the banwave's end commands.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param bannedPlayers Banned players' amount
		 * @param forwardProxyCommands Whether proxy's commands should be forwarded
		 * @param endCommands Banwave's end commands
		 * @return <code>BanwaveEnd</code> packet
		 */
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
		
		/**
		 * Carries information about a player's warning.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param id Warning's ID
		 * @param playerUUID Warned player's UUID
		 * @param playerName Warned player's name
		 * @param staffMember Warning's Staff member
		 * @param reason Warning's reason
		 * @param date Warning's date
		 * @param duration Warning's duration
		 * @param global Whether this warning is global
		 * @param silent Whether this warning is silent
		 * @return <code>PlayerWarning</code> packet
		 */
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
		
		/**
		 * Carries information about a player's unwarn.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param id Warning's ID
		 * @param whoUnwarned Who unwarned the player
		 * @param unwarnDate Unwarn's date
		 * @return <code>PlayerUnwarn</code> packet
		 */
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
		
		/**
		 * Carries information about a player's last warning's removal.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param playerUUID Warned player's UUID
		 * @param playerName Warned player's name
		 * @param whoUnwarned Who unwarned the player
		 * @param unwarnDate Unwarn's date
		 * @return <code>PlayerRemoveLastWarning</code> packet
		 */
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
		
		/**
		 * Carries information about a player's warnings' clearing.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param playerUUID Warned player's UUID
		 * @param playerName Warned player's name
		 * @param whoUnwarned Who unwarned the player
		 * @param unwarnDate Unwarn's date
		 * @return <code>PlayerClearWarnings</code> packet
		 */
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
		
		/**
		 * Carries information about a player's kick. 
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param id Kick's ID
		 * @param playerUUID Kicked player's UUID
		 * @param playerName Kicked player's name
		 * @param ipAddress Kicked IP address
		 * @param staffMember Kick's Staff member
		 * @param reason Kick's reason
		 * @param lobbyServer Kick's lobby server's ID
		 * @param type Kick's type
		 * @param date Kick's date
		 * @param silent Whether this kick is global
		 * @return <code>PlayerKick</code> packet
		 */
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
		
		/**
		 * Carries information about a player's mute.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param id Mute's ID
		 * @param playerUUID Muted player's UUID
		 * @param playerName Muted player's name
		 * @param staffMember Mute's Staff member
		 * @param reason Mute's reason
		 * @param date Mute's creation or modification date
		 * @param duration Mute's duration
		 * @param global Whether this mute is global
		 * @param silent Whether this mute is silent
		 * @return <code>PlayerMute</code> packet
		 */
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
		
		/**
		 * Carries information about a player's unmute.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param whoUnmuted Who unmuted the player
		 * @param unmuteDate Unmute's date
		 * @param playerUUID Muted player's UUID
		 * @param playerName Muted player's name
		 * @return <code>PlayerUnmute</code> packet
		 */
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
		
		/**
		 * Carries information about a player's unmute.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param whoUnmuted Who unmuted the player
		 * @param unmuteDate Unmute's date
		 * @param id Mute's ID
		 * @return <code>PlayerUnmute</code> packet
		 */
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
	
	/**
	 * Contains various misc packets.
	 * 
	 * @deprecated Internal use only.
	 */
	@Deprecated
	public static class Misc {
		
		/**
		 * Carries information about a server's
		 * online and vanished players' amount.
		 * 
		 * @deprecated Internal use only.
		 * @param server Target server
		 * @param onlinePlayers Online players' amount
		 * @param vanishedPlayers Vanished players' amount
		 * @return <code>ServerInformation</code> packet
		 */
		@PacketScope(Scope.SERVER_TO_SERVER)
		@Deprecated
		public static PacketSerializer serverInformation(
				@NotNull String server,
				int onlinePlayers,
				int vanishedPlayers
				) {
			return new PacketSerializer("ServerInformation")
					.writeUTF("ALL")
					.writeUTF(server)
					.writeInt(onlinePlayers)
					.writeInt(vanishedPlayers);
		}
		
		/**
		 * Makes a client disconnect from the server.
		 * 
		 * @deprecated Use {@link Client#disconnect()} or {@link ClientHandler#disconnect(String)} instead.
		 * @param reason Disconnection's reason
		 * @return <code>ClientDisconnection</code> packet
		 */
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
