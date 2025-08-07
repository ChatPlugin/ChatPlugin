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

package me.remigio07.chatplugin.server.join_quit;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.join_quit.JoinTitleManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

public class JoinTitleManagerImpl extends JoinTitleManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.JOIN_QUIT_MODULES.get().getBoolean("join-quit-modules.join-titles.settings.enabled"))
			return;
		fadeIn = ConfigurationType.JOIN_QUIT_MODULES.get().getLong("join-quit-modules.join-titles.settings.fade-in-ms");
		stay = ConfigurationType.JOIN_QUIT_MODULES.get().getLong("join-quit-modules.join-titles.settings.stay-ms");
		fadeOut = ConfigurationType.JOIN_QUIT_MODULES.get().getLong("join-quit-modules.join-titles.settings.fade-out-ms");
		delay = ConfigurationType.JOIN_QUIT_MODULES.get().getLong("join-quit-modules.join-titles.settings.delay-ms");
		placeholderTypes = PlaceholderType.getTypes(ConfigurationType.JOIN_QUIT_MODULES.get().getStringList("join-quit-modules.join-titles.settings.placeholder-types"));
		
		for (Language language : LanguageManager.getInstance().getLanguages()) {
			String translatedTitle = ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.join-titles.titles." + language.getID(), null);
			String translatedSubtitle = ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.join-titles.subtitles." + language.getID(), null);
			
			if (translatedTitle == null && language != Language.getMainLanguage())
				LogManager.log("Translation for language \"{0}\" not found at \"join-quit-modules.join-titles.titles.{0}\" in join-quit-modules.yml.", 1, language.getID());
			else titles.put(language, translatedTitle);
			if (translatedSubtitle == null && language != Language.getMainLanguage())
				LogManager.log("Translation for language \"{0}\" not found at \"join-quit-modules.join-titles.subtitles.{0}\" in join-quit-modules.yml.", 1, language.getID());
			else subtitles.put(language, translatedSubtitle);
		} if (titles.get(Language.getMainLanguage()) == null) {
			LogManager.log("Translation for main language (\"{0}\") not found at \"join-quit-modules.join-titles.titles.{0}\" in join-quit-modules.yml; disabling module.", 2, Language.getMainLanguage().getID());
			return;
		} else if (subtitles.get(Language.getMainLanguage()) == null) {
			LogManager.log("Translation for main language (\"{0}\") not found at \"join-quit-modules.join-titles.subtitles.{0}\" in join-quit-modules.yml; disabling module.", 2, Language.getMainLanguage().getID());
			return;
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		titles.clear();
		subtitles.clear();
		placeholderTypes.clear();
		
		fadeIn = stay = fadeOut = delay = 0L;
	}
	
	@Override
	public void sendJoinTitle(ChatPluginServerPlayer player, boolean applyDelay) {
		if (enabled)
			TaskManager.runAsync(() -> {
				player.sendTitle(
						PlaceholderManager.getInstance().translatePlaceholders(getTitle(player.getLanguage(), true), player, placeholderTypes),
						PlaceholderManager.getInstance().translatePlaceholders(getSubtitle(player.getLanguage(), true), player, placeholderTypes),
						(int) fadeIn,
						(int) stay,
						(int) fadeOut
						);
			}, applyDelay ? delay : 0L);
	}
	
}
