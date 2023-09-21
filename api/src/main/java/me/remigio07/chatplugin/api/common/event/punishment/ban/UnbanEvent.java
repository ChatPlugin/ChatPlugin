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

package me.remigio07.chatplugin.api.common.event.punishment.ban;

import java.net.InetAddress;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.ban.Ban;
import me.remigio07.chatplugin.api.common.punishment.ban.BanManager;

/**
 * Represents the event called after an unban gets processed.
 * 
 * @see BanManager#unban(OfflinePlayer, String, String)
 * @see BanManager#unbanIP(InetAddress, String, String)
 * @see BanManager#unban(int, String)
 */
public class UnbanEvent extends BanEvent {
	
	/**
	 * Constructs a new unban event.
	 * 
	 * @param ban Ban involved
	 */
	public UnbanEvent(Ban ban) {
		super(ban);
	}
	
}
