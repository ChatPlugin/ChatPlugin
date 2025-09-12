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

package me.remigio07.chatplugin.api.common.event.punishment.ban.banwave;

import me.remigio07.chatplugin.api.common.punishment.ban.banwave.BanwaveEntry;

/**
 * Represents the event called after a banwave entry gets updated.
 */
public class BanwaveEntryUpdateEvent extends BanwaveEntryAddEvent {
	
	private BanwaveEntry oldEntry;
	
	/**
	 * Constructs a new banwave entry update event.
	 * 
	 * @param oldEntry Old entry's copy
	 * @param entry Entry involved
	 */
	public BanwaveEntryUpdateEvent(BanwaveEntry oldEntry, BanwaveEntry entry) {
		super(entry);
		this.oldEntry = oldEntry;
	}
	
	/**
	 * Gets a copy of {@link #getEntry()} with its old values.
	 * 
	 * @return Old entry's copy
	 */
	public BanwaveEntry getOldEntry() {
		return oldEntry;
	}
	
}
