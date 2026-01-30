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

package me.remigio07.chatplugin.server.fabric.manager;

import java.lang.reflect.InvocationTargetException;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.server.chat.BaseChatManager;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class FabricChatManager extends BaseChatManager {
	
	private static boolean registered;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!load0())
			return;
		if (!overrideChatEvent) {
			LogManager.log("The \"chat.event.override\" setting in chat.yml is set to false. This is not supported on Fabric yet: ChatPlugin needs to take over the chat event to make its features work; disabling it.", 2);
			
			overrideChatEvent = false;
		} // TODO: we will figure out priorities/phases soon
		if (!registered && VersionUtils.getVersion().isAtLeast(Version.V1_19)) {
			if (VersionUtils.getVersion().equals(Version.V1_19))
				me.remigio07.chatplugin.mixin.Utils.registerAllowChatMessage();
			else ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> ((FabricChatManager) instance).allowChatMessage(message, sender));
			
			registered = true;
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	public boolean allowChatMessage(SignedMessage message, ServerPlayerEntity sender) {
		if (ChatPlugin.getState() != ChatPluginState.LOADED)
			return true;
		Text content = null;
		
		if (VersionUtils.getVersion().isOlderThan(Version.V1_19_3))
			try {
				content = (Text) SignedMessage.class.getMethod("method_44125").invoke(message);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		else content = message.getContent();
		return ((FabricEventManager) EventManager.getInstance()).handleChatMessage(sender, Utils.toLegacyText(content));
	}
	
}
