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

package me.remigio07.chatplugin.server.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.common.util.bstats.MetricsBase;
import me.remigio07.chatplugin.common.util.bstats.json.JsonObjectBuilder;
import me.remigio07.chatplugin.server.util.bstats.ServerMetrics;

public class BukkitMetrics extends ServerMetrics {

	private final JavaPlugin plugin;
	private final YamlConfiguration config;
	
	/**
	 * Creates a new Metrics instance.
	 *
	 * @param plugin Your plugin instance.
	 * @param serviceId The id of the service.
	 *				  It can be found at <a href="https://bstats.org/what-is-my-plugin-id">What is my plugin id?</a>
	 */
	@SuppressWarnings("deprecation")
	public BukkitMetrics(JavaPlugin plugin, int serviceId) {
		// Get the config file
		this.plugin = plugin;
		File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
		File configFile = new File(bStatsFolder, "config.yml");
		config = YamlConfiguration.loadConfiguration(configFile);
		
		if (!config.isSet("serverUuid")) {
			config.addDefault("enabled", true);
			config.addDefault("serverUuid", UUID.randomUUID().toString());
			config.addDefault("logFailedRequests", false);
			config.addDefault("logSentData", false);
			config.addDefault("logResponseStatusText", false);

			// Inform the server owners about bStats
			config.options().header(
					"bStats (https://bStats.org) collects some basic information for plugin authors, like how\n" +
					"many people use their plugin and their total player count. It's recommended to keep bStats\n" +
					"enabled, but if you're not comfortable with this, you can turn this setting off. There is no\n" +
					"performance penalty associated with having metrics enabled, and data sent to bStats is fully\n" +
					"anonymous."
			).copyDefaults(true);
			try {
				config.save(configFile);
			} catch (IOException ignored) { }
		}

		// Load the data
		boolean enabled = config.getBoolean("enabled", true);
		String serverUUID = config.getString("serverUuid");
		boolean logErrors = config.getBoolean("logFailedRequests", false);
		boolean logSentData = config.getBoolean("logSentData", false);
		boolean logResponseStatusText = config.getBoolean("logResponseStatusText", false);

		metricsBase = new MetricsBase(
				"bukkit",
				serverUUID,
				serviceId,
				enabled,
				this::appendPlatformData,
				this::appendServiceData,
				submitDataTask -> Bukkit.getScheduler().runTask(plugin, submitDataTask),
				plugin::isEnabled,
				(message, error) -> this.plugin.getLogger().log(Level.WARNING, message, error),
				(message) -> this.plugin.getLogger().log(Level.INFO, message),
				logErrors,
				logSentData,
				logResponseStatusText
		);
	}
	
	@Override
	public boolean areMetricsEnabled() {
		return ConfigurationType.CONFIG.get().getBoolean("settings.bstats-metrics-enabled", true) && config.getBoolean("enabled", true);
	}
	
	private void appendPlatformData(JsonObjectBuilder builder) {
		builder.appendField("playerAmount", PlayerAdapter.getOnlinePlayers().size());
		builder.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
		builder.appendField("bukkitVersion", Bukkit.getVersion());
		builder.appendField("bukkitName", Bukkit.getName());

		builder.appendField("javaVersion", System.getProperty("java.version"));
		builder.appendField("osName", System.getProperty("os.name"));
		builder.appendField("osArch", System.getProperty("os.arch"));
		builder.appendField("osVersion", System.getProperty("os.version"));
		builder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
	}

	private void appendServiceData(JsonObjectBuilder builder) {
		builder.appendField("pluginVersion", ChatPlugin.VERSION);
	}

}