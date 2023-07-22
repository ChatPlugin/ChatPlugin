/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07_
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

package me.remigio07_.chatplugin.server.bukkit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.remigio07_.chatplugin.api.common.integration.IntegrationType;
import me.remigio07_.chatplugin.api.common.ip_lookup.IPLookupManager;
import me.remigio07_.chatplugin.api.common.storage.DataContainer;
import me.remigio07_.chatplugin.api.common.storage.PlayersDataType;
import me.remigio07_.chatplugin.api.common.storage.StorageConnector;
import me.remigio07_.chatplugin.api.common.storage.StorageConnector.WhereCondition;
import me.remigio07_.chatplugin.api.common.storage.StorageConnector.WhereCondition.WhereOperator;
import me.remigio07_.chatplugin.api.common.util.Utils;
import me.remigio07_.chatplugin.api.common.util.VersionUtils;
import me.remigio07_.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07_.chatplugin.api.common.util.adapter.text.TextAdapter;
import me.remigio07_.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07_.chatplugin.api.common.util.manager.LogManager;
import me.remigio07_.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07_.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07_.chatplugin.api.server.event.player.PlayerFirstJoinEvent;
import me.remigio07_.chatplugin.api.server.f3servername.F3ServerNameManager;
import me.remigio07_.chatplugin.api.server.join_quit.MultiAccountCheckManager;
import me.remigio07_.chatplugin.api.server.language.Language;
import me.remigio07_.chatplugin.api.server.language.LanguageDetector;
import me.remigio07_.chatplugin.api.server.language.LanguageDetectorMethod;
import me.remigio07_.chatplugin.api.server.language.LanguageManager;
import me.remigio07_.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07_.chatplugin.api.server.rank.RankManager;
import me.remigio07_.chatplugin.api.server.util.adapter.inventory.InventoryAdapter;
import me.remigio07_.chatplugin.api.server.util.adapter.user.SoundAdapter;
import me.remigio07_.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07_.chatplugin.server.bossbar.NativeBossbar;
import me.remigio07_.chatplugin.server.bossbar.ReflectionBossbar;
import me.remigio07_.chatplugin.server.player.BaseChatPluginServerPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatPluginBukkitPlayer extends BaseChatPluginServerPlayer {
	
	private Player player;
	private Object craftPlayer;
	
	public ChatPluginBukkitPlayer(Player player) {
		super(new PlayerAdapter(player));
		this.player = player;
		version = ServerPlayerManager.getInstance().getPlayerVersion(uuid);
		version = version == null ? IntegrationType.PROTOCOLSUPPORT.isEnabled() ? IntegrationType.PROTOCOLSUPPORT.get().getVersion(toAdapter()) : VersionUtils.getVersion() : version;
		bedrockPlayer = ServerPlayerManager.getInstance().isBedrockPlayer(uuid);
		rank = RankManager.getInstance().calculateRank(this);
		craftPlayer = BukkitReflection.getLoadedClass("CraftPlayer").cast(player);
		playerConnection = BukkitReflection.getField("EntityPlayer", BukkitReflection.invokeMethod("CraftPlayer", "getHandle", craftPlayer), VersionUtils.getVersion().isAtLeast(Version.V1_17) ? "b" : "playerConnection", "");
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
						Language detected = detector.detectUsingClientLocale(this);
						
						if (!detected.equals(Language.getMainLanguage()))
							sendLanguageDetectedMessage(detected);
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
				if (MultiAccountCheckManager.getInstance().isPerformOnFirstJoin())
					MultiAccountCheckManager.getInstance().check(this);
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
	public void sendMessage(TextAdapter text) {
		if (VersionUtils.isSpigot() && VersionUtils.getVersion().isAtLeast(Version.V1_7_2))
			player.spigot().sendMessage(text.bukkitValue());
		else sendMessage(text.toPlain());
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}
	
	@Override
	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		if (VersionUtils.getVersion().getProtocol() > 47) {
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
			return;
		} Class<?>[] params = new Class<?>[] { BukkitReflection.getLoadedClass("EnumTitleAction"), BukkitReflection.getLoadedClass("IChatBaseComponent") };
		
		sendPacket(BukkitReflection.getInstance("PacketPlayOutTitle", fadeIn, stay, fadeOut));
		sendPacket(BukkitReflection.getInstance("PacketPlayOutTitle", params, BukkitReflection.getEnum("EnumTitleAction", "TITLE", ""), BukkitReflection.invokeMethod("ChatSerializer", "a", null, "\"" + title + "\"")));
		sendPacket(BukkitReflection.getInstance("PacketPlayOutTitle", params, BukkitReflection.getEnum("EnumTitleAction", "SUBTITLE", ""), BukkitReflection.invokeMethod("ChatSerializer", "a", null, "\"" + subtitle + "\"")));
	}
	
	@Override
	public void sendActionbar(String actionbar) {
		if (VersionUtils.getVersion().getProtocol() > 47 && VersionUtils.isSpigot())
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));
		else if (VersionUtils.getVersion().isAtLeast(Version.V1_19))
			sendPacket(BukkitReflection.getInstance("ClientboundSystemChatPacket", actionbar, 2));
		else try {
			sendPacket(BukkitReflection.getLoadedClass("PacketPlayOutChat").getConstructor(new Class[] { BukkitReflection.getLoadedClass("IChatBaseComponent"), Byte.TYPE }).newInstance(BukkitReflection.getInstance("ChatComponentText", actionbar), (byte) 2));
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			LogManager.log("The plugin has tried to send an actionbar to {0}, but this feature only works on 1.8+ servers.", 2, name);
		}
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
		player.playSound(player.getLocation(), sound.getID(), sound.getVolume(), sound.getPitch());
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
		return BukkitReflection.getLocale(this);
	}
	
	public Object getCraftPlayer() {
		return craftPlayer;
	}
	
}
