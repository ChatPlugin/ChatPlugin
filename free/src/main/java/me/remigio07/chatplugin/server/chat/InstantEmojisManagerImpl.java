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

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.chat.InstantEmojisManager;
import me.remigio07.chatplugin.api.server.chat.RangedChatManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

public class InstantEmojisManagerImpl extends InstantEmojisManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ChatManager.getInstance().isEnabled() || !ConfigurationType.CHAT.get().getBoolean("chat.instant-emojis.enabled"))
			return;
		for (String id : ConfigurationType.CHAT.get().getKeys("chat.instant-emojis.values"))
			if (getInstantEmoji(id) == null)
				instantEmojis.add(new InstantEmoji(id, ConfigurationType.CHAT.get().getString("chat.instant-emojis.values." + id)));
		for (String tone : ConfigurationType.CHAT.get().getStringList("chat.instant-emojis.tones"))
			try {
				tones.add(ChatColor.of(tone));
			} catch (NumberFormatException e) {
				LogManager.log("Invalid tone \"{0}\" set at \"chat.instant-emojis.tones\" in chat.yml: use the #xxxxxx format instead.", 2);
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
	public String translateInstantEmojis(ChatPluginServerPlayer player, String message, boolean globalChat) {
		String format = PlaceholderManager.getInstance().translatePlaceholders(RangedChatManager.getInstance().isEnabled() && globalChat ? RangedChatManager.getInstance().getGlobalModeFormat() : ChatManager.getInstance().getFormat(), player, ChatManager.getInstance().getPlaceholderTypes());
		ChatColor tone = player.getEmojisTone() == ChatColor.RESET ? getDefaultTone() : player.getEmojisTone();
		
		for (InstantEmoji instantEmoji : instantEmojis) {
			String string = instantEmoji.getString().replace("{emojis_tone}", (VersionUtils.getVersion().isAtLeast(Version.V1_16) ? tone : tone.getClosestDefaultColor()).toString());
			int index = 0;
			
			while ((index = message.indexOf(instantEmoji.getID(), index)) != -1)
				message = message.replaceFirst(instantEmoji.getLiteralPattern(), string + ChatColor.getLastColors(format + message.substring(0, index)));
		} return message;
	}
	
}
