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

package me.remigio07.chatplugin.api.server.rank;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;

/**
 * Manager that handles {@link Rank}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Ranks">ChatPlugin wiki/Modules/Ranks</a>
 */
public abstract class RankManager implements ChatPluginManager {
	
	/**
	 * Pattern representing the allowed rank IDs.
	 * 
	 * <p><strong>Regex:</strong> "^(?!settings$)[a-zA-Z0-9-_+]{2,36}$"</p>
	 * 
	 * @see #isValidRankID(String)
	 */
	public static final Pattern RANK_ID_PATTERN = Pattern.compile("^(?!settings$)[a-zA-Z0-9-_+]{2,36}$");
	protected static RankManager instance;
	protected boolean enabled, luckPermsMode, sortingEnabled, sortingFromTablistTop;
	protected List<Rank> ranks = new ArrayList<>();
	protected long loadTime;
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Checks if the {@linkplain IntegrationType#LUCKPERMS LuckPerms} mode is enabled.
	 * 
	 * <p>When enabled, ChatPlugin will load {@link Rank}s using its API instead
	 * of reading them from the {@link ConfigurationType#RANKS} file.</p>
	 * 
	 * <p><strong>Found at:</strong> "ranks.settings.luckperms-mode" in {@link ConfigurationType#RANKS}</p>
	 * 
	 * @return Whether the LuckPerms mode is enabled
	 */
	public boolean isLuckPermsMode() {
		return luckPermsMode;
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
	 * 
	 * <p>Will return <code>false</code> if they should be sorted from the bottom.</p>
	 * 
	 * <p><strong>Found at:</strong> "ranks.settings.sorting.from-tablist-top" in {@link ConfigurationType#RANKS}</p>
	 * 
	 * @return Whether to sort ranks from the tablist's top or bottom
	 */
	public boolean isSortingFromTablistTop() {
		return sortingFromTablistTop;
	}
	
	/**
	 * Gets the default rank that those who are
	 * not part of any other rank will have.
	 * 
	 * <p>This is always the rank with "default" as
	 * ID if {@link #isLuckPermsMode()}, otherwise it
	 * is the first rank in {@link #getRanks()}.</p>
	 * 
	 * @return Default rank
	 */
	public Rank getDefaultRank() {
		return luckPermsMode ? getRank("default") : ranks.get(0);
	}
	
	/**
	 * Gets the list of loaded ranks, sorted
	 * by {@link Rank#getPosition()} starting
	 * from 0, for the highest rank.
	 * 
	 * <p>Do <em>not</em> modify the returned list.</p>
	 * 
	 * @return Loaded ranks' list
	 */
	public List<Rank> getRanks() {
		return ranks;
	}
	
	/**
	 * Gets a rank from {@link #getRanks()} by its ID.
	 * 
	 * <p>Will return <code>null</code> if the rank is not loaded.</p>
	 * 
	 * @param id Rank's ID, case insensitive
	 * @return Loaded rank
	 */
	@Nullable(why = "Specified rank may not be loaded")
	public Rank getRank(String id) {
		return ranks.stream().filter(rank -> rank.getID().equalsIgnoreCase(id)).findAny().orElse(null);
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
	
}
