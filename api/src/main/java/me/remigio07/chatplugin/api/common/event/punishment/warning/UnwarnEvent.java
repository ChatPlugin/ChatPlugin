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

package me.remigio07.chatplugin.api.common.event.punishment.warning;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.warning.Warning;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;

/**
 * Represents the event called after a warning gets processed.
 * 
 * @see WarningManager#unwarn(int, String)
 * @see WarningManager#removeLastWarning(OfflinePlayer, String, String)
 * @see WarningManager#clearWarnings(OfflinePlayer, String, String)
 */
public class UnwarnEvent extends WarningEvent {
	
	/**
	 * Constructs a new unwarn event.
	 * 
	 * @param warning Warning involved
	 */
	public UnwarnEvent(Warning warning) {
		super(warning);
	}
	
}
