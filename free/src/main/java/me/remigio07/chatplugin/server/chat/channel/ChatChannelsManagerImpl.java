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

package me.remigio07.chatplugin.server.chat.channel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.HoverInfoManager;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelType;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannelsManager;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.chat.channel.data.DiscordChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.GlobalChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.LocalChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.NetworkChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.TelegramChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.WorldChatChannel;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.server.chat.BaseHoverInfoManager;

public class ChatChannelsManagerImpl extends ChatChannelsManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.channels.enabled"))
			return;
		if (!ChatManager.getInstance().shouldOverrideChatEvent()) {
			LogManager.log("The chat channels module is enabled but \"chat.event.override\" in chat.yml is set to false. This setup is not supported: ChatPlugin needs to take over the chat event to make its features work; disabling module.", 2);
			return;
		} for (String id : ConfigurationType.CHAT.get().getKeys("chat.channels.values")) {
			if (isValidChannelID(id)) {
				if (getChannel(id, false) == null) {
					try {
						String prefix = ConfigurationType.CHAT.get().getString("chat.channels.values." + id + ".prefix", null);
						List<String> aliases = ConfigurationType.CHAT.get().getStringList("chat.channels.values." + id + ".aliases");
						List<String> languagesIDs = ConfigurationType.CHAT.get().getStringList("chat.channels.values." + id + ".languages");
						List<Language> languages = new ArrayList<>();
						ChatChannelData data = null;
						
						if (languagesIDs.contains("*"))
							languages.addAll(LanguageManager.getInstance().getLanguages());
						else for (String languageID : languagesIDs) {
							Language language = LanguageManager.getInstance().getLanguage(id);
							
							if (language == null)
								LogManager.log(
										"Invalid language (\"{0}\") specified at \"chat.channels.values.{1}.languages\" in chat.yml; here are the loaded languages: {2}.",
										1,
										languageID,
										id,
										Utils.getStringFromList(LanguageManager.getInstance().getLanguages().stream().map(Language::getID).collect(Collectors.toList()), false, true)
										);
							else languages.add(language);
						} for (ChatChannel<?> channel : channels) {
							for (String alias : aliases)
								if (Utils.arrayContains(channel.getAliases().toArray(new String[0]), alias, true))
									LogManager.log("Chat channels \"{0}\" and \"{1}\" share the same \"{2}\" alias.", 1, id, channel.getID(), alias);
							if (prefix != null && prefix.equals(channel.getPrefix()))
								LogManager.log("Chat channels \"{0}\" and \"{1}\" share the same \"{2}\" prefix.", 1, id, channel.getID(), prefix);
						} switch (ChatChannelType.valueOf(ConfigurationType.CHAT.get().getString("chat.channels.values." + id + ".type"))) {
						case LOCAL:
							data = new LocalChatChannel(ConfigurationType.CHAT.get().getInt("chat.channels.values." + id + ".range"));
							break;
						case WORLD:
							data = new WorldChatChannel();
							break;
						case GLOBAL:
							data = new GlobalChatChannel();
							break;
						case NETWORK:
							data = new NetworkChatChannel();
							break;
						case DISCORD:
							data = new DiscordChatChannel(
									ConfigurationType.CHAT.get().getString("chat.channels.values." + id + ".minecraft-format"),
									ConfigurationType.CHAT.get().getString("chat.channels.values." + id + ".discord-format"),
									ConfigurationType.CHAT.get().getLong("chat.channels.values." + id + ".channel-id")
									);
							break;
						case TELEGRAM:
							data = new TelegramChatChannel(
									ConfigurationType.CHAT.get().getString("chat.channels.values." + id + ".minecraft-format"),
									ConfigurationType.CHAT.get().getString("chat.channels.values." + id + ".telegram-format"),
									ConfigurationType.CHAT.get().getLong("chat.channels.values." + id + ".chat-id")
									);
							break;
						} channels.add(new ChatChannel<>(
								id,
								ConfigurationType.CHAT.get().getString("chat.channels.values." + id + ".display-name", null),
								prefix,
								ConfigurationType.CHAT.get().getString("chat.channels.values." + id + ".format"),
								ConfigurationType.CHAT.get().getBoolean("chat.channels.values." + id + ".access-restricted"),
								ConfigurationType.CHAT.get().getBoolean("chat.channels.values." + id + ".writing-restricted"),
								ConfigurationType.CHAT.get().getBoolean("chat.channels.values." + id + ".console-included"),
								aliases,
								languages,
								data
								));
					} catch (IllegalArgumentException iae) {
						LogManager.log(
								"Invalid chat channel type (\"{0}\") specified at \"chat.channels.values.{1}.type\" in chat.yml: only LOCAL, WORLD, GLOBAL, NETWORK, DISCORD and TELEGRAM are allowed; skipping it.",
								2,
								ConfigurationType.CHAT.get().getString("chat.channels.values." + id + ".type"),
								id
								);
					}
				} else LogManager.log("A chat channel with ID \"{0}\" already exists at \"chat.channels.values.{0}\" in chat.yml; skipping it.", 1, id);
			} else LogManager.log("Chat channel ID specified at \"chat.channels.values.{0}\" in chat.yml does not respect the following pattern: \"{1}\"; skipping it.", 2, id, CHANNEL_ID_PATTERN.pattern());
		} spyOnJoinEnabled = ConfigurationType.CHAT.get().getBoolean("chat.channels.spy.on-join-enabled");
		spyFormat = ConfigurationType.CHAT.get().getString("chat.channels.spy.format");
		
		// some validation checks...
		if (channels.isEmpty()) {
			LogManager.log("There are no valid channels at \"chat.channels.values\" in chat.yml, at least one is required; disabling module.", 2);
			unload();
			return;
		} if ((defaultListeningChannelsIDs = ConfigurationType.CHAT.get().getStringList("chat.channels.default.listening")).isEmpty()) {
			LogManager.log(
					"Default listening channels' IDs' list ({0}) specified at \"chat.channels.default.listening\" in chat.yml is empty; disabling module.",
					2,
					Utils.getStringFromList(defaultListeningChannelsIDs, false, true)
					);
			unload();
			return;
		} if (defaultListeningChannelsIDs.stream().noneMatch(id -> getChannel(id, false) != null)) {
			LogManager.log(
					"Default listening channels' IDs' list ({0}) specified at \"chat.channels.default.listening\" in chat.yml does not contain valid channels' IDs ({1}), at least one is required; disabling module.",
					2,
					Utils.getStringFromList(defaultListeningChannelsIDs, false, true),
					Utils.getStringFromList(channels.stream().map(ChatChannel::getID).collect(Collectors.toList()), false, true)
					);
			unload();
			return;
		} if ((defaultWritingChannel = getChannel(ConfigurationType.CHAT.get().getString("chat.channels.default.writing"), false)) == null) {
			LogManager.log(
					"Default writing channel's ID (\"{0}\") specified at \"chat.channels.default.writing\" in chat.yml does not belong to any loaded channel ({1}); disabling module.",
					2,
					ConfigurationType.CHAT.get().getString("chat.channels.default.writing"),
					Utils.getStringFromList(channels.stream().map(ChatChannel::getID).collect(Collectors.toList()), false, true)
					);
			unload();
			return;
		} if (!Utils.arrayContains(defaultListeningChannelsIDs.toArray(new String[0]), defaultWritingChannel.getID(), true)) {
			LogManager.log(
					"Default writing channel's ID (\"{0}\") specified at \"chat.channels.default.writing\" in chat.yml is not included in the default listening channels' IDs' list ({1}) specified at \"chat.channels.default.listening\"; disabling module.",
					2,
					defaultWritingChannel.getID(),
					Utils.getStringFromList(defaultListeningChannelsIDs, false, true)
					);
			unload();
			return;
		} if (defaultWritingChannel.isAccessRestricted() || defaultWritingChannel.isWritingRestricted()) {
			LogManager.log(
					"Default writing channel (\"{0}\") specified at \"chat.channels.default.writing\" in chat.yml cannot be access- or writing-restricted; disabling module.",
					2,
					defaultWritingChannel.getID()
					);
			unload();
			return;
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = spyOnJoinEnabled = false;
		
		defaultListeningChannelsIDs.clear();
		channels.clear();
		
		spyFormat = null;
		defaultWritingChannel = null;
	}
	
	@Override
	public void addChannel(ChatChannel<? extends ChatChannelData> channel) {
		if (channels.stream().anyMatch(other -> other.getID().equalsIgnoreCase(channel.getID())))
			throw new IllegalArgumentException("Specified ID (" + channel.getID() + ") is already in use");
		channels.add(channel);
		
		if (HoverInfoManager.getInstance().isEnabled())
			((BaseHoverInfoManager) HoverInfoManager.getInstance()).loadChannelsFormats();
	}
	
}
