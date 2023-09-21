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

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;

/**
 * Represents a rank's tag, composed of a prefix, a suffix and a name color.
 * Prefix and suffix will appear first and after the player's name in the tablist.
 */
public abstract class RankTag {
	
	private Rank rank;
	private String prefix, suffix, nameColor;
	
	protected RankTag(Rank rank, String prefix, String suffix, String nameColor) {
		this.rank = rank;
		this.prefix = prefix == null || prefix.isEmpty() ? "&f" : prefix;
		this.suffix = suffix == null || suffix.isEmpty() ? "&f" : suffix;
		this.nameColor = nameColor == null || nameColor.isEmpty() ? "&f" : nameColor;
	}
	
	/**
	 * Gets the rank associated with this tag.
	 * 
	 * @return Associated rank
	 */
	public Rank getRank() {
		return rank;
	}
	
	/**
	 * Gets this tag's prefix. May be empty.
	 * 
	 * @return Tag's prefix
	 */
	@NotNull
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Gets this tag's suffix. May be empty.
	 * 
	 * @return Tag's suffix
	 */
	@NotNull
	public String getSuffix() {
		return suffix;
	}
	
	/**
	 * Gets this tag's name color. May be empty.
	 * 
	 * @return Tag's name color
	 */
	@NotNull
	public String getNameColor() {
		return nameColor;
	}
	
	/**
	 * Get this tag's representation as a simple string. This method is used to check if the tag is
	 * empty: sometimes it can be useful as ChatPlugin won't have to set up any vanilla team's tag.
	 * Example of output from this method (prefix + name color + suffix): "[S] &#167;e [*]".
	 * 
	 * @return Tag's representation
	 */
	@Override
	public String toString() {
		return prefix + nameColor + suffix;
	}
	
}