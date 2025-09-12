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

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.join_quit.JoinMessageManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

public class JoinMessageManagerImpl extends JoinMessageManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.JOIN_QUIT_MODULES.get().getBoolean("join-quit-modules.join-messages.settings.enabled"))
			return;
		for (String rankID : ConfigurationType.JOIN_QUIT_MODULES.get().getKeys("join-quit-modules.join-messages")) {
			Rank rank = RankManager.getInstance().getRank(rankID);
			
			if (rank != null) {
				Map<Language, List<String>> joinMessages = new HashMap<>();
				
				for (Language language : LanguageManager.getInstance().getLanguages()) {
					List<String> messages = ConfigurationType.JOIN_QUIT_MODULES.get().getList("join-quit-modules.join-messages." + rankID + "." + language.getID(), null);
					
					if (messages == null && language != Language.getMainLanguage())
						LogManager.log("Translations for language \"{0}\" not found at \"join-quit-modules.join-messages.{1}.{0}\" in join-quit-modules.yml.", 1, language.getID(), rankID);
					else joinMessages.put(language, messages);
				} if (joinMessages.get(Language.getMainLanguage()) == null)
					LogManager.log("Translations for main language (\"{0}\") not found at \"join-quit-modules.join-messages.{1}.{0}\" in join-quit-modules.yml; skipping it.", 2, Language.getMainLanguage().getID(), rankID);
				else this.joinMessages.put(rank, joinMessages);
			} else if (!rankID.equals("settings"))
				LogManager.log("Rank \"{0}\" specified at \"join-quit-modules.join-messages.{0}\" is not loaded; skipping it.", 2, rankID);
		} if (!joinMessages.containsKey(RankManager.getInstance().getDefaultRank())) {
			LogManager.log("Join messages for default rank {0} not found at \"join-quit-modules.join-messages.{0}\"; disabling module.", 2, RankManager.getInstance().getDefaultRank().getID());
			unload();
			return;
		} placeholderTypes = PlaceholderType.getTypes(ConfigurationType.JOIN_QUIT_MODULES.get().getStringList("join-quit-modules.join-messages.settings.placeholder-types"));
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		joinMessages.clear();
		placeholderTypes.clear();
	}
	
	@Override
	public String getJoinMessage(ChatPluginServerPlayer player, Language language) {
		List<String> joinMessages = getJoinMessages(player.getRank(), language);
		return PlaceholderManager.getInstance().translatePlaceholders(joinMessages.get(ThreadLocalRandom.current().nextInt(joinMessages.size())), player, language, placeholderTypes);
	}
	
	@Override
	public void sendJoinMessage(ChatPluginServerPlayer player) {
		if (!enabled)
			return;
		for (Language language : LanguageManager.getInstance().getLanguages()) {
			String message = getJoinMessage(player, language);
			
			for (ChatPluginServerPlayer other : language.getOnlinePlayers())
				other.sendMessage(message);
		}
	}
	
}
