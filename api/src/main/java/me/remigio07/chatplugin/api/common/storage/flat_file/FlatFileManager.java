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

package me.remigio07.chatplugin.api.common.storage.flat_file;

import me.remigio07.chatplugin.api.common.storage.StorageManager;

/**
 * Manager that handles connection to the flat-files.
 * 
 * @see FlatFileConnector
 */
public abstract class FlatFileManager extends StorageManager {
	
	@Override
	public FlatFileConnector getConnector() {
		return (FlatFileConnector) connector;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static FlatFileManager getInstance() {
		return (FlatFileManager) instance;
	}
	
}
