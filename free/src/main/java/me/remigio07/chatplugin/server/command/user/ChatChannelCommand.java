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

package me.remigio07.chatplugin.server.command.user;

import java.util.Arrays;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelType;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.command.PlayerCommand;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.util.Utils;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class ChatChannelCommand extends BaseCommand {
	
	public ChatChannelCommand() {
		super("/chatchannel <join|leave|switch|info|list>");
		tabCompletionArgs.put(0, Arrays.asList("join", "leave", "switch", "info", "list"));
	}
	
	@Override
	public java.util.List<String> getMainArgs() {
		return Arrays.asList("chatchannel", "channel", "ch", "chat");
	}
	
	@Override
	public boolean hasSubCommands() {
		return true;
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		if (ChatChannelsManager.getInstance().isEnabled())
			sendUsage(sender, language);
		else sender.sendMessage(language.getMessage("misc.disabled-feature"));
	}
	
	public static class Join extends PlayerCommand {
		
		public Join() {
			super("/chatchannel join <channel>");
			tabCompletionArgs.put(1, Arrays.asList("{channels}"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("join", "enter", "j");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.chatchannel.join";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(ChatPluginServerPlayer player, String[] args) {
			if (ChatChannelsManager.getInstance().isEnabled()) {
				if (args.length == 2) {
					ChatChannel<?> channel = ChatChannelsManager.getInstance().getChannel(args[1], true);
					
					if (channel != null) {
						if (!channel.getListeners().contains(player)) {
							if (channel.canAccess(player)) {
								if (player.joinChannel(channel))
									player.sendTranslatedMessage("chat.channel.join.joined", getDisplayName(channel), channel.getID());
							} else player.sendTranslatedMessage("chat.channel.cannot-access", getDisplayName(channel));
						} else player.sendTranslatedMessage("chat.channel.join.already-listening", getDisplayName(channel), channel.getID());
					} else player.sendTranslatedMessage("chat.channel.invalid", args[1]);
				} else sendUsage(player);
			} else player.sendTranslatedMessage("misc.disabled-feature");
		}
		
	}
	
	public static class Leave extends PlayerCommand {
		
		public Leave() {
			super("/chatchannel leave <channel>");
			tabCompletionArgs.put(1, Arrays.asList("{joined_channels}"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("leave", "quit", "l");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.chatchannel.leave";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(ChatPluginServerPlayer player, String[] args) {
			if (ChatChannelsManager.getInstance().isEnabled()) {
				if (args.length == 2) {
					ChatChannel<?> channel = ChatChannelsManager.getInstance().getChannel(args[1], true);
					
					if (channel != null) {
						if (channel.getListeners().contains(player)) {
							try {
								if (player.leaveChannel(channel))
									player.sendTranslatedMessage("chat.channel.leave.left", getDisplayName(channel));
							} catch (IllegalArgumentException iae) {
								player.sendTranslatedMessage("chat.channel.leave.cannot-leave", getDisplayName(channel));
							}
						} else player.sendTranslatedMessage("chat.channel.leave.not-listening", getDisplayName(channel));
					} else player.sendTranslatedMessage("chat.channel.invalid", args[1]);
				} else sendUsage(player);
			} else player.sendTranslatedMessage("misc.disabled-feature");
		}
		
	}
	
	public static class Switch extends PlayerCommand {
		
		public Switch() {
			super("/chatchannel switch <channel>");
			tabCompletionArgs.put(1, Arrays.asList("{joined_channels}"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("switch", "select", "set", "s");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.chatchannel.switch";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(ChatPluginServerPlayer player, String[] args) {
			if (ChatChannelsManager.getInstance().isEnabled()) {
				if (args.length == 2) {
					ChatChannel<?> channel = ChatChannelsManager.getInstance().getChannel(args[1], true);
					
					if (channel != null) {
						if (channel.getListeners().contains(player)) {
							if (channel.canWrite(player)) {
								if (player.switchChannel(channel))
									player.sendTranslatedMessage("chat.channel.switch.switched", getDisplayName(channel));
							} else player.sendTranslatedMessage("chat.channel.switch.cannot-write", getDisplayName(channel));
						} else player.sendTranslatedMessage("chat.channel.switch.not-listening", getDisplayName(channel), channel.getID());
					} else player.sendTranslatedMessage("chat.channel.invalid", args[1]);
				} else sendUsage(player);
			} else player.sendTranslatedMessage("misc.disabled-feature");
		}
		
	}
	
	public static class Info extends BaseCommand {
		
		public Info() {
			super("/chatchannel info [channel]");
			tabCompletionArgs.put(1, Arrays.asList("{channels}"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("info", "information", "i");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.chatchannel.info";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			if (ChatChannelsManager.getInstance().isEnabled()) {
				if (args.length == 1) {
					if (reportOnlyPlayers(sender)) {
						ChatPluginServerPlayer player = sender.toServerPlayer();
						
						player.sendTranslatedMessage(
								"chat.channel.info.self",
								Utils.getStringFromList(player.getChannels().stream().map(ChatChannelCommand::getDisplayName).collect(Collectors.toList()), true, true),
								getDisplayName(player.getWritingChannel())
								);
					}
				} else if (args.length == 2) {
					ChatChannel<?> channel = ChatChannelsManager.getInstance().getChannel(args[1], true);
					
					if (channel != null)
						if (sender.isConsole() || channel.canAccess(sender.toServerPlayer()))
							sender.sendMessage(channel.formatPlaceholders(language.getMessage("chat.channel.info.channel")
									+ (Arrays.asList(ChatChannelType.WORLD, ChatChannelType.GLOBAL, ChatChannelType.NETWORK).contains(channel.getType())
									? "" : '\n' + language.getMessage("chat.channel.info." + channel.getType().name().toLowerCase())), language));
						else sender.sendMessage(language.getMessage("chat.channel.cannot-access", getDisplayName(channel)));
					else sender.sendMessage(language.getMessage("chat.channel.invalid", args[1]));
				} else sendUsage(sender, language);
			} else sender.sendMessage(language.getMessage("misc.disabled-feature"));
		}
		
	}
	
	public static class List extends BaseCommand {
		
		public List() {
			super("/chatchannel list");
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("list", "ls", "l");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.chatchannel.list";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			if (ChatChannelsManager.getInstance().isEnabled()) {
				sender.sendMessage(language.getMessage("chat.channel.list.header"));
				
				for (ChatChannel<?> channel : ChatChannelsManager.getInstance().getChannels()) {
					ChatPluginServerPlayer player = sender.toServerPlayer();
					
					if (player == null)
						sender.sendMessage(channel.formatPlaceholders(language.getMessage("chat.channel.list.message-format.text"), language));
					else if (channel.canAccess(player))
						((BaseChatPluginServerPlayer) sender.toServerPlayer()).sendMessage(
								Utils.deserializeLegacy(channel.formatPlaceholders(language.getMessage("chat.channel.list.message-format.text"), language), false)
								.hoverEvent(HoverEvent.showText(Utils.deserializeLegacy(channel.formatPlaceholders(language.getMessage("chat.channel.list.message-format.hover"), language), false)))
								.clickEvent(ClickEvent.runCommand("/chatchannel info " + channel.getID()))
								);
				}
			} else sender.sendMessage(language.getMessage("misc.disabled-feature"));
		}
		
	}
	
	private static String getDisplayName(ChatChannel<?> channel) {
		return channel.getDisplayName() == null ? channel.getID() : ChatColor.translate(channel.getDisplayName());
	}
	
}
