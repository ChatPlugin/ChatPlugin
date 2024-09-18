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

package me.remigio07.chatplugin.server.ad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.text.ClickActionAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.ad.Ad;
import me.remigio07.chatplugin.api.server.ad.AdManager;
import me.remigio07.chatplugin.api.server.event.ad.AdSendEvent;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.util.PlaceholderType;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.util.Utils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.event.HoverEvent;

public class AdManagerImpl extends AdManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		if (!ConfigurationType.ADS.get().getBoolean("ads.settings.enabled"))
			return;
		randomOrder = ConfigurationType.ADS.get().getBoolean("ads.settings.random-order");
		hasPrefix = ConfigurationType.ADS.get().getBoolean("ads.settings.prefix.enabled");
		soundEnabled = ConfigurationType.ADS.get().getBoolean("ads.settings.sound.enabled");
		prefix = ConfigurationType.ADS.get().getString("ads.settings.prefix.format");
		sound = new SoundAdapter(ConfigurationType.ADS.get(), "ads.settings.sound");
		sendingTimeout = Utils.getTime(ConfigurationType.ADS.get().getString("ads.settings.sending-timeout"), false, false);
		placeholderTypes = PlaceholderType.getPlaceholders(ConfigurationType.ADS.get().getStringList("ads.settings.placeholder-types"));
		
		for (String id : ConfigurationType.ADS.get().getKeys("ads")) {
			if (id.equals("settings"))
				continue;
			if (isValidAdID(id)) {
				if (getAd(id) == null) {
					Map<Language, String> texts = new HashMap<>();
					Map<Language, String> hovers = new HashMap<>();
					Map<Language, String> clickValues = new HashMap<>();
					List<Rank> disabledRanks = new ArrayList<>();
					
					for (Language language : LanguageManager.getInstance().getLanguages()) {
						String translatedText = ConfigurationType.ADS.get().getString("ads." + id + ".texts." + language.getID(), null);
						
						if (translatedText == null && language != Language.getMainLanguage())
							LogManager.log("Translation for language \"{0}\" not found at \"ads.{1}.texts.{0}\" in ads.yml.", 1, language.getID(), id);
						else texts.put(language, translatedText);
						
						hovers.put(language, ConfigurationType.ADS.get().getString("ads." + id + ".hovers." + language.getID(), null));
						clickValues.put(language, ConfigurationType.ADS.get().getString("ads." + id + ".click.values." + language.getID(), null));
					} for (String disabledRank : ConfigurationType.ADS.get().getStringList("ads." + id + ".disabled-ranks")) {
						Rank rank = RankManager.getInstance().getRank(disabledRank);
						
						if (rank == null)
							LogManager.log("Rank ID specified at \"ads.{0}.disabled-ranks\" in ads.yml (\"{1}\") is invalid as it does not belong to any loaded rank.", 1, id, disabledRank);
						else disabledRanks.add(rank);
					} try {
						ads.add(new Ad(
								id,
								texts,
								hovers,
								ClickActionAdapter.valueOf(ConfigurationType.ADS.get().getString("ads." + id + ".click.action")),
								clickValues,
								disabledRanks
								));
					} catch (IllegalArgumentException e) {
						LogManager.log("Translation for main language (\"{0}\") not found at \"ads.{1}.texts.{0}\" in ads.yml; skipping it.", 2, Language.getMainLanguage().getID(), id);
					}
				} else LogManager.log("An ad with ID \"{0}\" already exists in ads.yml; skipping it.", 1, id);
			} else LogManager.log("Ad ID specified at \"ads.{0}\" in ads.yml is invalid as it does not respect the following pattern: \"{1}\"; skipping it.", 2, id, AD_ID_PATTERN.pattern());
		} timerTaskID = TaskManager.scheduleAsync(this, 0L, sendingTimeout);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = false;
		
		TaskManager.cancelAsync(timerTaskID);
		ads.clear();
		placeholderTypes.clear();
		
		randomOrder = hasPrefix = soundEnabled = false;
		prefix = null;
		sound = null;
		sendingTimeout = 0;
		timerTaskID = timerIndex = -1;
	}
	
	@Override
	public void run() {
		if (!enabled)
			return;
		switch (ads.size()) {
		case 0:
			return;
		case 1:
			timerIndex = 0;
			break;
		default:
			if (randomOrder) {
				int randomIndex = timerIndex;
				
				while (randomIndex == timerIndex)
					randomIndex = ThreadLocalRandom.current().nextInt(ads.size());
				timerIndex = randomIndex;
			} else if (timerIndex + 1 == ads.size())
				timerIndex = 0;
			else timerIndex++;
			break;
		} Ad ad = ads.get(timerIndex);
		
		for (ChatPluginServerPlayer player : ServerPlayerManager.getInstance().getPlayers().values())
			if (!ad.getDisabledRanks().contains(player.getRank()))
				sendAd(ad, player);
	}
	
	@Override
	public void sendAd(Ad ad, ChatPluginServerPlayer player) {
		if (!enabled)
			return;
		AdSendEvent event = new AdSendEvent(ad, player);
		Language language = player.getLanguage();
		
		event.call();
		
		if (event.isCancelled())
			return;
		List<TextComponent> components = new ArrayList<>();
		
		if (player.getVersion().isAtLeast(Version.V1_8)) {
			String[] lines = ad.getText(language, true).split("\n");
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < lines.length; i++)
				sb.append((hasPrefix && !lines[i].isEmpty() ? prefix + lines[i] : lines[i]) + (i == lines.length - 1 ? "" : "\n"));
			components.add(Utils.deserializeLegacy(sb.toString(), true));
		} else for (String line : ad.getText(language, true).split("\n")) // https://bugs.mojang.com/browse/MC-39987
			components.add(Utils.deserializeLegacy((hasPrefix && !line.isEmpty() ? prefix + line : line), true));
		if (ad.getHover(language) != null)
			for (int i = 0; i < components.size(); i++)
				components.set(i, components.get(i).hoverEvent(HoverEvent.showText(Utils.deserializeLegacy(ad.getHover(language), true))));
		if (ad.getClickAction() != null && ad.getClickValue(language) != null)
			for (int i = 0; i < components.size(); i++)
				components.set(i, components.get(i).clickEvent(ClickEvent.clickEvent(Action.NAMES.value(ad.getClickAction().getID()), ad.getClickValue(language))));
		if (soundEnabled)
			player.playSound(sound);
		((BaseChatPluginServerPlayer) player).sendMessage(components.toArray(new TextComponent[1]));
	}
	
}
