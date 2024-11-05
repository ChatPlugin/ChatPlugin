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

package me.remigio07.chatplugin.server.sponge.manager;

import java.util.Arrays;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.server.chat.BaseChatManager;
import me.remigio07.chatplugin.server.sponge.manager.SpongeEventManager.SpongeListener;

public class SpongeChatManager extends BaseChatManager {
	
	private SpongeListener listener = new SpongeListener();
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!load0())
			return;
		try {
			Order order = Order.valueOf(ConfigurationType.CHAT.get().getString("chat.event.priority").toUpperCase());
			
			if (Arrays.asList(Order.PRE, Order.AFTER_PRE, Order.BEFORE_POST, Order.POST).contains(order))
				throw new IllegalArgumentException();
			chatEventPriority = order.name(); // ensure upper case
		} catch (IllegalArgumentException e) {
			LogManager.log("Invalid event priority ({0}) set at \"settings.chat-event-priority\" in config.yml: only FIRST, EARLY, DEFAULT, LATE and LAST are allowed; setting to default value of LATE.", 2, ConfigurationType.CONFIG.get().getString("settings.chat-event-priority"));
			
			chatEventPriority = "LATE";
		} Sponge.getEventManager().registerListener(instance, MessageChannelEvent.Chat.class, Order.valueOf(ChatManager.getInstance().getChatEventPriority()), listener);
		
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		Sponge.getEventManager().unregisterListeners(listener);
		super.unload();
	}
	
}
