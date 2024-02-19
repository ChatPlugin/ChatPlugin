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

package me.remigio07.chatplugin.server.join_quit;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.join_quit.SuggestedVersionManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;

public class SuggestedVersionManagerImpl extends SuggestedVersionManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.JOIN_QUIT_MODULES.get().getBoolean("join-quit-modules.suggested-version.enabled"))
			return;
		version = Version.getVersion(PlaceholderManager.getInstance().translateServerPlaceholders(ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.suggested-version.version"), Language.getMainLanguage()));
		
		if (version == Version.UNSUPPORTED) {
			LogManager.log("Version \"{0}\" specified at \"join-quit-modules.suggested-version.version\" is invalid; disabling module.", 2, ConfigurationType.JOIN_QUIT_MODULES.get().getString("join-quit-modules.suggested-version.version"));
			unload();
			return;
		} delay = ConfigurationType.JOIN_QUIT_MODULES.get().getLong("join-quit-modules.suggested-version.delay-ms");
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
			if (player.isLoaded())
				player.sendMessage(player.getLanguage().getMessage("misc.suggest-version")
						.replace("{suggested_version}", version.format())
						.replace("{suggested_version_protocol}", String.valueOf(version.getProtocol()))
						);
		}, delay);
	}
	
}
