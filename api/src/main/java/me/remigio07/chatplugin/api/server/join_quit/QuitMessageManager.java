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

package me.remigio07.chatplugin.api.server.join_quit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

/**
 * Manager that handles quit messages.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Join-quit#quit-messages">ChatPlugin wiki/Modules/Join-quit/Quit messages</a>
 * @see QuitPacket
 */
public abstract class QuitMessageManager implements ChatPluginManager {
	
	protected static QuitMessageManager instance;
	protected boolean enabled;
	protected List<PlaceholderType> placeholderTypes = Collections.emptyList();
	protected Map<Rank, Map<Language, List<String>>> quitMessages = new HashMap<>();
	protected Map<UUID, QuitPacket> quitPackets = new HashMap<>();
	protected List<UUID> fakeQuits = new ArrayList<>();
	protected long loadTime;
	
	/**
	 * Checks if this manager is enabled.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.quit-messages.settings.enabled" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the list of placeholder types used
	 * to translate {@link #getQuitMessages()}.
	 * 
	 * <p><strong>Found at:</strong> "join-quit-modules.quit-messages.settings.placeholder-types" in {@link ConfigurationType#JOIN_QUIT_MODULES}</p>
	 * 
	 * @return Placeholders used to translate quit messages
	 */
	public List<PlaceholderType> getPlaceholderTypes() {
		return placeholderTypes;
	}
	
	/**
	 * Gets the map of loaded quit messages.
	 * 
	 * <p>You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link RankManager#getDefaultRank()}.</p>
	 * 
	 * @return Loaded quit messages' map
	 */
	public Map<Rank, Map<Language, List<String>>> getQuitMessages() {
		return quitMessages;
	}
	
	/**
	 * Gets the list of loaded quit messages for the specified rank and language.
	 * 
	 * <p>Will fall back to {@link RankManager#getDefaultRank()}'s quit
	 * messages if no quit messages are present for the specified rank.</p>
	 * 
	 * @param rank Target rank
	 * @param language Language used to translate the quit messages
	 * @return Quit messages' list
	 */
	@NotNull
	public List<String> getQuitMessages(Rank rank, Language language) {
		Rank otherRank = quitMessages.get(rank) == null ? RankManager.getInstance().getDefaultRank() : rank;
		return quitMessages.get(otherRank).get(quitMessages.get(otherRank).get(language) == null ? Language.getMainLanguage() : language);
	}
	
	/**
	 * Gets the loaded players' quit packets' map.
	 * 
	 * @return Quit packets' map
	 */
	public Map<UUID, QuitPacket> getQuitPackets() {
		return quitPackets;
	}
	
	/**
	 * Gets the list of players that have performed a fake quit.
	 * 
	 * <p>This includes players who have performed a silent teleport.</p>
	 * 
	 * @return Fake quits' list
	 */
	public List<UUID> getFakeQuits() {
		return fakeQuits;
	}
	
	/**
	 * Checks if the specified player has performed a fake quit.
	 * 
	 * @param player Player to check
	 * @return Whether the player has fake quit
	 */
	public boolean hasFakeQuit(UUID player) {
		return fakeQuits.contains(player);
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static QuitMessageManager getInstance() {
		return instance;
	}
	
	/**
	 * Gets a random quit message from {@link #getQuitMessages()}
	 * translated for the specified packet and language.
	 * 
	 * @param packet Packet of who has quit the server
	 * @param language Language used to translate the quit message
	 * @return Translated quit message
	 */
	@NotNull
	public abstract String getQuitMessage(QuitPacket packet, Language language);
	
	/**
	 * Announces that a player has quit the server.
	 * 
	 * @param packet Packet of who has quit the server
	 */
	public abstract void sendQuitMessage(QuitPacket packet);
	
	/**
	 * Represents a quit packet belonging to a player.
	 * 
	 * <p>Quit packets are used to translate placeholders of switch
	 * and quit messages sent after a player has quit the server.</p>
	 * 
	 * @see SwitchMessageManager
	 * @see QuitMessageManager
	 */
	public static abstract class QuitPacket {
		
		/**
		 * Array containing all available placeholders that
		 * can be translated with a packet's information.
		 * 
		 * <p><strong>Content:</strong> ["player", "uuid", "player_id", "rank_id", "rank_display_name", "prefix", "suffix", "tag_prefix", "tag_suffix", "tag_name_color", "chat_color", "rank_description"]</p>
		 * 
		 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Join-quit#placeholders">ChatPlugin wiki/Modules/Join-quit/Quit messages/Placeholders</a>
		 */
		public static final String[] PLACEHOLDERS = new String[] { "player", "uuid", "player_id", "rank_id", "rank_display_name", "prefix", "suffix", "tag_prefix", "tag_suffix", "tag_name_color", "chat_color", "rank_description" };
		protected OfflinePlayer player;
		protected Rank rank;
		protected int playerID;
		protected boolean vanished;
		
		/**
		 * Gets the player associated with this packet.
		 * 
		 * @return Packet's player
		 */
		public OfflinePlayer getPlayer() {
			return player;
		}
		
		/**
		 * Gets {@link #getPlayer()}'s rank.
		 * 
		 * @return Player's rank
		 */
		public Rank getRank() {
			return rank;
		}
		
		/**
		 * Gets {@link #getPlayer()}'s ID.
		 * 
		 * @return Player's ID
		 */
		public int getPlayerID() {
			return playerID;
		}
		
		/**
		 * Checks if {@link #getPlayer()} is vanished.
		 * 
		 * @return Whether the player is vanished
		 */
		public boolean isVanished() {
			return vanished;
		}
		
		/**
		 * Translates an input string with this packet's specific placeholders.
		 * 
		 * <p>Check {@link #PLACEHOLDERS} to find out the available placeholders.</p>
		 * 
		 * @param input Input containing placeholders
		 * @param language Language used to translate the placeholders
		 * @return Translated placeholders
		 */
		public abstract String formatPlaceholders(String input, Language language);
		
	}
	
}
