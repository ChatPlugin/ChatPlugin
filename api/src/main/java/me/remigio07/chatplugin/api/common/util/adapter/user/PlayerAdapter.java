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

package me.remigio07.chatplugin.api.common.util.adapter.user;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.ChatPluginPlayer;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.proxy.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import me.remigio07.chatplugin.bootstrap.VelocityBootstrapper;
import net.md_5.bungee.api.ProxyServer;

/**
 * Environment indipendent (Bukkit, Sponge, BungeeCord and Velocity) player adapter.
 */
public class PlayerAdapter {
	
	private static Method invokeAdventureMethod;
	private Object player;
	
	static {
		try {
			invokeAdventureMethod = Class.forName("me.remigio07.chatplugin.ChatPluginPremiumImpl$VelocityAdapter", false, JARLibraryLoader.getInstance()).getMethod("invokeAdventureMethod", Object.class, String.class, String.class);
		} catch (Throwable t) {
			// not on Velocity
		}
	}
	
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
	 * Checks if another object is an instance of {@link PlayerAdapter} and if this
	 * player's {@link #getUUID()} (if running on online mode) or {@link #getName()}
	 * (if running on offline mode) value is equal to the other object's one.
	 * 
	 * @param obj Object to compare
	 * @return Whether the two objects are equal
	 * @throws IllegalStateException If {@link ChatPlugin#isOnlineMode()} cannot be run yet
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PlayerAdapter ? ChatPlugin.getInstance().isOnlineMode() ? ((PlayerAdapter) obj).getUUID().equals(getUUID()) : ((PlayerAdapter) obj).getName() == null ? false : ((PlayerAdapter) obj).getName().equalsIgnoreCase(getName()) : false;
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
		return ChatPlugin.getInstance().isOnlineMode() ? getUUID().hashCode() : getName() == null ? -1 : getName().hashCode();
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
		throw new UnsupportedOperationException("Unable to adapt player to a Bukkit's Player on a " + Environment.getCurrent().getName() + " environment");
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
		throw new UnsupportedOperationException("Unable to adapt text to a Sponge's Player on a " + Environment.getCurrent().getName() + " environment");
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
		throw new UnsupportedOperationException("Unable to adapt text to a BungeeCord's TextComponent on a " + Environment.getCurrent().getName() + " environment");
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
		throw new UnsupportedOperationException("Unable to adapt text to a Velocity's Component on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the player adapted as a {@link ChatPluginPlayer} loaded by the {@link PlayerManager}.
	 * 
	 * <p>Will return <code>null</code> if !{@link #isLoaded()}.</p>
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
	 * <p><strong>Note:</strong> BungeeCord supports connections via
	 * <a href="https://en.wikipedia.org/wiki/Unix_domain_socket">Unix domain sockets</a>.
	 * If this method is called on a player connected through a Unix domain socket,
	 * {@link InetAddress#getLoopbackAddress()} is returned.</p>
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
			bukkitValue().sendMessage(ChatColor.translate(message));
			break;
		case SPONGE:
			spongeValue().sendMessage(me.remigio07.chatplugin.api.server.util.Utils.serializeSpongeText(message, true));
			break;
		case BUNGEECORD:
			BungeeCordMessages.sendMessage(this, message);
			break;
		case VELOCITY:
			try {
				invokeAdventureMethod.invoke(null, velocityValue(), "sendMessage", message);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			} break;
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
			bukkitValue().kickPlayer(ChatColor.translate(reason));
			break;
		case SPONGE:
			spongeValue().kick(me.remigio07.chatplugin.api.server.util.Utils.serializeSpongeText(reason, true));
			break;
		case BUNGEECORD:
			BungeeCordMessages.disconnect(this, reason);
			break;
		case VELOCITY:
			try {
				invokeAdventureMethod.invoke(null, velocityValue(), "disconnect", reason);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			} break;
		}
	}
	
	/**
	 * Gets an online player by specifying their UUID.
	 * 
	 * <p>Will return <code>null</code> if they are not online.</p>
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
			player = ProxyServer.getInstance().getPlayer(uuid);
			break;
		case VELOCITY:
			player = VelocityBootstrapper.getInstance().getProxy().getPlayer(uuid).orElse(null);
			break;
		} return player == null ? null : new PlayerAdapter(player);
	}
	
	/**
	 * Gets an online player by specifying their name.
	 * 
	 * <p>Will return <code>null</code> if they are not online.</p>
	 * 
	 * @param name Player's name, case insensitive
	 * @param checkPattern Whether to check the name against {@link PlayerManager#getUsernamePattern()}
	 * @return Player if online or <code>null</code> otherwise
	 * @throws IllegalArgumentException If <code>checkPattern</code> and specified name <code>!{@link PlayerManager#isValidUsername(String)}</code>
	 */
	@Nullable(why = "The specified player may not be online")
	public static PlayerAdapter getPlayer(String name, boolean checkPattern) {
		if (checkPattern && !PlayerManager.getInstance().isValidUsername(name))
			throw new IllegalArgumentException("Username \"" + name + "\" does not respect the following pattern: \"" + PlayerManager.getInstance().getUsernamePattern().pattern() + "\"");
		Object player = null;
		
		switch (Environment.getCurrent()) {
		case BUKKIT:
			player = Bukkit.getPlayerExact(name);
			break;
		case SPONGE:
			player = Sponge.getServer().getPlayer(name).orElse(null);
			break;
		case BUNGEECORD:
			player = ProxyServer.getInstance().getPlayer(name);
			break;
		case VELOCITY:
			player = VelocityBootstrapper.getInstance().getProxy().getPlayer(name).orElse(null);
			break;
		} return player == null ? null : new PlayerAdapter(player);
	}
	
	/**
	 * Gets the online players' list.
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
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
			players = ProxyServer.getInstance().getPlayers();
			break;
		case VELOCITY:
			players = VelocityBootstrapper.getInstance().getProxy().getAllPlayers();
			break;
		} return players.stream().map(PlayerAdapter::new).collect(Collectors.toList());
	}
	
	private static class BungeeCordMessages {
		
		public static void sendMessage(PlayerAdapter player, String message) {
			player.bungeeCordValue().sendMessage(Utils.serializeBungeeCordText(message));
		}
		
		public static void disconnect(PlayerAdapter player, String reason) {
			player.bungeeCordValue().disconnect(Utils.serializeBungeeCordText(reason));
		}
		
	}
	
}
