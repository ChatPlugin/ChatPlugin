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

package me.remigio07.chatplugin.server.rank;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankTag;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;

public class RankImpl extends Rank {
	
	private String displayName, prefix, suffix, chatColor;
	private Object permission;
	
	public RankImpl(String id, int position) {
		super(id, position);
		tag = new RankTagImpl(this);
		
		if (Environment.isBukkit())
			permission = new Permission("chatplugin.ranks." + id, PermissionDefault.FALSE);
	}
	
	@Override
	public String formatPlaceholders(String input, Language language) {
		return input
				.replace("{rank_id}", id)
				.replace("{rank_display_name}", displayName)
				.replace("{prefix}", prefix)
				.replace("{suffix}", suffix)
				.replace("{tag_prefix}", ((RankTagImpl) tag).prefix)
				.replace("{tag_suffix}", ((RankTagImpl) tag).suffix)
				.replace("{tag_name_color}", ((RankTagImpl) tag).nameColor)
				.replace("{chat_color}", chatColor)
				.replace("{rank_position}", String.valueOf(position))
				.replace("{rank_description}", ChatColor.translate(getDescription(language, true)))
				.replace("{max_ban_duration}", Utils.formatTime(maxPunishmentDurations[0], language, false, true))
				.replace("{max_mute_duration}", Utils.formatTime(maxPunishmentDurations[1], language, false, true));
	}
	
	@Override
	public List<String> formatPlaceholders(List<String> input, Language language) {
		return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
	}
	
	public void setDisplayName(String displayName) { // default: id
		this.displayName = ChatColor.translate(super.displayName = displayName == null ? id : displayName, false);
	}
	
	public void setPrefix(String prefix) { // default: ""
		this.prefix = ChatColor.translate(super.prefix = prefix == null ? "" : prefix, false);
	}
	
	public void setSuffix(String suffix) { // default: ""
		this.suffix = ChatColor.translate(super.suffix = suffix == null ? "" : suffix, false);
	}
	
	public void setTagPrefix(String tagPrefix) { // default: ""
		((RankTagImpl) tag).setPrefix(tagPrefix);
	}
	
	public void setTagSuffix(String tagSuffix) { // default: ""
		((RankTagImpl) tag).setSuffix(tagSuffix);
	}
	
	public void setTagNameColor(String tagNameColor) { // default: "&f", even if empty
		((RankTagImpl) tag).setNameColor(tagNameColor);
	}
	
	public void setChatColor(String chatColor) { // default: "&f", even if empty
		this.chatColor = ChatColor.translate(super.chatColor = chatColor == null || chatColor.isEmpty() ? "&f" : chatColor, false);
	}
	
	public Object getPermission() {
		return permission;
	}
	
	private static class RankTagImpl extends RankTag {
		
		private String prefix, suffix, nameColor;
		
		private RankTagImpl(Rank rank) {
			super(rank);
		}
		
		public void setPrefix(String prefix) {
			this.prefix = ChatColor.translate(super.prefix = prefix == null ? "" : prefix, false);
		}
		
		public void setSuffix(String suffix) {
			this.suffix = ChatColor.translate(super.suffix = suffix == null ? "" : suffix, false);
		}
		
		public void setNameColor(String nameColor) {
			this.nameColor = ChatColor.translate(super.nameColor = nameColor == null || nameColor.isEmpty() ? "&f" : nameColor, false);
		}
		
	}
	
}
