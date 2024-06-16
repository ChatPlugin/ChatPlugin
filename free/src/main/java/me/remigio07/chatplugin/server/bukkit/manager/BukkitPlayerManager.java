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

package me.remigio07.chatplugin.server.bukkit.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.viaversion.viaversion.api.Via;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
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
import me.remigio07.chatplugin.api.server.rank.RankTag;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.tablist.Tablist;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.CustomSuffixManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.RenderType;
import me.remigio07.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import me.remigio07.chatplugin.server.bukkit.ChatPluginBukkitPlayer;
import me.remigio07.chatplugin.server.join_quit.QuitMessageManagerImpl.QuitPacketImpl;
import me.remigio07.chatplugin.server.util.manager.VanishManagerImpl;

public class BukkitPlayerManager extends ServerPlayerManager {
	
	private long localeChangeTaskID;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!(everyWorldEnabled = ConfigurationType.CONFIG.get().getBoolean("settings.enable-every-world"))) {
			for (String world : ConfigurationType.CONFIG.get().getStringList("settings.enabled-worlds"))
				if (Bukkit.getWorld(world) == null)
					LogManager.log("World \"{0}\" specified at \"settings.enabled-worlds\" in config.yml does not exist (yet); skipping it.", 2, world);
				else enabledWorlds.add(world);
		} super.load();
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_12)) {
			localeChangeTaskID = TaskManager.scheduleAsync(() -> {
				for (ChatPluginServerPlayer serverPlayer : new ArrayList<>(players.values())) {
					ChatPluginBukkitPlayer player = (ChatPluginBukkitPlayer) serverPlayer;
					
					if (System.currentTimeMillis() - player.getLoginTime() > 10000L) {
						Locale lastLocale = player.getLastLocale();
						
						if (lastLocale != null) {
							if (!player.getLocale().equals(lastLocale)) {
								player.setLastLocale(player.getLocale());
								((BukkitEventManager) EventManager.getInstance()).onPlayerLocaleChange(player, lastLocale.getLanguage() + "_" + lastLocale.getCountry());
							}
						} else player.setLastLocale(player.getLocale());
					}
				}
			}, 0L, 2000L);
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		super.unload();
		TaskManager.cancelAsync(localeChangeTaskID);
	}
	
	@Override
	public void loadOnlinePlayers() {
		List<PlayerAdapter> players = PlayerAdapter.getOnlinePlayers();
		File file = new File(ChatPlugin.getInstance().getDataFolder(), "online-players-data.yml");
		
		if (players.size() != 0) {
			if (IntegrationType.VIAVERSION.isEnabled() && IntegrationType.VIAVERSION.get().getVersion(players.get(0)) == Version.UNSUPPORTED) {
				String reason = ChatColor.translate(Via.getConfig().getReloadDisconnectMsg());
				
				LogManager.log("Reload detected. This operation is not fully supported by ViaVersion and all players must be kicked in order to use its API.", 1);
				players.forEach(player -> player.disconnect(reason));
			} else {
				if (ServerPlayerManager.getPlayersVersions().isEmpty()) {
					String message = null;
					
					if (file.exists())
						try {
							Configuration onlinePlayersData = new Configuration(file);
							
							onlinePlayersData.load();
							
							for (String player : onlinePlayersData.getKeys()) {
								UUID uuid = UUID.fromString(player);
								
								ServerPlayerManager.getPlayersVersions().put(uuid, Version.getVersion(onlinePlayersData.getString(player + ".version")));
								ServerPlayerManager.getPlayersLoginTimes().put(uuid, onlinePlayersData.getLong(player + ".login-time"));
								
								if (onlinePlayersData.getBoolean(player + ".bedrock"))
									ServerPlayerManager.getBedrockPlayers().add(uuid);
							}
						} catch (IOException e) {
							message = e.getMessage();
						}
					else message = file.getPath() + " file does not exist";
					
					if (message != null) {
						LogManager.log("Error occurred while reading online players' data after a server reload: {0}; kicking all players...", 2, message);
						Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(null));
					}
				} for (Player player : Bukkit.getOnlinePlayers())
					if (getPlayer(player.getUniqueId()) == null && isWorldEnabled(player.getWorld().getName()))
						loadPlayer(new PlayerAdapter(player));
			}
		} file.delete();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public int loadPlayer(PlayerAdapter player) {
		if (players.containsKey(player.getUUID()))
			return 0;
		long ms = System.currentTimeMillis();
		Player bukkitValue = player.bukkitValue();
		ChatPluginBukkitPlayer serverPlayer = new ChatPluginBukkitPlayer(bukkitValue);
		// teams start
		org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");
		boolean atLeastV1_9 = VersionUtils.getVersion().isAtLeast(Version.V1_9);
		
		if (CustomSuffixManager.getInstance().isEnabled()) {
			Objective customSuffix = scoreboard.registerNewObjective("tablist_suffix", CustomSuffixManager.getInstance().getRenderType() == RenderType.HEARTS ? "health" : "dummy");
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_13_2))
				customSuffix.setRenderType(CustomSuffixManager.getInstance().getRenderType().bukkitValue());
			customSuffix.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		} for (int i = 0; i < 15; i++)
			if (atLeastV1_9)
				scoreboard.registerNewTeam("line_" + i).addEntry(Scoreboard.SCORES[i]);
			else BukkitReflection.invokeMethod("Scoreboard", "addPlayerToTeam", BukkitReflection.invokeMethod("CraftScoreboard", "getHandle", scoreboard), Scoreboard.SCORES[i], scoreboard.registerNewTeam("line_" + i).getName());
		bukkitValue.setScoreboard(scoreboard);
		serverPlayer.setObjective(new ObjectiveAdapter(objective));
		
		for (Rank rank : RankManager.getInstance().getRanks()) {
			Team team = scoreboard.registerNewTeam(rank.getTeamName());
			
			if (rank.getTag().toString().isEmpty())
				continue;
			RankTag tag = rank.getTag();
			
			team.setPrefix(ChatColor.translate(tag.getPrefix()));
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_12))
				team.setColor(ChatColor.getByChar(tag.getNameColor().charAt(1)).bukkitValue());
			else team.setPrefix(ChatColor.translate(team.getPrefix() + tag.getNameColor()));
			team.setSuffix(ChatColor.translate(tag.getSuffix()));
		} for (ChatPluginServerPlayer other : getPlayers().values()) {
			scoreboard.getTeam(other.getRank().getTeamName()).addPlayer(other.toAdapter().bukkitValue());
			other.getObjective().bukkitValue().getScoreboard().getTeam(serverPlayer.getRank().getTeamName()).addPlayer(bukkitValue);
		} if (!getPlayers().containsKey(player.getUUID())) {
			scoreboard.getTeam(serverPlayer.getRank().getTeamName()).addPlayer(bukkitValue);
			serverPlayer.getObjective().bukkitValue().getScoreboard().getTeam(serverPlayer.getRank().getTeamName()).addPlayer(bukkitValue);
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
		org.bukkit.scoreboard.Scoreboard scoreboard = serverPlayer.getObjective().bukkitValue().getScoreboard();
		
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
		
		if (scoreboard.getObjective("scoreboard") != null)
			scoreboard.getObjective("scoreboard").unregister();
		if (scoreboard.getObjective("tablist_suffix") != null)
			scoreboard.getObjective("tablist_suffix").unregister();
		new ArrayList<>(GUIManager.getInstance().getGUIs()).stream().filter(PerPlayerGUI.class::isInstance).map(PerPlayerGUI.class::cast).forEach(PerPlayerGUI::unload);
		QuitMessageManager.getInstance().getFakeQuits().remove(player);
		
		try {
			StorageConnector.getInstance().setPlayerData(PlayersDataType.LAST_LOGOUT, serverPlayer, System.currentTimeMillis());
			StorageConnector.getInstance().setPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer, StorageConnector.getInstance().getPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer) + (System.currentTimeMillis() - serverPlayer.getLoginTime()));
		} catch (Exception e) {
			LogManager.log("{0} occurred while setting {1}'s last logout or time played in the storage: {2}", 2, e.getClass().getSimpleName(), serverPlayer.getName(), e.getMessage());
		} LogManager.log("Player {0} has been unloaded in {1} ms.", 4, serverPlayer.getName(), ms = System.currentTimeMillis() - ms);
		return (int) ms;
	}
	
}
