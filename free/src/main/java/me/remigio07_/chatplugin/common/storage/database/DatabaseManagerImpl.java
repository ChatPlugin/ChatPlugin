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

package me.remigio07_.chatplugin.common.storage.database;

import me.remigio07_.chatplugin.api.common.storage.DataContainer;
import me.remigio07_.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07_.chatplugin.api.common.util.manager.ChatPluginManagerException;

public class DatabaseManagerImpl extends DatabaseManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		super.load();
		
		switch (method) {
		case H2:
			(connector = new H2Connector()).load();
			break;
		case SQLITE:
			(connector = new SQLiteConnector()).load();
			break;
		case MYSQL:
			throw new ChatPluginManagerException(this, "MySQL cannot be selected as storage method on the free version; only the following are allowed: H2, SQLITE, YAML, JSON.");
		default:
			return; // flat-file
		} try {
			for (DataContainer table : connector.getMissingDataContainers())
				connector.createDataContainer(table);
			connector.cleanOldPlayers();
			
			engineVersion = getConnector().getEngineVersion();
		} catch (Exception e) {
			throw new ChatPluginManagerException(this, e);
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		super.unload();
		
		engineVersion = null;
	}
	
}
