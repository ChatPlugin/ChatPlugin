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

package me.remigio07.chatplugin.api.common.util.adapter.user;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.spongepowered.api.Sponge;

import me.remigio07.chatplugin.api.common.player.ChatPluginPlayer;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.adapter.text.TextAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.VelocityBootstrapper;

/**
 * Environment indipendent (Bukkit, Sponge, BungeeCord and Velocity) player adapter.
 */
public class PlayerAdapter {
	
	private Object player;
	
	/**
	 * Constructs a player adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.entity.Player} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.entity.living.player.Player} for Sponge environments</li>
	 * 		<li>{@link net.md_5.bungee.api.connection.ProxiedPlayer} for BungeeCord environments</li>
	 * 		<li>{@link com.velocitypowered.api.proxy.Player} for Velocity environments</li>
	 * 	</ul>
	 * 
	 * @param player Player object
	 */
	public PlayerAdapter(Object player) {
		this.player = player;
	}
	
	/**
	 * Gets the player adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted player
	 * @throws UnsupportedOperationException If !{@link Environment#isBukkit()}
	 */
	public org.bukkit.entity.Player bukkitValue() {
		if (Environment.isBukkit())
			return (org.bukkit.entity.Player) player;
		else throw new UnsupportedOperationException("Unable to adapt player to a Bukkit's Player on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the player adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted player
	 * @throws UnsupportedOperationException If !{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.entity.living.player.Player spongeValue() {
		if (Environment.isSponge())
			return (org.spongepowered.api.entity.living.player.Player) player;
		else throw new UnsupportedOperationException("Unable to adapt text to a Sponge's Player on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the player adapted for BungeeCord environments.
	 * 
	 * @return BungeeCord-adapted player
	 * @throws UnsupportedOperationException If !{@link Environment#isBungeeCord()}
	 */
	public net.md_5.bungee.api.connection.ProxiedPlayer bungeeCordValue() {
		if (Environment.isBungeeCord())
			return (net.md_5.bungee.api.connection.ProxiedPlayer) player;
		else throw new UnsupportedOperationException("Unable to adapt text to a BungeeCord's TextComponent on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the player adapted for Velocity environments.
	 * 
	 * @return Velocity-adapted player
	 * @throws UnsupportedOperationException If !{@link Environment#isVelocity()}
	 */
	public com.velocitypowered.api.proxy.Player velocityValue() {
		if (Environment.isVelocity())
			return (com.velocitypowered.api.proxy.Player) player;
		else throw new UnsupportedOperationException("Unable to adapt text to a Velocity's Component on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the player adapted as a {@link ChatPluginPlayer} loaded by the {@link PlayerManager}.
	 * Will return <code>null</code> if !{@link #isLoaded()}.
	 * 
	 * @return Loaded player
	 */
	@Nullable(why = "The specified player may not be loaded")
	public ChatPluginPlayer chatPluginValue() {
		return isLoaded() ? PlayerManager.getInstance().getPlayer(getUUID()) : null;
	}
	
	/**
	 * Gets this player's name.
	 * 
	 * @return Player's name
	 */
	public String getName() {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			return bukkitValue().getName();
		case SPONGE:
			return spongeValue().getName();
		case BUNGEECORD:
			return bungeeCordValue().getName();
		case VELOCITY:
			return velocityValue().getUsername();
		} return null;
	}
	
	/**
	 * Gets this player's UUID.
	 * 
	 * @return Player's UUID
	 */
	public UUID getUUID() {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			return bukkitValue().getUniqueId();
		case SPONGE:
			return spongeValue().getUniqueId();
		case BUNGEECORD:
			return bungeeCordValue().getUniqueId();
		case VELOCITY:
			return velocityValue().getUniqueId();
		} return null;
	}
	
	/**
	 * Gets this player's IP address.
	 * 
	 * <p><strong>Note:</strong> BungeeCord supports connections via <a href="https://en.wikipedia.org/wiki/Unix_domain_socket">Unix domain sockets</a>.
	 * If this method is called on a player connected through a Unix domain socket, {@link InetAddress#getLoopbackAddress()} is returned.</p>
	 * 
	 * @return Player's IP address
	 */
	public InetAddress getIPAddress() {
		SocketAddress address = null;
		
		switch (Environment.getCurrent()) {
		case BUKKIT:
			address = bukkitValue().getAddress();
			break;
		case SPONGE:
			address = spongeValue().getConnection().getAddress();
			break;
		case BUNGEECORD:
			address = bungeeCordValue().getSocketAddress();
			break;
		case VELOCITY:
			address = velocityValue().getRemoteAddress();
			break;
		} return address instanceof InetSocketAddress ? ((InetSocketAddress) address).getAddress() : InetAddress.getLoopbackAddress();
	}
	
	/**
	 * Checks if this player is loaded by the {@link PlayerManager}.
	 * 
	 * @return Whether this player is loaded
	 */
	public boolean isLoaded() {
		return PlayerManager.getInstance().getPlayer(getUUID()) != null;
	}
	
	/**
	 * Checks if this player has the specified permission
	 * in their current context (world, dimension...).
	 * 
	 * @param permission Permission to check
	 * @return Whether this player has the permission
	 */
	public boolean hasPermission(@NotNull String permission) {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			return bukkitValue().hasPermission(permission);
		case SPONGE:
			return spongeValue().hasPermission(permission);
		case BUNGEECORD:
			return bungeeCordValue().hasPermission(permission);
		case VELOCITY:
			return velocityValue().hasPermission(permission);
		} return false;
	}
	
	/**
	 * Sends a plain message to this player.
	 * 
	 * @param message Message to send
	 */
	public void sendMessage(String message) {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			bukkitValue().sendMessage(message);
			break;
		case SPONGE:
			spongeValue().sendMessage(new TextAdapter(message).spongeValue());
			break;
		case BUNGEECORD:
			BungeeCordMessages.sendMessage(this, message);
			break;
		case VELOCITY:
			velocityValue().sendMessage(new TextAdapter(message).velocityValue());
			break;
		}
	}
	
	/**
	 * Disconnects this player with the specified reason.
	 * 
	 * @param reason Reason to kick the player for
	 */
	public void disconnect(String reason) {
		switch (Environment.getCurrent()) {
		case BUKKIT:
			bukkitValue().kickPlayer(reason);
			break;
		case SPONGE:
			spongeValue().kick(new TextAdapter(reason).spongeValue());
			break;
		case BUNGEECORD:
			BungeeCordMessages.disconnect(this, reason);
			break;
		case VELOCITY:
			velocityValue().disconnect(new TextAdapter(reason).velocityValue());
			break;
		}
	}
	
	/**
	 * Gets an online player by specifying their UUID.
	 * Will return <code>null</code> if they are not online.
	 * 
	 * @param uuid Player's UUID
	 * @return Player if online or <code>null</code> otherwise
	 */
	@Nullable(why = "The specified player may not be online")
	public static PlayerAdapter getPlayer(UUID uuid) {
		Object player = null;
		
		switch (Environment.getCurrent()) {
		case BUKKIT:
			player = Bukkit.getPlayer(uuid);
			break;
		case SPONGE:
			player = Sponge.getServer().getPlayer(uuid).orElse(null);
			break;
		case BUNGEECORD:
			player = Utils.invokeBungeeCordMethod("getPlayer", new Class<?>[] { UUID.class }, uuid);
			break;
		case VELOCITY:
			player = VelocityBootstrapper.getInstance().getProxy().getPlayer(uuid).orElse(null);
			break;
		} return player == null ? null : new PlayerAdapter(player);
	}
	
	/**
	 * Gets an online player by specifying their name.
	 * Will return <code>null</code> if they are not online.
	 * 
	 * @param name Player's name
	 * @param checkPattern Whether to check the name against {@link Utils#USERNAME_PATTERN}
	 * @return Player if online or <code>null</code> otherwise
	 * @throws IllegalArgumentException If <code>checkPattern</code> and specified name <code>!{@link Utils#isValidUsername(String)}</code>
	 */
	@Nullable(why = "The specified player may not be online")
	public static PlayerAdapter getPlayer(String name, boolean checkPattern) {
		if (checkPattern && !Utils.isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" is invalid as it does not respect the following pattern: \"" + Utils.USERNAME_PATTERN.pattern() + "\"");
		Object player = null;
		
		switch (Environment.getCurrent()) {
		case BUKKIT:
			player = Bukkit.getPlayerExact(name);
			break;
		case SPONGE:
			player = Sponge.getServer().getPlayer(name).orElse(null);
			break;
		case BUNGEECORD:
			player = Utils.invokeBungeeCordMethod("getPlayer", new Class<?>[] { String.class }, name);
			break;
		case VELOCITY:
			player = VelocityBootstrapper.getInstance().getProxy().getPlayer(name).orElse(null);
			break;
		} return player == null ? null : new PlayerAdapter(player);
	}
	
	/**
	 * Gets the online players' list.
	 * Do not modify the returned list.
	 * 
	 * @return Online players' list
	 */
	public static List<PlayerAdapter> getOnlinePlayers() {
		Collection<?> players = null;
		
		switch (Environment.getCurrent()) {
		case BUKKIT:
			try {
				players = Arrays.asList((Player[]) Server.class.getMethod("getOnlinePlayers").invoke(null)); // Bukkit 1.7
			} catch (NullPointerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassCastException e) {
				players = Bukkit.getOnlinePlayers();
			} break;
		case SPONGE:
			players = Sponge.getServer().getOnlinePlayers();
			break;
		case BUNGEECORD:
			players = (Collection<?>) Utils.invokeBungeeCordMethod("getPlayers", null);
			break;
		case VELOCITY:
			players = VelocityBootstrapper.getInstance().getProxy().getAllPlayers();
			break;
		} return players.stream().map(PlayerAdapter::new).collect(Collectors.toList());
	}
	
	private static class BungeeCordMessages {
		
		public static void sendMessage(PlayerAdapter player, String message) {
			player.bungeeCordValue().sendMessage(new TextAdapter(message).bungeeCordValue());
		}
		
		public static void disconnect(PlayerAdapter player, String reason) {
			player.bungeeCordValue().disconnect(new TextAdapter(reason).bungeeCordValue());
		}
		
	}
	
}
