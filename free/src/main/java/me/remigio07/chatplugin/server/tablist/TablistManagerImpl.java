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

package me.remigio07.chatplugin.server.tablist;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
import me.remigio07.chatplugin.api.server.tablist.custom_suffix.CustomSuffixManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.server.bukkit.BukkitReflection;

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
		prefixFormat = ConfigurationType.TABLISTS.get().getString("tablists.settings.format.prefix");
		suffixFormat = ConfigurationType.TABLISTS.get().getString("tablists.settings.format.suffix");
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
					} catch (IllegalArgumentException e) {
						LogManager.log("Translation for main language (\"{0}\") not found at \"tablists.{1}.headers.{0}\" or \"tablists.{1}.footers.{0}\" in tablists.yml; skipping it.", 2, Language.getMainLanguage().getID(), id);
					}
				} else LogManager.log("A tablist with ID \"{0}\" already exists in tablists.yml; skipping it.", 1, id);
			} else LogManager.log("Tablist ID specified at \"tablists.{0}\" in tablists.yml is invalid as it does not respect the following pattern: \"{1}\"; skipping it.", 2, id, TABLIST_ID_PATTERN.pattern());
		} if (Environment.isBukkit() && VersionUtils.getVersion().isOlderThan(Version.V1_13)) {
			Constructor<?>[] constructors = BukkitReflection.getLoadedClass("PacketPlayOutPlayerListHeaderFooter").getConstructors();
			constructor = constructors[constructors[0].getParameterCount() == 0 ? 0 : 1];
		} timerTaskID = TaskManager.scheduleAsync(this, 0L, sendingTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(timerTaskID);
		tablists.clear();
		placeholderTypes.clear();
		
		randomOrder = false;
		sendingTimeout = 0;
		prefixFormat = suffixFormat = null;
		timerTaskID = timerIndex = -1;
		constructor = null;
	}
	
	@Override
	public void run() {
		if (!enabled) // if (!enabled || (constructor == null && !Main.isSponge() && VersionUtils.getVersion().getProtocol() < 48)) TODO
			return;
		if (!enabled)
			return;
		switch (tablists.size()) {
		case 0:
			return;
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
		} Tablist tablist = tablists.get(timerIndex);
		
		for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values()) {
			sendTablist(tablist, player);
			CustomSuffixManager.getInstance().updateCustomSuffixes(player);
		}
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
			if (VersionUtils.getVersion().isAtLeast(Version.V1_13))
				player.toAdapter().bukkitValue().setPlayerListHeaderFooter(
						tablist.getHeader(language, true) == null ? null : PlaceholderManager.getInstance().translatePlaceholders(tablist.getHeader(language, true), player, placeholderTypes),
						tablist.getFooter(language, true) == null ? null : PlaceholderManager.getInstance().translatePlaceholders(tablist.getFooter(language, true), player, placeholderTypes)
						);
			else player.sendPacket(getHeaderFooterPacket(
					tablist.getHeader(language, true) == null ? null : PlaceholderManager.getInstance().translatePlaceholders(tablist.getHeader(language, true), player, placeholderTypes),
					tablist.getFooter(language, true) == null ? null : PlaceholderManager.getInstance().translatePlaceholders(tablist.getFooter(language, true), player, placeholderTypes)
					));
		else player.toAdapter().spongeValue().getTabList().setHeaderAndFooter(
				tablist.getHeader(language, true) == null ? null : Utils.serializeSpongeText(PlaceholderManager.getInstance().translatePlaceholders(tablist.getHeader(language, true), player, placeholderTypes), false),
				tablist.getFooter(language, true) == null ? null : Utils.serializeSpongeText(PlaceholderManager.getInstance().translatePlaceholders(tablist.getFooter(language, true), player, placeholderTypes), false)
				);
	}
	
	private Object getHeaderFooterPacket(String header, String footer) {
		if (!enabled)
			return null;
		Object packet = null;
		
		try {
			packet = constructor.newInstance();
			
			if (header == null)
				header = "";
			if (footer == null)
				footer = "";
			BukkitReflection.getField("PacketPlayOutPlayerListHeaderFooter", "a").set(packet, BukkitReflection.invokeMethod("ChatSerializer", "a", null, "{\"text\":\"" + header + "\"}"));
			BukkitReflection.getField("PacketPlayOutPlayerListHeaderFooter", "b").set(packet, BukkitReflection.invokeMethod("ChatSerializer", "a", null, "{\"text\":\"" + footer + "\"}"));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		} return packet;
	}
	
}
