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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.join_quit.ServerLinkManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;

public class ServerLinkManagerImpl extends ServerLinkManager {
	
	@Override
	public void load() {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.JOIN_QUIT_MODULES.get().getBoolean("join-quit-modules.server-links.settings.enabled") || !checkAvailability(true))
			return;
		placeholderTypes = PlaceholderType.getTypes(ConfigurationType.JOIN_QUIT_MODULES.get().getStringList("join-quit-modules.server-links.settings.placeholder-types"));
		
		for (String id : ConfigurationType.JOIN_QUIT_MODULES.get().getKeys("join-quit-modules.server-links.values")) {
			ServerLink.Type type = ServerLink.Type.value(id);
			ServerLink serverLink;
			
			try {
				if (type == null || type == ServerLink.Type.CUSTOM) {
					Map<Language, String> displayNames = new HashMap<>();
					
					for (Language language : LanguageManager.getInstance().getLanguages()) {
						String translatedDisplayName = ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.server-links.values." + id + ".display-names." + language.getID(), null);
						
						if (translatedDisplayName == null && language != Language.getMainLanguage())
							LogManager.log("Translation for language \"{0}\" not found at \"join-quit-modules.server-links.values.{1}.display-names.{0}\" in join-quit-modules.yml.", 1, language.getID(), id);
						else displayNames.put(language, translatedDisplayName);
					} serverLink = new ServerLink(id, new URI(ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.server-links.values." + id + ".uri")), displayNames);
				} else {
					String uri = ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.server-links.values." + id, null);
					serverLink = new ServerLink(id, type, new URI(uri == null ? ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.server-links.values." + id + ".uri") : uri));
				} serverLinks.add(serverLink);
			} catch (URISyntaxException urise) {
				LogManager.log("Invalid URI (\"{0}\") specified at \"join-quit-modules.server-links.values.{1}\" or \"join-quit-modules.server-links.values.{1}.uri\" in join-quit-modules.yml: {2}; skipping it.", 2, ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.server-links.values." + id + ".uri"), id, urise.getLocalizedMessage());
			} catch (NoSuchElementException nsee) {
				LogManager.log("Translation for main language (\"{0}\") not found at \"join-quit-modules.server-links.values.{1}.display-names.{0}\" in join-quit-modules.yml; skipping it.", 2, Language.getMainLanguage().getID(), id);
			}
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() {
		enabled = false;
		
		serverLinks.clear();
		placeholderTypes.clear();
	}
	
}
