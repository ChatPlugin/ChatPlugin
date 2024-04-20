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

package me.remigio07.chatplugin.api.server.event.chat;

import me.remigio07.chatplugin.api.server.chat.RangedChatManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents a public message-related event.
 */
public abstract class PublicMessageEvent extends ChatEvent {
	
	protected boolean global;
	
	protected PublicMessageEvent(ChatPluginServerPlayer player, String message, boolean global) {
		super(player, message);
		this.global = global;
	}
	
	/**
	 * Checks whether the message has been
	 * sent using the global chat mode.
	 * 
	 * <p>Will always return <code>true</code> when
	 * <code>!</code>{@link RangedChatManager#isEnabled()}.</p>
	 * 
	 * @return Whether the message is global
	 */
	public boolean isGlobal() {
		return global;
	}
	
}
