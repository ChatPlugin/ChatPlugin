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

package me.remigio07.chatplugin.api.server.event.chat;

import java.util.List;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamResult;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

/**
 * Represents an event called before a player's message is blocked.
 */
public class DenyChatEvent extends PublicMessageEvent {
	
	private DenyChatReason<?> denyChatReason;
	private AntispamResult antispamResult;
	
	/**
	 * Constructs a new deny chat event.
	 * 
	 * @param player Player involved
	 * @param message Message involved
	 * @param channel Channel involved
	 * @param denyChatReason Reason why the message has been blocked
	 * @param antispamResult Result of the antispam's check
	 */
	public DenyChatEvent(
			ChatPluginServerPlayer player,
			String message,
			@Nullable(why = "Null if !ChatChannelsManager#isEnabled()") ChatChannel<? extends ChatChannelData> channel,
			DenyChatReason<?> denyChatReason,
			@Nullable(why = "Null if getDenyChatReason().getHandlerClass() != AntispamManager.class") AntispamResult antispamResult
			) {
		super(player, message, channel);
		this.denyChatReason = denyChatReason;
		this.antispamResult = antispamResult;
	}
	
	/**
	 * Gets the reason why the message has been blocked.
	 * 
	 * @return Reason why the message has been blocked
	 */
	public DenyChatReason<?> getDenyChatReason() {
		return denyChatReason;
	}
	
	/**
	 * Gets the result of {@link AntispamManager#check(ChatPluginServerPlayer, String, List)}.
	 * 
	 * <p>Will return <code>null</code> if the antispam has allowed this message.
	 * 
	 * @return Result of the antispam's check
	 */
	@Nullable(why = "Null if getDenyChatReason().getHandlerClass() != AntispamManager.class")
	public AntispamResult getAntispamResult() {
		return antispamResult;
	}
	
}
