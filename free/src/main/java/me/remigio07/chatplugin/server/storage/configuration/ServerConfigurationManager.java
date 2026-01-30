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

package me.remigio07.chatplugin.server.storage.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionChange;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

public class ServerConfigurationManager extends ConfigurationManager {
	
	@SuppressWarnings("deprecation")
	@Override
	public void load() throws ChatPluginManagerException {
		try {
			if (!configurations.isEmpty()) {
				loadAll();
				return;
			} instance = this;
			long ms = System.currentTimeMillis();
			
			putConfiguration(new Configuration(ConfigurationType.CONFIG));
			
			putConfiguration(new Configuration(ConfigurationType.MESSAGES));
			
			putConfiguration(new Configuration(ConfigurationType.RANKS));
			putConfiguration(new Configuration(ConfigurationType.CHAT));
			putConfiguration(new Configuration(ConfigurationType.TABLISTS));
			putConfiguration(new Configuration(ConfigurationType.ACTIONBARS));
			putConfiguration(new Configuration(ConfigurationType.ADS));
			putConfiguration(new Configuration(ConfigurationType.F3_SERVER_NAMES));
			putConfiguration(new Configuration(ConfigurationType.JOIN_QUIT_MODULES));
			
			putConfiguration(new Configuration(ConfigurationType.MAIN_GUI));
			putConfiguration(new Configuration(ConfigurationType.LANGUAGES_GUI));
			putConfiguration(new Configuration(ConfigurationType.CHAT_COLOR_GUI));
			putConfiguration(new Configuration(ConfigurationType.EMOJIS_TONE_GUI));
			putConfiguration(new Configuration(ConfigurationType.PLAYER_INFO_GUI));
			putConfiguration(new Configuration(ConfigurationType.PREFERENCES_GUI));
			
			if (ChatPlugin.getInstance().isPremium()) {
				putConfiguration(new Configuration(ConfigurationType.DEFAULT_SCOREBOARD));
				putConfiguration(new Configuration(ConfigurationType.BOSSBARS));
				
				putConfiguration(new Configuration(ConfigurationType.BANLIST_GUI));
				putConfiguration(new Configuration(ConfigurationType.WARNLIST_GUI));
				putConfiguration(new Configuration(ConfigurationType.MUTELIST_GUI));
				putConfiguration(new Configuration(ConfigurationType.VIOLATIONS_GUI));
				putConfiguration(new Configuration(ConfigurationType.PLAYER_PUNISHMENTS_GUI));
				putConfiguration(new Configuration(ConfigurationType.PLAYER_VIOLATIONS_GUI));
				putConfiguration(new Configuration(ConfigurationType.VIOLATIONS_ICONS));
				
				putConfiguration(new Configuration(ConfigurationType.MOTD));
				
				putConfiguration(new Configuration(ConfigurationType.DISCORD_INTEGRATION));
				putConfiguration(new Configuration(ConfigurationType.TELEGRAM_INTEGRATION));
			}
			
			loadAll();
			LogManager.getInstance().setDebug(ConfigurationType.CONFIG.get().getBoolean("settings.debug"));
			
			if ((lastVersionChange = VersionChange.getVersionChange(ConfigurationType.CONFIG.get(), "version", ChatPlugin.VERSION)) != VersionChange.NULL) {
				if (!lastVersionChange.isSupported())
					throw new ChatPluginManagerException(this, "downgrading ChatPlugin's version is not supported. If you wish to use an older version, please delete the plugin's folder and let it generate new configuration files. You can also edit \"settings.version\" in config.yml to bypass this check, but errors may occur and no support will be given if you ask for it");
				addAllDefaults(true);
			} else addAllDefaults(false);
			
			enabled = true;
			loadTime = System.currentTimeMillis() - ms;
		} catch (IOException e) {
			e.printStackTrace();
			throw new ChatPluginManagerException(this, e);
		}
	}
	
	@Override
	public void addAllDefaults(boolean forceAdd) throws IOException {
		addConfigDefaults(forceAdd);
		
		addMessagesDefaults(forceAdd);
		
		addRanksDefaults(forceAdd);
		addChatDefaults(forceAdd);
		addTablistsDefaults(forceAdd);
		addActionbarsDefaults(forceAdd);
		addAdsDefaults(forceAdd);
		addF3ServerNamesDefaults(forceAdd);
		addJoinQuitModulesDefaults(forceAdd);
		
		addMainGUIDefaults(forceAdd);
		addLanguagesGUIDefaults(forceAdd);
		addChatColorGUIDefaults(forceAdd);
		addEmojisToneGUIDefaults(forceAdd);
		addPlayerInfoGUIDefaults(forceAdd);
		addPreferencesGUIDefaults(forceAdd);
		
		if (ChatPlugin.getInstance().isPremium()) {
			addDefaultScoreboardDefaults(forceAdd);
			addBossbarsDefaults(forceAdd);
			
			addBanlistGUIDefaults(forceAdd);
			addWarnlistGUIDefaults(forceAdd);
			addMutelistGUIDefaults(forceAdd);
			addViolationsGUIDefaults(forceAdd);
			addPlayerPunishmentsGUIDefaults(forceAdd);
			addPlayerViolationsGUIDefaults(forceAdd);
			addViolationsIconsDefaults(forceAdd);
			
			addMoTDDefaults(forceAdd);
			
			addDiscordIntegrationDefaults(forceAdd);
			addTelegramIntegrationDefaults(forceAdd);
		}
	}
	
	public void addConfigDefaults(boolean forceAdd) throws IOException {
		Configuration config = configurations.get(ConfigurationType.CONFIG);
		
		if (!Files.exists(config.getPath()))
			config.createFile();
		else if (!forceAdd)
			return;
		
		config.set("version", ChatPlugin.VERSION);
		
		config.addDefault("settings.debug", false);
		config.addDefault("settings.enable-update-notification", true);
		config.addDefault("settings.register-scoreboards", Environment.isSponge() || ChatPlugin.getInstance().isPremium() || VersionUtils.getVersion().isOlderThan(Version.V1_21_2));
		config.addDefault("settings.log-command-blocks-commands", true);
		config.addDefault("settings.truncate-version-string", true);
		config.addDefault("settings.use-week-timestamp", false);
		config.addDefault("settings.anticheat-integration.reasons-start-with", Arrays.asList("[Matrix]", "[Vulcan]", "[Anticheat]"));
		config.addDefault("settings.anticheat-integration.violations-expiration-timeout", "1m");
		config.addDefault("settings.storage-placeholders-update-timeout", "1m");
		config.addDefault("settings.floodgate-username-prefix", "");
		config.addDefault("settings.enabled-worlds", Arrays.asList("*"));
		config.addDefault("settings.disabled-commands", Arrays.asList());
		config.addDefault("settings.displayed-memory.unit", "MEGABYTE");
		config.addDefault("settings.displayed-memory.decimals", 0);
		config.addDefault("settings.displayed-time-zone", "");
		
		config.addDefault("multi-instance-mode.enabled", false);
		config.addDefault("multi-instance-mode.proxy-online-mode", false);
		config.addDefault("multi-instance-mode.server-id", ProxyManager.DEFAULT_SERVER_ID);
		config.addDefault("multi-instance-mode.server-display-name", "default-server-name");
		config.addDefault("multi-instance-mode.server-information-update-timeout", "10s");
		config.addDefault("multi-instance-mode.log-filtered-packets", Arrays.asList("F3ServerName", "ServerInformation", "IPLookupRequest", "IPLookupResponse", "PlayerJoin", "PlayerQuit", "PlayerSwitch", "PrivateMessage"));
		
		config.addDefault("storage.method", "H2");
		config.addDefault("storage.folder", "{0}/files");
		config.addDefault("storage.players-auto-cleaner-period", "-1d");
		config.addDefault("storage.database.address", "localhost");
		config.addDefault("storage.database.port", 3306);
		config.addDefault("storage.database.username", "");
		config.addDefault("storage.database.password", "");
		config.addDefault("storage.database.name", "minecraft");
		config.addDefault("storage.database.table-prefix", "chatplugin_");
		config.addDefault("storage.database.file-name", "chatplugin-h2");
		config.addDefault("storage.database.use-ssl", false);
		config.addDefault("storage.database.use-server-mode", false);
		
		config.addDefault("languages.main-language-id", "english");
		config.addDefault("languages.detector.enabled", false);
		config.addDefault("languages.detector.method", "CLIENT_LOCALE");
		config.addDefault("languages.detector.delay-ms", 5000L);
		config.addDefault("languages.command.cooldown", "5m");
		config.addDefault("languages.command.commands", Arrays.asList());
		config.addDefault("languages.english.display-name", "&fEnglish");
		config.addDefault("languages.english.country-codes", Arrays.asList("AU", "NZ", "GB", "US"));
		config.addDefault("languages.italian.display-name", "&fitaliano");
		config.addDefault("languages.italian.country-codes", Arrays.asList("IT"));
		
		config.addDefault("ip-lookup.enabled", false);
		config.addDefault("ip-lookup.load-on-join", false);
		config.addDefault("ip-lookup.method", "REMOTE");
		config.addDefault("ip-lookup.database-folder", "{0}/files");
		config.addDefault("ip-lookup.cache-time", "10m");
		config.addDefault("ip-lookup.maxmind-account.user-id", "");
		config.addDefault("ip-lookup.maxmind-account.key", "");
		config.addDefault("ip-lookup.max-ips-stored", 10);
		
		config.addDefault("guis.enabled", VersionUtils.getVersion().isAtLeast(Environment.isBukkit() ? Version.V1_8 : Version.V1_12));
		config.addDefault("guis.per-player-guis-unload-time", "2m");
		
		config.addDefault("ban.enabled", true);
		config.addDefault("ban.default-global", true);
		config.addDefault("ban.reason-required", true);
		config.addDefault("ban.allow-ban-not-stored-players", false);
		config.addDefault("ban.allow-ban-offline", true);
		config.addDefault("ban.allow-banip-offline", false);
		config.addDefault("ban.banwave.enabled", false);
		config.addDefault("ban.banwave.announce", true);
		config.addDefault("ban.banwave.timeout", "6h");
		config.addDefault("ban.banwave.delay-ms", 100);
		config.addDefault("ban.banwave.reasons-start-with", Arrays.asList("[Matrix]", "[Vulcan]", "[Anticheat]"));
		config.addDefault("ban.banwave.commands.start", Arrays.asList());
		config.addDefault("ban.banwave.commands.end", Arrays.asList());
		
		config.addDefault("warning.enabled", true);
		config.addDefault("warning.default-global", true);
		config.addDefault("warning.reason-required", true);
		config.addDefault("warning.allow-warning-not-stored-players", false);
		config.addDefault("warning.allow-warning-offline", false);
        config.addDefault("warning.duration", "7d");
        config.addDefault("warning.punish-commands.4.english", Arrays.asList("kick {player} You have been warned again (4/5 times). Next time you will get automatically banned for 5 days. -s"));
        config.addDefault("warning.punish-commands.4.italian", Arrays.asList("kick {player} Siete stati avvisati nuovamente (4/5 volte). La prossima volta verrete automaticamente bannati per 5 giorni. -s"));
        config.addDefault("warning.punish-commands.5.english", Arrays.asList("clearwarnings {player}", "tempban {player} 5d You have reached the limit of warnings (5/5). This is an automatic ban. -s"));
        config.addDefault("warning.punish-commands.5.italian", Arrays.asList("clearwarnings {player}", "tempban {player} 5d Hai raggiunto il limite di avvisi (5/5). Questo è un ban automatico. -s"));
        
		config.addDefault("kick.enabled", true);
		config.addDefault("kick.reason-required", true);
		config.addDefault("kick.default-kick-message-type", "KICK");
		config.addDefault("kick.lobby-server-id", "lobby");
        
		config.addDefault("mute.enabled", true);
		config.addDefault("mute.default-global", true);
		config.addDefault("mute.reason-required", true);
		config.addDefault("mute.allow-mute-not-stored-players", false);
		config.addDefault("mute.allow-mute-offline", true);
		
		config.addDefault("ping.enabled", true);
		config.addDefault("ping.update-timeout", "5s");
		config.addDefault("ping.qualities.excellent", 20);
		config.addDefault("ping.qualities.great", 50);
		config.addDefault("ping.qualities.good", 68);
		config.addDefault("ping.qualities.funny", 69);
		config.addDefault("ping.qualities.good-2", 100);
		config.addDefault("ping.qualities.poor", 150);
		config.addDefault("ping.qualities.bad", 200);
		config.addDefault("ping.qualities.unplayable", 201);
		
		config.addDefault("tps.enabled", !Environment.isBukkit() || VersionUtils.isSpigot());
		config.addDefault("tps.20-tps-cap.enabled", true);
		config.addDefault("tps.20-tps-cap.add-wildcard", false);
		config.addDefault("tps.update-timeout", "5s");
		config.addDefault("tps.qualities.excellent", 19.99);
		config.addDefault("tps.qualities.great", 19.5);
		config.addDefault("tps.qualities.good", 19);
		config.addDefault("tps.qualities.poor", 18);
		config.addDefault("tps.qualities.bad", 17);
		config.addDefault("tps.qualities.unplayable", 16);
		
		config.addDefault("mspt.enabled", VersionUtils.isPaper() && VersionUtils.getVersion().isAtLeast(Version.V1_16));
		config.addDefault("mspt.update-timeout", "1s");
		config.addDefault("mspt.qualities.excellent", 50);
		config.addDefault("mspt.qualities.great", 51.28);
		config.addDefault("mspt.qualities.good", 55.55);
		config.addDefault("mspt.qualities.poor", 58.82);
		config.addDefault("mspt.qualities.bad", 62.5);
		config.addDefault("mspt.qualities.unplayable", 66.66);
		
		config.addDefault("vanish.enabled", !Environment.isFabric());
		config.addDefault("vanish.invisibility", true);
		
		config.save();
	}
	
	public void addMessagesDefaults(boolean forceAdd) throws IOException {
		Configuration messages = configurations.get(ConfigurationType.MESSAGES);
		
		if (!Files.exists(messages.getPath()))
			messages.createFile();
		else if (!forceAdd)
			return;
		
		messages.addDefault("misc.prefix", "&8[&c&lChat&f&lPlugin&8]&f");
		
		messages.addDefault("misc.simple-date-format.full", "E, MM/dd/yyyy hh:mm a");
		messages.addDefault("misc.simple-date-format.day", "E, MM/dd/yyyy");
		messages.addDefault("misc.simple-date-format.hour", "hh:mm a");
		
		messages.addDefault("misc.only-players", "{pfx} &cOnly players can execute this command.");
		messages.addDefault("misc.no-permission", "{pfx} &cYou do not have the permission to execute this command.");
		messages.addDefault("misc.reload.start", "{pfx} &aChatPlugin is reloading...");
		messages.addDefault("misc.reload.end", "{pfx} &aChatPlugin has been reloaded. Took &f{0} ms &ato complete.");
		messages.addDefault("misc.player-not-found", "{pfx} &f{0} &cis not online. Recheck the typing.");
		messages.addDefault("misc.player-not-stored", "{pfx} &f{0} &cis not contained in the storage.");
		messages.addDefault("misc.cooldown-active", "{pfx} &cA cooldown is currently active, try later.");
		messages.addDefault("misc.wrong-args", "{pfx} &cThe arguments are wrong. Try &f/chatplugin help&c.");
		messages.addDefault("misc.wrong-syntax", "{pfx} &cThe syntax is wrong. Usage: &f{0}&c.");
		messages.addDefault("misc.invalid-number", "{pfx} &f{0} &cis not a valid number.");
		messages.addDefault("misc.invalid-rank", "{pfx} &f{0} &cis not a valid rank. Here are the loaded ranks: &f{1}&c.");
		messages.addDefault("misc.reason-required", "{pfx} &cYou have to specify a reason.");
		messages.addDefault("misc.debug.enabled", "{pfx} &aDebug mode enabled.");
		messages.addDefault("misc.debug.disabled", "{pfx} &aDebug mode disabled.");
		messages.addDefault("misc.debug.file.start", "{pfx} &aDebugging data and settings...");
		messages.addDefault("misc.debug.file.end", "{pfx} &aDebug saved to file &f{0}&a. Took &f{1} ms &ato complete.");
		messages.addDefault("misc.debug.file.too-fast", "{pfx} &cYou cannot generate more than a debug file per second.");
		messages.addDefault("misc.debug.manager.info", "{pfx} &aDebugging data of &f{0}&a:");
		messages.addDefault("misc.debug.manager.disabled", "{pfx} &cThat manager is disabled.");
		messages.addDefault("misc.at-least-one-online", "{pfx} &cAt least one player online is required to perform this action.");
		messages.addDefault("misc.disabled-world", "{pfx} &cThat world is not enabled.");
		messages.addDefault("misc.disabled-feature", "{pfx} &cThat feature is not enabled.");
		messages.addDefault("misc.inexistent-id", "{pfx} &cThat ID does not exist.");
		messages.addDefault("misc.inexistent-player", "{pfx} &f{0} &cis not a paid account's name.");
		messages.addDefault("misc.already-connected", "{pfx} &cYou are already connected to this server.");
		messages.addDefault("misc.invalid-player-name", "{pfx} &cInvalid player name.");
		messages.addDefault("misc.invalid-json", "{pfx} &cInvalid JSON: &f{0}&c.");
		messages.addDefault("misc.invalid-server", "{pfx} &f{0} &cis not a valid server. Is the proxy online with ChatPlugin installed?");
		messages.addDefault("misc.invalid-ip-address", "{pfx} &f{0} &cis not a valid IP address.");
		messages.addDefault("misc.error-occurred", "{pfx} &f{0} &coccurred while trying to perform the operation: &f{1}&c.");
		messages.addDefault("misc.update-notification", "{pfx} &eYou are running an outdated version of ChatPlugin. It is recommended to update to the latest version (&f{0}&e) to avoid bugs and incompatibilities.");
		messages.addDefault("misc.suggest-version", "{pfx} &eUse Minecraft version &f{suggested_version} &eor higher to enjoy the best experience on the server.");
		
		messages.addDefault("commands.help.free.user", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/language &8- &eChange your currently displayed language.\n&f&l/whisper &8- &eSend a private message to another player.\n&f&l/ignore &8- &eManage players you are ignoring.\n&f&l/ping &8- &eShow yours or another player's latency in ms.\n&f&l/rankinfo &8- &eDisplay info about a player's rank.\n&f&l/playerlist &8- &eDisplay the online players' list.\n&f&l/preferences &8- &eChange your personal preferences.");
		messages.addDefault("commands.help.free.admin", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/rank &8- &eCreate, remove, edit and view loaded ranks.\n&f&l/staffchat &8- &eVery handy cross-server Staff chat.\n&f&l/socialspy &8- &eSpy other players' private messages.\n&f&l/iplookup &8- &ePerform a lookup of an IP address.\n&f&l/lastseen &8- &eCheck when a player was last seen.\n&f&l/clearchat &8- &eClear chat to hide last messages.");
		messages.addDefault("commands.help.free.vanish", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/vanish &8- &eBecome invisible to non Staff members.");
		messages.addDefault("commands.help.free.misc", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/tps &8- &eDisplay the server's current ticks per second.\n&f&l/ad &8- &eSend a loaded ad to one or more online players.\n&f&l/broadcast &8- &eSend a message to every online player.\n&f&l/broadcastraw &8- &eLike /broadcast, but without the prefix.");
		
		messages.addDefault("commands.help.premium.user", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/language &8- &eChange your currently displayed language.\n&f&l/whisper &8- &eSend a private message to another player.\n&f&l/ignore &8- &eManage players you are ignoring.\n&f&l/ping &8- &eShow yours or another player's latency in ms.\n&f&l/rankinfo &8- &eDisplay info about a player's rank.\n&f&l/playerlist &8- &eDisplay the online players' list.\n&f&l/preferences &8- &eChange your personal preferences.");
		messages.addDefault("commands.help.premium.admin", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/rank &8- &eCreate, remove, edit and view loaded ranks.\n&f&l/staffchat &8- &eVery handy cross-server Staff chat.\n&f&l/socialspy &8- &eSpy other players' private messages.\n&f&l/iplookup &8- &ePerform a lookup of an IP address.\n&f&l/lastseen &8- &eCheck when a player was last seen.\n&f&l/clearchat &8- &eClear chat to hide last messages.\n&f&l/chatlog &8- &eLook up messages containing certain text.\n&f&l/accountcheck &8- &eCheck a player's alt accounts.");
		messages.addDefault("commands.help.premium.guis", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/cp status &8- &eDisplay the server's current status\n&f&l/banlist &8- &eDisplay the active bans list.\n&f&l/warnlist &8- &eDisplay the active warnings list.\n&f&l/mutelist &8- &eDisplay the active mutes list.\n&f&l/violations &8- &eDisplay last players' violations.\n&f&l/playerinfo &8- &eShow detailed info about a player.\n&f&l/playerpunishments &8- &eShow a player's punishments list.\n&f&l/playerviolations &8- &eShow a player's last violations.");
		messages.addDefault("commands.help.premium.punishments", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/tempban &8- &eTemporarily ban a player.\n&f&l/unban &8- &eUnban a previously banned player.\n&f&l/warning &8- &eWarn a player for a certain time.\n&f&l/removelastwarning &8- &eRemove a player's last warning.\n&f&l/clearwarnings &8- &eClear a player's active warnings.\n&f&l/kick &8- &eDisconnect a player from the server.\n&f&l/fakekick &8- &eKick with a random error as reason.\n&f&l/mute &8- &eTemporarily mute a player.\n&f&l/unmute &8- &eUnmute a previously muted player.");
		messages.addDefault("commands.help.premium.vanish", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/vanish &8- &eBecome invisible to non Staff members.\n&f&l/fakejoin &8- &eSend fake join message and disable vanish.\n&f&l/fakequit &8- &eSend fake quit message and enable vanish.\n&f&l/silentteleport &8- &eSilently teleport somewhere else.");
		messages.addDefault("commands.help.premium.misc", "{pfx} &aHelp for &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&f&l/tps &8- &eDisplay the server's current ticks per second.\n&f&l/ad &8- &eSend a loaded ad to one or more online players.\n&f&l/broadcast &8- &eSend a message to every online player.\n&f&l/broadcastraw &8- &eLike /broadcast, but without the prefix.\n&f&l/globalbroadcast &8- &eLike /broadcast, but cross-server.\n&f&l/discordmessage &8- &eSend a message to the server.\n&f&l/telegrammessage &8- &eSend a message to the group.");
		
		messages.addDefault("commands.info", "{pfx} &aInfo about &c&lChat&f&lPlugin &fv{0} &aby &9Remigio07&a:\n&eWebsite: &fhttps://remigio07.me/chatplugin\n&eGitHub: &fhttps://github.com/ChatPlugin/ChatPlugin\n&eDiscord: &fhttps://discord.gg/eSnAPhvMTG");
		messages.addDefault("commands.version", "{pfx} &aRunning &c&lChat&f&lPlugin {0} &fv{1} &aby &9Remigio07 &aon &f{2} {3}&a.");
		messages.addDefault("commands.status", "{pfx} &aCurrent server status:\n&eServer version: &f{server_version} (protocol: {server_version_protocol})\n&eChatPlugin version: &f{plugin_version}\n&eUptime: &f{uptime}\n&eTPS (1m, 5m, 15m): &f{tps_1_min_format}&f, {tps_5_min_format}&f, {tps_15_min_format}\n&eUsed memory: &f{used_memory}/{max_memory} MB\n&eAllocated memory: &f{total_memory} MB\n&eFree memory: &f{free_memory} MB\n&eStorage used: &f{used_storage}/{total_storage} GB\n&eFree storage: &f{free_storage} GB\n&eEnabled worlds: &f{enabled_worlds}x\n&eEnabled players: &f{enabled_players}x\n&eEnabled managers: &f{enabled_managers}x\n&eStartup time: &f{startup_time} ms\n&eLast reload time: &f{last_reload_time} ms");
		messages.addDefault("commands.scoreboard.enabled", "{pfx} &aThe scoreboard has been enabled.");
		messages.addDefault("commands.scoreboard.disabled", "{pfx} &aThe scoreboard has been disabled.");
		messages.addDefault("commands.bossbar.enabled", "{pfx} &aThe bossbar has been enabled.");
		messages.addDefault("commands.bossbar.disabled", "{pfx} &aThe bossbar has been disabled.");
		messages.addDefault("commands.actionbar.enabled", "{pfx} &aThe actionbar has been enabled.");
		messages.addDefault("commands.actionbar.disabled", "{pfx} &aThe actionbar has been disabled.");
		messages.addDefault("commands.rankinfo", "{pfx} &aInformation about &f{player}&a's rank:\n&eRank: &f{rank_display_name}\n&ePrefix: &f{prefix}\n&eSuffix: &f{suffix}\n&eTag: &f{tag_prefix}{tag_name_color}{player}{tag_suffix}\n&eDescription:\n&f{rank_description}");
		
		messages.addDefault("commands.rank.luckperms-mode", "{pfx} &cLuckPerms mode is enabled. Use its commands to create or delete ranks, then manually reload ChatPlugin with &f/chatplugin reload&c.");
		messages.addDefault("commands.rank.info", "{pfx} &aInformation about rank &f{rank_id} &a(&f{rank_display_name}&a):\n&ePrefix: &f{prefix}\n&eSuffix: &f{suffix}\n&eTag: &f{tag_prefix}{tag_name_color}Notch{tag_suffix}\n&eChat color: &f{chat_color}\n&ePosition: &f{rank_position}\n&eDescription (&fEnglish&e):\n&f{rank_description}\n&eMax ban duration: &f{max_ban_duration}\n&eMax mute duration: &f{max_mute_duration}");
		messages.addDefault("commands.rank.list.header", "{pfx} &aHere are displayed the loaded ranks:");
		messages.addDefault("commands.rank.list.message-format.text", "&f{rank_id} &e(&f{rank_display_name}&e) &8- &eposition: &f{rank_position}");
		messages.addDefault("commands.rank.list.message-format.hover", "&a&l{rank_id}\n&7Display name: &f{rank_display_name}\n&7Position: &f{rank_position}\n\n&e&oClick for more info!");
		messages.addDefault("commands.rank.edit.set", "{pfx} &aThe &f{0} &aproperty of rank &f{1} &ahas been set to &f{2}&a.");
		messages.addDefault("commands.rank.edit.reset", "{pfx} &aThe &f{0} &aproperty of rank &f{1} &ahas been reset.");
		messages.addDefault("commands.rank.edit.invalid-property", "{pfx} &cInvalid property. Here are the available properties: &f{0}&c.");
		messages.addDefault("commands.rank.edit.out-of-sync", "{pfx} &cRank &f{0} &cno longer exists on LuckPerms. It is recommended to reload ChatPlugin.");
		messages.addDefault("commands.rank.removed.text", "{pfx} &aRank &f{0} &ahas been removed. Click to reload ChatPlugin when you wish to apply changes.");
		messages.addDefault("commands.rank.removed.hover", "&9[Click here to reload ChatPlugin]");
		messages.addDefault("commands.rank.add.added.text", "{pfx} &aRank &f{0} &ahas been added. Click to reload ChatPlugin when you wish to apply changes.");
		messages.addDefault("commands.rank.add.added.hover", "&9[Click here to reload ChatPlugin]");
		messages.addDefault("commands.rank.add.already-exists", "{pfx} &cA rank with that ID already exists.");
		messages.addDefault("commands.rank.add.reached-limit", "{pfx} &cPlugin has reached the limit of 99 ranks.");
		messages.addDefault("commands.rank.add.invalid-position", "{pfx} &cInvalid position. Type a number between &f0 &cand &f{0}&c.");
		
		messages.addDefault("commands.playerlist.all.message", "{pfx} &aThere are currently &f{0}/{1} &aplayers online:");
		messages.addDefault("commands.playerlist.all.no-players-online", "{pfx} &eThere are currently no players online.");
		messages.addDefault("commands.playerlist.all.rank-format", "{prefix}&e(&f{0}&e): &f[{1}]");
		messages.addDefault("commands.playerlist.rank.message", "{pfx} &aThere are currently &f{0} &aplayers online who belong to the &f{1} &arank: &f[{2}]&a.");
		messages.addDefault("commands.playerlist.rank.no-players-online", "{pfx} &eThere are currently no players online who belong to that rank.");
		messages.addDefault("commands.playerlist.name-format.vanished", "&f&o");
		messages.addDefault("commands.playerlist.name-format.not-vanished", "&f");
		
		messages.addDefault("commands.chatcolor.set.self", "{pfx} &aChat's default color set to &f{0}&a.");
		messages.addDefault("commands.chatcolor.set.other", "{pfx} &f{0}&a's chat's default color set to &f{1}&a.");
		messages.addDefault("commands.chatcolor.reset.self", "{pfx} &aChat's default color reset.");
		messages.addDefault("commands.chatcolor.reset.other", "{pfx} &f{0}&a's chat's default color reset.");
		messages.addDefault("commands.chatcolor.hex-usage", "{pfx} &eTo set a hexadecimal color, type &f/chatcolor #&f{0}&e. This code is just an example: you can use any color.");
		messages.addDefault("commands.chatcolor.invalid-color", "{pfx} &f{0} &cis not a valid color.");
		messages.addDefault("commands.chatcolor.no-permission", "{pfx} &cYou do not have the permission to use that color.");
		
		messages.addDefault("commands.emojistone.set.self", "{pfx} &aEmojis' default tone set to tone &f#{0} &a(&f{1}&a).");
		messages.addDefault("commands.emojistone.set.other", "{pfx} &f{0}&a's emojis' tones set to tone &f#{1} &a(&f{2})&a.");
		messages.addDefault("commands.emojistone.reset.self", "{pfx} &aEmojis' default tone reset.");
		messages.addDefault("commands.emojistone.reset.other", "{pfx} &f{0}&a's emojis' default tone reset.");
		messages.addDefault("commands.emojistone.invalid-tone", "{pfx} &f{0} &cis not a valid tone.");
		
		messages.addDefault("commands.staff-chat.enabled", "{pfx} &aStaff chat mode enabled.");
		messages.addDefault("commands.staff-chat.disabled", "{pfx} &aStaff chat mode disabled.");
		messages.addDefault("commands.iplookup", "{pfx} &f{ip_address}&a's lookup:\n&eISP: &f{isp}\n&eCountry: &f{country}, {continent}\n&eSubdivision(s): &f{subdivisions}\n&eCity: &f{city} (postal code: {postal_code})\n&eCoords: &f{latitude}° {longitude}°\n&eAccuracy radius: &f~{accuracy_radius_km} km\n&eRelative date: &f{relative_date_full}");
		messages.addDefault("commands.lastseen.online", "{pfx} &f{0} &ehas been online for &f{1}&e.");
		messages.addDefault("commands.lastseen.offline", "{pfx} &f{0} &ewas last seen &f{1} &eago; IP address: &f{2}&e.");
		messages.addDefault("commands.lastseen.never-joined", "{pfx} &f{0} &ehas never joined the server.");
		messages.addDefault("commands.clearchat", "{pfx} &aChat has been cleared.");
		messages.addDefault("commands.muteall.muted", "{pfx} &eThe chat has been globally muted by a Staff member.");
		messages.addDefault("commands.muteall.unmuted", "{pfx} &aThe chat has been globally unmuted.");
		messages.addDefault("commands.baninfo", "{pfx} &aInformation and data about ban &f#{id}&a:\n&ePlayer name: &f{player}\n&ePlayer UUID: &f{player_uuid}\n&eIP address: &f{ip_address}\n&eStaff member: &f{staff_member}\n&eWho unbanned: &f{who_unbanned}\n&eReason: &f{reason}\n&eServer: &f{server}\n&eDate: &f{date}\n&eDuration: &f{duration}\n&eRemaining time: &f{remaining_time}\n&eUnban date: &f{unban_date}\n&eType: &f{type}\n&eActive: &f{active}\n&eGlobal: &f{global}\n&eSilent: &f{silent}");
		messages.addDefault("commands.warninginfo", "{pfx} &aInformation and data about warning &f#{id}&a:\n&ePlayer name: &f{player}\n&ePlayer UUID: &f{player_uuid}\n&eStaff member: &f{staff_member}\n&eWho unwarned: &f{who_unwarned}\n&eReason: &f{reason}\n&eServer: &f{server}\n&eDate: &f{date}\n&eDuration: &f{duration}\n&eRemaining time: &f{remaining_time}\n&eUnwarn date: &f{unwarn_date}\n&eActive: &f{active}\n&eGlobal: &f{global}\n&eSilent: &f{silent}");
		messages.addDefault("commands.kickinfo", "{pfx} &aInformation and data about kick &f#{id}&a:\n&ePlayer name: &f{player}\n&ePlayer UUID: &f{player_uuid}\n&eIP address: &f{ip_address}\n&eStaff member: &f{staff_member}\n&eReason: &f{reason}\n&eServer: &f{server}\n&eDate: &f{date}\n&eSilent: &f{silent}");
		messages.addDefault("commands.muteinfo", "{pfx} &aInformation and data about mute &f#{id}&a:\n&ePlayer name: &f{player}\n&ePlayer UUID: &f{player_uuid}\n&eStaff member: &f{staff_member}\n&eWho unmuted: &f{who_unmuted}\n&eReason: &f{reason}\n&eServer: &f{server}\n&eDate: &f{date}\n&eDuration: &f{duration}\n&eRemaining time: &f{remaining_time}\n&eUnmute date: &f{unmute_date}\n&eActive: &f{active}\n&eGlobal: &f{global}\n&eSilent: &f{silent}");
		messages.addDefault("commands.banlist.ids", "{pfx} &eActive bans' IDs: &f{0}&e.");
		messages.addDefault("commands.banlist.empty", "{pfx} &eThere are no active bans.");
		messages.addDefault("commands.warnlist.ids", "{pfx} &eActive warnings' IDs: &f{0}&e.");
		messages.addDefault("commands.warnlist.empty", "{pfx} &eThere are no active warnings.");
		messages.addDefault("commands.mutelist.ids", "{pfx} &eActive mutes' IDs: &f{0}&e.");
		messages.addDefault("commands.mutelist.empty", "{pfx} &eThere are no active mutes.");
		messages.addDefault("commands.playerinfo", "{pfx} &aInformation and data about &f{player}&a:\n&ePlayer ID: &f#{player_id}\n&eUUID: &f{uuid}\n&eVersion: &f{client_edition} Edition {version} (protocol: {version_protocol})\n&eIP address: &f{ip_address}\n&eISP: &f{isp}\n&eLocation: &f{subdivisions}\n&eCity: &f{city} (~{accuracy_radius_km} km)\n&ePing: &f{ping_format} ms\n&eRank: &f{rank_display_name}\n&eTag: &f{tag_prefix}{tag_name_color}{tag_suffix}\n&eLanguage: &f{language_display_name}\n&eLast login: &f{last_login} ago\n&eTime played: &f{time_played}\n&eTotal bans: &f{player_bans}x\n&eTotal warnings: &f{player_warnings}x\n&eTotal kicks: &f{player_kicks}x\n&eTotal mutes: &f{player_mutes}x\n&eMessages sent: &f{messages_sent}x\n&eAntispam infractions: &f{antispam_infractions}x");
		messages.addDefault("commands.playerpunishments", "{pfx} &f{0}&e's punishments' IDs:\n&eBans: &f{1}\n&eWarnings: &f{2}\n&eKicks: &f{3}\n&eMutes: &f{4}");
		messages.addDefault("commands.tps", "{pfx} &eTPS from last &f1m&e, &f5m&e, &f15m&e: &f{tps_1_min_format}&e, &f{tps_5_min_format}&e, &f{tps_15_min_format}&e.");
		messages.addDefault("commands.mspt", "{pfx} &eMSPT (&faverage&e/&fminimum&e/&fmaximum&e) from last:\n&f&l5 seconds &8- &f{mspt_5_sec_avg_format} ms&e/&f{mspt_5_sec_min_format} ms&e/&f{mspt_5_sec_max_format} ms\n&f&l10 seconds &8- &f{mspt_10_sec_avg_format} ms&e/&f{mspt_10_sec_min_format} ms&e/&f{mspt_10_sec_max_format} ms\n&f&l1 minute &8- &f{mspt_1_min_avg_format} ms&e/&f{mspt_1_min_min_format} ms&e/&f{mspt_1_min_max_format} ms");
		messages.addDefault("commands.ad.send.one", "{pfx} &aAd &f{0} &ahas been successfully sent to &f{1}&a.");
		messages.addDefault("commands.ad.send.all", "{pfx} &aAd &f{0} &ahas been successfully sent to &f{1} &aplayers.");
		messages.addDefault("commands.ad.list", "{pfx} &eLoaded ads' IDs: &f{0}&e.");
		messages.addDefault("commands.discordmessage", "{pfx} &aTrying to send the message through the Discord bot...");
		messages.addDefault("commands.telegrammessage", "{pfx} &aTrying to send the message through the Telegram bot...");
		
		messages.addDefault("commands.account-check.searching", "{pfx} &eSearching for multiple accounts...");
		messages.addDefault("commands.account-check.found", "{pfx} &aThe following accounts were found:");
		messages.addDefault("commands.account-check.not-found", "{pfx} &f{0} &cdoes not own any multiple accounts.");
		messages.addDefault("commands.account-check.message-format.text", "{banned} &8- &f{player}");
		messages.addDefault("commands.account-check.message-format.hover", "&ePlayer ID: &f#{player_id}\n&eIP address: &f{ip_address}\n&eLast logout: &f{last_logout} ago\n&eTime played: &f{time_played}");
		messages.addDefault("commands.account-check.message-format.banned-format.yes", "&4banned &e(IDs: &f{bans_ids}&e)");
		messages.addDefault("commands.account-check.message-format.banned-format.no", "&2not banned");
		
		messages.addDefault("commands.ignore.added.success.self", "{pfx} &aYou are now ignoring &f{0} &a(&f{1}/25&a).");
		messages.addDefault("commands.ignore.added.success.other", "{pfx} &f{0} &eis now ignoring you.");
		messages.addDefault("commands.ignore.added.already-ignoring", "{pfx} &cYou are already ignoring &f{0}&c.");
		messages.addDefault("commands.ignore.removed.success.self", "{pfx} &aYou are no longer ignoring &f{0} &a(&f{1}/25&a).");
		messages.addDefault("commands.ignore.removed.success.other", "{pfx} &f{0} &eis no longer ignoring you.");
		messages.addDefault("commands.ignore.removed.not-ignoring", "{pfx} &cYou are not ignoring &f{0}&c.");
		messages.addDefault("commands.ignore.cannot-ignore.self", "{pfx} &cYou cannot ignore yourself.");
		messages.addDefault("commands.ignore.cannot-ignore.other", "{pfx} &f{0} &ccannot be ignored.");
		messages.addDefault("commands.ignore.list.self", "{pfx} &eYou are ignoring the following players (&f{0}/25&e): &f{1}&e.");
		messages.addDefault("commands.ignore.list.other", "{pfx} &f{0} &eis ignoring the following players (&f{1}/25&e): &f{2}&e.");
		messages.addDefault("commands.ignore.no-ignored.self", "{pfx} &eYou are not ignoring anyone.");
		messages.addDefault("commands.ignore.no-ignored.other", "{pfx} &f{0} &eis not ignoring anyone.");
		messages.addDefault("commands.ignore.cleared", "{pfx} &aYou are no longer ignoring anyone.");
		messages.addDefault("commands.ignore.max-reached", "{pfx} &cYou have reached the maximum of &f25 &cignored players.");
		
		messages.addDefault("commands.whisper.ignored", "{pfx} &f{0} &ccannot read your messages because they are ignoring you.");
		messages.addDefault("commands.whisper.self", "{pfx} &cYou cannot send a private message to yourself.");
		messages.addDefault("commands.reply.not-found", "{pfx} &cUnable to find someone to reply to.");
		messages.addDefault("commands.socialspy.enabled", "{pfx} &aSocialspy mode enabled.");
		messages.addDefault("commands.socialspy.disabled", "{pfx} &aSocialspy mode disabled.");
		
		messages.addDefault("chat.broadcast-format.local", "&8[&c&lBroadcast&8] &f{0}");
		messages.addDefault("chat.broadcast-format.global", "&8[&c&lBroadcast&8] &f{0}");
		
		messages.addDefault("chat.antispam.notification-format.text", "{pfx} &f{player} &cin &f{location} &chas tried to say:\n&f{message}");
		messages.addDefault("chat.antispam.notification-format.hover", "&c&l{player}\n&7Location: &f{location}\n&7Reason: &f{reason}\n\n&e&oClick to perform a /mute!");
		messages.addDefault("chat.antispam.no-url", "{pfx} &cYour message contains a disallowed URL.");
		messages.addDefault("chat.antispam.no-ip-address", "{pfx} &cYour message contains a disallowed IP address.");
		messages.addDefault("chat.antispam.no-swear", "{pfx} &cYour message contains disallowed words.");
		messages.addDefault("chat.antispam.no-caps", "{pfx} &cYou cannot use more than &f{0}% &cof caps in messages longer than &f{1} &ccharacters. Buy a &fVIP package &cto bypass this restriction.");
		messages.addDefault("chat.antispam.no-flood", "{pfx} &cYou cannot chat more than once every &f{0} &cseconds. Buy a &fVIP package &cto bypass this restriction.");
		messages.addDefault("chat.antispam.no-spam", "{pfx} &cYou can write two identical messages every &f{0} &cseconds. Buy a &fVIP package &cto bypass this restriction.");
		messages.addDefault("chat.no-format", "{pfx} &cYou cannot write formatted messages. Buy a &fVIP package &cto bypass this restriction.");
		messages.addDefault("chat.no-blank-messages", "{pfx} &cYou cannot send blank messages.");
		messages.addDefault("chat.pinged", "{pfx} &eYou got pinged by &f{0}&e.");
		messages.addDefault("chat.cannot-ping", "{pfx} &cYou can ping a player once every &f{0}&c.");
		messages.addDefault("chat.nobody-read", "{pfx} &eNobody has read your message.");
		
		messages.addDefault("chat.log.searching", "{pfx} &eSearching for logged messages...");
		messages.addDefault("chat.log.found", "{pfx} &aThe following messages were found:");
		messages.addDefault("chat.log.not-found", "{pfx} &cNo logged messages were found.");
		messages.addDefault("chat.log.message-format.public.text", "&e[{date}] &8- &f{content}");
		messages.addDefault("chat.log.message-format.public.hover", "&aType: &epublic message\n&aSender: &f{sender}\n&aRank: &f{rank_id}\n&aServer: &f{server}\n&aWorld: &f{world}\n&aChannel: &f{channel_id}\n&aDate: &f{date}\n&aDenied: &f{denied}");
		messages.addDefault("chat.log.message-format.public.global-format.yes", "global");
		messages.addDefault("chat.log.message-format.public.global-format.no", "local");
		messages.addDefault("chat.log.message-format.private.text", "&e[{date}] &8- &f&o{content}");
		messages.addDefault("chat.log.message-format.private.hover", "&aType: &bprivate message\n&aSender: &f{sender}\n&aRecipient: &f{recipient}\n&aRank: &f{rank_id}\n&aServer: &f{server}\n&aWorld: &f{world}\n&aDate: &f{date}\n&aDenied: &f{denied}");
		messages.addDefault("chat.log.message-format.date-format", "MM/dd hh:mm:ss a");
		messages.addDefault("chat.log.message-format.denied-format.yes", "&4denied &f({deny_chat_reason})");
		messages.addDefault("chat.log.message-format.denied-format.no", "&2allowed");
		
		messages.addDefault("chat.channel.join.already-listening", "{pfx} &cYou are already listening to &f{0}&c. Use &f/channel switch {1} &cto switch to write mode.");
		messages.addDefault("chat.channel.join.joined", "{pfx} &aNow listening to &f{0}&a. Use &f/channel switch {1} &ato switch to write mode.");
		messages.addDefault("chat.channel.leave.not-listening", "{pfx} &cYou are not listening to &f{0}&c.");
		messages.addDefault("chat.channel.leave.left", "{pfx} &aYou are no longer listening to &f{0}&a.");
		messages.addDefault("chat.channel.leave.cannot-leave", "{pfx} &cYou cannot leave the channel you are writing in (&f{0}&c).");
		messages.addDefault("chat.channel.switch.not-listening", "{pfx} &cYou are not listening to &f{0}&c. Use &f/channel join {1} &cto join.");
		messages.addDefault("chat.channel.switch.cannot-write", "{pfx} &cYou do not have the permission to write in &f{0}&c.");
		messages.addDefault("chat.channel.switch.switched", "{pfx} &aNow writing in &f{0}&a.");
		messages.addDefault("chat.channel.info.self", "{pfx} &aYou are currently listening to &f{0} &aand writing in &f{1}&a.");
		messages.addDefault("chat.channel.info.channel", "{pfx} &aInformation about channel &f{channel_id} &a(&f{channel_display_name}&a):\n&eType: &f{channel_type}\n&eAliases: &f{channel_aliases}\n&eLanguages: &f{channel_languages}\n&eAccess: &f{channel_access}\n&eWriting: &f{channel_writing}");
		messages.addDefault("chat.channel.info.local", "&eRange: &f{channel_range} blocks");
		messages.addDefault("chat.channel.info.discord", "&eDiscord channel: &f{channel_discord_channel_display_name} ({channel_discord_channel_id})");
		messages.addDefault("chat.channel.info.telegram", "&eTelegram chat: &f{channel_telegram_chat_display_name} ({channel_telegram_chat_id})");
		messages.addDefault("chat.channel.list.header", "{pfx} &aHere are displayed the loaded channels:");
		messages.addDefault("chat.channel.list.message-format.text", "&f{channel_id} &e(&f{channel_display_name}&e) &8- &etype: &f{channel_type}");
		messages.addDefault("chat.channel.list.message-format.hover", "&a&l{channel_id}\n&7Display name: &f{channel_display_name}\n&7Type: &f{channel_type}\n\n&e&oClick for more info!");
		messages.addDefault("chat.channel.spy.enabled", "{pfx} &aChat channel spy mode enabled.");
		messages.addDefault("chat.channel.spy.disabled", "{pfx} &aChat channel spy mode disabled.");
		messages.addDefault("chat.channel.types.local", "local (ranged)");
		messages.addDefault("chat.channel.types.world", "world-based");
		messages.addDefault("chat.channel.types.global", "global (server-wide)");
		messages.addDefault("chat.channel.types.network", "network (proxy-wide)");
		messages.addDefault("chat.channel.types.discord", "Discord-linked");
		messages.addDefault("chat.channel.types.telegram", "Telegram-linked");
		messages.addDefault("chat.channel.restrictions.restricted", "&4restricted");
		messages.addDefault("chat.channel.restrictions.free", "&2free");
		messages.addDefault("chat.channel.invalid", "{pfx} &f{0} &cis not a valid channel.");
		messages.addDefault("chat.channel.cannot-access", "{pfx} &cYou do not have the permission to access &f{0}&c.");
		
		messages.addDefault("guis.no-permission", "{pfx} &cYou do not have the permission to use that GUI.");
		messages.addDefault("guis.no-permission-icon", "{pfx} &cYou do not have the permission to use that icon.");
		messages.addDefault("guis.player-went-offline", "{pfx} &f{0} &cwent offline so your open GUI (&f{1}&c) has been closed.");
		messages.addDefault("guis.unloaded", "{pfx} &eYour open GUI &f{0} &ehas been closed after &f{1} &eof inactivity.");
		messages.addDefault("guis.still-loading", "{pfx} &cGUI &f{0} &cis still loading. Retry in few seconds.");
		messages.addDefault("guis.banwave-executing", "{pfx} &cA banwave is being executed. Retry in few seconds.");
		
		messages.addDefault("ban.ban-format.kick", "&8[&a&lLogin&8] &cDisconnected from server &f{server}&c: &fyou got banned.\n\n&eBan ID: &f#{id}\n&eStaff member: &f{staff_member}\n&eReason: &f{reason}\n&eDate: &f{date}\n&eRemaining time: &f{remaining_time}\n&eType: &f{type}\n\n&7You can make an unban request on our website.\n\n&7If the request gets refused, you can buy the unban on our Store.");
		messages.addDefault("ban.ban-format.chat", "&8[&a&lLogin&8] &cDisconnected from server &f{server}&c: &fyou got banned. &eBan ID: &f#{id}&e, Staff member: &f{staff_member}&e, reason: &f{reason}&e, remaining time: &f{remaining_time}&e, type: &f{type}&e.");
		messages.addDefault("ban.command-usage", "{pfx} &cThe syntax is wrong. Usage: &f{0}&c. Additional flags:\n&f&l-silent &8- &emake it silent: only the Staff will be notified.\n&f&l-type &8- &ealternate ban scope (default is &f{1}&e).");
		messages.addDefault("ban.banwave.start", "{pfx} &cA new banwave has started. Total bans amount will be printed at the end. Cheaters, you will have &nno&c escape...");
		messages.addDefault("ban.banwave.end", "{pfx} &aBanwave completed in &f{0}&a. Banned: &f{1}x&a.");
		messages.addDefault("ban.banwave.added", "{pfx} &cNew banwave entry: &f{player}&c (type: &f{type}&c), for &f{duration}&c: &f{reason}&c.");
		messages.addDefault("ban.banwave.added-ip", "{pfx} &cNew banwave entry: &f{ip_address}&c (type: &f{type}&c), for &f{duration}&c: &f{reason}&c.");
		messages.addDefault("ban.banwave.updated", "{pfx} &cBanwave entry updated: &f{player}&c (type: &f{type}&c), for &f{duration}&c: &f{reason}&c.");
		messages.addDefault("ban.banwave.updated-ip", "{pfx} &cBanwave entry updated: &f{ip_address}&c (type: &f{type}&c), for &f{duration}&c: &f{reason}&c.");
		messages.addDefault("ban.banwave.removed", "{pfx} &f{player} &ahas been removed from the next banwave's entries.");
		messages.addDefault("ban.banwave.removed-ip", "{pfx} &f{ip_address} &ahas been removed from the next banwave's entries.");
		messages.addDefault("ban.banwave.invalid-type.account", "{pfx} &cYou cannot remove account entries with this command. Use &f/removebanwaveentry <player> &cinstead.");
		messages.addDefault("ban.banwave.invalid-type.ip", "{pfx} &cYou cannot remove IP entries with this command. Use &f/removebanwaveentryip <player|IP address> &cinstead.");
		messages.addDefault("ban.banwave.not-contained", "{pfx} &f{0} &cis not contained in the next banwave's entries.");
		messages.addDefault("ban.preset-reasons", Arrays.asList("Disallowed griefing", "Cheating", "Ban evading", "Bug abuse", "Multi account", "Spam of disallowed URL/address"));
		messages.addDefault("ban.unspecified-reason", "Reason not specified.");
		messages.addDefault("ban.no-ips-stored", "{pfx} &cThere are no IP addresses stored for that player.");
		messages.addDefault("ban.offline-player", "{pfx} &cYou cannot ban offline players.");
		messages.addDefault("ban.invalid-type.account", "{pfx} &cYou cannot remove account bans with this command. Use &f/unban <player> &cinstead.");
		messages.addDefault("ban.invalid-type.ip", "{pfx} &cYou cannot remove IP bans with this command. Use &f/unbanip <player|IP address> &cinstead.");
		messages.addDefault("ban.banned.other", "{pfx} &f{player} &chas been banned (ID: &f#{id}&c, type: &f{type}&c) for &f{duration} &cby &f{staff_member}&c: &f{reason}&c.");
		messages.addDefault("ban.banned-ip.other", "{pfx} &f{ip_address} &chas been banned (ID: &f#{id}&c, type: &f{type}&c) for &f{duration} &cby &f{staff_member}&c: &f{reason}&c.");
		messages.addDefault("ban.updated.other", "{pfx} &f{player}&c's active ban (ID: &f#{id}&c, type: &f{type}&c) has been updated by &f{staff_member}&c. Duration: &f{duration}&c, reason: &f{reason}&c.");
		messages.addDefault("ban.updated-ip.other", "{pfx} &f{ip_address}&c's active ban (ID: &f#{id}&c, type: &f{type}&c) has been updated by &f{staff_member}&c. Duration: &f{duration}&c, reason: &f{reason}&c.");
		messages.addDefault("ban.expired.other", "{pfx} &f{player}&e's ban (ID: &f#{id}&e) has just expired.");
		messages.addDefault("ban.expired-ip.other", "{pfx} &f{ip_address}&e's ban (ID: &f#{id}&e) has just expired.");
		messages.addDefault("ban.unbanned.other", "{pfx} &f{player} &ehas been unbanned (ID: &f#{id}&e) by &f{who_unbanned}&e.");
		messages.addDefault("ban.unbanned-ip.other", "{pfx} &f{ip_address} &ehas been unbanned (ID: &f#{id}&e) by &f{who_unbanned}&e.");
		messages.addDefault("ban.cannot-ban", "{pfx} &f{0} &ccannot be banned.");
		messages.addDefault("ban.not-banned", "{pfx} &f{0} &cis not banned.");
		messages.addDefault("ban.types-format.account", "account");
		messages.addDefault("ban.types-format.ip-address", "IP address");
		messages.addDefault("ban.active-format.yes", "&2active");
		messages.addDefault("ban.active-format.no", "&4disactive");
		messages.addDefault("ban.global-format.yes", "global");
		messages.addDefault("ban.global-format.no", "local");
		messages.addDefault("ban.silent-format.yes", "yes");
		messages.addDefault("ban.silent-format.no", "no");
		
		messages.addDefault("warning.warned.self", "{pfx} &cYou have been warned (ID: &f#{id}&c) (&f{amount}/{max_amount}&c) for &f{duration} &cby &f{staff_member}&c: &f{reason}&c.");
		messages.addDefault("warning.warned.other", "{pfx} &f{player} &chas been warned (ID: &f#{id}&c) (&f{amount}/{max_amount}&c) for &f{duration} &cby &f{staff_member}&c: &f{reason}&c.");
		messages.addDefault("warning.command-usage", "{pfx} &cThe syntax is wrong. Usage: &f{0}&c. Additional flags:\n&f&l-silent &8- &emake silent: only the Staff will be notified.\n&f&l-type &8- &ealternate warning scope (default is &f{1}&e).");
		messages.addDefault("warning.preset-reasons", Arrays.asList("Inappropriate language", "Bug abuse", "Spam of disallowed URL/address", "Disallowed (self) advertising"));
		messages.addDefault("warning.expired.self", "{pfx} &aYour warning (ID: &f#{id}&a) has just expired.");
		messages.addDefault("warning.expired.other", "{pfx} &f{player}&e's warning (ID: &f#{id}&e) has just expired.");
		messages.addDefault("warning.removed.self", "{pfx} &f{who_unwarned} &ahas removed your last warning (ID: &f#{id}&a).");
		messages.addDefault("warning.removed.other", "{pfx} &f{who_unwarned} &ehas removed &f{player}&e's last warning (ID: &f#{id}&e).");
		messages.addDefault("warning.cleared.self", "{pfx} &f{who_unwarned} &ahas cleared your warnings.");
		messages.addDefault("warning.cleared.other", "{pfx} &f{who_unwarned} &ehas cleared &f{player}&e's warnings.");
		messages.addDefault("warning.unspecified-reason", "Reason not specified.");
		messages.addDefault("warning.cannot-warn", "{pfx} &f{0} &ccannot be warned.");
		messages.addDefault("warning.not-warned", "{pfx} &f{0} &cis not warned.");
		messages.addDefault("warning.active-format.yes", "&2active");
		messages.addDefault("warning.active-format.no", "&4disactive");
		messages.addDefault("warning.global-format.yes", "global");
		messages.addDefault("warning.global-format.no", "local");
		messages.addDefault("warning.silent-format.yes", "yes");
		messages.addDefault("warning.silent-format.no", "no");
		
		messages.addDefault("kick.kick-format.kick", "&8[&a&lLogin&8] &cDisconnected from server &f{server}&c: &fyou got kicked.\n\n&eKick ID: &f#{id}\n&eStaff member: &f{staff_member}\n&eReason: &f{reason}\n&eDate: &f{date}");
		messages.addDefault("kick.kick-format.chat", "&8[&a&lLogin&8] &cDisconnected from server &f{server}&c: &fyou got kicked. &eKick ID: &f#{id}&e, Staff member: &f{staff_member}&e, reason: &f{reason}&e.");
		messages.addDefault("kick.fakekick-format.kick", "{fake_reason}");
		messages.addDefault("kick.fakekick-format.chat", "&8[&a&lLogin&8] &cDisconnected from server &f{server}&c: &f{fake_reason}&c.");
		messages.addDefault("kick.command-usage.kick", "{pfx} &cThe syntax is wrong. Usage: &f{0}&c. Additional flags:\n&f&l-silent &8- &emake it silent: only the Staff will be notified.\n&f&l-type &8- &ealternate kick type (default is &f{1}&e).");
		messages.addDefault("kick.command-usage.fakekick", "{pfx} &cThe syntax is wrong. Usage: &f{0}&c. Additional flags:\n&f&l-chat &8- &esend to lobby server with kick reason in chat.");
		messages.addDefault("kick.preset-reasons", Arrays.asList("Caps", "Flood", "Inappropriate language", "Insults"));
		messages.addDefault("kick.unspecified-reason", "Reason not specified.");
		messages.addDefault("kick.kicked.other", "{pfx} &f{player} &chas been kicked (ID: &f#{id}&c) by &f{staff_member}&c: &f{reason}&c.");
		messages.addDefault("kick.cannot-kick", "{pfx} &f{0} &ccannot be kicked.");
		messages.addDefault("kick.fakekicked", "{pfx} &f{0} &ahas been fake kicked successfully; message: &f{1}&a.");
		messages.addDefault("kick.types-format.kick", "kick");
		messages.addDefault("kick.types-format.chat", "chat");
		messages.addDefault("kick.silent-format.yes", "yes");
		messages.addDefault("kick.silent-format.no", "no");
		
		messages.addDefault("mute.muted.self", "{pfx} &cYou have been muted (ID: &f#{id}&c) for &f{duration} &cby &f{staff_member}&c: &f{reason}&c.");
		messages.addDefault("mute.muted.other", "{pfx} &f{player} &chas been muted (ID: &f#{id}&c) for &f{duration} &cby &f{staff_member}&c: &f{reason}&c.");
		messages.addDefault("mute.updated.self", "{pfx} &cYour active mute (ID: &f#{id}&c) has been updated by &f{staff_member}&c. Duration: &f{duration}&c, reason: &f{reason}&c.");
		messages.addDefault("mute.updated.other", "{pfx} &f{player}&c's active mute (ID: &f#{id}&c) has been updated by &f{staff_member}&c. Duration: &f{duration}&c, reason: &f{reason}&c.");
		messages.addDefault("mute.command-usage", "{pfx} &cThe syntax is wrong. Usage: &f{0}&c. Additional flags:\n&f&l-silent &8- &emake it silent: only the Staff will be notified.\n&f&l-type &8- &ealternate mute scope (default is &f{1}&e).");
		messages.addDefault("mute.preset-reasons", Arrays.asList("Caps", "Flood", "Inappropriate language", "Insults", "Blasphemies", "Spam of disallowed URL/address"));
		messages.addDefault("mute.expired.self", "{pfx} &aYour mute (ID: &f#{id}&a) has just expired.");
		messages.addDefault("mute.expired.other", "{pfx} &f{player}&e's mute (ID: &f#{id}&e) has just expired.");
		messages.addDefault("mute.unmuted.self", "{pfx} &aYou have been unmuted (ID: &f#{id}&a) by &f{who_unmuted}&a.");
		messages.addDefault("mute.unmuted.other", "{pfx} &f{player} &ehas been unmuted (ID: &f#{id}&e) by &f{who_unmuted}&e.");
		messages.addDefault("mute.no-chat", "{pfx} &cYou are muted (ID: &f#{id}&c) for &f{remaining_time} &cstill: &f{reason}&c.");
		messages.addDefault("mute.unspecified-reason", "Reason not specified.");
		messages.addDefault("mute.cannot-mute", "{pfx} &f{0} &ccannot be muted.");
		messages.addDefault("mute.not-muted", "{pfx} &f{0} &cis not muted.");
		messages.addDefault("mute.active-format.yes", "&2active");
		messages.addDefault("mute.active-format.no", "&4disactive");
		messages.addDefault("mute.global-format.yes", "global");
		messages.addDefault("mute.global-format.no", "local");
		messages.addDefault("mute.silent-format.yes", "yes");
		messages.addDefault("mute.silent-format.no", "no");
		
		messages.addDefault("ping.self", "{pfx} &eYou have a ping of {ping_format} ms&e. Quality: &f{ping_quality_text}&e.");
		messages.addDefault("ping.other", "{pfx} &f{other} &ehas a ping of {ping_format} ms&e. Quality: &f{ping_quality_text}&e.");
		messages.addDefault("ping.excellent.color", "&2");
		messages.addDefault("ping.excellent.text", "&2excellent");
		messages.addDefault("ping.great.color", "&a");
		messages.addDefault("ping.great.text", "&agreat");
		messages.addDefault("ping.good.color", "&e");
		messages.addDefault("ping.good.text", "&egood");
		messages.addDefault("ping.funny.color", "&d");
		messages.addDefault("ping.funny.text", "&dfunny");
		messages.addDefault("ping.good-2.color", "&e");
		messages.addDefault("ping.good-2.text", "&egood");
		messages.addDefault("ping.poor.color", "&6");
		messages.addDefault("ping.poor.text", "&6poor");
		messages.addDefault("ping.bad.color", "&c");
		messages.addDefault("ping.bad.text", "&cbad");
		messages.addDefault("ping.unplayable.color", "&4");
		messages.addDefault("ping.unplayable.text", "&4unplayable");
		
		messages.addDefault("tps-qualities.excellent", "&2");
		messages.addDefault("tps-qualities.great", "&a");
		messages.addDefault("tps-qualities.good", "&e");
		messages.addDefault("tps-qualities.poor", "&6");
		messages.addDefault("tps-qualities.bad", "&c");
		messages.addDefault("tps-qualities.unplayable", "&4");
		
		messages.addDefault("mspt-qualities.excellent", "&2");
		messages.addDefault("mspt-qualities.great", "&a");
		messages.addDefault("mspt-qualities.good", "&e");
		messages.addDefault("mspt-qualities.poor", "&6");
		messages.addDefault("mspt-qualities.bad", "&c");
		messages.addDefault("mspt-qualities.unplayable", "&4");
		
		messages.addDefault("vanish.enabled.self", "{pfx} &aYou are now vanished.");
		messages.addDefault("vanish.enabled.other", "{pfx} &f{0} &ais now vanished.");
		messages.addDefault("vanish.disabled.self", "{pfx} &eYou are no longer vanished.");
		messages.addDefault("vanish.disabled.other", "{pfx} &f{0} &eis no longer vanished.");
		messages.addDefault("vanish.no-permission", "{pfx} &f{0} &cdoes not have the permission to vanish.");
		messages.addDefault("vanish.no-chat", "{pfx} &cYou cannot use the public chat while you are vanished.");
		messages.addDefault("vanish.cosmetics-reset", "{pfx} &eThe following cosmetics were reset: &f{0}&e.");
		messages.addDefault("vanish.gadgetsmenu-reload", "{pfx} &cGadgetsMenu has been reloaded. Its cosmetics have not been reset.");
		messages.addDefault("vanish.fakejoin.no-fakequit", "{pfx} &cYou have to use &f/fakequit &cfirst.");
		messages.addDefault("vanish.fakejoin.performed", "{pfx} &aFake join performed. You are no longer vanished.");
		messages.addDefault("vanish.fakequit.already-vanished", "{pfx} &cYou cannot perform a fake quit while vanished.");
		messages.addDefault("vanish.fakequit.already-fakequit", "{pfx} &cVanish cannot be turned off while fake quit. Use &f/fakejoin &cinstead.");
		messages.addDefault("vanish.fakequit.performed", "{pfx} &aFake quit performed. You are now vanished.");
		messages.addDefault("vanish.safe-teleport", "{pfx} &aYou have been safely teleported to &f{0}&a.");
		
		messages.addDefault("languages.set", "{pfx} &aLanguage set to &f{0}&a.");
		messages.addDefault("languages.set-already", "{pfx} &cYour language is already set to &f{0}&c.");
		messages.addDefault("languages.invalid", "{pfx} &f{0} &cis not a valid language. Here are the loaded languages: &f{1}&c.");
		messages.addDefault("languages.detected.text", "{pfx} &eIt seems that your language is {0}&e. Click this message to set it as your default language.");
		messages.addDefault("languages.detected.hover", "&9[Click here to change your language]");
		
		messages.addDefault("page-switcher.footer", "{pfx} &aPage &f{current_page}/{max_page}&a. Browse: &f{page_switcher}");
		messages.addDefault("page-switcher.invalid", "{pfx} &cPage &f{current_page} &cnot found. Last page: &f{max_page}&c.");
		messages.addDefault("page-switcher.previous.text", "&8[&e&l&n⬅ {previous_page}/{max_page}&8]");
		messages.addDefault("page-switcher.previous.hover", "&9[Click here to go back to page {previous_page}]");
		messages.addDefault("page-switcher.next.text", "&8[&e&l&n{next_page}/{max_page} ➡&8]");
		messages.addDefault("page-switcher.next.hover", "&9[Click here to go to page {next_page}]");
		
		messages.addDefault("timestamps.invalid", "{pfx} &cInvalid timestamp. Use this format: &f1d,3h,35m,20s&c.");
		messages.addDefault("timestamps.rank-limitation", "{pfx} &cThe maximum duration allowed for your rank (&f{0}&c) is &f{1}&c.");
		messages.addDefault("timestamps.now", "now");
		messages.addDefault("timestamps.ever", "ever");
		messages.addDefault("timestamps.never", "never");
		messages.addDefault("timestamps.second", " second");
		messages.addDefault("timestamps.seconds", " seconds");
		messages.addDefault("timestamps.minute", " minute");
		messages.addDefault("timestamps.minutes", " minutes");
		messages.addDefault("timestamps.hour", " hour");
		messages.addDefault("timestamps.hours", " hours");
		messages.addDefault("timestamps.day", " day");
		messages.addDefault("timestamps.days", " days");
		messages.addDefault("timestamps.week", " week");
		messages.addDefault("timestamps.weeks", " weeks");
		messages.addDefault("timestamps.month", " month");
		messages.addDefault("timestamps.months", " months");
		messages.addDefault("timestamps.year", " year");
		messages.addDefault("timestamps.years", " years");
		
		messages.addDefault("placeholders.nobody", "nobody");
		messages.addDefault("placeholders.not-present", "not present");
		
		messages.addDefault("proxy-messages.help", "{pfx} &aCommands for &c&lChat&f&lPlugin &fv{0}&a by &9Remigio07&a:\n&f&l/cpp status &8- &eDisplay current network status.\n&f&l/cpp debug &8- &eToggle the debug/verbose mode.\n&f&l/cpp reload &8- &eReload the ChatPlugin system.\n&f&l/cpp version &8- &eDisplay current plugin version.\n&f&l/cpp info &8- &eShow useful info about this plugin.");
		messages.addDefault("proxy-messages.status", "{pfx} &aCurrent network status:\n&eProxy version: &f{network_version} (protocol: {network_protocol})\n&eChatPlugin version: &f{plugin_version}\n&eUptime: &f{uptime}\n&eUsed memory: &f{used_memory}/{max_memory} MB\n&eAllocated memory: &f{total_memory} MB\n&eFree memory: &f{free_memory} MB\n&eEnabled players: &f{enabled_players}x\n&eStartup time: &f{startup_time} ms\n&eLast reload time: &f{last_reload_time} ms");
		
		messages.save();
	}
	
	public void addRanksDefaults(boolean forceAdd) throws IOException {
		Configuration ranks = configurations.get(ConfigurationType.RANKS);
		boolean fileMissing = !Files.exists(ranks.getPath());
		
		if (fileMissing)
			ranks.createFile();
		else if (!forceAdd)
			return;
		
		ranks.addDefault("ranks.settings.luckperms-mode", false);
		ranks.addDefault("ranks.settings.sorting.enabled", true);
		ranks.addDefault("ranks.settings.sorting.from-tablist-top", true);
		
		if (fileMissing) {
			ranks.addDefault("ranks.default.display-name", "User");
			ranks.addDefault("ranks.default.prefix", "&8[&f&lUser&8] ");
			ranks.addDefault("ranks.default.suffix", "");
			ranks.addDefault("ranks.default.tag.prefix", "");
			ranks.addDefault("ranks.default.tag.suffix", "");
			ranks.addDefault("ranks.default.tag.name-color", "");
			ranks.addDefault("ranks.default.chat-color", "");
			ranks.addDefault("ranks.default.descriptions.english", "&aRank: &f&lUser\n&aType: &fdefault");
			ranks.addDefault("ranks.default.descriptions.italian", "&aRango: &f&lUser\n&aTipo: &fpredefinito");
			
			ranks.addDefault("ranks.vip.display-name", "VIP");
			ranks.addDefault("ranks.vip.prefix", "&8[&d&lVIP&8] ");
			ranks.addDefault("ranks.vip.suffix", " &a[*]");
			ranks.addDefault("ranks.vip.tag.prefix", "&8[&d&lV&8] ");
			ranks.addDefault("ranks.vip.tag.suffix", " &a[*]");
			ranks.addDefault("ranks.vip.tag.name-color", "");
			ranks.addDefault("ranks.vip.chat-color", "");
			ranks.addDefault("ranks.vip.descriptions.english", "&aRank: &d&lVIP\n&aType: &fpaid &o($4.99)");
			ranks.addDefault("ranks.vip.descriptions.italian", "&aRango: &d&lVIP\n&aTipo: &fpagato &o($4.99)");
			
			ranks.addDefault("ranks.staff.display-name", "Staff");
			ranks.addDefault("ranks.staff.prefix", "&8[&b&lStaff&8] ");
			ranks.addDefault("ranks.staff.suffix", " &4[*]");
			ranks.addDefault("ranks.staff.tag.prefix", "&8[&b&lS&8] ");
			ranks.addDefault("ranks.staff.tag.suffix", " &4[*]");
			ranks.addDefault("ranks.staff.tag.name-color", "");
			ranks.addDefault("ranks.staff.chat-color", "");
			ranks.addDefault("ranks.staff.descriptions.english", "&aRank: &b&lStaff\n&aType: &fadmin.");
			ranks.addDefault("ranks.staff.descriptions.italian", "&aRango: &b&lStaff\n&aTipo: &famministr.");
			ranks.addDefault("ranks.staff.max-punishment-durations.ban", "14d");
			ranks.addDefault("ranks.staff.max-punishment-durations.mute", "12h");
		} ranks.save();
	}
	
	public void addChatDefaults(boolean forceAdd) throws IOException {
		Configuration chat = configurations.get(ConfigurationType.CHAT);
		boolean fileMissing = !Files.exists(chat.getPath());
		
		if (fileMissing)
			chat.createFile();
		else if (!forceAdd)
			return;
		
		chat.addDefault("chat.enabled", true);
		chat.addDefault("chat.event.override", true);
		chat.addDefault("chat.event.priority", Environment.isBukkit() ? "HIGH" : "LATE"); // TODO Fabric
		chat.addDefault("chat.format", "&7«&f{prefix}{tag_name_color}{player}{suffix}&7» &f");
		chat.addDefault("chat.recognized-tlds", Arrays.asList("com", "net", "org", "me", "io", "edu", "gov", "int", "info", "pro", "xyz", "gg", "dev", "link", "eu", "it", "de", "fr", "es", "br", "jp", "ru", "uk", "co.uk"));
		chat.addDefault("chat.placeholder-types", Arrays.asList("PLAYER"));
		
		chat.addDefault("chat.formatted-chat.enabled", true);
		chat.addDefault("chat.formatted-chat.send-anyway", true);
		
		chat.addDefault("chat.channels.enabled", false);
		chat.addDefault("chat.channels.reading-notification-enabled", true);
		chat.addDefault("chat.channels.spy.on-join-enabled", true);
		chat.addDefault("chat.channels.spy.format", "&7«&8[&4&lS&8] &f{prefix}{tag_name_color}{player}{suffix}&7» &f");
		chat.addDefault("chat.channels.default.listening", Arrays.asList("default", "global"));
		chat.addDefault("chat.channels.default.writing", "default");
		chat.addDefault("chat.channels.values.default.display-name", "Default");
		chat.addDefault("chat.channels.values.default.format", "&7«&f{prefix}{tag_name_color}{player}{suffix}&7» &f");
		chat.addDefault("chat.channels.values.default.type", "LOCAL");
		chat.addDefault("chat.channels.values.default.console-included", true);
		chat.addDefault("chat.channels.values.default.aliases", Arrays.asList("ranged", "local", "l"));
		chat.addDefault("chat.channels.values.default.languages", Arrays.asList("*"));
		chat.addDefault("chat.channels.values.default.range", 50);
		chat.addDefault("chat.channels.values.global.display-name", "Global");
		chat.addDefault("chat.channels.values.global.prefix", "!");
		chat.addDefault("chat.channels.values.global.format", "&7«&8[&2&lG&8] &f{prefix}{tag_name_color}{player}{suffix}&7» &f");
		chat.addDefault("chat.channels.values.global.type", "GLOBAL");
		chat.addDefault("chat.channels.values.global.access-restricted", false);
		chat.addDefault("chat.channels.values.global.writing-restricted", true);
		chat.addDefault("chat.channels.values.global.console-included", true);
		chat.addDefault("chat.channels.values.global.aliases", Arrays.asList("g"));
		chat.addDefault("chat.channels.values.global.languages", Arrays.asList("*"));
		
		chat.addDefault("chat.antispam.enabled", true);
		chat.addDefault("chat.antispam.leet-filter-enabled", true);
		chat.addDefault("chat.antispam.prevention.urls.enabled", true);
		chat.addDefault("chat.antispam.prevention.urls.allowed-domains", Arrays.asList("remigio07.me", "megaproserver.com", "youtube.com"));
		chat.addDefault("chat.antispam.prevention.urls.whitelist", Arrays.asList("spigotmc.org/forums"));
		chat.addDefault("chat.antispam.prevention.ips.enabled", true);
		chat.addDefault("chat.antispam.prevention.ips.whitelist", Arrays.asList("127.0.0.1"));
		chat.addDefault("chat.antispam.seconds-between-messages", 2);
		chat.addDefault("chat.antispam.seconds-between-same-messages", 10);
		chat.addDefault("chat.antispam.max-caps-length", 4);
		chat.addDefault("chat.antispam.max-caps-percentage", 50F);
		chat.addDefault("chat.antispam.highlight-color", "&4&n");
		chat.addDefault("chat.antispam.messages-whitelist", Arrays.asList("hello", "hey", "ok", "ciao"));
		chat.addDefault("chat.antispam.words-blacklist", Arrays.asList(
				" arse ", "arsehole", " ass ", "asshole", "bastard", "bitch", "blowjob", "bollock", "bullshit", "cocksucker", " crap", " cum", "cunt", "dick", "dumbass", "faggot", "fuck", "gtfo", "handjob", "kys", "nigga", "nigger", "pussy", " shit", "slut", "twat ", " wank", "whore",
				"arrap", "bocchin", " caca", "caga", " cazz", "checc", "coglion", "cojon", "culo", "ditalin", "figa", "fotter", "fottut", "froci", "merd", "mignott", "minchi", "negr", "puttan", "pompin", "ricchion", "sborr", "stronz", "troia", " troie ", "zoccola", "zoccole"
				));
		
		chat.addDefault("chat.player-ping.enabled", true);
		chat.addDefault("chat.player-ping.at-sign-required", false);
		chat.addDefault("chat.player-ping.per-player-cooldown", "10s");
		chat.addDefault("chat.player-ping.color", "&b");
		chat.addDefault("chat.player-ping.sound.enabled", true);
		chat.addDefault("chat.player-ping.sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "entity.experience_orb.pickup" : "random.orb");
		chat.addDefault("chat.player-ping.sound.volume", 1F);
		chat.addDefault("chat.player-ping.sound.pitch", 1F);
		chat.addDefault("chat.player-ping.titles.enabled", true);
		chat.addDefault("chat.player-ping.titles.fade-in-ms", 500L);
		chat.addDefault("chat.player-ping.titles.stay-ms", 3500L);
		chat.addDefault("chat.player-ping.titles.fade-out-ms", 1000L);
		chat.addDefault("chat.player-ping.titles.titles.english", "&b@{player}");
		chat.addDefault("chat.player-ping.titles.titles.italian", "&b@{player}");
		chat.addDefault("chat.player-ping.titles.subtitles.english", "&ehas pinged you in the chat");
		chat.addDefault("chat.player-ping.titles.subtitles.italian", "&eti ha menzionato in chat");
		
		chat.addDefault("chat.private-messages.enabled", true);
		chat.addDefault("chat.private-messages.format.sent", "&7«&8[&b✎&8] &fYou &e➡ &f{recipient}&7» &f");
		chat.addDefault("chat.private-messages.format.received", "&7«&8[&b✎&8] &f{sender} &e➡ &fYou&7» &f");
		chat.addDefault("chat.private-messages.format.socialspy", "&7«&8[&4&lSS&8] &f{sender} &e➡ &f{recipient}&7» &f");
		chat.addDefault("chat.private-messages.format.placeholder.sender", "{player}");
		chat.addDefault("chat.private-messages.format.placeholder.recipient", "{player}");
		chat.addDefault("chat.private-messages.format.placeholder.placeholder-types", Arrays.asList("JUST_NAME"));
		chat.addDefault("chat.private-messages.sound.enabled", true);
		chat.addDefault("chat.private-messages.sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "entity.experience_orb.pickup" : "random.orb");
		chat.addDefault("chat.private-messages.sound.volume", 1F);
		chat.addDefault("chat.private-messages.sound.pitch", 1F);
		chat.addDefault("chat.private-messages.advancements.enabled", VersionUtils.getVersion().isAtLeast(Version.V1_12));
		chat.addDefault("chat.private-messages.advancements.format", "&8[&b✎&8] &f{sender_plain}\n&7");
		chat.addDefault("chat.private-messages.advancements.max-message-length", 19);
		chat.addDefault("chat.private-messages.advancements.icon.material", "writable_book");
		chat.addDefault("chat.private-messages.advancements.icon.glowing", true);
		chat.addDefault("chat.private-messages.bypass-antispam-checks", Arrays.asList("CAPS", "FLOOD", "SPAM", "SWEAR"));
		chat.addDefault("chat.private-messages.socialspy-on-join-enabled", true);
		chat.addDefault("chat.private-messages.muted-players-blocked", false);
		chat.addDefault("chat.private-messages.reply-to-last-sender", true);
		
		chat.addDefault("chat.player-ignore.enabled", true);
		
		chat.addDefault("chat.hover-info.enabled", true);
		chat.addDefault("chat.hover-info.rank.enabled", true);
		chat.addDefault("chat.hover-info.player.enabled", true);
		chat.addDefault("chat.hover-info.player.click.action", "SUGGEST_COMMAND");
		chat.addDefault("chat.hover-info.player.click.value", "/msg {player} ");
		chat.addDefault("chat.hover-info.player.placeholder-types", Arrays.asList("PLAYER", "SERVER"));
		chat.addDefault("chat.hover-info.player.hovers.english", "&a&l{player}\n&7Language: &f{language_display_name}\n&7Time: &f{date_hour}\n&7Ping: {ping_format} ms\n\n&e&oClick to send a /msg!");
		chat.addDefault("chat.hover-info.player.hovers.italian", "&a&l{player}\n&7Lingua: &f{language_display_name}\n&7Orario: &f{date_hour}\n&7Ping: {ping_format} ms\n\n&e&oClicca per inviare un /msg!");
		chat.addDefault("chat.hover-info.url.enabled", true);
		chat.addDefault("chat.hover-info.url.default-https", true);
		chat.addDefault("chat.hover-info.url.color", "&b&n");
		chat.addDefault("chat.hover-info.url.hovers.english", "&9[Click here to open the URL]");
		chat.addDefault("chat.hover-info.url.hovers.italian", "&9[Clicca qui per aprire l'URL]");
		chat.addDefault("chat.hover-info.player-ping.enabled", true);
		chat.addDefault("chat.hover-info.instant-emoji.enabled", true);
		chat.addDefault("chat.hover-info.instant-emoji.hovers.english", "{emoji} &f{emoji_id}\n&7A default emoji. You can use this\n&7emoji everywhere on this server.");
		chat.addDefault("chat.hover-info.instant-emoji.hovers.italian", "{emoji} &f{emoji_id}\n&7Un'emoji predefinita. Puoi usare questa\n&7emoji ovunque su questo server.");
		
		chat.addDefault("chat.instant-emojis.enabled", true);
		chat.addDefault("chat.instant-emojis.tones", Arrays.asList("#FFFF55", "#F9E0C1", "#E3C29C", "#C6956C", "#A06940", "#5C473C"));
		
		if (fileMissing) {
			chat.addDefault("chat.instant-emojis.values.:)", "{emojis_tone}☺");
			chat.addDefault("chat.instant-emojis.values.<3", "&c❤");
			chat.addDefault("chat.instant-emojis.values.:smile:", "{emojis_tone}😃");
			chat.addDefault("chat.instant-emojis.values.:slight_smile:", "{emojis_tone}🙂");
			chat.addDefault("chat.instant-emojis.values.:grin:", "{emojis_tone}😁");
			chat.addDefault("chat.instant-emojis.values.:grinning:", "{emojis_tone}😀");
			chat.addDefault("chat.instant-emojis.values.:sunglasses:", "{emojis_tone}😎");
			chat.addDefault("chat.instant-emojis.values.:weary:", "{emojis_tone}😩");
			chat.addDefault("chat.instant-emojis.values.:tired:", "{emojis_tone}😫");
			chat.addDefault("chat.instant-emojis.values.:money:", "&a🤑");
			chat.addDefault("chat.instant-emojis.values.:nerd:", "{emojis_tone}🤓");
			chat.addDefault("chat.instant-emojis.values.:skull:", "&f☠");
			chat.addDefault("chat.instant-emojis.values.:alien:", "&a👽");
			chat.addDefault("chat.instant-emojis.values.:thumbs_up:", "{emojis_tone}👍");
			chat.addDefault("chat.instant-emojis.values.:thumbs_down:", "{emojis_tone}👎");
			chat.addDefault("chat.instant-emojis.values.:note:", "&9🎵");
			chat.addDefault("chat.instant-emojis.values.:pizza:", "&6🍕");
			chat.addDefault("chat.instant-emojis.values.:copyright:", "©");
			chat.addDefault("chat.instant-emojis.values.:trademark:", "™");
			chat.addDefault("chat.instant-emojis.values.:tm:", "™");
			chat.addDefault("chat.instant-emojis.values.:alpha:", "α");
			chat.addDefault("chat.instant-emojis.values.:beta:", "β");
			chat.addDefault("chat.instant-emojis.values.:gamma:", "γ");
			chat.addDefault("chat.instant-emojis.values.:euro:", "€");
			chat.addDefault("chat.instant-emojis.values.:pound:", "£");
			chat.addDefault("chat.instant-emojis.values.:yen:", "¥");
			chat.addDefault("chat.instant-emojis.values.:infinity:", "∞");
			chat.addDefault("chat.instant-emojis.values.:pi:", "π");
			chat.addDefault("chat.instant-emojis.values.:degree:", "°");
		}
		
		chat.addDefault("chat.staff-chat.enabled", true);
		chat.addDefault("chat.staff-chat.format.player", "&7«&8[&6&lSC&8] &f{tag_prefix}{tag_name_color}{player}{tag_suffix}&7» &f");
		chat.addDefault("chat.staff-chat.format.console", "&7«&8[&6&lSC&8] &8[&5&lC&8] &fConsole &4[***]&7» &f");
		chat.addDefault("chat.staff-chat.placeholder-types", Arrays.asList("PLAYER", "SERVER"));
		
		chat.addDefault("chat.log.enabled", true);
		chat.addDefault("chat.log.print-to-log-file", false);
		chat.addDefault("chat.log.messages-auto-cleaner-period", "120d");
		
		chat.save();
	}
	
	public void addTablistsDefaults(boolean forceAdd) throws IOException {
		Configuration tablists = configurations.get(ConfigurationType.TABLISTS);
		boolean fileMissing = !Files.exists(tablists.getPath());
		
		if (fileMissing)
			tablists.createFile();
		else if (!forceAdd)
			return;
		
		tablists.addDefault("tablists.settings.enabled", VersionUtils.getVersion().isAtLeast(Version.V1_8));
		tablists.addDefault("tablists.settings.random-order", false);
		tablists.addDefault("tablists.settings.sending-timeout-ms", 250L);
		tablists.addDefault("tablists.settings.player-names.teams-mode", true);
		tablists.addDefault("tablists.settings.player-names.update-timeout-ms", 10000L);
		tablists.addDefault("tablists.settings.player-names.prefix", "{tag_prefix}{tag_name_color}");
		tablists.addDefault("tablists.settings.player-names.suffix", "{tag_suffix}");
		tablists.addDefault("tablists.settings.placeholder-types", Arrays.asList("SERVER", "PLAYER"));
		tablists.addDefault("tablists.settings.custom-suffix.enabled", true);
		tablists.addDefault("tablists.settings.custom-suffix.update-timeout-ms", 5000L);
		tablists.addDefault("tablists.settings.custom-suffix.displayed-value", "{ping}");
		tablists.addDefault("tablists.settings.custom-suffix.render-type", "INTEGER");
		tablists.addDefault("tablists.settings.custom-suffix.placeholder-types", Arrays.asList("PLAYER"));
		
		if (fileMissing) {
			tablists.addDefault("tablists.tl-0.headers.english", "&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&c&lChat&f&lPlugin server\n&eremigio07.me/chatplugin\n&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f");
			tablists.addDefault("tablists.tl-0.headers.italian", "&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f&lServer &c&lChat&f&lPlugin\n&eremigio07.me/chatplugin\n&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f");
			tablists.addDefault("tablists.tl-0.footers.english", "&f\n&7Ping: &f{ping_format} ms\n&7Online: &f{online_total}/{max_players}\n&7Time: &f{date_hour}\n&7RAM: &f{used_memory}/{max_memory} MB\n&7TPS: &f{tps_1_min_format}\n&f");
			tablists.addDefault("tablists.tl-0.footers.italian", "&f\n&7Ping: &f{ping_format} ms\n&7Online: &f{online_total}/{max_players}\n&7Orario: &f{date_hour}\n&7RAM: &f{used_memory}/{max_memory} MB\n&7TPS: &f{tps_1_min_format}\n&f");
			tablists.addDefault("tablists.tl-1.headers.english", "&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&c&lChat&f&lPlugin server\n&eremigio07.me/chatplugin\n&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f");
			tablists.addDefault("tablists.tl-1.headers.italian", "&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f&lServer &c&lChat&f&lPlugin\n&eremigio07.me/chatplugin\n&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f");
			tablists.addDefault("tablists.tl-1.footers.english", "&f\n&7Ping: &f{ping_format} ms\n&7Online: &f{online_total}/{max_players}\n&7Time: &f{date_hour}\n&7RAM: &f{used_memory}/{max_memory} MB\n&7TPS: &f{tps_1_min_format}\n&f");
			tablists.addDefault("tablists.tl-1.footers.italian", "&f\n&7Ping: &f{ping_format} ms\n&7Online: &f{online_total}/{max_players}\n&7Orario: &f{date_hour}\n&7RAM: &f{used_memory}/{max_memory} MB\n&7TPS: &f{tps_1_min_format}\n&f");
			tablists.addDefault("tablists.tl-2.headers.english", "&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&c&lChat&f&lPlugin server\n&e&oremigio07.me/chatplugin\n&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f");
			tablists.addDefault("tablists.tl-2.headers.italian", "&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f&lServer &c&lChat&f&lPlugin\n&e&oremigio07.me/chatplugin\n&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f");
			tablists.addDefault("tablists.tl-2.footers.english", "&f\n&7Ping: &f{ping_format} ms\n&7Online: &f{online_total}/{max_players}\n&7Time: &f{date_hour}\n&7RAM: &f{used_memory}/{max_memory} MB\n&7TPS: &f{tps_1_min_format}\n&f");
			tablists.addDefault("tablists.tl-2.footers.italian", "&f\n&7Ping: &f{ping_format} ms\n&7Online: &f{online_total}/{max_players}\n&7Orario: &f{date_hour}\n&7RAM: &f{used_memory}/{max_memory} MB\n&7TPS: &f{tps_1_min_format}\n&f");
			tablists.addDefault("tablists.tl-3.headers.english", "&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&c&lChat&f&lPlugin server\n&e&oremigio07.me/chatplugin\n&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f");
			tablists.addDefault("tablists.tl-3.headers.italian", "&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f&lServer &c&lChat&f&lPlugin\n&e&oremigio07.me/chatplugin\n&8«&m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &m &8»\n&f");
			tablists.addDefault("tablists.tl-3.footers.english", "&f\n&7Ping: &f{ping_format} ms\n&7Online: &f{online_total}/{max_players}\n&7Time: &f{date_hour}\n&7RAM: &f{used_memory}/{max_memory} MB\n&7TPS: &f{tps_1_min_format}\n&f");
			tablists.addDefault("tablists.tl-3.footers.italian", "&f\n&7Ping: &f{ping_format} ms\n&7Online: &f{online_total}/{max_players}\n&7Orario: &f{date_hour}\n&7RAM: &f{used_memory}/{max_memory} MB\n&7TPS: &f{tps_1_min_format}\n&f");
		} tablists.save();
	}
	
	public void addDefaultScoreboardDefaults(boolean forceAdd) throws IOException {
		Configuration defaultScoreboard = configurations.get(ConfigurationType.DEFAULT_SCOREBOARD);
		boolean fileMissing = !Files.exists(defaultScoreboard.getPath());
		
		if (fileMissing)
			defaultScoreboard.createFile();
		else if (!forceAdd)
			return;
		
		defaultScoreboard.addDefault("settings.enabled", VersionUtils.getVersion().isAtLeast(Environment.isBukkit() ? Version.V1_5 : Version.V1_8));
		defaultScoreboard.addDefault("settings.abbreviate-long-text", true);
		defaultScoreboard.addDefault("settings.numbers.display-mode", VersionUtils.getVersion().isAtLeast(Version.V1_20_3) ? "CUSTOM_TEXT" : "ONLY_ZEROS");
		defaultScoreboard.addDefault("settings.numbers.custom-text.value", "┃");
		defaultScoreboard.addDefault("settings.numbers.custom-text.colors.cycle-timeout", "30s");
		defaultScoreboard.addDefault("settings.numbers.custom-text.colors.interpolations", 15);
		defaultScoreboard.addDefault("settings.numbers.custom-text.colors.gradient", Arrays.asList("#FF0000", "#FF7F00", "#FFFF00", "#00FF00", "#007FFF", "#0000FF", "#7F00FF"));
		defaultScoreboard.addDefault("settings.placeholder-types", Arrays.asList("PLAYER", "SERVER"));
		defaultScoreboard.addDefault("titles.values.english", Arrays.asList("&a&lScoreboard", "&c&lScoreboard"));
		defaultScoreboard.addDefault("titles.values.italian", Arrays.asList("&a&lScoreboard", "&c&lScoreboard"));
		defaultScoreboard.addDefault("titles.random-order", false);
		defaultScoreboard.addDefault("titles.sending-timeout-ms", 500);
		
		//0 -----
		//1 
		//2 name
		//3 rank
		//4 ping
		//5 online
		//6 tps
		//7 
		//8 -----
		
		if (fileMissing) {
			defaultScoreboard.addDefault("lines.0.values.english", Arrays.asList("&8&m                        "));
			defaultScoreboard.addDefault("lines.0.values.italian", Arrays.asList("&8&m                        "));
			defaultScoreboard.addDefault("lines.0.random-order", false);
			defaultScoreboard.addDefault("lines.0.sending-timeout-ms", 1000); //-1 if just 1 value
			defaultScoreboard.addDefault("lines.1.values.english", Arrays.asList(""));
			defaultScoreboard.addDefault("lines.1.values.italian", Arrays.asList(""));
			defaultScoreboard.addDefault("lines.1.sending-timeout-ms", 1000); //-1 if just 1 value
			defaultScoreboard.addDefault("lines.2.values.english", Arrays.asList("&aName: &f{player}", "&eName: &f{player}", "&6Name: &f{player}", "&eName: &f{player}"));
			defaultScoreboard.addDefault("lines.2.values.italian", Arrays.asList("&aNome: &f{player}", "&eNome: &f{player}", "&6Nome: &f{player}", "&eNome: &f{player}"));
			defaultScoreboard.addDefault("lines.2.sending-timeout-ms", 250);
			defaultScoreboard.addDefault("lines.3.values.english", Arrays.asList("&aRank: &f{rank_display_name}", "&eRank: &f{rank_display_name}", "&6Rank: &f{rank_display_name}", "&eRank: &f{rank_display_name}"));
			defaultScoreboard.addDefault("lines.3.values.italian", Arrays.asList("&aRango: &f{rank_display_name}", "&eRango: &f{rank_display_name}", "&6Rango: &f{rank_display_name}", "&eRango: &f{rank_display_name}"));
			defaultScoreboard.addDefault("lines.3.sending-timeout-ms", 250);
			defaultScoreboard.addDefault("lines.4.values.english", Arrays.asList("&aPing: &f{ping_format} ms", "&ePing: &f{ping_format} ms", "&6Ping: &f{ping_format} ms", "&ePing: &f{ping_format} ms"));
			defaultScoreboard.addDefault("lines.4.values.italian", Arrays.asList("&aPing: &f{ping_format} ms", "&ePing: &f{ping_format} ms", "&6Ping: &f{ping_format} ms", "&ePing: &f{ping_format} ms"));
			defaultScoreboard.addDefault("lines.4.sending-timeout-ms", 250);
			defaultScoreboard.addDefault("lines.5.values.english", Arrays.asList("&aOnline: &f{online}/{max_players}", "&eOnline: &f{online}/{max_players}", "&6Online: &f{online}/{max_players}", "&eOnline: &f{online}/{max_players}"));
			defaultScoreboard.addDefault("lines.5.values.italian", Arrays.asList("&aOnline: &f{online}/{max_players}", "&eOnline: &f{online}/{max_players}", "&6Online: &f{online}/{max_players}", "&eOnline: &f{online}/{max_players}"));
			defaultScoreboard.addDefault("lines.5.sending-timeout-ms", 250);
			defaultScoreboard.addDefault("lines.6.values.english", Arrays.asList("&aTPS: &f{tps_1_min_format}", "&eTPS: &f{tps_1_min_format}", "&6TPS: &f{tps_1_min_format}", "&eTPS: &f{tps_1_min_format}"));
			defaultScoreboard.addDefault("lines.6.values.italian", Arrays.asList("&aTPS: &f{tps_1_min_format}", "&eTPS: &f{tps_1_min_format}", "&6TPS: &f{tps_1_min_format}", "&eTPS: &f{tps_1_min_format}"));
			defaultScoreboard.addDefault("lines.6.sending-timeout-ms", 250);
			defaultScoreboard.addDefault("lines.7.values.english", Arrays.asList(""));
			defaultScoreboard.addDefault("lines.7.values.italian", Arrays.asList(""));
			defaultScoreboard.addDefault("lines.7.sending-timeout-ms", 1000); //-1 if just 1 value
			defaultScoreboard.addDefault("lines.8.values.english", Arrays.asList("&8&m                        "));
			defaultScoreboard.addDefault("lines.8.values.italian", Arrays.asList("&8&m                        "));
			defaultScoreboard.addDefault("lines.8.sending-timeout-ms", 1000); //-1 if just 1 value
		} defaultScoreboard.save();
	}
	
	public void addBossbarsDefaults(boolean forceAdd) throws IOException {
		Configuration bossbars = configurations.get(ConfigurationType.BOSSBARS);
		boolean fileMissing = !Files.exists(bossbars.getPath());
		
		if (fileMissing)
			bossbars.createFile();
		else if (!forceAdd)
			return;
		
		bossbars.addDefault("bossbars.settings.enabled", VersionUtils.getVersion().isAtLeast(Version.V1_9));
		bossbars.addDefault("bossbars.settings.random-order", false);
		bossbars.addDefault("bossbars.settings.abbreviate-long-titles", true);
		bossbars.addDefault("bossbars.settings.send-full-to-legacy-players", true);
		bossbars.addDefault("bossbars.settings.reflection-wither-teleportation.distance", 42.0);
		bossbars.addDefault("bossbars.settings.reflection-wither-teleportation.timeout-ms", 250);
		bossbars.addDefault("bossbars.settings.sending-timeout-ms", 10000);
		bossbars.addDefault("bossbars.settings.placeholder-types", Arrays.asList("JUST_NAME"));
		bossbars.addDefault("bossbars.settings.enabled-worlds", Arrays.asList("*"));
		
		bossbars.addDefault("bossbars.settings.loading-bossbar.enabled", true);
		bossbars.addDefault("bossbars.settings.loading-bossbar.sending-timeout-ms", 200);
		bossbars.addDefault("bossbars.settings.loading-bossbar.titles.english", "&aLoading next bossbar... &f{loading_bossbar_remaining_seconds} &aremaining.");
		bossbars.addDefault("bossbars.settings.loading-bossbar.titles.italian", "&aCaricamento prossima bossbar... &f{loading_bossbar_remaining_seconds} &arimanenti.");
		bossbars.addDefault("bossbars.settings.loading-bossbar.color", "RANDOM");
		bossbars.addDefault("bossbars.settings.loading-bossbar.style", "SOLID");
		
		if (fileMissing) {
			bossbars.addDefault("bossbars.discord.titles.english", "&9Join the Discord server to chat with your friends!");
			bossbars.addDefault("bossbars.discord.titles.italian", "&9Entra nel server Discord per chattare con i tuoi amici!");
			bossbars.addDefault("bossbars.discord.color", "BLUE");
			bossbars.addDefault("bossbars.ping-command.titles.english", "&aYou can check your ping with &f/ping&a.");
			bossbars.addDefault("bossbars.ping-command.titles.italian", "&aPuoi controllare il tuo ping con &f/ping&a.");
			bossbars.addDefault("bossbars.ping-command.value", 75);
			bossbars.addDefault("bossbars.ping-command.color", "GREEN");
			bossbars.addDefault("bossbars.placeholders.titles.english", "&cPlaceholders are supported. Hi, &f{player}&c!");
			bossbars.addDefault("bossbars.placeholders.titles.italian", "&cI placeholders sono supportati. Ciao, &f{player}&c!");
			bossbars.addDefault("bossbars.placeholders.value", 50);
			bossbars.addDefault("bossbars.placeholders.color", "RED");
			bossbars.addDefault("bossbars.bossbar-command.titles.english", "&5Bossbar can be toggled off with &f/bossbar&5.");
			bossbars.addDefault("bossbars.bossbar-command.titles.italian", "&5La bossbar può essere disattivata con &f/bossbar&5.");
			bossbars.addDefault("bossbars.bossbar-command.value", 25);
			bossbars.addDefault("bossbars.bossbar-command.color", "PURPLE");
			bossbars.addDefault("bossbars.5-star-review.titles.english", "&eLeave a &f5 &e⭐ review on the plugin's page!");
			bossbars.addDefault("bossbars.5-star-review.titles.italian", "&eLascia una recensione &f5 &e⭐ sulla pagina del plugin!");
			bossbars.addDefault("bossbars.5-star-review.value", 0);
			bossbars.addDefault("bossbars.5-star-review.color", "YELLOW");
			bossbars.addDefault("bossbars.language-command.titles.english", "&aChange the plugin's language with &f/language&a!");
			bossbars.addDefault("bossbars.language-command.titles.italian", "&aCambia la lingua del plugin con &f/language&a!");
			bossbars.addDefault("bossbars.language-command.value", 25);
			bossbars.addDefault("bossbars.language-command.color", "GREEN");
			bossbars.addDefault("bossbars.instagram.titles.english", "&dVisit our Instagram page for memes about the Staff members!");
			bossbars.addDefault("bossbars.instagram.titles.italian", "&dVisita la pagina Instagram per memes sui membri dello Staff!");
			bossbars.addDefault("bossbars.instagram.value", 50);
			bossbars.addDefault("bossbars.instagram.color", "PINK");
			bossbars.addDefault("bossbars.random.titles.english", "&cThis bar will have a random color and style.");
			bossbars.addDefault("bossbars.random.titles.italian", "&cQuesta barra avrà colore e stile casuali.");
			bossbars.addDefault("bossbars.random.value", 75);
			bossbars.addDefault("bossbars.random.color", "RANDOM");
			bossbars.addDefault("bossbars.random.style", "RANDOM");
			bossbars.addDefault("bossbars.last.titles.english", "&fLast bossbar. The next one will not be shown.");
			bossbars.addDefault("bossbars.last.titles.italian", "&fUltima bossbar. La prossima non verrà mostrata.");
			bossbars.addDefault("bossbars.last.color", "WHITE");
			bossbars.addDefault("bossbars.hidden.titles.english", "&7This bossbar will not be shown.");
			bossbars.addDefault("bossbars.hidden.titles.italian", "&7Questa bossbar non verrà mostrata.");
			bossbars.addDefault("bossbars.hidden.hidden", true);
		} bossbars.save();
	}
	
	public void addActionbarsDefaults(boolean forceAdd) throws IOException {
		Configuration actionbars = configurations.get(ConfigurationType.ACTIONBARS);
		boolean fileMissing = !Files.exists(actionbars.getPath());
		
		if (fileMissing)
			actionbars.createFile();
		else if (!forceAdd)
			return;
		
		actionbars.addDefault("actionbars.settings.enabled", VersionUtils.getVersion().isAtLeast(Environment.isBukkit() ? Version.V1_8 : Version.V1_11));
		actionbars.addDefault("actionbars.settings.random-order", false);
		actionbars.addDefault("actionbars.settings.prefix.enabled", false);
		actionbars.addDefault("actionbars.settings.prefix.format", "&8[&5&lAds&8] &f");
		actionbars.addDefault("actionbars.settings.sending-timeout-ms", 10000);
		actionbars.addDefault("actionbars.settings.placeholder-types", Arrays.asList("JUST_NAME", "SERVER"));
		
		if (fileMissing) {
			actionbars.addDefault("actionbars.discord.texts.english", "&9Join Discord to chat with friends!");
			actionbars.addDefault("actionbars.discord.texts.italian", "&9Entra su Discord per chattare con amici!");
			actionbars.addDefault("actionbars.store.texts.english", "&6Visit the Store if you want to donate!");
			actionbars.addDefault("actionbars.store.texts.italian", "&6Visita lo Store se vuoi fare una donazione!");
			actionbars.addDefault("actionbars.vote.texts.english", "&aRemember to vote us everyday!");
			actionbars.addDefault("actionbars.vote.texts.italian", "&aRicorda di votarci ogni giorno!");
			actionbars.addDefault("actionbars.welcome.texts.english", "&bWelcome to the server, &f{player}&b!");
			actionbars.addDefault("actionbars.welcome.texts.italian", "&bBenvenuti nel server, &f{player}&b!");
			actionbars.addDefault("actionbars.date.texts.english", "&dDate: &f{date_full}");
			actionbars.addDefault("actionbars.date.texts.italian", "&dData: &f{date_full}");
			actionbars.addDefault("actionbars.last.texts.english", "&cNext one will not be shown.");
			actionbars.addDefault("actionbars.last.texts.italian", "&cLa prossima non verrà mostrata.");
			actionbars.addDefault("actionbars.hidden.texts.english", "&fThis text will not be shown.");
			actionbars.addDefault("actionbars.hidden.texts.italian", "&fQuesto testo non verrà mostrato.");
			actionbars.addDefault("actionbars.hidden.hidden", true);
		} actionbars.save();
	}
	
	public void addAdsDefaults(boolean forceAdd) throws IOException {
		Configuration ads = configurations.get(ConfigurationType.ADS);
		boolean fileMissing = !Files.exists(ads.getPath());
		
		if (fileMissing)
			ads.createFile();
		else if (!forceAdd)
			return;
		
		ads.addDefault("ads.settings.enabled", true);
		ads.addDefault("ads.settings.random-order", true);
		ads.addDefault("ads.settings.prefix.enabled", false);
		ads.addDefault("ads.settings.prefix.format", "&8[&5&lAds&8] &f");
		ads.addDefault("ads.settings.sound.enabled", true);
		ads.addDefault("ads.settings.sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		ads.addDefault("ads.settings.sound.volume", 1F);
		ads.addDefault("ads.settings.sound.pitch", 1F);
		ads.addDefault("ads.settings.sending-timeout", "5m");
		ads.addDefault("ads.settings.placeholder-types", Arrays.asList());
		
		if (fileMissing) {
			ads.addDefault("ads.discord.texts.english", "&e&lJoin our Discord server:\n&b&nstay updated about the new features!\n\n&7&oClick this message to receive the invitation.");
			ads.addDefault("ads.discord.texts.italian", "&e&lEntra nel nostro Discord:\n&b&nrimanete aggiornati sulle novità!\n\n&7&oClicca questo messaggio per ricevere l'invito.");
			ads.addDefault("ads.discord.hovers.english", "&9[Click here to open the link]");
			ads.addDefault("ads.discord.hovers.italian", "&9[Clicca qui per aprire il link]");
			ads.addDefault("ads.discord.click.action", "OPEN_URL");
			ads.addDefault("ads.discord.click.values.english", "https://discord.gg/eSnAPhvMTG");
			ads.addDefault("ads.discord.click.values.italian", "https://discord.gg/eSnAPhvMTG");
			
			ads.addDefault("ads.store.texts.english", "&e&lVisit our Store:\n&b&nbuy perks and cosmetics and support the server!\n\n&7&oClick this message to get the link.");
			ads.addDefault("ads.store.texts.italian", "&e&lVisita il nostro Store:\n&b&ncompra benefici e cosmetici e supporta il server!\n\n&7&oClicca questo messaggio per ricevere il link.");
			ads.addDefault("ads.store.hovers.english", "&9[Click here to open the link]");
			ads.addDefault("ads.store.hovers.italian", "&9[Clicca qui per aprire il link]");
			ads.addDefault("ads.store.click.action", "OPEN_URL");
			ads.addDefault("ads.store.click.values.english", "https://megaproserver.com/store");
			ads.addDefault("ads.store.click.values.italian", "https://megaproserver.com/store");
			ads.addDefault("ads.store.disabled-ranks", Arrays.asList("staff", "vip"));
		} ads.save();
	}
	
	public void addF3ServerNamesDefaults(boolean forceAdd) throws IOException {
		Configuration f3ServerNames = configurations.get(ConfigurationType.F3_SERVER_NAMES);
		boolean fileMissing = !Files.exists(f3ServerNames.getPath());
		
		if (fileMissing)
			f3ServerNames.createFile();
		else if (!forceAdd)
			return;
		
		f3ServerNames.addDefault("f3-server-names.settings.enabled", Environment.isBukkit() && VersionUtils.getVersion().isAtLeast(Version.V1_7_2));
		f3ServerNames.addDefault("f3-server-names.settings.random-order", true);
		f3ServerNames.addDefault("f3-server-names.settings.sending-timeout-ms", 5000);
		f3ServerNames.addDefault("f3-server-names.settings.placeholder-types", Arrays.asList("SERVER"));
		
		if (fileMissing) {
			f3ServerNames.addDefault("f3-server-names.f3sn-0.texts.english", "&e&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-0.texts.italian", "&e&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-1.texts.english", "&a&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-1.texts.italian", "&a&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-2.texts.english", "&b&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-2.texts.italian", "&b&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-3.texts.english", "&9&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-3.texts.italian", "&9&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-4.texts.english", "&d&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-4.texts.italian", "&d&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-5.texts.english", "&5&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-5.texts.italian", "&5&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-6.texts.english", "&4&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-6.texts.italian", "&4&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-7.texts.english", "&c&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-7.texts.italian", "&c&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-8.texts.english", "&6&l{server_version} &7server");
			f3ServerNames.addDefault("f3-server-names.f3sn-8.texts.italian", "&6&l{server_version} &7server");
		} f3ServerNames.save();
	}
	
	public void addJoinQuitModulesDefaults(boolean forceAdd) throws IOException {
		Configuration joinQuitModules = configurations.get(ConfigurationType.JOIN_QUIT_MODULES);
		boolean fileMissing = !Files.exists(joinQuitModules.getPath());
		
		if (fileMissing)
			joinQuitModules.createFile();
		else if (!forceAdd)
			return;
		
		joinQuitModules.addDefault("join-quit-modules.join-messages.settings.enabled", true);
		joinQuitModules.addDefault("join-quit-modules.join-messages.settings.placeholder-types", Arrays.asList("PLAYER"));
		joinQuitModules.addDefault("join-quit-modules.quit-messages.settings.enabled", true);
		joinQuitModules.addDefault("join-quit-modules.switch-messages.settings.enabled", false);
		
		if (fileMissing) {
			joinQuitModules.addDefault("join-quit-modules.join-messages.default.english", Arrays.asList("&8[&a&l+&8] &f{prefix}{tag_name_color}{player}{suffix}"));
			joinQuitModules.addDefault("join-quit-modules.join-messages.default.italian", Arrays.asList("&8[&a&l+&8] &f{prefix}{tag_name_color}{player}{suffix}"));
			joinQuitModules.addDefault("join-quit-modules.quit-messages.default.english", Arrays.asList("&8[&c&l-&8] &f{prefix}{tag_name_color}{player}{suffix}"));
			joinQuitModules.addDefault("join-quit-modules.quit-messages.default.italian", Arrays.asList("&8[&c&l-&8] &f{prefix}{tag_name_color}{player}{suffix}"));
			joinQuitModules.addDefault("join-quit-modules.switch-messages.default.english", Arrays.asList("&8[&e&l»&8] &f{prefix}{tag_name_color}{player}{suffix} &e➡ &f{server}"));
			joinQuitModules.addDefault("join-quit-modules.switch-messages.default.italian", Arrays.asList("&8[&e&l»&8] &f{prefix}{tag_name_color}{player}{suffix} &e➡ &f{server}"));
		}
		
		joinQuitModules.addDefault("join-quit-modules.join-titles.settings.enabled", true);
		joinQuitModules.addDefault("join-quit-modules.join-titles.settings.fade-in-ms", 500L);
		joinQuitModules.addDefault("join-quit-modules.join-titles.settings.stay-ms", 3500L);
		joinQuitModules.addDefault("join-quit-modules.join-titles.settings.fade-out-ms", 1000L);
		joinQuitModules.addDefault("join-quit-modules.join-titles.settings.delay-ms", 1000L);
		joinQuitModules.addDefault("join-quit-modules.join-titles.settings.placeholder-types", Arrays.asList("JUST_NAME"));
		joinQuitModules.addDefault("join-quit-modules.join-titles.titles.english", "&d&lWelcome, &f{player}&d&l!");
		joinQuitModules.addDefault("join-quit-modules.join-titles.titles.italian", "&d&lBenvenuti, &f{player}&d&l!");
		joinQuitModules.addDefault("join-quit-modules.join-titles.subtitles.english", "&eBe respectful and have fun!");
		joinQuitModules.addDefault("join-quit-modules.join-titles.subtitles.italian", "&eSiate rispettosi e divertitevi!");
		
		joinQuitModules.addDefault("join-quit-modules.welcome-messages.settings.enabled", true);
		joinQuitModules.addDefault("join-quit-modules.welcome-messages.settings.delay-ms", 1000L);
		joinQuitModules.addDefault("join-quit-modules.welcome-messages.settings.placeholder-types", Arrays.asList("PLAYER"));
		joinQuitModules.addDefault("join-quit-modules.welcome-messages.values.english", "{pfx} &eHey, &f{tag_prefix}{tag_name_color}{player}{tag_suffix}&e! This is the default welcome message.");
		joinQuitModules.addDefault("join-quit-modules.welcome-messages.values.italian", "{pfx} &eHey, &f{tag_prefix}{tag_name_color}{player}{tag_suffix}&e! Questo è il messaggio di benvenuto predefinito.");
		
		joinQuitModules.addDefault("join-quit-modules.suggested-version.enabled", false);
		joinQuitModules.addDefault("join-quit-modules.suggested-version.version", "{server_version}");
		joinQuitModules.addDefault("join-quit-modules.suggested-version.delay-ms", 10000L);
		
		joinQuitModules.addDefault("join-quit-modules.account-check.enabled", true);
		joinQuitModules.addDefault("join-quit-modules.account-check.perform-on-first-join", false);
		joinQuitModules.addDefault("join-quit-modules.account-check.timeout-between-checks-ms", 10);
		joinQuitModules.addDefault("join-quit-modules.account-check.max-time-played", "12h");
		
		if (fileMissing) {
			joinQuitModules.addDefault("join-quit-modules.account-check.punish-commands.2.english", Arrays.asList("staffchat &f{player} &cowns multiple (&f{amount}&c) accounts: &f{accounts}&c."));
			joinQuitModules.addDefault("join-quit-modules.account-check.punish-commands.2.italian", Arrays.asList("staffchat &f{player} &cpossiede accounts multipli (&f{amount}&c): &f{accounts}&c."));
			joinQuitModules.addDefault("join-quit-modules.account-check.punish-commands.3.english", Arrays.asList("tempban {player} 30d Multiple ({amount}) accounts detected: {accounts}. This is an automatic ban. -s"));
			joinQuitModules.addDefault("join-quit-modules.account-check.punish-commands.3.italian", Arrays.asList("tempban {player} 30d Accounts multipli ({amount}) rilevati: {accounts}. Questo è un ban automatico. -s"));
		} joinQuitModules.addDefault("join-quit-modules.account-check.ip-lookup.enabled", false);
		joinQuitModules.addDefault("join-quit-modules.account-check.ip-lookup.max-accuracy-radius-km", 10);
		joinQuitModules.addDefault("join-quit-modules.account-check.anti-ban-evading-system.enabled", false);
		joinQuitModules.addDefault("join-quit-modules.account-check.anti-ban-evading-system.commands.english", Arrays.asList("tempban {player} 30d Ban evading of account {account}. This is an automatic ban. -s"));
		joinQuitModules.addDefault("join-quit-modules.account-check.anti-ban-evading-system.commands.italian", Arrays.asList("tempban {player} 30d Elusione del ban dell'account {account}. Questo è un ban automatico. -s"));
		
		joinQuitModules.save();
	}
	
	public void addMainGUIDefaults(boolean forceAdd) throws IOException {
		Configuration mainGUI = configurations.get(ConfigurationType.MAIN_GUI);
		
		if (!Files.exists(mainGUI.getPath()))
			mainGUI.createFile();
		else if (!forceAdd)
			return;
		
		mainGUI.addDefault("settings.rows", 5);
		mainGUI.addDefault("settings.titles.english", "&c&lChat&f&lPlugin");
		mainGUI.addDefault("settings.titles.italian", "&c&lChat&f&lPlugin");
		mainGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &f{0} &aGUI.");
		mainGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &f{0} &ain corso.");
		mainGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		mainGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		mainGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		mainGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		mainGUI.addDefault("settings.click-sound.volume", 1F);
		mainGUI.addDefault("settings.click-sound.pitch", 1F);
		path = "icons.info.";
		mainGUI.addDefault(path + "display-names.english", "&c&lChat&f&lPlugin GUI");
		mainGUI.addDefault(path + "display-names.italian", "&c&lChat&f&lPlugin GUI");
		mainGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to view information", "&7about the server status and gives you", "&7the access to some useful Staff GUIs."));
		mainGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di visualizzare", "&7informazioni sullo stato del server e ti", "&7dà accesso ad alcune Staff GUIs utili."));
		mainGUI.addDefault(path + "material", "paper");
		mainGUI.addDefault(path + "keep-open", true);
		mainGUI.addDefault(path + "x", 5);
		mainGUI.addDefault(path + "y", 1);
		path = "icons.banlist.";
		mainGUI.addDefault(path + "display-names.english", "&4&lBanlist");
		mainGUI.addDefault(path + "display-names.italian", "&4&lBanlist");
		mainGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to open", "&7the active bans' list."));
		mainGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per", "&7aprire la lista dei bans attivi."));
		mainGUI.addDefault(path + "material", "barrier");
		mainGUI.addDefault(path + "glowing", true);
		mainGUI.addDefault(path + "commands", Arrays.asList("p: banlist"));
		mainGUI.addDefault(path + "x", 1);
		mainGUI.addDefault(path + "y", 3);
		path = "icons.warnlist.";
		mainGUI.addDefault(path + "display-names.english", "&c&lWarnlist");
		mainGUI.addDefault(path + "display-names.italian", "&c&lWarnlist");
		mainGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to open", "&7the active warnings' list."));
		mainGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per aprire", "&7la lista degli avvisi attivi."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			mainGUI.addDefault(path + "material", "banner");
			mainGUI.addDefault(path + "damage", 14);
		} else mainGUI.addDefault(path + "material", "orange_banner");
		mainGUI.addDefault(path + "glowing", true);
		mainGUI.addDefault(path + "commands", Arrays.asList("p: warnlist"));
		mainGUI.addDefault(path + "x", 3);
		mainGUI.addDefault(path + "y", 3);
		path = "icons.status.";
		mainGUI.addDefault(path + "display-names.english", "&d&lServer status");
		mainGUI.addDefault(path + "display-names.italian", "&d&lStato del server");
		mainGUI.addDefault(path + "lores.english", Arrays.asList("&7OS: &f{server_os_name} {server_os_version}&7, Java: &f{server_java_version}", "&7Server: &f{server_version} (protocol: &f{server_version_protocol})", "&7ChatPlugin version: &f{plugin_version}", "&7Uptime: &f{uptime}", "&7TPS (1m, 5m, 15m): &f{tps_1_min_format}&f, {tps_5_min_format}&f, {tps_15_min_format}", "&7Used memory: &f{used_memory}/{max_memory} MB", "&7Allocated memory: &f{total_memory} MB&7, free: &f{free_memory} MB", "&7Current threads count: &f{active_threads}x", "&7Used storage: &f{used_storage}/{total_storage} GB", "&7Free storage: &f{free_storage} GB", "&7Enabled worlds: &f{enabled_worlds}x&7, &f{enabled_players} &7players", "&7Enabled managers: &f{enabled_managers}x", "&7Startup: &f{startup_time} ms&7, last reload: &f{last_reload_time} ms"));
		mainGUI.addDefault(path + "lores.italian", Arrays.asList("&7OS: &f{server_os_name} {server_os_version}&7, Java: &f{server_java_version}", "&7Server: &f{server_version} (protocollo: &f{server_version_protocol})", "&7Versione ChatPlugin: &f{plugin_version}", "&7Uptime: &f{uptime}", "&7TPS (1m, 5m, 15m): &f{tps_1_min_format}&f, {tps_5_min_format}&f, {tps_15_min_format}", "&7Utilizzo memoria: &f{used_memory}/{max_memory} MB", "&7Memoria allocata: &f{total_memory} MB&7, libera: &f{free_memory} MB", "&7Conteggio threads attivi: &f{active_threads}x", "&7Storage utilizzato: &f{used_storage}/{total_storage} GB", "&7Storage libero: &f{free_storage} GB", "&7Mondi abilitati: &f{enabled_worlds}x&7, &f{enabled_players} &7giocatori", "&7Managers abilitati: &f{enabled_managers}x", "&7Startup: &f{startup_time} ms&7, ultimo reload: &f{last_reload_time} ms"));
		mainGUI.addDefault(path + "material", "ender_eye");
		mainGUI.addDefault(path + "keep-open", true);
		mainGUI.addDefault(path + "glowing", true);
		mainGUI.addDefault(path + "x", 5);
		mainGUI.addDefault(path + "y", 3);
		path = "icons.mutelist.";
		mainGUI.addDefault(path + "display-names.english", "&e&lMutelist");
		mainGUI.addDefault(path + "display-names.italian", "&e&lMutelist");
		mainGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to open", "&7the active mutes' list."));
		mainGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per", "&7aprire la lista dei mutes attivi."));
		mainGUI.addDefault(path + "material", "book");
		mainGUI.addDefault(path + "glowing", true);
		mainGUI.addDefault(path + "commands", Arrays.asList("p: mutelist"));
		mainGUI.addDefault(path + "x", 7);
		mainGUI.addDefault(path + "y", 3);
		path = "icons.violations.";
		mainGUI.addDefault(path + "display-names.english", "&3&lViolations");
		mainGUI.addDefault(path + "display-names.italian", "&3&lViolazioni");
		mainGUI.addDefault(path + "lores.english", Arrays.asList("&7Click to open the recent", "&7anticheat's violations GUI."));
		mainGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca per aprire la GUI", "&7violazioni recenti dell'anticheat."));
		mainGUI.addDefault(path + "material", "iron_sword");
		mainGUI.addDefault(path + "glowing", true);
		mainGUI.addDefault(path + "item-flags", Arrays.asList("HIDE_ATTRIBUTES"));
		mainGUI.addDefault(path + "commands", Arrays.asList("p: violations"));
		mainGUI.addDefault(path + "x", 9);
		mainGUI.addDefault(path + "y", 3);
		path = "icons.reload.";
		mainGUI.addDefault(path + "display-names.english", "&b&lReload plugin");
		mainGUI.addDefault(path + "display-names.italian", "&b&lRicarica il plugin");
		mainGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to", "&7reload ChatPlugin."));
		mainGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per", "&7ricaricare ChatPlugin."));
		mainGUI.addDefault(path + "material", "redstone");
		mainGUI.addDefault(path + "glowing", true);
		mainGUI.addDefault(path + "commands", Arrays.asList("p: chatplugin reload"));
		mainGUI.addDefault(path + "x", 5);
		mainGUI.addDefault(path + "y", 5);
		
		mainGUI.save();
	}
	
	public void addLanguagesGUIDefaults(boolean forceAdd) throws IOException {
		Configuration languagesGUI = configurations.get(ConfigurationType.LANGUAGES_GUI);
		
		if (!Files.exists(languagesGUI.getPath()))
			languagesGUI.createFile();
		else if (!forceAdd)
			return;
		
		languagesGUI.addDefault("settings.rows", 3);
		languagesGUI.addDefault("settings.titles.english", "&b&lLanguages");
		languagesGUI.addDefault("settings.titles.italian", "&b&lLingue");
		languagesGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &f{0} &aGUI.");
		languagesGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &f{0} &ain corso.");
		languagesGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		languagesGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		languagesGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		languagesGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		languagesGUI.addDefault("settings.click-sound.volume", 1F);
		languagesGUI.addDefault("settings.click-sound.pitch", 1F);
		path = "icons.info.";
		languagesGUI.addDefault(path + "display-names.english", "&b&lLanguages");
		languagesGUI.addDefault(path + "display-names.italian", "&b&lLingue");
		languagesGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to change", "&7your language on the server."));
		languagesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di cambiare", "&7la tua lingua sul server."));
		languagesGUI.addDefault(path + "material", "paper");
		languagesGUI.addDefault(path + "keep-open", true);
		languagesGUI.addDefault(path + "x", 5);
		languagesGUI.addDefault(path + "y", 1);
		path = "icons.english.";
		languagesGUI.addDefault(path + "display-names.english", "&9&lEnglish");
		languagesGUI.addDefault(path + "display-names.italian", "&9&lEnglish");
		languagesGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set", "&7your language to English."));
		languagesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Click this icon to set", "&7your language to English."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			languagesGUI.addDefault(path + "material", "skull");
			languagesGUI.addDefault(path + "damage", 3);
		} else languagesGUI.addDefault(path + "material", "player_head");
		languagesGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/7d15d566202ac0e76cd897759df5d01c11f991bd46c5c9a04357ea89ee75");
		languagesGUI.addDefault(path + "commands", Arrays.asList("p: chatplugin language english"));
		languagesGUI.addDefault(path + "x", 1);
		languagesGUI.addDefault(path + "y", 2);
		path = "icons.italian.";
		languagesGUI.addDefault(path + "display-names.english", "&9&lItaliano");
		languagesGUI.addDefault(path + "display-names.italian", "&9&lItaliano");
		languagesGUI.addDefault(path + "lores.english", Arrays.asList("&7Clicca questa icona per impostare", "&7la tua lingua sull'italiano."));
		languagesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare", "&7la tua lingua sull'italiano."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			languagesGUI.addDefault(path + "material", "skull");
			languagesGUI.addDefault(path + "damage", 3);
		} else languagesGUI.addDefault(path + "material", "player_head");
		languagesGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/a56c5cc17319a6c9ec847252e4d274552d97da95e1085072dba49d117cf3");
		languagesGUI.addDefault(path + "commands", Arrays.asList("p: chatplugin language italian"));
		languagesGUI.addDefault(path + "x", 2);
		languagesGUI.addDefault(path + "y", 2);
		
		languagesGUI.save();
	}
	
	public void addChatColorGUIDefaults(boolean forceAdd) throws IOException {
		Configuration chatColorGUI = configurations.get(ConfigurationType.CHAT_COLOR_GUI);
		
		if (!Files.exists(chatColorGUI.getPath()))
			chatColorGUI.createFile();
		else if (!forceAdd)
			return;
		
		chatColorGUI.addDefault("settings.rows", 4);
		chatColorGUI.addDefault("settings.titles.english", "&f&lChat color");
		chatColorGUI.addDefault("settings.titles.italian", "&f&lColore chat");
		chatColorGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &f{0} &aGUI.");
		chatColorGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &f{0} &ain corso.");
		chatColorGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		chatColorGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		chatColorGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		chatColorGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		chatColorGUI.addDefault("settings.click-sound.volume", 1F);
		chatColorGUI.addDefault("settings.click-sound.pitch", 1F);
		path = "icons.info.";
		chatColorGUI.addDefault(path + "display-names.english", "&f&lChat color");
		chatColorGUI.addDefault(path + "display-names.italian", "&f&lColore chat");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to change", "&7your chat's default color."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di cambiare", "&7il colore predefinito della tua chat."));
		chatColorGUI.addDefault(path + "material", "paper");
		chatColorGUI.addDefault(path + "keep-open", true);
		chatColorGUI.addDefault(path + "x", 5);
		chatColorGUI.addDefault(path + "y", 1);
		path = "icons.dark-red.";
		chatColorGUI.addDefault(path + "display-names.english", "&4&lDark red &f(&&f4)");
		chatColorGUI.addDefault(path + "display-names.italian", "&4&lRosso scuro &f(&&f4)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to dark red."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul rosso scuro."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/c65f3bae0d203ba16fe1dc3d1307a86a638be924471f23e82abd9d78f8a3fca");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &4"));
		chatColorGUI.addDefault(path + "x", 1);
		chatColorGUI.addDefault(path + "y", 2);
		path = "icons.red.";
		chatColorGUI.addDefault(path + "display-names.english", "&c&lRed &f(&&fc)");
		chatColorGUI.addDefault(path + "display-names.italian", "&c&lRosso &f(&&fc)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to red."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul rosso."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/2062d8d72f5891c71fab30d52e0481795b3d2d3d2ed2f8b9b517d7d2821e35d6");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &c"));
		chatColorGUI.addDefault(path + "x", 2);
		chatColorGUI.addDefault(path + "y", 2);
		path = "icons.gold.";
		chatColorGUI.addDefault(path + "display-names.english", "&6&lGold &f(&&f6)");
		chatColorGUI.addDefault(path + "display-names.italian", "&6&lOro &f(&&f6)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to gold."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sull'oro."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/5189f347f42450cd2a2e9b8a5398807d28c7f4254bd99a8a499ce5435320955");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &6"));
		chatColorGUI.addDefault(path + "x", 3);
		chatColorGUI.addDefault(path + "y", 2);
		path = "icons.yellow.";
		chatColorGUI.addDefault(path + "display-names.english", "&e&lYellow &f(&&fe)");
		chatColorGUI.addDefault(path + "display-names.italian", "&e&lGiallo &f(&&fe)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to yellow."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul giallo."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/200bf4bf14c8699c0f9209ca79fe18253e901e9ec3876a2ba095da052f69eba7");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &e"));
		chatColorGUI.addDefault(path + "x", 4);
		chatColorGUI.addDefault(path + "y", 2);
		path = "icons.dark-green.";
		chatColorGUI.addDefault(path + "display-names.english", "&2&lDark green &f(&&f2)");
		chatColorGUI.addDefault(path + "display-names.italian", "&2&lVerde scuro &f(&&f2)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to dark green."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul verde scuro."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/a3e9f4dbadde0f727c5803d75d8bb378fb9fcb4b60d33bec19092a3a2e7b07a9");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &2"));
		chatColorGUI.addDefault(path + "x", 5);
		chatColorGUI.addDefault(path + "y", 2);
		path = "icons.green.";
		chatColorGUI.addDefault(path + "display-names.english", "&a&lGreen &f(&&fa)");
		chatColorGUI.addDefault(path + "display-names.italian", "&a&lVerde &f(&&fa)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to green."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul verde."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/b985a29957d40fa564d5e31cbd905e3694a616393ce13710bfc31b1b8b0a522d");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &a"));
		chatColorGUI.addDefault(path + "x", 6);
		chatColorGUI.addDefault(path + "y", 2);
		path = "icons.aqua.";
		chatColorGUI.addDefault(path + "display-names.english", "&b&lAqua &f(&&fb)");
		chatColorGUI.addDefault(path + "display-names.italian", "&b&lAcqua &f(&&fb)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to aqua."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sull'acqua."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/f9e16979309b5a9b673d60d1390bbab0d0385eac7254d828ada2a36a46f73a59");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &b"));
		chatColorGUI.addDefault(path + "x", 7);
		chatColorGUI.addDefault(path + "y", 2);
		path = "icons.dark-aqua.";
		chatColorGUI.addDefault(path + "display-names.english", "&3&lDark aqua &f(&&f3)");
		chatColorGUI.addDefault(path + "display-names.italian", "&3&lAcqua scuro &f(&&f3)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to dark aqua."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sull'acqua scuro."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/975b7ac9f0c712303cd3b654e646ce1c4bf243ab348a6a25370f2603e79a62a0");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &3"));
		chatColorGUI.addDefault(path + "x", 8);
		chatColorGUI.addDefault(path + "y", 2);
		path = "icons.dark-blue.";
		chatColorGUI.addDefault(path + "display-names.english", "&1&lDark blue &f(&&f1)");
		chatColorGUI.addDefault(path + "display-names.italian", "&1&lBlu scuro &f(&&f1)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to dark blue."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul blu scuro."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/7e7ab712c87f67d48b98f70634d1dcfcd5980c3d6f0d622cdc3230912361b54e");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &1"));
		chatColorGUI.addDefault(path + "x", 9);
		chatColorGUI.addDefault(path + "y", 2);
		path = "icons.blue.";
		chatColorGUI.addDefault(path + "display-names.english", "&9&lBlue &f(&&f9)");
		chatColorGUI.addDefault(path + "display-names.italian", "&9&lBlu &f(&&f9)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to blue."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul blu."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/3b5106b060eaf398217349f3cfb4f2c7c4fd9a0b0307a17eba6af7889be0fbe6");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &9"));
		chatColorGUI.addDefault(path + "x", 1);
		chatColorGUI.addDefault(path + "y", 3);
		path = "icons.light-purple.";
		chatColorGUI.addDefault(path + "display-names.english", "&d&lLight purple &f(&&fd)");
		chatColorGUI.addDefault(path + "display-names.italian", "&d&lViola chiaro &f(&&fd)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to light purple."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul viola chiaro."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/7a9ea6e36f9e579f586adb1937bb14377b0d74034ffcb2556a2acb435671448f");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &d"));
		chatColorGUI.addDefault(path + "x", 2);
		chatColorGUI.addDefault(path + "y", 3);
		path = "icons.dark-purple.";
		chatColorGUI.addDefault(path + "display-names.english", "&5&lDark purple &f(&&f5)");
		chatColorGUI.addDefault(path + "display-names.italian", "&5&lViola scuro &f(&&f5)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to dark purple."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul viola scuro."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/467f2b506370c1e84f90fbf29c80e0cb7e2ac93230301b5d8e42c68fdde89fe0");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &5"));
		chatColorGUI.addDefault(path + "x", 3);
		chatColorGUI.addDefault(path + "y", 3);
		path = "icons.white.";
		chatColorGUI.addDefault(path + "display-names.english", "&f&lWhite &f(&&ff)");
		chatColorGUI.addDefault(path + "display-names.italian", "&f&lBianco &f(&&ff)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to white."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul bianco."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/8e0e8acabad27d4616fae9e472c0de60853d203c1c6f31367c939b619f3e3831");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &f"));
		chatColorGUI.addDefault(path + "x", 4);
		chatColorGUI.addDefault(path + "y", 3);
		path = "icons.gray.";
		chatColorGUI.addDefault(path + "display-names.english", "&7&lGray &f(&&f7)");
		chatColorGUI.addDefault(path + "display-names.italian", "&7&lGrigio &f(&&f7)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to gray."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul grigio."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/c328dcde173beff9f3f41b923213fc1bb7678967ccb2ede7a7cf40b1836b1a73");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &7"));
		chatColorGUI.addDefault(path + "x", 5);
		chatColorGUI.addDefault(path + "y", 3);
		path = "icons.dark-gray.";
		chatColorGUI.addDefault(path + "display-names.english", "&8&lDark gray &f(&&f8)");
		chatColorGUI.addDefault(path + "display-names.italian", "&8&lGrigio scuro &f(&&f8)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to dark gray."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul grigio scuro."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/7af6fab767ca4d7df6217b895b667bcacc524d407068619f819a070f3f629ce0");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &8"));
		chatColorGUI.addDefault(path + "x", 6);
		chatColorGUI.addDefault(path + "y", 3);
		path = "icons.black.";
		chatColorGUI.addDefault(path + "display-names.english", "&0&lBlack &f(&&f0)");
		chatColorGUI.addDefault(path + "display-names.italian", "&0&lNero &f(&&f0)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7chat's color to black."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il", "&7colore della tua chat sul nero."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/974fe9cb80029d66345277aa560d41ef1030962b7f29abf23961d9eba84250a3");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &0"));
		chatColorGUI.addDefault(path + "x", 7);
		chatColorGUI.addDefault(path + "y", 3);
		path = "icons.hexadecimal.";
		chatColorGUI.addDefault(path + "display-names.english", "{random_color}&lHexadecimal &f(#rrggbb)");
		chatColorGUI.addDefault(path + "display-names.italian", "{random_color}&lEsadecimale &f(#rrggbb)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your chat's", "&7color to a hexadecimal color.", "", "&7Requirement: &d&lVIP"));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare il colore", "&7della tua chat su un colore esadecimale.", "", "&7Requisito: &d&lVIP"));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			chatColorGUI.addDefault(path + "material", "skull");
			chatColorGUI.addDefault(path + "damage", 3);
		} else chatColorGUI.addDefault(path + "material", "player_head");
		chatColorGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/529d8283fdb9456d5dccccb61ffa595450ac58e3d08237a70ca6782c0997253");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor #"));
		chatColorGUI.addDefault(path + "x", 8);
		chatColorGUI.addDefault(path + "y", 3);
		path = "icons.reset.";
		chatColorGUI.addDefault(path + "display-names.english", "&c&lReset &f(&&fr)");
		chatColorGUI.addDefault(path + "display-names.italian", "&c&lReset &f(&&fr)");
		chatColorGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to reset", "&7your chat's color."));
		chatColorGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per resettare", "&7il colore della tua chat."));
		chatColorGUI.addDefault(path + "material", "barrier");
		chatColorGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor &r"));
		chatColorGUI.addDefault(path + "x", 9);
		chatColorGUI.addDefault(path + "y", 3);
		
		chatColorGUI.save();
	}
	
	public void addEmojisToneGUIDefaults(boolean forceAdd) throws IOException {
		Configuration emojisToneGUI = configurations.get(ConfigurationType.EMOJIS_TONE_GUI);
		
		if (!Files.exists(emojisToneGUI.getPath()))
			emojisToneGUI.createFile();
		else if (!forceAdd)
			return;
		
		emojisToneGUI.addDefault("settings.rows", 3);
		emojisToneGUI.addDefault("settings.titles.english", "&e&lEmojis' tone");
		emojisToneGUI.addDefault("settings.titles.italian", "&e&lTono delle emojis");
		emojisToneGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &f{0} &aGUI.");
		emojisToneGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &f{0} &ain corso.");
		emojisToneGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		emojisToneGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		emojisToneGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		emojisToneGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		emojisToneGUI.addDefault("settings.click-sound.volume", 1F);
		emojisToneGUI.addDefault("settings.click-sound.pitch", 1F);
		path = "icons.info.";
		emojisToneGUI.addDefault(path + "display-names.english", "&e&lEmojis' tone");
		emojisToneGUI.addDefault(path + "display-names.italian", "&e&lTono delle emojis");
		emojisToneGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to change", "&7your emojis' default tone."));
		emojisToneGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di cambiare", "&7il tono predefinito delle tue emojis."));
		emojisToneGUI.addDefault(path + "material", "paper");
		emojisToneGUI.addDefault(path + "keep-open", true);
		emojisToneGUI.addDefault(path + "x", 5);
		emojisToneGUI.addDefault(path + "y", 1);
		path = "icons.default-tone.";
		emojisToneGUI.addDefault(path + "display-names.english", "&e&lDefault tone &f(&&fe)");
		emojisToneGUI.addDefault(path + "display-names.italian", "&e&lTono predefinito &f(&&fe)");
		emojisToneGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to reset", "&7your emojis' tone."));
		emojisToneGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per", "&7resettare il tono delle tue emojis."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			emojisToneGUI.addDefault(path + "material", "skull");
			emojisToneGUI.addDefault(path + "damage", 3);
		} else emojisToneGUI.addDefault(path + "material", "player_head");
		emojisToneGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/e10cbeeff6184a4081d0a5462e2751793e65c68a4c6119629bf3aa3d1dff3a57");
		emojisToneGUI.addDefault(path + "commands", Arrays.asList("p: emojistone reset"));
		emojisToneGUI.addDefault(path + "x", 2);
		emojisToneGUI.addDefault(path + "y", 2);
		path = "icons.tone-1.";
		emojisToneGUI.addDefault(path + "display-names.english", "#F9E0C1&lTone #F9E0C1#&l1 &f(#&fF9E0C1)");
		emojisToneGUI.addDefault(path + "display-names.italian", "#F9E0C1&lTono #F9E0C1#&l1 &f(#&fF9E0C1)");
		emojisToneGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7emojis' tone to tone #1."));
		emojisToneGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare", "&7il tono delle tue emojis sul tono #1."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			emojisToneGUI.addDefault(path + "material", "skull");
			emojisToneGUI.addDefault(path + "damage", 3);
		} else emojisToneGUI.addDefault(path + "material", "player_head");
		emojisToneGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/f7e8e73cf7c1e43266d57efbd7e2707ec6c365d2984481d5783a1fda19281c50");
		emojisToneGUI.addDefault(path + "commands", Arrays.asList("p: emojistone 1"));
		emojisToneGUI.addDefault(path + "x", 4);
		emojisToneGUI.addDefault(path + "y", 2);
		path = "icons.tone-2.";
		emojisToneGUI.addDefault(path + "display-names.english", "#E3C29C&lTone #E3C29C#&l2 &f(#&fE3C29C)");
		emojisToneGUI.addDefault(path + "display-names.italian", "#E3C29C&lTono #E3C29C#&l2 &f(#&fE3C29C)");
		emojisToneGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7emojis' tone to tone #2."));
		emojisToneGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare", "&7il tono delle tue emojis sul tono #2."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			emojisToneGUI.addDefault(path + "material", "skull");
			emojisToneGUI.addDefault(path + "damage", 3);
		} else emojisToneGUI.addDefault(path + "material", "player_head");
		emojisToneGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/bcdb9ba0a6b28fe09bb8ddd9c4ea456d6a681dad2b6cc8c3755ddcef376aa9dd");
		emojisToneGUI.addDefault(path + "commands", Arrays.asList("p: emojistone 2"));
		emojisToneGUI.addDefault(path + "x", 5);
		emojisToneGUI.addDefault(path + "y", 2);
		path = "icons.tone-3.";
		emojisToneGUI.addDefault(path + "display-names.english", "#C6956C&lTone #C6956C#&l3 &f(#&fC6956C)");
		emojisToneGUI.addDefault(path + "display-names.italian", "#C6956C&lTono #C6956C#&l3 &f(#&fC6956C)");
		emojisToneGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7emojis' tone to tone #3."));
		emojisToneGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare", "&7il tono delle tue emojis sul tono #3."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			emojisToneGUI.addDefault(path + "material", "skull");
			emojisToneGUI.addDefault(path + "damage", 3);
		} else emojisToneGUI.addDefault(path + "material", "player_head");
		emojisToneGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/37d6ee1db4f10390d212c6e3e4df460373a2f2f695c56b0f08fccd214f79afa5");
		emojisToneGUI.addDefault(path + "commands", Arrays.asList("p: emojistone 3"));
		emojisToneGUI.addDefault(path + "x", 6);
		emojisToneGUI.addDefault(path + "y", 2);
		path = "icons.tone-4.";
		emojisToneGUI.addDefault(path + "display-names.english", "#A06940&lTone #A06940#&l4 &f(#&fA06940)");
		emojisToneGUI.addDefault(path + "display-names.italian", "#A06940&lTono #A06940#&l4 &f(#&fA06940)");
		emojisToneGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7emojis' tone to tone #4."));
		emojisToneGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare", "&7il tono delle tue emojis sul tono #4."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			emojisToneGUI.addDefault(path + "material", "skull");
			emojisToneGUI.addDefault(path + "damage", 3);
		} else emojisToneGUI.addDefault(path + "material", "player_head");
		emojisToneGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/d377f789e0a27af978c7cc47ab103b3f67cf0a265e02d6db2ce10b9f49a91ed7");
		emojisToneGUI.addDefault(path + "commands", Arrays.asList("p: emojistone 4"));
		emojisToneGUI.addDefault(path + "x", 7);
		emojisToneGUI.addDefault(path + "y", 2);
		path = "icons.tone-5.";
		emojisToneGUI.addDefault(path + "display-names.english", "#5C473C&lTone #5C473C#&l5 &f(#&f5C473C)");
		emojisToneGUI.addDefault(path + "display-names.italian", "#5C473C&lTono #5C473C#&l5 &f(#&f5C473C)");
		emojisToneGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to set your", "&7emojis' tone to tone #5."));
		emojisToneGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per impostare", "&7il tono delle tue emojis sul tono #5."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			emojisToneGUI.addDefault(path + "material", "skull");
			emojisToneGUI.addDefault(path + "damage", 3);
		} else emojisToneGUI.addDefault(path + "material", "player_head");
		emojisToneGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/a31764a2204938f97ac9a52fc9e1dd93898f10cbb1985957fa3999e38b0855a5");
		emojisToneGUI.addDefault(path + "commands", Arrays.asList("p: emojistone 5"));
		emojisToneGUI.addDefault(path + "x", 8);
		emojisToneGUI.addDefault(path + "y", 2);
		
		emojisToneGUI.save();
	}
	
	public void addBanlistGUIDefaults(boolean forceAdd) throws IOException {
		Configuration banlistGUI = configurations.get(ConfigurationType.BANLIST_GUI);
		
		if (!Files.exists(banlistGUI.getPath()))
			banlistGUI.createFile();
		else if (!forceAdd)
			return;
		
		banlistGUI.addDefault("settings.rows", 5);
		banlistGUI.addDefault("settings.titles.english", "&4&lBanlist &8(Page {current_page}/{max_page})");
		banlistGUI.addDefault("settings.titles.italian", "&4&lBanlist &8(Pagina {current_page}/{max_page})");
		banlistGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &4&lBanlist &aGUI.");
		banlistGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &4&lBanlist &ain corso.");
		banlistGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		banlistGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		banlistGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		banlistGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		banlistGUI.addDefault("settings.click-sound.volume", 1F);
		banlistGUI.addDefault("settings.click-sound.pitch", 1F);
		
		banlistGUI.addDefault("settings.filling-function.start-slot", 9);
		banlistGUI.addDefault("settings.filling-function.end-slot", 35);
		
		banlistGUI.addDefault("settings.filling-function.empty-list-icon.display-names.english", "&9&lNo bans");
		banlistGUI.addDefault("settings.filling-function.empty-list-icon.display-names.italian", "&9&lNessun ban");
		banlistGUI.addDefault("settings.filling-function.empty-list-icon.lores.english", Arrays.asList("&7There are no active", "&7bans at the moment."));
		banlistGUI.addDefault("settings.filling-function.empty-list-icon.lores.italian", Arrays.asList("&7Non ci sono bans", "&7attivi al momento."));
		banlistGUI.addDefault("settings.filling-function.empty-list-icon.material", VersionUtils.getVersion().isAtLeast(Version.V1_10) ? "structure_void" : "barrier");
		banlistGUI.addDefault("settings.filling-function.empty-list-icon.keep-open", true);
		banlistGUI.addDefault("settings.filling-function.empty-list-icon.glowing", true);
		banlistGUI.addDefault("settings.filling-function.empty-list-icon.x", 5);
		banlistGUI.addDefault("settings.filling-function.empty-list-icon.y", 3);
		
		banlistGUI.addDefault("settings.filling-function.icon-layouts.ban.display-names.english", "&4#&l{id}");
		banlistGUI.addDefault("settings.filling-function.icon-layouts.ban.display-names.italian", "&4#&l{id}");
		banlistGUI.addDefault("settings.filling-function.icon-layouts.ban.lores.english", Arrays.asList("&7Player name: &f{player}", "&7Player UUID: &f{player_uuid}", "&7IP address: &f{ip_address}", "&7Staff member: &f{staff_member}", "&7Reason: &f{reason}", "&7Server: &f{server}", "&7Date: &f{date}", "&7Duration: &f{duration}", "&7Remaining time: &f{remaining_time}", "&7Type: &f{type}", "&7Active: &f{active}", "&7Global: &f{global}", "&7Silent: &f{silent}"));
		banlistGUI.addDefault("settings.filling-function.icon-layouts.ban.lores.italian", Arrays.asList("&7Nome giocatore: &f{player}", "&7UUID giocatore: &f{player_uuid}", "&7Indirizzo IP: &f{ip_address}", "&7Membro dello Staff: &f{staff_member}", "&7Motivo: &f{reason}", "&7Server: &f{server}", "&7Data: &f{date}", "&7Durata: &f{duration}", "&7Tempo rimanente: &f{remaining_time}", "&7Tipo: &f{type}", "&7Attivo: &f{active}", "&7Globale: &f{global}", "&7Silenzioso: &f{silent}"));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			banlistGUI.addDefault("settings.filling-function.icon-layouts.ban.material", "skull");
			banlistGUI.addDefault("settings.filling-function.icon-layouts.ban.damage", 3);
		} else banlistGUI.addDefault("settings.filling-function.icon-layouts.ban.material", "player_head");
		banlistGUI.addDefault("settings.filling-function.icon-layouts.ban.skull-owner", "{player}");
		banlistGUI.addDefault("settings.filling-function.icon-layouts.ban.commands", Arrays.asList("p: baninfo {id}"));
		
		banlistGUI.addDefault("settings.filling-function.icon-layouts.banip.display-names.english", "&4#&l{id}");
		banlistGUI.addDefault("settings.filling-function.icon-layouts.banip.display-names.italian", "&4#&l{id}");
		banlistGUI.addDefault("settings.filling-function.icon-layouts.banip.lores.english", Arrays.asList("&7Player name: &f{player}", "&7Player UUID: &f{player_uuid}", "&7IP address: &f{ip_address}", "&7Staff member: &f{staff_member}", "&7Reason: &f{reason}", "&7Server: &f{server}", "&7Date: &f{date}", "&7Duration: &f{duration}", "&7Remaining time: &f{remaining_time}", "&7Type: &f{type}", "&7Active: &f{active}", "&7Global: &f{global}", "&7Silent: &f{silent}"));
		banlistGUI.addDefault("settings.filling-function.icon-layouts.banip.lores.italian", Arrays.asList("&7Nome giocatore: &f{player}", "&7UUID giocatore: &f{player_uuid}", "&7Indirizzo IP: &f{ip_address}", "&7Membro dello Staff: &f{staff_member}", "&7Motivo: &f{reason}", "&7Server: &f{server}", "&7Data: &f{date}", "&7Durata: &f{duration}", "&7Tempo rimanente: &f{remaining_time}", "&7Tipo: &f{type}", "&7Attivo: &f{active}", "&7Globale: &f{global}", "&7Silenzioso: &f{silent}"));
		banlistGUI.addDefault("settings.filling-function.icon-layouts.banip.material", "name_tag");
		banlistGUI.addDefault("settings.filling-function.icon-layouts.banip.commands", Arrays.asList("p: baninfo {id}"));
		
		path = "icons.info.";
		banlistGUI.addDefault(path + "display-names.english", "&4&lBanlist");
		banlistGUI.addDefault(path + "display-names.italian", "&4&lBanlist");
		banlistGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to", "&7view the active bans' list."));
		banlistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di", "&7visualizzare la lista dei bans attivi."));
		banlistGUI.addDefault(path + "material", "paper");
		banlistGUI.addDefault(path + "keep-open", true);
		banlistGUI.addDefault(path + "x", 5);
		banlistGUI.addDefault(path + "y", 1);
		path = "icons.previous-page.";
		banlistGUI.addDefault(path + "display-names.english", "&e&lPrevious page");
		banlistGUI.addDefault(path + "display-names.italian", "&e&lPagina precedente");
		banlistGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the previous", "&7page of the GUI."));
		banlistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7precedente della GUI."));
		banlistGUI.addDefault(path + "material", "arrow");
		banlistGUI.addDefault(path + "keep-open", true);
		banlistGUI.addDefault(path + "commands", Arrays.asList("gui open banlist {viewer} {previous_page}"));
		banlistGUI.addDefault(path + "x", 4);
		banlistGUI.addDefault(path + "y", 5);
		path = "icons.next-page.";
		banlistGUI.addDefault(path + "display-names.english", "&e&lNext page");
		banlistGUI.addDefault(path + "display-names.italian", "&e&lPagina successiva");
		banlistGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the next", "&7page of the GUI."));
		banlistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7successiva della GUI."));
		banlistGUI.addDefault(path + "material", "arrow");
		banlistGUI.addDefault(path + "keep-open", true);
		banlistGUI.addDefault(path + "commands", Arrays.asList("gui open banlist {viewer} {next_page}"));
		banlistGUI.addDefault(path + "x", 6);
		banlistGUI.addDefault(path + "y", 5);
		path = "icons.refresh.";
		banlistGUI.addDefault(path + "display-names.english", "&e&lRefresh");
		banlistGUI.addDefault(path + "display-names.italian", "&e&lAggiorna");
		banlistGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to refresh the", "&7GUI's variables and values."));
		banlistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per aggiornare", "&7le variabili ed i valori della GUI."));
		banlistGUI.addDefault(path + "material", "clock");
		banlistGUI.addDefault(path + "keep-open", true);
		banlistGUI.addDefault(path + "commands", Arrays.asList("gui refresh banlist"));
		banlistGUI.addDefault(path + "x", 5);
		banlistGUI.addDefault(path + "y", 5);
		
		banlistGUI.save();
	}
	
	public void addWarnlistGUIDefaults(boolean forceAdd) throws IOException {
		Configuration warnlistGUI = configurations.get(ConfigurationType.WARNLIST_GUI);
		
		if (!Files.exists(warnlistGUI.getPath()))
			warnlistGUI.createFile();
		else if (!forceAdd)
			return;
		
		warnlistGUI.addDefault("settings.rows", 5);
		warnlistGUI.addDefault("settings.titles.english", "&c&lWarnlist &8(Page {current_page}/{max_page})");
		warnlistGUI.addDefault("settings.titles.italian", "&c&lWarnlist &8(Pagina {current_page}/{max_page})");
		warnlistGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &c&lWarnlist &aGUI.");
		warnlistGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &c&lWarnlist &ain corso.");
		warnlistGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		warnlistGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		warnlistGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		warnlistGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		warnlistGUI.addDefault("settings.click-sound.volume", 1F);
		warnlistGUI.addDefault("settings.click-sound.pitch", 1F);
		
		warnlistGUI.addDefault("settings.filling-function.start-slot", 9);
		warnlistGUI.addDefault("settings.filling-function.end-slot", 35);
		
		warnlistGUI.addDefault("settings.filling-function.empty-list-icon.display-names.english", "&9&lNo warnings");
		warnlistGUI.addDefault("settings.filling-function.empty-list-icon.display-names.italian", "&9&lNessun avviso");
		warnlistGUI.addDefault("settings.filling-function.empty-list-icon.lores.english", Arrays.asList("&7There are no active", "&7warnings at the moment."));
		warnlistGUI.addDefault("settings.filling-function.empty-list-icon.lores.italian", Arrays.asList("&7Non ci sono avvisi", "&7attivi al momento."));
		warnlistGUI.addDefault("settings.filling-function.empty-list-icon.material", VersionUtils.getVersion().isAtLeast(Version.V1_10) ? "structure_void" : "barrier");
		warnlistGUI.addDefault("settings.filling-function.empty-list-icon.keep-open", true);
		warnlistGUI.addDefault("settings.filling-function.empty-list-icon.glowing", true);
		warnlistGUI.addDefault("settings.filling-function.empty-list-icon.x", 5);
		warnlistGUI.addDefault("settings.filling-function.empty-list-icon.y", 3);
		
		warnlistGUI.addDefault("settings.filling-function.icon-layouts.warning.display-names.english", "&c#&l{id}");
		warnlistGUI.addDefault("settings.filling-function.icon-layouts.warning.display-names.italian", "&c#&l{id}");
		warnlistGUI.addDefault("settings.filling-function.icon-layouts.warning.lores.english", Arrays.asList("&7Player name: &f{player}", "&7Player UUID: &f{player_uuid}", "&7Staff member: &f{staff_member}", "&7Last reason: &f{reason}", "&7Server: &f{server}", "&7Date: &f{date}", "&7Duration: &f{duration}", "&7Remaining time: &f{remaining_time}", "&7Warnings: &f{amount}/{max_amount}", "&7Active: &f{active}", "&7Global: &f{global}", "&7Silent: &f{silent}"));
		warnlistGUI.addDefault("settings.filling-function.icon-layouts.warning.lores.italian", Arrays.asList("&7Nome giocatore: &f{player}", "&7UUID giocatore: &f{player_uuid}", "&7Membro dello Staff: &f{staff_member}", "&7Ultimo motivo: &f{reason}", "&7Server: &f{server}", "&7Data: &f{date}", "&7Durata: &f{duration}", "&7Tempo rimanente: &f{remaining_time}", "&7Avvisi: &f{amount}/{max_amount}", "&7Attivo: &f{active}", "&7Globale: &f{global}", "&7Silenzioso: &f{silent}"));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			warnlistGUI.addDefault("settings.filling-function.icon-layouts.warning.material", "skull");
			warnlistGUI.addDefault("settings.filling-function.icon-layouts.warning.damage", 3);
		} else warnlistGUI.addDefault("settings.filling-function.icon-layouts.warning.material", "player_head");
		warnlistGUI.addDefault("settings.filling-function.icon-layouts.warning.skull-owner", "{player}");
		warnlistGUI.addDefault("settings.filling-function.icon-layouts.warning.commands", Arrays.asList("p: warninginfo {id}"));
		
		path = "icons.info.";
		warnlistGUI.addDefault(path + "display-names.english", "&c&lWarnlist");
		warnlistGUI.addDefault(path + "display-names.italian", "&c&lWarnlist");
		warnlistGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to", "&7view the active warnings' list."));
		warnlistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di visualizzare", "&7la lista degli avvisi attivi."));
		warnlistGUI.addDefault(path + "material", "paper");
		warnlistGUI.addDefault(path + "keep-open", true);
		warnlistGUI.addDefault(path + "x", 5);
		warnlistGUI.addDefault(path + "y", 1);
		path = "icons.previous-page.";
		warnlistGUI.addDefault(path + "display-names.english", "&e&lPrevious page");
		warnlistGUI.addDefault(path + "display-names.italian", "&e&lPagina precedente");
		warnlistGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the previous", "&7page of the GUI."));
		warnlistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7precedente della GUI."));
		warnlistGUI.addDefault(path + "material", "arrow");
		warnlistGUI.addDefault(path + "keep-open", true);
		warnlistGUI.addDefault(path + "commands", Arrays.asList("gui open warnlist {viewer} {previous_page}"));
		warnlistGUI.addDefault(path + "x", 4);
		warnlistGUI.addDefault(path + "y", 5);
		path = "icons.next-page.";
		warnlistGUI.addDefault(path + "display-names.english", "&e&lNext page");
		warnlistGUI.addDefault(path + "display-names.italian", "&e&lPagina successiva");
		warnlistGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the next", "&7page of the GUI."));
		warnlistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7successiva della GUI."));
		warnlistGUI.addDefault(path + "material", "arrow");
		warnlistGUI.addDefault(path + "keep-open", true);
		warnlistGUI.addDefault(path + "commands", Arrays.asList("gui open warnlist {viewer} {next_page}"));
		warnlistGUI.addDefault(path + "x", 6);
		warnlistGUI.addDefault(path + "y", 5);
		path = "icons.refresh.";
		warnlistGUI.addDefault(path + "display-names.english", "&e&lRefresh");
		warnlistGUI.addDefault(path + "display-names.italian", "&e&lAggiorna");
		warnlistGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to refresh the", "&7GUI's variables and values."));
		warnlistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per aggiornare", "&7le variabili ed i valori della GUI."));
		warnlistGUI.addDefault(path + "material", "clock");
		warnlistGUI.addDefault(path + "keep-open", true);
		warnlistGUI.addDefault(path + "commands", Arrays.asList("gui refresh warnlist"));
		warnlistGUI.addDefault(path + "x", 5);
		warnlistGUI.addDefault(path + "y", 5);
		
		warnlistGUI.save();
	}
	
	public void addMutelistGUIDefaults(boolean forceAdd) throws IOException {
		Configuration mutelistGUI = configurations.get(ConfigurationType.MUTELIST_GUI);
		
		if (!Files.exists(mutelistGUI.getPath()))
			mutelistGUI.createFile();
		else if (!forceAdd)
			return;
		
		mutelistGUI.addDefault("settings.rows", 5);
		mutelistGUI.addDefault("settings.titles.english", "&e&lMutelist &8(Page {current_page}/{max_page})");
		mutelistGUI.addDefault("settings.titles.italian", "&e&lMutelist &8(Pagina {current_page}/{max_page})");
		mutelistGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &e&lMutelist &aGUI.");
		mutelistGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &e&lMutelist &ain corso.");
		mutelistGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		mutelistGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		mutelistGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		mutelistGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		mutelistGUI.addDefault("settings.click-sound.volume", 1F);
		mutelistGUI.addDefault("settings.click-sound.pitch", 1F);
		
		mutelistGUI.addDefault("settings.filling-function.start-slot", 9);
		mutelistGUI.addDefault("settings.filling-function.end-slot", 35);
		
		mutelistGUI.addDefault("settings.filling-function.empty-list-icon.display-names.english", "&9&lNo mutes");
		mutelistGUI.addDefault("settings.filling-function.empty-list-icon.display-names.italian", "&9&lNessun mute");
		mutelistGUI.addDefault("settings.filling-function.empty-list-icon.lores.english", Arrays.asList("&7There are no active", "&7mutes at the moment."));
		mutelistGUI.addDefault("settings.filling-function.empty-list-icon.lores.italian", Arrays.asList("&7Non ci sono mutes", "&7attivi al momento."));
		mutelistGUI.addDefault("settings.filling-function.empty-list-icon.material", VersionUtils.getVersion().isAtLeast(Version.V1_10) ? "structure_void" : "barrier");
		mutelistGUI.addDefault("settings.filling-function.empty-list-icon.keep-open", true);
		mutelistGUI.addDefault("settings.filling-function.empty-list-icon.glowing", true);
		mutelistGUI.addDefault("settings.filling-function.empty-list-icon.x", 5);
		mutelistGUI.addDefault("settings.filling-function.empty-list-icon.y", 3);
		
		mutelistGUI.addDefault("settings.filling-function.icon-layouts.mute.display-names.english", "&e#&l{id}");
		mutelistGUI.addDefault("settings.filling-function.icon-layouts.mute.display-names.italian", "&e#&l{id}");
		mutelistGUI.addDefault("settings.filling-function.icon-layouts.mute.lores.english", Arrays.asList("&7Player name: &f{player}", "&7Player UUID: &f{player_uuid}", "&7Staff member: &f{staff_member}", "&7Reason: &f{reason}", "&7Server: &f{server}", "&7Date: &f{date}", "&7Duration: &f{duration}", "&7Remaining time: &f{remaining_time}", "&7Active: &f{active}", "&7Global: &f{global}", "&7Silent: &f{silent}"));
		mutelistGUI.addDefault("settings.filling-function.icon-layouts.mute.lores.italian", Arrays.asList("&7Nome giocatore: &f{player}", "&7UUID giocatore: &f{player_uuid}", "&7Membro dello Staff: &f{staff_member}", "&7Motivo: &f{reason}", "&7Server: &f{server}", "&7Data: &f{date}", "&7Durata: &f{duration}", "&7Tempo rimanente: &f{remaining_time}", "&7Attivo: &f{active}", "&7Globale: &f{global}", "&7Silenzioso: &f{silent}"));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			mutelistGUI.addDefault("settings.filling-function.icon-layouts.mute.material", "skull");
			mutelistGUI.addDefault("settings.filling-function.icon-layouts.mute.damage", 3);
		} else mutelistGUI.addDefault("settings.filling-function.icon-layouts.mute.material", "player_head");
		mutelistGUI.addDefault("settings.filling-function.icon-layouts.mute.skull-owner", "{player}");
		mutelistGUI.addDefault("settings.filling-function.icon-layouts.mute.commands", Arrays.asList("p: muteinfo {id}"));
		
		path = "icons.info.";
		mutelistGUI.addDefault(path + "display-names.english", "&e&lMutelist");
		mutelistGUI.addDefault(path + "display-names.italian", "&e&lMutelist");
		mutelistGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to", "&7view the active mutes' list."));
		mutelistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di", "&7visualizzare la lista dei mutes attivi."));
		mutelistGUI.addDefault(path + "material", "paper");
		mutelistGUI.addDefault(path + "keep-open", true);
		mutelistGUI.addDefault(path + "x", 5);
		mutelistGUI.addDefault(path + "y", 1);
		path = "icons.previous-page.";
		mutelistGUI.addDefault(path + "display-names.english", "&e&lPrevious page");
		mutelistGUI.addDefault(path + "display-names.italian", "&e&lPagina precedente");
		mutelistGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the previous", "&7page of the GUI."));
		mutelistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7precedente della GUI."));
		mutelistGUI.addDefault(path + "material", "arrow");
		mutelistGUI.addDefault(path + "keep-open", true);
		mutelistGUI.addDefault(path + "commands", Arrays.asList("gui open mutelist {viewer} {previous_page}"));
		mutelistGUI.addDefault(path + "x", 4);
		mutelistGUI.addDefault(path + "y", 5);
		path = "icons.next-page.";
		mutelistGUI.addDefault(path + "display-names.english", "&e&lNext page");
		mutelistGUI.addDefault(path + "display-names.italian", "&e&lPagina successiva");
		mutelistGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the next", "&7page of the GUI."));
		mutelistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7successiva della GUI."));
		mutelistGUI.addDefault(path + "material", "arrow");
		mutelistGUI.addDefault(path + "keep-open", true);
		mutelistGUI.addDefault(path + "commands", Arrays.asList("gui open mutelist {viewer} {next_page}"));
		mutelistGUI.addDefault(path + "x", 6);
		mutelistGUI.addDefault(path + "y", 5);
		path = "icons.refresh.";
		mutelistGUI.addDefault(path + "display-names.english", "&e&lRefresh");
		mutelistGUI.addDefault(path + "display-names.italian", "&e&lAggiorna");
		mutelistGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to refresh the", "&7GUI's variables and values."));
		mutelistGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per aggiornare", "&7le variabili ed i valori della GUI."));
		mutelistGUI.addDefault(path + "material", "clock");
		mutelistGUI.addDefault(path + "keep-open", true);
		mutelistGUI.addDefault(path + "commands", Arrays.asList("gui refresh mutelist"));
		mutelistGUI.addDefault(path + "x", 5);
		mutelistGUI.addDefault(path + "y", 5);
		
		mutelistGUI.save();
	}
	
	public void addViolationsGUIDefaults(boolean forceAdd) throws IOException {
		Configuration violationsGUI = configurations.get(ConfigurationType.VIOLATIONS_GUI);
		
		if (!Files.exists(violationsGUI.getPath()))
			violationsGUI.createFile();
		else if (!forceAdd)
			return;
		
		violationsGUI.addDefault("settings.rows", 5);
		violationsGUI.addDefault("settings.titles.english", "&3&lViolations &8(Page {current_page}/{max_page})");
		violationsGUI.addDefault("settings.titles.italian", "&3&lViolazioni &8(Pagina {current_page}/{max_page})");
		violationsGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &3&lViolations &aGUI.");
		violationsGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &3&lViolazioni &ain corso.");
		violationsGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		violationsGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		violationsGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		violationsGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		violationsGUI.addDefault("settings.click-sound.volume", 1F);
		violationsGUI.addDefault("settings.click-sound.pitch", 1F);
		
		violationsGUI.addDefault("settings.filling-function.start-slot", 9);
		violationsGUI.addDefault("settings.filling-function.end-slot", 35);
		
		violationsGUI.addDefault("settings.filling-function.empty-list-icon.display-names.english", "&9&lNo violations");
		violationsGUI.addDefault("settings.filling-function.empty-list-icon.display-names.italian", "&9&lNessuna violazione");
		violationsGUI.addDefault("settings.filling-function.empty-list-icon.lores.english", Arrays.asList("&7There are no recent", "&7violations at the moment."));
		violationsGUI.addDefault("settings.filling-function.empty-list-icon.lores.italian", Arrays.asList("&7Non ci sono violazioni", "&7recenti al momento."));
		violationsGUI.addDefault("settings.filling-function.empty-list-icon.material", VersionUtils.getVersion().isAtLeast(Version.V1_10) ? "structure_void" : "barrier");
		violationsGUI.addDefault("settings.filling-function.empty-list-icon.keep-open", true);
		violationsGUI.addDefault("settings.filling-function.empty-list-icon.glowing", true);
		violationsGUI.addDefault("settings.filling-function.empty-list-icon.x", 5);
		violationsGUI.addDefault("settings.filling-function.empty-list-icon.y", 3);
		
		violationsGUI.addDefault("settings.filling-function.icon-layouts.player.display-names.english", "&c&l{cheater}");
		violationsGUI.addDefault("settings.filling-function.icon-layouts.player.display-names.italian", "&c&l{cheater}");
		violationsGUI.addDefault("settings.filling-function.icon-layouts.player.lores.english", Arrays.asList("&7Anticheat: &f{anticheat}", "&7Server: &f{server}", "&7Ping: &f{ping_format} ms&7, TPS: &f{tps}", "&7Version: &f{client_edition} Edition {version} ({version_protocol})", "", "&7Recent violations:", "{violations}", "", "&e&oClick to teleport you there!"));
		violationsGUI.addDefault("settings.filling-function.icon-layouts.player.lores.italian", Arrays.asList("&7Anticheat: &f{anticheat}", "&7Server: &f{server}", "&7Ping: &f{ping_format} ms&7, TPS: &f{tps}", "&7Versione: &f{client_edition} Edition {version} ({version_protocol})", "", "&7Violazioni recenti:", "{violations}", "", "&e&oClicca per teletrasportarti!"));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			violationsGUI.addDefault("settings.filling-function.icon-layouts.player.material", "skull");
			violationsGUI.addDefault("settings.filling-function.icon-layouts.player.damage", 3);
		} else violationsGUI.addDefault("settings.filling-function.icon-layouts.player.material", "player_head");
		violationsGUI.addDefault("settings.filling-function.icon-layouts.player.skull-owner", "{cheater}");
		violationsGUI.addDefault("settings.filling-function.icon-layouts.player.commands", Arrays.asList("p: silentteleport player {cheater}"));
		
		violationsGUI.addDefault("settings.violations-list-format", "&8- &7{cheat_display_name}: &f{amount}x");
		
		path = "icons.info.";
		violationsGUI.addDefault(path + "display-names.english", "&3&lViolations");
		violationsGUI.addDefault(path + "display-names.italian", "&3&lViolazioni");
		violationsGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI lists the", "&7anticheat's violations."));
		violationsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI elenca le", "&7violazioni dell'anticheat."));
		violationsGUI.addDefault(path + "material", "paper");
		violationsGUI.addDefault(path + "keep-open", true);
		violationsGUI.addDefault(path + "x", 5);
		violationsGUI.addDefault(path + "y", 1);
		path = "icons.previous-page.";
		violationsGUI.addDefault(path + "display-names.english", "&e&lPrevious page");
		violationsGUI.addDefault(path + "display-names.italian", "&e&lPagina precedente");
		violationsGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the previous", "&7page of the GUI."));
		violationsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7precedente della GUI."));
		violationsGUI.addDefault(path + "material", "arrow");
		violationsGUI.addDefault(path + "keep-open", true);
		violationsGUI.addDefault(path + "commands", Arrays.asList("gui open violations {viewer} {previous_page}"));
		violationsGUI.addDefault(path + "x", 4);
		violationsGUI.addDefault(path + "y", 5);
		path = "icons.next-page.";
		violationsGUI.addDefault(path + "display-names.english", "&e&lNext page");
		violationsGUI.addDefault(path + "display-names.italian", "&e&lPagina successiva");
		violationsGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the next", "&7page of the GUI."));
		violationsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7successiva della GUI."));
		violationsGUI.addDefault(path + "material", "arrow");
		violationsGUI.addDefault(path + "keep-open", true);
		violationsGUI.addDefault(path + "commands", Arrays.asList("gui open violations {viewer} {next_page}"));
		violationsGUI.addDefault(path + "x", 6);
		violationsGUI.addDefault(path + "y", 5);
		path = "icons.refresh.";
		violationsGUI.addDefault(path + "display-names.english", "&e&lRefresh");
		violationsGUI.addDefault(path + "display-names.italian", "&e&lAggiorna");
		violationsGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to refresh the", "&7GUI's variables and values."));
		violationsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per aggiornare", "&7le variabili ed i valori della GUI."));
		violationsGUI.addDefault(path + "material", "clock");
		violationsGUI.addDefault(path + "keep-open", true);
		violationsGUI.addDefault(path + "commands", Arrays.asList("gui refresh violations"));
		violationsGUI.addDefault(path + "x", 5);
		violationsGUI.addDefault(path + "y", 5);
		
		violationsGUI.save();
	}
	
	public void addPlayerInfoGUIDefaults(boolean forceAdd) throws IOException {
		Configuration playerInfoGUI = configurations.get(ConfigurationType.PLAYER_INFO_GUI);
		
		if (!Files.exists(playerInfoGUI.getPath()))
			playerInfoGUI.createFile();
		else if (!forceAdd)
			return;
		
		playerInfoGUI.addDefault("settings.rows", 5);
		playerInfoGUI.addDefault("settings.titles.english", "&f&lPlayer info");
		playerInfoGUI.addDefault("settings.titles.italian", "&f&lInfo giocatore");
		playerInfoGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &f{0} &aGUI.");
		playerInfoGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &f{0} &ain corso.");
		playerInfoGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		playerInfoGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		playerInfoGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		playerInfoGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		playerInfoGUI.addDefault("settings.click-sound.volume", 1F);
		playerInfoGUI.addDefault("settings.click-sound.pitch", 1F);
		
		path = "icons.info.";
		playerInfoGUI.addDefault(path + "display-names.english", "&f&lPlayer info");
		playerInfoGUI.addDefault(path + "display-names.italian", "&f&lInfo giocatore");
		playerInfoGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to view", "&7information about a player."));
		playerInfoGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di visualizzare", "&7le informazioni di un giocatore."));
		playerInfoGUI.addDefault(path + "material", "paper");
		playerInfoGUI.addDefault(path + "keep-open", true);
		playerInfoGUI.addDefault(path + "x", 5);
		playerInfoGUI.addDefault(path + "y", 1);
		path = "icons.overview.";
		playerInfoGUI.addDefault(path + "display-names.english", "&a&l{player}");
		playerInfoGUI.addDefault(path + "display-names.italian", "&a&l{player}");
		playerInfoGUI.addDefault(path + "lores.english", Arrays.asList("&7UUID: &f{uuid}", "&7Player ID: &f#{player_id}", "&7Version: &f{client_edition} Edition {version} (protocol: {version_protocol})", "&7Language: &f{language_display_name}", "&7Ping: &f{ping_format} ms &f({ping_quality_text}&f)", "&7Time played: &f{time_played}", "&7Last login: &f{last_login} ago", "&7Messages sent: &f{messages_sent}x", "&7Antispam infractions: &f{antispam_infractions}x"));
		playerInfoGUI.addDefault(path + "lores.italian", Arrays.asList("&7UUID: &f{uuid}", "&7ID giocatore: &f#{player_id}", "&7Versione: &f{client_edition} Edition {version} (protocollo: {version_protocol})", "&7Lingua: &f{language_display_name}", "&7Ping: &f{ping_format} ms &f({ping_quality_text}&f)", "&7Tempo di gioco: &f{time_played}", "&7Ultimo login: &f{last_login} fa", "&7Messaggi inviati: &f{messages_sent}x", "&7Infrazioni antispam: &f{antispam_infractions}x"));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			playerInfoGUI.addDefault(path + "material", "skull");
			playerInfoGUI.addDefault(path + "damage", 3);
		} else playerInfoGUI.addDefault(path + "material", "player_head");
		playerInfoGUI.addDefault(path + "skull-owner", "{player}");
		playerInfoGUI.addDefault(path + "keep-open", true);
		playerInfoGUI.addDefault(path + "commands", Arrays.asList("p: playerinfo {player} -c"));
		playerInfoGUI.addDefault(path + "x", 5);
		playerInfoGUI.addDefault(path + "y", 3);
		path = "icons.ip-lookup.";
		playerInfoGUI.addDefault(path + "display-names.english", "&5&lIP lookup");
		playerInfoGUI.addDefault(path + "display-names.italian", "&5&lLookup IP");
		playerInfoGUI.addDefault(path + "lores.english", Arrays.asList("&7IP address: &f{ip_address}", "&7ISP: &f{isp}", "&7Country: &f{country}, {continent}", "&7Subdivisions: &f{subdivisions}", "&7City: &f{city} (postal code: {postal_code})", "&7Coords: &f{latitude}° {longitude}°", "&7Accuracy radius: &f~{accuracy_radius_km} km", "&7Relative date: &f{relative_date_full}"));
		playerInfoGUI.addDefault(path + "lores.italian", Arrays.asList("&7Indirizzo IP: &f{ip_address}", "&7ISP: &f{isp}", "&7Paese: &f{country}, {continent}", "&7Suddivisioni: &f{subdivisions}", "&7Città: &f{city} (codice postale: {postal_code})", "&7Coordinate: &f{latitude}° {longitude}°", "&7Raggio di accuratezza: &f~{accuracy_radius_km} km", "&7Data relativa: &f{relative_date_full}"));
		playerInfoGUI.addDefault(path + "material", "compass");
		playerInfoGUI.addDefault(path + "glowing", true);
		playerInfoGUI.addDefault(path + "commands", Arrays.asList("p: iplookup {player}"));
		playerInfoGUI.addDefault(path + "x", 1);
		playerInfoGUI.addDefault(path + "y", 3);
		path = "icons.rank.";
		playerInfoGUI.addDefault(path + "display-names.english", "&6&lRank");
		playerInfoGUI.addDefault(path + "display-names.italian", "&6&lRank");
		playerInfoGUI.addDefault(path + "lores.english", Arrays.asList("&7Rank: &f{rank_display_name}", "&7Prefix: &f{prefix}", "&7Suffix: &f{suffix}", "&7Tag: &f{tag_prefix}{tag_name_color}{player}{tag_suffix}", "&7Description:", "&f{rank_description}"));
		playerInfoGUI.addDefault(path + "lores.italian", Arrays.asList("&7Rank: &f{rank_display_name}", "&7Prefix: &f{prefix}", "&7Suffix: &f{suffix}", "&7Tag: &f{tag_prefix}{tag_name_color}{player}{tag_suffix}", "&7Descrizione:", "&f{rank_description}"));
		playerInfoGUI.addDefault(path + "material", "name_tag");
		playerInfoGUI.addDefault(path + "glowing", true);
		playerInfoGUI.addDefault(path + "commands", Arrays.asList("p: rankinfo {player}"));
		playerInfoGUI.addDefault(path + "x", 3);
		playerInfoGUI.addDefault(path + "y", 3);
		path = "icons.punishments.";
		playerInfoGUI.addDefault(path + "display-names.english", "&c&lPunishments");
		playerInfoGUI.addDefault(path + "display-names.italian", "&c&lPunizioni");
		playerInfoGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to open this", "&7player's punishments GUI."));
		playerInfoGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per aprire la GUI", "&7delle punizioni di questo giocatore."));
		playerInfoGUI.addDefault(path + "material", "barrier");
		playerInfoGUI.addDefault(path + "glowing", true);
		playerInfoGUI.addDefault(path + "commands", Arrays.asList("p: playerpunishments {player}"));
		playerInfoGUI.addDefault(path + "x", 7);
		playerInfoGUI.addDefault(path + "y", 3);
		path = "icons.violations.";
		playerInfoGUI.addDefault(path + "display-names.english", "&3&lViolations");
		playerInfoGUI.addDefault(path + "display-names.italian", "&3&lViolazioni");
		playerInfoGUI.addDefault(path + "lores.english", Arrays.asList("&7Click to open this player's", "&7anticheat's violations GUI."));
		playerInfoGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca per aprire la GUI delle violazioni", "&7dell'anticheat di questo giocatore."));
		playerInfoGUI.addDefault(path + "material", "iron_sword");
		playerInfoGUI.addDefault(path + "glowing", true);
		playerInfoGUI.addDefault(path + "item-flags", Arrays.asList("HIDE_ATTRIBUTES"));
		playerInfoGUI.addDefault(path + "commands", Arrays.asList("p: playerviolations {player}"));
		playerInfoGUI.addDefault(path + "x", 9);
		playerInfoGUI.addDefault(path + "y", 3);
		
		playerInfoGUI.save();
	}
	
	public void addPreferencesGUIDefaults(boolean forceAdd) throws IOException {
		Configuration preferencesGUI = configurations.get(ConfigurationType.PREFERENCES_GUI);
		
		if (!Files.exists(preferencesGUI.getPath()))
			preferencesGUI.createFile();
		else if (!forceAdd)
			return;
		
		preferencesGUI.addDefault("settings.rows", 3);
		preferencesGUI.addDefault("settings.titles.english", "&6&lPreferences");
		preferencesGUI.addDefault("settings.titles.italian", "&6&lPreferenze");
		preferencesGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &f{0} &aGUI.");
		preferencesGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &f{0} &ain corso.");
		preferencesGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		preferencesGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		preferencesGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		preferencesGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		preferencesGUI.addDefault("settings.click-sound.volume", 1F);
		preferencesGUI.addDefault("settings.click-sound.pitch", 1F);
		preferencesGUI.addDefault("settings.visibility-placeholder-format.enabled", "&2ON");
		preferencesGUI.addDefault("settings.visibility-placeholder-format.disabled", "&4OFF");
		
		path = "icons.info.";
		preferencesGUI.addDefault(path + "display-names.english", "&6&lPreferences");
		preferencesGUI.addDefault(path + "display-names.italian", "&6&lPreferenze");
		preferencesGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to set", "&7some personal preferences."));
		preferencesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di impostare", "&7alcune preferenze personali."));
		preferencesGUI.addDefault(path + "material", "paper");
		preferencesGUI.addDefault(path + "keep-open", true);
		preferencesGUI.addDefault(path + "x", 5);
		preferencesGUI.addDefault(path + "y", 1);
		path = "icons.language.";
		preferencesGUI.addDefault(path + "display-names.english", "&b&lLanguages");
		preferencesGUI.addDefault(path + "display-names.italian", "&b&lLingue");
		preferencesGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to change", "&7your language on the server."));
		preferencesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di", "&7cambiare la tua lingua sul server."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			preferencesGUI.addDefault(path + "material", "skull");
			preferencesGUI.addDefault(path + "damage", 3);
		} else preferencesGUI.addDefault(path + "material", "player_head");
		preferencesGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/25485031b37f0d8a4f3b7816eb717f03de89a87f6a40602aef52221cdfaf7488");
		preferencesGUI.addDefault(path + "commands", Arrays.asList("p: language"));
		preferencesGUI.addDefault(path + "x", 2);
		preferencesGUI.addDefault(path + "y", 2);
		path = "icons.chat-color.";
		preferencesGUI.addDefault(path + "display-names.english", "&f&lChat color");
		preferencesGUI.addDefault(path + "display-names.italian", "&f&lColore della chat");
		preferencesGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to change", "&7your chat's default color."));
		preferencesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di cambiare", "&7il colore predefinito della tua chat."));
		preferencesGUI.addDefault(path + "material", "writable_book");
		preferencesGUI.addDefault(path + "glowing", true);
		preferencesGUI.addDefault(path + "commands", Arrays.asList("p: chatcolor"));
		preferencesGUI.addDefault(path + "x", 3);
		preferencesGUI.addDefault(path + "y", 2);
		path = "icons.emojis-tone.";
		preferencesGUI.addDefault(path + "display-names.english", "&e&lEmojis' tone");
		preferencesGUI.addDefault(path + "display-names.italian", "&e&lTono delle emojis");
		preferencesGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to change", "&7your emojis' default tone."));
		preferencesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di cambiare", "&7il tono predefinito delle tue emojis."));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			preferencesGUI.addDefault(path + "material", "skull");
			preferencesGUI.addDefault(path + "damage", 3);
		} else preferencesGUI.addDefault(path + "material", "player_head");
		preferencesGUI.addDefault(path + "skull-texture-url", "http://textures.minecraft.net/texture/e10cbeeff6184a4081d0a5462e2751793e65c68a4c6119629bf3aa3d1dff3a57");
		preferencesGUI.addDefault(path + "commands", Arrays.asList("p: emojistone"));
		preferencesGUI.addDefault(path + "x", 4);
		preferencesGUI.addDefault(path + "y", 2);
		path = "icons.overview.";
		preferencesGUI.addDefault(path + "display-names.english", "&a&l{player}");
		preferencesGUI.addDefault(path + "display-names.italian", "&a&l{player}");
		preferencesGUI.addDefault(path + "lores.english", Arrays.asList("&7Player ID: &f#{player_id}", "&7Ping: {ping_format} ms &f({ping_quality_text}&f)", "&7Time played: &f{time_played}", "&7Bans/warnings/kicks/mutes: &f{player_bans}x/{player_warnings}x/{player_kicks}x/{player_mutes}x", "&7Messages sent: &f{messages_sent}x"));
		preferencesGUI.addDefault(path + "lores.italian", Arrays.asList("&7ID giocatore &f#{player_id}", "&7Ping: {ping_format} ms &f({ping_quality_text}&f)", "&7Tempo di gioco: &f{time_played}", "&7Bans/warnings/kicks/mutes: &f{player_bans}x/{player_warnings}x/{player_kicks}x/{player_mutes}x", "&7Messaggi inviati: &f{messages_sent}x"));
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			preferencesGUI.addDefault(path + "material", "skull");
			preferencesGUI.addDefault(path + "damage", 3);
		} else preferencesGUI.addDefault(path + "material", "player_head");
		preferencesGUI.addDefault(path + "skull-owner", "{player}");
		preferencesGUI.addDefault(path + "keep-open", true);
		preferencesGUI.addDefault(path + "x", 5);
		preferencesGUI.addDefault(path + "y", 2);
		path = "icons.scoreboard.";
		preferencesGUI.addDefault(path + "display-names.english", "&a&lScoreboard");
		preferencesGUI.addDefault(path + "display-names.italian", "&a&lScoreboard");
		preferencesGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to toggle", "&7the scoreboard's visibility.", "", "&7Visibility: {scoreboard_visibility}"));
		preferencesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per cambiare", "&7la visibilità della scoreboard.", "", "&7Visibilità: {scoreboard_visibility}"));
		preferencesGUI.addDefault(path + "material", "painting");
		preferencesGUI.addDefault(path + "keep-open", true);
		preferencesGUI.addDefault(path + "glowing", true);
		preferencesGUI.addDefault(path + "commands", Arrays.asList("p: scoreboard", "gui refresh preferences-{player}"));
		preferencesGUI.addDefault(path + "x", 6);
		preferencesGUI.addDefault(path + "y", 2);
		path = "icons.bossbar.";
		preferencesGUI.addDefault(path + "display-names.english", "&d&lBossbar");
		preferencesGUI.addDefault(path + "display-names.italian", "&d&lBossbar");
		preferencesGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to toggle", "&7the bossbar's visibility.", "", "&7Visibility: {bossbar_visibility}"));
		preferencesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per cambiare", "&7la visibilità della bossbar.", "", "&7Visibilità: {bossbar_visibility}"));
		preferencesGUI.addDefault(path + "material", "dragon_egg");
		preferencesGUI.addDefault(path + "keep-open", true);
		preferencesGUI.addDefault(path + "glowing", true);
		preferencesGUI.addDefault(path + "commands", Arrays.asList("p: bossbar", "gui refresh preferences-{player}"));
		preferencesGUI.addDefault(path + "x", 7);
		preferencesGUI.addDefault(path + "y", 2);
		path = "icons.actionbar.";
		preferencesGUI.addDefault(path + "display-names.english", "&6&lActionbar");
		preferencesGUI.addDefault(path + "display-names.italian", "&6&lActionbar");
		preferencesGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to toggle", "&7the actionbar's visibility.", "", "&7Visibility: {actionbar_visibility}"));
		preferencesGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per cambiare", "&7la visibilità dell'actionbar.", "", "&7Visibilità: {actionbar_visibility}"));
		preferencesGUI.addDefault(path + "material", VersionUtils.getVersion().isAtLeast(Version.V1_14) ? "oak_sign" : "sign");
		preferencesGUI.addDefault(path + "keep-open", true);
		preferencesGUI.addDefault(path + "glowing", true);
		preferencesGUI.addDefault(path + "commands", Arrays.asList("p: actionbar", "gui refresh preferences-{player}"));
		preferencesGUI.addDefault(path + "x", 8);
		preferencesGUI.addDefault(path + "y", 2);
		
		preferencesGUI.save();
	}
	
	public void addPlayerPunishmentsGUIDefaults(boolean forceAdd) throws IOException {
		Configuration playerPunishmentsGUI = configurations.get(ConfigurationType.PLAYER_PUNISHMENTS_GUI);
		
		if (!Files.exists(playerPunishmentsGUI.getPath()))
			playerPunishmentsGUI.createFile();
		else if (!forceAdd)
			return;
		boolean isAtLeastV1_13 = VersionUtils.getVersion().isAtLeast(Version.V1_13);
		
		playerPunishmentsGUI.addDefault("settings.rows", 5);
		playerPunishmentsGUI.addDefault("settings.titles.english", "&c&lPlayer punishments &8(Page {current_page}/{max_page})");
		playerPunishmentsGUI.addDefault("settings.titles.italian", "&c&lPunizioni giocatore &8(Pagina {current_page}/{max_page})");
		playerPunishmentsGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &c&lPlayer punishments &aGUI.");
		playerPunishmentsGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &c&lPunizioni giocatore &ain corso.");
		playerPunishmentsGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		playerPunishmentsGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		playerPunishmentsGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		playerPunishmentsGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		playerPunishmentsGUI.addDefault("settings.click-sound.volume", 1F);
		playerPunishmentsGUI.addDefault("settings.click-sound.pitch", 1F);
		
		playerPunishmentsGUI.addDefault("settings.filling-function.start-slot", 9);
		playerPunishmentsGUI.addDefault("settings.filling-function.end-slot", 35);
		
		playerPunishmentsGUI.addDefault("settings.filling-function.empty-list-icon.display-names.english", "&9&lNo punishments");
		playerPunishmentsGUI.addDefault("settings.filling-function.empty-list-icon.display-names.italian", "&9&lNessuna punizione");
		playerPunishmentsGUI.addDefault("settings.filling-function.empty-list-icon.lores.english", Arrays.asList("&7There are no active", "&7punishments at the moment."));
		playerPunishmentsGUI.addDefault("settings.filling-function.empty-list-icon.lores.italian", Arrays.asList("&7Non ci sono punizioni", "&7attive al momento."));
		playerPunishmentsGUI.addDefault("settings.filling-function.empty-list-icon.material", VersionUtils.getVersion().isAtLeast(Version.V1_10) ? "structure_void" : "barrier");
		playerPunishmentsGUI.addDefault("settings.filling-function.empty-list-icon.keep-open", true);
		playerPunishmentsGUI.addDefault("settings.filling-function.empty-list-icon.glowing", true);
		playerPunishmentsGUI.addDefault("settings.filling-function.empty-list-icon.x", 5);
		playerPunishmentsGUI.addDefault("settings.filling-function.empty-list-icon.y", 3);
		
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.ban.display-names.english", "&4#&l{id}");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.ban.display-names.italian", "&4#&l{id}");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.ban.lores.english", Arrays.asList("&7Punishment: &4ban", "&7IP address: &f{ip_address}", "&7Staff member: &f{staff_member}", "&7Reason: &f{reason}", "&7Server: &f{server}", "&7Date: &f{date}", "&7Duration: &f{duration}", "&7Remaining time: &f{remaining_time}", "&7Type: &f{type}", "&7Active: &f{active}", "&7Global: &f{global}", "&7Silent: &f{silent}"));
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.ban.lores.italian", Arrays.asList("&7Punizione: &4ban", "&7Indirizzo IP: &f{ip_address}", "&7Membro dello Staff: &f{staff_member}", "&7Motivo: &f{reason}", "&7Server: &f{server}", "&7Data: &f{date}", "&7Durata: &f{duration}", "&7Tempo rimanente: &f{remaining_time}", "&7Tipo: &f{type}", "&7Attivo: &f{active}", "&7Globale: &f{global}", "&7Silenzioso: &f{silent}"));
		
		if (!isAtLeastV1_13) {
			playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.ban.material", "stained_hardened_clay");
			playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.ban.damage", 14);
		} else playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.ban.material", "red_terracotta");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.ban.commands", Arrays.asList("p: baninfo {id}"));
		
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.warning.display-names.english", "&c#&l{id}");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.warning.display-names.italian", "&c#&l{id}");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.warning.lores.english", Arrays.asList("&7Punishment: &cwarning", "&7Staff member: &f{staff_member}", "&7Last reason: &f{reason}", "&7Server: &f{server}", "&7Date: &f{date}", "&7Duration: &f{duration}", "&7Remaining time: &f{remaining_time}", "&7Warnings: &f{amount}/{max_amount}", "&7Active: &f{active}", "&7Global: &f{global}", "&7Silent: &f{silent}"));
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.warning.lores.italian", Arrays.asList("&7Punizione: &cavviso", "&7Membro dello Staff: &f{staff_member}", "&7Ultimo motivo: &f{reason}", "&7Server: &f{server}", "&7Data: &f{date}", "&7Durata: &f{duration}", "&7Tempo rimanente: &f{remaining_time}", "&7Avvisi: &f{amount}/{max_amount}", "&7Attivo: &f{active}", "&7Globale: &f{global}", "&7Silenzioso: &f{silent}"));
		
		if (!isAtLeastV1_13) {
			playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.warning.material", "hardened_clay");
		} else playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.warning.material", "terracotta");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.warning.commands", Arrays.asList("p: warninginfo {id}"));
		
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.kick.display-names.english", "&6#&l{id}");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.kick.display-names.italian", "&6#&l{id}");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.kick.lores.english", Arrays.asList("&7Punishment: &6kick", "&7IP address: &f{ip_address}", "&7Staff member: &f{staff_member}", "&7Reason: &f{reason}", "&7Server: &f{server}", "&7Date: &f{date}", "&7Type: &f{type}", "&7Silent: &f{silent}"));
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.kick.lores.italian", Arrays.asList("&7Punizione: &6disconnessione", "&7Indirizzo IP: &f{ip_address}", "&7Membro dello Staff: &f{staff_member}", "&7Motivo: &f{reason}", "&7Server: &f{server}", "&7Data: &f{date}", "&7Tipo: &f{type}", "&7Silenzioso: &f{silent}"));
		
		if (!isAtLeastV1_13) {
			playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.kick.material", "stained_hardened_clay");
			playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.kick.damage", 1);
		} else playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.kick.material", "orange_terracotta");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.kick.commands", Arrays.asList("p: kickinfo {id}"));
		
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.mute.display-names.english", "&e#&l{id}");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.mute.display-names.italian", "&e#&l{id}");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.mute.lores.english", Arrays.asList("&7Punishment: &emute", "&7Staff member: &f{staff_member}", "&7Reason: &f{reason}", "&7Server: &f{server}", "&7Date: &f{date}", "&7Duration: &f{duration}", "&7Remaining time: &f{remaining_time}", "&7Active: &f{active}", "&7Global: &f{global}", "&7Silent: &f{silent}"));
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.mute.lores.italian", Arrays.asList("&7Punizione: &emute", "&7Membro dello Staff: &f{staff_member}", "&7Motivo: &f{reason}", "&7Server: &f{server}", "&7Data: &f{date}", "&7Durata: &f{duration}", "&7Tempo rimanente: &f{remaining_time}", "&7Attivo: &f{active}", "&7Globale: &f{global}", "&7Silenzioso: &f{silent}"));
		
		if (!isAtLeastV1_13) {
			playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.mute.material", "stained_hardened_clay");
			playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.mute.damage", 4);
		} else playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.mute.material", "yellow_terracotta");
		playerPunishmentsGUI.addDefault("settings.filling-function.icon-layouts.mute.commands", Arrays.asList("p: muteinfo {id}"));
		
		path = "icons.info.";
		playerPunishmentsGUI.addDefault(path + "display-names.english", "&c&lPlayer punishments");
		playerPunishmentsGUI.addDefault(path + "display-names.italian", "&c&lPunizioni giocatore");
		playerPunishmentsGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI allows you to view", "&7a player's punishments' list."));
		playerPunishmentsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI ti consente di visualizzare", "&7la lista delle punizioni di un giocatore."));
		playerPunishmentsGUI.addDefault(path + "material", "paper");
		playerPunishmentsGUI.addDefault(path + "keep-open", true);
		playerPunishmentsGUI.addDefault(path + "x", 5);
		playerPunishmentsGUI.addDefault(path + "y", 1);
		path = "icons.previous-page.";
		playerPunishmentsGUI.addDefault(path + "display-names.english", "&e&lPrevious page");
		playerPunishmentsGUI.addDefault(path + "display-names.italian", "&e&lPagina precedente");
		playerPunishmentsGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the previous", "&7page of the GUI."));
		playerPunishmentsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7precedente della GUI."));
		playerPunishmentsGUI.addDefault(path + "material", "arrow");
		playerPunishmentsGUI.addDefault(path + "keep-open", true);
		playerPunishmentsGUI.addDefault(path + "commands", Arrays.asList("gui open player-punishments-{player} {viewer} {previous_page}"));
		playerPunishmentsGUI.addDefault(path + "x", 4);
		playerPunishmentsGUI.addDefault(path + "y", 5);
		path = "icons.next-page.";
		playerPunishmentsGUI.addDefault(path + "display-names.english", "&e&lNext page");
		playerPunishmentsGUI.addDefault(path + "display-names.italian", "&e&lPagina successiva");
		playerPunishmentsGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the next", "&7page of the GUI."));
		playerPunishmentsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7successiva della GUI."));
		playerPunishmentsGUI.addDefault(path + "material", "arrow");
		playerPunishmentsGUI.addDefault(path + "keep-open", true);
		playerPunishmentsGUI.addDefault(path + "commands", Arrays.asList("gui open player-punishments-{player} {viewer} {next_page}"));
		playerPunishmentsGUI.addDefault(path + "x", 6);
		playerPunishmentsGUI.addDefault(path + "y", 5);
		path = "icons.refresh.";
		playerPunishmentsGUI.addDefault(path + "display-names.english", "&e&lRefresh");
		playerPunishmentsGUI.addDefault(path + "display-names.italian", "&e&lAggiorna");
		playerPunishmentsGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to refresh the", "&7GUI's variables and values."));
		playerPunishmentsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per aggiornare", "&7le variabili ed i valori della GUI."));
		playerPunishmentsGUI.addDefault(path + "material", "clock");
		playerPunishmentsGUI.addDefault(path + "keep-open", true);
		playerPunishmentsGUI.addDefault(path + "commands", Arrays.asList("gui refresh player-punishments-{player}"));
		playerPunishmentsGUI.addDefault(path + "x", 5);
		playerPunishmentsGUI.addDefault(path + "y", 5);
		
		playerPunishmentsGUI.save();
	}
	
	public void addPlayerViolationsGUIDefaults(boolean forceAdd) throws IOException {
		Configuration playerViolationsGUI = configurations.get(ConfigurationType.PLAYER_VIOLATIONS_GUI);
		
		if (!Files.exists(playerViolationsGUI.getPath()))
			playerViolationsGUI.createFile();
		else if (!forceAdd)
			return;
		
		playerViolationsGUI.addDefault("settings.rows", 5);
		playerViolationsGUI.addDefault("settings.titles.english", "&3&lPlayer violations &8(Page {current_page}/{max_page})");
		playerViolationsGUI.addDefault("settings.titles.italian", "&3&lViolazioni giocatore &8(Pagina {current_page}/{max_page})");
		playerViolationsGUI.addDefault("settings.open-actions.send-messages.english", "{pfx} &aOpening &3&lPlayer violations &aGUI.");
		playerViolationsGUI.addDefault("settings.open-actions.send-messages.italian", "{pfx} &aApertura GUI &3&lViolazioni giocatore &ain corso.");
		playerViolationsGUI.addDefault("settings.open-actions.play-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "block.chest.open" : "random.chestopen");
		playerViolationsGUI.addDefault("settings.open-actions.play-sound.volume", 1F);
		playerViolationsGUI.addDefault("settings.open-actions.play-sound.pitch", 1F);
		playerViolationsGUI.addDefault("settings.click-sound.id", VersionUtils.getVersion().isAtLeast(Version.V1_9) ? "ui.button.click" : "random.click");
		playerViolationsGUI.addDefault("settings.click-sound.volume", 1F);
		playerViolationsGUI.addDefault("settings.click-sound.pitch", 1F);
		
		playerViolationsGUI.addDefault("settings.filling-function.start-slot", 9);
		playerViolationsGUI.addDefault("settings.filling-function.end-slot", 35);
		
		playerViolationsGUI.addDefault("settings.filling-function.empty-list-icon.display-names.english", "&9&lNo violations");
		playerViolationsGUI.addDefault("settings.filling-function.empty-list-icon.display-names.italian", "&9&lNessuna violazione");
		playerViolationsGUI.addDefault("settings.filling-function.empty-list-icon.lores.english", Arrays.asList("&7There are no recent", "&7violations at the moment."));
		playerViolationsGUI.addDefault("settings.filling-function.empty-list-icon.lores.italian", Arrays.asList("&7Non ci sono violazioni", "&7recenti al momento."));
		playerViolationsGUI.addDefault("settings.filling-function.empty-list-icon.material", VersionUtils.getVersion().isAtLeast(Version.V1_10) ? "structure_void" : "barrier");
		playerViolationsGUI.addDefault("settings.filling-function.empty-list-icon.keep-open", true);
		playerViolationsGUI.addDefault("settings.filling-function.empty-list-icon.glowing", true);
		playerViolationsGUI.addDefault("settings.filling-function.empty-list-icon.x", 5);
		playerViolationsGUI.addDefault("settings.filling-function.empty-list-icon.y", 3);
		
		playerViolationsGUI.addDefault("settings.filling-function.icon-layouts.violation.display-names.english", "&c&l{cheat_display_name}");
		playerViolationsGUI.addDefault("settings.filling-function.icon-layouts.violation.display-names.italian", "&c&l{cheat_display_name}");
		playerViolationsGUI.addDefault("settings.filling-function.icon-layouts.violation.lores.english", Arrays.asList("&7Anticheat: &f{anticheat}", "&7Component: &f{component}", "&7Violations: &f{amount}x", "&7Ping: &f{ping_format} ms&7, TPS: &f{tps}", "&7Last time: &f{last_time} ago", "", "&e&oClick to teleport you there!"));
		playerViolationsGUI.addDefault("settings.filling-function.icon-layouts.violation.lores.italian", Arrays.asList("&7Anticheat: &f{anticheat}", "&7Componente: &f{component}", "&7Violazioni: &f{amount}x", "&7Ping: &f{ping_format} ms&7, TPS: &f{tps}", "&7Ultima volta: &f{last_time} fa", "", "&e&oClicca per teletrasportarti!"));
		playerViolationsGUI.addDefault("settings.filling-function.icon-layouts.violation.material", "air");
		playerViolationsGUI.addDefault("settings.filling-function.icon-layouts.violation.amount", "{amount}");
		playerViolationsGUI.addDefault("settings.filling-function.icon-layouts.violation.commands", Arrays.asList("p: silentteleport player {cheater}"));
		playerViolationsGUI.addDefault("settings.filling-function.icon-layouts.violation.item-flags", Arrays.asList("HIDE_ATTRIBUTES"));
		
		path = "icons.info.";
		playerViolationsGUI.addDefault(path + "display-names.english", "&3&lViolations");
		playerViolationsGUI.addDefault(path + "display-names.italian", "&3&lViolazioni");
		playerViolationsGUI.addDefault(path + "lores.english", Arrays.asList("&7This GUI lists the", "&7anticheat's violations."));
		playerViolationsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Questa GUI elenca le", "&7violazioni dell'anticheat."));
		playerViolationsGUI.addDefault(path + "material", "paper");
		playerViolationsGUI.addDefault(path + "keep-open", true);
		playerViolationsGUI.addDefault(path + "x", 5);
		playerViolationsGUI.addDefault(path + "y", 1);
		path = "icons.previous-page.";
		playerViolationsGUI.addDefault(path + "display-names.english", "&e&lPrevious page");
		playerViolationsGUI.addDefault(path + "display-names.italian", "&e&lPagina precedente");
		playerViolationsGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the previous", "&7page of the GUI."));
		playerViolationsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7precedente della GUI."));
		playerViolationsGUI.addDefault(path + "material", "arrow");
		playerViolationsGUI.addDefault(path + "keep-open", true);
		playerViolationsGUI.addDefault(path + "commands", Arrays.asList("gui open player-violations-{player} {viewer} {previous_page}"));
		playerViolationsGUI.addDefault(path + "x", 4);
		playerViolationsGUI.addDefault(path + "y", 5);
		path = "icons.next-page.";
		playerViolationsGUI.addDefault(path + "display-names.english", "&e&lNext page");
		playerViolationsGUI.addDefault(path + "display-names.italian", "&e&lPagina successiva");
		playerViolationsGUI.addDefault(path + "lores.english", Arrays.asList("&7Go to the next", "&7page of the GUI."));
		playerViolationsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Vai alla pagina", "&7successiva della GUI."));
		playerViolationsGUI.addDefault(path + "material", "arrow");
		playerViolationsGUI.addDefault(path + "keep-open", true);
		playerViolationsGUI.addDefault(path + "commands", Arrays.asList("gui open player-violations-{player} {viewer} {next_page}"));
		playerViolationsGUI.addDefault(path + "x", 6);
		playerViolationsGUI.addDefault(path + "y", 5);
		path = "icons.refresh.";
		playerViolationsGUI.addDefault(path + "display-names.english", "&e&lRefresh");
		playerViolationsGUI.addDefault(path + "display-names.italian", "&e&lAggiorna");
		playerViolationsGUI.addDefault(path + "lores.english", Arrays.asList("&7Click this icon to refresh the", "&7GUI's variables and values."));
		playerViolationsGUI.addDefault(path + "lores.italian", Arrays.asList("&7Clicca questa icona per aggiornare", "&7le variabili ed i valori della GUI."));
		playerViolationsGUI.addDefault(path + "material", "clock");
		playerViolationsGUI.addDefault(path + "keep-open", true);
		playerViolationsGUI.addDefault(path + "commands", Arrays.asList("gui refresh player-violations-{player}"));
		playerViolationsGUI.addDefault(path + "x", 5);
		playerViolationsGUI.addDefault(path + "y", 5);
		
		playerViolationsGUI.save();
	}
	
	public void addViolationsIconsDefaults(boolean forceAdd) throws IOException {
		Configuration violationsIcons = configurations.get(ConfigurationType.VIOLATIONS_ICONS);
		
		if (!Files.exists(violationsIcons.getPath()))
			violationsIcons.createFile();
		else if (!forceAdd)
			return;
		boolean isAtLeastV1_13 = VersionUtils.getVersion().isAtLeast(Version.V1_13);
		boolean isAtLeastV1_9 = VersionUtils.getVersion().isAtLeast(Version.V1_9);
		
		path = "matrix.";
		violationsIcons.addDefault(path + "killaura.name", "Combat hacks");
		violationsIcons.addDefault(path + "killaura.material", "iron_sword");
		violationsIcons.addDefault(path + "click.name", "Click hacks");
		violationsIcons.addDefault(path + "click.material", "comparator");
		violationsIcons.addDefault(path + "hitbox.name", "Reach hacks");
		violationsIcons.addDefault(path + "hitbox.material", "fishing_rod");
		violationsIcons.addDefault(path + "move.name", "Movement hacks");
		violationsIcons.addDefault(path + "move.material", "golden_boots");
		violationsIcons.addDefault(path + "badpackets.name", "Bad packets hacks");
		violationsIcons.addDefault(path + "badpackets.material", "tnt_minecart");
		violationsIcons.addDefault(path + "delay.name", "Delay hacks");
		violationsIcons.addDefault(path + "delay.material", "clock");
		violationsIcons.addDefault(path + "block.name", "Fast blocks hacks");
		violationsIcons.addDefault(path + "block.material", "diamond_pickaxe");
		violationsIcons.addDefault(path + "scaffold.name", "Scaffold hacks");
		violationsIcons.addDefault(path + "scaffold.material", "ladder");
		violationsIcons.addDefault(path + "velocity.name", "Velocity hacks");
		violationsIcons.addDefault(path + "velocity.material", isAtLeastV1_13 ? "firework_rocket" : "fireworks");
		violationsIcons.addDefault(path + "chat.name", "Spamming");
		violationsIcons.addDefault(path + "chat.material", "writable_book");
		violationsIcons.addDefault(path + "interact.name", "Interact hacks");
		violationsIcons.addDefault(path + "interact.material", "crafting_table");
		violationsIcons.addDefault(path + "phase.name", "Phase");
		violationsIcons.addDefault(path + "phase.material", "bedrock");
		violationsIcons.addDefault(path + "autobot.name", "Auto bot hacks");
		
		if (!isAtLeastV1_13) {
			violationsIcons.addDefault(path + "autobot.material", "skull");
			violationsIcons.addDefault(path + "autobot.damage", 3);
		} else violationsIcons.addDefault(path + "autobot.material", "player_head");
		violationsIcons.addDefault(path + "autobot.skull-owner", "zGhostTeo");
		violationsIcons.addDefault(path + "elytra.name", "Elytra hacks");
		violationsIcons.addDefault(path + "elytra.material", isAtLeastV1_9 ? "elytra" : "feather");
		violationsIcons.addDefault(path + "vehicle.name", "Vehicle hacks");
		violationsIcons.addDefault(path + "vehicle.material", "minecart");
		path = "vulcan.";
		violationsIcons.addDefault(path + "aim.name", "Aim");
		violationsIcons.addDefault(path + "aim.material", "arrow");
		violationsIcons.addDefault(path + "autoblock.name", "Auto block");
		violationsIcons.addDefault(path + "autoblock.material", "iron_chestplate");
		violationsIcons.addDefault(path + "autoclicker.name", "Auto clicker");
		violationsIcons.addDefault(path + "autoclicker.material", "comparator");
		violationsIcons.addDefault(path + "fastbow.name", "Fast bow");
		violationsIcons.addDefault(path + "fastbow.material", "bow");
		violationsIcons.addDefault(path + "hitbox.name", "Hitbox");
		violationsIcons.addDefault(path + "hitbox.material", "glass");
		violationsIcons.addDefault(path + "killaura.name", "Killaura");
		violationsIcons.addDefault(path + "killaura.material", "diamond_sword");
		violationsIcons.addDefault(path + "reach.name", "Reach");
		violationsIcons.addDefault(path + "reach.material", "fishing_rod");
		violationsIcons.addDefault(path + "velocity.name", "Velocity");
		violationsIcons.addDefault(path + "velocity.material", isAtLeastV1_13 ? "firework_rocket" : "fireworks");
		violationsIcons.addDefault(path + "criticals.name", "Criticals");
		violationsIcons.addDefault(path + "criticals.material", "iron_sword");
		violationsIcons.addDefault(path + "boatfly.name", "Boat fly");
		violationsIcons.addDefault(path + "boatfly.material", isAtLeastV1_13 ? "oak_boat" : "boat");
		violationsIcons.addDefault(path + "antilevitation.name", "Anti levitation");
		
		if (!isAtLeastV1_13) {
			violationsIcons.addDefault(path + "antilevitation.material", "dye");
			violationsIcons.addDefault(path + "antilevitation.damage", 15);
		} else violationsIcons.addDefault(path + "antilevitation.material", "bone_meal");
		violationsIcons.addDefault(path + "nosaddle.name", "No saddle");
		violationsIcons.addDefault(path + "nosaddle.material", "saddle");
		violationsIcons.addDefault(path + "entityspeed.name", "Entity speed");
		violationsIcons.addDefault(path + "entityspeed.material", "minecart");
		violationsIcons.addDefault(path + "entityflight.name", "Entity flight");
		violationsIcons.addDefault(path + "entityflight.material", "minecart");
		violationsIcons.addDefault(path + "elytra.name", "Elytra");
		violationsIcons.addDefault(path + "elytra.material", isAtLeastV1_9 ? "elytra" : "feather");
		violationsIcons.addDefault(path + "fastclimb.name", "Fast climb");
		violationsIcons.addDefault(path + "fastclimb.material", "ladder");
		violationsIcons.addDefault(path + "flight.name", "Flight");
		violationsIcons.addDefault(path + "flight.material", "feather");
		violationsIcons.addDefault(path + "jesus.name", "Jesus");
		violationsIcons.addDefault(path + "jesus.material", "water_bucket");
		violationsIcons.addDefault(path + "jump.name", "Jump");
		violationsIcons.addDefault(path + "jump.material", "slime_ball");
		violationsIcons.addDefault(path + "motion.name", "Motion");
		violationsIcons.addDefault(path + "motion.material", "golden_boots");
		violationsIcons.addDefault(path + "noslow.name", "No slow");
		violationsIcons.addDefault(path + "noslow.material", isAtLeastV1_13 ? "cobweb" : "web");
		violationsIcons.addDefault(path + "speed.name", "Speed");
		violationsIcons.addDefault(path + "speed.material", "packed_ice");
		violationsIcons.addDefault(path + "step.name", "Step");
		violationsIcons.addDefault(path + "step.material", "leather_boots");
		violationsIcons.addDefault(path + "sprint.name", "Sprint");
		violationsIcons.addDefault(path + "sprint.material", "chainmail_boots");
		violationsIcons.addDefault(path + "strafe.name", "Strafe");
		violationsIcons.addDefault(path + "strafe.material", "iron_boots");
		violationsIcons.addDefault(path + "wallclimb.name", "Wall climb");
		violationsIcons.addDefault(path + "wallclimb.material", "ladder");
		violationsIcons.addDefault(path + "vclip.name", "Vertical clip");
		violationsIcons.addDefault(path + "vclip.material", "feather");
		violationsIcons.addDefault(path + "ghosthand.name", "Ghost hand");
		
		if (!isAtLeastV1_13) {
			violationsIcons.addDefault(path + "ghosthand.material", "bed");
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_12))
				violationsIcons.addDefault(path + "ghosthand.damage", 14);
		} else violationsIcons.addDefault(path + "ghosthand.material", "red_bed");
		violationsIcons.addDefault(path + "baritone.name", "Baritone");
		
		if (!isAtLeastV1_13) {
			violationsIcons.addDefault(path + "baritone.material", "skull");
			violationsIcons.addDefault(path + "baritone.damage", 3);
		} else violationsIcons.addDefault(path + "baritone.material", "player_head");
		violationsIcons.addDefault(path + "baritone.skull-owner", "zGhostTeo");
		violationsIcons.addDefault(path + "badpackets.name", "Bad packets");
		violationsIcons.addDefault(path + "badpackets.material", "tnt_minecart");
		violationsIcons.addDefault(path + "fastplace.name", "Fast place");
		violationsIcons.addDefault(path + "fastplace.material", isAtLeastV1_13 ? "grass_block" : "grass");
		violationsIcons.addDefault(path + "fastbreak.name", "Fast break");
		violationsIcons.addDefault(path + "fastbreak.material", "diamond_pickaxe");
		violationsIcons.addDefault(path + "groundspoof.name", "Ground spoof");
		violationsIcons.addDefault(path + "groundspoof.material", "diamond_boots");
		violationsIcons.addDefault(path + "improbable.name", "Improbable");
		
		if (!isAtLeastV1_13) {
			violationsIcons.addDefault(path + "improbable.material", "skull");
			violationsIcons.addDefault(path + "improbable.damage", 3);
		} else violationsIcons.addDefault(path + "improbable.material", "player_head");
		violationsIcons.addDefault(path + "improbable.skull-owner", "MHF_Question");
		violationsIcons.addDefault(path + "invalid.name", "Invalid");
		violationsIcons.addDefault(path + "invalid.material", "diamond_boots");
		violationsIcons.addDefault(path + "airplace.name", "Air place");
		violationsIcons.addDefault(path + "airplace.material", "barrier");
		violationsIcons.addDefault(path + "inventory.name", "Inventory");
		violationsIcons.addDefault(path + "inventory.material", "chest");
		violationsIcons.addDefault(path + "scaffold.name", "Scaffold");
		violationsIcons.addDefault(path + "scaffold.material", "ladder");
		violationsIcons.addDefault(path + "timer.name", "Timer");
		violationsIcons.addDefault(path + "timer.material", "clock");
		violationsIcons.addDefault(path + "tower.name", "Tower");
		violationsIcons.addDefault(path + "tower.material", "ladder");
		
		path = "negativity.";
		violationsIcons.addDefault(path + "aimbot.name", "Aim bot");
		violationsIcons.addDefault(path + "aimbot.material", "iron_sword");
		violationsIcons.addDefault(path + "airjump.name", "Air jump");
		violationsIcons.addDefault(path + "airjump.material", "feather");
		violationsIcons.addDefault(path + "airplace.name", "Air place");
		violationsIcons.addDefault(path + "airplace.material", "barrier");
		violationsIcons.addDefault(path + "antiknockback.name", "Anti knockback");
		violationsIcons.addDefault(path + "antiknockback.material", isAtLeastV1_9 ? "shield" : "iron_chestplate");
		violationsIcons.addDefault(path + "antipotion.name", "Anti potion");
		violationsIcons.addDefault(path + "antipotion.material", "potion");
		violationsIcons.addDefault(path + "autoclick.name", "Auto click");
		violationsIcons.addDefault(path + "autoclick.material", "comparator");
		violationsIcons.addDefault(path + "autosteal.name", "Auto steal");
		violationsIcons.addDefault(path + "autosteal.material", "chest");
		violationsIcons.addDefault(path + "chat.name", "Spamming");
		violationsIcons.addDefault(path + "chat.material", "writable_book");
		violationsIcons.addDefault(path + "critical.name", "Critical");
		violationsIcons.addDefault(path + "critical.material", "iron_sword");
		violationsIcons.addDefault(path + "elytrafly.name", "Elytra hacks");
		violationsIcons.addDefault(path + "elytrafly.material", isAtLeastV1_9 ? "elytra" : "feather");
		violationsIcons.addDefault(path + "fastbow.name", "Fast bow");
		violationsIcons.addDefault(path + "fastbow.material", "bow");
		violationsIcons.addDefault(path + "fasteat.name", "Fast eat");
		violationsIcons.addDefault(path + "fasteat.material", "cooked_beef");
		violationsIcons.addDefault(path + "fastladder.name", "Fast ladder");
		violationsIcons.addDefault(path + "fastladder.material", "ladder");
		violationsIcons.addDefault(path + "fastplace.name", "Fast place");
		violationsIcons.addDefault(path + "fastplace.material", isAtLeastV1_13 ? "grass_block" : "grass");
		violationsIcons.addDefault(path + "faststairs.name", "Fast stairs");
		violationsIcons.addDefault(path + "faststairs.material", "acacia_stairs");
		violationsIcons.addDefault(path + "fly.name", "Fly");
		violationsIcons.addDefault(path + "fly.material", "feather");
		violationsIcons.addDefault(path + "forcefield.name", "Force field");
		violationsIcons.addDefault(path + "forcefield.material", "ender_pearl");
		violationsIcons.addDefault(path + "groundspoof.name", "Ground spoof");
		violationsIcons.addDefault(path + "groundspoof.material", "diamond_boots");
		violationsIcons.addDefault(path + "incorrectpacket.name", "Incorrect packet");
		violationsIcons.addDefault(path + "incorrectpacket.material", "tnt_minecart");
		violationsIcons.addDefault(path + "inventorymove.name", "Inventory move");
		violationsIcons.addDefault(path + "inventorymove.material", "chest");
		violationsIcons.addDefault(path + "jesus.name", "Jesus");
		violationsIcons.addDefault(path + "jesus.material", "water_bucket");
		violationsIcons.addDefault(path + "nofall.name", "No fall");
		violationsIcons.addDefault(path + "nofall.material", "diamond_boots");
		violationsIcons.addDefault(path + "nopitchlimit.name", "No pitch limit");
		violationsIcons.addDefault(path + "nopitchlimit.material", "ender_eye");
		violationsIcons.addDefault(path + "noslowdown.name", "No slow down");
		violationsIcons.addDefault(path + "noslowdown.material", isAtLeastV1_13 ? "cobweb" : "web");
		violationsIcons.addDefault(path + "noweb.name", "No web");
		violationsIcons.addDefault(path + "noweb.material", isAtLeastV1_13 ? "cobweb" : "web");
		violationsIcons.addDefault(path + "nuker.name", "Nuker");
		violationsIcons.addDefault(path + "nuker.material", "tnt");
		violationsIcons.addDefault(path + "motion.name", "Motion");
		violationsIcons.addDefault(path + "motion.material", "golden_boots");
		violationsIcons.addDefault(path + "pingspoof.name", "Ping spoof");
		violationsIcons.addDefault(path + "pingspoof.material", "ender_pearl");
		violationsIcons.addDefault(path + "phase.name", "Phase");
		violationsIcons.addDefault(path + "phase.material", "bedrock");
		violationsIcons.addDefault(path + "reach.name", "Reach");
		violationsIcons.addDefault(path + "reach.material", "fishing_rod");
		violationsIcons.addDefault(path + "regen.name", "Regen");
		violationsIcons.addDefault(path + "regen.material", "golden_apple");
		violationsIcons.addDefault(path + "scaffold.name", "Scaffold");
		violationsIcons.addDefault(path + "scaffold.material", "ladder");
		violationsIcons.addDefault(path + "sneak.name", "Sneak");
		violationsIcons.addDefault(path + "sneak.material", "chainmail_boots");
		violationsIcons.addDefault(path + "speed.name", "Speed");
		violationsIcons.addDefault(path + "speed.material", "packed_ice");
		violationsIcons.addDefault(path + "strafe.name", "Strafe");
		violationsIcons.addDefault(path + "strafe.material", "iron_boots");
		violationsIcons.addDefault(path + "spider.name", "Spider");
		violationsIcons.addDefault(path + "spider.material", "ladder");
		violationsIcons.addDefault(path + "step.name", "Step");
		violationsIcons.addDefault(path + "step.material", "leather_boots");
		violationsIcons.addDefault(path + "superknockback.name", "Super knockback");
		violationsIcons.addDefault(path + "superknockback.material", isAtLeastV1_9 ? "shield" : "iron_chestplate");
		violationsIcons.addDefault(path + "timer.name", "Timer");
		violationsIcons.addDefault(path + "timer.material", "clock");
		violationsIcons.addDefault(path + "unexpectedpacket.name", "Unexpected packet");
		violationsIcons.addDefault(path + "unexpectedpacket.material", "tnt_minecart");
		violationsIcons.addDefault(path + "xray.name", "X-ray");
		violationsIcons.addDefault(path + "xray.material", "diamond_ore");
		
		violationsIcons.save();
	}
	
	public void addMoTDDefaults(boolean forceAdd) throws IOException {
		Configuration motd = configurations.get(ConfigurationType.MOTD);
		
		if (!Files.exists(motd.getPath()))
			motd.createFile();
		else if (!forceAdd)
			return;
		
		motd.addDefault("motd.enabled", false);
		motd.addDefault("motd.minimum-supported-version-protocol", 47);
		motd.addDefault("motd.server-socket.address", "127.0.0.1");
		motd.addDefault("motd.server-socket.port", 25599);
		motd.addDefault("motd.max-players.one-more-instead-of-fixed-value", true);
		motd.addDefault("motd.max-players.fixed-value", 500);
		motd.addDefault("motd.header-placeholders.english", "&1Another ChatPlugin server");
		motd.addDefault("motd.header-placeholders.italian", "&1Un altro server con ChatPlugin");
		
		motd.addDefault("motd.unknown-player.icon-url", "https://live.staticflickr.com/65535/53597769749_fef9c6a017_o_d.png");
		motd.addDefault("motd.unknown-player.descriptions.english", Arrays.asList("{header}\n&7This is the default description."));
		motd.addDefault("motd.unknown-player.descriptions.italian", Arrays.asList("{header}\n&7Questa è la descrizione predefinita."));
		motd.addDefault("motd.unknown-player.hovers.enabled", true);
		motd.addDefault("motd.unknown-player.hovers.values.english", Arrays.asList("&eWelcome to our server!\n&f{online_total} &eplayers are currently online."));
		motd.addDefault("motd.unknown-player.hovers.values.italian", Arrays.asList("&eBenvenuti nel nostro server!\n&f{online_total} &egiocatori sono online al momento."));
		motd.addDefault("motd.unknown-player.version-names.enabled", false);
		motd.addDefault("motd.unknown-player.version-names.values.english", Arrays.asList("&7{online_total}&8/&7{online_total_plus_one}"));
		motd.addDefault("motd.unknown-player.version-names.values.italian", Arrays.asList("&7{online_total}&8/&7{online_total_plus_one}"));
		
		motd.addDefault("motd.stored-player.icon-url", "https://live.staticflickr.com/65535/53597769749_fef9c6a017_o_d.png");
		motd.addDefault("motd.stored-player.descriptions.english", Arrays.asList("{header}\n&7Welcome back, &f{player}&7!"));
		motd.addDefault("motd.stored-player.descriptions.italian", Arrays.asList("{header}\n&7Bentornati, &f{player}&7!"));
		motd.addDefault("motd.stored-player.hovers.enabled", true);
		motd.addDefault("motd.stored-player.hovers.values.english", Arrays.asList("&eWelcome back to our server, &f{player}&e!\n&f{online_total} &eplayers are currently online."));
		motd.addDefault("motd.stored-player.hovers.values.italian", Arrays.asList("&eBentornati nel nostro server, &f{player}&e!\n&f{online_total} &egiocatori sono online al momento."));
		motd.addDefault("motd.stored-player.version-names.enabled", false);
		motd.addDefault("motd.stored-player.version-names.values.english", Arrays.asList("&7{online_total}&8/&7{online_total_plus_one}"));
		motd.addDefault("motd.stored-player.version-names.values.italian", Arrays.asList("&7{online_total}&8/&7{online_total_plus_one}"));
		
		motd.addDefault("motd.banned-player.enabled", true);
		motd.addDefault("motd.banned-player.icon-url", "https://live.staticflickr.com/65535/53597769754_7c4f98bed5_o_d.png");
		motd.addDefault("motd.banned-player.descriptions.english", Arrays.asList("{header}\n&4You are still banned for &f{remaining_time}&4."));
		motd.addDefault("motd.banned-player.descriptions.italian", Arrays.asList("{header}\n&4Siete ancora bannati per &f{remaining_time}&4."));
		motd.addDefault("motd.banned-player.hovers.enabled", true);
		motd.addDefault("motd.banned-player.hovers.values.english", Arrays.asList("&eLooks like you're banned, &f{player}&e...\n&f{online_total} &eplayers are currently online."));
		motd.addDefault("motd.banned-player.hovers.values.italian", Arrays.asList("&eSembra che siate bannati, &f{player}&e...\n&f{online_total} &egiocatori sono online al momento."));
		motd.addDefault("motd.banned-player.version-names.enabled", false);
		motd.addDefault("motd.banned-player.version-names.values.english", Arrays.asList("&7{online_total}&8/&7{online_total_plus_one}"));
		motd.addDefault("motd.banned-player.version-names.values.italian", Arrays.asList("&7{online_total}&8/&7{online_total_plus_one}"));
		
		motd.addDefault("motd.outdated-version.enabled", true);
		motd.addDefault("motd.outdated-version.icon-url", "https://live.staticflickr.com/65535/53597769744_0d2ca4b809_o_d.png");
		motd.addDefault("motd.outdated-version.descriptions.english", Arrays.asList("{header}\n&cVersion &f{version} &cis outdated, try with a newer one."));
		motd.addDefault("motd.outdated-version.descriptions.italian", Arrays.asList("{header}\n&cLa versione &f{version} &cè obsoleta, provane un'altra."));
		motd.addDefault("motd.outdated-version.hovers.enabled", true);
		motd.addDefault("motd.outdated-version.hovers.values.english", Arrays.asList("&eVersion &f{version} &eis outdated.\n&f{online_total} &eplayers are currently online."));
		motd.addDefault("motd.outdated-version.hovers.values.italian", Arrays.asList("&eLa versione &f{version} &eè obsoleta.\n&f{online_total} &egiocatori sono online al momento."));
		motd.addDefault("motd.outdated-version.version-names.enabled", false);
		motd.addDefault("motd.outdated-version.version-names.values.english", Arrays.asList("&4Outdated version"));
		motd.addDefault("motd.outdated-version.version-names.values.italian", Arrays.asList("&4Versione obsoleta"));
		
		motd.save();
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
		discordIntegration.addDefault(path + "help.thumbnail", "https://live.staticflickr.com/65535/53596116157_d426b5c1c8_o_d.png");
		discordIntegration.addDefault(path + "help.color", "55FF55");
		discordIntegration.addDefault(path + "info.title.text", "Info and contacts for ChatPlugin");
		discordIntegration.addDefault(path + "info.description", "**Website:** https://remigio07.me/chatplugin\n**GitHub:** https://github.com/ChatPlugin/ChatPlugin\n**Discord:** https://discord.gg/eSnAPhvMTG");
		discordIntegration.addDefault(path + "info.thumbnail", "https://live.staticflickr.com/65535/53597447830_f9933947a8_o_d.png");
		discordIntegration.addDefault(path + "info.color", "55FF55");
		discordIntegration.addDefault(path + "reload-start.title.text", "Reload started");
		discordIntegration.addDefault(path + "reload-start.description", "ChatPlugin's Discord bot is reloading...");
		discordIntegration.addDefault(path + "reload-start.thumbnail", "https://live.staticflickr.com/65535/53596116162_6f5a68c410_o_d.png");
		discordIntegration.addDefault(path + "reload-start.color", "55FF55");
		discordIntegration.addDefault(path + "reload-end.title.text", "Reloaded successfully");
		discordIntegration.addDefault(path + "reload-end.description", "The bot has been reloaded. Took **{last_reload_time} ms** to complete.");
		discordIntegration.addDefault(path + "reload-end.thumbnail", "https://live.staticflickr.com/65535/53597447840_45bcbe8901_o_d.png");
		discordIntegration.addDefault(path + "reload-end.color", "55FF55");
		discordIntegration.addDefault(path + "status.title.text", "Current server status");
		discordIntegration.addDefault(path + "status.description", "**OS:** {os_name} {os_version}, **Java:** {java_version}\n**Environment:** {environment} {environment_version}\n**ChatPlugin:** {chatplugin_version}, **JDA:** {jda_version}\n**Uptime:** {uptime}\n**Used memory:** {used_memory}/{max_memory} MB\n**Allocated:** {total_memory} MB, **free:** {free_memory} MB\n**Current threads count:** {active_threads}x\n**Used storage:** {used_storage}/{total_storage} GB\n**Free storage:** {free_storage} GB\n**Enabled players:** {enabled_players}x\n**Startup:** {startup_time} ms, **last reload:** {last_reload_time} ms");
		discordIntegration.addDefault(path + "status.thumbnail", "https://live.staticflickr.com/65535/53597756194_8f7c5a2213_o_d.png");
		discordIntegration.addDefault(path + "status.color", "55FF55");
		discordIntegration.addDefault(path + "version.title.text", "Current plugin version");
		discordIntegration.addDefault(path + "version.description", "**ChatPlugin version:** {chatplugin_version}\n**JDA version:** {jda_version}");
		discordIntegration.addDefault(path + "version.thumbnail", "https://live.staticflickr.com/65535/53596550622_bf7816d18e_o_d.png");
		discordIntegration.addDefault(path + "version.color", "55FF55");
		
		path = "messages.ban.";
		
		discordIntegration.addDefault(path + "info.title.text", "Ban **#{id}**: {player}");
		discordIntegration.addDefault(path + "info.description", "**Staff member:** {staff_member}\n**Who unbanned:** {who_unbanned}\n**Reason:** {reason}\n**Server:** {server}\n**Date:** {date}\n**Duration:** {duration}\n**Remaining time:** {remaining_time}\n**Unban date:** {unban_date}\n**Type:** {type}\n**Active:** {active}\n**Scope:** {global}");
		discordIntegration.addDefault(path + "info.thumbnail", "https://mc-heads.net/avatar/{player}");
		discordIntegration.addDefault(path + "info.color", "AA0000");
		discordIntegration.addDefault(path + "list.title.text", "Banlist");
		discordIntegration.addDefault(path + "list.description", "**Active bans' IDs:** {bans}.");
		discordIntegration.addDefault(path + "list.thumbnail", "https://live.staticflickr.com/65535/53597885450_768654f4cb_o_d.png");
		discordIntegration.addDefault(path + "list.color", "AA0000");
		discordIntegration.addDefault(path + "empty-list.title.text", "Empty list");
		discordIntegration.addDefault(path + "empty-list.description", "There are no active bans.");
		discordIntegration.addDefault(path + "empty-list.thumbnail", "https://live.staticflickr.com/65535/53596550572_decef9c6d2_o_d.png");
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
		discordIntegration.addDefault(path + "list.thumbnail", "https://live.staticflickr.com/65535/53597428861_2f63295099_o_d.png");
		discordIntegration.addDefault(path + "list.color", "FF5555");
		discordIntegration.addDefault(path + "empty-list.title.text", "Empty list");
		discordIntegration.addDefault(path + "empty-list.description", "There are no active warnings.");
		discordIntegration.addDefault(path + "empty-list.thumbnail", "https://live.staticflickr.com/65535/53596550572_decef9c6d2_o_d.png");
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
		discordIntegration.addDefault(path + "list.thumbnail", "https://live.staticflickr.com/65535/53597428856_5bf1c0f710_o_d.png");
		discordIntegration.addDefault(path + "list.color", "FFFF55");
		discordIntegration.addDefault(path + "empty-list.title.text", "Empty list");
		discordIntegration.addDefault(path + "empty-list.description", "There are no active mutes.");
		discordIntegration.addDefault(path + "empty-list.thumbnail", "https://live.staticflickr.com/65535/53596550572_decef9c6d2_o_d.png");
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
		discordIntegration.addDefault(path + "thumbnail", "https://live.staticflickr.com/65535/53597756249_c3908ff7a7_o_d.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.no-permission.";
		
		discordIntegration.addDefault(path + "title.text", "No permission");
		discordIntegration.addDefault(path + "description", "You do not have the permission to execute this command.");
		discordIntegration.addDefault(path + "thumbnail", "https://live.staticflickr.com/65535/53597756249_c3908ff7a7_o_d.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.guild-only-action.";
		
		discordIntegration.addDefault(path + "title.text", "Guild only action");
		discordIntegration.addDefault(path + "description", "This action can only be performed inside of the configured guild.");
		discordIntegration.addDefault(path + "thumbnail", "https://live.staticflickr.com/65535/53597756249_c3908ff7a7_o_d.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.disabled-feature.";
		
		discordIntegration.addDefault(path + "title.text", "Disabled feature");
		discordIntegration.addDefault(path + "description", "That feature is disabled. Set it up in the server's config files.");
		discordIntegration.addDefault(path + "thumbnail", "https://live.staticflickr.com/65535/53597756249_c3908ff7a7_o_d.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.invalid-ip-address.";
		
		discordIntegration.addDefault(path + "title.text", "Invalid IP address");
		discordIntegration.addDefault(path + "description", "The specified IP address is invalid. Try with a different one.");
		discordIntegration.addDefault(path + "thumbnail", "https://live.staticflickr.com/65535/53597756249_c3908ff7a7_o_d.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.at-least-one-online.";
		
		discordIntegration.addDefault(path + "title.text", "No players online");
		discordIntegration.addDefault(path + "description", "At least one player online is required to perform this action.");
		discordIntegration.addDefault(path + "thumbnail", "https://live.staticflickr.com/65535/53597756249_c3908ff7a7_o_d.png");
		discordIntegration.addDefault(path + "color", "FF5555");
		
		path = "messages.ip-lookup.";
		
		discordIntegration.addDefault(path + "title.text", "IP lookup of {ip_address}");
		discordIntegration.addDefault(path + "title.url", "https://www.maxmind.com/en/geoip2-precision-demo");
		discordIntegration.addDefault(path + "description", "**ISP:** {isp}\n**Country:** {country}, {continent}\n**Subdivision(s):** {subdivisions}\n**City:** {city} (postal code: {postal_code})\n**Coords:** {latitude}° {longitude}°\n**Accuracy radius:** ~{accuracy_radius_km} km");
		discordIntegration.addDefault(path + "thumbnail", "https://live.staticflickr.com/65535/53597756269_8e08b3dfb1_o_d.png");
		discordIntegration.addDefault(path + "color", "5555FF");
		
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
