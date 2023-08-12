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

package me.remigio07.chatplugin.server.rank;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.bootstrap.Environment;

public class RankManagerImpl extends RankManager {
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long temp, ms = System.currentTimeMillis();
		permissionFormat = ConfigurationType.RANKS.get().getString("ranks.settings.permission-format");
		sortingEnabled = ConfigurationType.RANKS.get().getBoolean("ranks.settings.sorting.enabled");
		sortingFromTablistTop = ConfigurationType.RANKS.get().getBoolean("ranks.settings.sorting.from-tablist-top");
		
		for (String id : ConfigurationType.RANKS.get().getKeys("ranks")) {
			if (id.equals("settings"))
				continue;
			if (ranks.size() == 99) {
				LogManager.log("Plugin has reached the limit of 99 ranks. Rank {0} and following ones will not be loaded.", 2, id);
				break;
			} if (isValidRankID(id)) {
				if (getRank(id) == null) {
					Map<Language, String> descriptions = new HashMap<>();
					
					for (Language language : LanguageManager.getInstance().getLanguages()) {
						String translatedDescription = ConfigurationType.RANKS.get().getString("ranks." + id + ".descriptions." + language.getID(), null);
						
						if (translatedDescription == null && language != Language.getMainLanguage())
							LogManager.log("Translation for language \"{0}\" not found at \"ranks.{1}.descriptions.{0}\" in ranks.yml.", 1, language.getID(), id);
						else descriptions.put(language, translatedDescription);
					} try {
						Rank rank = new RankImpl(
								id,
								ConfigurationType.RANKS.get().getString("ranks." + id + ".display-name"),
								ConfigurationType.RANKS.get().getString("ranks." + id + ".prefix"),
								ConfigurationType.RANKS.get().getString("ranks." + id + ".suffix"),
								ConfigurationType.RANKS.get().getString("ranks." + id + ".tag.prefix"),
								ConfigurationType.RANKS.get().getString("ranks." + id + ".tag.suffix"),
								ConfigurationType.RANKS.get().getString("ranks." + id + ".tag.name-color"),
								ConfigurationType.RANKS.get().getString("ranks." + id + ".chat-color"),
								ranks.size(),
								descriptions,
								new long[] {
										ConfigurationType.RANKS.get().contains("ranks." + id + ".max-punishment-durations.ban") ? (temp = Utils.getTime(ConfigurationType.RANKS.get().getString("ranks." + id + ".max-punishment-durations.ban"), false)) == -1 ? -2 : temp : ranks.size() == 0 ? 0L : ranks.get(ranks.size() - 1).getMaxPunishmentDurations()[0],
										ConfigurationType.RANKS.get().contains("ranks." + id + ".max-punishment-durations.mute") ? (temp = Utils.getTime(ConfigurationType.RANKS.get().getString("ranks." + id + ".max-punishment-durations.mute"), false)) == -1 ? -2 : temp : ranks.size() == 0 ? 0L : ranks.get(ranks.size() - 1).getMaxPunishmentDurations()[1],
								});
						
						for (int i = 0; i < 2; i++) {
							if (rank.getMaxPunishmentDurations()[i] == -2) {
								LogManager.log("Punishment duration specified at \"ranks.{0}.max-punishment-durations.{1}\" in ranks.yml (\"{2}\") is invalid; setting to default value of 0s.", 2, rank.getID(), i == 0 ? "ban" : "mute", ConfigurationType.RANKS.get().getString("ranks." + id + ".max-punishment-durations." + (i == 0 ? "ban" : "mute")));
								
								rank.getMaxPunishmentDurations()[i] = 0;
							}
						} ranks.add(rank);
					} catch (IllegalArgumentException e) {
						LogManager.log("Translation for main language (\"{0}\") not found at \"ranks.{1}.descriptions.{0}\" in ranks.yml; skipping it.", 2, Language.getMainLanguage().getID(), id);
					}
				} else LogManager.log("A rank with ID \"{0}\" already exists in ranks.yml; skipping it.", 1, id);
			} else LogManager.log("Rank ID specified at \"ranks.{0}\" in ranks.yml is invalid as it does not respect the following pattern: \"{1}\"; skipping it.", 2, id, RANK_ID_PATTERN.pattern());
		} defaultRank = getRank(ConfigurationType.RANKS.get().getString("ranks.settings.default-rank-id"));
		
		if (defaultRank == null)
			throw new ChatPluginManagerException(
					this,
					"Rank ID specified at \"ranks.settings.default-rank-id\" in ranks.yml (\"{0}\") is invalid as it does not belong to any loaded rank ({1}).",
					ConfigurationType.RANKS.get().getString("ranks.settings.default-rank-id"),
					Utils.getStringFromList(ranks.stream().map(Rank::getID).collect(Collectors.toList()), false, false)
					);
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = sortingEnabled = sortingFromTablistTop = false;
		
		ranks.clear();
		
		defaultRank = null;
		permissionFormat = null;
	}
	
	@Override
	public Rank calculateRank(ChatPluginServerPlayer player) {
		Rank playerRank = defaultRank;
		
		for (Rank rank : ranks)
			if (Environment.isBukkit() ? player.toAdapter().bukkitValue().hasPermission(rank.getPermission().bukkitValue()) : player.hasPermission(rank.getPermission().toString()))
				playerRank = rank;
		return playerRank;
	}
	
}
