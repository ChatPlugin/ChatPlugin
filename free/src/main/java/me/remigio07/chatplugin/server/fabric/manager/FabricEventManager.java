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

package me.remigio07.chatplugin.server.fabric.manager;

import java.util.UUID;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.integration.IntegrationType;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.common.util.adapter.user.PlayerAdapter;
import me.remigio07.chatplugin.api.common.util.manager.ChatPluginManagerException;
import me.remigio07.chatplugin.api.common.util.manager.LogManager;
import me.remigio07.chatplugin.api.common.util.manager.TaskManager;
import me.remigio07.chatplugin.api.server.bossbar.BossbarManager;
import me.remigio07.chatplugin.api.server.chat.ChatManager;
import me.remigio07.chatplugin.api.server.integration.anticheat.AnticheatManager;
import me.remigio07.chatplugin.api.server.join_quit.JoinMessageManager;
import me.remigio07.chatplugin.api.server.join_quit.JoinTitleManager;
import me.remigio07.chatplugin.api.server.join_quit.QuitMessageManager;
import me.remigio07.chatplugin.api.server.join_quit.SuggestedVersionManager;
import me.remigio07.chatplugin.api.server.join_quit.WelcomeMessageManager;
import me.remigio07.chatplugin.api.server.language.Language;
import me.remigio07.chatplugin.api.server.language.LanguageDetector;
import me.remigio07.chatplugin.api.server.language.LanguageManager;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.scoreboard.event.EventScoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.event.ScoreboardEvent;
import me.remigio07.chatplugin.api.server.util.manager.ProxyManager;
import me.remigio07.chatplugin.api.server.util.manager.VanishManager;
import me.remigio07.chatplugin.mixin.extension.EntityExtension;
import me.remigio07.chatplugin.mixin.extension.ServerWorldExtension;
import me.remigio07.chatplugin.server.bossbar.NativeBossbar;
import me.remigio07.chatplugin.server.chat.BaseChatManager;
import me.remigio07.chatplugin.server.player.BaseChatPluginServerPlayer;
import me.remigio07.chatplugin.server.util.manager.VanishManagerImpl;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class FabricEventManager extends EventManager {
	
	private static boolean registered;
	
	@Override
	public void load() throws ChatPluginManagerException {
		instance = this;
		long ms = System.currentTimeMillis();
		
		// TODO: we will figure out priorities/phases soon
		
		if (!registered) { // we do not use "this::event" references to prevent memory leaks
			if (VersionUtils.getVersion().isAtLeast(Version.V1_16)) {
				ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> instance().afterChangeWorld(player, origin, destination)); // LOW
				
				if (VersionUtils.getVersion().isAtLeast(Version.V1_21_5)) {
					ServerPlayerEvents.JOIN.register(player -> instance().onJoin(player)); // LOW
					ServerPlayerEvents.LEAVE.register(player -> instance().onLeave(player)); // NORMAL
				}
			} registered = true;
		} enabled = true;
		loadTime = System.currentTimeMillis() - ms;
	}
	
	private static FabricEventManager instance() {
		return (FabricEventManager) instance;
	}
	
	public boolean handleChatMessage(ServerPlayerEntity sender, String message) {
		ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(sender.getUuid());
		
		if (serverPlayer == null || !ChatManager.getInstance().isEnabled())
			return true;
		if (!((BaseChatManager) ChatManager.getInstance()).handleChatEvent(serverPlayer, message)) // we just need 1 arg
			applyScoreboard(ScoreboardEvent.CHAT, sender);
		return false;
	}
	
	public void onJoin(ServerPlayerEntity player) {
		if (ChatPlugin.getState() != ChatPluginState.LOADED)
			return;
		PlayerAdapter adapter = new PlayerAdapter(player);
		
		if (player.getUuid().version() == 0 && !player.getName().getString().startsWith(ServerPlayerManager.getInstance().getFloodgateUsernamePrefix())) {
			LogManager.log(FLOODGATE_ERROR_MESSAGE, 2, player.getName(), ServerPlayerManager.getInstance().getFloodgateUsernamePrefix(), IntegrationType.FLOODGATE.isEnabled() ? "username-prefix\" in Floodgate's" : "settings.floodgate-username-prefix\" in");
			adapter.disconnect("Invalid ChatPlugin-Floodgate configuration. Please contact this server's Staff to fix the issue.");
			return;
		} ServerPlayerManager.getPlayersVersions().put(player.getUuid(), IntegrationType.VIAVERSION.isEnabled() ? IntegrationType.VIAVERSION.get().getVersion(adapter) : IntegrationType.PROTOCOLSUPPORT.isEnabled() ? IntegrationType.PROTOCOLSUPPORT.get().getVersion(adapter) : VersionUtils.getVersion());
		ServerPlayerManager.getPlayersLoginTimes().put(player.getUuid(), System.currentTimeMillis());
		
		if ((IntegrationType.FLOODGATE.isEnabled() && IntegrationType.FLOODGATE.get().isBedrockPlayer(adapter)) || (IntegrationType.GEYSER.isEnabled() && IntegrationType.GEYSER.get().isBedrockPlayer(adapter)))
			ServerPlayerManager.getBedrockPlayers().add(player.getUuid());
		if (ServerPlayerManager.getInstance().isWorldEnabled(((ServerWorldExtension) ((EntityExtension) player).chatPlugin$getWorld()).chatPlugin$getName()))
			TaskManager.runAsync(() -> {
				if (!ProxyManager.getInstance().isEnabled())
					processJoinEvent(adapter, false);
			}, 0L);
	}
	
	public void processJoinEvent(PlayerAdapter playerAdapter, boolean vanished) {
		ServerPlayerManager.getInstance().loadPlayer(playerAdapter);
		
		ChatPluginServerPlayer player = ServerPlayerManager.getInstance().getPlayer(playerAdapter.getUUID());
		
		((VanishManagerImpl) VanishManager.getInstance()).update(player, true);
		SuggestedVersionManager.getInstance().check(player);
		JoinTitleManager.getInstance().sendJoinTitle(player, true);
		WelcomeMessageManager.getInstance().sendWelcomeMessage(player, true);
		
		if (vanished) { // always false, for now
			VanishManager.getInstance().hide(player);
			QuitMessageManager.getInstance().getFakeQuits().add(player.getUUID());
		} else JoinMessageManager.getInstance().sendJoinMessage(player);
		
		if (ConfigurationType.CONFIG.get().getBoolean("settings.enable-update-notification") && player.hasPermission("chatplugin.update-notification"))
			TaskManager.runAsync(() -> {
				if (player.isLoaded()) {
					String latestVersion = me.remigio07.chatplugin.common.util.Utils.getLatestVersion();
					
					if (latestVersion != null)
						player.sendTranslatedMessage("misc.update-notification", latestVersion);
				}
			}, 1000L);
	}
	
	public void onLeave(ServerPlayerEntity player) {
		if (ChatPlugin.getState() != ChatPluginState.LOADED)
			return;
		UUID uuid = player.getUuid();
		ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(uuid);
		
		if (serverPlayer != null) {
			TaskManager.runAsync(() -> {
				if (!ProxyManager.getInstance().isEnabled()) {
					QuitMessageManager.getInstance().sendQuitMessage(QuitMessageManager.getInstance().getQuitPackets().get(uuid));
					QuitMessageManager.getInstance().getQuitPackets().remove(uuid);
				} AnticheatManager.getInstance().clearViolations(serverPlayer);
			}, 0L);
			ServerPlayerManager.getInstance().unloadPlayer(uuid);
		} ServerPlayerManager.getPlayersVersions().remove(uuid);
		ServerPlayerManager.getPlayersLoginTimes().remove(uuid);
		ServerPlayerManager.getBedrockPlayers().remove(uuid);
	}
	
	public void afterChangeWorld(ServerPlayerEntity player, ServerWorld origin, ServerWorld destination) {
		if (ChatPlugin.getState() != ChatPluginState.LOADED)
			return;
		ServerPlayerManager playerManager = ServerPlayerManager.getInstance();
		ChatPluginServerPlayer serverPlayer = playerManager.getPlayer(player.getUuid());
		
		if (serverPlayer != null) {
			if (playerManager.isWorldEnabled(serverPlayer.getWorld())) { // enabled -> enabled
				BossbarManager bossbarManager = BossbarManager.getInstance();
				
				if (bossbarManager.isEnabled()) {
					if (bossbarManager.isWorldEnabled(((ServerWorldExtension) origin).chatPlugin$getName())) {
						if (!bossbarManager.isWorldEnabled(serverPlayer.getWorld())) { // bossbar: enabled -> disabled
							serverPlayer.getBossbar().unregister();
							((BaseChatPluginServerPlayer) serverPlayer).setBossbar(null);
						}
					} else if (bossbarManager.isWorldEnabled(serverPlayer.getWorld())) { // bossbar: disabled -> enabled
						((BaseChatPluginServerPlayer) serverPlayer).setBossbar(new NativeBossbar(serverPlayer));
						
						if (bossbarManager.isLoadingBossbarEnabled())
							bossbarManager.startLoading(serverPlayer);
						else bossbarManager.sendBossbar(bossbarManager.getBossbars().get(bossbarManager.getTimerIndex() == -1 ? 0 : bossbarManager.getTimerIndex()), serverPlayer);
					}
				} applyScoreboard(ScoreboardEvent.CHANGED_WORLD, player, ((ServerWorldExtension) origin).chatPlugin$getName());
			} else { // enabled -> disabled
				((VanishManagerImpl) VanishManager.getInstance()).update(serverPlayer, false);
				playerManager.unloadPlayer(player.getUuid()); // we might call it asynchronously?
			}
		} else if (playerManager.isWorldEnabled(((ServerWorldExtension) destination).chatPlugin$getName())) // disabled -> enabled; TODO: we may need to check if the player is a NPC by checking use other mods' tags (such as TaterzenNPCTag, NPC, etc.)
			TaskManager.runAsync(() -> {
				playerManager.loadPlayer(new PlayerAdapter(player));
				applyScoreboard(ScoreboardEvent.CHANGED_WORLD, player, ((ServerWorldExtension) origin).chatPlugin$getName());
			}, 0L);
	}
	
	public void onPlayerLocaleChange(ChatPluginServerPlayer player, String locale) {
		if (player != null && System.currentTimeMillis() - player.getLoginTime() > 10000L && !player.getLocale().getLanguage().equals(locale.substring(0, locale.indexOf('_')))) {
			LanguageDetector detector = LanguageManager.getInstance().getDetector();
			
			if (detector.isEnabled())
				TaskManager.runAsync(() -> {
					if (player.isLoaded()) {
						Language detected = detector.detectUsingClientLocale(player);
						
						if (!detected.equals(player.getLanguage()))
							((BaseChatPluginServerPlayer) player).sendLanguageDetectedMessage(detected);
					}
				}, detector.getDelay());
			applyScoreboard(ScoreboardEvent.LOCALE_CHANGE, player.toAdapter().fabricValue(), player.getLocale().getDisplayLanguage());
		}
	}
	
	public void applyScoreboard(ScoreboardEvent event, ServerPlayerEntity player, Object... args) {
		Scoreboard scoreboard = ScoreboardManager.getInstance().getScoreboard(event.name().replace('_', '-').toLowerCase() + "-event");
		
		if (scoreboard != null) {
			ChatPluginServerPlayer serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid());
			
			if (serverPlayer != null && serverPlayer.getScoreboard() != null) {
				((EventScoreboard) scoreboard).prepareEvent(serverPlayer, args);
				scoreboard.addPlayer(serverPlayer);
			}
		}
	}
	
}
