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

import java.util.function.BiConsumer;

import me.remigio07.chatplugin.common.util.bstats.json.JsonObjectBuilder;

public abstract class CustomChart {

    private final String chartId;

    protected CustomChart(String chartId) {
        if (chartId == null) {
            throw new IllegalArgumentException("chartId must not be null");
        }
        this.chartId = chartId;
    }

    public JsonObjectBuilder.JsonObject getRequestJsonObject(BiConsumer<String, Throwable> errorLogger, boolean logErrors) {
        JsonObjectBuilder builder = new JsonObjectBuilder();
        builder.appendField("chartId", chartId);
        try {
            JsonObjectBuilder.JsonObject data = getChartData();
            if (data == null) {
                // If the data is null we don't send the chart.
                return null;
            }
            builder.appendField("data", data);
        } catch (Throwable t) {
            if (logErrors) {

                errorLogger.accept("Failed to get data for custom chart with id " + chartId, t);
            }
            return null;
        }
        return builder.build();
    }

    protected abstract JsonObjectBuilder.JsonObject getChartData() throws Exception;

}
