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

package me.remigio07_.chatplugin.api.server.gui;

import java.util.Map;

import me.remigio07_.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.util.adapter.user.SoundAdapter;

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
