/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a rank handled by the {@link RankManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Ranks">ChatPlugin wiki/Modules/Ranks</a>
 */
public abstract class Rank {
	
	/**
	 * Array containing all available placeholders that
	 * can be translated with a rank's information.
	 * 
	 * <p><strong>Content:</strong> ["rank_id", "rank_display_name", "prefix", "suffix", "tag_prefix", "tag_suffix", "tag_name_color", "chat_color", "rank_position", "rank_description", "max_ban_duration", "max_mute_duration"]</p>
	 * 
	 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Ranks#placeholders">ChatPlugin wiki/Modules/Ranks/Placeholders</a>
	 */
	public static final String[] PLACEHOLDERS = { "rank_id", "rank_display_name", "prefix", "suffix", "tag_prefix", "tag_suffix", "tag_name_color", "chat_color", "rank_position", "rank_description", "max_ban_duration", "max_mute_duration" };
	protected String id, displayName, prefix, suffix, chatColor;
	protected RankTag tag;
	protected int position;
	protected Map<Language, String> descriptions = new HashMap<>();
	protected long[] maxPunishmentDurations = new long[2];
	
	protected Rank(String id, int position) {
		this.id = id;
		this.position = position;
	}
	
	/**
	 * Gets this rank's ID.
	 * 
	 * @return Rank's ID
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Gets this rank's display name.
	 * 
	 * @return Rank's display name
	 */
	@NotNull
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Gets the prefix every player in this rank will have.
	 * 
	 * @return Rank's prefix
	 */
	@NotNull
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Gets the suffix every player in this rank will have.
	 * 
	 * @return Rank's suffix
	 */
	@NotNull
	public String getSuffix() {
		return suffix;
	}
	
	/**
	 * Gets the tag every player in this rank will have.
	 * 
	 * @return Rank's tag
	 */
	@NotNull
	public RankTag getTag() {
		return tag;
	}
	
	/**
	 * Gets the default chat color this rank will have.
	 * 
	 * @return Rank's chat color
	 */
	@NotNull
	public String getChatColor() {
		return chatColor;
	}
	
	/**
	 * Gets this rank's position in {@link RankManager#getRanks()}.
	 * 
	 * @return Rank's position
	 */
	public int getPosition() {
		return position;
	}
	
	/**
	 * Gets this rank's descriptions.
	 * 
	 * @return Rank's descriptions
	 */
	public Map<Language, String> getDescriptions() {
		return descriptions;
	}
	
	/**
	 * Gets this rank's description for the specified language.
	 * 
	 * <p>Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s description if no description is present for the specified language.
	 * Will return <code>null</code> if {@link #getDescriptions()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.</p>
	 * 
	 * @param language Language used to translate the description
	 * @param avoidNull Whether to avoid returning <code>null</code>
	 * @return Rank's description
	 */
	@Nullable(why = "No description may be present for the specified language")
	public String getDescription(Language language, boolean avoidNull) {
		return descriptions.get(language) == null ? avoidNull ? descriptions.get(Language.getMainLanguage()) : null : descriptions.get(language);
	}
	
	/**
	 * Gets this rank's max punishment durations.
	 * 
	 * <p>The returned array contains two elements:
	 * 	<ol start=0>
	 * 		<li>the max allowed /tempban or /tempbanip punishment duration, in milliseconds</li>
	 * 		<li>the max allowed /mute punishment duration, in milliseconds</li>
	 * 	</ol>
	 * 
	 * @return Rank's max punishment durations
	 */
	public long[] getMaxPunishmentDurations() {
		return maxPunishmentDurations;
	}
	
	/**
	 * Gets an alphabetically sortable String containing the rank's position in the following format:
	 * "03", "41", "17"... Will return "00" if <code>!</code>{@link RankManager#isSortingEnabled()}
	 * or be automatically adjusted based on {@link RankManager#isSortingFromTablistTop()}.
	 * 
	 * @return Position's sortable String
	 */
	public String formatPosition() {
		if (!RankManager.getInstance().isSortingEnabled())
			return "00";
		String str;
		return (str = RankManager.getInstance().isSortingFromTablistTop() ? String.valueOf(RankManager.getInstance().getRanks().size() - position - 1) : String.valueOf(position)).length() == 1 ? "0" + str : str;
	}
	
	/**
	 * Gets this rank's identifier for the specified player.
	 * 
	 * @param player Player to identify
	 * @return Rank's identifier
	 */
	public String formatIdentifier(ChatPluginServerPlayer player) {
		return formatPosition() + String.format("%-4s", player.getName()).substring(0, 4) + String.format("%010d", player.getID());
	}
	
	/**
	 * Translates an input string with this rank's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to find out the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract String formatPlaceholders(String input, Language language);
	
	/**
	 * Translates an input string list with this rank's specific placeholders.
	 * 
	 * <p>Check {@link #PLACEHOLDERS} to find out the available placeholders.</p>
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public abstract List<String> formatPlaceholders(List<String> input, Language language);
	
}
