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

package me.remigio07.chatplugin.api.server.event.chat.channel;

import me.remigio07.chatplugin.api.common.event.CancellableEvent;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.event.player.ChatPluginServerPlayerEvent;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a {@link ChatChannel}-related event.
 */
public abstract class ChatChannelEvent implements CancellableEvent, ChatPluginServerPlayerEvent {
	
	protected boolean cancelled;
	protected ChatPluginServerPlayer player;
	protected ChatChannel<? extends ChatChannelData> channel;
	
	protected ChatChannelEvent(ChatPluginServerPlayer player, ChatChannel<? extends ChatChannelData> channel) {
		this.player = player;
		this.channel = channel;
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
	 * Gets the channel involved with this event.
	 * 
	 * @return Channel involved
	 */
	public ChatChannel<? extends ChatChannelData> getChannel() {
		return channel;
	}
	
}
