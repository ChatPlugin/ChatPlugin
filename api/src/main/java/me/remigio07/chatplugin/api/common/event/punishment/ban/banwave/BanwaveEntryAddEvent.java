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

package me.remigio07.chatplugin.api.common.event.punishment.ban.banwave;

import java.net.InetAddress;

import me.remigio07.chatplugin.api.common.event.ChatPluginEvent;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.ban.banwave.BanwaveEntry;
import me.remigio07.chatplugin.api.common.punishment.ban.banwave.BanwaveManager;

/**
 * Represents the event called after a banwave entry gets added to the entries' list.
 * 
 * @see BanwaveManager#addEntry(OfflinePlayer, String, String, String, long, boolean, boolean)
 * @see BanwaveManager#addIPEntry(OfflinePlayer, String, String, String, long, boolean, boolean)
 * @see BanwaveManager#addIPEntry(InetAddress, String, String, String, long, boolean, boolean)
 */
public class BanwaveEntryAddEvent implements ChatPluginEvent {
	
	private BanwaveEntry entry;
	
	/**
	 * Constructs a new banwave entry add event.
	 * 
	 * @param entry Entry involved
	 */
	public BanwaveEntryAddEvent(BanwaveEntry entry) {
		this.entry = entry;
	}
	
	/**
	 * Gets the entry involved with this event.
	 * 
	 * @return Entry involved
	 */
	public BanwaveEntry getEntry() {
		return entry;
	}
	
}
