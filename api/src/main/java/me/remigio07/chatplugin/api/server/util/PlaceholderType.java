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

package me.remigio07.chatplugin.api.server.util;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

/**
 * Represents all ChatPlugin's integrated placeholders plus PlaceholderAPI and MVdWPlaceholderAPI extensions.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Placeholders">ChatPlugin wiki/Modules/Placeholders</a>
 * @see PlaceholderManager
 */
public enum PlaceholderType {
	
	/**
	 * Will translate only the {player} placeholder.
	 */
	JUST_NAME(new String[] { "player" }),
	
	/**
	 * Represents player-related placeholders.
	 */
	PLAYER(new String[] {
			"player", "uuid", "display_name", "ip_address", "health", "max_health", "food", "level", "xp", "gamemode",
			"language_id", "language_display_name", "locale", "version", "version_protocol", "client_edition", "last_login", "time_played", "emojis_tone",
			"world", "online_world", "vanished_world",
			"ping", "ping_format", "ping_quality_color", "ping_quality_text",
			"player_id", "player_bans", "player_warnings", "player_kicks", "player_mutes", "messages_sent", "player_anticheat_bans", "player_anticheat_warnings", "player_anticheat_kicks", "player_anticheat_mutes",
			"x", "y", "z", "yaw", "pitch",
			"rank_id", "rank_display_name", "prefix", "suffix", "tag_prefix", "tag_suffix", "tag_name_color", "chat_color", "rank_description",
			"isp", "continent", "country", "subdivisions", "city", "country_code", "inside_eu", "time_zone", "postal_code", "latitude", "longitude", "accuracy_radius_km", "accuracy_radius_mi", "accuracy_radius_nm", "relative_date_full", "relative_date_day", "relative_date_hour"
			}),
	
	/**
	 * Represents server-side placeholders.
	 */
	SERVER(new String[] {
			"online", "online@server-id", "online_total", "max_players", "vanished", "vanished@server-id",
			"date_full", "date_day", "date_hour",
			"enabled_worlds", "enabled_players", "enabled_managers", "startup_time", "last_reload_time", "uptime", "random_color",
			"plugin_version", "server_version", "server_version_protocol", "server_nms_version", "server_java_version",
			"server_id", "server_display_name", "main_language_id", "main_language_display_name",
			"total_players", "total_bans", "total_warnings", "total_kicks", "total_mutes", "total_staff_bans", "total_staff_warnings", "total_staff_kicks", "total_staff_mutes", "total_anticheat_bans", "total_anticheat_warnings", "total_anticheat_kicks", "total_anticheat_mutes",
			"max_memory", "total_memory", "used_memory", "free_memory", "total_storage", "used_storage", "free_storage", "unallocated_storage",
			"tps_1_min", "tps_5_min", "tps_15_min", "tps_1_min_format", "tps_5_min_format", "tps_15_min_format",
			"mspt_5_sec_avg", "mspt_5_sec_min", "mspt_5_sec_max", "mspt_10_sec_avg", "mspt_10_sec_min", "mspt_10_sec_max", "mspt_1_min_avg", "mspt_1_min_min", "mspt_1_min_max", "mspt_5_sec_avg_format", "mspt_5_sec_min_format", "mspt_5_sec_max_format", "mspt_10_sec_avg_format", "mspt_10_sec_min_format", "mspt_10_sec_max_format", "mspt_1_min_avg_format", "mspt_1_min_min_format", "mspt_1_min_max_format",
			"server_os_name", "server_os_arch", "server_os_version", "cpu_threads", "active_threads",
			"discord_punishments_channel_id", "discord_staff_notifications_channel_id"
			}),
	
	/**
	 * Represents integrations' placeholders (includes also PlaceholderAPI's and MVdWPlaceholderAPI's).
	 */
	INTEGRATIONS(new String[] { "version", "version_protocol", "balance" });
	
	private String[] placeholders;
	
	private PlaceholderType(String[] placeholders) {
		this.placeholders = placeholders;
	}
	
	/**
	 * Gets the list of available placeholders for this placeholder type.
	 * 
	 * <p><strong>Note:</strong> if <code>this == {@link #INTEGRATIONS}</code> an
	 * array with just the integrated placeholders will be returned, even if this
	 * includes all the placeholders from PlaceholderAPI and MVdWPlaceholderAPI.</p>
	 * 
	 * @return Array of available placeholders
	 */
	public String[] getPlaceholders() {
		return placeholders;
	}
	
	/**
	 * Gets the type of the specified placeholder.
	 * 
	 * <p>Will return <code>null</code> if the
	 * placeholder is not handled by ChatPlugin.</p>
	 * 
	 * @param placeholder Placeholder to check, without {brackets} or %percentages%
	 * @return Placeholder's type
	 */
	@Nullable(why = "Specified placeholder may be invalid")
	public static PlaceholderType getType(String placeholder) {
		if (placeholder.startsWith("online@") || placeholder.startsWith("vanished@"))
			return SERVER;
		for (PlaceholderType type : values())
			for (String identifier : type.getPlaceholders())
				if (placeholder.equals(identifier))
					return type;
		return null;
	}
	
	/**
	 * Gets placeholder types from an input
	 * String list, ignoring invalid ones.
	 * 
	 * @param types Placeholder types' {@link #name()}s, ignoring case
	 * @return Placeholder types
	 */
	@NotNull
	public static Set<PlaceholderType> getTypes(List<String> types) {
		Set<PlaceholderType> set = EnumSet.noneOf(PlaceholderType.class);
		
		for (String type : types)
			try {
				set.add(valueOf(type.toUpperCase()));
			} catch (IllegalArgumentException iae) {
				
			}
		return set;
	}
	
	/**
	 * Counts the supported placeholders.
	 * 
	 * @return Placeholders' count
	 */
	public static int count() {
		return PLAYER.getPlaceholders().length + SERVER.getPlaceholders().length + INTEGRATIONS.getPlaceholders().length;
	}
	
}
