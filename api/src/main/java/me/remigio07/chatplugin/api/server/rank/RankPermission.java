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

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Permission adapter used to prevent compatibility issues between Bukkit's and Sponge's libraries.
 * 
 * <p>The method {@link RankPermission#bukkitValue()} returns a {@link org.bukkit.permissions.Permission}
 * object with a default value of {@link PermissionDefault#FALSE} which prevents OPs from having the
 * highest rank in the ranks list. Sponge doesn't support this feature, unluckily. (Or does it? LMK!)</p>
 */
public abstract class RankPermission {
	
	private Rank rank;
	
	protected RankPermission(Rank rank) {
		this.rank = rank;
	}
	
	/**
	 * Gets the rank related to this object.
	 * 
	 * @return Related rank
	 */
	public Rank getRank() {
		return rank;
	}
	
	/**
	 * Gets the equivalent Bukkit's {@link org.bukkit.permissions.Permission}
	 * with a default value of {@link PermissionDefault#FALSE} which prevents
	 * operators from having the highest rank in the ranks list.
	 * 
	 * @return Bukkit-adapted permission
	 */
	public Permission bukkitValue() {
		if (Environment.isBukkit())
			return new Permission(toString(), PermissionDefault.FALSE);
		else throw new UnsupportedOperationException("Unable to adapt permission to a Bukkit's Permission on a " + Environment.getCurrent().getName() + " environment");
	}
	
	/**
	 * Gets a string representing the permission required to players to have this rank.
	 */
	@Override
	public String toString() {
		return RankManager.getInstance().getPermissionFormat().replace("{0}", rank.getID());
	}
	
}