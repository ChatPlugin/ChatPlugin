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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.server.bukkit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07.chatplugin.api.common.storage.DataContainer;
import me.remigio07.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07.chatplugin.api.common.storage.StorageConnector;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition;
import me.remigio07.chatplugin.api.common.storage.StorageConnector.WhereCondition.WhereOperator;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.event.player.PlayerFirstJoinEvent;
import me.remigio07.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07.chatplugin.api.server.join_quit.AccountCheckManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageDetector;
import me.remigio07.chatplugin.api.server.language.LanguageDetectorMethod;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.rank.RankManager;
import me.remigio07.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.bootstrap.BukkitBootstrapper;
import me.remigio07.chatplugin.common.util.Utils;
import me.remigio07.chatplugin.server.bossbar.NativeBossbar;
import me.remigio07.chatplugin.server.bossbar.ReflectionBossbar;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class ChatPluginBukkitPlayer extends BaseChatPluginServerPlayer {
	
	private static BukkitAudiences audiences = BukkitAudiences.create(BukkitBootstrapper.getInstance());
	private Player player;
	private Object craftPlayer;
	private Locale lastLocale;
	
	public ChatPluginBukkitPlayer(Player player) {
		super(new PlayerAdapter(player));
		this.player = player;
		audience = audiences.player(player);
		version = ServerPlayerManager.getInstance().getPlayerVersion(uuid);
		version = version == null ? IntegrationType.PROTOCOLSUPPORT.isEnabled() ? IntegrationType.PROTOCOLSUPPORT.get().getVersion(toAdapter()) : VersionUtils.getVersion() : version;
		bedrockPlayer = ServerPlayerManager.getInstance().isBedrockPlayer(uuid);
		rank = RankManager.getInstance().calculateRank(this);
		craftPlayer = BukkitReflection.getLoadedClass("CraftPlayer").cast(player);
		playerConnection = BukkitReflection.getFieldValue("EntityPlayer", BukkitReflection.invokeMethod("CraftPlayer", "getHandle", craftPlayer), "playerConnection", VersionUtils.getVersion().isAtLeast(Version.V1_20) ? "c" : "b");
		StorageConnector storage = StorageConnector.getInstance();
		
		try {
			playerStored = storage.isPlayerStored(this);
		} catch (SQLException e) {
			LogManager.log("SQLException occurred while checking if {0} is stored in the database: {1}", 2, name, e.getMessage());
		} if (playerStored)
			language = LanguageManager.getInstance().getLanguage(this);
		else {
			LanguageDetector detector = LanguageManager.getInstance().getDetector();
			
			try {
				storage.insertNewPlayer(this);
			} catch (Exception e) {
				LogManager.log("{0} occurred while inserting {1} in the storage: {2}", 2, e.getClass().getSimpleName(), name, e.getMessage());
			} if (detector.isEnabled()) {
				if (detector.getMethod() == LanguageDetectorMethod.CLIENT_LOCALE) {
					TaskManager.runAsync(() -> {
						if (isLoaded()) {
							Language detected = detector.detectUsingClientLocale(this);
							
							if (!detected.equals(Language.getMainLanguage()))
								sendLanguageDetectedMessage(detected);
						}
					}, detector.getDelay());
				} else {
					long ms = System.currentTimeMillis();
					
					TaskManager.runAsync(() -> {
						getIPLookup(true);
						
						Language detected = detector.detectUsingGeolocalization(ipLookup);
						
						if (!detected.equals(Language.getMainLanguage()))
							TaskManager.runAsync(() -> sendLanguageDetectedMessage(detected), detector.getDelay() - (System.currentTimeMillis() - ms));
					}, 0L);
				}
			} language = Language.getMainLanguage();
		} if (BossbarManager.getInstance().isEnabled()) {
			bossbar = VersionUtils.getVersion().isAtLeast(Version.V1_9) ? new NativeBossbar(this) : new ReflectionBossbar(this);
			
			if (BossbarManager.getInstance().isLoadingBossbarEnabled())
				BossbarManager.getInstance().startLoading(this);
		} if (F3ServerNameManager.getInstance().isEnabled()) {
			try {
				Field channels = player.getClass().getDeclaredField("channels");
				
				channels.setAccessible(true);
				
				@SuppressWarnings("unchecked")
				Set<String> playerChannels = (Set<String>) channels.get(player);
				
				if (playerChannels.contains(F3ServerNameManager.CHANNEL_ID))
					playerChannels.add(F3ServerNameManager.CHANNEL_ID);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				LogManager.log("{0} occurred while enabling the F3 server names' channels for {1}: {2}", 2, e.getClass().getSimpleName(), name, e.getMessage());
			}
		} TaskManager.runAsync(() -> {
			try {
				String currentIPAddress = player.getAddress().getAddress().getHostAddress();
				id = storage.getPlayerData(PlayersDataType.ID, this);
				
				if (playerStored && !storage.getPlayerData(PlayersDataType.PLAYER_NAME, this).equals(player.getName()))
					storage.setPlayerData(PlayersDataType.PLAYER_NAME, this, player.getName());
				if (IPLookupManager.getInstance().isEnabled()) {
					if (IPLookupManager.getInstance().isLoadOnJoin() && ipLookup == null)
						try {
							ipLookup = IPLookupManager.getInstance().getIPLookup(getIPAddress()).get(5, TimeUnit.SECONDS);
						} catch (TimeoutException | InterruptedException | ExecutionException e) {
							LogManager.log("{0} occurred while waiting for {1}'s IP lookup: {2}", 2, e.getClass().getSimpleName(), name, e.getMessage());
						}
					String lastIPAddress = storage.getPlayerData(PlayersDataType.PLAYER_IP, id);
					
					if (currentIPAddress.equals(lastIPAddress))
						return;
					int maxIPsStored = IPLookupManager.getInstance().getMaxIPsStored();
					
					if (lastIPAddress != null && maxIPsStored != 1) {
						List<String> ipAddresses = Utils.getListFromString(StorageConnector.getInstance().safeSelect(DataContainer.IP_ADDRESSES, "ip_addresses", "[]", new WhereCondition("player_id", WhereOperator.EQUAL, id)));
						
						if (!ipAddresses.contains(lastIPAddress)) {
							ipAddresses.add(0, lastIPAddress);
							
							if (ipAddresses.size() > maxIPsStored)
								ipAddresses = ipAddresses.subList(0, maxIPsStored);
							storage.setData(DataContainer.IP_ADDRESSES, "ip_addresses", id, Utils.getStringFromList(ipAddresses, false, false));
						}
					}
				} storage.setPlayerData(PlayersDataType.PLAYER_IP, id, currentIPAddress);
			} catch (SQLException | IOException e) {
				LogManager.log("{0} occurred while getting {1}'s name or ID from the storage: {2}", 2, e.getClass().getSimpleName(), name, e.getMessage());
			} if (!playerStored) {
				if (AccountCheckManager.getInstance().isPerformOnFirstJoin())
					AccountCheckManager.getInstance().check(this);
				new PlayerFirstJoinEvent(this).call();
			}
		}, 0L);
	}
	
	@Override
	public void sendMessage(String message) {
		player.sendMessage(message);
	}
	
	@Override
	public void connect(String server) {
		if (!ProxyManager.getInstance().getServersIDs().contains(server))
			LogManager.log("The plugin tried to connect a player ({0}) to a server which is not under the proxy ({1}). You should fix this immediately, otherwise kicks and bans will not work.", 2, player.getName(), server);
		ProxyManager.getInstance().connect(this, server);
	}
	
	@Override
	public void disconnect(String reason) {
		TaskManager.runSync(() -> player.kickPlayer(reason), 0L);
	}
	
	@Override
	public void sendMessage(Object adventureComponent) {
		audiences.player(player).sendMessage((ComponentLike) adventureComponent);
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}
	
	@Override
	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		audience.showTitle(
				Title.title(Utils.deserializeLegacy(title),
				Utils.deserializeLegacy(subtitle),
				Times.times(Duration.ofMillis(fadeIn / 50), Duration.ofMillis(stay / 50), Duration.ofMillis(fadeOut / 50)))
				);
	}
	
	@Override
	public void sendActionbar(String actionbar) {
		audience.sendActionBar(Utils.deserializeLegacy(actionbar));
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
		Sound bukkitValue = sound.bukkitValue();
		
		if (bukkitValue == null)
			player.playSound(player.getLocation(), sound.getID(), sound.getVolume(), sound.getPitch());
		else player.playSound(player.getLocation(), bukkitValue, sound.getVolume(), sound.getPitch());
	}
	
	@Override
	public void executeCommand(String command) {
		Bukkit.dispatchCommand(player, command);
	}
	
	@Override
	public String getWorld() {
		return player.getWorld().getName();
	}
	
	@Override
	public Locale getLocale() {
		String str = VersionUtils.getVersion().getProtocol() >= 341 ? player.getLocale() : (String) BukkitReflection.getFieldValue("EntityPlayer", BukkitReflection.invokeMethod("CraftPlayer", "getHandle", craftPlayer), "locale");
		
		if (str.contains("_")) {
			Locale locale = new Locale(str.substring(0, str.indexOf('_')), str.substring(str.indexOf('_') + 1));
			
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
	
	public static void closeAudiences() {
		audiences.close();
	}
	
}
