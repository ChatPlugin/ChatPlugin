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

package me.remigio07.chatplugin.api.common.discord;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Library;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.annotation.SensitiveData;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;

/**
 * Manager that handles the plugin's {@link DiscordBot}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Discord-integration">ChatPlugin wiki/Modules/Discord integration</a>
 */
public abstract class DiscordIntegrationManager implements ChatPluginManager, Runnable {
	
	/**
	 * Array containing all the libraries required for this module to work.
	 * 
	 * <p><strong>Content:</strong> [
	 * {@link Library#JETBRAINS_ANNOTATIONS}, {@link Library#KOTLIN_STDLIB}, {@link Library#OKIO}, {@link Library#OKHTTP}, {@link Library#JDA},
	 * {@link Library#CHECKER_QUAL}, {@link Library#APACHE_COMMONS_COLLECTIONS}, {@link Library#APACHE_COMMONS_MATH}, {@link Library#ERROR_PRONE_ANNOTATIONS}, {@link Library#FAILURE_ACCESS},
	 * {@link Library#GNU_TROVE}, {@link Library#GUAVA}, {@link Library#J2OBJC_ANNOTATIONS}, {@link Library#JACKSON_ANNOTATIONS}, {@link Library#JACKSON_CORE},
	 * {@link Library#JACKSON_DATABIND}, {@link Library#JAVA_NATIVE_ACCESS}, {@link Library#JAVAX_ANNOTATION}, {@link Library#NV_WEBSOCKET_CLIENT}, {@link Library#SLF4J_API},
	 * {@link Library#SLF4J_SIMPLE_PROVIDER}]</p>
	 */
	public static final Library[] LIBRARIES = new Library[] {
			Library.JETBRAINS_ANNOTATIONS, Library.KOTLIN_STDLIB, Library.OKIO, Library.OKHTTP, Library.JDA,
			Library.CHECKER_QUAL, Library.APACHE_COMMONS_COLLECTIONS, Library.APACHE_COMMONS_MATH, Library.ERROR_PRONE_ANNOTATIONS, Library.FAILURE_ACCESS,
			Library.GNU_TROVE, Library.GUAVA, Library.J2OBJC_ANNOTATIONS, Library.JACKSON_ANNOTATIONS, Library.JACKSON_CORE,
			Library.JACKSON_DATABIND, Library.JAVA_NATIVE_ACCESS, Library.JAVAX_ANNOTATION, Library.NV_WEBSOCKET_CLIENT, Library.SLF4J_API,
			Library.SLF4J_SIMPLE_PROVIDER
			};
	protected static DiscordIntegrationManager instance;
	protected boolean enabled;
	protected long guildID, applicationID, punishmentsChannelID, staffNotificationsChannelID, statusUpdateTimeout, statusUpdateTaskID = -1;
	@SensitiveData(warning = "Discord integration's bot's private token")
	protected String token;
	protected String statusValue;
	protected ActivityTypeAdapter statusActivityType;
	protected int lastReloadTime = -1;
	protected DiscordBot bot;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "settings.enabled" in {@link ConfigurationType#DISCORD_INTEGRATION}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the guild's ID.
	 * 
	 * <p><strong>Found at:</strong> "settings.guild-id" in {@link ConfigurationType#DISCORD_INTEGRATION}</p>
	 * 
	 * @return Guild's ID
	 */
	public long getGuildID() {
		return guildID;
	}
	
	/**
	 * Gets the application's ID.
	 * 
	 * <p><strong>Found at:</strong> "settings.application-id" in {@link ConfigurationType#DISCORD_INTEGRATION}</p>
	 * 
	 * @return Application's ID
	 */
	public long getApplicationID() {
		return applicationID;
	}
	
	/**
	 * Gets the bot's token.
	 * 
	 * <p><strong>Found at:</strong> "settings.token" in {@link ConfigurationType#DISCORD_INTEGRATION}</p>
	 * 
	 * @return Bot's token
	 */
	@SensitiveData(warning = "Discord integration's bot's private token")
	public String getToken() {
		return token;
	}
	
	/**
	 * Gets the ID of the channel used to announce punishments.
	 * 
	 * <p><strong>Found at:</strong> "settings.channels-ids.punishments" in {@link ConfigurationType#DISCORD_INTEGRATION}</p>
	 * 
	 * @return Punishments' channel's ID
	 */
	public long getPunishmentsChannelID() {
		return punishmentsChannelID;
	}
	
	/**
	 * Gets the ID of the channel used to announce Staff notifications.
	 * 
	 * <p><strong>Found at:</strong> "settings.channels-ids.staff-notifications" in {@link ConfigurationType#DISCORD_INTEGRATION}</p>
	 * 
	 * @return Staff notifications' channel's ID
	 */
	public long getStaffNotificationsChannelID() {
		return staffNotificationsChannelID;
	}
	
	/**
	 * Gets the bot's status' value.
	 * 
	 * <p><strong>Found at:</strong> "settings.status.value" in {@link ConfigurationType#DISCORD_INTEGRATION}</p>
	 * 
	 * @return Bot's status' value
	 */
	public String getStatusValue() {
		return statusValue;
	}
	
	/**
	 * Gets the bot's status' activity's type.
	 * 
	 * <p><strong>Found at:</strong> "settings.status.activity-type" in {@link ConfigurationType#DISCORD_INTEGRATION}</p>
	 * 
	 * @return Bot's status' activity's type
	 */
	public ActivityTypeAdapter getStatusActivityType() {
		return statusActivityType;
	}
	
	/**
	 * Gets the timeout between status updates, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "settings.status.update-timeout-ms" in {@link ConfigurationType#DISCORD_INTEGRATION}</p>
	 * 
	 * @return Time between status updates
	 */
	public long getStatusUpdateTimeout() {
		return statusUpdateTimeout;
	}
	
	/**
	 * Gets the time elapsed during the bot's last reload, in milliseconds.
	 * 
	 * @return Time elapsed in milliseconds
	 */
	public int getLastReloadTime() {
		return lastReloadTime;
	}
	
	/**
	 * Gets the {@link #run()}'s timer's task's ID.
	 * 
	 * <p>You can interact with it using {@link TaskManager}'s methods.</p>
	 * 
	 * @return Updating task's ID
	 */
	public long getStatusUpdateTaskID() {
		return statusUpdateTaskID;
	}
	
	/**
	 * Gets the bot's instance.
	 * 
	 * @return Bot's instance
	 */
	public DiscordBot getBot() {
		return bot;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static DiscordIntegrationManager getInstance() {
		return instance;
	}
	
	/**
	 * Automatic status updater, called once every {@link #getStatusUpdateTimeout()} ms.
	 */
	@Override
	public abstract void run();
	
	/**
	 * Reloads the bot.
	 * 
	 * <p>You can specify <code>null</code> as <code>whoReloaded</code> to not make
	 * appear in the logs the following message: <code>"User " + whoReloaded
	 * + " has reloaded the Discord integration through the bot."</code></p>
	 * 
	 * <p>You can specify -1 as <code>channelID</code> to not send {@link DiscordMessages.Main#RELOAD_END}.</p>
	 * 
	 * @param whoReloaded Who reloaded the bot
	 * @param channelID Reload command's channel's ID
	 * @return Time elapsed, in milliseconds
	 */
	public abstract int reload(@Nullable(why = "User will not show up in logs if null") String whoReloaded, long channelID);
	
	/**
	 * Gets the <a href="https://github.com/discord-jda/JDA">JDA</a>'s version.
	 * 
	 * @return JDA's version
	 */
	public abstract String getJDAVersion();
	
}
