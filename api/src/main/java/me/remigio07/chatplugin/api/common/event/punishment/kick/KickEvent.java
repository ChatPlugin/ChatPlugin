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

package me.remigio07.chatplugin.api.common.event.punishment.kick;

import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.common.player.ChatPluginPlayer;
import me.remigio07.chatplugin.api.common.punishment.kick.Kick;
import me.remigio07.chatplugin.api.common.punishment.kick.KickManager;
import me.remigio07.chatplugin.api.common.punishment.kick.KickType;

/**
 * Represents the event called after a kick gets processed.
 * 
 * @see KickManager#kick(ChatPluginPlayer, String, String, String, String, KickType, boolean)
 */
public class KickEvent implements ChatPluginEvent {
	
	private Kick kick;
	
	/**
	 * Constructs a new kick event.
	 * 
	 * @param kick Kick involved
	 */
	public KickEvent(Kick kick) {
		this.kick = kick;
	}
	
	/**
	 * Gets the kick involved with this event.
	 * 
	 * @return Kick involved
	 */
	public Kick getKick() {
		return kick;
	}
	
}
