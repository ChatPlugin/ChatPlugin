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

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07.chatplugin.api.server.event.chat.StaffChatEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;

public class StaffChatManagerImpl extends StaffChatManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.staff-chat.enabled"))
			return;
		playerFormat = ConfigurationType.CHAT.get().getString("chat.staff-chat.format.player");
		consoleFormat = ConfigurationType.CHAT.get().getString("chat.staff-chat.format.console");
		placeholderTypes = PlaceholderType.getTypes(ConfigurationType.CHAT.get().getStringList("chat.staff-chat.placeholder-types"));
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		players.clear();
		placeholderTypes.clear();
		
		playerFormat = consoleFormat = null;
	}
	
	@Override
	public void sendPlayerMessage(ChatPluginServerPlayer player, String message) {
		StaffChatEvent staffChatEvent = new StaffChatEvent(player, message);
		
		staffChatEvent.call();
		
		if (staffChatEvent.isCancelled())
			return;
		if (!ProxyManager.getInstance().isEnabled()) {
			for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values())
				if (other.hasPermission("chatplugin.commands.staffchat"))
					other.sendMessage(ChatColor.translate(
							PlaceholderManager.getInstance().translatePlaceholders(
									playerFormat,
									player,
									player.getLanguage(),
									placeholderTypes,
									false
									) + message
							));
			ChatPlugin.getInstance().sendConsoleMessage(ChatColor.translate(
					PlaceholderManager.getInstance().translatePlaceholders(
							playerFormat,
							player,
							player.getLanguage(),
							placeholderTypes,
							false
							) + message
					), false);
		} else ProxyManager.getInstance().sendPluginMessage(Packets.Messages.playerMessage(
				"ALL",
				"ALL LOADED",
				"chatplugin.commands.staffchat",
				true,
				false,
				ChatColor.translate(PlaceholderManager.getInstance().translatePlaceholders(
						playerFormat,
						player,
						player.getLanguage(),
						placeholderTypes,
						false
						) + message)
				));
	}
	
	@Override
	public void sendConsoleMessage(String message) {
		if (ProxyManager.getInstance().isEnabled() && PlayerAdapter.getOnlinePlayers().isEmpty())
			throw new IllegalStateException("Unable send PlayerMessage plugin message with no players online");
		StaffChatEvent staffChatEvent = new StaffChatEvent(null, message);
		
		staffChatEvent.call();
		
		if (staffChatEvent.isCancelled())
			return;
		if (!ProxyManager.getInstance().isEnabled()) {
			for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
				if (player.hasPermission("chatplugin.commands.staffchat"))
					player.sendMessage(ChatColor.translate(
							PlaceholderManager.getInstance().translateServerPlaceholders(
									consoleFormat,
									Language.getMainLanguage(),
									false
									) + message
							));
			ChatPlugin.getInstance().sendConsoleMessage(ChatColor.translate(
					PlaceholderManager.getInstance().translateServerPlaceholders(
							consoleFormat,
							Language.getMainLanguage(),
							false
							) + message
					), false);
		} else ProxyManager.getInstance().sendPluginMessage(Packets.Messages.playerMessage(
				"ALL",
				"ALL LOADED",
				"chatplugin.commands.staffchat",
				true,
				false,
				ChatColor.translate(PlaceholderManager.getInstance().translateServerPlaceholders(
						consoleFormat,
						Language.getMainLanguage(),
						false
						) + message)
				));
	}
	
}
