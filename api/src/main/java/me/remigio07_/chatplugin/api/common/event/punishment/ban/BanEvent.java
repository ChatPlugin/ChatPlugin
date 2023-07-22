/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.api.common.event.punishment.ban;

import me.remigio07_.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.punishment.ban.Ban;
import me.remigio07_.chatplugin.api.common.punishment.ban.BanManager;

/**
 * Represents the event called after a ban gets processed.
 * 
 * @see BanManager#ban(OfflinePlayer, String, String, String, long, boolean, boolean)
 * @see BanManager#banIP(OfflinePlayer, String, String, String, long, boolean, boolean)
 * @see BanManager#banIP(String, String, String, String, long, boolean, boolean)
 */
public class BanEvent implements ChatPluginEvent {
	
	private Ban ban;
	
	/**
	 * Constructs a new ban event.
	 * 
	 * @param ban Ban involved
	 */
	public BanEvent(Ban ban) {
		this.ban = ban;
	}
	
	/**
	 * Gets the ban involved with this event.
	 * 
	 * @return Ban involved
	 */
	public Ban getBan() {
		return ban;
	}
	
}
