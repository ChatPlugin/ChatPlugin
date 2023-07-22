/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.server.chat;

import me.remigio07_.chatplugin.api.ChatPlugin;
import me.remigio07_.chatplugin.api.common.player.ChatPluginPlayer;
import me.remigio07_.chatplugin.api.common.player.PlayerManager;
import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.common.util.packet.PacketSerializer;
import me.remigio07_.chatplugin.api.common.util.text.ChatColor;
import me.remigio07_.chatplugin.api.server.chat.ChatManager;
import me.remigio07_.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07_.chatplugin.api.server.event.chat.StaffChatEvent;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.PlaceholderType;
import me.remigio07_.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;

public class StaffChatManagerImpl extends StaffChatManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.staff-chat.enabled"))
			return;
		playerChatFormat = ConfigurationType.CHAT.get().getString("chat.staff-chat.format.player.chat");
		playerTerminalFormat = ConfigurationType.CHAT.get().getString("chat.staff-chat.format.player.terminal");
		consoleChatFormat = ConfigurationType.CHAT.get().getString("chat.staff-chat.format.console.chat");
		consoleTerminalFormat = ConfigurationType.CHAT.get().getString("chat.staff-chat.format.console.terminal");
		placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.CHAT.get().getStringList("chat.staff-chat.placeholder-types"));
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		players.clear();
		placeholderTypes.clear();
		
		playerChatFormat = playerTerminalFormat = consoleChatFormat = consoleTerminalFormat = null;
	}
	
	@Override
	public void sendPlayerMessage(ChatPluginServerPlayer player, String message) {
		StaffChatEvent staffChatEvent = new StaffChatEvent(player, message);
		
		staffChatEvent.call();
		
		if (staffChatEvent.isCancelled())
			return;
		if (ProxyManager.getInstance().isEnabled()) {
			ProxyManager.getInstance().sendPluginMessage(
					new PacketSerializer("PlayerMessage")
					.writeUTF("ALL")
					.writeUTF("ALL ENABLED")
					.writeUTF("chatplugin.commands.staffchat")
					.writeBoolean(false)
					.writeUTF(
							ChatColor.translate(
									PlaceholderManager.getInstance().translatePlaceholders(
											playerChatFormat,
											player,
											placeholderTypes
											) + message
									)
							)
					);
			ProxyManager.getInstance().sendPluginMessage(
					new PacketSerializer("PlayerMessage")
					.writeUTF("ALL")
					.writeUTF("CONSOLE")
					.writeUTF("")
					.writeBoolean(false)
					.writeUTF(
							ChatColor.translate(
									PlaceholderManager.getInstance().translatePlaceholders(
											playerTerminalFormat,
											player,
											placeholderTypes
											) + message
									)
							)
					);
		} else {
			for (ChatPluginPlayer other : PlayerManager.getInstance().getPlayers().values())
				other.sendMessage(
						ChatColor.translate(
								PlaceholderManager.getInstance().translatePlaceholders(
										playerChatFormat,
										player,
										placeholderTypes
										) + message
								)
						);
			ChatPlugin.getInstance().sendConsoleMessage(
					ChatColor.translate(
							PlaceholderManager.getInstance().translatePlaceholders(
									playerTerminalFormat,
									player,
									placeholderTypes
									) + message
							),
					false
					);
		}
	}
	
	@Override
	public void sendConsoleMessage(String message) {
		StaffChatEvent staffChatEvent = new StaffChatEvent(null, message);
		
		staffChatEvent.call();
		
		if (staffChatEvent.isCancelled())
			return;
		if (ProxyManager.getInstance().isEnabled()) {
			if (PlayerAdapter.getOnlinePlayers().size() == 0)
				throw new IllegalStateException("Unable send PlayerMessage plugin message with no players online");
			ProxyManager.getInstance().sendPluginMessage(
					new PacketSerializer("PlayerMessage")
					.writeUTF("ALL")
					.writeUTF("ALL ENABLED")
					.writeUTF("chatplugin.commands.staffchat")
					.writeBoolean(false)
					.writeUTF(
							ChatColor.translate(
									PlaceholderManager.getInstance().translateServerPlaceholders(
											consoleChatFormat,
											Language.getMainLanguage()
											) + message
									)
							)
					);
			ProxyManager.getInstance().sendPluginMessage(
					new PacketSerializer("PlayerMessage")
					.writeUTF("ALL")
					.writeUTF("CONSOLE")
					.writeUTF("")
					.writeBoolean(false)
					.writeUTF(
							ChatColor.translate(
									PlaceholderManager.getInstance().translateServerPlaceholders(
											consoleTerminalFormat,
											Language.getMainLanguage()
											) + message
									)
							)
					);
		} else {
			for (ChatPluginPlayer player : PlayerManager.getInstance().getPlayers().values())
				player.sendMessage(
						ChatColor.translate(
								PlaceholderManager.getInstance().translateServerPlaceholders(
										consoleChatFormat,
										Language.getMainLanguage()
										) + message
								)
						);
			ChatPlugin.getInstance().sendConsoleMessage(
					ChatColor.translate(
							PlaceholderManager.getInstance().translateServerPlaceholders(
									consoleTerminalFormat,
									Language.getMainLanguage()
									) + message
							),
					false
					);
		}
	}
	
}
