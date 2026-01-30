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

package me.remigio07.chatplugin.api.server.util.adapter.user;

import java.lang.reflect.InvocationTargetException;
import java.util.StringJoiner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;

import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

/**
 * Environment-indipendent (Bukkit, Sponge and Fabric) command sender adapter.
 */
public class CommandSenderAdapter {
	
	/**
	 * Represents the console.
	 */
	public static final CommandSenderAdapter CONSOLE = new CommandSenderAdapter(Environment.isBukkit() ? Bukkit.getConsoleSender() : Environment.isSponge() ? Sponge.getServer().getConsole() : FabricBootstrapper.getInstance().getServer().getCommandSource());
	private Object commandSender;
	
	/**
	 * Constructs a command sender adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.command.CommandSender} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.command.CommandSource} for Sponge environments</li>
	 * 		<li>{@link net.minecraft.server.command.ServerCommandSource} for Fabric environments</li>
	 * 	</ul>
	 * 
	 * @param commandSender Command sender object
	 */
	public CommandSenderAdapter(Object commandSender) {
		this.commandSender = commandSender;
	}
	
	/**
	 * Gets this sender's hash code.
	 * 
	 * <p>Will return the hash code of the
	 * sender specified by the constructor.</p>
	 * 
	 * @return Sender's hash code
	 */
	@Override
	public int hashCode() {
		return commandSender.hashCode();
	}
	
	/**
	 * Checks if another object is an instance of {@link CommandSenderAdapter} and
	 * if the sender specified by the constructor is equal to the other object's one.
	 * 
	 * @param obj Object to compare
	 * @return Whether the two objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof CommandSenderAdapter ? ((CommandSenderAdapter) obj).commandSender.equals(commandSender) : false;
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "CommandSenderAdapter{", "}")
				.add("uuid=" + getUUID().toString())
				.add("name=\"" + getName() + "\"")
				.add("isConsole=" + isConsole())
				.toString();
	}
	
	/**
	 * Gets the command sender adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted command sender
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public CommandSender bukkitValue() {
		if (Environment.isBukkit())
			return (CommandSender) commandSender;
		throw new UnsupportedOperationException("Unable to adapt command sender to a Bukkit's CommandSender on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the command sender adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted command sender
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public CommandSource spongeValue() {
		if (Environment.isSponge())
			return (CommandSource) commandSender;
		throw new UnsupportedOperationException("Unable to adapt command sender to a Sponge's CommandSource on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the command sender adapted for Fabric environments.
	 * 
	 * @return Fabric-adapted command sender
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isFabric()}
	 */
	public ServerCommandSource fabricValue() {
		if (Environment.isFabric())
			return (ServerCommandSource) commandSender;
		throw new UnsupportedOperationException("Unable to adapt command sender to a Fabric's ServerCommandSource on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Checks if this command sender is the console or a command block.
	 * 
	 * @return Whether this is the console
	 */
	public boolean isConsole() {
		return getName().equals("@") || (Environment.isFabric() ? !(fabricValue().getEntity() instanceof ServerPlayerEntity) : CONSOLE.equals(this));
	}
	
	/**
	 * Checks if this command sender is a player.
	 * 
	 * @return <code>!</code>{@link #isConsole()}
	 */
	public boolean isPlayer() {
		return !isConsole();
	}
	
	/**
	 * Checks if this command sender is a player and loaded.
	 * 
	 * @return {@link #toServerPlayer()}<code> != null</code>
	 */
	public boolean isLoaded() {
		return toServerPlayer() != null;
	}
	
	/**
	 * Gets this command sender's name.
	 * 
	 * @return Command sender's name
	 */
	public String getName() {
		return Environment.isBukkit() ? bukkitValue().getName() : Environment.isSponge() ? spongeValue().getName() : fabricValue().getName();
	}
	
	/**
	 * Gets this command sender's UUID.
	 * 
	 * <p>Will return <code>null</code> if {@link #isConsole()}.</p>
	 * 
	 * @return Command sender's UUID
	 */
	@Nullable(why = "Sender may not be a player")
	public UUID getUUID() {
		return isConsole() ? null
				: Environment.isBukkit() ? ((Player) bukkitValue()).getUniqueId()
				: Environment.isSponge() ? ((org.spongepowered.api.entity.living.player.Player) spongeValue()).getUniqueId()
				: fabricValue().getEntity().getUuid();
	}
	
	/**
	 * Checks if this command sender has the specified permission.
	 * 
	 * @param permission Permission to check
	 * @return Whether this command sender has the permission
	 */
	public boolean hasPermission(@NotNull String permission) {
		return Environment.isBukkit() ? bukkitValue().hasPermission(permission) : Environment.isSponge() ? spongeValue().hasPermission(permission) : isConsole() || new PlayerAdapter(fabricValue().getEntity()).hasPermission(permission);
	}
	
	/**
	 * Sends a plain message to this command sender.
	 * 
	 * @param message Message to send
	 */
	public void sendMessage(@NotNull String message) {
		if (Environment.isBukkit())
			bukkitValue().sendMessage(message);
		else if (Environment.isSponge())
			spongeValue().sendMessage(Utils.toSpongeComponent(message));
		else try {
			Class.forName("me.remigio07.chatplugin.server.fabric.ChatPluginFabric", false, JARLibraryLoader.getInstance()).getMethod("sendMessage", ServerCommandSource.class, Text.class).invoke(null, fabricValue(), Utils.toFabricComponent(message));
		} catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets this player's corresponding {@link ChatPluginServerPlayer} object.
	 * 
	 * <p>Will return <code>null</code> if the player is not loaded.</p>
	 * 
	 * @return Corresponding {@link ChatPluginServerPlayer}
	 */
	@Nullable(why = "Player may not be loaded")
	public ChatPluginServerPlayer toServerPlayer() {
		return isConsole() ? null : ServerPlayerManager.getInstance().getPlayer(getUUID());
	}
	
}
