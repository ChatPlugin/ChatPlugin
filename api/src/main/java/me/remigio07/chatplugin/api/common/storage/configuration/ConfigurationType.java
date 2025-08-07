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

package me.remigio07.chatplugin.api.common.storage.configuration;

import java.io.File;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.discord.DiscordIntegrationManager;
import me.remigio07.chatplugin.api.common.motd.MoTDManager;
import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.punishment.PunishmentManager;
import me.remigio07.chatplugin.api.common.punishment.ban.BanManager;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;
import me.remigio07.chatplugin.api.common.telegram.TelegramIntegrationManager;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.actionbar.ActionbarManager;
import me.remigio07.chatplugin.api.server.ad.AdManager;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatIntegration;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardType;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Represents a configuration's type.
 * 
 * <p>It comprehends the internal and {@link #CUSTOM}
 * configurations, which are handled by the plugin's addons.</p>
 * 
 * @see ConfigurationManager
 */
public enum ConfigurationType {
	
	/**
	 * Main configuration.
	 * 
	 * <p>Common to both proxy and server implementations.</p>
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/config.yml</p>
	 */
	CONFIG,
	
	/**
	 * Default language's messages.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/messages.yml</p>
	 * 
	 * @see LanguageManager
	 */
	MESSAGES,
	
	/**
	 * Ranks' configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/ranks.yml</p>
	 * 
	 * @see RankManager
	 */
	RANKS,
	
	/**
	 * Chat's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/chat.yml</p>
	 * 
	 * @see ChatManager
	 */
	CHAT,
	
	/**
	 * Tablists' configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/tablists.yml</p>
	 * 
	 * @see TablistManager
	 */
	TABLISTS,
	
	/**
	 * Default scoreboard's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/scoreboards/default.yml</p>
	 * 
	 * @see ScoreboardType#DEFAULT
	 * @see ScoreboardManager
	 */
	DEFAULT_SCOREBOARD,
	
	/**
	 * Bossbars' configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/bossbars.yml</p>
	 * 
	 * @see BossbarManager
	 */
	BOSSBARS,
	
	/**
	 * Actionbars' configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/actionbars.yml</p>
	 * 
	 * @see ActionbarManager
	 */
	ACTIONBARS,
	
	/**
	 * Ads' configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/ads.yml</p>
	 * 
	 * @see AdManager
	 */
	ADS,
	
	/**
	 * F3 server names' configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/f3-server-names.yml</p>
	 * 
	 * @see F3ServerNameManager
	 */
	F3_SERVER_NAMES,
	
	/**
	 * Join/quit modules' configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/join-quit-modules.yml</p>
	 * 
	 * @see me.remigio07.chatplugin.api.server.join_quit
	 */
	JOIN_QUIT_MODULES,
	
	/**
	 * Main GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/main.yml</p>
	 * 
	 * @see GUIManager
	 */
	MAIN_GUI,
	
	/**
	 * Languages GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/languages.yml</p>
	 * 
	 * @see GUIManager
	 * @see LanguageManager
	 */
	LANGUAGES_GUI,
	
	/**
	 * Chat color GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/chat-color.yml</p>
	 * 
	 * @see GUIManager
	 * @see ChatPluginServerPlayer#getChatColor()
	 */
	CHAT_COLOR_GUI,
	
	/**
	 * Emojis' tone GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/emojis-tone.yml</p>
	 * 
	 * @see GUIManager
	 * @see ChatPluginServerPlayer#getEmojisTone()
	 */
	EMOJIS_TONE_GUI,
	
	/**
	 * Banlist GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/banlist.yml</p>
	 * 
	 * @see GUIManager
	 * @see BanManager
	 */
	BANLIST_GUI,
	
	/**
	 * Warnlist GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/warnlist.yml</p>
	 * 
	 * @see GUIManager
	 * @see WarningManager
	 */
	WARNLIST_GUI,
	
	/**
	 * Mutelist GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/mutelist.yml</p>
	 * 
	 * @see GUIManager
	 * @see MuteManager
	 */
	MUTELIST_GUI,
	
	/**
	 * Violations GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/violations.yml</p>
	 * 
	 * @see GUIManager
	 * @see AnticheatManager
	 */
	VIOLATIONS_GUI,
	
	/**
	 * Player info GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/player-info.yml</p>
	 * 
	 * @see GUIManager
	 * @see PlayerManager
	 */
	PLAYER_INFO_GUI,
	
	/**
	 * Player punishments GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/player-punishments.yml</p>
	 * 
	 * @see GUIManager
	 * @see PunishmentManager
	 */
	PLAYER_PUNISHMENTS_GUI,
	
	/**
	 * Player violations GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/player-violations.yml</p>
	 * 
	 * @see GUIManager
	 * @see AnticheatManager
	 */
	PLAYER_VIOLATIONS_GUI,
	
	/**
	 * Preferences GUI's configuration.
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/preferences.yml</p>
	 * 
	 * @see GUIManager
	 */
	PREFERENCES_GUI,
	
	/**
	 * Violations icons' configuration.
	 * 
	 * <p>This is a special configuration that contains icons displayed by {@link AnticheatIntegration}s
	 * in the {@link #VIOLATIONS_GUI} and in {@link #PLAYER_VIOLATIONS_GUI}s.</p>
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/guis/violations-icons.yml</p>
	 * 
	 * @see GUIManager
	 * @see AnticheatManager
	 */
	VIOLATIONS_ICONS,
	
	/**
	 * MoTD's configuration.
	 * 
	 * <p>Common to both proxy and server implementations.</p>
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/motd.yml</p>
	 * 
	 * @see MoTDManager
	 */
	MOTD,
	
	/**
	 * Discord integration's configuration.
	 * 
	 * <p>Common to both proxy and server implementations.</p>
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/discord-integration.yml</p>
	 * 
	 * @see DiscordIntegrationManager
	 */
	DISCORD_INTEGRATION,
	
	/**
	 * Telegram integration's configuration.
	 * 
	 * <p>Common to both proxy and server implementations.</p>
	 * 
	 * <p><strong>Path:</strong> ChatPlugin/telegram-integration.yml</p>
	 * 
	 * @see TelegramIntegrationManager
	 */
	TELEGRAM_INTEGRATION,
	
	/**
	 * Represents a custom configuration which may be handled by another plugin.
	 * 
	 * <p>Common to both proxy and server implementations.</p>
	 * 
	 * <p>To interact with custom configurations, refer to {@link ConfigurationManager}.</p>
	 */
	CUSTOM;
	
	/**
	 * Gets this configuration's folder.
	 * 
	 * <p>Will return <code>null</code> for configurations
	 * in the plugin's folder (not inside any sub-folder)
	 * or if <code>this == </code>{@link #CUSTOM}.</p>
	 * 
	 * @return Configuration's folder
	 */
	@Nullable(why = "Configuration may be of type CUSTOM or not contained in any sub-folder")
	public String getFolder() {
		return this == DEFAULT_SCOREBOARD ? "scoreboards" : this == VIOLATIONS_ICONS || name().contains("GUI") ? "guis" : null;
	}
	
	/**
	 * Gets this configuration's file's name.
	 * 
	 * <p><strong>Note:</strong> if <code>this == </code>{@link #CUSTOM} this
	 * method will return "custom.yml", which is not applicable in most cases.</p>
	 * 
	 * @return Configuration's file's name
	 */
	@NotNull
	public String getFileName() {
		return this == DEFAULT_SCOREBOARD ? "default.yml" : name().toLowerCase().replace('_', '-').substring(0, name().endsWith("_GUI") ? name().length() - 4 : name().length()) + ".yml";
	}
	
	/**
	 * Gets this configuration's path.
	 * 
	 * <p><strong>Note:</strong> if <code>this == </code>{@link #CUSTOM}
	 * this method will return a custom.yml file, which is not applicable
	 * in most cases. Also, the corresponding file may not exist (yet).</p>
	 * 
	 * @return Configuration's path
	 */
	@NotNull
	public Path toPath() {
		String folder = getFolder();
		return ChatPlugin.getInstance().getDataFolder().resolve((folder == null ? "" : folder + File.separator) + getFileName());
	}
	
	/**
	 * Gets this configuration from the loaded {@link ConfigurationManager#getConfigurations()}.
	 * 
	 * <p>Will return <code>null</code> if <code>this == </code>{@link #CUSTOM} or if it is not loaded.</p>
	 * 
	 * @return Corresponding {@link Configuration}
	 */
	@Nullable(why = "Null for ConfigurationType#CUSTOM or if not loaded")
	public Configuration get() {
		return ConfigurationManager.getInstance().getConfiguration(this);
	}
	
	/**
	 * Checks if this configuration is available on the proxy implementation of the plugin.
	 * 
	 * <p>The only ones supported on proxies are {@link #CONFIG}, {@link #DISCORD_INTEGRATION},
	 * {@link #TELEGRAM_INTEGRATION} and {@link #CUSTOM} for custom configurations.</p>
	 * 
	 * @return Whether this configuration is available on proxy implementations
	 */
	public boolean isAvailableOnProxy() {
		return this == CONFIG || this == CUSTOM || name().endsWith("_INTEGRATION");
	}
	
	/**
	 * Gets the configuration types available on {@link Environment#getCurrent()}.
	 * 
	 * @return Available configuration types
	 * @see #isAvailableOnProxy()
	 */
	public static Set<ConfigurationType> getConfigurationTypes() {
		return Stream.of(values()).filter(type -> !Environment.isProxy() || type.isAvailableOnProxy()).collect(Collectors.toCollection(() -> EnumSet.noneOf(ConfigurationType.class)));
	}
	
}
