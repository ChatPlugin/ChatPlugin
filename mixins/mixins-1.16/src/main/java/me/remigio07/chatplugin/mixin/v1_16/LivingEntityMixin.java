package me.remigio07.chatplugin.mixin.v1_16;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import me.remigio07.chatplugin.api.common.event.EventManager;
import me.remigio07.chatplugin.api.common.util.VersionUtils;
import me.remigio07.chatplugin.api.common.util.VersionUtils.Version;
import me.remigio07.chatplugin.bootstrap.JARLibraryLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin { // 1.16-1.21
	
	@Unique
	private static Class<?> chatPlugin$FabricPremiumEventManager;
	
	@Unique
	private static Method chatPlugin$allowDeath, chatPlugin$afterDeath, chatPlugin$afterDamage;
	
	@Inject(
			method = "damage",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/LivingEntity;isDead()Z",
					ordinal = 1
					)
			)
	private void chatPlugin$damage$0(
			DamageSource source,
			float amount,
			
			CallbackInfoReturnable<Boolean> info
			) {
		if (VersionUtils.getVersion().isOlderThan(Version.V1_19_2))
			try {
				if (chatPlugin$FabricPremiumEventManager == null)
					chatPlugin$FabricPremiumEventManager = Class.forName("me.remigio07.chatplugin.server.fabric.manager.FabricPremiumEventManager", false, JARLibraryLoader.getInstance());
				if (chatPlugin$allowDeath == null)
					chatPlugin$allowDeath = chatPlugin$FabricPremiumEventManager.getMethod("allowDeath", LivingEntity.class, DamageSource.class, float.class);
				chatPlugin$allowDeath.invoke(EventManager.getInstance(), this, source, amount);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
	@Inject(
			method = "onDeath",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/World;sendEntityStatus(Lnet/minecraft/entity/Entity;B)V"
					)
			)
	private void chatPlugin$onDeath(
			DamageSource source,
			
			CallbackInfo info
			) {
		if (VersionUtils.getVersion().isOlderThan(Version.V1_19_2))
			try {
				if (chatPlugin$FabricPremiumEventManager == null)
					chatPlugin$FabricPremiumEventManager = Class.forName("me.remigio07.chatplugin.server.fabric.manager.FabricPremiumEventManager", false, JARLibraryLoader.getInstance());
				if (chatPlugin$afterDeath == null)
					chatPlugin$afterDeath = chatPlugin$FabricPremiumEventManager.getMethod("afterDeath", LivingEntity.class, DamageSource.class);
				chatPlugin$afterDeath.invoke(EventManager.getInstance(), this, source);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
	@Inject(
			method = "damage",
			at = @At("TAIL")
			)
	private void chatPlugin$damage$1(
			DamageSource source,
			float amount,
			
			CallbackInfoReturnable<Boolean> info,
			@Local(ordinal = 1) float dealt,
			@Local(ordinal = 0) boolean blocked
			) {
		if (((LivingEntity) (Object) this).isAlive())
			try {
				if (chatPlugin$FabricPremiumEventManager == null)
					chatPlugin$FabricPremiumEventManager = Class.forName("me.remigio07.chatplugin.server.fabric.manager.FabricPremiumEventManager", false, JARLibraryLoader.getInstance());
				if (chatPlugin$afterDamage == null)
					chatPlugin$afterDamage = chatPlugin$FabricPremiumEventManager.getMethod("afterDamage", LivingEntity.class, DamageSource.class, float.class, float.class, boolean.class);
				chatPlugin$afterDamage.invoke(EventManager.getInstance(), this, source, dealt, amount, blocked);
			} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
	}
	
}
