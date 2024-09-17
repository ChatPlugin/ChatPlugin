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

package me.remigio07.chatplugin.server.actionbar;

import java.util.HashMap;
import java.util.Map;

import io.netty.util.internal.ThreadLocalRandom;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.actionbar.Actionbar;
import me.remigio07.chatplugin.api.server.actionbar.ActionbarManager;
import me.remigio07.chatplugin.api.server.event.actionbar.ActionbarSendEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

public class ActionbarManagerImpl extends ActionbarManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.ACTIONBARS.get().getBoolean("actionbars.settings.enabled") || !checkAvailability(true))
			return;
		randomOrder = ConfigurationType.ACTIONBARS.get().getBoolean("actionbars.settings.random-order");
		hasPrefix = ConfigurationType.ACTIONBARS.get().getBoolean("actionbars.settings.prefix.enabled");
		prefix = ConfigurationType.ACTIONBARS.get().getString("actionbars.settings.prefix.format");
		sendingTimeout = ConfigurationType.ACTIONBARS.get().getLong("actionbars.settings.sending-timeout-ms");
		placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.ACTIONBARS.get().getStringList("actionbars.settings.placeholder-types"));
		
		for (String id : ConfigurationType.ACTIONBARS.get().getKeys("actionbars")) {
			if (id.equals("settings"))
				continue;
			if (isValidActionbarID(id)) {
				if (getActionbar(id) == null) {
					Map<Language, String> texts = new HashMap<>();
					
					for (Language language : LanguageManager.getInstance().getLanguages()) {
						String translatedText = ConfigurationType.ACTIONBARS.get().getString("actionbars." + id + ".texts." + language.getID(), null);
						
						if (translatedText == null && language != Language.getMainLanguage())
							LogManager.log("Translation for language \"{0}\" not found at \"actionbars.{1}.texts.{0}\" in actionbars.yml.", 1, language.getID(), id);
						else texts.put(language, translatedText);
					} try {
						actionbars.add(new Actionbar(id, texts, ConfigurationType.ACTIONBARS.get().getBoolean("actionbars." + id + ".hidden")));
					} catch (IllegalArgumentException e) {
						LogManager.log("Translation for main language (\"{0}\") not found at \"actionbars.{1}.texts.{0}\" in actionbars.yml; skipping it.", 2, Language.getMainLanguage().getID(), id);
					}
				} else LogManager.log("An actionbar with ID \"{0}\" already exists in actionbars.yml; skipping it.", 1, id);
			} else LogManager.log("Actionbar ID specified at \"actionbars.{0}\" in actionbars.yml is invalid as it does not respect the following pattern: \"{1}\"; skipping it.", 2, id, ACTIONBAR_ID_PATTERN.pattern());
		} timerTaskID = TaskManager.scheduleAsync(this, 0L, sendingTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(timerTaskID);
		actionbars.clear();
		placeholderTypes.clear();
		
		randomOrder = hasPrefix = false;
		prefix = null;
		sendingTimeout = 0;
		timerTaskID = timerIndex = -1;
	}
	
	@Override
	public void run() {
		if (!enabled)
			return;
		switch (actionbars.size()) {
		case 0:
			return;
		case 1:
			timerIndex = 0;
			break;
		default:
			if (randomOrder) {
				int randomIndex = timerIndex;
				
				while (randomIndex == timerIndex)
					randomIndex = ThreadLocalRandom.current().nextInt(actionbars.size());
				timerIndex = randomIndex;
			} else if (timerIndex + 1 == actionbars.size())
				timerIndex = 0;
			else timerIndex++;
			break;
		} Actionbar actionbar = actionbars.get(timerIndex);
		
		if (!actionbar.isHidden())
			for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
				sendActionbar(actionbar, player);
	}
	
	@Override
	public void sendActionbar(Actionbar actionbar, ChatPluginServerPlayer player) {
		if (!enabled)
			return;
		ActionbarSendEvent event = new ActionbarSendEvent(actionbar, player);
		
		event.call();
		
		if (!event.isCancelled() && !actionbar.isHidden() && player.hasActionbarEnabled())
			player.sendActionbar(PlaceholderManager.getInstance().translatePlaceholders((hasPrefix ? prefix : "") + actionbar.getText(player.getLanguage(), true), player, placeholderTypes));
	}
	
}
