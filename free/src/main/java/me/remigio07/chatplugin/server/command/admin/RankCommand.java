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

package me.remigio07.chatplugin.server.command.admin;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationMappings;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.HoverInfoManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.CommandSenderAdapter;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.bukkit.manager.BukkitPlayerManager;
import me.remigio07.chatplugin.server.chat.BaseHoverInfoManager;
import me.remigio07.chatplugin.server.command.BaseCommand;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.rank.RankManagerImpl;
import me.remigio07.chatplugin.server.sponge.manager.SpongePlayerManager;
import me.remigio07.chatplugin.server.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.DisplayNameNode;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;

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
											
											if (sender.isConsole())
												sender.sendMessage(language.getMessage("commands.rank.added.text", args[1]));
											else ((BaseChatPluginServerPlayer) sender.toServerPlayer()).sendMessage(Utils.deserializeLegacy(language.getMessage("commands.rank.add.added.text", args[1]), false)
													.hoverEvent(HoverEvent.showText(Utils.deserializeLegacy(language.getMessage("commands.rank.add.added.hover", args[1]), false)))
													.clickEvent(ClickEvent.runCommand("/chatplugin reload"))
													);
										} catch (IOException ioe) {
											LogManager.log("IOException occurred while saving ranks.yml: {0}", 2, ioe.getLocalizedMessage());
										}
									} else sender.sendMessage(language.getMessage("misc.invalid-rank", args[1]));
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
			return Arrays.asList("remove", "delete", "rem", "del", "r");
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
							
							if (sender.isConsole())
								sender.sendMessage(language.getMessage("commands.rank.removed.text", rank.getID()));
							else ((BaseChatPluginServerPlayer) sender.toServerPlayer()).sendMessage(Utils.deserializeLegacy(language.getMessage("commands.rank.removed.text", rank.getID()), false)
									.hoverEvent(HoverEvent.showText(Utils.deserializeLegacy(language.getMessage("commands.rank.removed.hover", rank.getID()), false)))
									.clickEvent(ClickEvent.runCommand("/chatplugin reload"))
									);
						} catch (IOException ioe) {
							LogManager.log("IOException occurred while saving ranks.yml: {0}", 2, ioe.getLocalizedMessage());
						}
					} else sender.sendMessage(language.getMessage("misc.invalid-rank", args[1]));
				} else sender.sendMessage(language.getMessage("commands.rank.luckperms-mode"));
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
				TextComponent[] components = new TextComponent[ranks.size()];
				String text = language.getMessage("commands.rank.list.message-format.text");
				String hover = language.getMessage("commands.rank.list.message-format.hover");
				
				sender.sendMessage(language.getMessage("commands.rank.list.header"));
				
				for (int i = 0; i < ranks.size(); i++) {
					Rank rank = ranks.get(i);
					components[i] = Utils.deserializeLegacy(rank.formatPlaceholders(text, language), false)
							.hoverEvent(HoverEvent.showText(Utils.deserializeLegacy(rank.formatPlaceholders(hover, language), false)))
							.clickEvent(ClickEvent.runCommand("/rank info " + rank.getID()));
				} sendComponents(sender, components);
				
				if (pages > 1) {
					String[] footer = formatPlaceholders(language.getMessage("page-switcher.footer") + " ", page + 1, pages).split(Pattern.quote("{page_switcher}"));
					
					if (footer.length == 2) {
						TextComponent component = Utils.deserializeLegacy(footer[0], false);
						TextComponent pageSwitcher = Component.empty();
						boolean hasPreviousPage = page != 0;
						
						if (hasPreviousPage)
							pageSwitcher = pageSwitcher.append(Utils.deserializeLegacy(formatPlaceholders(language.getMessage("page-switcher.previous.text"), page + 1, pages).replace("{previous_page}", String.valueOf(page)), false)
									.hoverEvent(HoverEvent.showText(Utils.deserializeLegacy(formatPlaceholders(language.getMessage("page-switcher.previous.hover"), page + 1, pages).replace("{previous_page}", String.valueOf(page)), false)))
									.clickEvent(ClickEvent.runCommand("/rank list " + String.valueOf(page)))
									);
						if (page != pages - 1)
							pageSwitcher = (hasPreviousPage ? pageSwitcher.append(Component.space()) : pageSwitcher).append(Utils.deserializeLegacy(formatPlaceholders(language.getMessage("page-switcher.next.text"), page + 1, pages).replace("{next_page}", String.valueOf(page + 2)), false)
									.hoverEvent(HoverEvent.showText(Utils.deserializeLegacy(formatPlaceholders(language.getMessage("page-switcher.next.hover"), page + 1, pages).replace("{next_page}", String.valueOf(page + 2)), false)))
									.clickEvent(ClickEvent.runCommand("/rank list " + String.valueOf(page + 2)))
									);
						sendComponents(sender, component.append(pageSwitcher).append(Utils.deserializeLegacy(footer[1] == " " ? "" : footer[1], false)));
					} else sender.sendMessage(language.getMessage("misc.prefix") + ChatColor.translate(" &cMessage specified at &fpage-switcher.footer &cin &f" + language.getConfiguration().getFile().getName() + " &cdoes not contain the &f{page_switcher} &cplaceholder."));
				}
			} else sender.sendMessage(formatPlaceholders(language.getMessage("page-switcher.invalid"), page + 1, pages));
		}
		
		private static String formatPlaceholders(String input, int currentPage, int maxPage) {
			return input
					.replace("{current_page}", String.valueOf(currentPage))
					.replace("{max_page}", String.valueOf(maxPage));
		}
		
		private static void sendComponents(CommandSenderAdapter sender, TextComponent... components) {
			if (sender.isConsole())
				for (TextComponent component : components)
					sender.sendMessage(Utils.serializeLegacy(component));
			else ((BaseChatPluginServerPlayer) sender.toServerPlayer()).sendMessage(components);
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
				else sender.sendMessage(language.getMessage("misc.invalid-rank", args[1]));
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
							
							// refresh stuff
							if (args[2].startsWith("descriptions.")) {
								if (HoverInfoManager.getInstance().isEnabled())
									((BaseHoverInfoManager) HoverInfoManager.getInstance()).loadRanksDescriptions();
							} else if (TablistManager.getInstance().isEnabled()) {
								String placeholder = '{' + (args[2].equals("display-name") ? "rank_" : "") + args[2].replace('.', '_').replace('-', '_') + '}';
								
								// this is horrible, but kinda required; maybe I could merge the setupTeams and the logic in loadPlayer(...) (BukkitPlayerManager, SpongePlayerManager)...?
								if (TablistManager.getInstance().getPrefixFormat().contains(placeholder) || TablistManager.getInstance().getSuffixFormat().contains(placeholder))
									for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values()) {
										boolean longTeams = VersionUtils.getVersion().isAtLeast(Version.V1_13) && player.getVersion().isAtLeast(Version.V1_13);
										// also, we could probably update concerned ranks only
										for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
											if (!player.equals(other))
												if (Environment.isBukkit()) {
													((BukkitPlayerManager) ServerPlayerManager.getInstance()).setupTeams(player, other, longTeams);
													((BukkitPlayerManager) ServerPlayerManager.getInstance()).setupTeams(other, player, longTeams);
												} else {
													((SpongePlayerManager) ServerPlayerManager.getInstance()).setupTeams(player, other);
													((SpongePlayerManager) ServerPlayerManager.getInstance()).setupTeams(other, player);
												}
										} if (Environment.isBukkit())
											((BukkitPlayerManager) ServerPlayerManager.getInstance()).setupTeams(player, player, longTeams);
										else ((SpongePlayerManager) ServerPlayerManager.getInstance()).setupTeams(player, player);
									}
							}
						} catch (IllegalStateException e) {
							sender.sendMessage(language.getMessage("commands.rank.edit.out-of-sync", e.getMessage()));
						}
					} else sender.sendMessage(language.getMessage("commands.rank.edit.invalid-property", Utils.getStringFromList(RankManagerImpl.getProperties(), false, true)));
				} else sender.sendMessage(language.getMessage("misc.invalid-rank", args[1]));
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
	
}
