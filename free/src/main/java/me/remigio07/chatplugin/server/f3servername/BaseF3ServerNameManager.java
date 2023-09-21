/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.server.f3servername;

import java.util.HashMap;
import java.util.Map;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerName;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

public abstract class BaseF3ServerNameManager extends F3ServerNameManager {
	
	protected long ms;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		ms = System.currentTimeMillis();
		
		if (!ConfigurationType.F3_SERVER_NAMES.get().getBoolean("f3-server-names.settings.enabled") || !checkAvailability(true))
			return;
		randomOrder = ConfigurationType.F3_SERVER_NAMES.get().getBoolean("f3-server-names.settings.random-order");
		sendingTimeout = ConfigurationType.F3_SERVER_NAMES.get().getLong("f3-server-names.settings.sending-timeout-ms");
		placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.F3_SERVER_NAMES.get().getStringList("f3-server-names.settings.placeholder-types"));
		
		for (String id : ConfigurationType.F3_SERVER_NAMES.get().getKeys("f3-server-names")) {
			if (id.equals("settings"))
				continue;
			if (isValidF3ServerNameID(id)) {
				if (getF3ServerName(id) == null) {
					Map<Language, String> texts = new HashMap<>();
					
					for (Language language : LanguageManager.getInstance().getLanguages()) {
						String translatedText = ConfigurationType.F3_SERVER_NAMES.get().getString("f3-server-names." + id + ".texts." + language.getID(), null);
						
						if (translatedText == null && language != Language.getMainLanguage())
							LogManager.log("Translation for language \"{0}\" not found at \"f3-server-names.{1}.texts.{0}\" in f3-server-names.yml.", 1, language.getID(), id);
						else texts.put(language, translatedText);
					} try {
						f3ServerNames.add(new F3ServerName(id, texts));
					} catch (IllegalArgumentException e) {
						LogManager.log("Translation for main language (\"{0}\") not found at \"f3-server-names.{1}.texts.{0}\" in f3-server-names.yml; skipping it.", 2, Language.getMainLanguage().getID(), id);
					}
				} else LogManager.log("An F3 server name with ID \"{0}\" already exists in f3-server-names.yml; skipping it.", 1, id);
			} else LogManager.log("F3 server name ID specified at \"f3-server-names.{0}\" in f3-server-names.yml is invalid as it does not respect the following pattern: \"{1}\"; skipping it.", 2, id, F3_SERVER_NAME_ID_PATTERN.pattern());
		}
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(timerTaskID);
		f3ServerNames.clear();
		placeholderTypes.clear();
		
		randomOrder = false;
		sendingTimeout = 0;
		timerTaskID = timerIndex = -1;
	}
	
}
