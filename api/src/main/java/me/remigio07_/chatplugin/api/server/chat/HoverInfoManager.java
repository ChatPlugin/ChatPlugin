/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.api.server.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07_.chatplugin.api.common.util.adapter.text.ClickActionAdapter;
import me.remigio07_.chatplugin.api.common.util.adapter.text.TextAdapter;
import me.remigio07_.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.rank.Rank;
import me.remigio07_.chatplugin.api.server.util.GameFeature;
import me.remigio07_.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles hover info in the chat. See wiki for more info:
 * <br><a href="https://github.com/Remigio07/ChatPlugin/wiki/Chat#hover-info">ChatPlugin wiki/Chat/Hover info</a>
 * 
 * @see me.remigio07_.chatplugin.api.server.chat Chat-related managers
 */
@GameFeature(
		name = "hover info",
		availableOnBukkit = true,
		availableOnSponge = true,
		spigotRequired = true,
		minimumBukkitVersion = Version.V1_7_2,
		minimumSpongeVersion = Version.V1_8
		)
public abstract class HoverInfoManager implements ChatPluginManager {
	
	protected static HoverInfoManager instance;
	protected boolean enabled, rankHoverEnabled, playerHoverEnabled, urlHoverEnabled, defaultHTTPS;
	protected ClickActionAdapter playerClickAction;
	protected String playerClickValue, urlColor;
	protected List<PlaceholderType> playerPlaceholderTypes = Collections.emptyList();
	protected Map<Language, String> playerHovers = new HashMap<>(), urlHovers = new HashMap<>();
	protected Map<Rank, Map<Language, TextAdapter>> ranksCache = new HashMap<>();
	protected List<String> chatFormat = new ArrayList<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.enabled" in {@link ConfigurationType#CHAT}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if info should be displayed when hovering over a player's rank in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.rank.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether rank hover should be displayed
	 */
	public boolean isRankHoverEnabled() {
		return rankHoverEnabled;
	}
	
	/**
	 * Checks if info should be displayed when hovering over a player's name in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether player hover should be displayed
	 */
	public boolean isPlayerHoverEnabled() {
		return playerHoverEnabled;
	}
	
	/**
	 * Checks if text should be displayed when hovering over an URL in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.url.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether URL hover should be displayed
	 */
	public boolean isURLHoverEnabled() {
		return urlHoverEnabled;
	}
	
	/**
	 * Checks if "https://" should be automatically applied to URLs sent in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.url.default-https" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether "https://" should be added to URLs
	 */
	public boolean isDefaultHTTPS() {
		return defaultHTTPS;
	}
	
	/**
	 * Gets the click action executed when a player clicks a player's name in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.click.action" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Action executed when clicking players' names
	 */
	public ClickActionAdapter getPlayerClickAction() {
		return playerClickAction;
	}
	
	/**
	 * Gets the value applied to {@link #getPlayerClickAction()}
	 * when a player clicks a player's name in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.click.value" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Value associated with the action executed on click
	 */
	public String getPlayerClickValue() {
		return playerClickValue;
	}
	
	/**
	 * Gets the color that will be applied to valid URLs sent in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.color" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return URLs' color
	 */
	public String getURLColor() {
		return urlColor;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate players' hovers sent in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.placeholder-types" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Placeholders used to translate hovers
	 */
	public List<PlaceholderType> getPlayerPlaceholderTypes() {
		return playerPlaceholderTypes;
	}
	
	/**
	 * Gets the map of the hovers displayed when hovering over a player's name in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.hovers" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Player hovers
	 */
	public Map<Language, String> getPlayerHovers() {
		return playerHovers;
	}
	
	/**
	 * Gets the map of the hovers displayed when hovering over an URL in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.url.hovers" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return URL hovers
	 */
	public Map<Language, String> getURLHovers() {
		return urlHovers;
	}
	
	/**
	 * Gets the ranks' hover info cache.
	 * Do not modify the returned map.
	 * 
	 * @return Ranks' cache
	 */
	public Map<Rank, Map<Language, TextAdapter>> getRanksCache() {
		return ranksCache;
	}
	
	/**
	 * Gets {@link ChatManager#getFormat()} split around placeholders contained in the format.
	 * 
	 * @return Chat's format, split up
	 */
	public List<String> getChatFormat() {
		return chatFormat;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static HoverInfoManager getInstance() {
		return instance;
	}
	
	/**
	 * Gets the hover info for the specified rank from {@link #getRanksCache()}.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s hover info if no hover info is present for the specified language.
	 * Will return <code>null</code> if {@link #getRanksCache()}<code>.get(rank).get(language) == null && !avoidNull</code>
	 * or a new instance of {@link TextAdapter#EMPTY_TEXT} if <code>!</code>{@link #isRankHoverEnabled()}.
	 * 
	 * @param rank Target rank
	 * @param language Language used to translate the hover info
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Rank's hover info
	 */
	@Nullable(why = "No hover info may be present for the specified language")
	public abstract TextAdapter getRankHoverInfo(Rank rank, Language language, boolean avoidNull);
	
	/**
	 * Gets the hover info for the specified player from {@link #getPlayerHovers()}.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s hover info if no hover info is present for the specified language.
	 * Will return <code>null</code> if {@link #getPlayerHovers()}<code>.get(language) == null && !avoidNull</code>
	 * or a new instance of {@link TextAdapter#EMPTY_TEXT} if <code>!</code>{@link #isPlayerHoverEnabled()}.
	 * 
	 * @param player Target player
	 * @param language Language used to translate the hover info
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Player's hover info
	 */
	@Nullable(why = "No hover info may be present for the specified language")
	public abstract TextAdapter getPlayerHoverInfo(ChatPluginServerPlayer player, Language language, boolean avoidNull);
	
	/**
	 * Gets the hover info for the URLs contained in the specified input from {@link #getURLHovers()}.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s hover info if no hover info is present for the specified language.
	 * Will return <code>null</code> if {@link #getURLHovers()}<code>.get(language) == null && !avoidNull</code>
	 * or a new instance of {@link TextAdapter#EMPTY_TEXT} if <code>!</code>{@link #isURLHoverEnabled()}.
	 * 
	 * @param input Input containing URLs
	 * @param language Language used to translate the hover info
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return URLs' hover info
	 */
	@Nullable(why = "No hover info may be present for the specified language")
	public abstract TextAdapter getURLsHoverInfo(String input, Language language, boolean avoidNull);
	
	/**
	 * Gets the hover info for the specified message.
	 * 
	 * @param message Message involved
	 * @param player Target player
	 * @param language Language used to translate the hover info
	 * @return Message's hover info
	 */
	public abstract TextAdapter getMessageHoverInfo(String message, ChatPluginServerPlayer player, Language language);
	
}
