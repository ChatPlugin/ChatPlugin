/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.server.command.user;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class ChatColorCommand extends BaseCommand {
	
	public ChatColorCommand() {
		super("/chatcolor [color] [player]");
		tabCompletionArgs.put(0, Arrays.asList(
				"#DDD605", "&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f", "&r",
				"black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", "yellow", "white", "reset"
				));
		tabCompletionArgs.put(1, players);
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("chatcolor", "color", "cc");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (args.length == 0) {
			if (reportOnlyPlayers(sender)) {
				if (GUIManager.getInstance().isEnabled() && GUIManager.getInstance().getGUI("chat-color") != null)
					((SinglePageGUI) GUIManager.getInstance().getGUI("chat-color")).open(sender.toServerPlayer(), true);
				else sender.toServerPlayer().sendTranslatedMessage("misc.disabled-feature");
			}
		} else if (args.length == 1) {
			if (!args[0].equals("#")) {
				if (reportOnlyPlayers(sender)) {
					ChatPluginServerPlayer player = sender.toServerPlayer();
					ChatColor color = getColor(args[0]);
					
					if (color != null) {
						if (color.isDefaultColor() || player.hasPermission(getPermission() + ".hex")) {
							player.setChatColor(color);
							
							if (color == ChatColor.RESET)
								player.sendTranslatedMessage("commands.chatcolor.reset.self");
							else player.sendTranslatedMessage("commands.chatcolor.set.self", (VersionUtils.getVersion().isAtLeast(Version.V1_16) ? color : color.getClosestDefaultColor()).toString() + (color.isDefaultColor() ? "&" + color.getCode() : color.name()));
						} else player.sendTranslatedMessage("commands.chatcolor.no-permission");
					} else player.sendTranslatedMessage("commands.chatcolor.invalid-color", args[0]);
				}
			} else sender.sendMessage(language.getMessage("commands.chatcolor.hex-usage", String.format("%06x", ThreadLocalRandom.current().nextInt(16777216)).toUpperCase()));
		} else if (args.length == 2) {
			if (sender.hasPermission(getPermission() + ".others")) {
				if (PlayerAdapter.getPlayer(args[1], false) != null) {
					@SuppressWarnings("deprecation")
					ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(args[1], false, true);
					
					if (player != null) {
						ChatColor color = getColor(args[0]);
						
						if (color != null) {
							player.setChatColor(color);
							
							if (color == ChatColor.RESET)
								sender.sendMessage(language.getMessage("commands.chatcolor.reset.other", player.getName()));
							else sender.sendMessage(language.getMessage("commands.chatcolor.set.other", player.getName(), (VersionUtils.getVersion().isAtLeast(Version.V1_16) ? color : color.getClosestDefaultColor()).toString() + (color.isDefaultColor() ? "&" + color.getCode() : color.name())));
						} else sender.sendMessage(language.getMessage("commands.chatcolor.invalid-color", args[0]));
					} else sender.sendMessage(language.getMessage("misc.disabled-world"));
				} else sender.sendMessage(language.getMessage("misc.player-not-found", args[1]));
			} else sender.sendMessage(language.getMessage("misc.no-permission"));
		} else sendUsage(sender, language);
	}
	
	public static ChatColor getColor(String arg) {
		if (arg.length() == 2 && arg.charAt(0) == '&' && ChatColor.isColorCode(arg.charAt(1)))
			return ChatColor.getByChar(arg.charAt(1));
		if (arg.length() == 7)
			try {
				return ChatColor.of(arg);
			} catch (NumberFormatException e) {
				
			}
		return ChatColor.valueOf(arg.toUpperCase());
	}
	
}
