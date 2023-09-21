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

package me.remigio07.chatplugin.common.util.bstats.charts;

import java.util.concurrent.Callable;

import me.remigio07.chatplugin.common.util.bstats.json.JsonObjectBuilder;

public class SingleLineChart extends CustomChart {

    private final Callable<Integer> callable;

    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     * @param callable The callable which is used to request the chart data.
     */
    public SingleLineChart(String chartId, Callable<Integer> callable) {
        super(chartId);
        this.callable = callable;
    }

    @Override
    protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
        int value = callable.call();
        if (value == 0) {
            // Null = skip the chart
            return null;
        }
        return new JsonObjectBuilder()
                .appendField("value", value)
                .build();
    }

}