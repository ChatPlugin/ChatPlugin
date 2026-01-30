package me.remigio07.chatplugin.mixin.latest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.scoreboard.event.EventScoreboard;
import net.minecraft.network.packet.c2s.common.ResourcePackStatusC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin {
	
	@Inject(
			method = "onResourcePackStatus",
			at = @At("TAIL")
			)
	public void chatPlugin$onResourcePackStatus(
			ResourcePackStatusC2SPacket packet,
			
			CallbackInfo info
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED && ScoreboardManager.getInstance().isEnabled()) {
			Scoreboard chatPlugin$scoreboard = ScoreboardManager.getInstance().getScoreboard("resource-pack-status-event");
			
			if (chatPlugin$scoreboard != null) {
				ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(((ServerPlayerEntity) (Object) this).getUuid());
				
				if (chatPlugin$serverPlayer != null) {
					((EventScoreboard) chatPlugin$scoreboard).prepareEvent(chatPlugin$serverPlayer, packet.status().name());
					chatPlugin$scoreboard.addPlayer(chatPlugin$serverPlayer);
				}
			}
		}
	}
	
}
