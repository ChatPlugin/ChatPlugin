package me.remigio07.chatplugin.mixin.latest;

import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.remigio07.chatplugin.mixin.extension.ServerScoreboardExtension;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin implements ServerScoreboardExtension {
	
	@Unique
	private static final String CHATPLUGIN$TARGET = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/packet/Packet;)V";
	@Unique
	private ServerPlayerEntity chatPlugin$owner;
	
	@Override
	public void chatPlugin$setOwner(ServerPlayerEntity chatPlugin$owner) {
		this.chatPlugin$owner = chatPlugin$owner;
	}
	
	@Redirect(
			method = "updateScore",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$updateScore(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "onScoreHolderRemoved",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$onScoreHolderRemoved(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "onScoreRemoved",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$onScoreRemoved(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "setObjectiveSlot",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$setObjectiveSlot(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "addScoreHolderToTeam",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$addScoreHolderToTeam(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "removeScoreHolderFromTeam",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$removeScoreHolderFromTeam(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "updateExistingObjective",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$updateExistingObjective(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "updateScoreboardTeamAndPlayers",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$updateScoreboardTeamAndPlayers(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "updateScoreboardTeam",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$updateScoreboardTeam(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "updateRemovedTeam",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$updateRemovedTeam(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "startSyncing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerManager;getPlayerList()Ljava/util/List;"
					)
			)
	private List<ServerPlayerEntity> chatPlugin$startSyncing(
			PlayerManager instance
			) {
		return chatPlugin$owner == null
				? instance.getPlayerList()
				: Collections.singletonList(chatPlugin$owner);
	}
	
	@Redirect(
			method = "stopSyncing",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerManager;getPlayerList()Ljava/util/List;"
					)
			)
	private List<ServerPlayerEntity> chatPlugin$stopSyncing(
			PlayerManager instance
			) {
		return chatPlugin$owner == null
				? instance.getPlayerList()
				: Collections.singletonList(chatPlugin$owner);
	}
	
	private void chatPlugin$process(PlayerManager instance, Packet<?> packet) {
		if (chatPlugin$owner == null)
			instance.sendToAll(packet);
		else chatPlugin$owner.networkHandler.sendPacket(packet);
	}
	
}
