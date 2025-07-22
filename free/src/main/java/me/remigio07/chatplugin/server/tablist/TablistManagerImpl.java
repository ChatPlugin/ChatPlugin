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

package me.remigio07.chatplugin.server.tablist;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;

import com.github.cliftonlabs.json_simple.Jsoner;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.event.tablist.TablistSendEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.tablist.Tablist;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;

public class TablistManagerImpl extends TablistManager {
	
	private Constructor<?> constructor;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.TABLISTS.get().getBoolean("tablists.settings.enabled"))
			return;
		enabled = true;
		randomOrder = ConfigurationType.TABLISTS.get().getBoolean("tablists.settings.random-order");
		sendingTimeout = ConfigurationType.TABLISTS.get().getLong("tablists.settings.sending-timeout-ms");
		playerNamesUpdateTimeout = ConfigurationType.TABLISTS.get().getLong("tablists.settings.player-names.update-timeout-ms");
		playerNamesPrefix = ConfigurationType.TABLISTS.get().getString("tablists.settings.player-names.prefix");
		playerNamesSuffix = ConfigurationType.TABLISTS.get().getString("tablists.settings.player-names.suffix");
		placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.TABLISTS.get().getStringList("tablists.settings.placeholder-types"));
		
		for (String id : ConfigurationType.TABLISTS.get().getKeys("tablists")) {
			if (id.equals("settings"))
				continue;
			if (isValidTablistID(id)) {
				if (getTablist(id) == null) {
					Map<Language, String> headers = new HashMap<>();
					Map<Language, String> footers = new HashMap<>();
					
					for (Language language : LanguageManager.getInstance().getLanguages()) {
						String translatedHeader = ConfigurationType.TABLISTS.get().getString("tablists." + id + ".headers." + language.getID(), null);
						String translatedFooter = ConfigurationType.TABLISTS.get().getString("tablists." + id + ".footers." + language.getID(), null);
						
						if (translatedHeader == null && language != Language.getMainLanguage())
							LogManager.log("Translation for language \"{0}\" not found at \"tablists.{1}.headers.{0}\" in tablists.yml.", 1, language.getID(), id);
						else headers.put(language, translatedHeader);
						if (translatedFooter == null && language != Language.getMainLanguage())
							LogManager.log("Translation for language \"{0}\" not found at \"tablists.{1}.footers.{0}\" in tablists.yml.", 1, language.getID(), id);
						else footers.put(language, translatedFooter);
					} try {
						tablists.add(new Tablist(id, headers, footers));
					} catch (NoSuchElementException nsee) {
						LogManager.log("Translation for main language (\"{0}\") not found at \"tablists.{1}.headers.{0}\" or \"tablists.{1}.footers.{0}\" in tablists.yml; skipping it.", 2, Language.getMainLanguage().getID(), id);
					}
				} else LogManager.log("A tablist with ID \"{0}\" already exists in tablists.yml; skipping it.", 1, id);
			} else LogManager.log("Tablist ID specified at \"tablists.{0}\" in tablists.yml does not respect the following pattern: \"{1}\"; skipping it.", 2, id, TABLIST_ID_PATTERN.pattern());
		} if (Environment.isBukkit() && VersionUtils.getVersion().isOlderThan(Version.V1_13_2)) { // check comments in sendTablist(...)
			Constructor<?>[] constructors = BukkitReflection.getLoadedClass("PacketPlayOutPlayerListHeaderFooter").getConstructors();
			constructor = constructors[constructors[0].getParameterCount() == 0 ? 0 : 1];
		} timerTaskID = TaskManager.scheduleAsync(this, 0L, sendingTimeout);
		playerNamesTimerTaskID = TaskManager.scheduleAsync(playerNamesUpdater = () -> {
			if (enabled)
				for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
					((BaseChatPluginServerPlayer) player).updatePlayerListName();
		}, 0L, playerNamesUpdateTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(timerTaskID);
		TaskManager.cancelAsync(playerNamesTimerTaskID);
		tablists.clear();
		placeholderTypes.clear();
		
		randomOrder = false;
		sendingTimeout = playerNamesUpdateTimeout = 0;
		playerNamesPrefix = playerNamesSuffix = null;
		timerTaskID = playerNamesTimerTaskID = timerIndex = -1;
		constructor = null;
		playerNamesUpdater = null;
	}
	
	@Override
	public void run() {
		if (!enabled)
			return;
		switch (tablists.size()) {
		case 0:
			break;
		case 1:
			timerIndex = 0;
			break;
		default:
			if (randomOrder) {
				int randomIndex = timerIndex;
				
				while (randomIndex == timerIndex)
					randomIndex = ThreadLocalRandom.current().nextInt(tablists.size());
				timerIndex = randomIndex;
			} else if (timerIndex + 1 == tablists.size())
				timerIndex = 0;
			else timerIndex++;
			break;
		} Tablist tablist = timerIndex == -1 ? null : tablists.get(timerIndex);
		
		for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
			if (tablist != null)
				sendTablist(tablist, player);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void sendTablist(Tablist tablist, ChatPluginServerPlayer player) {
		if (!enabled || !player.isLoaded())
			return;
		if (tablist != Tablist.NULL_TABLIST) {
			TablistSendEvent event = new TablistSendEvent(tablist, player);
			
			event.call();
			
			if (event.isCancelled())
				return;
		} Language language = player.getLanguage();
		
		if (Environment.isBukkit())
			if (VersionUtils.getVersion().isAtLeast(Version.V1_13_2)) // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/commits/39a287b7da8ad52ff94ba70a9a7584a9337d6f3e
				player.toAdapter().bukkitValue().setPlayerListHeaderFooter( // this method has been added in 1.13 but this bug ^ has been fixed in a 1.13.1 snapshot
						tablist.getHeader(language, true) == null ? null : PlaceholderManager.getInstance().translatePlaceholders(tablist.getHeader(language, true), player, placeholderTypes),
						tablist.getFooter(language, true) == null ? null : PlaceholderManager.getInstance().translatePlaceholders(tablist.getFooter(language, true), player, placeholderTypes)
						);
			else player.sendPacket(getHeaderFooterPacket(
					tablist.getHeader(language, true) == null ? null : PlaceholderManager.getInstance().translatePlaceholders(tablist.getHeader(language, true), player, placeholderTypes),
					tablist.getFooter(language, true) == null ? null : PlaceholderManager.getInstance().translatePlaceholders(tablist.getFooter(language, true), player, placeholderTypes),
					player
					));
		else player.toAdapter().spongeValue().getTabList().setHeaderAndFooter(
				tablist.getHeader(language, true) == null ? null : Utils.serializeSpongeText(PlaceholderManager.getInstance().translatePlaceholders(tablist.getHeader(language, true), player, placeholderTypes), false),
				tablist.getFooter(language, true) == null ? null : Utils.serializeSpongeText(PlaceholderManager.getInstance().translatePlaceholders(tablist.getFooter(language, true), player, placeholderTypes), false)
				);
	}
	
	private Object getHeaderFooterPacket(String header, String footer, ChatPluginServerPlayer player) {
		if (!enabled)
			return null;
		Object packet = null;
		
		try {
			packet = constructor.newInstance();
			
			// "a" and "b" need to be attempted before "header" and "footer"
			BukkitReflection.getField("PacketPlayOutPlayerListHeaderFooter", "a", "header").set(packet, BukkitReflection.getIChatBaseComponent("{\"text\":\"" + (header == null ? "" : Jsoner.escape(VersionUtils.getVersion().isAtLeast(Version.V1_13_2) ? header : player.getVersion().isAtLeast(Version.V1_13) ? header : header.replace("\n", "§r\n"))) + "\"}"));
			BukkitReflection.getField("PacketPlayOutPlayerListHeaderFooter", "b", "footer").set(packet, BukkitReflection.getIChatBaseComponent("{\"text\":\"" + (footer == null ? "" : Jsoner.escape(VersionUtils.getVersion().isAtLeast(Version.V1_13_2) ? footer : player.getVersion().isAtLeast(Version.V1_13) ? footer : footer.replace("\n", "§r\n"))) + "\"}"));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		} return packet;
	}
	
}
