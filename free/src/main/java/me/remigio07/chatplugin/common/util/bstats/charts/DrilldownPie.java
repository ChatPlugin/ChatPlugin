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

import java.util.Map;
import java.util.concurrent.Callable;

import me.remigio07.chatplugin.common.util.bstats.json.JsonObjectBuilder;

public class DrilldownPie extends CustomChart {

    private final Callable<Map<String, Map<String, Integer>>> callable;

    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     * @param callable The callable which is used to request the chart data.
     */
    public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
        super(chartId);
        this.callable = callable;
    }

    @Override
    public JsonObjectBuilder.JsonObject getChartData() throws Exception {
        JsonObjectBuilder valuesBuilder = new JsonObjectBuilder();

        Map<String, Map<String, Integer>> map = callable.call();
        if (map == null || map.isEmpty()) {
            // Null = skip the chart
            return null;
        }
        boolean reallyAllSkipped = true;
        for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
            JsonObjectBuilder valueBuilder = new JsonObjectBuilder();
            boolean allSkipped = true;
            for (Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {
                valueBuilder.appendField(valueEntry.getKey(), valueEntry.getValue());
                allSkipped = false;
            }
            if (!allSkipped) {
                reallyAllSkipped = false;
                valuesBuilder.appendField(entryValues.getKey(), valueBuilder.build());
            }
        }
        if (reallyAllSkipped) {
            // Null = skip the chart
            return null;
        }

        return new JsonObjectBuilder()
                .appendField("values", valuesBuilder.build())
                .build();
    }
}