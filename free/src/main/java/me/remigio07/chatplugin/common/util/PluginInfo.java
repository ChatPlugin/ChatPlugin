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

package me.remigio07.chatplugin.common.util;

import java.util.List;

public class PluginInfo {
	
	private String name, version;
	private List<String> authors;
	private boolean enabled;
	
	public PluginInfo(String name, String version, List<String> authors, boolean enabled) {
		this.name = name;
		int index = version.indexOf(':', version.indexOf(':') + 1);
		this.version = index == -1 ? version : version.substring(index + 1);
		this.authors = authors;
		this.enabled = enabled;
	}
	
	public String getName() {
		return name;
	}
	
	public String getVersion() {
		return version;
	}
	
	public List<String> getAuthors() {
		return authors;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
}
