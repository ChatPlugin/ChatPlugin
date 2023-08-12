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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.server.bukkit.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;

import io.netty.util.internal.ThreadLocalRandom;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.server.event.f3servername.F3ServerNameSendEvent;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerName;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.common.f3servername.F3ServerNamePacket;
import me.remigio07.chatplugin.server.f3servername.BaseF3ServerNameManager;

public class BukkitF3ServerNameManager extends BaseF3ServerNameManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		super.load();
		try {
			Messenger messenger = Bukkit.getMessenger();
			Method method = messenger.getClass().getDeclaredMethod("addToOutgoing", new Class[] { Plugin.class, String.class });
			
			method.setAccessible(true);
			method.invoke(messenger, new Object[] { BukkitBootstrapper.getInstance(), CHANNEL_ID });
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new ChatPluginManagerException(this, e);
		} timerTaskID = TaskManager.scheduleAsync(this, 0L, sendingTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void run() {
		if (!enabled)
			return;
		if (!enabled)
			return;
		switch (f3ServerNames.size()) {
		case 0:
			return;
		case 1:
			timerIndex = 0;
			break;
		default:
			if (randomOrder) {
				int randomIndex = timerIndex;
				
				while (randomIndex == timerIndex)
					randomIndex = ThreadLocalRandom.current().nextInt(f3ServerNames.size());
				timerIndex = randomIndex;
			} else if (timerIndex + 1 == f3ServerNames.size())
				timerIndex = 0;
			else timerIndex++;
			break;
		} F3ServerName f3ServerName = f3ServerNames.get(timerIndex);
		
		for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
			sendF3ServerName(f3ServerName, player);
	}
	
	@Override
	public void sendF3ServerName(F3ServerName f3ServerName, ChatPluginServerPlayer player) {
		if (!enabled)
			return;
		F3ServerNameSendEvent event = new F3ServerNameSendEvent(f3ServerName, player);
		
		event.call();
		
		if (event.isCancelled())
			return;
		if (ProxyManager.getInstance().isEnabled())
			ProxyManager.getInstance().sendPluginMessage(Packets.Messages.f3ServerName(
					ProxyManager.getInstance().getServerID(),
					player.getUUID(),
					PlaceholderManager.getInstance().translatePlaceholders(f3ServerName.getText(player.getLanguage(), true), player, placeholderTypes)
					));
		else player.toAdapter().bukkitValue().sendPluginMessage(
				BukkitBootstrapper.getInstance(),
				CHANNEL_ID,
				new F3ServerNamePacket(PlaceholderManager.getInstance().translatePlaceholders(f3ServerName.getText(player.getLanguage(), true), player, placeholderTypes))
				.toArray()
				);
	}
	
}
