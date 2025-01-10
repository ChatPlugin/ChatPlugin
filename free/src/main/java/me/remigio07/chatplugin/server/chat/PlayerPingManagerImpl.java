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

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.PlayerIgnoreManager;
import me.remigio07.chatplugin.api.server.chat.PlayerPingManager;
import me.remigio07.chatplugin.api.server.chat.RangedChatManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;

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
		color = ConfigurationType.CHAT.get().getString("chat.player-ping.color");
		sound = new SoundAdapter(
				ConfigurationType.CHAT.get().getString("chat.player-ping.sound.id"),
				ConfigurationType.CHAT.get().getFloat("chat.player-ping.sound.volume"),
				ConfigurationType.CHAT.get().getFloat("chat.player-ping.sound.pitch")
				);
		titlesEnabled = ConfigurationType.CHAT.get().getBoolean("chat.player-ping.titles.enabled");
		titlesFadeIn = ConfigurationType.CHAT.get().getLong("chat.player-ping.titles.fade-in-ms");
		titlesStay = ConfigurationType.CHAT.get().getLong("chat.player-ping.titles.stay-ms");
		titlesFadeOut = ConfigurationType.CHAT.get().getLong("chat.player-ping.titles.fade-out-ms");
		
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
		
		color = null;
		sound = null;
		titlesFadeIn = titlesStay = titlesFadeOut = 0L;
	}
	
	@Override
	public String performPing(ChatPluginServerPlayer player, String message, boolean globalChat, List<ChatPluginServerPlayer> pingedPlayers) {
		if (enabled) {
			if (!pingedPlayers.isEmpty()) {
				String str = PlaceholderManager.getInstance().translatePlaceholders(RangedChatManager.getInstance().isEnabled() && globalChat ? RangedChatManager.getInstance().getGlobalModeFormat() : ChatManager.getInstance().getFormat(), player, ChatManager.getInstance().getPlaceholderTypes());
				Matcher matcher = EVERYONE_PATTERN.matcher(message);
				
				if (matcher.find() && player.hasPermission("chatplugin.player-ping.everyone")) {
					String[] array = new String[] { message.substring(0, matcher.start()), message.substring(matcher.end(), message.length()) };
					message = array[0] + ChatColor.translate(color) + "@everyone" + ChatColor.getLastColors(str + array[0] + matcher.group()) + array[1];
				} for (ChatPluginServerPlayer pinged : pingedPlayers) {
					matcher = pattern(pinged.getName(), atSignRequired).matcher(message);
					
					if (matcher.find()) {
						String[] array = new String[] { message.substring(0, matcher.start()), message.substring(matcher.end(), message.length()) };
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
	public List<ChatPluginServerPlayer> getPingedPlayers(ChatPluginServerPlayer player, String message, boolean globalChat) {
		if (!player.hasPermission("chatplugin.player-ping"))
			return Collections.emptyList();
		Predicate<ChatPluginServerPlayer> predicate = other -> other != player
				&& !other.isVanished()
				&& (globalChat || !(((BaseChatPluginServerPlayer) other).getDistance(player.getWorld(), player.getX(), player.getY(), player.getZ()) > RangedChatManager.getInstance().getRange()));
		return ServerPlayerManager.getInstance().getPlayers().values()
				.stream()
				.filter(EVERYONE_PATTERN.matcher(message).find() && player.hasPermission("chatplugin.player-ping.everyone")
						? predicate : other -> predicate.test(other) && pattern(other.getName(), atSignRequired).matcher(message).find())
				.collect(Collectors.toList());
	}
	
	@Override
	public void playPingSound(ChatPluginServerPlayer player) {
		if (enabled && soundEnabled)
			player.playSound(sound);
	}
	
}
