package me.remigio07.chatplugin.mixin.v1_14;

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
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(FishingBobberEntity.class)
abstract class FishingBobberEntityMixin extends Entity {
	
	@Shadow
	private Entity hookedEntity;
	
	public FishingBobberEntityMixin() {
		super(null, null);
	}
	
	@Inject(
			method = "method_6957",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"
					)
			)
	public void chatPlugin$method_6957$0(
			ItemStack usedItem,
			
			CallbackInfoReturnable<Integer> info
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED && ScoreboardManager.getInstance().isEnabled()) {
			Scoreboard chatPlugin$scoreboard = ScoreboardManager.getInstance().getScoreboard("fish-event");
			
			if (chatPlugin$scoreboard != null) {
				ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(((FishingBobberEntity) (Object) this).getOwner().getUuid());
				
				if (chatPlugin$serverPlayer != null) {
					((EventScoreboard) chatPlugin$scoreboard).prepareEvent(chatPlugin$serverPlayer, Utils.capitalizeEveryWord(hookedEntity.getType().getName().getString()), hookedEntity.hasCustomName() ? MixinPlugin.toLegacyText(hookedEntity.getCustomName()) : hookedEntity.getName().getString(), 0);
					chatPlugin$scoreboard.addPlayer(chatPlugin$serverPlayer);
				}
			}
		}
	}
	
	@Redirect(
			method = "method_6957",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z",
					ordinal = 1
					)
			)
	public boolean chatPlugin$method_6957$1(
			World instance,
			Entity entity,
			
			@Local ItemEntity item
			) {
		if (ChatPlugin.getState() == ChatPluginState.LOADED && ScoreboardManager.getInstance().isEnabled()) {
			Scoreboard chatPlugin$scoreboard = ScoreboardManager.getInstance().getScoreboard("fish-event");
			
			if (chatPlugin$scoreboard != null) {
				ChatPluginServerPlayer chatPlugin$serverPlayer = ServerPlayerManager.getInstance().getPlayer(((FishingBobberEntity) (Object) this).getOwner().getUuid());
				
				if (chatPlugin$serverPlayer != null) {
					int chatPlugin$amount = random.nextInt(6) + 1;
					
					((EventScoreboard) chatPlugin$scoreboard).prepareEvent(chatPlugin$serverPlayer, "Item", item.getName().getString(), chatPlugin$amount);
					chatPlugin$scoreboard.addPlayer(chatPlugin$serverPlayer);
					return instance.spawnEntity(new ExperienceOrbEntity(instance, chatPlugin$serverPlayer.getX(), chatPlugin$serverPlayer.getY() + 0.5D, chatPlugin$serverPlayer.getZ() + 0.5D, chatPlugin$amount));
				}
			}
		} return instance.spawnEntity(entity);
	}
	
}
