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

package me.remigio07.chatplugin.server.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.event.EventPriority;
import org.spongepowered.api.event.Order;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
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
import me.remigio07.chatplugin.api.server.chat.RangedChatManager;
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
import me.remigio07.chatplugin.bootstrap.Environment;
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
		overrideChatEvent = ConfigurationType.CHAT.get().getBoolean("chat.event.override");
		chatEventPriority = ConfigurationType.CHAT.get().getString("chat.event.priority");
		format = ConfigurationType.CHAT.get().getString("chat.format");
		consoleFormat = ConfigurationType.CHAT.get().getString("chat.console-format");
		recognizedTLDs = ConfigurationType.CHAT.get().getStringList("chat.recognized-tlds").stream().map(String::toLowerCase).collect(Collectors.toCollection(ArrayList::new));
		placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.CHAT.get().getStringList("chat.placeholder-types"));
		
		if (Environment.isBukkit())
			try {
				EventPriority priority = EventPriority.valueOf(ConfigurationType.CHAT.get().getString("chat.event.priority").toUpperCase());
				
				if (priority == EventPriority.MONITOR)
					throw new IllegalArgumentException();
				chatEventPriority = priority.name();
			} catch (IllegalArgumentException e) {
				LogManager.log("Invalid event priority ({0}) set at \"settings.chat-event-priority\" in config.yml: only LOWEST, LOW, NORMAL, HIGH and HIGHEST are allowed; setting to default value of HIGH.", 2, ConfigurationType.CONFIG.get().getString("settings.chat-event-priority"));
				
				chatEventPriority = "HIGH";
			}
		else try {
				Order order = Order.valueOf(ConfigurationType.CHAT.get().getString("chat.event.priority").toUpperCase());
				
				if (Arrays.asList(Order.PRE, Order.AFTER_PRE, Order.BEFORE_POST, Order.POST).contains(order))
					throw new IllegalArgumentException();
				chatEventPriority = order.name();
			} catch (IllegalArgumentException e) {
				LogManager.log("Invalid event priority ({0}) set at \"settings.chat-event-priority\" in config.yml: only FIRST, EARLY, DEFAULT, LATE and LAST are allowed; setting to default value of LATE.", 2, ConfigurationType.CONFIG.get().getString("settings.chat-event-priority"));
				
				chatEventPriority = "LATE";
			}
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = overrideChatEvent = chatMuted = false;
		
		recognizedTLDs.clear();
		placeholderTypes.clear();
		
		chatEventPriority = format = consoleFormat = null;
	}
	
	@Override
	public void setChatMuted(boolean chatMuted) {
		ToggleChatMuteEvent toggleChatMuteEvent = new ToggleChatMuteEvent(isChatMuted());
		
		toggleChatMuteEvent.call();
		
		if (toggleChatMuteEvent.isCancelled())
			return;
		this.chatMuted = chatMuted;
	}
	
	public boolean handleChatEvent(ChatPluginServerPlayer player, String... args) {
		String message = args[0].trim();
		boolean global = false;
		RangedChatManager rangedChatManager = RangedChatManager.getInstance();
		
		// 0. ranged-chat
		if (rangedChatManager.isEnabled()) {
			if (message.startsWith(rangedChatManager.getGlobalModePrefix())
					&& message.length() > rangedChatManager.getGlobalModePrefix().length()
					&& player.hasPermission("chatplugin.global-chat")) {
				message = message.substring(rangedChatManager.getGlobalModePrefix().length()).trim();
				global = true;
			}
		} else global = true;
		
		message = message.replaceAll(" +", " ");
		PreChatEvent preChatEvent = new PreChatEvent(player, message, global);
		
		preChatEvent.call();
		
		if (preChatEvent.isCancelled())
			return true;
		
		// 1. staff-chat
		if (StaffChatManager.getInstance().isEnabled() && player.hasPermission("chatplugin.commands.staffchat") && StaffChatManager.getInstance().isUsingStaffChat(player.getUUID())) {
			StaffChatManager.getInstance().sendPlayerMessage(player, message);
			return true;
		} DenyChatReason<?> reason = null;
		List<String> urls = URLValidator.getURLs(message);
		
		// 2. vanish
		if (player.isVanished())
			reason = DenyChatReason.VANISH;
		
		// 3. mute
		if (reason == null)
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
		
		// 6. blank-message
		if (reason == null && ChatColor.stripColor(message).replaceAll("\\s", "").isEmpty())
			reason = DenyChatReason.BLANK_MESSAGE;
		
		// 7. instant-emojis
		if (reason == null && InstantEmojisManager.getInstance().isEnabled())
			message = InstantEmojisManager.getInstance().translateInstantEmojis(player, message, global);
		
		// 8. player-ping
		if (reason == null && PlayerPingManager.getInstance().isEnabled())
			message = PlayerPingManager.getInstance().performPing(player, message, global);
		
		// denied
		if (reason != null) {
			String denyMessage = reason.getMessage(player.getLanguage());
			
			new DenyChatEvent(player, message, global, reason).call();
			
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
			} if (reason != DenyChatReason.MUTEALL && reason != DenyChatReason.VANISH && reason != DenyChatReason.BLANK_MESSAGE) {
				for (ChatPluginServerPlayer staff : ServerPlayerManager.getInstance().getPlayers().values())
					if (staff.hasPermission("chatplugin.deny-chat-notify"))
						staff.sendTranslatedMessage("chat.deny-chat-notify", player.getName(), reason.name(), message);
				if (ChatLogManager.getInstance().isEnabled())
					ChatLogManager.getInstance().logPublicMessage(player, message, global, reason);
				ChatPlugin.getInstance().sendConsoleMessage(ChatColor.translate(Language.getMainLanguage().getMessage("chat.deny-chat-notify", player.getName(), reason.name(), message)), false);
			} player.sendMessage(denyMessage);
			return true;
		} AllowChatEvent allowChatEvent = new AllowChatEvent(player, message, global);
		
		allowChatEvent.call();
		
		if (allowChatEvent.isCancelled())
			return true;
		if (ChatLogManager.getInstance().isEnabled())
			ChatLogManager.getInstance().logPublicMessage(player, message, global, null);
		if (HoverInfoManager.getInstance().isEnabled()) {
			for (Language language : LanguageManager.getInstance().getLanguages()) {
				TextComponent text = ((BaseHoverInfoManager) HoverInfoManager.getInstance()).getMessageHoverInfo(message, rangedChatManager.isEnabled() && global, urls, player, language);
				
				for (ChatPluginServerPlayer other : language.getOnlinePlayers())
					if (!other.getIgnoredPlayers().contains(player)) {
						if (rangedChatManager.isEnabled() && !global && ((BaseChatPluginServerPlayer) other).getDistance(player.getX(), player.getY(), player.getZ()) > rangedChatManager.getRange()) {
							if (other.hasRangedChatSpyEnabled())
								other.sendMessage(PlaceholderManager.getInstance().translatePlaceholders(rangedChatManager.getSpyFormat(), player, language, placeholderTypes) + message);
							continue;
						} ((BaseChatPluginServerPlayer) other).sendMessage(text);
					}
			}
		} else {
			if (!overrideChatEvent) {
				args[0] = message;
				args[1] = PlaceholderManager.getInstance().translatePlaceholders(format, player, Language.getMainLanguage(), placeholderTypes);
			} else for (Language language : LanguageManager.getInstance().getLanguages()) {
				String text = PlaceholderManager.getInstance().translatePlaceholders(rangedChatManager.isEnabled() && global ? rangedChatManager.getGlobalModeFormat() : format, player, language, placeholderTypes) + message;
				
				for (ChatPluginServerPlayer other : language.getOnlinePlayers())
					if (!other.getIgnoredPlayers().contains(player)) {
						if (rangedChatManager.isEnabled() && !global && ((BaseChatPluginServerPlayer) other).getDistance(player.getX(), player.getY(), player.getZ()) > rangedChatManager.getRange()) {
							if (other.hasRangedChatSpyEnabled())
								other.sendMessage(PlaceholderManager.getInstance().translatePlaceholders(rangedChatManager.getSpyFormat(), player, language, placeholderTypes) + message);
							continue;
						} other.sendMessage(text);
					}
			}
		} try {
			StorageConnector.getInstance().incrementPlayerStat(PlayersDataType.MESSAGES_SENT, player);
		} catch (Exception e) {
			LogManager.log("{0} occurred while incrementing messages sent stat for {1}: {2}", 2, e.getClass().getSimpleName(), player.getName(), e.getMessage());
		} logMessage(ChatColor.translate(PlaceholderManager.getInstance().translatePlaceholders(consoleFormat, player, placeholderTypes)) + message);
		
		if (IntegrationType.DISCORDSRV.isEnabled())
			IntegrationType.DISCORDSRV.get().handleChatEvent(player, message);
		return false;
	}
	
	private void logMessage(String message) {
		if (!ChatLogManager.getInstance().isEnabled() || ChatLogManager.getInstance().isPrintToLogFile())
			LogManager.getInstance().writeToFile(ChatColor.stripColor(message));
		if (overrideChatEvent)
			ChatPlugin.getInstance().sendConsoleMessage(message, false);
	}
	
}
