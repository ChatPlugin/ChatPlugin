/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.server.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import me.remigio07_.chatplugin.api.ChatPlugin;
import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07_.chatplugin.api.server.gui.GUIManager;
import me.remigio07_.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07_.chatplugin.api.server.language.LanguageManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07_.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07_.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07_.chatplugin.common.util.Debugger;
import me.remigio07_.chatplugin.common.util.Utils;
import me.remigio07_.chatplugin.server.language.LanguageManagerImpl;

public class ChatPluginCommand extends BaseCommand {
	
	public ChatPluginCommand() {
		super("/chatplugin help");
		tabCompletionArgs.put(0, Arrays.asList("help", "info", "version", "language", "reload", "status", "debug"));
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("chatplugin", "chatp", "chat", "cplugin", "cp");
	}
	
	@Override
	public boolean hasSubCommands() {
		return true;
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, me.remigio07_.chatplugin.api.server.language.Language language, String[] args) {
		sendUsage(sender, language);
	}
	
	public static class Help extends BaseCommand {
		
		public Help() {
			super("/chatplugin help [category]");
			tabCompletionArgs.put(1, Arrays.asList("user", "punishments", "guis", "admin", "vanish", "misc"));
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("help", "helpme", "what", "?");
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, me.remigio07_.chatplugin.api.server.language.Language language, String[] args) {
			if (args.length == 1 || args[1].equalsIgnoreCase("user"))
				sender.sendMessage(language.getMessage("commands.help.user", ChatPlugin.VERSION));
			else if (sender.hasPermission(getPermission() + ".admin"))
				switch (args[1].toLowerCase()) {
				case "user":
					sender.sendMessage(language.getMessage("commands.help.user", ChatPlugin.VERSION));
					break;
				case "admin":
					sender.sendMessage(language.getMessage("commands.help.admin", ChatPlugin.VERSION));
					break;
				case "punishments":
					sender.sendMessage(language.getMessage("commands.help.punishments", ChatPlugin.VERSION));
					break;
				case "guis":
					sender.sendMessage(language.getMessage("commands.help.guis", ChatPlugin.VERSION));
					break;
				case "vanish":
					sender.sendMessage(language.getMessage("commands.help.vanish", ChatPlugin.VERSION));
					break;
				case "misc":
					sender.sendMessage(language.getMessage("commands.help.misc", ChatPlugin.VERSION));
					break;
				default:
					sender.sendMessage(language.getMessage("misc.wrong-syntax", usage));
					break;
				}
			else sender.sendMessage(language.getMessage("misc.no-permission"));
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
		public void execute(CommandSenderAdapter sender, me.remigio07_.chatplugin.api.server.language.Language language, String[] args) {
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
		public void execute(CommandSenderAdapter sender, me.remigio07_.chatplugin.api.server.language.Language language, String[] args) {
			if (sender.getName().equals("Remigio07_") || sender.hasPermission(super.getPermission()))
				sender.sendMessage(language.getMessage("commands.version", ChatPlugin.VERSION));
			else sender.sendMessage(language.getMessage("misc.no-permission"));
		}
		
	}
	
	public static class Language extends PlayerCommand {
		
		public Language() {
			super("/chatplugin language <language>");
			tabCompletionArgs.put(1, new ArrayList<>(LanguageManager.getInstance().getLanguages().stream().map(me.remigio07_.chatplugin.api.server.language.Language::getID).collect(Collectors.toList())));
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("language", "languages", "lang", "langs", "localization", "locale", "l18n");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.language.sub";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(ChatPluginServerPlayer player, String[] args) {
			if (args.length == 2) {
				me.remigio07_.chatplugin.api.server.language.Language newLanguage = LanguageManager.getInstance().getLanguage(args[1]);
				
				if (newLanguage != null) {
					if (player.getLanguage() != newLanguage) {
						if (!LanguageManagerImpl.isCommandCooldownActive(player) || player.hasPermission("chatplugin.commands.language.cooldown-bypass")) {
							LanguageManager.getInstance().setLanguage(player, newLanguage);
							CommandsHandler.executeCommands(player, formatPlaceholders(player, newLanguage, ConfigurationType.CONFIG.get().getStringList("languages.command.commands")), 0L);
							player.sendTranslatedMessage("languages.set", newLanguage.getDisplayName());
							LanguageManagerImpl.startCommandCooldown(player.getUUID());
						} else player.sendTranslatedMessage("misc.cooldown-active");
					} else player.sendTranslatedMessage("languages.set-already", player.getLanguage().getID());
				} else player.sendTranslatedMessage("languages.invalid", Utils.getStringFromList(LanguageManager.getInstance().getLanguages().stream().map(me.remigio07_.chatplugin.api.server.language.Language::getID).collect(Collectors.toList()), false, true));
			} else sendUsage(player);
		}
		
		public static List<String> formatPlaceholders(OfflinePlayer player, me.remigio07_.chatplugin.api.server.language.Language language, List<String> input) {
			List<String> list = new ArrayList<>();
			
			for (String str : input)
				list.add(str
						.replace("{id}", language.getID())
						.replace("{player_name}", player.getName())
						.replace("{player_uuid}", player.getUUID().toString())
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
		public void execute(CommandSenderAdapter sender, me.remigio07_.chatplugin.api.server.language.Language language, String[] args) {
			LogManager.log("ChatPlugin is reloading...", 0);
			sender.sendMessage(language.getMessage("misc.reload.start"));
			
			int ms = ChatPlugin.getInstance().reload();
			
			if (ms == -1)
				sender.sendMessage("\u00A7cChatPlugin could not reload and will be disabled. Check the console for more information.");
			else sender.sendMessage((sender.isConsole() ? me.remigio07_.chatplugin.api.server.language.Language.getMainLanguage() : LanguageManager.getInstance().getLanguage(new OfflinePlayer(sender.getUUID(), sender.getName()))).getMessage("misc.reload.end", ms));
		}
		
	}
	
	public static class Status extends BaseCommand {
		
		public Status() {
			super("/chatplugin status [-chat]");
			tabCompletionArgs.put(1, Arrays.asList("-c"));
		}
		
		@Override
		public List<String> getMainArgs() {
			return Arrays.asList("status", "overview", "server");
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
		public void execute(CommandSenderAdapter sender, me.remigio07_.chatplugin.api.server.language.Language language, String[] args) {
			if (sender.getName().equals("Remigio07_") || sender.hasPermission("chatplugin.commands.status"))
				if (sender.isPlayer() && (args.length < 2 || (!args[1].equalsIgnoreCase("-chat") && !args[1].equalsIgnoreCase("-c"))))
					if (GUIManager.getInstance().isEnabled() && GUIManager.getInstance().getGUI("main") != null)
						((SinglePageGUI) GUIManager.getInstance().getGUI("main")).open(ServerPlayerManager.getInstance().getPlayer(sender.getUUID()), true);
					else sender.sendMessage(language.getMessage("misc.disabled-feature"));
				else sender.sendMessage(PlaceholderManager.getInstance().translateServerPlaceholders(language.getMessage("commands.status"), language));
			else sender.sendMessage(language.getMessage("misc.no-permission"));
		}
		
	}
	
	public static class Debug extends BaseCommand {
		
		public Debug() {
			super("/chatplugin debug [manager|-f]");
			tabCompletionArgs.put(1, Arrays.asList("{managers}", "-f"));
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
		public void execute(CommandSenderAdapter sender, me.remigio07_.chatplugin.api.server.language.Language language, String[] args) {
			LogManager logManager = LogManager.getInstance();
			
			if (args.length == 1) {
				try {
					if (logManager.isDebug()) {
						logManager.setDebug(false);
						sender.sendMessage(language.getMessage("misc.debug.disabled"));
					} else {
						logManager.setDebug(true);
						sender.sendMessage(language.getMessage("misc.debug.enabled"));
					}
				} catch (IOException e) {
					LogManager.log("IOException occurred while toggling the debug mode: {0}", 2, e.getMessage());
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
						if (manager.equalsIgnoreCase(args[1])) {
							if (Debugger.getEnabledManagersNames().contains(manager)) {
								sender.sendMessage(language.getMessage("misc.debug.manager.info", manager + "Manager"));
								sender.sendMessage(Debugger.getContent(getOriginalClass(ChatPluginManagers.getInstance().getManager(manager))).replaceAll(" +", " ").trim());
							} else sender.sendMessage(language.getMessage("misc.debug.manager.disabled"));
							return;
						}
					} sendUsage(sender, language);
					break;
				}
			}
		}
		
		private static Class<? extends ChatPluginManager> getOriginalClass(ChatPluginManager manager) {
			for (Entry<Class<? extends ChatPluginManager>, ChatPluginManager> entry : ChatPluginManagers.getInstance().getManagers().entrySet())
				if (entry.getValue().equals(manager))
					return entry.getKey();
			return null;
		}
		
	}
	
}
