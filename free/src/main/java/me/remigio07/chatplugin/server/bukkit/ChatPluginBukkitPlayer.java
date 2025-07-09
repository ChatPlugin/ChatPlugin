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

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition.WhereOperator;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.common.util.text.ChatColor;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.event.player.PlayerFirstJoinEvent;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07.chatplugin.api.server.join_quit.AccountCheckManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageDetectionMethod;
import me.remigio07.chatplugin.api.server.language.LanguageDetector;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.api.server.util.manager.PlaceholderManager;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.server.bossbar.NativeBossbar;
import me.remigio07.chatplugin.server.bossbar.ReflectionBossbar;
import me.remigio07.chatplugin.server.bukkit.manager.BukkitPlayerManager;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.rank.RankManagerImpl;
import me.remigio07.chatplugin.server.util.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class ChatPluginBukkitPlayer extends BaseChatPluginServerPlayer {
	
	private static final int MAX_TEAM_TEXT_LENGTH = VersionUtils.getVersion().isAtLeast(Version.V1_20_1) ? Integer.MAX_VALUE : VersionUtils.getVersion().isAtLeast(Version.V1_13) ? 64 : 16;
	private static Class<?> CraftPlayer = BukkitReflection.getLoadedClass("CraftPlayer");
	private static Field channels;
	private Player player;
	private Object craftPlayer;
	private Locale lastLocale;
	
	static {
		try {
			(channels = CraftPlayer.getDeclaredField("channels")).setAccessible(true);
		} catch (NoSuchFieldException nsfe) {
			throw new ExceptionInInitializerError(nsfe);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ChatPluginBukkitPlayer(Player player) {
		super(new PlayerAdapter(player));
		this.player = player;
		audience = BukkitPlayerManager.getAudiences().player(player);
		rank = ((RankManagerImpl) RankManager.getInstance()).calculateRank(this);
		version = version == null ? VersionUtils.getVersion() : version;
		craftPlayer = CraftPlayer.cast(player);
		playerConnection = BukkitReflection.getFieldValue("EntityPlayer", BukkitReflection.invokeMethod("CraftPlayer", "getHandle", craftPlayer), "playerConnection", "connection", VersionUtils.getVersion().isAtLeast(Version.V1_20) ? VersionUtils.getVersion().isAtLeast(Version.V1_21_3) ? "f" : "c" : "b");
		StorageConnector storage = StorageConnector.getInstance();
		
		if (playerStored)
			language = LanguageManager.getInstance().getLanguage(this);
		else {
			try {
				storage.insertNewPlayer(this);
			} catch (SQLException | IOException e) {
				LogManager.log("{0} occurred while inserting {1} in the storage: {2}", 2, e.getClass().getSimpleName(), name, e.getLocalizedMessage());
			} language = Language.getMainLanguage();
		} try {
			id = storage.getPlayerData(PlayersDataType.ID, this);
		} catch (SQLException sqle) {
			LogManager.log("SQLException occurred while getting the ID of {0} from the the database: {1}", 2, name, sqle.getLocalizedMessage());
		} if (BossbarManager.getInstance().isEnabled() && BossbarManager.getInstance().isWorldEnabled(getWorld())) {
			bossbar = VersionUtils.getVersion().isAtLeast(Version.V1_9) ? new NativeBossbar(this) : new ReflectionBossbar(this);
			
			if (BossbarManager.getInstance().isLoadingBossbarEnabled())
				BossbarManager.getInstance().startLoading(this);
			else BossbarManager.getInstance().sendBossbar(BossbarManager.getInstance().getBossbars().get(BossbarManager.getInstance().getTimerIndex() == -1 ? 0 : BossbarManager.getInstance().getTimerIndex()), this);
		} if (F3ServerNameManager.getInstance().isEnabled()) {
			try {
				((Set<String>) channels.get(player)).add(F3ServerNameManager.CHANNEL_ID);
			} catch (IllegalAccessException iae) {
				LogManager.log("IllegalAccessException occurred while enabling the F3 server names' channels of {1}: {2}", 2, name, iae.getLocalizedMessage());
			}
		} TaskManager.runAsync(() -> {
			try {
				String currentIPAddress = getIPAddress().getHostAddress();
				
				if (playerStored) { // update name and UUID
					if (!storage.getPlayerData(PlayersDataType.PLAYER_NAME, id).equals(name))
						storage.setPlayerData(PlayersDataType.PLAYER_NAME, id, name);
					else if (!storage.getPlayerData(PlayersDataType.PLAYER_UUID, id).equals(uuid.toString()))
						storage.setPlayerData(PlayersDataType.PLAYER_UUID, id, uuid.toString());
				} else { // language detection
					LanguageDetector detector = LanguageManager.getInstance().getDetector();
					
					if (detector.getMethod() == LanguageDetectionMethod.CLIENT_LOCALE) {
						TaskManager.runAsync(() -> {
							if (isLoaded()) {
								Language detected = detector.detectUsingClientLocale(this);
								
								if (!detected.equals(Language.getMainLanguage()))
									sendLanguageDetectedMessage(detected);
							}
						}, detector.getDelay());
					} else {
						long ms = System.currentTimeMillis();
						Language detected = detector.detectUsingGeolocalization(getIPLookup(true).join());
						
						if (!detected.equals(Language.getMainLanguage()))
							TaskManager.runAsync(() -> sendLanguageDetectedMessage(detected), detector.getDelay() - (System.currentTimeMillis() - ms));
					}
				} if (IPLookupManager.getInstance().isEnabled()) { // update IP address(es)
					if (ipLookup == null && IPLookupManager.getInstance().isLoadOnJoin())
						getIPLookup(true).join();
					String lastIPAddress = storage.getPlayerData(PlayersDataType.PLAYER_IP, id);
					
					if (currentIPAddress.equals(lastIPAddress))
						return;
					int maxIPsStored = IPLookupManager.getInstance().getMaxIPsStored();
					
					if (lastIPAddress != null && maxIPsStored != 1) {
						List<String> ipAddresses = Utils.getListFromString(storage.select(DataContainer.IP_ADDRESSES, "ip_addresses", String.class, new WhereCondition("player_id", WhereOperator.EQUAL, id)));
						
						if (!ipAddresses.contains(lastIPAddress)) {
							ipAddresses.add(0, lastIPAddress);
							
							if (ipAddresses.size() > maxIPsStored)
								ipAddresses = ipAddresses.subList(0, maxIPsStored);
							storage.setData(DataContainer.IP_ADDRESSES, "ip_addresses", id, Utils.getStringFromList(ipAddresses, false, false));
						}
					}
				} storage.setPlayerData(PlayersDataType.PLAYER_IP, id, currentIPAddress);
			} catch (SQLException | IOException e) {
				LogManager.log("{0} occurred while getting/setting the name or IP address(es) of {1} from/in the storage: {2}", 2, e.getClass().getSimpleName(), name, e.getLocalizedMessage());
			} if (!playerStored) {
				if (AccountCheckManager.getInstance().isPerformOnFirstJoin())
					AccountCheckManager.getInstance().check(this);
				new PlayerFirstJoinEvent(this).call();
			}
		}, 0L);
	}
	
	@Override
	public InetAddress getIPAddress() {
		return player.getAddress().getAddress();
	}
	
	@Override
	public void sendMessage(String message) {
		if (version.isOlderThan(Version.V1_8)) // https://bugs.mojang.com/browse/MC-39987
			for (String str : message.split("\n"))
				player.sendMessage(str);
		else player.sendMessage(message);
	}
	
	@Override
	public void connect(String server) {
		if (!ProxyManager.getInstance().getServersIDs().contains(server))
			LogManager.log("The plugin tried to connect a player ({0}) to a server which is not under the proxy ({1}). You should fix this immediately, otherwise kicks and bans will not work.", 2, name, server);
		ProxyManager.getInstance().connect(this, server);
	}
	
	@Override
	public void disconnect(String reason) {
		if (Bukkit.isPrimaryThread())
			player.kickPlayer(reason);
		else TaskManager.runSync(() -> disconnect(reason), 0L);
	}
	
	@Override
	public double getDistance(double x, double y, double z) {
		return player.getLocation().distance(new Location(player.getWorld(), x, y, z));
	}
	
	@Override
	public void sendMessage(Component... components) {
		for (Component component : components)
			audience.sendMessage(component);
	}
	
	public void updatePlayerListName() {
		if (ConfigurationType.CONFIG.get().getBoolean("settings.register-scoreboards")) {
			for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
				((ChatPluginBukkitPlayer) other).setupTeams(this);
				setupTeams(other);
			} setupTeams(this);
		} else setPlayerListName(
				PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPrefixFormat(), this, TablistManager.getInstance().getPlaceholderTypes())
				+ name
				+ PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getSuffixFormat(), this, TablistManager.getInstance().getPlaceholderTypes())
				);
	}
	
	@SuppressWarnings("deprecation")
	private void setupTeams(ChatPluginServerPlayer other) {
		String prefix = PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPrefixFormat(), other, language, TablistManager.getInstance().getPlaceholderTypes());
		String suffix = PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getSuffixFormat(), other, language, TablistManager.getInstance().getPlaceholderTypes());
		Scoreboard scoreboard = objective.bukkitValue().getScoreboard();
		Team team = scoreboard.getTeam(other.getRank().formatIdentifier(other));
		
		if (team == null)
			team = scoreboard.registerNewTeam(other.getRank().formatIdentifier(other));
		if (VersionUtils.getVersion().isAtLeast(Version.V1_12)) {
			String lastColors = ChatColor.getLastColors(prefix);
			
			if (!lastColors.isEmpty())
				team.setColor((lastColors.startsWith("§x") ? ChatColor.of(lastColors.substring(3, 14).replace("§", "")).getClosestDefaultColor() : ChatColor.getByChar(lastColors.charAt(1))).bukkitValue());
		} team.setPrefix(prefix.length() > MAX_TEAM_TEXT_LENGTH ? me.remigio07.chatplugin.common.util.Utils.abbreviate(prefix, MAX_TEAM_TEXT_LENGTH, false) : prefix);
		team.setSuffix(suffix.length() > MAX_TEAM_TEXT_LENGTH ? me.remigio07.chatplugin.common.util.Utils.abbreviate(suffix, MAX_TEAM_TEXT_LENGTH, false) : suffix);
		team.addPlayer(other.toAdapter().bukkitValue());
	}
	
	public void setPlayerListName(String name) {
		player.setPlayerListName(name);
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}
	
	@Override
	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		audience.showTitle(
				Title.title(Utils.deserializeLegacy(title, true),
				Utils.deserializeLegacy(subtitle, true),
				Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut)))
				);
	}
	
	@Override
	public void sendActionbar(String actionbar) {
		audience.sendActionBar(Utils.deserializeLegacy(actionbar, true));
	}
	
	@Deprecated
	@Override
	public void sendPacket(Object packet) {
		if (packet != null)
			BukkitReflection.invokeMethod("PlayerConnection", "sendPacket", playerConnection, packet);
	}
	
	@Override
	public void openInventory(InventoryAdapter inventory) {
		player.openInventory(inventory.bukkitValue());
	}
	
	@Override
	public void closeInventory() {
		player.closeInventory();
	}
	
	@Override
	public void playSound(SoundAdapter sound) {
		Object bukkitValue = sound.bukkitValue();
		
		if (bukkitValue != null)
			BukkitReflection.invokeMethod("Player", "playSound", player, player.getLocation(), bukkitValue, sound.getVolume(), sound.getPitch());
		else if (sound.isVanillaCompliant())
			player.playSound(player.getLocation(), sound.getID(), sound.getVolume(), sound.getPitch());
	}
	
	@Override
	public void executeCommand(String command) {
		Bukkit.dispatchCommand(player, command);
	}
	
	@Override
	public void teleport(ChatPluginServerPlayer player) {
		this.player.teleport(player.toAdapter().bukkitValue());
	}
	
	@Override
	public String getDisplayName() {
		return player.getDisplayName();
	}
	
	@Override
	public String getWorld() {
		return player.getWorld().getName();
	}
	
	@Override
	public double getX() {
		return player.getLocation().getX();
	}
	
	@Override
	public double getY() {
		return player.getLocation().getY();
	}
	
	@Override
	public double getZ() {
		return player.getLocation().getZ();
	}
	
	@Override
	public Locale getLocale() {
		String str = VersionUtils.getVersion().getProtocol() >= 341 ? player.getLocale() : (String) BukkitReflection.getFieldValue("EntityPlayer", BukkitReflection.invokeMethod("CraftPlayer", "getHandle", craftPlayer), "locale");
		
		if (str.contains("_")) {
			Locale locale = new Locale.Builder().setLanguage(str.substring(0, str.indexOf('_'))).setRegion(str.substring(str.indexOf('_') + 1)).build();
			
			for (Locale other : Locale.getAvailableLocales())
				if (other.equals(locale));
					return locale;
		} return Locale.US;
	}
	
	public Object getCraftPlayer() {
		return craftPlayer;
	}
	
	public Locale getLastLocale() {
		return lastLocale;
	}
	
	public void setLastLocale(Locale lastLocale) {
		this.lastLocale = lastLocale;
	}
	
}
