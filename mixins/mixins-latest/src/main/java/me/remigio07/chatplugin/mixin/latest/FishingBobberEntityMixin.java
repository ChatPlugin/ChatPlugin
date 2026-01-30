package me.remigio07.chatplugin.mixin.latest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import me.remigio07.chatplugin.api.ChatPlugin;
import me.remigio07.chatplugin.api.common.util.ChatPluginState;
import me.remigio07.chatplugin.api.server.player.ChatPluginServerPlayer;
import me.remigio07.chatplugin.api.server.player.ServerPlayerManager;
import me.remigio07.chatplugin.api.server.scoreboard.Scoreboard;
import me.remigio07.chatplugin.api.server.scoreboard.ScoreboardManager;
import me.remigio07.chatplugin.api.server.scoreboard.event.EventScoreboard;
import me.remigio07.chatplugin.api.server.util.Utils;
import me.remigio07.chatplugin.mixin.MixinPlugin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Mixin(FishingBobberEntity.class)
abstract class FishingBobberEntityMixin {
	
	@Shadow
	private Random velocityRandom;
	
	@Shadow
	private Entity hookedEntity;
	
	@Inject(
			method = "use",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;pullHookedEntity(Lnet/minecraft/entity/Entity;)V"
					)
			)
	public void chatPlugin$use$0(
			ItemStack usedItem,
			
			CallbackInfoReturnable<Integer> info,
			@Local PlayerEntity player
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED && ScoreboardManager.getInstance().isEnabled()) {
			Scoreboard chatPlugin$scoreboard = ScoreboardManager.getInstance().getScoreboard("fish-event");
			
			if (chatPlugin$scoreboard != null) {
				ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid());
				
				if (chatPlugin$serverPlayer != null) {
					((EventScoreboard) chatPlugin$scoreboard).prepareEvent(chatPlugin$serverPlayer, Utils.capitalizeEveryWord(hookedEntity.getType().getName().getString()), hookedEntity.hasCustomName() ? MixinPlugin.toLegacyText(hookedEntity.getCustomName()) : hookedEntity.getName().getString(), 0);
					chatPlugin$scoreboard.addPlayer(chatPlugin$serverPlayer);
				}
			}
		}
	}
	
	@Redirect(
			method = "use",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z",
					ordinal = 1
					)
			)
	public boolean chatPlugin$use$1(
			World instance,
			Entity entity,
			
			@Local PlayerEntity player,
			@Local ItemEntity item
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED && ScoreboardManager.getInstance().isEnabled()) {
			Scoreboard chatPlugin$scoreboard = ScoreboardManager.getInstance().getScoreboard("fish-event");
			
			if (chatPlugin$scoreboard != null) {
				ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(player.getUuid());
				
				if (chatPlugin$serverPlayer != null) {
					int chatPlugin$amount = velocityRandom.nextInt(6) + 1;
					
					((EventScoreboard) chatPlugin$scoreboard).prepareEvent(chatPlugin$serverPlayer, "Item", item.getName().getString(), chatPlugin$amount);
					chatPlugin$scoreboard.addPlayer(chatPlugin$serverPlayer);
					return instance.spawnEntity(new ExperienceOrbEntity(instance, player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D, chatPlugin$amount));
				}
			}
		} return instance.spawnEntity(entity);
	}
	
}
