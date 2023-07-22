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

package me.remigio07_.chatplugin.api.common.event.punishment.ban.banwave;

import me.remigio07_.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07_.chatplugin.api.common.punishment.ban.banwave.BanwaveManager;

/**
 * Represents the event called just before a banwave is executed.
 * 
 * @see BanwaveManager#run()
 */
public class BanwaveStartEvent implements ChatPluginEvent {
	
	private long estimatedDuration;
	
	/**
	 * Constructs a new banwave start event.
	 * 
	 * @param estimatedDuration Banwave's estimated duration
	 */
	public BanwaveStartEvent(long estimatedDuration) {
		this.estimatedDuration = estimatedDuration;
	}
	
	/**
	 * Gets the banwave's estimated duration, in milliseconds.
	 * 
	 * @return Banwave's estimated duration
	 */
	public long getEstimatedDuration() {
		return estimatedDuration;
	}
	
}
