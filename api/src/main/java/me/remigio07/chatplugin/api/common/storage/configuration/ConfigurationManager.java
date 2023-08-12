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

package me.remigio07.chatplugin.api.common.storage.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.VersionChange;
import me.remigio07.chatplugin.api.common.util.annotation.NotNull;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;

/**
 * Manager that handles internal and custom {@link Configuration}s.
 */
public abstract class ConfigurationManager implements ChatPluginManager {
	
	protected static ConfigurationManager instance;
	protected boolean enabled;
	protected Map<ConfigurationType, Configuration> configurations = new HashMap<>();
	protected List<Configuration> customConfigurations = new ArrayList<>();
	protected VersionChange lastVersionChange;
	protected String path; // temp path string
	protected long loadTime;
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		if (!ChatPlugin.getInstance().isReloading())
			try {
				saveAll();
			} catch (IOException e) {
				throw new ChatPluginManagerException(this, e);
			}
		configurations.clear();
		customConfigurations.clear();
		
		path = null;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the loaded configurations' map.
	 * Do not modify the returned map.
	 * 
	 * @return Loaded configurations' map
	 */
	public Map<ConfigurationType, Configuration> getConfigurations() {
		return configurations;
	}
	
	/**
	 * Gets a configuration from its type.
	 * Will return <code>null</code> if <code>type == </code>
	 * {@link ConfigurationType#CUSTOM} or if it is not loaded.
	 * 
	 * @param type Configuration's type
	 * @return Corresponding {@link Configuration}
	 */
	@Nullable(why = "Null for ConfigurationType#CUSTOM or if not loaded")
	public Configuration getConfiguration(ConfigurationType type) {
		return configurations.get(type);
	}
	
	/**
	 * Gets the custom configurations' list.
	 * 
	 * @return Custom configurations' list
	 */
	@NotNull
	public List<Configuration> getCustomConfigurations() {
		return customConfigurations;
	}
	
	/**
	 * Loads every internal configuration from its file.
	 * 
	 * @throws IOException If something goes wrong
	 */
	public void loadAll() throws IOException {
		long ms = System.currentTimeMillis();
		
		for (Configuration configuration : configurations.values())
			configuration.load();
		LogManager.log("Loaded {0} configuration files in {1} ms.", 3, configurations.size(), System.currentTimeMillis() - ms);
	}
	
	/**
	 * Saves every internal configuration to its file.
	 * 
	 * @throws IOException If something goes wrong
	 */
	public void saveAll() throws IOException {
		long ms = System.currentTimeMillis();
		
		for (Configuration configuration : configurations.values()) {
			if (!configuration.getFile().exists())
				configuration.createFile();
			configuration.save();
		} LogManager.log("Saved {0} configuration files in {1} ms.", 3, configurations.size(), System.currentTimeMillis() - ms);
	}
	
	protected void putConfiguration(Configuration configuration) {
		configurations.put(configuration.getType(), configuration);
	}
	
	/**
	 * Adds a custom configuration to {@link #getCustomConfigurations()}.
	 * 
	 * @param customConfiguration Configuration to add
	 */
	public void addCustomConfiguration(Configuration customConfiguration) {
		customConfigurations.add(customConfiguration);
	}
	
	/**
	 * Gets the last version change occurred.
	 * 
	 * @return Last version change
	 */
	@NotNull
	public VersionChange getLastVersionChange() {
		return lastVersionChange;
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static ConfigurationManager getInstance() {
		return instance;
	}
	
	protected abstract void addAllDefaults(boolean forceAdd) throws IOException;
	
}
