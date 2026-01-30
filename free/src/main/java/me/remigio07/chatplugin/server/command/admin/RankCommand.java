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

package me.remigio07.chatplugin.server.command.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationMappings;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.HoverInfoManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.chat.BaseHoverInfoManager;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.rank.RankManagerImpl;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.DisplayNameNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class RankCommand extends BaseCommand {
	
	public RankCommand() {
		super("/rank <add|remove|edit|info|list>");
		tabCompletionArgs.put(0, Arrays.asList("add", "remove", "edit", "info", "list"));
	}
	
	@Override
	public java.util.List<String> getMainArgs() {
		return Arrays.asList("rank", "managerank");
	}
	
	@Override
	public boolean hasSubCommands() {
		return true;
	}
	
	@Override
	public void execute(CommandSenderAdapter sender, Language language, String[] args) {
		sendUsage(sender, language);
	}
	
	public static class Add extends BaseCommand {
		
		public Add() {
			super("/rank add <ID> <position> [display name]");
			tabCompletionArgs.put(2, numbers);
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("add", "create", "new", "a");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.rank.add";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			if (args.length > 2) {
				RankManager manager = RankManager.getInstance();
				
				if (!manager.isLuckPermsMode()) {
					if (Utils.isInteger(args[2])) {
						int position = Integer.parseInt(args[2]);
						int size = ConfigurationType.RANKS.get().getKeys("ranks").size();
						
						if (position > -1 && position < size) {
							if (manager.getRank(args[1]) == null) { // overwrites if they have the same ID in ranks.yml
								if (size != 100) { // 99 ranks + settings section
									if (manager.isValidRankID(args[1])) {
										Map<String, Object> map = ConfigurationType.RANKS.get().getSection("ranks").getMappings();
										Map<String, Object> newMap = new LinkedHashMap<>();
										int i = 0;
										
										for (Entry<String, Object> entry : map.entrySet()) {
											if (i == position + 1)
												newMap.put(args[1], new ConfigurationMappings(ImmutableMap.of("display-name", args.length == 3 ? args[1] : String.join(" ", Arrays.copyOfRange(args, 3, args.length)))));
											newMap.put(entry.getKey(), entry.getValue());
											i++;
										} if (position == size - 1)
											newMap.put(args[1], new ConfigurationMappings(ImmutableMap.of("display-name", args.length == 3 ? args[1] : String.join(" ", Arrays.copyOfRange(args, 3, args.length)))));
										ConfigurationType.RANKS.get().set("ranks", newMap);
										
										try {
											ConfigurationType.RANKS.get().save();
											
											if (sender.isPlayer()) {
												BaseComponent component = Utils.toBungeeCordComponent(language.getMessage("commands.rank.add.added.text", args[1]));
												
												component.setHoverEvent(Utils.getHoverEvent(HoverEvent.Action.SHOW_TEXT, language.getMessage("commands.rank.add.added.hover", args[1])));
												component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatplugin reload"));
												((BaseChatPluginServerPlayer) sender.toServerPlayer()).sendMessage(component);
											} else sender.sendMessage(language.getMessage("commands.rank.added.text", args[1]));
										} catch (IOException ioe) {
											LogManager.log("IOException occurred while saving ranks.yml: {0}", 2, ioe.getLocalizedMessage());
										}
									} else sender.sendMessage(language.getMessage("misc.invalid-rank", args[1], manager.getRanks().stream().map(Rank::getID).collect(Collectors.toList())));
								} else sender.sendMessage(language.getMessage("commands.rank.add.reached-limit"));
							} else sender.sendMessage(language.getMessage("commands.rank.add.already-exists"));
						} else sender.sendMessage(language.getMessage("commands.rank.add.invalid-position", size - 1));
					} else sender.sendMessage(language.getMessage("misc.invalid-number", args[2]));
				} else sender.sendMessage(language.getMessage("commands.rank.luckperms-mode"));
			} else sendUsage(sender, language);
		}
		
	}
	
	public static class Remove extends BaseCommand {
		
		public Remove() {
			super("/rank remove <ID>");
			tabCompletionArgs.put(1, Arrays.asList("{ranks}"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("remove", "delete", "del", "rm", "r");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.rank.remove";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			if (args.length == 2) {
				if (!RankManager.getInstance().isLuckPermsMode()) {
					Rank rank = RankManager.getInstance().getRank(args[1]);
					
					if (rank != null) {
						ConfigurationType.RANKS.get().set("ranks." + rank.getID(), null);
						
						try {
							ConfigurationType.RANKS.get().save();
							
							if (sender.isPlayer()) {
								BaseComponent component = Utils.toBungeeCordComponent(language.getMessage("commands.rank.removed.text", rank.getID()));
								
								component.setHoverEvent(Utils.getHoverEvent(HoverEvent.Action.SHOW_TEXT, language.getMessage("commands.rank.removed.hover", rank.getID())));
								component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatplugin reload"));
								((BaseChatPluginServerPlayer) sender.toServerPlayer()).sendMessage(component);
							} else sender.sendMessage(language.getMessage("commands.rank.removed.text", rank.getID()));
						} catch (IOException ioe) {
							LogManager.log("IOException occurred while saving ranks.yml: {0}", 2, ioe.getLocalizedMessage());
						}
					} else sender.sendMessage(language.getMessage("misc.invalid-rank", args[1], RankManager.getInstance().getRanks().stream().map(Rank::getID).collect(Collectors.toList())));
				} else sender.sendMessage(language.getMessage("commands.rank.luckperms-mode"));
			} else sendUsage(sender, language);
		}
		
	}
	
	public static class Edit extends BaseCommand {
		
		public Edit() {
			super("/rank edit <ID> <property> <value>");
			tabCompletionArgs.put(1, Arrays.asList("{ranks}"));
			tabCompletionArgs.put(2, Arrays.asList("{ranks_properties}"));
			tabCompletionArgs.put(3, Arrays.asList("reset", "empty"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("edit", "set", "e");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.rank.edit";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			if (args.length > 3) {
				Rank rank = RankManager.getInstance().getRank(args[1]);
				
				if (rank != null) {
					if (RankManagerImpl.getProperties().contains(args[2])) {
						String value = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
						
						if (value.equals("empty"))
							value = "";
						else if (value.equals("reset"))
							value = null;
						if (RankManager.getInstance().isLuckPermsMode()) {
							Group group = LuckPermsProvider.get().getGroupManager().getGroup(rank.getID());
							
							if (group != null) {
								switch (args[2]) {
								case "display-name":
									group.data().clear(NodeType.DISPLAY_NAME::matches);
									
									if (value != null)
										LuckPermsAdapter.setDisplayName(group, value.isEmpty() ? "${empty_string}" : value);
									break;
								case "prefix":
									group.data().clear(NodeType.PREFIX::matches);
									
									if (value != null)
										LuckPermsAdapter.setPrefix(group, value, group.getWeight().orElse(1));
									break;
								case "suffix":
									group.data().clear(NodeType.SUFFIX::matches);
									
									if (value != null)
										LuckPermsAdapter.setSuffix(group, value, group.getWeight().orElse(1));
									break;
								case "tag.prefix":
									group.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(args[2])));
									
									if (value != null)
										LuckPermsAdapter.setMeta(group, args[2], value);
									break;
								case "tag.suffix":
									group.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(args[2])));
									
									if (value != null)
										LuckPermsAdapter.setMeta(group, args[2], value);
									break;
								case "tag.name-color":
									group.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(args[2])));
									
									if (value != null)
										LuckPermsAdapter.setMeta(group, args[2], value);
									break;
								case "chat-color":
									group.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(args[2])));
									
									if (value != null)
										LuckPermsAdapter.setMeta(group, args[2], value);
									break;
								case "max-punishment-durations.ban":
								case "max-punishment-durations.mute":
									if (value != null && Utils.getTime(value, false, true) == -1) {
										sender.sendMessage(language.getMessage("timestamps.invalid"));
										return;
									} group.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(args[2])));
									
									if (value != null)
										LuckPermsAdapter.setMeta(group, args[2], value);
									break;
								default:
									group.data().clear(NodeType.META.predicate(mn -> mn.getMetaKey().equals(args[2])));
									
									if (value != null) {
										LuckPermsAdapter.setMeta(group, args[2], value);
										
										value = value.replace("\\n", "\n");
									} break;
								} LuckPermsProvider.get().getGroupManager().saveGroup(group);
							} else sender.sendMessage(language.getMessage("commands.rank.edit.out-of-sync", rank.getID()));
						} else {
							if (args[2].startsWith("max-punishment-durations.")) {
								if (value != null && Utils.getTime(value, false, true) == -1) {
									sender.sendMessage(language.getMessage("timestamps.invalid"));
									return;
								}
							} else if (args[2].startsWith("descriptions.") && value != null)
								value = value.replace("\\n", "\n");
							ConfigurationType.RANKS.get().set("ranks." + rank.getID() + "." + args[2], value);
							
							try {
								ConfigurationType.RANKS.get().save();
							} catch (IOException ioe) {
								LogManager.log("IOException occurred while saving ranks.yml: {0}", 2, ioe.getLocalizedMessage());
								return;
							}
						} try {
							((RankManagerImpl) RankManager.getInstance()).loadRanks();
							sender.sendMessage(value == null
									? language.getMessage("commands.rank.edit.reset", args[2], rank.getID())
									: language.getMessage("commands.rank.edit.set", args[2], rank.getID(), value.isEmpty() ? "\"\"" : args[2].endsWith("-color") ? value : ChatColor.translate(value, false))
									);
							
							if (HoverInfoManager.getInstance().isEnabled() && args[2].startsWith("descriptions."))
								((BaseHoverInfoManager) HoverInfoManager.getInstance()).loadRanksDescriptions();
						} catch (IllegalStateException ise) {
							sender.sendMessage(language.getMessage("commands.rank.edit.out-of-sync", ise.getMessage()));
						}
					} else sender.sendMessage(language.getMessage("commands.rank.edit.invalid-property", Utils.getStringFromList(RankManagerImpl.getProperties(), false, true)));
				} else sender.sendMessage(language.getMessage("misc.invalid-rank", args[1], RankManager.getInstance().getRanks().stream().map(Rank::getID).collect(Collectors.toList())));
			} else sendUsage(sender, language);
		}
		
		public static class LuckPermsAdapter {
			
			public static void setDisplayName(Group group, String displayName) {
				group.data().add(DisplayNameNode.builder(displayName.isEmpty() ? "${empty_string}" : displayName).build());
			}
			
			public static void setPrefix(Group group, String prefix, int weight) {
				group.data().add(PrefixNode.builder(prefix, weight).build());
			}
			
			public static void setSuffix(Group group, String suffix, int weight) {
				group.data().add(SuffixNode.builder(suffix, weight).build());
			}
			
			public static void setMeta(Group group, String key, String value) {
				group.data().add(MetaNode.builder(key, value).build());
			}
			
		}
		
	}
	
	public static class Info extends BaseCommand {
		
		public Info() {
			super("/rank info <ID>");
			tabCompletionArgs.put(1, Arrays.asList("{ranks}"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("info", "information", "i");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.rank.info";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			if (args.length == 2) {
				Rank rank = RankManager.getInstance().getRank(args[1]);
				
				if (rank != null)
					sender.sendMessage(rank.formatPlaceholders(language.getMessage("commands.rank.info")
							.replace("{chat_color}", rank.getChatColor()), // gets translated otherwise
							language));
				else sender.sendMessage(language.getMessage("misc.invalid-rank", args[1], RankManager.getInstance().getRanks().stream().map(Rank::getID).collect(Collectors.toList())));
			} else sendUsage(sender, language);
		}
		
	}
	
	public static class List extends BaseCommand {
		
		public List() {
			super("/rank list [page]");
			tabCompletionArgs.put(1, numbers);
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("list", "ls", "l");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.rank.list";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(CommandSenderAdapter sender, Language language, String[] args) {
			int page = 0;
			
			if (args.length == 2) {
				if (!Utils.isPositiveInteger(args[1])) {
					sender.sendMessage(language.getMessage("misc.invalid-number", args[1]));
					return;
				} page = Integer.parseInt(args[1]) - 1;
			} java.util.List<Rank> ranks = RankManager.getInstance().getRanks();
			int pages = (ranks.size() - 1) / 8 + 1;
			
			if (page > -1 && page < pages) {
				ranks = ranks.subList(page * 8, page == ranks.size() / 8 ? ranks.size() : (page + 1) * 8);
				BaseComponent[] components = new BaseComponent[ranks.size()];
				String text = language.getMessage("commands.rank.list.message-format.text");
				String hover = language.getMessage("commands.rank.list.message-format.hover");
				
				sender.sendMessage(language.getMessage("commands.rank.list.header"));
				
				for (int i = 0; i < ranks.size(); i++) {
					Rank rank = ranks.get(i);
					components[i] = Utils.toBungeeCordComponent(rank.formatPlaceholders(text, language));
					
					components[i].setHoverEvent(Utils.getHoverEvent(HoverEvent.Action.SHOW_TEXT, rank.formatPlaceholders(hover, language)));
					components[i].setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank info " + rank.getID()));
				} sendComponents(sender, components);
				
				if (pages > 1) {
					String[] footer = formatPlaceholders(language.getMessage("page-switcher.footer") + " ", page + 1, pages).split(Pattern.quote("{page_switcher}"));
					
					if (footer.length == 2) {
						BaseComponent component = Utils.toBungeeCordComponent(footer[0]);
						BaseComponent pageSwitcher = new TextComponent("§r");
						boolean hasPreviousPage = page != 0;
						
						if (hasPreviousPage) {
							BaseComponent previous = Utils.toBungeeCordComponent(formatPlaceholders(language.getMessage("page-switcher.previous.text"), page + 1, pages).replace("{previous_page}", String.valueOf(page)));
							
							previous.setHoverEvent(Utils.getHoverEvent(HoverEvent.Action.SHOW_TEXT, formatPlaceholders(language.getMessage("page-switcher.previous.hover"), page + 1, pages).replace("{previous_page}", String.valueOf(page))));
							previous.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank list " + String.valueOf(page)));
							pageSwitcher.addExtra(previous);
						} if (page != pages - 1) {
							if (hasPreviousPage)
								pageSwitcher.addExtra(new TextComponent(" "));
							BaseComponent next = Utils.toBungeeCordComponent(formatPlaceholders(language.getMessage("page-switcher.next.text"), page + 1, pages).replace("{next_page}", String.valueOf(page + 2)));
							
							next.setHoverEvent(Utils.getHoverEvent(HoverEvent.Action.SHOW_TEXT, formatPlaceholders(language.getMessage("page-switcher.next.hover"), page + 1, pages).replace("{next_page}", String.valueOf(page + 2))));
							next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rank list " + String.valueOf(page + 2)));
							pageSwitcher.addExtra(next);
						} component.addExtra(pageSwitcher);
						component.addExtra(Utils.toBungeeCordComponent(footer[1] == " " ? "" : footer[1]));
						sendComponents(sender, component);
					} else sender.sendMessage(language.getMessage("misc.prefix") + ChatColor.translate(" &cMessage specified at &fpage-switcher.footer &cin &f" + language.getConfiguration().getPath().getFileName().toString() + " &cdoes not contain the &f{page_switcher} &cplaceholder."));
				}
			} else sender.sendMessage(formatPlaceholders(language.getMessage("page-switcher.invalid"), page + 1, pages));
		}
		
		private static String formatPlaceholders(String input, int currentPage, int maxPage) {
			return input
					.replace("{current_page}", String.valueOf(currentPage))
					.replace("{max_page}", String.valueOf(maxPage));
		}
		
		private static void sendComponents(CommandSenderAdapter sender, BaseComponent... components) {
			if (sender.isConsole())
				for (BaseComponent component : components)
					sender.sendMessage(Utils.toLegacyText(component));
			else ((BaseChatPluginServerPlayer) sender.toServerPlayer()).sendMessage(components);
		}
		
	}
	
}
