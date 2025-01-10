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

package me.remigio07.chatplugin.server.gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.Punishment;
import me.remigio07.chatplugin.api.common.punishment.ban.Ban;
import me.remigio07.chatplugin.api.common.punishment.ban.BanManager;
import me.remigio07.chatplugin.api.common.punishment.ban.BanType;
import me.remigio07.chatplugin.api.common.punishment.kick.Kick;
import me.remigio07.chatplugin.api.common.punishment.kick.KickManager;
import me.remigio07.chatplugin.api.common.punishment.mute.Mute;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.punishment.warning.Warning;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition.WhereOperator;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.FillableGUILayout;
import me.remigio07.chatplugin.api.server.gui.GUIFiller;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.gui.IconLayout;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUILayout;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07.chatplugin.api.server.integration.anticheat.Violation;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.common.util.Utils;

public class InternalGUIs {
	
	private static SinglePageGUILayout playerInfoLayout, preferencesLayout;
	private static FillableGUILayout playerPunishmentsLayout, playerViolationsLayout;
	private static String[] preferencesPlaceholders;
	
	public static void createMain() {
		try {
			SinglePageGUI main = (SinglePageGUI) manager().createGUI(manager().createGUILayout(ConfigurationType.MAIN_GUI.get()));
			
			main.setServerTitlesTranslator();
			main.setServerStringPlaceholdersTranslator();
			main.refresh();
			manager().addGUI(main);
		} catch (Exception e) {
			error(e, "main");
		}
	}
	
	public static void createLanguages() {
		try {
			manager().addGUI(manager().createGUI(manager().createGUILayout(ConfigurationType.LANGUAGES_GUI.get())));
		} catch (Exception e) {
			error(e, "languages");
		}
	}
	
	public static void createChatColor() {
		try {
			SinglePageGUI chatColor = (SinglePageGUI) manager().createGUI(manager().createGUILayout(ConfigurationType.CHAT_COLOR_GUI.get()));
			
			chatColor.setServerTitlesTranslator();
			chatColor.setServerStringPlaceholdersTranslator();
			chatColor.refresh();
			manager().addGUI(chatColor);
		} catch (Exception e) {
			error(e, "chat-color");
		}
	}
	
	public static void createEmojisTone() {
		try {
			SinglePageGUI emojisTone = (SinglePageGUI) manager().createGUI(manager().createGUILayout(ConfigurationType.EMOJIS_TONE_GUI.get()));
			
			emojisTone.setServerTitlesTranslator();
			emojisTone.setServerStringPlaceholdersTranslator();
			emojisTone.refresh();
			manager().addGUI(emojisTone);
		} catch (Exception e) {
			error(e, "emojis-tone");
		}
	}
	
	public static void createBanlist() {
		if (BanManager.getInstance().isEnabled()) {
			try {
				@SuppressWarnings("unchecked")
				FillableGUI<Ban> banlist = (FillableGUI<Ban>) manager().createGUI(manager().createGUILayout(ConfigurationType.BANLIST_GUI.get()));
				
				banlist.setServerTitlesTranslator();
				banlist.setServerStringPlaceholdersTranslator();
				banlist.setFillers(BanManager.getInstance().getActiveBans().stream().map(ban -> new GUIFiller<Ban>() {
					
					@Override
					public FillableGUI<Ban> getGUI() {
						return banlist;
					}
					
					@Override
					public Ban getFiller() {
						return ban;
					}
					
					@Override
					public String formatPlaceholders(String input, Language language) {
						return ban.formatPlaceholders(input, language);
					}
					
					@Override
					public IconLayout getIconLayout() {
						return banlist.getLayout().getIconLayout(ban.getType() == BanType.ACCOUNT ? "ban" : "banip");
					}
					
				}).collect(Collectors.toCollection(CopyOnWriteArrayList::new)), true);
				manager().addGUI(banlist);
			} catch (Exception e) {
				error(e, "banlist");
			}
		}
	}
	
	public static void createWarnlist() {
		if (WarningManager.getInstance().isEnabled()) {
			try {
				@SuppressWarnings("unchecked")
				FillableGUI<Warning> warnlist = (FillableGUI<Warning>) manager().createGUI(manager().createGUILayout(ConfigurationType.WARNLIST_GUI.get()));
				
				warnlist.setServerTitlesTranslator();
				warnlist.setServerStringPlaceholdersTranslator();
				warnlist.setFillers(WarningManager.getInstance().getActiveWarnings().stream().map(warning -> new GUIFiller<Warning>() {
					
					@Override
					public FillableGUI<Warning> getGUI() {
						return warnlist;
					}
					
					@Override
					public Warning getFiller() {
						return warning;
					}
					
					@Override
					public String formatPlaceholders(String input, Language language) {
						return warning.formatPlaceholders(input, language);
					}
					
				}).collect(Collectors.toCollection(CopyOnWriteArrayList::new)), true);
				manager().addGUI(warnlist);
			} catch (Exception e) {
				error(e, "warnlist");
			}
		}
	}
	
	public static void createMutelist() {
		if (MuteManager.getInstance().isEnabled()) {
			try {
				@SuppressWarnings("unchecked")
				FillableGUI<Mute> mutelist = (FillableGUI<Mute>) manager().createGUI(manager().createGUILayout(ConfigurationType.MUTELIST_GUI.get()));
				
				mutelist.setServerTitlesTranslator();
				mutelist.setServerStringPlaceholdersTranslator();
				mutelist.setFillers(MuteManager.getInstance().getActiveMutes().stream().map(mute -> new GUIFiller<Mute>() {
					
					@Override
					public FillableGUI<Mute> getGUI() {
						return mutelist;
					}
					
					@Override
					public Mute getFiller() {
						return mute;
					}
					
					@Override
					public String formatPlaceholders(String input, Language language) {
						return mute.formatPlaceholders(input, language);
					}
					
				}).collect(Collectors.toCollection(CopyOnWriteArrayList::new)), true);
				manager().addGUI(mutelist);
			} catch (Exception e) {
				error(e, "mutelist");
			}
		}
	}
	
	public static void createViolations() {
		if (AnticheatManager.getInstance().isEnabled()) {
			try {
				@SuppressWarnings("unchecked")
				FillableGUI<OfflinePlayer> violations = (FillableGUI<OfflinePlayer>) manager().createGUI(manager().createGUILayout(ConfigurationType.VIOLATIONS_GUI.get()));
				
				violations.setServerTitlesTranslator();
				violations.setServerStringPlaceholdersTranslator();
				violations.setFillers(AnticheatManager.getInstance().getViolations().keySet().stream().map(player -> new GUIFiller<OfflinePlayer>() {
					
					@Override
					public FillableGUI<OfflinePlayer> getGUI() {
						return violations;
					}
					
					@Override
					public OfflinePlayer getFiller() {
						return player;
					}
					
					@Override
					public String formatPlaceholders(String input, Language language) {
						List<Violation> violationsList = AnticheatManager.getInstance().getViolations(player);
						Violation lastViolation = violationsList.get(0);
						
						for (Violation violation : violationsList)
							if (violation.getLastTime() > lastViolation.getLastTime())
								lastViolation = violation;
						return lastViolation.formatPlaceholders(input, language);
					}
					
					@Override
					public List<String> formatPlaceholders(List<String> input, Language language) {
						int index = input.indexOf("{violations}");
						List<String> list = input;
						
						if (index != -1) {
							String format = ConfigurationType.VIOLATIONS_GUI.get().translateString("settings.violations-list-format", Utils.STRING_NOT_FOUND, false);
							list = new ArrayList<>(input);
							
							list.remove(index);
							list.addAll(index, AnticheatManager.getInstance().getViolations(player).stream().map(violation -> violation.formatPlaceholders(format, language)).collect(Collectors.toList()));
						} return list.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
					}
					
				}).collect(Collectors.toCollection(CopyOnWriteArrayList::new)), true);
				manager().addGUI(violations);
			} catch (Exception e) {
				error(e, "violations");
			}
		}
	}
	
	public static void preparePlayerInfo() {
		try {
			playerInfoLayout = (SinglePageGUILayout) manager().createGUILayout(ConfigurationType.PLAYER_INFO_GUI.get());
		} catch (Exception e) {
			error(e, "player-info");
		}
	}
	
	public static SinglePageGUI createPlayerInfo(ChatPluginServerPlayer player) {
		SinglePageGUI playerInfo = manager().createPerPlayerGUI(playerInfoLayout, player);
		
		TaskManager.runAsync(() -> player.getIPLookup(true), 0L);
		playerInfo.setTitlesTranslator((t, u) -> PlaceholderManager.getInstance().translatePlaceholders(t, player, Arrays.asList(PlaceholderType.SERVER, PlaceholderType.PLAYER)));
		playerInfo.setStringPlaceholdersTranslator((t, u, v) -> PlaceholderManager.getInstance().translatePlaceholders(t, player, u, Arrays.asList(PlaceholderType.SERVER, PlaceholderType.PLAYER)));
		playerInfo.refresh();
		return playerInfo;
	}
	
	public static SinglePageGUILayout getPlayerInfoLayout() {
		return playerInfoLayout;
	}
	
	public static void preparePreferences() {
		try {
			preferencesLayout = (SinglePageGUILayout) manager().createGUILayout(ConfigurationType.PREFERENCES_GUI.get());
			preferencesPlaceholders = new String[] { ConfigurationType.PREFERENCES_GUI.get().translateString("settings.visibility-placeholder-format.enabled"), ConfigurationType.PREFERENCES_GUI.get().translateString("settings.visibility-placeholder-format.disabled") };
		} catch (Exception e) {
			error(e, "preferences");
		}
	}
	
	public static SinglePageGUI createPreferences(ChatPluginServerPlayer player) {
		SinglePageGUI playerInfo = manager().createPerPlayerGUI(preferencesLayout, player);
		
		playerInfo.setTitlesTranslator((t, u) -> PlaceholderManager.getInstance().translatePlaceholders(t, player, Arrays.asList(PlaceholderType.SERVER, PlaceholderType.PLAYER)));
		playerInfo.setStringPlaceholdersTranslator((t, u, v) -> PlaceholderManager.getInstance().translatePlaceholders(t, player, u, Arrays.asList(PlaceholderType.SERVER, PlaceholderType.PLAYER))
				.replace("{scoreboard_visibility}", preferencesPlaceholders[player.getScoreboard() == null ? 1 : 0])
				.replace("{bossbar_visibility}", preferencesPlaceholders[player.getBossbar() == null ? 1 : 0])
				.replace("{actionbar_visibility}", preferencesPlaceholders[player.hasActionbarEnabled() ? 0 : 1])
				);
		playerInfo.refresh();
		return playerInfo;
	}
	
	public static SinglePageGUILayout getPreferencesLayout() {
		return preferencesLayout;
	}
	
	public static void preparePlayerPunishments() {
		try {
			playerPunishmentsLayout = (FillableGUILayout) manager().createGUILayout(ConfigurationType.PLAYER_PUNISHMENTS_GUI.get());
			
			if (!playerPunishmentsLayout.getIconsLayouts().stream().map(IconLayout::getID).collect(Collectors.toList()).containsAll(Arrays.asList("ban", "warning", "kick", "mute")))
				throw new IllegalArgumentException("At least one of the following icons layouts' IDs is missing: [ban, warning, kick, mute]");
		} catch (Exception e) {
			error(e, "player-punishments");
		}
	}
	
	public static FillableGUI<Punishment> createPlayerPunishments(ChatPluginServerPlayer player) {
		@SuppressWarnings("unchecked")
		FillableGUI<Punishment> playerPunishments = (FillableGUI<Punishment>) manager().createPerPlayerGUI(playerPunishmentsLayout, player);
		StorageConnector storage = StorageConnector.getInstance();
		List<Punishment> punishments = new ArrayList<>();
		
		try {
			if (BanManager.getInstance().isEnabled())
				for (Number id : storage.getColumnValues(DataContainer.BANS, "id", Number.class, new WhereCondition("player_uuid", WhereOperator.EQUAL, player.getUUID().toString())))
					punishments.add(storage.getBan(id.intValue()));
			if (WarningManager.getInstance().isEnabled())
				for (Number id : storage.getColumnValues(DataContainer.WARNINGS, "id", Number.class, new WhereCondition("player_uuid", WhereOperator.EQUAL, player.getUUID().toString())))
					punishments.add(storage.getWarning(id.intValue()));
			if (KickManager.getInstance().isEnabled())
				for (Number id : storage.getColumnValues(DataContainer.KICKS, "id", Number.class, new WhereCondition("player_uuid", WhereOperator.EQUAL, player.getUUID().toString())))
					punishments.add(storage.getKick(id.intValue()));
			if (MuteManager.getInstance().isEnabled())
				for (Number id : storage.getColumnValues(DataContainer.MUTES, "id", Number.class, new WhereCondition("player_uuid", WhereOperator.EQUAL, player.getUUID().toString())))
					punishments.add(storage.getMute(id.intValue()));
		} catch (SQLException e) {
			
		} playerPunishments.setTitlesTranslator((t, u, v) -> PlaceholderManager.getInstance().translatePlaceholders(t, player, Arrays.asList(PlaceholderType.SERVER, PlaceholderType.PLAYER)));
		playerPunishments.setStringPlaceholdersTranslator((t, u, v) -> PlaceholderManager.getInstance().translatePlaceholders(t, player, u, Arrays.asList(PlaceholderType.SERVER, PlaceholderType.PLAYER)));
		playerPunishments.setFillers(punishments.stream().map(punishment -> new GUIFiller<Punishment>() {
			
			@Override
			public FillableGUI<Punishment> getGUI() {
				return playerPunishments;
			}
			
			@Override
			public Punishment getFiller() {
				return punishment;
			}
			
			@Override
			public String formatPlaceholders(String input, Language language) {
				return punishment.formatPlaceholders(input, language);
			}
			
			@Override
			public IconLayout getIconLayout() {
				return punishment instanceof Ban ? playerPunishments.getLayout().getIconLayout("ban") : punishment instanceof Warning ? playerPunishments.getLayout().getIconLayout("warning") : punishment instanceof Kick ? playerPunishments.getLayout().getIconLayout("kick") : playerPunishments.getLayout().getIconLayout("mute");
			}
			
		}).collect(Collectors.toCollection(CopyOnWriteArrayList::new)), true);
		return playerPunishments;
	}
	
	public static FillableGUILayout getPlayerPunishmentsLayout() {
		return playerPunishmentsLayout;
	}
	
	public static void preparePlayerViolations() {
		try {
			playerViolationsLayout = (FillableGUILayout) manager().createGUILayout(ConfigurationType.PLAYER_VIOLATIONS_GUI.get());
		} catch (Exception e) {
			error(e, "player-violations");
		}
	}
	
	public static FillableGUI<Violation> createPlayerViolations(ChatPluginServerPlayer player) {
		FillableGUI<Violation> playerViolations = manager().createPerPlayerGUI(playerViolationsLayout, player);
		
		playerViolations.setTitlesTranslator((t, u, v) -> PlaceholderManager.getInstance().translatePlaceholders(t, player, Arrays.asList(PlaceholderType.SERVER, PlaceholderType.PLAYER)));
		playerViolations.setStringPlaceholdersTranslator((t, u, v) -> PlaceholderManager.getInstance().translatePlaceholders(t, player, u, Arrays.asList(PlaceholderType.SERVER, PlaceholderType.PLAYER)));
		playerViolations.setFillers(AnticheatManager.getInstance().getViolations(player).stream().map(violation -> new GUIFiller<Violation>() {
			
			@Override
			public FillableGUI<Violation> getGUI() {
				return playerViolations;
			}
			
			@Override
			public Violation getFiller() {
				return violation;
			}
			
			@Override
			public String formatPlaceholders(String input, Language language) {
				return violation.formatPlaceholders(input, language);
			}
			
			@Override
			public Icon getIcon(Icon icon) {
				String path = violation.getAnticheat().name().toLowerCase() + "." + violation.getCheatID().toLowerCase() + ".";
				
				try {
					return icon
							.setMaterial(new MaterialAdapter(ConfigurationType.VIOLATIONS_ICONS.get().getString(path + "material")))
//							.setAmount(ConfigurationType.PLAYER_VIOLATIONS_GUI.get().getString("") )
							.setDamage(ConfigurationType.VIOLATIONS_ICONS.get().getShort(path + "damage"))
							.setSkullOwner(ConfigurationType.VIOLATIONS_ICONS.get().getString(path + "skull-owner"));
				} catch (IllegalArgumentException e) {
					LogManager.log("Invalid material ID found at {0} in violations-icons.yml: {1}.", 2, path + "material", ConfigurationType.VIOLATIONS_ICONS.get().getString(path + "material", Utils.STRING_NOT_FOUND));
					return icon.setMaterial(new MaterialAdapter("BARRIER"));
				}
			}
			
		}).collect(Collectors.toCollection(CopyOnWriteArrayList::new)), true);
		return playerViolations;
	}
	
	public static FillableGUILayout getPlayerViolationsLayout() {
		return playerViolationsLayout;
	}
	
	public static void clearLayouts() {
		playerInfoLayout = preferencesLayout = null;
		playerPunishmentsLayout = playerViolationsLayout = null;
	}
	
	private static GUIManager manager() {
		return GUIManager.getInstance();
	}
	
	private static void error(Exception e, String id) {
		LogManager.log("{0} occurred while loading GUI \"{1}\": {2}; skipping it.", 2, e.getClass().getSimpleName(), id, e.getMessage() == null ? "<no error message>" : Character.toLowerCase(e.getMessage().charAt(0)) + e.getMessage().substring(1));
	}
	
}
