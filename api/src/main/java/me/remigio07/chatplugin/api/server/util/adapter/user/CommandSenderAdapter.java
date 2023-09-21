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

package me.remigio07.chatplugin.api.server.util.adapter.user;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Environment indipendent (Bukkit and Sponge) command sender adapter.
 */
public class CommandSenderAdapter {
	
	/**
	 * Represents the console.
	 */
	public static final CommandSenderAdapter CONSOLE = new CommandSenderAdapter(Environment.isBukkit() ? Bukkit.getConsoleSender() : Sponge.getServer().getConsole());
	private Object commandSender;
	
	/**
	 * Constructs a command sender adapter that accepts one of the following specified as input:
	 * 	<ul>
	 * 		<li>{@link org.bukkit.command.CommandSender} for Bukkit environments</li>
	 * 		<li>{@link org.spongepowered.api.command.CommandSource} for Sponge environments</li>
	 * 	</ul>
	 * 
	 * @param commandSender Command sender object
	 */
	public CommandSenderAdapter(Object commandSender) {
		this.commandSender = commandSender;
	}
	
	/**
	 * Gets the command sender adapted for Bukkit environments.
	 * 
	 * @return Bukkit-adapted command sender
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isBukkit()}
	 */
	public org.bukkit.command.CommandSender bukkitValue() {
		if (Environment.isBukkit())
			return (CommandSender) commandSender;
		else throw new UnsupportedOperationException("Unable to adapt command sender to a Bukkit's CommandSender on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets the command sender adapted for Sponge environments.
	 * 
	 * @return Sponge-adapted command sender
	 * @throws UnsupportedOperationException If <code>!</code>{@link Environment#isSponge()}
	 */
	public org.spongepowered.api.command.CommandSource spongeValue() {
		if (Environment.isSponge())
			return (CommandSource) commandSender;
		else throw new UnsupportedOperationException("Unable to adapt command sender to a Sponge's CommandSource on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Checks if this command sender is the console or a command block.
	 * 
	 * @return Whether this is the console
	 */
	public boolean isConsole() {
		return CONSOLE.equals(this) || getName().equals("@");
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
	 * Gets this command sender's name.
	 * 
	 * @return Command sender's name
	 */
	public String getName() {
		return Environment.isBukkit() ? bukkitValue().getName() : spongeValue().getName();
	}
	
	/**
	 * Gets this command sender's UUID.
	 * Will return <code>null</code> if {@link #isConsole()}.
	 * 
	 * @return Command sender's UUID
	 */
	@Nullable(why = "Sender may not be a player")
	public UUID getUUID() {
		return isConsole() ? null : Environment.isBukkit() ? ((org.bukkit.entity.Player) bukkitValue()).getUniqueId() : ((org.spongepowered.api.entity.living.player.Player) spongeValue()).getUniqueId();
	}
	
	/**
	 * Checks if this command sender
	 * has the specified permission.
	 * 
	 * @param permission Permission to check
	 * @return Whether this command sender has the permission
	 */
	public boolean hasPermission(@NotNull String permission) {
		return Environment.isBukkit() ? bukkitValue().hasPermission(permission) : spongeValue().hasPermission(permission);
	}
	
	/**
	 * Sends a plain message
	 * to this command sender.
	 * 
	 * @param message Message to send
	 */
	public void sendMessage(@NotNull String message) {
		if (Environment.isBukkit())
			bukkitValue().sendMessage(message);
		else spongeValue().sendMessage(Utils.serializeSpongeText(message));
	}
	
	/**
	 * Gets this player's corresponding {@link ChatPluginServerPlayer} object.
	 * Will return <code>null</code> if the player is not loaded.
	 * 
	 * @return Corresponding {@link ChatPluginServerPlayer}
	 */
	@Nullable(why = "Player may not be loaded")
	public ChatPluginServerPlayer toServerPlayer() {
		return isConsole() ? null : ServerPlayerManager.getInstance().getPlayer(getUUID());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof CommandSenderAdapter ? ((CommandSenderAdapter) obj).commandSender.equals(commandSender) : false;
	}
	
}
