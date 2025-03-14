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

package me.remigio07.chatplugin.api.server.chat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.event.chat.PlayerPingEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Manager that handles player pings in the chat.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#player-ping">ChatPlugin wiki/Modules/Chat/Player ping</a>
 */
public abstract class PlayerPingManager implements ChatPluginManager {
	
	protected static PlayerPingManager instance;
	protected boolean enabled, atSignRequired, soundEnabled, titlesEnabled;
	protected String color;
	protected SoundAdapter sound;
	protected long titlesFadeIn, titlesStay, titlesFadeOut;
	protected Map<Language, String> titles = new HashMap<>();
	protected Map<Language, String> subtitles = new HashMap<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if @ in front of player names is
	 * required to ping them; example: "@Remigio07".
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.at-sign-required" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether @ is required
	 */
	public boolean isAtSignRequired() {
		return atSignRequired;
	}
	
	/**
	 * Checks if players should hear a sound when pinged.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.sound.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether players should hear a sound when pinged
	 */
	public boolean isSoundEnabled() {
		return soundEnabled;
	}
	
	/**
	 * Checks if a title should be displayed to players when pinged.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.titles.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether a title should be displayed to players when pinged
	 */
	public boolean areTitlesEnabled() {
		return titlesEnabled;
	}
	
	/**
	 * Gets the color pings in chat will have.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.color" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Pings' color
	 */
	public String getColor() {
		return color;
	}
	
	/**
	 * Gets the sound that pings will produce.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.sound" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Pings' sound
	 */
	public SoundAdapter getSound() {
		return sound;
	}
	
	/**
	 * Gets the time for the titles
	 * to fade in, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.titles.fade-in-ms" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Titles' time to fade in
	 */
	public long getTitlesFadeIn() {
		return titlesFadeIn;
	}
	
	/**
	 * Gets the time for the titles
	 * to stay, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.titles.stay-ms" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Titles' time to stay
	 */
	public long getTitlesStay() {
		return titlesStay;
	}
	
	/**
	 * Gets the time for the titles
	 * to fade out, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.titles.fade-out-ms" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Titles' time to fade out
	 */
	public long getTitlesFadeOut() {
		return titlesFadeOut;
	}
	
	/**
	 * Gets the map of loaded titles.
	 * 
	 * <p>You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.titles.titles" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Loaded titles' map
	 */
	public Map<Language, String> getTitles() {
		return titles;
	}
	
	/**
	 * Gets the title for the specified language.
	 * 
	 * <p>Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s title if no title is present for the specified language.
	 * Will return <code>null</code> if {@link #getTitles()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.</p>
	 * 
	 * @param language Language used to translate the title
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Loaded title
	 */
	@Nullable(why = "No title may be present for the specified language")
	public String getTitle(Language language, boolean avoidNull) {
		return titles.get(language) == null ? avoidNull ? titles.get(Language.getMainLanguage()) : null : titles.get(language);
	}
	
	/**
	 * Gets the map of loaded subtitles.
	 * 
	 * <p>You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "chat.player-ping.titles.subtitles" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Loaded subtitles' map
	 */
	public Map<Language, String> getSubtitles() {
		return subtitles;
	}
	
	/**
	 * Gets the subtitle for the specified language.
	 * 
	 * <p>Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s subtitle if no subtitle is present for the specified language.
	 * Will return <code>null</code> if {@link #getSubtitles()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.</p>
	 * 
	 * @param language Language used to translate the subtitle
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Loaded subtitle
	 */
	@Nullable(why = "No subtitle may be present for the specified language")
	public String getSubtitle(Language language, boolean avoidNull) {
		return subtitles.get(language) == null ? avoidNull ? subtitles.get(Language.getMainLanguage()) : null : subtitles.get(language);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static PlayerPingManager getInstance() {
		return instance;
	}
	
	/**
	 * Pings every loaded player contained in <code>pingedPlayers</code> and
	 * returns the message with their names colored using {@link #getColor()}.
	 * 
	 * <p>Specify <code>null</code> as <code>channel</code> if
	 * <code>!</code>{@link ChatChannelsManager#isEnabled()}.</p>
	 * 
	 * <p>Will do nothing if the player does not have the permission "chatplugin.player-ping"
	 * or "chatplugin.player-ping.everyone" if they have tried to ping everyone.</p>
	 * 
	 * <p>This method will consider that some players may be ignored by other players.</p>
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @param channel Channel the message has been sent on
	 * @param pingedPlayers Pinged players' list ({@link #getPingedPlayers(ChatPluginServerPlayer, String, ChatChannel)})
	 * @return Message adjusted with color
	 * @see PlayerPingEvent
	 * @see PlayerIgnoreManager#getIgnoredPlayers(OfflinePlayer)
	 */
	public abstract String performPing(
			ChatPluginServerPlayer player,
			String message,
			@Nullable(why = "Null if !ChatChannelsManager#isEnabled()") ChatChannel<? extends ChatChannelData> channel,
			List<ChatPluginServerPlayer> pingedPlayers
			);
	
	/**
	 * Gets the list of every loaded player contained in <code>message</code>.
	 * 
	 * <p>Specify <code>null</code> as <code>channel</code> if
	 * <code>!</code>{@link ChatChannelsManager#isEnabled()}.</p>
	 * 
	 * <p>Will return an empty list if the player does not have the permission "chatplugin.player-ping"
	 * or "chatplugin.player-ping.everyone" if they have tried to ping everyone.</p>
	 * 
	 * <p>This method will <em>not</em> consider that some players may be ignored
	 * by other players but will consider that some players may be vanished.</p>
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @param channel Channel the message has been sent on
	 * @return Pinged players' list
	 */
	public abstract List<ChatPluginServerPlayer> getPingedPlayers(
			ChatPluginServerPlayer player,
			String message,
			@Nullable(why = "Null if !ChatChannelsManager#isEnabled()") ChatChannel<? extends ChatChannelData> channel
			);
	
	/**
	 * Plays {@link #getSound()} to the specified player.
	 * 
	 * <p>This will have no effect if <code>!</code>{@link #isSoundEnabled()}.</p>
	 * 
	 * @param player Player to play the sound to
	 */
	public abstract void playPingSound(ChatPluginServerPlayer player);
	
}
