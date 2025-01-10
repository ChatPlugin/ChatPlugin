/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
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

package me.remigio07.chatplugin.api.common.storage.flat_file;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;

/**
 * Represents the flat-file connector used by the plugin.
 */
public abstract class FlatFileConnector extends StorageConnector {
	
	@Override
	public @NotNull List<DataContainer> getMissingDataContainers() throws SQLException {
		return Arrays.asList(DataContainer.values()).stream().filter(container -> !container.getFlatFile().exists()).collect(Collectors.toList());
	}
	
	@Override
	public void createDataContainer(DataContainer container) throws SQLException, IOException {
		LogManager.log("Creating default data container \"{0}\"...", 0, container.getFlatFile().getName());
		container.getFlatFile().createNewFile();
	}
	
}
