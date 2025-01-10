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

package me.remigio07.chatplugin.server.language;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.player.OfflinePlayer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.event.language.LanguageChangeEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.common.util.Utils;

public class LanguageManagerImpl extends LanguageManager {
	
	public static final List<UUID> COMMAND_COOLDOWNS = new ArrayList<>();
	private static long commandCooldown;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		commandCooldown = Utils.getTime(ConfigurationType.CONFIG.get().getString("languages.command.cooldown"), false, false);
		File customMessagesFolder = new File(ChatPlugin.getInstance().getDataFolder(), "custom-messages");
		File messagesItalian = new File(customMessagesFolder, "messages-italian.yml");
		
		if (commandCooldown == -1) {
			commandCooldown = 300000L;
			
			LogManager.log("Invalid timestamp (\"{0}\") specified at \"languages.command.cooldown\" in config.yml; setting to default value of 5 minutes.", 2, ConfigurationType.CONFIG.get().getString("languages.command.cooldown"));
		} customMessagesFolder.mkdirs();
		
		if (!messagesItalian.exists())
			try {
				Files.copy(ChatPlugin.class.getResourceAsStream("/messages-italian.yml"), messagesItalian.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		try {
			List<String> ids = ConfigurationType.CONFIG.get().getKeys("languages");
			
			if (!ids.contains("english"))
				throw new ChatPluginManagerException(this, "unable to load without the \"english\" language ID specified at \"languages\" in config.yml");
			for (String id : ids) {
				if (id.equalsIgnoreCase("command") || id.equalsIgnoreCase("detector") || id.equalsIgnoreCase("main-language-id"))
					continue;
				if (isValidLanguageID(id)) {
					languages.add(new LanguageImpl(
							id,
							ConfigurationType.CONFIG.get().translateString("languages." + id + ".display-name"),
							ConfigurationType.CONFIG.get().getStringList("languages." + id + ".country-codes")
							));
				} else LogManager.log("Language ID specified at \"languages.{0}\" in config.yml is invalid as it does not respect the following pattern: \"{1}\"; skipping it.", 2, id, LANGUAGE_ID_PATTERN.pattern());
			} for (Language language : languages) {
				if (getMainLanguage() != language)
					language.getConfiguration().load();
			}
		} catch (IOException e) {
			throw new ChatPluginManagerException(this, e);
		} mainLanguage = getLanguage(ConfigurationType.CONFIG.get().getString("languages.main-language-id"));
		
		if (mainLanguage == null)
			throw new ChatPluginManagerException(
					this,
					"Language ID specified at \"languages.main-language-id\" in config.yml (\"{0}\") is invalid as it does not belong to any loaded language ({1}).",
					ConfigurationType.CONFIG.get().getString("languages.main-language-id"),
					Utils.getStringFromList(languages.stream().map(Language::getID).collect(Collectors.toList()), false, false)
					);
		(detector = new LanguageDetectorImpl()).load();
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		detector.unload();
		languages.clear();
		
		detector = null;
		mainLanguage = null;
		commandCooldown = 0;
	}
	
	@Override
	public Language getLanguage(OfflinePlayer player) {
		if (player.isLoaded())
			return player.toServerPlayer().getLanguage();
		try {
			String id = StorageConnector.getInstance().getPlayerData(PlayersDataType.LANGUAGE, player);
			Language language = id == null ? mainLanguage : getLanguage(id);
			return language == null ? mainLanguage : language;
		} catch (SQLException e) {
			LogManager.log("SQLException occurred while getting {0}'s language from the storage: {1}", 2, player.getName(), e.getMessage());
			return mainLanguage;
		}
	}
	
	@Override
	public void setLanguage(OfflinePlayer player, Language language) {
		if (getLanguage(player) == language)
			throw new IllegalArgumentException("The specified language (" + language.getID() + ") corresponds to the old one");
		try {
			StorageConnector.getInstance().setPlayerData(PlayersDataType.LANGUAGE, player, language.getID());
			
			if (player.isLoaded()) {
				ServerPlayerManager.getInstance().unloadPlayer(player.getUUID());
				ServerPlayerManager.getInstance().loadPlayer(player.toAdapter());
			} new LanguageChangeEvent(player, language).call();
			LogManager.log("{0}'s language has been set to {1}.", 4, player.getName(), language.getID());
		} catch (SQLException | IOException e) {
			LogManager.log("{0} occurred while setting {1}'s language to {2}: {3}", 2, e.getClass().getSimpleName(), player.getName(), language.getDisplayName(), e.getMessage());
		}
	}
	
	public static boolean isCommandCooldownActive(ChatPluginServerPlayer player) {
		return COMMAND_COOLDOWNS.contains(player.getUUID());
	}
	
	public static void startCommandCooldown(UUID player) {
		COMMAND_COOLDOWNS.add(player);
		TaskManager.runAsync(() -> COMMAND_COOLDOWNS.remove(player), commandCooldown);
	}
	
}
