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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
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
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.util.Utils;

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
		advancementsEnabled = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.advancements.enabled");
		advancementsFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.advancements.format");
		advancementsMaxMessageLength = ConfigurationType.CHAT.get().getInt("chat.private-messages.advancements.max-message-length");
		
		if (advancementsEnabled && VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			LogManager.log("You have enabled advancements for private messages at \"chat.private-messages.advancements.enabled\" in chat.yml, but this feature only works on 1.13+ servers; disabling it.", 2);
			
			advancementsEnabled = false;
		} try {
			advancementsIconMaterial = new MaterialAdapter(ConfigurationType.CHAT.get().getString("chat.private-messages.advancements.icon.material").toUpperCase());
		} catch (IllegalArgumentException e) {
			LogManager.log("Invalid material (\"{0}\") set at \"chat.private-messages.advancements.icon.material\" in chat.yml; setting to default value of BARRIER.", 2, ConfigurationType.CHAT.get().getString("chat.private-messages.advancements.icon.material"));
			
			advancementsIconMaterial = new MaterialAdapter("BARRIER");
		} advancementsIconGlowing = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.advancements.icon.glowing");
		bypassAntispamChecks = new ArrayList<>();
		
		for (DenyChatReason<?> check : ConfigurationType.CHAT.get().getStringList("chat.private-messages.bypass-antispam-checks").stream().map(DenyChatReason::valueOf).filter(Objects::nonNull).collect(Collectors.toList()))
			if (check.getHandlerClass() == AntispamManager.class)
				bypassAntispamChecks.add((DenyChatReason<AntispamManager>) check);
		socialspyOnJoinEnabled = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.socialspy-on-join-enabled");
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = soundEnabled = socialspyOnJoinEnabled = advancementsEnabled = advancementsIconGlowing = false;
		
		bypassAntispamChecks.clear();
		
		sentChatFormat = sentTerminalFormat = receivedChatFormat = receivedTerminalFormat = socialspyChatFormat = socialspyTerminalFormat = advancementsFormat = null;
		sound = null;
		advancementsMaxMessageLength = 0;
		advancementsIconMaterial = null;
	}
	
	@Override
	public void sendPrivateMessage(@Nullable(why = "Null to represent the console") ChatPluginServerPlayer sender, @Nullable(why = "Null to represent the console") ChatPluginServerPlayer recipient, String privateMessage) {
		if (!enabled)
			return;
		if ((sender == null && recipient == null) || (sender != null && sender.equals(recipient)))
			throw new IllegalArgumentException("The sender and the recipient correspond");
		privateMessage = privateMessage.trim().replaceAll(" +", " ");
		PrePrivateMessageEvent prePrivateMessageEvent = new PrePrivateMessageEvent(sender, recipient, privateMessage);
		
		prePrivateMessageEvent.call();
		
		if (prePrivateMessageEvent.isCancelled())
			return;
		
		// 0. instant emojis
		if (InstantEmojisManager.getInstance().isEnabled())
			privateMessage = InstantEmojisManager.getInstance().format(privateMessage);
		DenyChatReason<?> reason = null;
		
		// 1. antispam
		if (sender != null && AntispamManager.getInstance().isEnabled())
			reason = AntispamManager.getInstance().getDenyChatReason(sender, privateMessage, bypassAntispamChecks);
		
		// 2. formatted-chat
		if (reason == null && FormattedChatManager.getInstance().isEnabled()) {
			List<String> urls = URLValidator.getURLs(privateMessage);
			
			if (FormattedChatManager.getInstance().containsFormattedText(privateMessage, urls, true)) {
				if (sender != null && !sender.hasPermission("chatplugin.formatted-chat"))
					if (!FormattedChatManager.getInstance().isSendAnyway())
						reason = DenyChatReason.FORMAT;
					else sender.sendTranslatedMessage("chat.no-format");
				else privateMessage = FormattedChatManager.getInstance().translate(privateMessage, urls, true);
			}
		}
		
		// 3. blank-message
		if (reason == null && ChatColor.stripColor(privateMessage).replaceAll("\\s", "").isEmpty())
			reason = DenyChatReason.BLANK_MESSAGE;
		
		// denied
		if (reason != null) {
			String denyMessage = reason.getMessage(sender == null ? Language.getMainLanguage() : sender.getLanguage());
			
			new DenyPrivateMessageEvent(sender, recipient, privateMessage, reason).call();
			
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
			} if (reason != DenyChatReason.BLANK_MESSAGE) {
				for (ChatPluginServerPlayer staff : ServerPlayerManager.getInstance().getPlayers().values())
					if (staff.hasPermission("chatplugin.deny-chat-notify"))
						staff.sendTranslatedMessage("chat.deny-chat-notify", sender.getName(), reason.name(), privateMessage);
				if (ChatLogManager.getInstance().isEnabled() && recipient != null)
					ChatLogManager.getInstance().logPrivateMessage(sender, recipient, privateMessage, reason);
				ChatPlugin.getInstance().sendConsoleMessage(Language.getMainLanguage().getMessage("chat.deny-chat-notify", sender.getName(), reason.name(), privateMessage), false);
			} if (sender == null)
				ChatPlugin.getInstance().sendConsoleMessage(denyMessage, false);
			else sender.sendMessage(denyMessage);
			return;
		} AllowPrivateMessageEvent allowPrivateMessageEvent = new AllowPrivateMessageEvent(sender, recipient, privateMessage);
		
		allowPrivateMessageEvent.call();
		
		if (allowPrivateMessageEvent.isCancelled())
			return;
		if (sender != null) {
			if (ChatLogManager.getInstance().isEnabled() && recipient != null)
				ChatLogManager.getInstance().logPrivateMessage(sender, recipient, privateMessage, null);
			sender.sendMessage(formatPlaceholders(sentChatFormat, sender, recipient) + privateMessage);
		} else ChatPlugin.getInstance().sendConsoleMessage(formatPlaceholders(sentTerminalFormat, sender, recipient) + privateMessage, false);
		
		if (recipient != null) {
			if (soundEnabled)
				recipient.playSound(sound);
			((BaseChatPluginServerPlayer) recipient).setLastCorrespondent(sender);
			recipient.sendMessage(formatPlaceholders(receivedChatFormat, sender, recipient) + privateMessage);
			
			if (sender != null && !sender.hasPermission("chatplugin.commands.socialspy") && !recipient.hasPermission("chatplugin.commands.socialspy")) {
				for (ChatPluginServerPlayer staffMember : ServerPlayerManager.getInstance().getPlayers().values())
					if (staffMember.hasSocialspyEnabled())
						staffMember.sendMessage(formatPlaceholders(socialspyChatFormat, sender, recipient) + privateMessage);
				ChatPlugin.getInstance().sendConsoleMessage(formatPlaceholders(socialspyTerminalFormat, sender, recipient) + privateMessage, false);
			} if (advancementsEnabled)
				Utils.displayAdvancement(recipient, formatPlaceholders(advancementsFormat, sender, recipient)
						+ me.remigio07.chatplugin.common.util.Utils.abbreviate(privateMessage, privateMessage.toLowerCase().contains("\u00A7l") ? advancementsMaxMessageLength - 2 : advancementsMaxMessageLength, true),
						advancementsIconMaterial, advancementsIconGlowing);
		} else ChatPlugin.getInstance().sendConsoleMessage(formatPlaceholders(receivedTerminalFormat, sender, recipient) + privateMessage, false);
		if (!ChatLogManager.getInstance().isEnabled() || ChatLogManager.getInstance().isPrintToLogFile())
			LogManager.getInstance().writeToFile(ChatColor.stripColor(formatPlaceholders(socialspyTerminalFormat, sender, recipient) + privateMessage));
	}
	
	private static String formatPlaceholders(String input, ChatPluginServerPlayer sender, ChatPluginServerPlayer recipient) {
		return ChatColor.translate(input
				.replace("{sender}", sender == null ? "Console" : sender.getName())
				.replace("{sender_uuid}", (sender == null ? Utils.NIL_UUID : sender.getUUID()).toString())
				.replace("{recipient}", recipient == null ? "Console" : recipient.getName())
				.replace("{recipient_uuid}", (recipient == null ? Utils.NIL_UUID : recipient.getUUID()).toString())
				);
	}
	
}
