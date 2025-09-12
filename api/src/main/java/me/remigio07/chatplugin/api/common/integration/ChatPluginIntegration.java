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

import me.remigio07.chatplugin.api.common.util.annotation.Nullable;

/**
 * Represents an integration handled by the {@link IntegrationManager}.
 * 
 * @see <a href="https://remigio07.me/chatplugin/wiki/modules/Integrations#plugin-integrations">ChatPlugin wiki/Modules/Integrations/Plugin integrations</a>
 */
public interface ChatPluginIntegration {
	
	/**
	 * Checks if this integration is enabled.
	 * 
	 * @return Whether this integration is enabled
	 */
	public boolean isEnabled();
	
	/**
	 * Gets this integration's type.
	 * 
	 * @return Integration's type
	 */
	public IntegrationType<?> getType();
	
	/**
	 * Gets this integration's plugin object.
	 * 
	 * @return Integrations' plugin object
	 */
	public Object getPlugin();
	
	/**
	 * Gets this integration's API object, if present.
	 * 
	 * <p>Will return <code>null</code> if not used.</p>
	 * 
	 * @return Integration's API object
	 */
	@Nullable(why = "Not all integrations use an API object")
	public Object getAPI();
	
	/**
	 * Loads this integration and enable it if present in the server.
	 */
	public void load();
	
}
