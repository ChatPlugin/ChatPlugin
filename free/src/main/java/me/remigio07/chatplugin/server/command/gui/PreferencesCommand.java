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

package me.remigio07.chatplugin.server.command.gui;

import java.util.Arrays;
import java.util.List;

import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.server.command.PlayerCommand;
import me.remigio07.chatplugin.server.gui.InternalGUIs;

public class PreferencesCommand extends PlayerCommand {
	
	public PreferencesCommand() {
		super("/preferences");
	}
	
	@Override
	public List<String> getMainArgs() {
		return Arrays.asList("preferences", "settings");
	}
	
	@Override
	public void execute(ChatPluginServerPlayer player, String[] args) {
		if (GUIManager.getInstance().isEnabled() && InternalGUIs.getPreferencesLayout() != null) {
			SinglePageGUI gui = (SinglePageGUI) GUIManager.getInstance().getGUI("preferences-" + player.getName());
			
			TaskManager.runAsync(() -> (gui == null ? InternalGUIs.createPreferences(player) : gui).open(player, true), 0L);
		} else player.sendTranslatedMessage("misc.disabled-feature");
	}
	
}
