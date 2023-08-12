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

package me.remigio07.chatplugin.common.util.bstats;

import me.remigio07.chatplugin.common.util.bstats.charts.CustomChart;

public abstract class CommonMetrics {
	
	protected MetricsBase metricsBase;
	
	public CommonMetrics addCustomChart(CustomChart chart) {
		metricsBase.addCustomChart(chart);
		return this;
	}
	
	public MetricsBase getMetricsBase() {
		return metricsBase;
	}
	
	public abstract CommonMetrics load();
	
	public abstract boolean areMetricsEnabled();
	
}
