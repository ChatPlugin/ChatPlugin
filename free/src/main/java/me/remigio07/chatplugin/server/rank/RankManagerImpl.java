/*
 * 	ChatPlugin - A feature-rich and modular chat ecosystem, lightweight and efficient by design.
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

package me.remigio07.chatplugin.server.rank;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.permissions.Permission;

import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.rank.Rank;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.bootstrap.Environment;
import me.remigio07.chatplugin.common.util.Utils;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.query.QueryOptions;

public class RankManagerImpl extends RankManager {
	
	public static final String DEFAULT_DESCRIPTION = "&7Default rank description.\n&7Change with &f/rank edit&7!";
	private static final List<String> PROPERTIES = Arrays.asList(
			"display-name", "prefix", "suffix", "tag.prefix", "tag.suffix", "tag.name-color",
			"chat-color", "max-punishment-durations.ban", "max-punishment-durations.mute");
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		luckPermsMode = ConfigurationType.RANKS.get().getBoolean("ranks.settings.luckperms-mode");
		sortingEnabled = ConfigurationType.RANKS.get().getBoolean("ranks.settings.sorting.enabled");
		sortingFromTablistTop = ConfigurationType.RANKS.get().getBoolean("ranks.settings.sorting.from-tablist-top");
		
		if (sortingEnabled && !ConfigurationType.CONFIG.get().getBoolean("settings.register-scoreboards") && VersionUtils.getVersion().isOlderThan(Version.V1_21_2)) {
			LogManager.log("The \"ranks.settings.sorting.enabled\" setting in ranks.yml is enabled but \"settings.register-scoreboards\" in config.yml is set to false. This setup is not supported: ChatPlugin needs to register its own scoreboards to sort ranks in versions older than 1.21.2; disabling sorting.", 2);
			
			sortingEnabled = false;
		} if (luckPermsMode) {
			try {
				for (Group group : LuckPermsProvider.get().getGroupManager().getLoadedGroups().stream().sorted((o1, o2) -> {
					int i = Integer.compare(o2.getWeight().orElse(0), o1.getWeight().orElse(0));
					return - (i != 0 ? i : o1.getName().compareToIgnoreCase(o2.getName()));
				}).collect(Collectors.toList())) {
					String id = group.getName();
					
					if (ranks.size() == 99) {
						LogManager.log("Plugin has reached the limit of 99 ranks. Rank {0} and following ones will not be loaded.", 2, id);
						break;
					} if (isValidRankID(id))
						ranks.add(new RankImpl(id, ranks.size()));
					else LogManager.log("Rank ID of LuckPerms' group \"{0}\" does not respect the following pattern: \"{1}\"; skipping it.", 2, id, RANK_ID_PATTERN.pattern());
				}
			} catch (NoClassDefFoundError ncdfe) {
				throw new ChatPluginManagerException(this, "\"ranks.settings.luckperms-mode\" in ranks.yml is set to true but LuckPerms is not installed or running correctly");
			} catch (IllegalStateException ise) {
				throw new ChatPluginManagerException(this, ise);
			} if (ranks.isEmpty())
				throw new ChatPluginManagerException(this, "LuckPerms has no valid groups, at least one is required");
			if (!ranks.stream().filter(rank -> rank.getID().equals("default")).findAny().isPresent())
				throw new ChatPluginManagerException(this, "LuckPerms does not have the required \"default\" group loaded");
		} else {
			for (String id : ConfigurationType.RANKS.get().getKeys("ranks")) {
				if (id.equals("settings"))
					continue;
				if (ranks.size() == 99) {
					LogManager.log("Plugin has reached the limit of 99 ranks. Rank {0} and following ones will not be loaded.", 2, id);
					break;
				} if (isValidRankID(id))
					if (getRank(id) == null)
						ranks.add(new RankImpl(id, ranks.size()));
					else LogManager.log("A rank with ID \"{0}\" already exists in ranks.yml; skipping it.", 1, id);
				else LogManager.log("Rank ID specified at \"ranks.{0}\" in ranks.yml does not respect the following pattern: \"{1}\"; skipping it.", 2, id, RANK_ID_PATTERN.pattern());
			} if (ranks.isEmpty())
				throw new ChatPluginManagerException(this, "there are no valid ranks in ranks.yml, at least one is required");
		} loadRanks();
		
		enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	/**
	 * @throws IllegalStateException
	 */
	public void loadRanks() {
		long temp;
		
		for (Rank rank : ranks) {
			String id = rank.getID();
			boolean first = rank.getPosition() == 0;
			RankImpl impl = (RankImpl) rank;
			
			if (luckPermsMode) {
				Group group = LuckPermsProvider.get().getGroupManager().getGroup(id);
				
				if (group != null) {
					String displayName = group.getDisplayName();
					CachedMetaData data = group.getCachedData().getMetaData();
					
					impl.setDisplayName(displayName == null ? id : displayName.equals("${empty_string}") ? "" : displayName);
					impl.setPrefix(data.getPrefix());
					impl.setSuffix(data.getSuffix());
					impl.setTagPrefix(data.getMetaValue("tag.prefix"));
					impl.setTagSuffix(data.getMetaValue("tag.suffix"));
					impl.setTagNameColor(data.getMetaValue("tag.name-color"));
					impl.setChatColor(data.getMetaValue("chat-color"));
					rank.getDescriptions().clear();
					LanguageManager.getInstance().getLanguages().forEach(language -> {
						String translatedDescription = data.getMetaValue("descriptions." + language.getID());
						
						rank.getDescriptions().put(language, translatedDescription == null ? language.equals(Language.getMainLanguage()) ? DEFAULT_DESCRIPTION : null : translatedDescription);
					});
					rank.getMaxPunishmentDurations()[0] = data.getMetaValue("max-punishment-durations.ban") == null ? 0L : (temp = Utils.getTime(data.getMetaValue("max-punishment-durations.ban"), false, true)) == -1 ? -2 : temp;
					rank.getMaxPunishmentDurations()[1] = data.getMetaValue("max-punishment-durations.mute") == null ? 0L : (temp = Utils.getTime(data.getMetaValue("max-punishment-durations.mute"), false, true)) == -1 ? -2 : temp;
					
					for (int i = 0; i < 2; i++) {
						if (rank.getMaxPunishmentDurations()[i] == -2) {
							LogManager.log("Invalid punishment duration specified through property \"max-punishment-durations.{0}\" of LuckPerms' group \"{1}\" (\"{2}\"); setting to default value of 0s.", 2, i == 0 ? "ban" : "mute", id, data.getMetaValue("max-punishment-durations." + (i == 0 ? "ban" : "mute")));
							
							rank.getMaxPunishmentDurations()[i] = 0;
						}
					}
				} else throw new IllegalStateException(id);
			} else {
				impl.setDisplayName(ConfigurationType.RANKS.get().getString("ranks." + id + ".display-name", id));
				impl.setPrefix(ConfigurationType.RANKS.get().getString("ranks." + id + ".prefix", first ? "" : ranks.get(rank.getPosition() - 1).getPrefix()));
				impl.setSuffix(ConfigurationType.RANKS.get().getString("ranks." + id + ".suffix", first ? "" : ranks.get(rank.getPosition() - 1).getSuffix()));
				impl.setTagPrefix(ConfigurationType.RANKS.get().getString("ranks." + id + ".tag.prefix", first ? "" : ranks.get(rank.getPosition() - 1).getTag().getPrefix()));
				impl.setTagSuffix(ConfigurationType.RANKS.get().getString("ranks." + id + ".tag.suffix", first ? "" : ranks.get(rank.getPosition() - 1).getTag().getSuffix()));
				impl.setTagNameColor(ConfigurationType.RANKS.get().getString("ranks." + id + ".tag.name-color", first ? "" : ranks.get(rank.getPosition() - 1).getTag().getNameColor()));
				impl.setChatColor(ConfigurationType.RANKS.get().getString("ranks." + id + ".chat-color", first ? "" : ranks.get(rank.getPosition() - 1).getChatColor()));
				rank.getDescriptions().clear();
				LanguageManager.getInstance().getLanguages().forEach(language -> rank.getDescriptions().put(language, ConfigurationType.RANKS.get().getString("ranks." + id + ".descriptions." + language.getID(), first ? language.equals(Language.getMainLanguage()) ? DEFAULT_DESCRIPTION : null : ranks.get(rank.getPosition() - 1).getDescription(language, false))));
				rank.getMaxPunishmentDurations()[0] = ConfigurationType.RANKS.get().contains("ranks." + id + ".max-punishment-durations.ban") ? (temp = Utils.getTime(ConfigurationType.RANKS.get().getString("ranks." + id + ".max-punishment-durations.ban"), false, true)) == -1 ? -2 : temp : first ? 0L : ranks.get(rank.getPosition() - 1).getMaxPunishmentDurations()[0];
				rank.getMaxPunishmentDurations()[1] = ConfigurationType.RANKS.get().contains("ranks." + id + ".max-punishment-durations.mute") ? (temp = Utils.getTime(ConfigurationType.RANKS.get().getString("ranks." + id + ".max-punishment-durations.mute"), false, true)) == -1 ? -2 : temp : first ? 0L : ranks.get(rank.getPosition() - 1).getMaxPunishmentDurations()[1];
				
				for (int i = 0; i < 2; i++) {
					if (rank.getMaxPunishmentDurations()[i] == -2) {
						LogManager.log("Invalid punishment duration specified at \"ranks.{0}.max-punishment-durations.{1}\" in ranks.yml (\"{2}\"); setting to default value of 0s.", 2, rank.getID(), i == 0 ? "ban" : "mute", ConfigurationType.RANKS.get().getString("ranks." + id + ".max-punishment-durations." + (i == 0 ? "ban" : "mute")));
						
						rank.getMaxPunishmentDurations()[i] = 0;
					}
				}
			}
		}
	}
	
	@Override
	public void unload() throws ChatPluginManagerException {
		enabled = luckPermsMode = sortingEnabled = sortingFromTablistTop = false;
		
		ranks.clear();
	}
	
	public Rank calculateRank(ChatPluginServerPlayer player) {
		Rank playerRank = getDefaultRank();
		
		if (luckPermsMode)
			for (Group group : LuckPermsProvider.get().getUserManager().getUser(player.getUUID()).getInheritedGroups(QueryOptions.nonContextual()).stream().sorted((o1, o2) -> {
				int i = Integer.compare(o2.getWeight().orElse(0), o1.getWeight().orElse(0));
				return - (i != 0 ? i : o1.getName().compareToIgnoreCase(o2.getName()));
			}).collect(Collectors.toList()))
				playerRank = getRank(group.getName());
		else for (Rank rank : ranks)
			if (Environment.isBukkit() ? player.toAdapter().bukkitValue().hasPermission((Permission) ((RankImpl) rank).getPermission()) : player.hasPermission("chatplugin.ranks." + rank.getID())) // does Sponge have an equivalent of Bukkit's PermissionDefault? let me know!
				playerRank = rank;
		return playerRank;
	}
	
	public static List<String> getProperties() {
		return Stream.concat(PROPERTIES.stream(), LanguageManager.getInstance().getLanguages().stream().map(language -> "descriptions." + language.getID())).collect(Collectors.toList());
	}
	
}
