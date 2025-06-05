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
	private static final Runtime RUNTIME = Runtime.getRuntime();
	private static final WhereCondition STAFF_MEMBER_EQUAL_CONSOLE = new WhereCondition("staff_member", WhereOperator.EQUAL, "CONSOLE");
	private static final WhereCondition STAFF_MEMBER_NOT_EQUAL_CONSOLE = new WhereCondition("staff_member", WhereOperator.NOT_EQUAL, "CONSOLE");
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
	public void run() { // TODO: set these in BaseChatPluginServerPlayer's constructor and increment them when needed
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
				((BaseChatPluginServerPlayer) player).setMessagesSent(storage.getPlayerData(PlayersDataType.MESSAGES_SENT, player.getID()));
				((BaseChatPluginServerPlayer) player).setAntispamInfractions(storage.getPlayerData(PlayersDataType.ANTISPAM_INFRACTIONS, player.getID()));
				
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
		} catch (SQLException sqle) {
			LogManager.log("SQLException occurred while updating placeholders for a player or the server: {0}", 2, sqle.getLocalizedMessage());
		}
	}
	
	private int getStaffPunishments(DataContainer container) throws SQLException {
		return storage.count(container, STAFF_MEMBER_NOT_EQUAL_CONSOLE).intValue();
	}
	
	private short getStaffPunishments(ChatPluginServerPlayer player, DataContainer container) throws SQLException {
		return storage.count(container, STAFF_MEMBER_NOT_EQUAL_CONSOLE, new WhereCondition("player_name", WhereOperator.EQUAL, player.getName())).shortValue();
	}
	
	private int getAnticheatPunishments(DataContainer container) throws SQLException {
		AnticheatManager anticheat = AnticheatManager.getInstance();
		int count = 0;
		
		if (anticheat.isEnabled())
			for (String reason : storage.getColumnValues(container, "reason", String.class, STAFF_MEMBER_EQUAL_CONSOLE))
				if (anticheat.isAnticheatReason(reason))
					count++;
		return count;
	}
	
	private short getAnticheatPunishments(ChatPluginServerPlayer player, DataContainer container) throws SQLException {
		AnticheatManager anticheat = AnticheatManager.getInstance();
		short count = 0;
		
		if (anticheat.isEnabled())
			for (String reason : storage.getColumnValues(container, "reason", String.class, STAFF_MEMBER_EQUAL_CONSOLE, new WhereCondition("player_name", WhereOperator.EQUAL, player.getName())))
				if (anticheat.isAnticheatReason(reason))
					count++;
		return count;
	}
	
	@Override
	public String translatePlaceholders(String input, ChatPluginServerPlayer player, Language language, List<PlaceholderType> placeholders, boolean translateColors) {
		if (input == null)
			return null;
		if (placeholders.contains(PlaceholderType.JUST_NAME))
			input = input.replace("{player}", player.getName());
		if (placeholders.contains(PlaceholderType.SERVER))
			input = translateServerPlaceholders(input, language);
		if (placeholders.contains(PlaceholderType.PLAYER))
			input = translatePlayerPlaceholders(input, player, language);
		if (placeholders.contains(PlaceholderType.INTEGRATIONS))
			input = translateIntegrationsPlaceholders(input, player, language);
		return translateColors ? ChatColor.translate(input.replace("{pfx}", language.getConfiguration().getString("misc.prefix", Language.getMainLanguage().getConfiguration().getString("misc.prefix"))))
				: input.replace("{pfx}", language.getConfiguration().getString("misc.prefix", Language.getMainLanguage().getConfiguration().getString("misc.prefix")));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String translatePlayerPlaceholders(String input, ChatPluginServerPlayer player, Language language, boolean translateColors) {
		if (input == null)
			return null;
		if (input.contains("{player}"))
			input = input.replace("{player}", player.getName());
		if (input.contains("{uuid}"))
			input = input.replace("{uuid}", player.getUUID().toString());
		if (input.contains("{display_name}"))
			input = input.replace("{display_name}", player.getDisplayName());
		if (input.contains("{ip_address}"))
			input = input.replace("{ip_address}", player.getIPAddress().getHostAddress());
		if (input.contains("{health}"))
			input = input.replace("{health}", String.valueOf((int) (Environment.isBukkit() ? player.toAdapter().bukkitValue().getHealth() : player.toAdapter().spongeValue().health().get())));
		if (input.contains("{max_health}"))
			input = input.replace("{max_health}", String.valueOf((int) (Environment.isBukkit() ? player.toAdapter().bukkitValue().getHealthScale() : player.toAdapter().spongeValue().maxHealth().get())));
		if (input.contains("{food}"))
			input = input.replace("{food}", String.valueOf(Environment.isBukkit() ? player.toAdapter().bukkitValue().getFoodLevel() : player.toAdapter().spongeValue().foodLevel().get()));
		if (input.contains("{level}"))
			input = input.replace("{level}", String.valueOf(Environment.isBukkit() ? player.toAdapter().bukkitValue().getLevel() : player.toAdapter().spongeValue().get(Keys.EXPERIENCE_LEVEL).orElse(0).intValue()));
		if (input.contains("{xp}"))
			input = input.replace("{xp}", String.valueOf(Environment.isBukkit() ? player.toAdapter().bukkitValue().getTotalExperience() : player.toAdapter().spongeValue().get(Keys.TOTAL_EXPERIENCE).orElse(0).intValue()));
		if (input.contains("{gamemode}"))
			input = input.replace("{gamemode}", Environment.isBukkit() ? player.toAdapter().bukkitValue().getGameMode().name() : player.toAdapter().spongeValue().gameMode().get().getName()).toLowerCase();
		if (input.contains("{ping}"))
			input = input.replace("{ping}", String.valueOf(player.getPing()));
		if (input.contains("{ping_format}"))
			input = input.replace("{ping_format}", PingManager.getInstance().formatPing(player));
		if (input.contains("{ping_quality_color}"))
			input = input.replace("{ping_quality_color}", PingManager.getInstance().getPingQuality(player.getPing()).getColor(language));
		if (input.contains("{ping_quality_text}"))
			input = input.replace("{ping_quality_text}", PingManager.getInstance().getPingQuality(player.getPing()).getText(language));
		if (input.contains("{language_id}"))
			input = input.replace("{language_id}", player.getLanguage().getID());
		if (input.contains("{language_display_name}"))
			input = input.replace("{language_display_name}", player.getLanguage().getDisplayName());
		if (input.contains("{locale}")) // maybe add more of its variants in the future
			input = input.replace("{locale}", player.getLocale().getCountry());
		if (input.contains("{version}"))
			input = input.replace("{version}", player.getVersion().format());
		if (input.contains("{version_protocol}"))
			input = input.replace("{version_protocol}", String.valueOf(player.getVersion().getProtocol()));
		if (input.contains("{client_edition}"))
			input = input.replace("{client_edition}", player.isBedrockPlayer() ? "Bedrock" : "Java");
		if (input.contains("{last_login}"))
			input = input.replace("{last_login}", Utils.formatTime(System.currentTimeMillis() - player.getLoginTime(), language, false, true));
		if (input.contains("{time_played}"))
			input = input.replace("{time_played}", Utils.formatTime((Environment.isBukkit() ? player.toAdapter().bukkitValue().getStatistic(Statistic.valueOf(VersionUtils.getVersion().getProtocol() < 341 ? "PLAY_ONE_TICK" : "PLAY_ONE_MINUTE")) : player.toAdapter().spongeValue().getStatisticData().get(Keys.STATISTICS).get().get(Statistics.TIME_PLAYED)) / 20 * 1000, language, false, true)); // Sponge v4.2
		if (input.contains("{emojis_tone}") && InstantEmojisManager.getInstance().isEnabled()) {
			ChatColor tone = player.getEmojisTone() == ChatColor.RESET ? InstantEmojisManager.getInstance().getDefaultTone() : player.getEmojisTone();
			input = input.replace("{emojis_tone}", (VersionUtils.getVersion().isAtLeast(Version.V1_16) ? tone : tone.getClosestDefaultColor()).toString());
		} if (input.contains("{world}"))
			input = input.replace("{world}", player.getWorld());
		if (input.contains("{online_world}"))
			input = input.replace("{online_world}", String.valueOf(VanishManager.getInstance().getOnlineWorld(player.getWorld())));
		if (input.contains("{vanished_world}"))
			input = input.replace("{vanished_world}", String.valueOf(VanishManager.getInstance().getVanishedList(player.getWorld()).size()));
		if (input.contains("{player_id}"))
			input = input.replace("{player_id}", String.valueOf(player.getID()));
		if (input.contains("{player_bans}"))
			input = input.replace("{player_bans}", String.valueOf(player.getBans()));
		if (input.contains("{player_warnings}"))
			input = input.replace("{player_warnings}", String.valueOf(player.getWarnings()));
		if (input.contains("{player_kicks}"))
			input = input.replace("{player_kicks}", String.valueOf(player.getKicks()));
		if (input.contains("{player_mutes}"))
			input = input.replace("{player_mutes}", String.valueOf(player.getMutes()));
		if (input.contains("{messages_sent}"))
			input = input.replace("{messages_sent}", String.valueOf(player.getMessagesSent()));
		if (input.contains("{antispam_infractions}"))
			input = input.replace("{antispam_infractions}", String.valueOf(player.getAntispamInfractions()));
		if (input.contains("{player_anticheat_bans}"))
			input = input.replace("{player_anticheat_bans}", String.valueOf(player.getAnticheatBans()));
		if (input.contains("{player_anticheat_warnings}"))
			input = input.replace("{player_anticheat_warnings}", String.valueOf(player.getAnticheatWarnings()));
		if (input.contains("{player_anticheat_kicks}"))
			input = input.replace("{player_anticheat_kicks}", String.valueOf(player.getAnticheatKicks()));
		if (input.contains("{player_anticheat_mutes}"))
			input = input.replace("{player_anticheat_mutes}", String.valueOf(player.getAnticheatMutes()));
		if (input.contains("{x}") || input.contains("{y}") || input.contains("{z}") || input.contains("{yaw}") || input.contains("{pitch}")) {
			Object location = Environment.isBukkit() ? player.toAdapter().bukkitValue().getLocation() : player.toAdapter().spongeValue().getLocation();
			Object headRotation = Environment.isBukkit() ? null : player.toAdapter().spongeValue().getHeadRotation();
			
			input = input
					.replace("{x}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getBlockX() : ((org.spongepowered.api.world.Location<World>) location).getBlockX()))
					.replace("{y}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getBlockY() : ((org.spongepowered.api.world.Location<World>) location).getBlockY()))
					.replace("{z}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getBlockZ() : ((org.spongepowered.api.world.Location<World>) location).getBlockZ()))
					.replace("{yaw}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getYaw() : ((Vector3d) headRotation).getX()))
					.replace("{pitch}", String.valueOf(Environment.isBukkit() ? ((org.bukkit.Location) location).getPitch() : ((Vector3d) headRotation).getY()));
		} if (input.contains("{rank") || input.contains("prefix}") || input.contains("suffix}") || input.contains("color}") || input.contains("{rank_description}"))
			input = player.getRank().formatPlaceholders(player.getChatColor() == ChatColor.RESET ? input : input.replace("{chat_color}", VersionUtils.getVersion().isAtLeast(Version.V1_16) ? player.getChatColor().toString() : player.getChatColor().getClosestDefaultColor().toString()), language);
		if (input.contains("{isp}") || input.contains("{continent}") || input.contains("{country}") || input.contains("{subdivisions}") || input.contains("{city}")
				|| input.contains("{country_code}") || input.contains("{inside_eu}") || input.contains("{time_zone}") || input.contains("{country_code}") || input.contains("{postal_code}")
				|| input.contains("{latitude}") || input.contains("{longitude}") || input.contains("{accuracy_radius_") || input.contains("{relative_date_")) {
			input = player.getIPLookup(false).formatPlaceholders(input, language);
		} return translateColors ? ChatColor.translate(input) : input;
	}
	
	@Override
	public String translateServerPlaceholders(String input, Language language, boolean translateColors) {
		if (input == null)
			return null;
		if (input.contains("{online}"))
			input = input.replace("{online}", String.valueOf(VanishManager.getInstance().getOnlineServer()));
		if (input.contains("{online_total}"))
			input = input.replace("{online_total}", String.valueOf(ProxyManager.getInstance().isEnabled() ? ProxyManager.getInstance().getOnlinePlayers("ALL", true) : VanishManager.getInstance().getOnlineServer()));
		if (input.contains("{max_players}"))
			input = input.replace("{max_players}", String.valueOf(Utils.getMaxPlayers()));
		if (input.contains("{vanished}"))
			input = input.replace("{vanished}", String.valueOf(VanishManager.getInstance().getVanishedAmount()));
		if (input.contains("{date_full}"))
			input = input.replace("{date_full}", Utils.formatDate(System.currentTimeMillis(), language, DateFormat.FULL));
		if (input.contains("{date_day}"))
			input = input.replace("{date_day}", Utils.formatDate(System.currentTimeMillis(), language, DateFormat.DAY));
		if (input.contains("{date_hour}"))
			input = input.replace("{date_hour}", Utils.formatDate(System.currentTimeMillis(), language, DateFormat.HOUR));
		if (input.contains("{enabled_worlds}"))
			input = input.replace("{enabled_worlds}", String.valueOf((ServerPlayerManager.getInstance().getEnabledWorlds().contains("*") ? Utils.getWorlds() : ServerPlayerManager.getInstance().getEnabledWorlds()).size()));
		if (input.contains("{enabled_players}"))
			input = input.replace("{enabled_players}", String.valueOf(ServerPlayerManager.getInstance().getPlayers().size()));
		if (input.contains("{enabled_managers}"))
			input = input.replace("{enabled_managers}", String.valueOf(ChatPluginManagers.getInstance().getEnabledManagers().size()));
		if (input.contains("{startup_time}"))
			input = input.replace("{startup_time}", String.valueOf(ChatPlugin.getInstance().getStartupTime()));
		if (input.contains("{last_reload_time}"))
			input = input.replace("{last_reload_time}", String.valueOf(ChatPlugin.getInstance().getLastReloadTime()));
		if (input.contains("{uptime}"))
			input = input.replace("{uptime}", Utils.formatTime(ManagementFactory.getRuntimeMXBean().getUptime(), language, false, true));
		if (input.contains("{plugin_version}"))
			input = input.replace("{plugin_version}", ChatPlugin.VERSION);
		if (input.contains("{server_version}"))
			input = input.replace("{server_version}", VersionUtils.getVersion().format());
		if (input.contains("{server_version_protocol}"))
			input = input.replace("{server_version_protocol}", String.valueOf(VersionUtils.getVersion().getProtocol()));
		if (input.contains("{server_nms_version}"))
			input = input.replace("{server_nms_version}", VersionUtils.getNMSVersion());
		if (input.contains("{server_java_version}"))
			input = input.replace("{server_java_version}", System.getProperty("java.version"));
		if (input.contains("{server_id}"))
			input = input.replace("{server_id}", ProxyManager.getInstance().getServerID());
		if (input.contains("{server_display_name}"))
			input = input.replace("{server_display_name}", ProxyManager.getInstance().getServerDisplayName());
		if (input.contains("{main_language_id}"))
			input = input.replace("{main_language_id}", Language.getMainLanguage().getID());
		if (input.contains("{main_language_display_name}"))
			input = input.replace("{main_language_display_name}", Language.getMainLanguage().getDisplayName());
		if (input.contains("{total_storage}"))
			input = input.replace("{total_storage}", MemoryUtils.formatMemory(Utils.getTotalStorage(), MemoryUtils.GIGABYTE));
		if (input.contains("{used_storage}"))
			input = input.replace("{used_storage}", MemoryUtils.formatMemory(Utils.getTotalStorage() - Utils.getFreeStorage(), MemoryUtils.GIGABYTE));
		if (input.contains("{free_storage}"))
			input = input.replace("{free_storage}", MemoryUtils.formatMemory(Utils.getFreeStorage(), MemoryUtils.GIGABYTE));
		if (input.contains("{server_os_name}"))
			input = input.replace("{server_os_name}", System.getProperty("os.name"));
		if (input.contains("{server_os_arch}"))
			input = input.replace("{server_os_arch}", System.getProperty("os.arch"));
		if (input.contains("{server_os_version}"))
			input = input.replace("{server_os_version}", System.getProperty("os.version"));
		if (input.contains("{active_threads}"))
			input = input.replace("{active_threads}", String.valueOf(Thread.activeCount()));
		if (input.contains("{total_players}"))
			input = input.replace("{total_players}", String.valueOf(ServerPlayerManager.getInstance().getStorageCount()));
		if (input.contains("{total_bans}"))
			input = input.replace("{total_bans}", String.valueOf(BanManager.getInstance().getStorageCount()));
		if (input.contains("{total_warnings}"))
			input = input.replace("{total_warnings}", String.valueOf(WarningManager.getInstance().getStorageCount()));
		if (input.contains("{total_kicks}"))
			input = input.replace("{total_kicks}", String.valueOf(KickManager.getInstance().getStorageCount()));
		if (input.contains("{total_mutes}"))
			input = input.replace("{total_mutes}", String.valueOf(MuteManager.getInstance().getStorageCount()));
		if (input.contains("{total_staff_bans}"))
			input = input.replace("{total_staff_bans}", String.valueOf(BanManager.getInstance().getStaffStorageCount()));
		if (input.contains("{total_staff_warnings}"))
			input = input.replace("{total_staff_warnings}", String.valueOf(WarningManager.getInstance().getStaffStorageCount()));
		if (input.contains("{total_staff_kicks}"))
			input = input.replace("{total_staff_kicks}", String.valueOf(KickManager.getInstance().getStaffStorageCount()));
		if (input.contains("{total_staff_mutes}"))
			input = input.replace("{total_staff_mutes}", String.valueOf(MuteManager.getInstance().getStaffStorageCount()));
		if (input.contains("{total_anticheat_bans}"))
			input = input.replace("{total_anticheat_bans}", String.valueOf(BanManager.getInstance().getAnticheatStorageCount()));
		if (input.contains("{total_anticheat_warnings}"))
			input = input.replace("{total_anticheat_warnings}", String.valueOf(WarningManager.getInstance().getAnticheatStorageCount()));
		if (input.contains("{total_anticheat_kicks}"))
			input = input.replace("{total_anticheat_kicks}", String.valueOf(KickManager.getInstance().getAnticheatStorageCount()));
		if (input.contains("{total_anticheat_mutes}"))
			input = input.replace("{total_anticheat_mutes}", String.valueOf(MuteManager.getInstance().getAnticheatStorageCount()));
		if (input.contains("{tps_")) {
			TPSManager tpsManager = TPSManager.getInstance();
			
			input = input
					.replace("{tps_1_min}", String.valueOf(Utils.truncate(tpsManager.getTPS(TPSTimeInterval.ONE_MINUTE), 2)))
					.replace("{tps_5_min}", String.valueOf(Utils.truncate(tpsManager.getTPS(TPSTimeInterval.FIVE_MINUTES), 2)))
					.replace("{tps_15_min}", String.valueOf(Utils.truncate(tpsManager.getTPS(TPSTimeInterval.FIFTEEN_MINUTES), 2)))
					.replace("{tps_1_min_format}", tpsManager.formatTPS(TPSTimeInterval.ONE_MINUTE, language))
					.replace("{tps_5_min_format}", tpsManager.formatTPS(TPSTimeInterval.FIVE_MINUTES, language))
					.replace("{tps_15_min_format}", tpsManager.formatTPS(TPSTimeInterval.FIFTEEN_MINUTES, language));
		} if (input.contains("_memory}") || input.contains("{cpu_threads}")) {
			input = input
					.replace("{max_memory}", MemoryUtils.formatMemory(RUNTIME.maxMemory()))
					.replace("{total_memory}", MemoryUtils.formatMemory(RUNTIME.totalMemory()))
					.replace("{used_memory}", MemoryUtils.formatMemory(RUNTIME.totalMemory() - RUNTIME.freeMemory()))
					.replace("{free_memory}", MemoryUtils.formatMemory(RUNTIME.freeMemory()))
					.replace("{cpu_threads}", String.valueOf(RUNTIME.availableProcessors()));
		} if (input.contains("{discord_punishments_channel_id}"))
			input = input.replace("{discord_punishments_channel_id}", String.valueOf(DiscordIntegrationManager.getInstance().isEnabled() ? DiscordIntegrationManager.getInstance().getPunishmentsChannelID() : -1));
		if (input.contains("{discord_staff_notifications_channel_id}"))
			input = input.replace("{discord_staff_notifications_channel_id}", String.valueOf(DiscordIntegrationManager.getInstance().isEnabled() ? DiscordIntegrationManager.getInstance().getStaffNotificationsChannelID() : -1));
		if (input.contains("{random_color}"))
			input = input.replace("{random_color}", ChatColor.getRandomColor().toString());
		return translateColors ? ChatColor.translate(ProxyManager.getInstance().formatOnlineAndVanishedPlaceholders(input, true)) : ProxyManager.getInstance().formatOnlineAndVanishedPlaceholders(input, true);
	}
	
	@Override
	public String translateIntegrationsPlaceholders(String input, ChatPluginServerPlayer player, Language language, boolean translateColors) {
		if (input == null)
			return null;
		if (input.contains("{balance")) {
			double balance;
			
			if (IntegrationType.VAULT.isEnabled() && (balance = IntegrationType.VAULT.get().getBalance(player)) != Double.MIN_VALUE)
				input = input.replace("{balance}", IntegrationType.VAULT.get().formatBalance(balance));
			else if (IntegrationType.ESSENTIALSX.isEnabled() && (balance = IntegrationType.ESSENTIALSX.get().getBalance(player)) != Double.MIN_VALUE)
				input = input.replace("{balance}", IntegrationType.ESSENTIALSX.get().formatBalance(balance));
		} if (IntegrationType.PLACEHOLDERAPI.isEnabled())
			input = IntegrationType.PLACEHOLDERAPI.get().translatePlaceholders(input, player);
		if (IntegrationType.MVDWPLACEHOLDERAPI.isEnabled())
			input = IntegrationType.MVDWPLACEHOLDERAPI.get().translatePlaceholders(input, player);
		return translateColors ? ChatColor.translate(input) : input;
	}
	
}
