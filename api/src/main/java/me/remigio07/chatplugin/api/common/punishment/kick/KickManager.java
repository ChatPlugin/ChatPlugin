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

package me.remigio07.chatplugin.api.common.punishment.kick;

import java.sql.SQLException;

import me.remigio07.chatplugin.api.common.event.punishment.kick.KickEvent;
import me.remigio07.chatplugin.api.common.player.ChatPluginPlayer;
import me.remigio07.chatplugin.api.common.punishment.PunishmentManager;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.annotation.ServerImplementationOnly;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Manager that handles {@link Kick}s and interacts with the storage.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Punishments#kicks">ChatPlugin wiki/Modules/Punishments/Kicks</a>
 */
public abstract class KickManager extends PunishmentManager {
	
	protected static KickManager instance;
	protected KickType defaultKickMessageType;
	protected String lobbyServerID;
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "kick.enabled" in {@link ConfigurationType#CONFIG}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the default kick message type.
	 * 
	 * <p><strong>Found at:</strong> "kick.default-kick-message-type" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Default kick message type
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public KickType getDefaultKickMessageType() {
		return defaultKickMessageType;
	}
	
	/**
	 * Gets the lobby server's ID.
	 * 
	 * <p><strong>Found at:</strong> "kick.lobby-server-id" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Lobby server's ID
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.SETTINGS_NOT_PRESENT)
	public String getLobbyServerID() {
		return lobbyServerID;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static KickManager getInstance() {
		return instance;
	}
	
	/**
	 * Kicks a player.
	 * 
	 * @param player Player to kick
	 * @param staffMember Who kicked the player
	 * @param reason Kick's reason, nullable
	 * @param server Origin server ({@link ProxyManager#getServerID()})
	 * @param lobbyServer Lobby server ({@link #getLobbyServerID()})
	 * @param type Kick's type ({@link #getDefaultKickMessageType()})
	 * @param silent Whether this kick is silent
	 * @throws IllegalStateException If <code>!{@link Environment#isProxy()} &amp;&amp;
	 * {@link ProxyManager#isEnabled()} &amp;&amp; {@link PlayerAdapter#getOnlinePlayers()}.isEmpty()</code>
	 * @see KickEvent
	 */
	public abstract void kick(
			ChatPluginPlayer player,
			@NotNull String staffMember,
			@Nullable(why = "Reason may not be specified") String reason,
			@NotNull String server,
			@NotNull String lobbyServer,
			KickType type,
			boolean silent
			);
	
	/**
	 * Gets a kick from the database.
	 * 
	 * <p>Will return <code>null</code> if the kick does not exist.</p>
	 * 
	 * <p>This method is equivalent to {@link StorageConnector#getKick(int)}
	 * with the difference that it suppresses {@link SQLException}s.</p>
	 * 
	 * @param id Kick's ID
	 * @return Kick object
	 */
	@Nullable(why = "Specified kick may not exist")
	public abstract Kick getKick(int id);
	
	/**
	 * Formats a kick's type message using the message
	 * set in the specified language's messages file.
	 * 
	 * @param type Type to format
	 * @param language Language used to translate the message
	 * @return Formatted type message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatTypeMessage(KickType type, Language language);
	
	/**
	 * Formats a kick's silent message using the message
	 * set in the specified language's messages file.
	 * 
	 * @param silent Whether the kick is silent
	 * @param language Language used to translate the message
	 * @return Formatted silent message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatSilentMessage(boolean silent, Language language);
	
	/**
	 * Formats a kick's kick message using the message
	 * set in the specified language's messages file.
	 * 
	 * @param type Type to format
	 * @param language Language used to translate the message
	 * @return Formatted kick message
	 */
	@ServerImplementationOnly(why = ServerImplementationOnly.NO_LANGUAGES)
	@NotNull
	public abstract String formatKickMessage(KickType type, Language language);
	
}
