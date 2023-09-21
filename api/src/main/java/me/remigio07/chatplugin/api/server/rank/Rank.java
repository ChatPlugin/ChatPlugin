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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.language.Language;

/**
 * Represents a rank handled by the {@link RankManager}. See wiki for more info:
 * <br><a href="https://github.com/ChatPlugin/ChatPlugin/wiki/Ranks">ChatPlugin wiki/Ranks</a>
 */
public abstract class Rank {
	
	/**
	 * Array containing all available placeholders that can
	 * be translated with a rank's information. See wiki for more info:
	 * <br><a href="https://github.com/ChatPlugin/ChatPlugin/wiki/Ranks#placeholders">ChatPlugin wiki/Ranks/Placeholders</a>
	 * 
	 * <p><strong>Content:</strong> ["rank_id", "rank_display_name", "prefix", "suffix", "tag_prefix", "tag_suffix", "tag_name_color", "chat_color", "rank_description"]</p>
	 */
	public static final String[] PLACEHOLDERS = new String[] { "rank_id", "rank_display_name", "prefix", "suffix", "tag_prefix", "tag_suffix", "tag_name_color", "chat_color", "rank_description" };
	private String id, displayName, prefix, suffix, chatColor;
	protected RankTag tag;
	protected RankPermission permission;
	private int position;
	private Map<Language, String> descriptions;
	private long[] maxPunishmentDurations;
	
	protected Rank(String id, String displayName, String prefix, String suffix, String tagPrefix, String tagSuffix, String tagNameColor, String chatColor, int position, Map<Language, String> descriptions, long[] maxPunishmentDurations) {
		if (descriptions.get(Language.getMainLanguage()) == null)
			throw new IllegalArgumentException("Specified map does not contain a translation for the main language");
		this.id = id;
		this.displayName = displayName;
		this.prefix = prefix;
		this.suffix = suffix;
		this.chatColor = chatColor;
		this.position = position;
		this.descriptions = descriptions;
		this.maxPunishmentDurations = maxPunishmentDurations;
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
	 * Gets the prefix every player in this rank will have. May be empty.
	 * 
	 * @return Rank's prefix
	 */
	@NotNull
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Gets the suffix every player in this rank will have. May be empty.
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
	 * Gets the default chat color this rank will have. May be empty.
	 * 
	 * @return Rank's chat color
	 */
	@NotNull
	public String getChatColor() {
		return chatColor;
	}
	
	/**
	 * Gets the permission required to players to have this rank.
	 * 
	 * @return Rank's permission
	 */
	@NotNull
	public RankPermission getPermission() {
		return permission;
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
	 * You may modify the returned map, but it cannot point to a
	 * <code>null</code> value for {@link Language#getMainLanguage()}.
	 * 
	 * @return Rank's descriptions
	 */
	public Map<Language, String> getDescriptions() {
		return descriptions;
	}
	
	/**
	 * Gets this rank's description for the specified language.
	 * Specify <code>true</code> as <code>avoidNull</code> to fall back to
	 * {@link Language#getMainLanguage()}'s description if no description is present for the specified language.
	 * Will return <code>null</code> if {@link #getDescriptions()}<code>.get(language) == null &amp;&amp; !avoidNull</code>.
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
	 * Gets this rank's max punishment durations. The returned array contains two elements:
	 * 	<ul>
	 * 		<li>the max allowed /tempban or /tempbanip punishment duration, in milliseconds</li>
	 * 		<li>the max allowed /mute punishment duration, in milliseconds</li>
	 * 	</ul>
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
	 * Gets this rank's ID which will be set as the Vanilla team name.
	 * It is obtained by joining {@link #formatPosition()} with {@link #getID()}.
	 * 
	 * @return The Vanilla team name
	 */
	public String getTeamName() {
		return formatPosition() + id;
	}
	
	/**
	 * Translates an input string with this rank's specific placeholders.
	 * Check {@link #PLACEHOLDERS} to know the available placeholders.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public String formatPlaceholders(String input, Language language) {
		return ChatColor.translate(input
				.replace("{rank_id}", id)
				.replace("{rank_display_name}", displayName)
				.replace("{prefix}", prefix)
				.replace("{suffix}", suffix)
				.replace("{tag_prefix}", tag.getPrefix())
				.replace("{tag_suffix}", tag.getSuffix())
				.replace("{tag_name_color}", tag.getNameColor())
				.replace("{chat_color}", chatColor)
				.replace("{rank_description}", descriptions.get(language))
				);
	}
	
	/**
	 * Translates an input string list with this rank's specific placeholders.
	 * Check {@link #PLACEHOLDERS} to know the available placeholders.
	 * 
	 * @param input Input containing placeholders
	 * @param language Language used to translate the placeholders
	 * @return Translated placeholders
	 */
	public List<String> formatPlaceholders(List<String> input, Language language) {
		return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
	}
	
}
