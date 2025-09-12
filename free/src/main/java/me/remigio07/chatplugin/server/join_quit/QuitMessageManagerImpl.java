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

package me.remigio07.chatplugin.server.join_quit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;

public class QuitMessageManagerImpl extends QuitMessageManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.JOIN_QUIT_MODULES.get().getBoolean("join-quit-modules.quit-messages.settings.enabled"))
			return;
		for (String rankID : ConfigurationType.JOIN_QUIT_MODULES.get().getKeys("join-quit-modules.quit-messages")) {
			Rank rank = RankManager.getInstance().getRank(rankID);
			
			if (rank != null) {
				Map<Language, List<String>> quitMessages = new HashMap<>();
				
				for (Language language : LanguageManager.getInstance().getLanguages()) {
					List<String> messages = ConfigurationType.JOIN_QUIT_MODULES.get().getList("join-quit-modules.quit-messages." + rankID + "." + language.getID(), null);
					
					if (messages == null && language != Language.getMainLanguage())
						LogManager.log("Translations for language \"{0}\" not found at \"join-quit-modules.quit-messages.{1}.{0}\" in join-quit-modules.yml.", 1, language.getID(), rankID);
					else quitMessages.put(language, messages);
				} if (quitMessages.get(Language.getMainLanguage()) == null)
					LogManager.log("Translations for main language (\"{0}\") not found at \"join-quit-modules.quit-messages.{1}.{0}\" in join-quit-modules.yml; skipping it.", 2, Language.getMainLanguage().getID(), rankID);
				else this.quitMessages.put(rank, quitMessages);
			} else if (!rankID.equals("settings"))
				LogManager.log("Rank \"{0}\" specified at \"join-quit-modules.quit-messages.{0}\" is not loaded; skipping it.", 2, rankID);
		} if (!quitMessages.containsKey(RankManager.getInstance().getDefaultRank())) {
			LogManager.log("Quit messages for default rank {0} not found at \"join-quit-modules.quit-messages.{0}\"; disabling module.", 2, RankManager.getInstance().getDefaultRank().getID());
			unload();
			return;
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		quitMessages.clear();
	}
	
	@Override
	public String getQuitMessage(QuitPacket packet, Language language) {
		List<String> quitMessages = getQuitMessages(packet.getRank(), language);
		return packet.formatPlaceholders(quitMessages.get(ThreadLocalRandom.current().nextInt(quitMessages.size())), language);
	}
	
	@Override
	public void sendQuitMessage(QuitPacket packet) {
		if (!enabled || packet.isVanished())
			return;
		for (Language language : LanguageManager.getInstance().getLanguages()) {
			String message = getQuitMessage(packet, language);
			
			for (ChatPluginServerPlayer other : language.getOnlinePlayers())
				other.sendMessage(message);
		}
	}
	
	public static class QuitPacketImpl extends QuitPacket {
		
		public QuitPacketImpl(ChatPluginServerPlayer player) {
			this.player = new OfflinePlayer(player.getUUID(), player.getName());
			displayName = player.getDisplayName();
			rank = player.getRank();
			playerID = player.getID();
			vanished = player.isVanished();
			
			QuitMessageManager.getInstance().getQuitPackets().put(player.getUUID(), this);
		}
		
		@Override
		public String formatPlaceholders(String input, Language language) {
			return ChatColor.translate(
					rank.formatPlaceholders(input, language)
					.replace("{pfx}", language.getMessage("misc.prefix"))
					.replace("{player}", player.getName())
					.replace("{uuid}", player.getUUID().toString())
					.replace("{display_name}", displayName)
					.replace("{player_id}", String.valueOf(playerID))
					);
		}
		
		@Override
		public List<String> formatPlaceholders(List<String> input, Language language) {
			return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
		}
		
		@Override
		public String toString() {
			return "QuitPacketImpl{player=" + player + "}";
		}
		
		public void setVanished(boolean vanished) {
			this.vanished = vanished;
		}
		
	}
	
}
