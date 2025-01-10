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

import me.remigio07.chatplugin.api.common.util.annotation.NotNull;

/**
 * Represents a rank's tag, composed of a prefix, a suffix and a name color.
 * 
 * <p>Prefix and suffix will appear first and after the player's name in the tablist.</p>
 */
public abstract class RankTag {
	
	private Rank rank;
	protected String prefix, suffix, nameColor;
	
	protected RankTag(Rank rank) {
		this.rank = rank;
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
	 * Gets this tag's prefix.
	 * 
	 * @return Tag's prefix
	 */
	@NotNull
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Gets this tag's suffix.
	 * 
	 * @return Tag's suffix
	 */
	@NotNull
	public String getSuffix() {
		return suffix;
	}
	
	/**
	 * Gets this tag's name color.
	 * 
	 * @return Tag's name color
	 */
	@NotNull
	public String getNameColor() {
		return nameColor;
	}
	
	/**
	 * Get this tag's representation as a simple string.
	 * 
	 * <p>This method is used to check if the tag is empty:
	 * sometimes it can be useful as ChatPlugin won't have to set up any vanilla team's tag.
	 * Example of output from this method (prefix + name color + suffix): "[S] &#167;e [*]".</p>
	 * 
	 * @return Tag's representation
	 */
	@Override
	public String toString() {
		return prefix + nameColor + suffix;
	}
	
}