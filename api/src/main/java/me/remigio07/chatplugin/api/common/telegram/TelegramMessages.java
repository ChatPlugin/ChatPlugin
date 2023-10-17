/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.telegram;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Target;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition.WhereOperator;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.MemoryUtils;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;

/**
 * Class containing implementations of {@link TelegramMessage}.
 */
public class TelegramMessages {
	
	/**
	 * Represents the main messages.
	 * 
	 * <p><strong>Found at:</strong> "messages.main" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 */
	public enum Main implements TelegramMessage {
		
		/**
		 * Represents the help message.
		 * 
		 * <p><strong>Found at:</strong> "messages.main.help" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>}
		 */
		HELP,
		
		/**
		 * Represents the info message.
		 * 
		 * <p><strong>Found at:</strong> "messages.main.help" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		INFO,
		
		/**
		 * Represents the status message.
		 * 
		 * <p><strong>Found at:</strong> "messages.main.status" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		STATUS,
		
		/**
		 * Represents the version message.
		 * 
		 * <p><strong>Found at:</strong> "messages.main.version" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		VERSION;
		
		@Override
		public String getPath() {
			return "messages.main." + name().toLowerCase().replace('_', '-');
		}
		
		@Override
		public String getValue(Object... args) {
			return translateStatus(getString(this));
		}
		
		private String translateVersion(String input) {
			return input == null ? null : input
					.replace("{chatplugin_version}", ChatPlugin.VERSION)
					.replace("{java_telegram_bot_api_version}", TelegramIntegrationManager.getInstance().getJavaTelegramBotAPIVersion());
		}
		
		/**
		 * Translates an input String with the server's status' placeholders.
		 * 
		 * <p>Unlike other methods of this class, this one is public just because
		 * it needs to be visible to {@link TelegramIntegrationManager#run()}.</p>
		 * 
		 * @param input Input containing placeholders
		 * @return Translated placeholders
		 */
		public String translateStatus(String input) {
			Runtime runtime = Runtime.getRuntime();
			return input == null ? null : translateVersion(input
					.replace("{online_minecraft}", String.valueOf(PlayerAdapter.getOnlinePlayers().size()))
					.replace("{telegram_users}", String.valueOf(TelegramBot.getInstance().getUsersAmount()))
					.replace("{enabled_players}", String.valueOf(PlayerManager.getInstance().getTotalPlayers()))
					.replace("{enabled_managers}", String.valueOf(ChatPluginManagers.getInstance().getEnabledManagers().size()))
					.replace("{max_players}", String.valueOf(Utils.getMaxPlayers()))
					.replace("{date}", formatDate(System.currentTimeMillis()))
					.replace("{startup_time}", String.valueOf(ChatPlugin.getInstance().getStartupTime()))
					.replace("{last_reload_time}", String.valueOf(ChatPlugin.getInstance().getLastReloadTime()))
					.replace("{java_version}", System.getProperty("java.version"))
					.replace("{environment}", VersionUtils.getImplementationName())
					.replace("{environment_version}", VersionUtils.getImplementationVersion())
					.replace("{uptime}", formatTime(ManagementFactory.getRuntimeMXBean().getUptime(), false, false))
					.replace("{max_memory}", MemoryUtils.formatMemory(runtime.maxMemory(), MemoryUtils.MEGABYTE))
					.replace("{total_memory}", MemoryUtils.formatMemory(runtime.totalMemory(), MemoryUtils.MEGABYTE))
					.replace("{used_memory}", MemoryUtils.formatMemory(runtime.totalMemory() - runtime.freeMemory(), MemoryUtils.MEGABYTE))
					.replace("{free_memory}", MemoryUtils.formatMemory(runtime.freeMemory(), MemoryUtils.MEGABYTE))
					.replace("{total_storage}", MemoryUtils.formatMemory(Utils.getTotalStorage(), MemoryUtils.GIGABYTE))
					.replace("{used_storage}", MemoryUtils.formatMemory(Utils.getTotalStorage() - Utils.getFreeStorage(), MemoryUtils.GIGABYTE))
					.replace("{free_storage}", MemoryUtils.formatMemory(Utils.getFreeStorage(), MemoryUtils.GIGABYTE))
					.replace("{os_name}", System.getProperty("os.name"))
					.replace("{os_arch}", System.getProperty("os.arch"))
					.replace("{os_version}", System.getProperty("os.version"))
					.replace("{active_threads}", String.valueOf(Thread.activeCount()))
					);
		}
		
	}
	
	/**
	 * Not available yet.
	 */
	public enum OnlinePlayer implements TelegramMessage {
		
		/**
		 * Not available yet.
		 */
		PLAYER_INFO;
		
		@Override
		public String getPath() {
			throw new UnsupportedOperationException("Not available yet");
		}
		
		@Override
		public String getValue(Object... args) {
			throw new UnsupportedOperationException("Not available yet");
		}
		
	}
	
	/**
	 * Not available yet.
	 */
	public enum OfflinePlayer implements TelegramMessage {
		
		/**
		 * Not available yet.
		 */
		PLAYER_PUNISHMENTS;
		
		@Override
		public String getPath() {
			throw new UnsupportedOperationException("Not available yet");
		}
		
		@Override
		public String getValue(Object... args) {
			throw new UnsupportedOperationException("Not available yet");
		}
		
	}
	
	/**
	 * Represents the misc messages.
	 * 
	 * <p><strong>Found at:</strong> "messages" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 */
	public enum Misc implements TelegramMessage {
		
		/**
		 * Represents the inexistent ID message.
		 * 
		 * <p><strong>Found at:</strong> "messages.inexistent-id" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		INEXISTENT_ID,
		
		/**
		 * Represents the no permission message.
		 * 
		 * <p><strong>Found at:</strong> "messages.no-permission" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		NO_PERMISSION,
		
		/**
		 * Represents the invalid number message.
		 * 
		 * <p><strong>Found at:</strong> "messages.invalid-number" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		INVALID_NUMBER,
		
		/**
		 * Represents the group only action message.
		 * 
		 * <p><strong>Found at:</strong> "messages.group-only-action" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		GROUP_ONLY_ACTION,
		
		/**
		 * Represents the wrong syntax message.
		 * 
		 * <p><strong>Found at:</strong> "messages.wrong-syntax" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = String.class, descriptions = "Command's correct usage")
		WRONG_SYNTAX,
		
		/**
		 * Represents the disabled feature message.
		 * 
		 * <p><strong>Found at:</strong> "messages.disabled-feature" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		DISABLED_FEATURE,
		
		/**
		 * Represents the invalid IP address message.
		 * 
		 * <p><strong>Found at:</strong> "messages.invalid-ip-address" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		INVALID_IP_ADDRESS,
		
		/**
		 * Represents the at least one online message.
		 * 
		 * <p><strong>Found at:</strong> "messages.at-least-one-online" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		AT_LEAST_ONE_ONLINE,
		
		/**
		 * Represents the IP lookup message.
		 * 
		 * <p><strong>Found at:</strong> "messages.ip-lookup" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = IPLookup.class, descriptions = "Related IP lookup's object")
		IP_LOOKUP;
		
		@Override
		public String getPath() {
			return "messages." + name().toLowerCase().replace('_', '-');
		}
		
		@Override
		public String getValue(Object... args) {
			String value = getString(this);
			return this == WRONG_SYNTAX ? value.replace("{usage}", (String) args[0]) : this == IP_LOOKUP ? ((IPLookup) args[0]).formatPlaceholders(value) : value;
		}
		
	}
	
	/**
	 * Represents the ban messages.
	 * 
	 * <p><strong>Found at:</strong> "messages.ban" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 */
	public enum Ban implements TelegramMessage {
		
		/**
		 * Represents the ban info message.
		 * 
		 * <p><strong>Found at:</strong> "messages.ban.info" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.ban.Ban.class, descriptions = "Related ban's object")
		INFO,
		
		/**
		 * Represents the ban list message.
		 * 
		 * <p><strong>Found at:</strong> "messages.ban.list" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		LIST,
		
		/**
		 * Represents the ban empty list message.
		 * 
		 * <p><strong>Found at:</strong> "messages.ban.empty-list" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		EMPTY_LIST,
		
		/**
		 * Represents the ban banned message.
		 * 
		 * <p><strong>Found at:</strong> "messages.ban.banned" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.ban.Ban.class, descriptions = "Related ban's object")
		BANNED,
		
		/**
		 * Represents the ban updated message.
		 * 
		 * <p><strong>Found at:</strong> "messages.ban.updated" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.ban.Ban.class, descriptions = "Related ban's object")
		UPDATED,
		
		/**
		 * Represents the ban unbanned message.
		 * 
		 * <p><strong>Found at:</strong> "messages.ban.unbanned" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.ban.Ban.class, descriptions = "Related ban's object")
		UNBANNED,
		
		/**
		 * Represents the ban expired message.
		 * 
		 * <p><strong>Found at:</strong> "messages.ban.expired" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.ban.Ban.class, descriptions = "Related ban's object")
		EXPIRED;
		
		@Override
		public String getPath() {
			return "messages.ban." + name().toLowerCase().replace('_', '-');
		}
		
		@Override
		public String getValue(Object... args) {
			String value = getString(this);
			return name().endsWith("LIST") ? this == LIST ? getBanlistValue() : value : formatValue((me.remigio07.chatplugin.api.common.punishment.ban.Ban) args[0]);
		}
		
		private String getBanlistValue() {
			List<Number> ids;
			String value = getString(this);
			
			try {
				ids = StorageConnector.getInstance().getColumnValues(DataContainer.BANS, "id", Number.class, new WhereCondition("active", WhereOperator.EQUAL, 1));
			} catch (SQLException e) {
				ids = Collections.emptyList();
			} if (ids.isEmpty())
				return value;
			return value.replace("{bans}", Utils.getStringFromList(Utils.integerListToStringList(Utils.numberListToIntegerList(ids)), false, true));
		}
		
		private String formatValue(me.remigio07.chatplugin.api.common.punishment.ban.Ban ban) {
			int id = ban.getID();
			String value = getString(this);
			String whoUnbanned = StorageConnector.getInstance().safeSelect(DataContainer.BANS, "who_unbanned", null, new WhereCondition("id", WhereOperator.EQUAL, id));
			Number unbanDate = StorageConnector.getInstance().safeSelect(DataContainer.BANS, "unban_date", null, new WhereCondition("id", WhereOperator.EQUAL, id));
			Boolean active = StorageConnector.getInstance().safeSelect(DataContainer.BANS, "active", null, new WhereCondition("id", WhereOperator.EQUAL, id));
			long duration = ban.getDuration();
			
			return value
					.replace("{id}", String.valueOf(id))
					.replace("{player}", ban.getPlayer().getName())
					.replace("{player_uuid}", ban.getPlayer().getUUID().toString())
					.replace("{ip_address}", ban.getIPAddress() == null ? getString("placeholders.not-present") : ban.getIPAddress().getHostAddress())
					.replace("{staff_member}", ban.getStaffMember())
					.replace("{who_unbanned}", whoUnbanned == null ? getString("placeholders.nobody") : whoUnbanned)
					.replace("{reason}", ban.getReason() == null ? getString("messages.ban.unspecified-reason") : ChatColor.stripColor(ban.getReason()))
					.replace("{server}", ban.getServer())
					.replace("{type}", getFormat("types." + ban.getType().name().toLowerCase()))
					.replace("{date}", formatDate(ban.getDate()))
					.replace("{unban_date}", unbanDate == null ? getString("placeholders.not-present") : formatDate(unbanDate.longValue()))
					.replace("{expiration_date}", duration == -1 ? getString("timestamps.never") : formatDate(ban.getDate() + duration))
					.replace("{duration}", formatTime(duration, true, true))
					.replace("{remaining_time}", formatTime(ban.getRemainingTime(), true, true))
					.replace("{active}", getFormat(active == null ? "active.no" : (active ? "active.yes" : "active.no")))
					.replace("{global}", getFormat(ban.isGlobal() ? "global.yes" : "global.no"))
					.replace("{silent}", getFormat(ban.isSilent() ? "silent.yes" : "silent.no"));
		}
		
		private static String getFormat(String type) {
			return getString("messages.ban.formats." + type);
		}
		
	}
	
	/**
	 * Represents the warning messages.
	 * 
	 * <p><strong>Found at:</strong> "messages.warning" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 */
	public enum Warning implements TelegramMessage {
		
		/**
		 * Represents the warning info message.
		 * 
		 * <p><strong>Found at:</strong> "messages.warning.info" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.warning.Warning.class, descriptions = "Related warning's object")
		INFO,
		
		/**
		 * Represents the warning list message.
		 * 
		 * <p><strong>Found at:</strong> "messages.warning.list" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		LIST,
		
		/**
		 * Represents the warning empty list message.
		 * 
		 * <p><strong>Found at:</strong> "messages.warning.empty-list" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		EMPTY_LIST,
		
		/**
		 * Represents the warning warned message.
		 * 
		 * <p><strong>Found at:</strong> "messages.warning.warned" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.warning.Warning.class, descriptions = "Related warning's object")
		WARNED,
		
		/**
		 * Represents the warning removed message.
		 * 
		 * <p><strong>Found at:</strong> "messages.warning.removed" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.warning.Warning.class, descriptions = "Related warning's object")
		REMOVED,
		
		/**
		 * Represents the warning cleared message.
		 * 
		 * <p><strong>Found at:</strong> "messages.warning.cleared" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = { me.remigio07.chatplugin.api.common.player.OfflinePlayer.class, String.class }, descriptions = { "Player whose warnings got cleared", "Who unwarned the player" })
		CLEARED,
		
		/**
		 * Represents the warning expired message.
		 * 
		 * <p><strong>Found at:</strong> "messages.warning.expired" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.warning.Warning.class, descriptions = "Related warning's object")
		EXPIRED;
		
		@Override
		public String getPath() {
			return "messages.warning." + name().toLowerCase().replace('_', '-');
		}
		
		@Override
		public String getValue(Object... args) {
			switch (this) {
			case LIST:
				return getWarnlistValue();
			case EMPTY_LIST:
				return getString(this);
			case CLEARED:
				return getClearWarningsValues((me.remigio07.chatplugin.api.common.player.OfflinePlayer) args[0], (String) args[1]);
			default:
				return formatValue((me.remigio07.chatplugin.api.common.punishment.warning.Warning) args[0]);
			}
		}
		
		private String getWarnlistValue() {
			List<Number> ids;
			String value = getString(this);
			
			try {
				ids = StorageConnector.getInstance().getColumnValues(DataContainer.WARNINGS, "id", Number.class, new WhereCondition("active", WhereOperator.EQUAL, 1));
			} catch (SQLException e) {
				ids = Collections.emptyList();
			} if (ids.isEmpty())
				return value;
			return value.replace("{warnings}", Utils.getStringFromList(Utils .integerListToStringList(Utils.numberListToIntegerList(ids)), false, true));
		}
		
		private String formatValue(me.remigio07.chatplugin.api.common.punishment.warning.Warning warning) {
			int id = warning.getID();
			String value = getString(this);
			String whoUnwarned = StorageConnector.getInstance().safeSelect(DataContainer.WARNINGS, "who_unwarned", null, new WhereCondition("id", WhereOperator.EQUAL, id));
			Number unwarnDate = StorageConnector.getInstance().safeSelect(DataContainer.WARNINGS, "unwarn_date", null, new WhereCondition("id", WhereOperator.EQUAL, id));
			Boolean active = StorageConnector.getInstance().safeSelect(DataContainer.WARNINGS, "active", null, new WhereCondition("id", WhereOperator.EQUAL, id));
			me.remigio07.chatplugin.api.common.player.OfflinePlayer player = warning.getPlayer();
			
			return value
					.replace("{id}", String.valueOf(id))
					.replace("{player}", player.getName())
					.replace("{player_uuid}", player.getUUID().toString())
					.replace("{staff_member}", warning.getStaffMember())
					.replace("{who_unwarned}", whoUnwarned == null ? getString("placeholders.nobody") : whoUnwarned)
					.replace("{reason}", warning.getReason() == null ? getString("messages.warning.unspecified-reason") : ChatColor.stripColor(warning.getReason()))
					.replace("{server}", warning.getServer())
					.replace("{date}", formatDate(warning.getDate()))
					.replace("{unwarn_date}", unwarnDate == null ? getString("timestamps.never") : formatDate(unwarnDate.longValue()))
					.replace("{expiration_date}", formatDate(warning.getDate() + warning.getDuration()))
					.replace("{duration}", formatTime(warning.getDuration(), true, true))
					.replace("{remaining_time}", formatTime(warning.getRemainingTime(), true, true))
					.replace("{amount}", String.valueOf(WarningManager.getInstance().getActiveWarnings(player, warning.getServer()).stream().filter(other -> other.getDate() <= warning.getDate()).count()))
					.replace("{max_amount}", String.valueOf(ConfigurationType.CONFIG.get().getInt("warning.max-warnings-placeholder." + warning.getServer())))
					.replace("{active}", getFormat(active == null ? "active.no" : (active ? "active.yes" : "active.no")))
					.replace("{global}", getFormat(warning.isSilent() ? "global.yes" : "global.no"))
					.replace("{silent}", getFormat(warning.isSilent() ? "silent.yes" : "silent.no"));
		}
		
		private String getClearWarningsValues(me.remigio07.chatplugin.api.common.player.OfflinePlayer user, String whoUnwarned) {
			return getString(this)
					.replace("{player}", user.getName())
					.replace("{player_uuid}", user.getUUID().toString())
					.replace("{who_unwarned}", whoUnwarned == null ? getString("placeholders.nobody") : whoUnwarned);
		}
		
		private static String getFormat(String type) {
			return getString("messages.warning.formats." + type);
		}
		
	}
	
	/**
	 * Represents the kick messages.
	 * 
	 * <p><strong>Found at:</strong> "messages.kick" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 */
	public enum Kick implements TelegramMessage {
		
		/**
		 * Represents the kick info message.
		 * 
		 * <p><strong>Found at:</strong> "messages.kick.info" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.kick.Kick.class, descriptions = "Related kick's object")
		INFO,
		
		/**
		 * Represents the kick kicked message.
		 * 
		 * <p><strong>Found at:</strong> "messages.kick.kicked" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.kick.Kick.class, descriptions = "Related kick's object")
		KICKED;
		
		@Override
		public String getPath() {
			return "messages.kick." + name().toLowerCase().replace('_', '-');
		}
		
		@Override
		public String getValue(Object... args) {
			return formatValue((me.remigio07.chatplugin.api.common.punishment.kick.Kick) args[0]);
		}
		
		private String formatValue(me.remigio07.chatplugin.api.common.punishment.kick.Kick kick) {
			String value = getString(this);
			me.remigio07.chatplugin.api.common.player.OfflinePlayer player = kick.getPlayer();
			
			return value
					.replace("{id}", String.valueOf(kick.getID()))
					.replace("{player}", player.getName())
					.replace("{player_uuid}", player.getUUID().toString())
					.replace("{ip_address}", kick.getIPAddress() == null ? getString("placeholders.not-present") : kick.getIPAddress().getHostAddress())
					.replace("{staff_member}", kick.getStaffMember())
					.replace("{reason}", kick.getReason() == null ? getString("messages.kick.unspecified-reason") : ChatColor.stripColor(kick.getReason()))
					.replace("{server}", kick.getServer())
					.replace("{type}", getFormat("types." + kick.getType().name().toLowerCase()))
					.replace("{date}", formatDate(kick.getDate()))
					.replace("{silent}", getFormat(kick.isSilent() ? "silent.yes" : "silent.no"));
		}
		
		private static String getFormat(String type) {
			return getString("messages.kick.formats." + type);
		}
		
	}
	
	/**
	 * Represents the mute messages.
	 * 
	 * <p><strong>Found at:</strong> "messages.mute" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
	 */
	public enum Mute implements TelegramMessage {
		
		/**
		 * Represents the mute info message.
		 * 
		 * <p><strong>Found at:</strong> "messages.mute.info" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.mute.Mute.class, descriptions = "Related mute's object")
		INFO,
		
		/**
		 * Represents the mute list message.
		 * 
		 * <p><strong>Found at:</strong> "messages.mute.list" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		LIST,
		
		/**
		 * Represents the mute empty list message.
		 * 
		 * <p><strong>Found at:</strong> "messages.mute.empty-list" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		EMPTY_LIST,
		
		/**
		 * Represents the mute muted message.
		 * 
		 * <p><strong>Found at:</strong> "messages.mute.muted" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.mute.Mute.class, descriptions = "Related mute's object")
		MUTED,
		
		/**
		 * Represents the mute updated message.
		 * 
		 * <p><strong>Found at:</strong> "messages.mute.updated" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.mute.Mute.class, descriptions = "Related mute's object")
		UPDATED,
		
		/**
		 * Represents the mute unmuted message.
		 * 
		 * <p><strong>Found at:</strong> "messages.mute.unmuted" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.mute.Mute.class, descriptions = "Related mute's object")
		UNMUTED,
		
		/**
		 * Represents the mute expired message.
		 * 
		 * <p><strong>Found at:</strong> "messages.mute.expired" in {@link ConfigurationType#TELEGRAM_INTEGRATION}</p>
		 */
		@MessageArguments(types = me.remigio07.chatplugin.api.common.punishment.mute.Mute.class, descriptions = "Related mute's object")
		EXPIRED;
		
		@Override
		public String getPath() {
			return "messages.mute." + name().toLowerCase().replace('_', '-');
		}
		
		@Override
		public String getValue(Object... args) {
			return name().endsWith("LIST") ? this == LIST ? getMutelistValue() : getString(this) : formatValue((me.remigio07.chatplugin.api.common.punishment.mute.Mute) args[0]);
		}
		
		private String getMutelistValue() {
			List<Number> ids;
			String value = getString(this);
			
			try {
				ids = StorageConnector.getInstance().getColumnValues(DataContainer.MUTES, "id", Number.class, new WhereCondition("active", WhereOperator.EQUAL, 1));
			} catch (SQLException e) {
				ids = Collections.emptyList();
			} if (ids.isEmpty())
				return value;
			return value.replace("{mutes}", Utils.getStringFromList(Utils.integerListToStringList(Utils.numberListToIntegerList(ids)), false, true));
		}
		
		private String formatValue(me.remigio07.chatplugin.api.common.punishment.mute.Mute mute) {
			int id = mute.getID();
			String value = getString(this);
			String whoUnmuted = StorageConnector.getInstance().safeSelect(DataContainer.WARNINGS, "who_unmuted", null, new WhereCondition("id", WhereOperator.EQUAL, id));
			Number unmuteDate = StorageConnector.getInstance().safeSelect(DataContainer.WARNINGS, "unmute_date", null, new WhereCondition("id", WhereOperator.EQUAL, id));
			Boolean active = StorageConnector.getInstance().safeSelect(DataContainer.WARNINGS, "active", null, new WhereCondition("id", WhereOperator.EQUAL, id));
			me.remigio07.chatplugin.api.common.player.OfflinePlayer player = mute.getPlayer();
			long duration = mute.getDuration();
			
			return value
					.replace("{id}", String.valueOf(id))
					.replace("{player}", player.getName())
					.replace("{player_uuid}", player.getUUID().toString())
					.replace("{staff_member}", mute.getStaffMember())
					.replace("{who_unmuted}", whoUnmuted == null ? getString("placeholders.nobody") : whoUnmuted)
					.replace("{reason}", mute.getReason() == null ? getString("messages.mute.unspecified-reason") : ChatColor.stripColor(mute.getReason()))
					.replace("{server}", mute.getServer())
					.replace("{date}", formatDate(mute.getDate()))
					.replace("{unmute_date}", unmuteDate == null ? getString("timestamps.never") : formatDate(unmuteDate.longValue()))
					.replace("{expiration_date}", duration == -1 ? getString("timestamps.never") : formatDate(mute.getDate() + duration))
					.replace("{duration}", formatTime(duration, true, true))
					.replace("{remaining_time}", formatTime(mute.getRemainingTime(), true, true))
					.replace("{active}", getFormat(active == null ? "active.no" : (active ? "active.yes" : "active.no")))
					.replace("{global}", getFormat(mute.isGlobal() ? "global.yes" : "global.no"))
					.replace("{silent}", getFormat(mute.isSilent() ? "silent.yes" : "silent.no"));
		}
		
		private static String getFormat(String type) {
			return getString("messages.mute.formats." + type);
		}
		
	}
	
	private static String getString(String path) {
		return ConfigurationType.TELEGRAM_INTEGRATION.get().getString(path);
	}
	
	private static String getString(TelegramMessage message) {
		return getString(message.getPath() + (ConfigurationType.TELEGRAM_INTEGRATION.get().contains(message.getPath() + ".enabled") ? ".value" : ""));
	}
	
	private static String formatTime(long totalMilliseconds, boolean everInsteadOfNever, boolean useZeroSecondsInstead) {
		StringBuilder sb = new StringBuilder();
		long totalSeconds = (totalMilliseconds + 999) / 1000L;
		
		if (totalMilliseconds == -1)
			return getString(everInsteadOfNever ? "timestamps.ever" : "timestamps.never");
		if (totalSeconds < 1 && !useZeroSecondsInstead)
			return getString("timestamps.now");
		int years = (int) (totalSeconds / Utils.SECONDS_IN_A_YEAR);
		totalSeconds -= years * Utils.SECONDS_IN_A_YEAR;
		int months = (int) (totalSeconds / Utils.SECONDS_IN_A_MONTH);
		totalSeconds -= months * Utils.SECONDS_IN_A_MONTH;
		int days = (int) (totalSeconds / Utils.SECONDS_IN_A_DAY);
		totalSeconds -= days * Utils.SECONDS_IN_A_DAY;
		int weeks = 0;
		
		if (ConfigurationType.CONFIG.get().getBoolean("settings.use-week-timestamp")) {
			weeks = (int) (totalSeconds / Utils.SECONDS_IN_A_WEEK);
			totalSeconds -= weeks * Utils.SECONDS_IN_A_WEEK;
		} int hours = (int) (totalSeconds / Utils.SECONDS_IN_AN_HOUR);
		totalSeconds -= hours * Utils.SECONDS_IN_AN_HOUR;
		int minutes = (int) (totalSeconds / Utils.SECONDS_IN_A_MINUTE);
		totalSeconds -= minutes * Utils.SECONDS_IN_A_MINUTE;
		
		if (years != 0)
			sb.append(years + (years == 1 ? "{year}, " : "{years}, "));
		if (months != 0)
			sb.append(months + (months == 1 ? "{month}, " : "{months}, "));
		if (weeks != 0)
			sb.append(weeks + (weeks == 1 ? "{week}, " : "{weeks}, "));
		if (days != 0)
			sb.append(days + (days == 1 ? "{day}, " : "{days}, "));
		if (hours != 0)
			sb.append(hours + (hours == 1 ? "{hour}, " : "{hours}, "));
		if (minutes != 0)
			sb.append(minutes + (minutes == 1 ? "{minute}, " : "{minutes}, "));
		if (totalSeconds != 0 || (useZeroSecondsInstead && minutes == 0 && hours == 0 && days == 0 && weeks == 0 && months == 0 && years == 0))
			sb.append(totalSeconds + (totalSeconds == 1 ? "{second}" : "{seconds}"));
		else sb.delete(sb.length() - 2, sb.length());
		
		return sb.toString()
				.replace("{second}", getString("timestamps.second"))
				.replace("{seconds}", getString("timestamps.seconds"))
				.replace("{minute}", getString("timestamps.minute"))
				.replace("{minutes}", getString("timestamps.minutes"))
				.replace("{hour}", getString("timestamps.hour"))
				.replace("{hours}", getString("timestamps.hours"))
				.replace("{day}", getString("timestamps.day"))
				.replace("{days}", getString("timestamps.days"))
				.replace("{week}", getString("timestamps.week"))
				.replace("{weeks}", getString("timestamps.weeks"))
				.replace("{month}", getString("timestamps.month"))
				.replace("{months}", getString("timestamps.months"))
				.replace("{year}", getString("timestamps.year"))
				.replace("{years}", getString("timestamps.years"));
	}
	
	private static String formatDate(long ms) {
		return new SimpleDateFormat(getString("simple-date-format")).format(new Date(ms));
	}
	
	/**
	 * Annotation used to describe the arguments of a {@link TelegramMessage}'s
	 * {@link TelegramMessage#getValue(Object...)} method.
	 */
	@Target(FIELD)
	public @interface MessageArguments {
		
		/**
		 * Gets the message's arguments' types.
		 * 
		 * @return Message's arguments' types
		 */
		public Class<?>[] types();
		
		/**
		 * Gets the message's arguments' descriptions.
		 * 
		 * @return Message's arguments' descriptions
		 */
		public String[] descriptions();
		
	}
	
}
