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

package me.remigio07.chatplugin.server.util.manager;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.manager.MSPTManager;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;

public class MSPTManagerImpl extends MSPTManager {
	
	@Override
	public void load() {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.CONFIG.get().getBoolean("mspt.enabled") || !checkAvailability(true))
			return;
		updateTimeout = Utils.getTime(ConfigurationType.CONFIG.get().getString("mspt.update-timeout"), false, false);
		Configuration messages = Language.getMainLanguage().getConfiguration();
		
		for (String id : ConfigurationType.CONFIG.get().getKeys("mspt.qualities")) {
			double maximumMSPT = ConfigurationType.CONFIG.get().getDouble("mspt.qualities." + id);
			
			if (messages.contains("mspt-qualities." + id))
				try {
					qualities.add(new MSPTQuality(id, maximumMSPT));
				} catch (IllegalArgumentException iae) {
					LogManager.log("Invalid maximum MSPT ({0}) set at \"mspt.qualities.{1}\" in config.yml: the number must be at least 0; skipping it.", 2, maximumMSPT, id);
				}
			else LogManager.log("Missing translation at \"mspt-qualities.{0}\" in {1}; skipping it.", 2, id, messages.getPath().getFileName().toString());
		} if (qualities.isEmpty()) {
			qualities.add(new MSPTQuality("default-quality", 0));
			LogManager.log("No MSPT qualities have been found at \"mspt.qualities\" in config.yml.", 1);
		} else Collections.sort(qualities);
		
		timerTaskID = TaskManager.scheduleAsync(this, 0L, updateTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(timerTaskID);
		qualities.clear();
		
		averageMSPT = array();
		minimumMSPT = array();
		maximumMSPT = array();
		updateTimeout = 0L;
		timerTaskID = -1;
	}
	
	@Override
	public void run() {
		if (enabled) {
			Object server = BukkitReflection.invokeMethod("MinecraftServer", "getServer", null);
			double[] times5s;
			double[] times10s;
			double[] times1m;
			
			if (VersionUtils.getVersion().isAtLeast(Version.V1_21_9)) { // works from build #49
				times5s = eval(BukkitReflection.getFieldValue("MinecraftServer", server, "tickTimes5s"));
				times10s = eval(BukkitReflection.getFieldValue("MinecraftServer", server, "tickTimes10s"));
				times1m = eval(BukkitReflection.getFieldValue("MinecraftServer", server, "tickTimes1m"));
			} else {
				times5s = evalOld(BukkitReflection.invokeMethod("TickTimes", "getTimes", BukkitReflection.getFieldValue("MinecraftServer", server, "tickTimes5s")));
				times10s = evalOld(BukkitReflection.invokeMethod("TickTimes", "getTimes", BukkitReflection.getFieldValue("MinecraftServer", server, "tickTimes10s")));
				times1m = evalOld(BukkitReflection.invokeMethod("TickTimes", "getTimes", BukkitReflection.getFieldValue("MinecraftServer", server, "tickTimes60s")));
			}
			
			averageMSPT[0] = times5s[0];
			averageMSPT[1] = times10s[0];
			averageMSPT[2] = times1m[0];
			minimumMSPT[0] = times5s[1];
			minimumMSPT[1] = times10s[1];
			minimumMSPT[2] = times1m[1];
			maximumMSPT[0] = times5s[2];
			maximumMSPT[1] = times10s[2];
			maximumMSPT[2] = times1m[2];
		}
	}
	
	private static double[] evalOld(Object times) {
		long min = Integer.MAX_VALUE;
		long max = 0L;
		long total = 0L;
		
		for (long value : (long[]) times) {
			if (value > 0 && value < min)
				min = value;
			if (value > max)
				max = value;
			total += value;
		} return new double[] { ((double) total / (double) ((long[]) times).length) * 1.0E-6, ((double) min) * 1.0E-6, ((double) max) * 1.0E-6 };
	}
	
	private static double[] eval(Object data) {
		Object reportData = BukkitReflection.invokeMethod("TickData", "generateTickReport", data, null, System.nanoTime(), BukkitReflection.invokeMethod("TickRateManager", "nanosecondsPerTick", BukkitReflection.invokeMethod("MinecraftServer", "tickRateManager", BukkitReflection.invokeMethod("MinecraftServer", "getServer", null))));
		
		if (reportData == null)
			return new double[] { 0.0, 0.0, 0.0 };
		Object segmentAll = BukkitReflection.invokeMethod("SegmentedAverage", "segmentAll", BukkitReflection.invokeMethod("TickReportData", "timePerTickData", reportData));
		return new double[] { (double) BukkitReflection.invokeMethod("SegmentData", "average", segmentAll) * 1.0E-6D, (double) BukkitReflection.invokeMethod("SegmentData", "least", segmentAll) * 1.0E-6D, (double) BukkitReflection.invokeMethod("SegmentData", "greatest", segmentAll) * 1.0E-6D };
	}
	
	@Override
	public double getAverageMSPT(MSPTTimeInterval interval) {
		return averageMSPT[interval.ordinal()];
	}
	
	@Override
	public double getMinimumMSPT(MSPTTimeInterval interval) {
		return minimumMSPT[interval.ordinal()];
	}
	
	@Override
	public double getMaximumMSPT(MSPTTimeInterval interval) {
		return maximumMSPT[interval.ordinal()];
	}
	
	@Override
	public MSPTQuality getMSPTQuality(double mspt) {
		if (mspt < 0)
			throw new IllegalArgumentException("Specified MSPT is less than 0");
		for (MSPTQuality quality : qualities)
			if (quality.getMaximumMSPT() >= mspt)
				return quality;
		return qualities.get(qualities.size() - 1);
	}
	
	@Override
	public String formatMSPT(double mspt, Language language) {
		return getMSPTQuality(mspt).getColor(language) + Utils.truncate(mspt, 2);
	}
	
	@Override
	public String formatAverageMSPT(MSPTTimeInterval interval, Language language) {
		return formatMSPT(getAverageMSPT(interval), language);
	}
	
	@Override
	public String formatMinimumMSPT(MSPTTimeInterval interval, Language language) {
		return formatMSPT(getMinimumMSPT(interval), language);
	}
	
	@Override
	public String formatMaximumMSPT(MSPTTimeInterval interval, Language language) {
		return formatMSPT(getMaximumMSPT(interval), language);
	}
	
	@Override
	public String formatPlaceholders(String input, Language language) {
		return input
				.replace("{mspt_5_sec_avg}", String.valueOf(Utils.truncate(getAverageMSPT(MSPTTimeInterval.FIVE_SECONDS), 2)))
				.replace("{mspt_5_sec_min}", String.valueOf(Utils.truncate(getMinimumMSPT(MSPTTimeInterval.FIVE_SECONDS), 2)))
				.replace("{mspt_5_sec_max}", String.valueOf(Utils.truncate(getMaximumMSPT(MSPTTimeInterval.FIVE_SECONDS), 2)))
				.replace("{mspt_10_sec_avg}", String.valueOf(Utils.truncate(getAverageMSPT(MSPTTimeInterval.TEN_SECONDS), 2)))
				.replace("{mspt_10_sec_min}", String.valueOf(Utils.truncate(getMinimumMSPT(MSPTTimeInterval.TEN_SECONDS), 2)))
				.replace("{mspt_10_sec_max}", String.valueOf(Utils.truncate(getMaximumMSPT(MSPTTimeInterval.TEN_SECONDS), 2)))
				.replace("{mspt_1_min_avg}", String.valueOf(Utils.truncate(getAverageMSPT(MSPTTimeInterval.ONE_MINUTE), 2)))
				.replace("{mspt_1_min_min}", String.valueOf(Utils.truncate(getMinimumMSPT(MSPTTimeInterval.ONE_MINUTE), 2)))
				.replace("{mspt_1_min_max}", String.valueOf(Utils.truncate(getMaximumMSPT(MSPTTimeInterval.ONE_MINUTE), 2)))
				.replace("{mspt_5_sec_avg_format}", formatAverageMSPT(MSPTTimeInterval.FIVE_SECONDS, language))
				.replace("{mspt_5_sec_min_format}", formatMinimumMSPT(MSPTTimeInterval.FIVE_SECONDS, language))
				.replace("{mspt_5_sec_max_format}", formatMaximumMSPT(MSPTTimeInterval.FIVE_SECONDS, language))
				.replace("{mspt_10_sec_avg_format}", formatAverageMSPT(MSPTTimeInterval.TEN_SECONDS, language))
				.replace("{mspt_10_sec_min_format}", formatMinimumMSPT(MSPTTimeInterval.TEN_SECONDS, language))
				.replace("{mspt_10_sec_max_format}", formatMaximumMSPT(MSPTTimeInterval.TEN_SECONDS, language))
				.replace("{mspt_1_min_avg_format}", formatAverageMSPT(MSPTTimeInterval.ONE_MINUTE, language))
				.replace("{mspt_1_min_min_format}", formatMinimumMSPT(MSPTTimeInterval.ONE_MINUTE, language))
				.replace("{mspt_1_min_max_format}", formatMaximumMSPT(MSPTTimeInterval.ONE_MINUTE, language));
	}
	
	@Override
	public List<String> formatPlaceholders(List<String> input, Language language) {
		return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
	}
	
}
