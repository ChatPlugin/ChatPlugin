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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.Utils;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import net.minecraft.server.MinecraftServer;

public class TPSManagerImpl extends TPSManager {
	
	private static BlockingQueue<Double> queue;
	private static Method getTickTime;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.CONFIG.get().getBoolean("tps.enabled") || !checkAvailability(true))
			return;
		if (!(tpsCapEnabled = ConfigurationType.CONFIG.get().getBoolean("tps.20-tps-cap.enabled")) && Environment.isFabric()) {
			LogManager.log("The 20 TPS cap at \"tps.20-tps-cap.enabled\" in config.yml is set to false but the server is running on Fabric: this setup is not supported; setting to default value of true.", 1);
			
			tpsCapEnabled = true;
		} updateTimeout = Utils.getTime(ConfigurationType.CONFIG.get().getString("tps.update-timeout"), false, false);
		Configuration messages = Language.getMainLanguage().getConfiguration();
		
		for (String id : ConfigurationType.CONFIG.get().getKeys("tps.qualities")) {
			double minimumTPS = ConfigurationType.CONFIG.get().getDouble("tps.qualities." + id);
			
			if (messages.contains("tps-qualities." + id))
				try {
					qualities.add(new TPSQuality(id, minimumTPS));
				} catch (IllegalArgumentException iae) {
					LogManager.log("Invalid minimum TPS ({0}) set at \"tps.qualities.{1}\" in config.yml: the number must be at least 0; skipping it.", 2, minimumTPS, id);
				}
			else LogManager.log("Missing translation at \"tps-qualities.{0}\" in {1}; skipping it.", 2, id, messages.getPath().getFileName().toString());
		} if (updateTimeout == -1) {
			LogManager.log("Invalid timestamp (\"{0}\") specified at \"tps.update-timeout\" in config.yml; setting to default value of 5 seconds.", 2, ConfigurationType.CONFIG.get().getString("tps.update-timeout"));
			
			updateTimeout = 5000L;
		} if (!Environment.isBukkit()) { // TODO: we could use this method on Bukkit (non-Spigot) too! (and reflection)
			if (Environment.isFabric() && VersionUtils.getVersion().isOlderThan(Version.V1_20_3))
				try {
					getTickTime = MinecraftServer.class.getMethod("method_3830");
				} catch (NoSuchMethodException nsme) {
					throw new ChatPluginManagerException(this, nsme);
				}
			if (updateTimeout > 900000L) {
				LogManager.log("Invalid timestamp (\"{0}\") specified at \"tps.update-timeout\" in config.yml: it cannot be more than 15 minutes; setting to default value of 5 seconds.", 2, ConfigurationType.CONFIG.get().getString("tps.update-timeout"));
				
				updateTimeout = 5000L;
			} int capacity = 900000 / (int) updateTimeout;
			
			if (queue != null) {
				if (queue.remainingCapacity() + queue.size() != capacity) {
					queue.clear();
					
					queue = new ArrayBlockingQueue<>(capacity);
				}
			} else queue = new ArrayBlockingQueue<>(capacity);
		} if (qualities.isEmpty()) {
			qualities.add(new TPSQuality("default-quality", 0));
			LogManager.log("No TPS qualities have been found at \"tps.qualities\" in config.yml.", 1);
		} else Collections.sort(qualities);
		
		timerTaskID = TaskManager.scheduleAsync(this, 0L, updateTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = tpsCapEnabled = false;
		
		if (queue != null && ChatPlugin.getState() != ChatPluginState.RELOADING)
			queue.clear();
		TaskManager.cancelAsync(timerTaskID);
		qualities.clear();
		
		recentTPS = new double[] { 20D, 20D, 20D };
		updateTimeout = 0L;
		timerTaskID = -1;
	}
	
	@Override
	public void run() {
		if (enabled)
			if (Environment.isBukkit())
				recentTPS = ((double[]) (VersionUtils.isPaper() && VersionUtils.getVersion().isAtLeast(Version.V1_21_9) // works from build #50
						? BukkitReflection.invokeMethod("MinecraftServer", "getTPS", BukkitReflection.invokeMethod("MinecraftServer", "getServer", null))
						: BukkitReflection.getFieldValue("MinecraftServer", BukkitReflection.invokeMethod("MinecraftServer", "getServer", null), "recentTps", "tps"))).clone();
			else {
				try {
					if (queue.remainingCapacity() == 0)
						queue.take();
					queue.offer(Environment.isSponge() ? Sponge.getServer().getTicksPerSecond() : VersionUtils.getVersion().isAtLeast(Version.V1_20_3) ? FabricBootstrapper.getInstance().getServer().getTickManager().getTickRate() : (1000F / (float) getTickTime.invoke(FabricBootstrapper.getInstance().getServer())));
				} catch (InterruptedException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
				
				recentTPS[0] = queue.stream().skip(queue.size() - Math.min(queue.size(), 60000 / updateTimeout)).mapToDouble(d -> d).average().orElse(20);
				recentTPS[1] = queue.stream().skip(queue.size() - Math.min(queue.size(), 300000 / updateTimeout)).mapToDouble(d -> d).average().orElse(20);
				recentTPS[2] = queue.stream().mapToDouble(d -> d).average().orElse(20);
			}
	}
	
	@Override
	public double getTPS(TPSTimeInterval interval) {
		return recentTPS[interval.ordinal()];
	}
	
	@Override
	public TPSQuality getTPSQuality(double tps) {
		if (tps < 0)
			throw new IllegalArgumentException("Specified TPS is less than 0");
		for (int i = qualities.size() - 1; i >= 0; i--) {
			TPSQuality quality = qualities.get(i);
			
			if (tps >= quality.getMinimumTPS())
				return quality;
		} return qualities.get(0);
	}
	
	@Override
	public String formatTPS(double tps, Language language) {
		boolean over20 = tps > 20;
		
		if (over20 && tpsCapEnabled)
			tps = 20D;
		return getTPSQuality(tps).getColor(language)
				+ (over20 && ConfigurationType.CONFIG.get().getBoolean("tps.20-tps-cap.add-wildcard") ? "*" : "")
				+ Utils.truncate(tps, 2);
	}
	
	@Override
	public String formatTPS(TPSTimeInterval interval, Language language) {
		return formatTPS(getTPS(interval), language);
	}
	
	@Override
	public String formatPlaceholders(String input, Language language) {
		return input
				.replace("{tps_1_min}", String.valueOf(Utils.truncate(getTPS(TPSTimeInterval.ONE_MINUTE), 2)))
				.replace("{tps_5_min}", String.valueOf(Utils.truncate(getTPS(TPSTimeInterval.FIVE_MINUTES), 2)))
				.replace("{tps_15_min}", String.valueOf(Utils.truncate(getTPS(TPSTimeInterval.FIFTEEN_MINUTES), 2)))
				.replace("{tps_1_min_format}", formatTPS(TPSTimeInterval.ONE_MINUTE, language))
				.replace("{tps_5_min_format}", formatTPS(TPSTimeInterval.FIVE_MINUTES, language))
				.replace("{tps_15_min_format}", formatTPS(TPSTimeInterval.FIFTEEN_MINUTES, language));
	}
	
	@Override
	public List<String> formatPlaceholders(List<String> input, Language language) {
		return input.stream().map(str -> formatPlaceholders(str, language)).collect(Collectors.toList());
	}
	
}
