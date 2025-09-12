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

package me.remigio07.chatplugin.server.gui;

import java.util.Map;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.server.gui.OpenActions;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUILayout;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;

public class SinglePageGUILayoutImpl extends SinglePageGUILayout {
	
	public SinglePageGUILayoutImpl(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
		super(id, rows, openActions, clickSound, titles);
	}
	
	public SinglePageGUILayoutImpl(Configuration configuration) {
		super(configuration);
	}
	
	public static class Builder extends SinglePageGUILayout.Builder {
		
		public Builder(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
			layout = new SinglePageGUILayoutImpl(id, rows, openActions, clickSound, titles);
		}
		
		public Builder(Configuration configuration) {
			layout = new SinglePageGUILayoutImpl(configuration);
		}
		
		@Override
		public SinglePageGUILayout build() {
			return (SinglePageGUILayout) layout;
		}
		
	}
	
}
