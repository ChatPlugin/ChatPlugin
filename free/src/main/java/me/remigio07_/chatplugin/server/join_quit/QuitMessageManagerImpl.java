/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07_.chatplugin.server.join_quit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import me.remigio07_.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.api.common.util.text.ChatColor;
import me.remigio07_.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.language.LanguageManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.rank.Rank;
import me.remigio07_.chatplugin.api.server.rank.RankManager;
import me.remigio07_.chatplugin.api.server.util.PlaceholderType;
import me.remigio07_.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07_.chatplugin.api.server.util.manager.VanishManager;

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
		} if ((placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.JOIN_QUIT_MODULES.get().getStringList("join-quit-modules.quit-messages.settings.placeholder-types"))).contains(PlaceholderType.INTEGRATIONS)) {
			LogManager.log("The INTEGRATIONS placeholder type specified at \"join-quit-modules.quit-messages.settings.placeholder-types\" in join-quit-modules.yml is not supported; removing it.", 1);
			placeholderTypes.remove(PlaceholderType.INTEGRATIONS);
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		quitMessages.clear();
		placeholderTypes.clear();
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
			rank = player.getRank();
			playerID = player.getID();
			vanished = VanishManager.getInstance().isVanished(player);
			
			QuitMessageManager.getInstance().getQuitPackets().put(player.getUUID(), this);
		}
		
		@Override
		public String formatPlaceholders(String input, Language language) {
			String output = input;
			
			if (instance.getPlaceholderTypes().contains(PlaceholderType.JUST_NAME))
				output = output.replace("{player}", player.getName());
			if (instance.getPlaceholderTypes().contains(PlaceholderType.SERVER))
				output = PlaceholderManager.getInstance().translateServerPlaceholders(output, language);
			if (instance.getPlaceholderTypes().contains(PlaceholderType.PLAYER)) {
				output = output
						.replace("{player}", player.getName())
						.replace("{uuid}", player.getUUID().toString())
						.replace("{player_id}", String.valueOf(playerID))
						.replace("{rank_id}", rank.getID())
						.replace("{rank_display_name}", rank.getDisplayName())
						.replace("{prefix}", rank.getPrefix())
						.replace("{suffix}", rank.getSuffix())
						.replace("{tag_prefix}", rank.getTag().getPrefix())
						.replace("{tag_suffix}", rank.getTag().getSuffix())
						.replace("{tag_name_color}", rank.getTag().getNameColor())
						.replace("{chat_color}", rank.getChatColor())
						.replace("{rank_description}", rank.getDescription(language, true));
			} return ChatColor.translate(output.replace("{pfx}", language.getMessage("misc.prefix")));
		}
		
		public void setVanished(boolean vanished) {
			this.vanished = vanished;
		}
		
	}
	
}
