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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.event.punishment.ban.banwave;

import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.common.punishment.ban.banwave.BanwaveManager;

/**
 * Represents the event called after a banwave has completed.
 * 
 * @see BanwaveManager#run()
 */
public class BanwaveEndEvent implements ChatPluginEvent {
	
	private int bannedPlayers;
	
	/**
	 * Constructs a new banwave end event.
	 * 
	 * @param bannedPlayers Amount of banned players
	 */
	public BanwaveEndEvent(int bannedPlayers) {
		this.bannedPlayers = bannedPlayers;
	}
	
	/**
	 * Gets the amount of players the banwave has banned.
	 * 
	 * @return Amount of banned players
	 */
	public int getBannedPlayers() {
		return bannedPlayers;
	}
	
}
