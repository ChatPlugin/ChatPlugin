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

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.chat.PlayerPingManager;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.event.chat.PlayerPingEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

public class PlayerPingManagerImpl extends PlayerPingManager {
	
	private static final Pattern EVERYONE_PATTERN = pattern("everyone", true);
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.player-ping.enabled"))
			return;
		atSignRequired = ConfigurationType.CHAT.get().getBoolean("chat.player-ping.at-sign-required");
		soundEnabled = ConfigurationType.CHAT.get().getBoolean("chat.player-ping.sound.enabled");
		titlesEnabled = ConfigurationType.CHAT.get().getBoolean("chat.player-ping.titles.enabled");
		perPlayerCooldown = Utils.getTime(ConfigurationType.CHAT.get().getString("chat.player-ping.per-player-cooldown"), false, true);
		titlesFadeIn = ConfigurationType.CHAT.get().getLong("chat.player-ping.titles.fade-in-ms");
		titlesStay = ConfigurationType.CHAT.get().getLong("chat.player-ping.titles.stay-ms");
		titlesFadeOut = ConfigurationType.CHAT.get().getLong("chat.player-ping.titles.fade-out-ms");
		color = ConfigurationType.CHAT.get().getString("chat.player-ping.color");
		sound = new SoundAdapter(
				ConfigurationType.CHAT.get().getString("chat.player-ping.sound.id"),
				ConfigurationType.CHAT.get().getFloat("chat.player-ping.sound.volume"),
				ConfigurationType.CHAT.get().getFloat("chat.player-ping.sound.pitch")
				);
		
		if (titlesEnabled) {
			for (Language language : LanguageManager.getInstance().getLanguages()) {
				String translatedTitle = ConfigurationType.CHAT.get().getString("chat.player-ping.titles.titles." + language.getID(), null);
				String translatedSubtitle = ConfigurationType.CHAT.get().getString("chat.player-ping.titles.subtitles." + language.getID(), null);
				
				if (translatedTitle == null && language != Language.getMainLanguage())
					LogManager.log("Translation for language \"{0}\" not found at \"chat.player-ping.titles.titles.{0}\" in chat.yml.", 1, language.getID());
				else titles.put(language, translatedTitle);
				if (translatedSubtitle == null && language != Language.getMainLanguage())
					LogManager.log("Translation for language \"{0}\" not found at \"chat.player-ping.titles.subtitles.{0}\" in chat.yml.", 1, language.getID());
				else subtitles.put(language, translatedSubtitle);
			} if (titles.get(Language.getMainLanguage()) == null) {
				LogManager.log("Translation for main language (\"{0}\") not found at \"chat.player-ping.titles.titles.{0}\" in chat.yml; disabling titles.", 2, Language.getMainLanguage().getID());
				
				titlesEnabled = false;
			} else if (subtitles.get(Language.getMainLanguage()) == null) {
				LogManager.log("Translation for main language (\"{0}\") not found at \"chat.player-ping.titles.subtitles.{0}\" in chat.yml; disabling titles.", 2, Language.getMainLanguage().getID());
				
				titlesEnabled = false;
			}
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = atSignRequired = soundEnabled = titlesEnabled = false;
		
		titles.clear();
		subtitles.clear();
		
		perPlayerCooldown = titlesFadeIn = titlesStay = titlesFadeOut = 0L;
		color = null;
		sound = null;
		
	}
	
	@Override
	public String performPing(
			ChatPluginServerPlayer player,
			String message,
			@Nullable(why = "Null if !ChatChannelsManager#isEnabled()") ChatChannel<? extends ChatChannelData> channel,
			List<ChatPluginServerPlayer> pingedPlayers
			) {
		if (enabled) {
			if (!pingedPlayers.isEmpty()) {
				String str = PlaceholderManager.getInstance().translatePlaceholders(channel == null ? ChatManager.getInstance().getFormat() : channel.getFormat(), player, ChatManager.getInstance().getPlaceholderTypes());
				Matcher matcher = EVERYONE_PATTERN.matcher(message);
				boolean success = false;
				
				if (matcher.find() && player.hasPermission("chatplugin.player-ping.everyone")) {
					String[] array = { message.substring(0, matcher.start()), message.substring(matcher.end(), message.length()) };
					message = array[0] + ChatColor.translate(color) + "@everyone" + ChatColor.getLastColors(str + array[0] + matcher.group()) + array[1];
				} for (ChatPluginServerPlayer pinged : pingedPlayers) {
					PlayerPingEvent event = new PlayerPingEvent(player, pinged, message);
					
					event.call();
					
					if (event.isCancelled())
						continue;
					matcher = pattern(pinged.getName(), atSignRequired).matcher(message);
					success = true;
					
					if (matcher.find()) {
						String[] array = { message.substring(0, matcher.start()), message.substring(matcher.end(), message.length()) };
						message = array[0] + ChatColor.translate(color) + "@" + pinged.getName() + ChatColor.getLastColors(str + array[0] + matcher.group()) + array[1];
					} if (!PlayerIgnoreManager.getInstance().isEnabled() || !pinged.getIgnoredPlayers().contains(player)) {
						if (titlesEnabled)
							pinged.sendTitle(
									PlaceholderManager.getInstance().translatePlayerPlaceholders(getTitle(pinged.getLanguage(), true), player, pinged.getLanguage()),
									PlaceholderManager.getInstance().translatePlayerPlaceholders(getSubtitle(pinged.getLanguage(), true), player, pinged.getLanguage()),
									(int) titlesFadeIn,
									(int) titlesStay,
									(int) titlesFadeOut
									);
						pinged.sendTranslatedMessage("chat.pinged", player.getName());
						playPingSound(pinged);
					}
				} if (success && !player.hasPermission("chatplugin.player-ping.bypass")) {
					playersOnCooldown.add(player.getUUID());
					TaskManager.runAsync(() -> playersOnCooldown.remove(player.getUUID()), perPlayerCooldown);
				}
			}
		} return message;
	}
	
	private static Pattern pattern(String str, boolean appendAtSign) {
		StringBuilder sb = new StringBuilder("(?i)(?<=^|\\W)");
		
		if (appendAtSign)
			sb.append("(§[0-9A-FK-ORX])*@");
		for (char c : str.toCharArray())
			sb.append("(§[0-9A-FK-ORX])*(?-i:").append(c).append(')');
		return Pattern.compile(sb.append("(§[0-9A-FK-ORX])*(?=$|\\W)").toString());
	}
	
	@Override
	public List<ChatPluginServerPlayer> getPingedPlayers(
			ChatPluginServerPlayer player,
			String message,
			@Nullable(why = "Null if !ChatChannelsManager#isEnabled()") ChatChannel<? extends ChatChannelData> channel
			) {
		List<ChatPluginServerPlayer> pingedPlayers = player.hasPermission("chatplugin.player-ping") ? (channel == null ? ServerPlayerManager.getInstance().getPlayers().values() : channel.getRecipients(player, true))
				.stream()
				.filter(other -> other != player && !other.isVanished() && ((EVERYONE_PATTERN.matcher(message).find() && player.hasPermission("chatplugin.player-ping.everyone")) || pattern(other.getName(), atSignRequired).matcher(message).find()))
				.collect(Collectors.toList())
				: Collections.emptyList();
		
		if (!pingedPlayers.isEmpty() && playersOnCooldown.contains(player.getUUID())) {
			player.sendTranslatedMessage("chat.cannot-ping", Utils.formatTime(perPlayerCooldown, player.getLanguage(), false, true));
			return Collections.emptyList();
		} return pingedPlayers;
	}
	
	@Override
	public void playPingSound(ChatPluginServerPlayer player) {
		if (enabled && soundEnabled)
			player.playSound(sound);
	}
	
}
