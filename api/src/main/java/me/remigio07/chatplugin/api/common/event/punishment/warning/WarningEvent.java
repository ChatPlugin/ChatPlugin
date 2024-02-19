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

package me.remigio07.chatplugin.api.common.event.punishment.warning;

import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.warning.Warning;
import me.remigio07.chatplugin.api.common.punishment.warning.WarningManager;

/**
 * Represents the event called after a warning gets processed.
 * 
 * @see WarningManager#warn(OfflinePlayer, String, String, String, long, boolean, boolean)
 */
public class WarningEvent implements ChatPluginEvent {
	
	private Warning warning;
	
	/**
	 * Constructs a new warning event.
	 * 
	 * @param warning Warning involved
	 */
	public WarningEvent(Warning warning) {
		this.warning = warning;
	}
	
	/**
	 * Gets the warning involved with this event.
	 * 
	 * @return Warning involved
	 */
	public Warning getWarning() {
		return warning;
	}
	
}
