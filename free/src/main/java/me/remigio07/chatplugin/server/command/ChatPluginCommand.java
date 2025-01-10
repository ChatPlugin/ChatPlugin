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

package me.remigio07.chatplugin.server.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.common.util.Debugger;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.language.LanguageManagerImpl;

public class ChatPluginCommand extends BaseCommand {
	
	public ChatPluginCommand() {
		super("/chatplugin help");
		tabCompletionArgs.put(0, Arrays.asList("help", "info", "version", "language", "reload", "status", "debug"));
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("chatplugin", "chatpl", "cp");
	}
	
	@Override
	public boolean hasSubCommands() {
		return true;
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, me.remigio07.chatplugin.api.server.language.Language language, String[] args) {
		sendUsage(sender, language);
	}
	
	public static class Help extends BaseCommand {
		
		private static boolean premium = ChatPlugin.getInstance().isPremium();
		
		public Help() {
			super("/chatplugin help [category]");
			tabCompletionArgs.put(1, premium ? Arrays.asList("user", "punishments", "guis", "admin", "vanish", "misc") : Arrays.asList("user", "admin", "vanish", "misc"));
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("help", "?");
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, me.remigio07.chatplugin.api.server.language.Language language, String[] args) {
			String str = premium ? "premium" : "free";
			
			if (args.length == 1 || args[1].equalsIgnoreCase("user"))
				sender.sendMessage(language.getMessage("commands.help." + str + ".user", ChatPlugin.VERSION));
			else if (sender.hasPermission(getPermission() + ".admin")) {
				switch (args[1].toLowerCase()) {
				case "user":
					sender.sendMessage(language.getMessage("commands.help." + str + ".user", ChatPlugin.VERSION));
					return;
				case "admin":
					sender.sendMessage(language.getMessage("commands.help." + str + ".admin", ChatPlugin.VERSION));
					return;
				case "punishments":
					if (premium) {
						sender.sendMessage(language.getMessage("commands.help.premium.punishments", ChatPlugin.VERSION));
						return;
					} break;
				case "guis":
					if (premium) {
						sender.sendMessage(language.getMessage("commands.help.premium.guis", ChatPlugin.VERSION));
						return;
					} break;
				case "vanish":
					sender.sendMessage(language.getMessage("commands.help." + str + ".vanish", ChatPlugin.VERSION));
					return;
				case "misc":
					sender.sendMessage(language.getMessage("commands.help." + str + ".misc", ChatPlugin.VERSION));
					return;
				default:
					break;
				} sender.sendMessage(language.getMessage("misc.wrong-syntax", usage));
			} else sender.sendMessage(language.getMessage("misc.no-permission"));
		}
		
	}
	
	public static class Info extends BaseCommand {
		
		public Info() {
			super("/chatplugin info");
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("info", "information", "contact", "contacts", "support");
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, me.remigio07.chatplugin.api.server.language.Language language, String[] args) {
			sender.sendMessage(language.getMessage("commands.info", ChatPlugin.VERSION));
		}
		
	}
	
	public static class Version extends BaseCommand {
		
		public Version() {
			super("/chatplugin version");
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("version", "ver", "build");
		}
		
		@Override
		public String getPermission() {
			return null;
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, me.remigio07.chatplugin.api.server.language.Language language, String[] args) {
			if (sender.getName().equals("Remigio07") || sender.hasPermission(super.getPermission()))
				sender.sendMessage(language.getMessage("commands.version", ChatPlugin.getInstance().isPremium() ? "§6Premium" : "§2Free", ChatPlugin.VERSION, VersionUtils.getImplementationName(), VersionUtils.getVersion().getName()));
			else sender.sendMessage(language.getMessage("misc.no-permission"));
		}
		
	}
	
	public static class Language extends PlayerCommand {
		
		public Language() {
			super("/chatplugin language <language>");
			tabCompletionArgs.put(1, Arrays.asList("{languages}"));
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("language", "languages", "lang", "langs", "locale");
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(ChatPluginServerPlayer player, String[] args) {
			if (args.length == 2) {
				me.remigio07.chatplugin.api.server.language.Language newLanguage = LanguageManager.getInstance().getLanguage(args[1]);
				
				if (newLanguage != null) {
					if (!LanguageManagerImpl.isCommandCooldownActive(player) || player.hasPermission(getPermission() + ".cooldown-bypass")) {
						TaskManager.runAsync(() -> {
							try {
								LanguageManager.getInstance().setLanguage(player, newLanguage);
								
								ChatPluginServerPlayer newPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUUID());
								
								TaskManager.runSync(() -> CommandsHandler.executeCommands(newPlayer, formatPlaceholders(newPlayer, newLanguage, ConfigurationType.CONFIG.get().getStringList("languages.command.commands"))), 0L);
								newPlayer.sendTranslatedMessage("languages.set", newLanguage.getDisplayName());
								LanguageManagerImpl.startCommandCooldown(newPlayer.getUUID());
							} catch (IllegalArgumentException e) {
								player.sendTranslatedMessage("languages.set-already", player.getLanguage().getDisplayName());
							}
						}, 0L);
					} else player.sendTranslatedMessage("misc.cooldown-active");
				} else player.sendTranslatedMessage("languages.invalid", Utils.getStringFromList(LanguageManager.getInstance().getLanguages().stream().map(me.remigio07.chatplugin.api.server.language.Language::getID).collect(Collectors.toList()), false, true));
			} else sendUsage(player);
		}
		
		public static List<String> formatPlaceholders(OfflinePlayer player, me.remigio07.chatplugin.api.server.language.Language language, List<String> input) {
			List<String> list = new ArrayList<>();
			
			for (String str : input)
				list.add(str
						.replace("{id}", language.getID())
						.replace("{player}", player.getName())
						.replace("{uuid}", player.getUUID().toString())
						);
			return list;
		}
		
	}
	
	public static class Reload extends BaseCommand {
		
		public Reload() {
			super("/chatplugin reload");
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("reload", "rl", "restart", "reboot");
		}

		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, me.remigio07.chatplugin.api.server.language.Language language, String[] args) {
			sender.sendMessage(language.getMessage("misc.reload.start"));
			
			int ms = ChatPlugin.getInstance().reload();
			
			sender.sendMessage(ms == -1 ? "§cChatPlugin could not reload and will be disabled. Check the console for more information."
					: (sender.isConsole() ? me.remigio07.chatplugin.api.server.language.Language.getMainLanguage() : LanguageManager.getInstance().getLanguage(new OfflinePlayer(sender.getUUID(), sender.getName()))).getMessage("misc.reload.end", ms));
		}
		
	}
	
	public static class Status extends BaseCommand {
		
		public Status() {
			super("/chatplugin status [-chat]");
			tabCompletionArgs.put(1, Arrays.asList("-chat", "-c"));
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("status", "overview", "server");
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, me.remigio07.chatplugin.api.server.language.Language language, String[] args) {
			if (sender.isPlayer() && (args.length < 2 || (!args[1].equalsIgnoreCase("-chat") && !args[1].equalsIgnoreCase("-c"))))
				if (GUIManager.getInstance().isEnabled() && GUIManager.getInstance().getGUI("main") != null)
					((SinglePageGUI) GUIManager.getInstance().getGUI("main")).open(ServerPlayerManager.getInstance().getPlayer(sender.getUUID()), true);
				else sender.sendMessage(language.getMessage("misc.disabled-feature"));
			else sender.sendMessage(PlaceholderManager.getInstance().translateServerPlaceholders(language.getMessage("commands.status"), language));
		}
		
	}
	
	public static class Debug extends BaseCommand {
		
		public Debug() {
			super("/chatplugin debug [manager|-file]");
			tabCompletionArgs.put(1, Arrays.asList("{managers}", "-file", "-f"));
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("debug", "verbose");
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void execute(CommandSenderAdapter sender, me.remigio07.chatplugin.api.server.language.Language language, String[] args) {
			if (args.length == 1) {
				LogManager logManager = LogManager.getInstance();
				
				try {
					if (logManager.isDebug()) {
						logManager.setDebug(false);
						sender.sendMessage(language.getMessage("misc.debug.disabled"));
					} else {
						logManager.setDebug(true);
						sender.sendMessage(language.getMessage("misc.debug.enabled"));
					}
				} catch (IOException ioe) {
					LogManager.log("IOException occurred while toggling the debug mode: {0}", 2, ioe.getLocalizedMessage());
				}
			} else if (args.length == 2) {
				switch (args[1].toLowerCase()) {
				case "-f":
				case "-file":
					long ms = System.currentTimeMillis();
					
					sender.sendMessage(language.getMessage("misc.debug.file.start"));
					TaskManager.runAsync(() -> {
						String file = Debugger.writeToFile();
						
						sender.sendMessage(language.getMessage(file.equals(Utils.NOT_APPLICABLE) ? "misc.debug.file.too-fast" : "misc.debug.file.end", file, System.currentTimeMillis() - ms));
					}, 0L);
					break;
				default:
					for (String manager : ChatPluginManagers.getInstance().getManagers().keySet().stream().map(clazz -> clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Manager"))).collect(Collectors.toList())) {
						if (manager.equals(args[1])) {
							if (Debugger.getEnabledManagersNames().contains(manager)) {
								sender.sendMessage(language.getMessage("misc.debug.manager.info", manager + "Manager"));
								sender.sendMessage(" " + Debugger.getContent(Utils.getOriginalClass(ChatPluginManagers.getInstance().getManager(manager))).trim().replaceAll(" +", " "));
							} else sender.sendMessage(language.getMessage("misc.debug.manager.disabled"));
							return;
						}
					} sender.sendMessage(language.getMessage("misc.inexistent-id"));
					break;
				}
			} else sendUsage(sender, language);
		}
		
	}
	
}
