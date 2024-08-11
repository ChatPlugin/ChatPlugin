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

package me.remigio07.chatplugin.server.sponge.manager;

import java.util.ArrayList;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
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
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07.chatplugin.api.server.event.player.ServerPlayerLoadEvent;
import me.remigio07.chatplugin.api.server.event.player.ServerPlayerUnloadEvent;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.PerPlayerGUI;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.tablist.Tablist;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.CustomSuffixManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.RenderType;
import me.remigio07.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.server.join_quit.QuitMessageManagerImpl.QuitPacketImpl;
import me.remigio07.chatplugin.server.sponge.ChatPluginSpongePlayer;
import me.remigio07.chatplugin.server.util.Utils;
import me.remigio07.chatplugin.server.util.manager.VanishManagerImpl;

public class SpongePlayerManager extends ServerPlayerManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!(everyWorldEnabled = ConfigurationType.CONFIG.get().getBoolean("settings.enable-every-world"))) {
			for (String world : ConfigurationType.CONFIG.get().getStringList("settings.enabled-worlds"))
				if (!Sponge.getServer().getWorld(world).isPresent())
					LogManager.log("World \"{0}\" specified at \"settings.enabled-worlds\" in config.yml does not exist (yet); skipping it.", 2, world);
				else enabledWorlds.add(world);
		} super.load();
		
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
		// teams start
		org.spongepowered.api.scoreboard.Scoreboard scoreboard = org.spongepowered.api.scoreboard.Scoreboard.builder().build();
		
		scoreboard.addObjective(Objective.builder().name("scoreboard").criterion(Criteria.DUMMY).build());
		
		Objective objective = scoreboard.getObjective("scoreboard").get();
		
		if (CustomSuffixManager.getInstance().isEnabled()) {
			scoreboard.addObjective(Objective.builder().name("tablist_suffix").criterion(CustomSuffixManager.getInstance().getRenderType() == RenderType.HEARTS ? Criteria.HEALTH : Criteria.DUMMY).build());
			
			Objective customSuffix = scoreboard.getObjective("tablist_suffix").get();
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_9))
				customSuffix.setDisplayMode(CustomSuffixManager.getInstance().getRenderType().spongeValue());
			scoreboard.updateDisplaySlot(customSuffix, DisplaySlots.LIST);
		} for (int i = 0; i < 15; i++) {
			Team team = Team.builder().name("line_" + i).build();
			
			scoreboard.registerTeam(team);
			team.addMember(Utils.serializeSpongeText(Scoreboard.SCORES[i], false));
		} player.spongeValue().setScoreboard(scoreboard);
		serverPlayer.setObjective(new ObjectiveAdapter(objective));
		
		for (Rank rank : RankManager.getInstance().getRanks()) {
			scoreboard.registerTeam(Team.builder().name(rank.getTeamName()).build());
			
			if (!TablistManager.getInstance().isEnabled() ||  rank.getTag().toString().isEmpty())
				continue;
			Team team = scoreboard.getTeam(rank.getTeamName()).get();
			String prefix = PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPrefixFormat(), serverPlayer, TablistManager.getInstance().getPlaceholderTypes());
			
			if (prefix.contains(" ")) {
				int index = prefix.lastIndexOf(' ');
				
				if (index != prefix.length() - 1) {
					String str = prefix.substring(index + 1);
					
					if (ChatColor.stripColor(ChatColor.translate(str)).isEmpty())
						prefix = prefix.substring(0, index) + str + " ";
				}
			} team.setPrefix(Utils.serializeSpongeText(prefix, false));
			team.setSuffix(Utils.serializeSpongeText(PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getSuffixFormat(), serverPlayer, TablistManager.getInstance().getPlaceholderTypes()), false));
		} for (ChatPluginServerPlayer other : getPlayers().values()) {
			scoreboard.getTeam(other.getRank().getTeamName()).get().addMember(Utils.serializeSpongeText(other.getName(), false));
			Iterables.getFirst(other.getObjective().spongeValue().getScoreboards(), null).getTeam(serverPlayer.getRank().getTeamName()).get().addMember(Utils.serializeSpongeText(player.getName(), false));
		} if (!getPlayers().containsKey(player.getUUID())) {
			scoreboard.getTeam(serverPlayer.getRank().getTeamName()).get().addMember(Utils.serializeSpongeText(player.getName(), false));
			Iterables.getFirst(serverPlayer.getObjective().spongeValue().getScoreboards(), null).getTeam(serverPlayer.getRank().getTeamName()).get().addMember(Utils.serializeSpongeText(player.getName(), false));
		} // teams end
		
		if (QuitMessageManager.getInstance().isEnabled())
			new QuitPacketImpl(serverPlayer);
		if (VanishManager.getInstance().isEnabled())
			VanishManager.getInstance().update(serverPlayer, false);
		if (ScoreboardManager.getInstance().getScoreboard("default") != null)
			ScoreboardManager.getInstance().getScoreboard("default").addPlayer(serverPlayer);
		if (!serverPlayer.isPlayerStored() && ScoreboardManager.getInstance().getScoreboard("first-join-event") != null)
			ScoreboardManager.getInstance().getScoreboard("first-join-event").addPlayer(serverPlayer);
		else if (ScoreboardManager.getInstance().getScoreboard("join-event") != null)
			ScoreboardManager.getInstance().getScoreboard("join-event").addPlayer(serverPlayer);
		players.put(player.getUUID(), serverPlayer);
		new ServerPlayerLoadEvent(serverPlayer, (int) ms).call();
		LogManager.log("Player {0} has been loaded in {1} ms.", 4, player.getName(), ms = System.currentTimeMillis() - ms);
		return (int) ms;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int unloadPlayer(UUID player) {
		if (!players.containsKey(player))
			return 0;
		long ms = System.currentTimeMillis();
		ChatPluginServerPlayer serverPlayer = players.get(player);
		org.spongepowered.api.scoreboard.Scoreboard scoreboard = Iterables.getFirst(serverPlayer.getObjective().spongeValue().getScoreboards(), null);
		
		new ServerPlayerUnloadEvent(serverPlayer).call();
		players.remove(player);
		
		if (serverPlayer.getScoreboard() != null)
			serverPlayer.getScoreboard().removePlayer(serverPlayer);
		if (serverPlayer.getBossbar() != null)
			serverPlayer.getBossbar().unregister();
		if (GUIManager.getInstance().getOpenGUI(serverPlayer) != null)
			serverPlayer.closeInventory();
		if (IPLookupManager.getInstance().isEnabled())
			IPLookupManager.getInstance().removeFromCache(serverPlayer.getIPAddress());
		if (VanishManager.getInstance().isEnabled())
			((VanishManagerImpl) VanishManager.getInstance()).show(serverPlayer, false);
		if (BossbarManager.getInstance().getLoadingBossbarsTasks().containsKey(serverPlayer))
			TaskManager.getInstance().getAsyncTasks().get(BossbarManager.getInstance().getLoadingBossbarsTasks().remove(serverPlayer)).run();
		if (StaffChatManager.getInstance().isEnabled())
			StaffChatManager.getInstance().removePlayer(player);
		TablistManager.getInstance().sendTablist(Tablist.NULL_TABLIST, serverPlayer);
		scoreboard.getScores().forEach(score -> scoreboard.removeScores(score.getName()));
		new ArrayList<>(GUIManager.getInstance().getGUIs()).stream().filter(PerPlayerGUI.class::isInstance).map(PerPlayerGUI.class::cast).forEach(PerPlayerGUI::unload);
		QuitMessageManager.getInstance().getFakeQuits().remove(player);
		Utils.inventoryTitles.remove(player);
		
		try {
			StorageConnector.getInstance().setPlayerData(PlayersDataType.LAST_LOGOUT, serverPlayer, System.currentTimeMillis());
			StorageConnector.getInstance().setPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer, StorageConnector.getInstance().getPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer) + (System.currentTimeMillis() - serverPlayer.getLoginTime()));
		} catch (Exception e) {
			LogManager.log("{0} occurred while setting {1}'s last logout or time played in the storage: {2}", 2, e.getClass().getSimpleName(), serverPlayer.getName(), e.getMessage());
		} LogManager.log("Player {0} has been unloaded in {1} ms.", 4, serverPlayer.getName(), ms = System.currentTimeMillis() - ms);
		return (int) ms;
	}
	
}
