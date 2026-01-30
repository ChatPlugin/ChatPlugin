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

package me.remigio07.chatplugin.server.fabric;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

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
import me.remigio07.chatplugin.bootstrap.FabricBootstrapper;
import me.remigio07.chatplugin.mixin.common.ScoreboardObjectiveAccessor;
import me.remigio07.chatplugin.mixin.extension.EntityExtension;
import me.remigio07.chatplugin.mixin.extension.ServerPlayerEntityExtension;
import me.remigio07.chatplugin.mixin.extension.ServerWorldExtension;
import me.remigio07.chatplugin.server.bossbar.NativeBossbar;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.rank.RankManagerImpl;
import me.remigio07.chatplugin.server.util.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TeleportTarget;

public class ChatPluginFabricPlayer extends BaseChatPluginServerPlayer {
	
	private ServerPlayerEntity player;
	private Locale lastLocale;
	
	public ChatPluginFabricPlayer(ServerPlayerEntity player) {
		super(new PlayerAdapter(player));
		this.player = player;
		rank = ((RankManagerImpl) RankManager.getInstance()).calculateRank(this);
		version = version == null ? VersionUtils.getVersion() : version;
		playerConnection = player.networkHandler;
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
			bossbar = new NativeBossbar(this);
			
			if (BossbarManager.getInstance().isLoadingBossbarEnabled())
				BossbarManager.getInstance().startLoading(this);
			else BossbarManager.getInstance().sendBossbar(BossbarManager.getInstance().getBossbars().get(BossbarManager.getInstance().getTimerIndex() == -1 ? 0 : BossbarManager.getInstance().getTimerIndex()), this);
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
		return toAdapter().getIPAddress();
	}
	
	@Override
	public void sendMessage(String message) {
		player.sendMessage(Utils.toFabricComponent(message), false);
	}
	
	@Override
	public void connect(String server) {
		if (!ProxyManager.getInstance().getServersIDs().contains(server))
			LogManager.log("The plugin tried to connect a player ({0}) to a server which is not under the proxy ({1}). You should fix this immediately, otherwise kicks and bans will not work.", 2, name, server);
		ProxyManager.getInstance().connect(this, server);
	}
	
	@Override
	public void disconnect(String reason) {
		if (FabricBootstrapper.getInstance().getServer().isOnThread())
			player.networkHandler.disconnect(Utils.toFabricComponent(reason));
		else TaskManager.runSync(() -> disconnect(reason), 0L);
	}
	
	@Override
	public double getDistance(double x, double y, double z) {
		return ((EntityExtension) player).chatPlugin$getLocation().distanceTo(new Vec3d(x, y, z));
	}
	
	@Override
	public void sendMessage(BaseComponent... components) {
		for (BaseComponent component : components)
			player.sendMessage(ChatPluginFabric.toFabricComponent(component), false);
	}
	
	@Override
	public void updatePlayerListName() {
		if (ConfigurationType.CONFIG.get().getBoolean("settings.register-scoreboards")) {
			if (TablistManager.getInstance().isPlayerNamesTeamsMode()) {
				for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
					((ChatPluginFabricPlayer) other).setupTeamsFull(this);
					setupTeamsFull(other);
				} setupTeamsFull(this);
				return;
			} for (ChatPluginServerPlayer other : ServerPlayerManager.getInstance().getPlayers().values()) {
				((ChatPluginFabricPlayer) other).setupTeams(this);
				setupTeams(other);
			} setupTeams(this);
		} setPlayerListName(Utils.toFabricComponent(
				PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPlayerNamesPrefix(), this, TablistManager.getInstance().getPlaceholderTypes())
				+ name
				+ PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPlayerNamesSuffix(), this, TablistManager.getInstance().getPlaceholderTypes())
				));
	}
	
	private void setupTeamsFull(ChatPluginServerPlayer other) {
		String prefix = PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPlayerNamesPrefix(), other, language, TablistManager.getInstance().getPlaceholderTypes());
		String suffix = PlaceholderManager.getInstance().translatePlaceholders(TablistManager.getInstance().getPlayerNamesSuffix(), other, language, TablistManager.getInstance().getPlaceholderTypes());
		String identifier = other.getRank().formatIdentifier(other);
		Scoreboard scoreboard = ((ScoreboardObjectiveAccessor) objective.fabricValue()).chatPlugin$getScoreboard();
		Team team = scoreboard.getTeam(identifier);
		String lastColors = ChatColor.getLastColors(prefix);
		
		if (team == null)
			team = scoreboard.addTeam(identifier);
		if (!lastColors.isEmpty())
			team.setColor(Formatting.byName((lastColors.startsWith("§x") ? ChatColor.of(lastColors.substring(3, 14).replace("§", "")).getClosestDefaultColor() : ChatColor.getByChar(lastColors.charAt(1))).name()));
		team.setPrefix(Utils.toFabricComponent(prefix.length() > 16 ? me.remigio07.chatplugin.common.util.Utils.abbreviate(prefix, 16, false) : prefix));
		team.setSuffix(Utils.toFabricComponent(suffix.length() > 16 ? me.remigio07.chatplugin.common.util.Utils.abbreviate(suffix, 16, false) : suffix));
		scoreboard.addScoreHolderToTeam(other.getName(), team);
	}
	
	private void setupTeams(ChatPluginServerPlayer other) {
		String identifier = other.getRank().formatIdentifier(other);
		Scoreboard scoreboard = ((ScoreboardObjectiveAccessor) objective.fabricValue()).chatPlugin$getScoreboard();
		Team team = scoreboard.getTeam(identifier);
		
		if (team == null)
			team = scoreboard.addTeam(identifier);
		scoreboard.addScoreHolderToTeam(other.getName(), team);
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return toAdapter().hasPermission(permission);
	}
	
	@Override
	public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		if (VersionUtils.getVersion().isOlderThan(Version.V1_17)) {
			try {
				Class<?> TitleS2CPacket = Class.forName("net.minecraft.class_2762");
				Class<?> Action = TitleS2CPacket.getClasses()[0];
				Constructor<?> constructor = TitleS2CPacket.getConstructor(Action, Text.class);
				Object[] values = Action.getEnumConstants();
				
				sendPacket(TitleS2CPacket.getConstructor(int.class, int.class, int.class).newInstance(fadeIn / 50, stay / 50, fadeOut / 50));
				
				if (title != null)
					sendPacket(constructor.newInstance(values[0], Utils.toFabricComponent(title)));
				if (subtitle != null)
					sendPacket(constructor.newInstance(values[1], Utils.toFabricComponent(subtitle)));
			} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			sendPacket(new TitleFadeS2CPacket(fadeIn / 50, stay / 50, fadeOut / 50));
			
			if (title != null)
				sendPacket(new TitleS2CPacket(Utils.toFabricComponent(title)));
			if (subtitle != null)
				sendPacket(new SubtitleS2CPacket(Utils.toFabricComponent(subtitle)));
		}
	}
	
	@Override
	public void sendActionbar(String actionbar) {
		if (VersionUtils.getVersion().isOlderThan(Version.V1_17)) {
			try {
				Class<?> TitleS2CPacket = Class.forName("net.minecraft.class_2762");
				Class<?> Action = TitleS2CPacket.getClasses()[0];
				
				sendPacket(TitleS2CPacket.getConstructor(Action, Text.class).newInstance(Action.getEnumConstants()[2], Utils.toFabricComponent(actionbar)));
			} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		} else player.sendMessage(Utils.toFabricComponent(actionbar), true);
	}
	
	@Deprecated
	@Override
	public void sendPacket(Object packet) {
		if (packet != null)
			player.networkHandler.sendPacket((Packet<?>) packet);
	}
	
	@Override
	public void openInventory(InventoryAdapter inventory) {
		player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncID, playerInventory, player) -> {
			int rows = inventory.getSize() / 9;
			
			try {
				return new GenericContainerScreenHandler(VersionUtils.getVersion().isAtLeast(Version.V1_21_2)
						? Registries.SCREEN_HANDLER.get(Identifier.ofVanilla("generic_9x" + rows))
						: (ScreenHandlerType<?>) Registry.class.getMethod("method_10223", Identifier.class).invoke(VersionUtils.getVersion().isAtLeast(Version.V1_19_3)
								? Registries.SCREEN_HANDLER
								: Registry.class.getField("field_17429").get(null),
								Identifier.tryParse("minecraft:" + "generic_9x" + rows)),
						syncID, playerInventory, inventory.fabricValue(), rows);
			} catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		}, Utils.toFabricComponent(inventory.getTitle())));
	}
	
	@Override
	public void closeInventory() {
		player.closeHandledScreen();
	}
	
	@Override
	public void playSound(SoundAdapter sound) {
		SoundEvent event = sound.fabricValue(true);
		
		if (event != null) {
			PlaySoundS2CPacket packet = null;
			long nextLong = 0;
			
			try { // we could use Entity#getRandom() starting from 1.21
				if (VersionUtils.getVersion().isAtLeast(Version.V1_19)) {
					Field field = Entity.class.getDeclaredField("field_5974");
					
					field.setAccessible(true);
					
					nextLong = ((Random) field.get(player)).nextLong();
				} packet = VersionUtils.getVersion().isAtLeast(Version.V1_19_3)
						? new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(event), SoundCategory.MASTER, getX(), getY(), getZ(), sound.getVolume(), sound.getPitch(), nextLong)
						: VersionUtils.getVersion().isAtLeast(Version.V1_19)
						? PlaySoundS2CPacket.class.getConstructor(SoundEvent.class, SoundCategory.class, double.class, double.class, double.class, float.class, float.class, long.class).newInstance(event, SoundCategory.MASTER, getX(), getY(), getZ(), sound.getVolume(), sound.getPitch(), nextLong)
						: PlaySoundS2CPacket.class.getConstructor(SoundEvent.class, SoundCategory.class, double.class, double.class, double.class, float.class, float.class).newInstance(event, SoundCategory.MASTER, getX(), getY(), getZ(), sound.getVolume(), sound.getPitch());
			} catch (NoSuchFieldException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				return;
			} sendPacket(packet);
		}
	}
	
	@Override
	public void executeCommand(String command) {
		ChatPluginFabric.CommandExecutor.runCommand(ChatPluginFabric.getCommandSource(player), command);
	}
	
	@Override
	public void teleport(ChatPluginServerPlayer player) {
		ServerPlayerEntity target = player.toAdapter().fabricValue();
		this.player.teleportTo(new TeleportTarget((ServerWorld) ((EntityExtension) target).chatPlugin$getWorld(), ((EntityExtension) target).chatPlugin$getLocation(), Vec3d.ZERO, target.getYaw(), target.getPitch(), TeleportTarget.NO_OP));
	}
	
	@Override
	public String getDisplayName() {
		 return player.hasCustomName() ? Utils.toLegacyText(player.getCustomName()) : player.getName().getString();
	}
	
	@Override
	public String getWorld() {
		return ((ServerWorldExtension) ((EntityExtension) player).chatPlugin$getWorld()).chatPlugin$getName();
	}
	
	@Override
	public double getX() {
		return ((EntityExtension) player).chatPlugin$getLocation().getX();
	}
	
	@Override
	public double getY() {
		return ((EntityExtension) player).chatPlugin$getLocation().getY();
	}
	
	@Override
	public double getZ() {
		return ((EntityExtension) player).chatPlugin$getLocation().getZ();
	}
	
	@Override
	public Locale getLocale() {
		String str = ((ServerPlayerEntityExtension) player).chatPlugin$getClientLanguage();
		
		if (str.contains("_")) {
			Locale locale = new Locale.Builder().setLanguage(str.substring(0, str.indexOf('_'))).setRegion(str.substring(str.indexOf('_') + 1)).build();
			
			for (Locale other : Locale.getAvailableLocales())
				if (other.equals(locale));
					return locale;
		} return Locale.US;
	}
	
	@Override
	public String toString() {
		return new StringJoiner(", ", "ChatPluginFabricPlayer{", "}")
				.add("uuid=" + uuid.toString())
				.add("name=\"" + name + "\"")
				.toString();
	}
	
	public Locale getLastLocale() {
		return lastLocale;
	}
	
	public void setLastLocale(Locale lastLocale) {
		this.lastLocale = lastLocale;
	}
	
	public void setPlayerListName(Text playerListName) {
		((ServerPlayerEntityExtension) player).chatPlugin$setPlayerListName(playerListName);
		
		for (ServerPlayerEntity other : FabricBootstrapper.getInstance().getServer().getPlayerManager().getPlayerList())
			other.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
	} // can't we create just one instance and send it to each player?
	
}
