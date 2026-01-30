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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.mixin.extension.ServerWorldExtension;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.command.CommandsHandler;
import me.remigio07.chatplugin.server.command.PlayerCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class FabricCommandAdapter implements Command<ServerCommandSource>, SuggestionProvider<ServerCommandSource> {
	
	private BaseCommand command;
	
	public FabricCommandAdapter(BaseCommand command) {
		this.command = command;
	}
	
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		String text = context.getInput();
		ServerCommandSource sender = context.getSource();
		String[] args = text.contains(" ") ? text.substring(text.indexOf(' ') + 1).split(" ") : new String[0];
		
		for (BaseCommand command : CommandsHandler.getCommands().get(command.getName())) {
			if (!command.isSubCommand() || (args.length > 0 && command.getMainArgs().contains(args[0].toLowerCase()))) {
				CommandSenderAdapter senderAdapter = new CommandSenderAdapter(sender);
				Language language = senderAdapter.isConsole() ? Language.getMainLanguage() : LanguageManager.getInstance().getLanguage(new OfflinePlayer(new PlayerAdapter(sender.getEntity())));
				
				if (sender.getEntity() instanceof ServerPlayerEntity) {
					if (senderAdapter.toServerPlayer() == null) {
						senderAdapter.sendMessage(language.getMessage("misc.disabled-world"));
						return 1;
					}
				} else if (command instanceof PlayerCommand) {
					senderAdapter.sendMessage(language.getMessage("misc.only-players"));
					return 1;
				} if (command.getPermission() != null && !senderAdapter.hasPermission(command.getPermission())) {
					senderAdapter.sendMessage(language.getMessage("misc.no-permission"));
					return 1;
				} if (sender.getName().equals("@")) {
					Vec3d location = sender.getPosition();
					
					if (CommandsHandler.shouldLogCommandBlocksCommands())
						LogManager.log("@ ({0}, {1} {2} {3}) issued command: /{4}", 3, ((ServerWorldExtension) sender.getWorld()).chatPlugin$getName(), (int) Math.floor(location.getX()), (int) Math.floor(location.getY()), (int) Math.floor(location.getZ()), text.startsWith("/") ? text.substring(1) : text);
				} else LogManager.log("{0} issued command: /{1}", 3, sender.getName(), text.startsWith("/") ? text.substring(1) : text);
				command.execute(senderAdapter, language, args);
				break;
			}
		} return 1;
	}
	
	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
		String text = context.getInput();
		int firstSpaceIndex = text.indexOf(' ');
		
		if (firstSpaceIndex == -1)
			return builder.buildFuture();
		builder = builder.createOffset(text.lastIndexOf(' ') + 1);
		List<String> arrayList = new ArrayList<>();
		String trimmed = text.substring(firstSpaceIndex + 1).trim();
		
		if (!trimmed.isEmpty())
			arrayList.addAll(Arrays.asList(trimmed.split("\\s+")));
		if (text.endsWith(" "))
			arrayList.add("");
		String[] args = arrayList.toArray(new String[arrayList.size()]);
		List<String> list = null;
		
		for (BaseCommand command : CommandsHandler.getCommands().get(command.getName())) {
			if (command.hasSubCommands() && args.length == 1) {
				list = command.getTabCompletionArgs(new CommandSenderAdapter(context.getSource()), args[0], 0);
				break;
			} else if ((!command.isSubCommand() && command.getMainArgs().contains(command.getName().toLowerCase()))
					|| (command.isSubCommand() && command.getMainArgs().contains(args[0].toLowerCase()))) {
				list = command.getTabCompletionArgs(new CommandSenderAdapter(context.getSource()), args[args.length - 1], args.length - 1);
				break;
			}
		} if (list != null)
			list.forEach(builder::suggest);
		return builder.buildFuture();
	}
	
	public BaseCommand getCommand() {
		return command;
	}
	
}
