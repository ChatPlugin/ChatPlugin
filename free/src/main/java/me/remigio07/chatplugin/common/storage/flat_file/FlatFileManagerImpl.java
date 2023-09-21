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

package me.remigio07.chatplugin.common.storage.flat_file;

import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.flat_file.FlatFileManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;

public class FlatFileManagerImpl extends FlatFileManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		super.load();
		
		try {
			switch (method) {
			case YAML:
				connector = new YAMLConnector();
				
				for (DataContainer container : connector.getMissingDataContainers())
					connector.createDataContainer(container);
				connector.load();
				break;
			case JSON:
				(connector = new JSONConnector()).load();
				break;
			default:
				return; // database
			}
		} catch (Exception e) {
			throw new ChatPluginManagerException(this, e);
		} connector.cleanOldPlayers();
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
}
