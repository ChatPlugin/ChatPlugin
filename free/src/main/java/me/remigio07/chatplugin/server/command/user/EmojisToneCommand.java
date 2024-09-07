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

import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.server.command.BaseCommand;

public class EmojisToneCommand extends BaseCommand {
	
	public EmojisToneCommand() {
		super("/emojistone [tone] [player]");
		tabCompletionArgs.put(0, Arrays.asList("{emojis_tones}"));
		tabCompletionArgs.put(1, players);
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("emojistone", "emojitone", "skintone");
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (InstantEmojisManager.getInstance().isEnabled()) {
			if (args.length == 0) {
				if (reportOnlyPlayers(sender)) {
					if (GUIManager.getInstance().isEnabled() && GUIManager.getInstance().getGUI("emojis-tone") != null)
						((SinglePageGUI) GUIManager.getInstance().getGUI("emojis-tone")).open(sender.toServerPlayer(), true);
					else sender.toServerPlayer().sendTranslatedMessage("misc.disabled-feature");
				}
			} else if (args.length == 1) {
				if (reportOnlyPlayers(sender)) {
					ChatPluginServerPlayer player = sender.toServerPlayer();
					
					if (player != null) {
						ChatColor tone = getColor(args[0]);
						List<ChatColor> tones = InstantEmojisManager.getInstance().getTones();
						
						if (tone == ChatColor.RESET || tones.contains(tone)) {
							int index = 0;
							
							for (int i = 0; i < tones.size(); i++)
								if (tone.equals(tones.get(i))) {
									index = i;
									break;
								}
							player.setEmojisTone(tone);
							
							if (tone == ChatColor.RESET)
								player.sendTranslatedMessage("commands.emojistone.reset.self");
							else player.sendTranslatedMessage("commands.emojistone.set.self", index, unformatColorString(tone.isDefaultColor() ? "&" + tone.getCode() : tone.name(), (VersionUtils.getVersion().isAtLeast(Version.V1_16) ? tone : tone.getClosestDefaultColor()).toString()));
						} else player.sendTranslatedMessage("commands.emojistone.invalid-tone", unformatColorString(args[0], "&r"));
					} else sender.sendMessage(language.getMessage("misc.disabled-world"));
				}
			} else if (args.length == 2) {
				if (sender.hasPermission(getPermission() + ".others")) {
					if (PlayerAdapter.getPlayer(args[1], false) != null) {
						@SuppressWarnings("deprecation")
						ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(args[1], false, true);
						
						if (player != null) {
							ChatColor tone = getColor(args[0]);
							List<ChatColor> tones = InstantEmojisManager.getInstance().getTones();
							
							if (tone == ChatColor.RESET || tones.contains(tone)) {
								int index = 0;
								
								for (int i = 0; i < tones.size(); i++)
									if (tone.equals(tones.get(i))) {
										index = i;
										break;
									}
								player.setEmojisTone(tone);
								
								if (tone == ChatColor.RESET)
									sender.sendMessage(language.getMessage("commands.emojistone.reset.other", player.getName()));
								else sender.sendMessage(language.getMessage("commands.emojistone.set.other", player.getName(), index, unformatColorString(tone.isDefaultColor() ? "&" + tone.getCode() : tone.name(), (VersionUtils.getVersion().isAtLeast(Version.V1_16) ? tone : tone.getClosestDefaultColor()).toString())));
							} else sender.sendMessage(language.getMessage("commands.emojistone.invalid-tone", unformatColorString(args[0], "&r")));
						} else sender.sendMessage(language.getMessage("misc.disabled-world"));
					} else sender.sendMessage(language.getMessage("misc.player-not-found", args[1]));
				} else sender.sendMessage(language.getMessage("misc.no-permission"));
			} else sendUsage(sender, language);
		} else sender.sendMessage(language.getMessage("misc.disabled-feature"));
	}
	
	public static ChatColor getColor(String arg) {
		if (arg.equalsIgnoreCase("reset"))
			return ChatColor.RESET;
		if (arg.length() == 2 && arg.charAt(0) == '&' && ChatColor.isColorCode(arg.charAt(1)))
			return ChatColor.getByChar(arg.charAt(1));
		if (Utils.isPositiveInteger(arg)) {
			if (Integer.parseInt(arg) < InstantEmojisManager.getInstance().getTones().size())
				return InstantEmojisManager.getInstance().getTones().get(Integer.parseInt(arg));
		} else if (arg.length() == 7)
			try {
				return ChatColor.of(arg);
			} catch (NumberFormatException e) {
				
			}
		return ChatColor.valueOf(arg.toUpperCase());
	}
	
	public static String unformatColorString(String string, String replacement) {
		return string.replaceAll(".", replacement + "$0");
	}
	
	
}
