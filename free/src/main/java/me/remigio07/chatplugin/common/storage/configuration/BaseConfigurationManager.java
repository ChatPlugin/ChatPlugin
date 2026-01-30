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

package me.remigio07.chatplugin.common.storage.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;

public abstract class BaseConfigurationManager extends ConfigurationManager {
	
	@Override
	public Configuration createConfiguration(Path path) {
		return new ConfigurationImpl(path);
	}
	
	public void addDiscordIntegrationDefaults(boolean forceAdd) throws IOException {
		Configuration discordIntegration = configurations.get(ConfigurationType.DISCORD_INTEGRATION);
		
		if (!Files.exists(discordIntegration.getPath()))
			discordIntegration.createFile();
		else if (!forceAdd)
			return;
		
		discordIntegration.addDefault("settings.enabled", false);
		discordIntegration.addDefault("settings.guild-id", 0L);
		discordIntegration.addDefault("settings.application-id", 0L);
		discordIntegration.addDefault("settings.token", "");
		discordIntegration.addDefault("settings.channels-ids.punishments", 0L);
		discordIntegration.addDefault("settings.channels-ids.staff-notifications", 0L);
		discordIntegration.addDefault("settings.status.value", "with {online_minecraft} other players.");
		discordIntegration.addDefault("settings.status.activity-type", "PLAYING");
		discordIntegration.addDefault("settings.status.update-timeout-ms", 10000L);
		
		path = "messages.main.";
		
		discordIntegration.addDefault(path + "help.title.text", "Help for ChatPlugin");
		discordIntegration.addDefault(path + "help.description", "Click [here](https://remigio07.me/chatplugin/wiki/modules/Integrations#commands) to visit the wiki with the commands list.");
		discordIntegration.addDefault(path + "help.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/help-thumbnail.png");
		discordIntegration.addDefault(path + "help.color", "0000AA");
		discordIntegration.addDefault(path + "info.title.text", "Info and contacts for ChatPlugin");
		discordIntegration.addDefault(path + "info.description", "**Website:** https://remigio07.me/chatplugin\n**GitHub:** https://github.com/ChatPlugin/ChatPlugin\n**Discord:** https://discord.gg/eSnAPhvMTG");
		discordIntegration.addDefault(path + "info.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/info-thumbnail.png");
		discordIntegration.addDefault(path + "info.color", "0000AA");
		discordIntegration.addDefault(path + "reload-start.title.text", "Reload started");
		discordIntegration.addDefault(path + "reload-start.description", "ChatPlugin's Discord bot is reloading...");
		discordIntegration.addDefault(path + "reload-start.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/reload-start-thumbnail.png");
		discordIntegration.addDefault(path + "reload-start.color", "FFFF55");
		discordIntegration.addDefault(path + "reload-end.title.text", "Reloaded successfully");
		discordIntegration.addDefault(path + "reload-end.description", "The bot has been reloaded. Took **{last_reload_time} ms** to complete.");
		discordIntegration.addDefault(path + "reload-end.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/reload-end-thumbnail.png");
		discordIntegration.addDefault(path + "reload-end.color", "00AA00");
		discordIntegration.addDefault(path + "status.title.text", "Current server status");
		discordIntegration.addDefault(path + "status.description", "**OS:** {os_name} {os_version}, **Java:** {java_version}\n**Environment:** {environment} {environment_version}\n**ChatPlugin:** {chatplugin_version}, **JDA:** {jda_version}\n**Uptime:** {uptime}\n**Used memory:** {used_memory}/{max_memory} MB\n**Allocated:** {total_memory} MB, **free:** {free_memory} MB\n**Current threads count:** {active_threads}x\n**Used storage:** {used_storage}/{total_storage} GB\n**Free storage:** {free_storage} GB\n**Enabled players:** {enabled_players}x\n**Startup:** {startup_time} ms, **last reload:** {last_reload_time} ms");
		discordIntegration.addDefault(path + "status.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/status-thumbnail.png");
		discordIntegration.addDefault(path + "status.color", "55FF55");
		discordIntegration.addDefault(path + "version.title.text", "Current plugin version");
		discordIntegration.addDefault(path + "version.description", "**ChatPlugin version:** {chatplugin_version}\n**JDA version:** {jda_version}");
		discordIntegration.addDefault(path + "version.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/version-thumbnail.png");
		discordIntegration.addDefault(path + "version.color", "55FF55");
		
		path = "messages.ban.";
		
		discordIntegration.addDefault(path + "info.title.text", "Ban **#{id}**: {player}");
		discordIntegration.addDefault(path + "info.description", "**Staff member:** {staff_member}\n**Who unbanned:** {who_unbanned}\n**Reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Duration:** {duration}\n**Remaining time:** {remaining_time}\n**Unban date:** {unban_date}\n**Type:** {type}\n**Active:** {active}\n**Scope:** {global}");
		discordIntegration.addDefault(path + "info.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "info.color", "AA0000");
		discordIntegration.addDefault(path + "list.title.text", "Banlist");
		discordIntegration.addDefault(path + "list.description", "**Active bans' IDs:** {bans}.");
		discordIntegration.addDefault(path + "list.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/banlist-thumbnail.png");
		discordIntegration.addDefault(path + "list.color", "AA0000");
		discordIntegration.addDefault(path + "empty-list.title.text", "Empty list");
		discordIntegration.addDefault(path + "empty-list.description", "There are no active bans.");
		discordIntegration.addDefault(path + "empty-list.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/empty-list-thumbnail.png");
		discordIntegration.addDefault(path + "empty-list.color", "55FF55");
		discordIntegration.addDefault(path + "banned.enabled", true);
		discordIntegration.addDefault(path + "banned.title.text", "Ban **#{id}**: {player}");
		discordIntegration.addDefault(path + "banned.description", "**Staff member:** {staff_member}\n**Reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Duration:** {duration}\n**Type:** {type}\n**Scope:** {global}");
		discordIntegration.addDefault(path + "banned.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "banned.color", "AA0000");
		discordIntegration.addDefault(path + "updated.enabled", true);
		discordIntegration.addDefault(path + "updated.title.text", "Ban **#{id}** updated: {player}");
		discordIntegration.addDefault(path + "updated.description", "**Staff member:** {staff_member}\n**Reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Duration:** {duration}\n**Type:** {type}\n**Scope:** {global}");
		discordIntegration.addDefault(path + "updated.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "updated.color", "AA0000");
		discordIntegration.addDefault(path + "unbanned.enabled", true);
		discordIntegration.addDefault(path + "unbanned.title.text", "Unban **#{id}**: {player}");
		discordIntegration.addDefault(path + "unbanned.description", "**Staff member:** {who_unbanned}\n**Date:** {date}");
		discordIntegration.addDefault(path + "unbanned.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "unbanned.color", "55FF55");
		discordIntegration.addDefault(path + "expired.enabled", true);
		discordIntegration.addDefault(path + "expired.title.text", "Ban **#{id}** expired: {player}");
		discordIntegration.addDefault(path + "expired.description", "**Date:** {date}");
		discordIntegration.addDefault(path + "expired.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "expired.color", "55FF55");
		discordIntegration.addDefault(path + "unspecified-reason", "Reason not specified.");
		discordIntegration.addDefault(path + "formats.types.account", "username/UUID");
		discordIntegration.addDefault(path + "formats.types.ip", "IP address");
		discordIntegration.addDefault(path + "formats.active.yes", "active");
		discordIntegration.addDefault(path + "formats.active.no", "disactive");
		discordIntegration.addDefault(path + "formats.global.yes", "global");
		discordIntegration.addDefault(path + "formats.global.no", "local");
		discordIntegration.addDefault(path + "formats.silent.yes", "yes");
		discordIntegration.addDefault(path + "formats.silent.no", "no");
		
		path = "messages.warning.";
		
		discordIntegration.addDefault(path + "info.title.text", "Warning **#{id}**: {player}");
		discordIntegration.addDefault(path + "info.description", "**Staff member:** {staff_member}\n**Who unwarned:** {who_unwarned}\n**Last reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Duration:** {duration}\n**Remaining time:** {remaining_time}\n**Unwarn date:** {unwarn_date}\n**Warnings:** {amount}/{max_warnings}\n**Active:** {active}\n**Scope:** {global}");
		discordIntegration.addDefault(path + "info.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "info.color", "FF5555");
		discordIntegration.addDefault(path + "list.title.text", "Warnlist");
		discordIntegration.addDefault(path + "list.description", "**Active warnings' IDs:** {warnings}.");
		discordIntegration.addDefault(path + "list.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/warnlist-thumbnail.png");
		discordIntegration.addDefault(path + "list.color", "FF5555");
		discordIntegration.addDefault(path + "empty-list.title.text", "Empty list");
		discordIntegration.addDefault(path + "empty-list.description", "There are no active warnings.");
		discordIntegration.addDefault(path + "empty-list.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/empty-list-thumbnail.png");
		discordIntegration.addDefault(path + "empty-list.color", "55FF55");
		discordIntegration.addDefault(path + "warned.enabled", true);
		discordIntegration.addDefault(path + "warned.title.text", "Warning **#{id}**: {player}");
		discordIntegration.addDefault(path + "warned.description", "**Staff member:** {staff_member}\n**Last reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Duration:** {duration}\n**Warnings:** {amount}/{max_warnings}\n**Scope:** {global}");
		discordIntegration.addDefault(path + "warned.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "warned.color", "FF5555");
		discordIntegration.addDefault(path + "removed.enabled", true);
		discordIntegration.addDefault(path + "removed.title.text", "Warning **#{id}**: {player}");
		discordIntegration.addDefault(path + "removed.description", "**Staff member:** {staff_member}\n**Date:** {date}");
		discordIntegration.addDefault(path + "removed.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "removed.color", "AA0000");
		discordIntegration.addDefault(path + "cleared.enabled", true);
		discordIntegration.addDefault(path + "cleared.title.text", "Cleared **{player}**'s warnings");
		discordIntegration.addDefault(path + "cleared.description", "**Staff member:** {staff_member}\n**Date:** {date}");
		discordIntegration.addDefault(path + "cleared.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "cleared.color", "AA0000");
		discordIntegration.addDefault(path + "expired.enabled", true);
		discordIntegration.addDefault(path + "expired.title.text", "Warning **#{id}** expired: {player}");
		discordIntegration.addDefault(path + "expired.description", "**Date:** {date}");
		discordIntegration.addDefault(path + "expired.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "expired.color", "AA0000");
		discordIntegration.addDefault(path + "unspecified-reason", "Reason not specified.");
		discordIntegration.addDefault(path + "formats.active.yes", "active");
		discordIntegration.addDefault(path + "formats.active.no", "disactive");
		discordIntegration.addDefault(path + "formats.global.yes", "global");
		discordIntegration.addDefault(path + "formats.global.no", "local");
		discordIntegration.addDefault(path + "formats.silent.yes", "yes");
		discordIntegration.addDefault(path + "formats.silent.no", "no");
		
		path = "messages.kick.";
		
		discordIntegration.addDefault(path + "info.title.text", "Kick **#{id}**: {player}");
		discordIntegration.addDefault(path + "info.description", "**Staff member:** {staff_member}\n**Reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Type:** {type}");
		discordIntegration.addDefault(path + "info.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "info.color", "FFAA00");
		discordIntegration.addDefault(path + "kicked.enabled", true);
		discordIntegration.addDefault(path + "kicked.title.text", "Kick **#{id}**: {player}");
		discordIntegration.addDefault(path + "kicked.description", "**Staff member:** {staff_member}\n**Reason:** {reason}\n**Server:** {server}\n**Date:** {date}");
		discordIntegration.addDefault(path + "kicked.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "kicked.color", "FFAA00");
		discordIntegration.addDefault(path + "unspecified-reason", "Reason not specified.");
		discordIntegration.addDefault(path + "formats.silent.yes", "yes");
		discordIntegration.addDefault(path + "formats.silent.no", "no");
		
		path = "messages.mute.";
		
		discordIntegration.addDefault(path + "info.title.text", "Mute **#{id}**: {player}");
		discordIntegration.addDefault(path + "info.description", "**Staff member:** {staff_member}\n**Who unmuted:** {who_unmuted}\n**Reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Duration:** {duration}\n**Remaining time:** {remaining_time}\n**Unmute date:** {unmute_date}\n**Active:** {active}\n**Scope:** {global}");
		discordIntegration.addDefault(path + "info.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "info.color", "FFFF55");
		discordIntegration.addDefault(path + "list.title.text", "Mutelist");
		discordIntegration.addDefault(path + "list.description", "**Active mutes' IDs:** {mutes}.");
		discordIntegration.addDefault(path + "list.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/mutelist-thumbnail.png");
		discordIntegration.addDefault(path + "list.color", "FFFF55");
		discordIntegration.addDefault(path + "empty-list.title.text", "Empty list");
		discordIntegration.addDefault(path + "empty-list.description", "There are no active mutes.");
		discordIntegration.addDefault(path + "empty-list.thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/empty-list-thumbnail.png");
		discordIntegration.addDefault(path + "empty-list.color", "55FF55");
		discordIntegration.addDefault(path + "muted.enabled", true);
		discordIntegration.addDefault(path + "muted.title.text", "Mute **#{id}**: {player}");
		discordIntegration.addDefault(path + "muted.description", "**Staff member:** {staff_member}\n**Reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Duration:** {duration}\n**Scope:** {global}");
		discordIntegration.addDefault(path + "muted.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "muted.color", "FFFF55");
		discordIntegration.addDefault(path + "updated.enabled", true);
		discordIntegration.addDefault(path + "updated.title.text", "Mute **#{id}** updated: {player}");
		discordIntegration.addDefault(path + "updated.description", "**Staff member:** {staff_member}\n**Reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Duration:** {duration}\n**Scope:** {global}");
		discordIntegration.addDefault(path + "updated.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "updated.color", "FFFF55");
		discordIntegration.addDefault(path + "unmuted.enabled", true);
		discordIntegration.addDefault(path + "unmuted.title.text", "Unmute **#{id}**: {player}");
		discordIntegration.addDefault(path + "unmuted.description", "**Staff member:** {staff_member}\n**Date:** {date}");
		discordIntegration.addDefault(path + "unmuted.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "unmuted.color", "55FF55");
		discordIntegration.addDefault(path + "expired.enabled", true);
		discordIntegration.addDefault(path + "expired.title.text", "Mute **#{id}** expired: {player}");
		discordIntegration.addDefault(path + "expired.description", "**Date:** {date}");
		discordIntegration.addDefault(path + "expired.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "expired.color", "55FF55");
		discordIntegration.addDefault(path + "unspecified-reason", "Reason not specified.");
		discordIntegration.addDefault(path + "formats.active.yes", "active");
		discordIntegration.addDefault(path + "formats.active.no", "disactive");
		discordIntegration.addDefault(path + "formats.global.yes", "global");
		discordIntegration.addDefault(path + "formats.global.no", "local");
		discordIntegration.addDefault(path + "formats.silent.yes", "yes");
		discordIntegration.addDefault(path + "formats.silent.no", "no");
		
		path = "messages.inexistent-id.";
		
		discordIntegration.addDefault(path + "title.text", "Inexistent ID");
		discordIntegration.addDefault(path + "description", "The specified ID does not exist. Try with a different one.");
		discordIntegration.addDefault(path + "thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/error-thumbnail.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.no-permission.";
		
		discordIntegration.addDefault(path + "title.text", "No permission");
		discordIntegration.addDefault(path + "description", "You do not have the permission to execute this command.");
		discordIntegration.addDefault(path + "thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/error-thumbnail.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.guild-only-action.";
		
		discordIntegration.addDefault(path + "title.text", "Guild only action");
		discordIntegration.addDefault(path + "description", "This action can only be performed inside of the configured guild.");
		discordIntegration.addDefault(path + "thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/error-thumbnail.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.disabled-feature.";
		
		discordIntegration.addDefault(path + "title.text", "Disabled feature");
		discordIntegration.addDefault(path + "description", "That feature is disabled. Set it up in the server's config files.");
		discordIntegration.addDefault(path + "thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/error-thumbnail.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.invalid-ip-address.";
		
		discordIntegration.addDefault(path + "title.text", "Invalid IP address");
		discordIntegration.addDefault(path + "description", "The specified IP address is invalid. Try with a different one.");
		discordIntegration.addDefault(path + "thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/error-thumbnail.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.at-least-one-online.";
		
		discordIntegration.addDefault(path + "title.text", "No players online");
		discordIntegration.addDefault(path + "description", "At least one player online is required to perform this action.");
		discordIntegration.addDefault(path + "thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/error-thumbnail.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.ip-lookup.";
		
		discordIntegration.addDefault(path + "title.text", "IP lookup of {ip_address}");
		discordIntegration.addDefault(path + "title.url", "https://www.maxmind.com/en/geoip2-precision-demo");
		discordIntegration.addDefault(path + "description", "**ISP:** {isp}\n**Country:** {country}, {continent}\n**Subdivision(s):** {subdivisions}\n**City:** {city} (postal code: {postal_code})\n**Coords:** {latitude}° {longitude}°\n**Accuracy radius:** ~{accuracy_radius_km} km");
		discordIntegration.addDefault(path + "thumbnail", "https://remigio07.me/chatplugin/assets/discord-integration/ip-lookup-thumbnail.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		discordIntegration.addDefault("timestamps.now", "now");
		discordIntegration.addDefault("timestamps.ever", "ever");
		discordIntegration.addDefault("timestamps.never", "never");
		discordIntegration.addDefault("timestamps.second", " second");
		discordIntegration.addDefault("timestamps.seconds", " seconds");
		discordIntegration.addDefault("timestamps.minute", " minute");
		discordIntegration.addDefault("timestamps.minutes", " minutes");
		discordIntegration.addDefault("timestamps.hour", " hour");
		discordIntegration.addDefault("timestamps.hours", " hours");
		discordIntegration.addDefault("timestamps.day", " day");
		discordIntegration.addDefault("timestamps.days", " days");
		discordIntegration.addDefault("timestamps.month", " month");
		discordIntegration.addDefault("timestamps.months", " months");
		discordIntegration.addDefault("timestamps.year", " year");
		discordIntegration.addDefault("timestamps.years", " years");
		
		discordIntegration.addDefault("placeholders.nobody", "nobody");
		discordIntegration.addDefault("placeholders.not-present", "not present");
		
		discordIntegration.addDefault("commands.chatplugin.description", "ChatPlugin's main command.");
		discordIntegration.addDefault("commands.chatplugin.args-descriptions.sub-command", "Sub-command for the main command");
		discordIntegration.addDefault("commands.baninfo.description", "Show information about a ban.");
		discordIntegration.addDefault("commands.baninfo.args-descriptions.id", "The ban's ID");
		discordIntegration.addDefault("commands.warninginfo.description", "Show information about a warning.");
		discordIntegration.addDefault("commands.warninginfo.args-descriptions.id", "The warning's ID");
		discordIntegration.addDefault("commands.kickinfo.description", "Show information about a kick.");
		discordIntegration.addDefault("commands.kickinfo.args-descriptions.id", "The kick's ID");
		discordIntegration.addDefault("commands.muteinfo.description", "Show information about a mute.");
		discordIntegration.addDefault("commands.muteinfo.args-descriptions.id", "The mute's ID");
		discordIntegration.addDefault("commands.banlist.description", "Show the active bans list.");
		discordIntegration.addDefault("commands.warnlist.description", "Show the active warnings list.");
		discordIntegration.addDefault("commands.mutelist.description", "Show the active mutes list.");
		discordIntegration.addDefault("commands.iplookup.description", "Perform an IP address lookup.");
		discordIntegration.addDefault("commands.iplookup.args-descriptions.ip-address", "The IP address to check");
		
		discordIntegration.save();
	}
	
	public void addTelegramIntegrationDefaults(boolean forceAdd) throws IOException {
		Configuration telegramIntegration = configurations.get(ConfigurationType.TELEGRAM_INTEGRATION);
		
		if (!Files.exists(telegramIntegration.getPath()))
			telegramIntegration.createFile();
		else if (!forceAdd)
			return;
		
		telegramIntegration.addDefault("settings.enabled", false);
		telegramIntegration.addDefault("settings.chat-id", 0L);
		telegramIntegration.addDefault("settings.username", "");
		telegramIntegration.addDefault("settings.token", "");
		telegramIntegration.addDefault("settings.status.value", "Playing with {online_minecraft} other players.");
		telegramIntegration.addDefault("settings.status.update-timeout-ms", 30000L);
		
		telegramIntegration.addDefault("messages.main.help", "❔ Help for ChatPlugin\n\nClick <a href=\"https://remigio07.me/chatplugin/wiki/modules/Integrations#commands-1\">here</a> to visit the wiki with the commands list.");
		telegramIntegration.addDefault("messages.main.info", "ℹ Info and contacts for ChatPlugin\n\n<strong>Website:</strong> https://remigio07.me/chatplugin\n<strong>GitHub:</strong> https://github.com/ChatPlugin/ChatPlugin\n<strong>Discord:</strong> https://discord.gg/eSnAPhvMTG");
		telegramIntegration.addDefault("messages.main.status", "ℹ Current server status\n\n<strong>OS:</strong> {os_name} {os_version}, <strong>Java:</strong> {java_version}\n<strong>Environment:</strong> {environment} {environment_version}\n<strong>ChatPlugin:</strong> {chatplugin_version}, <strong>Java Telegram Bot API version:</strong> {java_telegram_bot_api_version}\n<strong>Uptime:</strong> {uptime}\n<strong>Used memory:</strong> {used_memory}/{max_memory} MB\n<strong>Allocated:</strong> {total_memory} MB, <strong>free:</strong> {free_memory} MB\n<strong>Current threads count:</strong> {active_threads}x\n<strong>Used storage:</strong> {used_storage}/{total_storage} GB\n<strong>Free storage:</strong> {free_storage} GB\n<strong>Enabled players:</strong> {enabled_players}x\n<strong>Startup:</strong> {startup_time} ms, <strong>last reload:</strong> {last_reload_time} ms");
		telegramIntegration.addDefault("messages.main.version", "⚙ Current plugin version\n\n<strong>ChatPlugin version:</strong> {chatplugin_version}\n<strong>Java Telegram Bot API version:</strong> {java_telegram_bot_api_version}");
		
		path = "messages.ban.";
		
		telegramIntegration.addDefault(path + "info", "⛔ Ban <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Who unbanned:</strong> {who_unbanned}\n<strong>Reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}\n<strong>Duration:</strong> {duration}\n<strong>Remaining time:</strong> {remaining_time}\n<strong>Unban date:</strong> {unban_date}\n<strong>Type:</strong> {type}\n<strong>Active:</strong> {active}\n<strong>Scope:</strong> {global}");
		telegramIntegration.addDefault(path + "list", "⛔ Banlist\n\n<strong>Active bans' IDs:</strong> {bans}.");
		telegramIntegration.addDefault(path + "empty-list", "✅ Empty list\n\nThere are no active bans.");
		telegramIntegration.addDefault(path + "banned.enabled", true);
		telegramIntegration.addDefault(path + "banned.value", "⛔ Ban <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}\n<strong>Duration:</strong> {duration}\n<strong>Type:</strong> {type}\n<strong>Scope:</strong> {global}");
		telegramIntegration.addDefault(path + "updated.enabled", true);
		telegramIntegration.addDefault(path + "updated.value", "⛔ Ban <strong>#{id}</strong> updated: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}\n<strong>Duration:</strong> {duration}\n<strong>Type:</strong> {type}\n<strong>Scope:</strong> {global}");
		telegramIntegration.addDefault(path + "unbanned.enabled", true);
		telegramIntegration.addDefault(path + "unbanned.value", "⛔ Unban <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {who_unbanned}\n<strong>Date:</strong> {date}");
		telegramIntegration.addDefault(path + "expired.enabled", true);
		telegramIntegration.addDefault(path + "expired.value", "⛔ Ban <strong>#{id}</strong> expired: {player}\n\n<strong>Date:</strong> {date}");
		telegramIntegration.addDefault(path + "unspecified-reason", "Reason not specified.");
		telegramIntegration.addDefault(path + "formats.types.account", "username/UUID");
		telegramIntegration.addDefault(path + "formats.types.ip", "IP address");
		telegramIntegration.addDefault(path + "formats.active.yes", "active");
		telegramIntegration.addDefault(path + "formats.active.no", "disactive");
		telegramIntegration.addDefault(path + "formats.global.yes", "global");
		telegramIntegration.addDefault(path + "formats.global.no", "local");
		telegramIntegration.addDefault(path + "formats.silent.yes", "yes");
		telegramIntegration.addDefault(path + "formats.silent.no", "no");
		
		path = "messages.warning.";
		
		telegramIntegration.addDefault(path + "info", "⚠ Warning <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Who unwarned:</strong> {who_unwarned}\n<strong>Last reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}\n<strong>Duration:</strong> {duration}\n<strong>Remaining time:</strong> {remaining_time}\n<strong>Unwarn date:</strong> {unwarn_date}\n<strong>Warnings:</strong> {warnings_amount}/{max_warnings}\n<strong>Active:</strong> {active}\n<strong>Scope:</strong> {global}");
		telegramIntegration.addDefault(path + "list", "⚠ Warnlist\n\n<strong>Active warnings' IDs:</strong> {warnings}.");
		telegramIntegration.addDefault(path + "empty-list", "✅ Empty list\n\nThere are no active warnings.");
		telegramIntegration.addDefault(path + "warned.enabled", true);
		telegramIntegration.addDefault(path + "warned.value", "⚠ Warning <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Last reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}\n<strong>Duration:</strong> {duration}\n<strong>Warnings:</strong> {warnings_amount}/{max_warnings}\n<strong>Scope:</strong> {global}");
		telegramIntegration.addDefault(path + "removed.enabled", true);
		telegramIntegration.addDefault(path + "removed.value", "⚠ Warning <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Date:</strong> {date}");
		telegramIntegration.addDefault(path + "cleared.enabled", true);
		telegramIntegration.addDefault(path + "cleared.value", "⚠ Cleared <strong>{player}</strong>'s warnings\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Date:</strong> {date}");
		telegramIntegration.addDefault(path + "expired.enabled", true);
		telegramIntegration.addDefault(path + "expired.value", "⚠ Warning <strong>#{id}</strong> expired: {player}\n\n<strong>Date:</strong> {date}");
		telegramIntegration.addDefault(path + "unspecified-reason", "Reason not specified.");
		telegramIntegration.addDefault(path + "formats.active.yes", "active");
		telegramIntegration.addDefault(path + "formats.active.no", "disactive");
		telegramIntegration.addDefault(path + "formats.global.yes", "global");
		telegramIntegration.addDefault(path + "formats.global.no", "local");
		telegramIntegration.addDefault(path + "formats.silent.yes", "yes");
		telegramIntegration.addDefault(path + "formats.silent.no", "no");
		
		path = "messages.kick.";
		
		telegramIntegration.addDefault(path + "info", "➡ Kick <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}\n<strong>Type:</strong> {type}");
		telegramIntegration.addDefault(path + "kicked.enabled", true);
		telegramIntegration.addDefault(path + "kicked.value", "➡ Kick <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}");
		telegramIntegration.addDefault(path + "unspecified-reason", "Reason not specified.");
		telegramIntegration.addDefault(path + "formats.silent.yes", "yes");
		telegramIntegration.addDefault(path + "formats.silent.no", "no");
		
		path = "messages.mute.";
		
		telegramIntegration.addDefault(path + "info", "✉ Mute <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Who unmuted:</strong> {who_unmuted}\n<strong>Reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}\n<strong>Duration:</strong> {duration}\n<strong>Remaining time:</strong> {remaining_time}\n<strong>Unmute date:</strong> {unmute_date}\n<strong>Active:</strong> {active}\n<strong>Scope:</strong> {global}");
		telegramIntegration.addDefault(path + "list", "✉ Mutelist\n\n<strong>Active mutes' IDs:</strong> {mutes}.");
		telegramIntegration.addDefault(path + "empty-list", "✅ Empty list\n\nThere are no active mutes.");
		telegramIntegration.addDefault(path + "muted.enabled", true);
		telegramIntegration.addDefault(path + "muted.value", "✉ Mute <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}\n<strong>Duration:</strong> {duration}\n<strong>Scope:</strong> {global}");
		telegramIntegration.addDefault(path + "updated.enabled", true);
		telegramIntegration.addDefault(path + "updated.value", "✉ Mute <strong>#{id}</strong> updated: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Reason:</strong> {reason}\n<strong>Server:</strong> {server}\n<strong>Date:</strong> {date}\n<strong>Duration:</strong> {duration}\n<strong>Scope:</strong> {global}");
		telegramIntegration.addDefault(path + "unmuted.enabled", true);
		telegramIntegration.addDefault(path + "unmuted.value", "✉ Unmute <strong>#{id}</strong>: {player}\n\n<strong>Staff member:</strong> {staff_member}\n<strong>Date:</strong> {date}");
		telegramIntegration.addDefault(path + "expired.enabled", true);
		telegramIntegration.addDefault(path + "expired.value", "✉ Mute <strong>#{id}</strong> expired: {player}\n\n<strong>Date:</strong> {date}");
		telegramIntegration.addDefault(path + "unspecified-reason", "Reason not specified.");
		telegramIntegration.addDefault(path + "formats.active.yes", "active");
		telegramIntegration.addDefault(path + "formats.active.no", "disactive");
		telegramIntegration.addDefault(path + "formats.global.yes", "global");
		telegramIntegration.addDefault(path + "formats.global.no", "local");
		telegramIntegration.addDefault(path + "formats.silent.yes", "yes");
		telegramIntegration.addDefault(path + "formats.silent.no", "no");
		
		telegramIntegration.addDefault("messages.inexistent-id", "❌ Inexistent ID\n\nThe specified ID does not exist. Try with a different one.");
		telegramIntegration.addDefault("messages.no-permission", "❌ No permission\n\nYou do not have the permission to execute this command.");
		telegramIntegration.addDefault("messages.invalid-number", "❌ Invalid number\n\nThe specified number is invalid. Try with a different one.");
		telegramIntegration.addDefault("messages.group-only-action", "❌ Group only action\n\nThis action can only be performed inside of the configured group.");
		telegramIntegration.addDefault("messages.wrong-syntax", "❌ Wrong syntax\n\nThe syntax is wrong. Usage: <strong>{usage}</strong>.");
		telegramIntegration.addDefault("messages.disabled-feature", "❌ Disabled feature\n\nThat feature is disabled. Set it up in the server's config files.");
		telegramIntegration.addDefault("messages.invalid-ip-address", "❌ Invalid IP address\n\nThe specified IP address is invalid. Try with a different one.");
		telegramIntegration.addDefault("messages.at-least-one-online", "❌ No players online\n\nAt least one player online is required to perform this action.");
		telegramIntegration.addDefault("messages.ip-lookup", "⌖ IP lookup of <strong>{ip_address}</strong>\n\n<strong>ISP:</strong> {isp}\n<strong>Country:</strong> {country}, {continent}\n<strong>Subdivision(s):</strong> {subdivisions}\n<strong>City:</strong> {city} (postal code: {postal_code})\n<strong>Coords:</strong> {latitude}° {longitude}°\n<strong>Accuracy radius:</strong> ~{accuracy_radius_km} km");
		
		telegramIntegration.addDefault("simple-date-format", "E, MM/dd/yyyy hh:mm a");
		telegramIntegration.addDefault("timestamps.now", "now");
		telegramIntegration.addDefault("timestamps.ever", "ever");
		telegramIntegration.addDefault("timestamps.never", "never");
		telegramIntegration.addDefault("timestamps.second", " second");
		telegramIntegration.addDefault("timestamps.seconds", " seconds");
		telegramIntegration.addDefault("timestamps.minute", " minute");
		telegramIntegration.addDefault("timestamps.minutes", " minutes");
		telegramIntegration.addDefault("timestamps.hour", " hour");
		telegramIntegration.addDefault("timestamps.hours", " hours");
		telegramIntegration.addDefault("timestamps.day", " day");
		telegramIntegration.addDefault("timestamps.days", " days");
		telegramIntegration.addDefault("timestamps.month", " month");
		telegramIntegration.addDefault("timestamps.months", " months");
		telegramIntegration.addDefault("timestamps.year", " year");
		telegramIntegration.addDefault("timestamps.years", " years");
		
		telegramIntegration.addDefault("placeholders.nobody", "nobody");
		telegramIntegration.addDefault("placeholders.not-present", "not present");
		
		telegramIntegration.addDefault("commands-descriptions.chatplugin", "ChatPlugin's main command.");
		telegramIntegration.addDefault("commands-descriptions.baninfo", "Show information about a ban.");
		telegramIntegration.addDefault("commands-descriptions.warninginfo", "Show information about a warning.");
		telegramIntegration.addDefault("commands-descriptions.kickinfo", "Show information about a kick.");
		telegramIntegration.addDefault("commands-descriptions.muteinfo", "Show information about a mute.");
		telegramIntegration.addDefault("commands-descriptions.banlist", "Show the active bans list.");
		telegramIntegration.addDefault("commands-descriptions.warnlist", "Show the active warnings list.");
		telegramIntegration.addDefault("commands-descriptions.mutelist", "Show the active mutes list.");
		telegramIntegration.addDefault("commands-descriptions.iplookup", "Perform an IP address lookup.");
		
		telegramIntegration.save();
	}
	
}
