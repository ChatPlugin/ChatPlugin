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

package me.remigio07.chatplugin.api.server.event.actionbar;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.server.actionbar.Actionbar;
import me.remigio07.chatplugin.api.server.actionbar.ActionbarManager;
import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called when an {@link Actionbar} is sent to a player.
 * 
 * @see ActionbarManager#sendActionbar(Actionbar, ChatPluginServerPlayer)
 */
public class ActionbarSendEvent implements CancellableEvent, ChatPluginServerPlayerEvent {
	
	private boolean cancelled;
	private ChatPluginServerPlayer player;
	private Actionbar actionbar;
	
	/**
	 * Constructs a new actionbar send event.
	 * 
	 * @param actionbar Actionbar involved
	 * @param player Player involved
	 */
	public ActionbarSendEvent(Actionbar actionbar, ChatPluginServerPlayer player) {
		this.actionbar = actionbar;
		this.player = player;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	@Override
	public ChatPluginServerPlayer getPlayer() {
		return player;
	}
	
	/**
	 * Gets the actionbar sent to the player.
	 * 
	 * @return Actionbar sent
	 */
	public Actionbar getActionbar() {
		return actionbar;
	}
	
}
