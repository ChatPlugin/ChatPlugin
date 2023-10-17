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

package me.remigio07.chatplugin.api.common.util.manager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import me.remigio07.chatplugin.api.common.player.PlayerManager;
import me.remigio07.chatplugin.api.common.storage.StorageMethod;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.server.chat.ChatManager;

/**
 * Class that handles ChatPlugin's managers.
 * 
 * <p>The current instance is provided using {@link #getInstance()}.</p>
 */
public abstract class ChatPluginManagers {
	
	protected static ChatPluginManagers instance;
	protected Map<Class<? extends ChatPluginManager>, ChatPluginManager> managers = new LinkedHashMap<>();
	
	/**
	 * Gets the loaded managers map.
	 * 
	 * <p>The returned map is an instance of {@link LinkedHashMap}.</p>
	 * 
	 * @return Loaded managers
	 */
	public Map<Class<? extends ChatPluginManager>, ChatPluginManager> getManagers() {
		return managers;
	}
	
	/**
	 * Gets a list containing enabled managers.
	 * 
	 * @return Enabled managers list
	 */
	public List<ChatPluginManager> getEnabledManagers() {
		return managers.values().stream().filter(ChatPluginManager::isEnabled).collect(Collectors.toList());
	}
	
	/**
	 * Unloads every manager in the list of loaded managers.
	 * 
	 * <p>Some managers do not need to be unloaded and in those cases nothing will happen.</p>
	 * 
	 * @throws ChatPluginManagerException If something goes wrong
	 */
	public void unloadManagers() throws ChatPluginManagerException {
		for (ChatPluginManager manager : Lists.reverse(managers.values().stream().filter(ChatPluginManager::isEnabled).collect(Collectors.toList())))
			manager.unload(); // lambdas don't feature exception handling :(
	}
	
	/**
	 * Gets a manager from the list of loaded managers.
	 * 
	 * <p>Will return <code>null</code> if the class is invalid.</p>
	 * 
	 * @param <T> Manager's type
	 * @param clazz Manager's API class
	 * @return Corresponding manager's implementation
	 */
	@SuppressWarnings("unchecked")
	@Nullable(why = "The specified class may be invalid")
	public <T extends ChatPluginManager> T getManager(Class<T> clazz) {
		return (T) managers.get(clazz);
	}
	
	/**
	 * Gets a manager from the list of loaded managers by its name.
	 * 
	 * <p>Will return <code>null</code> if the name is invalid.</p>
	 * 
	 * <p><strong>Example:</strong> "Chat" will return the current instance of {@link ChatManager}.
	 * The check is performed ignoring the string's case.</p>
	 * 
	 * @deprecated Names should not be used to identify managers. Use {@link #getManager(Class)} instead.
	 * @param name Manager's name
	 * @return Corresponding manager's implementation
	 * @throws IndexOutOfBoundsException If <code>name</code> does not contain "Manager"
	 */
	@Nullable(why = "The specified name may be invalid")
	@Deprecated
	public ChatPluginManager getManager(String name) {
		for (Class<?> clazz : managers.keySet())
			if (clazz.getSimpleName().substring(0, clazz.getSimpleName().indexOf("Manager")).equalsIgnoreCase(name))
				return managers.get(clazz);
		return null;
	}
	
	/**
	 * Adds a manager to {@link #getManagers()} and initializes it using {@link ChatPluginManager#load()}.
	 * 
	 * @param clazz Manager's API class
	 * @param manager Corresponding manager's implementation
	 * @throws ChatPluginManagerException If something goes wrong
	 */
	public void addManager(Class<? extends ChatPluginManager> clazz, ChatPluginManager manager) throws ChatPluginManagerException {
		managers.put(clazz, manager);
		manager.load();
	}
	
	/**
	 * Reloads every reloadable manager in the list of loaded managers.
	 * 
	 * <p>To check if a manager is reloadable, use {@link ChatPluginManager#isReloadable()}.</p>
	 * 
	 * @throws ChatPluginManagerException If something goes wrong
	 */
	public void reloadManagers() throws ChatPluginManagerException {
		List<ChatPluginManager> managers = this.managers.values().stream().filter(ChatPluginManager::isReloadable).collect(Collectors.toList());
		
		for (ChatPluginManager manager : Lists.reverse(managers.stream().filter(ChatPluginManager::isEnabled).collect(Collectors.toList())))
			manager.unload();
		for (ChatPluginManager manager : managers)
			manager.load();
		PlayerManager.getInstance().loadOnlinePlayers();
	}
	
	@SuppressWarnings("deprecation")
	protected StorageMethod getStorageMethod() throws ChatPluginManagerException {
		String method = ConfigurationType.CONFIG.get().getString("storage.method");
		
		try {
			return StorageMethod.valueOf(method.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new ChatPluginManagerException("managers utils", "Invalid storage method (\"{0}\") set at \"storage.method\" in config.yml; only the following are allowed: H2, SQLITE, MYSQL, YAML, JSON.", method);
		}
	}
	
	/**
	 * Gets the current instance of this class.
	 * 
	 * @return Current instance
	 */
	public static ChatPluginManagers getInstance() {
		return instance;
	}
	
	/**
	 * Initializes every manager in the loaded managers map.
	 * 
	 * @throws ChatPluginManagerException If something goes wrong
	 */
	public abstract void loadManagers() throws ChatPluginManagerException;
	
}
