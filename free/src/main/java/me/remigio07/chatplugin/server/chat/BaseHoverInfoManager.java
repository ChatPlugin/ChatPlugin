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

package me.remigio07.chatplugin.server.chat;

import java.util.List;

import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.chat.HoverInfoManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import net.kyori.adventure.text.TextComponent;

public abstract class BaseHoverInfoManager extends HoverInfoManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	public abstract TextComponent getMessageHoverInfo(String message, List<String> urls, ChatPluginServerPlayer player, Language language);
	
	public static class DummyHoverInfoManager extends BaseHoverInfoManager {
		
		@Override
		public TextComponent getMessageHoverInfo(String message, List<String> urls, ChatPluginServerPlayer player, Language language) {
			return null;
		}
		
	}
	
}
