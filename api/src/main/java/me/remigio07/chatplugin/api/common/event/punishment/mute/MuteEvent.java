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

package me.remigio07.chatplugin.api.common.event.punishment.mute;

import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.mute.Mute;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;

/**
 * Represents the event called after a mute gets processed.
 * 
 * @see MuteManager#mute(OfflinePlayer, String, String, String, long, boolean, boolean)
 */
public class MuteEvent implements ChatPluginEvent {
	
	private Mute mute;
	
	/**
	 * Constructs a new mute event.
	 * 
	 * @param mute Mute involved
	 */
	public MuteEvent(Mute mute) {
		this.mute = mute;
	}
	
	/**
	 * Gets the mute involved with this event.
	 * 
	 * @return Mute involved
	 */
	public Mute getMute() {
		return mute;
	}
	
}
