/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
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

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.manager.PingManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import me.remigio07.chatplugin.server.bukkit.ChatPluginBukkitPlayer;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;

public class PingManagerImpl extends PingManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		updateTimeout = ConfigurationType.CONFIG.get().getLong("ping.update-timeout-ms");
		Configuration messages = Language.getMainLanguage().getConfiguration();
		
		for (String id : ConfigurationType.CONFIG.get().getKeys("ping.qualities")) {
			int maxMs = ConfigurationType.CONFIG.get().getInt("ping.qualities." + id);
			
			if (messages.contains("ping." + id + ".color") && messages.contains("ping." + id + ".text"))
				qualities.add(new PingQuality(id, maxMs));
			else LogManager.log("Missing translation in {0} for ping quality with ID {1}; skipping.", 2, messages.getFile().getName(), id);
		} PingQuality last = qualities.isEmpty() ? new PingQuality("default-quality", Integer.MAX_VALUE) : qualities.get(0);
		
		for (PingQuality maxMs : qualities)
			if (maxMs.getMaxMs() > last.getMaxMs())
				last = maxMs;
		if (qualities.isEmpty())
			qualities.add(last);
		last.setMaxMs(Integer.MAX_VALUE);
		
		timerTaskID = TaskManager.scheduleAsync(this, 0L, updateTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(timerTaskID);
		qualities.clear();
		
		updateTimeout = 0L;
		timerTaskID = -1;
	}
	
	@Override
	public void run() {
		if (enabled)
			for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
				((BaseChatPluginServerPlayer) player).setPing(getRealTimePing(player));
	}
	
	@Deprecated
	@Override
	public int getRealTimePing(ChatPluginServerPlayer player) {
		return player.isOnline() ? Environment.isBukkit() ? VersionUtils.getVersion().isAtLeast(Version.V1_16_5) ? player.toAdapter().bukkitValue().getPing() : (int) BukkitReflection.getFieldValue("EntityPlayer", BukkitReflection.invokeMethod("CraftPlayer", "getHandle", ((ChatPluginBukkitPlayer) player).getCraftPlayer()), "ping", "e") : player.toAdapter().spongeValue().getConnection().getLatency() : 0;
	}
	
	@Override
	public PingQuality getPingQuality(int ms) {
		for (PingQuality quality : qualities)
			if (ms <= quality.getMaxMs())
				return quality;
		return qualities.get(qualities.size() - 1);
	}
	
	@Override
	public String formatPing(int ms, Language language) {
		return ChatColor.translate(getPingQuality(ms).getColor(language) + String.valueOf(ms));
	}
	
}
