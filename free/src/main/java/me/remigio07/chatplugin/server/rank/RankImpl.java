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

package me.remigio07.chatplugin.server.rank;

import java.util.Map;

import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankPermission;
import me.remigio07.chatplugin.api.server.rank.RankTag;

public class RankImpl extends Rank {
	
	public RankImpl(String id, String displayName, String prefix, String suffix, String tagPrefix, String tagSuffix, String tagNameColor, String chatColor, int position, Map<Language, String> descriptions, long[] maxPunishmentDurations) {
		super(id, displayName, prefix, suffix, tagPrefix, tagSuffix, tagNameColor, chatColor, position, descriptions, maxPunishmentDurations);
		tag = new RankTagImpl(this, tagPrefix, tagSuffix, tagNameColor);
		permission = new RankPermissionImpl(this);
	}
	
	private static class RankTagImpl extends RankTag {
		
		private RankTagImpl(Rank rank, String prefix, String suffix, String nameColor) {
			super(rank, prefix, suffix, nameColor);
		}
		
	}
	
	private static class RankPermissionImpl extends RankPermission {
		
		private RankPermissionImpl(Rank rank) {
			super(rank);
		}
		
	}
	
}
