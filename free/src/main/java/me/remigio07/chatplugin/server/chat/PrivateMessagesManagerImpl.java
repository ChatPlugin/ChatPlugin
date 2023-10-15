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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.FormattedChatManager;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.chat.log.ChatLogManager;
import me.remigio07.chatplugin.api.server.event.chat.AllowPrivateMessageEvent;
import me.remigio07.chatplugin.api.server.event.chat.DenyPrivateMessageEvent;
import me.remigio07.chatplugin.api.server.event.chat.PrePrivateMessageEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.URLValidator;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;

public class PrivateMessagesManagerImpl extends PrivateMessagesManager {
	
	@SuppressWarnings("unchecked")
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.private-messages.enabled"))
			return;
		sentChatFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.sent.chat");
		sentTerminalFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.sent.terminal");
		receivedChatFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.received.chat");
		receivedTerminalFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.received.terminal");
		socialspyChatFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.socialspy.chat");
		socialspyTerminalFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.socialspy.terminal");
		soundEnabled = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.sound.enabled");
		sound = new SoundAdapter(ConfigurationType.CHAT.get(), "chat.private-messages.sound");
		socialspyOnJoinEnabled = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.socialspy-on-join-enabled");
		bypassAntispamChecks = new ArrayList<>();
		
		for (DenyChatReason<?> check : ConfigurationType.CHAT.get().getStringList("chat.private-messages.bypass-antispam-checks").stream().map(DenyChatReason::valueOf).filter(Objects::nonNull).collect(Collectors.toList()))
			if (check.getHandlerClass() == AntispamManager.class)
				bypassAntispamChecks.add((DenyChatReason<AntispamManager>) check);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = soundEnabled = socialspyOnJoinEnabled = false;
		
		bypassAntispamChecks.clear();
		
		sentChatFormat = sentTerminalFormat = receivedChatFormat = receivedTerminalFormat = socialspyChatFormat = socialspyTerminalFormat = null;
		sound = null;
	}
	
	@Override
	public void sendMessage(@Nullable(why = "Null to represent the console") ChatPluginServerPlayer sender, @Nullable(why = "Null to represent the console") ChatPluginServerPlayer recipient, String message) {
		if (!enabled)
			return;
		if ((sender == null && recipient == null) || (sender != null && sender.equals(recipient)))
			throw new IllegalArgumentException("The sender and the recipient correspond");
		message = message.trim().replaceAll(" +", " ");
		PrePrivateMessageEvent prePrivateMessageEvent = new PrePrivateMessageEvent(sender, recipient, message);
		
		prePrivateMessageEvent.call();
		
		if (prePrivateMessageEvent.isCancelled())
			return;
		
		// 0. instant emojis
		if (InstantEmojisManager.getInstance().isEnabled())
			message = InstantEmojisManager.getInstance().format(message);
		DenyChatReason<?> reason = null;
		
		// 1. antispam
		if (sender != null && AntispamManager.getInstance().isEnabled())
			reason = AntispamManager.getInstance().getDenyChatReason(sender, message, bypassAntispamChecks);
		
		// 2. formatted-chat
		if (reason == null && FormattedChatManager.getInstance().isEnabled()) {
			List<String> urls = URLValidator.getURLs(message);
			
			if (FormattedChatManager.getInstance().containsFormattedText(message, urls, true)) {
				if (sender != null && !sender.hasPermission("chatplugin.formatted-chat"))
					if (!FormattedChatManager.getInstance().isSendAnyway())
						reason = DenyChatReason.FORMAT;
					else sender.sendTranslatedMessage("chat.no-format");
				else message = FormattedChatManager.getInstance().translate(message, urls, true);
			}
		}
		
		// denied
		if (reason != null) {
			String denyMessage = reason.getMessage(sender.getLanguage());
			
			new DenyPrivateMessageEvent(sender, recipient, message, reason).call();
			
			switch (reason.name()) {
			case "CAPS":
				denyMessage = Utils.numericPlaceholders(denyMessage, AntispamManager.getInstance().getMaxCapsPercent(), AntispamManager.getInstance().getMaxCapsLength());
				break;
			case "FLOOD":
				denyMessage = Utils.numericPlaceholders(denyMessage, AntispamManager.getInstance().getSecondsBetweenMsg());
				break;
			case "SPAM":
				denyMessage = Utils.numericPlaceholders(denyMessage, AntispamManager.getInstance().getSecondsBetweenSameMsg());
				break;
			default:
				break;
			} for (ChatPluginServerPlayer staff : ServerPlayerManager.getInstance().getPlayers().values())
				if (staff.hasPermission("chatplugin.deny-chat-notify"))
					staff.sendTranslatedMessage("chat.deny-chat-notify", sender.getName(), reason.name(), message);
			ChatPlugin.getInstance().sendConsoleMessage(ChatColor.translate(Language.getMainLanguage().getMessage("chat.deny-chat-notify", sender.getName(), reason.name(), message)), false);
			sender.sendMessage(denyMessage);
			
			if (ChatLogManager.getInstance().isEnabled())
				ChatLogManager.getInstance().logPrivateMessage(sender, recipient, message, reason);
			return;
		} AllowPrivateMessageEvent allowPrivateMessageEvent = new AllowPrivateMessageEvent(sender, recipient, message);
		
		allowPrivateMessageEvent.call();
		
		if (allowPrivateMessageEvent.isCancelled())
			return;
		if (sender != null) {
			if (ChatLogManager.getInstance().isEnabled())
				ChatLogManager.getInstance().logPrivateMessage(sender, recipient, message, null);
			sender.sendMessage(ChatColor.translate(sentChatFormat
					.replace("{sender}", sender.getName())
					.replace("{recipient}", recipient == null ? "Console" : recipient.getName())
					) + message);
		} else ChatPlugin.getInstance().sendConsoleMessage(ChatColor.translate(sentTerminalFormat
				.replace("{sender}", "Console")
				.replace("{recipient}", recipient.getName())
				) + message, false);
		if (recipient != null) {
			if (soundEnabled)
				recipient.playSound(sound);
			((BaseChatPluginServerPlayer) recipient).setLastCorrespondent(sender);
			recipient.sendMessage(ChatColor.translate(receivedChatFormat
					.replace("{sender}", sender == null ? "Console" : sender.getName())
					.replace("{recipient}", recipient.getName())
					) + message);
			
			if (sender != null && !sender.hasPermission("chatplugin.commands.socialspy") && !recipient.hasPermission("chatplugin.commands.socialspy")) {
				for (ChatPluginServerPlayer staffMember : ServerPlayerManager.getInstance().getPlayers().values())
					if (staffMember.hasSocialspyEnabled())
						staffMember.sendMessage(ChatColor.translate(socialspyChatFormat
								.replace("{sender}", sender.getName())
								.replace("{recipient}", recipient.getName())
								) + message);
				ChatPlugin.getInstance().sendConsoleMessage(ChatColor.translate(socialspyTerminalFormat
						.replace("{sender}", sender.getName())
						.replace("{recipient}", recipient.getName())
						) + message, false);
			}
		} else ChatPlugin.getInstance().sendConsoleMessage(ChatColor.translate(receivedTerminalFormat
				.replace("{sender}", sender.getName())
				.replace("{recipient}", "Console")
				) + message, false);
		if (!ChatLogManager.getInstance().isEnabled() || ChatLogManager.getInstance().isPrintToLogFile())
			LogManager.getInstance().writeToFile(ChatColor.stripColor(ChatColor.translate(socialspyTerminalFormat
					.replace("{sender}", sender == null ? "Console" : sender.getName())
					.replace("{recipient}", recipient == null ? "Console" : recipient.getName())
					) + message));
	}
	
}
