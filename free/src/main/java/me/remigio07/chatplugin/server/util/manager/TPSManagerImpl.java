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

package me.remigio07.chatplugin.server.util.manager;

import org.spongepowered.api.Sponge;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.util.manager.TPSManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;

public class TPSManagerImpl extends TPSManager {
	
	@Override
	public void load() {
		instance = this;
		long ms = System.currentTimeMillis();
		updateTimeout = ConfigurationType.CONFIG.get().getLong("tps.update-timeout-ms");
		Configuration messages = Language.getMainLanguage().getConfiguration();
		
		for (String id : ConfigurationType.CONFIG.get().getKeys("tps.qualities")) {
			double minTPS = ConfigurationType.CONFIG.get().getDouble("tps.qualities." + id);
			
			if (messages.contains("tps-qualities." + id))
				qualities.add(new TPSQuality(id, minTPS));
			else LogManager.log("Missing translation in messages.yml for TPS quality with ID " + id + "; skipping.", 2);
		} TPSQuality last = qualities.get(qualities.size() - 1);
		
		for (TPSQuality minTPS : qualities)
			if (minTPS.getMinTPS() < last.getMinTPS())
				last = minTPS;
		last.setMinTPS(0);
		timerTaskID = TaskManager.scheduleAsync(this, 0L, updateTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(timerTaskID);
		qualities.clear();
		
		recentTPS = new double[] { 20D, 20D, 20D };
		updateTimeout = 0L;
		timerTaskID = -1;
	}
	
	@Override
	public void run() {
		if (enabled)
			if (Environment.isBukkit()) {
				if (VersionUtils.isSpigot())
					recentTPS = ((double[]) BukkitReflection.getFieldValue("MinecraftServer", BukkitReflection.invokeMethod("MinecraftServer", "getServer", null), "recentTps")).clone();
			} else {
				double tps = Sponge.getServer().getTicksPerSecond();
				recentTPS = new double[] { tps, tps, tps };
				
				
				
//				System.out.println(Utils.getStringFromList(Arrays.asList(((MinecraftServerMixin_API) Sponge.getServer()).field_71311_j), false, false));
				
//				SpongeCommon
//				
//				for (long tps : ((MinecraftServerMixin_API) Sponge.getServer()).field_71311_j)
//					System.out.println(tps);
			}
	}
	
	@Override
	public double getTPS(TPSTimeInterval interval) {
		return recentTPS[interval.ordinal()];
	}
	
	@Override
	public TPSQuality getTPSQuality(double tps) {
		for (TPSQuality quality : qualities)
			if (tps >= quality.getMinTPS())
				return quality;
		return qualities.get(qualities.size() - 1);
	}
	
	@Override
	public String formatTPS(TPSTimeInterval interval, Language language) {
		return formatTPS(getTPS(interval), language);
	}
	
	@Override
	public String formatTPS(double tps, Language language) {
		boolean over20 = tps > 20;
		
		if (over20 && ConfigurationType.CONFIG.get().getBoolean("tps.20-tps-cap.enabled"))
			tps = 20D;
		return getTPSQuality(tps).getColor(language)
				+ (over20 && ConfigurationType.CONFIG.get().getBoolean("tps.20-tps-cap.add-wildcard") ? "*" : "")
				+ Utils.truncate(tps, 2);
	}
}
