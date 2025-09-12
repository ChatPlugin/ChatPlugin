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

package me.remigio07.chatplugin.server.sponge.manager;

import java.util.Arrays;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.bootstrap.SpongeBootstrapper;
import me.remigio07.chatplugin.server.chat.BaseChatManager;

public class SpongeChatManager extends BaseChatManager {
	
	@SuppressWarnings("unchecked")
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
		} catch (IllegalArgumentException iae) {
			LogManager.log("Invalid event priority ({0}) set at \"settings.chat-event-priority\" in config.yml: only FIRST, EARLY, DEFAULT, LATE and LAST are allowed; setting to default value of LATE.", 2, ConfigurationType.CONFIG.get().getString("settings.chat-event-priority"));
			
			chatEventPriority = "LATE";
		} Sponge.getEventManager().registerListener(SpongeBootstrapper.getInstance(), MessageChannelEvent.Chat.class, Order.valueOf(ChatManager.getInstance().getChatEventPriority()), (EventListener<Event>) EventManager.getInstance());
		
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
}
