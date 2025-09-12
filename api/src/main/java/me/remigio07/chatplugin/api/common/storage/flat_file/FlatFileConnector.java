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

package me.remigio07.chatplugin.api.common.storage.flat_file;

import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;

/**
 * Represents the flat-file connector used by the plugin.
 */
public abstract class FlatFileConnector extends StorageConnector {
	
	@Override
	public @NotNull Set<DataContainer> getMissingDataContainers() {
		return Stream.of(DataContainer.values()).filter(container -> Files.exists(container.getFlatFile())).collect(Collectors.toCollection(() -> EnumSet.noneOf(DataContainer.class)));
	}
	
	@Override
	public void createDataContainer(DataContainer container) throws IOException {
		LogManager.log("Creating default data container \"{0}\"...", 0, container.getFlatFile().getFileName().toString());
		Files.createFile(container.getFlatFile());
	}
	
}
