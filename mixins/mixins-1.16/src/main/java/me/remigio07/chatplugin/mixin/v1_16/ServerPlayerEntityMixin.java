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

package me.remigio07.chatplugin.mixin.v1_16;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.storage.configuration.ConfigurationType;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.api.server.event.gui.GUICloseEvent;
import me.remigio07.chatplugin.api.server.gui.FillableGUI;
import me.remigio07.chatplugin.api.server.gui.GUI;
import me.remigio07.chatplugin.api.server.gui.GUIManager;
import me.remigio07.chatplugin.api.server.gui.SinglePageGUI;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.scoreboard.event.EventScoreboard;
import me.remigio07.chatplugin.api.server.tablist.TablistManager;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import me.remigio07.chatplugin.mixin.extension.ClientSettingsC2SPacketExtension;
import me.remigio07.chatplugin.mixin.extension.ServerPlayerEntityExtension;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityExtension { // 1.16-1.20.1
	
	@Unique
	private static Class<?> chatPlugin$FabricEventManager, chatPlugin$FabricPremiumEventManager;
	
	@Unique
	private static Method chatPlugin$afterChangeWorld, chatPlugin$afterDeath;
	
	@Unique
	private String chatPlugin$language;
	
	@Unique
	private Text chatPlugin$playerListName;
	
	@Unique
	private int chatPlugin$oldLevel = -1;
	
	@Override
	public String chatPlugin$getClientLanguage() {
		return chatPlugin$language;
	}
	
	@Override
	public void chatPlugin$setPlayerListName(Text chatPlugin$playerListName) {
		this.chatPlugin$playerListName = chatPlugin$playerListName;
	}
	
	@Inject(
			method = "setClientSettings",
			at = @At("HEAD")
			)
	private void chatPlugin$setClientSettings(
			ClientSettingsC2SPacket packet,
			
			CallbackInfo info
			) {
		chatPlugin$language = ((ClientSettingsC2SPacketExtension) packet).chatPlugin$getLanguage();
	}
	
	@Inject(
			method = "closeCurrentScreen",
			at = @At("HEAD")
			)
	private void chatPlugin$closeCurrentScreen( // GUICloseEvent
			CallbackInfo info
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED) {
			ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(((ServerPlayerEntity) (Object) this).getUuid());
			
			if (chatPlugin$serverPlayer != null) {
				GUI chatPlugin$gui = GUIManager.getInstance().getOpenGUI(chatPlugin$serverPlayer);
				
				if (chatPlugin$gui != null) {
					if (chatPlugin$gui instanceof SinglePageGUI)
						((SinglePageGUI) chatPlugin$gui).getViewers().remove(chatPlugin$serverPlayer);
					else ((FillableGUI<?>) chatPlugin$gui).getViewers().remove(chatPlugin$serverPlayer);
					
					new GUICloseEvent(chatPlugin$gui, chatPlugin$serverPlayer).call();
				}
			}
		}
	}
	
	@Inject(
			method = "playerTick",
			at = @At("TAIL")
			)
	private void chatPlugin$playerTick( // ScoreboardEvent#LEVEL_CHANGE
			CallbackInfo info
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED && ScoreboardManager.getInstance().isEnabled()) {
			int chatPlugin$experienceLevel = ((ServerPlayerEntity) (Object) this).experienceLevel;
			
			if (chatPlugin$oldLevel == -1)
				chatPlugin$oldLevel = chatPlugin$experienceLevel;
			if (chatPlugin$oldLevel != chatPlugin$experienceLevel) {
				Scoreboard chatPlugin$scoreboard = ScoreboardManager.getInstance().getScoreboard("level-change-event");
				
				if (chatPlugin$scoreboard != null) {
					ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(((ServerPlayerEntity) (Object) this).getUuid());
					
					if (chatPlugin$serverPlayer != null) {
						((EventScoreboard) chatPlugin$scoreboard).prepareEvent(chatPlugin$serverPlayer, chatPlugin$oldLevel);
						chatPlugin$scoreboard.addPlayer(chatPlugin$serverPlayer);
					}
				} chatPlugin$oldLevel = chatPlugin$experienceLevel;
			}
		}
	}
	
	@Inject(
			method = "getPlayerListName",
			at = @At("HEAD"),
			cancellable = true
			)
	public void chatPlugin$getPlayerListName(
			CallbackInfoReturnable<Text> info
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED && !(ConfigurationType.CONFIG.get().getBoolean("settings.register-scoreboards") && TablistManager.getInstance().isPlayerNamesTeamsMode()))
			info.setReturnValue(chatPlugin$playerListName);
	}
	
	@Inject(
			method = "onDeath",
			at = @At("TAIL")
			)
	public void chatPlugin$onDeath(
			DamageSource source,
			
			CallbackInfo info
			) {
		if (VersionUtils.getVersion().isOlderThan(Version.V1_19_2))
			try {
				if (chatPlugin$FabricPremiumEventManager == null) {
					chatPlugin$FabricPremiumEventManager = Class.forName("me.remigio07.chatplugin.server.fabric.manager.FabricPremiumEventManager", false, JARLibraryLoader.getInstance());
					chatPlugin$afterDeath = chatPlugin$FabricPremiumEventManager.getMethod("afterDeath", LivingEntity.class, DamageSource.class);
				} chatPlugin$afterDeath.invoke(EventManager.getInstance(), this, source);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
}
