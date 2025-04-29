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

package me.remigio07.chatplugin.server.sponge;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.Team;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.title.Title;

import com.google.common.collect.Iterables;

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
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.rank.RankManagerImpl;
import me.remigio07.chatplugin.server.sponge.manager.SpongePlayerManager;
import me.remigio07.chatplugin.server.util.Utils;
import net.kyori.adventure.text.Component;

public class ChatPluginSpongePlayer extends BaseChatPluginServerPlayer {
	
	private static Cause inventoryCause;
	private Player player;
	
	static {
		try { // Sponge v4.2
			inventoryCause = (Cause) Class.forName("org.spongepowered.api.event.cause.NamedCause").getMethod("of", String.class, Object.class).invoke(null, "ChatPluginInventories", null);
		} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			
		}
	}
	
	public ChatPluginSpongePlayer(Player player) {
		super(new PlayerAdapter(player));
		this.player = player;
		audience = SpongePlayerManager.getAudiences().player(player);
		rank = ((RankManagerImpl) RankManager.getInstance()).calculateRank(this);
		version = version == null ? VersionUtils.getVersion() : version;
		playerConnection = player.getConnection();
		StorageConnector storage = StorageConnector.getInstance();
		
		if (playerStored)
			language = LanguageManager.getInstance().getLanguage(this);
		else {
			LanguageDetector detector = LanguageManager.getInstance().getDetector();
			
			try {
				storage.insertNewPlayer(this);
			} catch (SQLException | IOException e) {
				LogManager.log("{0} occurred while inserting {1} in the storage: {2}", 2, e.getClass().getSimpleName(), name, e.getMessage());
			} if (detector.isEnabled()) {
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
					
					TaskManager.runAsync(() -> {
						getIPLookup(true);
						
						Language detected = detector.detectUsingGeolocalization(ipLookup);
						
						if (!detected.equals(Language.getMainLanguage()))
							TaskManager.runAsync(() -> sendLanguageDetectedMessage(detected), detector.getDelay() - (System.currentTimeMillis() - ms));
					}, 0L);
				}
			} language = Language.getMainLanguage();
		} try {
			id = storage.getPlayerData(PlayersDataType.ID, this);
		} catch (SQLException e) {
			LogManager.log("SQLException occurred while getting {0}'s ID from the the database: {1}", 2, name, e.getMessage());
		} if (BossbarManager.getInstance().isEnabled() && BossbarManager.getInstance().isWorldEnabled(getWorld())) {
			bossbar = new NativeBossbar(this);
			
			if (BossbarManager.getInstance().isLoadingBossbarEnabled())
				BossbarManager.getInstance().startLoading(this);
			else BossbarManager.getInstance().sendBossbar(BossbarManager.getInstance().getBossbars().get(BossbarManager.getInstance().getTimerIndex() == -1 ? 0 : BossbarManager.getInstance().getTimerIndex()), this);
		} TaskManager.runAsync(() -> {
			try {
				String currentIPAddress = player.getConnection().getAddress().getAddress().getHostAddress();
				
				if (playerStored) {
					if (!storage.getPlayerData(PlayersDataType.PLAYER_NAME, id).equals(name))
						storage.setPlayerData(PlayersDataType.PLAYER_NAME, id, name);
					else if (!storage.getPlayerData(PlayersDataType.PLAYER_UUID, id).equals(uuid.toString()))
						storage.setPlayerData(PlayersDataType.PLAYER_UUID, id, uuid.toString());
				} if (IPLookupManager.getInstance().isEnabled()) {
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
				LogManager.log("{0} occurred while getting/setting {1}'s name or IP address(es) from/in the storage: {2}", 2, e.getClass().getSimpleName(), name, e.getMessage());
			} if (!playerStored) {
				if (AccountCheckManager.getInstance().isPerformOnFirstJoin())
					AccountCheckManager.getInstance().check(this);
				new PlayerFirstJoinEvent(this).call();
			}
		}, 0L);
	}
	
	@Override
	public void sendMessage(String message) {
		if (version.isOlderThan(Version.V1_8)) // https://bugs.mojang.com/browse/MC-39987
			for (String str : message.split("\n"))
				player.sendMessage(Utils.serializeSpongeText(str, false));
		else player.sendMessage(Utils.serializeSpongeText(message, false));
	}
	
	@Override
	public void connect(String server) {
		if (!ProxyManager.getInstance().getServersIDs().contains(server))
			LogManager.log("The plugin tried to connect a player ({0}) to a server which is not under the proxy ({1}). You should fix this immediately, otherwise kicks and bans will not work.", 2, player.getName(), server);
		ProxyManager.getInstance().connect(this, server);
	}
	
	@Override
	public void disconnect(String reason) {
		if (Sponge.getServer().isMainThread())
			player.kick(Utils.serializeSpongeText(reason, true));
		else TaskManager.runSync(() -> disconnect(reason), 0L);
	}
	
	@Override
	public double getDistance(double x, double y, double z) {
		return player.getLocation().getPosition().distance(x, y, z);
	}
	
	@Override
	public void sendMessage(Component... components) {
		for (Component component : components)
			audience.sendMessage(component);
	}
	
	@Override
	public void updatePlayerListName() {
		if (ConfigurationType.CONFIG.get().getBoolean("settings.register-scoreboards")) {
			for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
				((ChatPluginSpongePlayer) other).setupTeams(this);
				setupTeams(other);
			} setupTeams(this);
		} else {
			for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
				((ChatPluginSpongePlayer) other).setPlayerListName(this);
				setPlayerListName(other);
			} setPlayerListName(this);
		}
	}
	
	private void setupTeams(ChatPluginServerPlayer other) {
		String prefix = PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPrefixFormat(), other, language, TablistManager.getInstance().getPlaceholderTypes());
		String suffix = PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getSuffixFormat(), other, language, TablistManager.getInstance().getPlaceholderTypes());
		Scoreboard scoreboard = Iterables.getFirst(objective.spongeValue().getScoreboards(), null);
		Team team = scoreboard.getTeam(other.getRank().formatIdentifier(other)).orElse(null);
		
		if (team == null) // specifying the following in orElse(...) would build a team every time
			team = Team.builder().name(other.getRank().formatIdentifier(other)).build();
		
		// not future-proof (Sponge v8/1.13+)
		if (prefix.contains(" ")) {
			int index = prefix.lastIndexOf(' ');
			
			if (index != prefix.length() - 1) {
				String str = prefix.substring(index + 1);
				
				if (ChatColor.stripColor(ChatColor.translate(str)).isEmpty())
					prefix = prefix.substring(0, index) + str + " ";
			}
		} team.setPrefix(Utils.serializeSpongeText(prefix.length() > 16 ? me.remigio07.chatplugin.common.util.Utils.abbreviate(prefix, 16, false) : prefix, false));
		team.setSuffix(Utils.serializeSpongeText(suffix.length() > 16 ? me.remigio07.chatplugin.common.util.Utils.abbreviate(suffix, 16, false) : suffix, false));
		team.addMember(Utils.serializeSpongeText(other.toAdapter().spongeValue().getName(), false));
		
		if (!team.getScoreboard().isPresent())
			scoreboard.registerTeam(team);
	}
	
	private void setPlayerListName(ChatPluginServerPlayer other) {
		setPlayerListName(other, Utils.serializeSpongeText(
				PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPrefixFormat(), other, language, TablistManager.getInstance().getPlaceholderTypes())
				+ other.getName()
				+ PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getSuffixFormat(), other, language, TablistManager.getInstance().getPlaceholderTypes()),
				false));
	}
	
	public void setPlayerListName(ChatPluginServerPlayer other, Text name) {
		player.getTabList().getEntry(other.getUUID()).get().setDisplayName(name);
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return player.hasPermission(permission);
	}
	
	@Override
	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		// sent separately because of a bug
		player.sendTitle(Title.builder()
				.reset()
				.subtitle(subtitle == null ? null : Utils.serializeSpongeText(subtitle, false))
				.fadeIn(fadeIn)
				.stay(stay)
				.fadeOut(fadeOut)
				.build()
				);
		player.sendTitle(Title.builder()
				.title(title == null ? null : Utils.serializeSpongeText(title, false))
				.build()
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
			SpongeReflection.invokeMethod("NetHandlerPlayServer", "func_147359_a", playerConnection, packet);
	}
	
	@Override
	public void openInventory(InventoryAdapter inventory) {
		try { // Sponge v4.2
			Player.class.getMethod("openInventory", Inventory.class, Cause.class).invoke(player, inventory.spongeValue(), inventoryCause);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			player.openInventory(inventory.spongeValue());
		}
	}
	
	@Override
	public void closeInventory() {
		try { // Sponge v4.2
			Player.class.getMethod("closeInventory", Cause.class).invoke(player, inventoryCause);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			player.closeInventory();
		}
	}
	
	@Override
	public void playSound(SoundAdapter sound) {
		SoundType type = sound.spongeValue(true);
		
		if (type != null)
			player.playSound(type, player.getLocation().getPosition(), sound.getVolume(), sound.getPitch()); // Sponge v4.2
	}
	
	@Override
	public void executeCommand(String command) {
		Sponge.getCommandManager().process(player, command);
	}
	
	@Override
	public void teleport(ChatPluginServerPlayer player) {
		this.player.setLocation(player.toAdapter().spongeValue().getLocation());
	}
	
	@Override
	public String getDisplayName() {
		 return Utils.deserializeSpongeText(player.getDisplayNameData().displayName().get());
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
		return player.getLocale();
	}
	
}
