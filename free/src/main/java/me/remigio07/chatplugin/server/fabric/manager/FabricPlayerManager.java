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

package me.remigio07.chatplugin.server.fabric.manager;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;
import me.remigio07.chatplugin.api.server.event.player.ServerPlayerLoadEvent;
import me.remigio07.chatplugin.api.server.event.player.ServerPlayerUnloadEvent;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.PerPlayerGUI;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.tablist.Tablist;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.CustomSuffixManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.RenderTypeAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import me.remigio07.chatplugin.mixin.common.ScoreboardObjectiveAccessor;
import me.remigio07.chatplugin.mixin.extension.EntityExtension;
import me.remigio07.chatplugin.mixin.extension.ServerPlayerEntityExtension;
import me.remigio07.chatplugin.mixin.extension.ServerScoreboardExtension;
import me.remigio07.chatplugin.mixin.extension.ServerWorldExtension;
import me.remigio07.chatplugin.server.fabric.ChatPluginFabricPlayer;
import me.remigio07.chatplugin.server.join_quit.QuitMessageManagerImpl.QuitPacketImpl;
import me.remigio07.chatplugin.server.util.Utils;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardCriterion.RenderType;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FabricPlayerManager extends ServerPlayerManager {
	
	private long localeChangeTaskID;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		super.load();
		
		localeChangeTaskID = TaskManager.scheduleAsync(() -> {
			for (ChatPluginServerPlayer serverPlayer : new ArrayList<>(players.values())) {
				ChatPluginFabricPlayer player = (ChatPluginFabricPlayer) serverPlayer;
				
				if (System.currentTimeMillis() - player.getLoginTime() > 10000L) {
					Locale lastLocale = player.getLastLocale();
					
					if (lastLocale != null) {
						if (!player.getLocale().equals(lastLocale)) {
							player.setLastLocale(player.getLocale());
							((FabricEventManager) EventManager.getInstance()).onPlayerLocaleChange(player, lastLocale.getLanguage() + "_" + lastLocale.getCountry());
						}
					} else player.setLastLocale(player.getLocale());
				}
			}
		}, 0L, 2000L);
		
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() {
		super.unload();
		TaskManager.cancelAsync(localeChangeTaskID);
	}
	
	@Override
	public void loadOnlinePlayers() {
		PlayerManager manager = FabricBootstrapper.getInstance().getServer().getPlayerManager();
		
		if (manager != null)
			for (ServerPlayerEntity player : manager.getPlayerList())
				if (getPlayer(player.getUuid()) == null && isWorldEnabled(((ServerWorldExtension) ((EntityExtension) player).chatPlugin$getWorld()).chatPlugin$getName()))
					loadPlayer(new PlayerAdapter(player));
	}
	
	@Override
	public int loadPlayer(PlayerAdapter player) {
		if (players.containsKey(player.getUUID()))
			return 0;
		long ms = System.currentTimeMillis();
		ChatPluginFabricPlayer serverPlayer = new ChatPluginFabricPlayer(player.fabricValue());
		
		if (ConfigurationType.CONFIG.get().getBoolean("settings.register-scoreboards")) {
			ServerScoreboard scoreboard = new ServerScoreboard(FabricBootstrapper.getInstance().getServer());
			ScoreboardObjective objective = null;
			
			((ServerScoreboardExtension) scoreboard).chatPlugin$setOwner(player.fabricValue());
			
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20_3)) {
				try {
					objective = (ScoreboardObjective) ServerScoreboard.class.getMethod("method_1168", String.class, ScoreboardCriterion.class, Text.class, RenderType.class).invoke(scoreboard, "chatplugin", ScoreboardCriterion.DUMMY, Utils.toFabricComponent("chatplugin"), RenderType.INTEGER);
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			} else objective = scoreboard.addObjective("chatplugin", ScoreboardCriterion.DUMMY, Utils.toFabricComponent("chatplugin"), RenderType.INTEGER, true, null);
			
			serverPlayer.setObjective(new ObjectiveAdapter(objective));
			setScoreboard(serverPlayer, scoreboard);
			
			// custom suffix
			if (CustomSuffixManager.getInstance().isEnabled() && !serverPlayer.isBedrockPlayer()) {
				boolean hearts = CustomSuffixManager.getInstance().getRenderType() == RenderTypeAdapter.HEARTS;
				
				if (VersionUtils.getVersion().isOlderThan(Version.V1_20_3)) {
					try {
						objective = (ScoreboardObjective) ServerScoreboard.class.getMethod("method_1168", String.class, ScoreboardCriterion.class, Text.class, RenderType.class).invoke(scoreboard, "tablist_suffix", hearts ? ScoreboardCriterion.HEALTH : ScoreboardCriterion.DUMMY, Utils.toFabricComponent("tablist_suffix"), hearts ? RenderType.HEARTS : RenderType.INTEGER);
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				} else objective = scoreboard.addObjective("tablist_suffix", hearts ? ScoreboardCriterion.HEALTH : ScoreboardCriterion.DUMMY, Utils.toFabricComponent("tablist_suffix"), hearts ? RenderType.HEARTS : RenderType.INTEGER, true, null);
				
				if (VersionUtils.getVersion().isOlderThan(Version.V1_20_2)) {
					try {
						ServerScoreboard.class.getMethod("method_1158", int.class, ScoreboardObjective.class).invoke(scoreboard, 0, objective);
					} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
				} else scoreboard.setObjectiveSlot(ScoreboardDisplaySlot.LIST, objective);
				
				objective.setRenderType(CustomSuffixManager.getInstance().getRenderType().fabricValue());
			}
			
			// scoreboard
			for (int i = 0; i < 15; i++)
				scoreboard.addScoreHolderToTeam(Scoreboard.SCORES[i], scoreboard.addTeam("line_" + i));
			if (ScoreboardManager.getInstance().getScoreboard("default") != null)
				ScoreboardManager.getInstance().getScoreboard("default").addPlayer(serverPlayer);
			if (!serverPlayer.isPlayerStored() && ScoreboardManager.getInstance().getScoreboard("first-join-event") != null)
				ScoreboardManager.getInstance().getScoreboard("first-join-event").addPlayer(serverPlayer);
			else if (ScoreboardManager.getInstance().getScoreboard("join-event") != null)
				ScoreboardManager.getInstance().getScoreboard("join-event").addPlayer(serverPlayer);
		} else if (TablistManager.getInstance().isEnabled() && VersionUtils.getVersion().isAtLeast(Version.V1_21_2))
			((ServerPlayerEntityExtension) serverPlayer.toAdapter().fabricValue()).chatPlugin$setPlayerListOrder(getPlayerListOrder(serverPlayer));
		
		// ranks (tablist)
		if (TablistManager.getInstance().isEnabled())
			serverPlayer.updatePlayerListName();
		
		// channels
		if (ChatChannelsManager.getInstance().isEnabled()) {
			for (String id : ChatChannelsManager.getInstance().getDefaultListeningChannelsIDs()) {
				ChatChannel<?> channel = ChatChannelsManager.getInstance().getChannel(id, false);
				
				if (channel != null && channel.canAccess(serverPlayer))
					serverPlayer.joinChannel(channel);
			} serverPlayer.switchChannel(ChatChannelsManager.getInstance().getDefaultWritingChannel());
		}
		
		// quit
		if (QuitMessageManager.getInstance().isEnabled())
			new QuitPacketImpl(serverPlayer);
//		((VanishManagerImpl) VanishManager.getInstance()).update(serverPlayer, false);
		
		players.put(player.getUUID(), serverPlayer);
		new ServerPlayerLoadEvent(serverPlayer, (int) (ms = System.currentTimeMillis() - ms)).call();
		LogManager.log("Player {0} has been loaded in {1} ms.", 4, player.getName(), ms);
		return (int) ms;
	}
	
	/*
	 * creates a (non-unique) identifier to sort alphabetically based on the player's rank position and their name's first 3 letters
	 * we have to produce a 10-digit integer; an extra letter would require a 12-digit integer which is more than Integer#MAX_VALUE
	 * 
	 * uses the following ASCII codes:
	 * - 32 (space)
	 * - 48-57 (0-9)
	 * - 65-90 (A-Z)
	 * - 95 (_)
	 * - 97-122 (a-z)
	 */
	private int getPlayerListOrder(ChatPluginServerPlayer player) {
		String identifier = player.getRank().formatIdentifier(player).substring(0, 5);
		StringBuilder sb = new StringBuilder(identifier.substring(0, 2)); // 2 digits
		
		for (int i = 2; i < 5; i++) { // 3 letters
			char letter = identifier.charAt(i);
			
			sb.append(letter == ' ' ? "00" : String.valueOf(letter - 23)); // shift so we have 99 for z
		} return Integer.MAX_VALUE - Integer.valueOf(sb.toString());
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int unloadPlayer(UUID player) {
		if (!players.containsKey(player))
			return 0;
		long ms = System.currentTimeMillis();
		ChatPluginServerPlayer serverPlayer = players.get(player);
		
		new ServerPlayerUnloadEvent(serverPlayer).call();
		players.remove(player);
		
		if (ConfigurationType.CONFIG.get().getBoolean("settings.register-scoreboards")) {
			net.minecraft.scoreboard.Scoreboard scoreboard = ((ScoreboardObjectiveAccessor) serverPlayer.getObjective().fabricValue()).chatPlugin$getScoreboard();
			
			if (serverPlayer.getScoreboard() != null)
				serverPlayer.getScoreboard().removePlayer(serverPlayer);
			new ArrayList<>(scoreboard.getTeams()).forEach(scoreboard::removeTeam);
			new ArrayList<>(scoreboard.getObjectives()).forEach(scoreboard::removeObjective);
			setScoreboard(serverPlayer, FabricBootstrapper.getInstance().getServer().getScoreboard());
			players.values().forEach(other -> ((ScoreboardObjectiveAccessor) other.getObjective().fabricValue()).chatPlugin$getScoreboard().getTeams().stream().filter(team -> team.getName().equals(serverPlayer.getRank().formatIdentifier(serverPlayer))).forEach(scoreboard::removeTeam));
		} else if (TablistManager.getInstance().isEnabled()) {
			if (VersionUtils.getVersion().isAtLeast(Version.V1_21_2))
				((ServerPlayerEntityExtension) serverPlayer.toAdapter().fabricValue()).chatPlugin$setPlayerListOrder(0);
			((ChatPluginFabricPlayer) serverPlayer).setPlayerListName(null);
		} if (serverPlayer.getBossbar() != null)
			serverPlayer.getBossbar().unregister();
		if (GUIManager.getInstance().getOpenGUI(serverPlayer) != null)
			serverPlayer.closeInventory();
		if (TablistManager.getInstance().isEnabled())
			TablistManager.getInstance().sendTablist(Tablist.NULL_TABLIST, serverPlayer);
		IPLookupManager.getInstance().removeFromCache(serverPlayer.getIPAddress());
		StaffChatManager.getInstance().removePlayer(player);
//		((VanishManagerImpl) VanishManager.getInstance()).show(serverPlayer, false);
		serverPlayer.getChannels().forEach(serverPlayer::leaveChannel);
		
		Long taskID = BossbarManager.getInstance().getLoadingBossbarsTasks().remove(serverPlayer);
		
		if (taskID != null) {
			ScheduledFuture<?> task = TaskManager.getInstance().getAsyncTasks().get(taskID);
			
			if (task != null)
				((Runnable) task).run();
		} GUIManager.getInstance().getGUIs().stream().filter(PerPlayerGUI.class::isInstance).map(PerPlayerGUI.class::cast).forEach(gui -> gui.unload(true));
		QuitMessageManager.getInstance().getFakeQuits().remove(player);
		Utils.inventoryTitles.remove(player);
		verifyAndRun(() -> {
			try {
				StorageConnector.getInstance().setPlayerData(PlayersDataType.LAST_LOGOUT, serverPlayer.getID(), System.currentTimeMillis()); // this is called on every unload too, not just quits...
				StorageConnector.getInstance().setPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer.getID(), StorageConnector.getInstance().getPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer.getID()) + (System.currentTimeMillis() - serverPlayer.getLoginTime()));
			} catch (SQLException | IOException  e) {
				LogManager.log("{0} occurred while setting {1}'s last logout or time played in the storage: {2}", 2, e.getClass().getSimpleName(), serverPlayer.getName(), e.getLocalizedMessage());
			}
		});
		LogManager.log("Player {0} has been unloaded in {1} ms.", 4, serverPlayer.getName(), ms = System.currentTimeMillis() - ms);
		return (int) ms;
	}
	
	@SuppressWarnings("deprecation")
	private static void setScoreboard(ChatPluginServerPlayer player, ServerScoreboard scoreboard) {
		ServerScoreboard oldBoard = FabricBootstrapper.getInstance().getServer().getScoreboard();
		Set<ScoreboardObjective> removed = new HashSet<>();
		
		for (int i = 0; i < 3; i++) {
			ScoreboardObjective objective = null;
			
			if (VersionUtils.getVersion().isOlderThan(Version.V1_20_2)) {
				try {
					objective = (ScoreboardObjective) ServerScoreboard.class.getMethod("method_1189", int.class).invoke(scoreboard, i);
				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			} else objective = oldBoard.getObjectiveForSlot(ScoreboardDisplaySlot.FROM_ID.apply(i));
			
			if (objective != null && !removed.contains(objective)) {
				player.sendPacket(new ScoreboardObjectiveUpdateS2CPacket(objective, 1));
				removed.add(objective);
			}
		} for (Team team : oldBoard.getTeams()) {
			TeamS2CPacket packet = null;
			
			if (VersionUtils.getVersion().isOlderThan(Version.V1_17)) {
				try {
					packet = TeamS2CPacket.class.getConstructor(Team.class, int.class).newInstance(team, 1);
				} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			} else packet = TeamS2CPacket.updateRemovedTeam(team);
			player.sendPacket(packet);
		} try {
			Method sendScoreboard = PlayerManager.class.getDeclaredMethod("method_14588", ServerScoreboard.class, ServerPlayerEntity.class);
			
			sendScoreboard.setAccessible(true);
			sendScoreboard.invoke(FabricBootstrapper.getInstance().getServer().getPlayerManager(), scoreboard, player.toAdapter().fabricValue());
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
}
