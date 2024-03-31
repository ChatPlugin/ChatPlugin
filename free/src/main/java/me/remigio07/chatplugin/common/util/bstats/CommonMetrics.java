/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.common.util.bstats;

import me.remigio07.chatplugin.api.common.integration.ChatPluginIntegration;
import me.remigio07.chatplugin.api.common.integration.IntegrationManager;
import me.remigio07.chatplugin.api.common.storage.database.DatabaseManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManager;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagers;
import me.remigio07.chatplugin.common.util.bstats.charts.CustomChart;
import me.remigio07.chatplugin.common.util.bstats.charts.SimplePie;

public abstract class CommonMetrics {
	
	protected MetricsBase metricsBase;
	
	public CommonMetrics load() {
		addCustomChart(new SimplePie("databaseStorageMethod", () -> DatabaseManager.getInstance().getMethod().getName()));
		addCustomChart(new SimplePie("enabledIntegrations", () -> range(IntegrationManager.class, (int) IntegrationManager.getInstance().getIntegrations().values().stream().filter(ChatPluginIntegration::isEnabled).count())));
		return this;
	}
	
	protected static String range(Class<? extends ChatPluginManager> manager, int amount) {
		return ChatPluginManagers.getInstance().getManager(manager).isEnabled() ? String.valueOf(amount > 4 ? amount > 9 ? amount > 14 ? "15+" : "10-14" : "5-9" : "0-4") : "Disabled";
	}
	
	public CommonMetrics addCustomChart(CustomChart chart) {
		metricsBase.addCustomChart(chart);
		return this;
	}
	
	public MetricsBase getMetricsBase() {
		return metricsBase;
	}
	
	public abstract boolean areMetricsEnabled();
	
}
