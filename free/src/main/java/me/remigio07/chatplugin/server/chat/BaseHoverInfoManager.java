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

package me.remigio07.chatplugin.server.chat;

import java.util.List;
import java.util.Set;

import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.chat.HoverInfoManager;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager.InstantEmoji;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import net.kyori.adventure.text.TextComponent;

public abstract class BaseHoverInfoManager extends HoverInfoManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	public abstract TextComponent getMessageHoverInfo(
			ChatPluginServerPlayer player,
			Language language,
			String message,
			ChatChannel<?> channel,
			List<String> urls,
			List<ChatPluginServerPlayer> pingedPlayers,
			Set<InstantEmoji> instantEmojis
			);
	
	public abstract void loadChannelsFormats();
	
	public abstract void loadRanksDescriptions();
	
	public static class DummyHoverInfoManager extends BaseHoverInfoManager {
		
		@Override
		public TextComponent getMessageHoverInfo(
				ChatPluginServerPlayer player,
				Language language,
				String message,
				ChatChannel<?> channel,
				List<String> urls,
				List<ChatPluginServerPlayer> pingedPlayers,
				Set<InstantEmoji> instantEmojis
				) {
			return null;
		}
		
		@Override
		public void loadChannelsFormats() {
			
		}
		
		@Override
		public void loadRanksDescriptions() {
			
		}
		
	}
	
}
