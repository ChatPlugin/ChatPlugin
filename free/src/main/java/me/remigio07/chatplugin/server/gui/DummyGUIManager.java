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

package me.remigio07.chatplugin.server.gui;

import java.util.Map;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.server.gui.FillableGUILayout;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.GUILayout;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.gui.OpenActions;
import me.remigio07.chatplugin.api.server.gui.PerPlayerGUI;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUILayout;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

public class DummyGUIManager extends GUIManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public GUILayout createGUILayout(Configuration configuration) {
		return null;
	}
	
	@Override
	public SinglePageGUILayout.Builder createSinglePageGUILayoutBuilder(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
		return null;
	}
	
	@Override
	public FillableGUILayout.Builder createFillableGUILayoutBuilder(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
		return null;
	}
	
	@Override
	public GUI createGUI(GUILayout layout) {
		return null;
	}
	
	@Override
	public <T extends GUI & PerPlayerGUI> T createPerPlayerGUI(GUILayout layout, ChatPluginServerPlayer player) {
		return null;
	}
	
	@Override
	public Icon createIcon(Configuration configuration, String path) {
		return null;
	}
	
	@Override
	public @Nullable(why = "Player may not have a GUI open") GUI getOpenGUI(ChatPluginServerPlayer player) {
		return null;
	}
	
}
