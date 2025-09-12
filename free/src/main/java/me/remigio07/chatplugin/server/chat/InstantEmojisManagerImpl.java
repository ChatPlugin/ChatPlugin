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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager;
import me.remigio07.chatplugin.api.server.chat.channel.ChatChannel;
import me.remigio07.chatplugin.api.server.chat.channel.data.ChatChannelData;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;

public class InstantEmojisManagerImpl extends InstantEmojisManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.instant-emojis.enabled"))
			return;
		for (String id : ConfigurationType.CHAT.get().getKeys("chat.instant-emojis.values"))
			if (getInstantEmoji(id) == null)
				try {
					instantEmojis.add(new InstantEmoji(id, ConfigurationType.CHAT.get().getString("chat.instant-emojis.values." + id)));
				} catch (IllegalArgumentException e) {
					LogManager.log("Invalid instant emoji \"{0}\" set at \"chat.instant-emojis.values.{1}\" in chat.yml: the string cannot contain spaces.", 2, ConfigurationType.CHAT.get().getString("chat.instant-emojis.values." + id), id);
				}
		for (String tone : ConfigurationType.CHAT.get().getStringList("chat.instant-emojis.tones"))
			try {
				tones.add(ChatColor.of(tone));
			} catch (NumberFormatException e) {
				LogManager.log("Invalid tone \"{0}\" set at \"chat.instant-emojis.tones\" in chat.yml: use the #rrggbb format instead.", 2);
			}
		if (tones.isEmpty()) {
			LogManager.log("Tones list at \"chat.instant-emojis.tones\" in chat.yml is empty; using only default value of #FFFF55.", 2);
			
			tones.add(ChatColor.of("#FFFF55"));
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		instantEmojis.clear();
		tones.clear();
	}
	
	@Override
	public String translateInstantEmojis(
			ChatPluginServerPlayer player,
			String message,
			@Nullable(why = "Null if !ChatChannelsManager#isEnabled()") ChatChannel<? extends ChatChannelData> channel,
			List<InstantEmoji> instantEmojis
			) {
		String format = channel == null ? ChatManager.getInstance().getFormat() : channel.getFormat();
		ChatColor tone = player.getEmojisTone() == ChatColor.RESET ? getDefaultTone() : player.getEmojisTone();
		
		for (InstantEmoji instantEmoji : sort(instantEmojis)) {
			String string = instantEmoji.getString().replace("{emojis_tone}", (VersionUtils.getVersion().isAtLeast(Version.V1_16) ? tone : tone.getClosestDefaultColor()).toString());
			int index = 0;
			
			while ((index = message.indexOf(instantEmoji.getID(), index)) != -1)
				message = message.replaceFirst(instantEmoji.getLiteralPattern(), string + ChatColor.getLastColors(format + message.substring(0, index)));
		} return message;
	}
	
	@Override
	public List<InstantEmoji> getInstantEmojis(ChatPluginServerPlayer player, String message) {
		List<InstantEmoji> instantEmojis = new ArrayList<>();
		
		for (InstantEmoji instantEmoji : sort(this.instantEmojis)) {
			int index = 0;
			
			while ((index = message.indexOf(instantEmoji.getID(), index)) != -1) {
				index += instantEmoji.getID().length();
				
				instantEmojis.add(instantEmoji);
			}
		} return instantEmojis;
	}
	
	private List<InstantEmoji> sort(List<InstantEmoji> instantEmojis) {
		List<InstantEmoji> sorted = new ArrayList<>(instantEmojis);
		
		sorted.sort(Comparator.comparingInt(emoji -> emoji.getString().length()));
		Collections.reverse(sorted);
		return sorted;
	}
	
}
