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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager.QuitPacket;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;

/**
 * Manager that handles switch messages.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Join-quit#switch-messages">ChatPlugin wiki/Modules/Join-quit/Switch messages</a>
 * @see QuitPacket
 */
public abstract class SwitchMessageManager implements ChatPluginManager {
	
	protected static SwitchMessageManager instance;
	protected boolean enabled;
	protected Map<Rank, Map<Language, List<String>>> switchMessages = new HashMap<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.switch-messages.settings.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the loaded switch messages' map.
	 * 
	 * <p>You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link RankManager#getDefaultRank()}.</p>
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.switch-messages.values" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Loaded switch messages' map
	 */
	public Map<Rank, Map<Language, List<String>>> getSwitchMessages() {
		return switchMessages;
	}
	
	/**
	 * Gets the list of loaded switch messages for the specified rank and language.
	 * 
	 * <p>Will fall back to {@link RankManager#getDefaultRank()}'s switch
	 * messages if no switch messages are present for the specified rank.</p>
	 * 
	 * @param rank Target rank
	 * @param language Language used to translate the switch messages
	 * @return Switch messages' list
	 */
	public List<String> getSwitchMessages(Rank rank, Language language) {
		Rank otherRank = switchMessages.get(rank) == null ? RankManager.getInstance().getDefaultRank() : rank;
		return switchMessages.get(otherRank).get(switchMessages.get(otherRank).get(language) == null ? Language.getMainLanguage() : language);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static SwitchMessageManager getInstance() {
		return instance;
	}
	
	/**
	 * Gets a random switch message from {@link #getSwitchMessages()}
	 * translated for the specified player and language.
	 * 
	 * @param packet Packet of who has switched servers
	 * @param language Language used to translate the switch message
	 * @return Translated switch message
	 */
	@NotNull
	public abstract String getSwitchMessage(QuitPacket packet, Language language);
	
	/**
	 * Announces that the specified player has switched servers.
	 * 
	 * @param packet Packet of who has switched servers
	 * @param newServerDisplayName Display name of the server the player has switched to
	 */
	public abstract void sendSwitchMessage(QuitPacket packet, String newServerDisplayName);
	
}
