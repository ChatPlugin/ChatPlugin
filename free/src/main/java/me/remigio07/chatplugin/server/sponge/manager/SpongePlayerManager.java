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

package me.remigio07.chatplugin.server.sponge.manager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;

import com.google.common.collect.Iterables;

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
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.tablist.Tablist;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.CustomSuffixManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.RenderType;
import me.remigio07.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.server.join_quit.QuitMessageManagerImpl.QuitPacketImpl;
import me.remigio07.chatplugin.server.sponge.ChatPluginSpongePlayer;
import me.remigio07.chatplugin.server.util.Utils;
import me.remigio07.chatplugin.server.util.manager.VanishManagerImpl;
import net.kyori.adventure.platform.spongeapi.SpongeAudiences;

public class SpongePlayerManager extends ServerPlayerManager {
	
	private static SpongeAudiences audiences;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		super.load();
		
		if (audiences == null)
			TaskManager.runSync(() -> audiences = SpongeAudiences.create(Sponge.getPluginManager().getPlugin("chatplugin").get(), Sponge.getGame()), 0L);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void loadOnlinePlayers() {
		for (Player player : Sponge.getServer().getOnlinePlayers())
			if (getPlayer(player.getUniqueId()) == null && isWorldEnabled(player.getWorld().getName()))
				loadPlayer(new PlayerAdapter(player));
	}
	
	@Override
	public int loadPlayer(PlayerAdapter player) {
		if (players.containsKey(player.getUUID()))
			return 0;
		long ms = System.currentTimeMillis();
		ChatPluginSpongePlayer serverPlayer = new ChatPluginSpongePlayer(player.spongeValue());
		
		if (ConfigurationType.CONFIG.get().getBoolean("settings.register-scoreboards")) {
			Scoreboard scoreboard = Scoreboard.builder().build();
			
			scoreboard.addObjective(Objective.builder().name("chatplugin").criterion(Criteria.DUMMY).build());
			serverPlayer.setObjective(new ObjectiveAdapter(scoreboard.getObjective("chatplugin").get()));
			player.spongeValue().setScoreboard(scoreboard);
			
			// custom suffix
			if (CustomSuffixManager.getInstance().isEnabled() && !serverPlayer.isBedrockPlayer()) {
				scoreboard.addObjective(Objective.builder().name("tablist_suffix").criterion(CustomSuffixManager.getInstance().getRenderType() == RenderType.HEARTS ? Criteria.HEALTH : Criteria.DUMMY).build());
				
				Objective customSuffix = scoreboard.getObjective("tablist_suffix").get();
				
				if (VersionUtils.getVersion().isAtLeast(Version.V1_9))
					customSuffix.setDisplayMode(CustomSuffixManager.getInstance().getRenderType().spongeValue());
				scoreboard.updateDisplaySlot(customSuffix, DisplaySlots.LIST);
			}
			
			// scoreboard
			for (int i = 0; i < 15; i++) {
				Team team = Team.builder().name("line_" + i).build();
				
				scoreboard.registerTeam(team);
				team.addMember(Utils.serializeSpongeText(me.remigio07.chatplugin.api.server.scoreboard.Scoreboard.SCORES[i], false));
			} if (ScoreboardManager.getInstance().getScoreboard("default") != null)
				ScoreboardManager.getInstance().getScoreboard("default").addPlayer(serverPlayer);
			if (!serverPlayer.isPlayerStored() && ScoreboardManager.getInstance().getScoreboard("first-join-event") != null)
				ScoreboardManager.getInstance().getScoreboard("first-join-event").addPlayer(serverPlayer);
			else if (ScoreboardManager.getInstance().getScoreboard("join-event") != null)
				ScoreboardManager.getInstance().getScoreboard("join-event").addPlayer(serverPlayer);
		}
		
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
		((VanishManagerImpl) VanishManager.getInstance()).update(serverPlayer, false);
		
		players.put(player.getUUID(), serverPlayer);
		new ServerPlayerLoadEvent(serverPlayer, (int) (ms = System.currentTimeMillis() - ms)).call();
		LogManager.log("Player {0} has been loaded in {1} ms.", 4, player.getName(), ms);
		return (int) ms;
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
			Scoreboard scoreboard = Iterables.getFirst(serverPlayer.getObjective().spongeValue().getScoreboards(), null);
			
			if (serverPlayer.getScoreboard() != null)
				serverPlayer.getScoreboard().removePlayer(serverPlayer);
			scoreboard.getScores().forEach(score -> scoreboard.removeScores(score.getName()));
			scoreboard.getTeams().forEach(Team::unregister);
			scoreboard.getObjectives().forEach(scoreboard::removeObjective);
			serverPlayer.toAdapter().spongeValue().setScoreboard(Sponge.getServer().getServerScoreboard().get());
			players.values().forEach(other -> Iterables.getFirst(other.getObjective().spongeValue().getScoreboards(), null).getTeam(serverPlayer.getRank().formatIdentifier(serverPlayer)).ifPresent(Team::unregister));
		} else if (TablistManager.getInstance().isEnabled()) {
			((ChatPluginSpongePlayer) serverPlayer).setPlayerListName(serverPlayer, null);
			players.values().forEach(other -> ((ChatPluginSpongePlayer) other).setPlayerListName(serverPlayer, null));
		} if (serverPlayer.getBossbar() != null)
			serverPlayer.getBossbar().unregister();
		if (GUIManager.getInstance().getOpenGUI(serverPlayer) != null)
			serverPlayer.closeInventory();
		IPLookupManager.getInstance().removeFromCache(serverPlayer.getIPAddress());
		StaffChatManager.getInstance().removePlayer(player);
		TablistManager.getInstance().sendTablist(Tablist.NULL_TABLIST, serverPlayer);
		((VanishManagerImpl) VanishManager.getInstance()).show(serverPlayer, false);
		serverPlayer.getChannels().forEach(serverPlayer::leaveChannel);
		
		Long taskID = BossbarManager.getInstance().getLoadingBossbarsTasks().remove(serverPlayer);
		
		if (taskID != null) {
			ScheduledFuture<?> task = TaskManager.getInstance().getAsyncTasks().get(taskID);
			
			if (task != null)
				((Runnable) task).run();
		} GUIManager.getInstance().getGUIs().stream().filter(PerPlayerGUI.class::isInstance).map(PerPlayerGUI.class::cast).forEach(gui -> gui.unload(true));
		QuitMessageManager.getInstance().getFakeQuits().remove(player);
		Utils.inventoryTitles.remove(player);
		checkState(() -> {
			try {
				StorageConnector.getInstance().setPlayerData(PlayersDataType.LAST_LOGOUT, serverPlayer, System.currentTimeMillis()); // this is called on every unload too, not just quits...
				StorageConnector.getInstance().setPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer, StorageConnector.getInstance().getPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer) + (System.currentTimeMillis() - serverPlayer.getLoginTime()));
			} catch (SQLException | IOException e) {
				LogManager.log("{0} occurred while setting {1}'s last logout or time played in the storage: {2}", 2, e.getClass().getSimpleName(), serverPlayer.getName(), e.getLocalizedMessage());
			}
		});
		LogManager.log("Player {0} has been unloaded in {1} ms.", 4, serverPlayer.getName(), ms = System.currentTimeMillis() - ms);
		return (int) ms;
	}
	
	public static SpongeAudiences getAudiences() {
		return audiences;
	}
	
}
