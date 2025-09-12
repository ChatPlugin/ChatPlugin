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

package me.remigio07.chatplugin.common.storage.database;

import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;

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
			engine = connector.getEngineName() + " v" + connector.getEngineVersion();
			
			for (DataContainer table : connector.getMissingDataContainers())
				connector.createDataContainer(table);
			connector.cleanOldPlayers();
			ensureCompatibility();
		} catch (Exception e) {
			throw new ChatPluginManagerException(this, e);
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		super.unload();
	}
	
}
