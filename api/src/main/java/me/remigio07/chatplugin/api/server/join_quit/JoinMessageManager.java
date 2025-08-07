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

package me.remigio07.chatplugin.api.server.join_quit;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles join messages.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Join-quit#join-messages">ChatPlugin wiki/Modules/Join-quit/Join messages</a>
 */
public abstract class JoinMessageManager implements ChatPluginManager {
	
	protected static JoinMessageManager instance;
	protected boolean enabled;
	protected Set<PlaceholderType> placeholderTypes = Collections.emptySet();
	protected Map<Rank, Map<Language, List<String>>> joinMessages = new HashMap<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-messages.settings.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the set of placeholder types used
	 * to translate {@link #getJoinMessages()}.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-messages.settings.placeholder-types" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Placeholders used to translate join messages
	 */
	public Set<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the map of loaded join messages.
	 * 
	 * <p>You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link RankManager#getDefaultRank()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.join-messages.values" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Loaded join messages' map
	 */
	public Map<Rank, Map<Language, List<String>>> getJoinMessages() {
		return joinMessages;
	}
	
	/**
	 * Gets the list of loaded join messages for the specified rank and language.
	 * 
	 * <p>Will fall back to {@link RankManager#getDefaultRank()}'s join
	 * messages if no join messages are present for the specified rank.</p>
	 * 
	 * @param rank Target rank
	 * @param language Language used to translate the join messages
	 * @return Join messages' list
	 */
	@NotNull
	public List<String> getJoinMessages(Rank rank, Language language) {
		Rank otherRank = joinMessages.get(rank) == null ? RankManager.getInstance().getDefaultRank() : rank;
		return joinMessages.get(otherRank).get(joinMessages.get(otherRank).get(language) == null ? Language.getMainLanguage() : language);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static JoinMessageManager getInstance() {
		return instance;
	}
	
	/**
	 * Gets a random join message from {@link #getJoinMessages()}
	 * translated for the specified player and language.
	 * 
	 * @param player Who has joined the server
	 * @param language Language used to translate the join message
	 * @return Translated join message
	 */
	@NotNull
	public abstract String getJoinMessage(ChatPluginServerPlayer player, Language language);
	
	/**
	 * Announces that the specified player has joined the server.
	 * 
	 * @param player Who has joined the server
	 */
	public abstract void sendJoinMessage(ChatPluginServerPlayer player);
	
}
