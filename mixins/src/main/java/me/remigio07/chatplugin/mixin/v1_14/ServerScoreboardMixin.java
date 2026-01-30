package me.remigio07.chatplugin.mixin.v1_14;

import java.util.Collections;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import me.remigio07.chatplugin.mixin.extension.ServerScoreboardExtension;
import net.minecraft.network.Packet;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerScoreboard.class)
public class ServerScoreboardMixin implements ServerScoreboardExtension {
	
	@Unique
	private static final String CHATPLUGIN$TARGET = "Lnet/minecraft/server/PlayerManager;sendToAll(Lnet/minecraft/network/Packet;)V";
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
			method = "updatePlayerScore(Ljava/lang/String;)V",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$updatePlayerScore$0(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "updatePlayerScore(Ljava/lang/String;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$updatePlayerScore$1(
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
			method = "addPlayerToTeam",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$addPlayerToTeam(
			PlayerManager instance,
			Packet<?> packet
			) {
		chatPlugin$process(instance, packet);
	}
	
	@Redirect(
			method = "removePlayerFromTeam",
			at = @At(
					value = "INVOKE",
					target = CHATPLUGIN$TARGET
					)
			)
	private void chatPlugin$removePlayerFromTeam(
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
			method = "addScoreboardObjective",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerManager;getPlayerList()Ljava/util/List;"
					)
			)
	private List<ServerPlayerEntity> chatPlugin$addScoreboardObjective(
			PlayerManager instance
			) {
		return chatPlugin$owner == null
				? instance.getPlayerList()
				: Collections.singletonList(chatPlugin$owner);
	}
	
	@Redirect(
			method = "removeScoreboardObjective",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/PlayerManager;getPlayerList()Ljava/util/List;"
					)
			)
	private List<ServerPlayerEntity> chatPlugin$removeScoreboardObjective(
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
