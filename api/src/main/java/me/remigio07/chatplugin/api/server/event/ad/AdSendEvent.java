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

package me.remigio07.chatplugin.api.server.event.ad;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.server.ad.Ad;
import me.remigio07.chatplugin.api.server.ad.AdManager;
import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called when an {@link Ad} is sent to a player.
 * 
 * @see AdManager#sendAd(Ad, ChatPluginServerPlayer)
 */
public class AdSendEvent implements CancellableEvent, ChatPluginServerPlayerEvent {
	
	private boolean cancelled;
	private ChatPluginServerPlayer player;
	private Ad ad;
	
	/**
	 * Constructs a new ad send event.
	 * 
	 * @param ad Ad involved
	 * @param player Player involved
	 */
	public AdSendEvent(Ad ad, ChatPluginServerPlayer player) {
		this.ad = ad;
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
	 * Gets the ad sent to the player.
	 * 
	 * @return Ad sent
	 */
	public Ad getAd() {
		return ad;
	}
	
}
