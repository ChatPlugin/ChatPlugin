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

package me.remigio07.chatplugin.server.fabric;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;

import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.command.CommandsHandler;
import net.minecraft.server.command.ServerCommandSource;

public class FabricCommandsHandler extends CommandsHandler {
	
	public static void registerCommands() {
		registerCommands0();
		RootCommandNode<ServerCommandSource> root = FabricBootstrapper.getInstance().getServer().getCommandManager().getDispatcher().getRoot();
		
		for (String command : commands.keySet()) {
			BaseCommand[] subCommands = commands.get(command);
			BaseCommand mainCommand = subCommands[subCommands.length - 1];
			List<String> aliases = mainCommand.getMainArgs().stream().filter(alias -> !disabledCommands.contains(alias)).collect(Collectors.toList());
			
			if (aliases.isEmpty())
				continue;
			FabricCommandAdapter adapter = new FabricCommandAdapter(mainCommand);
			LiteralCommandNode<ServerCommandSource> brigadierCommand = literal(aliases.get(0)).executes(adapter).then(argument("arguments", StringArgumentType.greedyString()).suggests(adapter).executes(adapter)).build();
			
			aliases.forEach(alias -> unregisterCommand(alias, root));
			root.addChild(brigadierCommand);
			
			if (aliases.size() != 1)
				aliases.stream().skip(1).forEach(alias -> root.addChild(buildRedirect(alias, brigadierCommand)));
		} printTotalLoaded();
	}
	
	// see 3rd comment of https://github.com/Mojang/brigadier/issues/46
	public static LiteralCommandNode<ServerCommandSource> buildRedirect(String alias, LiteralCommandNode<ServerCommandSource> target) {
		LiteralArgumentBuilder<ServerCommandSource> builder = LiteralArgumentBuilder
				.<ServerCommandSource>literal(alias)
				.requires(target.getRequirement())
				.forward(target.getRedirect(), target.getRedirectModifier(), target.isFork())
				.executes(target.getCommand());
		
		for (CommandNode<ServerCommandSource> child : target.getChildren())
			builder.then(child);
		return builder.build();
	}
	
	public static void unregisterCommands(boolean sync) {
		RootCommandNode<ServerCommandSource> root = FabricBootstrapper.getInstance().getServer().getCommandManager().getDispatcher().getRoot();
		
		for (String command : commands.keySet()) {
			BaseCommand[] subcommands = commands.get(command);
			
			for (String alias : subcommands[subcommands.length - 1].getMainArgs())
				if (!disabledCommands.contains(alias))
					unregisterCommand(alias, root);
		} if (sync)
			syncCommands();
	}
	
	public static void unregisterCommand(String alias, RootCommandNode<ServerCommandSource> root) {
		mapRemove("children", alias, root);
		mapRemove("literals", alias, root);
		mapRemove("arguments", alias, root);
	}
	
	@SuppressWarnings("unchecked")
	private static void mapRemove(String fieldName, String command, RootCommandNode<ServerCommandSource> root) {
		try {
			Field field = CommandNode.class.getDeclaredField(fieldName);
			
			field.setAccessible(true);
			((Map<String, ?>) field.get(root)).remove(command);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static void syncCommands() {
		FabricBootstrapper.getInstance().getServer().getPlayerManager().getPlayerList().forEach(FabricBootstrapper.getInstance().getServer().getCommandManager()::sendCommandTree);
	}
	
}
