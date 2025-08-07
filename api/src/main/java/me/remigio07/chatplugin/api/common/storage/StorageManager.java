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

package me.remigio07.chatplugin.api.common.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07.chatplugin.api.common.storage.flat_file.FlatFileManager;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;

/**
 * Manager used to store, read and write the plugin's data.
 * 
 * @see DatabaseManager
 * @see FlatFileManager
 * @see #getConnector()
 */
public abstract class StorageManager implements ChatPluginManager {
	
	protected static StorageManager instance;
	protected StorageConnector connector;
	protected boolean enabled;
	protected StorageMethod method;
	protected Path folder;
	protected long playersAutoCleanerPeriod = -1, loadTime;
	protected String engine; // unused: here to be read by the debugger
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void load() throws ChatPluginManagerException {
		try {
			Files.createDirectories(folder = Paths.get(ConfigurationType.CONFIG.get().getString("storage.folder").replace("{0}", ChatPlugin.getInstance().getDataFolder().toAbsolutePath().toString())));
		} catch (InvalidPathException | IOException e) {
			throw new ChatPluginManagerException(this, e);
		} method = StorageMethod.valueOf(ConfigurationType.CONFIG.get().getString("storage.method").toUpperCase());
		playersAutoCleanerPeriod = Utils.getTime(ConfigurationType.CONFIG.get().getString("storage.players-auto-cleaner-period"), false, false);
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		try {
			connector.unload();
		} catch (SQLException | IOException e) {
			throw new ChatPluginManagerException(this, e);
		} connector = null;
		method = null;
		folder = null;
		playersAutoCleanerPeriod = -1;
		engine = null;
	}
	
	/**
	 * Gets the connector currently in use.
	 * 
	 * @return Current storage connector
	 */
	public StorageConnector getConnector() {
		return connector;
	}
	
	/**
	 * Gets the storage method currently in use.
	 * 
	 * <p><strong>Found at:</strong> "storage.method" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Storage's method
	 */
	public StorageMethod getMethod() {
		return method;
	}
	
	/**
	 * Gets the folder used to store files.
	 * 
	 * <p><strong>Found at:</strong> "storage.folder" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Storage's folder
	 */
	public Path getFolder() {
		return folder;
	}
	
	/**
	 * Gets the period of time after which inactive players will be
	 * removed from the storage by the auto cleaner, in milliseconds.
	 * 
	 * <p><strong>Found at:</strong> "storage.players-auto-cleaner-period" in {@link ConfigurationType#CONFIG}</p>
	 * 
	 * @return Players' auto cleaner's period
	 */
	public long getPlayersAutoCleanerPeriod() {
		return playersAutoCleanerPeriod;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static StorageManager getInstance() {
		return instance;
	}
	
}
