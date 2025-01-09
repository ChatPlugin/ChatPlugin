/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.server.bukkit.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.packet.Packets;
import me.remigio07.chatplugin.api.server.event.f3servername.F3ServerNameSendEvent;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerName;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.common.f3servername.F3ServerNamePacket;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;

public class BukkitF3ServerNameManager extends F3ServerNameManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.F3_SERVER_NAMES.get().getBoolean("f3-server-names.settings.enabled") || !checkAvailability(true))
			return;
		randomOrder = ConfigurationType.F3_SERVER_NAMES.get().getBoolean("f3-server-names.settings.random-order");
		sendingTimeout = ConfigurationType.F3_SERVER_NAMES.get().getLong("f3-server-names.settings.sending-timeout-ms");
		placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.F3_SERVER_NAMES.get().getStringList("f3-server-names.settings.placeholder-types"));
		
		for (String id : ConfigurationType.F3_SERVER_NAMES.get().getKeys("f3-server-names")) {
			if (id.equals("settings"))
				continue;
			if (isValidF3ServerNameID(id)) {
				if (getF3ServerName(id) == null) {
					Map<Language, String> texts = new HashMap<>();
					
					for (Language language : LanguageManager.getInstance().getLanguages()) {
						String translatedText = ConfigurationType.F3_SERVER_NAMES.get().getString("f3-server-names." + id + ".texts." + language.getID(), null);
						
						if (translatedText == null && language != Language.getMainLanguage())
							LogManager.log("Translation for language \"{0}\" not found at \"f3-server-names.{1}.texts.{0}\" in f3-server-names.yml.", 1, language.getID(), id);
						else texts.put(language, translatedText);
					} try {
						f3ServerNames.add(new F3ServerName(id, texts));
					} catch (IllegalArgumentException e) {
						LogManager.log("Translation for main language (\"{0}\") not found at \"f3-server-names.{1}.texts.{0}\" in f3-server-names.yml; skipping it.", 2, Language.getMainLanguage().getID(), id);
					}
				} else LogManager.log("An F3 server name with ID \"{0}\" already exists in f3-server-names.yml; skipping it.", 1, id);
			} else LogManager.log("F3 server name ID specified at \"f3-server-names.{0}\" in f3-server-names.yml is invalid as it does not respect the following pattern: \"{1}\"; skipping it.", 2, id, F3_SERVER_NAME_ID_PATTERN.pattern());
		} try {
			Messenger messenger = Bukkit.getMessenger();
			Method method = messenger.getClass().getDeclaredMethod("addToOutgoing", new Class[] { Plugin.class, String.class });
			
			method.setAccessible(true);
			method.invoke(messenger, new Object[] { BukkitBootstrapper.getInstance(), CHANNEL_ID });
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new ChatPluginManagerException(this, e);
		} timerTaskID = TaskManager.scheduleAsync(this, 0L, sendingTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = randomOrder = false;
		
		TaskManager.cancelAsync(timerTaskID);
		f3ServerNames.clear();
		placeholderTypes.clear();
		
		sendingTimeout = 0;
		timerTaskID = timerIndex = -1;
	}
	
	@Override
	public void run() {
		if (!enabled)
			return;
		if (!enabled)
			return;
		switch (f3ServerNames.size()) {
		case 0:
			return;
		case 1:
			timerIndex = 0;
			break;
		default:
			if (randomOrder) {
				int randomIndex = timerIndex;
				
				while (randomIndex == timerIndex)
					randomIndex = ThreadLocalRandom.current().nextInt(f3ServerNames.size());
				timerIndex = randomIndex;
			} else if (timerIndex + 1 == f3ServerNames.size())
				timerIndex = 0;
			else timerIndex++;
			break;
		} F3ServerName f3ServerName = f3ServerNames.get(timerIndex);
		
		for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
			sendF3ServerName(f3ServerName, player);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void sendF3ServerName(F3ServerName f3ServerName, ChatPluginServerPlayer player) {
		if (!enabled)
			return;
		F3ServerNameSendEvent event = new F3ServerNameSendEvent(f3ServerName, player);
		
		event.call();
		
		if (event.isCancelled())
			return;
		if (ProxyManager.getInstance().isEnabled())
			ProxyManager.getInstance().sendPluginMessage(Packets.Messages.f3ServerName(
					ProxyManager.getInstance().getServerID(),
					player.getUUID(),
					PlaceholderManager.getInstance().translatePlaceholders(f3ServerName.getText(player.getLanguage(), true), player, placeholderTypes)
					));
		else if (VersionUtils.getVersion().isOlderThan(Version.V1_20_5))
			player.toAdapter().bukkitValue().sendPluginMessage(
				BukkitBootstrapper.getInstance(),
				CHANNEL_ID,
				new F3ServerNamePacket(PlaceholderManager.getInstance().translatePlaceholders(f3ServerName.getText(player.getLanguage(), true), player, placeholderTypes))
				.toArray()
				);
		else player.sendPacket(BukkitReflection.getInstance("ClientboundCustomPayloadPacket", new Class<?>[] { BukkitReflection.getLoadedClass("CustomPacketPayload") },
				BukkitReflection.getInstance("BrandPayload", PlaceholderManager.getInstance().translatePlaceholders(f3ServerName.getText(player.getLanguage(), true), player, placeholderTypes) + "§r")));
	}
	
}
