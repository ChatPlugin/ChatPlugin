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

import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07_.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07_.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07_.chatplugin.api.server.join_quit.SuggestedVersionManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07_.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07_.chatplugin.common.util.Utils;

public class SuggestedVersionManagerImpl extends SuggestedVersionManager {

	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.JOIN_QUIT_MODULES.get().getBoolean("join-quit-modules.suggested-version.enabled"))
			return;
		version = Version.getVersion(PlaceholderManager.getInstance().translateServerPlaceholders(ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.suggested-version.version"), Language.getMainLanguage()));
		delay = Utils.getTime(ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.suggested-version.delay"), false);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		version = null;
		delay = 0L;
	}
	
	@Override
	public void check(ChatPluginServerPlayer player) {
		if (!enabled || player.getVersion().isAtLeast(version) || (IntegrationType.GEYSERMC.isEnabled() && IntegrationType.GEYSERMC.get().isBedrockPlayer(player.toAdapter())))
			return;
		TaskManager.runAsync(() -> {
			if (player != null)
				player.sendMessage(translate(player.getLanguage().getMessage("misc.suggest-version")));
		}, delay);
	}
	
	private String translate(String input) {
		return enabled ? input
				.replace("{suggested_version}", version.format())
				.replace("{suggested_version_protocol}", String.valueOf(version.getProtocol()))
				: input;
	}
	
}
