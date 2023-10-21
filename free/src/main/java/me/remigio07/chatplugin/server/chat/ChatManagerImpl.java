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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.FormattedChatManager;
import me.remigio07.chatplugin.api.server.chat.HoverInfoManager;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager;
import me.remigio07.chatplugin.api.server.chat.PlayerPingManager;
import me.remigio07.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.chat.log.ChatLogManager;
import me.remigio07.chatplugin.api.server.event.chat.AllowChatEvent;
import me.remigio07.chatplugin.api.server.event.chat.DenyChatEvent;
import me.remigio07.chatplugin.api.server.event.chat.PreChatEvent;
import me.remigio07.chatplugin.api.server.event.chat.ToggleChatMuteEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.URLValidator;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import net.kyori.adventure.text.TextComponent;

public class ChatManagerImpl extends ChatManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.CHAT.get().getBoolean("chat.enabled"))
			return;
		format = ConfigurationType.CHAT.get().getString("chat.format");
		consoleFormat = ConfigurationType.CHAT.get().getString("chat.console-format");
		recognizedTLDs = ConfigurationType.CHAT.get().getStringList("chat.recognized-tlds").stream().map(String::toLowerCase).collect(Collectors.toList());
		placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.CHAT.get().getStringList("chat.placeholder-types"));
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = chatMuted = false;
		
		recognizedTLDs.clear();
		placeholderTypes.clear();
		
		format = consoleFormat = null;
	}
	
	@Override
	public void setChatMuted(boolean chatMuted) {
		ToggleChatMuteEvent toggleChatMuteEvent = new ToggleChatMuteEvent(isChatMuted());
		
		toggleChatMuteEvent.call();
		
		if (toggleChatMuteEvent.isCancelled())
			return;
		this.chatMuted = chatMuted;
	}
	
	@Override
	public void handleChatEvent(ChatPluginServerPlayer player, String message) {
		message = message.trim().replaceAll(" +", " ");
		PreChatEvent preChatEvent = new PreChatEvent(player, message);
		
		preChatEvent.call();
		
		if (preChatEvent.isCancelled())
			return;
		
		// 0. instant-emojis
		if (InstantEmojisManager.getInstance().isEnabled())
			message = InstantEmojisManager.getInstance().format(message);
		
		// 1. staff-chat
		if (StaffChatManager.getInstance().isEnabled() && player.hasPermission("chatplugin.commands.staffchat") && StaffChatManager.getInstance().isUsingStaffChat(player.getUUID())) {
			StaffChatManager.getInstance().sendPlayerMessage(player, message);
			return;
		} DenyChatReason<?> reason = null;
		List<String> urls = URLValidator.getURLs(message);
		
		// 2. vanish
		if (player.isVanished())
			reason = DenyChatReason.VANISH;
		
		// 3. mute
		if (chatMuted && !player.hasPermission("chatplugin.commands.muteall"))
			reason = DenyChatReason.MUTEALL;
		else if (MuteManager.getInstance().isEnabled() && MuteManager.getInstance().isMuted(player, ProxyManager.getInstance().getServerID()))
			reason = DenyChatReason.MUTE;
		
		// 4. antispam
		if (reason == null && AntispamManager.getInstance().isEnabled())
			reason = AntispamManager.getInstance().getDenyChatReason(player, message, Collections.emptyList());
		
		// 5. formatted-chat
		if (reason == null && FormattedChatManager.getInstance().isEnabled() && FormattedChatManager.getInstance().containsFormattedText(message, urls, true)) {
			if (!player.hasPermission("chatplugin.formatted-chat"))
				if (!FormattedChatManager.getInstance().isSendAnyway())
					reason = DenyChatReason.FORMAT;
				else player.sendTranslatedMessage("chat.no-format");
			else message = FormattedChatManager.getInstance().translate(message, urls, true);
		}
		
		// 6. player-ping
		if (reason == null && PlayerPingManager.getInstance().isEnabled() && !PlayerPingManager.getInstance().getPingedPlayers(player, message).isEmpty() && player.hasPermission("chatplugin.player-ping"))
			message = PlayerPingManager.getInstance().performPing(player, message);
		
		// denied
		if (reason != null) {
			String denyMessage = reason.getMessage(player.getLanguage());
			
			new DenyChatEvent(player, message, reason).call();
			
			switch (reason.name()) {
			case "CAPS":
				denyMessage = Utils.numericPlaceholders(denyMessage, AntispamManager.getInstance().getMaxCapsPercent(), AntispamManager.getInstance().getMaxCapsLength());
				break;
			case "FLOOD":
				denyMessage = Utils.numericPlaceholders(denyMessage, AntispamManager.getInstance().getSecondsBetweenMsg());
				break;
			case "MUTE":
				denyMessage = MuteManager.getInstance().getActiveMute(player, ProxyManager.getInstance().getServerID()).formatPlaceholders(denyMessage, player.getLanguage());
				break;
			case "SPAM":
				denyMessage = Utils.numericPlaceholders(denyMessage, AntispamManager.getInstance().getSecondsBetweenSameMsg());
				break;
			default:
				break;
			} if (reason != DenyChatReason.MUTEALL && reason != DenyChatReason.VANISH) {
				for (ChatPluginServerPlayer staff : ServerPlayerManager.getInstance().getPlayers().values())
					if (staff.hasPermission("chatplugin.deny-chat-notify"))
						staff.sendTranslatedMessage("chat.deny-chat-notify", player.getName(), reason.name(), message);
				ChatPlugin.getInstance().sendConsoleMessage(ChatColor.translate(Language.getMainLanguage().getMessage("chat.deny-chat-notify", player.getName(), reason.name(), message)), false);
			} player.sendMessage(denyMessage);
			
			if (ChatLogManager.getInstance().isEnabled())
				ChatLogManager.getInstance().logChatMessage(player, message, reason);
			return;
		} AllowChatEvent allowChatEvent = new AllowChatEvent(player, message);
		
		allowChatEvent.call();
		
		if (allowChatEvent.isCancelled())
			return;
		if (ChatLogManager.getInstance().isEnabled())
			ChatLogManager.getInstance().logChatMessage(player, message, null);
		if (HoverInfoManager.getInstance().isEnabled()) {
			for (Language language : LanguageManager.getInstance().getLanguages()) {
				TextComponent text = ((BaseHoverInfoManager) HoverInfoManager.getInstance()).getMessageHoverInfo(message, urls, player, language);
				
				for (ChatPluginServerPlayer other : language.getOnlinePlayers())
					if (!other.getIgnoredPlayers().contains(player))
						((BaseChatPluginServerPlayer) other).sendMessage(text);
			}
		} else for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
			if (!other.getIgnoredPlayers().contains(player))
				other.sendMessage(PlaceholderManager.getInstance().translatePlaceholders(format, player, other.getLanguage(), placeholderTypes) + message);
		} try {
			StorageConnector.getInstance().incrementPlayerStat(PlayersDataType.MESSAGES_SENT, player);
		} catch (Exception e) {
			LogManager.log("{0} occurred while incrementing messages sent stat for {1}: {2}", 2, e.getClass().getSimpleName(), player.getName(), e.getMessage());
		} logMessage(ChatColor.translate(PlaceholderManager.getInstance().translatePlaceholders(consoleFormat, player, placeholderTypes)) + message);
	}
	
	private static void logMessage(String message) {
		if (!ChatLogManager.getInstance().isEnabled() || ChatLogManager.getInstance().isPrintToLogFile())
			LogManager.getInstance().writeToFile(ChatColor.stripColor(message));
		ChatPlugin.getInstance().sendConsoleMessage(message, false);
	}
	
}
