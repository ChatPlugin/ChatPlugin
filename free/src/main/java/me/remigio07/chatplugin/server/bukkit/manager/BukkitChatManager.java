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

package me.remigio07.chatplugin.server.bukkit.manager;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.server.chat.BaseChatManager;

public class BukkitChatManager extends BaseChatManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!load0())
			return;
		try {
			EventPriority priority = EventPriority.valueOf(ConfigurationType.CHAT.get().getString("chat.event.priority").toUpperCase());
			
			if (priority == EventPriority.MONITOR)
				throw new IllegalArgumentException();
			chatEventPriority = priority.name(); // ensure upper case
		} catch (IllegalArgumentException iae) {
			LogManager.log("Invalid event priority ({0}) set at \"settings.chat-event-priority\" in config.yml: only LOWEST, LOW, NORMAL, HIGH and HIGHEST are allowed; setting to default value of HIGH.", 2, ConfigurationType.CONFIG.get().getString("settings.chat-event-priority"));
			
			chatEventPriority = "HIGH";
		} Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, (Listener) EventManager.getInstance(), EventPriority.valueOf(chatEventPriority), (EventExecutor) EventManager.getInstance(), BukkitBootstrapper.getInstance());
		
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
}
