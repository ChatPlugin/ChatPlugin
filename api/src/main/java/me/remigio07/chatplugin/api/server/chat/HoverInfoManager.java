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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.text.ClickActionAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.GameFeature;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles hover info in the chat.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Chat#hover-info">ChatPlugin wiki/Modules/Chat/Hover info</a>
 */
@GameFeature(
		name = "hover info",
		availableOnBukkit = true,
		availableOnSponge = true,
		spigotRequired = false,
		paperRequired = false,
		minimumBukkitVersion = Version.V1_7_2,
		minimumSpongeVersion = Version.V1_8
		)
public abstract class HoverInfoManager implements ChatPluginManager {
	
	protected static HoverInfoManager instance;
	protected boolean enabled, rankHoverEnabled, playerHoverEnabled, urlHoverEnabled, defaultHTTPS, playerPingHoverEnabled, instantEmojiHoverEnabled;
	protected ClickActionAdapter playerClickAction;
	protected String playerClickValue, urlColor;
	protected Set<PlaceholderType> playerPlaceholderTypes = Collections.emptySet();
	protected Map<Language, String> playerHovers = new HashMap<>(), urlHovers = new HashMap<>(), instantEmojiHovers = new HashMap<>();
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
	 * Checks if info should be displayed when
	 * hovering over a player's rank in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.rank.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether rank hover should be displayed
	 */
	public boolean isRankHoverEnabled() {
		return rankHoverEnabled;
	}
	
	/**
	 * Checks if info should be displayed when
	 * hovering over a player's name in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether player hover should be displayed
	 */
	public boolean isPlayerHoverEnabled() {
		return playerHoverEnabled;
	}
	
	/**
	 * Checks if text should be displayed
	 * when hovering over a URL in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.url.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether URL hover should be displayed
	 */
	public boolean isURLHoverEnabled() {
		return urlHoverEnabled;
	}
	
	/**
	 * Checks if "https://" should be automatically
	 * applied to URLs sent in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.url.default-https" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether "https://" should be added to URLs
	 */
	public boolean isDefaultHTTPS() {
		return defaultHTTPS;
	}
	
	/**
	 * Checks if info should be displayed when
	 * hovering over a player ping in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player-ping.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether player ping hover should be displayed
	 */
	public boolean isPlayerPingHoverEnabled() {
		return playerPingHoverEnabled;
	}
	
	/**
	 * Checks if info should be displayed when
	 * hovering over an instant emoji in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.instant-emoji.enabled" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Whether instant emoji hover should be displayed
	 */
	public boolean isInstantEmojiHoverEnabled() {
		return instantEmojiHoverEnabled;
	}
	
	/**
	 * Gets the click action executed when a
	 * player clicks a player's name in the chat.
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
	 * Gets the color that will be applied
	 * to valid URLs sent in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.color" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return URLs' color
	 */
	public String getURLColor() {
		return urlColor;
	}
	
	/**
	 * Gets the set of placeholder types used
	 * to translate players' hovers sent in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.placeholder-types" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Placeholders used to translate hovers
	 */
	public Set<PlaceholderType> getPlayerPlaceholderTypes() {
		return playerPlaceholderTypes;
	}
	
	/**
	 * Gets the map of the hovers displayed when
	 * hovering over a player's name in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.player.hovers" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return Player hovers
	 */
	public Map<Language, String> getPlayerHovers() {
		return playerHovers;
	}
	
	/**
	 * Gets the map of the hovers displayed
	 * when hovering over a URL in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.url.hovers" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return URL hovers
	 */
	public Map<Language, String> getURLHovers() {
		return urlHovers;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static HoverInfoManager getInstance() {
		return instance;
	}
	
}
