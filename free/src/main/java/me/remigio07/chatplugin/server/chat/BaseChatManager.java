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

package me.remigio07.chatplugin.server.chat;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.FormattedChatManager;
import me.remigio07.chatplugin.api.server.chat.HoverInfoManager;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager.InstantEmoji;
import me.remigio07.chatplugin.api.server.chat.PlayerPingManager;
import me.remigio07.chatplugin.api.server.chat.StaffChatManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamResult;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;
import me.remigio07.chatplugin.api.server.chat.log.ChatLogManager;
import me.remigio07.chatplugin.api.server.event.chat.AllowChatEvent;
import me.remigio07.chatplugin.api.server.event.chat.DenyChatEvent;
import me.remigio07.chatplugin.api.server.event.chat.PreChatEvent;
import me.remigio07.chatplugin.api.server.event.chat.ToggleChatMuteEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.URLValidator;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.chat.antispam.AntispamManagerImpl;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import net.md_5.bungee.api.chat.BaseComponent;

public abstract class BaseChatManager extends ChatManager {
	
	protected boolean load0() throws ChatPluginManagerException {
		if (!ConfigurationType.CHAT.get().getBoolean("chat.enabled"))
			return false;
		overrideChatEvent = ConfigurationType.CHAT.get().getBoolean("chat.event.override");
		chatEventPriority = ConfigurationType.CHAT.get().getString("chat.event.priority");
		format = ConfigurationType.CHAT.get().getString("chat.format");
		recognizedTLDs = ConfigurationType.CHAT.get().getStringList("chat.recognized-tlds").stream().map(String::toLowerCase).collect(Collectors.toCollection(ArrayList::new));
		placeholderTypes = PlaceholderType.getTypes(ConfigurationType.CHAT.get().getStringList("chat.placeholder-types"));
		return true;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = overrideChatEvent = chatMuted = false;
		
		recognizedTLDs.clear();
		placeholderTypes.clear();
		
		chatEventPriority = format = null;
	}
	
	@Override
	public void setChatMuted(boolean chatMuted) {
		ToggleChatMuteEvent toggleChatMuteEvent = new ToggleChatMuteEvent(isChatMuted());
		
		toggleChatMuteEvent.call();
		
		if (toggleChatMuteEvent.isCancelled())
			return;
		this.chatMuted = chatMuted;
	}
	
	public boolean handleChatEvent(ChatPluginServerPlayer player, String... args) { // *chaos starts*
		String message = args[0].trim().replaceAll("\\s+", " ");
		ChatChannel<?> channel = player.getWritingChannel();
		
		if (channel != null)
			for (ChatChannel<?> other : player.getChannels()) {
				String prefix = other.getPrefix();
				
				if (prefix != null && message.startsWith(prefix) && message.length() > prefix.length() && other.canWrite(player)) {
					message = message.substring(prefix.length()).trim();
					channel = other;
					break;
				}
			}
		
		PreChatEvent preChatEvent = new PreChatEvent(player, message, channel);
		
		preChatEvent.call();
		
		if (preChatEvent.isCancelled())
			return true;
		
		// 1. staff chat
		if (StaffChatManager.getInstance().isEnabled() && player.hasPermission("chatplugin.commands.staffchat") && StaffChatManager.getInstance().isUsingStaffChat(player.getUUID())) {
			StaffChatManager.getInstance().sendPlayerMessage(player, message);
			return true;
		} DenyChatReason<?> denyChatReason = null;
		AntispamResult antispamResult = null;
		
		// 2. vanish
		if (player.isVanished())
			denyChatReason = DenyChatReason.VANISH;
		
		// 3. mute(all)
		if (denyChatReason == null)
			if (chatMuted && !player.hasPermission("chatplugin.commands.muteall"))
				denyChatReason = DenyChatReason.MUTEALL;
			else if (MuteManager.getInstance().isEnabled() && MuteManager.getInstance().isMuted(player, ProxyManager.getInstance().getServerID()))
				denyChatReason = DenyChatReason.MUTE;
		
		String stripColor = ChatColor.stripColor(message);
		
		// 4. antispam
		if (denyChatReason == null && AntispamManager.getInstance().isEnabled()) {
			antispamResult = AntispamManager.getInstance().check(player, stripColor, Collections.emptyList());
			denyChatReason = antispamResult.getReason();
		} List<String> urls = URLValidator.getURLs(stripColor);
		
		// 5. formatted chat
		if (denyChatReason == null && FormattedChatManager.getInstance().isEnabled() && FormattedChatManager.getInstance().containsFormattedText(stripColor, urls, true)) {
			if (!player.hasPermission("chatplugin.formatted-chat"))
				if (!FormattedChatManager.getInstance().isSendAnyway())
					denyChatReason = DenyChatReason.FORMAT;
				else player.sendTranslatedMessage("chat.no-format");
			else message = FormattedChatManager.getInstance().translate(message, urls, true);
		}
		
		// 6. blank message
		if (denyChatReason == null && ChatColor.stripColor(message).replace(" ", "").isEmpty())
			denyChatReason = DenyChatReason.BLANK_MESSAGE;
		List<InstantEmoji> instantEmojis = Collections.emptyList();
		List<ChatPluginServerPlayer> pingedPlayers = Collections.emptyList();
		
		if (denyChatReason == null) {
			// 7. chat color
			message = (player.getChatColor().equals(ChatColor.RESET) ? ChatColor.translate(player.getRank().getChatColor()) : player.getChatColor().toString()) + message;
			
			// 8. instant emojis
			if (InstantEmojisManager.getInstance().isEnabled())
				message = InstantEmojisManager.getInstance().translateInstantEmojis(player, message, channel, instantEmojis = InstantEmojisManager.getInstance().getInstantEmojis(player, message));
			
			// 9. player ping
			if (PlayerPingManager.getInstance().isEnabled())
				message = PlayerPingManager.getInstance().performPing(player, message, channel, pingedPlayers = PlayerPingManager.getInstance().getPingedPlayers(player, message, channel));
		} else { // denied
			String denyMessage = denyChatReason.getMessage(player.getLanguage());
			
			new DenyChatEvent(player, message, channel, denyChatReason, antispamResult).call();
			
			switch (denyChatReason.name()) {
			case "CAPS":
				denyMessage = Utils.replaceNumericPlaceholders(denyMessage, Utils.truncate(AntispamManager.getInstance().getMaxCapsPercentage(), 2), AntispamManager.getInstance().getMaxCapsLength());
				break;
			case "FLOOD":
				denyMessage = Utils.replaceNumericPlaceholders(denyMessage, AntispamManager.getInstance().getSecondsBetweenMessages());
				break;
			case "MUTE":
				denyMessage = MuteManager.getInstance().getActiveMute(player, ProxyManager.getInstance().getServerID()).formatPlaceholders(denyMessage, player.getLanguage());
				break;
			case "SPAM":
				denyMessage = Utils.replaceNumericPlaceholders(denyMessage, AntispamManager.getInstance().getSecondsBetweenSameMessages());
				break;
			default:
				break;
			} if (denyChatReason != DenyChatReason.MUTEALL && denyChatReason != DenyChatReason.VANISH && denyChatReason != DenyChatReason.BLANK_MESSAGE) {
				if (ChatLogManager.getInstance().isEnabled())
					ChatLogManager.getInstance().logPublicMessage(player, message, channel, denyChatReason);
				if (denyChatReason.getHandlerClass() == AntispamManager.class)
					((AntispamManagerImpl) AntispamManager.getInstance()).sendNotification(player, antispamResult);
			} player.sendMessage(denyMessage);
			
			try {
				StorageConnector.getInstance().incrementPlayerStat(PlayersDataType.ANTISPAM_INFRACTIONS, player);
			} catch (SQLException | IOException e) {
				LogManager.log("{0} occurred while incrementing antispam infractions stat for {1}: {2}", 2, e.getClass().getSimpleName(), player.getName(), e.getLocalizedMessage());
			} return true;
		} AllowChatEvent allowChatEvent = new AllowChatEvent(player, message, channel);
		
		allowChatEvent.call();
		
		if (allowChatEvent.isCancelled())
			return true;
		if (ChatLogManager.getInstance().isEnabled())
			ChatLogManager.getInstance().logPublicMessage(player, message, channel, null);
		List<ChatPluginServerPlayer> recipients = channel == null ? null : channel.getRecipients(player, false);
		String format = channel == null ? this.format : channel.getFormat();
		boolean somebodyRead = channel == null || channel.getType().isProxyWide();
		
		if (HoverInfoManager.getInstance().isEnabled()) {
			for (Language language : LanguageManager.getInstance().getLanguages()) {
				BaseComponent text = ((BaseHoverInfoManager) HoverInfoManager.getInstance()).getMessageHoverInfo(player, language, message, channel, urls, pingedPlayers, new HashSet<>(instantEmojis));
				
				for (ChatPluginServerPlayer other : language.getOnlinePlayers()) {
					if (!other.getIgnoredPlayers().contains(player)) {
						if (channel != null && !recipients.contains(other)) {
							if (other.hasChatChannelSpyEnabled())
								other.sendMessage(PlaceholderManager.getInstance().translatePlaceholders(ChatChannelsManager.getInstance().getSpyFormat(), player, language, placeholderTypes) + message);
							continue;
						} if (other != player && (!other.isVanished() || player.hasPermission(VanishManager.VANISH_PERMISSION)))
							somebodyRead = true;
						((BaseChatPluginServerPlayer) other).sendMessage(text);
					}
				} if (channel != null && channel.getType().isProxyWide() && ProxyManager.getInstance().isEnabled())
					ProxyManager.getInstance().sendPluginMessage(Packets.Messages.chatChannelMessage(
							"ALL",
							ProxyManager.getInstance().getServerID(),
							player.getUUID(),
							player.getName(),
							channel.getID(),
							language.getID(),
							true,
							Utils.toJSON(text).toString() // FIXME this will not work if the sender and receiver servers are on incompatible versions
							));
			}
		} else {
			if (!overrideChatEvent) {
				args[0] = message;
				args[1] = PlaceholderManager.getInstance().translatePlaceholders(format, player, Language.getMainLanguage(), placeholderTypes);
			} else for (Language language : LanguageManager.getInstance().getLanguages()) {
				String text = PlaceholderManager.getInstance().translatePlaceholders(format, player, language, placeholderTypes) + message;
				
				for (ChatPluginServerPlayer other : language.getOnlinePlayers()) {
					if (!other.getIgnoredPlayers().contains(player)) {
						if (channel != null && !recipients.contains(other)) {
							if (other.hasChatChannelSpyEnabled())
								other.sendMessage(PlaceholderManager.getInstance().translatePlaceholders(ChatChannelsManager.getInstance().getSpyFormat(), player, language, placeholderTypes) + message);
							continue;
						} if (other != player && (!other.isVanished() || player.hasPermission(VanishManager.VANISH_PERMISSION)))
							somebodyRead = true;
						other.sendMessage(text);
					}
				} if (channel != null && channel.getType().isProxyWide() && ProxyManager.getInstance().isEnabled())
					ProxyManager.getInstance().sendPluginMessage(Packets.Messages.chatChannelMessage(
							"ALL",
							ProxyManager.getInstance().getServerID(),
							player.getUUID(),
							player.getName(),
							channel.getID(),
							language.getID(),
							false,
							text
							));
			}
		} if (!somebodyRead && ChatChannelsManager.getInstance().isReadingNotificationEnabled())
			player.sendTranslatedMessage("chat.nobody-read");
		try {
			StorageConnector.getInstance().incrementPlayerStat(PlayersDataType.MESSAGES_SENT, player);
		} catch (SQLException | IOException e) {
			LogManager.log("{0} occurred while incrementing messages sent stat for {1}: {2}", 2, e.getClass().getSimpleName(), player.getName(), e.getLocalizedMessage());
		} logMessage(PlaceholderManager.getInstance().translatePlaceholders(format, player, placeholderTypes) + message, channel);
		
		if (overrideChatEvent && IntegrationType.DISCORDSRV.isEnabled() && (channel == null || (channel.getType().ordinal() > 1 && !channel.isAccessRestricted()))) // this will be changed with the introduction of DISCORD-type channels
			IntegrationType.DISCORDSRV.get().handleChatEvent(player, message);
		return false;
	}
	
	private void logMessage(String message, ChatChannel<?> channel) {
		if (!ChatLogManager.getInstance().isEnabled() || ChatLogManager.getInstance().isPrintToLogFile())
			LogManager.getInstance().writeToFile(ChatColor.stripColor(message));
		if (overrideChatEvent && (channel == null || channel.isConsoleIncluded()))
			ChatPlugin.getInstance().sendConsoleMessage(message, false);
	}
	
}
