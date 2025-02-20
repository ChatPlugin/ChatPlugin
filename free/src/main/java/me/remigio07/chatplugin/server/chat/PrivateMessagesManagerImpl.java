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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.punishment.mute.Mute;
import me.remigio07.chatplugin.api.common.punishment.mute.MuteManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.FormattedChatManager;
import me.remigio07.chatplugin.api.server.chat.PrivateMessagesManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamManager;
import me.remigio07.chatplugin.api.server.chat.antispam.AntispamResult;
import me.remigio07.chatplugin.api.server.chat.antispam.DenyChatReason;
import me.remigio07.chatplugin.api.server.chat.log.ChatLogManager;
import me.remigio07.chatplugin.api.server.event.chat.AllowPrivateMessageEvent;
import me.remigio07.chatplugin.api.server.event.chat.DenyPrivateMessageEvent;
import me.remigio07.chatplugin.api.server.event.chat.PrePrivateMessageEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.URLValidator;
import me.remigio07.chatplugin.api.server.util.adapter.block.MaterialAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.chat.antispam.AntispamManagerImpl;
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
		sentFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.sent");
		receivedFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.received");
		socialspyFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.socialspy");
		senderPlaceholderFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.placeholder.sender");
		recipientPlaceholderFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.format.placeholder.recipient");
		placeholderPlaceholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.CHAT.get().getStringList("chat.private-messages.format.placeholder.placeholder-types"));
		soundEnabled = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.sound.enabled");
		sound = new SoundAdapter(ConfigurationType.CHAT.get(), "chat.private-messages.sound");
		advancementsEnabled = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.advancements.enabled");
		advancementsFormat = ConfigurationType.CHAT.get().getString("chat.private-messages.advancements.format");
		advancementsMaxMessageLength = ConfigurationType.CHAT.get().getInt("chat.private-messages.advancements.max-message-length");
		
		if (advancementsEnabled && VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			LogManager.log("You have enabled advancements for private messages at \"chat.private-messages.advancements.enabled\" in chat.yml, but this feature only works on 1.13+ servers; disabling module.", 2);
			
			advancementsEnabled = false;
		} try {
			advancementsIconMaterial = new MaterialAdapter(ConfigurationType.CHAT.get().getString("chat.private-messages.advancements.icon.material").toUpperCase());
		} catch (IllegalArgumentException e) {
			String material = Environment.isSponge() || VersionUtils.getVersion().isAtLeast(Version.V1_13) ? "WRITABLE_BOOK" : "BOOK_AND_QUILL";
			
			LogManager.log("Invalid material (\"{0}\") set at \"chat.private-messages.advancements.icon.material\" in chat.yml; setting to default value of {1}.", 2, ConfigurationType.CHAT.get().getString("chat.private-messages.advancements.icon.material"), material);
			
			advancementsIconMaterial = new MaterialAdapter(material);
		} advancementsIconGlowing = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.advancements.icon.glowing");
		bypassAntispamChecks = new ArrayList<>();
		
		for (DenyChatReason<?> check : ConfigurationType.CHAT.get().getStringList("chat.private-messages.bypass-antispam-checks").stream().map(DenyChatReason::valueOf).filter(Objects::nonNull).collect(Collectors.toList()))
			if (check.getHandlerClass() == AntispamManager.class)
				bypassAntispamChecks.add((DenyChatReason<AntispamManager>) check);
		socialspyOnJoinEnabled = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.socialspy-on-join-enabled");
		mutedPlayersBlocked = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.muted-players-blocked");
		replyToLastSender = ConfigurationType.CHAT.get().getBoolean("chat.private-messages.reply-to-last-sender");
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = soundEnabled = advancementsEnabled = advancementsIconGlowing = socialspyOnJoinEnabled = mutedPlayersBlocked = replyToLastSender = false;
		
		placeholderPlaceholderTypes.clear();
		bypassAntispamChecks.clear();
		
		sentFormat = receivedFormat = socialspyFormat = senderPlaceholderFormat = recipientPlaceholderFormat = advancementsFormat = null;
		sound = null;
		advancementsMaxMessageLength = 0;
		advancementsIconMaterial = null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void sendPrivateMessage(@Nullable(why = "Null to represent the console") ChatPluginServerPlayer sender, @Nullable(why = "Null to represent the console") OfflinePlayer recipient, String privateMessage) {
		if (!enabled)
			return;
		if ((sender == null && recipient == null) || (sender != null && sender.equals(recipient)))
			throw new IllegalArgumentException("The sender and the recipient correspond");
		if (privateMessage.length() > 505)
			throw new IllegalArgumentException("Specified private message is longer than 505 characters");
		String[] placeholders = { sender == null ? "" : formatPlaceholder(senderPlaceholderFormat, sender), recipient == null ? "" : formatPlaceholder(recipientPlaceholderFormat, recipient) };
		Mute mute;
		
		if (sender == null) {
			if (recipient.isOnline()) {
				ChatPluginServerPlayer recipientPlayer = recipient.toServerPlayer();
				
				if (recipientPlayer != null) {
					if ((privateMessage = performEvents(null, recipient, placeholders, privateMessage)) != null) {
						sendMessageToSender(null, recipient, placeholders, privateMessage);
						sendMessageToRecipient(null, recipientPlayer, placeholders, privateMessage);
					}
				} else ChatPlugin.getInstance().sendConsoleMessage(Language.getMainLanguage().getMessage("misc.disabled-world"), false);
			} else ChatPlugin.getInstance().sendConsoleMessage(Language.getMainLanguage().getMessage("misc.player-not-found", recipient.getName()), false);
		} else if (!mutedPlayersBlocked || !MuteManager.getInstance().isEnabled() || (mute = MuteManager.getInstance().getActiveMute(sender, ProxyManager.getInstance().getServerID())) == null) {
			if (recipient == null) {
				if ((privateMessage = performEvents(sender, null, placeholders, privateMessage)) != null) {
					sendMessageToSender(sender, null, placeholders, privateMessage);
					sendMessageToRecipient(sender, null, placeholders, privateMessage);
				}
			} else if (recipient.isOnline()) {
				ChatPluginServerPlayer recipientPlayer = recipient.toServerPlayer();
				
				if (recipientPlayer != null) {
					if (!recipientPlayer.isVanished() || sender.hasPermission(VanishManager.VANISH_PERMISSION)) {
						if (!recipientPlayer.getIgnoredPlayers().contains(sender)) {
							if ((privateMessage = performEvents(sender, recipient, placeholders, privateMessage)) != null) {
								if (!ProxyManager.getInstance().isEnabled()) {
									if (!sender.hasPermission("chatplugin.commands.socialspy") && !recipientPlayer.hasPermission("chatplugin.commands.socialspy"))
										sendSocialspyNotifications(sender, recipientPlayer, placeholders, privateMessage);
									sendMessageToSender(sender, recipient, placeholders, privateMessage);
									sendMessageToRecipient(sender, recipientPlayer, placeholders, privateMessage);
								} else ProxyManager.getInstance().sendPluginMessage(Packets.Messages.privateMessage(
										"ALL",
										ProxyManager.getInstance().getServerID(),
										sender.getUUID(),
										sender.getName(),
										recipient.getUUID(),
										recipient.getName(),
										sender.hasPermission(VanishManager.VANISH_PERMISSION),
										!sender.hasPermission("chatplugin.commands.socialspy") && !recipientPlayer.hasPermission("chatplugin.commands.socialspy"),
										placeholders,
										privateMessage
										));
							}
						} else sender.sendTranslatedMessage("commands.whisper.ignored", recipient.getName());
					} else sender.sendTranslatedMessage("misc.player-not-found", recipient.getName());
				} else sender.sendTranslatedMessage("misc.disabled-world");
			} else if (ProxyManager.getInstance().isEnabled())
				ProxyManager.getInstance().sendPluginMessage(Packets.Messages.privateMessage(
						"ALL",
						ProxyManager.getInstance().getServerID(),
						sender.getUUID(),
						sender.getName(),
						recipient.getUUID(),
						recipient.getName(),
						sender.hasPermission(VanishManager.VANISH_PERMISSION),
						!sender.hasPermission("chatplugin.commands.socialspy"),
						placeholders,
						privateMessage
						));
			else sender.sendTranslatedMessage("misc.player-not-found", recipient.getName());
		} else sender.sendMessage(mute.formatPlaceholders(sender.getLanguage().getMessage("mute.no-chat"), sender.getLanguage()));
	}
	
	public void sendMessageToSender(ChatPluginServerPlayer sender, OfflinePlayer recipient, String[] placeholders, String privateMessage) {
		if (sender != null) {
			if (!replyToLastSender)
				((BaseChatPluginServerPlayer) sender).setLastCorrespondent(recipient);
			sender.sendMessage(formatPlaceholders(sentFormat, placeholders, sender, recipient) + privateMessage);
		} else ChatPlugin.getInstance().sendConsoleMessage(formatPlaceholders(sentFormat, placeholders, sender, recipient) + privateMessage, false);
	}
	
	public void sendMessageToRecipient(OfflinePlayer sender, ChatPluginServerPlayer recipient, String[] placeholders, String privateMessage) {
		if (recipient != null) {
			if (advancementsEnabled)
				Utils.displayAdvancement(recipient, formatPlaceholders(advancementsFormat, placeholders, sender, recipient)
						+ me.remigio07.chatplugin.common.util.Utils.abbreviate(privateMessage, privateMessage.toLowerCase().contains("§l") ? advancementsMaxMessageLength - 2 : advancementsMaxMessageLength, true),
						advancementsIconMaterial, advancementsIconGlowing);
			if (soundEnabled)
				recipient.playSound(sound);
			if (replyToLastSender)
				((BaseChatPluginServerPlayer) recipient).setLastCorrespondent(sender);
			recipient.sendMessage(formatPlaceholders(receivedFormat, placeholders, sender, recipient) + privateMessage);
		} else ChatPlugin.getInstance().sendConsoleMessage(formatPlaceholders(receivedFormat, placeholders, sender, recipient) + privateMessage, false);
	}
	
	public void sendSocialspyNotifications(OfflinePlayer sender, OfflinePlayer recipient, String[] placeholders, String privateMessage) {
		for (ChatPluginServerPlayer staffMember : ServerPlayerManager.getInstance().getPlayers().values())
			if (staffMember.hasSocialspyEnabled())
				staffMember.sendMessage(formatPlaceholders(socialspyFormat, placeholders, sender, recipient) + privateMessage);
		ChatPlugin.getInstance().sendConsoleMessage(formatPlaceholders(socialspyFormat, placeholders, sender, recipient) + privateMessage, false);
	}
	
	private String formatPlaceholders(String input, String[] placeholders, OfflinePlayer sender, OfflinePlayer recipient) {
		return ChatColor.translate(input
				.replace("{sender}", sender == null ? "Console" : placeholders[0])
				.replace("{sender_plain}", sender == null ? "Console" : sender.getName())
				.replace("{recipient}", recipient == null ? "Console" : placeholders[1])
				.replace("{recipient_plain}", recipient == null ? "Console" : recipient.getName())
				);
	}
	
	public String formatPlaceholder(String placeholder, OfflinePlayer player) {
		ChatPluginServerPlayer serverPlayer = player.toServerPlayer();
		return serverPlayer == null ? placeholder : PlaceholderManager.getInstance().translatePlaceholders(placeholder, serverPlayer, serverPlayer.getLanguage(), placeholderPlaceholderTypes);
	}
	
	public String performEvents(ChatPluginServerPlayer sender, OfflinePlayer recipient, String[] placeholders, String privateMessage) {
		privateMessage = privateMessage.trim().replaceAll(" +", " ");
		PrePrivateMessageEvent prePrivateMessageEvent = new PrePrivateMessageEvent(sender, recipient, privateMessage);
		
		prePrivateMessageEvent.call();
		
		if (prePrivateMessageEvent.isCancelled())
			return null;
		AntispamResult antispamResult = null;
		DenyChatReason<?> denyChatReason = null;
		
		// 0. antispam
		if (sender != null && AntispamManager.getInstance().isEnabled()) {
			antispamResult = AntispamManager.getInstance().check(sender, privateMessage, bypassAntispamChecks);
			denyChatReason = antispamResult.getReason();
		}
		
		// 1. formatted-chat
		if (denyChatReason == null && FormattedChatManager.getInstance().isEnabled()) {
			List<String> urls = URLValidator.getURLs(privateMessage);
			
			if (FormattedChatManager.getInstance().containsFormattedText(privateMessage, urls, true)) {
				if (sender != null && !sender.hasPermission("chatplugin.formatted-chat"))
					if (!FormattedChatManager.getInstance().isSendAnyway())
						denyChatReason = DenyChatReason.FORMAT;
					else sender.sendTranslatedMessage("chat.no-format");
				else privateMessage = FormattedChatManager.getInstance().translate(privateMessage, urls, true);
			}
		}
		
		// 2. blank-message
		if (denyChatReason == null && ChatColor.stripColor(privateMessage).replaceAll("\\s", "").isEmpty())
			denyChatReason = DenyChatReason.BLANK_MESSAGE;
		
		// denied
		if (denyChatReason != null) {
			String denyMessage = denyChatReason.getMessage(sender == null ? Language.getMainLanguage() : sender.getLanguage());
			
			new DenyPrivateMessageEvent(sender, recipient, privateMessage, denyChatReason, antispamResult).call();
			
			switch (denyChatReason.name()) {
			case "CAPS":
				denyMessage = Utils.replaceNumericPlaceholders(denyMessage, Utils.truncate(AntispamManager.getInstance().getMaxCapsPercentage(), 2), AntispamManager.getInstance().getMaxCapsLength());
				break;
			case "FLOOD":
				denyMessage = Utils.replaceNumericPlaceholders(denyMessage, AntispamManager.getInstance().getSecondsBetweenMessages());
				break;
			case "SPAM":
				denyMessage = Utils.replaceNumericPlaceholders(denyMessage, AntispamManager.getInstance().getSecondsBetweenSameMessages());
				break;
			default:
				break;
			} if (denyChatReason != DenyChatReason.BLANK_MESSAGE) {
				if (ChatLogManager.getInstance().isEnabled() && recipient != null)
					ChatLogManager.getInstance().logPrivateMessage(sender, recipient, privateMessage, denyChatReason);
				if (denyChatReason.getHandlerClass() == AntispamManager.class)
					((AntispamManagerImpl) AntispamManager.getInstance()).sendNotification(sender, antispamResult);
			} if (sender == null)
				ChatPlugin.getInstance().sendConsoleMessage(denyMessage, false);
			else sender.sendMessage(denyMessage);
			return null;
		} AllowPrivateMessageEvent allowPrivateMessageEvent = new AllowPrivateMessageEvent(sender, recipient, privateMessage);
		
		allowPrivateMessageEvent.call();
		
		if (allowPrivateMessageEvent.isCancelled())
			return null;
		if (ChatLogManager.getInstance().isEnabled()) {
			if (sender != null && recipient != null)
				ChatLogManager.getInstance().logPrivateMessage(sender, recipient, privateMessage, null);
			if (!ChatLogManager.getInstance().isPrintToLogFile())
				return privateMessage;
		} LogManager.getInstance().writeToFile(ChatColor.stripColor(formatPlaceholders(socialspyFormat, placeholders, sender, recipient) + privateMessage));
		return privateMessage;
	}
	
	@Override
	public void sendReply(@NotNull ChatPluginServerPlayer sender, String privateMessage) {
		OfflinePlayer correspondent = sender.getLastCorrespondent();
		
		if (correspondent == null)
			throw new IllegalArgumentException("No correspondent found for " + sender.getName());
		sendPrivateMessage(sender, correspondent, privateMessage);
	}
	
}
