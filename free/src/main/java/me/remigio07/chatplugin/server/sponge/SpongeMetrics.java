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

package me.remigio07.chatplugin.server.sponge;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.common.util.bstats.MetricsBase;
import me.remigio07.chatplugin.common.util.bstats.json.JsonObjectBuilder;
import me.remigio07.chatplugin.server.util.bstats.ServerMetrics;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class SpongeMetrics extends ServerMetrics {

	private final PluginContainer plugin;
	private final Path dataFolder;
//	private final int serviceId;

	private String serverUUID;
	private boolean logErrors = false;
	private boolean logSentData;
	private boolean logResponseStatusText;

	public SpongeMetrics(PluginContainer plugin, int serviceId) {
		this.plugin = plugin;
		this.dataFolder = ChatPlugin.getInstance().getDataFolder().getParentFile().toPath();
		
		try {
			loadConfig();
		} catch (IOException e) {
			// Failed to load configuration
			plugin.getLogger().warn("Failed to load bStats config!", e);
			return;
		}

		metricsBase = new MetricsBase(
				"sponge",
				serverUUID,
				serviceId,
				areMetricsEnabled(),
				this::appendPlatformData,
				this::appendServiceData,
				task -> {
					Scheduler scheduler = Sponge.getScheduler();
					Task.Builder taskBuilder = scheduler.createTaskBuilder();
					taskBuilder.execute(task).submit(plugin);
				},
				() -> true,
				plugin.getLogger()::warn,
				plugin.getLogger()::info,
				logErrors,
				logSentData,
				logResponseStatusText
		);
	}

	/**
	 * Loads the bStats configuration.
	 */
	private void loadConfig() throws IOException {
		File configPath = dataFolder.resolve("bStats").toFile();
		configPath.mkdirs();
		File configFile = new File(configPath, "config.conf");
		HoconConfigurationLoader configurationLoader = HoconConfigurationLoader.builder().setFile(configFile).build();
		CommentedConfigurationNode node;
		
		String serverUuidComment =
			"bStats (https://bStats.org) collects some basic information for plugin authors, like how\n" +
			"many people use their plugin and their total player count. It's recommended to keep bStats\n" +
			"enabled, but if you're not comfortable with this, you can disable data collection in the\n" +
			"Sponge configuration file. There is no performance penalty associated with having metrics\n" +
			"enabled, and data sent to bStats is fully anonymous.";

		if (!configFile.exists()) {
			configFile.createNewFile();
			node = configurationLoader.load();

			node.getNode("serverUuid").setValue(UUID.randomUUID().toString());
			node.getNode("logFailedRequests").setValue(false);
			node.getNode("logSentData").setValue(false);
			node.getNode("logResponseStatusText").setValue(false);
			node.getNode("serverUuid").setComment(serverUuidComment);
			node.getNode("configVersion").setValue(2);

			configurationLoader.save(node);
		} else {
			node = configurationLoader.load();

			if (!node.getNode("configVersion").isVirtual()) {

				node.getNode("configVersion").setValue(2);

				node.getNode("enabled").setComment(
						"Enabling bStats in this file is deprecated. At least one of your plugins now uses the\n" +
						"Sponge config to control bStats. Leave this value as you want it to be for outdated plugins,\n" +
						"but look there for further control");

				node.getNode("serverUuid").setComment(serverUuidComment);
				configurationLoader.save(node);
			}
		}

		// Load configuration
		serverUUID = node.getNode("serverUuid").getString();
		logErrors = node.getNode("logFailedRequests").getBoolean(false);
		logSentData = node.getNode("logSentData").getBoolean(false);
		logResponseStatusText = node.getNode("logResponseStatusText").getBoolean(false);
	}
	
	@Override
	public boolean areMetricsEnabled() {
		if (!ConfigurationType.CONFIG.get().getBoolean("settings.enable-bstats-metrics", true))
			return false;
		try {
			return (boolean) Class.forName("org.spongepowered.api.util.metric.MetricsConfigManager").getMethod("areMetricsEnabled", PluginContainer.class).invoke(Sponge.class.getMethod("getMetricsConfigManager").invoke(null), plugin);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			return true;
		}
	}
	
	private void appendPlatformData(JsonObjectBuilder builder) {
		builder.appendField("playerAmount",  Sponge.getServer().getOnlinePlayers().size());
		builder.appendField("onlineMode", Sponge.getServer().getOnlineMode() ? 1 : 0);
		builder.appendField("minecraftVersion", Sponge.getGame().getPlatform().getMinecraftVersion().getName());
		builder.appendField("spongeImplementation", VersionUtils.getImplementationName()); // Sponge v4.2

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