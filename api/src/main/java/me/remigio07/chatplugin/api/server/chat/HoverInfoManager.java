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

package me.remigio07.chatplugin.api.server.chat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.text.ClickActionAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.GameFeature;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles hover info in the chat. See wiki for more info:
 * <br><a href="https://github.com/ChatPlugin/ChatPlugin/wiki/Chat#hover-info">ChatPlugin wiki/Chat/Hover info</a>
 */
@GameFeature(
		name = "hover info",
		availableOnBukkit = true,
		availableOnSponge = true,
		spigotRequired = false,
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
	 * Checks if text should be displayed when hovering over a URL in the chat.
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
	 * Gets the map of the hovers displayed when hovering over a URL in the chat.
	 * 
	 * <p><strong>Found at:</strong> "chat.hover-info.url.hovers" in {@link ConfigurationType#CHAT}</p>
	 * 
	 * @return URL hovers
	 */
	public Map<Language, String> getURLHovers() {
		return urlHovers;
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
	
}
