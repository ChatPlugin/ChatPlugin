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

package me.remigio07.chatplugin.server.util.manager;

import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.Statistic;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.statistic.Statistics;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.discord.DiscordIntegrationManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.punishment.ban.BanManager;
import me.remigio07.chatplugin.api.common.punishment.kick.KickManager;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition.WhereOperator;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.DateFormat;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.PingManager;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager.TPSTimeInterval;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;

public class PlaceholderManagerImpl extends PlaceholderManager {
	
	private static StorageConnector storage;
	private static Runtime runtime = Runtime.getRuntime();
	private boolean refreshAnticheatCounters = false;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		storage = StorageConnector.getInstance();
		timerTaskID = TaskManager.scheduleAsync(this, 0L, Utils.getTime(ConfigurationType.CONFIG.get().getString("settings.storage-placeholders-update-timeout"), false, false));
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = refreshAnticheatCounters = false;
		
		TaskManager.cancelAsync(timerTaskID);
		
		timerTaskID = -1;
	}
	
	@Override
	public void run() {
		if (!enabled)
			return;
		try {
			ServerPlayerManager.getInstance().setStorageCount(storage.count(DataContainer.PLAYERS).intValue());
			
			if (ChatPlugin.getInstance().isPremium()) {
				if (BanManager.getInstance().isEnabled()) {				
					BanManager.getInstance().setStorageCount(storage.count(DataContainer.BANS).intValue());
					BanManager.getInstance().setStaffStorageCount(getStaffPunishments(DataContainer.BANS));
				} if (WarningManager.getInstance().isEnabled()) {
					WarningManager.getInstance().setStorageCount(storage.count(DataContainer.WARNINGS).intValue());
					WarningManager.getInstance().setStaffStorageCount(getStaffPunishments(DataContainer.WARNINGS));
				} if (KickManager.getInstance().isEnabled()) {
					KickManager.getInstance().setStorageCount(storage.count(DataContainer.KICKS).intValue());
					KickManager.getInstance().setStaffStorageCount(getStaffPunishments(DataContainer.KICKS));
				} if (MuteManager.getInstance().isEnabled()) {
					MuteManager.getInstance().setStorageCount(storage.count(DataContainer.MUTES).intValue());
					MuteManager.getInstance().setStaffStorageCount(getStaffPunishments(DataContainer.MUTES));
				}
			} for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values()) {
				((BaseChatPluginServerPlayer) player).setMessagesSent(storage.getPlayerData(PlayersDataType.MESSAGES_SENT, player));
				
				if (ChatPlugin.getInstance().isPremium()) {
					((BaseChatPluginServerPlayer) player).setBans(getStaffPunishments(player, DataContainer.BANS));
					((BaseChatPluginServerPlayer) player).setWarnings(getStaffPunishments(player, DataContainer.WARNINGS));
					((BaseChatPluginServerPlayer) player).setKicks(getStaffPunishments(player, DataContainer.KICKS));
					((BaseChatPluginServerPlayer) player).setMutes(getStaffPunishments(player, DataContainer.MUTES));
					
					if (refreshAnticheatCounters) {
						((BaseChatPluginServerPlayer) player).setBans(getAnticheatPunishments(player, DataContainer.BANS));
						((BaseChatPluginServerPlayer) player).setWarnings(getAnticheatPunishments(player, DataContainer.WARNINGS));
						((BaseChatPluginServerPlayer) player).setKicks(getAnticheatPunishments(player, DataContainer.KICKS));
						((BaseChatPluginServerPlayer) player).setMutes(getAnticheatPunishments(player, DataContainer.MUTES));
					}
				}
			} if (ChatPlugin.getInstance().isPremium() && IntegrationManager.getInstance().isAtLeastOneAnticheatEnabled()) {
				if (refreshAnticheatCounters) {
					BanManager.getInstance().setAnticheatStorageCount(getAnticheatPunishments(DataContainer.BANS));
					WarningManager.getInstance().setAnticheatStorageCount(getAnticheatPunishments(DataContainer.WARNINGS));
					KickManager.getInstance().setAnticheatStorageCount(getAnticheatPunishments(DataContainer.KICKS));
					MuteManager.getInstance().setAnticheatStorageCount(getAnticheatPunishments(DataContainer.MUTES));
				} refreshAnticheatCounters = !refreshAnticheatCounters;
			}
		} catch (SQLException e) {
			LogManager.log("SQLException occurred while updating the server's punishments' placeholders: {0}", 2, e.getMessage());
		}
	}
	
	private int getStaffPunishments(DataContainer container) throws SQLException {
		return storage.count(container, new WhereCondition("staff_member", WhereOperator.NOT_EQUAL, "CONSOLE")).intValue();
	}
	
	private short getStaffPunishments(ChatPluginServerPlayer player, DataContainer container) throws SQLException {
		return storage.count(container, new WhereCondition("staff_member", WhereOperator.NOT_EQUAL, "CONSOLE"), new WhereCondition("player_name", WhereOperator.EQUAL, player.getName())).shortValue();
	}
	
	private int getAnticheatPunishments(DataContainer container) throws SQLException {
		AnticheatManager anticheat = AnticheatManager.getInstance();
		int count = 0;
		
		if (anticheat.isEnabled())
			for (String reason : storage.getColumnValues(container, "reason", String.class, new WhereCondition("staff_member", WhereOperator.EQUAL, "CONSOLE")))
				if (anticheat.isAnticheatReason(reason))
					count++;
		return count;
	}
	
	private short getAnticheatPunishments(ChatPluginServerPlayer player, DataContainer container) throws SQLException {
		AnticheatManager anticheat = AnticheatManager.getInstance();
		short count = 0;
		
		if (anticheat.isEnabled())
			for (String reason : storage.getColumnValues(container, "reason", String.class, new WhereCondition("staff_member", WhereOperator.EQUAL, "CONSOLE"), new WhereCondition("player_name", WhereOperator.EQUAL, player.getName())))
				if (anticheat.isAnticheatReason(reason))
					count++;
		return count;
	}
	
	@Override
	public String translatePlaceholders(String input, ChatPluginServerPlayer player, Language language, List<PlaceholderType> placeholders, boolean translateColors) {
		if (input == null)
			return null;
		String output = input;
		
		if (placeholders.contains(PlaceholderType.JUST_NAME))
			output = output.replace("{player}", player.getName());
		if (placeholders.contains(PlaceholderType.SERVER))
			output = translateServerPlaceholders(output, language);
		if (placeholders.contains(PlaceholderType.PLAYER))
			output = translatePlayerPlaceholders(output, player, language);
		if (placeholders.contains(PlaceholderType.INTEGRATIONS))
			output = translateIntegrationsPlaceholders(output, player, language);
		return translateColors ? ChatColor.translate(output.replace("{pfx}", language.getConfiguration().getString("misc.prefix", Language.getMainLanguage().getConfiguration().getString("misc.prefix"))))
				: output.replace("{pfx}", language.getConfiguration().getString("misc.prefix", Language.getMainLanguage().getConfiguration().getString("misc.prefix")));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String translatePlayerPlaceholders(String input, ChatPluginServerPlayer player, Language language, boolean translateColors) {
		if (input == null)
			return null;
		String output = input;
		
		if (output.contains("{player}"))
			output = output.replace("{player}", player.getName());
		if (output.contains("{uuid}"))
			output = output.replace("{uuid}", player.getUUID().toString());
		if (output.contains("{display_name}"))
			output = output.replace("{display_name}", player.getDisplayName());
		if (output.contains("{ip_address}"))
			output = output.replace("{ip_address}", player.getIPAddress().getHostAddress());
		if (output.contains("{health}"))
			output = output.replace("{health}", String.valueOf((int) (Environment.isBukkit() ? player.toAdapter().bukkitValue().getHealth() : player.toAdapter().spongeValue().health().get())));
		if (output.contains("{max_health}"))
			output = output.replace("{max_health}", String.valueOf((int) (Environment.isBukkit() ? player.toAdapter().bukkitValue().getHealthScale() : player.toAdapter().spongeValue().maxHealth().get())));
		if (output.contains("{food}"))
			output = output.replace("{food}", String.valueOf(Environment.isBukkit() ? player.toAdapter().bukkitValue().getFoodLevel() : player.toAdapter().spongeValue().foodLevel().get()));
		if (output.contains("{level}"))
			output = output.replace("{level}", String.valueOf(Environment.isBukkit() ? player.toAdapter().bukkitValue().getLevel() : player.toAdapter().spongeValue().get(Keys.EXPERIENCE_LEVEL).orElse(0).intValue()));
		if (output.contains("{xp}"))
			output = output.replace("{xp}", String.valueOf(Environment.isBukkit() ? player.toAdapter().bukkitValue().getTotalExperience() : player.toAdapter().spongeValue().get(Keys.TOTAL_EXPERIENCE).orElse(0).intValue()));
		if (output.contains("{gamemode}"))
			output = output.replace("{gamemode}", Environment.isBukkit() ? player.toAdapter().bukkitValue().getGameMode().name() : player.toAdapter().spongeValue().gameMode().get().getName()).toLowerCase();
		if (output.contains("{ping}"))
			output = output.replace("{ping}", String.valueOf(player.getPing()));
		if (output.contains("{ping_format}"))
			output = output.replace("{ping_format}", PingManager.getInstance().formatPing(player));
		if (output.contains("{ping_quality_color}"))
			output = output.replace("{ping_quality_color}", PingManager.getInstance().getPingQuality(player.getPing()).getColor(language));
		if (output.contains("{ping_quality_text}"))
			output = output.replace("{ping_quality_text}", PingManager.getInstance().getPingQuality(player.getPing()).getText(language));
		if (output.contains("{language_id"))
			output = output.replace("{language_id}", player.getLanguage().getID());
		if (output.contains("{language_display_name"))
			output = output.replace("{language_display_name}", player.getLanguage().getDisplayName());
		if (output.contains("{locale}")) // maybe add more of its variants in the future
			output = output.replace("{locale}", player.getLocale().getCountry());
		if (output.contains("{version}"))
			output = output.replace("{version}", player.getVersion().format());
		if (output.contains("{version_protocol}"))
			output = output.replace("{version_protocol}", String.valueOf(player.getVersion().getProtocol()));
		if (output.contains("{client_edition}"))
			output = output.replace("{client_edition}", player.isBedrockPlayer() ? "Bedrock" : "Java");
		if (output.contains("{last_login}"))
			output = output.replace("{last_login}", Utils.formatTime(System.currentTimeMillis() - player.getLoginTime(), language, false, true));
		if (output.contains("{time_played}"))
			output = output.replace("{time_played}", Utils.formatTime((Environment.isBukkit() ? player.toAdapter().bukkitValue().getStatistic(Statistic.valueOf(VersionUtils.getVersion().getProtocol() < 341 ? "PLAY_ONE_TICK" : "PLAY_ONE_MINUTE")) : player.toAdapter().spongeValue().getStatisticData().get(Keys.STATISTICS).get().get(Statistics.TIME_PLAYED)) / 20 * 1000, language, false, true)); // Sponge v4.2
		if (output.contains("{emojis_tone}") && InstantEmojisManager.getInstance().isEnabled()) {
			ChatColor tone = player.getEmojisTone() == ChatColor.RESET ? InstantEmojisManager.getInstance().getDefaultTone() : player.getEmojisTone();
			output = output.replace("{emojis_tone}", (VersionUtils.getVersion().isAtLeast(Version.V1_16) ? tone : tone.getClosestDefaultColor()).toString());
		} if (output.contains("{world}"))
			output = output.replace("{world}", player.getWorld());
		if (output.contains("{online_world}"))
			output = output.replace("{online_world}", String.valueOf(VanishManager.getInstance().getOnlineWorld(player.getWorld())));
		if (output.contains("{vanished_world}"))
			output = output.replace("{vanished_world}", String.valueOf(VanishManager.getInstance().getVanishedList(player.getWorld()).size()));
		if (output.contains("{player_id}"))
			output = output.replace("{player_id}", String.valueOf(player.getID()));
		if (output.contains("{player_bans}"))
			output = output.replace("{player_bans}", String.valueOf(player.getBans()));
		if (output.contains("{player_warnings}"))
			output = output.replace("{player_warnings}", String.valueOf(player.getWarnings()));
		if (output.contains("{player_kicks}"))
			output = output.replace("{player_kicks}", String.valueOf(player.getKicks()));
		if (output.contains("{player_mutes}"))
			output = output.replace("{player_mutes}", String.valueOf(player.getMutes()));
		if (output.contains("{messages_sent}"))
			output = output.replace("{messages_sent}", String.valueOf(player.getMessagesSent()));
		if (output.contains("{player_anticheat_bans}"))
			output = output.replace("{player_anticheat_bans}", String.valueOf(player.getAnticheatBans()));
		if (output.contains("{player_anticheat_warnings}"))
			output = output.replace("{player_anticheat_warnings}", String.valueOf(player.getAnticheatWarnings()));
		if (output.contains("{player_anticheat_kicks}"))
			output = output.replace("{player_anticheat_kicks}", String.valueOf(player.getAnticheatKicks()));
		if (output.contains("{player_anticheat_mutes}"))
			output = output.replace("{player_anticheat_mutes}", String.valueOf(player.getAnticheatMutes()));
		if (output.contains("{x}") || output.contains("{y}") || output.contains("{z}") || output.contains("{yaw}") || output.contains("{pitch}")) {
			Object location = Environment.isBukkit() ? player.toAdapter().bukkitValue().getLocation() : player.toAdapter().spongeValue().getLocation();
			Object headRotation = Environment.isBukkit() ? null : player.toAdapter().spongeValue().getHeadRotation();
			
			output = output
					.replace("{x}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getBlockX() : ((org.spongepowered.api.world.Location<World>) location).getBlockX()))
					.replace("{y}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getBlockY() : ((org.spongepowered.api.world.Location<World>) location).getBlockY()))
					.replace("{z}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getBlockZ() : ((org.spongepowered.api.world.Location<World>) location).getBlockZ()))
					.replace("{yaw}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getYaw() : ((Vector3d) headRotation).getX()))
					.replace("{pitch}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getPitch() : ((Vector3d) headRotation).getY()));
		} if (output.contains("{rank") || output.contains("prefix}") || output.contains("suffix}") || output.contains("color}") || output.contains("{rank_description}"))
			output = player.getRank().formatPlaceholders(player.getChatColor() == ChatColor.RESET ? output : output.replace("{chat_color}", VersionUtils.getVersion().isAtLeast(Version.V1_16) ? player.getChatColor().toString() : player.getChatColor().getClosestDefaultColor().toString()), language);
		if (output.contains("{isp}") || output.contains("{continent}") || output.contains("{country}") || output.contains("{subdivisions}") || output.contains("{city}")
				|| output.contains("{country_code}") || output.contains("{inside_eu}") || output.contains("{time_zone}") || output.contains("{country_code}") || output.contains("{postal_code}")
				|| output.contains("{latitude}") || output.contains("{longitude}") || output.contains("{accuracy_radius_") || output.contains("{relative_date_")) {
			output = player.getIPLookup(false).formatPlaceholders(output, language);
		} return translateColors ? ChatColor.translate(output) : output;
	}
	
	@Override
	public String translateServerPlaceholders(String input, Language language, boolean translateColors) {
		if (input == null)
			return null;
		String output = input;
		
		if (output.contains("{online}"))
			output = output.replace("{online}", String.valueOf(VanishManager.getInstance().getOnlineServer()));
		if (output.contains("{online_total}"))
			output = output.replace("{online_total}", String.valueOf(ProxyManager.getInstance().isEnabled() ? ProxyManager.getInstance().getOnlinePlayers("ALL", true) : VanishManager.getInstance().getOnlineServer()));
		if (output.contains("{max_players}"))
			output = output.replace("{max_players}", String.valueOf(Utils.getMaxPlayers()));
		if (output.contains("{vanished}"))
			output = output.replace("{vanished}", String.valueOf(VanishManager.getInstance().getVanishedAmount()));
		if (output.contains("{date_full}"))
			output = output.replace("{date_full}", Utils.formatDate(System.currentTimeMillis(), language, DateFormat.FULL));
		if (output.contains("{date_day}"))
			output = output.replace("{date_day}", Utils.formatDate(System.currentTimeMillis(), language, DateFormat.DAY));
		if (output.contains("{date_hour}"))
			output = output.replace("{date_hour}", Utils.formatDate(System.currentTimeMillis(), language, DateFormat.HOUR));
		if (output.contains("{enabled_worlds}"))
			output = output.replace("{enabled_worlds}", String.valueOf(((ServerPlayerManager) PlayerManager.getInstance()).getEnabledWorlds().size()));
		if (output.contains("{enabled_players}"))
			output = output.replace("{enabled_players}", String.valueOf(PlayerManager.getInstance().getTotalPlayers()));
		if (output.contains("{enabled_managers}"))
			output = output.replace("{enabled_managers}", String.valueOf(ChatPluginManagers.getInstance().getEnabledManagers().size()));
		if (output.contains("{startup_time}"))
			output = output.replace("{startup_time}", String.valueOf(ChatPlugin.getInstance().getStartupTime()));
		if (output.contains("{last_reload_time}"))
			output = output.replace("{last_reload_time}", String.valueOf(ChatPlugin.getInstance().getLastReloadTime()));
		if (output.contains("{uptime}"))
			output = output.replace("{uptime}", Utils.formatTime(ManagementFactory.getRuntimeMXBean().getUptime(), language, false, true));
		if (output.contains("{plugin_version}"))
			output = output.replace("{plugin_version}", ChatPlugin.VERSION);
		if (output.contains("{server_version}"))
			output = output.replace("{server_version}", VersionUtils.getVersion().format());
		if (output.contains("{server_version_protocol}"))
			output = output.replace("{server_version_protocol}", String.valueOf(VersionUtils.getVersion().getProtocol()));
		if (output.contains("{server_nms_version}"))
			output = output.replace("{server_nms_version}", VersionUtils.getNMSVersion());
		if (output.contains("{server_java_version}"))
			output = output.replace("{server_java_version}", System.getProperty("java.version"));
		if (output.contains("{server_id}"))
			output = output.replace("{server_id}", ProxyManager.getInstance().getServerID());
		if (output.contains("{server_display_name}"))
			output = output.replace("{server_display_name}", ProxyManager.getInstance().getServerDisplayName());
		if (output.contains("{main_language_id}"))
			output = output.replace("{main_language_id}", Language.getMainLanguage().getID());
		if (output.contains("{main_language_display_name}"))
			output = output.replace("{main_language_display_name}", Language.getMainLanguage().getDisplayName());
		if (output.contains("{total_storage}"))
			output = output.replace("{total_storage}", MemoryUtils.formatMemory(Utils.getTotalStorage(), MemoryUtils.GIGABYTE));
		if (output.contains("{used_storage}"))
			output = output.replace("{used_storage}", MemoryUtils.formatMemory(Utils.getTotalStorage() - Utils.getFreeStorage(), MemoryUtils.GIGABYTE));
		if (output.contains("{free_storage}"))
			output = output.replace("{free_storage}", MemoryUtils.formatMemory(Utils.getFreeStorage(), MemoryUtils.GIGABYTE));
		if (output.contains("{server_os_name}"))
			output = output.replace("{server_os_name}", System.getProperty("os.name"));
		if (output.contains("{server_os_arch}"))
			output = output.replace("{server_os_arch}", System.getProperty("os.arch"));
		if (output.contains("{server_os_version}"))
			output = output.replace("{server_os_version}", System.getProperty("os.version"));
		if (output.contains("{active_threads}"))
			output = output.replace("{active_threads}", String.valueOf(Thread.activeCount()));
		if (output.contains("{total_players}"))
			output = output.replace("{total_players}", String.valueOf(((ServerPlayerManager) PlayerManager.getInstance()).getStorageCount()));
		if (output.contains("{total_bans}"))
			output = output.replace("{total_bans}", String.valueOf(BanManager.getInstance().getStorageCount()));
		if (output.contains("{total_warnings}"))
			output = output.replace("{total_warnings}", String.valueOf(WarningManager.getInstance().getStorageCount()));
		if (output.contains("{total_kicks}"))
			output = output.replace("{total_kicks}", String.valueOf(KickManager.getInstance().getStorageCount()));
		if (output.contains("{total_mutes}"))
			output = output.replace("{total_mutes}", String.valueOf(MuteManager.getInstance().getStorageCount()));
		if (output.contains("{total_staff_bans}"))
			output = output.replace("{total_staff_bans}", String.valueOf(BanManager.getInstance().getStaffStorageCount()));
		if (output.contains("{total_staff_warnings}"))
			output = output.replace("{total_staff_warnings}", String.valueOf(WarningManager.getInstance().getStaffStorageCount()));
		if (output.contains("{total_staff_kicks}"))
			output = output.replace("{total_staff_kicks}", String.valueOf(KickManager.getInstance().getStaffStorageCount()));
		if (output.contains("{total_staff_mutes}"))
			output = output.replace("{total_staff_mutes}", String.valueOf(MuteManager.getInstance().getStaffStorageCount()));
		if (output.contains("{total_anticheat_bans}"))
			output = output.replace("{total_anticheat_bans}", String.valueOf(BanManager.getInstance().getAnticheatStorageCount()));
		if (output.contains("{total_anticheat_warnings}"))
			output = output.replace("{total_anticheat_warnings}", String.valueOf(WarningManager.getInstance().getAnticheatStorageCount()));
		if (output.contains("{total_anticheat_kicks}"))
			output = output.replace("{total_anticheat_kicks}", String.valueOf(KickManager.getInstance().getAnticheatStorageCount()));
		if (output.contains("{total_anticheat_mutes}"))
			output = output.replace("{total_anticheat_mutes}", String.valueOf(MuteManager.getInstance().getAnticheatStorageCount()));
		if (output.contains("{tps_")) {
			TPSManager tpsManager = TPSManager.getInstance();
			
			output = output
					.replace("{tps_1_min}", String.valueOf(Utils.truncate(tpsManager.getTPS(TPSTimeInterval.ONE_MINUTE), 2)))
					.replace("{tps_5_min}", String.valueOf(Utils.truncate(tpsManager.getTPS(TPSTimeInterval.FIVE_MINUTES), 2)))
					.replace("{tps_15_min}", String.valueOf(Utils.truncate(tpsManager.getTPS(TPSTimeInterval.FIFTEEN_MINUTES), 2)))
					.replace("{tps_1_min_format}", tpsManager.formatTPS(TPSTimeInterval.ONE_MINUTE, language))
					.replace("{tps_5_min_format}", tpsManager.formatTPS(TPSTimeInterval.FIVE_MINUTES, language))
					.replace("{tps_15_min_format}", tpsManager.formatTPS(TPSTimeInterval.FIFTEEN_MINUTES, language));
		} if (output.contains("_memory}") || output.contains("{cpu_threads}")) {
			output = output
					.replace("{max_memory}", MemoryUtils.formatMemory(runtime.maxMemory()))
					.replace("{total_memory}", MemoryUtils.formatMemory(runtime.totalMemory()))
					.replace("{used_memory}", MemoryUtils.formatMemory(runtime.totalMemory() - runtime.freeMemory()))
					.replace("{free_memory}", MemoryUtils.formatMemory(runtime.freeMemory()))
					.replace("{cpu_threads}", String.valueOf(runtime.availableProcessors()));
		} if (output.contains("{discord_punishments_channel_id}"))
			output = output.replace("{discord_punishments_channel_id}", String.valueOf(DiscordIntegrationManager.getInstance().isEnabled() ? DiscordIntegrationManager.getInstance().getPunishmentsChannelID() : -1));
		if (output.contains("{discord_staff_notifications_channel_id}"))
			output = output.replace("{discord_staff_notifications_channel_id}", String.valueOf(DiscordIntegrationManager.getInstance().isEnabled() ? DiscordIntegrationManager.getInstance().getStaffNotificationsChannelID() : -1));
		if (output.contains("{random_color}"))
			output = output.replace("{random_color}", ChatColor.getRandomColor().toString());
		return translateColors ? ChatColor.translate(ProxyManager.getInstance().formatOnlineAndVanishedPlaceholders(output, true)) : ProxyManager.getInstance().formatOnlineAndVanishedPlaceholders(output, true);
	}
	
	@Override
	public String translateIntegrationsPlaceholders(String input, ChatPluginServerPlayer player, Language language, boolean translateColors) {
		if (input == null)
			return null;
		String output = input;
		
		try {
			if (output.contains("{balance}")) {
				double vaultBalance, balance = -1D;
				
				if (IntegrationType.VAULT.isEnabled() && (vaultBalance = IntegrationType.VAULT.get().getBalance(player)) != Double.MIN_VALUE)
					balance = vaultBalance;
				else if (IntegrationType.ESSENTIALSX.isEnabled())
					balance = IntegrationType.ESSENTIALSX.get().getBalance(player);
				output = output.replace("{balance}", Utils.formatBalance(balance, ConfigurationType.CONFIG.get().getInt("settings.balance-placeholder.decimals", 2)));
			} if (IntegrationType.PLACEHOLDERAPI.isEnabled())
				output = IntegrationType.PLACEHOLDERAPI.get().translatePlaceholders(output, player);
			if (IntegrationType.MVDWPLACEHOLDERAPI.isEnabled())
				output = IntegrationType.MVDWPLACEHOLDERAPI.get().translatePlaceholders(output, player);
			return translateColors ? ChatColor.translate(output) : output;
		} catch (NullPointerException e) {
			return input;
		}
	}
	
}
