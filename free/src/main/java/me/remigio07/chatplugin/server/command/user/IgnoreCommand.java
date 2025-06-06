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

package me.remigio07.chatplugin.server.command.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.command.PlayerCommand;

public class IgnoreCommand extends PlayerCommand {
	
	public IgnoreCommand() {
		super("/ignore <add|remove|clear|list> [player]");
		tabCompletionArgs.put(0, Arrays.asList("add", "remove", "clear", "list"));
	}
	
	@Override
	public java.util.List<String> getMainArgs() {
		return Arrays.asList("ignore", "avoid");
	}
	
	@Override
	public boolean hasSubCommands() {
		return true;
	}
	
	@Override
	public void execute(ChatPluginServerPlayer player, String[] args) {
		if (PlayerIgnoreManager.getInstance().isEnabled())
			sendUsage(player);
		else player.sendTranslatedMessage("misc.disabled-feature");
	}
	
	public static class Add extends PlayerCommand {
		
		public Add() {
			super("/ignore add <player>");
			tabCompletionArgs.put(1, Arrays.asList("{players_excluding_self}"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("add", "a");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.ignore.add";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(ChatPluginServerPlayer player, String[] args) {
			if (PlayerIgnoreManager.getInstance().isEnabled()) {
				if (args.length == 2) {
					TaskManager.runAsync(() -> {
						try {
							OfflinePlayer target = OfflinePlayer.get(args[1]).get();
							
							if (!target.equals(player)) {
								if (target.hasPlayedBefore()) {
									if (!target.hasPermission(getPermission() + ".bypass")) {
										java.util.List<OfflinePlayer> ignoredPlayers = player.getIgnoredPlayers();
										
										if (!ignoredPlayers.contains(target)) {
											if (ignoredPlayers.size() != 25) {
												if (PlayerIgnoreManager.getInstance().ignore(player, target)) {
													player.sendTranslatedMessage("commands.ignore.added.success.self", target.getName(), ignoredPlayers.size());
													
													if (target.isLoaded())
														target.toServerPlayer().sendTranslatedMessage("commands.ignore.added.success.other", player.getName());
												}
											} else player.sendTranslatedMessage("commands.ignore.max-reached");
										} else player.sendTranslatedMessage("commands.ignore.added.already-ignoring", target.getName());
									} else player.sendTranslatedMessage("commands.ignore.cannot-ignore.other", target.getName());
								} else player.sendTranslatedMessage("commands.ignore.cannot-ignore.self");
							} else player.sendTranslatedMessage("misc.player-not-stored", target.getName());
						} catch (IllegalArgumentException iae) {
							player.sendTranslatedMessage("misc.invalid-player-name");
						} catch (InterruptedException | ExecutionException e) {
							if (e.getCause() instanceof NoSuchElementException)
								player.sendTranslatedMessage("misc.inexistent-player", args[1]);
							else player.sendTranslatedMessage("misc.error-occurred", e.getClass().getSimpleName(), e.getLocalizedMessage());
						}
					}, 0L);
				} else sendUsage(player);
			} else player.sendTranslatedMessage("misc.disabled-feature");
		}
		
	}
	
	public static class Remove extends PlayerCommand {
		
		public Remove() {
			super("/ignore remove <player>");
			tabCompletionArgs.put(1, Arrays.asList("{ignored_players}"));
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("remove", "rm", "r");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.ignore.remove";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(ChatPluginServerPlayer player, String[] args) {
			if (PlayerIgnoreManager.getInstance().isEnabled()) {
				if (args.length == 2) {
					TaskManager.runAsync(() -> {
						try {
							OfflinePlayer target = OfflinePlayer.get(args[1]).get();
							
							if (target.hasPlayedBefore()) {
								java.util.List<OfflinePlayer> ignoredPlayers = player.getIgnoredPlayers();
								
								if (ignoredPlayers.contains(target)) {
									try {
										if (PlayerIgnoreManager.getInstance().unignore(player, target)) {
											player.sendTranslatedMessage("commands.ignore.removed.success.self", target.getName(), ignoredPlayers.size());
											
											if (target.isLoaded())
												target.toServerPlayer().sendTranslatedMessage("commands.ignore.removed.success.other", player.getName());
										}
									} catch (IllegalArgumentException e) {
										
									} return;
								} player.sendTranslatedMessage("commands.ignore.removed.not-ignoring", target.getName());
							} else player.sendTranslatedMessage("misc.player-not-stored", target.getName());
						} catch (IllegalArgumentException iae) {
							player.sendTranslatedMessage("misc.invalid-player-name");
						} catch (InterruptedException | ExecutionException e) {
							if (e.getCause() instanceof NoSuchElementException)
								player.sendTranslatedMessage("misc.inexistent-player", args[1]);
							else player.sendTranslatedMessage("misc.error-occurred", e.getClass().getSimpleName(), e.getLocalizedMessage());
						}
					}, 0L);
				} else sendUsage(player);
			} else player.sendTranslatedMessage("misc.disabled-feature");
		}
		
	}
	
	public static class Clear extends PlayerCommand {
		
		public Clear() {
			super("/ignore clear");
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("clear", "c");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.ignore.clear";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(ChatPluginServerPlayer player, String[] args) {
			if (PlayerIgnoreManager.getInstance().isEnabled()) {
				java.util.List<OfflinePlayer> ignoredPlayers = player.getIgnoredPlayers();
				
				if (!ignoredPlayers.isEmpty()) {
					for (OfflinePlayer ignored : new ArrayList<>(ignoredPlayers))
						if (!PlayerIgnoreManager.getInstance().unignore(player, ignored))
							return;
					player.sendTranslatedMessage("commands.ignore.cleared");
				} else player.sendTranslatedMessage("commands.ignore.no-ignored.self");
			} else player.sendTranslatedMessage("misc.disabled-feature");
		}
		
	}
	
	public static class List extends PlayerCommand {
		
		public List() {
			super("/ignore list [player]");
			tabCompletionArgs.put(1, players);
		}
		
		@Override
		public java.util.List<String> getMainArgs() {
			return Arrays.asList("list", "ls", "l");
		}
		
		@Override
		public String getPermission() {
			return "chatplugin.commands.ignore.list";
		}
		
		@Override
		public boolean isSubCommand() {
			return true;
		}
		
		@Override
		public void execute(ChatPluginServerPlayer player, String[] args) {
			if (PlayerIgnoreManager.getInstance().isEnabled()) {
				if (args.length == 2) {
					if (player.hasPermission(getPermission() + ".others")) {
						TaskManager.runAsync(() -> {
							try {
								OfflinePlayer target = OfflinePlayer.get(args[1]).get();
								
								if (target.hasPlayedBefore()) {
									java.util.List<OfflinePlayer> ignoredPlayers = PlayerIgnoreManager.getInstance().getIgnoredPlayers(target);
									
									if (ignoredPlayers.isEmpty())
										player.sendTranslatedMessage("commands.ignore.no-ignored.other", target.getName());
									else player.sendTranslatedMessage("commands.ignore.list.other", target.getName(), ignoredPlayers.size(), Utils.getStringFromList(ignoredPlayers.stream().map(OfflinePlayer::getName).collect(Collectors.toList()), false, false));
								} else player.sendTranslatedMessage("misc.player-not-stored", target.getName());
							} catch (IllegalArgumentException iae) {
								player.sendTranslatedMessage("misc.invalid-player-name");
							} catch (InterruptedException | ExecutionException e) {
								if (e.getCause() instanceof NoSuchElementException)
									player.sendTranslatedMessage("misc.inexistent-player", args[1]);
								else player.sendTranslatedMessage("misc.error-occurred", e.getClass().getSimpleName(), e.getLocalizedMessage());
							}
						}, 0L);
					} else player.sendTranslatedMessage("misc.no-permission");
				} else {
					java.util.List<OfflinePlayer> ignoredPlayers = player.getIgnoredPlayers();
					
					if (ignoredPlayers.isEmpty())
						player.sendTranslatedMessage("commands.ignore.no-ignored.self");
					else player.sendTranslatedMessage("commands.ignore.list.self", ignoredPlayers.size(), Utils.getStringFromList(ignoredPlayers.stream().map(OfflinePlayer::getName).collect(Collectors.toList()), false, false));
				}
			} else player.sendTranslatedMessage("misc.disabled-feature");
		}
		
	}
	
}
