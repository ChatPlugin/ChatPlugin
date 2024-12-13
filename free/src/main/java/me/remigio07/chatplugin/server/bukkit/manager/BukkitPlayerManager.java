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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.viaversion.viaversion.api.Via;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
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
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.tablist.Tablist;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.CustomSuffixManager;
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.RenderType;
import me.remigio07.chatplugin.api.server.util.adapter.scoreboard.ObjectiveAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import me.remigio07.chatplugin.server.bukkit.ChatPluginBukkitPlayer;
import me.remigio07.chatplugin.server.join_quit.QuitMessageManagerImpl.QuitPacketImpl;
import me.remigio07.chatplugin.server.util.Utils;
import me.remigio07.chatplugin.server.util.manager.VanishManagerImpl;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;

public class BukkitPlayerManager extends ServerPlayerManager {
	
	private static BukkitAudiences audiences;
	private long localeChangeTaskID;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		super.load();
		
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
		} if (audiences == null)
			TaskManager.runSync(() -> audiences = BukkitAudiences.create(BukkitBootstrapper.getInstance()), 0L);
		enabled = true;
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
				if (playersVersions.isEmpty()) {
					String message = null;
					
					if (file.exists())
						try {
							Configuration onlinePlayersData = new Configuration(file);
							
							onlinePlayersData.load();
							
							for (String player : onlinePlayersData.getKeys()) {
								UUID uuid = UUID.fromString(player);
								
								playersVersions.put(uuid, Version.getVersion(onlinePlayersData.getString(player + ".version")));
								playersLoginTimes.put(uuid, onlinePlayersData.getLong(player + ".login-time"));
								
								if (onlinePlayersData.getBoolean(player + ".bedrock"))
									bedrockPlayers.add(uuid);
							}
						} catch (IOException ioe) {
							message = ioe.getLocalizedMessage();
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
		Scoreboard scoreboard = getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("scoreboard", "dummy");
		boolean atLeastV1_9 = VersionUtils.getVersion().isAtLeast(Version.V1_9);
		
		// custom suffix
		if (CustomSuffixManager.getInstance().isEnabled()) {
			Objective customSuffix = scoreboard.registerNewObjective("tablist_suffix", CustomSuffixManager.getInstance().getRenderType() == RenderType.HEARTS ? "health" : "dummy");
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_13_2))
				customSuffix.setRenderType(CustomSuffixManager.getInstance().getRenderType().bukkitValue());
			customSuffix.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		}
		
		// scoreboard
		for (int i = 0; i < 15; i++)
			if (atLeastV1_9)
				scoreboard.registerNewTeam("line_" + i).addEntry(me.remigio07.chatplugin.api.server.scoreboard.Scoreboard.SCORES[i]);
			else BukkitReflection.invokeMethod("Scoreboard", "addPlayerToTeam", BukkitReflection.invokeMethod("CraftScoreboard", "getHandle", scoreboard), me.remigio07.chatplugin.api.server.scoreboard.Scoreboard.SCORES[i], scoreboard.registerNewTeam("line_" + i).getName());
		bukkitValue.setScoreboard(scoreboard);
		serverPlayer.setObjective(new ObjectiveAdapter(objective));
		
		if (ScoreboardManager.getInstance().getScoreboard("default") != null)
			ScoreboardManager.getInstance().getScoreboard("default").addPlayer(serverPlayer);
		if (!serverPlayer.isPlayerStored() && ScoreboardManager.getInstance().getScoreboard("first-join-event") != null)
			ScoreboardManager.getInstance().getScoreboard("first-join-event").addPlayer(serverPlayer);
		else if (ScoreboardManager.getInstance().getScoreboard("join-event") != null)
			ScoreboardManager.getInstance().getScoreboard("join-event").addPlayer(serverPlayer);
		
		// ranks
		Boolean longTeams = TablistManager.getInstance().isEnabled() ? VersionUtils.getVersion().isAtLeast(Version.V1_13) && serverPlayer.getVersion().isAtLeast(Version.V1_13) : null;
		
		if (longTeams != null) {
			for (ChatPluginServerPlayer other : players.values()) {
				setupTeams(serverPlayer, other, longTeams);
				setupTeams(other, serverPlayer, longTeams);
			} setupTeams(serverPlayer, serverPlayer, longTeams);
		}
		
		// misc
		if (QuitMessageManager.getInstance().isEnabled())
			new QuitPacketImpl(serverPlayer);
		((VanishManagerImpl) VanishManager.getInstance()).update(serverPlayer, false);
		players.put(player.getUUID(), serverPlayer);
		new ServerPlayerLoadEvent(serverPlayer, (int) (ms = System.currentTimeMillis() - ms)).call();
		LogManager.log("Player {0} has been loaded in {1} ms.", 4, player.getName(), ms);
		return (int) ms;
	}
	
	private static Scoreboard getNewScoreboard() {
		if (Bukkit.isPrimaryThread())
			return Bukkit.getScoreboardManager().getNewScoreboard();
		CompletableFuture<Scoreboard> future = new CompletableFuture<>();
		
		TaskManager.runSync(() -> future.complete(getNewScoreboard()), 0L);
		return future.join();
	}
	
	@SuppressWarnings("deprecation")
	private void setupTeams(ChatPluginServerPlayer player, ChatPluginServerPlayer other, boolean longTeams) {
		if (!other.getRank().getTag().toString().isEmpty()) {
			Team team = player.getObjective().bukkitValue().getScoreboard().registerNewTeam(other.getRank().formatIdentifier(other));
			String prefix = PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPrefixFormat(), other, player.getLanguage(), TablistManager.getInstance().getPlaceholderTypes());
			String suffix = PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getSuffixFormat(), other, player.getLanguage(), TablistManager.getInstance().getPlaceholderTypes());
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_12)) {
				String lastColors = ChatColor.getLastColors(prefix);
				
				if (!lastColors.isEmpty())
					team.setColor((lastColors.startsWith("\u00A7x") ? ChatColor.of(lastColors.substring(3, 14).replace("\u00A7", "")).getClosestDefaultColor() : ChatColor.getByChar(lastColors.charAt(1))).bukkitValue());
			} team.setPrefix(prefix.length() > (longTeams ? 64 : 16) ? me.remigio07.chatplugin.common.util.Utils.abbreviate(prefix, longTeams ? 64 : 16, false) : prefix);
			team.setSuffix(suffix.length() > (longTeams ? 64 : 16) ? me.remigio07.chatplugin.common.util.Utils.abbreviate(suffix, longTeams ? 64 : 16, false) : suffix);
			team.addPlayer(other.toAdapter().bukkitValue());
		}
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
		IPLookupManager.getInstance().removeFromCache(serverPlayer.getIPAddress());
		StaffChatManager.getInstance().removePlayer(player);
		TablistManager.getInstance().sendTablist(Tablist.NULL_TABLIST, serverPlayer);
		((VanishManagerImpl) VanishManager.getInstance()).show(serverPlayer, false);
		
		Long taskID = BossbarManager.getInstance().getLoadingBossbarsTasks().remove(serverPlayer);
		
		if (taskID != null) {
			ScheduledFuture<?> task = TaskManager.getInstance().getAsyncTasks().get(taskID);
			
			if (task != null)
				((Runnable) task).run();
		} scoreboard.getTeams().forEach(Team::unregister);
		scoreboard.getObjectives().forEach(Objective::unregister);
		players.values().forEach(other -> other.getObjective().bukkitValue().getScoreboard().getTeams().stream().filter(team -> team.getName().equals(serverPlayer.getRank().formatIdentifier(serverPlayer))).forEach(Team::unregister));
		GUIManager.getInstance().getGUIs().stream().filter(PerPlayerGUI.class::isInstance).map(PerPlayerGUI.class::cast).forEach(gui -> gui.unload(true));
		QuitMessageManager.getInstance().getFakeQuits().remove(player);
		Utils.inventoryTitles.remove(player);
		checkState(() -> {
			try {
				StorageConnector.getInstance().setPlayerData(PlayersDataType.LAST_LOGOUT, serverPlayer, System.currentTimeMillis()); // this is called on every unload too, not just quits...
				StorageConnector.getInstance().setPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer, StorageConnector.getInstance().getPlayerData(PlayersDataType.TIME_PLAYED, serverPlayer) + (System.currentTimeMillis() - serverPlayer.getLoginTime()));
			} catch (SQLException | IOException  e) {
				LogManager.log("{0} occurred while setting {1}'s last logout or time played in the storage: {2}", 2, e.getClass().getSimpleName(), serverPlayer.getName(), e.getLocalizedMessage());
			}
		});
		LogManager.log("Player {0} has been unloaded in {1} ms.", 4, serverPlayer.getName(), ms = System.currentTimeMillis() - ms);
		return (int) ms;
	}
	
	public static BukkitAudiences getAudiences() {
		return audiences;
	}
	
}
