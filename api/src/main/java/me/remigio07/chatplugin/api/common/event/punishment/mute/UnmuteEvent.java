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

package me.remigio07.chatplugin.api.common.event.punishment.mute;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.mute.Mute;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;

/**
 * Represents the event called after an unmute gets processed.
 * 
 * @see MuteManager#unmute(OfflinePlayer, String, String)
 * @see MuteManager#unmute(int, String)
 */
public class UnmuteEvent extends MuteEvent {
	
	/**
	 * Constructs a new unmute event.
	 * 
	 * @param mute Mute involved
	 */
	public UnmuteEvent(Mute mute) {
		super(mute);
	}
	
}
