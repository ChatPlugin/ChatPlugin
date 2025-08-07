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

import java.util.Collections;
import java.util.List;

import me.remigio07.chatplugin.api.common.storage.configuration.Configuration;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.Utils;
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
		
		if (!ConfigurationType.CONFIG.get().getBoolean("ping.enabled"))
			return;
		updateTimeout = Utils.getTime(ConfigurationType.CONFIG.get().getString("ping.update-timeout"), false, false);
		Configuration messages = Language.getMainLanguage().getConfiguration();
		
		for (String id : ConfigurationType.CONFIG.get().getKeys("ping.qualities")) {
			int maximumPing = ConfigurationType.CONFIG.get().getInt("ping.qualities." + id);
			
			if (messages.contains("ping." + id + ".color") && messages.contains("ping." + id + ".text"))
				try {
					qualities.add(new PingQuality(id, maximumPing));
				} catch (IllegalArgumentException iae) {
					LogManager.log("Invalid maximum ping ({0}) set at \"ping.qualities.{1}\" in config.yml: the number must be at least 0; skipping it.", 2, maximumPing, id);
				}
			else LogManager.log("Missing translation at \"ping.{0}\" in {1}; skipping it.", 2, id, messages.getPath().getFileName().toString());
		}  if (qualities.isEmpty()) {
			qualities.add(new PingQuality("default-quality", 0));
			LogManager.log("No ping qualities have been found at \"ping.qualities\" in config.yml.", 1);
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
	public PingQuality getPingQuality(int ping) {
		
		if (ping < 0)
			throw new IllegalArgumentException("Specified ping is less than 0");
		for (PingQuality quality : qualities)
			if (quality.getMaximumPing() >= ping)
				return quality;
		return qualities.get(qualities.size() - 1);
	}
	
	@Override
	public String formatPing(int ping, Language language) {
		return ChatColor.translate(getPingQuality(ping).getColor(language) + String.valueOf(ping));
	}
	
	@Override
	public String formatPlaceholders(String input, int ping, Language language) {
		PingQuality quality = getPingQuality(ping);
		return input
				.replace("{ping_format}", quality.getColor(language) + "{ping}") // :)
				.replace("{ping}", String.valueOf(ping))
				.replace("{ping_quality_color}", quality.getColor(language))
				.replace("{ping_quality_text}", quality.getText(language));
	}
	
	@Override
	public List<String> formatPlaceholders(List<String> input, int ms, Language language) {
		return null;
	}
	
}
