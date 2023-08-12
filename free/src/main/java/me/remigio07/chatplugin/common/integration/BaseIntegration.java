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

package me.remigio07.chatplugin.common.integration;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;

public abstract class BaseIntegration<T extends ChatPluginIntegration> implements ChatPluginIntegration {
	
	protected boolean enabled;
	protected IntegrationType<T> type;
	protected Object plugin, api;
	
	protected BaseIntegration(IntegrationType<T> type) {
		this.type = type;
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public IntegrationType<T> getType() {
		return type;
	}
	
	@Override
	public Object getPlugin() {
		return plugin;
	}
	
	@Override
	public Object getAPI() {
		return api;
	}
	
	protected void loadAPI() {
		// default
	}
	
}
