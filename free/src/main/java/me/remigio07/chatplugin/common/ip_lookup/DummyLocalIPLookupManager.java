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
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.common.ip_lookup;

import java.io.IOException;
import java.net.URL;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookup;
import me.remigio07.chatplugin.api.common.ip_lookup.LocalIPLookupManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;

public class DummyLocalIPLookupManager extends LocalIPLookupManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
	}
	
	@Override
	public void update(IPLookup lookup) {
		
	}
	
	@Override
	public void downloadDatabase(DatabaseType type) throws IOException {
		
	}
	
	@Override
	public URL formatURL(DatabaseType type) {
		return null;
	}
	
	@Override
	public Object getCityDatabaseReader() {
		return null;
	}
	
	@Override
	public Object getASNDatabaseReader() {
		return null;
	}
	
	@Override
	public void refreshDatabaseFile(DatabaseType type) throws IOException {
		
	}
	
}
