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

package me.remigio07.chatplugin.server.chat;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.RangedChatManager;

public class RangedChatManagerImpl extends RangedChatManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.ranged-chat.enabled"))
			return;
		if (!ChatManager.getInstance().shouldOverrideChatEvent()) {
			LogManager.log("The ranged chat module is enabled but \"chat.event.override\" in chat.yml is set to false. This setup is not supported: ChatPlugin needs to take over the chat event to make its features work; disabling module.", 2);
			return;
		} spyOnJoinEnabled = ConfigurationType.CHAT.get().getBoolean("chat.ranged-chat.spy.on-join-enabled");
		globalModeEnabled = ConfigurationType.CHAT.get().getBoolean("chat.ranged-chat.global-mode.enabled");
		range = ConfigurationType.CHAT.get().getInt("chat.ranged-chat.range");
		spyFormat = ConfigurationType.CHAT.get().getString("chat.ranged-chat.spy.format");
		globalModePrefix = ConfigurationType.CHAT.get().getString("chat.ranged-chat.global-mode.prefix");
		globalModeFormat = ConfigurationType.CHAT.get().getString("chat.ranged-chat.global-mode.format");
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = spyOnJoinEnabled = globalModeEnabled = false;
		
		range = 0;
		spyFormat = globalModePrefix = globalModeFormat = null;
	}
	
}
