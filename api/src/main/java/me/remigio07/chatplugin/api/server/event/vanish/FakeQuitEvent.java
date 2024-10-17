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

package me.remigio07.chatplugin.api.server.event.vanish;

import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called after a successful <code>/fakequit</code>.
 * 
 * <p><strong>Note:</strong> a {@link VanishEnableEvent} is always called before this event.</p>
 */
public class FakeQuitEvent extends VanishEvent {
	
	/**
	 * Constructs a new fake quit event.
	 * 
	 * @param player Player involved
	 */
	public FakeQuitEvent(ChatPluginServerPlayer player) {
		super(player);
	}
	
}
