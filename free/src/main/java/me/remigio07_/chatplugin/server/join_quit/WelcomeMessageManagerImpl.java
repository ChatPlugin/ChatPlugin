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

import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07_.chatplugin.api.server.join_quit.WelcomeMessageManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.language.LanguageManager;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.PlaceholderType;
import me.remigio07_.chatplugin.api.server.util.manager.PlaceholderManager;

public class WelcomeMessageManagerImpl extends WelcomeMessageManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.JOIN_QUIT_MODULES.get().getBoolean("join-quit-modules.welcome-messages.settings.enabled"))
			return;
		for (Language language : LanguageManager.getInstance().getLanguages()) {
			String translatedMessage = ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.welcome-messages.values." + language.getID(), null);
			
			if (translatedMessage == null && language != Language.getMainLanguage())
				LogManager.log("Translation for language \"{0}\" not found at \"join-quit-modules.welcome-messages.values.{0}\" in join-quit-modules.yml.", 1, language.getID());
			else welcomeMessages.put(language, translatedMessage);
		} if (welcomeMessages.get(Language.getMainLanguage()) == null) {
			LogManager.log("Translation for main language (\"{0}\") not found at \"join-quit-modules.welcome-messages.values.{0}\" in join-quit-modules.yml; disabling module.", 2, Language.getMainLanguage().getID());
			return;
		} delay = ConfigurationType.JOIN_QUIT_MODULES.get().getLong("join-quit-modules.welcome-messages.settings.delay-ms");
		placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.JOIN_QUIT_MODULES.get().getStringList("join-quit-modules.welcome-messages.settings.placeholder-types"));
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		welcomeMessages.clear();
		placeholderTypes.clear();
		
		delay = 0L;
	}
	
	@Override
	public void sendWelcomeMessage(ChatPluginServerPlayer player, boolean applyDelay) {
		if (enabled)
			TaskManager.runAsync(() -> player.sendMessage(PlaceholderManager.getInstance().translatePlaceholders(getWelcomeMessage(player.getLanguage(), true), player, placeholderTypes)), applyDelay ? delay : 0L);
	}
	
}
