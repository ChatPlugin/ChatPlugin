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

package me.remigio07.chatplugin.api.server.rank;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Manager that handles {@link Rank}s. See wiki for more info:
 * <br><a href="https://github.com/ChatPlugin/ChatPlugin/wiki/Ranks">ChatPlugin wiki/Ranks</a>
 */
public abstract class RankManager implements ChatPluginManager {
	
	/**
	 * Pattern representing the allowed rank IDs.
	 * 
	 * <p><strong>Regex:</strong> "^[a-zA-Z0-9-_]{2,14}$"</p>
	 * 
	 * @see #isValidRankID(String)
	 */
	public static final Pattern RANK_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]{2,14}$");
	protected static RankManager instance;
	protected boolean enabled, sortingEnabled, sortingFromTablistTop;
	protected Rank defaultRank;
	protected String permissionFormat;
	protected List<Rank> ranks = new ArrayList<>();
	protected long loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if ranks should be sorted in the tablist.
	 * 
	 * <p><strong>Found at:</strong> "ranks.settings.sorting.enabled" in {@link ConfigurationType#RANKS}</p>
	 * 
	 * @return Whether to sort ranks
	 */
	public boolean isSortingEnabled() {
		return sortingEnabled;
	}
	
	/**
	 * Checks if ranks should be sorted from the top of the tablist.
	 * Will return <code>false</code> if they should be sorted from the bottom.
	 * 
	 * <p><strong>Found at:</strong> "ranks.settings.sorting.from-tablist-top" in {@link ConfigurationType#RANKS}</p>
	 * 
	 * @return Whether to sort ranks from the tablist's top or bottom
	 */
	public boolean isSortingFromTablistTop() {
		return sortingFromTablistTop;
	}
	
	/**
	 * Gets the default rank that those who are not part of any other rank will have.
	 * 
	 * <p><strong>Found at:</strong> "ranks.settings.default-rank-id" in {@link ConfigurationType#RANKS}</p>
	 * 
	 * @return Default rank
	 */
	public Rank getDefaultRank() {
		return defaultRank;
	}
	
	/**
	 * Gets the permission format used to check if a player is part of a certain rank.
	 * Should contain the placeholder "{0}" which should be replaced with the rank's ID.
	 * 
	 * <p><strong>Found at:</strong> "ranks.settings.permission-format" in {@link ConfigurationType#RANKS}</p>
	 * 
	 * @return Ranks' permission format
	 */
	public String getPermissionFormat() {
		return permissionFormat;
	}
	
	/**
	 * Gets the list of loaded ranks.
	 * Do not modify the returned list.
	 * 
	 * @return Loaded ranks' list
	 */
	public List<Rank> getRanks() {
		return ranks;
	}
	
	/**
	 * Gets a rank from {@link #getRanks()} by its ID.
	 * Will return <code>null</code> if the rank is not loaded.
	 * 
	 * @param id Language's ID
	 * @return Loaded language
	 */
	@Nullable(why = "Specified rank may not be loaded")
	public Rank getRank(String id) {
		return ranks.stream().filter(rank -> rank.getID().equals(id)).findAny().orElse(null);
	}
	
	/**
	 * Checks if the specified String is a valid rank ID.
	 * 
	 * @param rankID Rank ID to check
	 * @return Whether the specified rank ID is valid
	 * @see #RANK_ID_PATTERN
	 */
	public boolean isValidRankID(String rankID) {
		return RANK_ID_PATTERN.matcher(rankID).matches();
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static RankManager getInstance() {
		return instance;
	}
	
	/**
	 * Calculates a player's rank by checking every loaded rank's {@link Rank#getPermission()}.
	 * 
	 * @param player Player to calculate the rank for
	 * @return Player's rank
	 */
	public abstract Rank calculateRank(ChatPluginServerPlayer player);
	
}
