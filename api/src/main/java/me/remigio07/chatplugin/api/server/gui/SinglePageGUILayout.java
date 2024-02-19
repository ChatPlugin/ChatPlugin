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

package me.remigio07.chatplugin.api.server.gui;

import java.util.Map;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

/**
 * Represents a single page GUI's layout.
 */
public class SinglePageGUILayout extends GUILayout {
	
	protected SinglePageGUILayout(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
		super(id, rows, openActions, clickSound, titles);
	}
	
	protected SinglePageGUILayout(Configuration configuration) {
		super(configuration);
	}
	
	/**
	 * Represents the builder used to create {@link SinglePageGUILayout}s using
	 * {@link GUIManager#createSinglePageGUILayoutBuilder(String, int, OpenActions, SoundAdapter, Map)}.
	 */
	public static abstract class Builder extends GUILayout.Builder {
		
		/**
		 * Builds the single page GUI layout.
		 * 
		 * @return New GUI layout
		 */
		public abstract SinglePageGUILayout build();
		
	}
	
}
