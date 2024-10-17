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

package me.remigio07.chatplugin.server.gui;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.server.gui.FillableGUILayout;
import me.remigio07.chatplugin.api.server.gui.Icon;
import me.remigio07.chatplugin.api.server.gui.IconLayout;
import me.remigio07.chatplugin.api.server.gui.IconType;
import me.remigio07.chatplugin.api.server.gui.OpenActions;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.common.util.Utils;

public class FillableGUILayoutImpl extends FillableGUILayout {
	
	public FillableGUILayoutImpl(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
		super(id, rows, openActions, clickSound, titles);
	}
	
	public FillableGUILayoutImpl(Configuration configuration) {
		super(configuration);
	}
	
	public static class Builder extends FillableGUILayout.Builder {
		
		private byte state = 0;
		
		public Builder(String id, int rows, OpenActions openActions, SoundAdapter clickSound, Map<Language, String> titles) {
			layout = new FillableGUILayoutImpl(id, rows, openActions, clickSound, titles);
		}
		
		public Builder(Configuration configuration) {
			layout = new FillableGUILayoutImpl(configuration);
		}
		
		@Override
		public Builder setSlots(int startSlot, int endSlot) {
			((FillableGUILayout) layout).setStartSlot(startSlot);
			((FillableGUILayout) layout).setEndSlot(endSlot);
			
			if ((state & 0b1) != 0b1)
				state += 1;
			return this;
		}
		
		@Override
		public Builder setEmptyListIcon(Icon emptyListIcon) {
			((FillableGUILayout) layout).setEmptyListIcon(emptyListIcon);
			
			if ((state & 0b10) != 0b10)
				state += 2;
			return this;
		}
		
		@Override
		public Builder setIconLayout(IconLayout iconLayout) {
			((FillableGUILayout) layout).setIconLayout(iconLayout);
			return this;
		}
		
		@Override
		public FillableGUILayout build() {
			if ((state & 0b11) != 0b11)
				throw new IllegalStateException("GUI cannot be built until the slots and the empty list icon have been set");
			if (layout.getIcons().stream().filter(icon -> icon != null && icon.getType() == IconType.PAGE_SWITCHER).anyMatch(icon -> icon.getPosition() >= ((FillableGUILayout) layout).getStartSlot() && icon.getPosition() <= ((FillableGUILayout) layout).getEndSlot()))
				throw new IndexOutOfBoundsException("A page switcher icon's position is inside of filling start-end slots' range (" + ((FillableGUILayout) layout).getStartSlot() + " - " + ((FillableGUILayout) layout).getEndSlot() + ")");
			if (layout.getIcons().stream().filter(Objects::nonNull).map(Icon::getID).collect(Collectors.toList()).containsAll(IconType.PAGE_SWITCHER_ICONS_IDS))
				return (FillableGUILayout) layout;
			throw new IllegalStateException("Not all page switcher icons' IDs have been specified (" + Utils.getStringFromList(IconType.PAGE_SWITCHER_ICONS_IDS, false, false) + ")");
		}
		
	}
	
}
