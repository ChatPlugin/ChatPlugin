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

package me.remigio07.chatplugin.api.common.integration;

import java.util.HashMap;
import java.util.Map;

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.bootstrap.Environment;

/**
 * Manager that handles {@link ChatPluginIntegration}s.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Integrations#plugin-integrations">ChatPlugin wiki/Modules/Integrations/Plugin integrations</a>
 */
public abstract class IntegrationManager implements ChatPluginManager {
	
	protected static IntegrationManager instance;
	protected boolean enabled;
	protected Map<IntegrationType<? extends ChatPluginIntegration>, ChatPluginIntegration> integrations = new HashMap<>();
	protected long loadTime;
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		integrations.clear();
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public boolean isReloadable() {
		return false;
	}
	
	/**
	 * Gets the integrations map, where the keys are
	 * {@link IntegrationType#getSupportedIntegrations()} and the values are
	 * the corresponding {@link ChatPluginIntegration}s' instances, which may
	 * either be enabled or not ({@link ChatPluginIntegration#isEnabled()}).
	 * 
	 * @return Integrations map
	 */
	public Map<IntegrationType<?>, ChatPluginIntegration> getIntegrations() {
		return integrations;
	}
	
	/**
	 * Gets an integration's instance.
	 * 
	 * <p>Will return <code>null</code> if
	 * <code>!{@link IntegrationType#getSupportedEnvironments()}.contains({@link Environment#getCurrent()})</code>.</p>
	 * 
	 * @param <T> Integration's type
	 * @param type Integration's type
	 * @return Integration's instance
	 */
	@SuppressWarnings("unchecked")
	@Nullable(why = "Specified integration may not be supported on current environment")
	public <T extends ChatPluginIntegration> T getIntegration(IntegrationType<T> type) {
		return (T) integrations.get(type);
	}
	
	protected void putIntegration(ChatPluginIntegration integration) {
		integrations.put(integration.getType(), integration);
	}
	
	/**
	 * Checks if <code>{@link IntegrationType#MATRIX}.isEnabled()
	 * || {@link IntegrationType#VULCAN}.isEnabled()
	 * || {@link IntegrationType#NEGATIVITY}.isEnabled()</code>.
	 * 
	 * <p>Will return <code>false</code> if <code>!</code>{@link #isEnabled()}.</p>
	 * 
	 * @return Whether at least one anti cheat is enabled
	 */
	public boolean isAtLeastOneAnticheatEnabled() {
		return enabled && (IntegrationType.MATRIX.isEnabled()
				|| IntegrationType.VULCAN.isEnabled()
				|| IntegrationType.NEGATIVITY.isEnabled());
	}
	
	/**
	 * Gets this manager's instance.
	 * 
	 * @return Manager's instance
	 */
	public static IntegrationManager getInstance() {
		return instance;
	}
	
}
